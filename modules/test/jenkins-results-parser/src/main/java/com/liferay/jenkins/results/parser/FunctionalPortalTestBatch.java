/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

/**
 * @author Michael Hashimoto
 */
public class FunctionalPortalTestBatch
	extends BasePortalTestBatch<PortalBatchBuildData> {

	@Override
	public void run() {
		try {
			executeBatch();
		}
		catch (AntException antException) {
			throw new RuntimeException(antException);
		}
		finally {
			publishResults();

			publishPoshiReport();
		}
	}

	protected FunctionalPortalTestBatch(
		PortalBatchBuildData portalBatchBuildData, Workspace workspace) {

		super(portalBatchBuildData, workspace);
	}

	protected void publishPoshiReport() {
		PortalBatchBuildData portalBatchBuildData = getBatchBuildData();

		File portalWebTestResultsDir = new File(
			getPrimaryPortalWorkspaceDirectory(), "portal-web/test-results");

		File[] poshiResultsDirs = portalWebTestResultsDir.listFiles();

		for (File poshiResultsDir : poshiResultsDirs) {
			String poshiResultsDirName = poshiResultsDir.getName();

			for (String test : portalBatchBuildData.getTestList()) {
				if (!poshiResultsDirName.contains(test.replace('#', '_'))) {
					continue;
				}

				try {
					JenkinsResultsParserUtil.copy(
						poshiResultsDir,
						new File(
							portalBatchBuildData.getArtifactDir(),
							poshiResultsDirName));
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(portalBatchBuildData.getPortalBranchSHA());
		sb.append(" - ");
		sb.append(portalBatchBuildData.getBatchName());
		sb.append(" - ");
		sb.append("<a href=\"https://");
		sb.append(portalBatchBuildData.getTopLevelMasterHostname());
		sb.append(".liferay.com/userContent/");
		sb.append(portalBatchBuildData.getUserContentRelativePath());
		sb.append("jenkins-report.html\">Jenkins Report</a>");

		sb.append("<ul>");

		for (String test : portalBatchBuildData.getTestList()) {
			String testName = test.replace('#', '_');

			if (!testName.matches("[^\\.]+\\.[^_]+_.+")) {
				testName = "LocalFile." + testName;
			}

			String poshiReportBaseURL = JenkinsResultsParserUtil.combine(
				"https://", portalBatchBuildData.getTopLevelMasterHostname(),
				".liferay.com/userContent/",
				portalBatchBuildData.getUserContentRelativePath(),
				portalBatchBuildData.getRunId(), "/", testName);

			sb.append("<li>");
			sb.append(test);
			sb.append(" - <a href=\"");
			sb.append(poshiReportBaseURL);
			sb.append("/index.html\">index.html</a>");
			sb.append(" - <a href=\"");
			sb.append(poshiReportBaseURL);
			sb.append("/summary.html\">summary.html</a>");
			sb.append(" - <a href=\"");
			sb.append(poshiReportBaseURL);
			sb.append("/console.txt\">console.txt</a>");
		}

		sb.append("</ul>");

		portalBatchBuildData.setBuildDescription(sb.toString());
	}

}