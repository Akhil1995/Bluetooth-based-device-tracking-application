package bas.devicediscovery.ui;

import bas.devicediscovery.bluetooth.Device;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Akhil on 13-07-2017.
 */
public class JFrameUIScr2 extends Thread{
    private LinkedList<Device> listOfDevices;
    private LinkedList<Device> listOfDevicesTracked;
    private String deviceListLocation;
    private AtomicBoolean isRefReady = new AtomicBoolean(false);
    public JFrameUIScr2(LinkedList<Device> ls, LinkedList<Device> ls1, String loc, AtomicBoolean iar){
        listOfDevices = ls;
        listOfDevicesTracked= ls1;
        deviceListLocation = loc;
        isRefReady = iar;
    }
    public void run(){
        // Sleep for the booting period of the application.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        renderUI1();
        // Refresh for every particular number of seconds.
    }
    private void renderUI(final LinkedList<Device> listOfDevices, final LinkedList<Device> listOfDevicesTracked,
                          final String deviceListLocation){
        int tracker;
        JFrame frame = new JFrame("Available Devices");
        Object[] tableRow = new Object[]{"Device Id","Bluetooth Id","Name","Status","Track"};
        final Object[][] tableMatrix = new Object[listOfDevices.size()][5];
        //TODO: iterate only when the list is not empty
        for(tracker=0;tracker<listOfDevices.size();tracker++){
            tableMatrix[tracker][0] = listOfDevices.get(tracker).getDeviceId();
            tableMatrix[tracker][1] = listOfDevices.get(tracker).getBluetoothId();
            tableMatrix[tracker][2] = listOfDevices.get(tracker).getNickName();
            tableMatrix[tracker][3] = listOfDevices.get(tracker).getStatus();
            //tableMatrix[tracker][4] = listOfDevices.get(tracker).getTrack();
            if(listOfDevices.get(tracker).getTrack() == 1){
                // Row has to deleted and accordingly the entry in the file.
                tableMatrix[tracker][4] = "Stop Tracking this Device";
            }
            else{
                // Track button should be displayed.
                //final JButton stopTrack = new JButton();
                tableMatrix[tracker][4] = "Track this Device";
            }
            //tracker = tracker + 1;
        }
        JTable mainTable = new JTable(tableMatrix,tableRow);
        mainTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int col = target.getSelectedColumn();
                if(col==4){
                    if(tableMatrix[row][col].equals("Track this Device")){
                        Device newTrackingDev = new Device();
                        newTrackingDev.setBluetoothId((String) tableMatrix[row][1]);
                        newTrackingDev.setNickName((String) tableMatrix[row][2]);
                        newTrackingDev.setStatus(0);
                        newTrackingDev.setTrack(1);
                        String fileloc = newTrackingDev.getBluetoothId() + ".txt";
                        newTrackingDev.setFileLoc(fileloc);
                        if(listOfDevicesTracked.isEmpty()){
                            newTrackingDev.setDeviceId(1);
                        }
                        else{
                            newTrackingDev.setDeviceId(listOfDevicesTracked.getLast().getDeviceId() + 1);
                        }
                        listOfDevicesTracked.add(newTrackingDev);
                        // Open file and write in file also.
                        try {
                            // Replace with sqlite database.
                            FileWriter fw = new FileWriter(deviceListLocation);
                            BufferedWriter bw = new BufferedWriter(fw);
                            bw.write("DeviceDelimiter");
                            bw.write(newTrackingDev.getDeviceId());
                            bw.write("\n");
                            bw.write(newTrackingDev.getBluetoothId());
                            bw.write("\n");
                            bw.write(newTrackingDev.getNickName());
                            bw.write("\n");
                            bw.write(newTrackingDev.getFileLoc());
                            bw.write("\n");
                            bw.close();
                            fw.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            // TODO: What can be done now?
                        }
                    }
                    else{
                        // Delete row. Add functionality later.
                    }
                }
                else{
                    // Do Nothing
                }
                //System.out.println(row);
                //System.out.println(col);
                super.mouseClicked(e);
            }
        });
        frame.add(new JScrollPane(mainTable));
        frame.setTitle("Available Devices");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        //stopTrack.setText("Track");
    }
    private void renderUI1() {
        synchronized (listOfDevices){
            int tracker,j;
            final JFrame frame = new JFrame("Available Devices");
            JPanel[][] panelHolder = new JPanel[listOfDevices.size()+2][5];
            frame.setLayout(new GridLayout(listOfDevices.size()+2,5,3,3));
            for(tracker=0;tracker<=listOfDevices.size()+1;tracker++){
                for(j=0;j<5;j++){
                    panelHolder[tracker][j] = new JPanel();
                    frame.add(panelHolder[tracker][j]);
                }
            }
            String[] strArr = new String[]{"Device Id","Bluetooth Id","Name","Status","Track"};
            for(tracker=0;tracker<=listOfDevices.size();tracker++){
                for(j=0;j<5;j++){
                    if (tracker == 0){
                        JLabel label = new JLabel(strArr[j]);
                        panelHolder[tracker][j].add(label);
                    }

                    else{
                        if(!listOfDevices.isEmpty()){
                            if (j == 4){
                                if(listOfDevices.get(tracker-1).getTrack()==1){
                                    JButton jbt = new JButton("Tracking");
                                    jbt.setEnabled(false);
                                    // Do Nothing. Add functionality later.
                                    panelHolder[tracker][j].add(jbt);

                                }
                                else if(listOfDevices.get(tracker-1).getTrack()==0){
                                    final JButton jbt = new JButton("Track");
                                    final Device tempDev = listOfDevices.get(tracker-1);
                                    jbt.addActionListener(new ActionListener(){
                                        public void actionPerformed(ActionEvent e){
                                            // Add the new device to the linked list.
                                            Device newTrackingDev = new Device();
                                            newTrackingDev.setBluetoothId(tempDev.getBluetoothId());
                                            newTrackingDev.setNickName(tempDev.getNickName());
                                            newTrackingDev.setStatus(0);
                                            newTrackingDev.setTrack(1);
                                            String fileloc = newTrackingDev.getBluetoothId() + ".txt";
                                            newTrackingDev.setFileLoc(fileloc);
                                            if(listOfDevicesTracked.isEmpty()){
                                                newTrackingDev.setDeviceId(1);
                                            }
                                            else{
                                                newTrackingDev.setDeviceId(listOfDevicesTracked.getLast().getDeviceId() + 1);
                                            }
                                            listOfDevicesTracked.add(newTrackingDev);
                                            // Write to file.
                                            try {
                                                // Replace with sqlite database.
                                                FileWriter fw = new FileWriter(deviceListLocation,true);
                                                BufferedWriter bw = new BufferedWriter(fw);
                                                bw.write("DeviceDelimiter");
                                                //bw.write(newTrackingDev.getDeviceId());
                                                bw.write("\n");
                                                bw.write(newTrackingDev.getBluetoothId());
                                                bw.write("\n");
                                                bw.write(newTrackingDev.getNickName());
                                                bw.write("\n");
                                                bw.write(newTrackingDev.getFileLoc());
                                                bw.write("\n");
                                                bw.close();
                                                fw.close();
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                                // TODO: What can be done now?
                                            }
                                            jbt.setText("Tracking");
                                            jbt.setEnabled(false);
                                        }
                                    });
                                    panelHolder[tracker][j].add(jbt);
                                }
                            }
                            else if(j==0){
                                if(listOfDevices.get(tracker-1).getDeviceId()==10000){
                                    JLabel label1 = new JLabel(" ");
                                    panelHolder[tracker][j].add(label1);
                                }
                                else{
                                    JLabel label1 =  new JLabel((listOfDevices.get(tracker-1).getDeviceId()).toString());
                                    //System.out.println(listOfDevices.get(tracker-1).getDeviceId());
                                    panelHolder[tracker][j].add(label1);
                                }
                            }
                            else if(j==1){
                                JLabel label1 = new JLabel(listOfDevices.get(tracker-1).getBluetoothId());
                                panelHolder[tracker][j].add(label1);
                            }
                            else if(j==2){
                                JLabel label1 = new JLabel(listOfDevices.get(tracker-1).getNickName());
                                panelHolder[tracker][j].add(label1);
                            }
                            else if(j==3){
                                if(listOfDevices.get(tracker-1).getStatus() == 1){
                                    JLabel label1 = new JLabel("Available");
                                    panelHolder[tracker][j].add(label1);
                                }
                                else if(listOfDevices.get(tracker-1).getStatus() == 0){
                                    JLabel label1 = new JLabel("UnAvailable");
                                    panelHolder[tracker][j].add(label1);
                                }
                            }
                        }
                    }
                }
            }
            JButton jbt = new JButton("Refresh");
            jbt.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    System.out.println("Refreshing list....");
                    synchronized (listOfDevices){
                        isRefReady.set(true);
                        JOptionPane.showMessageDialog(null,"Refreshing Please wait.","Alert",
                                JOptionPane.INFORMATION_MESSAGE);
                        try {
                            listOfDevices.wait();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        renderUI1();
                        isRefReady.set(false);
                        listOfDevices.notify();
                    }
                    //listOfDevices.notify();
                }
            });
            panelHolder[listOfDevices.size()+1][2].add(jbt);
            for(tracker=0;tracker<=listOfDevices.size()+1;tracker++){
                for(j=0;j<5;j++){
                    frame.add(panelHolder[tracker][j]);
                }
            }
            frame.setTitle("Available Devices");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }
}
