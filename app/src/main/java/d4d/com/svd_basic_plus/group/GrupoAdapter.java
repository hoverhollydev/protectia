package d4d.com.svd_basic_plus.group;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;
import d4d.com.svd_basic_plus.MainActivity;
import d4d.com.svd_basic_plus.MainActivityContent;
import d4d.com.svd_basic_plus.R;
import d4d.com.svd_basic_plus.utils.SessionManager;

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.ViewHolder> {
    private Context context;
    List<GrupoDetalle> grupo_adapter;

    public GrupoAdapter(List<GrupoDetalle> grupo_adapter,Context context){
        super();
        this.grupo_adapter = grupo_adapter;
        this.context = context;
    }

    public void clear() {
        int size = grupo_adapter.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                grupo_adapter.remove(0);
            }
            notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_grupos, parent, false);
        ViewHolder cvh = new ViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LayerDrawable bgDrawable = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.circle_icon_group);/*drawable*/
        GradientDrawable bg_layer = (GradientDrawable)bgDrawable.findDrawableByLayerId (R.id.bg_layer);/*findDrawableByLayerId*/
        bg_layer.setColor(Color.WHITE);/*set color to layer*/
        bgDrawable.setDrawableByLayerId(R.id.ll_icon_group, grupo_adapter.get(position).getGrupoImagen());/*set drawable to layer*/
        holder.imagen.setBackground(bgDrawable);/*set drawable to image*/
        holder.title.setText(grupo_adapter.get(position).getGrupoNombre());
        holder.description.setText(grupo_adapter.get(position).getGrupoId());
        if(grupo_adapter.get(position).getGrupoId().compareToIgnoreCase(SessionManager.getGrupoId())==0) {
            holder.imagen_activo.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_ptt_active));
        }
    }

    @Override
    public int getItemCount() {
        if (grupo_adapter != null)
            return grupo_adapter.size();
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button imagen;
        public Button imagen_activo;
        public TextView title, description, user_ptt;
        public RelativeLayout content_group;
        public ViewHolder(View itemView) {
            super(itemView);
            content_group = itemView.findViewById(R.id.content_group);
            imagen = itemView.findViewById(R.id.imagen_grupo);
            imagen_activo = itemView.findViewById(R.id.imagen_activo);
            title = itemView.findViewById(R.id.grupo_nombre);
            description = itemView.findViewById(R.id.id_grupo);
            user_ptt = itemView.findViewById(R.id.id_user_ptt);
            content_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    String grupo_nombre=grupo_adapter.get(position).getGrupoNombre();
                    String grupo_id=grupo_adapter.get(position).getGrupoId();
                    View view;
                    Log.i("OCRW","ID GROUP:"+grupo_id+", UUID:"+SessionManager.getUuid()+", TOKEN:"+SessionManager.getToken()+",  NOMBRE:"+SessionManager.getNombreUsuario());
                    //Log.i("HOLDER SIZE", grupo_adapter.size() +"");
                    for (int i = 0; i < grupo_adapter.size(); i++) {

                        if(position == i ){
                            view = MainActivity.recyclerView.findViewHolderForAdapterPosition(i).itemView;
                            Button imagen_activo_aux = view.findViewById(R.id.imagen_activo);
                            imagen_activo_aux.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_ptt_active));
                        }else{
                            view = MainActivity.recyclerView.getChildAt(i);
                            if(view!=null) {
                                //view = MainActivity.recyclerView.findViewHolderForAdapterPosition(i).itemView;
                                //Log.i("HOLDER", i +"");
                                Button imagen_activo_aux = view.findViewById(R.id.imagen_activo);
                                imagen_activo_aux.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_ptt_inactive));
                            }
                        }
                    }
                    Intent intent = new Intent(context, MainActivityContent.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id_group", grupo_id);
                    intent.putExtra("name_group", grupo_nombre);
                    context.startActivity(intent);
                }
            });
        }
    }
}
