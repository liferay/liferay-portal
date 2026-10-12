/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.Build;
import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.TopLevelBuild;

import java.net.URL;

import java.util.Date;
import java.util.Map;

/**
 * @author Michael Hashimoto
 */
public class BuildTestrayContext extends BaseTestrayContext {

	protected BuildTestrayContext(
		BuildDatabase buildDatabase, TopLevelBuild topLevelBuild) {

		super(buildDatabase);

		_topLevelBuild = topLevelBuild;
	}

	@Override
	protected int getControllerBuildNumber() {
		Build controllerBuild = _topLevelBuild.getControllerBuild();

		return controllerBuild.getBuildNumber();
	}

	@Override
	protected Map<String, String> getControllerBuildParameters() {
		Build controllerBuild = _topLevelBuild.getControllerBuild();

		return controllerBuild.getParameters();
	}

	@Override
	protected JenkinsMaster getControllerJenkinsMaster() {
		Build controllerBuild = _topLevelBuild.getControllerBuild();

		return controllerBuild.getJenkinsMaster();
	}

	@Override
	protected String getControllerJobName() {
		Build controllerBuild = _topLevelBuild.getControllerBuild();

		return controllerBuild.getJobName();
	}

	@Override
	protected Date getControllerStartDate() {
		Build controllerBuild = _topLevelBuild.getControllerBuild();

		return new Date(controllerBuild.getStartTime());
	}

	@Override
	protected String getTestSuiteName() {
		return _topLevelBuild.getTestSuiteName();
	}

	@Override
	protected int getTopLevelBuildNumber() {
		return _topLevelBuild.getBuildNumber();
	}

	@Override
	protected synchronized Map<String, String> getTopLevelBuildParameters() {
		if (_buildParameters != null) {
			return _buildParameters;
		}

		_buildParameters = _topLevelBuild.getParameters();

		return _buildParameters;
	}

	@Override
	protected JenkinsMaster getTopLevelJenkinsMaster() {
		return _topLevelBuild.getJenkinsMaster();
	}

	@Override
	protected String getTopLevelJobName() {
		return _topLevelBuild.getJobName();
	}

	@Override
	protected Date getTopLevelStartDate() {
		return new Date(_topLevelBuild.getStartTime());
	}

	@Override
	protected URL getTopLevelTestrayAttachmentURLBySuffix(String suffix) {
		for (URL testrayAttachmentURL :
				_topLevelBuild.getTestrayAttachmentURLs()) {

			String testrayAttachmentURLString = String.valueOf(
				testrayAttachmentURL);

			if (testrayAttachmentURLString.endsWith(suffix)) {
				return testrayAttachmentURL;
			}
		}

		return null;
	}

	@Override
	protected boolean hasControllerBuild() {
		if (_topLevelBuild.getControllerBuild() == null) {
			return false;
		}

		return true;
	}

	private Map<String, String> _buildParameters;
	private final TopLevelBuild _topLevelBuild;

}