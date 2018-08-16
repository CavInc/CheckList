package tk.cavinc.checklist.utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by cav on 16.08.18.
 */

public class CustomFileNameFilter implements FilenameFilter {
    private String ext;

    public CustomFileNameFilter(String ext) {
        this.ext = ext.toLowerCase();
    }

    @Override
    public boolean accept(File file, String name) {
        return name.toLowerCase().endsWith(ext);
    }
}
