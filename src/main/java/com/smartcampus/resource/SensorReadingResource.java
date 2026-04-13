package com.smartcampus.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final DataStore store = DataStore.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings
    @GET
    public Response getReadings() {
        try {
            // Check sensor exists
            Sensor sensor = store.getSensors().get(sensorId);
            if (sensor == null) {
                return Response.status(404)
                        .entity("{\"error\":\"Sensor not found: " + sensorId + "\"}")
                        .build();
            }

            List<SensorReading> readings =
                    store.getReadingsForSensor(sensorId);
            return Response.ok(
                    mapper.writeValueAsString(readings)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // POST /api/v1/sensors/{sensorId}/readings
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReading(String body) {
        try {
            // Check sensor exists
            Sensor sensor = store.getSensors().get(sensorId);
            if (sensor == null) {
                return Response.status(404)
                        .entity("{\"error\":\"Sensor not found: " + sensorId + "\"}")
                        .build();
            }

            // Check sensor is not in MAINTENANCE
            if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
                return Response.status(403)
                        .entity("{\"error\":\"Sensor is under maintenance" +
                                " and cannot accept readings\"}")
                        .build();
            }

            // Check sensor is not OFFLINE
            if ("OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
                return Response.status(403)
                        .entity("{\"error\":\"Sensor is offline" +
                                " and cannot accept readings\"}")
                        .build();
            }

            SensorReading reading = mapper.readValue(body,
                    SensorReading.class);

            // Auto generate ID if not provided
            if (reading.getId() == null || reading.getId().isEmpty()) {
                reading.setId(UUID.randomUUID().toString());
            }

            // Auto set timestamp if not provided
            if (reading.getTimestamp() == 0) {
                reading.setTimestamp(System.currentTimeMillis());
            }

            // Save reading
            store.getReadingsForSensor(sensorId).add(reading);

            // Update currentValue on parent sensor
            sensor.setCurrentValue(reading.getValue());

            return Response.status(201)
                    .entity(mapper.writeValueAsString(reading))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}