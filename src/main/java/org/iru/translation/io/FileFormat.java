package org.iru.translation.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class FileFormat {
    
    public enum FileType {
        WINDOWS("\r\n"), UNIX("\n"), MAC("\r"), UNKNOWN("\n");
        
        private final String EOL;
        
        FileType(String EOL) {
            this.EOL = EOL;
        }
        
        public String getEOL() {
            return EOL;
        }
    }

    private static final char CR = '\r';
    private static final char LF = '\n';

    public static FileType discover(File f) throws IOException {    

        FileType result;
        try (Reader reader = new BufferedReader(new FileReader(f))) {
            result = discover(reader);
        }
        return result;
    }

    private static FileType discover(Reader reader) throws IOException {
        int c;
        while ((c = reader.read()) != -1) {
            switch(c) {        
            case LF: return FileType.UNIX;
            case CR: {
                if (reader.read() == LF) return FileType.WINDOWS;
                return FileType.MAC;
            }
            default:            
            }
        }
        return FileType.UNKNOWN;
    }
}