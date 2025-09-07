package com.ibn.arrp.json.recipe;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public class JResult {
	protected final String item;

	JResult(final String id) {
		this.item = id;
	}

	public static JResult item(final Item item) {
		return result(Registries.ITEM.getId(item).toString());
	}

	public static JResult result(final String id) {
		return new JResult(id);
	}

	public static JStackedResult itemStack(final Item item, final int count) {
		return stackedResult(Registries.ITEM.getId(item).toString(), count);
	}

	public static JStackedResult stackedResult(final String id, final int count) {
		final JStackedResult stackedResult = new JStackedResult(id);

		stackedResult.count = count;

		return stackedResult;
	}

	@Override
	protected JResult clone() {
		try {
			return (JResult) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}
}
