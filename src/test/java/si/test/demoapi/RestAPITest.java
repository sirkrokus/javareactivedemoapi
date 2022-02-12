package si.test.demoapi;

import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;
import si.test.demoapi.domain.*;
import si.test.demoapi.repository.ChargingStationRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestAPITest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ChargingStationRepository repository;
    @Autowired
    private ReactiveMongoTemplate operations;

    private List<ChargingStation> stationList;

    @BeforeEach
    void beforeEach() {
        var collectionMono = operations.dropCollection(ChargingStation.class)
                .then(operations.createCollection(ChargingStation.class, CollectionOptions.empty()))
                .then(operations.indexOps(ChargingStation.class)
                        .ensureIndex(new GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE)));
        collectionMono.as(StepVerifier::create).expectNextCount(1).verifyComplete();

        List<PowerSocket> socketList = new ArrayList<>() {
            {add(new PowerSocket(250, PowerSocketType.TESLA));}
        };

        List<ChargingStation> list = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> {
            ChargingStation s = new ChargingStation(ChargingStandard.OCPP20,
                    new Location(-75d, -50d + i),
                    BigDecimal.valueOf(i + 1));
            s.setPowerSocketList(socketList);
            list.add(s);
        });

        stationList = repository.saveAll(list).collectList().block();
    }

    @Test
    public void testGetAll() {
        webTestClient
                .get().uri("/api/v1/charging-stations")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChargingStation.class)
                .hasSize(10);
    }

    @Test
    public void testFind() {
        webTestClient
                .get().uri("/api/v1/charging-station-service/find?power=50&socketType=Q&standard=Q")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChargingStation.class)
                .hasSize(10);
    }

    @Test
    public void testFindNearest() {
        webTestClient
                .get().uri("/api/v1/charging-station-service/nearest?longitude=-75&latitude=-50&distance=0.05")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChargingStation.class)
                .hasSize(3);
    }

    @Test
    public void testBookExisted() {
        webTestClient
                .post().uri("/api/v1/charging-station-service/book/" + stationList.get(0).getId() + "?date=2020-01-01&hour=10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ChargingStationSchedule.class);
    }

    @Test
    public void testBookUnexisted() {
        webTestClient
                .post().uri("/api/v1/charging-station-service/book/237823798472?date=2020-01-01&hour=10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    public void testActivateExisted() {
        webTestClient
                .post().uri("/api/v1/charging-station-service/activate/" + stationList.get(0).getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ChargingStation.class)
                .value(station -> station.isActive(), new IsEqual(true));
    }

    @Test
    public void testActivateNonExisted() {
        webTestClient
                .post().uri("/api/v1/charging-station-service/activate/23po4i32po4i")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

}
