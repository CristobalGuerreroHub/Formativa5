package formativa_6;
import java.util.ArrayList;
import java.util.Scanner;

public class Formativa_6 {
  
    static int totalEntradasVendidas = 0;
    static double totalIngresos = 0.0;
    static int numeroEntradaActual = 1;

    static ArrayList<Entrada> entradasVendidas = new ArrayList<>();

    static int[] stock = {40, 40, 40, 40};
    static String[] Zonas = {"VIP", "Platea Baja", "Platea Alta", "Palcos"};
    static double[] precios = {
        30000, 15000, 18000, 13000
    };

    static boolean[][] asientosReservados = new boolean[Zonas.length][40];
    static long[][] tiempoReserva = new long[Zonas.length][40];

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("===== MENÚ PRINCIPAL =====");
            System.out.println("1. Comprar entradas");
            System.out.println("2. Ver promociones");
            System.out.println("3. Buscar entrada");
            System.out.println("4. Eliminar entrada");
            System.out.println("5. Modificar entrada");
            System.out.println("6. Ver estadísticas");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1 -> comprarEntrada(scanner);
                case 2 -> mostrarPromociones();
                case 3 -> buscarEntrada(scanner);
                case 4 -> eliminarEntrada(scanner);
                case 5 -> modificarEntrada(scanner);
                case 6 -> mostrarEstadisticas();
                case 7 -> System.out.println("¡Hasta luego!");
                default -> System.out.println("Opción inválida.");
            }
        } while (opcion != 7);

        scanner.close();
    }

    static void limpiarReservasExpiradas() {
        long ahora = System.currentTimeMillis();
        for (int i = 0; i < Zonas.length; i++) {
            for (int j = 0; j < 40; j++) {
                if (asientosReservados[i][j] && (ahora - tiempoReserva[i][j]) > 30000) {
                    asientosReservados[i][j] = false;
                    tiempoReserva[i][j] = 0;
                    System.out.println("[Reserva expirada] Zona: " + Zonas[i] + ", Asiento: " + (j + 1));
                }
            }
        }
    }

    static void comprarEntrada(Scanner scanner) {
        limpiarReservasExpiradas();
        System.out.println("Iniciando proceso de compra de entrada");
        System.out.println("Zonas disponibles:");
        for (int i = 0; i < Zonas.length; i++) {
            System.out.println((i + 1) + ". " + Zonas[i] + " [Stock: " + stock[i] + "]");
        }
        System.out.print("Seleccione la zona (1-4): ");
        int zona = scanner.nextInt();

        if (zona < 1 || zona > Zonas.length || stock[zona - 1] == 0) {
            System.out.println("Zona inválida o sin stock.");
            return;
        }
        System.out.print("Ingrese número de asiento (1-40): ");
        int asiento = scanner.nextInt();
        if (asiento < 1 || asiento > 40 || asientosReservados[zona - 1][asiento - 1]) {
            System.out.println("Asiento inválido o ya reservado.");
            return;
        }
        asientosReservados[zona - 1][asiento - 1] = true;
        tiempoReserva[zona - 1][asiento - 1] = System.currentTimeMillis();
        System.out.println("Asiento reservado por 30 segundos. Complete su compra antes de que expire la reserva.");

        System.out.println("¿Desea continuar con la compra? (s/n): ");
        String respuesta = scanner.next();
        if (!respuesta.equalsIgnoreCase("s")) {
            asientosReservados[zona - 1][asiento - 1] = false;
            tiempoReserva[zona - 1][asiento - 1] = 0;
            System.out.println("Compra cancelada. El asiento fue liberado.");
            return;
        }

        limpiarReservasExpiradas();

        if (!asientosReservados[zona - 1][asiento - 1]) {
            System.out.println("Tiempo expirado. El asiento fue liberado.");
            return;
        }
        System.out.print("Tipo de persona (1. Estudiante, 2. Público General, 3. Tercera Edad): ");
        int tipoPersona = scanner.nextInt();

        System.out.print("Ingrese edad: ");
        int edad = scanner.nextInt();

        if (tipoPersona < 1 || tipoPersona > 3) {
            System.out.println("Tipo no válido.");
            return;
        }
        
        double precioBase = precios[zona - 1];
        double descuento = 0.0;
        if (tipoPersona == 1) descuento = 0.10;
        else if (tipoPersona == 3) descuento = 0.15;
        double precioFinal = precioBase * (1 - descuento);
        precioFinal = Math.round(precioFinal * 100.0) / 100.0;
       

        Entrada entrada = new Entrada(numeroEntradaActual, Zonas[zona - 1], 
                tipoPersona, edad, precioFinal, precioBase, asiento);
        entradasVendidas.add(entrada);
        totalEntradasVendidas++;
        totalIngresos += precioFinal;
        stock[zona - 1]--;
        asientosReservados[zona - 1][asiento - 1] = true;
        tiempoReserva[zona - 1][asiento - 1] = 0;

        System.out.println("Compra realizada con éxito. Entrada registrada:");
        System.out.println(entrada);
        imprimirBoleta(entrada);
    }

    static void mostrarPromociones() {
        System.out.println("*** PROMOCIONES DISPONIBLES ***");
        System.out.println("1. 10% de descuento para estudiantes");
        System.out.println("2. 15% de descuento para tercera edad (>60 años)");
        System.out.println("3. 15% de descuento adicional por comprar 3 o más entradas.");
    }

    static void buscarEntrada(Scanner scanner) {
        System.out.println("*** BUSCAR ENTRADA ***");
        System.out.println("1. Por número");
        System.out.println("2. Por zona");
        System.out.print("Seleccione una opción: ");
        int criterio = scanner.nextInt();

        switch (criterio) {
            case 1 -> {
                System.out.print("Ingrese el número de entrada: ");
                int num = scanner.nextInt();
                Entrada entrada = entradasVendidas.stream().filter(e -> e.numero == num).findFirst().orElse(null);
                if (entrada != null) System.out.println(entrada);
                else System.out.println("Entrada no encontrada.");
            }
            case 2 -> {
                System.out.print("Ingrese el nombre de la zona: ");
                String zona = scanner.next();
                entradasVendidas.stream()
                        .filter(e -> e.zona.equalsIgnoreCase(zona))
                        .forEach(System.out::println);
            }
            default -> System.out.println("Opción inválida.");
        }
    }

    static void eliminarEntrada(Scanner scanner) {
        System.out.print("Ingrese el número de entrada a eliminar: ");
        int num = scanner.nextInt();
        Entrada entrada = entradasVendidas.stream().filter(e -> e.numero == num).findFirst().orElse(null);
        if (entrada != null) {
            entradasVendidas.remove(entrada);
            totalEntradasVendidas--;
            totalIngresos -= entrada.precioFinal;
            for (int i = 0; i < Zonas.length; i++) {
                if (Zonas[i].equalsIgnoreCase(entrada.zona)) {
                    asientosReservados[i][entrada.asiento - 1] = false;
                    stock[i]++;
                }
            }
            System.out.println("Entrada eliminada correctamente.");
        } else {
            System.out.println("Entrada no encontrada.");
        }
    }

    static void modificarEntrada(Scanner scanner) {
        System.out.print("Ingrese el número de entrada a modificar: ");
        int num = scanner.nextInt();
        Entrada entrada = entradasVendidas.stream().filter(e -> e.numero == num).findFirst().orElse(null);
        if (entrada == null) {
            System.out.println("Entrada no encontrada.");
            return;
        }

        System.out.println("Nueva zona (1. VIP, 2. Platea Baja, 3. Platea Alta, 4. Palcos): ");
        int nuevaZona = scanner.nextInt();
        System.out.print("Nuevo asiento (1-40): ");
        int nuevoAsiento = scanner.nextInt();
        if (asientosReservados[nuevaZona - 1][nuevoAsiento - 1]) {
            System.out.println("Asiento inválido o ya reservado.");
            return;
        }

        System.out.println("Nuevo tipo de persona (1. Estudiante, 2. Público General, 3. Tercera Edad): ");
        int nuevoTipo = scanner.nextInt();
        System.out.print("Nueva edad: ");
        int nuevaEdad = scanner.nextInt();

        for (int i = 0; i < Zonas.length; i++) {
            if (Zonas[i].equalsIgnoreCase(entrada.zona)) {
                asientosReservados[i][entrada.asiento - 1] = false;
                stock[i]++;
            }
        }
        double precioInicial;
        double nuevoPrecio = precios[nuevaZona - 1];
        double descuento = 0.0;
        if (nuevoTipo == 1) descuento = 0.10;
        else if (nuevaEdad > 60) descuento = 0.15;

        nuevoPrecio = nuevoPrecio * (1 - descuento);
        nuevoPrecio = Math.round(nuevoPrecio * 100.0) / 100.0;
        if (nuevoTipo == 1 && nuevaEdad < 18) {
            nuevoPrecio *= 0.90;
            nuevoPrecio = Math.round(nuevoPrecio * 100.0) / 100.0;
        }

        entrada.zona = Zonas[nuevaZona - 1];
        entrada.tipoPersona = nuevoTipo;
        entrada.edad = nuevaEdad;
        entrada.precioFinal = nuevoPrecio;
        entrada.asiento = nuevoAsiento;

        asientosReservados[nuevaZona - 1][nuevoAsiento - 1] = true;
        stock[nuevaZona - 1]--;

        System.out.println("Entrada modificada con éxito.");
    }
