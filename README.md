# DubTrack4J
A Java API for Dubtrack.

[![Build Status](https://travis-ci.org/Sponges/DubTrack4J.svg?branch=master)](https://travis-ci.org/Sponges/DubTrack4J)

### Maven
Will add soon

### Usage
##### Creating your DubtrackAPI Builder instance:
```java
DubtrackBuilder builder = DubtrackBuilder.create(username, password);
DubtrackAPI dubtrack = builder.buildAndLogin();
```

##### Joining a room:
This method takes a String room name, which is found at the end of the join url. http://dubtrack.fm/join/RoomName
```java
Room room = dubtrack.joinRoom(roomName);
```

##### Listening for events:
The `EventBus` class uses consumers to handle events.
```java
dubtrack.getEventBus().register(UserChatEvent.class, event -> {
    System.out.println("Got message: " + event.getMessage().getContent());
});
```
See the [Event package](https://github.com/Sponges/DubTrack4J/tree/master/src/main/java/io/sponges/dubtrack4j/event) to see all the available events.

##### Sending a message
```java
room.sendMessage("This is a message!");
```

##### Other stuff
Check out the javadocs @ placeholder.exe

### TODOs
https://github.com/Sponges/DubTrack4J/issues/1
