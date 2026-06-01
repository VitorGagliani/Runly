package br.com.runly.controller;

import br.com.runly.dto.CriarPostRequest;
import br.com.runly.dto.PostResponse;
import br.com.runly.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> criar(
            Authentication authentication,
            @RequestBody @Valid CriarPostRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.criar(authentication.getName(), request));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponse>> listarFeed() {
        return ResponseEntity.ok(postService.listarFeed());
    }

    @GetMapping("/meus")
    public ResponseEntity<List<PostResponse>> listarMeusPosts(Authentication authentication) {
        return ResponseEntity.ok(postService.listarMeusPosts(authentication.getName()));
    }
}
