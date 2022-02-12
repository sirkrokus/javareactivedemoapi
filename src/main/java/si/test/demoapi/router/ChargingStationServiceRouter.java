package si.test.demoapi.router;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import si.test.demoapi.domain.ChargingStation;
import si.test.demoapi.domain.ChargingStationSchedule;
import si.test.demoapi.handler.ChargingStationServiceHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ChargingStationServiceRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/charging-station-service/find",
                    produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET,
                    beanClass = ChargingStationServiceHandler.class, beanMethod = "findStations",
                    operation = @Operation(operationId = "findStations",
                            description = "Looks up for charging stations according to specified criteria",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "OK",
                                            content = @Content(schema = @Schema(implementation = ChargingStation.class))),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            },
                            parameters = {
                                    @Parameter(in = ParameterIn.QUERY, name = "power"),
                                    @Parameter(in = ParameterIn.QUERY, name = "socketType"),
                                    @Parameter(in = ParameterIn.QUERY, name = "standard")
                            })
            ),
            @RouterOperation(path = "/api/v1/charging-station-service/nearest",
                    produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET,
                    beanClass = ChargingStationServiceHandler.class, beanMethod = "findNearestStations",
                    operation = @Operation(operationId = "findNearestStations",
                            description = "Looks up for nearest charging stations around of specified geocoordinates",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "OK",
                                            content = @Content(schema = @Schema(implementation = ChargingStation.class))),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            },
                            parameters = {
                                    @Parameter(in = ParameterIn.QUERY, name = "longitude"),
                                    @Parameter(in = ParameterIn.QUERY, name = "latitude"),
                                    @Parameter(in = ParameterIn.QUERY, name = "distance")
                            })
            ),
            @RouterOperation(path = "/api/v1/charging-station-service/book/{id}",
                    produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST,
                    beanClass = ChargingStationServiceHandler.class, beanMethod = "bookStation",
                    operation = @Operation(operationId = "bookStation",
                            description = "Books a selected station for a specific day and hour",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "OK",
                                            content = @Content(schema = @Schema(implementation = ChargingStationSchedule.class))),
                                    @ApiResponse(responseCode = "404", description = "Station is not found"),
                                    @ApiResponse(responseCode = "500", description = "Station is busy at this time")
                            },
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "id"),
                                    @Parameter(in = ParameterIn.QUERY, name = "date"),
                                    @Parameter(in = ParameterIn.QUERY, name = "hour")
                            })
            ),
            @RouterOperation(path = "/api/v1/charging-station-service/activate/{id}",
                    produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST,
                    beanClass = ChargingStationServiceHandler.class, beanMethod = "activateStation",
                    operation = @Operation(operationId = "activateStation",
                            description = "Activates a specified station",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "OK",
                                            content = @Content(schema = @Schema(implementation = ChargingStation.class))),
                                    @ApiResponse(responseCode = "404", description = "Station is not found")
                            }, parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "id")
                            })
            )
    })
    public RouterFunction<ServerResponse> getChargingStationServiceRouter(ChargingStationServiceHandler handler) {
        return RouterFunctions.nest(path("/api/v1/charging-station-service"),
                RouterFunctions.route(GET("/find"), handler::findStations)
                        .andRoute(GET("/nearest"), handler::findNearestStations)
                        .andRoute(POST("/book/{id}"), handler::bookStation)
                        .andRoute(POST("/activate/{id}"), handler::activateStation)
        );
    }

}
