package si.test.demoapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import si.test.demoapi.domain.ChargingStandard;
import si.test.demoapi.domain.ChargingStation;
import si.test.demoapi.domain.ChargingStationSchedule;
import si.test.demoapi.domain.Location;
import si.test.demoapi.repository.ChargingStationRepository;
import si.test.demoapi.repository.ChargingStationScheduleRepository;
import si.test.demoapi.service.ChargingStationScheduleService;
import si.test.demoapi.service.StationAlreadyBookedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@DataMongoTest
public class ChargingStationScheduleTest {

    @Autowired
    private ChargingStationRepository stationRepository;
    @Autowired
    private ChargingStationScheduleRepository scheduleRepository;
    @Autowired
    private ChargingStationScheduleService scheduleService;
    @Autowired
    private ReactiveMongoTemplate operations;

    @BeforeEach
    void beforeEach() {
        operations.dropCollection(ChargingStation.class)
                .then(operations.createCollection(ChargingStation.class, CollectionOptions.empty()))
                .then(operations.dropCollection(ChargingStationSchedule.class))
                .then(operations.createCollection(ChargingStationSchedule.class, CollectionOptions.empty()))
                .block();
    }

    @Test
    public void testScheduleList() {

        List<ChargingStation> list = new ArrayList<>();
        IntStream.range(0, 100).forEach(i -> {
            list.add(new ChargingStation(ChargingStandard.OCPP20, new Location(-75d, -50d + i), BigDecimal.valueOf(i+1)));
        });

        List<ChargingStation> stationList = stationRepository.saveAll(list).collectList().block();

        AtomicReference<LocalDate> dt = new AtomicReference<>(LocalDate.of(2020, 1, 1));
        stationList.forEach(s -> {
            dt.set(dt.get().plusDays(1));
            ChargingStationSchedule sch = new ChargingStationSchedule(null, s.getId(), dt.get(),15);
            scheduleRepository.save(sch).block();
        });

        Flux<ChargingStationSchedule> scheduleFlux = scheduleRepository.findAll();
        StepVerifier
                .create(scheduleFlux)
                .expectNextCount(100)
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindScheduleOnDate() {

        ChargingStation st = new ChargingStation(ChargingStandard.OCPP20, new Location(-75d, -50d), BigDecimal.valueOf(1));
        st = stationRepository.save(st).block();

        LocalDate dt = LocalDate.of(2020, 1, 1);
        ChargingStationSchedule schedule = new ChargingStationSchedule(null, st.getId(), dt, 15);
        scheduleRepository.save(schedule).block();

        Mono<ChargingStationSchedule> scheduleMono = scheduleRepository.findOne(st.getId(), dt, 15);
        StepVerifier
                .create(scheduleMono)
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindSchedulesForPeriod() {

        ChargingStation st = new ChargingStation(ChargingStandard.OCPP20,
                new Location(-75d, -50d),
                BigDecimal.valueOf(1));
        AtomicReference<ChargingStation> stationAtomicReference
                = new AtomicReference<>(stationRepository.save(st).block());

        List<ChargingStationSchedule> list = new ArrayList<>();
        LocalDate dt = LocalDate.of(2020, 1, 1);
        IntStream.range(0, 5).forEach(i -> {
            list.add(new ChargingStationSchedule(null, stationAtomicReference.get().getId(), dt.plusDays(i), 15));
        });
        scheduleRepository.saveAll(list).collectList().block();

        Flux<ChargingStationSchedule> scheduleFlux = scheduleRepository
                .find(LocalDate.of(2020, 1, 2), LocalDate.of(2020, 1, 4));
        StepVerifier
                .create(scheduleFlux)
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }

    @Test
    public void testBooking() {
        ChargingStation st = new ChargingStation(ChargingStandard.OCPP20, new Location(-75d, -50d), BigDecimal.valueOf(1));
        st = stationRepository.save(st).block();

        Mono<ChargingStationSchedule> scheduleMono =
                scheduleService.book(st.getId(), LocalDate.of(2020, 1, 1), 10);

        StepVerifier
                .create(scheduleMono)
                .expectNextCount(1)
                .expectComplete()
                .verify();

        scheduleMono = scheduleService.book(st.getId(), LocalDate.of(2020, 1, 1), 10);

        StepVerifier
                .create(scheduleMono)
                .expectError(StationAlreadyBookedException.class)
                .verify();
    }

}
