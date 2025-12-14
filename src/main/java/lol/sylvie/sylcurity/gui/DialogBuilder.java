package lol.sylvie.sylcurity.gui;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.Dialogs;
import net.minecraft.server.dialog.Input;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.action.Action;
import net.minecraft.server.dialog.action.CustomAll;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.body.PlainMessage;
import net.minecraft.server.dialog.input.BooleanInput;
import net.minecraft.server.dialog.input.InputControl;
import net.minecraft.server.dialog.input.NumberRangeInput;
import net.minecraft.server.dialog.input.SingleOptionInput;
import net.minecraft.server.dialog.input.TextInput;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class DialogBuilder {
	private final ServerPlayer player;
	private Component title;
	private boolean closeOnEsc = true;
	private int columns = 1;

	private final ArrayList<DialogBody> body = new ArrayList<>();
	private final ArrayList<Input> inputs = new ArrayList<>();
	private final ArrayList<ActionButton> buttons = new ArrayList<>();

	private final HashMap<Identifier, Consumer<CompoundTag>> actions = new HashMap<>();

	public DialogBuilder(ServerPlayer player, Component title) {
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

	public void setTitle(Component title) {
		this.title = title;
	}

	public DialogBuilder addText(Component text) {
		this.body.add(new PlainMessage(text, PlainMessage.DEFAULT_WIDTH));
		return this;
	}

	public DialogBuilder addInput(String key, InputControl control) {
		this.inputs.add(new Input(key, control));
		return this;
	}

	public DialogBuilder addTextInput(String key, int width, Component label, String value, int maxLength, @Nullable TextInput.MultilineOptions multiline) {
		return this.addInput(key, new TextInput(width, label, !label.getString().isEmpty(), value, maxLength, Optional.ofNullable(multiline)));
	}

	public DialogBuilder addTextInput(String key, int width, Component label, String value, int maxLength) {
		return this.addTextInput(key, width, label, value, maxLength, null);
	}

	public DialogBuilder addBooleanInput(String key, Component label, boolean value) {
		return this.addInput(key, new BooleanInput(label, value, null, null));
	}

	public DialogBuilder addNumberInput(String key, int width, Component label, String format, NumberRangeInput.RangeInfo range) {
		return this.addInput(key, new NumberRangeInput(width, label, format, range));
	}

	public DialogBuilder addNumberInput(String key, int width, Component label, NumberRangeInput.RangeInfo range) {
		return this.addNumberInput(key, width, label, null, range);
	}

	public DialogBuilder addSingleOptionInput(String key, int width, Component label, List<SingleOptionInput.Entry> options) {
		return this.addInput(key, new SingleOptionInput(width, options, label, !label.getString().isEmpty()));
	}

	public DialogBuilder addActionButton(Identifier buttonId, Component text, Consumer<CompoundTag> callback, @Nullable CompoundTag extraData, @Nullable Integer buttonWidth) {
		Action action = new CustomAll(buttonId, Optional.ofNullable(extraData));
		ActionButton button = new ActionButton(new CommonButtonData(text, buttonWidth == null ? Dialogs.BIG_BUTTON_WIDTH : buttonWidth), Optional.of(action));
		buttons.add(button);
		if (!actions.containsKey(buttonId)) actions.put(buttonId, callback);
		return this;
	}

	public DialogBuilder addActionButton(Identifier buttonId, Component text, Consumer<CompoundTag> callback) {
		return this.addActionButton(buttonId, text, callback, null, null);
	}

	public Dialog build() {
		for (Map.Entry<Identifier, Consumer<CompoundTag>> entry : actions.entrySet()) {
			DialogHelper.register(player, entry.getKey(), entry.getValue());
		}
		CommonDialogData data = new CommonDialogData(this.title, Optional.empty(), closeOnEsc, false, DialogAction.CLOSE, body, inputs);
		return new MultiActionDialog(data, buttons, Optional.empty(), columns);
	}

	public void openTo(ServerPlayer player) {
		player.openDialog(new Holder.Direct<>(this.build()));
	}
}
