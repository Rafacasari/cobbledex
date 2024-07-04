package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.summary.SummaryButton
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cobblemon.mod.common.pokemon.Species
import com.rafacasari.mod.cobbledex.CobbledexConstants.Client.discoveredList
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI.CobbledexRelatedMenu
import com.rafacasari.mod.cobbledex.network.client.handlers.SyncServerSettingsHandler
import com.rafacasari.mod.cobbledex.utils.CobblemonUtils
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexResource
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexTranslation
import java.util.*

class PokemonEvolutionDisplay(x: Int, y: Int): CobbledexScrollList<PokemonEvolutionDisplay.EvolveSlot>(x, y, SLOT_HEIGHT + SLOT_SPACING) {
    companion object {
        const val SLOT_HEIGHT = 27
        const val SLOT_SPACING = 3
        const val PORTRAIT_DIAMETER = 25
        const val PORTRAIT_OFFSET_X = 54
        const val PORTRAIT_OFFSET_Y = 1

        private val portraitBackground = cobbledexResource("textures/gui/evolution_slot_background.png")
        private val slotOverlay = cobbledexResource("textures/gui/evolution_slot_overlay.png")

        private val buttonResource = cobbledexResource("textures/gui/evolution_button.png")
    }

    public override fun addEntry(entry: EvolveSlot): Int {
        return super.addEntry(entry)
    }

    fun resetScrollPosition() { scrollAmount = 0.0 }

    fun clearEvolutions() = clearEntries()

    var targetMenu: CobbledexRelatedMenu = CobbledexRelatedMenu.Evolutions

    fun selectEvolutions(currentMenu: CobbledexRelatedMenu, pokemonList: List<Pair<Species, Set<String>>>?) {
        targetMenu = currentMenu

        clearEntries()

        pokemonList?.filter {

            val filteredVariations = PokemonModelRepository.variations[it.first.resourceIdentifier]?.variations?.any {
                x -> x.model != null && x.aspects == it.second
            }

            it.second.isEmpty() || (filteredVariations != null && filteredVariations)
        }?.map {
            EvolveSlot(it.first, it.second, this)
        }?.forEach { entry ->
            this.addEntry(entry)
        }
    }

    class EvolveSlot(private val evolution: Species, private val aspects: Set<String>, val display: PokemonEvolutionDisplay) : Entry<EvolveSlot>() {
        private var isEnabled: Boolean
        val client: MinecraftClient = MinecraftClient.getInstance()

        val form by lazy {
            evolution.getForm(aspects)
        }

        init {
            val evolutionForm = evolution.getForm(aspects)
            val config = SyncServerSettingsHandler.config
            val registerType = discoveredList[evolution.showdownId()]?.get(evolutionForm.formOnlyShowdownId())?.status
            val hasCaught = registerType == DiscoveryRegister.RegisterType.CAUGHT
            val hasSeen = hasCaught || registerType == DiscoveryRegister.RegisterType.SEEN
            
            isEnabled = (!config.Collection_NeedCatch || hasCaught) && (!config.Collection_NeedSeen || hasSeen)
        }

        private val selectButton: SummaryButton = SummaryButton(
            buttonX = 0F,
            buttonY = 0F,
            buttonWidth = 40,
            buttonHeight = 10,
            clickAction = {
                if (isEnabled) {
                    val form = evolution.getForm(aspects)

                    CobbledexGUI.Instance?.selectedPokemon = form
                    CobbledexGUI.Instance?.selectedAspects = aspects
                    CobbledexGUI.Instance?.setPreviewPokemon(form, aspects)

                    CobbledexGUI.Instance?.updateMenu()
                    CobbledexGUI.Instance?.updateRelatedSpecies()
                }
            },
            text = cobbledexTranslation("cobbledex.texts.select"),
            resource = buttonResource,
            boldText = true,
            largeText = false,
            textScale = 0.5F
        )

        override fun getNarration() = evolution.translatedName

        override fun render(
            context: DrawContext,
            index: Int,
            y: Int,
            rowLeft: Int,
            rowWidth: Int,
            rowHeight: Int,
            mouseX: Int,
            mouseY: Int,
            isHovered: Boolean,
            partialTicks: Float
        ) {
            val x = rowLeft - 2
            val matrices = context.matrices


            blitk(
                matrixStack = matrices,
                texture = portraitBackground,
                x = x + PORTRAIT_OFFSET_X,
                y = y + PORTRAIT_OFFSET_Y,
                height = PORTRAIT_DIAMETER,
                width = PORTRAIT_DIAMETER
            )

            context.enableScissor(
                x + PORTRAIT_OFFSET_X,
                y + PORTRAIT_OFFSET_Y,
                x + PORTRAIT_OFFSET_X + PORTRAIT_DIAMETER,
                y + PORTRAIT_OFFSET_Y + PORTRAIT_DIAMETER
            )

            matrices.push()
            matrices.translate(
                x + PORTRAIT_OFFSET_X + PORTRAIT_DIAMETER / 2.0 - 1.0,
                y.toDouble() - 8,
                0.0
            )

            CobblemonUtils.drawPortraitPokemon(evolution, aspects, matrices, blackSilhouette = !isEnabled)

            matrices.pop()
            context.disableScissor()


            blitk(
                matrixStack = matrices,
                texture = slotOverlay,
                x = x,
                y = y,
                height = SLOT_HEIGHT,
                width = rowWidth
            )


            selectButton.setPosFloat(x + 4.5F, y + 14F)
            selectButton.render(context, mouseX, mouseY, partialTicks)

            val speciesName = evolution.translatedName
            val formName = form.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }.text()
            val targetName = if(this.display.targetMenu == CobbledexRelatedMenu.Forms) formName else speciesName

            drawScaledText(
                context = context,
                font = CobblemonResources.DEFAULT_LARGE,
                text = if(isEnabled) targetName.bold() else "?????".text().bold(),
                x = x + 3.7f,
                y = y + 1f,
                shadow = true,
                centered = false
            )
        }

        override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
            if (selectButton.isHovered) {
                selectButton.onPress()
                return true
            }
            return false
        }
    }
}