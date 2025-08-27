package com.cavs.model;

import java.util.UUID;

/**
 * Representa un usuario en el sistema de gestión.
 * Incluye información personal, credenciales, rol y historial de acciones.
 * 
 * @author Carlos Velez
 * @version 1.0
 */
public class User {
    // Constantes
    private static final int MAX_ACTIONS_PER_USER = 100;
    private static final int MAX_FAILED_ATTEMPTS = 3;
    
    // Atributos principales
    private final String userId;
    private String fullName;
    private final String username;
    private String password;
    private Role role;
    
    // Historial de acciones
    private final Action[] actionHistory;
    private int actionCount;
    
    // Control de seguridad
    private boolean isBlocked;
    private int failedLoginAttempts;
    
    /**
     * Constructor principal para crear un nuevo usuario
     * @param userId ID único del usuario
     * @param fullName Nombre completo del usuario
     * @param username Nombre de usuario para login
     * @param password Contraseña del usuario
     * @param role Rol asignado al usuario
     */
    public User(String userId, String fullName, String username, String password, Role role) {
        // Validaciones
        validateUserId(userId);
        validateFullName(fullName);
        validateUsername(username);
        validatePasswordFormat(password);
        validateRole(role);
        
        // Asignacion con sanitizacion de datos
        this.userId = userId.trim();
        this.fullName = fullName.trim();
        this.username = username.trim();
        this.password = password;
        this.role = role;
        
        // Inicializar historial y controles
        this.actionHistory = new Action[MAX_ACTIONS_PER_USER];
        this.actionCount = 0;
        this.isBlocked = false;
        this.failedLoginAttempts = 0;
        
        // Registrar acción de creación
        addAction(new Action("Usuario creado en el sistema", userId));
    }
    
    /**
     * Constructor conveniente que genera un ID automáticamente
     */
    public User(String fullName, String username, String password, Role role) {
        this(generateUserId(), fullName, username, password, role);
    }
    
    // Getters básicos
    public String getUserId() {
        return userId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public String getUsername() {
        return username;
    }
    
    /**
     * @return La contraseña (en una implementación real estaría encriptada)
     */
    public String getPassword() {
        return password;
    }
    
    public Role getRole() {
        return role;
    }
    
    public boolean isBlocked() {
        return isBlocked;
    }
    
    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    
    // Setters con validación
    public void setFullName(String fullName) {
        validateFullName(fullName);
        String oldName = this.fullName;
        this.fullName = fullName.trim();
        addAction(new Action("Nombre actualizado de '" + oldName + "' a '" + this.fullName + "'", userId));
    }
    
    public void setRole(Role role) {
        validateRole(role);
        Role oldRole = this.role;
        this.role = role;
        addAction(new Action("Rol cambiado de '" + oldRole + "' a '" + role + "'", userId));
    }
    
    public void setBlocked(boolean blocked) {
        this.isBlocked = blocked;
        String action = blocked ? "Usuario bloqueado" : "Usuario desbloqueado";
        addAction(new Action(action, userId));
        
        if (!blocked) {
            resetFailedLoginAttempts();
        }
    }
    
    // Métodos de contraseña
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
    
    public void setPassword(String newPassword) {
        validatePasswordFormat(newPassword);
        this.password = newPassword;
        addAction(new Action("Contraseña actualizada", userId));
    }
    
    // Métodos de autenticación
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        addAction(new Action("Intento de login fallido #" + failedLoginAttempts, userId));
        
        if (failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
            setBlocked(true);
        }
    }
    
    public void resetFailedLoginAttempts() {
        if (failedLoginAttempts > 0) {
            this.failedLoginAttempts = 0;
            addAction(new Action("Contador de intentos fallidos reiniciado", userId));
        }
    }
    
    // Métodos de historial
    public void addAction(Action action) {
        if (actionCount < MAX_ACTIONS_PER_USER) {
            actionHistory[actionCount] = action;
            actionCount++;
        } else {
            // Si se alcanza el límite, mover todas las acciones una posición hacia adelante
            // y agregar la nueva al final (FIFO - First In, First Out)
            System.arraycopy(actionHistory, 1, actionHistory, 0, MAX_ACTIONS_PER_USER - 1);
            actionHistory[MAX_ACTIONS_PER_USER - 1] = action;
        }
    }
    
    public Action[] getActionHistory() {
        // Retornar solo las acciones válidas (no nulas)
        Action[] validActions = new Action[actionCount];
        System.arraycopy(actionHistory, 0, validActions, 0, Math.min(actionCount, MAX_ACTIONS_PER_USER));
        return validActions;
    }
    
    public int getActionCount() {
        return Math.min(actionCount, MAX_ACTIONS_PER_USER);
    }
    
    // Métodos de utilidad
    public boolean isAdministrator() {
        return role == Role.ADMINISTRATOR;
    }
    
    public boolean isStandardUser() {
        return role == Role.STANDARD;
    }
    
    // Métodos de validación privados
    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo o vacío");
        }
    }
    
    private void validateFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre completo no puede ser nulo o vacío");
        }
        if (fullName.trim().length() < 2) {
            throw new IllegalArgumentException("El nombre completo debe tener al menos 2 caracteres");
        }
    }
    
    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío");
        }
        if (username.trim().length() < 3) {
            throw new IllegalArgumentException("El nombre de usuario debe tener al menos 3 caracteres");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("El nombre de usuario solo puede contener letras, números y guiones bajos");
        }
    }
    
    private void validatePasswordFormat(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }
        if (password.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres");
        }
    }
    
    private void validateRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
    }
    
    /**
     * Genera un ID único para el usuario
     */
    private static String generateUserId() {
        return "USR_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    @Override
    public String toString() {
        return String.format("Usuario{id='%s', nombre='%s', username='%s', rol=%s, bloqueado=%s, acciones=%d}", 
                           userId, fullName, username, role, isBlocked, getActionCount());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId.equals(user.userId);
    }
    
    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}
