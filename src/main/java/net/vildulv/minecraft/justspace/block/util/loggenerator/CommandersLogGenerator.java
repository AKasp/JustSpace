package net.vildulv.minecraft.justspace.block.util.loggenerator;

import net.minecraft.util.RandomSource;

import java.util.List;

import static net.vildulv.minecraft.justspace.block.util.loggenerator.CommandersLogGenerator.CommonSnippetTypes.ILLEGIBLE;
import static net.vildulv.minecraft.justspace.block.util.loggenerator.CommandersLogGenerator.CommonSnippetTypes.NAMES;

public class CommandersLogGenerator {


    static final RandomSource RANDOM = RandomSource.create(193944213L);


    public static String generateSpaceShipLog() {
        return enrichSpaceShipLogs(getHeader() + SpaceShipSnippetsTypes.BASE_LINES.getRandomSnippet());
    }


    private static String enrichSpaceShipLogs(String snippet) {
        for (SpaceShipSnippetsTypes type : SpaceShipSnippetsTypes.values()) {
            snippet = type.insertMe(snippet);
        }
        for (CommonSnippetTypes type : CommonSnippetTypes.values()) {
            snippet = type.insertMe(snippet);
        }
        return snippet;
    }


    public static String generateSpaceStationLog() {
        return enrichSpaceStationLogs(getHeader() + SpaceStationSnippetsTypes.BASE_LINES.getRandomSnippet());
    }


    private static String enrichSpaceStationLogs(String snippet) {
        for (SpaceStationSnippetsTypes type : SpaceStationSnippetsTypes.values()) {
            snippet = type.insertMe(snippet);
        }
        for (CommonSnippetTypes type : CommonSnippetTypes.values()) {
            snippet = type.insertMe(snippet);
        }
        return snippet;
    }


    public static String generateScienceLogs() {
        return enrichScienceLogs(getScienceHeader() + ScienceSnippetsTypes.BASE_LINES.getRandomSnippet());
    }


    private static String enrichScienceLogs(String snippet) {
        for (ScienceSnippetsTypes type : ScienceSnippetsTypes.values()) {
            snippet = type.insertMe(snippet);
        }
        for (CommonSnippetTypes type : CommonSnippetTypes.values()) {
            snippet = type.insertMe(snippet);
        }
        return snippet;
    }

    private static String getHeader() {
        int roll = RANDOM.nextInt(10);
        if (roll < 4) {
            return String.format("Log entry (%d): ", RANDOM.nextInt(1000));
        }
        if (roll < 8) {
            int day = RANDOM.nextInt(28) + 1;
            int month = RANDOM.nextInt(12) + 1;
            int year = 2200 + RANDOM.nextInt(100);
            if (roll < 5) {
                return String.format("Captain's Log: %d/%d/%d: ", day, month, year);
            }
            return String.format("Commanders Log - %d/%d/%d: ", day, month, year);
        }
        if (roll == 8) {
            return "Captain's Log: " + ILLEGIBLE.getRandomSnippet();
        }
        if (roll == 9) {
            return "Log entry " + ILLEGIBLE.getRandomSnippet() + " ";
        }
        return ILLEGIBLE.getRandomSnippet();
    }

    private static String getScienceHeader() {
        int roll = RANDOM.nextInt(10);
        if (roll < 4) {
            return String.format("Log entry (%d): ", RANDOM.nextInt(1000));
        }
        if (roll < 8) {
            int day = RANDOM.nextInt(28) + 1;
            int month = RANDOM.nextInt(12) + 1;
            int year = 2200 + RANDOM.nextInt(100);
            if (roll < 5) {
                return String.format("Report for: %d/%d/%d: ", day, month, year);
            }
            return String.format("Dr %s - %d/%d/%d: ", NAMES.getRandomSnippet(), day, month, year);
        }
        if (roll == 8) {
            return "Reporting: " + ILLEGIBLE.getRandomSnippet();
        }
        if (roll == 9) {
            return "Log entry " + ILLEGIBLE.getRandomSnippet() + " ";
        }
        return ILLEGIBLE.getRandomSnippet();
    }


    enum SpaceShipSnippetsTypes {
        BASE_LINES("", List.of("{EE}.",
                "{EE}.",
                "Rough day. {EE}. As well as breakdown {SD}. Affecting crew but {CS}.",
                "{EE}. However {SS}.",
                "{EE}. But {CS}.",
                "{EE}. However {SE}.",
                "{IL}>{SE}<{IL}>{SS}<{IL}",
                "{IL}>{ER}<{IL}>{MY}<{IL}",
                "{IL}{IL}>{MY}<{IL}{IL}",
                "Misshapen {SD}. Hard work to getting {SS}.",
                "{SS}. Indicating that previous error {SD} was fully resolved.")),

