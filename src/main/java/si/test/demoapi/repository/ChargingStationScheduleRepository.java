package si.test.demoapi.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import si.test.demoapi.domain.ChargingStationSchedule;

import java.time.LocalDate;

@Repository
public interface ChargingStationScheduleRepository extends ReactiveMongoRepository<ChargingStationSchedule, String> {

    @Query(value = "{$and: [ " +
            "?#{ [0] == null ? {$where: 'true'} : {'bookDate' : {$gte:[0]}} }, " +
            "?#{ [1] == null ? {$where: 'true'} : {'bookDate' : {$lte:[1]}} } " +
            "]}")
    Flux<ChargingStationSchedule> find(LocalDate from, LocalDate to);

    @Query(value = "{'stationId': ?0, 'bookDate': ?1, 'bookHour': ?2}")
    Mono<ChargingStationSchedule> findOne(String stationId, LocalDate date, int hour);

}
