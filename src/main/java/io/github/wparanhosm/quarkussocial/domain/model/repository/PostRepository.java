package io.github.wparanhosm.quarkussocial.domain.model.repository;

import io.github.wparanhosm.quarkussocial.domain.model.Post;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class PostRepository implements PanacheRepository<Post> {
}