static int calcularDescuento(int tipoPersona) {
    if (tipoPersona == 1) return 10;  // Estudiante
    if (tipoPersona == 3) return 15;  // Tercera Edad
    return 0;  // Público general
}
    static void mostrarEstadisticas() {
        System.out.println("*** ESTADÍSTICAS ***");
        System.out.println("Total de entradas vendidas: " + totalEntradasVendidas);
        System.out.println("Ingresos totales: $" + Math.round(totalIngresos * 100.0) / 100.0);
    }
static void imprimirBoleta(Entrada entrada) {
    System.out.println("--------------------------------------------");
    System.out.println("                Teatro Moro                 ");
    System.out.println("--------------------------------------------");
    System.out.println("Número de boleta: " + entrada.numero);
    System.out.println("Ubicación: " + entrada.zona);
    System.out.println("Asiento: " + entrada.asiento);
    System.out.println("Tipo de Persona: " + (entrada.tipoPersona == 1 ? "Estudiante" : 
                                          entrada.tipoPersona == 2 ? "Público General" :  "Tercera Edad"));
    System.out.println("Edad: " + entrada.edad + " años");
    System.out.println("Costo Base: $" + entrada.precioBase);
    System.out.println("Descuento Aplicado: " + calcularDescuento(entrada.tipoPersona) + "%");
    System.out.println("Costo Final: $" + entrada.precioFinal);
    System.out.println("--------------------------------------------");
    System.out.println("           Gracias por su compra            ");
    System.out.println("--------------------------------------------");
}
    static class Entrada {
        int numero;
        String zona;
        int tipoPersona;
        int edad;
        double precioBase;
        double precioFinal;
        int asiento;

        Entrada(int numero, String zona, int tipoPersona, int edad, double precioFinal, double precioBase, int asiento) {
          this.numero = numero;
          this.zona = zona;
          this.tipoPersona = tipoPersona;
          this.edad = edad;
          this.precioFinal = precioFinal;
          this.precioBase = precioBase;
          this.asiento = asiento;
}

        public String toString() {
            String tipoStr = tipoPersona == 1 ? "Estudiante" : "Público General";
            return "Entrada N°: " + numero +
                    ", Zona: " + zona +
                    ", Asiento: " + asiento +
                    ", Tipo: " + tipoStr +
                    ", Edad: " + edad +
                    ", Precio final: $" + precioFinal;
        }
    }
}