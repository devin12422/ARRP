package com.ibn.arrp.json.models;

import com.ibn.arrp.json.loot.JCondition;
import com.ibn.arrp.util.BaseClonable;

import net.minecraft.util.Identifier;

public class JOverride extends BaseClonable<JOverride> {
	public final JCondition predicate;
	public final String model;

	/**
	 * @see JModel#override(JCondition, Identifier)
	 */
	public JOverride(JCondition condition, String model) {
		this.predicate = condition;
		this.model = model;
	}
}
