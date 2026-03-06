/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.List;

/**
 * @author Michael Hashimoto
 */
public interface WorkspaceGitRepository extends LocalGitRepository {

	public void addPropertyOption(String propertyOption);

	public void fetchGitHubDevBranch();

	public String getBaseBranchSHA();

	public String getBaseBranchSHAShort();

	public String getBranchName();

	public String getFileContent(String filePath);

	public String getGitHubDevBranchName();

	public String getGitHubURL();

	public List<LocalGitCommit> getHistoricalLocalGitCommits();

	public String getSenderBranchName();

	public String getSenderBranchSHA();

	public String getSenderBranchSHAShort();

	public String getSenderBranchUsername();

	public List<List<LocalGitCommit>> partitionLocalGitCommits(
		List<LocalGitCommit> localGitCommits, int count);

	public void setBaseBranchSHA(String branchSHA);

	public void setCommitFileIsSHA(boolean commitFileIsSha);

	public void setGitHubURL(String gitHubURL);

	public void setPatchSHAs(List<String> patchSHAs);

	public void setRebase(boolean rebase);

	public void setSenderBranchSHA(String branchSHA);

	public void setUp();

	public void storeCommitHistory(List<String> commitSHAs);

	public void synchronizeToGitHubDev();

	public void tearDown();

	public void writePropertiesFiles();

}