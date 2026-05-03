package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.RecyclerListView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class AlyGramSettingsActivity extends BaseFragment {

    private static final String KEY_SHOW_ID = "alygram_show_user_id";
    private static final String KEY_FOUNDER_VISUALS = "alygram_founder_visuals";
    private static final String KEY_AUTO_CHANNEL = "alygram_auto_channel";
    private static final String KEY_WALLPAPER_DONE = "alygram_wallpaper_applied";
    private static final String KEY_GAME_TAPS = "alygram_tap_game_taps";
    private static final String KEY_GAME_COINS = "alygram_tap_game_coins";
    private static final String KEY_GAME_LEVEL = "alygram_tap_game_level";

    private RecyclerListView listView;
    private ListAdapter adapter;
    private final ArrayList<Row> rows = new ArrayList<>();

    private SharedPreferences prefs;

    @Override
    public View createView(Context context) {
        prefs = MessagesController.getGlobalMainSettings();

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("AlyGram Settings");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listView = new RecyclerListView(context);
        listView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(adapter = new ListAdapter());
        buildRows();
        listView.setOnItemClickListener(this::onItemClick);

        fragmentView = listView;
        return fragmentView;
    }

    private void buildRows() {
        rows.clear();
        rows.add(new Row(0, "Бренд и визуал", 0));
        rows.add(new Row(1, "ID always visible", 0));
        rows.add(new Row(2, "Founder visuals", 0));
        rows.add(new Row(3, "Авто-открытие канала", 0));
        rows.add(new Row(4, null, 0));
        rows.add(new Row(5, "Быстрые действия", 0));
        rows.add(new Row(6, "Применить wallaperaly1 как дефолт", 0));
        rows.add(new Row(7, "Открыть канал @drobashnikovlife", 0));
        rows.add(new Row(8, "Открыть профиль Founder", 0));
        rows.add(new Row(9, "Открыть мини-игру Fire Clicker", 0));
        rows.add(new Row(10, "Сбросить прогресс Fire Clicker", 0));
        rows.add(new Row(11, "Сбросить примененные AlyGram флаги", 0));
        rows.add(new Row(12, "Визуал AlyGram ближе к iPhone стилю", 0));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void onItemClick(View view, int position, float x, float y) {
        if (position < 0 || position >= rows.size()) {
            return;
        }
        Row row = rows.get(position);
        if (row.id == 1) {
            boolean value = !prefs.getBoolean(KEY_SHOW_ID, true);
            prefs.edit().putBoolean(KEY_SHOW_ID, value).apply();
            ((TextCheckCell) view).setChecked(value);
        } else if (row.id == 2) {
            boolean value = !prefs.getBoolean(KEY_FOUNDER_VISUALS, true);
            prefs.edit().putBoolean(KEY_FOUNDER_VISUALS, value).apply();
            ((TextCheckCell) view).setChecked(value);
        } else if (row.id == 3) {
            boolean value = !prefs.getBoolean(KEY_AUTO_CHANNEL, true);
            prefs.edit().putBoolean(KEY_AUTO_CHANNEL, value).apply();
            ((TextCheckCell) view).setChecked(value);
        } else if (row.id == 6) {
            applyDefaultWallpaper();
        } else if (row.id == 7) {
            getMessagesController().openByUserName("drobashnikovlife", this, 1);
        } else if (row.id == 8) {
            Bundle args = new Bundle();
            args.putLong("user_id", 780682804L);
            presentFragment(new ProfileActivity(args));
        } else if (row.id == 9) {
            presentFragment(new AlyGramTapGameActivity());
        } else if (row.id == 10) {
            prefs.edit()
                    .putLong(KEY_GAME_TAPS, 0)
                    .putLong(KEY_GAME_COINS, 0)
                    .putInt(KEY_GAME_LEVEL, 1)
                    .apply();
        } else if (row.id == 11) {
            prefs.edit()
                    .putBoolean(KEY_WALLPAPER_DONE, false)
                    .putBoolean("alygram_founder_channel_opened", false)
                    .apply();
        }
    }

    private void applyDefaultWallpaper() {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getParentActivity().getResources(), R.drawable.theme_preview_image);
            if (bitmap == null) {
                return;
            }
            File wallpaperFile = new File(org.telegram.messenger.ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg");
            File wallpaperOriginalFile = new File(org.telegram.messenger.ApplicationLoader.getFilesDirFixed(), "wallpaper_original.jpg");
            try (FileOutputStream out = new FileOutputStream(wallpaperFile);
                 FileOutputStream outOriginal = new FileOutputStream(wallpaperOriginalFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, outOriginal);
            }
            prefs.edit()
                    .putBoolean("overrideThemeWallpaper", true)
                    .putLong("selectedBackground2", 1111111L)
                    .putString("selectedBackgroundSlug", "")
                    .putBoolean(KEY_WALLPAPER_DONE, true)
                    .apply();
            Theme.reloadWallpaper(true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private static class Row {
        final int id;
        final String text;
        final int icon;
        Row(int id, String text, int icon) {
            this.id = id;
            this.text = text;
            this.icon = icon;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        @Override
        public int getItemCount() {
            return rows.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 1 || type == 2;
        }

        @Override
        public int getItemViewType(int position) {
            int id = rows.get(position).id;
            if (id == 0 || id == 5) return 0;
            if (id == 4 || id == 12) return 3;
            if (id >= 1 && id <= 3) return 1;
            return 2;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = new HeaderCell(parent.getContext());
                view.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 1) {
                view = new TextCheckCell(parent.getContext());
                view.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 2) {
                view = new TextSettingsCell(parent.getContext());
                view.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
            } else {
                view = new TextInfoPrivacyCell(parent.getContext());
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Row row = rows.get(position);
            int type = holder.getItemViewType();
            if (type == 0) {
                ((HeaderCell) holder.itemView).setText(row.text);
            } else if (type == 1) {
                TextCheckCell cell = (TextCheckCell) holder.itemView;
                boolean checked = row.id == 1 ? prefs.getBoolean(KEY_SHOW_ID, true)
                        : row.id == 2 ? prefs.getBoolean(KEY_FOUNDER_VISUALS, true)
                        : prefs.getBoolean(KEY_AUTO_CHANNEL, true);
                cell.setTextAndCheck(row.text, checked, true);
            } else if (type == 2) {
                ((TextSettingsCell) holder.itemView).setText(row.text, true);
            } else {
                TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                cell.setText("AlyGram-переключатели применяются сразу. Для полного обновления визуала перезапусти приложение.");
                cell.setBackground(Theme.getThemedDrawableByKey(holder.itemView.getContext(), R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            }
        }
    }
}
