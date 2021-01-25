package norbertprosniak.quizz;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class QuizzFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "QUIZZ_FRAGMENT_LOG";
    private NavController navController;



    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String currentUserId;


    private String quizId;

    //UI
    private TextView quizTitle;
    private Button optionOneBtn;
    private Button optionTwoBtn;
    private Button optionThreeBtn;
    private Button nextBtn;
    private TextView questionFeedback;
    private TextView questionText;
    private TextView questionTime;
    private ProgressBar questionProgress;
    private TextView questionNumber;



    private List<QuestionsModel> allQuestionsList = new ArrayList<>();
    private List<QuestionsModel> questionsToAnswer = new ArrayList<>();
    private Long totalQuestionsToAnswer = 0L;  // na ile odpowiedzi mamy odpowiedziec
    private CountDownTimer countDownTimer;

    private boolean canAnswer = false;
    private int currentQuestion = 0;

    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    private int notAnswered = 0;


    public QuizzFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_quizz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        navController = Navigation.findNavController(view);

        firebaseAuth = FirebaseAuth.getInstance();


        if(firebaseAuth.getCurrentUser() != null){
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        } else {
            //wraca do strony głownej
        }

        firebaseFirestore = FirebaseFirestore.getInstance();

        optionOneBtn = view.findViewById(R.id.quiz_option_1);
        optionTwoBtn = view.findViewById(R.id.quiz_option_2);
        optionThreeBtn = view.findViewById(R.id.quiz_option_3);
        nextBtn = view.findViewById(R.id.quiz_next_btn);
        questionFeedback = view.findViewById(R.id.quiz_weryf);
        questionText = view.findViewById(R.id.quiz_quest);
        questionTime = view.findViewById(R.id.quiz_time);
        questionProgress = view.findViewById(R.id.quiz_progress);
        questionNumber = view.findViewById(R.id.quiz_quest_number);


        quizId = QuizzFragmentArgs.fromBundle(getArguments()).getQuizid();

        totalQuestionsToAnswer = QuizzFragmentArgs.fromBundle(getArguments()).getTotalQuestions();


        firebaseFirestore.collection("QuizList")
                .document(quizId).collection("Questions")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    allQuestionsList = task.getResult().toObjects(QuestionsModel.class); // ktore wybierzemy z calej listy
                    pickQuestions();
                    loadUI();
                } else {
                    quizTitle.setText("Error : " + task.getException().getMessage());
                }
            }
        });

        optionOneBtn.setOnClickListener(this);
        optionTwoBtn.setOnClickListener(this);
        optionThreeBtn.setOnClickListener(this);

        nextBtn.setOnClickListener(this);
    }

    private void loadUI() {

                questionText.setText("Load First Question");

                enableOptions();

                loadQuestion(1);
            }

            private void loadImage(){



            }

    private void loadQuestion(int questNum) {

        questionNumber.setText(questNum + "");

        questionText.setText(questionsToAnswer.get(questNum-1).getQuestion());

        optionOneBtn.setText(questionsToAnswer.get(questNum-1).getOption_a());
        optionTwoBtn.setText(questionsToAnswer.get(questNum-1).getOption_b());
        optionThreeBtn.setText(questionsToAnswer.get(questNum-1).getOption_c());

        canAnswer = true;
        currentQuestion = questNum;


        startTimer(questNum);
    }

    private void startTimer(int questionNumber) {

        //Text timera
        final Long timeToAnswer = questionsToAnswer.get(questionNumber-1).getTimer();
        questionTime.setText(timeToAnswer.toString());

        questionProgress.setVisibility(View.VISIBLE);

        //Start
        countDownTimer = new CountDownTimer(timeToAnswer*1000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                questionTime.setText(millisUntilFinished/1000 + "");

                //Progres w procentach
                Long percent = millisUntilFinished/(timeToAnswer*10);
                questionProgress.setProgress(percent.intValue());
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFinish() {
                //skonczony czas, nie mozna odpowidziec
                canAnswer = false;

                questionFeedback.setText("Czas się skończył!\n \n Prawidłowa odpowiedź : " + questionsToAnswer.get(currentQuestion-1).getAnswer());
                questionFeedback.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                notAnswered++;
                showNextBtn();
            }
        };

        countDownTimer.start();
    }

    private void enableOptions() {
        optionOneBtn.setVisibility(View.VISIBLE);
        optionTwoBtn.setVisibility(View.VISIBLE);
        optionThreeBtn.setVisibility(View.VISIBLE);


        optionOneBtn.setEnabled(true);
        optionTwoBtn.setEnabled(true);
        optionThreeBtn.setEnabled(true);


        questionFeedback.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        nextBtn.setEnabled(false);
    }

    private void pickQuestions() {
        for(int i=0; i < totalQuestionsToAnswer; i++) {
            int randomNumber = getRandomInt(0, allQuestionsList.size()); // bierze randomową liczbe i wybiera na tej podstawie pytania z listy która załadowaliśmhy
            questionsToAnswer.add(allQuestionsList.get(randomNumber));
            allQuestionsList.remove(randomNumber); // tego co już wylosowaliśmy nie będziemy mogli wylosować kolejny raze
            Log.d("QUESTIONS LOG", "Question " + i + " : " + questionsToAnswer.get(i).getQuestion());
        }
    }

    private int getRandomInt(int min, int max) {
        return ((int) (Math.random()*(max-min))) + min;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quiz_option_1:
                verifyAnswer(optionOneBtn);
                break;
            case R.id.quiz_option_2:
                verifyAnswer(optionTwoBtn);
                break;
            case R.id.quiz_option_3:
                verifyAnswer(optionThreeBtn);
                break;
            case R.id.quiz_next_btn:
                if(currentQuestion == totalQuestionsToAnswer){
                    //Load Results
                    submitResults();
                } else {
                    currentQuestion++;
                    loadQuestion(currentQuestion);
                    resetOptions();
                }
                break;
        }
    }

    private void submitResults() {
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("correct", correctAnswers);
        resultMap.put("wrong", wrongAnswers);
        resultMap.put("unanswered", notAnswered);

        firebaseFirestore.collection("QuizList")
                .document(quizId).collection("Results")
                .document(currentUserId).set(resultMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //przekierowanie do results
                    QuizzFragmentDirections.ActionQuizzFragmentToResultFragment action = QuizzFragmentDirections.actionQuizzFragmentToResultFragment();
                    action.setQuizId(quizId);
                    navController.navigate(action);
                } else {
                    // Error
                    quizTitle.setText(task.getException().getMessage());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void resetOptions() {
        optionOneBtn.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg, null));
        optionTwoBtn.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg, null));
        optionThreeBtn.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg, null));

        optionOneBtn.setTextColor(getResources().getColor(R.color.colorLightText, null));
        optionTwoBtn.setTextColor(getResources().getColor(R.color.colorLightText, null));
        optionThreeBtn.setTextColor(getResources().getColor(R.color.colorLightText, null));

        questionFeedback.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        nextBtn.setEnabled(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void verifyAnswer(Button selectedAnswerBtn) {

        if(canAnswer){
            selectedAnswerBtn.setTextColor(getResources().getColor(R.color.colorDark, null));

            if(questionsToAnswer.get(currentQuestion-1).getAnswer().equals(selectedAnswerBtn.getText())){
                //Prawidlowa odpowiedz
                correctAnswers++;
                selectedAnswerBtn.setBackground(getResources().getDrawable(R.drawable.correct_answer_btn_bg, null));


                questionFeedback.setText("Prawidłowa odpowiedź");
                questionFeedback.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                //Zla odpowiedz
                wrongAnswers++;
                selectedAnswerBtn.setBackground(getResources().getDrawable(R.drawable.wrong_answer_btn_bg, null));

                //feedback
                questionFeedback.setText("Zła odpowiedź \n \n Prawidłowa odpowiedź : " + questionsToAnswer.get(currentQuestion-1).getAnswer());
                questionFeedback.setTextColor(getResources().getColor(R.color.colorAccent, null));
            }
            canAnswer = false;

            //Stop timer
            countDownTimer.cancel();

            showNextBtn();
        }
    }

    private void showNextBtn() {
        if(currentQuestion == totalQuestionsToAnswer){
            nextBtn.setText("Prześlij wyniki");
        }
        questionFeedback.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.VISIBLE);
        nextBtn.setEnabled(true);
    }
}