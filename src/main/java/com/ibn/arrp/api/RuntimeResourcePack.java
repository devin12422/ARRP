package com.ibn.arrp.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import com.ibn.arrp.impl.RuntimeResourcePackImpl;
import com.ibn.arrp.json.animation.JAnimation;
import com.ibn.arrp.json.blockstate.JState;
import com.ibn.arrp.json.lang.JLang;
import com.ibn.arrp.json.loot.JLootTable;
import com.ibn.arrp.json.models.JModel;
import com.ibn.arrp.json.recipe.JRecipe;
import com.ibn.arrp.json.tags.JTag;
import com.ibn.arrp.util.CallableFunction;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

/**
 * a resource pack who's assets and data are evaluated at runtime
 *
 * remember to register it!
 * @see RRPCallback
 */
public interface RuntimeResourcePack extends ResourcePack {
	/**
	 * create a new runtime resource pack with the default supported resource pack version
	 */
    static RuntimeResourcePack create(String id) {
        return new RuntimeResourcePackImpl(Identifier.of(id));
    }

	static RuntimeResourcePack create(Identifier id) {
		return new RuntimeResourcePackImpl(id);
	}

	static RuntimeResourcePack create(Identifier id, int version) {
		return new RuntimeResourcePackImpl(id, version);
	}


	static Identifier id(String namespace, String string) {
        return Identifier.of(namespace, string);}


	/**
	 * reads, clones, and recolors the texture at the given path, and puts the newly created image in the given id.
	 *
	 * <b>if your resource pack is registered at a higher priority than where you expect the texture to be in, mc will
	 * be unable to find the asset you are looking for</b>
	 *
	 * @param identifier the place to put the new texture
	 * @param target the input stream of the original texture
	 * @param pixel the pixel recolorer
	 */
	void addRecoloredImage(Identifier identifier, InputStream target, IntUnaryOperator pixel);

	/**
	 * add a lang file for the given language
	 *
	 * DO **NOT** CALL THIS METHOD MULTIPLE TIMES FOR THE SAME LANGUAGE, THEY WILL OVERRIDE EACH OTHER!
	 * <p>
	 * ex. addLang(MyMod.id("en_us"), lang().translate("something.something", "test"))
	 */
	byte[] addLang(Identifier identifier, JLang lang);

	/**
	 * Multiple calls to this method with the same identifier will merge them into one lang file
	 */
	void mergeLang(Identifier identifier, JLang lang);

	/**
	 * adds a loot table
	 */
	byte[] addLootTable(Identifier identifier, JLootTable table);

	/**
	 * adds an async resource, this is evaluated off-thread, this does not hold all resource retrieval unlike
	 *
	 * @see #async(Consumer)
	 */
	Future<byte[]> addAsyncResource(ResourceType type,
			Identifier identifier,
			CallableFunction<Identifier, byte[]> data);

	/**
	 * add a resource that is lazily evaluated
	 */
	void addLazyResource(ResourceType type, Identifier path, BiFunction<RuntimeResourcePack, Identifier, byte[]> data);

	/**
	 * add a raw resource
	 */
	byte[] addResource(ResourceType type, Identifier path, byte[] data);

	/**
	 * adds an async root resource, this is evaluated off-thread, this does not hold all resource retrieval unlike
	 *
	 * A root resource is something like pack.png, pack.mcmeta, etc. By default ARRP generates a default mcmeta
	 * @see #async(Consumer)
	 */
	Future<byte[]> addAsyncRootResource(String path,
			CallableFunction<String, byte[]> data);

	/**
	 * add a root resource that is lazily evaluated.
	 *
	 * A root resource is something like pack.png, pack.mcmeta, etc. By default ARRP generates a default mcmeta
	 */
	void addLazyRootResource(String path, BiFunction<RuntimeResourcePack, String, byte[]> data);

	/**
	 * add a raw resource to the root path
	 *
	 * A root resource is something like pack.png, pack.mcmeta, etc. By default ARRP generates a default mcmeta
	 */
	byte[] addRootResource(String path, byte[] data);

	/**
	 * add a clientside resource
	 */
	byte[] addAsset(Identifier path, byte[] data);

	/**
	 * add a serverside resource
	 */
	byte[] addData(Identifier path, byte[] data);

	/**
	 * add a model, Items should go in item/... and Blocks in block/... ex. mymod:items/my_item ".json" is
	 * automatically
	 * appended to the path
	 */
	byte[] addModel(JModel model, Identifier path);

	/**
	 * adds a blockstate json
	 * <p>
	 * ".json" is automatically appended to the path
	 */
	byte[] addBlockState(JState state, Identifier path);

	/**
	 * adds a texture png
	 * <p>
	 * ".png" is automatically appended to the path
	 */
	byte[] addTexture(Identifier id, BufferedImage image);

	/**
	 * adds an animation json
	 * <p>
	 * ".png.mcmeta" is automatically appended to the path
	 */
	byte[] addAnimation(Identifier id, JAnimation animation);

	/**
	 * add a tag under the id
	 * <p>
	 * ".json" is automatically appended to the path
	 */
	byte[] addTag(Identifier id, JTag tag);

	/**
	 * add a recipe
	 * <p>
	 * ".json" is automatically appended to the path
	 *
	 * @param id the {@linkplain Identifier} identifier of the recipe and that represents its directory
	 * @param recipe the recipe to add
	 * @return the new resource
	 */
	byte[] addRecipe(Identifier id, JRecipe recipe);

	/**
	 * invokes the action on the RRP executor, RRPs are thread-safe you can create expensive assets here, all resources
	 * are blocked until all async tasks are completed invokes the action on the RRP executor, RRPs are thread-safe you
	 * can create expensive assets here, all resources are blocked until all async tasks are completed
	 * <p>
	 * calling an this function from itself will result in a infinite loop
	 *
	 * @see #addAsyncResource(ResourceType, Identifier, CallableFunction)
	 */
	Future<?> async(Consumer<RuntimeResourcePack> action);

	File DEFAULT_OUTPUT = new File("rrp.debug");

	/**
	 * forcefully dump all assets and data
	 */
	default void dump() {
		this.dump(DEFAULT_OUTPUT);
	}

	void dumpDirect(Path path);

	void load(Path path) throws IOException;

	/**
	 * forcefully dump all assets and data to a specified file
	 *
	 * @deprecated use {@link #dump(Path)}
	 */
	@Deprecated
	void dump(File file);

	/**
	 * forcefully dump all assets and data into `namespace;path/`, useful for debugging
	 */
	default void dump(Path path) {
		String id = this.getInfo().id();
		Path folder = path.resolve(id);
		this.dumpDirect(folder);
	}

	/**
	 * @see ByteBufOutputStream
	 */
	void dump(ZipOutputStream stream) throws IOException;

	/**
	 * @see ByteBufInputStream
	 */
	void load(ZipInputStream stream) throws IOException;
}
