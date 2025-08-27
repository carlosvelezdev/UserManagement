package com.cavs.controller;

import com.cavs.model.Role;
import com.cavs.model.User;
import com.cavs.service.ActionHistoryService;
import com.cavs.service.AuthenticationService;
import com.cavs.service.UserService;

import java.util.Scanner;

/**
 * Controlador principal que maneja la interfaz de usuario del sistema.
 * Coordina la interacción entre el usuario y los servicios del sistema.
 * 
 * @author Carlos Velez
 * @version 1.0
 */
public class UserController {
    private final UserService userService;
    private final AuthenticationService authService;
    private final ActionHistoryService historyService;
    private final Scanner scanner;
    private User currentUser;
    private boolean running;
    
    /**
     * Constructor que inicializa el controlador con sus dependencias
     */
    public UserController() {
        this.userService = new UserService();
        this.authService = new AuthenticationService(userService);
        this.historyService = new ActionHistoryService();
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
        this.running = true;
        
        // Crear usuarios por defecto para demostración
        initializeDefaultUsers();
    }
    
    /**
     * Punto de entrada principal del sistema
     */
    public void start() {
        displayWelcomeMessage();
        
        while (running) {
            try {
                if (currentUser == null) {
                    showMainMenu();
                } else {
                    showUserMenu();
                }
            } catch (Exception e) {
                System.out.println("Error inesperado: " + e.getMessage());
                System.out.println("El sistema continuará funcionando...\n");
            }
        }
        
        System.out.println("¡Gracias por usar el Sistema de Gestión de Usuarios!");
        scanner.close();
    }
    
    /**
     * Muestra el menú principal (sin usuario autenticado)
     */
    private void showMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       SISTEMA DE GESTIÓN DE USUARIOS");
        System.out.println("=".repeat(50));
        System.out.println("1. Iniciar Sesión");
        System.out.println("2. Ver Estado del Sistema");
        System.out.println("3. Salir");
        System.out.println("=".repeat(50));
        System.out.print("Seleccione una opción: ");
        
