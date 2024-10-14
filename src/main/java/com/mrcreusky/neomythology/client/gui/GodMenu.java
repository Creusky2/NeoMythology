package com.mrcreusky.neomythology.client.gui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
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
import com.mrcreusky.neomythology.client.PlayerHelper;
import com.mrcreusky.neomythology.powers.PlayerSpellData;
import com.mrcreusky.neomythology.powers.Spell;
import com.mrcreusky.neomythology.powers.SpellManager;
import com.mrcreusky.neomythology.quests.DefaultQuests;
import com.mrcreusky.neomythology.quests.PlayerQuestData;
import com.mrcreusky.neomythology.server.ChatRenderer;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import java.util.HashMap;


public class GodMenu extends Screen {
    // private static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation BACKGROUND_TEXTURE = NeoMythology.getGuiTexture("gods_selection_menu.png");

    private static Map<String, String> civilisationColors = new HashMap<>();
    public static List<God> godsList;  // Liste des objets God pour stocker les détails des dieux
    private List<CircularSpriteWidget> godsButtons;  // Liste des boutons des dieux
    // Déclaration de la liste des positions des labels
    private List<LabelPosition> labelsPositions;

    // Pour l'affichage des détails à droite
    private God selectedGod;  // Le dieu sélectionné

    public GodMenu() {
        super(Component.literal("NeoMythology"));
        loadGods();
        godsButtons = new ArrayList<>();  // Liste des boutons des dieux
        // Sélection par défaut
        selectedGod = godsList.isEmpty() ? null : godsList.get(0);
    }

    private static void loadCivilisations() {
        Gson gson = new Gson();
        
        try (InputStreamReader reader = new InputStreamReader(ChatRenderer.class.getResourceAsStream("/data/neomythology/gods_config.json"))) {
            civilisationColors = new HashMap<>();
            JsonObject civilisationsJson = gson.fromJson(reader, JsonObject.class).getAsJsonObject("civilisations");
            civilisationsJson.entrySet().forEach(entry -> {
                String civilisation = entry.getKey();
                String colorHex = entry.getValue().getAsJsonObject().get("color").getAsString();
                civilisationColors.put(civilisation, colorHex);
            });

        } catch (IOException e) {
            e.printStackTrace();  // Gérer les exceptions IO
        } catch (JsonParseException e) {
            e.printStackTrace();  // Gérer les exceptions de parsing JSON
        }catch (Exception e) {
                e.printStackTrace();
        }
    }

