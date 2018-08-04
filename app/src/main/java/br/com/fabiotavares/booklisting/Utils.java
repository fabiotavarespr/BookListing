package br.com.fabiotavares.booklisting;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();

    private static List<Book> extractBookFromJson(String livroJSON) {
        if (TextUtils.isEmpty(livroJSON)) {
            return null;
        }

        List<Book> books = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(livroJSON);
            JSONArray livroArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < livroArray.length(); i++) {

                JSONObject livro = livroArray.getJSONObject(i);
                JSONObject informacaoes = livro.getJSONObject("volumeInfo");

                String autor;
                if (informacaoes.has("authors")) {
                    JSONArray autores = informacaoes.getJSONArray("authors");

                    if (!informacaoes.isNull("authors")) {
                        autor = (String) autores.get(0);
                    } else {
                        autor = "*** Autor desconhecido ***";
                    }
                } else {
                    autor = "*** Sem informações do autor ***";
                }

                JSONObject imageLinks = informacaoes.getJSONObject("imageLinks");

                JSONObject informacoesItem = livro.getJSONObject("saleInfo");
                JSONObject precoItem = informacoesItem.getJSONObject("retailPrice");

                String titulo = informacaoes.getString("title");

                String idioma = informacaoes.getString("language");

                String urlImagem = imageLinks.getString("smallThumbnail");

                StringBuilder stringBuilder = new StringBuilder();

                Pattern p = Pattern.compile("id=(.*?)&");
                Matcher m = p.matcher(urlImagem);
                if (m.matches()) {
                    String id = m.group(1);
                    urlImagem = String.valueOf(stringBuilder.append("https://books.google.com/books/content/images/frontcover/").append(id).append("?fife=w300"));
                } else {
                    Log.i(LOG_TAG, "Problema com a capa do livro");
                }

                double preco = precoItem.getDouble("amount");

                String moeda = precoItem.getString("currencyCode");

                String urlLivro = (String) informacoesItem.get("buyLink");

                Book bookItem = new Book(titulo, autor, urlImagem, preco, moeda, idioma, urlLivro);

                books.add(bookItem);

            }

        } catch (JSONException e) {
                Log.e(LOG_TAG, "Erro com o parsing do json", e);
        }

        return books;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Erro construindo a url ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {

        final int READ_TIMEOUT = 10000;
        final int CONNECT_TIMEOUT = 15000;
        final int CORRECT_RESPONSE_CODE = 200;

        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == CORRECT_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Codigo de retorno: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Erro ao receber o Json", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    static List<Book> fetchBookData(String requestUrl) {

        final int SLEEP_TIME_MILLIS = 2000;

        try {
            Thread.sleep(SLEEP_TIME_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Erro com o HTTP Request", e);
        }

        List<Book> listLivros = extractBookFromJson(jsonResponse);

        return listLivros;
    }
}
