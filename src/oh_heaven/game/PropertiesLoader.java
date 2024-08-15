package oh_heaven.game;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class PropertiesLoader {
    public static final String DEFAULT_DIRECTORY_PATH = "properties/";
    public static Properties loadPropertiesFile(String propertiesFile) {
        if (propertiesFile == null) {
            try (InputStream input = new FileInputStream( DEFAULT_DIRECTORY_PATH + "runmode.properties")) {

                Properties prop = new Properties();

                // load a properties file
                prop.load(input);

                propertiesFile = DEFAULT_DIRECTORY_PATH + prop.getProperty("current_mode");
                System.out.println(propertiesFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        try (InputStream input = new FileInputStream(propertiesFile)) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            return prop;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void loadPlayers(Properties properties) {
        ArrayList<NPC> npcArray = NPCInitialiser.getNPCs();
        for (int i=0; i < Oh_Heaven.nbPlayers; i++) {
            // default to random NPC players if no npc type is provided
            if (properties.getProperty("players." + i).isEmpty()) {
                for (NPC npc : npcArray) {
                    if (npc.getNPCName().equals("random")) {
                        Oh_Heaven.addPlayer(i, new Player(npc, i));
                    }
                }
            }
            // check if the player is a human player
            else if (properties.getProperty("players." + i).equals("human")) {
                Oh_Heaven.addPlayer(i, new Player(null, i));
            } else {
                // otherwise, find the npc type specified in the properties file and create a new player who will use this npc type
                for (NPC npc : npcArray) {
                    if (properties.getProperty("players." + i).equals(npc.getNPCName())) {
                        Oh_Heaven.addPlayer(i, new Player(npc, i));
                        break;
                    }
                }
            }

        }
    }

    public static void loadEnforceRule(Properties properties) {
        if (!properties.getProperty("enforceRules").isEmpty()) {
            Round.setEnforceRules(Boolean.parseBoolean(properties.getProperty("enforceRules")));
        }
    }

    public static void loadNbStartCards(Properties properties) {
        if (!properties.getProperty("nbStartCards").isEmpty()) {
            Round.setNbStartCards(Integer.parseInt(properties.getProperty("nbStartCards")));
        }
    }

    public static void loadNbRounds(Properties properties) {
        if (!properties.getProperty("rounds").isEmpty()) {
                Oh_Heaven.setNbRounds(Integer.parseInt(properties.getProperty("rounds")));
        }
    }

    public static void loadSeed(Properties properties) {
        if (!properties.getProperty("seed").isEmpty()) {
            Oh_Heaven.setSeed(Integer.parseInt(properties.getProperty("seed")));
        }
    }

    public static void loadProperties(Properties properties){
        loadPlayers(properties);
        loadEnforceRule(properties);
        loadNbStartCards(properties);
        loadNbRounds(properties);
        loadSeed(properties);
    }
}