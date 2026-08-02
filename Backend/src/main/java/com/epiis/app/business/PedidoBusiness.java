package com.epiis.app.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.epiis.app.dataaccess.EmpleadoRepository;
import com.epiis.app.dataaccess.PedidoItemRepository;
import com.epiis.app.dataaccess.PedidoRepository;
import com.epiis.app.dto.DtoPedido;
import com.epiis.app.dto.DtoPedidoDetalle;
import com.epiis.app.dto.DtoPedidoItemDetalle;
import com.epiis.app.entity.Empleado;
import com.epiis.app.entity.Pedido;
import com.epiis.app.entity.PedidoItem;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Service
public class PedidoBusiness {

    private final PedidoRepository pedidoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final Gson gson = new Gson();

    public PedidoBusiness(PedidoRepository pedidoRepository,
                          EmpleadoRepository empleadoRepository,
                          PedidoItemRepository pedidoItemRepository) {
        this.pedidoRepository = pedidoRepository;
        this.empleadoRepository = empleadoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
    }

    // 🔹 INSERTAR PEDIDO
    @Transactional
    public DtoPedido insert(DtoPedido dto) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Empleado empleado = empleadoRepository.findById("11111111-1111-1111-1111-111111111111")
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setIdPedido(UUID.randomUUID().toString());
        pedido.setEmpleado(empleado);
        pedido.setNombreCliente(dto.getNombreCliente());

        String tipoPedido = dto.getTipoPedido() == null ? "MESA" : dto.getTipoPedido().trim().toUpperCase();
        String metodoPago = dto.getMetodoPago() == null ? "EFECTIVO" : dto.getMetodoPago().trim().toUpperCase();
        String estado = dto.getEstado() == null || dto.getEstado().trim().isEmpty() ? "ESPERA" : dto.getEstado().trim().toUpperCase();

        pedido.setTipoPedido(Pedido.TipoPedido.valueOf(tipoPedido));
        pedido.setMetodoPago(Pedido.MetodoPago.valueOf(metodoPago));

        if (pedido.getTipoPedido() == Pedido.TipoPedido.MESA) {
            pedido.setMesa(dto.getMesa());
        } else {
            pedido.setMesa(null);
        }

        pedido.setEstado(Pedido.EstadoPedido.valueOf(estado));
        pedido.setTotalPagar(dto.getTotalPagar() == null ? 0.0 : dto.getTotalPagar().doubleValue());
        pedido.setCreatedAt(now);
        pedido.setUpdatedAt(now);

        pedidoRepository.save(pedido);

        dto.setIdPedido(pedido.getIdPedido());
        dto.setEstado(pedido.getEstado().name());
        dto.setCreatedAt(pedido.getCreatedAt());

