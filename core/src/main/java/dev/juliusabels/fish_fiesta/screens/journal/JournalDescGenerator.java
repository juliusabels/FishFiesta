package dev.juliusabels.fish_fiesta.screens.journal;

import dev.juliusabels.fish_fiesta.game.CreatureSize;
import dev.juliusabels.fish_fiesta.game.WaterCreature;
import dev.juliusabels.fish_fiesta.game.features.WaterSubtype;
import dev.juliusabels.fish_fiesta.game.features.WaterTemperature;
import dev.juliusabels.fish_fiesta.game.features.WaterType;

import java.util.List;

public class JournalDescGenerator {

    public static String generateFor(WaterCreature waterCreature) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(waterCreature.getDescription()).append(" ");
        CreatureSize size = waterCreature.getSize();
        List<WaterType> type = waterCreature.getWaterTypes();
        List<WaterSubtype> waterSubtypes = waterCreature.getWaterSubtypes();
        List<WaterTemperature> waterTemperatures = waterCreature.getWaterTemperatures();

        if (size.isValid()) {
            if (size.rangeStart() == size.rangeEnd()) {
                buffer.append("It's around ")
                    .append(size.rangeStart())
                    .append("cm large, making it a ")
                    .append(size.getCategoriesFromSize().getFormattedForDesc())
                    .append(" fish. ");
            } else {
                buffer.append("It's size ranges from ")
                    .append(size.rangeStart())
                    .append(" cm to ")
                    .append(size.rangeEnd())
                    .append(" cm, making it a ")
                    .append(size.getCategoriesFromSize().getFormattedForDesc())
                    .append(" fish. ");
            }
        }

        return buffer.toString();
    }
}
