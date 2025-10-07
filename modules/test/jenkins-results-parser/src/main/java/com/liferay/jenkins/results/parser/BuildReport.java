/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.net.URL;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public interface BuildReport {

	public int getBuildNumber();

	public JSONObject getBuildReportJSONObject();

	public URL getBuildURL();

	public long getDuration();

	public String getFailureMessage();

	public JenkinsMaster getJenkinsMaster();

	public String getJobName();

	public String getResult();

	public Date getStartDate();

	public StopWatchRecordsGroup getStopWatchRecordsGroup();

	public List<URL> getTestrayAttachmentURLs();

	public boolean isFailing();

}