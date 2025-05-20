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
        List<WaterType> waterTypes = waterCreature.getWaterTypes();
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

        if (!waterTypes.isEmpty()) {
            buffer.append("The ").append(waterCreature.getName().toLowerCase()).append(" can be found in ");

            if (waterTypes.size() == 1) {
                buffer.append(waterTypes.getFirst().toString().toLowerCase())
                    .append(" water ");
            } else {
                buffer.append("both ")
                    .append(waterTypes.get(0).toString().toLowerCase())
                    .append(" and ")
                    .append(waterTypes.get(1).toString().toLowerCase())
                    .append(" water ");
            }
        }

        if (!waterSubtypes.isEmpty()) {
            buffer.append("and it typically inhabits ");
            int listSize = waterSubtypes.size();

            if (listSize == 1) {
                buffer.append(waterSubtypes.getFirst().getFormattedForDesc()).append(". ");
            } else {
                for (int i = 0; i < listSize; i++) {
                    if (i > 0) {
                        if (i == listSize - 1) {
                            buffer.append(" and ");
                        } else {
                            buffer.append(", ");
                        }
                    }
                    buffer.append(waterSubtypes.get(i).getFormattedForDesc());
                }
                buffer.append(". ");
            }
        }

        if (!waterTemperatures.isEmpty()) {
            buffer.append("In addition this fish ");
            int tempSize = waterTemperatures.size();

            if (tempSize == 3) {
                buffer.append("enjoys any type of water temperature. ");
            } else {
                buffer.append("prefers ");

                if (tempSize == 1) {
                    buffer.append(waterTemperatures.getFirst().toString().toLowerCase())
                        .append(" waters. ");
                } else { // tempSize == 2
                    buffer.append("both ")
                        .append(waterTemperatures.get(0).toString().toLowerCase())
                        .append(" and ")
                        .append(waterTemperatures.get(1).toString().toLowerCase())
                        .append(" waters. ");
                }
            }
        }

        return buffer.toString();
    }
}
