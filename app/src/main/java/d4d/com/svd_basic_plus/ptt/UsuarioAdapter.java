package d4d.com.svd_basic_plus.ptt;

import android.app.Activity;
import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import d4d.com.svd_basic_plus.R;

public class UsuarioAdapter extends BaseAdapter {

    private final List<UsuarioDetalle> items;
    private Activity context;
    public UsuarioAdapter(Activity context, List<UsuarioDetalle> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    @Override
    public UsuarioDetalle getItem(int position) {
        if (items != null) {
            return items.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clear() {
        items.clear();
    }

    public void add(UsuarioDetalle message) {
        items.add(message);
    }

    public void add(List<UsuarioDetalle> messages) {
        items.addAll(messages);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        UsuarioDetalle usuarioDetalle = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.list_item_usuarios, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.usuario_nombre.setText(usuarioDetalle.getUsuarioNombre());
        holder.descripcion_estado.setText(usuarioDetalle.getDescripcionEstado());
        if(usuarioDetalle.getEstado()==true) {
            holder.usuario_nombre.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.descripcion_estado.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.imagen_estado.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_conectado));
            if(usuarioDetalle.getSiPtt()==true) {
                holder.imagen_ptt.setImageDrawable(context.getResources().getDrawable(R.drawable.ptt_on));
            }else{
                holder.imagen_ptt.setImageDrawable(context.getResources().getDrawable(R.drawable.ptt_off));
            }
            /*if(usuarioDetalle.getSiChat()==true) {
                holder.imagen_chat.setImageDrawable(context.getResources().getDrawable(R.drawable.chat_on));
            }else{
                holder.imagen_chat.setImageDrawable(context.getResources().getDrawable(R.drawable.chat_off));
            }*/

        }else{
            holder.usuario_nombre.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.descripcion_estado.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.imagen_estado.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_desconectado));
            holder.imagen_ptt.setImageDrawable(context.getResources().getDrawable(R.drawable.ptt_off));
            //holder.imagen_chat.setImageDrawable(context.getResources().getDrawable(R.drawable.chat_off));
        }
        return convertView;
    }


    private ViewHolder createViewHolder(View v) {
        final ViewHolder holder = new ViewHolder();

        holder.imagen_usuario = (ImageView) v.findViewById(R.id.imagen_usuario);
        holder.usuario_nombre = (TextView) v.findViewById(R.id.usuario_nombre);
        holder.descripcion_estado = (TextView) v.findViewById(R.id.descripcion_estado);
        holder.imagen_estado = (ImageView) v.findViewById(R.id.imagen_estado);
        holder.imagen_ptt = (ImageView) v.findViewById(R.id.imagen_si_ptt);
        //holder.imagen_chat = (ImageView) v.findViewById(R.id.imagen_si_chat);
        return holder;
    }

    private static class ViewHolder {
        public ImageView imagen_usuario;
        public TextView descripcion_estado;
        public TextView usuario_nombre;
        public ImageView imagen_estado;
        public ImageView imagen_ptt;
        //public ImageView imagen_chat;
    }
}