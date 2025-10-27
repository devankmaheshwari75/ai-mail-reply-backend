package com.email.writer.controllers;

import com.email.writer.entities.EmailRequest;
import com.email.writer.services.EmailGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EmailGeneratorController {

    private final EmailGeneratorService emailGeneratorService;


    @PostMapping("/generate")

    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest){

        String response  = emailGeneratorService.generateEmailReply(emailRequest);

        return ResponseEntity.ok(response);

    }

}
