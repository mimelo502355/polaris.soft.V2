package com.epiis.app.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epiis.app.JwtService;
import com.epiis.app.dataaccess.EmpleadoRepository;
import com.epiis.app.dto.DtoEmpleado;
import com.epiis.app.entity.Empleado;

@RestController
@RequestMapping("empleado")
@CrossOrigin(origins = "http://localhost:4200")
public class EmpleadoController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private JwtService jwtService;

    // 🔹 Clase interna para Login
    public static class LoginRequest {
        private String nombre;
        private String password;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    // 🔹 1. LISTAR TODOS
    @GetMapping({"/getall", ""})
    public ResponseEntity<List<DtoEmpleado>> getAll() {
        List<Empleado> lista = empleadoRepository.findAll();
        List<DtoEmpleado> dtoList = lista.stream().map(emp -> {
            DtoEmpleado dto = new DtoEmpleado();
            dto.setIdEmpleado(emp.getIdEmpleado());
            dto.setNombre(emp.getNombre());
            dto.setPassword(emp.getPassword());
            dto.setRol(emp.getRol() != null ? emp.getRol() : "EMPLEADO");
            dto.setEstado(emp.getEstado() != null ? emp.getEstado() : true);
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    // 🔹 2. CREAR / INSERTAR
    @PostMapping({"/insert", ""})
    public ResponseEntity<?> insert(@RequestBody DtoEmpleado dto) {
        try {
            Empleado emp = new Empleado();

            if (dto.getIdEmpleado() != null && !dto.getIdEmpleado().trim().isEmpty()) {
                emp.setIdEmpleado(dto.getIdEmpleado());
            } else {
                emp.setIdEmpleado(UUID.randomUUID().toString());
            }

            emp.setNombre(dto.getNombre());
            emp.setPassword(dto.getPassword());

            String rolUpper = (dto.getRol() != null) ? dto.getRol().toUpperCase().trim() : "EMPLEADO";
            if (!rolUpper.equals("SUPERADMIN") && !rolUpper.equals("EMPLEADO")) {
                rolUpper = "EMPLEADO";
            }
            emp.setRol(rolUpper);
            emp.setEstado(dto.getEstado() != null ? dto.getEstado() : true);

            empleadoRepository.save(emp);

            return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Empleado registrado correctamente",
                "idEmpleado", emp.getIdEmpleado()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar empleado: " + e.getMessage()));
        }
    }

    // 🔹 3. ACTUALIZAR / EDITAR
    @PutMapping({"/update", "/update/{id}", "/{id}"})
    public ResponseEntity<?> update(@PathVariable(required = false) String id, @RequestBody DtoEmpleado dto) {
        try {
            String targetId = (id != null) ? id : dto.getIdEmpleado();
            if (targetId == null || targetId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El idEmpleado es obligatorio para actualizar");
            }

            Optional<Empleado> opt = empleadoRepository.findById(targetId);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado");
            }

            Empleado emp = opt.get();
            if (dto.getNombre() != null && !dto.getNombre().trim().isEmpty()) {
                emp.setNombre(dto.getNombre());
            }
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                emp.setPassword(dto.getPassword());
            }
            if (dto.getRol() != null && !dto.getRol().trim().isEmpty()) {
                String rolUpper = dto.getRol().toUpperCase().trim();
                if (rolUpper.equals("SUPERADMIN") || rolUpper.equals("EMPLEADO")) {
                    emp.setRol(rolUpper);
                }
            }
            if (dto.getEstado() != null) {
                emp.setEstado(dto.getEstado());
            }

            empleadoRepository.save(emp);

            return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Empleado actualizado correctamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar empleado: " + e.getMessage()));
        }
    }

    // 🔹 4. CAMBIAR ESTADO / DESACTIVAR
    @PutMapping({"/desactivar/{id}", "/estado/{id}"})
    public ResponseEntity<?> cambiarEstado(@PathVariable String id) {
        try {
            Optional<Empleado> opt = empleadoRepository.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado");
            }

            Empleado emp = opt.get();
            boolean nuevoEstado = (emp.getEstado() == null) ? false : !emp.getEstado();
            emp.setEstado(nuevoEstado);

            empleadoRepository.save(emp);

            return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Estado actualizado correctamente",
                "nuevoEstado", nuevoEstado
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cambiar estado: " + e.getMessage()));
        }
    }

    // 🔹 5. LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest body) {
        String nombre = body.getNombre();
        String password = body.getPassword();

        if (nombre == null || password == null) {
            return ResponseEntity.badRequest().body("Faltan parámetros");
        }

        Empleado empleado = empleadoRepository.findByNombre(nombre);
        if (empleado == null || !empleado.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        if (Boolean.FALSE.equals(empleado.getEstado())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El usuario se encuentra inactivo");
        }

        String token = jwtService.generateToken(nombre);
        return ResponseEntity.ok(Map.of(
            "token", token,
            "rol", empleado.getRol() != null ? empleado.getRol() : "EMPLEADO"
        ));
    }
    @DeleteMapping({"/{id}", "/delete/{id}"})
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            Optional<Empleado> opt = empleadoRepository.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Empleado no encontrado"));
            }

            Empleado emp = opt.get();

            // Protección en Backend: Impide eliminar al admin o roles SUPERADMIN
            if ("admin".equalsIgnoreCase(emp.getNombre()) || "SUPERADMIN".equalsIgnoreCase(emp.getRol())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "🚫 No es posible eliminar al SuperAdmin del sistema"));
            }

            empleadoRepository.deleteById(id);

            return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Empleado eliminado permanentemente"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar empleado: " + e.getMessage()));
        }
    }
}