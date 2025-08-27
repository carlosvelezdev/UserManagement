package com.cavs.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Representa una acción realizada por un usuario en el sistema.
 * Cada acción incluye una descripción, timestamp y el ID del usuario que la realizó.
 * 
 * @author Carlos Velez
 * @version 1.0
 */
public class Action {
    private final String description;
    private final long timestamp;
    private final String userId;
    
    /**
     * Constructor para crear una nueva acción
     * @param description Descripción de la acción realizada
     * @param userId ID del usuario que realizó la acción
     */
    public Action(String description, String userId) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la acción no puede ser nula o vacía");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo o vacío");
        }
        
        this.description = description.trim();
        this.userId = userId.trim();
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * @return La descripción de la acción
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @return El timestamp de cuando se realizó la acción
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * @return El ID del usuario que realizó la acción
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * @return El timestamp formateado como fecha legible
     */
    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * @return Una representación detallada de la acción
     */
    @Override
    public String toString() {
        return String.format("[%s] %s (Usuario: %s)", 
                           getFormattedTimestamp(), 
                           description, 
                           userId);
    }
    
    /**
     * Compara dos acciones por su timestamp
     * @param other La otra acción a comparar
     * @return -1 si esta acción es anterior, 0 si son iguales, 1 si es posterior
     */
    public int compareByTimestamp(Action other) {
        return Long.compare(this.timestamp, other.timestamp);
    }
}
