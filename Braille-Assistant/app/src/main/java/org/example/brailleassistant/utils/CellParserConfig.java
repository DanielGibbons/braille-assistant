package org.example.brailleassistant.utils;

import android.content.Context;
import android.util.Log;

import org.example.brailleassistant.R;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

public class CellParserConfig {

    private static final String LOG_TAG = CellParserConfig.class.getSimpleName();

    private Context mContext;

    public CellParserConfig(Context context) {
        mContext = context;
        extractDataFiles();

    }

    private void extractDataFiles() {Log.e(LOG_TAG, "Couldn't extract data files");
        File tablesDir = mContext.getDir("translator", MODE_PRIVATE);
        LibLouisWrapper.setTablesDir(tablesDir.getPath());
        ZipResourceExtractor extractor = new ZipResourceExtractor(mContext, R.raw.translationtables, tablesDir) {
            @Override
            protected void onPostExecute(Integer result) {
                synchronized (CellParserConfig.this) {
                    if (result == RESULT_OK) {
                        Log.d(LOG_TAG, "Extracted data files");
                    } else {
                        Log.e(LOG_TAG, "Couldn't extract data files");
                    }
                }
            }
        };
        extractor.execute();
    }

}
