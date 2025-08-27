package com.cavs;

import com.cavs.controller.UserController;

/**
 * Clase principal del Sistema de Gestión de Usuarios.
 * Punto de entrada de la aplicación.
 * 
 * @author Carlos Velez
 * @version 1.0
 */
public class Main {
    /**
     * Método principal que inicia la aplicación
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        try {
            // Crear e iniciar el controlador principal
            UserController controller = new UserController();
            controller.start();
        } catch (Exception e) {
            System.err.println("Error fatal al iniciar el sistema: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}