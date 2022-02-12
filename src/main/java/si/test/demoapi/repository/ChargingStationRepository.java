package si.test.demoapi.repository;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import si.test.demoapi.domain.ChargingStandard;
import si.test.demoapi.domain.ChargingStation;
import si.test.demoapi.domain.PowerSocketType;

@Repository
public interface ChargingStationRepository extends ReactiveMongoRepository<ChargingStation, String> {

    @Query(value = "{$and: [ " +
            "{'powerSocketList.power': {'$gte' : ?0}}, " +
            "?#{ [1] == null ? {$where: 'true'} : {'powerSocketList.socketType' : [1]} }, " +
            "?#{ [2] == null ? {$where: 'true'} : {'standard' : [2]} } " +
            "]}")
    Flux<ChargingStation> find(int minPower, PowerSocketType type, ChargingStandard standard);

    Flux<ChargingStation> findAllByLocationNear(Point point, Distance distance);

}
