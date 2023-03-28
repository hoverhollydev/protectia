package d4d.com.svd_basic_plus.proyecto;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import d4d.com.svd_basic_plus.R;
import pl.droidsonroids.gif.GifImageView;


import static d4d.com.svd_basic_plus.proyecto.ProyectoActivity.contextProAct;

public class ProyectoAdapter extends RecyclerView.Adapter<ProyectoAdapter.ViewHolder> {

        private Context context;
        List<ProyectoModel> proyecto_adapter;

        private final static int VIEW_TYPE_ITEM = 1;
        private final static int VIEW_TYPE_LOADING = 2;

        public ProyectoAdapter(List<ProyectoModel> proyecto_adapter, Context context){
                super();
                this.proyecto_adapter = proyecto_adapter;
                this.context = context;
        }

        public void add(ProyectoModel response) {
                proyecto_adapter.add(response);
                notifyItemInserted(proyecto_adapter.size() - 1);
        }

        public void addAll(List<ProyectoModel> postItems) {
                for (ProyectoModel response : postItems) {
                        add(response);
                }
        }

        public void addUp(ProyectoModel message) {
                proyecto_adapter.add(0,message);
                notifyItemInserted(0);
        }

        public void addDown(ProyectoModel message) {
                proyecto_adapter.add(message);
                notifyItemInserted(proyecto_adapter.size() - 1);
        }

        public void addNullData() {
                proyecto_adapter.add(0,null);
                notifyItemInserted(0);
        }

