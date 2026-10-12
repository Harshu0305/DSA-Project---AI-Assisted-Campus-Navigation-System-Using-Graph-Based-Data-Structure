package edu.vitap.navigation.ai;

import edu.vitap.navigation.ai.NavigationContext.TimeOfDay;
import edu.vitap.navigation.ai.NavigationContext.UserRole;
import edu.vitap.navigation.ai.NavigationContext.Weather;

import java.util.*;

public class CampusQueryParser {

    public static class ParsedQuery {
        private final String startNodeId;
        private final String targetNodeId;
        private final NavigationContext context;

        public ParsedQuery(String startNodeId, String targetNodeId, NavigationContext context) {
            this.startNodeId = startNodeId;
            this.targetNodeId = targetNodeId;
            this.context = context;
        }

        public String getStartNodeId() {
            return startNodeId;
        }

        public String getTargetNodeId() {
            return targetNodeId;
        }

        public NavigationContext getContext() {
            return context;
        }

        public boolean isValid() {
            return startNodeId != null && targetNodeId != null && !startNodeId.equals(targetNodeId);
        }
    }

    private static final Map<String, String> ALIAS_MAP = new LinkedHashMap<>();

    static {
        ALIAS_MAP.put("main security gate", "GATE");
        ALIAS_MAP.put("main gate", "GATE");
        ALIAS_MAP.put("security gate", "GATE");
        ALIAS_MAP.put("security", "GATE");
        ALIAS_MAP.put("gate", "GATE");
        ALIAS_MAP.put("central roundabout", "ROUNDABOUT");
        ALIAS_MAP.put("roundabout", "ROUNDABOUT");
        ALIAS_MAP.put("circle", "ROUNDABOUT");

        ALIAS_MAP.put("apj abdul kalam block", "AB2");
        ALIAS_MAP.put("abdul kalam block", "AB2");
        ALIAS_MAP.put("kalam block", "AB2");
        ALIAS_MAP.put("ab-2", "AB2");
        ALIAS_MAP.put("ab2", "AB2");
        ALIAS_MAP.put("kalam", "AB2");
        ALIAS_MAP.put("rock plaza amphitheatre", "ROCK_PLAZA");
        ALIAS_MAP.put("rock plaza", "ROCK_PLAZA");
        ALIAS_MAP.put("amphitheatre", "ROCK_PLAZA");
        ALIAS_MAP.put("student activity center", "SAC");
        ALIAS_MAP.put("student activity centre", "SAC");
        ALIAS_MAP.put("sac building", "SAC");
        ALIAS_MAP.put("sac", "SAC");

        ALIAS_MAP.put("sarvepalli radhakrishnan block", "CB");
        ALIAS_MAP.put("radhakrishnan block", "CB");
        ALIAS_MAP.put("central block", "CB");
        ALIAS_MAP.put("cb", "CB");
        ALIAS_MAP.put("central library", "LIBRARY");
        ALIAS_MAP.put("library", "LIBRARY");
        ALIAS_MAP.put("vit-ap school of business", "VSB");
        ALIAS_MAP.put("school of business", "VSB");
        ALIAS_MAP.put("business school", "VSB");
        ALIAS_MAP.put("vsb", "VSB");
        ALIAS_MAP.put("central crescent", "CRESCENT");
        ALIAS_MAP.put("crescent block", "CRESCENT");
        ALIAS_MAP.put("crescent", "CRESCENT");

        ALIAS_MAP.put("food street", "FOOD_STREET");
        ALIAS_MAP.put("food court", "FOOD_STREET");
        ALIAS_MAP.put("canteen", "FOOD_STREET");
        ALIAS_MAP.put("mess", "FOOD_STREET");
        ALIAS_MAP.put("gazebo", "FOOD_STREET");
        ALIAS_MAP.put("basketball court", "BB_COURT");
        ALIAS_MAP.put("basketball", "BB_COURT");
        ALIAS_MAP.put("sports ground", "SPORTS_GROUND");
        ALIAS_MAP.put("sports triangle", "SPORTS_GROUND");
        ALIAS_MAP.put("cricket ground", "SPORTS_GROUND");
        ALIAS_MAP.put("ground", "SPORTS_GROUND");

        ALIAS_MAP.put("ladies hostel 1", "LH1");
        ALIAS_MAP.put("ladies hostel 3", "LH3");
        ALIAS_MAP.put("lh-1", "LH1");
        ALIAS_MAP.put("lh-3", "LH3");
        ALIAS_MAP.put("lh1", "LH1");
        ALIAS_MAP.put("lh3", "LH3");
        ALIAS_MAP.put("ladies hostel", "LH3");
        ALIAS_MAP.put("lh", "LH3");

        ALIAS_MAP.put("mens hostel 1", "MH1");
        ALIAS_MAP.put("mens hostel 2", "MH2");
        ALIAS_MAP.put("mens hostel 3", "MH3");
        ALIAS_MAP.put("mens hostel 4", "MH4");
        ALIAS_MAP.put("mens hostel 5", "MH5");
        ALIAS_MAP.put("mens hostel 6", "MH6");
        ALIAS_MAP.put("mh-1", "MH1");
        ALIAS_MAP.put("mh-2", "MH2");
        ALIAS_MAP.put("mh-3", "MH3");
        ALIAS_MAP.put("mh-4", "MH4");
        ALIAS_MAP.put("mh-5", "MH5");
        ALIAS_MAP.put("mh-6", "MH6");
        ALIAS_MAP.put("mh1", "MH1");
        ALIAS_MAP.put("mh2", "MH2");
        ALIAS_MAP.put("mh3", "MH3");
        ALIAS_MAP.put("mh4", "MH4");
        ALIAS_MAP.put("mh5", "MH5");
        ALIAS_MAP.put("mh6", "MH6");
        ALIAS_MAP.put("mens hostel", "MH1");
        ALIAS_MAP.put("mh", "MH1");
    }

