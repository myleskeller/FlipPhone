package com.flipphone.listing;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.jaredrummler.android.device.DeviceName;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.telephony.TelephonyManager.PHONE_TYPE_CDMA;
import static android.telephony.TelephonyManager.PHONE_TYPE_GSM;
import static android.telephony.TelephonyManager.PHONE_TYPE_NONE;
import static android.telephony.TelephonyManager.PHONE_TYPE_SIP;

public class PhoneSpecifications { //maybe extending application not a good idea?
    private static final String TAG = "SPEC";

    public String internalStorage;
    public String expandableStorage;
    public String battery;
    public String telephony;
    public String manufacturer;
    public String model;
    public String ram;
    public String cpu;
    public String os;
    public String resolution;
    public String screen;
    public String name;
    private String deviceName;

    private Context context; //ugh...

    public PhoneSpecifications(Context _context, String _resolution, String _screen, String _battery) {
        context = _context;
        getDeviceNames();
        this.manufacturer = getManufacturer();
        this.name = getName();
        this.model = getModel();
        this.telephony = getTelephony();
        this.battery = _battery;
        this.os = getOS();
        this.ram = getMemorySize();
        this.cpu = getCPU();
        this.internalStorage = getInternalMemorySize();
        this.expandableStorage = getExpandableStorage();
        this.resolution = _resolution;
        this.screen = _screen;
    }

    public PhoneSpecifications() {
    }

    public static String getStringFromInputStream(InputStream stream) throws IOException {
        int n = 0;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, "UTF8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
        return writer.toString();
    }

    private static String getInternalMemorySize() {
        final File pathOBJ = Environment.getDataDirectory();
        final StatFs stat = new StatFs(pathOBJ.getPath());
        final long blockSize;
        final long totalBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
        } else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
        }
        double usable = totalBlocks * blockSize / 1024.0 / 1024.0 / 1024.0;
        int total = 0;

        if (usable > 4 && usable < 8) //8gb storage
            total = 8;
        else if (usable > 8 && usable < 16) //16gb storage
            total = 16;
        else if (usable > 16 && usable < 32) //32gb storage
            total = 32;
        else if (usable > 32 && usable < 64) //64gb storage
            total = 64;
        else if (usable > 64 && usable < 128) //128gb storage
            total = 128;
        else if (usable > 128 && usable < 256) //256gb storage
            total = 256;
        else if (usable > 256 && usable < 512) //512gb storage
            total = 512;
        else if (usable > 512 && usable < 1024) //1tb storage
            total = 1024;

        Log.e(TAG, "storage: " + total + " GB, (" + (int) usable + " GB)");
        return "storage: " + total + " GB, (" + (int) usable + " GB)";
    }

    public String getMemorySize() {
        RandomAccessFile reader;
        String load;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double totRam;
        String lastValue = "";
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);
            }
            reader.close();
            totRam = Double.parseDouble(value);
            double mb = totRam / 1024.0;
            double gb = totRam / 1048576.0;
            if (gb > 1) {
                lastValue = String.valueOf((int) Math.ceil(gb)).concat(" GB");
//                lastValue = twoDecimalForm.format(gb).concat(" GB");
            } else if (mb > 1) {
                lastValue = twoDecimalForm.format(mb).concat(" MB");
            } else {
                lastValue = twoDecimalForm.format(totRam).concat(" KB");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Log.e(TAG, "memory: " + lastValue);
        return lastValue;
    }

    public String getTelephony() {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String carrier = manager.getNetworkOperatorName().trim();
        int radio_val = manager.getPhoneType();
        switch (radio_val) {
            case PHONE_TYPE_GSM: {
                Log.e(TAG, "GSM (" + carrier + ")");
                return "GSM (" + carrier + ")";
            }
            case PHONE_TYPE_CDMA: {
                Log.e(TAG, "CDMA (" + carrier + ")");
                return "CDMA (" + carrier + ")";
            }
            case PHONE_TYPE_SIP: {
                return "SIP (VoIP)";
            }
            case PHONE_TYPE_NONE:
            default: {
                return "N/A";
            }
        }
    }
    private void getDeviceNames() {
        DeviceName.with(context).request(new DeviceName.Callback() {
            @Override
            public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                if (manufacturer != null)
                    manufacturer = info.manufacturer;  // "Samsung"; null if depreciated
                else
                    manufacturer = capitalize(Build.BRAND);    // "Samsung"
                name = info.marketName;            // "Galaxy S8+"
                //TODO: don't return model if model == manufacturer
//                model = info.model;                // "SM-G955W"
                model = Build.BOARD; //honestly, i prefer this for the model
                deviceName = info.getName();       // "Galaxy S8+"
                // FYI: We are on the UI thread.
                Log.e(TAG, "phone: " + manufacturer + ' ' + deviceName + " (" + model + ")");
            }
        });
    }

    public String getOS() {
        double release = Double.parseDouble(Build.VERSION.RELEASE.replaceAll("(\\d+[.]\\d+)(.*)", "$1"));
        String codeName = "Unsupported";//below Jelly bean OR above Oreo
        if (release >= 4.1 && release < 4.4) codeName = "Jelly Bean";
        else if (release < 5) codeName = "Kit Kat";
        else if (release < 6) codeName = "Lollipop";
        else if (release < 7) codeName = "Marshmallow";
        else if (release < 8) codeName = "Nougat";
        else if (release < 9) codeName = "Oreo";
        else if (release < 10) codeName = "Pie";
        else if (release < 11) codeName = "10";
        else if (release < 12) codeName = "11";

        Log.e(TAG, "os: " + codeName + " v" + release);
        return codeName + " v" + release + ", API Level: " + Build.VERSION.SDK_INT;
    }

