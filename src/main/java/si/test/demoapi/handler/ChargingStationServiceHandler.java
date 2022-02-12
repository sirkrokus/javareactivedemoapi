package si.test.demoapi.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import si.test.demoapi.domain.ChargingStandard;
import si.test.demoapi.domain.ChargingStation;
import si.test.demoapi.domain.ChargingStationSchedule;
import si.test.demoapi.domain.PowerSocketType;
import si.test.demoapi.service.ChargingStationScheduleService;
import si.test.demoapi.service.ChargingStationService;
import si.test.demoapi.service.StationAlreadyBookedException;
import si.test.demoapi.service.StationIsNotFoundException;

import java.time.LocalDate;

@Component
public class ChargingStationServiceHandler {

    @Autowired
    private ChargingStationService chargingStationService;
    @Autowired
    private ChargingStationScheduleService scheduleService;

    public Mono<ServerResponse> findStations(ServerRequest request) {
        int power = Integer.parseInt(request.queryParam("power").orElse("0"));
        PowerSocketType socketType = PowerSocketType.fromString(request.queryParam("socketType").orElse(null));
        ChargingStandard standard = ChargingStandard.fromString(request.queryParam("standard").orElse(null));
        return ServerResponse.ok()
                .body(chargingStationService.find(power, socketType, standard), ChargingStation.class);
    }

    public Mono<ServerResponse> findNearestStations(ServerRequest request) {
        double longitude = Double.parseDouble(request.queryParam("longitude").orElse("0"));
        double latitude = Double.parseDouble(request.queryParam("latitude").orElse("0"));
        double distance = Double.parseDouble(request.queryParam("distance").orElse("0"));
        return ServerResponse.ok()
                .body(chargingStationService.nearest(longitude, latitude, distance), ChargingStation.class)
                .switchIfEmpty(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> bookStation(ServerRequest request) {
        String stationId = request.pathVariable("id");
        int hour = Integer.parseInt(request.queryParam("hour").orElseThrow());
        String strDate = request.queryParam("date").orElseThrow();
        return ServerResponse.ok()
                .body(scheduleService.book(stationId, LocalDate.parse(strDate), hour), ChargingStationSchedule.class)
                .doOnError(throwable -> {
                    if (throwable instanceof StationIsNotFoundException) {
                        ServerResponse.notFound().build();
                    } else if (throwable instanceof StationAlreadyBookedException) {
                        ServerResponse.badRequest().build();
                    }})
                .switchIfEmpty(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> activateStation(ServerRequest request) {
        String stationId = request.pathVariable("id");
        return ServerResponse.ok()
                .body(chargingStationService.activate(stationId), ChargingStation.class)
                .doOnError(throwable -> ServerResponse.notFound().build());
    }

}
