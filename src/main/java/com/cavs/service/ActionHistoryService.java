package com.cavs.service;

import com.cavs.model.Action;
import com.cavs.model.User;

/**
 * Servicio para gestión del historial de acciones de usuarios.
 * Permite registrar, consultar y mostrar el historial de actividades.
 * 
 * @author Carlos Velez
 * @version 1.0
 */
public class ActionHistoryService {
    
    /**
     * Registra una nueva acción para un usuario específico
     * @param user Usuario que realizó la acción
     * @param description Descripción de la acción
     */
    public void registerAction(User user, String description) {
        if (user == null) {
            System.out.println("Error: No se puede registrar acción para un usuario nulo.");
            return;
        }
        
        if (description == null || description.trim().isEmpty()) {
            System.out.println("Error: La descripción de la acción no puede estar vacía.");
            return;
        }
        
        try {
            Action action = new Action(description.trim(), user.getUserId());
            user.addAction(action);
        } catch (Exception e) {
            System.out.println("Error al registrar acción: " + e.getMessage());
        }
    }
    
    /**
     * Muestra el historial completo de acciones de un usuario
     * @param user Usuario cuyo historial se quiere mostrar
     */
    public void displayUserHistory(User user) {
        if (user == null) {
            System.out.println("Error: Usuario no válido.");
            return;
        }
        
        Action[] history = user.getActionHistory();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("HISTORIAL DE ACCIONES - " + user.getFullName() + " (" + user.getUsername() + ")");
        System.out.println("=".repeat(80));
        
        if (history.length == 0) {
            System.out.println("No hay acciones registradas para este usuario.");
        } else {
            System.out.println("Total de acciones: " + history.length);
            System.out.println("-".repeat(80));
            
            // Mostrar las acciones más recientes primero
            for (int i = history.length - 1; i >= 0; i--) {
                Action action = history[i];
                System.out.printf("%d. %s\n", (history.length - i), action.toString());
            }
        }
        
        System.out.println("=".repeat(80) + "\n");
    }
    
    /**
     * Obtiene el historial de acciones de un usuario
     * @param user Usuario cuyo historial se solicita
     * @return Array de acciones del usuario
     */
    public Action[] getUserHistory(User user) {
        if (user == null) {
            return new Action[0];
        }
        return user.getActionHistory();
    }
    
    /**
     * Muestra el historial de todos los usuarios (solo para administradores)
     * @param currentUser Usuario que solicita la información
     * @param userService Servicio de usuarios para obtener la lista completa
     */
    public void displayAllUsersHistory(User currentUser, UserService userService) {
        if (currentUser == null) {
            System.out.println("Error: Usuario no válido.");
            return;
        }
        
        // Verificar permisos
        if (!currentUser.getRole().canViewAllHistory()) {
            System.out.println("Error: No tienes permisos para ver el historial de todos los usuarios.");
            return;
        }
        
        User[] allUsers = userService.getAllUsers(currentUser);
        
        System.out.println("\n" + "=".repeat(100));
        System.out.println("HISTORIAL GLOBAL DE ACCIONES - VISTA DE ADMINISTRADOR");
        System.out.println("=".repeat(100));
        
        if (allUsers.length == 0) {
            System.out.println("No hay usuarios en el sistema.");
        } else {
            // Registrar que el admin consultó el historial global
            registerAction(currentUser, "Consultó historial global de todos los usuarios");
            
            for (User user : allUsers) {
                displayUserSummary(user);
                System.out.println("-".repeat(50));
            }
            
            displayGlobalStatistics(allUsers);
        }
        
        System.out.println("=".repeat(100) + "\n");
    }
    
    /**
     * Muestra un resumen del historial de un usuario específico
     * @param user Usuario del cual mostrar el resumen
     */
    private void displayUserSummary(User user) {
        Action[] history = user.getActionHistory();
        
        System.out.println("\nUsuario: " + user.getFullName() + " (" + user.getUsername() + ")");
        System.out.println("Rol: " + user.getRole().getDisplayName() + 
                          " | Estado: " + (user.isBlocked() ? "BLOQUEADO" : "ACTIVO"));
        System.out.println("Total de acciones: " + history.length);
        
        if (history.length > 0) {
            // Mostrar las últimas 3 acciones
            System.out.println("Últimas acciones:");
            int limit = Math.min(3, history.length);
            for (int i = history.length - 1; i >= history.length - limit; i--) {
                System.out.println("  • " + history[i].getFormattedTimestamp() + 
                                 ": " + history[i].getDescription());
            }
        } else {
            System.out.println("Sin acciones registradas.");
        }
    }
    
