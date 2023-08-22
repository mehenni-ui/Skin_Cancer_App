package mohamed.mehenni.skincancer;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

import mohamed.mehenni.skincancer.ml.ModelTF;

public class MainActivity extends AppCompatActivity {
    private ImageView image;
    private Button select, predict;
    private TextView tv, per;
    private Bitmap bitmap;



    String [] info = {
            "Le carcinome basocellulaire est le cancer de la peau le plus fréquent. Il se développe dans certaines cellules de la couche supérieure de la peau (épiderme). En général, une petite papule brillante apparaît sur la peau et grossit lentement.",
            "Le mélanome est un cancer de la peau qui naît des cellules qui produisent des pigments de la peau. Les mélanomes peuvent se développer sur une peau normale ou dans des grains de beauté",
            "Le carcinome épidermoïde est une tumeur maligne qui naît dans les cellules squameuses de la peau.\n" +
                    "Les symptômes du Carcinome épidermoïde :\n Le carcinome épidermoïde est caractérisé par son apparence épaisse mais son apparence est très variable et un médecin peut soupçonner sa présence à partir de toute lésion qui ne guérit pas sur la peau exposée au soleil",
            "Le sarcome de Kaposi est un cancer de la peau provoquant de multiples taches planes, de couleur rose, rouge ou pourpre, ou des papules sur la peau. Il est dû à une infection par l’herpès virus humain de type 8.\n" +
                    "Les symptômes du Sarcome de Kaposi :\n" +
                    "Le sarcome de Kaposi apparaît généralement sous forme de taches de couleur pourpre, rose ou rouge ou de papules sur la peau. Le cancer peut s’étendre sur plusieurs centimètres sous forme de zone plate ou légèrement surélevée, bleu-violet à noire. Un gonflement peut être présent.\n",
            "•\tCarcinome à cellules de Merkel : Le carcinome à cellules de Merkel est un cancer de la peau rare, de propagation rapide, qui touche principalement les personnes âgées.\n" +
                    "Les symptômes du Carcinome à cellules de Merkel :\n" +
                    "La tumeur cancéreuse prend généralement la forme d’une papule ferme, brillante, de couleur chair ou bleu-rouge. Les tumeurs cancéreuses tendent à évoluer rapidement sans provoquer de douleur ni de sensibilité. Même si le carcinome à cellules de Merkel peut toucher toutes les parties de la peau, il est plus fréquent dans les zones fortement exposées au soleil.\n",
            "•\tLe Histiocytofibrome : est une tumeur bénigne sous-cutanée (dermohypodermique) fréquente chez l'adulte, L’HCF est une tumeur majoritairement bénigne, mais certaines formes morphologiques particulières peuvent présenter des récidives locales, donner des métastases ganglionnaires et viscérales, et provoquer exceptionnellement des décès",
            "•\tLe granulome pyogénique : est une croissance bénigne courante qui apparaît souvent comme une bosse saignante à croissance rapide sur la peau ou à l'intérieur de la bouche. Il est composé de vaisseaux sanguins et peut survenir au site d'une blessure mineure.",
                    "A : Asymétrie\n" +
                    "la moitié du naevus ne colle pas avec l'autre moitié.\n" +

                    "\nB :  Bords irréguliers\n" +
                    "les bords peuvent être encochés, mal délimités.\n" +

                    "\nC :  Couleur inhomogène\n" +
                    "variant d'une zone à l'autre de la lésion\n" +

                    "\nD :  Diamètre\n" +
                    "qui est souvent supérieur à 6 mm, la taille de section d'un crayon.\n" +

                    "\nE :  Évolution\n" +
                    "l’aspect de la lésion dans sa taille surtout, sa forme, ou sa couleur a changé."

};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_info,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
        int id = item.getItemId();
        switch (id) {
            case R.id.one:
                b.setMessage(info[0]);
                break;
            case R.id.two:
                b.setMessage(info[1]);
                break;
            case R.id.three:
                b.setMessage(info[2]);
                break;
            case R.id.four:
                b.setMessage(info[3]);
                break;
            case R.id.five:
                b.setMessage(info[4]);
                break;
            case R.id.six:
                b.setMessage(info[5]);
                break;
            case R.id.seven:
                b.setMessage(info[6]);
                break;
            case R.id.eight:
                b.setMessage(info[7]);
                break;

        }

        b.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "useful information ", Toast.LENGTH_SHORT).show();
            }
        }).show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.imageView);
        per = findViewById(R.id.percentage);
        select = findViewById(R.id.select);
        predict = findViewById(R.id.predict);

        tv = findViewById(R.id.textView);

        select.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent it = new Intent(Intent.ACTION_GET_CONTENT);
                it.setType("image/*");
                startActivityForResult(it,100);

            }
        });


        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = Bitmap.createScaledBitmap(bitmap,28,28, true);

                try {
                    ModelTF model = ModelTF.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 28, 28, 3}, DataType.FLOAT32);

                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    tensorImage.load(bitmap);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();

                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    ModelTF.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();


                    float [] conf = outputFeature0.getFloatArray();

                    int max = 0;
                    float maxConf = 0;

                    for (int i=0; i<conf.length; i++){
                        if (conf[i]> maxConf){
                            maxConf = conf[i];
                            max = i;
                        }
                    }

                    String [] classes={"kératose actinique et carcinome intraépithélial(Cancereuses)",
                            "carcinome basocellulaire (cancereuses)",
                            "lésions bénignes de type kératose (non cancéreuses)",
                            "dermatofibrome (non cancéreuses)",
                            "naevus mélanocytaires (non cancéreuses)",
                            "granulomes pyogéniques et hémorragies (Peut conduire au cancer)",
                            "mélanome (cancéreuses)"};

                    tv.setText(classes[max]);
                    per.setText(String.format("%.2f", conf[max]*100)  + "%");



                    // Releases model resources if no longer used.
                    model.close();

                    //tv.setText(outputFeature0.getFloatArray()[0] + "\n" + outputFeature0.getFloatArray()[1]+ "\n" +outputFeature0.getFloatArray()[2] + "\n" + outputFeature0.getFloatArray()[3]+ "\n" +outputFeature0.getFloatArray()[4] + "\n" + outputFeature0.getFloatArray()[5]+ "\n" +outputFeature0.getFloatArray()[6]);
                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }
        });







    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100){
            image.setImageURI(data.getData());

            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}