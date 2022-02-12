package si.test.demoapi.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import si.test.demoapi.service.ChargingStationService;
import si.test.demoapi.domain.ChargingStation;

@Component
public class ChargingStationHandler {

    @Autowired
    private ChargingStationService chargingStationService;

    public Mono<ServerResponse> getStations(ServerRequest request) {
        return ServerResponse.ok()
                .body(chargingStationService.getAll(), ChargingStation.class);
    }

    public Mono<ServerResponse> getStationById(ServerRequest request) {
        String id = request.pathVariable("id");
        return chargingStationService.getById(id)
                .flatMap(item -> ServerResponse.ok().body(BodyInserters.fromValue(item)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createStation(ServerRequest request) {
        return request.bodyToMono(ChargingStation.class)
                .flatMap(item -> chargingStationService.save(item))
                .flatMap(item -> ServerResponse.ok().body(BodyInserters.fromValue(item)));
    }

    public Mono<ServerResponse> updateStation(ServerRequest request) {
        return request.bodyToMono(ChargingStation.class)
                .flatMap(item -> chargingStationService.update(item))
                .flatMap(item -> ServerResponse.ok().body(BodyInserters.fromValue(item)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteStation(ServerRequest request) {
        String id = request.pathVariable("id");
        return chargingStationService.getById(id)
                .flatMap(item -> ServerResponse.ok().build(chargingStationService.delete(item.getId())))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}
