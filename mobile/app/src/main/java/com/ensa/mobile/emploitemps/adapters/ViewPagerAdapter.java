package com.ensa.mobile.emploitemps.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ensa.mobile.emploitemps.ui.FragmentJour;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private String[] jours;

    public ViewPagerAdapter(@NonNull FragmentActivity fa, String[] jours) {
        super(fa);
        this.jours = jours;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return FragmentJour.newInstance(jours[position]);
    }

    @Override
    public int getItemCount() {
        return jours.length;
    }
}
