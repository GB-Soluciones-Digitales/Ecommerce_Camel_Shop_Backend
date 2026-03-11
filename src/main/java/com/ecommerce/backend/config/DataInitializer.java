package com.ecommerce.backend.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ecommerce.backend.model.Categoria;
import com.ecommerce.backend.model.Producto;
import com.ecommerce.backend.model.ProductoVariantes;
import com.ecommerce.backend.model.RolUsuario;
import com.ecommerce.backend.model.Usuario;
import com.ecommerce.backend.repository.CategoriaRepository;
import com.ecommerce.backend.repository.ProductoRepository;
import com.ecommerce.backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository; 
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // USUARIO ADMIN
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = Usuario.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .nombreCompleto("Admin Camel Shop")
                    .email("admin@camelshop.com")
                    .rol(RolUsuario.ADMIN)
                    .activo(true)
                    .build();
            usuarioRepository.save(admin);
        }

        // CATEGORÍAS Y PRODUCTOS
        if (categoriaRepository.count() == 0) {
            Categoria remeras = categoriaRepository.save(Categoria.builder().nombre("Remeras").build());
            Categoria vestidos = categoriaRepository.save(Categoria.builder().nombre("Vestidos").build());
            Categoria pantalones = categoriaRepository.save(Categoria.builder().nombre("Pantalones").build());
            Categoria camisas = categoriaRepository.save(Categoria.builder().nombre("Camisas").build());
            Categoria buzos = categoriaRepository.save(Categoria.builder().nombre("Buzos").build());

            // --- REMERAS ---
            crearProductoCompleto("Remera Oversize Earth", "Algodón pesado, calce relajado.", new BigDecimal("25000"), remeras, 
                List.of("https://images.unsplash.com/photo-1521572163474-6864f9cf17ab", "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a"),
                "Arena", Map.of("S", 10, "M", 20, "L", 15));

            crearProductoCompleto("Remera Boxy Black", "Corte cuadrado, estilo urbano.", new BigDecimal("26500"), remeras, 
                List.of("https://images.unsplash.com/photo-1503341504253-dff4815485f1"),
                "Negro", Map.of("M", 25, "L", 25, "XL", 10));

            // --- VESTIDOS ---
            crearProductoCompleto("Vestido Satin Night", "Elegancia pura para eventos.", new BigDecimal("55000"), vestidos, 
                List.of("https://images.unsplash.com/photo-1595777457583-95e059d581b8", "https://images.unsplash.com/photo-1496747611176-843222e1e57c"),
                "Negro", Map.of("S", 5, "M", 5));

            crearProductoCompleto("Vestido Floral Summer", "Liviano y fresco.", new BigDecimal("42000"), vestidos, 
                List.of("https://images.unsplash.com/photo-1572804013309-59a88b7e92f1"),
                "Estampado", Map.of("S", 8, "M", 8, "L", 4));

            // --- PANTALONES ---
            crearProductoCompleto("Jean Wide Leg Blue", "Denim rígido premium.", new BigDecimal("48000"), pantalones, 
                List.of("https://images.unsplash.com/photo-1542272454315-4c01d7abdf4a"),
                "Azul", Map.of("38", 5, "40", 10, "42", 10, "44", 5));

            crearProductoCompleto("Cargo Urbano Olive", "Múltiples bolsillos, tela ripstop.", new BigDecimal("52000"), pantalones, 
                List.of("https://images.unsplash.com/photo-1552902865-b72c031ac5ea"),
                "Oliva", Map.of("40", 12, "42", 12, "44", 8));

            // --- CAMISAS ---
            crearProductoCompleto("Camisa Linno Blanca", "100% lino para verano.", new BigDecimal("39000"), camisas, 
                List.of("https://images.unsplash.com/photo-1596755094514-f87e34085b2c", "https://images.unsplash.com/photo-1621072156002-e2fccdc0b176"),
                "Blanco", Map.of("M", 10, "L", 15, "XL", 10));

            crearProductoCompleto("Camisa Oxford Celeste", "Clásico atemporal.", new BigDecimal("35000"), camisas, 
                List.of("https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf"),
                "Celeste", Map.of("S", 5, "M", 10, "L", 10));

            // --- BUZOS ---
            crearProductoCompleto("Hoodie Heavyweight Camel", "Frisa premium abrigada.", new BigDecimal("45000"), buzos, 
                List.of("https://images.unsplash.com/photo-1556821840-3a63f95609a7"),
                "Camel", Map.of("S", 10, "M", 20, "L", 20, "XL", 10));

            crearProductoCompleto("Sweatshirt Minimal Grey", "Sin capucha, calce fit.", new BigDecimal("38000"), buzos, 
                List.of("https://images.unsplash.com/photo-1620799140408-edc6dcb6d633"),
                "Gris", Map.of("M", 15, "L", 15));

            System.out.println("✅ Camel Shop inicializado con éxito.");
        }
    }

    private void crearProductoCompleto(String nombre, String descripcion, BigDecimal precio, Categoria cat, List<String> imagenes, String color, Map<String, Integer> stockTalles) {
        int stockTotal = stockTalles.values().stream().mapToInt(Integer::intValue).sum();

        Producto p = Producto.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .precio(precio)
                .stock(stockTotal)
                .categoria(cat)
                .activo(true)
                .imagenes(imagenes)
                .build();

        ProductoVariantes variante = ProductoVariantes.builder()
                .color(color)
                .stockPorTalle(stockTalles)
                .producto(p) 
                .build();

        p.setVariantes(new ArrayList<>(List.of(variante)));
        
        productoRepository.save(p);
    }
}