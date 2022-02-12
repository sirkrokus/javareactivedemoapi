package si.test.demoapi.router;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
import si.test.demoapi.handler.ChargingStationHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ChargingStationsRouter {

    @Bean
    @RouterOperations(
    {
        @RouterOperation(path = "/api/v1/charging-stations",
                produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET,
                beanClass = ChargingStationHandler.class, beanMethod = "getStations",
                operation = @Operation(operationId = "getStations",
                        description = "Returns all stations",
                        responses = {
                                @ApiResponse(responseCode = "200", description = "OK",
                                        content = @Content(schema = @Schema(implementation = ChargingStation.class))),
                                @ApiResponse(responseCode = "400", description = "Some error occurs"),
                                @ApiResponse(responseCode = "404", description = "Error")
                        })
        ),
        @RouterOperation(path = "/api/v1/charging-stations/{id}",
                produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET,
                beanClass = ChargingStationHandler.class, beanMethod = "getStationById",
                operation = @Operation(operationId = "getStationById",
                        description = "Returns one station by ID",
                        responses = {
                                @ApiResponse(responseCode = "200", description = "OK",
                                        content = @Content(schema = @Schema(implementation = ChargingStation.class))),
                                @ApiResponse(responseCode = "400", description = "Some error occurs"),
                                @ApiResponse(responseCode = "404", description = "Station is not found")
                        }, parameters = {
                                @Parameter(in = ParameterIn.PATH, name = "id")
                        })
        ),
        @RouterOperation(path = "/api/v1/charging-stations",
                produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST,
                beanClass = ChargingStationHandler.class, beanMethod = "createStation",
                operation = @Operation(operationId = "createStation",
                        description = "Creates a new charging station",
                        responses = {
                                @ApiResponse(responseCode = "200", description = "OK",
                                        content = @Content(schema = @Schema(implementation = ChargingStation.class))),
                                @ApiResponse(responseCode = "400", description = "Some error occurs")
                        },
                        requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ChargingStation.class))))
        ),
        @RouterOperation(path = "/api/v1/charging-stations",
                produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.PUT,
                beanClass = ChargingStationHandler.class, beanMethod = "updateStation",
                operation = @Operation(operationId = "updateStation",
                        description = "Updates an existing charging station",
                        responses = {
                            @ApiResponse(responseCode = "200", description = "OK",
                                    content = @Content(schema = @Schema(implementation = ChargingStation.class))),
                            @ApiResponse(responseCode = "400", description = "Some error occurs"),
                            @ApiResponse(responseCode = "404", description = "Station is not found")
                        },
                        requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ChargingStation.class))))
        ),
        @RouterOperation(path = "/api/v1/charging-stations/{id}",
                produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.DELETE,
                beanClass = ChargingStationHandler.class, beanMethod = "deleteStation",
                operation = @Operation(operationId = "deleteStation",
                        description = "Deletes a charging station",
                        responses = {
                                @ApiResponse(responseCode = "200", description = "OK",
                                        content = @Content(schema = @Schema(implementation = ChargingStation.class))),
                                @ApiResponse(responseCode = "400", description = "Some error occurs"),
                                @ApiResponse(responseCode = "404", description = "Station is not found")
                        }, parameters = {
                                @Parameter(in = ParameterIn.PATH, name = "id")
                        })
        )
    })
    public RouterFunction<ServerResponse> getChargingStationsRouter(ChargingStationHandler handler) {
        return RouterFunctions.nest(path("/api/v1/charging-stations"),
               RouterFunctions.route(GET(""), handler::getStations)
                           .andRoute(GET("/{id}"), handler::getStationById)
                           .andRoute(POST(""), handler::createStation)
                           .andRoute(PUT(""), handler::updateStation)
                           .andRoute(DELETE("/{id}"), handler::deleteStation)
        );
    }

}
