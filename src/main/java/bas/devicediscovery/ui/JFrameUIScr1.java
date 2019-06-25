package bas.devicediscovery.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class JFrameUIScr1 implements Runnable{
    private AtomicBoolean startStopApp;
    public JFrameUIScr1(AtomicBoolean a){
        this.startStopApp = a;
    }
    public void run(){
        final JFrame fra = new JFrame();
        fra.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.ipady = 40;
        c.gridwidth = 8;
        JLabel lbl = new JLabel("Welcome to Bluetooth based Device tracking application");
        fra.add(lbl,c);
        //JPanel grid = new JPanel(new FlowLayout());
        JButton startButton = new JButton("Start Application");
        //JButton stopButton  = new JButton("Stop Application");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //startStopApp=true;
                startStopApp.set(true);
                fra.dispose();
            }
        });
        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridx = 2;
        c1.gridy = 1;
        c1.gridwidth =1;
        //c1.weightx = 0.5;
        fra.add(startButton,c1);
        fra.setTitle("Welcome");
        fra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fra.pack();
        fra.setLocationRelativeTo(null);
        fra.setVisible(true);
    }
}