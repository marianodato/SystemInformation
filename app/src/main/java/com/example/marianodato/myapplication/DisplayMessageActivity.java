package com.example.marianodato.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;


public class DisplayMessageActivity extends Activity {

    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_message);

        // Get the message from the intent
        Intent intent = getIntent();
        Bundle bundle=getIntent().getExtras();

        textView=(TextView) findViewById(R.id.text);
        textView.setMovementMethod(new ScrollingMovementMethod());

        // Create the text view
        //TextView textView = new TextView(this);
        textView.setTextSize(12);
        int message=bundle.getInt("id");

        if(message==R.id.process)
                {    setTitle("Process Info");
                     textView.setGravity(0);
                     textView.setText(consola("ps"));
                }
        else if (message==R.id.memory)
                { textView.setTextSize(10);
                     setTitle("Memory Info");
                     textView.setText(consola("cat /proc/meminfo"));}
        else if (message==R.id.cpu)
                {

                     setTitle("CPU Info");
                     textView.setText(consola("cat /proc/cpuinfo"));}
        else if (message==R.id.version)
                {    setTitle("Kernel Info");
                     textView.setTextSize(17);
                     textView.setText(consola("cat /proc/version"));}
        else if (message==R.id.authors)
        {    setTitle("Developers");
            textView.setTextSize(17);
            textView.setGravity(Gravity.CENTER);
            String tex="GRUPO CHMJ\nOrtiz de Zarate, Juan Cruz\nDato, Mariano Nicolas\nLa Valle, Chiara";
            textView.setText(tex);}
        else if (message==R.id.contacts)
        {
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(17);
            setTitle("Contacts");
            textView.setText(displayContacts());

        }else
                {    setTitle("Date Info");
                     textView.setTextSize(17);
                     textView.setText(consola("date"));}

        // Set the text view as the activity layout
       // setContentView(textView);

        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public String consola(String consola) {
        String text="";
        // LLAMAMOS LA VARIABLE DE ENTORNO WINDOWS Y EL PROGRAMA Q GESTIONA
        // LOS PROCESOS
        // Ejecutamos el comando
        Process proceso;
        try{
            proceso=Runtime.getRuntime().exec(consola);
            //OBTENEMOS EL BUFFER DE SALIDA
            BufferedReader entrada = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            String tmp;
            while((tmp=entrada.readLine())!=null){
                text+=tmp+"\n";

            }
            entrada.close();

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return text;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String displayContacts() {

        ContentResolver cr = getContentResolver();
        String name="";
        String phone="";
        String pepe="";
        ArrayList<String> arrayList = new ArrayList<String>();


        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))+": ";
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                         phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                         pepe=name+phone+'\n';
                        arrayList.add(pepe);
                        //Toast.makeText(NativeContentProvider.this, "Name: " + name + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                    }
                    pCur.close();
                }


            }
        }
        Collections.sort(arrayList);
        String hola="NOMBRE: NUMERO\n";
        for(int i=0; i<arrayList.size();i++)
            hola+=arrayList.get(i);
        return hola;


    }
}
