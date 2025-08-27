package com.cavs.service;

import com.cavs.model.Action;
import com.cavs.model.User;

/**
 * Servicio para manejo de autenticación y autorización de usuarios.
 * Gestiona login, logout y cambio de contraseñas.
 * 
 * @author Carlos Velez
 * @version 1.0
 */
public class AuthenticationService {
    private final UserService userService;
    
    /**
     * Constructor que inicializa el servicio con dependencia de UserService
     * @param userService Servicio de gestión de usuarios
     */
    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Realiza el proceso de autenticación de un usuario
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return El usuario autenticado o null si falló la autenticación
     */
    public User login(String username, String password) {
        try {
            // Validar parámetros
            if (username == null || username.trim().isEmpty()) {
                System.out.println("Error: El nombre de usuario no puede estar vacío.");
                return null;
            }
            
            if (password == null || password.isEmpty()) {
                System.out.println("Error: La contraseña no puede estar vacía.");
                return null;
            }
            
            // Buscar el usuario
            User user = userService.findUserByUsername(username.trim());
            if (user == null) {
                System.out.println("Error: Usuario no encontrado.");
                registerFailedLoginAttempt(username, "Usuario no encontrado");
                return null;
            }
            
            // Verificar si la cuenta está bloqueada
            if (user.isBlocked()) {
                System.out.println("Error: La cuenta está bloqueada. Contacte a un administrador.");
                registerLoginAction(user, false, "Intento de acceso con cuenta bloqueada");
                return null;
            }
            
            // Validar contraseña
            if (!user.validatePassword(password)) {
                System.out.println("Error: Contraseña incorrecta.");
                user.incrementFailedLoginAttempts();
                registerLoginAction(user, false, "Contraseña incorrecta");
                
                // Verificar si se bloqueó la cuenta
                if (user.isBlocked()) {
                    System.out.println("Atención: La cuenta ha sido bloqueada por múltiples intentos fallidos.");
                }
                
                return null;
            }
            
            // Login exitoso
            user.resetFailedLoginAttempts();
            registerLoginAction(user, true, "Inicio de sesión exitoso");
            
            System.out.println("¡Bienvenido, " + user.getFullName() + "!");
            System.out.println("Rol: " + user.getRole().getDisplayName());
            
            return user;
            
        } catch (Exception e) {
            System.out.println("Error inesperado durante el login: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Realiza el proceso de cierre de sesión
     * @param user Usuario que cierra sesión
     */
    public void logout(User user) {
        if (user != null) {
            registerLoginAction(user, true, "Cierre de sesión");
            System.out.println("Sesión cerrada exitosamente. ¡Hasta luego, " + user.getFullName() + "!");
        }
    }
    
    /**
     * Cambia la contraseña de un usuario
     * @param user Usuario que quiere cambiar su contraseña
     * @param currentPassword Contraseña actual
     * @param newPassword Nueva contraseña
     * @return true si se cambió exitosamente, false en caso contrario
     */
    public boolean changePassword(User user, String currentPassword, String newPassword) {
        try {
            if (user == null) {
                System.out.println("Error: Usuario no válido.");
                return false;
            }
            
            // Validar contraseña actual
            if (!user.validatePassword(currentPassword)) {
                System.out.println("Error: La contraseña actual es incorrecta.");
                user.addAction(new Action("Intento fallido de cambio de contraseña", user.getUserId()));
                return false;
            }
            
            // Validar que la nueva contraseña sea diferente
            if (currentPassword.equals(newPassword)) {
                System.out.println("Error: La nueva contraseña debe ser diferente a la actual.");
                return false;
            }
            
            // Cambiar la contraseña
            user.setPassword(newPassword);
            System.out.println("Contraseña cambiada exitosamente.");
            return true;
            
        } catch (IllegalArgumentException e) {
            System.out.println("Error al cambiar contraseña: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error inesperado al cambiar contraseña: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Desbloquea una cuenta de usuario (solo para administradores)
     * @param targetUserId ID del usuario a desbloquear
     * @param adminUser Usuario administrador que realiza la operación
     * @return true si se desbloqueó exitosamente, false en caso contrario
     */
    public boolean unblockUser(String targetUserId, User adminUser) {
        try {
            // Validar que el usuario actual es administrador
            if (adminUser == null || !adminUser.getRole().canUnblockUsers()) {
                System.out.println("Error: Solo los administradores pueden desbloquear usuarios.");
                return false;
            }
            
            // Buscar el usuario objetivo
            User targetUser = userService.findUserById(targetUserId);
            if (targetUser == null) {
                System.out.println("Error: Usuario no encontrado.");
                return false;
            }
            
            // Verificar si realmente está bloqueado
            if (!targetUser.isBlocked()) {
                System.out.println("Información: El usuario '" + targetUser.getUsername() + "' no está bloqueado.");
                return true;
            }
            
            // Desbloquear
            targetUser.setBlocked(false);
            
            // Registrar acción en ambos usuarios
            adminUser.addAction(new Action(
                "Desbloqueó al usuario: " + targetUser.getUsername(),
                adminUser.getUserId()
            ));
            
            System.out.println("Usuario '" + targetUser.getUsername() + "' desbloqueado exitosamente.");
            return true;
            
        } catch (Exception e) {
            System.out.println("Error inesperado al desbloquear usuario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cambia el rol de un usuario (solo para administradores)
     * @param targetUserId ID del usuario cuyo rol se va a cambiar
     * @param newRole Nuevo rol
     * @param adminUser Usuario administrador que realiza la operación
     * @return true si se cambió exitosamente, false en caso contrario
     */
    public boolean changeUserRole(String targetUserId, com.cavs.model.Role newRole, User adminUser) {
        try {
            // Validar que el usuario actual es administrador
            if (adminUser == null || !adminUser.getRole().canChangeUserRoles()) {
                System.out.println("Error: Solo los administradores pueden cambiar roles de usuarios.");
                return false;
            }
            
            // Buscar el usuario objetivo
            User targetUser = userService.findUserById(targetUserId);
            if (targetUser == null) {
                System.out.println("Error: Usuario no encontrado.");
                return false;
            }
            
            // Prevenir que un administrador cambie su propio rol
            if (adminUser.getUserId().equals(targetUser.getUserId())) {
                System.out.println("Error: No puedes cambiar tu propio rol.");
                return false;
            }
            
            // Verificar si el rol es diferente
            if (targetUser.getRole() == newRole) {
                System.out.println("Información: El usuario ya tiene el rol '" + newRole.getDisplayName() + "'.");
                return true;
            }
            
            // Cambiar el rol
            com.cavs.model.Role oldRole = targetUser.getRole();
            targetUser.setRole(newRole);
            
            // Registrar acción en el administrador
            adminUser.addAction(new Action(
                "Cambió rol de " + targetUser.getUsername() + " de '" + 
                oldRole.getDisplayName() + "' a '" + newRole.getDisplayName() + "'",
                adminUser.getUserId()
            ));
            
            System.out.println("Rol del usuario '" + targetUser.getUsername() + 
                             "' cambiado exitosamente a '" + newRole.getDisplayName() + "'.");
            return true;
            
        } catch (Exception e) {
            System.out.println("Error inesperado al cambiar rol: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Registra una acción de login en el historial del usuario
     * @param user Usuario que realizó el intento de login
     * @param success Si el login fue exitoso
     * @param details Detalles adicionales del intento
     */
    private void registerLoginAction(User user, boolean success, String details) {
        if (user != null) {
            String actionDescription = success ? 
                "Inicio de sesión exitoso" : 
                "Intento de inicio de sesión fallido: " + details;
            user.addAction(new Action(actionDescription, user.getUserId()));
        }
    }
    
    /**
     * Registra un intento de login fallido para un username que no existe
     * @param username Username utilizado
     * @param reason Razón del fallo
     */
    private void registerFailedLoginAttempt(String username, String reason) {
        // En una implementación real, esto podría guardarse en un log de seguridad
        // Por ahora, solo lo mostramos en consola para debugging
        System.out.println("DEBUG: Intento de login fallido para username '" + username + "': " + reason);
    }
    
    /**
     * Verifica el estado de seguridad de una cuenta
     * @param username Username a verificar
     * @return Información sobre el estado de la cuenta
     */
    public String getAccountStatus(String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return "Usuario no encontrado";
        }
        
        StringBuilder status = new StringBuilder();
        status.append("Estado de la cuenta '").append(username).append("':\n");
        status.append("- Bloqueada: ").append(user.isBlocked() ? "Sí" : "No").append("\n");
        status.append("- Intentos fallidos: ").append(user.getFailedLoginAttempts()).append("\n");
        status.append("- Rol: ").append(user.getRole().getDisplayName()).append("\n");
        
        return status.toString();
    }
}
