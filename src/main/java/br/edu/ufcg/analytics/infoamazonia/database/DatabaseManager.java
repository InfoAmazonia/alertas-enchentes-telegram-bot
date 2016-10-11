/*
 * This is the source code of Telegram Bot v. 2.0
 * It is licensed under GNU GPL v. 3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Ruben Bermudez, 3/12/14.
 */
package br.edu.ufcg.analytics.infoamazonia.database;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.edu.ufcg.analytics.infoamazonia.River;
import br.edu.ufcg.analytics.infoamazonia.State;

/**
 * @author Ruben Bermudez
 * @version 2.0
 * @brief Database Manager to perform database operations
 * @date 3/12/14
 */
public class DatabaseManager {

	private static final String LOGTAG = "DATABASEMANAGER";

	private static volatile DatabaseManager instance;

	private State state;

	private Map<River, List<Integer>> alerts;

	/**
	 * Private constructor (due to Singleton)
	 */
	private DatabaseManager() {
		state = State.START;
		alerts = new HashMap<>();
		for (River river : River.values()) {
			alerts.put(river, new LinkedList<>());
		}
	}

	/**
	 * Get Singleton instance
	 *
	 * @return instance of the class
	 */
	public static DatabaseManager getInstance() {
		final DatabaseManager currentInstance;
		if (instance == null) {
			synchronized (DatabaseManager.class) {
				if (instance == null) {
					instance = new DatabaseManager();
				}
				currentInstance = instance;
			}
		} else {
			currentInstance = instance;
		}
		return currentInstance;
	}

	public State getState(Integer id, Long chatId) {
		return this.state;
	}

	public void setState(Integer integer, Long long1, State state) {
		this.state = state;
	}

	public String getLastStatus(Long code, Integer userId, String language) {
		return "Boletim do Rio " + River.fromCode(code);
	}

	public List<String> getAlertNamesByUser(Integer userId) {
		List<String> list = new LinkedList<>();
		for (Entry<River, List<Integer>> entry : alerts.entrySet()) {
			if(entry.getValue().contains(userId)){
				list.add(entry.getKey().getName());
			}
		}
		return list;
	}
	

	public void createNewAlert(Integer userId, River river) {
		List<Integer> subscriptions = alerts.get(river);
		if(!subscriptions.contains(userId)){
			subscriptions.add(userId);
		}
	}

	public void deleteAlert(Integer userId, River river) {
		List<Integer> subscriptions = alerts.get(river);
		if(subscriptions.contains(userId)){
			subscriptions.remove(userId);
		}
	}

}
