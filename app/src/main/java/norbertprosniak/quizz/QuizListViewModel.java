package norbertprosniak.quizz;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

// TO JEST VIEWMODEL

// obserwowanie live data i update listFragment i detailsFragment


public class QuizListViewModel extends ViewModel implements FirebaseRepository.OnFirestoreTaskComplete {

    private MutableLiveData<List<QuizListModel>> quizListModelData = new MutableLiveData<>();   // MutableLiveData jest podklasą LiveData ale zawiera dodatkowe funkcje
                                                                                            // jak setValue i postValue które użyjemy w naszym quizListDataAdded

    public LiveData<List<QuizListModel>> getQuizListModelData() {    // get LiveData
        return quizListModelData;
    }

    private FirebaseRepository firebaseRepository = new FirebaseRepository(this);


    public QuizListViewModel(){     // konstruktor
        firebaseRepository.getQuizData(); // jeśli QuizListModel jest zainicjalizowany odrazu wywoła geQuizData, jak tylko się wywoła to wywoła metody quizListDataAdded
                                            // i onError z QuizListViewModel
    }


    @Override
    public void quizListDataAdded(List<QuizListModel> quizListModelsList) {     // następnie dodajemy cała baze (List<QuizListModel>) i dodajemy do LiveData
        quizListModelData.setValue(quizListModelsList);
    }

    @Override
    public void onError(Exception e) {

    }
}