        EVENTS("{EE}",
                List.of("Navigated asteroid field; {SD}",
                        "Detected unknown signal; {ER}",
                        "Engine calibration complete; {SS}",
                        "Encountered micro-meteoroid shower; {SD}",
                        "Discovered rogue planet; {ER}",
                        "Crew member reported strange dreams. Medical team investigating. Meanwhile {CS}",
                        "Lost contact with probe, but {ER}",
                        "Held ceremony for crew birthdays, {LL} morale, {CS}",
                        "Charted new trade route; {ER}",
                        "Successful test of new propulsion system; {SS}",
                        "Disastrous test of new propulsion system; {SD}",
                        "Left Overworld. {CS}",
                        "Recalculated course to avoid The End, Stress on hull {SD}",
                        "Collided with a Creeper ship. Damage taken {SD}",
                        "Encountered roaming Enderman, core stolen. Calculated increased travel time {##} days. {CS}",
                        "Rescued survivors from a wrecked ship; {ER}, {CS}",
                        "Enderman spotted on the hull, crew party exited to remove it but reporting spotting {MY}",
                        "Strange readings from nearby nebula. Exloration resulted in {ER}",
                        "Unidentified alien vessel encountered; {ER}",
                        "Collied with Bedrock block, {SD}",
                        "Got to close to the Nether, beds exploding, {SD}. Crew morale {LL} (understandably)",
                        "Aether wind encountered, {SD}"
                )),

        SHIP_DAMAGE("{SD}",
                List.of("minor hull breach in sector {##} but repaired by engineering team",
                        "causing life support system glitch we had to resolved it with a software patch",
                        "a radiation leak contained - no exposure to crew",
                        "resulting in engine overheating, some coolant systems adjusted",
                        "risking navigation array malfunction but recalibration was successfully",
                        "causing immediate communication blackout, signal not restored until after {##} hours",
                        "leading to a power surge in main reactor - safety protocols activated",
                        "resulting in an airlock failure. Luckely was emergency seal engaged, no injuries",
                        "causing sensor array damages. Replacement ordered but weeks away",
                        "inflicting structural integrity compromised. Emergency reinforced bulkheads")),

        SYSTEM_STATUS("{SS}",
                List.of("Life support functioning within normal parameters",
                        "Oxygen levels stable. CO2 scrubbers operating efficiently",
                        "Power systems at {H%} capacity but with {LL} fluctuations detected",
                        "Thermal control systems maintaining {LL} temperature",
                        "Radiation shielding integrity at {H%}; no breaches detected",
                        "Waste recycling systems operational; water recovery at {H%}",
                        "Shield generators at full strength",
                        "Engine performance at {H%}; minor efficiency loss noted",
                        "Navigation systems recalibrated; star charts updated",
                        "Communication systems restored; long-range signal strength {H%}",
                        "Artificial gravity stable at {H%} levels",
                        "Emergency protocols reviewed; crew training ongoing, efficiency {LL}",
                        "Power {LL} but stable"
                )),

        CREW_STATUS("{CS}",
                List.of("Crew morale remains high",
                        "Dr. {NN} reports successful completion of zero-gravity experiments",
                        "Engineer {NN} resolved minor issue with the hydroponics bay.",
                        "Crew adapting to extended space travel. Morale {LL}",
                        "Psychological evaluations indicate {LL} mental health among crew",
                        "Training drills conducted; emergency protocols reviewed",
                        "Cultural exchange night planned; promoting unity among diverse crew",
                        "Physical fitness routines ongoing; crew health {LL}",
                        "New crew member, {TT} {NN} acclimating well; skills enhancing team capabilities",
                        "Crew rotation schedule adjusted for optimal rest periods",
                        "Caught {TT} {NN} stargazing, it reminded of my home planet",
                        "Caught {TT} {NN} stealing. Disciplinary action carried out",
                        "{TT} {NN} injured during accident {SD}",
                        "{TT} {NN} promoted for exceptional performance",
                        "{TT} {NN} awarded medal for bravery when ship {EE}",
                        "{TT} {NN} discovered ancient alien artifact; sent to science team for analysis. {TT} claiming it resembles {MY}",
                        "{TT} {NN} has been acting strangely, often staring into space and muttering about {MY}",
                        "{TT} {NN} keeps complaining",
                        "There was cake"
                )),

