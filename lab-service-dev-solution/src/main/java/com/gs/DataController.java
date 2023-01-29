package com.gs;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.jdbc.GSConnection;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import org.apache.tomcat.util.http.parser.MediaType;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.SpaceProxyConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


@RequestMapping()
@RestController
public class DataController {
    @Value("${space.name}")
    private String spaceName;

    @Value("${space.group}")
    private String group;

    @Value("${space.manager}")
    private String locators;



    @Value("${query.limit}")
    private int limit;


    private GigaSpace space;

    private Connection connection;
    @GetMapping("/")
    public String welcome(){
        return ("WELCOME to space rest api example! working with space:" ) + spaceName;
    }

    @GetMapping("/queryrs")
    public String query(@RequestParam String tableName) throws Exception {
        System.out.println("============ Query was called:" + spaceName);
        space = connectToSpace();
        if (!tableExists(tableName)) return ("Failed to run select Table "+ tableName + " Doesn't exists in space " + spaceName);
        ResultSet rs = executeQuery("select * from " + "\"" + tableName +"\" LIMIT "+limit ) ;
        return dumpResult(rs);
    }

    public boolean tableExists(String tableName){
        SpaceTypeDescriptor typeDescriptor = space.getTypeManager().getTypeDescriptor(tableName);
        if (typeDescriptor == null)   return false; else return true;
    }

    @PostMapping("/insert")
    public String newEntry(@RequestParam String tableName, @RequestBody Map<String,Object> properties) throws Exception {
        space = connectToSpace();
        if (!tableExists(tableName))
            return ("Failed to insert object Type:" + tableName + "doesn't exists in space:" + spaceName);
        SpaceDocument spaceDocument = new SpaceDocument(tableName);
        spaceDocument.addProperties(properties);
        try {
            space.write(spaceDocument);
        }
        catch (Throwable t){
            return "Failed to insert object: " + t;
        }
        return ("Object inserted successfully");
    }

    @PostMapping("/newtable")
    public String createTable(@RequestBody String query) throws Exception {
        connectToSpace();
        String tableName = findTableName(query);
        if (tableExists(tableName)) {
            return ("Fail to create table:" + tableName + " table already exists in space " + spaceName);
        }
        updateQuery(query);
        return ("Table was created successfully");
    }

    protected String findTableName(String query) throws Exception{
        String[] words = query.split(" ");
        String queryLC = query.toLowerCase();
        if (queryLC.startsWith("create"))
            return words[2];
        if (queryLC.startsWith("select")){
            for (int i=0; i < words.length; i++){
                if (words[i].equalsIgnoreCase("from"))
                    return words[i+1];
            }
        }
        throw new Exception("Can't extract table name from query:" + query);
    }

    private boolean updateQuery(String query) throws SQLException {
        space = connectToSpace();
        try  {
            Statement stmt = connection.createStatement();
             stmt.execute(query);
             return true;
        }
        catch (Exception e){
            System.out.println("Fail to run query:" + query);
            throw e;
        }

    }



    private ResultSet executeQuery(String query) throws SQLException {
        space = connectToSpace();
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);

    }


    public String dumpResult(ResultSet resultSet) throws SQLException {
        StringBuilder output = new StringBuilder();
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        for (int k=1; k<= columnsNumber; k++){
            if (k > 1) output.append("\t");
            output.append(rsmd.getColumnName(k));
        }
        output.append("\n\r");
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) output.append("|\t");
                String columnValue = resultSet.getString(i);
                output.append(columnValue);
            }
            output.append("\n\r");
        }
        return output.toString();
    }

    private GigaSpace connectToSpace() throws SQLException{
        if (space != null)
            return space;

        space = new GigaSpaceConfigurer(new SpaceProxyConfigurer(spaceName)
                .lookupLocators(locators).lookupGroups(group)).create();
        Properties props = new Properties();
        connection = GSConnection.getInstance(space.getSpace(), props);
        return space;
    }


}
