package norbertprosniak.quizz;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;



// zarządzanie bazą danych


public class FirebaseRepository {

    private OnFirestoreTaskComplete onFirestoreTaskComplete;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private Query quizRef = firebaseFirestore.collection("QuizList").whereEqualTo("visibility", "public");  // pomoże uporzątkować baze danych

    public FirebaseRepository(OnFirestoreTaskComplete onFirestoreTaskComplete){  // konstruktor FirebaseRepository
        this.onFirestoreTaskComplete = onFirestoreTaskComplete;
    }

    public void getQuizData(){      // tworzymy baze danych (pobieramy)

        quizRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    onFirestoreTaskComplete.quizListDataAdded
                            (task.getResult().toObjects(QuizListModel.class));     // pobierze wszystkie dane z bazy danych i utworze liste
                }else{
                    onFirestoreTaskComplete.onError(task.getException());
                }
            }
        });
    }

    public interface OnFirestoreTaskComplete {      // wysłanie bazy z FireRepository do QuizListModel (klasa: model w MVVM)
        void quizListDataAdded(List<QuizListModel> quizListModelsList);
        void onError(Exception e);      // jeśli nie jest succesful wyrzuci wyjątek
    }

}
