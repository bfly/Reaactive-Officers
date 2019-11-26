package edu.dcccd.reactiveofficers.dao;

import edu.dcccd.reactiveofficers.entities.Officer;
import edu.dcccd.reactiveofficers.entities.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class OfficerRepositoryTest {
    @Autowired
    OfficerRepository repository;

    private List<Officer> officers = Arrays.asList(
            new Officer(Rank.CAPTAIN, "James", "Kirk"),
            new Officer(Rank.CAPTAIN, "Jean-Luc", "Picard"),
            new Officer(Rank.CAPTAIN, "Benjamin", "Sisko"),
            new Officer(Rank.CAPTAIN, "Kathryn", "Janeway"),
            new Officer(Rank.CAPTAIN, "Jonathan", "Archer"));

    @BeforeEach
    void setup() {
        repository.deleteAll()
                .thenMany(Flux.fromIterable(officers))
                .flatMap(repository::save)
                .then()
                .block();
    }

    @Test
    void save() {
        Officer lorca = new Officer(Rank.CAPTAIN, "Gabriel", "Lorca");
        StepVerifier.create(repository.save(lorca))
                .expectNextMatches(officer -> !officer.getId().equals(""))
                .verifyComplete();
    }

    @Test
    void findAll() {
        StepVerifier.create(repository.findAll())
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void findById() {
        officers.stream()
                .map(Officer::getId)
                .forEach(id ->
                        StepVerifier.create(repository.findById(id))
                                .expectNextCount(1)
                                .verifyComplete());
    }

    @Test
    void findByIdNotExist() {
        StepVerifier.create(repository.findById("xyz"))
                .verifyComplete();
    }

    @Test
    void count() {
        StepVerifier.create(repository.count())
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    void findByRank() {
        StepVerifier.create(
                repository.findByRank(Rank.CAPTAIN)
                        .map(Officer::getRank)
                        .distinct())
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(
                repository.findByRank(Rank.ENSIGN)
                        .map(Officer::getRank)
                        .distinct())
                .verifyComplete();
    }

    @Test
    void findByLast() {
        officers.stream()
                .map(Officer::getLast)
                .forEach(lastName ->
                        StepVerifier.create(repository.findByLast(lastName))
                                .expectNextMatches(officer ->
                                        officer.getLast().equals(lastName))
                                .verifyComplete());
    }
}