        EXPLORATION_RESULTS("{ER}",
                List.of("potential mining site identified",
                        "data sent to science team for analysis",
                        "secured for further study",
                        "signs of past microbial life detected",
                        "analysis underway",
                        "logged coordinates for future missions",
                        "no survivors found, but valuable data retrieved",
                        "images and readings archived",
                        "further investigation required",
                        "found evidence of ancient civilization; artifacts recovered for study",
                        "discovered unusual energy readings; science team intrigued",
                        "mapped new star system; potential for future colonization",
                        "found derelict ship; boarded for salvage operations",
                        "discovered new mineral deposits; samples collected for analysis",
                        "found signs of water ice on asteroid; potential resource for future missions"
                )),

        STRANGE_EFFECTS("{SE}",
                List.of("strange lights observed outside the viewport;",
                        "unidentified signals interfering with ship's systems; source unknown",
                        "crew experiencing vivid dreams dr {NN} noticed several crew members refer to {MY} in their dreams",
                        "time dilation effects noted, cause unknown",
                        "odd metallic taste reported by crew; dr {NN} investigating",
                        "sudden temperature drops in certain sections of the ship no source identified, but crew members report of strange shapes in the frost resembling {MY}",
                        "unexplained power fluctuations; engineering team baffled; purple light often seen flickering near the main reactor",
                        "crew member {NN} reports hearing whispers when alone; psychological evaluation scheduled")),

        ;
        private List<String> snippets;
        private String code;

        SpaceShipSnippetsTypes(String code, List<String> snippets) {
            this.snippets = snippets;
            this.code = code;
        }

        public String getRandomSnippet() {
            return snippets.get(RANDOM.nextInt(snippets.size()));
        }

        public String insertMe(String snippet) {
            if (this != BASE_LINES && snippet.contains(code)) {
                return snippet.replace(code, enrichSpaceShipLogs(getRandomSnippet()));
            }
            return snippet;
        }
    }

    enum SpaceStationSnippetsTypes {


        BASE_LINES("", List.of("{EE}.",
                "{EE}.",
                "Rough day. {EE}. As well as breakdown {SD}. Affecting crew but {CS}.",
                "{EE}. However {SS}.",
                "{EE}. But {CS}.",
                "{EE}. However {SE}.",
                "{IL}>{SE}<{IL}>{SS}<{IL}",
                "{IL}>{IR}<{IL}>{MY}<{IL}",
                "{IL}{IL}>{MY}<{IL}{IL}",
                "Misshapen {SD}. Hard work to getting {SS}.",
                "{SS}. Indicating that previous error {SD} was fully resolved.")),

        EVENTS("{EE}",
                List.of("Routine maintenance completed; {SS}",
                        "Received supply shipment from distant colony, invertory fully restocked. However {CS}",
                        "Minor hull breach in docking bay causing {SD}. Engineering team performed rapid repairs",
                        "Communications blackout lasted {##]} hours, cause traced to solar flare interference. Investigation gave clue to {IR}",
                        "Unidentified vessel approached station perimeter, security protocols activated. {CS}",
                        "Detected micro-meteoroid impact, {SD}",
                        "Station rotation speed adjusted for optimal artificial gravity; crew adapting well. {CS}",
                        "Strange lights observed outside observation deck, cause undetermined. Reports of sightings of {MY}. Crew uneasy but {CS}",
                        "Routine maintenance completed on hydroponics bay. Food production {LL}",
                        "Celebrated station anniversary with crew event. Morale {LL}")),

        STATION_DAMAGE("{SD}",
                List.of("minor hull breach in sector {##} but repaired by engineering team",
                        "power grid overload causing temporary blackout, Engineer {NN} resolved issue",
                        "a radiation leak contained - no exposure to crew",
                        "airlock seal failure on lock {##}{AA}, loss of atmosphere contained, no injuries, crew reaction; {CS}",
                        "hydroponics bay flooding, estimated food reserves {L%}",
                        "computer system corruption due to solar flare, downtime {##} hours"
                        )),

        SYSTEM_STATUS("{SS}",
                List.of("Life support functioning within normal parameters",
                        "Oxygen levels stable. CO2 scrubbers operating efficiently",
                        "Radiation shielding integrity at {H%}; no breaches detected",
                        "Waste recycling systems operational; water recovery at {H%}",
                        "Docking bay systems operational; incoming/outgoing traffic normal",
                        "Communication systems restored; long-range signal strength {H%}",
                        "Artificial gravity stable at {H%} levels",
                        "Power {LL} but stable",
                        "Station rotation speed adjusted for optimal artificial gravity; crew adapting well. {CS}"
                )),

