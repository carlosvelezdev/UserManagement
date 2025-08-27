package com.cavs.service;

import com.cavs.model.Role;
import com.cavs.model.User;

/**
 * Servicio para la gestión de usuarios en el sistema.
 * Maneja operaciones CRUD y validaciones de permisos.
 * 
 * @author Carlos Velez
 * @version 1.0
 */
public class UserService {
    private static final int MAX_USERS = 50;
    
    private final User[] users;
    private int userCount;
    
    /**
     * Constructor que inicializa el servicio
     */
    public UserService() {
        this.users = new User[MAX_USERS];
        this.userCount = 0;
    }
    
    /**
     * Crea un nuevo usuario en el sistema
     * @param fullName Nombre completo del usuario
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param role Rol del usuario
     * @param currentUser Usuario que realiza la operación
     * @return true si se creó exitosamente, false en caso contrario
     */
    public boolean createUser(String fullName, String username, String password, Role role, User currentUser) {
        try {
            // Validar permisos
            if (!hasPermission(currentUser, "CREATE_USER", null)) {
                System.out.println("Error: No tienes permisos para crear usuarios.");
                return false;
            }
            
            // Validar que hay espacio
            if (userCount >= MAX_USERS) {
                System.out.println("Error: Se ha alcanzado el límite máximo de usuarios (" + MAX_USERS + ").");
                return false;
            }
            
            // Validar unicidad del username
            if (!isUsernameUnique(username)) {
                System.out.println("Error: El nombre de usuario '" + username + "' ya está en uso.");
                return false;
            }
            
            // Crear el usuario
            User newUser = new User(fullName, username, password, role);
            
            // Verificar que el ID generado es único (por seguridad adicional)
            if (!isUserIdUnique(newUser.getUserId())) {
                System.out.println("Error: Conflicto en la generación de ID. Intente nuevamente.");
                return false;
            }
            
            // Agregar al array
            users[userCount] = newUser;
            userCount++;
            
            // Registrar acción en el usuario actual
            if (currentUser != null) {
                currentUser.addAction(new com.cavs.model.Action(
                    "Creó usuario: " + newUser.getUsername() + " (" + newUser.getFullName() + ")",
                    currentUser.getUserId()
                ));
            }
            
            System.out.println("Usuario '" + username + "' creado exitosamente con ID: " + newUser.getUserId());
            return true;
            
        } catch (IllegalArgumentException e) {
            System.out.println("Error al crear usuario: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error inesperado al crear usuario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Busca un usuario por su ID
     * @param userId ID del usuario
     * @return El usuario encontrado o null si no existe
     */
    public User findUserById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        
        for (int i = 0; i < userCount; i++) {
            if (users[i] != null && users[i].getUserId().equals(userId)) {
                return users[i];
            }
        }
        return null;
    }
    
    /**
     * Busca un usuario por su nombre de usuario
     * @param username Nombre de usuario
     * @return El usuario encontrado o null si no existe
     */
    public User findUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        
        for (int i = 0; i < userCount; i++) {
            if (users[i] != null && users[i].getUsername().equals(username)) {
                return users[i];
            }
        }
        return null;
    }
    
    /**
     * Actualiza la información de un usuario
     * @param userId ID del usuario a actualizar
     * @param newFullName Nuevo nombre completo (null para no cambiar)
     * @param newPassword Nueva contraseña (null para no cambiar)
     * @param currentPassword Contraseña actual para validación
     * @param currentUser Usuario que realiza la operación
     * @return true si se actualizó exitosamente, false en caso contrario
     */
    public boolean updateUser(String userId, String newFullName, String newPassword, 
                             String currentPassword, User currentUser) {
        try {
            User targetUser = findUserById(userId);
            if (targetUser == null) {
                System.out.println("Error: Usuario no encontrado.");
                return false;
            }
            
            // Validar permisos
            if (!hasPermission(currentUser, "UPDATE_USER", targetUser)) {
                System.out.println("Error: No tienes permisos para actualizar este usuario.");
                return false;
            }
            
            // Si se va a cambiar la contraseña, validar la actual
            if (newPassword != null && !newPassword.isEmpty()) {
                if (currentPassword == null || !targetUser.validatePassword(currentPassword)) {
                    System.out.println("Error: La contraseña actual es incorrecta.");
                    return false;
                }
            }
            
            boolean updated = false;
            
            // Actualizar nombre completo
            if (newFullName != null && !newFullName.trim().isEmpty()) {
                targetUser.setFullName(newFullName);
                updated = true;
            }
            
            // Actualizar contraseña
            if (newPassword != null && !newPassword.isEmpty()) {
                targetUser.setPassword(newPassword);
                updated = true;
            }
            
            if (updated) {
                // Registrar acción en el usuario actual (si es diferente al objetivo)
                if (currentUser != null && !currentUser.getUserId().equals(targetUser.getUserId())) {
                    currentUser.addAction(new com.cavs.model.Action(
                        "Actualizó información del usuario: " + targetUser.getUsername(),
                        currentUser.getUserId()
                    ));
                }
                System.out.println("Usuario actualizado exitosamente.");
                return true;
            } else {
                System.out.println("No se realizaron cambios.");
                return false;
            }
            
        } catch (IllegalArgumentException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error inesperado al actualizar usuario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un usuario del sistema
     * @param userId ID del usuario a eliminar
     * @param currentUser Usuario que realiza la operación
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    public boolean deleteUser(String userId, User currentUser) {
        try {
            User targetUser = findUserById(userId);
            if (targetUser == null) {
                System.out.println("Error: Usuario no encontrado.");
                return false;
            }
            
            // Validar permisos
            if (!hasPermission(currentUser, "DELETE_USER", targetUser)) {
                System.out.println("Error: No tienes permisos para eliminar usuarios.");
                return false;
            }
            
            // Prevenir que un usuario se elimine a sí mismo
            if (currentUser != null && currentUser.getUserId().equals(targetUser.getUserId())) {
                System.out.println("Error: No puedes eliminarte a ti mismo.");
                return false;
            }
            
            // Buscar y eliminar el usuario
            for (int i = 0; i < userCount; i++) {
                if (users[i] != null && users[i].getUserId().equals(userId)) {
                    String deletedUsername = users[i].getUsername();
                    String deletedFullName = users[i].getFullName();
                    
                    // Mover todos los usuarios hacia adelante
                    for (int j = i; j < userCount - 1; j++) {
                        users[j] = users[j + 1];
                    }
                    users[userCount - 1] = null;
                    userCount--;
                    
                    // Registrar acción
                    if (currentUser != null) {
                        currentUser.addAction(new com.cavs.model.Action(
                            "Eliminó usuario: " + deletedUsername + " (" + deletedFullName + ")",
                            currentUser.getUserId()
                        ));
                    }
                    
                    System.out.println("Usuario '" + deletedUsername + "' eliminado exitosamente.");
                    return true;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            System.out.println("Error inesperado al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene todos los usuarios (respetando permisos)
     * @param currentUser Usuario que solicita la lista
     * @return Array de usuarios visible para el usuario actual
     */
    public User[] getAllUsers(User currentUser) {
        if (currentUser == null) {
            return new User[0];
        }
        
        // Si es administrador, puede ver todos los usuarios
        if (currentUser.getRole().canViewAllUsers()) {
            User[] result = new User[userCount];
            System.arraycopy(users, 0, result, 0, userCount);
            return result;
        } else {
            // Usuario estándar solo puede verse a sí mismo
            User[] result = new User[1];
            result[0] = currentUser;
            return result;
        }
    }
    
    /**
     * Verifica si un nombre de usuario es único
     * @param username Nombre de usuario a verificar
     * @return true si es único, false si ya existe
     */
    public boolean isUsernameUnique(String username) {
        return findUserByUsername(username) == null;
    }
    
    /**
     * Verifica si un ID de usuario es único
     * @param userId ID a verificar
     * @return true si es único, false si ya existe
     */
    public boolean isUserIdUnique(String userId) {
        return findUserById(userId) == null;
    }
    
    /**
     * Obtiene el número total de usuarios en el sistema
     * @return Número de usuarios
     */
    public int getUserCount() {
        return userCount;
    }
    
    /**
     * Verifica si el usuario actual tiene permisos para realizar una operación
     * @param currentUser Usuario que intenta realizar la operación
     * @param operation Tipo de operación
     * @param targetUser Usuario objetivo (puede ser null)
     * @return true si tiene permisos, false en caso contrario
     */
    private boolean hasPermission(User currentUser, String operation, User targetUser) {
        if (currentUser == null) {
            return false;
        }
        
        switch (operation) {
            case "CREATE_USER":
                return currentUser.getRole().canCreateUsers();
                
            case "DELETE_USER":
                return currentUser.getRole().canDeleteUsers();
                
            case "UPDATE_USER":
                if (targetUser == null) {
                    return false;
                }
                return currentUser.getRole().canUpdateUser(currentUser.getUserId(), targetUser.getUserId());
                
            case "VIEW_ALL_USERS":
                return currentUser.getRole().canViewAllUsers();
                
            default:
                return false;
        }
    }
    
    /**
     * Crea usuarios de prueba para demostración
     * @return true si se crearon exitosamente
     */
    public boolean createDefaultUsers() {
        try {
            // Crear usuario administrador
            User admin = new User("admin", "Administrador del Sistema", "admin", "admin123", Role.ADMINISTRATOR);
            if (isUserIdUnique(admin.getUserId()) && isUsernameUnique(admin.getUsername())) {
                users[userCount++] = admin;
            }
            
            // Crear usuario estándar
            User standardUser = new User("user1", "Usuario Estándar", "user1", "user123", Role.STANDARD);
            if (isUserIdUnique(standardUser.getUserId()) && isUsernameUnique(standardUser.getUsername())) {
                users[userCount++] = standardUser;
            }
            
            return true;
        } catch (Exception e) {
            System.out.println("Error al crear usuarios por defecto: " + e.getMessage());
            return false;
        }
    }
}
