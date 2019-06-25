package bas.devicediscovery.storage;

import bas.devicediscovery.bluetooth.Device;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class FileIO {
    public void writeDataToFiles(LinkedList<Device> listOfDevices){
        int tracker=0;
        if(!listOfDevices.isEmpty()){
            for(tracker=0;tracker<listOfDevices.size();tracker++){
                if(listOfDevices.get(tracker).getTrack().equals(1)){
                    // Write the epoch time and the status to the corresponding file
                    try {
                        FileWriter fw = new FileWriter(listOfDevices.get(tracker).getFileLoc(),true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write("\n");
                        bw.write(Long.toString(listOfDevices.get(tracker).getEpochTime()));
                        bw.write("\n");
                        if(listOfDevices.get(tracker).getStatus().equals(1)){
                            bw.write("A");
                        }
                        else if(listOfDevices.get(tracker).getStatus().equals(0)){
                            bw.write("U");
                        }
                        bw.close();
                        fw.close();
                    } catch (IOException e){
                        e.printStackTrace();
                        // If the location is not found, create source directory and retry.
                    }
                }
            }
        }
    }
}
