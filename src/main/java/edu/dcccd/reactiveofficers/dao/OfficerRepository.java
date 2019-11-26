package edu.dcccd.reactiveofficers.dao;

import edu.dcccd.reactiveofficers.entities.Officer;
import edu.dcccd.reactiveofficers.entities.Rank;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface OfficerRepository extends ReactiveMongoRepository<Officer, String> {
    public Flux<Officer> findByRank(Rank rank);
    public Flux<Officer> findByLast(String last);
}
