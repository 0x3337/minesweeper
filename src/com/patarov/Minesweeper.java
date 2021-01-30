package com.patarov;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Mirsaid Patarov on 02-06-2016.
 */
public class Minesweeper extends JFrame {
    private static final long serialVersionUID = 1L;

    private final int WINDOWS_WIDTH = 360;
    private final int WINDOWS_HEIGHT = 465;
    private final int BOX_SIZE = 8;

    private int time = 0;
    private int bomb = 10;
    private int bombCounter = bomb;
    private int record = Integer.MAX_VALUE;
    private boolean io = false;

    private Timer timer;

    private Icon startGameIcon = new ImageIcon(getClass().getResource("/images/startgame.png"));
    private Icon winGameIcon = new ImageIcon(getClass().getResource("/images/wingame.png"));
    private Icon gameOverIcon = new ImageIcon(getClass().getResource("/images/gameover.png"));

    private String pathToFile = "data.txt";

    private JLabel timeLabel = new JLabel("Time: " + time + "s");
    private JLabel bombLabel = new JLabel("Bombs: " + bombCounter);
    private JLabel recordLabel = new JLabel("No records");
    private JButton start = new JButton(startGameIcon);
    private JButton [][] box = new JButton[BOX_SIZE][BOX_SIZE];
    private int [][] bombs = new int[BOX_SIZE][BOX_SIZE];

    public Minesweeper() {
        super("Minesweeper");
        setSize(WINDOWS_WIDTH, WINDOWS_HEIGHT);
        setMaximumSize(new Dimension(WINDOWS_WIDTH, WINDOWS_HEIGHT));
        setMinimumSize(new Dimension(WINDOWS_WIDTH, WINDOWS_HEIGHT));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            Scanner sc = new Scanner(new File(pathToFile));

            String name = "";
            if (sc.hasNext())
                name = sc.nextLine();
            if (sc.hasNextInt())
                record = sc.nextInt();

            if (name.length() != 0 && record != 0)
                recordLabel.setText(name + " record: " + record + "s");
            else
                recordLabel.setText("No records");
        }
        catch(IOException e){
            recordLabel.setText("No records");
        }

