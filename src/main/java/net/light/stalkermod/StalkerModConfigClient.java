package net.light.stalkermod;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

public class StalkerModConfigClient {
    public static double emissionVolume = 1.0;
    public static double anomalyVolume = 1.0;

    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "stalkermod_sounds.properties");

    public static final SimpleOption<Double> EMISSION_VOLUME_OPTION = new SimpleOption<>(
            "stalker.options.emission",
            SimpleOption.emptyTooltip(),
            (text, value) -> Text.literal("Звук выбросов: " + (int)(value * 100.0) + "%"),
            SimpleOption.DoubleSliderCallbacks.INSTANCE,
            emissionVolume,
            value -> {
                emissionVolume = value;
                save();
            }
    );

    public static final SimpleOption<Double> ANOMALY_VOLUME_OPTION = new SimpleOption<>(
            "stalker.options.anomaly",
            SimpleOption.emptyTooltip(),
            (text, value) -> Text.literal("Звук аномалий: " + (int)(value * 100.0) + "%"),
            SimpleOption.DoubleSliderCallbacks.INSTANCE,
            anomalyVolume,
            value -> {
                anomalyVolume = value;
                save();
            }
    );

    public static void load() {
        try {
            if (CONFIG_FILE.exists()) {
                Properties props = new Properties();
                props.load(new FileReader(CONFIG_FILE));
                emissionVolume = Double.parseDouble(props.getProperty("emissionVolume", "1.0"));
                anomalyVolume = Double.parseDouble(props.getProperty("anomalyVolume", "1.0"));

                EMISSION_VOLUME_OPTION.setValue(emissionVolume);
                ANOMALY_VOLUME_OPTION.setValue(anomalyVolume);
            }
        } catch (Exception e) {
            System.err.println("Не удалось загрузить настройки звука Stalker Mod");
        }
    }

    public static void save() {
        try {
            Properties props = new Properties();
            props.setProperty("emissionVolume", String.valueOf(emissionVolume));
            props.setProperty("anomalyVolume", String.valueOf(anomalyVolume));
            props.store(new FileWriter(CONFIG_FILE), "Stalker Mod Sound Config");
        } catch (Exception e) {
            System.err.println("Не удалось сохранить настройки звука Stalker Mod");
        }
    }
}