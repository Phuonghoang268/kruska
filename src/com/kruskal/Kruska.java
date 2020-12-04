package com.kruskal;
import javafx.animation.AnimationTimer;
import javafx.scene.shape.Circle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Kruska extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JFrame frameAbout, frameHelp;
    private String[] listGraphDemo = { "0", "1", "2", "3", "4", "5" };
    private String data[][], head[];
    private JComboBox<String> cbbBeginPoint = new JComboBox<String>();
    private JComboBox<String> cbbEndPoint = new JComboBox<String>();
    private JComboBox<String> cbbGraphDemo = new JComboBox<String>();

    private JButton btnRunStep;

    private JTable tableMatrix;
    private JTable tableLog;

    // draw
    private JPanel drawPanel = new JPanel();
    private JButton btnPoint, btnLine, btnMove, btnOpen, btnNew;
    // graph
    private MyDraw myDraw = new MyDraw();

    private JTextArea textMatrix;

    private JTextField textNumerPoint;

    private MyPopupMenu popupMenu;

    private int indexBeginPoint = 0, indexEndPoint = 0;
    private int step = 0;
    private boolean mapType = false;

    int WIDTH_SELECT, HEIGHT_SELECT;
    private JPanel mainPanel;
    public Kruska(){
        setTitle("Krus");
        setLayout(new BorderLayout(5, 5));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // addMenu
        add(creatMenu(), BorderLayout.PAGE_START);
        // add content
        add(creatPaintPanel(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }
    private JMenuBar creatMenu(){
        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        // menuFile.add(menuFileNew);
        menuFile.add(createMenuItem("New", KeyEvent.VK_N, Event.CTRL_MASK));
        menuFile.addSeparator();


        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menuFile);
        return menuBar;
    };

    private JPanel creatPaintPanel() {

        drawPanel.setLayout(new BoxLayout(drawPanel, BoxLayout.X_AXIS));
        drawPanel.setBorder(new TitledBorder(""));
        drawPanel.setBackground(Color.pink);
        Icon icon;
        // String link = File.separator + "icon" + File.separator;
        String link = "/icon/";

        icon = getIcon(link + "iconPoint.png");
        drawPanel.add(btnPoint = createButtonImage(icon, "Draw Point"));

        icon = getIcon(link + "iconLine.png");
        drawPanel.add(btnLine = createButtonImage(icon, "Draw line"));

        icon = getIcon(link + "iconMove.png");
        drawPanel.add(btnMove = createButtonImage(icon, "Move Point"));

        JPanel run= new JPanel();
        run.add(btnRunStep = createButton("Run Step"));
        drawPanel.add(run, BorderLayout.EAST);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(drawPanel, BorderLayout.PAGE_END);
        panel.add(myDraw, BorderLayout.CENTER);
        return panel;
    }

    private ImageIcon getIcon(String link) {
        return new ImageIcon(getClass().getResource(link));
    }

    private JMenuItem createMenuItem(String title, int keyEvent, int event) {
        JMenuItem mi = new JMenuItem(title);
        mi.setMnemonic(keyEvent);
        mi.setAccelerator(KeyStroke.getKeyStroke(keyEvent, event));
        mi.addActionListener(this);
        return mi;
    }

    // create button and add to panel
    private JButton createButton(String lable) {
        JButton btn = new JButton(lable);
        btn.addActionListener(this);
        return btn;
    }

    // create buttonImage and add to panel
    private JButton createButtonImage(Icon icon, String toolTip) {
        JButton btn = new JButton(icon);
        btn.setMargin(new Insets(10, 10, 0, 0));
        btn.addActionListener(this);
        btn.setToolTipText(toolTip);
        return btn;
    }

    // create comboBox and add to panel
    private JComboBox<String> createComboxBox(String title) {
        String list[] = { title };
        JComboBox<String> cbb = new JComboBox<String>(list);
        cbb.addActionListener(this);
        cbb.setEditable(false);
        cbb.setMaximumRowCount(5);
        return cbb;
    }

    // create matrix panel with cardLayout

    private JTable createTable() {
        JTable table = new JTable();
        return table;
    }
    //------------------Action----------


    private void actionUpdate() {
        updateListPoint();

        reDraw();
        loadMatrix();
    }

    private void actionDrawPoint() {
        myDraw.setDraw(1);

    }

    private void actionDrawLine() {
        myDraw.setDraw(2);

    }

    private void actionNew() {

        myDraw.setResetGraph(true);
        myDraw.repaint();
        myDraw.init();
        updateListPoint();
        clearMatrix();
    }

    private void actionChoosePoint() {

        reDraw();
    }

    private void updateListPoint() {
        int size = myDraw.getData().getArrMyPoint().size();
        String listPoint[] = new String[size];
        listPoint[0] = "Begin";
        for (int i = 1; i < listPoint.length; i++) {
            listPoint[i] = String.valueOf(i);
        }

        cbbBeginPoint.setModel(new DefaultComboBoxModel<String>(listPoint));
        cbbBeginPoint.setMaximumRowCount(5);

        if (size > 1) {
            listPoint = new String[size + 1];
            listPoint[0] = "End";
            for (int i = 1; i < listPoint.length; i++) {
                listPoint[i] = String.valueOf(i);
            }
            listPoint[listPoint.length - 1] = "All";
        } else {
            listPoint = new String[1];
            listPoint[0] = "End";
        }

        cbbEndPoint.setModel(new DefaultComboBoxModel<String>(listPoint));
        cbbEndPoint.setMaximumRowCount(5);
    }

    private void setEnableDraw(boolean check, String matrix) {
        // btnLine.setEnabled(check);
        // btnPoint.setEnabled(check);
        // btnUpdate.setEnabled(check);

        // CardLayout cl = (CardLayout) (matrixPandl.getLayout());
        // cl.show(matrixPandl, matrix);
        cbbGraphDemo.setEnabled(!check);
    }

    private void setEnableMapType(boolean mapType) {
        this.mapType = mapType;
        myDraw.setTypeMap(mapType);
        myDraw.repaint();
        loadMatrix();
    }


    private void resetDataKruskal() {
        step = 0;
    }

    private void reDraw() {
        myDraw.setReDraw(true);
        myDraw.repaint();
    }

    private void clearMatrix() {
        DefaultTableModel model = new DefaultTableModel();
        tableMatrix.setModel(model);
    }

    private void loadMatrix() {
        final int width = 35;
        final int col = WIDTH_SELECT / width - 1;
        DefaultTableModel model = new DefaultTableModel(data, head);
        tableMatrix.setModel(model);
        if (tableMatrix.getColumnCount() > col) {
            for (int i = 0; i < head.length; i++) {
                TableColumn tc = tableMatrix.getColumnModel().getColumn(i);
                tc.setPreferredWidth(width);
            }
            tableMatrix.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        } else {
            tableMatrix.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        }
    }

    private void loadLog(boolean isStep) {
        final int width = 70;
        final int col = tableLog.getWidth() / width - 1;

        // update data for table log
        DefaultTableModel model = new DefaultTableModel(data, head);
        tableLog.setModel(model);
        if (tableLog.getColumnCount() > col) {
            for (int i = 0; i < head.length; i++) {
                TableColumn tc = tableLog.getColumnModel().getColumn(i);
                tc.setPreferredWidth(width);
            }
        }
    }

    private void processInputMatrix() {
        int numberPoint = 0;
        boolean isSuccess = true;
        try {
            numberPoint = Integer.parseInt(textNumerPoint.getText());
            int a[][] = new int[numberPoint][numberPoint];
            String temp = textMatrix.getText();
            Scanner scan = new Scanner(temp);
            for (int i = 0; i < numberPoint; i++) {
                for (int j = 0; j < numberPoint; j++) {
                    try {
                        a[i][j] = scan.nextInt();
                    } catch (InputMismatchException e) {
                        JOptionPane.showMessageDialog(null,
                                "Enter your matrix!");
                        isSuccess = false;
                        break;
                    }
                }
                if (!isSuccess) {
                    break;
                }
            }

            for (int i = 0; i < numberPoint; i++) {
                for (int j = 0; j < numberPoint; j++) {
                    System.out.printf("%3d", a[i][j]);
                }
                System.out.println();
            }

            scan.close();

            myDraw.setA(a);
            myDraw.convertMatrixToData();
            myDraw.repaint();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Enter one integer number < 30!");
        }

    }

    /**
     * new.
     */

    /**
     * cái này khi click runstep thi nó chạy. Nhưng nó chỉ hiện kết quả cuối cùng
     */
    private void runStep() {
        MyDraw.reDraw=true;
//        double min=myDraw.data.getArrMyLine().get(1).getCost();
//        for(int i=2;i<myDraw.data.getArrMyLine().size();i++){
//            if(min>myDraw.data.getArrMyLine().get(i).getCost()){
//                min=myDraw.data.getArrMyLine().get(i).getCost();
//            }
//        }
//        for(int i=1;i<myDraw.data.getArrMyLine().size();i++){
//            if(myDraw.data.getArrMyLine().get(i).getCost()==min){
//
//            }
//        }
        myDraw.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (e.getSource() == btnPoint) {
            actionDrawPoint();
        }

        if (e.getSource() == btnLine) {
            actionDrawLine();
        }
        if (e.getSource() == btnMove) {
            myDraw.setDraw(3);
        }

        if (e.getSource() == btnNew) {
            actionNew();
        }
        // select point
        if (e.getSource() == cbbBeginPoint || e.getSource() == cbbEndPoint) {
            actionChoosePoint();
        }

        // select menu bar
        if (command == "New") {
            actionNew();
        }
        if (e.getSource() == btnRunStep) {
            runStep();
        }

    }
    public static void main(String [] args ){
        new Kruska();
    }

}

