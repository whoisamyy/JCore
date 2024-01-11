package ru.whoisamyy.core.out;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;

public class MultiOutStream extends OutputStream {
    private static MultiOutStream instance;

    HashSet<OutputStream> streams = new HashSet<>();


    /**
     * Not recommended. Creates new instance of {@code MultiOutStream}, containing given in argument {@code OutputStream}s
     * @param streams {@code OutputStream}s that will be in new instance of {@code MultiOutStream}
     */
    public MultiOutStream(OutputStream... streams) {
        for (OutputStream os : streams) {
            this.addStream(os);
        }
    }

    @Override
    public void write(int b) throws IOException {
        for (OutputStream s : streams)
            s.write(b);
    }

    @Override
    public void close() throws IOException {
        for (OutputStream s : streams)
            s.close();
    }

    @Override
    public void flush() throws IOException {
        for (OutputStream s : streams)
            s.flush();
    }

    public static MultiOutStream getInstance() {
        if (instance==null) {
            instance = new MultiOutStream();
        }
        return instance;
    }


    /**
     * It's not possible (for now) to remove any of added streams, so be careful if you use this
     * @param out {@code OutputStream} that will be added as output stream
     * @return instance of {@code MultiOutStream}
     */
    public MultiOutStream addStream(OutputStream out) {
        streams.add(out);
        return this; //instance
    }
}
