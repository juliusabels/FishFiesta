package dev.juliusabels.fish_fiesta.screens.journal;

import dev.juliusabels.fish_fiesta.game.CreatureSize;
import dev.juliusabels.fish_fiesta.game.WaterCreature;
import dev.juliusabels.fish_fiesta.game.features.WaterSubtype;
import dev.juliusabels.fish_fiesta.game.features.WaterTemperature;
import dev.juliusabels.fish_fiesta.game.features.WaterType;

import java.util.List;

/**
 * Generates detailed descriptions for fishes in the journal.
 * <p>
 * This utility class automatically creates readable, informative descriptions
 * by combining the creature's basic description with formatted details about:
 * <ul>
 *   <li>Size and classification</li>
 *   <li>Water type preferences (salt or fresh water)</li>
 *   <li>Habitats (subtypes of water environments)</li>
 *   <li>Temperature preferences</li>
 * </ul>
 * <p>
 * The descriptions are dynamically generated based on the available data for each creature,
 * ensuring consistent formatting and natural-sounding text.
 */
public class JournalDescGenerator {

    /**
     * Generates a formatted description for the specified water creature.
     *
     * @param waterCreature The water creature to generate a description for
     * @return A complete, formatted description string
     */
    public static String generateFor(WaterCreature waterCreature) {
        StringBuilder buffer = new StringBuilder();

        // Start with the basic description
        buffer.append(waterCreature.getDescription()).append(" ");

        // Add size information
        appendSizeDescription(buffer, waterCreature.getSize());

        // Add water type and habitat information
        appendWaterTypeDescription(buffer, waterCreature);
        appendTemperatureDescription(buffer, waterCreature.getWaterTemperatures());

        return buffer.toString();
    }

    /**
     * Appends size information to the description.
     *
     * @param buffer The string builder to append to
     * @param size The creature's size information
     */
    private static void appendSizeDescription(StringBuilder buffer, CreatureSize size) {
        if (!size.isValid()) {
            return;
        }

        if (size.rangeStart() == size.rangeEnd()) {
            buffer.append("It's around ")
                .append(size.rangeStart())
                .append("cm large, making it a ")
                .append(size.getCategory().getFormattedForDesc())
                .append(" fish. ");
        } else {
            buffer.append("It's size ranges from ")
                .append(size.rangeStart())
                .append(" cm to ")
                .append(size.rangeEnd())
                .append(" cm, making it a ")
                .append(size.getCategory().getFormattedForDesc())
                .append(" fish. ");
        }
    }

    /**
     * Appends water type and habitat information to the description.
     *
     * @param buffer The string builder to append to
     * @param creature The water creature with type and subtype data
     */
    private static void appendWaterTypeDescription(StringBuilder buffer, WaterCreature creature) {
        List<WaterType> waterTypes = creature.getWaterTypes();
        List<WaterSubtype> waterSubtypes = creature.getWaterSubtypes();

        // Skip if no water type data
        if (waterTypes.isEmpty()) {
            return;
        }

        // Add water type information
        buffer.append("The ").append(creature.getName().toLowerCase()).append(" can be found in ");

        if (waterTypes.size() == 1) {
            buffer.append(waterTypes.getFirst().toString().toLowerCase()).append(" water ");
        } else {
            buffer.append("both fresh and salt water ");
        }

        // Add water subtype information if available
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
    }

    /**
     * Appends temperature preference information to the description.
     *
     * @param buffer The string builder to append to
     * @param temperatures The list of temperature preferences
     */
    private static void appendTemperatureDescription(StringBuilder buffer,
                                                     List<WaterTemperature> temperatures) {
        if (temperatures.isEmpty()) {
            return;
        }

        buffer.append("In addition this fish ");
        int tempSize = temperatures.size();

        if (tempSize == 3) {
            buffer.append("enjoys any type of water temperature. ");
        } else {
            buffer.append("prefers ");

            if (tempSize == 1) {
                buffer.append(temperatures.getFirst().toString().toLowerCase())
                    .append(" water temperatures. ");
            } else {
                buffer.append(temperatures.get(0).toString().toLowerCase())
                    .append(" and ")
                    .append(temperatures.get(1).toString().toLowerCase())
                    .append(" water temperatures. ");
            }
        }
    }
}
