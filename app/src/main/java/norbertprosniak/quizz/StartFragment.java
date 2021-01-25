package norbertprosniak.quizz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class StartFragment extends Fragment {

    // zdeklarowanie elementów layoutu
    private ProgressBar startProgress;
    private TextView startFeedbackText;

    // zdeklarowanie firebase authentication
    private FirebaseAuth firebaseAuth;
    private static final String START_TAG = "START LOG";

    // zdeklarowanie navController
    private NavController navController;

    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // inicjalizacja firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // inicjalizacja elementów layoutu
        startProgress = view.findViewById(R.id.start_progress);
        startFeedbackText = view.findViewById(R.id.start_feedback);

        // feedback dla użytkownika
        startFeedbackText.setText("Sprawdzanie konta użytkownika..");

        // inicjalizacja navController
        navController = Navigation.findNavController(view);

    }

    @Override
    public void onStart() {
        super.onStart();

        // sprawdzenie czy użytkownik jest zalogowany
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser == null){
            // jeśli nie -> utworzenie nowego użytkownika
            startFeedbackText.setText("Tworzenie konta..");
            firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        startFeedbackText.setText("Konto zostało stworzone!");
                        navController.navigate(R.id.action_startFragment_to_listFragment);
                    }else{
                        Log.d(START_TAG, "Start Log: " + task.getException());
                    }
                }
            });
        } else{
            // jeśli tak -> przejście do strony głównej
            startFeedbackText.setText("Zalogowano!");
            navController.navigate(R.id.action_startFragment_to_listFragment);
        }
    }
}