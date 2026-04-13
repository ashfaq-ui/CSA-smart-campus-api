package com.smartcampus.resource;

import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/api/v1/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore store = DataStore.getInstance();

    // GET /api/v1/rooms - list all rooms
    @GET
    public Response getAllRooms() {
        List<Room> rooms = new ArrayList<>(store.getRooms().values());
        return Response.ok(rooms).build();
    }

    // POST /api/v1/rooms - create a new room
    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Room ID is required\"}")
                    .build();
        }
        if (store.getRooms().containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"Room with this ID already exists\"}")
                    .build();
        }
        store.getRooms().put(room.getId(), room);
        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
    }

    // GET /api/v1/rooms/{roomId} - get a specific room
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = store.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found with ID: " + roomId + "\"}")
                    .build();
        }
        return Response.ok(room).build();
    }

    // DELETE /api/v1/rooms/{roomId} - delete a room
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found with ID: " + roomId + "\"}")
                    .build();
        }
        if (!room.getSensorIds().isEmpty()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"Cannot delete room. Remove all sensors first.\"}")
                    .build();
        }
        store.getRooms().remove(roomId);
        return Response.noContent().build();
    }
}