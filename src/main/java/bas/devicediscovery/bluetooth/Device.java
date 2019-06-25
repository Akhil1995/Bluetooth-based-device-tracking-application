package bas.devicediscovery.bluetooth;

/**
 * Created by Akhil on 30-06-2017.
 */
public class Device {
    public void cloneFromOtherDeviceObject(Device d){
        this.deviceId = d.getDeviceId();
        this.bluetoothId = d.getBluetoothId();
        this.fileLoc = d.getFileLoc();
        this.track = d.getTrack();
        this.status = d.getStatus();
        this.nickName = d.getNickName();
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }
    // Default value unless until modified
    private Integer deviceId= 10000;

    public String getBluetoothId() {
        return bluetoothId;
    }

    public void setBluetoothId(String bluetoothId) {
        this.bluetoothId = bluetoothId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private String bluetoothId;
    private String nickName;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFileLoc() {
        return fileLoc;
    }

    public void setFileLoc(String fileLoc) {
        this.fileLoc = fileLoc;
    }
    public long getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(long epochTime) {
        this.epochTime = epochTime;
    }

    private Integer status;
    private String fileLoc;
    private long epochTime;

    public Integer getTrack() {
        return track;
    }

    public void setTrack(Integer track) {
        this.track = track;
    }

    private Integer track;
}
