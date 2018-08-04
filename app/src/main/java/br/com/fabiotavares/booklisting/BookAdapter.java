package br.com.fabiotavares.booklisting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Activity context, ArrayList<Book> Books) {
        super(context, 0, Books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        final Book livro = getItem(position);

        TextView tituloTextView = (TextView) listItemView.findViewById(R.id.titulo_livro);
        TextView autorTextView = (TextView) listItemView.findViewById(R.id.autor);
        ImageView capaImageView = (ImageView) listItemView.findViewById(R.id.capa_image);
        TextView precoTextView = (TextView) listItemView.findViewById(R.id.preco_livro);
        TextView idiomaCode = (TextView) listItemView.findViewById(R.id.codigo_pais);
        TextView moedaCode = (TextView) listItemView.findViewById(R.id.codigo_moeda);

        assert livro != null;
        tituloTextView.setText(livro.getTitulo());
        autorTextView.setText(livro.getAutor());
        Picasso.get().load(livro.getUrlImagem()).into(capaImageView);
        precoTextView.setText(String.valueOf(formatarPreco(livro.getPreco())));
        idiomaCode.setText(livro.getIdioma());
        moedaCode.setText(livro.getMoeda());

        return listItemView;

    }

    // Formata o preço para casa digital de dois digitos
    private String formatarPreco(double preco) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(preco);
    }
}
