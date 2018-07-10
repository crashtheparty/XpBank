package org.ctp.xpbank.threads;

import java.util.UUID;

import org.ctp.xpbank.XpBank;
import org.ctp.xpbank.commands.Open;

public class OpenTime implements Runnable{
	
	private UUID player;
	private int scheduler, runTime;
	
	public OpenTime(UUID uuid) {
		runTime = XpBank.getConfigUtils().getAccessTime();
		player = uuid;
	}

	@Override
	public void run() {
		if(runTime <= 0) {
			Open.revokeAccess(this);
		}
		runTime--;
	}

	public int getScheduler() {
		return scheduler;
	}

	public void setScheduler(int scheduler) {
		this.scheduler = scheduler;
	}

	public UUID getPlayer() {
		return player;
	}
	
	public int getRunTime() {
		return runTime;
	}

}
