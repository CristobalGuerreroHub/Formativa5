package formativa5|;

import java.util.ArrayList;
import java.util.Scanner;

public class Formativa5 {

    // Variables estáticas (estadísticas globales)
    static int totalEntradasVendidas = 0;
    static double totalIngresos = 0;
    static int numeroEntradaActual = 1;

    // Variables de instancia (almacenan entradas vendidas)
    static ArrayList<Entrada> entradasVendidas = new ArrayList<>();

    // Stock de entradas por zona
    static int[] stock = {40, 40, 40, 40};
    static String[] zonas = {"VIP", "Platea Baja", "Platea Alta", "Palcos"};
    static double[][] precios = {
        {16808.72, 22518.80},  // VIP
        {14356.12, 19286.08},  // Platea Baja
        {7680.33, 11526.45},   // Platea Alta
        {5462.18, 10924.37}    // Palcos
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n==== MENÚ PRINCIPAL ====");
            System.out.println("1. Comprar entrada");
            System.out.println("2. Ver promociones");
            System.out.println("3. Buscar entrada");
            System.out.println("4. Eliminar entrada");
            System.out.println("5. Ver estadísticas");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1 -> comprarEntrada(scanner);
                case 2 -> mostrarPromociones();
                case 3 -> buscarEntrada(scanner);
                case 4 -> eliminarEntrada(scanner);
                case 5 -> mostrarEstadisticas();
                case 0 -> System.out.println("¡Hasta luego!");
                default -> System.out.println("Opción inválida.");
            }
        } while (opcion != 0);

        scanner.close();
    }

    static void comprarEntrada(Scanner scanner) {
        System.out.println("\nZonas disponibles:");
        for (int i = 0; i < zonas.length; i++) {
            System.out.println((i + 1) + ". " + zonas[i] + " (Stock: " + stock[i] + ")");
        }

        System.out.print("Seleccione la zona (1-4): ");
        int zona = scanner.nextInt();
        if (zona < 1 || zona > 4 || stock[zona - 1] <= 0) {
            System.out.println("Zona no válida o sin stock.");
            return;
        }

        System.out.print("Tipo de persona (1. Estudiante, 2. Público General): ");
        int tipoPersona = scanner.nextInt();
        if (tipoPersona < 1 || tipoPersona > 2) {
            System.out.println("Tipo no válido.");
            return;
        }

        System.out.print("Edad del cliente: ");
        int edad = scanner.nextInt();
        if (edad < 0 || edad > 120) {
            System.out.println("Edad no válida.");
            return;
        }

        System.out.print("¿Cuántas entradas desea comprar?: ");
        int cantidad = scanner.nextInt();
        if (cantidad < 1 || cantidad > stock[zona - 1]) {
            System.out.println("Cantidad inválida o no hay suficiente stock.");
            return;
        }

        for (int i = 0; i < cantidad; i++) {
            double precioBase = precios[zona - 1][tipoPersona - 1];
            double iva = precioBase * 0.19;
            double precioConIVA = precioBase + iva;

            double descuento = 0;
            if (edad < 18) descuento = 0.10;
            else if (edad > 60) descuento = 0.15;

            if (cantidad >= 3) descuento += 0.05; // Descuento por compra múltiple

            double precioFinal = precioConIVA * (1 - descuento);
            int precioRedondeado = (int) Math.round(precioFinal);

            // Crear y registrar entrada
            Entrada entrada = new Entrada(numeroEntradaActual++, zonas[zona - 1], tipoPersona, edad, precioRedondeado);
            entradasVendidas.add(entrada);

            stock[zona - 1]--;
            totalEntradasVendidas++;
            totalIngresos += precioRedondeado;
        }

        System.out.println("¡Compra realizada con éxito! " + cantidad + " entradas registradas.");
    }

    static void mostrarPromociones() {
        System.out.println("\n=== PROMOCIONES DISPONIBLES ===");
        System.out.println("- 10% de descuento para estudiantes (<18 años).");
        System.out.println("- 15% de descuento para adultos mayores (>60 años).");
        System.out.println("- 5% de descuento adicional por comprar 3 o más entradas.");
    }

    static void buscarEntrada(Scanner scanner) {
        System.out.println("\nBuscar por:");
        System.out.println("1. Número de entrada");
        System.out.println("2. Zona");
        System.out.println("3. Tipo de persona");
        System.out.print("Seleccione una opción: ");
        int criterio = scanner.nextInt();

        switch (criterio) {
            case 1 -> {
                System.out.print("Ingrese el número de entrada: ");
                int num = scanner.nextInt();
                entradasVendidas.stream()
                    .filter(e -> e.numero == num)
    .findFirst()
    .ifPresentOrElse(
        System.out::println,
        () -> System.out.println("Entrada no encontrada.")
                    );
            }
            case 2 -> {
                System.out.print("Ingrese el nombre de la zona (VIP, Platea Baja, etc.): ");
                scanner.nextLine(); // limpiar buffer
                String zona = scanner.nextLine();
                entradasVendidas.stream()
                    .filter(e -> e.zona.equalsIgnoreCase(zona))
                    .forEach(System.out::println);
            }
            case 3 -> {
                System.out.print("Ingrese tipo (1. Estudiante, 2. Público General): ");
                int tipo = scanner.nextInt();
                entradasVendidas.stream()
                    .filter(e -> e.tipoPersona == tipo)
                    .forEach(System.out::println);
            }
            default -> System.out.println("Opción no válida.");
        }
    }

    static void eliminarEntrada(Scanner scanner) {
        System.out.print("Ingrese el número de la entrada a eliminar: ");
        int num = scanner.nextInt();

        Entrada entrada = entradasVendidas.stream()
            .filter(e -> e.numero == num)
            .findFirst()
            .orElse(null);

        if (entrada != null) {
            entradasVendidas.remove(entrada);
            totalEntradasVendidas--;
            totalIngresos -= entrada.precioFinal;
            // Devolver stock
            for (int i = 0; i < zonas.length; i++) {
                if (zonas[i].equalsIgnoreCase(entrada.zona)) {
                    stock[i]++;
                    break;
                }
            }
            System.out.println("Entrada eliminada correctamente.");
        } else {
            System.out.println("Entrada no encontrada.");
        }
    }

    static void mostrarEstadisticas() {
        System.out.println("\n=== ESTADÍSTICAS ===");
        System.out.println("Total de entradas vendidas: " + totalEntradasVendidas);
        int ingresoRedondeado = (int) (Math.round(totalIngresos / 10.0) * 10);
        System.out.println("Ingresos totales: $" + ingresoRedondeado);
    }

    // Clase Entrada
    static class Entrada {
        int numero;
        String zona;
        int tipoPersona;
        int edad;
        double precioFinal;

        Entrada(int numero, String zona, int tipoPersona, int edad, double precioFinal) {
            this.numero = numero;
            this.zona = zona;
            this.tipoPersona = tipoPersona;
            this.edad = edad;
            this.precioFinal = precioFinal;
        }

        public String toString() {
            String tipoStr = (tipoPersona == 1) ? "Estudiante" : "Público General";
            return "Entrada #" + numero + " | Zona: " + zona + " | Tipo: " + tipoStr +
                    " | Edad: " + edad + " | Precio final: $" + precioFinal;
        }
    }
}