    public static ParsedQuery parse(String rawInput) {
        if (rawInput == null || rawInput.trim().isEmpty()) {
            return new ParsedQuery(null, null, NavigationContext.standard());
        }

        String query = rawInput.toLowerCase().replaceAll("[^a-z0-9\\s\\-]", " ").replaceAll("\\s+", " ").trim();

        // 1. Accessibility detection
        boolean avoidStairs = query.contains("stair") || query.contains("step") ||
                query.contains("crutch") || query.contains("wheelchair") ||
                query.contains("ramp") || query.contains("lift") ||
                query.contains("accessible");

        // 2. Weather detection
        Weather weather = Weather.CLEAR;
        if (query.contains("rain") || query.contains("wet") || query.contains("monsoon") ||
                query.contains("umbrella") || query.contains("covered") || query.contains("dry")) {
            weather = Weather.RAIN;
        } else if (query.contains("sun") || query.contains("heat") || query.contains("hot") ||
                query.contains("afternoon") || query.contains("shade")) {
            weather = Weather.EXTREME_SUN;
        }

        // 3. Timing detection
        TimeOfDay timeOfDay = TimeOfDay.REGULAR;
        if (query.contains("rush") || query.contains("crowd") || query.contains("peak") ||
                query.contains("slot change") || query.contains("class break")) {
            timeOfDay = TimeOfDay.RUSH_HOUR;
        } else if (query.contains("night") || query.contains("dark") || query.contains("evening") ||
                query.contains("late")) {
            timeOfDay = TimeOfDay.NIGHT;
        }

        // 4. Role detection
        UserRole role = UserRole.STAFF_FACULTY;
        if (query.contains("boy") || query.contains("male") || query.contains("guy")) {
            role = UserRole.MALE_STUDENT;
        } else if (query.contains("girl") || query.contains("female") || query.contains("lady")) {
            role = UserRole.FEMALE_STUDENT;
        }

        NavigationContext context = new NavigationContext(weather, timeOfDay, avoidStairs, role);

        // 5. Source & Target detection
        String startId = null;
        String targetId = null;

        if (query.contains("from ") && query.contains(" to ")) {
            int fromIdx = query.indexOf("from ") + 5;
            int toIdx = query.indexOf(" to ", fromIdx);

            if (toIdx > fromIdx) {
                String fromSub = query.substring(fromIdx, toIdx).trim();
                String toSub = query.substring(toIdx + 4).trim();
                startId = matchLongestAlias(fromSub);
                targetId = matchLongestAlias(toSub);
            }
        }

        if (startId == null || targetId == null) {
            List<String> matched = extractSequentialMatches(query);
            if (matched.size() >= 2) {
                startId = matched.get(0);
                targetId = matched.get(1);
            } else if (matched.size() == 1 && startId == null) {
                startId = matched.get(0);
            }
        }

        return new ParsedQuery(startId, targetId, context);
    }

    private static String matchLongestAlias(String text) {
        for (Map.Entry<String, String> entry : ALIAS_MAP.entrySet()) {
            if (text.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static List<String> extractSequentialMatches(String query) {
        List<String> results = new ArrayList<>();
        String workingText = query;

        for (Map.Entry<String, String> entry : ALIAS_MAP.entrySet()) {
            String alias = entry.getKey();
            if (workingText.contains(alias)) {
                String nodeId = entry.getValue();
                if (!results.contains(nodeId)) {
                    results.add(nodeId);
                }
                workingText = workingText.replaceFirst(alias, " ");
            }
        }
        return results;
    }
}