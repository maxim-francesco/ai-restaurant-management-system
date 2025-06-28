package com.example.Restaurant.events; // Adaptează la pachetul tău

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogEvent implements Serializable {

    private String message;
    private String logType;       // ex: "PRODUCT"
    private String operationType; // ex: "CREATE", "UPDATE", "DELETE"
    private Date timestamp;

    public LogEvent(String message, String logType, String operationType) {
        this.message = message;
        this.logType = logType;
        this.operationType = operationType;
        this.timestamp = new Date();
    }
}