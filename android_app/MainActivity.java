package com.citlife.yourassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView chatsRV;
    private EditText userMsgEdt;
    private FloatingActionButton sendMsgFAB;
    private final String BOT_KEY = "bot";
    private final String BOT_KEY2 = "bot2";
    private final String USER_KEY = "user";

    private ArrayList<ChatsModal>chatsModalArrayList;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatsRV =  findViewById(R.id.idRVChats);
        userMsgEdt = findViewById(R.id.idEdtMsg);
        sendMsgFAB = findViewById(R.id.FABSend);

        chatsModalArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatsModalArrayList,this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatsRV.setLayoutManager(manager);
        chatsRV.setAdapter(chatAdapter);

        sendMsgFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userMsgEdt.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter your message",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userMsgEdt.getText().toString().equalsIgnoreCase("hod") || userMsgEdt.getText().toString().equalsIgnoreCase("department") || userMsgEdt.getText().toString().equalsIgnoreCase("courses")){
                    getResponseSpecific(userMsgEdt.getText().toString().toLowerCase());
                } else {
                    getResponse(userMsgEdt.getText().toString());
                }

                userMsgEdt.setText("");
                chatsRV.scrollToPosition(chatsModalArrayList.size() - 1);
            }
        });

        chatAdapter.setOnItemClickListener(new ChatAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                String message = chatsModalArrayList.get(position).getMessage();
                getResponse(message);

            }
        });

    }

    private void getResponse(String message){
        chatsModalArrayList.add(new ChatsModal(message,USER_KEY));
        chatAdapter.notifyDataSetChanged();
//        String url = "https://yourassistant.azurewebsites.net/interact?query=" + message;
        String url = "interact?query=" + message;
        String BASE_URL = "https://yourassistantmax.azurewebsites.net/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MsgModal> call = retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MsgModal>() {
            @Override
            public void onResponse(Call<MsgModal> call, Response<MsgModal> response) {
                if(response.isSuccessful()){
                    MsgModal modal = response.body();
                    chatsModalArrayList.add(new ChatsModal(modal.getResponse(),BOT_KEY));
                    chatAdapter.notifyDataSetChanged();
                    chatsRV.scrollToPosition(chatsModalArrayList.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<MsgModal> call, Throwable t) {
                chatsModalArrayList.add(new ChatsModal("Contact Helpdesk For further queries at 91-3661-277279" , BOT_KEY));
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getResponseSpecific(String message){
        chatsModalArrayList.add(new ChatsModal(message,USER_KEY));
        chatAdapter.notifyDataSetChanged();
//        String url = "https://yourassistant.azurewebsites.net/interact?query=" + message;
        String url = "interact?query=" + message;
        String BASE_URL = "https://yourassistantmax.azurewebsites.net/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MsgModal2> call = retrofitAPI.getMessage2(url);
        call.enqueue(new Callback<MsgModal2>() {
            @Override
            public void onResponse(Call<MsgModal2> call, Response<MsgModal2> response) {
                if(response.isSuccessful()){
                    MsgModal2 modal = response.body();
                    chatsModalArrayList.add(new ChatsModal("",BOT_KEY));

                    for (int i=0; i<modal.getResponse().length; i++) {
                        chatsModalArrayList.add(new ChatsModal(modal.getResponse()[i], BOT_KEY2));
                    }

                    chatAdapter.notifyDataSetChanged();
                    chatsRV.scrollToPosition(chatsModalArrayList.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<MsgModal2> call, Throwable t) {
                chatsModalArrayList.add(new ChatsModal("Contact Helpdesk For further queries at 91-3661-277279" , BOT_KEY));
                chatAdapter.notifyDataSetChanged();
            }
        });
    }


    /*@Override
    public void onItemClick(int position, View v) {

        String message = chatsModalArrayList.get(position).getMessage();
        getResponse(message);

    }*/
}