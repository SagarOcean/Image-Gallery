package in.devco.imagegallery.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import in.devco.imagegallery.Adapter.ImageAdapter;
import in.devco.imagegallery.Model.Photo;
import in.devco.imagegallery.Presenter.IPhotoListPresenter;
import in.devco.imagegallery.Presenter.PhotoListPresenter;
import in.devco.imagegallery.R;
import in.devco.imagegallery.View.IPhotoListView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener,
        IPhotoListView {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private IPhotoListPresenter photoListPresenter;
    private TextView textView;
    private LinearLayoutManager layoutManager;
    private ImageAdapter imageAdapter;
    private List<Photo> photos;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_search) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRefresh() {
        photoListPresenter.fetchData();
        photoListPresenter.reset();
    }

    @Override
    public void update(List<Photo> photos) {
        this.photos = photos;
        imageAdapter = new ImageAdapter(MainActivity.this, this.photos);
        recyclerView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        recyclerView.setAdapter(imageAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void updateFailed() {
        recyclerView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void loadMore(List<Photo> photos) {
        progressBar.setVisibility(View.GONE);
        this.photos.addAll(photos);
        imageAdapter.notifyDataSetChanged();
    }

    void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        layoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.main_activity_rv);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new ScrollListener());

        swipeRefreshLayout = findViewById(R.id.main_activity_srl);
        swipeRefreshLayout.setOnRefreshListener(this);

        photoListPresenter = new PhotoListPresenter(this);
        photoListPresenter.fetchData();

        textView = findViewById(R.id.main_activity_tv);
        progressBar = findViewById(R.id.main_activity_pb);
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {
        private boolean isScrolling = false;

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (isScrolling && (layoutManager.getChildCount() + layoutManager.findFirstVisibleItemPosition() == layoutManager.getItemCount())) {
                progressBar.setVisibility(View.VISIBLE);
                photoListPresenter.loadMoreData();
                isScrolling = false;
            }
        }
    }
}
