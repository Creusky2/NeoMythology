package com.mrcreusky.neomythology.client.gui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import com.mrcreusky.neomythology.NeoMythology;
import com.mrcreusky.neomythology.client.PlayerDivinity;
import com.mrcreusky.neomythology.client.PlayerHelper;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;


public class GodSelectionMenu extends Screen {
    // private static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation BACKGROUND_TEXTURE = NeoMythology.getGuiTexture("gods_selection_menu.png");

    public static List<God> godsList;  // Liste des objets God pour stocker les détails des dieux
    private List<CircularSpriteWidget> godsButtons;  // Liste des boutons des dieux
    // Déclaration de la liste des positions des labels
    private List<LabelPosition> labelsPositions;

    // Pour l'affichage des détails à droite
    private God selectedGod;  // Le dieu sélectionné

    public GodSelectionMenu() {
        super(Component.literal("NeoMythology"));
        godsList  = loadGods();
        godsButtons = new ArrayList<>();  // Liste des boutons des dieux
        // Sélection par défaut
        selectedGod = godsList.isEmpty() ? null : godsList.get(0);
    }

    public static void reloadGods() {
        godsList = loadGods();
    }

    private static List<God> loadGods() {
        try {
            InputStream inputStream = GodSelectionMenu.class.getResourceAsStream("/data/neomythology/gods_config.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
            Type listType = new TypeToken<List<God>>() {}.getType();
            return new Gson().fromJson(jsonObject.getAsJsonArray("gods"), listType);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Retourne une liste vide en cas d'erreur
        }
    }

    public final class God {
        String name;
        String icon;
        String icon_hovered;
        String description;
        public List<Stat> stats;
        public String civilisation;

        public static God getGodByName(String name) {
            for (God god : godsList) {
                if (god.name.equals(name)) {
                    return god;
                }
            }
            return null;
        }
        public String getName() {
            return name;
        }
    
        public static class Stat {
            private String name;
            private Float value;
            public Stat(String name, Float value) {
                this.name = name;
                setValue(value);
            }

            public String getName() {
                return name;
            }
            public Float getValue() {
                return value;
            }
            public void setValue(Float value) {
                this.value = value;
            }
            
            public Holder<Attribute> getAttributeHolderFromName(String name) {
                System.out.println("TEST");
                switch (name) {
                    case "health":
                        System.out.println("health");
                        return Attributes.MAX_HEALTH;
                    case "speed":
                        return Attributes.MOVEMENT_SPEED;
                    case "attack":
                        return Attributes.ATTACK_DAMAGE;
                    case "defense":
                        return Attributes.ARMOR;
                    default:
                        return null;
                }
            }
            public Operation getOperationFromName(String name) {
                switch (name) {
                    case "defense":
                    case "attack":
                    case "health":
                        return Operation.ADD_VALUE;
                    case "speed":
                        return Operation.ADD_MULTIPLIED_TOTAL;
                    case "other":
                        return Operation.ADD_MULTIPLIED_BASE;
                    default:
                        return null;
                }
            }

            public void AddPermanentModifierToPlayer(ServerPlayer player){
                AttributeInstance statAttributeInstance = player.getAttribute(getAttributeHolderFromName(name));
                if (statAttributeInstance != null) {
                    statAttributeInstance.addPermanentModifier(new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath("neomythology", "god_modifier_" + name),
                    getValue(),
                    getOperationFromName(name)
                    ));
                }
            }
        }
             
    }


    
    private static class LabelPosition {
        String civilisation;
        int y;
        int x;
    
        LabelPosition(String civilisation, int y, int x) {
            this.civilisation = civilisation;
            this.y = y;
            this.x = x;
        }
    }

