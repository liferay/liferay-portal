/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.scancode;

import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

/**
 * @author Brittney Nguyen
 */
public class InspectPackagesScanCodePipeline extends BaseScanCodePipeline {

	@Override
	public void execute() throws IOException, TimeoutException {
		invokeScan(getJSONObject());

		waitForScan(_pipelineName);

		addAdditionalPipeline("populate_purldb");

		waitForScan("populate_purldb");

		sendSlackNotification(getCloudBucketURL());
	}

	public JSONObject getJSONObject() throws IOException {
		JSONObject jsonObject = new JSONObject();

		SimpleDateFormat simpleDateFormat = getSimpleDateFormat();

		jsonObject.put(
			"execute_now", true
		).put(
			"input_urls",
			"https://github.com/liferay/liferay-portal/archive/refs/heads" +
				"/master.tar.gz"
		).put(
			"labels", getLabels("master")
		).put(
			"name", "Master Daily Scan-" + simpleDateFormat.format(new Date())
		).put(
			"pipeline", "inspect_packages"
		);

		return jsonObject;
	}

	protected InspectPackagesScanCodePipeline(
		String buildURL, String pipelineName) {

		super(buildURL, pipelineName);

		_pipelineName = pipelineName;
	}

	private final String _pipelineName;

}