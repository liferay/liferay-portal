/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class StopWatchRecordsGroup implements Iterable<StopWatchRecord> {

	public StopWatchRecordsGroup() {
	}

	public StopWatchRecordsGroup(JSONObject buildResultJSONObject) {
		if ((buildResultJSONObject == null) ||
			!buildResultJSONObject.has("duration")) {

			return;
		}

		StopWatchRecord stopWatchRecord = new StopWatchRecord(
			"total.duration", _startTimestamp,
			buildResultJSONObject.getLong("duration"));

		_startTimestamp += 1000;

		add(stopWatchRecord);

		if (!buildResultJSONObject.has("stopWatchRecords")) {
			return;
		}

		JSONArray stopWatchRecordsJSONArray =
			buildResultJSONObject.getJSONArray("stopWatchRecords");

		for (int i = 0; i < stopWatchRecordsJSONArray.length(); i++) {
			JSONObject childStopWatchRecordJSONObject =
				stopWatchRecordsJSONArray.getJSONObject(i);

			_startTimestamp += 1000;

			long startTimestamp = childStopWatchRecordJSONObject.optLong(
				"startTimestamp", _startTimestamp);

			if (!childStopWatchRecordJSONObject.has("duration") ||
				!childStopWatchRecordJSONObject.has("name")) {

				continue;
			}

			childStopWatchRecordJSONObject.put(
				"startTimestamp", startTimestamp);

			StopWatchRecord childStopWatchRecord = new StopWatchRecord(
				childStopWatchRecordJSONObject);

			stopWatchRecord.addChildStopWatchRecord(childStopWatchRecord);

			add(childStopWatchRecord);

			_addChildStopWatchRecords(
				childStopWatchRecord, childStopWatchRecordJSONObject);
		}
	}

	public void add(StopWatchRecord newStopWatchRecord) {
		_stopWatchRecordsMap.put(
			newStopWatchRecord.getName(), newStopWatchRecord);
	}

	public StopWatchRecord get(String name) {
		return _stopWatchRecordsMap.get(name);
	}

	public List<StopWatchRecord> getAllStopWatchRecords() {
		return new ArrayList<>(_stopWatchRecordsMap.values());
	}

	public JSONArray getJSONArray() {
		JSONArray jsonArray = new JSONArray();

		for (StopWatchRecord stopWatchRecord : getStopWatchRecords()) {
			jsonArray.put(stopWatchRecord.getJSONObject());
		}

		return jsonArray;
	}

	public List<StopWatchRecord> getStopWatchRecords() {
		List<StopWatchRecord> allStopWatchRecords = new ArrayList<>(
			_stopWatchRecordsMap.values());

		Collections.sort(allStopWatchRecords);

		List<StopWatchRecord> parentStopWatchRecords = new ArrayList<>();

		for (StopWatchRecord stopWatchRecord : allStopWatchRecords) {
			for (StopWatchRecord parentStopWatchRecord :
					parentStopWatchRecords) {

				if (parentStopWatchRecord.isParentOf(stopWatchRecord)) {
					parentStopWatchRecord.addChildStopWatchRecord(
						stopWatchRecord);

					break;
				}
			}

			if (stopWatchRecord.getParentStopWatchRecord() != null) {
				continue;
			}

			parentStopWatchRecords.add(stopWatchRecord);
		}

		return parentStopWatchRecords;
	}

	public boolean isEmpty() {
		return _stopWatchRecordsMap.isEmpty();
	}

	@Override
	public Iterator<StopWatchRecord> iterator() {
		List<StopWatchRecord> list = getStopWatchRecords();

		return list.iterator();
	}

	public int size() {
		List<StopWatchRecord> stopWatchRecords = getStopWatchRecords();

		return stopWatchRecords.size();
	}

	private void _addChildStopWatchRecords(
		StopWatchRecord stopWatchRecord, JSONObject stopWatchRecordJSONObject) {

		if (!stopWatchRecordJSONObject.has("childStopWatchRecords")) {
			return;
		}

		JSONArray childStopWatchRecordsJSONArray =
			stopWatchRecordJSONObject.getJSONArray("childStopWatchRecords");

		for (int i = 0; i < childStopWatchRecordsJSONArray.length(); i++) {
			JSONObject childStopWatchRecordJSONObject =
				childStopWatchRecordsJSONArray.getJSONObject(i);

			childStopWatchRecordJSONObject.put(
				"startTimestamp", _startTimestamp);

			_startTimestamp += 1000;

			StopWatchRecord childStopWatchRecord = new StopWatchRecord(
				childStopWatchRecordJSONObject);

			add(childStopWatchRecord);

			stopWatchRecord.addChildStopWatchRecord(childStopWatchRecord);

			_addChildStopWatchRecords(
				childStopWatchRecord, childStopWatchRecordJSONObject);
		}
	}

	private long _startTimestamp;
	private final Map<String, StopWatchRecord> _stopWatchRecordsMap =
		new HashMap<>();

}