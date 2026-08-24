/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brittney Nguyen
 */
public class MasterResourceReader {

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
						_getURL("/prometheus"), false, 1, null, null,
						_SECONDS_RETRY_PERIOD, timeoutMillis, null, false));
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

	private static final int _SECONDS_RETRY_PERIOD = 1;

	private static final Map<String, MasterResourceReader>
		_masterResourceReaders = new HashMap<>();

	private final String _masterName;
	private String _memoryInfo;
	private final Object _memoryInfoLock = new Object();
	private PrometheusScrape _prometheusScrape;
	private final Object _prometheusScrapeLock = new Object();

}