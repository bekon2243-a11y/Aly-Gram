package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;

public class AlyGramTapGameActivity extends BaseFragment {

    private static final String PREF_TAPS = "alygram_tap_game_taps";
    private static final String PREF_COINS = "alygram_tap_game_coins";
    private static final String PREF_LEVEL = "alygram_tap_game_level";

    private long taps;
    private long coins;
    private int level;

    private TextView statsView;
    private TextView upgradeView;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("AlyGram Fire Clicker");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        loadState();

        FrameLayout layout = new FrameLayout(context);
        layout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        fragmentView = layout;

        RLottieImageView fireView = new RLottieImageView(context);
        fireView.setAutoRepeat(true);
        fireView.setAnimation(R.raw.fire_on, 180, 180);
        fireView.playAnimation();
        fireView.setOnClickListener(v -> {
            taps++;
            coins += getCoinPerTap();
            saveState();
            updateTexts();
        });
        layout.addView(fireView, LayoutHelper.createFrame(180, 180, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 44, 0, 0));

        RLottieImageView ringView = new RLottieImageView(context);
        ringView.setAutoRepeat(true);
        ringView.setAnimation(R.raw.aly_ring, 74, 74);
        ringView.playAnimation();
        layout.addView(ringView, LayoutHelper.createFrame(74, 74, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 82, 56, 0, 0));

        RLottieImageView duckView = new RLottieImageView(context);
        duckView.setAutoRepeat(true);
        duckView.setAnimation(R.raw.utyan_cache, 82, 82);
        duckView.playAnimation();
        layout.addView(duckView, LayoutHelper.createFrame(82, 82, Gravity.TOP | Gravity.CENTER_HORIZONTAL, -82, 56, 0, 0));

        statsView = new TextView(context);
        statsView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        statsView.setTextSize(17);
        statsView.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(statsView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP, 24, 240, 24, 0));

        upgradeView = new TextView(context);
        upgradeView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        upgradeView.setTextSize(16);
        upgradeView.setGravity(Gravity.CENTER);
        upgradeView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(12), AndroidUtilities.dp(16), AndroidUtilities.dp(12));
        upgradeView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(
                AndroidUtilities.dp(12),
                Theme.getColor(Theme.key_windowBackgroundWhite),
                Theme.getColor(Theme.key_listSelector)
        ));
        upgradeView.setOnClickListener(v -> {
            long cost = getUpgradeCost();
            if (coins >= cost) {
                coins -= cost;
                level++;
                saveState();
                updateTexts();
            }
        });
        layout.addView(upgradeView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP, 24, 344, 24, 0));

        TextView helpView = new TextView(context);
        helpView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        helpView.setTextSize(14);
        helpView.setGravity(Gravity.CENTER_HORIZONTAL);
        helpView.setText("Тапай по огню, копи монеты и покупай улучшения.");
        layout.addView(helpView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP, 24, 410, 24, 0));

        updateTexts();
        return fragmentView;
    }

    private void updateTexts() {
        statsView.setText("Уровень: " + level + "\nТапы: " + taps + "\nМонеты: " + coins + "\nМонет за тап: " + getCoinPerTap());
        upgradeView.setText("Купить улучшение (" + getUpgradeCost() + " монет)");
    }

    private long getCoinPerTap() {
        return Math.max(1, level);
    }

    private long getUpgradeCost() {
        return 40L + (long) level * 30L;
    }

    private void loadState() {
        SharedPreferences prefs = MessagesController.getGlobalMainSettings();
        taps = prefs.getLong(PREF_TAPS, 0);
        coins = prefs.getLong(PREF_COINS, 0);
        level = prefs.getInt(PREF_LEVEL, 1);
    }

    private void saveState() {
        SharedPreferences prefs = MessagesController.getGlobalMainSettings();
        prefs.edit()
                .putLong(PREF_TAPS, taps)
                .putLong(PREF_COINS, coins)
                .putInt(PREF_LEVEL, level)
                .apply();
    }
}
