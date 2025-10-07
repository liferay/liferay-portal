/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class WorkspacesCompileBatchTestClassGroup extends BatchTestClassGroup {

	protected WorkspacesCompileBatchTestClassGroup(
		JSONObject jsonObject, PortalTestClassJob portalTestClassJob) {

		super(jsonObject, portalTestClassJob);
	}

	protected WorkspacesCompileBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob) {

		super(batchName, portalTestClassJob);

		setTestClasses();

		setAxisTestClassGroups();

		setSegmentTestClassGroups();
	}

	protected void setTestClasses() {
		for (String workspacesName : _getWorkspacesNames()) {
			File workspaceDir = new File(
				portalGitWorkingDirectory.getWorkingDirectory(),
				"workspaces/" + workspacesName);

			addTestClass(TestClassFactory.newTestClass(this, workspaceDir));
		}
	}

	private List<String> _getWorkspacesNames() {
		List<String> workspaceNames = new ArrayList<>();

		JobProperty jobProperty = getJobProperty(
			"workspaces.names", getTestSuiteName(), getBatchName());

		if ((jobProperty == null) ||
			JenkinsResultsParserUtil.isNullOrEmpty(jobProperty.getValue())) {

			return workspaceNames;
		}

		String workspacesNames = jobProperty.getValue();

		Collections.addAll(workspaceNames, workspacesNames.split(","));

		return workspaceNames;
	}

}