import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;

public class PonteBarcoSimulator extends JFrame {

    //Paleta de cores do projeto
    private final Color COLOR_FUNDO = new Color(36, 48, 56);
    private final Color COLOR_PAINEIS = new Color(45, 55, 65);
    private final Color COLOR_ACCENT = new Color(15, 188, 249); // Azul Brilhante
    private final Color COLOR_TEXTO = Color.WHITE;

    // Componentes de Input
    private JComboBox<String> gravidadeBox;
    private JTextField velChaveField;
    private JSlider sliderH, sliderL; 
    private JTextField distBrinqField, acelBrinqField;
    private JTextField distRealField, acelRealField;
    private JTabbedPane abas;
    
    // Componentes de Output
    private JLabel tempoResult, velResult, localImpactoResult, statusResult;

    public PonteBarcoSimulator() {
        //adiciona a possibilidade de fazer uma interface mais elaborada
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
       
        //Determina as informações básicas da interface
        setTitle("Ponte e Barco Simulator - CC 2º Ano");
        setSize(1150, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(COLOR_FUNDO);
        setLayout(new BorderLayout(15, 15));

        //Cria as colunas dos INPUTS ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(320, 0));
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 10));

        // Parâmetros Globais
        JPanel globalPanel = criarSecao("PARÂMETROS GLOBAIS");
        globalPanel.setLayout(new GridLayout(3, 1, 8, 8));
        
        //Cria o parâmetro da gravidade, especificando que pode ser ou 9.8 ou 10
        gravidadeBox = new JComboBox<>(new String[]{"9.80", "10.00"});
        velChaveField = new JTextField("0.00");
        
        //cria o slide para determinar a altura da ponte
        sliderH = new JSlider(2, 100, 45);
        sliderH.setOpaque(false);
        sliderH.addChangeListener(e -> repaint());

        //Cria os inputs gerais e informa as suas limitações
        globalPanel.add(criarInputComRotulo("GRAVIDADE (G):", gravidadeBox));
        globalPanel.add(criarInputComRotulo("V0 CHAVE (m/s) [-30/30]:", velChaveField));
        globalPanel.add(criarInputComRotulo("ALTURA (H) [2-100m]:", sliderH));

        // Abas para os dois casos possíveis do problema
        abas = new JTabbedPane();
        abas.addChangeListener(e -> repaint());

        // Aba Acadêmico(brinquedo)
        JPanel abaBrinq = new JPanel(new GridLayout(2, 1, 8, 8));
        abaBrinq.setBackground(COLOR_PAINEIS);
        distBrinqField = new JTextField("12.00");
        acelBrinqField = new JTextField("0.00");
        //Limitações dos inputs do academico
        abaBrinq.add(criarInputComRotulo("D0 (1-50m):", distBrinqField));
        abaBrinq.add(criarInputComRotulo("ACELERAÇÃO (0-1.5):", acelBrinqField));

        // Aba Realista
        JPanel abaReal = new JPanel(new GridLayout(3, 1, 8, 8));
        abaReal.setBackground(COLOR_PAINEIS);
        distRealField = new JTextField("60.00");
        acelRealField = new JTextField("1.50");
        //Cria o slide para colocar o input do tamanho do barco
        sliderL = new JSlider(3, 8, 5);
        sliderL.setOpaque(false);
        sliderL.addChangeListener(e -> repaint());
        
        //Apresenta as limitações dos inputs do barco realista
        abaReal.add(criarInputComRotulo("D0 (50-100m):", distRealField));
        abaReal.add(criarInputComRotulo("ACELERAÇÃO (0-3.0):", acelRealField));
        abaReal.add(criarInputComRotulo("COMPRIMENTO (L) [3-8m]:", sliderL));

        abas.addTab("ACADÊMICO", abaBrinq);
        abas.addTab("REALISTA", abaReal);

