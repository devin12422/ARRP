package test;

import static com.ibn.arrp.json.blockstate.JState.multipart;
import static com.ibn.arrp.json.blockstate.JState.state;
import static com.ibn.arrp.json.blockstate.JState.variant;
import static com.ibn.arrp.json.blockstate.JState.when;
import static com.ibn.arrp.json.loot.JLootTable.condition;
import static com.ibn.arrp.json.loot.JLootTable.entry;
import static com.ibn.arrp.json.loot.JLootTable.loot;
import static com.ibn.arrp.json.loot.JLootTable.pool;
import static com.ibn.arrp.json.models.JModel.element;
import static com.ibn.arrp.json.models.JModel.face;
import static com.ibn.arrp.json.models.JModel.faces;
import static com.ibn.arrp.json.models.JModel.model;
import static com.ibn.arrp.json.models.JModel.textures;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibn.arrp.api.RuntimeResourcePack;
import com.ibn.arrp.impl.RuntimeResourcePackImpl;
import com.ibn.arrp.json.blockstate.JMultipart;
import com.ibn.arrp.json.blockstate.JState;
import com.ibn.arrp.json.blockstate.JVariant;
import com.ibn.arrp.json.blockstate.JWhen;
import com.ibn.arrp.json.lang.JLang;
import com.ibn.arrp.json.models.JModel;
import com.ibn.arrp.json.models.JTextures;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import java.nio.file.Path;

public class RRPPreTest {
	public static void main(String[] args) {
		RuntimeResourcePack pack = RuntimeResourcePack.create("test:test");
		pack.addLang( Identifier.of("aaaa:aaaa"), new JLang().entry("aaaa", "bbbbb"));
		pack.dumpDirect(Path.of("aaaa"));

		JState iron_block = state(variant(JState.model("block/iron_block")));
		JState oak_fence = state(multipart(JState.model("block/oak_fence_post")),
				multipart(JState.model("block/oak_fence_side").uvlock()).when(when().add("north", "true")),
				multipart(JState.model("block/oak_fence_side").y(90).uvlock()).when(when().add("east", "true")),
				multipart(JState.model("block/oak_fence_side").y(180).uvlock()).when(when().add("south", "true")),
				multipart(JState.model("block/oak_fence_side").y(270).uvlock()).when(when().add("west", "true")));

		JModel model = model().textures(textures().var("all", "block/bamboo_stalk").particle("block/bamboo_stalk"))
		                      .element(element().from(7, 0, 7)
		                                        .to(9, 16, 9)
		                                        .faces(faces().down(face("all").cullface(Direction.DOWN).uv(13, 4, 15, 6))
		                                                      .up(face("all").cullface(Direction.UP).uv(13, 0, 15, 2))
		                                                      .north(face("all").uv(9, 0, 11, 16))
		                                                      .south(face("all").uv(9, 0, 11, 16))
		                                                      .west(face("all").uv(9, 0, 11, 16))
		                                                      .east(face("all").uv(9, 0, 11, 16))));



		Gson gson = new GsonBuilder().registerTypeAdapter(JMultipart.class, new JMultipart.Serializer())
		                             .registerTypeAdapter(JWhen.class, new JWhen.Serializer())
		                             .registerTypeAdapter(JState.class, new JState.Serializer())
		                             .registerTypeAdapter(JVariant.class, new JVariant.Serializer())
		                             .registerTypeAdapter(JTextures.class, new JTextures.Serializer())
		                             .setPrettyPrinting()
		                             .create();

		JLang lang = JLang.lang().allPotionOf(Identifier.of("mod_id", "potion_id"), "Example");

		System.out.println(RuntimeResourcePackImpl.GSON.toJson(loot("minecraft:block").pool(pool().rolls(1)
                                                                                                  .entry(entry().type("minecraft:item").name("minecraft:diamond"))
                                                                                                  .condition(condition("minecraft:survives_explosion")))));
		//System.out.println(gson.toJson(iron_block));
		//System.out.println(gson.toJson(oak_fence));
		System.out.println(gson.toJson(model));

		System.out.println(gson.toJson(lang));
	}
}
