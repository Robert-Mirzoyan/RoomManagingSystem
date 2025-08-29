package com.example.project.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomPortController {
    @Value("${server.port}") String port;
    @Value("${eureka.instance.instance-id:unknown}") String id;

    @GetMapping("/port")
    public String getPort() { return "room-service port=" + port + ", id=" + id; }
}

