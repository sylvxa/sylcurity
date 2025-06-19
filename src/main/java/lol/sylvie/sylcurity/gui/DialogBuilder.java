package lol.sylvie.sylcurity.gui;

import net.minecraft.dialog.*;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.action.DynamicCustomDialogAction;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.body.ItemDialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.input.*;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class DialogBuilder {
	private final ServerPlayerEntity player;
	private Text title;
	private boolean closeOnEsc = true;
	private int columns = 1;

	private final ArrayList<DialogBody> body = new ArrayList<>();
	private final ArrayList<DialogInput> inputs = new ArrayList<>();
	private final ArrayList<DialogActionButtonData> buttons = new ArrayList<>();

	private final HashMap<Identifier, Consumer<NbtCompound>> actions = new HashMap<>();

	public DialogBuilder(ServerPlayerEntity player, Text title) {
		this.player = player;
		this.title = title;
	}

	public DialogBuilder setCloseOnEsc(boolean value) {
		this.closeOnEsc = value;
		return this;
	}

	public DialogBuilder setColumns(int value) {
		this.columns = value;
		return this;
	}

	public void setTitle(Text title) {
		this.title = title;
	}

	public DialogBuilder addText(Text text) {
		this.body.add(new PlainMessageDialogBody(text, PlainMessageDialogBody.DEFAULT_WIDTH));
		return this;
	}

	public DialogBuilder addInput(String key, InputControl control) {
		this.inputs.add(new DialogInput(key, control));
		return this;
	}

	public DialogBuilder addTextInput(String key, int width, Text label, String value, int maxLength, @Nullable TextInputControl.Multiline multiline) {
		return this.addInput(key, new TextInputControl(width, label, !label.getString().isEmpty(), value, maxLength, Optional.ofNullable(multiline)));
	}

	public DialogBuilder addTextInput(String key, int width, Text label, String value, int maxLength) {
		return this.addTextInput(key, width, label, value, maxLength, null);
	}

	public DialogBuilder addBooleanInput(String key, Text label, boolean value) {
		return this.addInput(key, new BooleanInputControl(label, value, null, null));
	}

	public DialogBuilder addNumberInput(String key, int width, Text label, String format, NumberRangeInputControl.RangeInfo range) {
		return this.addInput(key, new NumberRangeInputControl(width, label, format, range));
	}

	public DialogBuilder addNumberInput(String key, int width, Text label, NumberRangeInputControl.RangeInfo range) {
		return this.addNumberInput(key, width, label, null, range);
	}

	public DialogBuilder addSingleOptionInput(String key, int width, Text label, List<SingleOptionInputControl.Entry> options) {
		return this.addInput(key, new SingleOptionInputControl(width, options, label, !label.getString().isEmpty()));
	}

	public DialogBuilder addActionButton(Identifier buttonId, Text text, Consumer<NbtCompound> callback, @Nullable NbtCompound extraData, @Nullable Integer buttonWidth) {
		DialogAction action = new DynamicCustomDialogAction(buttonId, Optional.ofNullable(extraData));
		DialogActionButtonData button = new DialogActionButtonData(new DialogButtonData(text, buttonWidth == null ? Dialogs.BUTTON_WIDTH : buttonWidth), Optional.of(action));
		buttons.add(button);
		if (!actions.containsKey(buttonId)) actions.put(buttonId, callback);
		return this;
	}

	public DialogBuilder addActionButton(Identifier buttonId, Text text, Consumer<NbtCompound> callback) {
		return this.addActionButton(buttonId, text, callback, null, null);
	}

	public Dialog build() {
		for (Map.Entry<Identifier, Consumer<NbtCompound>> entry : actions.entrySet()) {
			DialogHelper.register(player, entry.getKey(), entry.getValue());
		}
		DialogCommonData data = new DialogCommonData(this.title, Optional.empty(), closeOnEsc, false, AfterAction.CLOSE, body, inputs);
		return new MultiActionDialog(data, buttons, Optional.empty(), columns);
	}

	public void openTo(ServerPlayerEntity player) {
		player.openDialog(new RegistryEntry.Direct<>(this.build()));
	}
}
