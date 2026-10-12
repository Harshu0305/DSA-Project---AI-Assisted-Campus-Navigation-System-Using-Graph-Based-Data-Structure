package edu.vitap.navigation.graph;

import edu.vitap.navigation.model.Node;
import edu.vitap.navigation.model.Node.Zone;

public class CampusDataLoader {

    public static CampusGraph loadVITAPGraph() {
        CampusGraph graph = new CampusGraph();

        // -------------------------------------------------------------
        // 1. VERTICES (Calibrated with satellite geometry & security zones)
        // -------------------------------------------------------------

        // North & Central Boulevard (COMMON)
        graph.addNode(new Node("GATE", "Main Gate & Security", 0, 0, "GATE", Zone.COMMON));
        graph.addNode(new Node("ROUNDABOUT", "Central Roundabout", 0, 210, "JUNCTION", Zone.COMMON));

        // Academic Core (COMMON)
        graph.addNode(new Node("AB2", "Dr. APJ Abdul Kalam Block (AB-2)", -130, 240, "ACADEMIC", Zone.COMMON));
        graph.addNode(new Node("ROCK_PLAZA", "Rock Plaza Amphitheatre", -130, 290, "AMENITY", Zone.COMMON));
        graph.addNode(new Node("SAC", "SAC Building", -130, 340, "AMENITY", Zone.COMMON));
        graph.addNode(new Node("LIBRARY", "VIT-AP Central Library", 90, 230, "ACADEMIC", Zone.COMMON));
        graph.addNode(new Node("CB", "Central Block (Sarvepalli Radhakrishnan)", 150, 250, "ACADEMIC", Zone.COMMON));
        graph.addNode(new Node("VSB", "School of Business (VSB)", 230, 220, "ACADEMIC", Zone.COMMON));
        graph.addNode(new Node("CRESCENT", "Central Crescent Academic Block", 0, 330, "ACADEMIC", Zone.COMMON));

        // Central Dining & Sports Triangle (COMMON)
        graph.addNode(new Node("FOOD_STREET", "Food Street & Dining Hub", 10, 425, "AMENITY", Zone.COMMON));
        graph.addNode(new Node("BB_COURT", "Basketball Courts & North Sports Corner", -90, 390, "SPORTS", Zone.COMMON));
        graph.addNode(new Node("SPORTS_GROUND", "Sports Ground / Cricket Oval", -130, 490, "SPORTS", Zone.COMMON));

        // Ladies Hostel Zone (Restricted to FEMALE_STUDENT & STAFF)
        graph.addNode(new Node("LH3", "Ladies Hostel Block 3", -120, 560, "HOSTEL", Zone.LADIES_HOSTEL));
        graph.addNode(new Node("LH1", "Ladies Hostel Block 1", -170, 620, "HOSTEL", Zone.LADIES_HOSTEL));

        // Men's Hostel Zone (Restricted to MALE_STUDENT & STAFF)
        graph.addNode(new Node("MH1", "Men's Hostel 1", 180, 440, "HOSTEL", Zone.MENS_HOSTEL));
        graph.addNode(new Node("MH5", "Men's Hostel 5", 230, 420, "HOSTEL", Zone.MENS_HOSTEL));
        graph.addNode(new Node("MH2", "Men's Hostel 2", 270, 470, "HOSTEL", Zone.MENS_HOSTEL));
        graph.addNode(new Node("MH6", "Men's Hostel 6", 160, 580, "HOSTEL", Zone.MENS_HOSTEL));
        graph.addNode(new Node("MH4", "Men's Hostel 4", 260, 600, "HOSTEL", Zone.MENS_HOSTEL));
        graph.addNode(new Node("MH3", "Men's Hostel 3", 330, 570, "HOSTEL", Zone.MENS_HOSTEL));

        // -------------------------------------------------------------
        // 2. EDGES (Strictly bypassing the red off-limits construction zone)
        // -------------------------------------------------------------

        // Main Boulevard
        graph.addEdge("GATE", "ROUNDABOUT", 210, false, false);

        // Academic Routes
        graph.addEdge("ROUNDABOUT", "AB2", 140, false, false);
        graph.addEdge("ROUNDABOUT", "LIBRARY", 105, false, false);
        graph.addEdge("ROUNDABOUT", "CRESCENT", 120, false, false);
        graph.addEdge("LIBRARY", "CB", 65, false, true); // Covered skyway
        graph.addEdge("CB", "VSB", 85, false, false);
        graph.addEdge("CRESCENT", "CB", 160, false, true); // Covered arcade

        // Rock Plaza steps vs ramp
        graph.addEdge("AB2", "ROCK_PLAZA", 50, true, false); // Stairs
        graph.addEdge("AB2", "SAC", 110, false, false); // Accessible ramp perimeter
        graph.addEdge("ROCK_PLAZA", "SAC", 50, true, false); // Stairs
        graph.addEdge("CRESCENT", "SAC", 130, false, false);

        // Spine to Dining & Sports Triangle
        graph.addEdge("CRESCENT", "FOOD_STREET", 95, false, false);
        graph.addEdge("SAC", "BB_COURT", 65, false, false);
        graph.addEdge("BB_COURT", "FOOD_STREET", 110, false, false);
        graph.addEdge("BB_COURT", "SPORTS_GROUND", 110, false, false);

        // Paved Western Route to Ladies Hostels (Avoids central construction
        // completely)
        graph.addEdge("SPORTS_GROUND", "LH3", 80, false, false);
        graph.addEdge("LH3", "LH1", 80, false, false);

        // Eastern Perimeter Route to Men's Hostels (Avoids central construction
        // completely)
        graph.addEdge("CRESCENT", "MH1", 210, false, false);
        graph.addEdge("FOOD_STREET", "MH1", 170, false, false);
        graph.addEdge("MH1", "MH5", 55, false, false);
        graph.addEdge("MH5", "MH2", 65, false, false);
        graph.addEdge("MH1", "MH6", 145, false, false); // North-South perimeter bypass
        graph.addEdge("MH6", "MH4", 105, false, false);
        graph.addEdge("MH4", "MH3", 75, false, false);
        graph.addEdge("MH2", "MH3", 120, false, false);

        return graph;
    }
}