        return dto;
    }

    // 🔹 LISTAR TODOS LOS PEDIDOS (AHORA CON ACOMPAÑAMIENTOS PARSEADOS)
    @Transactional(readOnly = true)
    public List<DtoPedido> getAll() {
        List<DtoPedido> lista = new ArrayList<>();
        for (Pedido p : pedidoRepository.findAllWithDetalles()) {
            DtoPedido dto = new DtoPedido();
            dto.setIdPedido(p.getIdPedido());

            if (p.getEmpleado() != null) {
                dto.setIdEmpleado(p.getEmpleado().getIdEmpleado());
            }
            dto.setNombreCliente(p.getNombreCliente());
            dto.setTipoPedido(p.getTipoPedido() != null ? p.getTipoPedido().name() : null);
            dto.setMetodoPago(p.getMetodoPago() != null ? p.getMetodoPago().name() : null);
            dto.setEstado(p.getEstado() != null ? p.getEstado().name() : null);
            dto.setMesa(p.getMesa());

            if (p.getTotalPagar() != null) {
                dto.setTotalPagar(java.math.BigDecimal.valueOf(p.getTotalPagar()));
            } else {
                dto.setTotalPagar(java.math.BigDecimal.ZERO);
            }

            dto.setMotivoCancelacion(p.getMotivoCancelacion());
            dto.setCreatedAt(p.getCreatedAt());

            // ✅ MAPEAR ITEMS CON ACOMPAÑAMIENTOS PARSEADOS
            if (p.getItems() != null && !p.getItems().isEmpty()) {
                List<DtoPedidoItemDetalle> itemsDto = p.getItems().stream().map(pi -> {
                    DtoPedidoItemDetalle itemDto = new DtoPedidoItemDetalle();
                    itemDto.setIdItem(pi.getIdItem());
                    itemDto.setNombreProducto(pi.getProducto() != null ? pi.getProducto().getNombre() : "Producto descontinuado");
                    itemDto.setCantidad(pi.getCantidad());
                    itemDto.setPrecioUnitarioFinal(pi.getPrecioUnitarioFinal());

                    // ✅ PARSEAR ACOMPAÑAMIENTOS (ahora sin ñ)
                    parseAcompanamientos(pi.getAcompanamientos(), itemDto);

                    return itemDto;
                }).collect(Collectors.toList());
                dto.setItems(itemsDto);
            }
            lista.add(dto);
        }
        return lista;
    }

    // ✅ NUEVO: OBTENER PEDIDOS DE UN CLIENTE (TODOS LOS ESTADOS)
    @Transactional(readOnly = true)
    public List<DtoPedido> getPedidosByCliente(String nombreCliente) {
        System.out.println("🔍 Buscando pedidos para cliente: " + nombreCliente);
        List<DtoPedido> lista = new ArrayList<>();

        // Buscar todos los pedidos del cliente
        List<Pedido> pedidos = pedidoRepository.findByNombreClienteWithItems(nombreCliente);
        System.out.println("📦 Pedidos encontrados: " + pedidos.size());

        for (Pedido p : pedidos) {
            DtoPedido dto = new DtoPedido();
            dto.setIdPedido(p.getIdPedido());

            if (p.getEmpleado() != null) {
                dto.setIdEmpleado(p.getEmpleado().getIdEmpleado());
            }
            dto.setNombreCliente(p.getNombreCliente());
            dto.setTipoPedido(p.getTipoPedido() != null ? p.getTipoPedido().name() : null);
            dto.setMetodoPago(p.getMetodoPago() != null ? p.getMetodoPago().name() : null);
            dto.setEstado(p.getEstado() != null ? p.getEstado().name() : null);
            dto.setMesa(p.getMesa());
            dto.setTotalPagar(p.getTotalPagar() != null
                    ? java.math.BigDecimal.valueOf(p.getTotalPagar())
                    : java.math.BigDecimal.ZERO);
            dto.setMotivoCancelacion(p.getMotivoCancelacion());
            dto.setCreatedAt(p.getCreatedAt());

            System.out.println("  → Pedido: " + p.getIdPedido() + " Estado: " + p.getEstado().name());

            // Mapear items
            if (p.getItems() != null && !p.getItems().isEmpty()) {
                List<DtoPedidoItemDetalle> itemsDto = p.getItems().stream().map(pi -> {
                    DtoPedidoItemDetalle itemDto = new DtoPedidoItemDetalle();
                    itemDto.setIdItem(pi.getIdItem());
                    itemDto.setNombreProducto(pi.getProducto() != null ? pi.getProducto().getNombre() : "Producto descontinuado");
                    itemDto.setCantidad(pi.getCantidad());
                    itemDto.setPrecioUnitarioFinal(pi.getPrecioUnitarioFinal());

                    parseAcompanamientos(pi.getAcompanamientos(), itemDto);

                    return itemDto;
                }).collect(Collectors.toList());
                dto.setItems(itemsDto);
            }

            lista.add(dto);
        }

        System.out.println("✅ Total de pedidos retornados: " + lista.size());
        return lista;
    }

    @Transactional(readOnly = true)
    public String getEstadoPorId(String idPedido) {
        return pedidoRepository.obtenerEstadoPorId(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"))
                .name();
    }

    @Transactional
    public void cambiarEstado(String idPedido, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(Pedido.EstadoPedido.valueOf(nuevoEstado.toUpperCase()));
        pedido.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void solicitarCancelacion(String idPedido, String motivo) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        if (pedido.getEstado() != Pedido.EstadoPedido.ESPERA && pedido.getEstado() != Pedido.EstadoPedido.PREPARACION) {
            throw new RuntimeException("Solo se puede solicitar cancelación en espera o preparación");
        }
        pedido.setEstado(Pedido.EstadoPedido.SOLICITUD_CANCELACION);
        pedido.setMotivoCancelacion(motivo);
        pedido.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void responderCancelacion(String idPedido, boolean aprobado) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        if (pedido.getEstado() != Pedido.EstadoPedido.SOLICITUD_CANCELACION) {
            throw new RuntimeException("El pedido no tiene una solicitud de cancelación activa");
        }
        if (aprobado) {
            pedido.setEstado(Pedido.EstadoPedido.CANCELADO);
        } else {
            pedido.setEstado(Pedido.EstadoPedido.PREPARACION);
            pedido.setMotivoCancelacion("CANCELACION_RECHAZADA");
        }
        pedido.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void finalizarPedido(String idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        if (pedido.getEstado() != Pedido.EstadoPedido.LISTO) {
            throw new RuntimeException("Solo se puede finalizar un pedido listo");
        }
        pedido.setEstado(Pedido.EstadoPedido.FINALIZADO);
        pedido.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public Pedido obtenerPedidoPorId(String idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    // 🟢 OPTIMIZADO: Ahora usa 'findByPedidoIdWithProducto' para traer items + producto en 1 solo SELECT
    @Transactional(readOnly = true)
    public DtoPedidoDetalle getPedidoConDetalle(String idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        DtoPedidoDetalle detalle = new DtoPedidoDetalle();
        detalle.setIdPedido(pedido.getIdPedido());
        detalle.setNombreCliente(pedido.getNombreCliente());
        detalle.setEstado(pedido.getEstado().name());
        detalle.setTipoPedido(pedido.getTipoPedido().name());
        detalle.setMetodoPago(pedido.getMetodoPago().name());
        detalle.setMesa(pedido.getMesa());

        detalle.setTotalPagar(pedido.getTotalPagar() != null
                ? java.math.BigDecimal.valueOf(pedido.getTotalPagar())
                : java.math.BigDecimal.ZERO);

        // 🟢 Se cambió findByPedido_IdPedido por el método optimizado con JOIN FETCH
        List<PedidoItem> items = pedidoItemRepository.findByPedidoIdPedido(idPedido);
        List<DtoPedidoItemDetalle> itemsDto = new ArrayList<>();

        for (PedidoItem pi : items) {
            DtoPedidoItemDetalle itemDto = new DtoPedidoItemDetalle();
            itemDto.setIdItem(pi.getIdItem());
            itemDto.setNombreProducto(pi.getProducto() != null ? pi.getProducto().getNombre() : "Producto descontinuado");
            itemDto.setCantidad(pi.getCantidad());
            itemDto.setPrecioUnitarioFinal(pi.getPrecioUnitarioFinal());

            // ✅ PARSEAR ACOMPAÑAMIENTOS (ahora sin ñ)
            parseAcompanamientos(pi.getAcompanamientos(), itemDto);

            itemsDto.add(itemDto);
        }
        detalle.setItems(itemsDto);
        return detalle;
    }

    // ✅ MÉTODO PRIVADO ACTUALIZADO: PARSEAR STRINGS SIMPLES DE ACOMPAÑAMIENTOS
    // Ahora maneja el formato "Toppings: Queso, Bacon | Salsas: BBQ, Picante"
    private void parseAcompanamientos(String acompanamientosTexto, DtoPedidoItemDetalle itemDto) {
        try {
            if (acompanamientosTexto == null || acompanamientosTexto.trim().isEmpty()) {
                itemDto.setToppings(new ArrayList<>());
                itemDto.setToppingsIds(new ArrayList<>());
                itemDto.setSalsas(new ArrayList<>());
                itemDto.setSalsasIds(new ArrayList<>());
                return;
            }

            List<String> toppingNames = new ArrayList<>();
            List<String> salsaNames = new ArrayList<>();

            // ✅ Dividir por " | " para separar toppings y salsas
            String[] partes = acompanamientosTexto.split("\\|");
            for (String parte : partes) {
                parte = parte.trim();

                if (parte.startsWith("Toppings:")) {
                    // Extraer nombres de toppings
                    String toppingsText = parte.substring("Toppings:".length()).trim();
                    String[] toppings = toppingsText.split(",");
                    for (String topping : toppings) {
                        toppingNames.add(topping.trim());
                    }
                } else if (parte.startsWith("Salsas:")) {
                    // Extraer nombres de salsas
                    String salsasText = parte.substring("Salsas:".length()).trim();
                    String[] salsas = salsasText.split(",");
                    for (String salsa : salsas) {
                        salsaNames.add(salsa.trim());
                    }
                }
            }

            itemDto.setToppings(toppingNames);
            itemDto.setToppingsIds(new ArrayList<>()); // Vacío porque no tenemos IDs en strings simples
            itemDto.setSalsas(salsaNames);
            itemDto.setSalsasIds(new ArrayList<>()); // Vacío porque no tenemos IDs en strings simples

            System.out.println("✅ Acompañamientos parseados - Toppings: " + toppingNames + ", Salsas: " + salsaNames);
        } catch (Exception e) {
            System.err.println("⚠️ Error al parsear acompañamientos: " + e.getMessage());
            itemDto.setToppings(new ArrayList<>());
            itemDto.setToppingsIds(new ArrayList<>());
            itemDto.setSalsas(new ArrayList<>());
            itemDto.setSalsasIds(new ArrayList<>());
        }
    }
}