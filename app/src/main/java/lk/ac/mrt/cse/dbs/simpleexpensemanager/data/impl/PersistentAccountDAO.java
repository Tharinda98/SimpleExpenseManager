package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    //database name as DTB_NAME
    private static final String DTB_NAME="180632M";

    public PersistentAccountDAO(Context context){
        super(context,DTB_NAME,null,1);
        this.onCreate(this.getReadableDatabase());
    }

//implementing the abstract methods in SQLiteOpenHelper
    //this is called the first time a database is accessed.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create account table
        String CreateTableStmt="CREATE TABLE IF NOT EXISTS account (accountNo TEXT PRIMARY KEY, bankName TEXT, accountHolderName TEXT, balance REAL)";
        sqLiteDatabase.execSQL(CreateTableStmt);
    }
    //this is called whenever the version number of DB is changed
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS account");
        onCreate(sqLiteDatabase);
    }



    //implement AccountDAO
    public List<String> getAccountNumbersList(){
        ArrayList<String> accountNumbersList= new ArrayList<>();

        String queryString = "SELECT accountNo FROM account";
        SQLiteDatabase sqLiteDatabase =this.getReadableDatabase();
        Cursor cursor= sqLiteDatabase.rawQuery(queryString,null);

        if (cursor.moveToFirst()){
            do {
                accountNumbersList.add(cursor.getString((cursor.getColumnIndex("accountNo"))));
            }
            while (cursor.moveToNext());
        }
        return accountNumbersList;
    }



    public List<Account> getAccountsList(){
        ArrayList<Account> accountsList=new ArrayList<>();

        String queryString="SELECT * FROM account";
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(queryString,null);

        if (cursor.moveToFirst()){
            do {
                Account account=new Account();
                account.setAccountNo(cursor.getString(cursor.getColumnIndex("accountNo")));
                account.setBankName(cursor.getString(cursor.getColumnIndex("bankName")));
                account.setAccountHolderName(cursor.getString(cursor.getColumnIndex("accountHolderName")));
                account.setBalance(cursor.getFloat(cursor.getColumnIndex("balance")));
                accountsList.add(account);
            }while (cursor.moveToNext());
        }
        return accountsList;
    }


    public Account getAccount(String accountNo) throws InvalidAccountException{
        String queryString="SELECT * FROM account WHERE accountNo="+accountNo+"";
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(queryString,null);
        if (cursor.moveToFirst()){
            Account account=new Account();
            account.setAccountNo(cursor.getString(cursor.getColumnIndex("accountNo")));
            account.setBankName(cursor.getString(cursor.getColumnIndex("bankName")));
            account.setAccountHolderName(cursor.getString(cursor.getColumnIndex("accountHolderName")));
            account.setBalance(cursor.getFloat(cursor.getColumnIndex("balance")));
            return account;
        }
        else{
            String error="Account number: "+accountNo+" invalid";
            throw new InvalidAccountException(error);
        }

    }


    public void addAccount(Account account){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues cv =new ContentValues();
        cv.put("accountNo",account.getAccountNo());
        cv.put("bankName",account.getBankName());
        cv.put("accountHolderName",account.getAccountHolderName());
        cv.put("balance",account.getBalance());

        sqLiteDatabase.insert("account",null,cv);

    }


    public void removeAccount(String accountNo) throws InvalidAccountException{
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.delete("account","accountNo=?",new String[]{accountNo});
    }


    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException{
        Account account = this.getAccount(accountNo);
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues cv =new ContentValues();
        cv.put("balance",account.getBalance());

        sqLiteDatabase.update("account",cv,"accountNo=?",new String[]{accountNo});
    }

}
