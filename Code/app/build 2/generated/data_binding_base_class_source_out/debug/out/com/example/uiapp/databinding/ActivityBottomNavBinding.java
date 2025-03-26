// Generated by view binder compiler. Do not edit!
package com.example.uiapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.uiapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityBottomNavBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final RelativeLayout container;

  @NonNull
  public final BottomNavigationView navView;

  @NonNull
  public final MaterialCardView navViewCard;

  private ActivityBottomNavBinding(@NonNull RelativeLayout rootView,
      @NonNull RelativeLayout container, @NonNull BottomNavigationView navView,
      @NonNull MaterialCardView navViewCard) {
    this.rootView = rootView;
    this.container = container;
    this.navView = navView;
    this.navViewCard = navViewCard;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityBottomNavBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityBottomNavBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_bottom_nav, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityBottomNavBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      RelativeLayout container = (RelativeLayout) rootView;

      id = R.id.nav_view;
      BottomNavigationView navView = ViewBindings.findChildViewById(rootView, id);
      if (navView == null) {
        break missingId;
      }

      id = R.id.nav_view_card;
      MaterialCardView navViewCard = ViewBindings.findChildViewById(rootView, id);
      if (navViewCard == null) {
        break missingId;
      }

      return new ActivityBottomNavBinding((RelativeLayout) rootView, container, navView,
          navViewCard);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
