# Sistema de Gestión de Usuarios

Un sistema completo de gestión de usuarios desarrollado en Java que implementa principios de Programación Orientada a Objetos (POO) y patrones de diseño robustos.

## 📋 Descripción del Proyecto

Este sistema permite gestionar usuarios con diferentes roles, controlar permisos de acceso, mantener un historial detallado de acciones y proporcionar características avanzadas de seguridad. Está diseñado como un proyecto educativo que demuestra la implementación práctica de conceptos fundamentales de POO.

## 🏗️ Arquitectura del Sistema

### Diagrama de Clases

El sistema está diseñado siguiendo una arquitectura por capas:

```
┌─────────────────┐
│   Presentación  │ ← UserController, Main
├─────────────────┤
│    Servicios    │ ← UserService, AuthenticationService, ActionHistoryService
├─────────────────┤
│     Modelo      │ ← User, Role, Action
└─────────────────┘
```

### Componentes Principales

#### 1. **Capa de Modelo (Model)**
- **`User`**: Entidad principal que representa un usuario del sistema
- **`Role`**: Enumeración que define roles y permisos (ADMINISTRATOR, STANDARD)
- **`Action`**: Representa acciones realizadas por usuarios para auditoría

#### 2. **Capa de Servicios (Service)**
- **`UserService`**: Gestión CRUD de usuarios y validación de permisos
- **`AuthenticationService`**: Manejo de autenticación, autorización y seguridad
- **`ActionHistoryService`**: Gestión del historial de acciones y auditoría

#### 3. **Capa de Controlador (Controller)**
- **`UserController`**: Interfaz de usuario y coordinación entre servicios
- **`Main`**: Punto de entrada de la aplicación

## 🚀 Características Principales

### ✅ Gestión de Usuarios
- ✅ Crear nuevos usuarios con validación completa
- ✅ Buscar usuarios por ID o username
- ✅ Actualizar información personal y contraseñas
- ✅ Eliminar usuarios del sistema
- ✅ Listado completo de usuarios (con control de permisos)

### 🔐 Sistema de Roles y Permisos
- ✅ **Rol Administrador**: Permisos completos sobre el sistema
- ✅ **Rol Estándar**: Acceso limitado a información propia
- ✅ Validación automática de permisos para cada operación
- ✅ Cambio dinámico de roles (solo administradores)

### 📊 Historial de Acciones
- ✅ Registro automático de todas las actividades
- ✅ Visualización completa del historial personal
- ✅ Búsqueda filtrada en el historial
- ✅ Exportación del historial a texto
- ✅ Vista global de auditoría (solo administradores)

### 🛡️ Características de Seguridad
- ✅ Bloqueo automático tras intentos fallidos (3 intentos)
- ✅ Desbloqueo de cuentas por administradores
- ✅ Validación robusta de credenciales
- ✅ Registro de intentos de acceso fallidos

### 🎯 Características Adicionales Implementadas
- ✅ Interfaz de usuario intuitiva con menús organizados
- ✅ Validaciones exhaustivas de entrada de datos
- ✅ Manejo de errores robusto
- ✅ Estadísticas del sistema
- ✅ Usuarios de demostración preconfigurados

## 🔧 Tecnologías y Herramientas

- **Lenguaje**: Java 17+
- **Build Tool**: Maven
- **Paradigma**: Programación Orientada a Objetos
- **Patrones**: MVC, Service Layer, Enum Strategy Pattern
- **Principios**: SOLID, Encapsulación, Abstracción, Herencia, Polimorfismo

## 📁 Estructura del Proyecto

```
src/main/java/com/cavs/
├── Main.java                    # Punto de entrada
├── controller/
│   └── UserController.java     # Controlador principal
├── service/
│   ├── UserService.java        # Servicio de gestión de usuarios
│   ├── AuthenticationService.java # Servicio de autenticación
│   └── ActionHistoryService.java  # Servicio de historial
├── model/
    ├── User.java              # Entidad Usuario
    ├── Role.java              # Enumeración de Roles
    └── Action.java            # Entidad Acción

```

## 🚀 Cómo Ejecutar el Proyecto

### Requisitos Previos
- Java 17 o superior
- Maven 3.6+

### Ejecución Paso a Paso

1. **Clonar o descargar el proyecto**
```bash
# Si tienes el código fuente
cd cuatomermanagement
```

2. **Compilar el proyecto**
```bash
mvn clean compile
```

3. **Ejecutar la aplicación principal**
```bash
mvn exec:java -Dexec.mainClass="com.cavs.Main"
```

## 👥 Usuarios de Demostración

El sistema incluye usuarios preconfigurados para pruebas:

| Usuario | Contraseña | Rol | Descripción |
|---------|------------|-----|-------------|
| `admin` | `admin123` | Administrador | Acceso completo al sistema |
| `user1` | `user123` | Estándar | Usuario con permisos limitados |

## 🎮 Guía de Uso

### Para Usuarios Estándar:
1. **Iniciar Sesión**: Use sus credenciales
2. **Ver Perfil**: Consulte su información personal
3. **Actualizar Perfil**: Modifique su nombre
4. **Cambiar Contraseña**: Actualice su contraseña
5. **Ver Historial**: Consulte sus acciones
6. **Buscar en Historial**: Filtre acciones por palabra clave
7. **Exportar Historial**: Obtenga un reporte de texto

### Para Administradores:
**Todas las funciones de usuario estándar, más:**
1. **Crear Usuarios**: Agregue nuevos usuarios al sistema
2. **Ver Todos los Usuarios**: Lista completa del sistema
3. **Actualizar Otros Usuarios**: Modifique información de cualquier usuario
4. **Eliminar Usuarios**: Remover usuarios del sistema
5. **Ver Historial Global**: Auditoría completa del sistema
6. **Desbloquear Usuarios**: Restaurar cuentas bloqueadas
7. **Cambiar Roles**: Promover/degradar usuarios
8. **Estado del Sistema**: Estadísticas y métricas

## 🔒 Características de Seguridad Implementadas

### Autenticación
- Validación de credenciales
- Gestión de sesiones básica
- Registro de intentos de acceso

### Autorización
- Control de permisos basado en roles
- Validación previa a cada operación
- Restricciones específicas por funcionalidad

### Auditoría
- Registro automático de todas las acciones
- Timestamps precisos
- Trazabilidad completa de actividades

### Protección de Cuentas
- Bloqueo automático tras intentos fallidos
- Contador de intentos por usuario
- Desbloqueo controlado por administradores

## 📈 Validaciones Implementadas

### Validaciones de Usuario
- Nombre completo: mínimo 2 caracteres
- Username: mínimo 3 caracteres, solo alfanuméricos y guiones bajos
- Contraseña: mínimo 4 caracteres
- Unicidad de username e ID

### Validaciones de Operaciones
- Permisos según rol del usuario
- Existencia de usuarios objetivo
- Validación de contraseñas actuales
- Prevención de auto-eliminación

### Validaciones de Datos
- Campos no nulos ni vacíos
- Formatos válidos
- Rangos apropiados

## 👨‍💻 Autor

**Carlos Velez**
- Versión: 1.0
- Desarrollado con fines educativos
- Implementación completa de conceptos de POO

---

*Este proyecto demuestra la aplicación práctica de principios de Programación Orientada a Objetos en un sistema real de gestión de usuarios, proporcionando una base sólida para comprender conceptos avanzados de desarrollo de software.*
