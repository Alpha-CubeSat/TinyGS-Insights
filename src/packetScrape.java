package TinyGS;

import java.io.*;
import java.util.Scanner;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;


public class packetScrape {

    public static int fileNo = 0;
    public static int packetNo = 0;


    public static void main(String[] args) throws Exception {
        while (fileNo <= 42){
            grabData();
            parsePackets();
            fileNo++;
            TimeUnit.HOURS.sleep(7);
        }
    }

    public static void grabData(){
        try {
            URL url = new URL("https://api.tinygs.com/v2/packets");
            File destinationFile = new File("packets" + fileNo + ".txt");
            destinationFile.createNewFile();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            BufferedWriter writer = new BufferedWriter(new FileWriter("packets" + fileNo + ".txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
            }
            reader.close();
            writer.close();
            System.out.println("Successfully Downloaded summary file #" + fileNo);
        }
        catch (MalformedURLException mue) {
            System.out.println("Malformed URL Exception raised");
        }
        catch (IOException ioe){
            System.out.println("Data write error");
        }
    }

    public static void parsePackets() throws Exception {
        File input = new File("packets" + fileNo + ".txt");
        File output = new File("parsed.csv");
        Scanner sc = new Scanner(input);
        sc.useDelimiter(",");
        String packetID = "";
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output, true)))) {
            if (fileNo == 0) out.println("Transmit Power, Stations Received, Frequency, Spreading Factor, Bandwidth, Coding Rate, Satellite, Station List");
            while(sc.hasNext()){
                String line = sc.next();
                if (line.contains("stationNumber") || line.contains("tinygsTxPower") || line.contains("satellite") || line.contains("freq")
                    || line.contains("sf") || line.contains("bw") || line.contains("cr") || line.contains("\"id\"")){
                    String[] lineArr = line.split(":");
                    if (lineArr[0].equals("\"satellite\"") || lineArr[0].equals("\"freq\"") || lineArr[0].equals("\"sf\"")
                            || lineArr[0].equals("\"bw\"") || lineArr[0].equals("\"cr\"")){
                        out.print(lineArr[1].replaceAll("\"", "") + ",");
                    }
                    if (lineArr[0].equals("\"tinygsTxPower\"")){
                        out.print(lineArr[1].replaceAll("}", "") + ",");
                    }
                    if (lineArr[0].equals("\"stationNumber\"")){
                        out.print(lineArr[1].replaceAll("}", "").replaceAll("]", "") + ",");
                    }
                    if (lineArr[0].equals("\"id\"")){
                        packetID = lineArr[1].replaceAll("\"", "");
                        out.println(packetAnalysis(packetID));
                        TimeUnit.SECONDS.sleep(35);
                    }
                }
            }
            System.out.println("Successfully parsed file #" + fileNo);
        }
        catch (Exception e) {
            System.out.println("parsing error");
        }
    }

    public static String packetAnalysis(String packetID){
        String receivingStations = "";
        String text = "";
        try {
            URL url = new URL("https://api.tinygs.com/v1/packet/" + packetID);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                text += line;
            }
            reader.close();
            System.out.println("Successfully downloaded packet file #" + packetNo);
            packetNo++;
        }

        catch (MalformedURLException mue) {
            System.out.println("Malformed URL Exception raised");
        }
        catch (IOException ioe){
            System.out.println("Data write error");
        }

        String[] split = text.split("\"stations\":");
        String[] stationFields = split[1].split("\"crc_error\"");
        String[] stationAttr = {};
        String[] stationLabels = {};
        for (String station : stationFields){
            stationAttr = station.split(",");
            for (String chars : stationAttr){
                stationLabels = chars.split(":");
                if (stationLabels[0].equals("\"name\"")) receivingStations += stationLabels[1].replaceAll("\"", "") + "-";
            }
        }
        return receivingStations;
    }
}
