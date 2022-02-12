package si.test.demoapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import si.test.demoapi.domain.*;
import si.test.demoapi.repository.ChargingStationRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
public class ChargingStationTest {

    @Autowired
    private ChargingStationRepository repository;
    @Autowired
    private ReactiveMongoTemplate operations;

    @BeforeEach
    void beforeEach() {
        var collectionMono = operations.dropCollection(ChargingStation.class)
                .then(operations.createCollection(ChargingStation.class, CollectionOptions.empty()))
                .then(operations.indexOps(ChargingStation.class).ensureIndex(new GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE)));
        collectionMono.as(StepVerifier::create).expectNextCount(1).verifyComplete();
    }

    @Test
    public void testCreateOne() {

        List<PowerSocket> socketList = new ArrayList<>() {
            {add(new PowerSocket(50, PowerSocketType.TYPE1));}
            {add(new PowerSocket(50, PowerSocketType.TYPE2));}
        };

        ChargingStation s = new ChargingStation(ChargingStandard.OCPP16, new Location(-75d, 40d), BigDecimal.valueOf(10));
        s.setPowerSocketList(socketList);
        Mono<ChargingStation> stationMono = repository.save(s);

        StepVerifier
                .create(stationMono)
                .assertNext(station -> assertNotNull(station.getId()))
                .expectComplete()
                .verify();
    }

    @Test
    public void testCreateThenUpdate() {

        List<PowerSocket> socketList = new ArrayList<>() {
            {add(new PowerSocket(50, PowerSocketType.TYPE1));}
        };

        ChargingStation station = new ChargingStation(ChargingStandard.OCPP16, new Location(-75d, 40d), BigDecimal.valueOf(10));
        station.setPowerSocketList(socketList);

        station = repository.save(station).block();
        station.setStandard(ChargingStandard.OCPP20);

        station = repository.save(station).block();

        Mono<ChargingStation> stationMono = repository.findById(station.getId());
        StepVerifier
                .create(stationMono)
                .assertNext(s -> assertEquals(ChargingStandard.OCPP20, s.getStandard()))
                .expectComplete()
                .verify();
    }

    @Test
    public void testCreateThenDelete() {

        ChargingStation station = new ChargingStation(ChargingStandard.OCPP16, new Location(-75d, 40d), BigDecimal.valueOf(10));
        station = repository.save(station).block();
        repository.deleteById(station.getId()).block();

        Mono<ChargingStation> stationMono = repository.findById(station.getId());
        StepVerifier
                .create(stationMono)
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

    @Test
    public void testCreateList() {

        List<ChargingStation> list = new ArrayList<>();
        IntStream.range(0, 100).forEach(i -> {
            list.add(new ChargingStation(ChargingStandard.OCPP20, new Location(-75d, -50d + i), BigDecimal.valueOf(i+1)));
        });

        Flux<ChargingStation> stationFlux = repository.saveAll(list);

        StepVerifier
                .create(stationFlux)
                .expectNextCount(100)
                .expectComplete()
                .verify();
    }

    @Test
    public void testFind() {

        List<PowerSocket> socketList1 = new ArrayList<>() {
            {add(new PowerSocket(50, PowerSocketType.TYPE1));}
            {add(new PowerSocket(50, PowerSocketType.TYPE2));}
        };

        List<PowerSocket> socketList2 = new ArrayList<>() {
            {add(new PowerSocket(150, PowerSocketType.TYPE2));}
            {add(new PowerSocket(150, PowerSocketType.CCS));}
            {add(new PowerSocket(150, PowerSocketType.CHADEMO));}
            {add(new PowerSocket(250, PowerSocketType.TESLA));}
        };

        List<ChargingStation> list = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> {
            ChargingStation s = new ChargingStation(i % 2 == 0 ? ChargingStandard.OCPP20 : ChargingStandard.OCPP16, new Location(-75d, -50d + i), BigDecimal.valueOf(i+1));
            s.setPowerSocketList(i % 2 == 0 ? socketList1 : socketList2);
            list.add(s);
        });

        repository.saveAll(list).collectList().block();

        Flux<ChargingStation> listFlux = repository.find(250, null, null);
        StepVerifier
                .create(listFlux)
                .expectNextCount(5)
                .expectComplete()
                .verify();

        listFlux = repository.find(0, PowerSocketType.TYPE2, null);
        StepVerifier
                .create(listFlux)
                .expectNextCount(10)
                .expectComplete()
                .verify();

        listFlux = repository.find(0, null, ChargingStandard.OCPP20);
        StepVerifier
                .create(listFlux)
                .expectNextCount(5)
                .expectComplete()
                .verify();

    }

    @Test
    public void testFindByLocation() {

        List<PowerSocket> socketList = new ArrayList<>() {
            {add(new PowerSocket(50, PowerSocketType.TYPE1));}
        };

        List<ChargingStation> list = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> {
            ChargingStation s = new ChargingStation(ChargingStandard.OCPP20, new Location(-75d, -50d + i), BigDecimal.valueOf(i+1));
            s.setPowerSocketList(socketList);
            list.add(s);
        });

        repository.saveAll(list).collectList().block();

        Flux<ChargingStation> stationFlux = repository.findAllByLocationNear(new Point(-75d, -50d), new Distance(0.05));
        StepVerifier
                .create(stationFlux)
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }

}
