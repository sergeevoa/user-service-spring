package com.example.userservice.util;

import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class TestUtils {
    public static ResultMatcher jsonField(String path, Object expectedValue) {
        return jsonPath(path).value(expectedValue);
    }
}