    @Override
    protected void init() {
        RandomSource random = RandomSource.create();
        int buttonWidth = 64;
        int buttonHeight = 64;
        int padding = 5;
        int startY = this.height / 6;

        super.init();

        // Ajouter un bouton pour la sélection aléatoire en haut de l'interface
        this.addRenderableWidget(new Button.Builder(Component.literal("Random"),
            button -> {
                // Action lors de la sélection aléatoire
                int randomIndex = Mth.randomBetweenInclusive(random, 0, godsList.size() - 1);
                God randomGod = godsList.get(randomIndex);
                setSelectedGod(randomGod);
            }).bounds(this.width / 2 - 50, 20, 100, 20)  // Centrer le bouton "Random" en haut de l'écran
        .build());

        // Grouper les dieux par civilisation
        Map<String, List<God>> godsByCivilisation = godsList.stream()
                .collect(Collectors.groupingBy(god -> god.civilisation));

        // Initialiser la position de départ pour les boutons
        int currentY = startY;

        // Créer une liste pour stocker les positions Y des labels de chaque civilisation
        labelsPositions = new ArrayList<>();

        // Parcourir chaque civilisation et ajouter les dieux associés
        for (Map.Entry<String, List<God>> entry : godsByCivilisation.entrySet()) {
            String civilisation = entry.getKey();
            List<God> gods = entry.getValue();

            // Ajouter les boutons pour chaque dieu de cette civilisation
            int godsPerRow = 3;
            int numberOfRows = (int) Math.ceil(gods.size() / godsPerRow);
            boolean isFirstRow = true;

            for (int row = 0; row < numberOfRows; row++) {
                int godsInThisRow = Math.min(godsPerRow, gods.size() - row * godsPerRow);
                int currentX = (this.width - (godsInThisRow * (buttonWidth + padding) - padding)) / 2;

                // Sauvegarder la position du label de la civilisation pour la première ligne
                if (isFirstRow) {
                    int labelX = (this.width - (godsPerRow * (buttonWidth + padding) - padding)) / 2 - buttonWidth - padding;
                    labelsPositions.add(new LabelPosition(civilisation, currentY, labelX));
                    isFirstRow = false;
                }

                for (int i = 0; i < godsInThisRow; i++) {
                    God god = gods.get(row * godsPerRow + i);
                    CircularSpriteWidget godButton = new CircularSpriteWidget(currentX, currentY, buttonWidth, buttonHeight,
                            new WidgetSprites(
                                    NeoMythology.getGuiTexture(god.icon),
                                    NeoMythology.getGuiTexture(god.icon_hovered)
                            ),
                            () -> {
                                this.setSelectedGod(god);
                            }, god.name);

                    this.godsButtons.add(godButton);  // Ajouter le bouton à la liste
                    this.addRenderableWidget(godButton);
                    currentX += buttonWidth + padding; // Avancer à la position du prochain bouton
                }
                currentY += buttonHeight + padding; // Ajouter un espace après les boutons de la ligne
            }
            currentY += 10; // Ajouter un espace supplémentaire après chaque groupe de dieux
        }

        // Ajouter un bouton "Save" en bas à droite du panneau droit
        int saveButtonX = this.width - 120;  // Positionné 20 pixels depuis la droite
        int saveButtonY = this.height - 40;  // Positionné 20 pixels depuis le bas
        this.addRenderableWidget(new Button.Builder(Component.literal("Save"),
                button -> {
                    // Action quand le joueur clique sur "Save"
                    @SuppressWarnings("resource")
                    LocalPlayer localPlayer = Minecraft.getInstance().player;
                    MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
                    ServerPlayer player = PlayerHelper.getServerPlayerByName(server, localPlayer.getName().getString());

                    if (player != null) {
                        // Sauvegarder les informations sur le dieu sélectionné
                        CompoundTag tag = player.getPersistentData().getCompound(AbstractClientPlayer.PERSISTED_NBT_TAG);
                        tag.putString("SelectedGod", this.selectedGod.name);
                        player.save(tag);
                        // player.setData(null, null);
                        player.getPersistentData().put(AbstractClientPlayer.PERSISTED_NBT_TAG, tag);

                        System.out.println("Persisted data: " + player.getPersistentData());

                        PlayerDivinity.applyGodStatsToPlayer(this.selectedGod, player);
                    }
                }).bounds(saveButtonX, saveButtonY, 100, 20)  // Position du bouton "Save" en bas à droite
        .build());
    }


