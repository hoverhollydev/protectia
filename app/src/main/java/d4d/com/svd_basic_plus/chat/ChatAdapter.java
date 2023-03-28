package d4d.com.svd_basic_plus.chat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;
import java.util.Random;

import d4d.com.svd_basic_plus.R;

import static d4d.com.svd_basic_plus.chat.ChatActivity.contextCHA;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

        private Context context;
        List<ChatMessage> chat_adapter;

        private final static int VIEW_TYPE_ITEM = 1;
        private final static int VIEW_TYPE_LOADING = 2;

        public ChatAdapter(List<ChatMessage> chat_adapter,Context context){
                super();
                this.chat_adapter = chat_adapter;
                this.context = context;
        }

        public void add(ChatMessage response) {
                chat_adapter.add(response);
                notifyItemInserted(chat_adapter.size() - 1);
        }

        public void addAll(List<ChatMessage> postItems) {
                for (ChatMessage response : postItems) {
                        add(response);
                }
        }

        public void addUp(ChatMessage message) {
                chat_adapter.add(0,message);
                notifyItemInserted(0);
        }

        public void addDown(ChatMessage message) {
                chat_adapter.add(message);
                notifyItemInserted(chat_adapter.size() - 1);
        }

        public void addNullData() {
                chat_adapter.add(0,null);
                notifyItemInserted(0);
        }

        public void removeNull() {
                chat_adapter.remove(0);
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
                        root = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_message, parent, false);
                        return new DataViewHolder(root);
                } else {
                        root = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_progress, parent, false);
                        return new ProgressViewHolder(root);
                }
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                if (holder instanceof DataViewHolder) {
                        ChatMessage chatMessage = chat_adapter.get(position);
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
                if (chat_adapter.get(position) != null)
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
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
                        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        holder.content.setLayoutParams(lp);
                       //if(holder.txtName.getText().toString().compareToIgnoreCase("Prueba Consola")==0){
                               holder.contentWithBG.setBackgroundResource(R.drawable.burbuja_chat1);
                               holder.pico_burbuja.setBackgroundResource(R.drawable.coners);
                       //}else{
                               //holder.contentWithBG.setBackgroundResource(R.drawable.burbuja_chat3);
                              // holder.pico_burbuja.setBackgroundResource(R.drawable.coners);
                       //}


                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
                        layoutParams.gravity = Gravity.END;
                        holder.txtMessage.setLayoutParams(layoutParams);
                        layoutParams = (LinearLayout.LayoutParams) holder.txtName.getLayoutParams();
                        layoutParams.gravity = Gravity.END;
                        Random rnd = new Random();
                        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        holder.txtName.setTextColor(color);
                        holder.txtName.setLayoutParams(layoutParams);
                        layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
                        layoutParams.gravity = Gravity.END;
                        holder.txtInfo.setLayoutParams(layoutParams);
                        layoutParams = (LinearLayout.LayoutParams) holder.pico_burbuja.getLayoutParams();
                        layoutParams.gravity = Gravity.END;
                        holder.pico_burbuja.setLayoutParams(layoutParams);
                } else {
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
                        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        holder.content.setLayoutParams(lp);

                        if(holder.txtName.getText().toString().compareToIgnoreCase("Prueba Consola")!=0){
                                holder.contentWithBG.setBackgroundResource(R.drawable.burbuja_chat2);
                                holder.pico_burbuja.setBackgroundResource(R.drawable.coners2);
                        }else{
                                holder.contentWithBG.setBackgroundResource(R.drawable.burbuja_chat3);
                                 holder.pico_burbuja.setBackgroundResource(R.drawable.coners3);
                        }
                        //holder.contentWithBG.setBackgroundResource(R.drawable.burbuja_chat2);
                        //holder.pico_burbuja.setBackgroundResource(R.drawable.coners2);

                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
                        layoutParams.gravity = Gravity.START;
                        holder.txtMessage.setLayoutParams(layoutParams);
                        layoutParams = (LinearLayout.LayoutParams) holder.txtName.getLayoutParams();
                        layoutParams.gravity = Gravity.START;
                        Random rnd = new Random();
                        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        holder.txtName.setTextColor(color);
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
                if (chat_adapter != null)
                        return chat_adapter.size();
                return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
                public TextView txtName;
                public TextView txtMessage;
                public TextView txtInfo;
                public LinearLayout content;
                public CardView contentWithBG;
                public LinearLayout contentItems;
                public ImageView preview_file;
                public ImageView play_video;
                public ImageView pico_burbuja;

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

                        if(contentWithBG!=null) {
                                contentWithBG.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                                int position = getLayoutPosition();
                                                String tipo = chat_adapter.get(position).getTipo();
                                                String path = chat_adapter.get(position).getPathArchivo();
                                                Intent intent;
                                                String autority;
                                                switch (tipo) {
                                                        case "imagen":
                                                                autority = contextCHA.getPackageName() + ".provider";
                                                                intent = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(contextCHA, autority, new File(path)));
                                                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                contextCHA.startActivity(intent);
                                                                break;
                                                        case "video":
                                                                /*intent = new Intent(contextCHA, VideoviewFullActivity.class);
                                                                intent.putExtra("path_video", path);
                                                                contextCHA.startActivity(intent);*/
                                                                File videoFile = new File(path);
                                                                autority = contextCHA.getPackageName() + ".provider";
                                                                intent = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(contextCHA, autority, videoFile));
                                                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                contextCHA.startActivity(intent);
                                                                break;
                                                        case "archivo":
                                                                File pdfFile = new File(path);
                                                                if (pdfFile.exists()) {
                                                                        autority = contextCHA.getPackageName() + ".provider";
                                                                        Intent i = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(contextCHA, autority, pdfFile));
                                                                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                        try {
                                                                                try {
                                                                                        contextCHA.startActivity(i);
                                                                                } catch (Exception e) {
                                                                                        Toast.makeText(contextCHA, "No existe una aplicación de lectura instalada para este archivo", Toast.LENGTH_LONG);
                                                                                }
                                                                        } catch (ActivityNotFoundException e) {
                                                                                Toast.makeText(contextCHA, "No existe una aplicación de lectura instalada para este archivo", Toast.LENGTH_LONG);
                                                                        }
                                                                } else {
                                                                        Toast.makeText(contextCHA, "Error en la lectura del archivo", Toast.LENGTH_LONG);
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