    public static void loadGods() {
        loadCivilisations();
        try (InputStreamReader reader = new InputStreamReader(ChatRenderer.class.getResourceAsStream("/data/neomythology/gods_config.json"))) {
            List<God> gods = new ArrayList<>();
            new Gson().fromJson(
                reader,
                JsonObject.class
            ).getAsJsonArray("gods").forEach(godElement ->{
                JsonObject godObject = godElement.getAsJsonObject();
    
                String name = godObject.get("name").getAsString();
                String icon = godObject.get("icon").getAsString();
                String icon_hovered = godObject.get("icon_hovered").getAsString();
                String description = godObject.get("description").getAsString();
                String civilisation = godObject.get("civilisation").getAsString();
    
                // Charger les statistiques du dieu
                List<com.mrcreusky.neomythology.client.gui.GodMenu.God.Stat> stats = new ArrayList<>();
                JsonArray statsArray = godObject.getAsJsonArray("stats");
                for (JsonElement statElement : statsArray) {
                    JsonObject statObject = statElement.getAsJsonObject();
                    String statName = statObject.get("name").getAsString();
                    float statValue = statObject.get("value").getAsFloat();
                    stats.add(new com.mrcreusky.neomythology.client.gui.GodMenu.God.Stat(statName, statValue));
                }
    
                // Charger le kit de base du dieu
                List<ItemStack> baseKit = God.loadBaseKitFromJson(godObject);
    
                // Ajouter le dieu à la liste
                gods.add(new God(name, icon, icon_hovered, description, stats, civilisation, baseKit));
            });

            // Gson gson = new Gson();
            // gson.fromJson(reader,JsonObject.class).getAsJsonArray("gods").forEach(godElement -> {
            //     gods.add(gson.fromJson(godElement, God.class)); // Ajouter le dieu à la liste
            // });
            godsList = gods;
    
        }catch (IOException e) {
            e.printStackTrace();  // Gérer les exceptions IO
        }catch (JsonParseException e) {
            e.printStackTrace();  // Gérer les exceptions de parsing JSON
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public static class God {
        String name;
        String icon;
        String icon_hovered;
        String description;
        public List<Stat> stats;
        String civilisation;
        public List<ItemStack> baseKit;

        // Constructeur ou méthode d'initialisation depuis JSON
        public God(String name, String icon, String icon_hovered, String description, List<Stat> stats, String civilisation, List<ItemStack> baseKit) {
            this.name = name;
            this.icon = icon;
            this.icon_hovered = icon_hovered;
            this.description = description;
            this.stats = stats;
            this.civilisation = civilisation;
            this.baseKit = baseKit;  // Initialise le kit de base
        }

        // Méthode pour donner le kit au joueur
        public void giveBaseKitToPlayer(ServerPlayer player) {
            if (baseKit != null) {
                for (ItemStack itemStack : baseKit) {
                    player.getInventory().add(itemStack);
                }
            }
        }

        public static God getFromPlayer(ServerPlayer player) {
            return getGodByName(player.getPersistentData().getCompound(ServerPlayer.PERSISTED_NBT_TAG).getString("SelectedGod"));
        }

        public static boolean hasSelectedGod(ServerPlayer player) {
            return player.getPersistentData().getCompound(ServerPlayer.PERSISTED_NBT_TAG).contains("SelectedGod");
        }

        private static God getGodByName(String name) {
            return godsList.stream().filter(god -> god.name.equals(name)).findFirst().orElse(null);
        }
        public String getName() {
            return name;
        }
        public String getCivilisation() {
            return civilisation;
        }

        public void applyGodStatsToPlayer(ServerPlayer player) {
            if(this != null) {
                if (stats != null) {   
                    removeGodStatsFromPlayer(player);
                    stats.forEach(stat -> {
                        stat.AddPermanentModifierToPlayer(player);
                    });
                }
            }
        }

        private void removeGodStatsFromPlayer(ServerPlayer player) {
            stats.forEach(stat -> {
                stat.GetModifiersFromPlayer(player).forEach(
                    player.getAttribute(stat.getAttributeHolderFromName(stat.getName()))
                    ::removeModifier
                );
            });
        }

        // Méthode statique pour charger le kit de base à partir du JSON
        public static List<ItemStack> loadBaseKitFromJson(JsonObject jsonObject) {
            List<ItemStack> baseKit = new ArrayList<>();
            JsonArray jsonKitArray = jsonObject.getAsJsonArray("baseKit");
            for (JsonElement kitElement : jsonKitArray) {
                JsonObject kitObject = kitElement.getAsJsonObject();
                String itemID = kitObject.get("item").getAsString();
                int count = kitObject.get("count").getAsInt();
                // Utilisation de BuiltInRegistries pour charger l'item à partir de son ID
                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemID));
                baseKit.add(new ItemStack(item, count));  // Créer une pile d'items pour le kit
            }
            return baseKit;
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

            public Float getValuebyOperation() {
                switch (getOperationFromName(name)) {
                    case ADD_MULTIPLIED_TOTAL:
                        return value * 0.1F;
                    case ADD_MULTIPLIED_BASE:
                        return value * 100;
                    default:
                        return value;
                }
            }
            public void setValue(Float value) {
                this.value = value;
            }
            
            public Holder<Attribute> getAttributeHolderFromName(String name) {
                switch (name) {
                    case "health":
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

            public Set<AttributeModifier> GetModifiersFromPlayer(ServerPlayer player){
                AttributeInstance statAttributeInstance = player.getAttribute(getAttributeHolderFromName(name));
                if (statAttributeInstance != null) {
                    return statAttributeInstance.getModifiers();
                }
                return null;
            }

            public void AddPermanentModifierToPlayer(ServerPlayer player){
                AttributeInstance statAttributeInstance = player.getAttribute(getAttributeHolderFromName(name));
                if (statAttributeInstance != null) {
                    statAttributeInstance.addPermanentModifier(new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath("neomythology", "god_modifier_" + name),
                    getValuebyOperation(),
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
        @SuppressWarnings("resource")
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
        ServerPlayer player = PlayerHelper.getServerPlayerByName(server, localPlayer.getName().getString());

        if(!God.hasSelectedGod(player)){
            DefaultQuests.addChooseDivinityQuestToPlayer(player);
            initIfNoGodSelected(player);
        }else {
            selectedGod = God.getFromPlayer(player);
            System.out.println("Selected god: " + selectedGod.name);
            // this.onClose();
        }
    }

    private void initIfNoGodSelected(ServerPlayer player) {
        RandomSource random = RandomSource.create();

        // Adapter dynamiquement la largeur et la hauteur des boutons en fonction de la largeur de l'écran
        int screenWidth = this.width;
        int screenHeight = this.height;
        int buttonWidth = Math.max(48, screenWidth / 15);  // Calculer la largeur du bouton en fonction de la largeur de l'écran
        int buttonHeight = buttonWidth;  // Garder les boutons carrés
        int padding = Math.max(5, screenWidth / 100);  // Ajuster le padding en fonction de la taille de l'écran

        int startY = screenHeight / 6;

        super.init();

        // Adapter la taille du bouton "Random" dynamiquement
        int randomButtonWidth = Math.max(100, screenWidth / 10);  // Largeur adaptée à l'écran
        int randomButtonHeight = 20;  // Garde une hauteur fixe
        int randomButtonX = (screenWidth - randomButtonWidth) / 2;  // Centrer le bouton horizontalement
        int randomButtonY = 20;  // Garder une marge fixe depuis le haut

        this.addRenderableWidget(new Button.Builder(Component.literal("Random"),
            button -> {
                // Action lors de la sélection aléatoire
                int randomIndex = Mth.randomBetweenInclusive(random, 0, godsList.size() - 1);
                God randomGod = godsList.get(randomIndex);
                setSelectedGod(randomGod);
            }).bounds(randomButtonX, randomButtonY, randomButtonWidth, randomButtonHeight)  // Positionnement dynamique
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

            // Sauvegarder la position du label de la civilisation
            int labelX = (screenWidth - (3 * (buttonWidth + padding) - padding)) / 2 - buttonWidth - padding;
            labelsPositions.add(new LabelPosition(civilisation, currentY, labelX));

            // Calculer dynamiquement le nombre de dieux par ligne en fonction de la largeur de l'écran
            int godsPerRow = Math.min(3, Math.max(1, (screenWidth - padding) / (buttonWidth + padding)));
            int totalGods = gods.size();
            int rows = (int) Math.ceil((double) totalGods / godsPerRow);  // Calculer le nombre total de lignes

            for (int row = 0; row < rows; row++) {
                // Calculer le nombre de dieux dans la ligne actuelle
                int godsInThisRow = Math.min(godsPerRow, totalGods - row * godsPerRow);

                // Centrer dynamiquement la ligne en fonction du nombre de dieux
                int currentX = (screenWidth - (godsInThisRow * (buttonWidth + padding) - padding)) / 2;

                for (int i = 0; i < godsInThisRow; i++) {
                    God god = gods.get(row * godsPerRow + i);

                    // Créer un widget pour chaque dieu
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
                    currentX += buttonWidth + padding;  // Avancer à la position du prochain bouton
                }

                currentY += buttonHeight + padding;  // Ajouter un espace après les boutons de la ligne
            }
            currentY += 10;  // Ajouter un espace supplémentaire après chaque groupe de dieux
        }

        // Adapter dynamiquement la taille et la position du bouton "Save"
        int saveButtonWidth = Math.max(100, screenWidth / 10);  // Largeur adaptée à l'écran
        int saveButtonHeight = 20;  // Garde une hauteur fixe
        int saveButtonX = screenWidth - saveButtonWidth - 20;  // 20px de marge depuis le bord droit
        int saveButtonY = screenHeight - saveButtonHeight - 20;  // 20px de marge depuis le bas

        this.addRenderableWidget(new Button.Builder(Component.literal("Save"),
            button -> {
                // Action quand le joueur clique sur "Save"

                if (player != null) {
                    // Sauvegarder les informations sur le dieu sélectionné
                    CompoundTag tag = player.getPersistentData().getCompound(ServerPlayer.PERSISTED_NBT_TAG);
                    tag.putString("SelectedGod", this.selectedGod.name);
                    tag.putInt("level", 0);
                    player.getPersistentData().put(ServerPlayer.PERSISTED_NBT_TAG, tag);

                    if (selectedGod != null) {
                        this.selectedGod.giveBaseKitToPlayer(player);
                        this.selectedGod.applyGodStatsToPlayer(player);
                        PlayerQuestData.completeQuest(player, "Choose a Divinity");
                        // Fermer le menu après avoir cliqué sur "Save"
                        // Envoyer un message dans le chat
                        String colorHex = civilisationColors.getOrDefault(selectedGod.getCivilisation(), "#FFFFFF"); // Blanc par défaut
                        int color = Integer.parseInt(colorHex.substring(1), 16);
                        player.sendSystemMessage(Component.literal("Vous vénérez maintenant " + this.selectedGod.getName() + " !").withColor(color));

                        // Débloquer et équiper le sort "light_beam" dans le slot 1 (index 0)
                        PlayerSpellData.unlockAndEquipSpell(player, "light_beam", 0);
                        player.displayClientMessage(Component.literal("Le sort 'Light Beam' a été équipé dans le slot 1."), true);

                        // Débloquer et équiper le sort "frost_bolt" dans le slot 2 (index 1)
                        PlayerSpellData.unlockAndEquipSpell(player, "frost_bolt", 1);
                        player.displayClientMessage(Component.literal("Le sort 'Frost Bolt' a été équipé dans le slot 2."), true);

                        // Débloquer et équiper le sort "heal" dans le slot 3 (index 2)
                        PlayerSpellData.unlockAndEquipSpell(player, "heal", 2);
                        player.displayClientMessage(Component.literal("Le sort 'Heal' a été équipé dans le slot 3."), true);

                        // Sauvegarder les données du joueur
                        PlayerSpellData.saveSpellData(player, PlayerSpellData.getSpellData(player));

                        // Afficher un message pour confirmer l'assignation des sorts
                        player.displayClientMessage(Component.literal("Les sorts ont été assignés avec succès !"), true);

                        this.onClose();
                    }
                }
            }).bounds(saveButtonX, saveButtonY, saveButtonWidth, saveButtonHeight)  // Positionnement dynamique
        .build());

        setSelectedGod(this.selectedGod);
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

        @SuppressWarnings("resource")
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
        ServerPlayer player = PlayerHelper.getServerPlayerByName(server, localPlayer.getName().getString());

        if(God.hasSelectedGod(player)) {
            // Dessiner le nom du dieu sélectionné
            drawSelectedGodInfo(guiGraphics, player);
        } else {
            drawRightPanelIfNoGodSelected(guiGraphics);
            // Afficher les noms des civilisations
            drawCivilisationLabelsIfNoGodSelected(guiGraphics);
        }
    }

    private void drawCivilisationLabelsIfNoGodSelected(GuiGraphics guiGraphics) {
        for (LabelPosition label : labelsPositions) {
            guiGraphics.drawString(this.font, label.civilisation, label.x, label.y, 0xFFFFFF);
        }
    }
    
    private void drawRightPanelIfNoGodSelected(GuiGraphics guiGraphics) {
        if (selectedGod != null) {
            // Adapter dynamiquement la position et la taille du right panel sans chevaucher les dieux
            int buttonWidth = Math.max(48, this.width / 15);  // Calculer la largeur du bouton en fonction de la largeur de l'écran
            int padding = Math.max(5, this.width / 100);  // Ajuster le padding en fonction de la taille de l'écran
            int godsPerRow = Math.min(3, Math.max(1, (this.width - padding) / (buttonWidth + padding)));
    
            // Calculer la largeur occupée par les boutons de dieux (sur 3 dieux maximum par ligne)
            int buttonsWidth = godsPerRow * (buttonWidth + padding) - padding;  // Largeur totale occupée par les boutons
            int paddingLeft = (this.width - buttonsWidth) / 2;  // Centrer les boutons horizontalement
    
            // Calculer l'espace restant pour le panneau à droite des boutons
            int panelX = paddingLeft + buttonsWidth + padding + 5;  // Position du panneau à droite des boutons
            int availableWidth = this.width - panelX - padding;  // Largeur disponible pour le panneau
    
            // Limiter la largeur du panneau à l'espace disponible ou une taille minimale
            int panelWidth = Math.min(Math.max(200, availableWidth), this.width / 3);  // Largeur minimale 200, maximale un tiers de l'écran
            int infoY = this.height / 6;
            int panelHeight = 100;  // Hauteur initiale (sera ajustée)
            int paddingY = 20;  // Espace entre les éléments
    
            // Calculer la hauteur du contenu dynamique
            int descriptionHeight = calculateWrappedTextHeight(selectedGod.description, panelWidth);
            int civilisationHeight = calculateWrappedTextHeight("Civilisation: " + selectedGod.civilisation, panelWidth);
            panelHeight += descriptionHeight + civilisationHeight + paddingY * 2;
    
            // Dessiner le panneau droit
            guiGraphics.fill(panelX - 10, infoY - 10, panelX + panelWidth + 10, infoY + panelHeight, 0x88000000);
    
            // Afficher les informations du dieu
            guiGraphics.drawString(this.font, selectedGod.name, panelX, infoY, 0xFFFFFF);
            infoY += paddingY;
    
            // Afficher la description avec un texte wrap
            int descriptionHeightUsed = drawWrappedText(guiGraphics, selectedGod.description, panelX, infoY, panelWidth, 0xFFFFFF);
            infoY += descriptionHeightUsed + paddingY;
    
            // Afficher la civilisation
            drawWrappedText(guiGraphics, "Civilisation: " + selectedGod.civilisation, panelX, infoY, panelWidth, 0xFFFFFF);
            infoY += paddingY;
    
            // Afficher les statistiques sous forme de barres de progression
            if (selectedGod.stats != null) {
                guiGraphics.drawString(this.font, "Stats:", panelX, infoY, 0xFFFFFF);
                infoY += paddingY / 2;
    
                // Dessiner les barres de progression pour chaque statistique
                for (God.Stat stat : selectedGod.stats) {
                    drawMinecraftStyleBar(guiGraphics, panelX, infoY, stat.getName(), stat.getValue(), 7);
                    infoY += 15;  // Espacement entre les barres
                }
            }
        }
    }    
    
    private void drawMinecraftStyleBar(GuiGraphics guiGraphics, int x, int y, String statName, Float statValue, int maxStatValue) {
        int segmentWidth = 20; // Largeur d'un segment de la barre
        int segmentHeight = 10; // Hauteur de la barre
        int padding = 2; // Espacement entre les segments
    
        // Afficher le nom de la statistique
        guiGraphics.drawString(this.font, statName + " : ", x, y - 2, 0xFFFFFF);
    
        int barX = x + this.font.width(statName + " : ") + 5; // Position de départ de la barre
    
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
    
    private void drawSelectedGodInfo(GuiGraphics guiGraphics, ServerPlayer player) {
        if (selectedGod != null) {
            int panelWidth = this.width / 3;
            int infoX = (this.width / 2) + 50;
            int infoY = this.height / 6;
            int paddingY = 20;

            int iconSize = 128;
            int iconX = infoX - iconSize - 20;
            int panelCenterY = infoY + 100;
            int iconY = panelCenterY - (iconSize / 2);

            ResourceLocation godIcon = NeoMythology.getGuiTexture(selectedGod.icon);
            guiGraphics.blit(godIcon, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);

            infoY = this.height / 6;
            guiGraphics.drawString(this.font, "God: " + selectedGod.name, infoX, infoY, 0xFFFFFF);
            infoY += paddingY;

            guiGraphics.drawString(this.font, "Civilisation: " + selectedGod.civilisation, infoX, infoY, 0xFFFFFF);
            infoY += paddingY;

            drawWrappedText(guiGraphics, selectedGod.description, infoX, infoY, panelWidth, 0xFFFFFF);
            infoY += calculateWrappedTextHeight(selectedGod.description, panelWidth) + paddingY;

            if (selectedGod.stats != null) {
                guiGraphics.drawString(this.font, "God's statistics:", infoX, infoY, 0xFFFFFF);
                infoY += paddingY / 2;
                for (God.Stat stat : selectedGod.stats) {
                    drawMinecraftStyleBar(guiGraphics, infoX, infoY, stat.getName(), stat.getValue(), 7);
                    infoY += 15;
                }
            }

            int playerLevel = player.getPersistentData().getInt("level");
            guiGraphics.drawString(this.font, "Level: " + playerLevel, infoX, infoY, 0xFFFFFF);
            infoY += paddingY;

            // Ajouter l'affichage des sorts
            drawPlayerSpellsInfo(guiGraphics, player, infoX, infoY, panelWidth);
        }
    }
    
    private void drawPlayerSpellsInfo(GuiGraphics guiGraphics, ServerPlayer player, int infoX, int infoY, int panelWidth) {
        PlayerSpellData spellData = PlayerSpellData.getSpellData(player);

        guiGraphics.drawString(this.font, "Unlocked Spells:", infoX, infoY, 0xFFFFFF);
        infoY += 15;
        for (String spellName : spellData.getSpellsUnlocked()) {
            Spell currentSpell = SpellManager.getSpell(spellName);
            guiGraphics.drawString(this.font, "- " + currentSpell.getName(), infoX + 10, infoY, 0xAAAAAA);
            infoY += 15;
        }

        infoY += 10;
        guiGraphics.drawString(this.font, "Equipped Spells:", infoX, infoY, 0xFFFFFF);
        infoY += 15;
        for (int i = 0; i < 3; i++) {
            String spellName = spellData.getEquippedSpell(i);
            Spell currentSpell = SpellManager.getSpell(spellName);
            String displayName = spellName.isEmpty() ? "Empty Slot" : currentSpell.getName();
            guiGraphics.drawString(this.font, "Slot " + (i + 1) + ": " + displayName, infoX + 10, infoY, 0xAAAAAA);
            infoY += 15;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Indique si l'écran met le jeu en pause
    }
}
