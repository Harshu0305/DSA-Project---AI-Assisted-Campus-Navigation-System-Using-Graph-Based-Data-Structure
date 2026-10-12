package edu.vitap.navigation.ai;

public class NavigationContext {

    public enum Weather {
        CLEAR,
        RAIN, // Open-air paths penalized; prioritizes covered corridors
        EXTREME_SUN // Penalizes long unshaded routes (e.g., across sports ground)
    }

    public enum TimeOfDay {
        REGULAR,
        RUSH_HOUR, // Academic slot changes & dining rush
        NIGHT // Late evening
    }

    public enum UserRole {
        MALE_STUDENT, // Restricted from LADIES_HOSTEL zone
        FEMALE_STUDENT, // Restricted from MENS_HOSTEL zone
        STAFF_FACULTY // Unrestricted access
    }

    private final Weather weather;
    private final TimeOfDay timeOfDay;
    private final boolean avoidStairs;
    private final UserRole userRole;

    public NavigationContext(Weather weather, TimeOfDay timeOfDay, boolean avoidStairs, UserRole userRole) {
        this.weather = weather != null ? weather : Weather.CLEAR;
        this.timeOfDay = timeOfDay != null ? timeOfDay : TimeOfDay.REGULAR;
        this.avoidStairs = avoidStairs;
        this.userRole = userRole != null ? userRole : UserRole.STAFF_FACULTY;
    }

    public static NavigationContext standard() {
        return new NavigationContext(Weather.CLEAR, TimeOfDay.REGULAR, false, UserRole.STAFF_FACULTY);
    }

    public Weather getWeather() {
        return weather;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public boolean isAvoidStairs() {
        return avoidStairs;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    @Override
    public String toString() {
        return String.format("[Role: %s | Weather: %s | Timing: %s | Avoid Stairs: %b]",
                userRole, weather, timeOfDay, avoidStairs);
    }
}