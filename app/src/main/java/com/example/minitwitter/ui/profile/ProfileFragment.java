package com.example.minitwitter.ui.profile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.data.ProfileViewModel;
import com.example.minitwitter.R;
import com.example.minitwitter.retrofit.request.RequestUserProfile;
import com.example.minitwitter.retrofit.response.ResponseUserProfile;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    ImageView avatarIV;
    EditText usernameET, emailET, currentPassET, websiteET, descriptionET;
    Button changePassBtn, saveBtn;
    Boolean loadingData = true;
    PermissionListener permissionListener;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = new ViewModelProvider(getActivity()).get(ProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);

        avatarIV = v.findViewById(R.id.avatarIV);
        usernameET = v.findViewById(R.id.usernameET);
        emailET = v.findViewById(R.id.emailET);
        currentPassET = v.findViewById(R.id.currentPassET);
        changePassBtn = v.findViewById(R.id.changePassBtn);
        saveBtn = v.findViewById(R.id.saveBtn);
        descriptionET = v.findViewById(R.id.descriptionET);
        websiteET = v.findViewById(R.id.websiteET);

        //Eventos
        saveBtn.setOnClickListener(view -> {
            String username = usernameET.getText().toString();
            String email = emailET.getText().toString();
            String description = descriptionET.getText().toString();
            String website = websiteET.getText().toString();
            String password = currentPassET.getText().toString();

            if (username.isEmpty()) {
                usernameET.setError("El nombre de usuario es requerido");
            } else if (email.isEmpty()) {
                emailET.setError("El email es requerido");
            } else if (password.isEmpty()) {
                currentPassET.setError("La contraseña es requerida");
            } else {
                RequestUserProfile requestUserProfile = new RequestUserProfile(username, email, description, website, password);
                profileViewModel.updateProfile(requestUserProfile);
                Toast.makeText(getActivity(), "Enviando informacion al servidor", Toast.LENGTH_SHORT).show();
                saveBtn.setEnabled(false);
                loadingData = false;
            }
        });

        changePassBtn.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "Click on Change", Toast.LENGTH_SHORT).show();
        });

        avatarIV.setOnClickListener(v1 -> {
            checkPermissions();
        });

        profileViewModel.userProfileLiveData.observe(getActivity(), new Observer<ResponseUserProfile>() {
            @Override
            public void onChanged(ResponseUserProfile responseUserProfile) {
                usernameET.setText(responseUserProfile.getUsername());
                emailET.setText(responseUserProfile.getEmail());
                websiteET.setText(responseUserProfile.getWebsite());
                descriptionET.setText(responseUserProfile.getDescripcion());

                if (!responseUserProfile.getPhotoUrl().isEmpty()) {
                    Glide.with(getActivity())
                            .load(Constantes.API_MINITWITTER_FILES_URL + responseUserProfile.getPhotoUrl())
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .centerCrop()
                            .into(avatarIV);
                }

                if (!loadingData){
                    saveBtn.setEnabled(true);
                    Toast.makeText(getActivity(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                }
            }
        });

        profileViewModel.photoProfile.observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!s.isEmpty()) {
                    Glide.with(getActivity())
                            .load(Constantes.API_MINITWITTER_FILES_URL + s)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .centerCrop()
                            .into(avatarIV);
                }
            }
        });

        return v;
    }

    private void checkPermissions() {
        PermissionListener dialogOnDeniedPermissionListener = DialogOnDeniedPermissionListener.Builder.withContext(getActivity())
                .withTitle("Permisos")
                .withMessage("Los permisos solicitados son necesarios para poder seleccionar foto")
                .withButtonText("Aceptar")
                .withIcon(R.mipmap.ic_launcher)
                .build();

        permissionListener = new CompositePermissionListener((PermissionListener) getActivity(),dialogOnDeniedPermissionListener);
        Dexter.withContext(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(permissionListener)
                .check();
    }

}