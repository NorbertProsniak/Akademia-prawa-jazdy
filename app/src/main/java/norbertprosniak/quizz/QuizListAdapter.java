package norbertprosniak.quizz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizViewHolder> {

    private List<QuizListModel> quizListModels;
    private OnQuizListItemClicked onQuizListItemClicked;

    public QuizListAdapter(OnQuizListItemClicked onQuizListItemClicked) {
        this.onQuizListItemClicked = onQuizListItemClicked;
    }

    public void setQuizListModelList(List<QuizListModel> quizListModels) {
        this.quizListModels = quizListModels;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        // tutaj będą przypisywane dane

        holder.listTitle.setText(quizListModels.get(position).getName());

        String imageUrl = quizListModels.get(position).getImage();
        Glide.with(holder.itemView.getContext())        // ładowanie zdjęcia
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(holder.listImage);

        String listDescryption = quizListModels.get(position).getDesc();
        if (listDescryption.length() > 150){
            listDescryption = listDescryption.substring(0, 150);
        }
        holder.listDesc.setText(listDescryption + "...");

    }

    @Override
    public int getItemCount() {
        if(quizListModels == null){
            return 0;
        } else {
            return quizListModels.size();   // automatycznie ustawi liczbe elementów w recycleView
        }
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // deklaracja elementów z single_list_item.xml
        private ImageView listImage;
        private TextView listTitle;
        private TextView listDesc;
        private Button listBtn;

        public QuizViewHolder(@NonNull View itemView) {

            super(itemView);

            // inicjalizacja elementów z single_list_item.xml
            listImage = itemView.findViewById(R.id.list_image);
            listTitle = itemView.findViewById(R.id.list_title);
            listDesc = itemView.findViewById(R.id.list_desc);
            listBtn = itemView.findViewById(R.id.list_btn);

            listBtn.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
                onQuizListItemClicked.onItemClicked(getAdapterPosition());
            }
        }

    public interface OnQuizListItemClicked {
        void onItemClicked(int position);
    }
}