        leftPanel.add(globalPanel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(abas);

        // Faz a coluna central da ponte e do barquinho
        CanvasVisualizacao visualPanel = new CanvasVisualizacao();

        // Coluna da direita dos resultados
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(300, 0));
        rightPanel.setBorder(new EmptyBorder(20, 10, 20, 20));

        JPanel resultPanel = criarSecao("RESULTADOS E AÇÕES");
        resultPanel.setLayout(new GridLayout(7, 1, 10, 10));
        
        tempoResult = criarLabelRes("Tempo Queda: ---");
        velResult = criarLabelRes("Vel. Barco: ---");
        localImpactoResult = criarLabelRes("Local Impacto: ---");
        statusResult = criarLabelRes("STATUS: AGUARDANDO");
        statusResult.setForeground(Color.YELLOW);

        //Faz o botão de calcular e chama a função que irá realizar o código
        JButton btnCalc = new JButton("CALCULAR");
        btnCalc.setBackground(new Color(46, 204, 113)); 
        btnCalc.setForeground(Color.WHITE);
        btnCalc.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCalc.addActionListener(e -> executarCalculo());

        //Faz o botão de resetar e chama a função que apaga os valores que apareceram antes
        JButton btnReset = new JButton("RESETAR CAMPOS");
        btnReset.setBackground(new Color(231, 76, 60)); 
        btnReset.setForeground(Color.WHITE);
        btnReset.addActionListener(e -> resetar());

        //Faz os componentes da aba de resultados
        resultPanel.add(tempoResult);
        resultPanel.add(velResult);
        resultPanel.add(localImpactoResult);
        resultPanel.add(statusResult);
        resultPanel.add(new JSeparator());
        resultPanel.add(btnCalc);
        resultPanel.add(btnReset);

        rightPanel.add(resultPanel);

        add(leftPanel, BorderLayout.WEST);
        add(visualPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        setLocationRelativeTo(null);
    }