        INVESTIGATION_RESULTS("{IR}",
                List.of("data sent to science team for analysis",
                        "strange readings logged, {TT} {NN} set to resolve the issue",
                        "further investigation required",
                        "no anomalies found, situation normalised",
                        "found evidence of ancient alien artifact; sent to science team for study",
                        "discovered unusual energy readings; {TT} {NN} intrigued")),

        CREW_STATUS("{CS}",
                List.of("Crew morale remains {LL}",
                        "Crew efficency {LL} after recent events"
                        )),

        STRANGE_EFFECTS("{SE}",
                List.of("strange lights observed outside the viewport",
                        "sighting of gigantic space ray floating by the station, {L%} of crew reportin small radiocative doses",
                        "unidentified signals interfering with station's systems; source unknown"
                        ))

        ;

        private List<String> snippets;
        private String code;

        SpaceStationSnippetsTypes(String code, List<String> snippets) {
            this.snippets = snippets;
            this.code = code;
        }

        public String getRandomSnippet() {
            return snippets.get(RANDOM.nextInt(snippets.size()));
        }

        public String insertMe(String snippet) {
            if (this != BASE_LINES && snippet.contains(code)) {
                return snippet.replace(code, enrichSpaceStationLogs(getRandomSnippet()));
            }
            return snippet;
        }

    }

    enum ScienceSnippetsTypes {


        BASE_LINES("", List.of("{EE}",
                "{EE}")),

        EVENTS("{EE}",
                List.of("Preliminary analysis of {IT} complete. {IR}",
                        "New specimen acquired from nearby asteroid. {IR}",
                        "Strange energy readings detected in lab. {IR}",
                        "Successful test of new experimental equipment; {IR}",
                        "Unexpected reaction during experiment; {IR}",
                        "Discovered potential new element; samples sent for further analysis. {IR}",
                        "Analyzed alien artifact; findings inconclusive but intriguing. {IR}",
                        "Conducted zero-gravity biology experiments; results promising. {IR}",
                        "Studied effects of cosmic radiation on microbial life; data logged. {IR}",
                        "Collaborated with external research teams via holographic link; knowledge exchange fruitful. {IR}",
                        "Investigated tissue sample from a so called Void Manta; {VM}",
                        "Tachyon beam pulse used to investigate nearby species often called Void Mata. {VM}",
                        "Analyzed alien artifact resembling {MY}; {AL}",
                        "Preliminary report on the excavated alien artifact: {AL}. {IR}",
                        "Cataloged references to a being known as {MY} in ancient texts; {IR}",
                        "Cataloged references to a being known as  Y'ad Nib Nub found on alien artifacts. {YF}",
                        "The reference to Y'ad Nib Nub on many alien artifacts seems to be related to {MY}. {YF}"
                        )),

        INVESTIGATION_TARGETS("{IT}",
                List.of("alien artifact resembling {MY}",
                        "mineral sample from asteroid rich in rare elements",
                        "microbial life form found on derelict ship",
                        "energy readings from nearby nebula",
                        "data from deep-space probe recently recovered",
                        "unidentified substance found in cargo bay",
                        "biological sample from exoplanet surface",
                        "crystalline structure emitting unusual radiation patterns"
                )),


        VOID_MANTA("{VM}",
                List.of("Specimen shows signs of bioluminescence.",
                        "Specimen exhibits unique energy manipulation abilities.",
                        "Specimen's cellular structure defies known biological principles.",
                        "Specimen appears to be able to communicate through electromagnetic pulses.",
                        "Specimen's metabolism is unknown.",
                        "Specimen has been know to show at places of major space disasters.",
                        "Specimen seems to have relation to {MY}.",
                        "{IR}"
                )),

        ALIEN_FACTS("{AL}",
                List.of("The artifact is made of an unknown alloy that is incredibly durable.",
                        "The inscriptions on the artifact do not match any known language or code. But our translator seem to pick up references to {MY}",
                        "The artifact emits a low-level energy field that interferes with nearby electronics.",
                        "The artifact's purpose is unclear, but it appears to be a form of advanced technology",
                        "No biological material has been detected on the artifact, suggesting it was not created by a living organism.",
                        "The artifact seem to not be fully present in our dimension, often flickering in and out of visibility.",
                        "Both {TT} {NN} have shown great interest in the artifact and are leading the research team. {IR}",
                        "Both {TT} {NN} and {TT} {NN} have acted strangely since the artifact was brought on board. {IR}"

                )),

