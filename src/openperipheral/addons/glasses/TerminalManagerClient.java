package openperipheral.addons.glasses;

import java.io.DataInput;
import java.io.IOException;
import java.util.*;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.ForgeSubscribe;
import openmods.utils.ByteUtils;
import openperipheral.addons.drawable.*;

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;

// TODO: rewrite receiving
public class TerminalManagerClient {

	public static final byte CLEAR_ALL_FLAG = 0;
	public static final byte CHANGE_FLAG = 1;
	public static final byte PRIVATE_FLAG = 2;

	private HashMap<Short, IDrawable> drawables = new HashMap<Short, IDrawable>();
	private HashMap<Short, IDrawable> privateDrawables = new HashMap<Short, IDrawable>();

	private ArrayList<IDrawable> drawableList = new ArrayList<IDrawable>();

	/***
	 * Sort drawable items by zIndex
	 */
	private Comparator<IDrawable> zIndexComparator = new Comparator<IDrawable>() {
		@Override
		public int compare(IDrawable s1, IDrawable s2) {
			return s1.getZIndex() - s2.getZIndex();
		}
	};

	public TerminalManagerClient() {}

	public Collection<IDrawable> getDrawables() {
		return drawableList;
	}

	@ForgeSubscribe
	public void handlePacket(TerminalDataEvent packet) {
		try {
			// TODO: make EventPacket use PacketChunker
			byte[] bytes = packet.payload;

			if (bytes == null) return;

			DataInput inputStream = ByteStreams.newDataInput(bytes);

			byte type = inputStream.readByte();

			/* Modify only one of the lists at a time */
			HashMap<Short, IDrawable> drawables = this.drawables;
			if ((type & PRIVATE_FLAG) == PRIVATE_FLAG) {
				drawables = privateDrawables;
			}

			/*
			 * Clearing the bridge should remove ALL drawables Clearing only the
			 * private surface should not remove the bridge's drawables
			 */
			if (type == CLEAR_ALL_FLAG || type == PRIVATE_FLAG) {
				// If there is any clearing then private is ALWAYS getting
				// cleared regardless
				privateDrawables.clear();
				// If this isn't a private operation, then clear everything and
				// quickly return
				if ((type & PRIVATE_FLAG) != PRIVATE_FLAG) {
					drawables.clear();
					drawableList.clear();
					return;
				}
				// else we want to run the normal parse code.
				// That way we still rebuild the drawableList at the end if it
				// wasn't a clear all global
			} else {

				// how many drawable objects are in this packet
				short drawableCount = inputStream.readShort();

				for (int i = 0; i < drawableCount; i++) {

					// the change mask specifies which properties have changed
					short changeMask = inputStream.readShort();

					// get the id for this drawable object
					short drawableId = inputStream.readShort();

					// if slot 0 is false, it means we need to remove the object
					if (!ByteUtils.get(changeMask, 0)) {
						drawables.remove(drawableId);
					} else {
						// drawable type means text/box
						byte drawableType = inputStream.readByte();
						IDrawable drawable = null;
						if (drawables.containsKey(drawableId)) {
							drawable = drawables.get(drawableId);
						} else {
							switch (drawableType) {
								case 0:
									drawable = new DrawableText();
									break;
								case 2:
									drawable = new DrawableIcon();
									break;
								case 3:
									drawable = new DrawableLiquid();
									break;
								default:
									drawable = new DrawableBox();
							}
						}
						if (drawable != null) {
							drawable.readFrom(inputStream, changeMask);
							drawables.put(drawableId, drawable);
						}
					}
				}
			}

			/* Rebuild the list with both private and public drawable components */
			drawableList.clear();
			drawableList.addAll(this.drawables.values());
			drawableList.addAll(privateDrawables.values());
			Collections.sort(drawableList, zIndexComparator);
		} catch (IOException e) {
			Throwables.propagate(e);
		}
	}

	@ForgeSubscribe
	public void onRenderGameOverlay(RenderGameOverlayEvent evt) {
		if (evt.type == ElementType.HELMET
				&& evt instanceof RenderGameOverlayEvent.Post) {
			for (IDrawable drawable : drawableList) {
				drawable.draw(evt.partialTicks);
			}
		}
	}
}
