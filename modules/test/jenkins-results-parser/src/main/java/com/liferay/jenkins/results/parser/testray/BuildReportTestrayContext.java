/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.ControllerBuildReport;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;

import java.net.URL;

import java.util.Date;
import java.util.Map;

/**
 * @author Michael Hashimoto
 */
public class BuildReportTestrayContext extends BaseTestrayContext {

	protected BuildReportTestrayContext(
		BuildDatabase buildDatabase, TopLevelBuildReport topLevelBuildReport) {

		super(buildDatabase);

		_topLevelBuildReport = topLevelBuildReport;
	}

	@Override
	protected int getControllerBuildNumber() {
		ControllerBuildReport controllerBuildReport =
			_topLevelBuildReport.getControllerBuildReport();

		return controllerBuildReport.getBuildNumber();
	}

	@Override
	protected Map<String, String> getControllerBuildParameters() {
		ControllerBuildReport controllerBuildReport =
			_topLevelBuildReport.getControllerBuildReport();

		return controllerBuildReport.getBuildParameters();
	}

	@Override
	protected JenkinsMaster getControllerJenkinsMaster() {
		ControllerBuildReport controllerBuildReport =
			_topLevelBuildReport.getControllerBuildReport();

		return controllerBuildReport.getJenkinsMaster();
	}

	@Override
	protected String getControllerJobName() {
		ControllerBuildReport controllerBuildReport =
			_topLevelBuildReport.getControllerBuildReport();

		return controllerBuildReport.getJobName();
	}

	@Override
	protected Date getControllerStartDate() {
		ControllerBuildReport controllerBuildReport =
			_topLevelBuildReport.getControllerBuildReport();

		return controllerBuildReport.getStartDate();
	}

	@Override
	protected synchronized String getTestSuiteName() {
		if (_testSuiteName != null) {
			return _testSuiteName;
		}

		_testSuiteName = _topLevelBuildReport.getTestSuiteName();

		return _testSuiteName;
	}

	@Override
	protected int getTopLevelBuildNumber() {
		return _topLevelBuildReport.getBuildNumber();
	}

	@Override
	protected synchronized Map<String, String> getTopLevelBuildParameters() {
		if (_buildParameters != null) {
			return _buildParameters;
		}

		_buildParameters = _topLevelBuildReport.getBuildParameters();

		return _buildParameters;
	}

	@Override
	protected JenkinsMaster getTopLevelJenkinsMaster() {
		return _topLevelBuildReport.getJenkinsMaster();
	}

	@Override
	protected String getTopLevelJobName() {
		return _topLevelBuildReport.getJobName();
	}

	@Override
	protected Date getTopLevelStartDate() {
		return _topLevelBuildReport.getStartDate();
	}

	@Override
	protected URL getTopLevelTestrayAttachmentURLBySuffix(String suffix) {
		return _topLevelBuildReport.getTestrayAttachmentURLBySuffix(suffix);
	}

	@Override
	protected boolean hasControllerBuild() {
		if (_topLevelBuildReport.getControllerBuildReport() == null) {
			return false;
		}

		return true;
	}

	private Map<String, String> _buildParameters;
	private String _testSuiteName;
	private final TopLevelBuildReport _topLevelBuildReport;

}