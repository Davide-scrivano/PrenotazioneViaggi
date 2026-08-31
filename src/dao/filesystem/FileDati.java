package dao.filesystem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

final class FileDati {

    private final File file;

    FileDati(String percorso) {
        this.file = new File(percorso);
    }

    public boolean esiste() {
        return file.exists();
    }

    public String percorso() {
        return file.getPath();
    }

    public DataInputStream apriLettura() throws IOException {
        return new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
    }

    public DataOutputStream apriScrittura(boolean inCoda) throws IOException {
        preparaCartella();
        return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file, inCoda)));
    }

    private void preparaCartella() throws IOException {
        File cartella = file.getParentFile();
        if (cartella != null && !cartella.exists() && !cartella.mkdirs()) {
            throw new IOException("Impossibile creare la cartella " + cartella.getPath());
        }
    }
}
