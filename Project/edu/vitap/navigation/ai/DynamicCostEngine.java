package edu.vitap.navigation.ai;

import edu.vitap.navigation.ai.NavigationContext.UserRole;
import edu.vitap.navigation.algorithm.EdgeWeightProvider;
import edu.vitap.navigation.model.Edge;
import edu.vitap.navigation.model.Node.Zone;

public class DynamicCostEngine implements EdgeWeightProvider {
    private final NavigationContext context;

    public DynamicCostEngine(NavigationContext context) {
        this.context = context;
    }

    @Override
    public double getCost(Edge edge) {
        Zone targetZone = edge.getTarget().getZone();
        UserRole role = context.getUserRole();

        // 1. Gender-based hostel security enforcement
        if (role == UserRole.MALE_STUDENT && targetZone == Zone.LADIES_HOSTEL) {
            return Double.POSITIVE_INFINITY;
        }
        if (role == UserRole.FEMALE_STUDENT && targetZone == Zone.MENS_HOSTEL) {
            return Double.POSITIVE_INFINITY;
        }

        // 2. Physical accessibility filter
        if (context.isAvoidStairs() && edge.hasStairs()) {
            return Double.POSITIVE_INFINITY;
        }

        double cost = edge.getBaseDistance();

        // 3. Environmental weather modifier
        switch (context.getWeather()) {
            case RAIN:
                if (!edge.isIndoor()) {
                    cost *= 2.5; // Uncovered paths penalized heavily in rain
                } else {
                    cost *= 0.85; // Encourages covered skyways
                }
                break;
            case EXTREME_SUN:
                if (edge.getSource().getCategory().equals("SPORTS")
                        || edge.getTarget().getCategory().equals("SPORTS")) {
                    cost *= 2.0; // Open fields offer zero shade
                } else if (!edge.isIndoor()) {
                    cost *= 1.4;
                }
                break;
            case CLEAR:
            default:
                break;
        }

        // 4. Class transition rush hour crowd penalty
        if (context.getTimeOfDay() == NavigationContext.TimeOfDay.RUSH_HOUR) {
            if (edge.getSource().getCategory().equals("ACADEMIC")
                    || edge.getTarget().getCategory().equals("ACADEMIC")) {
                cost *= 1.5;
            }
        }

        return cost;
    }
}