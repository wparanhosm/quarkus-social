package io.github.wparanhosm.quarkussocial.domain.model.repository;

import io.github.wparanhosm.quarkussocial.domain.model.Follower;
import io.github.wparanhosm.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {


    public Boolean follows(User follower, User user){

        var params  = Parameters
            .with("follower",follower)
            .and("user",user)
        .map();

        PanacheQuery<Follower> query = find("follower = :follower and user = :user",params);
        return query.firstResultOptional().isPresent();
    }

    public List<Follower> findByUser(Long userId){
        PanacheQuery<Follower> query = find("user.id",userId);
        return query.list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {
        var params  = Parameters
                .with("followerId",followerId)
                .and("userId",userId)
                .map();

        delete("follower.id = :followerId and user.id = :userId",params);
    }
}