        try {
            int option = Integer.parseInt(scanner.nextLine().trim());
            
            switch (option) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    showSystemStatus();
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, seleccione 1, 2 o 3.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        }
    }
    
    /**
     * Muestra el menú de usuario autenticado
     */
    private void showUserMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("       BIENVENIDO, " + currentUser.getFullName().toUpperCase());
        System.out.println("       Rol: " + currentUser.getRole().getDisplayName());
        System.out.println("=".repeat(60));
        
        // Opciones básicas disponibles para todos los usuarios
        System.out.println("1. Ver Mi Perfil");
        System.out.println("2. Actualizar Mi Perfil");
        System.out.println("3. Cambiar Mi Contraseña");
        System.out.println("4. Ver Mi Historial de Acciones");
        System.out.println("5. Buscar en Mi Historial");
        System.out.println("6. Exportar Mi Historial");
        
        // Opciones adicionales para administradores
        if (currentUser.isAdministrator()) {
            System.out.println("\n--- OPCIONES DE ADMINISTRADOR ---");
            System.out.println("7. Crear Usuario");
            System.out.println("8. Ver Todos los Usuarios");
            System.out.println("9. Actualizar Otro Usuario");
            System.out.println("10. Eliminar Usuario");
            System.out.println("11. Ver Historial Global");
            System.out.println("12. Desbloquear Usuario");
            System.out.println("13. Cambiar Rol de Usuario");
            System.out.println("14. Estado del Sistema");
        }
        
        System.out.println("\n0. Cerrar Sesión");
        System.out.println("=".repeat(60));
        System.out.print("Seleccione una opción: ");
        
        try {
            int option = Integer.parseInt(scanner.nextLine().trim());
            handleUserMenuOption(option);
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        }
    }
    
    /**
     * Maneja las opciones del menú de usuario
     */
    private void handleUserMenuOption(int option) {
        switch (option) {
            case 1:
                handleViewProfile();
                break;
            case 2:
                handleUpdateProfile();
                break;
            case 3:
                handleChangePassword();
                break;
            case 4:
                handleViewHistory();
                break;
            case 5:
                handleSearchHistory();
                break;
            case 6:
                handleExportHistory();
                break;
            case 7:
                if (currentUser.isAdministrator()) {
                    handleCreateUser();
                } else {
                    System.out.println("Opción no válida.");
                }
                break;
            case 8:
                if (currentUser.isAdministrator()) {
                    handleViewAllUsers();
                } else {
                    System.out.println("Opción no válida.");
                }
                break;
            case 9:
                if (currentUser.isAdministrator()) {
                    handleUpdateOtherUser();
                } else {
                    System.out.println("Opción no válida.");
                }
                break;
            case 10:
                if (currentUser.isAdministrator()) {
                    handleDeleteUser();
                } else {
                    System.out.println("Opción no válida.");
                }
                break;
            case 11:
                if (currentUser.isAdministrator()) {
                    handleViewGlobalHistory();
                } else {
                    System.out.println("Opción no válida.");
                }
                break;
            case 12:
                if (currentUser.isAdministrator()) {
                    handleUnblockUser();
                } else {
                    System.out.println("Opción no válida.");
                }
                break;
            case 13:
                if (currentUser.isAdministrator()) {
                    handleChangeUserRole();
                } else {
                    System.out.println("Opción no válida.");
                }
                break;
            case 14:
                if (currentUser.isAdministrator()) {
                    showSystemStatus();
                } else {
                    System.out.println("Opción no válida.");
                }
                break;
            case 0:
                handleLogout();
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }
    
    /**
     * Maneja el proceso de inicio de sesión
     */
    private void handleLogin() {
        System.out.println("\n--- INICIAR SESIÓN ---");
        System.out.print("Nombre de usuario: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();
        
        User authenticatedUser = authService.login(username, password);
        if (authenticatedUser != null) {
            currentUser = authenticatedUser;
            // La acción ya se registra en el AuthenticationService
        } else {
            System.out.println("Presione Enter para continuar...");
            scanner.nextLine();
        }
    }
    
    /**
     * Maneja la visualización del perfil del usuario
     */
    private void handleViewProfile() {
        System.out.println("\n--- MI PERFIL ---");
        System.out.println("ID de Usuario: " + currentUser.getUserId());
        System.out.println("Nombre Completo: " + currentUser.getFullName());
        System.out.println("Nombre de Usuario: " + currentUser.getUsername());
        System.out.println("Rol: " + currentUser.getRole().getDisplayName());
        System.out.println("Estado: " + (currentUser.isBlocked() ? "BLOQUEADO" : "ACTIVO"));
        System.out.println("Total de Acciones: " + currentUser.getActionCount());
        
        historyService.registerAction(currentUser, "Consultó su perfil");
        
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Maneja la actualización del perfil del usuario
     */
    private void handleUpdateProfile() {
        System.out.println("\n--- ACTUALIZAR MI PERFIL ---");
        System.out.println("Nombre actual: " + currentUser.getFullName());
        System.out.print("Nuevo nombre completo (Enter para mantener actual): ");
        String newName = scanner.nextLine().trim();
        
        if (!newName.isEmpty()) {
            try {
                currentUser.setFullName(newName);
                System.out.println("Perfil actualizado exitosamente.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("No se realizaron cambios.");
        }
        
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Maneja el cambio de contraseña del usuario
     */
    private void handleChangePassword() {
        System.out.println("\n--- CAMBIAR CONTRASEÑA ---");
        System.out.print("Contraseña actual: ");
        String currentPassword = scanner.nextLine();
        
        System.out.print("Nueva contraseña: ");
        String newPassword = scanner.nextLine();
        
        System.out.print("Confirmar nueva contraseña: ");
        String confirmPassword = scanner.nextLine();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Error: Las contraseñas no coinciden.");
        } else {
            boolean success = authService.changePassword(currentUser, currentPassword, newPassword);
            if (!success) {
                System.out.println("No se pudo cambiar la contraseña.");
            }
        }
        
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Maneja la visualización del historial del usuario
     */
    private void handleViewHistory() {
        historyService.displayUserHistory(currentUser);
        historyService.registerAction(currentUser, "Consultó su historial de acciones");
        
        System.out.println("Presione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Maneja la búsqueda en el historial del usuario
     */
    private void handleSearchHistory() {
        System.out.println("\n--- BUSCAR EN HISTORIAL ---");
        System.out.print("Ingrese palabra clave para buscar: ");
        String keyword = scanner.nextLine().trim();
        
        if (!keyword.isEmpty()) {
            historyService.displayFilteredHistory(currentUser, keyword);
            historyService.registerAction(currentUser, "Buscó en su historial: '" + keyword + "'");
        } else {
            System.out.println("No se ingresó ninguna palabra clave.");
        }
        
        System.out.println("Presione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Maneja la exportación del historial del usuario
     */
    private void handleExportHistory() {
        System.out.println("\n--- EXPORTAR HISTORIAL ---");
        String exportedData = historyService.exportUserHistory(currentUser);
        
        System.out.println("Historial exportado:");
        System.out.println("-".repeat(80));
        System.out.println(exportedData);
        System.out.println("-".repeat(80));
        
        System.out.println("Presione Enter para continuar...");
        scanner.nextLine();
    }
    
    // Métodos para administradores
    
    /**
     * Maneja la creación de un nuevo usuario (solo administradores)
     */
    private void handleCreateUser() {
        System.out.println("\n--- CREAR USUARIO ---");
        System.out.print("Nombre completo: ");
        String fullName = scanner.nextLine().trim();
        
        System.out.print("Nombre de usuario: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();
        
        System.out.println("Seleccione el rol:");
        System.out.println("1. Estándar");
        System.out.println("2. Administrador");
        System.out.print("Opción: ");
        
        try {
            int roleOption = Integer.parseInt(scanner.nextLine().trim());
            Role role = (roleOption == 2) ? Role.ADMINISTRATOR : Role.STANDARD;
            
            userService.createUser(fullName, username, password, role, currentUser);
        } catch (NumberFormatException e) {
            System.out.println("Opción de rol no válida. Se asignará rol Estándar por defecto.");
            userService.createUser(fullName, username, password, Role.STANDARD, currentUser);
        }
        
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Maneja la visualización de todos los usuarios (solo administradores)
     */
    private void handleViewAllUsers() {
        System.out.println("\n--- TODOS LOS USUARIOS ---");
        User[] allUsers = userService.getAllUsers(currentUser);
        
        if (allUsers.length == 0) {
            System.out.println("No hay usuarios en el sistema.");
        } else {
            System.out.println("Total de usuarios: " + allUsers.length);
            System.out.println("-".repeat(100));
            
            for (int i = 0; i < allUsers.length; i++) {
                User user = allUsers[i];
                System.out.printf("%d. %-15s | %-20s | %-15s | %-12s | %-8s | %d acciones\n",
                    (i + 1),
                    user.getUserId(),
                    user.getFullName(),
                    user.getUsername(),
                    user.getRole().getDisplayName(),
                    user.isBlocked() ? "BLOQUEADO" : "ACTIVO",
                    user.getActionCount()
                );
            }
        }
        
        historyService.registerAction(currentUser, "Consultó lista de todos los usuarios");
        
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Maneja la actualización de otro usuario (solo administradores)
     */
    private void handleUpdateOtherUser() {
        System.out.println("\n--- ACTUALIZAR OTRO USUARIO ---");
        System.out.print("ID del usuario a actualizar: ");
        String userId = scanner.nextLine().trim();
        
        User targetUser = userService.findUserById(userId);
        if (targetUser == null) {
            System.out.println("Usuario no encontrado.");
        } else {
            System.out.println("Usuario encontrado: " + targetUser.getFullName() + " (" + targetUser.getUsername() + ")");
            
            System.out.print("Nuevo nombre completo (Enter para no cambiar): ");
            String newName = scanner.nextLine().trim();
            if (newName.isEmpty()) newName = null;
            
            System.out.print("Nueva contraseña (Enter para no cambiar): ");
            String newPassword = scanner.nextLine();
            if (newPassword.isEmpty()) newPassword = null;
            
            String currentPassword = null;
            if (newPassword != null) {
                System.out.print("Contraseña actual del usuario: ");
                currentPassword = scanner.nextLine();
            }
            
            userService.updateUser(userId, newName, newPassword, currentPassword, currentUser);
        }
        
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Maneja la eliminación de un usuario (solo administradores)
     */
    private void handleDeleteUser() {
        System.out.println("\n--- ELIMINAR USUARIO ---");
        System.out.print("ID del usuario a eliminar: ");
        String userId = scanner.nextLine().trim();
        
        User targetUser = userService.findUserById(userId);
        if (targetUser == null) {
            System.out.println("Usuario no encontrado.");
        } else {
            System.out.println("Usuario encontrado: " + targetUser.getFullName() + " (" + targetUser.getUsername() + ")");
            System.out.print("¿Está seguro de eliminar este usuario? (s/N): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (confirmation.equals("s") || confirmation.equals("si")) {
                userService.deleteUser(userId, currentUser);
            } else {
                System.out.println("Eliminación cancelada.");
            }
        }
        
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Maneja la visualización del historial global (solo administradores)
     */
    private void handleViewGlobalHistory() {
        historyService.displayAllUsersHistory(currentUser, userService);
        
        System.out.println("Presione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Maneja el desbloqueo de usuarios (solo administradores)
     */
    private void handleUnblockUser() {
        System.out.println("\n--- DESBLOQUEAR USUARIO ---");
        System.out.print("ID del usuario a desbloquear: ");
        String userId = scanner.nextLine().trim();
        
        authService.unblockUser(userId, currentUser);
        
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Maneja el cambio de rol de usuarios (solo administradores)
     */
    private void handleChangeUserRole() {
        System.out.println("\n--- CAMBIAR ROL DE USUARIO ---");
        System.out.print("ID del usuario: ");
        String userId = scanner.nextLine().trim();
        
        User targetUser = userService.findUserById(userId);
        if (targetUser == null) {
            System.out.println("Usuario no encontrado.");
        } else {
            System.out.println("Usuario: " + targetUser.getFullName() + " (" + targetUser.getUsername() + ")");
            System.out.println("Rol actual: " + targetUser.getRole().getDisplayName());
            
            System.out.println("\nSeleccione nuevo rol:");
            System.out.println("1. Estándar");
            System.out.println("2. Administrador");
            System.out.print("Opción: ");
            
            try {
                int roleOption = Integer.parseInt(scanner.nextLine().trim());
                Role newRole = (roleOption == 2) ? Role.ADMINISTRATOR : Role.STANDARD;
                
                authService.changeUserRole(userId, newRole, currentUser);
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida.");
            }
        }
        
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Maneja el cierre de sesión
     */
    private void handleLogout() {
        if (currentUser != null) {
            authService.logout(currentUser);
            currentUser = null;
        }
    }
    
    /**
     * Muestra el estado del sistema
     */
    private void showSystemStatus() {
        System.out.println("\n--- ESTADO DEL SISTEMA ---");
        System.out.println("Total de usuarios registrados: " + userService.getUserCount());
        System.out.println("Usuario actual: " + (currentUser != null ? currentUser.getUsername() : "Ninguno"));
        System.out.println("Versión del sistema: 1.0");
        System.out.println("Memoria utilizada: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024) + " MB");
        
        if (currentUser != null) {
            historyService.registerAction(currentUser, "Consultó estado del sistema");
        }
        
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Muestra el mensaje de bienvenida
     */
    private void displayWelcomeMessage() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("        BIENVENIDO AL SISTEMA DE GESTIÓN DE USUARIOS");
        System.out.println("=".repeat(70));
        System.out.println("Este sistema permite gestionar usuarios, roles y acciones de forma segura.");
        System.out.println("Para comenzar, inicie sesión con las siguientes credenciales de prueba:");
        System.out.println("");
        System.out.println("👤 ADMINISTRADOR:");
        System.out.println("   Usuario: admin");
        System.out.println("   Contraseña: admin123");
        System.out.println("");
        System.out.println("👤 USUARIO ESTÁNDAR:");
        System.out.println("   Usuario: user1");
        System.out.println("   Contraseña: user123");
        System.out.println("=".repeat(70));
    }
    
    /**
     * Inicializa los usuarios por defecto del sistema
     */
    private void initializeDefaultUsers() {
        try {
            userService.createDefaultUsers();
        } catch (Exception e) {
            System.out.println("Advertencia: No se pudieron crear usuarios por defecto: " + e.getMessage());
        }
    }
}
