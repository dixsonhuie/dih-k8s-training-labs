package com.mycompany.app;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@RestController
public class ValidateController {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @GetMapping("/measurement/compare/{measurementId1}/{measurementId2}")
    public Map<String,String> compareMeasurement(@PathVariable String measurementId1
            ,@PathVariable String measurementId2
            ,@RequestParam(defaultValue="0") int executionTime) {

        Map<String, String> response = new HashMap<>();

        try {
            response.put("response", "Record fetched ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    return response;
    }
}