        bombsGenerator();

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                time++;
                timeLabel.setText("Time: " + time + "s");
            }
        });


        start.setBounds(new Rectangle(WINDOWS_WIDTH / 2 - 20, 3, 40, 40));
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restart();
            }
        });

        timeLabel.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
        timeLabel.setBounds(new Rectangle(0, 3, 140, 40));
        timeLabel.setHorizontalAlignment(JTextField.CENTER);

        bombLabel.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
        bombLabel.setBounds(new Rectangle(WINDOWS_WIDTH - 140, 3, 140, 40));
        bombLabel.setHorizontalAlignment(JTextField.CENTER);

        recordLabel.setBounds(new Rectangle(0, 405, WINDOWS_WIDTH, 20));
        recordLabel.setHorizontalAlignment(JTextField.LEFT);

        for (int i = 0; i < BOX_SIZE; i++) {
            for (int j = 0; j < BOX_SIZE; j++) {
                box[i][j] = new JButton();
                box[i][j].setName(String.valueOf(i* 100 + 101 + j));
                box[i][j].setBounds(new Rectangle(i * 45, j * 45 + 45, 45, 45));
                box[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.getSource() instanceof JButton) {
                            String text = ((JButton) e.getSource()).getName();
                            Scanner sc = new Scanner(text);
                            int id = sc.nextInt();
                            int x = id % 100 - 1, y = id / 100 - 1;

                            if (e.getModifiers() == e.BUTTON1_MASK)
                                check(x, y);

                            if (e.getModifiers() == e.BUTTON3_MASK)
                                flag(x, y);
                        }
                    }
                });
            }
        }



        JPanel contentPanel = (JPanel) this.getContentPane();
        contentPanel.setLayout(null);

        contentPanel.add(start);
        contentPanel.add(timeLabel);
        contentPanel.add(bombLabel);

        for (int i = 0; i < BOX_SIZE; i++) {
            for (int j = 0; j < BOX_SIZE; j++) {
                contentPanel.add(box[i][j]);
            }
        }

        contentPanel.add(recordLabel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void bombsGenerator() {
        Random r = new Random();

        for (int i = 0; i < BOX_SIZE; i++)
            for (int j = 0; j < BOX_SIZE; j++)
                bombs[i][j] = 0;

        for (int i = 0; i < bomb; i ++) {
            int x = r.nextInt(BOX_SIZE);
            int y = r.nextInt(BOX_SIZE);

            if (bombs[x][y] != 9)
                bombs[x][y] = 9;
            else i--;
        }

        for (int i = 0; i < BOX_SIZE; i++)
            for (int j = 0; j < BOX_SIZE; j++)
                if (bombs[i][j] != 9)
                    bombs[i][j] = bombsCounter(i, j, false);
    }

    private int bombsCounter(int x, int y, boolean io) {

        if (io == true) {
            try {
                if (bombs[x][y] == 9)
                    return 1;
                else
                    return 0;
            }
            catch (ArrayIndexOutOfBoundsException e) {
                return 0;
            }
        }

        return bombsCounter(x - 1, y - 1, true) + bombsCounter(x - 1, y, true) + bombsCounter(x - 1, y + 1, true) +
                bombsCounter(x, y + 1, true) + bombsCounter(x + 1, y + 1, true) + bombsCounter(x + 1, y, true) +
                bombsCounter(x + 1, y - 1, true) + bombsCounter(x, y - 1, true);
    }

    private void check(int x, int y) {
        if (box[y][x].getText().equals("O"))
            return;

        if (io == false) {
            timer.start();
            io = true;
        }

        if (bombs[x][y] == 9) {
            start.setIcon(gameOverIcon);
            timer.stop();

            for (int i = 0; i < BOX_SIZE; i++) {
                for (int j = 0; j < BOX_SIZE; j++) {
                    box[i][j].setEnabled(false);

                    if (bombs[j][i] <= 0)
                        box[i][j].setText("");
                    else if (bombs[j][i] == 9)
                        box[i][j].setText("X");
                    else
                        box[i][j].setText("" + bombs[j][i]);
                }
            }

            return;
        }

        if (bombs[x][y] == 0)
            openVoid(x, y);
        else {
            box[y][x].setText("" + (bombs[x][y] > 0 ? bombs[x][y] : ""));
        }

        box[y][x].setEnabled(false);

        int count = 0;
        for (int i = 0; i < BOX_SIZE; i++) {
            for (int j = 0; j < BOX_SIZE; j++) {
                if (box[i][j].isEnabled() == true)
                    count++;
            }
        }

        if (count == bomb) {
            start.setIcon(winGameIcon);
            timer.stop();

            for (int i = 0; i < BOX_SIZE; i++) {
                for (int j = 0; j < BOX_SIZE; j++) {
                    box[i][j].setEnabled(false);
                }
            }

            if (time < record) {
                record = time;

                try(FileWriter writer = new FileWriter(pathToFile, false)) {
                    String name = JOptionPane.showInputDialog("New record!\nPlease enter your name");
                    writer.write(name);
                    writer.append('\n');
                    writer.write("" + time);

                    writer.flush();
                    recordLabel.setText(name + " record: " + record + "s");
                }
                catch(IOException ex){
                    System.out.println(ex.getMessage());
                }

                restart();
            }

            return;
        }
    }

    private void flag(int x, int y) {
        if (box[y][x].isEnabled() == true) {
            if (box[y][x].getText().equals("O")) {
                box[y][x].setText("");
                bombCounter++;
            } else {
                box[y][x].setText("O");
                bombCounter--;
            }

            bombLabel.setText("Bombs: " + bombCounter);
        }
    }

    private void openVoid(int x, int y) {
        try {
            box[y][x].setText("" + (bombs[x][y] > 0 ? bombs[x][y] : ""));
            box[y][x].setEnabled(false);

            if (bombs[x][y] == 0) {
                box[y][x].setText("");
                bombs[x][y] = -1;

                openVoid(x - 1, y - 1);
                openVoid(x - 1, y);
                openVoid(x - 1, y + 1);
                openVoid(x, y + 1);
                openVoid(x + 1, y + 1);
                openVoid(x + 1, y);
                openVoid(x + 1, y - 1);
                openVoid(x, y - 1);
                return;
            }
            else
                return;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return;
        }

    }

    private void restart() {
        bombsGenerator();
        start.setIcon(startGameIcon);
        io = false;
        time = 0;
        bombCounter = 10;
        timer.stop();
        timeLabel.setText("Time: " + time + "s");
        bombLabel.setText("Bombs: " + bombCounter);

        for (int i = 0; i < BOX_SIZE; i++) {
            for (int j = 0; j < BOX_SIZE; j++) {
                box[i][j].setText("");
                box[i][j].setEnabled(true);
            }
        }
    }
}
