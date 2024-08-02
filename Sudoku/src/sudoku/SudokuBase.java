package sudoku;

import javax.swing.JOptionPane;

public abstract class SudokuBase {

    protected int[][] tablero;

    public SudokuBase() {
        this.tablero = new int[9][9];
        generarSudoku();
    }

    protected abstract void generarSudoku();
    protected abstract boolean resolverSudoku(int fila, int columna);
    protected abstract boolean esNumeroValido(int numero, int fila, int columna);
    protected abstract void inicializarTablero();
    protected abstract void llenarNumerosIniciales();

    public void imprimir() {
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                System.out.print("[" + tablero[fila][columna] + "] ");
            }
            System.out.println();
        }
    }

    public int[][] getTablero() {
        return tablero;
    }

    public void ingresarNumero(int fila, int columna, int numero) {
        if (tablero[fila][columna] != 0) {
            mostrarMensaje("La celda ya contiene un número.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (esNumeroValido(numero, fila, columna)) {
            tablero[fila][columna] = numero;
            mostrarMensaje("Número ingresado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje("Número no válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, tipo);
    }
}
