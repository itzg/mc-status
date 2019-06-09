
![GitHub release](https://img.shields.io/github/release/itzg/mc-status.svg)
![Docker Pulls](https://img.shields.io/docker/pulls/itzg/mc-status.svg)

A web application that serves up a little REST API to query and convey the status of Minecraft servers using the native MC protocol

[![](https://jitpack.io/v/itzg/mc-status.svg)](https://jitpack.io/#itzg/mc-status)

[Download pre-built jar](https://jitpack.io/com/github/itzg/mc-status/1.2.0/mc-status-1.2.0.jar)

## Usage as a REST Server

To use only the ad hoc query endpoint, `/server`, just start using:

    java -jar mc-status-$VERSION.jar
    
The following configuration parameters can be passed after the jar:

* **--mcstatus.servers**
  
  A comma separated list of `host[:port]` to pre-configure for use with `/servers` endpoint 

* **--mcstatus.defaultPort**=25565

  The default port to use when absent from the property above or the ad hoc `/server` endpoint

* **--mcstatus.serverInfoTimeoutSec**=10

  The number of seconds allowed for a server info response to be received

* **--cors.allowOrigins**

  A comma separated list of specific origins (as URLs) to allow. Implies a setting of `cors.allowAll=false`

* **--cors.allowAll**=true

  Set to false to disable allowing of all cross-origin requests.

## Usage as a one-shot status checker

You can also run mc-status in a one-shot, terse, script-friendly way by invoking it as:

    java -jar mc-status --one-shot SERVER[:PORT] ...

If any `SERVER[:PORT]` status checks times out, then the process exits with a non-zero status code.

Some other options that are useful to include are:

* **--results**=FILE

  The name of a file where each line is a JSON response structure for the corresponding server.
  
* **--mcstatus.exclude-icon**=false

  Can be used to exclude the Base64 icon inclusion.
  
* **--mcstatus.exclude-players**=false

  Can be used to exclude the list of player names. The online and max values are still included.

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
  "icon": "iVBORw0KG...snip...AAASUVORK5CYII=",
  "motd": {
    "raw": "A Vanilla Minecraft Server powered by Docker",
    "html": "A Vanilla Minecraft Server powered by Docker",
    "stripped": "A Vanilla Minecraft Server powered by Docker"
  }
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
    "icon": "iVBORw0KG...snip...ORK5CYII=",
    "motd": {
      "raw": "A Vanilla Minecraft Server powered by Docker",
      "html": "A Vanilla Minecraft Server powered by Docker",
      "stripped": "A Vanilla Minecraft Server powered by Docker"
    }
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

## Formatted MOTD

The server status responses will take care of providing alternate formats for the Message of the Day (MOTD),
where `motd.html` will replace the [formatting codes](https://minecraft.gamepedia.com/Formatting_codes) 
with the appropriate `<span>` styling. 

The `motd.stripped` provides a simplified string that removes the formatting codes and normalizes whitespace.

The `description` and `motd.raw` fields contain the original value as returned by the server.

For example, this is the response when querying `mc.hypixel.net`:
```json
{
	"host": "mc.hypixel.net",
	"port": 25565,
	"version": "Requires MC 1.8-1.13",
	"protocolVersion": 316,
	"players": {
		"max": 62000,
		"online": 27264,
		"players": []
	},
	"description": "                §eHypixel Network §c[1.8-1.13]\n     §6§lBED WARS CASTLE V2 §7- §2§lMM BUG FIXES",
	"icon": "iVBORw0KGgoAAAANSUhEUgAAAE...",
	"motd": {
		"raw": "                §eHypixel Network §c[1.8-1.13]\n     §6§lBED WARS CASTLE V2 §7- §2§lMM BUG FIXES",
		"html": "                <span style=\"color:#FFFF55;\">Hypixel Network </span><span style=\"color:#FF5555;\">[1.8-1.13]<br>     </span><span style=\"color:#FFAA00;\"><span style=\"font-weight:bold;\">BED WARS CASTLE V2 </span></span><span style=\"color:#AAAAAA;\">- </span><span style=\"color:#00AA00;\"><span style=\"font-weight:bold;\">MM BUG FIXES</span></span>",
		"stripped": "Hypixel Network [1.8-1.13] BED WARS CASTLE V2 - MM BUG FIXES"
	}
}
```