package br.com.fabiotavares.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final int BOOK_LOADER_ID = 1;
    ListView bookListView;
    boolean isConnected;

    private String mUrlRequestGoogleBooks = "";

    private TextView mEmptyStateTextView;

    private View circleProgressBar;

    private BookAdapter mAdapter;

    private SearchView mSearchViewField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);


        checkConnection(cm);

        bookListView = (ListView) findViewById(R.id.lista);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        bookListView.setAdapter(mAdapter);

        mEmptyStateTextView = (TextView) findViewById(R.id.vazio);
        bookListView.setEmptyView(mEmptyStateTextView);

        circleProgressBar = findViewById(R.id.loading_spinner);

        Button mSearchButton = (Button) findViewById(R.id.botao_buscar);

        mSearchViewField = (SearchView) findViewById(R.id.campo_buscar);
        mSearchViewField.onActionViewExpanded();
        mSearchViewField.setIconified(true);
        mSearchViewField.setQueryHint("Digite título do livro");


        if (isConnected) {
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            circleProgressBar.setVisibility(GONE);
            mEmptyStateTextView.setText(R.string.sem_internet);
        }


        mSearchButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                checkConnection(cm);

                if (isConnected) {
                    updateQueryUrl(mSearchViewField.getQuery().toString());
                    restartLoader();
                } else {
                    mAdapter.clear();
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText(R.string.sem_internet);
                }

            }

        });

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Book currentBook = mAdapter.getItem(position);

                assert currentBook != null;
                Uri buyBookUri = Uri.parse(currentBook.getUrlLivro());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, buyBookUri);

                startActivity(websiteIntent);
            }
        });

    }

    private String updateQueryUrl(String searchValue) {

        if (searchValue.contains(" ")) {
            searchValue = searchValue.replace(" ", "+");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("https://www.googleapis.com/books/v1/volumes?q=").append(searchValue).append("&filter=paid-ebooks&maxResults=40");
        mUrlRequestGoogleBooks = sb.toString();
        return mUrlRequestGoogleBooks;
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        Log.i("There is no instance", ": Created new one loader at the beginning!");
        updateQueryUrl(mSearchViewField.getQuery().toString());
        return new BookLoader(this, mUrlRequestGoogleBooks);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        View circleProgressBar = findViewById(R.id.loading_spinner);
        circleProgressBar.setVisibility(GONE);

        mEmptyStateTextView.setText(R.string.sem_livro);
        mAdapter.clear();

        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }

    public void restartLoader() {
        mEmptyStateTextView.setVisibility(GONE);
        circleProgressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
    }

    public void checkConnection(ConnectivityManager connectivityManager) {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting()) {
            isConnected = true;
        } else {
            isConnected = false;

        }
    }

}
