package com.backend.expense_tracker_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EtResourceNotFoundException extends RuntimeException {
  public EtResourceNotFoundException(String message) {
    super(message);
  }
}
