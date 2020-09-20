package io.stream.com.controllers;

import io.stream.com.models.dtos.CommentDto;
import io.stream.com.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    @Autowired
    private CommentService service;

    @PostMapping
    public ResponseEntity<?> add(@RequestBody CommentDto commentDto){
        
        service.save(commentDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>>  getAll(){ 
        return ResponseEntity.ok(service.getAllCommentsDto()); 
    }

    @GetMapping("/movieid/{id}")
    public ResponseEntity<List<CommentDto>> getAllCommentsOfMovieId(@PathVariable Long id){ 
        return  ResponseEntity.ok(service.getAllCommentsOfMovieId(id)); 
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<List<CommentDto>> getAllCommentsOfUsername(@PathVariable String username){ 
        return  ResponseEntity.ok(service.getAllCommentsOfUsername(username)); 
    }
}
