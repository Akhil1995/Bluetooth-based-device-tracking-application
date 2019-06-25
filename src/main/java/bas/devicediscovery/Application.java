package bas.devicediscovery;

import bas.devicediscovery.bluetooth.Device;
import bas.devicediscovery.storage.FileIO;
import bas.devicediscovery.ui.JFrameUIScr1;
import bas.devicediscovery.ui.JFrameUIScr2;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Akhil on 30-06-2017.
 */
// Driver class
public class Application {
    private static FileIO fileWriter = new FileIO();
    public static void main(String args[]){
        System.out.println("Booting Application...");
        BlockingQueue<HashMap<String,String>> queueOfAvailableDevices =
                                            new ArrayBlockingQueue<HashMap<String, String>>(10);
        long timeDifference=0,uiRenderTime=0,currentTime=0;
        int tracker=0,check=0,check_1=1,deviceId=1;
        String deviceListLocation = "trackdevicefile.txt";
        /**
         * Starting the tracking thread. The tracking thread sends list of available devices
         * every 10 seconds or so. The application and the threads all start in the next few lines
         */
        ScanDevices t1 = new ScanDevices(queueOfAvailableDevices);
        LinkedList<Device> listOfDevicesTracked = new LinkedList<Device>();
        HashMap<String,String> tempVar = new HashMap<String, String>();
        AtomicBoolean startStopApp = new AtomicBoolean(false);
        AtomicBoolean isRefReady = new AtomicBoolean(false);
        //listOfDevicesTracked.add(akh);
        /**
         * The list of devices to be tracked should be taken in from application storage. But, the user
         * has to input the details 1st, for which a form has to be designed.
         */
        System.out.println("Reading data from permanent storage");
        try{
            BufferedReader br = new BufferedReader(new FileReader(deviceListLocation));
            try {
                String str = br.readLine();
                while(str != null){
                    //System.out.println(str);
                    if(str.contains("DeviceDelimiter")){
                        //System.out.println("true");
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
            // List is Empty. Leave it as it is.
            //e.printStackTrace();
        }
        LinkedList<Device> duplicateList = new LinkedList<Device>();
        if(!listOfDevicesTracked.isEmpty()){
             duplicateList = (LinkedList<Device>)listOfDevicesTracked.clone();
        }
        JFrameUIScr2 ui = new JFrameUIScr2(duplicateList,listOfDevicesTracked,deviceListLocation,isRefReady);
        // Put an Interrupt here. A button click mostly to start tracking.
        System.out.println("Getting up the User Interface");
        JFrameUIScr1 startingDisplayBox = new JFrameUIScr1(startStopApp);
        Thread t2 = new Thread(startingDisplayBox);
        t2.start();
        while(true){
            if(startStopApp.get()){
                break;
            }
        }
        // Start Bluetooth
        System.out.println("Booting bluetooth module");
        t1.start();
        System.out.println("Loading the User Interface");
        ui.start();
        while(startStopApp.get()){
            // condition put here to check whether loop has been entered.
            synchronized(duplicateList){
                check_1=0;
                long currTime = System.currentTimeMillis();
                // Replicate duplicateList here.
                duplicateList.clear();
                if(!listOfDevicesTracked.isEmpty()){
                    for(tracker=0;tracker<listOfDevicesTracked.size();tracker++){
                        Device newTempDev = new Device();
                        newTempDev.cloneFromOtherDeviceObject(listOfDevicesTracked.get(tracker));
                        // No cloning as it changes the object reference.
                        // Replicating a complete device, write a complete method for this, in device class.
                        duplicateList.add(newTempDev);
                        //System.out.println(listOfDevicesTracked.get(tracker).getStatus());
                    }
                }
                if(!queueOfAvailableDevices.isEmpty()){
                    System.out.println("Consuming from queue");
                    try {
                        // Input is taken from the queue. Every input is a hashmap with entries of available
                        // devices.
                        //duplicateList = (LinkedList<Device>) listOfDevicesTracked.clone();
                        tempVar= queueOfAvailableDevices.take();
                        // If any device being tracked is in the input list, set available time to current time.
                        synchronized(duplicateList){
                            if(!duplicateList.isEmpty()){
                                for(tracker=0;tracker<duplicateList.size();tracker++){
                                    duplicateList.get(tracker).setEpochTime(currTime);
                                }
                            }
                        }
                        for(String key: tempVar.keySet()){
                            if(key.equals("default")){
                                // End iteration and send the list to the User Interface.
                            }
                            else{
                                // A device object. Has to be initiated if unavailable, and other changes necessary
                                if (!duplicateList.isEmpty()){
                                    for(tracker=0;tracker<duplicateList.size();tracker++){
                                        //System.out.println(key + " " + duplicateList.get(tracker).getBluetoothId());
                                        if(key.equals(duplicateList.get(tracker).getBluetoothId())){
                                            // If the name in the list is different from the actual device name,
                                            // change the name in the list.
                                            if(!(tempVar.get(key).equals(duplicateList.get(tracker).getNickName()))){
                                                listOfDevicesTracked.get(tracker).setNickName(tempVar.get(key));
                                                duplicateList.get(tracker).setNickName(tempVar.get(key));
                                            }
                                            System.out.println("true");
                                            duplicateList.get(tracker).setStatus(1);
                                            // 1 for available status.
                                            //duplicateList.get(tracker).setEpochTime(currTime);
                                            check = 1;
                                        }
                                        //tracker= tracker +1;
                                    }
                                }
                                // If the device is not available in the list, create a new object.
                                if(check==0){
                                    Device newDev = new Device();
                                    newDev.setBluetoothId(key);
                                    newDev.setNickName(tempVar.get(key));
                                    newDev.setStatus(1);
                                    newDev.setEpochTime(currTime);
                                    newDev.setTrack(0);
                                    duplicateList.add(newDev);
                                }
                                check=0;
                            }
                        }
                        currentTime = System.currentTimeMillis();
                        timeDifference = currentTime - uiRenderTime;
                        // For good user experience, only after a certain minimum time
                        // the old user interface will be disposed and fresh User Interface will be displayed.
                        if(timeDifference>60000){
                            uiRenderTime = System.currentTimeMillis();
                            fileWriter.writeDataToFiles(duplicateList);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(isRefReady.get()){
                        duplicateList.notify();
                        try {
                            duplicateList.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if(check_1==0){
            // Exit signal received, exit and dump all threads, exit the application smoothly.
            System.exit(0);
        }

    }
}
