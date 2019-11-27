package edu.dcccd.reactiveofficers.controllers;

import edu.dcccd.reactiveofficers.dao.OfficerRepository;
import edu.dcccd.reactiveofficers.entities.Officer;
import edu.dcccd.reactiveofficers.entities.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OfficerControllerTest {
//    private WebTestClient client = WebTestClient.bindToServer()
//                                                .baseUrl("http://localhost:8080")
//                                                .build();

    @Autowired
    private WebTestClient client;

    @Autowired
    private OfficerRepository repository;

    private List<Officer> officers = Arrays.asList(
            new Officer(Rank.CAPTAIN, "James", "Kirk"),
            new Officer(Rank.CAPTAIN, "Jean-Luc", "Picard"),
            new Officer(Rank.CAPTAIN, "Benjamin", "Sisko"),
            new Officer(Rank.CAPTAIN, "Kathryn", "Janeway"),
            new Officer(Rank.CAPTAIN, "Jonathan", "Archer"));

    @BeforeEach
    void setUp() {
        repository.deleteAll()
                .thenMany(Flux.fromIterable(officers))
                .flatMap(repository::save)
                .doOnNext(System.out::println)
                .then()
                .block();
    }

    @Test
    void testGetAllOfficers() {
        client.get().uri("/officers")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(Officer.class)
                .hasSize(5)
                .consumeWith(System.out::println);
    }

    @Test
    void testGetOfficer() {
        client.get().uri("/officers/{id}", officers.get(0).getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Officer.class)
                .consumeWith(System.out::println);
    }

    @Test
    void testCreateOfficer() {
        Officer officer = new Officer(Rank.LIEUTENANT, "Nyota", "Uhuru");

        client.post().uri("/officers")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(Mono.just(officer), Officer.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.first").isEqualTo("Nyota")
                .jsonPath("$.last").isEqualTo("Uhuru")
                .consumeWith(System.out::println);
    }
}
