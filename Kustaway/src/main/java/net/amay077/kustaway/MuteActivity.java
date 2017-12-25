package net.amay077.kustaway;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.amay077.kustaway.adapter.SimplePagerAdapter;
import net.amay077.kustaway.fragment.mute.SourceFragment;
import net.amay077.kustaway.fragment.mute.UserFragment;
import net.amay077.kustaway.fragment.mute.WordFragment;
import net.amay077.kustaway.util.ThemeUtil;

public class MuteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.setTheme(this);
        setContentView(R.layout.activity_mute);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3); // 3だと不要なんだけど一応...

        SimplePagerAdapter simplePagerAdapter = new SimplePagerAdapter(this, viewPager);
        simplePagerAdapter.addTab(UserFragment.class, null);
        simplePagerAdapter.addTab(SourceFragment.class, null);
        simplePagerAdapter.addTab(WordFragment.class, null);
        simplePagerAdapter.notifyDataSetChanged();

        final int colorBlue = ThemeUtil.getThemeTextColor(R.attr.holo_blue);
        final int colorWhite = ThemeUtil.getThemeTextColor(R.attr.text_color);

        /**
         * タブのラベル情報を配列に入れておく
         */
        final TextView[] tabs = {
                (TextView) findViewById(R.id.tab_user),
                (TextView) findViewById(R.id.tab_source),
                (TextView) findViewById(R.id.tab_word),
        };

        /**
         * タップしたら移動
         */
        for (int i = 0; i < tabs.length; i++) {
            final int item = i;
            tabs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(item);
                }
            });
        }

        /**
         * 最初のタブを青くする
         */
        tabs[0].setTextColor(colorBlue);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                /**
                 * タブのindexと選択されたpositionを比較して色を設定
                 */
                for (int i = 0; i < tabs.length; i++) {
                    tabs[i].setTextColor(i == position ? colorBlue : colorWhite);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
