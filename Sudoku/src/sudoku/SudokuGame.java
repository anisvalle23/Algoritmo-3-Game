package sudoku;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class SudokuGame extends JFrame implements KeyListener {

    private Logica clase = new Logica();
    private int[][] tablero = clase.getTablero();
    private JTextField[][] visual = new JTextField[9][9];
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color SUBGRID_COLOR = new Color(200, 200, 200);
    private static final Color CELL_COLOR = new Color(255, 255, 255);
    private static final Color HIGHLIGHT_COLOR = new Color(173, 216, 230); 
    private static final Color COMPLETE_COLOR = new Color(144, 238, 144); 
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Font CELL_FONT = new Font("Arial", Font.BOLD, 24);
    private JTextField lastSelectedCell = null;

    public SudokuGame() {
        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setTitle("Sudoku");
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new GridLayout(3, 3, 5, 5));
        getContentPane().setBackground(BACKGROUND_COLOR);
        JPanel[][] panels = new JPanel[3][3];

        for (int subFila = 0; subFila < 3; subFila++) {
            for (int subColumna = 0; subColumna < 3; subColumna++) {
                panels[subFila][subColumna] = new JPanel(new GridLayout(3, 3));
                panels[subFila][subColumna].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                panels[subFila][subColumna].setBackground(SUBGRID_COLOR);
                add(panels[subFila][subColumna]);
            }
        }

        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                visual[fila][columna] = new JTextField();
                visual[fila][columna].setFont(CELL_FONT);
                visual[fila][columna].setHorizontalAlignment(JTextField.CENTER);
                visual[fila][columna].addKeyListener(this);
                visual[fila][columna].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                visual[fila][columna].setBackground(CELL_COLOR);
                visual[fila][columna].setForeground(TEXT_COLOR);
                panels[fila / 3][columna / 3].add(visual[fila][columna]);
            }
        }

        Random rand = new Random();
        int dificultad = rand.nextInt(4) + 3;
        System.out.println("Dificultad seleccionada: " + dificultad);
        Mostrar(dificultad);
    }

    private void Mostrar(int dificultad) {
        int celdasVisibles = 81 - dificultad * 5;
        Random random = new Random();
        for (int ciclo = 0; ciclo < celdasVisibles; ciclo++) {
            int fila, columna;
            do {
                fila = random.nextInt(9);
                columna = random.nextInt(9);
            } while (!visual[fila][columna].getText().isEmpty());

            visual[fila][columna].setText(Integer.toString(tablero[fila][columna]));
            visual[fila][columna].setEditable(false);
        }
    }

    private boolean Victoria() {
        try {
            for (int fila = 0; fila < 9; fila++) {
                for (int columna = 0; columna < 9; columna++) {
                    if (visual[fila][columna].isEditable() && Integer.parseInt(visual[fila][columna].getText()) != tablero[fila][columna]) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isBoardComplete() {
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                if (visual[fila][columna].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void highlightRowAndColumn(JTextField selectedCell) {
        int fila = getFila(selectedCell);
        int columna = getColumna(selectedCell);

        for (int i = 0; i < 9; i++) {
            if (visual[fila][i].getBackground() != COMPLETE_COLOR) {
                visual[fila][i].setBackground(HIGHLIGHT_COLOR);
            }
            if (visual[i][columna].getBackground() != COMPLETE_COLOR) {
                visual[i][columna].setBackground(HIGHLIGHT_COLOR);
            }
        }
        selectedCell.setBackground(HIGHLIGHT_COLOR);
        lastSelectedCell = selectedCell;
    }

    private void resetHighlight() {
        if (lastSelectedCell != null) {
            int fila = getFila(lastSelectedCell);
            int columna = getColumna(lastSelectedCell);
            for (int i = 0; i < 9; i++) {
                if (visual[fila][i].getBackground() != COMPLETE_COLOR) {
                    visual[fila][i].setBackground(CELL_COLOR);
                }
                if (visual[i][columna].getBackground() != COMPLETE_COLOR) {
                    visual[i][columna].setBackground(CELL_COLOR);
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char tecla = e.getKeyChar();
        if (tecla < '1' || tecla > '9') {
            e.consume();
        }

        JTextField source = (JTextField) e.getSource();
        if (source.getText().length() >= 1) {
            e.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        JTextField source = (JTextField) e.getSource();
        resetHighlight();
        highlightRowAndColumn(source);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        JTextField source = (JTextField) e.getSource();
        try {
            int fila = getFila(source);
            int columna = getColumna(source);
            String texto = source.getText();
            if (texto.length() > 0) {
                int numero = Integer.parseInt(texto);
                if (!esNumeroValido(numero, fila, columna)) {
                   
                    JOptionPane errorPane = new JOptionPane("Número no válido en esta celda.", JOptionPane.ERROR_MESSAGE);
                    final javax.swing.JDialog dialog = errorPane.createDialog(this, "Error");
                    dialog.setModal(false);
                    dialog.setVisible(true);
                    Timer timer = new Timer(3000, e1 -> {
                        dialog.setVisible(false);
                        dialog.dispose();
                        source.setText("");
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else if (isBoardComplete()) {
                    if (Victoria()) {
                        int option = JOptionPane.showOptionDialog(this, "Ganó, el orden era correcto. ¿Volver a jugar?", "Victoria",
                                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Sí", "No"}, "Sí");
                        if (option == JOptionPane.YES_OPTION) {
                            new SudokuGame().setVisible(true);
                            this.dispose();
                        } else {
                            this.dispose();
                        }
                    }
                }
                
                if (isSubgridComplete(fila, columna)) {
                    colorSubgrid(fila, columna, COMPLETE_COLOR);
                }

                resetHighlight();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error al verificar el número.", "Error", JOptionPane.ERROR_MESSAGE);
            source.setText("");
        }
    }

    private int getFila(JTextField source) {
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                if (visual[fila][columna] == source) {
                    return fila;
                }
            }
        }
        return -1;
    }

    private int getColumna(JTextField source) {
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                if (visual[fila][columna] == source) {
                    return columna;
                }
            }
        }
        return -1;
    }

    private boolean esNumeroValido(int numero, int fila, int columna) {
        return !enFila(numero, fila, columna) && !enColumna(numero, columna, fila) && !enSubCuadricula(numero, fila, columna);
    }

    private boolean enFila(int numero, int fila, int columna) {
        for (int index = 0; index < 9; index++) {
            if (index != columna && !visual[fila][index].getText().isEmpty() && Integer.parseInt(visual[fila][index].getText()) == numero) {
                return true;
            }
        }
        return false;
    }

    private boolean enColumna(int numero, int columna, int fila) {
        for (int index = 0; index < 9; index++) {
            if (index != fila && !visual[index][columna].getText().isEmpty() && Integer.parseInt(visual[index][columna].getText()) == numero) {
                return true;
            }
        }
        return false;
    }

    private boolean enSubCuadricula(int numero, int fila, int columna) {
        int inicioFila = (fila / 3) * 3;
        int inicioColumna = (columna / 3) * 3;

        for (int index = inicioFila; index < inicioFila + 3; index++) {
            for (int j = inicioColumna; j < inicioColumna + 3; j++) {
                if ((index != fila || j != columna) && !visual[index][j].getText().isEmpty() && Integer.parseInt(visual[index][j].getText()) == numero) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSubgridComplete(int fila, int columna) {
        int inicioFila = (fila / 3) * 3;
        int inicioColumna = (columna / 3) * 3;
        boolean[] seen = new boolean[10]; 

        for (int index = inicioFila; index < inicioFila + 3; index++) {
            for (int j = inicioColumna; j < inicioColumna + 3; j++) {
                if (visual[index][j].getText().isEmpty()) {
                    return false;
                }
                int num = Integer.parseInt(visual[index][j].getText());
                if (seen[num]) {
                    return false; 
                }
                seen[num] = true;
            }
        }
        return true;
    }

    private void colorSubgrid(int fila, int columna, Color color) {
        int inicioFila = (fila / 3) * 3;
        int inicioColumna = (columna / 3) * 3;

        for (int index = inicioFila; index < inicioFila + 3; index++) {
            for (int j = inicioColumna; j < inicioColumna + 3; j++) {
                visual[index][j].setBackground(color);
            }
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new SudokuGame().setVisible(true);
        });
    }
}
