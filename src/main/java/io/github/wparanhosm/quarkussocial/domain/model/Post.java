package io.github.wparanhosm.quarkussocial.domain.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_text")
    private String text;

    @Column(name = "dt_post")
    private LocalDateTime dataTime;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @PrePersist
    public void prePersist(){
        setDataTime(LocalDateTime.now());
    }

}
