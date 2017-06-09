package me.itzg.mcstatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.concurrent.TimeoutException;

/**
 * @author Geoff Bourne
 * @since Jun 2017
 */
@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class ServerTimeoutException extends TimeoutException {
    public ServerTimeoutException(String message) {
        super(message);
    }
}
