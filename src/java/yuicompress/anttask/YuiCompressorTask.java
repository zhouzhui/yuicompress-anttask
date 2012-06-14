package yuicompress.anttask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.LogLevel;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class YuiCompressorTask extends Task {
    private boolean munge = true;

    private boolean warn = false;

    private boolean preserveAllSemiColons = false;

    private boolean preserveStringLiterals = false;

    private int maxColumnWidth = -1;

    private File toDir = null;

    private List<FileSet> fileSets = new LinkedList<FileSet>();

    private String type = null;

    private String inputCharset = "utf-8";

    private String outputCharset = "utf-8";

    public void addFileset(FileSet fileset) {
        if (null == fileset) {
            throw new IllegalArgumentException("fileset could not be null");
        }
        this.fileSets.add(fileset);
    }

    public void setTodir(File toDir) {
        this.toDir = toDir;
    }

    public void setType(String type) {
        if (null == type) {
            throw new IllegalArgumentException("type could not be null");
        }
        if (!type.equalsIgnoreCase("js") && !type.equalsIgnoreCase("css")) {
            throw new IllegalArgumentException("type must be either js or css");
        }
        this.type = type;
    }

    public void setMunge(boolean munge) {
        this.munge = munge;
    }

    public void setWarn(boolean warn) {
        this.warn = warn;
    }

    public void setPreserveAllSemiColons(boolean preserveAllSemiColons) {
        this.preserveAllSemiColons = preserveAllSemiColons;
    }

    public void setPreserveStringLiterals(boolean preserveStringLiterals) {
        this.preserveStringLiterals = preserveStringLiterals;
    }

    public void setMaxColumnWidth(int maxColumnWidth) {
        this.maxColumnWidth = maxColumnWidth;
        if (this.maxColumnWidth < 0) {
            this.maxColumnWidth = -1;
        }
    }

    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }

    public void setOutputCharset(String outputCharset) {
        this.outputCharset = outputCharset;
    }

    @Override
    public void execute() {
        try {
            for (int i = 0; i < this.fileSets.size(); i++) {
                FileSet fileSet = (FileSet) this.fileSets.get(i);
                File fromDir = fileSet.getDir(getProject());
                DirectoryScanner directoryScanner = fileSet
                        .getDirectoryScanner(getProject());
                String[] srcFiles = directoryScanner.getIncludedFiles();
                for (int j = 0; j < srcFiles.length; j++) {
                    File srcFile = new File(fromDir + File.separator
                            + srcFiles[j]);

                    File destDir = fromDir;
                    if (null != this.toDir) {
                        destDir = this.toDir;
                    }

                    File destFile = new File(destDir + File.separator
                            + srcFiles[j]);
                    compressFile(srcFile, destFile);
                }
            }
        } catch (IOException e) {
            log(e, LogLevel.ERR.getLevel());
        }
    }

    private void compressFile(File srcFile, File destFile) throws IOException {
        if ((srcFile != null) && (srcFile.exists()) && (destFile != null)) {
            destFile.getParentFile().mkdirs();
            if ("css".equalsIgnoreCase(type)
                    || (null == type && srcFile.getName().endsWith("css"))) {
                log("compressing: " + srcFile.getAbsolutePath());
                compressCss(srcFile, destFile);
            } else if ("js".equalsIgnoreCase(type)
                    || (null == type && srcFile.getName().endsWith("js"))) {
                log("compressing: " + srcFile.getAbsolutePath());
                compressJs(srcFile, destFile);
            } else {
                log("cannot determine the type of " + srcFile.getAbsolutePath());
            }
        }
    }

    private void compressCss(File srcFile, File destFile) throws IOException {
        Reader reader = null;
        Writer writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(srcFile), this.inputCharset));
            String source = readAsString(reader);

            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(destFile, false), this.outputCharset));
            CssCompressor compressor = new CssCompressor(new StringReader(
                    source));
            compressor.compress(writer, this.maxColumnWidth);
        } finally {
            closeQuietly(reader);
            closeQuietly(writer);
        }
    }

    private void compressJs(File srcFile, File destFile) throws IOException {
        Reader reader = null;
        Writer writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(srcFile), this.inputCharset));
            String source = readAsString(reader);

            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(destFile, false), this.outputCharset));
            JavaScriptCompressor compressor = new JavaScriptCompressor(
                    new StringReader(source), new AntErrorReporter(this));
            compressor.compress(writer, this.maxColumnWidth, this.munge,
                    this.warn, this.preserveAllSemiColons,
                    this.preserveStringLiterals);
        } finally {
            closeQuietly(reader);
            closeQuietly(writer);
        }
    }

    private String readAsString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i = -1;
        while (-1 != (i = reader.read())) {
            sb.append((char) i);
        }
        return sb.toString();
    };

    private void closeQuietly(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {}
        }
    }
}
