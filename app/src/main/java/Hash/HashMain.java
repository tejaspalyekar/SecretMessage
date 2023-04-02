package Hash;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.Algorithms.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;


public class HashMain extends Fragment {
    private Button Switch;
    private Button Hash_Buuton;
    private TextView Answer;
    private EditText Textfield_Text;
    private EditText Textfield_salt;
    private String message;
    private String salt;
    private View view;
    Button encrypt,decrypt;
    String image;
    ClipboardManager clipboardManager;
    ImageView imgView;
    EditText encimg;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.hash_main, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Switch = view.findViewById(R.id.Swtich);

        Answer = view.findViewById(R.id.Answer);
        Textfield_Text = view.findViewById(R.id.TextArea);

        encrypt = view.findViewById(R.id.enc_btn);
        decrypt = view.findViewById(R.id.dec_btn);
        encimg = view.findViewById(R.id.enc_txt);
        encimg.setEnabled(false);
        imgView = view.findViewById(R.id.imgView);
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                view.getContext().getSystemService(CLIPBOARD_SERVICE);



        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(ContextCompat.checkSelfPermission(view.getContext(),
               Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(view.,new String[] {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 100);
                }*/

               selectPhoto();
            }
        });

        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes = Base64.decode(encimg.getText().toString(),Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
                imgView.setImageBitmap(bitmap);
            }
        });
        return view;
    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"select picture"),100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selectPhoto();
        }
        else{
            Toast.makeText(view.getContext(),"Permsiion Denied!",Toast.LENGTH_SHORT).show();
        }
    }

   /* public void copyToClipboard (View view){
        String codes = encimg.getText().toString().trim();
        if(!codes.isEmpty()){
            ClipData temp = ClipData.newPlainText("text",codes);
            clipboardManager.setPrimaryClip(temp);
            Toast.makeText(view.getContext(),"Copied to Clipboard!!",Toast.LENGTH_SHORT).show();

        }
    }*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK && data!=null){
            Uri uri  = data.getData();
            Bitmap bitmap;
            ImageDecoder.Source source = null;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                source = ImageDecoder.createSource(view.getContext().getContentResolver(),uri);
            try{
                bitmap = ImageDecoder.decodeBitmap(source);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                byte[] bytes = stream.toByteArray();
                image = Base64.encodeToString(bytes,Base64.DEFAULT);
                encimg.setText(image);
                Toast.makeText(view.getContext(), "Image encrypted! click on Decrypt to restore!",Toast.LENGTH_SHORT).show();

            }
            catch(IOException e){
                e.printStackTrace();
            }
            }
        }
    }

    public void copyToClipboard(View view) {
        String copyText = String.valueOf(encimg.getText());
        if (encimg.length() == 0) {
            Toast.makeText(view.getContext(), "There is no message to copy", Toast.LENGTH_SHORT).show();
            return;
        }

        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                    view.getContext().getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(copyText);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                    view.getContext().getSystemService(CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData
                    .newPlainText("Your message :", copyText);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(view.getContext(),
                "Your message has be copied", Toast.LENGTH_SHORT).show();
    }

    public void reset(View view) {
        encimg.setText("");
        if(view!=null)
            Toast.makeText(view.getContext(), "All data has been deleted", Toast.LENGTH_SHORT).show();
    }




































    public void hash(View view) throws Exception {
        if (Textfield_Text.length() == 0) {
            Toast.makeText(view.getContext(), "Enter a message to Hash", Toast.LENGTH_SHORT).show();
            return;
        }
        message = String.valueOf(Textfield_Text.getText());
        salt = String.valueOf(Textfield_salt.getText());
        String Algorithm = String.valueOf(Switch.getText());
        String answer="";
        switch (Algorithm) {
            case "MD5":
                answer=hashText("MD5",salt,message);
                Answer.setText(answer);
                break;
            case "SHA-256":
                answer=hashText("SHA-256",salt,message);
                Answer.setText(answer);
                break;
            case "SHA-512":
                answer=hashText("SHA-512",salt,message);
                Answer.setText(answer);
                break;
        }
    }







    public void switchAlgho(View view) {
        reset(null);
        String SwitchValue = Switch.getText().toString();
        switch (SwitchValue) {
            case "MD5":
                Switch.setText("SHA-256");
                break;
            case "SHA-256":
                Switch.setText("SHA-512");
                break;
            case "SHA-512":
                Switch.setText("MD5");
                break;
        }
    }



    public  String hashText(String algo,String salt, String plainText)
            throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance(algo);
        m.reset();
        if (salt.length() != 0) {
            m.update(salt.getBytes());
        }
        m.update(plainText.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while(hashtext.length() < 32 ){
            hashtext = "0"+hashtext;
        }
        return hashtext;
    }



    private  byte[] getRandomSalt() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        //Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        //Create array for salt
        byte[] salt = new byte[16];
        //Get a random salt
        sr.nextBytes(salt);
        //return salt
        return salt;
    }


    }






















