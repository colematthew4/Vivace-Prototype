package cole.matthew.vivace.Activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link android.preference.PreferenceActivity} which implements and proxies the necessary calls
 * to be used with AppCompat.
 */
public abstract class AppCompatPreferenceActivity extends PreferenceActivity
{
    private AppCompatDelegate mDelegate;

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    /** {@inheritDoc} */
    @Override
    protected void onStop()
    {
        super.onStop();
        getDelegate().onStop();
    }

    /** {@inheritDoc} */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    /** Create a {@link android.support.v7.app.AppCompatDelegate} to use with {@code activity}. */
    private AppCompatDelegate getDelegate()
    {
        if (mDelegate == null)
            mDelegate = AppCompatDelegate.create(this, null);

        return mDelegate;
    }

    /** {@inheritDoc} */
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    /** {@inheritDoc} */
    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    /** {@inheritDoc} */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    /** {@inheritDoc} */
    @Override
    public void setContentView(@LayoutRes int layoutResID)
    {
        getDelegate().setContentView(layoutResID);
    }

    /** {@inheritDoc} */
    @Override
    public void setContentView(View view)
    {
        getDelegate().setContentView(view);
    }

    /** {@inheritDoc} */
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params)
    {
        getDelegate().setContentView(view, params);
    }

    /** {@inheritDoc} */
    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params)
    {
        getDelegate().addContentView(view, params);
    }

    /**
     * Declare that the options menu has changed, so should be recreated.
     * The {@link #onCreateOptionsMenu(Menu)} method will be called the next
     * time it needs to be displayed.
     */
    public void invalidateOptionsMenu()
    {
        getDelegate().invalidateOptionsMenu();
    }

    /** {@inheritDoc} */
    @Override
    @NotNull
    public MenuInflater getMenuInflater()
    {
        return getDelegate().getMenuInflater();
    }

    /** {@inheritDoc} */
    @Override
    protected void onTitleChanged(CharSequence title, int color)
    {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    public ActionBar getSupportActionBar()
    {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar)
    {
        getDelegate().setSupportActionBar(toolbar);
    }
}
