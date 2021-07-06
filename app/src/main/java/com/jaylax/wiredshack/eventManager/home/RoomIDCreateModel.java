package com.jaylax.wiredshack.eventManager.home;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoomIDCreateModel {
    @SerializedName("room")
    @Expose
    private RoomIDData room;

    public RoomIDData getRoom() {
        return room;
    }

    public void setRoom(RoomIDData room) {
        this.room = room;
    }

    public static class RoomIDData {

        @SerializedName("room_id")
        @Expose
        private String roomId;

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

    }
}
