package br.com.fabiotavares.booklisting;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
    /**
     * Titulo do livro
     */
    private final String titulo;
    /**
     * Autor do livro
     */
    private final String autor;
    /**
     * URL da imagem
     */
    private final String urlImagem;
    /**
     * Preço do livro
     */
    private final Double preco;
    /**
     * Moeda do preço
     */
    private final String moeda;
    /**
     * Idioma
     */
    private final String idioma;
    /**
     * Url do livro
     */
    private String urlLivro;

    /**
     * @param titulo     - (String) Nome do livro: "Harry Potter e a Pedra Filosofal"
     * @param autor    - (String) Nome do autor: "J.K. Rowling"
     * @param urlImagem - (String) Url da capa da imagem: "http://books.google.com/books/(...)"
     * @param preco     - (Double) Preço do livro: 39.00
     * @param moeda      - (String) Moeda utilizada para o preço: "BRL"
     * @param idioma  - (String) Idioma do livro: "PL"
     * @param urlLivro       - (String) Url do livro no Google Play
     */
    public Book(String titulo, String autor, String urlImagem, Double preco, String moeda, String idioma, String urlLivro) {
        this.titulo = titulo;
        this.autor = autor;
        this.urlImagem = urlImagem;
        this.preco = preco;
        this.moeda = moeda;
        this.idioma = idioma;
        this.urlLivro = urlLivro;

    }

    protected Book(Parcel in) {
        this.titulo = in.readString();
        this.autor = in.readString();
        this.urlImagem = in.readString();
        this.preco = (Double) in.readValue(Double.class.getClassLoader());
        this.moeda = in.readString();
        this.idioma = in.readString();
        this.urlLivro = in.readString();
    }

    // Getters
    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public Double getPreco() {
        return preco;
    }

    public String getMoeda() {
        return moeda;
    }

    public String getIdioma() {
        return idioma;
    }

    public String getUrlLivro() {
        return urlLivro;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.titulo);
        dest.writeString(this.autor);
        dest.writeString(this.urlImagem);
        dest.writeValue(this.preco);
        dest.writeString(this.moeda);
        dest.writeString(this.idioma);
        dest.writeString(this.urlLivro);
    }
}