//    private String getProcessor(){ //vomits out everything about cpu, mostly for debugging
//        String[] DATA = {"/system/bin/cat", "/proc/cpuinfo"};
//        String Holder = "";
//        byte[] byteArry = new byte[1024];
//
//        try{
//            ProcessBuilder processBuilder = new ProcessBuilder(DATA);
//            Process process = processBuilder.start();
//            InputStream inputStream;
//            inputStream = process.getInputStream();
//            while(inputStream.read(byteArry) != -1){
//                Holder = Holder + new String(byteArry);
//            }
//            inputStream.close();
//        } catch(IOException ex){
//            ex.printStackTrace();
//        }
//        return Holder;
//    }

//    private String getProcessor() {
//        try {
//        Process proc = Runtime.getRuntime().exec("cat /proc/cpuinfo");
//        InputStream is = proc.getInputStream();
//        return  getStringFromInputStream(is);
//    }
//        catch (IOException e) {
//        Log.e(TAG, "------ getCpuInfo " + e.getMessage());
//    }
//        return null;
//    }

    private String getProcessor(){ //vomits out everything about cpu, mostly for debugging
        String[] DATA = {"/system/bin/cat", "/proc/cpuinfo"};
        String Holder = "";
        byte[] byteArry = new byte[1024];

        try{
            ProcessBuilder processBuilder = new ProcessBuilder(DATA);
            Process process = processBuilder.start();
            InputStream inputStream;
            inputStream = process.getInputStream();
            while(inputStream.read(byteArry) != -1){
                String temp = new String(byteArry);
                if (temp.contains("Hardware")){
                    return temp.replace(":", "").trim();
                }
            }
            inputStream.close();
        } catch(IOException ex){
            ex.printStackTrace();
        }
        return "Arm64";
    }

    public String getCPU() {
        int cores = getNumberOfCores();
        Log.e(TAG, "cpu: " + cores + "-core");
        return String.valueOf(cores) + "-core";
    }