    /**
     * Muestra estadísticas globales del sistema
     * @param allUsers Array con todos los usuarios del sistema
     */
    private void displayGlobalStatistics(User[] allUsers) {
        System.out.println("\nESTADÍSTICAS GLOBALES:");
        System.out.println("-".repeat(30));
        
        int totalUsers = allUsers.length;
        int blockedUsers = 0;
        int administrators = 0;
        int standardUsers = 0;
        int totalActions = 0;
        
        for (User user : allUsers) {
            if (user.isBlocked()) {
                blockedUsers++;
            }
            
            switch (user.getRole()) {
                case ADMINISTRATOR:
                    administrators++;
                    break;
                case STANDARD:
                    standardUsers++;
                    break;
            }
            
            totalActions += user.getActionCount();
        }
        
        System.out.println("• Total de usuarios: " + totalUsers);
        System.out.println("• Usuarios activos: " + (totalUsers - blockedUsers));
        System.out.println("• Usuarios bloqueados: " + blockedUsers);
        System.out.println("• Administradores: " + administrators);
        System.out.println("• Usuarios estándar: " + standardUsers);
        System.out.println("• Total de acciones registradas: " + totalActions);
        
        if (totalUsers > 0) {
            double avgActionsPerUser = (double) totalActions / totalUsers;
            System.out.printf("• Promedio de acciones por usuario: %.2f\n", avgActionsPerUser);
        }
    }
    
    /**
     * Busca acciones que contengan una palabra clave específica
     * @param user Usuario en cuyo historial buscar
     * @param keyword Palabra clave a buscar
     * @return Array de acciones que contienen la palabra clave
     */
    public Action[] searchActions(User user, String keyword) {
        if (user == null || keyword == null || keyword.trim().isEmpty()) {
            return new Action[0];
        }
        
        Action[] allActions = user.getActionHistory();
        Action[] filteredActions = new Action[allActions.length];
        int count = 0;
        
        String lowerKeyword = keyword.toLowerCase().trim();
        
        for (Action action : allActions) {
            if (action.getDescription().toLowerCase().contains(lowerKeyword)) {
                filteredActions[count++] = action;
            }
        }
        
        // Crear array del tamaño exacto
        Action[] result = new Action[count];
        System.arraycopy(filteredActions, 0, result, 0, count);
        
        return result;
    }
    
    /**
     * Muestra acciones filtradas por palabra clave
     * @param user Usuario cuyo historial filtrar
     * @param keyword Palabra clave para filtrar
     */
    public void displayFilteredHistory(User user, String keyword) {
        Action[] filtered = searchActions(user, keyword);
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("HISTORIAL FILTRADO - " + user.getFullName() + 
                          " (Filtro: '" + keyword + "')");
        System.out.println("=".repeat(80));
        
        if (filtered.length == 0) {
            System.out.println("No se encontraron acciones que contengan '" + keyword + "'.");
        } else {
            System.out.println("Acciones encontradas: " + filtered.length);
            System.out.println("-".repeat(80));
            
            for (int i = 0; i < filtered.length; i++) {
                System.out.printf("%d. %s\n", (i + 1), filtered[i].toString());
            }
        }
        
        System.out.println("=".repeat(80) + "\n");
    }
    
    /**
     * Exporta el historial de un usuario a formato de texto
     * @param user Usuario cuyo historial exportar
     * @return String con el historial formateado para exportación
     */
    public String exportUserHistory(User user) {
        if (user == null) {
            return "Error: Usuario no válido para exportación.";
        }
        
        StringBuilder export = new StringBuilder();
        Action[] history = user.getActionHistory();
        
        // Registrar la acción de exportación
        registerAction(user, "Exportó su historial de acciones");
        
        // Generar encabezado
        export.append("EXPORTACIÓN DE HISTORIAL DE ACCIONES\n");
        export.append("=====================================\n");
        export.append("Usuario: ").append(user.getFullName()).append("\n");
        export.append("Username: ").append(user.getUsername()).append("\n");
        export.append("ID: ").append(user.getUserId()).append("\n");
        export.append("Rol: ").append(user.getRole().getDisplayName()).append("\n");
        export.append("Fecha de exportación: ").append(new java.util.Date()).append("\n");
        export.append("Total de acciones: ").append(history.length).append("\n");
        export.append("=====================================\n\n");
        
        // Agregar todas las acciones
        if (history.length > 0) {
            for (int i = 0; i < history.length; i++) {
                Action action = history[i];
                export.append(String.format("%d. %s\n", (i + 1), action.toString()));
            }
        } else {
            export.append("No hay acciones registradas.\n");
        }
        
        export.append("\n=====================================\n");
        export.append("Fin de la exportación\n");
        
        return export.toString();
    }
}
