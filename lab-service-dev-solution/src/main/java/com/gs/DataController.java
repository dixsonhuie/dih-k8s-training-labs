package com.gs;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.jdbc.GSConnection;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
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

    @GetMapping("/queryrs")
    public String query(@RequestParam String tableName) throws Exception {
        System.out.println("============ Query was called:" + spaceName);
        space = connectToSpace();
        ResultSet rs = executeQuery("select * from " + "\"" + tableName +"\" LIMIT "+limit ) ;
        return dumpResult(rs);
    }

    @PostMapping("/insert")
    public String newEntry(@RequestParam String tableName, @RequestBody Map<String,Object> properties) throws Exception {
        space = connectToSpace();
        SpaceTypeDescriptor typeDescriptor = space.getTypeManager().getTypeDescriptor(tableName);
        if (typeDescriptor == null)
            throw new Exception("Failed to insert object Type:" + tableName + "doesn't exists in space:" + spaceName);
        SpaceDocument spaceDocument = new SpaceDocument(tableName);
        spaceDocument.addProperties(properties);
        try {
            space.write(spaceDocument);
        }
        catch (Throwable t){
            throw new Exception("Failed to insert object: " + t);
        }
        return ("Object inserted successfully");
    }

    @PostMapping("/newtable")
    public String createTable(@RequestBody String query) throws Exception {
        connectToSpace();
        updateQuery(query);
        return ("Table was created successfully");
    }

    private boolean updateQuery(String query) throws SQLException {
        space = connectToSpace();
        Properties props = new Properties();
        try (Connection con = GSConnection.getInstance(space.getSpace(), props)) {
            Statement stmt = con.createStatement();
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
        Properties props = new Properties();
        try (Connection con = GSConnection.getInstance(space.getSpace(), props)) {
            Statement stmt = con.createStatement();
            return stmt.executeQuery(query);
        }
    }


    public String dumpResult(ResultSet resultSet) throws SQLException {
        StringBuilder output = new StringBuilder();
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        for (int k=1; k<= columnsNumber; k++){
            if (k > 1) output.append("\t");
            output.append(rsmd.getColumnName(k));
        }
        output.append(System.lineSeparator());
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) output.append("|\t");
                String columnValue = resultSet.getString(i);
                output.append(columnValue);
            }
            output.append(System.lineSeparator());
        }
        return output.toString();
    }

    private GigaSpace connectToSpace() {
        if (space != null)
            return space;
        space = new GigaSpaceConfigurer(new SpaceProxyConfigurer(spaceName)
                .lookupLocators(locators).lookupGroups(group)).create();
        return space;
    }


}