//    private String getProcessor() {
//        BufferedReader br = null;
//        try {
//            br = new BufferedReader(new FileReader("/proc/cpuinfo"));
//            String str;
//            Map<String, String> output = new HashMap<>();
//            while ((str = br.readLine()) != null) {
//                String[] data = str.split(":");
//                if (data.length > 1) {
//                    String key = data[0].trim().replace(" ", "_");
//                    if (key.equals("model_name")) key = "cpu_model";
//                    String value = data[1].trim();
//                    if (key.equals("cpu_model"))
//                        value = value.replaceAll("\\s+", " ");
//                    output.put(key, value);
//                }
//            }
//            br.close();
//            return output.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private int getNumberOfCores() {
        if (Build.VERSION.SDK_INT >= 17) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return getNumCoresOldPhones();
        }
    }

    private int getNumCoresOldPhones() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }
        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            //Default to return 1 core
            return 1;
        }
    }

    private String getExpandableStorage() {
        if (externalMemoryAvailable()) {
            Log.e(TAG, "sdcard: " + getTotalExternalMemorySize() + "SD Inserted");
            return getTotalExternalMemorySize() + "SD Inserted";
        } else
            return null;
    }

    //TODO: test with device that has external SD card
    private boolean externalMemoryAvailable() {
        // i have no way to test if this works or not.
        Log.e(TAG, "sdcard: " + String.valueOf(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) && android.os.Environment.isExternalStorageRemovable()));
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) && android.os.Environment.isExternalStorageRemovable();
    }

    //TODO: test with device that has external SD card
    private String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.
                    getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long BlockSize = stat.getBlockSize();
            long TotalBlocks = stat.getBlockCount();
            return formatSize(TotalBlocks * BlockSize);
        } else {
            return null;
        }
    }

    private String formatSize(long size) {
        String suffixSize = null;
        if (size >= 1024) {
            suffixSize = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffixSize = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffixSize = "GB";
                    size /= 1024;
                }
            }
        }
        StringBuilder BufferSize = new StringBuilder(Long.toString(size));
        int commaOffset = BufferSize.length() - 3;
        while (commaOffset > 0) {
            BufferSize.insert(commaOffset, ',');
            commaOffset -= 3;
        }
        if (suffixSize != null) BufferSize.append(suffixSize);
        return BufferSize.toString();
    }

    String getManufacturer() {
        return manufacturer;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

//    @Override
//    public String toString(){
//        String output = "Specs:\n";
//        output += "internalStorage: " + this.internalStorage + '\n';
//        output += "expandableStorage: " + this.expandableStorage + '\n';
//        output += "battery: " + this.battery + '\n';
//        output += "telephony: " + this.telephony + '\n';
//        output += "manufacturer: " + this.manufacturer + '\n';
//        output += "model: " + this.model + '\n';
//        output += "ram: " + this.ram + '\n';
//        output += "cpu: " + this.cpu + '\n';
//        output += "os: " + this.os + '\n';
//        output += "resolution: " + this.resolution + '\n';
//        output += "screen: " + this.screen + '\n';
//        output += "name: " + this.name + '\n';
//        output += "deviceName: " + this.deviceName;// + '\n';
//
//        return output;
//    }

    @Override
    public String toString() {
        String output = this.manufacturer + ' ' + this.name + " (" + this.model + ")" + '\n';
        //telephony
        output += "Carrier: " + this.telephony + '\n';
        //storage
        output += "Storage: " + this.internalStorage;
        if (this.expandableStorage != null)
            output += ", (expandable)";
        output += '\n';
        //screen
        output += "Screen: " + this.screen + " @" + this.resolution + '\n';
        output += "Battery: " + this.battery + '\n';
        output += "Memory: " + this.ram + '\n';
        output += "Processor: " + this.cpu + '\n';
        output += "Android " + this.os + '\n';
//        output += "deviceName: " + this.deviceName;// + '\n';

        return output;
    }
}