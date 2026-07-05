package com.crio.starter.service;

import com.crio.starter.data.MemeEntity;
import com.crio.starter.repository.MemeRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MemeService {

  private final MemeRepository memeRepository;

  /**
   * Saves a new meme and returns its generated MongoDB ID.
   */
  public String postMeme(String name, String url, String caption) {
    MemeEntity meme = new MemeEntity(null, name, url, caption);
    MemeEntity saved = memeRepository.save(meme);
    return saved.getId();
  }

  /**
   * Returns up to 100 most recently posted memes, newest first.
   * MongoDB ObjectId embeds a timestamp, so sorting by _id descending
   * gives us chronological order without needing a separate timestamp field.
   */
  public List<MemeEntity> getLatestMemes() {
    return memeRepository.findAll(Sort.by(Sort.Direction.DESC, "_id"))
        .stream()
        .limit(100)
        .collect(Collectors.toList());
  }

  /**
   * Returns a meme by ID, or empty if not found.
   */
  public Optional<MemeEntity> getMemeById(String id) {
    return memeRepository.findById(id);
  }

  /**
   * Returns true if a meme with the exact same name, url, and caption already exists.
   */
  public boolean isDuplicate(String name, String url, String caption) {
    return memeRepository.existsByNameAndUrlAndCaption(name, url, caption);
  }
}
