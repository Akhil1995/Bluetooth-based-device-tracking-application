package bluetoothdevicediscovery;
/**
 * Created by Akhil on 12-08-2016.
 */
import javax.bluetooth.*;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class is for creating a thread, running the ScanDevices code.
 */
class ScanDevicesThread extends Thread{
    private BlockingQueue<HashMap<String,String>> q;
    ScanDevicesThread(BlockingQueue<HashMap<String,String>> queue){
        this.q= queue;
    }
    public void run(){
        System.out.println("Connecting to bluetooth hardware");
        ScanDevices newConnection = new ScanDevices();
        newConnection.setQueueForListOfDevices(q);
        try {
            newConnection.startDeviceInquiry();
            System.out.println("Bluetooth module started");
        } catch (BluetoothStateException e) {
            e.printStackTrace();
        }
    }
}
public class ScanDevices {
    private static LocalDevice hostDevice;
    private static DiscoveryAgent discAgent;
    private BlockingQueue queueForListOfDevices;

    void setQueueForListOfDevices(BlockingQueue queueForListOfDevices) {
        this.queueForListOfDevices = queueForListOfDevices;
    }

    private final Object inquiryCompletedEvent = new Object();
    private HashMap<String, String> listOfDevices = new HashMap<String, String>();

    /**
     * This method initiates the bluetooth stack of that device from which thia application is being used
     *
     * @throws BluetoothStateException
     */
    private void bluetoothInit() throws BluetoothStateException {
        hostDevice = null;
        discAgent = null;
        // Make the laptop bluetooth discoverable.
        hostDevice = LocalDevice.getLocalDevice();
        hostDevice.setDiscoverable(DiscoveryAgent.GIAC); // GIAC is for General Discovery, discoverable at all times.
        discAgent = hostDevice.getDiscoveryAgent();
    }

    /**
     * This method will start the device enquiry
     * and push all the available devices to a HashMap, which is inturn pushed to a queue,
     * for the 2nd thread to process
     *
     * @throws InterruptedException
     */
    void startDeviceInquiry() throws BluetoothStateException {
        if (hostDevice == null) {
            //TODO:Start device initiation.
            bluetoothInit();
        }
        if (queueForListOfDevices == null) {
            //TODO:No queue initiated
        }
        DiscoveryListener listener = new DiscoveryListener() {
            public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
                try {
                    listOfDevices.put(remoteDevice.getBluetoothAddress(), remoteDevice.getFriendlyName(false));
                } catch (IOException ex) {
                    //TODO:Deal with it later
                }
            }

            public void servicesDiscovered(int i, ServiceRecord[] serviceRecords) {
                System.out.println(serviceRecords.toString());
            }

            public void serviceSearchCompleted(int i, int i1) {

            }

            public void inquiryCompleted(int i) {
                System.out.println(" Enquiry completed");
                synchronized (inquiryCompletedEvent) {
                    inquiryCompletedEvent.notifyAll();
                }
            }
        };
        while (true) {
            synchronized (inquiryCompletedEvent) {
                boolean started = false;
                HashMap<String, String> dummyMap = new HashMap<String, String>();
                try {
                    started = hostDevice.getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
                } catch (BluetoothStateException e) {
                    //TODO:Handle Exception Later
                    e.printStackTrace();
                    //BREAK Out of the loop, once this exception occurs.
                    break;
                }
                if (started) {
                    System.out.println("Wait till the enquiry is completed");
                    try {
                        inquiryCompletedEvent.wait();
                        dummyMap.putAll(listOfDevices);//Copy global variable into new local variable
                        dummyMap.put("default", "default");
                        String s = dummyMap.toString();
                        System.out.println(s);
                        if (!dummyMap.isEmpty()) {
                            queueForListOfDevices.add(dummyMap);
                        }
                        listOfDevices.clear();// Clear Global variable for more enquiries
                    } catch (InterruptedException e) {
                        //TODO:Handle Exception
                    }
                }
            }
        }
    }
    /*
    public static void main(String args[]) {
        int i;
        BlockingQueue<HashMap<String, String>> q = new ArrayBlockingQueue<HashMap<String, String>>(10);
        ScanDevicesThread thread1 = new ScanDevicesThread(q);
        HashMap<String, String> dummyMap1 = new HashMap<String, String>();
        thread1.start();
        for (i = 0; i < 4; i++) {
            System.out.println(q.remainingCapacity());
            try {
                dummyMap1 = q.take();
                String s = dummyMap1.toString();
                System.out.println(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread1.interrupt();
        }

        }
        */
}
