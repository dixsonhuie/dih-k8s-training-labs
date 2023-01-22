# GSTM375 - IIDR hands-on lab

## Goals:

Learn how to configure IIDR to perform CDC between Oracle DB and Kafka topic

----------------

## Milestones:

1. Configure the IIDR agent for Oracle DB
2. Configure the IIDR agent for Kafka
3. Create an administrator user to login Access Server
4. Create source and target datastores in the Access Server
5. Create and configure a new subscription
6. Start the Mirroring (replication) process
7. Monitor the Mirroring process
----------------

## Launch an EC2 instance for this Lab

1. Login to AWS 
2. Select Ireland region
3. Go to EC2 page
4. On the left side-bar, select **Launch Templates**
5. Search for **GSTM375-IIDR-Hands-On** (lt-05a44f8f572b88180)
6. Select the template and click on 'Actions' --> 'Launch instance from template'
7. Scroll down to 'Resource tags' and edit the Name tag: GSTM375-IIDR-yourName
8. Click on 'Launch Instance' (orange button) and wait for the VM to be available.
9. Back to EC2 page, under **Instances** search your VM by 'GSTM375-IIDR-yourName' and locate the public-ip
10. Login to your VM by the following command, the password is: gsods .

    ``` 
    ssh gsods@your-public-ip
    ```
11. You will get the welcome screen
    ```
    ###################################################
    #               IIDR Hands-On VM                  #
    ###################################################


    Access Server: /data/gs_software/iidr/as
    Kafka agent: /data/gs_software/iidr/kafka
    Oracle agent: /data/gs_software/iidr/oracle
    DB2ZOS agent: /data/gs_software/iidr/db2zos



    ORACLE_HOME: /data/install/ORACLEDB
    ORACLE_SID: testDB
    ```
--------------

## Start Oracle DB and Kafka server

