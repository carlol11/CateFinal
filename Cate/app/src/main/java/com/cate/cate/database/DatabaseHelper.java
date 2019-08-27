package com.cate.cate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.cate.cate.models.models.Decision;
import com.cate.cate.models.models.Memory;
import com.cate.cate.models.models.Patient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;

/*
* The original code was created by Mike Dayupay.
* For the purpose of integration, the code was modified.
* This class serves as the helper class and main
* connection to the database. It initializes, opens, and
* closes the database.
*
* @author Mike Dayupay
* @author Mary Grace Malana
*/

/*
    This code is from Geebee and reused and redesigned for Cate

    -Edward Valdez
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * Directory path of the database
     */
    private static String DB_PATH = "";

    /**
     * Name of the database
     */
    private static String DB_NAME = "get_better";

    /**
     * Version number of the database
     */
    private static int DB_VERSION = 2;

    /**
     * Used to identify the source of a log message
     */
    private static String TAG = "DatabaseHelper";

    /**
     * Context of the database
     */
    private final Context myContext;

    /**
     * Database of the application
     */
    private SQLiteDatabase getBetterDatabase;

    /**
     * Constructor. Initializes class attributes.
     * @param context used by the database.
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        if(Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.myContext = context;
    }

    /**
     * Creates the database
     * @throws IOException if database creation problem occurs.
     */
    public void createDatabase() throws IOException {

        boolean databaseExists = checkDatabase();

        if(!databaseExists) {
            this.getReadableDatabase();
            this.close();

            try {
                copyDatabase();
                Log.d(TAG, "createDatabase database created");
            }catch (IOException ioe) {
                throw new Error("Error creating database");
            }
        }
    }

    /**
     * Checks whether an application database already exist.
     * @return true if application database exist, else false.
     */
    private boolean checkDatabase() {

        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    /**
     * Copies the database from the assets folder of
     * the application.
     * @throws IOException if a InputStream/OutputStream problem
     *         is encountered.
     */
    private void copyDatabase() throws IOException {

        InputStream mInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];

        int mLength;

        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }

        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    /**
     * Opens the database. A readable or writeable database
     * can now be retrieved.
     * @throws SQLException if database does not exist
     *          or encounters other opening database problem.
     */
    public void openDatabase() throws SQLException {

        String myPath = DB_PATH + DB_NAME;
        getBetterDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    /**
     * Closes the database. The database is no longer available for
     * read or write. The database must be opened again in order
     * to have access again.
     */
    @Override
    public synchronized void close() {

        if(getBetterDatabase != null) {
            getBetterDatabase.close();
        }

        super.close();
    }

    /**
     * @see SQLiteOpenHelper#onCreate(SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    /**
     * @see SQLiteOpenHelper#onUpgrade(SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 2) {
            db.execSQL("ALTER TABLE " + Patient.TABLE_NAME + " ADD COLUMN score INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + Patient.TABLE_NAME + " ADD COLUMN hobby TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + Patient.TABLE_NAME + " ADD COLUMN new_user INTEGER DEFAULT 1");
            db.execSQL("ALTER TABLE " + Patient.TABLE_NAME + " ADD COLUMN previous_emotion TEXT DEFAULT 'HAPPY'");
        }

        if (newVersion == 2) {
            db.execSQL("ALTER TABLE " + Patient.TABLE_NAME + " ADD COLUMN " + Patient.C_PHONE + " TEXT DEFAULT ''");
        }

        if (newVersion == 2) {
            db.execSQL("CREATE TABLE " + Memory.TABLE_NAME + " (" +
                    Memory.C_ID + " INTEGER primary key autoincrement," +
                    Memory.C_PATIENT + " INTEGER not null," +
                    Memory.C_VARIABLE + " TEXT DEFAULT ''," +
                    Memory.C_VALUE + " TEXT DEFAULT ''" +
                    ")");
        }

        if (newVersion == 2){
            try {
                db.execSQL("CREATE TABLE " + Decision.TABLE_NAME + " (" +
                        Decision.C_ID + " INTEGER primary key, " +
                        Decision.C_CHOICES + " TEXT not null, " +
                        Decision.C_LABEL + " TEXT, " +
                        Decision.C_QUESTION + " TEXT, " +
                        Decision.C_INIT + " INTEGER DEFAULT 0," +
                        Decision.C_SPEECH + " TEXT, " +
                        Decision.C_EMOTION + " TEXT, " +
                        Decision.C_EDITTEXT + " INTEGER DEFAULT 0, " +
                        Decision.C_VARIABLE + " TEXT)");

                InputStream mInput = myContext.getAssets().open("Decisions.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(mInput));
                String line = "";
                String tableName = Decision.TABLE_NAME;
                String columns = "id, choices, label, question, init, speech, emotion, edittext, variable";
                String str1 = "INSERT INTO " + tableName + " (" + columns + ") values(";
                String str2 = ");";

                db.beginTransaction();
                while ((line = reader.readLine()) != null) {
                    StringBuilder sb = new StringBuilder(str1);
                    String[] str = line.split(";(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    for (int i = 0; i < str.length; i++)
                        if (i < str.length - 1)
                            sb.append(str[i]).append(",");
                        else
                            sb.append(str[i]);
                    sb.append(str2);
                    db.execSQL(sb.toString());
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
