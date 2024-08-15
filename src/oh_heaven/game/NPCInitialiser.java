package oh_heaven.game;

import java.util.ArrayList;

public class NPCInitialiser {
    private static ArrayList<NPC> npcs = new ArrayList<>();

    public static ArrayList<NPC> getNPCs() {
        NPC randomNPC = new RandomNPC();  // initialise new NPCs here and add them to the NPC array list
        NPC legalNPC = new LegalNPC();
        NPC smartNPC = new SmartNPC();
        npcs.add(randomNPC);
        npcs.add(legalNPC);
        npcs.add(smartNPC);
        return npcs; }
}
