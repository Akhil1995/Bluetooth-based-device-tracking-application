package bas.devicediscovery;
/**
 * Created by Akhil on 12-08-2016.
 */
import bas.devicediscovery.bluetooth.DeviceScanner;

import javax.bluetooth.*;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

/**
 * This class is for creating a thread, running the ScanDevices code.
 */
class ScanDevices extends Thread{
    private BlockingQueue<HashMap<String,String>> q;
    ScanDevices(BlockingQueue<HashMap<String,String>> queue){
        this.q= queue;
    }
    public void run(){
        System.out.println("Connecting to bluetooth hardware");
        DeviceScanner deviceScanner = new DeviceScanner();
        deviceScanner.setQueueForListOfDevices(q);
        try {
            deviceScanner.startDeviceInquiry();
            System.out.println("Bluetooth module started");
        } catch (BluetoothStateException e) {
            e.printStackTrace();
        }
    }
}
