
A web application that serves up a little REST API to query and convey the status of Minecraft servers using the native MC protocol

[![](https://jitpack.io/v/itzg/mc-status.svg)](https://jitpack.io/#itzg/mc-status)

## Usage

To use only the ad hoc query endpoint, `/server`, just start using:

    java -jar mc-status-$VERSION.jar
    
The following configuration parameters can be passed after the jar:

* **--mcstatus.servers**
  
  A comma separated list of `host[:port]` to pre-configure for use with `/servers` endpoint 

* **--mcstatus.defaultPort**=25565

  The default port to use when absent from the property above or the ad hoc `/server` endpoint

* **--mcstatus.serverInfoTimeoutSec**=10

  The number of seconds allowed for a server info response to be received

## Response Structure

In response to `GET /server?host=:host&port=:port`

```json
{
  "host": "localhost",
  "port": 25565,
  "version": "1.12",
  "protocolVersion": 335,
  "players": {
    "max": 20,
    "online": 0,
    "players": []
  },
  "description": "A Minecraft Server Powered by Docker",
  "icon": "iVBORw0KG...snip...AAASUVORK5CYII="
}
```

**NOTE** the `icon` is the Base64 encoding of a 64x64 PNG image

In response to `GET /servers`

```json
[
  {
    "host": "localhost",
    "port": 25565,
    "version": "1.12",
    "protocolVersion": 335,
    "players": {
      "max": 20,
      "online": 0,
      "players": []
    },
    "description": "A Minecraft Server Powered by Docker",
    "icon": "iVBORw0KG...snip...ORK5CYII="
  }
]
```

Error when timed out connecting to Minecraft server:

```json
{
  "timestamp": 1497050389168,
  "status": 502,
  "error": "Bad Gateway",
  "exception": "me.itzg.mcstatus.ServerTimeoutException",
  "message": "Timed out getting server info from localhost:25567 after 10 seconds",
  "path": "/server"
}
```