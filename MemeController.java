package com.crio.starter.controller;

import com.crio.starter.data.MemeEntity;
import com.crio.starter.exchange.MemeDto;
import com.crio.starter.exchange.PostMemeRequest;
import com.crio.starter.exchange.PostMemeResponse;
import com.crio.starter.service.MemeService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/memes")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MemeController {

  private final MemeService memeService;

  /**
   * POST /memes/
   * Returns 400 if any required field is missing or blank.
   * Returns 409 if exact duplicate exists.
   * Returns 200 with allocated id on success.
   */
  @PostMapping("/")
  public ResponseEntity<?> postMeme(@RequestBody PostMemeRequest request) {
    if (request.getName() == null || request.getName().isBlank()
        || request.getUrl() == null || request.getUrl().isBlank()
        || request.getCaption() == null || request.getCaption().isBlank()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    if (memeService.isDuplicate(request.getName(), request.getUrl(), request.getCaption())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    String id = memeService.postMeme(request.getName(), request.getUrl(), request.getCaption());
    return ResponseEntity.ok(new PostMemeResponse(id));
  }

  /**
   * GET /memes/
   * Returns latest 100 memes. Returns empty array if none exist.
   */
  @GetMapping("/")
  public ResponseEntity<List<MemeDto>> getMemes() {
    List<MemeDto> memes = memeService.getLatestMemes()
        .stream()
        .map(e -> new MemeDto(e.getId(), e.getName(), e.getUrl(), e.getCaption()))
        .collect(Collectors.toList());
    return ResponseEntity.ok(memes);
  }

  /**
   * GET /memes/{id}
   * Returns a single meme by id. Returns 404 if not found.
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getMemeById(@PathVariable String id) {
    Optional<MemeEntity> meme = memeService.getMemeById(id);
    if (meme.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    MemeEntity e = meme.get();
    return ResponseEntity.ok(new MemeDto(e.getId(), e.getName(), e.getUrl(), e.getCaption()));
  }
}