    //Determina os componentes e padronização do Swing
    private JPanel criarSecao(String titulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_PAINEIS);
        p.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.GRAY), titulo, 0, 0, null, Color.WHITE));
        return p;
    }

    //padroniza e alinha a interface toda
    private JPanel criarInputComRotulo(String texto, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(5, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(texto);
        l.setForeground(new Color(200, 200, 200));
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    //Coloca uma fonte diferente
    private JLabel criarLabelRes(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Monospaced", Font.BOLD, 13));
        l.setForeground(Color.WHITE);
        return l;
    }

    //Função que de fato realiza o cálculo físico
    private void executarCalculo() {
        try {
            //Pega as entradas de gravidade, altura da ponte e velocidade inicial dos parâmetro gerais
            double g = Double.parseDouble(gravidadeBox.getSelectedItem().toString());
            double h = sliderH.getValue();
            double v0 = Double.parseDouble(velChaveField.getText().replace(",", "."));
            
            // Validação V0
            if (v0 < -30 || v0 > 30) throw new IllegalArgumentException("V0 Chave deve ser entre -30 e 30 m/s!");

            double d0, a;
            int modo = abas.getSelectedIndex();

            //essa parte vai determinar se vai ser preciso chamar a mensagem caso o dado de entrada violar a limitação
            if (modo == 0) { // Validação Acadêmico
                d0 = Double.parseDouble(distBrinqField.getText().replace(",", "."));
                a = Double.parseDouble(acelBrinqField.getText().replace(",", "."));
                if (d0 < 1 || d0 > 50) throw new IllegalArgumentException("D0 Acadêmico deve ser entre 1 e 50m!");
                if (a < 0 || a > 1.5) throw new IllegalArgumentException("Aceleração Acadêmica deve ser entre 0 e 1.5 m/s²!");
            } else { // Validação Realista
                d0 = Double.parseDouble(distRealField.getText().replace(",", "."));
                a = Double.parseDouble(acelRealField.getText().replace(",", "."));
                if (d0 < 50 || d0 > 100) throw new IllegalArgumentException("D0 Realista deve ser entre 50 e 100m!");
                if (a < 0 || a > 3.0) throw new IllegalArgumentException("Aceleração Realista deve ser entre 0 e 3.0 m/s²!");
            }

            // Conta física das fórmulas vistas em aula
            double delta = (v0 * v0) - (4 * (0.5 * g) * (-h));
            double t = (-v0 + Math.sqrt(delta)) / g;
            double vBarco = (d0 - (0.5 * a * t * t)) / t;

            //Mostra o resultado das contas
            tempoResult.setText(String.format("Tempo Queda: %.3f s", t));
            velResult.setText(String.format("Vel. Barco: %.3f m/s", vBarco));
            localImpactoResult.setText(modo == 1 ? "Impacto: Proa (Ponto 0)" : "Impacto: Centro");
            statusResult.setText("STATUS: CONCLUÍDO");
            statusResult.setForeground(Color.GREEN);

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Erro: Insira apenas números válidos!", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException iae) {
            // EXIBE A MENSAGEM ESPECÍFICA DO LIMITE
            JOptionPane.showMessageDialog(this, iae.getMessage(), "Limite de Parâmetros", JOptionPane.WARNING_MESSAGE);
            statusResult.setText("STATUS: ERRO LIMITES");
            statusResult.setForeground(Color.RED);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado no cálculo.");
        }
    }//essa parte acima mostra uma najelinha de erro

    //Reseta todos os valores dos resultados para 00 e os de entrada para as condições iniciais do problema
    private void resetar() {
        velChaveField.setText("0.00");
        distBrinqField.setText("12.00");
        acelBrinqField.setText("0.00");
        distRealField.setText("60.00");
        acelRealField.setText("1.50");
        sliderH.setValue(45);
        sliderL.setValue(5);
        tempoResult.setText("Tempo Queda: ---");
        velResult.setText("Vel. Barco: ---");
        localImpactoResult.setText("Local Impacto: ---");
        statusResult.setText("STATUS: AGUARDANDO");
        statusResult.setForeground(Color.YELLOW);
        repaint();
    }

    //Essa parte em especifico faz a ponte e o barco do meio da tela
    class CanvasVisualizacao extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            setBackground(new Color(25, 35, 45));

            //Essa parte é responsável por pegar os dados de entrada do usuário e os inserir aqui
            int w = getWidth(); int h = getHeight();

            g2.setColor(new Color(30, 60, 90));
            g2.fillRect(0, h - 80, w, 80);

            //Pega o valor referente no slide da ponte e modifica o seu tamnho de acordo com o mostrado
            int altPx = sliderH.getValue() * 3;
            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(12));
            g2.drawLine(40, h-80-altPx, 200, h-80-altPx);
            g2.setStroke(new BasicStroke(8));
            g2.drawLine(50, h-80, 50, h-80-altPx);

            //Pega o valor do tamanho do barco e o modifica caso for a aba realista
            int lMetros = (abas.getSelectedIndex() == 0) ? 3 : sliderL.getValue();
            int tamBarco = lMetros * 20; 
            int bX = w/2 - tamBarco/2;
            int bY = h - 110;
            
            g2.setColor(COLOR_ACCENT);
            Path2D barco = new Path2D.Double();
            barco.moveTo(bX, bY);
            barco.lineTo(bX + tamBarco, bY);
            barco.lineTo(bX + tamBarco - 15, bY + 30);
            barco.lineTo(bX + 15, bY + 30);
            barco.closePath();
            g2.fill(barco);
            
            g2.setColor(Color.WHITE);
            g2.drawString(abas.getSelectedIndex() == 0 ? "ACADÊMICO" : "REALISTA (L="+lMetros+"m)", bX, bY-10);
        }
    }

    //Permite que a janela da interface do swing seja aberta de maneira segura
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PonteBarcoSimulator().setVisible(true));
    }
}