package com.social.socialjobs.prenotazioni;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.social.socialjobs.R;
import com.social.socialjobs.addon.NavigationMenu;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by Marco on 11/01/2020.
 *
 * Java class relativa alla gestione delle prenotazioni come cliente.
 */
public class PrenotazioniDrawer extends NavigationMenu {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setMenu(R.layout.activity_drawer_prenotazioni);

        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        SectionPagerAdapter sectionsPagerAdapter = new SectionPagerAdapter(this,
                getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

