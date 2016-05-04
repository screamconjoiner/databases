/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HW3;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author Connor
 */
public class DBMS {
        // These are all used for enclosing data
    public final byte TAB_BEG = 5;  
    public final byte TAB_END = 6;
    public final byte TKEY_BEG = 2; // Table key
    public final byte TKEY_END = 3;
    public final byte REL_BEG = 4;
    public final byte REL_END = 5;
    
    
    public DBMS() {
        try {
            Files.delete(Paths.get("test.db"));
        } catch (IOException ex) {
            Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
     /**
     * Create a new table.
     * @param table: schema
     * @param key: key to find table
     */
    public void createTable(Object table, String key) {
        StringBuilder sb = new StringBuilder();
        addKey(sb, key.getBytes());
        addAttributes(sb, table.toString().getBytes());
        writeTable(sb);
    }
    
    private void addKey(StringBuilder sb, byte[] keydata) {
       sb.append(TKEY_BEG).append(" ");
        for (byte b : keydata) {
            sb.append(b).append(" ");
        }
        sb.append(TKEY_END).append(" ");
        sb.append("\n");
    }
    
    private void addAttributes(StringBuilder sb, byte[] tabledata) {
       /* sb.append(TAB_BEG).append(" ");
        for (byte b : tabledata) {
            sb.append(b).append(" ");
        }
        sb.append(TAB_END).append(" ");
        sb.append("\n");*/ 
    }
    
    private void writeTable(StringBuilder sb) {
        try {
            try (RandomAccessFile raf = new RandomAccessFile("test.db", "rw")) {
                raf.seek(raf.length());
                for (byte b : sb.toString().getBytes()) {
                    raf.writeByte(b);
                }
                raf.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void insertRecord(Object record) {
        byte[] data = record.toString().getBytes();
        
        StringBuilder sb = new StringBuilder();
        sb.append(REL_BEG).append(" ");
        for (byte b : data) {
            sb.append(b).append(" ");
        }
        sb.append(REL_END).append(" ");
        sb.append("\n");
        byte[] converted = sb.toString().getBytes();
        
        try {
            try (RandomAccessFile raf = new RandomAccessFile("test.db", "rw")) {
                raf.seek(raf.length());
                for (byte b : converted) {
                    System.out.println(b);
                    raf.writeByte(b);
                }
                raf.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void findRecord(String query, String key) {
        StringBuilder record = new StringBuilder();
        byte[] keydata = key.getBytes();
        byte[] querydata = query.getBytes();
        try {
            RandomAccessFile raf = new RandomAccessFile("test.db", "rw");
            
            raf.seek(0);
            System.out.println(raf.read());
          /*  raf.seek(findFirstTuple(raf,keydata)); // Roll to first tuple
            byte[] relation = getRelation(raf);
            if (relation.length > 0) {
                // If relation contains querydata
                if (contains(relation, querydata)) {
                    System.out.println("here");
                    for (byte b : relation) {
                        record.append(Integer.toHexString(b));
                    }
                }
            }
            // go to next relation
            */
            raf.close();
        } catch (IOException ex) {
            Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
        }
        Logger.getLogger(Record.class.getName()).log(Level.INFO, record.toString());
    }
    
    /**
     * Return true if b is a subset of a
     * @param a
     * @param b
     * @return 
     */
    private boolean contains(byte[] a, byte[] b) {
        boolean contains = false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == b[0]) {
                
            }
        }
        return contains;
    }
    
    
    private byte[] getRelation(RandomAccessFile raf) throws IOException {
        int relationLength = getRelationLength(raf);
        byte[] relation = new byte[relationLength];
        for (int i = 0; i < relationLength; i++) {
            relation[i] = raf.readByte();
        }
        return relation;
    }
    
    /**
     * NEEDS TO ROLL BACK TO START POSITION.
     * @param raf
     * @return
     * @throws IOException 
     */
    private int getRelationLength(RandomAccessFile raf) throws IOException {
        long position = raf.getFilePointer();
        int count = 0;
        raf.seek(position-1); // We can do this because we know we're in a relation.
        byte b = raf.readByte();
        if (b == REL_BEG) {
            while (b != REL_END) {
                b = raf.readByte();
                count++;
            }
        }
        raf.seek(position);
        return count;
    }
    
    
    /**
     * Provide a long that is the offset indicating the location of the 
     * first actual tuple of data contained within a table that is specified
     * by the byte[] keydata.
     * Or, if there is no such relation, return -1;
     * @param raf
     * @param keydata
     * @return 
     */
    private long findFirstTuple(RandomAccessFile raf, byte[] keydata) throws IOException {
        try {
            //position = raf.getFilePointer();
            byte b = raf.readByte();
            if (b == TKEY_BEG) {
                if (byteCompare(raf, keydata)) {
                    while (b != TKEY_END) {
                        b = raf.readByte();
                    }
                    if (b == TAB_BEG) {
                        while (b != TAB_END) {
                            b = raf.readByte();
                        }
                    }
                    if (b == REL_BEG) {
                        b = raf.readByte();
                        return raf.getFilePointer();
                    }
                }
            } else { // Find TKEY_BEG
                while (b != TKEY_BEG && raf.getFilePointer() < raf.length()-1){
                    b = raf.readByte();                    
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
        }
        return raf.getFilePointer();
    }
    
    
    /**
     * Compares the current place of raf to the byte[] data
     * @param raf
     * @param data
     * @return 
     */
    private boolean byteCompare(RandomAccessFile raf, byte[] data) {
        boolean isEqual = false;
        try {
            long startingposition = raf.getFilePointer();
            isEqual = true;
            byte curbyte = raf.readByte();
            for (int i = 0; i < data.length; i++) {
                if (curbyte != data[i]) {
                    isEqual = false;
                    break;
                }
            }
            raf.seek(startingposition);
        } catch (IOException ex) {
            Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isEqual;
    }
    
    public void modifyRecord(String query, String modified) {
        //find record
        // change it
        // rewrite it
    }
    
    public void deleteRecord(String query) {
        
    }
    
    public void printTableBytes() {
        
    }
}
