package com.github.pozo.bamosz.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class CustomDataController {

    @GetMapping
    public Iterable<String> findAll() {
        return Arrays.asList("a", "b");
    }
}