1. Start Oracle Database
   ```
   cd scripts
   ./OracleDB-start.sh
   
2. Start Kafka Server and zookeeper
   ```
   ./Kafka-start.sh
   ```

3. Validate that Oracle DB (1521), Kafka server (9092) and zookeeper(2181) are running
   ```
   netstat -nltp |grep '1521\|9092\|2181'
      
   tcp6       0      0 :::1521                 :::*                    LISTEN      1466/tnslsnr
   tcp6       0      0 :::9092                 :::*                    LISTEN      2341/java
   tcp6       0      0 :::2181                 :::*                    LISTEN      1972/java
   ```

----------
## Start IIDR Access Server

1. Start the access server 
   
   ```
   cd /data/gs_software/iidr/as/bin
   nohup ./dmaccessserver &
   ```
   (If the prompet did not return, press enter)

2. Validate that Access server is running
   ```
   netstat -nltp |grep 10101

   tcp6       0      0 :::10101                :::*                    LISTEN      2872/dmaccessserver
   ```
--------------
## Create IIDR Access Server Administration User

1. ```
   cd /data/gs_software/iidr/as/bin
   ```
   To review the command parameters run
   ```
   ./dmcreateuser -help 

   Creates a new user.

   DMCREATEUSER username fullname description password role manager changePassword passwordNeverExpires [-accessserver hostname port adminuser adminpassword]

   username         Name of the user.
   fullname         Full name for the user.
   description      Description for the user.
   password         Password for the user.
   role             One of SYSADMIN, ADMIN, OPERATOR or MONITOR (case insensitive).
   manager          User has access manager privileges (TRUE/FALSE, case insensitive).
   changePassword   Password must be changed on first login (TRUE/FALSE, case insensitive).
   passwordNeverExpires Password never expires (TRUE/FALSE, case insensitive).

   ```
2. Create your admin user for this lab. In this example we use 'iidradmin' as username.
   ```
   ./dmcreateuser iidradmin "Access Server Admin" "Administration User for Hands-On" password123 SYSADMIN TRUE FALSE TRUE
   ```
-----------------
## Create IIDR Kafka instance (agent)

1. ```
   cd /data/gs_software/iidr/kafka/bin
   
   ./dmconfigurets
   ```
   ```
   Welcome to the configuration tool for IBM Data Replication (Kafka). Use this tool to create instances of IBM Data Replication (Kafka).

   Press ENTER to continue...
   ```
   ---------
   instance name: KAFKA

   port number: Press enter to leave as default

   memory: 4096

   Encryption profile: 1

   Select y to use JMS or TCP/IP: Press enter to leave as default

   authentication password (tsuser): password123
   
   Would you like to START instance KAFKA now (y/n)? y
   
   ---------
   
   ```
   CONFIGURATION TOOL - CREATING A NEW INSTANCE
   --------------------------------------------

   Enter the name of the new instance: KAFKA
   Enter the server port number [11701]:
   Enter the Maximum Memory Allowed for this instance (MB) [8192]: 4096
   Encryption profile:

   1. dummy
   2. Manage encryption profiles

   Select an encryption profile: 1
   Select y to use JMS or TCP/IP engine communication connection, select n to use TCP only engine communication connection (y/n) [n]:

   IBM Data Replication Authentication

   The username and password are used to authenticate with IBM Data Replication (Kafka) when configuring a user in Access Manager perspective of IBM InfoSphere Data Replication Management Console.

   Username: tsuser
   Enter IBM Data Replication authentication password:
   Confirm IBM Data Replication authentication password:


   Creating a new instance. Please wait...


   Instance KAFKA was successfully created.

   Would you like to START instance KAFKA now (y/n)?

   Starting instance KAFKA. Please wait...

   Instance KAFKA started successfully. Press ENTER to go to the Main menu...
   ```
   In the Main Menu, select 1 to see the instance you just created

   ```
   MAIN MENU
   ---------

   1. List Current Instances
   2. Add an Instance
   3. Edit an Instance
   4. Delete an Instance
   5. Manage encryption profiles

   6. Exit

   Enter your selection:1
   ```
   ```
   LIST OF CURRENT INSTANCES
   -------------------------

   Name      Server Port Status
   --------- ----------- ------------
   KAFKA    11701       running


   Press ENTER to return to the Main menu...
   ```
2. Press ENTER
3. Select 6 to Exit.
-------------
## Create IIDR OracleDB instance (agent)

   For this step, please provide the following credentials:
   > User: cdc \
   > Password: cdc
1. ```
   cd /data/gs_software/iidr/oracle/bin
   ./dmconfigurets
   ```
   ```
   Welcome to the configuration tool for IBM Data Replication (Oracle). Use this tool to create instances of IBM Data Replication (Oracle).

   Press ENTER to continue...
   ```
   ```
   CONFIGURATION TOOL - CREATING A NEW INSTANCE
   --------------------------------------------

   Enter the name of the new instance: ORACLEDB
   Enter the server port number [11001]:

   Staging Store Disk Quota is used to limit the disk space used by IBM Data Replication staging Store. If this space is exhausted, this instance may run at a lower speed. The minimum value allowed is 1 GB.

   Enter the Staging Store Disk Quota for this instance (GB) [100]: 1
   Enter the Maximum Memory Allowed for this instance (MB) [1024]:
   Use read-only connection to database (y/n) [n]:
   Use archive-only mode (y/n) [n]:
   Encryption profile:

   1. oracledummy
   2. Manage encryption profiles

   Select an encryption profile: 1
   Select y to use JMS or TCP/IP engine communication connection, select n to use TCP only engine communication connection (y/n) [n]:
   Enter the path for ORACLE_HOME [/data/install/ORACLEDB]:
   Enter the path for the TNSNAMES.ORA file [/data/install/ORACLEDB/network/admin]:
   TNS Name:

   1. LISTENER_TESTDB
   2. TESTDB
   3. Other...

   Select a TNS Name: 2
   Would you like to configure advanced parameters (y/n) [n]:
   Use kerberos authentication (y/n) [n]:
   Would you like to retrieve database login credentials from an external secret store through a Java user exit? (y/n) [n]:
   Enter the username: cdc
   Enter the password: cdc
   Retrieving schema list...
   Metadata schema:

   Enter a database schema for metadata tables or press ENTER to list schemas:
   1. ANONYMOUS
   2. APPQOSSYS
   3. AUDSYS
   4. CDC
   5. CTXSYS
   6. DBSFWUSER
   7. DBSNMP
   8. DIP
   9. DVF
   10. DVSYS
   11. GGSYS
   12. GSMADMIN_INTERNAL
   13. GSMCATUSER
   14. GSMROOTUSER


   Press ENTER to continue...
   15. GSMUSER
   16. HR
   17. LBACSYS
   18. MDDATA
   19. MDSYS
   20. OJVMSYS
   21. OLAPSYS
   22. ORACLE_OCM
   23. ORDDATA
   24. ORDPLUGINS
   25. ORDSYS
   26. OUTLN
   27. REMOTE_SCHEDULER_AGENT
   28. SI_INFORMTN_SCHEMA
   29. SYS


   Press ENTER to continue...
   Press ENTER to continue...
   30. SYS$UMF
   31. SYSBACKUP
   32. SYSDG
   33. SYSKM
   34. SYSRAC
   35. SYSTEM
   36. WMSYS
   37. XDB
   38. XS$NULL

   Select a database schema for metadata tables: 4
   Select y to replicate encrypted Columns/Tables, select n to ignore those (y/n) [y]: n
   ```
   ```
   NEW INSTANCE: ORACLEDB >> Configuration mode
   --------------------------------------------

   1. Local log reading
   2. Remote log reading
   3. Manual log shipping
   4. Log shipping with Data Guard

   Enter your selection: 1

   Validating database support. Please wait...
   Retrieving ASM info. Please wait...


   Creating a new instance. Please wait...

   You are about to overwrite metadata for a previous instance of IBM Data Replication that appears to be removed from the system. If you overwrite the metadata, you will not be able to use previous instance of IBM Data Replication. Do you want to proceed(y/n)?y

   Instance ORACLEDB was successfully created.

   Would you like to START instance ORACLEDB now (y/n)?y

   Starting instance ORACLEDB. Please wait...
   Instance ORACLEDB started successfully. Press ENTER to go to the Main menu...
   ```
   ```
   LIST OF CURRENT INSTANCES
   -------------------------

   Name      Server Port Database      Schema      Status
   --------- ----------- ------------- ----------- ------------
   ORACLEDB  11001       testdb        CDC         running


   Press ENTER to return to the Main menu...
   ```
2. Press ENTER
3. Select 6 to Exit.
---------------

## Install the Management Console GUI

### Note: The GUI client for IIDR can be installed on Windows machines only.
* You can use any windows environment to install the GUI client such EC2 or laptop
1. Use this url to download the GUI installation file to a Windows machine:
   
   https://csm-training.s3.eu-west-1.amazonaws.com/GSTM-375-IIDR-Hands-On-Lab/iidrmc-11.4.0.4-11086-setup.exe

2. Double-click to install.
   In some Windows version you might face the error:
   ```
   'Details: Flexeraayd$aaa: Windows DLL failed to load
   at Flexeraayd.af(Unknown Source)
   at Flexeraayd.aa(Unknown Source)
   at com.zerog.ia.installer.LifeCycleManager.init(Unknown Source)
   ```
   In this case, please do the following:
   To resolve this issue, you can run the Installer exe file under Compatibility Mode.  

   > * Right-click the Installer > select 'Troubleshoot compatibility'. 
   > * Choose 'Program worked on a previous version of Windows' - click next 
   > * Select to run under Windows 7 compatibility mode. 
   > * Click 'Test Program' to run the installer. The installer should run and then provide the UI to complete the installation of the product. 
   > * If the installer launches correctly, you can then cancel the troubleshooting wizard.

 3. Follow the instructions, use defaults
--------------

## Login to the IIDR Management Console

1. From your WIndows machine, Open the GUI client by double-click on the 'Management Console' shortcut in the Desktop, or press winKey and start typing 'Management Console' to fins this app, then click on it.
2. In the login windows provide:
   
   >   User Name: iidradmin \
   >   Password: password123 \
   >   Server Name: your IIDR public-ip \
   >   Port Number: 10101 
   
   **Click 'Login'**
----------------

## Create datastores for source (OracleDB)

1. Go to the Access Server tab
2. In the Datastore Management area, right-click and choose **'New Datastore'**
3. In the Identification section, provide:
   > Name: ORACLEDB_DS \
   > Description: Oracle DB datastore \
   > Host Name: localhost or your IIDR's Private IP \
   > Port: 11001

   **Click 'Ping'**

   The Properties section will be updated. 

   Take a look at the Datastore Type which is **Dual**.

   It means that this datastore can be used as source or target.

4. Click on "connection Parameters' and fill in the following:
   > DB Login: cdc \
   > DB Password: cdc \
   > Confirm Password: cdc

   **Click 'OK'**
5. Right-Click on ORACLEDB_DS datastore, select **Assign User** and choose **'iidradmin'**
6. **Click 'OK'**
7. **Click 'OK'**
------------
## Create datastores for source (OracleDB)

1. Go to the Access Server tab
2. In the Datastore Management area, right-click and choose **'New Datastore'**
3. In the Identification section, provide:
   > Name: KAFKA_DS \
   > Description: KAFKA datastore \
   > Host Name: localhost or IIDR's private IP \
   > Port: 11701

   **Click 'Ping'**

   The Properties section will be updated.

   Take a look at the Datastore Type which is **Target**.

   It means that this datastore can be used as a target only.
4. Click on "connection Parameters' and fill in the following:
   > DB Login: tsuser \
   > DB Password: password123 \
   > Confirm Password: password123

   **Click 'OK'**
5. Right-Click on ORACLEDB_DS datastore, select **Assign User** and choose **'iidradmin'**
6. **Click 'OK'**
7. **Click 'OK'**
--------------
## Create a new Subscription

1. Go to the Configuration tab
2. In the Subscription area, right-click and choose **'New Subscription'**
3. In the Identification section, provide:
   > Name: SUB_DEMO_CDC \ 
   > Description: CDC DEMO \
   > Source: default (ORACLEDB_DS) \
   > Target: KAFKA_DS

   **Click 'OK'**

4. Map Tables - "Do you want to map tables for the subscription?" - **Click 'Yes'**
5. Select 'Multiple Kafka Mappings' and **Click 'Next'**
6. In the 'Select Source Tables' window, search for **HR** schema, extend it and select the **EMPLOYEES** table.
7. **Click 'Next'**
8. **Click 'Finish'**
9. In the "Table Mappings" area you should have **HR.EMPLOYEES** as a Source Table, Kafka as a Target Table
10. In the Subscription area, Right-Click on SUB_DEMO_CDC and select **Kafka Properties**.
11. In the Communication Details provide:
    > Host Name: localhost or Zookeeper's private IP \
    > Port: 2181

      **Click 'OK'**

12. In the Subscription area, Right-Click on SUB_DEMO_CDC and select **User Exits**
13. In the configuration section provide:
    > Class Name: `com.datamirror.ts.target.publication.userexit.sample.kafka.KcopJsonSingleRowAuditFormatIntegrated` \
    \
    > Parameter: `-file:/data/gs_software/iidr/kafka/instance/KAFKA/conf/SUB_DEMO_CDC.properties`

      **Click 'OK'**

14. Go to your Linux machine, use your terminal to create the SUB_DEMO_CDC.properties file:
    ``` 
    vim /data/gs_software/iidr/kafka/instance/KAFKA/conf/SUB_DEMO_CDC.properties
    ```
    Type :wq and ENTER to save the file

    Add the following rows to the file:
    ```
    audit.jcfs=ENTTYP,USER,OBJECT,CCID,CODE,TIMSTAMP,LIBRARY
    MAP_DEFAULT=demoTopic
    before.image.prefix=B_
    before.update.record.mode=ALWAYS
    ```
15. Use your terminal to edit the kafkaproducer.properties file:
    ``` 
    vim /data/gs_software/iidr/kafka/instance/KAFKA/conf/kafkaproducer.properties
    ```
    
     Add the following rows to the file:
    ```
    bootstrap.servers=localhost:9092
    ```
    In case of Kafka cluster we will specify:
    ``` host1:9092,host2:9092,host3:9092 ```

    Type :wq ENTER to save the file
-------
## Create a new topic in Kafka

1. Run the following commands:
   ```
   cd /data/gs_software/kafka_2.13-3.3.1/bin
   ./kafka-topics.sh --create --topic demoTopic --bootstrap-server localhost:9092
   ```
   Validate it:
   ```
   ./kafka-topics.sh --bootstrap-server=localhost:9092 --list
   ```

## Start Mirroring (Replication)

1. In the Subscription area, Right-Click on SUB_DEMO_CDC and select **Start Mirroring**
2. In Mirroring Methods choose **Continuous** and **Click OK**
3. The SUB_DEMO_CDC's icon will be changed to triangle.
4. Go to Monitoring tab and check if SUB_DEMO_CDC is Active and has a green sign.

## Test the replication in the target side

1. In the terminal, run the following commands:
   ```
   cd /data/gs_software/kafka_2.13-3.3.1/bin
   ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic demoTopic 
   ```
   Wait until the messages will be displayed, and press **Ctrl + C**.
   You will see a message like "Processed a total of xxx messages".

2. Open another terminal session and run the following commands:
   ```
    sqlplus

    SQL*Plus: Release 19.0.0.0.0 - Production on Mon Jan 16 10:52:45 2023
    Version 19.3.0.0.0

    Copyright (c) 1982, 2019, Oracle.  All rights reserved.

    Enter user-name: cdc
    Enter password: <enter 'cdc' as well>

    Connected to:
    Oracle Database 19c Enterprise Edition Release 19.0.0.0.0 - Production
    Version 19.3.0.0.0

    SQL>
    ```

    Run this query:
    ```
    SQL> select count(*) from HR.EMPLOYEES;
    ```

    Compare the results received from the Source (Oracle DB) and the Target (Kafka)

3.  Back to the Kafka session 
4.  if the kafka consumer is not running, run it by:
    ```
    cd /data/gs_software/kafka_2.13-3.3.1/bin
    ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic demoTopic 
    ```
    In the SQLPLUS session, insert a new row in the EMPLOYEES table:
    ```
    SQL> INSERT INTO HR.EMPLOYEES
    (EMPLOYEE_ID, FIRST_NAME, LAST_NAME, EMAIL, PHONE_NUMBER, HIRE_DATE, JOB_ID, SALARY, COMMISSION_PCT, MANAGER_ID, DEPARTMENT_ID)
    VALUES(10003, 'George', 'Rose', 'GeorgeR@gmail.com', '590.423.4567', TIMESTAMP '2006-01-03 00:00:00.000000', 'IT_PROG', 21000, NULL, 102, 60);
    ```
    commit the insert:
    ```
    SQL> commit;
    ```
    
    Go to the Kafka session - can you see another message? What has been changed?

5.  Back to the Kafka session
    if the kafka consumer is not running, run it by:
    ```
    cd /data/gs_software/kafka_2.13-3.3.1/bin
    ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic demoTopic 
    ```
    In the SQLPLUS session, update a row in the EMPLOYEES table:
    ```
    SQL> UPDATE HR.EMPLOYEES
    SET  SALARY=88888
    WHERE EMPLOYEE_ID=10003;
    ```
    commit the update:
    ```
    SQL> commit;
    ```
    Go to the Kafka session - can you see another message? What has been changed?

7.  Back to the Kafka session
    if the kafka consumer is not running, run it by:
    ```
    cd /data/gs_software/kafka_2.13-3.3.1/bin
    ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic demoTopic 
    ```
    In the SQLPLUS session, update a row in the EMPLOYEES table:
    ```
    SQL> DELETE FROM HR.EMPLOYEES
    WHERE EMPLOYEE_ID=10003;
    ```
    commit the delete:
    ```
    SQL> commit;
    ```
    Go to the Kafka session - can you see another message? What has been changed?
 
    ----------  
    ### As you can see, every change in the source is captured by IIDR and reflected in the target.
    ----------
    ## End of Lab.