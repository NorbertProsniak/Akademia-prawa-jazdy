package norbertprosniak.quizz;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class DetailsFragment extends Fragment implements View.OnClickListener {


    private NavController navController;
    private QuizListViewModel quizListViewModel;
    private int position;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    private ImageView detailsImage;
    private TextView detailsTitle;
    private TextView detailsDesc;
    private TextView detailsQuestions;
    private TextView detailsScore;

    private Button detailsStartBtn;
    private String quizId;
    private long totalQuestions = 0;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        position = DetailsFragmentArgs.fromBundle(getArguments()).getPosition();

        //inicializacja elementów
        detailsImage = view.findViewById(R.id.details_image);
        detailsTitle = view.findViewById(R.id.details_title);
        detailsDesc = view.findViewById(R.id.details_desc);
        detailsQuestions = view.findViewById(R.id.details_quest_text);
        detailsScore = view.findViewById(R.id.details_score_text);

        detailsStartBtn = view.findViewById(R.id.details_start_btn);
        detailsStartBtn.setOnClickListener(this);

        //Zaladuj poprzednie wyniki
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //inicjalizacja quizListViewModel
        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);
        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {

                Glide.with(getContext())
                        .load(quizListModels.get(position).getImage())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_image)
                        .into(detailsImage);

                detailsTitle.setText(quizListModels.get(position).getName());
                detailsDesc.setText(quizListModels.get(position).getDesc());
                detailsQuestions.setText(quizListModels.get(position).getQuestions() + "");

                // Przypisanie wartości do zmiennej quizId
                quizId = quizListModels.get(position).getQuiz_id();
                totalQuestions  = quizListModels.get(position).getQuestions();

                loadResultsData();


            }
        });

    }


    private void loadResultsData() {
        firebaseFirestore.collection("QuizList")
                .document(quizId).collection("Results")
                .document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null && document.exists()){
                        //Get Result
                        Long correct = document.getLong("correct");
                        Long wrong = document.getLong("wrong");
                        Long missed = document.getLong("unanswered");

                        Long total = correct + wrong + missed;
                        Long percent = (correct*100)/total;

                        detailsScore.setText(percent + "%");
                    } else {
                        //N/A
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {       // stąd przejdziemy z detailsFragment do quizFregment
                switch (v.getId()) {
                    case R.id.details_start_btn:

                        DetailsFragmentDirections.ActionDetailsFragmentToQuizzFragment action = DetailsFragmentDirections.actionDetailsFragmentToQuizzFragment();
                        action.setPosition(position);
                        action.setQuizid(quizId);
                        action.setTotalQuestions(totalQuestions);
                        navController.navigate(action);
                break;

        }
    }
}
