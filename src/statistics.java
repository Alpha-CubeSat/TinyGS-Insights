package TinyGS;

import java.io.*;
import java.util.*;

public class statistics {

    public static void main (String[] args) throws FileNotFoundException {
        Map<String, int[]> stationList = new HashMap<>();

        File input = new File("parsed.csv");
        Scanner sc = new Scanner(input);
        int transmitPower = 0;
        int powerIndex = 0;
        String[] lineArr = {};

        while(sc.hasNext()) {
            String line = sc.next();
            lineArr = line.split(",");
            transmitPower = Integer.parseInt(lineArr[0]);
            switch (transmitPower){
                    case 63:
                        powerIndex = 0;
                        break;
                    case 158:
                        powerIndex = 1;
                        break;
                    case 500:
                        powerIndex = 2;
                        break;
                    case 800:
                        powerIndex = 3;
                        break;
                    case 2000:
                        powerIndex = 4;
                        break;
            }
            String[] stationSeries = lineArr[7].split("-");
            for (String station: stationSeries){
                if (stationList.get(station) != null){
                    int[] oldData = stationList.get(station);
                    oldData[powerIndex] += 1;
                    stationList.put(station, oldData);
                }
                else{
                    int[] data = {0, 0, 0, 0, 0};
                    stationList.put(station, data);
                }
            }
        }
        File output = new File("statistics.csv");
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output, true)))) {
            out.println("Station Name, 63mW, 158mW, 500mW, 800mW, 2000mW");
            for (String station : stationList.keySet()){
                out.print(station + ",");
                for (int count : stationList.get(station)){
                    out.print(count + ",");
                }
                out.print("\n");
            }
        }
        catch (Exception e) {
            System.out.println("parsing error");
        }
    }
}
