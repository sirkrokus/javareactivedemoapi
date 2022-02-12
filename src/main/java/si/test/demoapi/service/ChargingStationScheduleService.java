package si.test.demoapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import si.test.demoapi.domain.ChargingStation;
import si.test.demoapi.domain.ChargingStationSchedule;
import si.test.demoapi.repository.ChargingStationRepository;
import si.test.demoapi.repository.ChargingStationScheduleRepository;

import java.time.LocalDate;
import java.util.function.Function;

@Service
public class ChargingStationScheduleService {

    @Autowired
    private ChargingStationScheduleRepository repository;
    @Autowired
    private ChargingStationRepository stationRepository;

    public Flux<ChargingStationSchedule> getAll() {
        return repository.findAll();
    }

    public Mono<ChargingStationSchedule> book(String stationId, LocalDate date, int hour) {
        Mono<ChargingStationSchedule> createNew = repository.save(
                new ChargingStationSchedule(null, stationId, date, hour));
        return stationRepository.findById(stationId)
                .switchIfEmpty(Mono.error(new StationIsNotFoundException()))
                .flatMap((Function<ChargingStation, Mono<ChargingStationSchedule>>) station ->
                        repository.findOne(stationId, date, hour)
                )
                .flatMap((Function<ChargingStationSchedule, Mono<ChargingStationSchedule>>) schedule ->
                        Mono.error(new StationAlreadyBookedException()))
                .switchIfEmpty(createNew);
    }

    public Flux<ChargingStationSchedule> findBooked(LocalDate from, LocalDate to) {
        return repository.find(from, to);
    }

    public Mono<ChargingStationSchedule> findOne(String stationId, LocalDate date, int hour) {
        return repository.findOne(stationId, date, hour);
    }

    public Mono<ChargingStationSchedule> update(ChargingStationSchedule schedule) {
        return repository
                .findById(schedule.getId())
                .map(s -> schedule)
                .flatMap(repository::save);
    }

    public Mono<Void> delete(String id) {
        return repository
                .findById(id)
                .flatMap(s -> repository.deleteById(s.getId()));
    }

    public Mono<ChargingStationSchedule> save(ChargingStationSchedule schedule) {
        return repository.save(schedule);
    }

}