    private void setSelectedGod(God god) {
        // Mettre à jour le dieu sélectionné
        this.selectedGod = god;

        // Mettre à jour l'état de sélection des boutons
        for (CircularSpriteWidget button : godsButtons) {
            button.setFocused(button.getName().equals(god.name));  // Mettre en surbrillance le bouton sélectionné
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Dessiner l'arrière-plan
        guiGraphics.blit(BACKGROUND_TEXTURE, 0, 0, 0, 0, this.width, this.height, this.width, this.height);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        drawRightPanel(guiGraphics);

        // Afficher les noms des civilisations
        drawCivilisationLabels(guiGraphics);
    }

    private void drawCivilisationLabels(GuiGraphics guiGraphics) {
        for (LabelPosition label : labelsPositions) {
            guiGraphics.drawString(this.font, label.civilisation, label.x, label.y, 0xFFFFFF);
        }
    }
    
    private void drawRightPanel(GuiGraphics guiGraphics) {
        if (selectedGod != null) {
            // Définir les coordonnées et dimensions du panneau droit
            int paddingX = 130; // Augmenter la marge à gauche pour éviter le chevauchement avec les boutons
            int infoX = this.width / 2 + paddingX;
            int infoY = this.height / 6;
            int panelWidth = (this.width - infoX) - 40; // Assurez-vous que le panneau droit reste dans l'espace disponible
            int panelHeight = 100; // Hauteur initiale du panneau (ajusté plus tard en fonction du contenu)
            int paddingY = 20; // Espace entre les éléments
        
            // Calculer la hauteur du contenu dynamique
            int descriptionHeight = calculateWrappedTextHeight(selectedGod.description, panelWidth);
            int civilisationHeight = calculateWrappedTextHeight("Civilisation: " + selectedGod.civilisation, panelWidth);
            panelHeight += descriptionHeight + civilisationHeight + paddingY * 2;
        
            // Dessiner le fond semi-transparent du panneau droit
            int backgroundColor = 0x88000000; // Couleur noire avec une opacité de 0x88 (34% environ)
            guiGraphics.fill(infoX - 10, infoY - 10, infoX + panelWidth + 10, infoY + panelHeight, backgroundColor);
        
            // Afficher le nom du dieu
            guiGraphics.drawString(this.font, selectedGod.name, infoX, infoY, 0xFFFFFF);
            infoY += paddingY;
        
            // Afficher la description avec un texte "wrap" et ajuster la position Y pour l'élément suivant
            int descriptionHeightUsed = drawWrappedText(guiGraphics, selectedGod.description, infoX, infoY, panelWidth, 0xFFFFFF);
            infoY += descriptionHeightUsed + paddingY;
        
            // Afficher la civilisation
            drawWrappedText(guiGraphics, "Civilisation: " + selectedGod.civilisation, infoX, infoY, panelWidth, 0xFFFFFF);
            infoY += paddingY;
    
            // Afficher les statistiques du dieu sélectionné sous forme de barres de progression
            if (selectedGod.stats != null) {
                guiGraphics.drawString(this.font, "Stats:", infoX, infoY, 0xFFFFFF);
                infoY += paddingY / 2;
    
                // Dessiner une barre de progression pour chaque statistique
                for (God.Stat stat : selectedGod.stats) {
                    drawMinecraftStyleBar(guiGraphics, infoX, infoY, stat.getName(), stat.getValue(), 7);
                    infoY += 15; // Espacement entre les barres
                }
            }
        }
    }
    
    private void drawMinecraftStyleBar(GuiGraphics guiGraphics, int x, int y, String statName, Float statValue, int maxStatValue) {
        int segmentWidth = 20; // Largeur d'un segment de la barre
        int segmentHeight = 10; // Hauteur de la barre
        int padding = 2; // Espacement entre les segments
    
        // Afficher le nom de la statistique
        guiGraphics.drawString(this.font, statName + ": ", x, y - 2, 0xFFFFFF);
    
        int barX = x + this.font.width(statName + ": ") + 5; // Position de départ de la barre
    
        // Dessiner les segments de la barre
        for (int i = 0; i < maxStatValue; i++) {
            // Dessiner le contour blanc
            guiGraphics.fill(barX + i * (segmentWidth + padding) - 1, y - 1, barX + (i + 1) * segmentWidth + i * padding + 1, y + segmentHeight + 1, 0xFFFFFFFF); // Blanc
            
            if (i < statValue) {
                // Segment rempli
                guiGraphics.fill(barX + i * (segmentWidth + padding), y, barX + (i + 1) * segmentWidth + i * padding, y + segmentHeight, 0xFF00FF00); // Vert
            } else {
                // Segment vide
                guiGraphics.fill(barX + i * (segmentWidth + padding), y, barX + (i + 1) * segmentWidth + i * padding, y + segmentHeight, 0xFF555555); // Gris foncé
            }
        }
    }    
    
    private int drawWrappedText(GuiGraphics guiGraphics, String text, int x, int y, int maxWidth, int color) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int currentY = y;
        int lineHeight = 10; // Hauteur de chaque ligne (peut être ajustée si nécessaire)
        int lineCount = 0;
    
        for (String word : words) {
            String testLine = line + word + " ";
            if (this.font.width(testLine) > maxWidth) {
                // Dessiner la ligne actuelle
                guiGraphics.drawString(this.font, line.toString(), x, currentY, color);
                currentY += lineHeight;
                line = new StringBuilder(word + " ");
                lineCount++;
            } else {
                line.append(word).append(" ");
            }
        }
    
        // Dessiner la dernière ligne restante
        if (line.length() > 0) {
            guiGraphics.drawString(this.font, line.toString(), x, currentY, color);
            lineCount++;
        }
    
        // Retourner la hauteur totale occupée par ce bloc de texte
        return lineCount * lineHeight;
    }
    
    private int calculateWrappedTextHeight(String text, int maxWidth) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineHeight = 10; // Hauteur de chaque ligne (peut être ajustée si nécessaire)
        int lineCount = 0;
    
        for (String word : words) {
            String testLine = line + word + " ";
            if (this.font.width(testLine) > maxWidth) {
                // Nouvelle ligne nécessaire
                line = new StringBuilder(word + " ");
                lineCount++;
            } else {
                line.append(word).append(" ");
            }
        }
    
        // Compter la dernière ligne restante
        if (line.length() > 0) {
            lineCount++;
        }
    
        // Retourner la hauteur totale occupée par ce bloc de texte
        return lineCount * lineHeight;
    }
    
    @Override
    public boolean isPauseScreen() {
        return false; // Indique si l'écran met le jeu en pause
    }
}
