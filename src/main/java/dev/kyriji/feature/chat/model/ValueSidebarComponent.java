package dev.kyriji.feature.chat.model;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.sidebar.component.LineDrawable;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ValueSidebarComponent implements SidebarComponent {
	private final Supplier<Component> valueSupplier;

	public ValueSidebarComponent( @NotNull Supplier<Component> valueSupplier) {
		this.valueSupplier = valueSupplier;
	}

	@Override
	public void draw(@NotNull LineDrawable drawable) {
		var value = valueSupplier.get();
		var line = Component.text()
				.append(value.colorIfAbsent(NamedTextColor.AQUA))
				.build();

		drawable.drawLine(line);
	}
}