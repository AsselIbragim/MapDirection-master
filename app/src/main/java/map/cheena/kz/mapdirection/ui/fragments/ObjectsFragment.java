package map.cheena.kz.mapdirection.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import map.cheena.kz.mapdirection.R;
import map.cheena.kz.mapdirection.entities.Object;
import map.cheena.kz.mapdirection.utils.Constants;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ObjectsFragment extends Fragment {
    public  static  final  String TAG = Constants.OBJECTSFRAGMENT;
    private ImageView backImage;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    private List<Object> objectList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_objects, container, false);

        initViews(view);
        getObjects();
        return view;
    }

    private void initViews(View view) {
        backImage = view.findViewById(R.id.fo_back_image);
        recyclerView = view.findViewById(R.id.fo_recyclerview);
        progressBar = view.findViewById(R.id.fo_progressbar);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void getObjects(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        progressBar.setVisibility(View.VISIBLE);

        reference.child("Objects").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                objectList = new ArrayList<>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    objectList.add(child.getValue(Object.class));
                }
                setObjects(objectList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setObjects(List<Object> objects){
        progressBar.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ObjectsAdapter(getContext(), objects));
    }

    public class ObjectsAdapter extends RecyclerView.Adapter<ObjectsAdapter.ViewHolder> {

        private Context context;
        private List<Object> objectList;

        public ObjectsAdapter(Context context, List<Object> objectList) {
            this.context = context;
            this.objectList = objectList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title,desc;
            private ImageView imageView;
            public ConstraintLayout view;


            public ViewHolder(View v) {
                super(v);
                title = (TextView)v.findViewById(R.id.foi_title);
                desc = (TextView)v.findViewById(R.id.foi_desc);
                imageView = (ImageView) v.findViewById(R.id.foi_image);
                view = (ConstraintLayout) v.findViewById(R.id.foi_view);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_object_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
            viewHolder.title.setText(objectList.get(i).getName());
            viewHolder.desc.setText(objectList.get(i).getDesc());
            Glide.with(context)
                    .load(objectList.get(i).getImg())
                    .apply(RequestOptions.circleCropTransform())
                    .into(viewHolder.imageView);

            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ObjectsFragment.class);
                    intent.putExtra("object", objectList.get(i));

                    getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                    getFragmentManager().popBackStack();
                }
            });
        }

        @Override
        public int getItemCount() {
            return objectList == null ? 0 : objectList.size();
        }
    }
}
