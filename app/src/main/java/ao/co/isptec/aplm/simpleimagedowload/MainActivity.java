package ao.co.isptec.aplm.simpleimagedowload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements Handler.Callback {

    public static final String KEY_HANDLER_MSG = "status";
    private static final String IMAGE_SOURCE = "https://android.com/images/froyo.png";
    private Button btnDownloadFile;
    private Button btnDownloadFileAsync;
    private TextView statusTextView;
    private ImageView imageView;
    private Handler handler;

    private Runnable imageDownloader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDownloadFile = findViewById(R.id.btnDownloadFile);
        btnDownloadFileAsync = findViewById(R.id.btnDownloadFileAsync);
        imageView = findViewById(R.id.image_view);
        statusTextView = findViewById(R.id.status);

        // Inicializando o Handler e associando ao Callback
        handler = new Handler(Looper.getMainLooper(), this);

        // Configuração do botão de download
        btnDownloadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(imageDownloader, "Download thread").start();
                // Enviando mensagem ao Handler para mostrar que o download foi iniciado
                Message message = handler.obtainMessage();
                Bundle data = new Bundle();
                data.putString(KEY_HANDLER_MSG, getString(R.string.download_started));
                message.setData(data);
                handler.sendMessage(message);
            }
        });

        // Inicializando o Runnable para o download da imagem
        imageDownloader = new Runnable() {
            @Override
            public void run() {
                downloadImage(IMAGE_SOURCE);
            }
        };

        // Implementar para download assíncrono, se necessário
        btnDownloadFileAsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pode ser implementado usando AsyncTask ou ExecutorService, por exemplo
            }
        });
    }

    // Método de download da imagem
    private void downloadImage(String urlStr) {
        try {
            URL imageUrl = new URL(urlStr);
            Bitmap image = BitmapFactory.decodeStream(imageUrl.openStream());

            if (image != null) {
                Log.i("DL", getString(R.string.download_success));
                // Enviando mensagem ao Handler informando que o download foi concluído com sucesso
                Message message = handler.obtainMessage();
                Bundle data = new Bundle();
                data.putString(KEY_HANDLER_MSG, getString(R.string.download_success));
                message.setData(data);
                handler.sendMessage(message);

                // Atualizando a imagem na UI thread
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(image);
                    }
                });
            } else {
                Log.i("DL", getString(R.string.download_failed_stream));
                // Enviando mensagem ao Handler para falha no stream
                Message message = handler.obtainMessage();
                Bundle data = new Bundle();
                data.putString(KEY_HANDLER_MSG, getString(R.string.download_failed_stream));
                message.setData(data);
                handler.sendMessage(message);
            }
        } catch (Exception e) {
            Log.i("DL", getString(R.string.download_failed));
            e.printStackTrace();
            // Enviando mensagem ao Handler para falha no download
            Message message = handler.obtainMessage();
            Bundle data = new Bundle();
            data.putString(KEY_HANDLER_MSG, getString(R.string.download_failed));
            message.setData(data);
            handler.sendMessage(message);
        }
    }

    // Implementação do método handleMessage para lidar com as mensagens enviadas ao Handler
    @Override
    public boolean handleMessage(Message msg) {
        String text = msg.getData().getString("status");
        TextView statusText = (TextView) findViewById(R.id.status);
        statusText.setText(text);
        return true;
    }
}
