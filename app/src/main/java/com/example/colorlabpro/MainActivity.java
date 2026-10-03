package com.example.colorlabpro;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private View root;
    private TextView tvHex;
    private Button btnFav, btnTheme;
    private GridLayout gridPalette;
    private ListView listFav;

    private String selectedHex = "#2196F3";

    private SharedPreferences prefs;
    private static final String PREFS = "colorlab_prefs";
    private static final String KEY_THEME = "theme_color";
    private static final String KEY_FAVS = "favorites";

    private ArrayList<String> favList = new ArrayList<>();
    private ArrayAdapter<String> favAdapter;


    private final String[] palette = {
            "#F44336",
            "#E91E63",
            "#9C27B0",
            "#673AB7",
            "#3F51B5",
            "#2196F3",
            "#03A9F4",
            "#00BCD4",
            "#009688",
            "#4CAF50",
            "#FFC107",
            "#FF5722",

            "#B71C1C",
            "#880E4F",
            "#4A148C",
            "#311B92",
            "#1A237E",
            "#0D47A1",
            "#01579B",
            "#006064",
            "#004D40",
            "#1B5E20",
            "#F57F17",
            "#BF360C"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        root = findViewById(R.id.main);
        tvHex = findViewById(R.id.tvHex);
        btnFav = findViewById(R.id.btnFav);
        btnTheme = findViewById(R.id.btnTheme);
        gridPalette = findViewById(R.id.gridPalette);
        listFav = findViewById(R.id.listFav);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // 1) Cargar tema guardado
        selectedHex = prefs.getString(KEY_THEME, selectedHex);
        aplicarColorAUi(selectedHex);

        // 2) Cargar favoritos
        cargarFavoritos();
        favAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favList);
        listFav.setAdapter(favAdapter);

        // 3) Crear paleta de colores
        crearPaleta();

        // 4) Guardar en favoritos
        btnFav.setOnClickListener(v -> agregarAFavoritos(selectedHex));

        // 5) Aplicar tema (guardar color principal)
        btnTheme.setOnClickListener(v -> prefs.edit().putString(KEY_THEME, selectedHex).apply());

        // 6) Tocar favorito -> aplicarlo
        listFav.setOnItemClickListener((parent, view, position, id) -> {
            selectedHex = favList.get(position);
            aplicarColorAUi(selectedHex);
        });
    }


    private void crearPaleta() {
        gridPalette.removeAllViews();

        int size = (int) (getResources().getDisplayMetrics().density * 64); // 64dp
        int margin = (int) (getResources().getDisplayMetrics().density * 8); // 8dp

        for (String hex : palette) {
            View swatch = new View(this);
            swatch.setBackgroundColor(Color.parseColor(hex));

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = size;
            params.height = size;
            params.setMargins(margin, margin, margin, margin);
            swatch.setLayoutParams(params);

            swatch.setOnClickListener(v -> {
                selectedHex = hex;
                aplicarColorAUi(selectedHex);
            });

            gridPalette.addView(swatch);
        }
    }

    private void aplicarColorAUi(String hex) {
        tvHex.setText(hex);
        root.setBackgroundColor(Color.parseColor(hex));

        int textColor = esColorOscuro(hex) ? Color.WHITE : Color.BLACK;
        tvHex.setTextColor(textColor);
    }

    private boolean esColorOscuro(String hex) {
        int c = Color.parseColor(hex);
        int r = Color.red(c), g = Color.green(c), b = Color.blue(c);
        double luminancia = (0.299 * r + 0.587 * g + 0.114 * b);
        return luminancia < 140;
    }

    private void cargarFavoritos() {
        Set<String> set = prefs.getStringSet(KEY_FAVS, new LinkedHashSet<>());
        favList.clear();
        favList.addAll(set);
    }

    private void guardarFavoritos() {
        Set<String> set = new LinkedHashSet<>(favList);
        prefs.edit().putStringSet(KEY_FAVS, set).apply();
    }

    private void agregarAFavoritos(String hex) {
        if (!favList.contains(hex)) {
            favList.add(0, hex);
            if (favList.size() > 10) favList.remove(favList.size() - 1);
            guardarFavoritos();
            favAdapter.notifyDataSetChanged();
        }
    }
}