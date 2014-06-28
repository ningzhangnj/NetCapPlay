package com.network.protocols;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CommandsUtil.
 *
 * @author ningzhangnj
 */
public class CommandsUtil {
    public static void saveRecords(List<String> cmds) {
        File file = null;
        OutputStream out = null;
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CAP txt file", "captxt");
        fileChooser.setFileFilter(filter);
        int sel = fileChooser.showSaveDialog(null);

        if (sel == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }

        if (file != null) {
            try {
                out = new FileOutputStream(file, false);
                for (String cmd:cmds) {
                    out.write(cmd.getBytes());
                }
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }
    }

    public static List<String> loadRecords() {
        File file = null;
        BufferedReader in = null;
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CAP txt file", "captxt");
        fileChooser.setFileFilter(filter);
        List<String> cachedCmds =  new ArrayList<String>();
        int sel = fileChooser.showOpenDialog(null);

        if (sel == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }

        if (file != null) {
            try {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                while ((line = in.readLine()) != null) {
                    cachedCmds.add(line);
                }
                return  cachedCmds;
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }
        return  null;
    }
}
