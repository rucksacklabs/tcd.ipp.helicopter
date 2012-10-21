package de.reneruck.tcd.ipp.helicopter.actions;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.reneruck.tcd.ipp.datamodel.Callback;
import de.reneruck.tcd.ipp.datamodel.Datagram;
import de.reneruck.tcd.ipp.datamodel.Statics;
import de.reneruck.tcd.ipp.datamodel.TemporalTransitionsStore;
import de.reneruck.tcd.ipp.datamodel.Transition;

public class DataSender extends Thread {

	private TemporalTransitionsStore transitionsStore;
	private ObjectOutputStream out;
	private Callback callback;

	public DataSender(ObjectOutputStream out, TemporalTransitionsStore transitionsStore, Callback callback) {
		this.out = out;
		this.transitionsStore = transitionsStore;
		this.callback = callback;
	}
	
	@Override
	public void run() {
		do {
			for (Transition transition : this.transitionsStore) {
				Map<String, Object> datagramContent = new HashMap<String, Object>();
				datagramContent.put(Statics.CONTENT_TRANSITION, transition);
				try {
					this.out.writeObject(new Datagram(Statics.DATA, datagramContent));
					this.out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (!this.transitionsStore.isEmpty());
		this.callback.handleCallback();
	}

}