        public void removeNull() {
                proyecto_adapter.remove(0);
                notifyItemRemoved(0);
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                /*View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_message, parent, false);
                ViewHolder cvh = new ViewHolder(v);
                Log.i("INFLADO","HELO");
                return cvh;*/

                View root = null;
                Log.i("viewType",viewType+ "__");
                if (viewType == VIEW_TYPE_ITEM) {
                        root = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_proyectos, parent, false);
                        return new DataViewHolder(root);
                } else {
                        root = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_progress, parent, false);
                        return new ProgressViewHolder(root);
                }
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                if (holder instanceof DataViewHolder) {
                        ProyectoModel chatMessage = proyecto_adapter.get(position);
                        boolean myMsg = chatMessage.getIsme();
                        holder.txtName.setText(chatMessage.getNombreUser());
                        Log.i("INFLADO", "HELO " + myMsg);
                        setAlignment(holder, myMsg);
                        String tipo_message = chatMessage.getTipo();
                        if (tipo_message.compareToIgnoreCase("texto") == 0) {
                                holder.preview_file.setVisibility(View.GONE);
                                holder.play_video.setVisibility(View.GONE);
                                int tamanio = chatMessage.getMessage().length();
                                if (tamanio > 65) {
                                        holder.contentItems.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                        holder.contentItems.getLayoutParams().width = 640;
                                        holder.contentItems.requestLayout();
                                } else {
                                        holder.contentItems.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                        holder.contentItems.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
                                        holder.contentItems.requestLayout();
                                }
                                holder.txtMessage.setText(chatMessage.getMessage());

                        } else {
                                holder.preview_file.setVisibility(View.VISIBLE);
                                holder.play_video.setVisibility(View.GONE);
                                if (tipo_message.compareToIgnoreCase("video") == 0) {
                                        holder.play_video.setVisibility(View.VISIBLE);
                                }
                                if (tipo_message.compareToIgnoreCase("archivo") == 0) {
                                        holder.preview_file.getLayoutParams().height = 120;
                                        holder.preview_file.getLayoutParams().width = 120;
                                } else {
                                        holder.preview_file.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;
                                        holder.preview_file.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                                }
                                holder.contentItems.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;
                                holder.contentItems.getLayoutParams().width = 360;
                                holder.contentItems.requestLayout();

                                holder.preview_file.setDrawingCacheEnabled(true);
                                holder.preview_file.setImageBitmap(chatMessage.getPreviewArchivo());
                                holder.txtMessage.setText(chatMessage.getMessage());
                        }
                        holder.txtInfo.setText(chatMessage.getDate());
                       /* if(validaPicoAux!=myMsg) {
                            validaPicoAux = myMsg;
                            validaPicoBurbuja1=0;
                        }
                        if (validaPicoBurbuja1 == 0) {
                           holder.pico_burbuja.setVisibility(View.VISIBLE);
                           validaPicoBurbuja1++;
                           validaPicoAux = myMsg;
                        }else{
                           holder.pico_burbuja.setVisibility(View.GONE);
                           validaPicoBurbuja1++;
                        }*/
                }else{
                        //Do whatever you want. Or nothing !!
                }
        }

        @Override
        public int getItemViewType(int position) {
                if (proyecto_adapter.get(position) != null)
                        return VIEW_TYPE_ITEM;
                else
                        return VIEW_TYPE_LOADING;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
                super.onAttachedToRecyclerView(recyclerView);
        }

        private void setAlignment(ViewHolder holder, boolean isMe) {
                if (!isMe) {

                        holder.linearAnimationBot1.setVisibility(View.VISIBLE);
                        holder.linearAnimationBot.setVisibility(View.GONE);

                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
                        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        holder.content.setLayoutParams(lp);

                        holder.cardview_root.setBackgroundResource(R.drawable.burbuja_chat1);
                        holder.pico_burbuja.setBackgroundResource(R.drawable.coners);

                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
                        layoutParams.gravity = Gravity.END;
                        holder.txtMessage.setLayoutParams(layoutParams);
                        layoutParams = (LinearLayout.LayoutParams) holder.txtName.getLayoutParams();
                        layoutParams.gravity = Gravity.END;
                        holder.txtName.setLayoutParams(layoutParams);
                        layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
                        layoutParams.gravity = Gravity.END;
                        holder.txtInfo.setLayoutParams(layoutParams);
                        layoutParams = (LinearLayout.LayoutParams) holder.pico_burbuja.getLayoutParams();
                        layoutParams.gravity = Gravity.END;
                        holder.pico_burbuja.setLayoutParams(layoutParams);
                } else {

                        holder.linearAnimationBot.setVisibility(View.VISIBLE);
                        holder.linearAnimationBot1.setVisibility(View.GONE);
                        holder.txtName.setText("Rogelio");


                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
                        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        holder.content.setLayoutParams(lp);

                        holder.cardview_root.setBackgroundResource(R.drawable.burbuja_chat2);
                        holder.pico_burbuja.setBackgroundResource(R.drawable.coners2);

                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
                        layoutParams.gravity = Gravity.START;
                        holder.txtMessage.setLayoutParams(layoutParams);
                        layoutParams = (LinearLayout.LayoutParams) holder.txtName.getLayoutParams();
                        layoutParams.gravity = Gravity.START;
                        holder.txtName.setLayoutParams(layoutParams);
                        layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
                        layoutParams.gravity = Gravity.END;
                        holder.txtInfo.setLayoutParams(layoutParams);
                        layoutParams = (LinearLayout.LayoutParams) holder.pico_burbuja.getLayoutParams();
                        layoutParams.gravity = Gravity.START;
                        holder.pico_burbuja.setLayoutParams(layoutParams);

                }
        }

        @Override
        public int getItemCount() {
                if (proyecto_adapter != null)
                        return proyecto_adapter.size();
                return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
                public TextView txtName;
                public TextView txtMessage;
                public TextView txtInfo;
                public LinearLayout content;
                public CardView cardview_root;
                public LinearLayout contentItems;
                public ImageView preview_file;
                public ImageView play_video;
                public ImageView pico_burbuja;
                public LinearLayout linearAnimationBot;
                public LinearLayout linearAnimationBot1;
                public LinearLayout contentWithBG;


                public ViewHolder(View itemView) {
                        super(itemView);
                        txtName = itemView.findViewById(R.id.txtName);
                        txtMessage = itemView.findViewById(R.id.txtMessage);
                        content = itemView.findViewById(R.id.content);
                        contentWithBG = itemView.findViewById(R.id.contentWithBackground);
                        contentItems = itemView.findViewById(R.id.contentItems);
                        txtInfo = itemView.findViewById(R.id.txtInfo);
                        preview_file = itemView.findViewById(R.id.preview_file);
                        play_video = itemView.findViewById(R.id.play_video);
                        pico_burbuja = itemView.findViewById(R.id.pico_burbuja);
                        linearAnimationBot = itemView.findViewById(R.id.bot_animation);
                        linearAnimationBot1 = itemView.findViewById(R.id.bot_animation1);

                        cardview_root= itemView.findViewById(R.id.cardview_root);

                        if(cardview_root!=null) {
                                cardview_root.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                                int position = getLayoutPosition();
                                                String tipo = proyecto_adapter.get(position).getTipo();
                                                String path = proyecto_adapter.get(position).getPathArchivo();
                                                Intent intent;
                                                String autority;
                                                switch (tipo) {
                                                        case "imagen":
                                                                autority = contextProAct.getPackageName() + ".provider";
                                                                intent = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(contextProAct, autority, new File(path)));
                                                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                contextProAct.startActivity(intent);
                                                                break;
                                                        case "video":
                                                                /*intent = new Intent(contextProAct, VideoviewFullActivity.class);
                                                                intent.putExtra("path_video", path);
                                                                contextProAct.startActivity(intent);*/
                                                                File videoFile = new File(path);
                                                                autority = contextProAct.getPackageName() + ".provider";
                                                                intent = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(contextProAct, autority, videoFile));
                                                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                contextProAct.startActivity(intent);
                                                                break;
                                                        case "archivo":
                                                                File pdfFile = new File(path);
                                                                if (pdfFile.exists()) {
                                                                        autority = contextProAct.getPackageName() + ".provider";
                                                                        Intent i = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(contextProAct, autority, pdfFile));
                                                                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                        try {
                                                                                try {
                                                                                        contextProAct.startActivity(i);
                                                                                } catch (Exception e) {
                                                                                        Toast.makeText(contextProAct, "No existe una aplicación de lectura instalada para este archivo", Toast.LENGTH_LONG);
                                                                                }
                                                                        } catch (ActivityNotFoundException e) {
                                                                                Toast.makeText(contextProAct, "No existe una aplicación de lectura instalada para este archivo", Toast.LENGTH_LONG);
                                                                        }
                                                                } else {
                                                                        Toast.makeText(contextProAct, "Error en la lectura del archivo", Toast.LENGTH_LONG);
                                                                }
                                                                break;
                                                }
                                        }
                                });
                        }
                }
        }

        class DataViewHolder extends ViewHolder {
                public DataViewHolder(View itemView) {
                        super(itemView);

                }

        }

        class ProgressViewHolder extends ViewHolder {

                public ProgressViewHolder(View itemView) {
                        super(itemView);
                }
        }

}
