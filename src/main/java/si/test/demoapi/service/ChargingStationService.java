package si.test.demoapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import si.test.demoapi.domain.ChargingStandard;
import si.test.demoapi.domain.ChargingStation;
import si.test.demoapi.domain.PowerSocketType;
import si.test.demoapi.repository.ChargingStationRepository;

@Service
public class ChargingStationService {

    @Autowired
    private ChargingStationRepository repository;

    public Mono<ChargingStation> getById(String id) {
        return repository.findById(id);
    }

    public Flux<ChargingStation> getAll() {
        return repository.findAll();
    }

    public Flux<ChargingStation> find(int minPower, PowerSocketType socketType, ChargingStandard standard) {
        return repository.find(minPower, socketType, standard);
    }

    public Flux<ChargingStation> nearest(double lon, double lat, double distance) {
        return repository.findAllByLocationNear(new Point(lon, lat), new Distance(distance));
    }

    public Mono<ChargingStation> update(ChargingStation station) {
        return repository
                .findById(station.getId())
                .map(s -> station)
                .flatMap(repository::save);
    }

    public Mono<Void> delete(String id) {
        return repository
                .findById(id)
                .flatMap(s -> repository.deleteById(s.getId()));
    }

    public Mono<ChargingStation> save(ChargingStation station) {
        return repository.save(station);
    }

    public Mono<ChargingStation> activate(String stationId) {
        return repository
                .findById(stationId)
                .map(s -> {
                    s.setActive(true);
                    return s;
                })
                .flatMap(repository::save)
                .switchIfEmpty(Mono.error(new StationIsNotFoundException()));
    }
}