        YAD_NIB_NUB("{YF}",
                List.of("The texts describe Y'ad Nib Nub as a cosmic entity that embodies chaos and transformation.",
                        "The texts suggest that Y'ad Nib Nub has the ability to manipulate space-time.",
                        "Many references seem to relate Y'ad Nib Nub to the act of giving",
                        "Many references seem to relate Y'ad Nib Nub to the act of taking",
                        "An indication of the meaning of the name seem to be something similar to 'The Bringer', but bringer of what?",
                        "Most references to Y'ad Nib Nub seem be occur shortly before the collapse of the alien civilization."
        )),


        INVESTIGATION_RESULTS("{IR}",
                List.of("Data sent to science team for analysis",
                        "Strange readings logged, {TT} {NN} set to resolve the issue",
                        "Further investigation required",
                        "No anomalies found, situation normalised",
                        "Found evidence of ancient alien artifact; sent to science team for study",
                        "Discovered unusual energy readings; {TT} {NN} intrigued")),


        ;

        private List<String> snippets;
        private String code;

        ScienceSnippetsTypes(String code, List<String> snippets) {
            this.snippets = snippets;
            this.code = code;
        }

        public String getRandomSnippet() {
            return snippets.get(RANDOM.nextInt(snippets.size()));
        }

        public String insertMe(String snippet) {
            if (this != BASE_LINES && snippet.contains(code)) {
                return snippet.replace(code, enrichScienceLogs(getRandomSnippet()));
            }
            return snippet;
        }

    }

    enum CommonSnippetTypes {
        NAMES("{NN}",
                List.of("Reynolds",
                        "Shepard",
                        "Ripley",
                        "McCoy",
                        "Scott",
                        "Wash",
                        "Tuvok",
                        "Crusher",
                        "Kaylee",
                        "Zoe",
                        "Vex",
                        "Aris",
                        "Virek",
                        "Nova",
                        "Orin",
                        "Corona",
                        "Dax",
                        "Saren",
                        "Garrus",
                        "Liara",
                        "Tali",
                        "Wrex",
                        "Hansen",
                        "Fransen",
                        "Lansen")),

        TITLES("{TT}",
                List.of("Captain",
                        "Commander",
                        "Lieutenant",
                        "Ensign",
                        "Dr.",
                        "Chief Engineer",
                        "Science Officer",
                        "Pilot",
                        "Navigator",
                        "Communications Officer",
                        "Security Officer",
                        "Medic",
                        "Technician",
                        "Quartermaster",
                        "Helmsman",
                        "Tactical Officer"
                )),

        MYTHOS("{MY}",
                List.of("a Blue Star",
                        "a Celestial Serpent",
                        "a Void Manta",
                        "a Silver Phoenix",
                        "a Cosmic Dragon",
                        "a Starborn Titan",
                        "a Nebula Fox",
                        "something they call Y'ad Nib Nub",
                        "the Great Devourer",
                        "the Eternal Voyager")),

        LEVEL("{LL}", List.of("strong", "weak", "failing", "increasing", "decreasing", "optimal", "critical", "stable", "unstable", "fluctuating")),

        NUMBER("{##}", List.of("1", "2", "3", "4", "5", "6", "8", "8", "9", "10")),

        LETTER("{AA}", List.of("A", "B", "C", "E", "J", "K", "T", "X", "W", "Z")),

        PERCENT_LOW("{L%}", List.of("5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%")),

        PERCENT_HIGH("{H%}", List.of("65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%")),

        ILLEGIBLE("{IL}",
                List.of("##¤¤&%!!",
                        "???¤6462##",
                        "##crss(*)",
                        "¤¤&&%##!!",
                        "##¤¤&%!!",
                        "???¤6462##",
                        "##crss(*)",
                        ">>#¤%**^^#¤%"));;


        private List<String> snippets;
        private String code;

        CommonSnippetTypes(String code, List<String> snippets) {
            this.snippets = snippets;
            this.code = code;
        }

        public String getRandomSnippet() {
            return snippets.get(RANDOM.nextInt(snippets.size()));
        }

        public String insertMe(String snippet) {
            if (snippet.contains(code)) {
                return snippet.replace(code, getRandomSnippet());
            }
            return snippet;
        }

    }


}
