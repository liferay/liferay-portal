/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Brittney Nguyen
 */
public class MasterResourceReader {

	public static final int RETRIES_SIZE_MAX = 1;

	public static void clearInstances() {
		synchronized (_masterResourceReaders) {
			_masterResourceReaders.clear();
		}
	}

	public static MasterResourceReader getInstance(String masterName) {
		synchronized (_masterResourceReaders) {
			MasterResourceReader masterResourceReader =
				_masterResourceReaders.get(masterName);

			if (masterResourceReader == null) {
				masterResourceReader = new MasterResourceReader(masterName);

				_masterResourceReaders.put(masterName, masterResourceReader);
			}

			return masterResourceReader;
		}
	}

	public Document getJobConfigDocument(String jobName, int timeoutMillis)
		throws DocumentException, IOException {

		return Dom4JUtil.parse(
			JenkinsResultsParserUtil.toString(
				_getURL(
					JenkinsResultsParserUtil.combine(
						"/job/", jobName, "/config.xml")),
				false, RETRIES_SIZE_MAX, null, null, _SECONDS_RETRY_PERIOD,
				timeoutMillis, null, false));
	}

	public Map<String, JSONObject> getJobJSONObjects(int timeoutMillis)
		throws IOException {

		synchronized (_jobJSONObjectsLock) {
			if (_jobJSONObjects == null) {
				_jobJSONObjects = _newJobJSONObjects(timeoutMillis);
			}

			return _jobJSONObjects;
		}
	}

	public String getMemoryInfo() {
		synchronized (_memoryInfoLock) {
			if (_memoryInfo == null) {
				JenkinsMaster jenkinsMaster = JenkinsMaster.getInstance(
					_masterName);

				_memoryInfo = jenkinsMaster.executeBashCommand(
					"cat /proc/meminfo");
			}

			return _memoryInfo;
		}
	}

	public PrometheusScrape getPrometheusScrape(int timeoutMillis)
		throws IOException {

		synchronized (_prometheusScrapeLock) {
			if (_prometheusScrape == null) {
				_prometheusScrape = new PrometheusScrape(
					JenkinsResultsParserUtil.toString(
						_getURL("/prometheus"), false, RETRIES_SIZE_MAX, null,
						null, _SECONDS_RETRY_PERIOD, timeoutMillis, null,
						false));
			}

			return _prometheusScrape;
		}
	}

	private MasterResourceReader(String masterName) {
		_masterName = masterName;
	}

	private String _getURL(String path) {
		JenkinsMaster jenkinsMaster = JenkinsMaster.getInstance(_masterName);

		return JenkinsResultsParserUtil.combine(jenkinsMaster.getURL(), path);
	}

	private Map<String, JSONObject> _newJobJSONObjects(int timeoutMillis)
		throws IOException {

		Map<String, JSONObject> jobJSONObjects = new HashMap<>();

		JSONObject jsonObject = JenkinsResultsParserUtil.toJSONObject(
			_getURL(
				JenkinsResultsParserUtil.combine(
					"/api/json?tree=jobs[buildable,disabled,",
					"lastBuild[building,number,timestamp],",
					"lastCompletedBuild[number,result,timestamp],name]")),
			false, RETRIES_SIZE_MAX, _SECONDS_RETRY_PERIOD, timeoutMillis);

		JSONArray jobsJSONArray = jsonObject.optJSONArray("jobs");

		if (jobsJSONArray == null) {
			return Collections.unmodifiableMap(jobJSONObjects);
		}

		for (int i = 0; i < jobsJSONArray.length(); i++) {
			JSONObject jobJSONObject = jobsJSONArray.optJSONObject(i);

			if (jobJSONObject == null) {
				continue;
			}

			String name = jobJSONObject.optString("name");

			if (JenkinsResultsParserUtil.isNullOrEmpty(name)) {
				continue;
			}

			jobJSONObjects.put(name, jobJSONObject);
		}

		return Collections.unmodifiableMap(jobJSONObjects);
	}

	private static final int _SECONDS_RETRY_PERIOD = 1;

	private static final Map<String, MasterResourceReader>
		_masterResourceReaders = new HashMap<>();

	private Map<String, JSONObject> _jobJSONObjects;
	private final Object _jobJSONObjectsLock = new Object();
	private final String _masterName;
	private String _memoryInfo;
	private final Object _memoryInfoLock = new Object();
	private PrometheusScrape _prometheusScrape;
	private final Object _prometheusScrapeLock = new Object();

}