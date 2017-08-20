package bluetoothdevicediscovery;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Akhil on 28-09-2016.
 */
class TestThread extends Thread{
    private Device d;
    TestThread(Device q){this.d = q; }
    public void run(){
        synchronized (d){
            while(true){
                System.out.println("two");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                d.notify();
                try {
                    d.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
class TestClass1 extends Thread{
    private LinkedList<Device> listOfDevices;
    private LinkedList<Device> listOfDevicesTracked;
    private String deviceListLocation;
    TestClass1(LinkedList<Device> ls,LinkedList<Device> ls1, String loc){
        listOfDevices = ls;
        listOfDevicesTracked= ls1;
        deviceListLocation = loc;
    }
    public void run(){
        renderUI1();
    }
    private void renderUI1() {
        int tracker=1,j;
        final JFrame frame = new JFrame("Available Devices");
        JPanel pnl = new JPanel();
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
                                    String fileloc = "deviceFiles/" + newTrackingDev.getBluetoothId() + ".txt";
                                    newTrackingDev.setFileLoc(fileloc);
                                    if(listOfDevicesTracked.isEmpty()){
                                        newTrackingDev.setDeviceId(1);
                                    }
                                    else{
                                        newTrackingDev.setDeviceId(listOfDevicesTracked.getLast().getDeviceId() + 1);
                                    }
                                    listOfDevicesTracked.add(newTrackingDev);
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
        JButton jbt = new JButton("Refresh");
        jbt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                renderUI1();
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
public class TestClass extends JFrame{
    public void renderUI(final LinkedList<Device> listOfDevices, final LinkedList<Device> listOfDevicesTracked,
                         final String deviceListLocation){
        int tracker=1;
        DefaultTableModel dm = new DefaultTableModel();
        Object[] tableRow = new Object[]{"Device Id","Bluetooth Id","Name","Status","Track"};
        final Object[][] tableMatrix = new Object[listOfDevices.size()][5];
        //TODO: iterate only when the list is not empty
        for(tracker=0;tracker<listOfDevices.size();tracker++){
            System.out.println(tracker + "tracker");
            System.out.println(listOfDevices.get(tracker).getBluetoothId());
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
                            FileWriter fw = new FileWriter(deviceListLocation,true);
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
        this.add(new JScrollPane(mainTable));
        //stopTrack.setText("Track");
    }


    public void addElement(LinkedList<Device> listOfDevices , Device D){
        listOfDevices.add(D);
    /*
    JLabel l;
    public void start(){

        HashMap<String,String> map1= new HashMap<String, String>();
        HashMap<String,String> map2= new HashMap<String, String>();
        map1.put("name","Akhil Sai");
        map1.put("Roll No","EE13B006");
        map2.putAll(map1);
        map1.clear();
        System.out.println(map2.get("name"));

        l= new JLabel();
        l.setText("Name");
        JTextField txt = new JTextField();
        txt.setText("Happy");
        JPasswordField ps = new JPasswordField(10);
        JButton but = new JButton();
        but.setText("Submit");
        add(l);
        add(txt);
        add(ps);
        add(but);
        but.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                l.setText("Button Clicked");
            }
        });
        setLayout(new FlowLayout());
        setSize(400,400);
        setVisible(true);
        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        */
    }
    public void writeToFile(BufferedWriter bw, FileWriter fw) throws IOException {
    }
    public static void main(String args[]) throws InterruptedException {

        LinkedList<Device> listOfDevicesTracked = new LinkedList<Device>();
        LinkedList<Device> dupList = new LinkedList<Device>();
        Device akh = new Device();
        akh.setBluetoothId("szadngmadofgn");
        akh.setNickName("Akhil Sai");
        akh.setTrack(0);
        akh.setStatus(1);
        akh.setDeviceId(10000);
        //listOfDevicesTracked.add(akh);
        //dupList.add(akh);
        Device gop = new Device();
        DefaultTableModel dm = new DefaultTableModel();
        gop.setBluetoothId("agdfgsdf");
        gop.setNickName("Gopi");
        gop.setStatus(0);
        gop.setTrack(0);
        //gop.setDeviceId(10000);
        gop.setDeviceId(10000);
        //dupList.add(gop);
        try {
            BufferedReader br = new BufferedReader(new FileReader("trackdevicefile.txt"));
            try {
                String str = br.readLine();
                int deviceId=1;
                while(str != null){
                    System.out.println(str);
                    if(str.contains("DeviceDelimiter")){
                        System.out.println("true");
                        str= br.readLine();
                        Device d = new Device();
                        d.setBluetoothId(str);
                        str = br.readLine();
                        d.setNickName(str);
                        str = br.readLine();
                        d.setFileLoc(str);
                        d.setDeviceId(deviceId);
                        d.setTrack(1);
                        d.setStatus(0);
                        deviceId++;
                        listOfDevicesTracked.add(d);
                        //continue;
                    }
                    str= br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //TestThread tst = new TestThread(gop);
        //tst.start();
        /*
        AtomicBoolean a = new AtomicBoolean(false);
        StartApplicationUI appl = new StartApplicationUI(a);
        Thread t = new Thread(appl);
        t.start();
        while(true){
            if(a.get()){
                break;
            }
        }

        while(a.get()){
            System.out.println("Works!");
            Thread.sleep(1000);
        }
        appl.dispose();
        //t.interrupt();
        /*
        synchronized (gop){
            while(true){
                System.out.println("one");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                gop.notify();
                try {
                    gop.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("three");
            }
        }
        */
        //System.out.println(dupList.get(0).getBluetoothId());
        //System.out.println(dupList.get(0).getNickName());
        //tableMatrix[1][4] = jbt1;
        /*
        JTable jtab  = new JTable(tableMatrix,tableRow);

        jtab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int col = target.getSelectedColumn();
                System.out.println(row);
                System.out.println(col);
                super.mouseClicked(e);
            }
        });
        int i=0;
        JFrame newFrame = new JFrame();
        for(i=0;i<10;i++){

            JScrollPane newPane = new JScrollPane(jtab);
            newFrame.add(newPane);
            newFrame.pack();
            newFrame.setVisible(true);
            Thread.sleep(3000);
            //newFrame.dispose();
            Thread.sleep(1000);
            System.out.println("Working!");
        }
        */
        //tst.setVisible(true);
        dupList= (LinkedList<Device>) listOfDevicesTracked.clone();
        TestClass1 tst = new TestClass1(dupList,listOfDevicesTracked,"trackdevicefile.txt");
        tst.start();

        while(true){
            Thread.sleep(3000);
            System.out.println(listOfDevicesTracked.size());
            System.out.println(listOfDevicesTracked.getLast().getBluetoothId());
            //tst.renderUI1(dupList,listOfDevicesTracked,"trackdevicefile.txt");
            //tst.repaint();
            dupList.clear();
            for(int i=0;i<listOfDevicesTracked.size();i++){
                dupList.add(listOfDevicesTracked.get(i));
            }
            dupList.add(akh);
            //tst.revalidate();
            //tst.repaint();
            //tst.renderUI1(dupList,listOfDevicesTracked,"trackdevicefile.txt");
        }
        //System.out.println(listOfDevicesTracked.size());
        /*
        try {
            FileWriter fw =  new FileWriter("trackdevicefile.txt",true);
            BufferedWriter bw = new BufferedWriter(fw);
            String content = "Write this to file";
            bw.write(content);
            String content1 = "Write this also to the file";
            bw.write(content1);
            bw.close();
            fw.close();
            //new TestClass().writeToFile(bw,fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
}
