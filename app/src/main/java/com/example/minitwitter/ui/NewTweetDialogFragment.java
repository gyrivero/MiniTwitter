package com.example.minitwitter.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.data.TweetViewModel;

public class NewTweetDialogFragment extends DialogFragment implements View.OnClickListener {
    ImageView closeDialogIV, avatarIV;
    Button twittearDialogBtn;
    EditText messageET;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.FullScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.new_tweet_full_dialog,container,false);

        closeDialogIV = view.findViewById(R.id.closeDialogIV);
        avatarIV = view.findViewById(R.id.avatarIV);
        twittearDialogBtn = view.findViewById(R.id.twittearDialogBtn);
        messageET = view.findViewById(R.id.messageET);

        //Eventos

        closeDialogIV.setOnClickListener(this);
        twittearDialogBtn.setOnClickListener(this);

        //Setear imagen del usuario de perfil
        String photoUrl = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_PHOTOURL);

        if (!photoUrl.isEmpty()) {
            Glide.with(getActivity())
                    .load(Constantes.API_MINITWITTER_FILES_URL + photoUrl)
                    .into(avatarIV);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String message = messageET.getText().toString();

        if (id == closeDialogIV.getId()) {
            if (message.isEmpty()) {
                getDialog().dismiss();
            } else {
                showDialogConfirm();
            }
        } else if (id == twittearDialogBtn.getId()) {
            if (message.isEmpty()) {
                messageET.setError(getString(R.string.new_tweet_empty_error));
            } else {
                TweetViewModel tweetViewModel = new ViewModelProvider(getActivity()).get(TweetViewModel.class);
                tweetViewModel.insertTweet(message);
                getDialog().dismiss();
            }
        }
    }

    private void showDialogConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.tweet_cancele_message)
                .setTitle(R.string.tweet_cancele_title)
                .setCancelable(false)
                .setPositiveButton(R.string.eliminar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getDialog().dismiss();

                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
