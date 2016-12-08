import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class hashtagcounter {
/*A map gives value as output by taking a key as input. This method is used to access key from value
    In the map of nodes, there should be no duplicates so this method would return relavant keys accurately*/
    public static Object getKeyString (Map m, Object value) {
        for (Object o : m.keySet()) {
            if (m.get(o).equals(value)) 
                return o;
        }
        return null;
    }
    
    public static void main(String[] args) throws IOException {
        ListIterator itr;   //to iterate over buffer holding extracted nodes
        Map<String, Node> myMap = new HashMap<>(); // conforming structure for node management and parsing
        FibHeap h = new FibHeap();
        StringBuilder currentLine = new StringBuilder(""); // input parsing; SBuilder over string to avoid recurring string objects 
        File outputFile = new File ( "output_file.txt" );
        
        outputFile.createNewFile();
        
        BufferedReader br = new BufferedReader (new FileReader(args[0]));
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(outputFile));
        
        while ((currentLine.append(br.readLine())) != null) {
            if (currentLine.charAt(0) == '#') {            
                String iterStr = currentLine.substring(1, currentLine.indexOf(" "));
                int i = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1, currentLine.length()));    
                
                Node temp = new Node(i, iterStr); //holder for newly created node
                
                if (!myMap.containsKey(iterStr)) { //if key not found
                    myMap.put(iterStr, temp);
                    h.insert(temp);
                }else { // remove pointer to old node and update it to new node
                    int newFrequency = myMap.get(iterStr).getFrequency() + i;
                    Node x = h.increaseKey(myMap.get(iterStr),newFrequency);
                    myMap.remove(iterStr);
                    myMap.put(iterStr, x);
                /* remove and reinsert in hashmap too so as to avoid clashing pointers*/
                }
                
            } else if (currentLine.toString().equalsIgnoreCase("STOP")) {
                br.close();
                break; //end of parsing
                
            }else {
                List<Node> outputBuffer =new ArrayList<>();
                int count = Integer.parseInt(currentLine.toString());
                for ( int i = 0; i < count; i++ ) {
                    Node bufHold = h.extractMax();
//                    Node buf = new Node(bufHold.getFrequency(), bufHold.getHashtagString());
                    outputBuffer.add(i, bufHold);
//                    myMap.remove((String) getKeyString(myMap,bufHold));
                }
                
                int commaCount = 0;    //maintaining this to avoid the last comma
//                itr = outputBuffer.listIterator();
                StringBuilder sb = new StringBuilder("");

                for (int i = 0; i < outputBuffer.size(); i++) {
//                while (itr.hasNext()) {
                    Node yo = outputBuffer.get(i);
                    sb.append(yo.s);
                    if ( commaCount < Integer.parseInt(currentLine.toString()) - 1 )
                        sb.append(",");
                    /* removal of old node, and inserting a new node initialized to the extracted node
                    instead of reinserting the old node because the old node sitting in buffer has pointers to other
                    existing nodes as parents and children and siblings; this will leas to conflicting assignments of new pointers
                    and leading to NullPointerExceptions during the runtime*/
//                    Node yo1 = new Node(yo.frequency,yo.s);
                    h.insert(yo);
//                    myMap.put(yo1.s, yo1);
                    commaCount++;
                }
                bw.write(sb.toString());
                bw.newLine();
            }
            currentLine.setLength(0); // resetting the stringBuilder
            bw.flush();
        }
        bw.close();    
    }
}
