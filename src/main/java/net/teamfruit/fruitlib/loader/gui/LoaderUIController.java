package net.teamfruit.fruitlib.loader.gui;

import java.awt.Image;

import com.google.common.eventbus.EventBus;

public class LoaderUIController {
	public final EventBus EVENT_BUS = new EventBus();

	private Speed speed = new Speed(20);

	public Speed getSpeed() {
		return this.speed;
	}

	public void setTotal(final long bytes) {
		this.EVENT_BUS.post(new TotalSetEvent(bytes));
	}

	public static interface LoadingContentController {
		LoadingContent getInternalContent();

		void setTitle(String title);

		String getTitle();

		void setImage(Image image);

		Image getImage();

		void setProgressMaximum(int maximum);

		int getProgressMaximum();

		void setProgressValue(int value);

		int getProgressValue();

		void setPause(boolean pause);

		boolean isPause();

		void setStartedTime(String time);

		String getStartedTime();

		void setRemainingTime(String time);

		String getRemainingTime();

		void setDoneTime(String time);

		String getDoneTime();
	}

	static class TotalSetEvent {
		public final long bytes;

		public TotalSetEvent(final long bytes) {
			this.bytes = bytes;
		}
	}
}
