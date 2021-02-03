package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    //database name as DTB_NAME
    private static final String DTB_NAME="180632M";

    public PersistentTransactionDAO(Context context){
        super(context,DTB_NAME,null,1);
        this.onCreate(this.getReadableDatabase());
    }


    //implementing the abstract methods in SQLiteOpenHelper

    //this is called the first time a database is accessed.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create transaction table
        String CreateTableStmt="CREATE TABLE IF NOT EXISTS transactionLog (id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, accountNo TEXT, expenseType TEXT CHECK(expenseType IN ('EXPENSE','INCOME')),amount REAL)";
        sqLiteDatabase.execSQL(CreateTableStmt);
    }
    //this is called whenever the version number of DB is changed
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS transactionLog");
        onCreate(sqLiteDatabase);
    }


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy");
        String MyDate = simpleDate.format(date);
        cv.put("date",String.valueOf(MyDate));
        cv.put("accountNo",String.valueOf(accountNo));
        cv.put("expenseType",String.valueOf(expenseType));
        cv.put("amount",amount);

        sqLiteDatabase.insert("transactionLog",null,cv);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        ArrayList<Transaction> transactionsList= new ArrayList<>();

        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String queryString="SELECT * FROM transactionLog";
        Cursor cursor =sqLiteDatabase.rawQuery(queryString,null);
        if (cursor.moveToFirst()){
            do {
                Transaction transaction = new Transaction();

                String[] date = cursor.getString(cursor.getColumnIndex("date")).split("-");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
                Date transactionDate = calendar.getTime();
                transaction.setDate(transactionDate);

                transaction.setAccountNo(cursor.getString(cursor.getColumnIndex("accountNo")));

                String type = cursor.getString(cursor.getColumnIndex("expenseType"));
                ExpenseType expenseType;
                if (type.equals("EXPENSE")){
                    expenseType = ExpenseType.EXPENSE;
                }else{
                    expenseType = ExpenseType.INCOME;
                }
                transaction.setExpenseType(expenseType);

                transaction.setAmount(cursor.getFloat(cursor.getColumnIndex("amount")));

                transactionsList.add(transaction);
            }while (cursor.moveToNext());
        }
        return transactionsList;

    }




    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        ArrayList<Transaction> paginatedTransactionsList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String queryString="SELECT * FROM transactionLog LIMIT "+limit+"";
        Cursor cursor =sqLiteDatabase.rawQuery(queryString,null);

        if (cursor.moveToFirst()){
            do {
                Transaction transaction = new Transaction();

                String[] date = cursor.getString(cursor.getColumnIndex("date")).split("-");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
                Date transactionDate = calendar.getTime();
                transaction.setDate(transactionDate);

                transaction.setAccountNo(cursor.getString(cursor.getColumnIndex("accountNo")));

                String type = cursor.getString(cursor.getColumnIndex("expenseType"));
                ExpenseType expenseType;
                if (type.equals("EXPENSE")){
                    expenseType = ExpenseType.EXPENSE;
                }else{
                    expenseType = ExpenseType.INCOME;
                }
                transaction.setExpenseType(expenseType);

                transaction.setAmount(cursor.getFloat(cursor.getColumnIndex("amount")));

                paginatedTransactionsList.add(transaction);
            }while (cursor.moveToNext());
        }
        return paginatedTransactionsList;

    }


}
