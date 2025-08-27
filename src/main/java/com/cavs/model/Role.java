package com.cavs.model;

/**
 * Enumeración que define los roles disponibles en el sistema y sus permisos.
 * 
 * @author Carlos Velez
 * @version 1.0
 */
public enum Role {
    /**
     * Rol de Administrador con permisos completos sobre el sistema
     */
    ADMINISTRATOR("Administrador") {
        @Override
        public boolean canCreateUsers() {
            return true;
        }
        
        @Override
        public boolean canDeleteUsers() {
            return true;
        }
        
        @Override
        public boolean canUpdateOtherUsers() {
            return true;
        }
        
        @Override
        public boolean canViewAllUsers() {
            return true;
        }
        
        @Override
        public boolean canViewAllHistory() {
            return true;
        }
        
        @Override
        public boolean canUnblockUsers() {
            return true;
        }
        
        @Override
        public boolean canChangeUserRoles() {
            return true;
        }
    },
    
    /**
     * Rol Estándar con permisos limitados
     */
    STANDARD("Estándar") {
        @Override
        public boolean canCreateUsers() {
            return false;
        }
        
        @Override
        public boolean canDeleteUsers() {
            return false;
        }
        
        @Override
        public boolean canUpdateOtherUsers() {
            return false;
        }
        
        @Override
        public boolean canViewAllUsers() {
            return false;
        }
        
        @Override
        public boolean canViewAllHistory() {
            return false;
        }
        
        @Override
        public boolean canUnblockUsers() {
            return false;
        }
        
        @Override
        public boolean canChangeUserRoles() {
            return false;
        }
    };
    
    private final String displayName;
    
    /**
     * Constructor del enum Role
     * @param displayName Nombre amigable para mostrar del rol
     */
    Role(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * @return El nombre amigable del rol
     */
    public String getDisplayName() {
        return displayName;
    }
    
    // Métodos abstractos que cada rol debe implementar
    public abstract boolean canCreateUsers();
    public abstract boolean canDeleteUsers();
    public abstract boolean canUpdateOtherUsers();
    public abstract boolean canViewAllUsers();
    public abstract boolean canViewAllHistory();
    public abstract boolean canUnblockUsers();
    public abstract boolean canChangeUserRoles();
    
    /**
     * Verifica si el rol puede actualizar a un usuario específico
     * @param currentUserId ID del usuario que realiza la acción
     * @param targetUserId ID del usuario objetivo
     * @return true si tiene permisos, false en caso contrario
     */
    public boolean canUpdateUser(String currentUserId, String targetUserId) {
        // Un usuario siempre puede actualizarse a sí mismo
        if (currentUserId.equals(targetUserId)) {
            return true;
        }
        // Solo administradores pueden actualizar otros usuarios
        return this.canUpdateOtherUsers();
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
