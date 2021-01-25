package norbertprosniak.quizz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import java.util.List;

// TO JEST VIEW
// nie powinna ta klasa wiedzieć nic o zapytaniach do bazy danych jedyna rzecz którą powinna dostać ta klasa to lista danych
// całą resztą martwi się QuizListViewModel, więc za każdym razem kiedy dane się zmieniają, QuizListViewModel dostanie tylko liste
// reszdta procesów wykona się w ViewModel i innych klasach związanych z tym. Więc to jest cały point of view model class


public class ListFragment extends Fragment implements QuizListAdapter.OnQuizListItemClicked {

    // zdeklarowanie elementów layoutu
    private RecyclerView listView;
    private ProgressBar listProgress;
    private NavController navController;

    // zdeklarowanie adaptera
    private QuizListAdapter adapter;

    // zdeklarowanie animacji
    private Animation fadeInAnim;
    private Animation fadeOutAnim;



    // wyświetlamy baze danych z QuizListViewModel class
    private QuizListViewModel quizListViewModel;

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // inicjalizacja elementów layoutu
        listView = view.findViewById(R.id.list_view);
        listProgress = view.findViewById(R.id.list_progress);
        navController = Navigation.findNavController(view);

        // inicjalizacja adaptera
        adapter = new QuizListAdapter(this);

        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setHasFixedSize(true);
        listView.setAdapter(adapter);

    // inicjalizacja animacji
        fadeInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fadeOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class); // inicjalizacja ViewModel klasy

        //mamy już LiveDate więc żeby jej użyć
        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {  // Observer sprawdzi wszystkie zmiany w quizListModelData
                                                                                                                        // powiadomi nas o tym i wyśle nam nowe dane użwając quizListModels
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {     // mamy tutaj całą liste naszych quizów możemy więc teraz użyc Adaptera do wypełnienia całego Recycle View
                adapter.setQuizListModelList(quizListModels);
                adapter.notifyDataSetChanged();

                listView.startAnimation(fadeInAnim);
                listProgress.startAnimation(fadeOutAnim);
            }
        });
    }

    @Override
    public void onItemClicked(int position) {

                ListFragmentDirections.ActionListFragmentToDetailsFragment action = ListFragmentDirections.actionListFragmentToDetailsFragment();
                action.setPosition(position);
                navController.navigate(action);




    }



    // jak już ViewModel kalasa jest zainicjializowana to wywoła firabaseRepository.getQuizData() (z QuizListViweModel)
    // ktora znów wywoła quizRef.get().addOnCompleteListener... (FirebaseRepository) i ustawi do listy (quizListDataAdded) którą wysłaliśmy z interfejstu
    // wracając się do ViewModel klasy do MutableLiveData
}