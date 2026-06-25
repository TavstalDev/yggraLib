package io.github.tavstaldev.yggra.core.gui;

import java.util.ArrayList;
import java.util.List;

public class SlotParser {
    public static List<Integer> parse(List<String> rawSlots) {
        List<Integer> slots = new ArrayList<>();
        for (String rawNum : rawSlots) {
            if (!rawNum.contains("-")) {
                try {
                    slots.add(Integer.parseInt(rawNum));
                } catch (Exception e) {
                    // ignored
                }
                continue;
            }

            String[] rangeParts = rawNum.split("\\s*-\\s*", 2);
            if (rangeParts.length != 2)
                continue;

            try {
                int start = Integer.parseInt(rangeParts[0]);
                int end = Integer.parseInt(rangeParts[1]);
                if (start > end)
                    continue;
                for (int i = start; i <= end; i++) {
                    slots.add(i);
                }
            } catch (Exception e) {
                // ignored
            }
        }

        return slots;
    }
}
