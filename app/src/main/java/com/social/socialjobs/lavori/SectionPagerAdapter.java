package com.social.socialjobs.lavori;

import android.content.Context;

import com.social.socialjobs.R;
import com.social.socialjobs.addon.PlaceholderFragment;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Created by Marco on 23/01/2020.
 *
 * Java class relativa alla selezione del fragment.
 */
public class SectionPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;

    SectionPagerAdapter(Context aContext, FragmentManager fm) {
        super(fm);
        mContext = aContext;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem è un metodo che serve per istanziare il fragment per la pagina data.
        return PlaceholderFragment.newInstance(position, "lavori");
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Mostra 3 pagine in totale.
        return 3;
    }
}
