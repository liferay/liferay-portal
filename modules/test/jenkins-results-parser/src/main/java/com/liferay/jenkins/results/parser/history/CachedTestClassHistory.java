/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.history;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class CachedTestClassHistory extends BaseTestClassHistory {

	@Override
	public long getAverageDuration() {
		return _jsonObject.optLong("averageDuration");
	}

	@Override
	public long getAverageOverheadDuration() {
		return _jsonObject.optLong("averageOverheadDuration");
	}

	@Override
	public int getFailureCount() {
		return _jsonObject.optInt("failureCount");
	}

	@Override
	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	@Override
	public int getStatusChanges() {
		return _jsonObject.optInt("statusChanges");
	}

	@Override
	public long getTestCount() {
		return _jsonObject.optInt("testCount");
	}

	@Override
	public String getTestTaskName() {
		return _jsonObject.optString("testTaskName");
	}

	@Override
	public URL getTestrayCaseURL() {
		String testrayCaseURL = _jsonObject.optString("testrayCaseURL");

		if (!JenkinsResultsParserUtil.isURL(testrayCaseURL)) {
			return null;
		}

		try {
			return new URL(testrayCaseURL);
		}
		catch (MalformedURLException malformedURLException) {
			return null;
		}
	}

	@Override
	public boolean isFlaky() {
		return _jsonObject.optBoolean("flaky");
	}

	protected CachedTestClassHistory(
		BatchHistory batchHistory, JSONObject jsonObject,
		String testClassName) {

		super(batchHistory, testClassName);

		_jsonObject = jsonObject;
	}

	private final JSONObject _jsonObject;

}