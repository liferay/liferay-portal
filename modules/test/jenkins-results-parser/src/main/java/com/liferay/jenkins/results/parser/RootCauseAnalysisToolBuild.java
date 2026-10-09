/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class RootCauseAnalysisToolBuild extends DefaultTopLevelBuild {

	@Override
	public String getBaseGitRepositoryName() {
		String branchName = getBranchName();

		if (branchName.equals("master")) {
			return "liferay-portal";
		}

		return "liferay-portal-ee";
	}

	@Override
	public String getBranchName() {
		return getParameterValue("PORTAL_UPSTREAM_BRANCH_NAME");
	}

	@Override
	public synchronized Element getJenkinsReportElement() {
		if (_workspaceGitRepository == null) {
			throw new IllegalStateException(
				"Please set the workspace Git repository");
		}

		if (_downstreamPortalBuildDataList == null) {
			throw new IllegalStateException(
				"Please set the downstream portal build data list");
		}

		return Dom4JUtil.getNewElement(
			"html", null, getJenkinsReportHeadElement(),
			getJenkinsReportBodyElement());
	}

	public void setDownstreamPortalBuildDataList(
		List<PortalBuildData> downstreamPortalBuildDataList) {

		_downstreamPortalBuildDataList = downstreamPortalBuildDataList;
	}

	public void setWorkspaceGitRepository(
		WorkspaceGitRepository workspaceGitRepository) {

		_workspaceGitRepository = workspaceGitRepository;
	}

	protected RootCauseAnalysisToolBuild(String buildURL) {
		this(buildURL, null);
	}

	protected RootCauseAnalysisToolBuild(
		String buildURL, TopLevelBuild topLevelBuild) {

		super(buildURL, topLevelBuild);
	}

	protected Element getBuildDurationCellElement(
		PortalBuildData portalBuildData) {

		if (portalBuildData == null) {
			return getEmptyCellElement();
		}

		return Dom4JUtil.getNewElement(
			"td", null,
			JenkinsResultsParserUtil.toDurationString(
				portalBuildData.getBuildDuration()));
	}

	protected Element getBuildLinkCellElement(PortalBuildData portalBuildData) {
		if (portalBuildData == null) {
			return getEmptyCellElement();
		}

		return Dom4JUtil.getNewElement(
			"td", null,
			Dom4JUtil.getNewAnchorElement(
				portalBuildData.getBuildURL(), "build"));
	}

	protected Element getBuildResultCellElement(
		PortalBuildData portalBuildData) {

		if (portalBuildData == null) {
			return getEmptyCellElement();
		}

		return Dom4JUtil.getNewElement(
			"td", null, portalBuildData.getBuildResult());
	}

	protected Element getBuildStatusCellElement(
		PortalBuildData portalBuildData) {

		if (portalBuildData == null) {
			return getEmptyCellElement();
		}

		return Dom4JUtil.getNewElement(
			"td", null, portalBuildData.getBuildStatus());
	}

	protected Element getCommitDateCellElement(LocalGitCommit localGitCommit) {
		return Dom4JUtil.getNewElement(
			"td", null,
			StringEscapeUtils.escapeXml10(
				JenkinsResultsParserUtil.toDateString(
					localGitCommit.getCommitDate(), _DATE_FORMAT_COMMIT,
					"PST")));
	}

	protected Element getCommitDiffLinkCellElement(
		LocalGitCommit localGitCommit, GitCommitGroup currentGitCommitGroup,
		GitCommitGroup nextGitCommitGroup) {

		if ((nextGitCommitGroup == null) ||
			(currentGitCommitGroup.size() <= 1)) {

			return getEmptyCellElement();
		}

		LocalGitCommit firstNextLocalGitCommit = nextGitCommitGroup.get(0);

		String gitHubCommitDiffURL = _workspaceGitRepository.getGitHubURL();

		gitHubCommitDiffURL = gitHubCommitDiffURL.replaceAll(
			"/tree/.+", "/compare/");

		return Dom4JUtil.getNewElement(
			"td", null,
			Dom4JUtil.getNewAnchorElement(
				JenkinsResultsParserUtil.combine(
					gitHubCommitDiffURL, firstNextLocalGitCommit.getSHA(),
					"...", localGitCommit.getSHA()),
				JenkinsResultsParserUtil.combine(
					String.valueOf(currentGitCommitGroup.size()), " commits")));
	}

	protected Element getCommitGroupHeaderRowElement(
		LocalGitCommit localGitCommit, PortalBuildData portalBuildData,
		GitCommitGroup currentGitCommitGroup, GitCommitGroup nextGitCommitGroup,
		boolean firstCommit) {

		return Dom4JUtil.getNewElement(
			"tr", null,
			getCommitGroupHeaderToggleCellElement(
				localGitCommit, currentGitCommitGroup),
			getCommitLinkCellElement(localGitCommit, firstCommit),
			getCommitDateCellElement(localGitCommit),
			getCommitMessageCellElement(localGitCommit),
			getCommitDiffLinkCellElement(
				localGitCommit, currentGitCommitGroup, nextGitCommitGroup),
			getBuildLinkCellElement(portalBuildData),
			getBuildDurationCellElement(portalBuildData),
			getBuildStatusCellElement(portalBuildData),
			getBuildResultCellElement(portalBuildData));
	}

	protected Element getCommitGroupHeaderToggleCellElement(
		LocalGitCommit localGitCommit, GitCommitGroup currentGitCommitGroup) {

		if ((currentGitCommitGroup == null) ||
			(currentGitCommitGroup.size() <= 1)) {

			return getEmptyCellElement();
		}

		Element labelElement = Dom4JUtil.getNewElement("label", null, "+");

		labelElement.addAttribute("for", localGitCommit.getSHA());

		Element inputElement = Dom4JUtil.getNewElement("input", null);

		inputElement.addAttribute("data-toggle", "toggle");
		inputElement.addAttribute("id", localGitCommit.getSHA());
		inputElement.addAttribute("name", localGitCommit.getSHA());
		inputElement.addAttribute("type", "checkbox");

		return Dom4JUtil.getNewElement("td", null, labelElement, inputElement);
	}

	protected Element getCommitGroupRowElement(LocalGitCommit localGitCommit) {
		return Dom4JUtil.getNewElement(
			"tr", null,
			getCommitGroupHeaderToggleCellElement(localGitCommit, null),
			getCommitLinkCellElement(localGitCommit, false),
			getCommitDateCellElement(localGitCommit),
			getCommitMessageCellElement(localGitCommit), getEmptyCellElement(),
			getEmptyCellElement(), getEmptyCellElement(), getEmptyCellElement(),
			getEmptyCellElement());
	}

	protected List<GitCommitGroup> getCommitGroups() {
		List<PortalBuildData> portalBuildDataList = Lists.newArrayList(
			_downstreamPortalBuildDataList);

		List<GitCommitGroup> gitCommitGroups = new ArrayList<>(
			_downstreamPortalBuildDataList.size());

		GitCommitGroup gitCommitGroup = null;

		List<LocalGitCommit> historicalLocalGitCommits =
			_workspaceGitRepository.getHistoricalLocalGitCommits();

		if (portalBuildDataList.size() > 1) {
			PortalBuildData firstPortalBuildData = portalBuildDataList.get(0);
			PortalBuildData secondPortalBuildData = portalBuildDataList.get(1);

			String firstPortalBuildDataPortalBranchSHA =
				firstPortalBuildData.getPortalBranchSHA();
			String secondPortalBuildDataPortalBranchSHA =
				secondPortalBuildData.getPortalBranchSHA();

			if (firstPortalBuildDataPortalBranchSHA.equals(
					secondPortalBuildDataPortalBranchSHA)) {

				LocalGitCommit retestLocalGitCommit = null;

				for (LocalGitCommit historicalLocalGitCommit :
						historicalLocalGitCommits) {

					String sha = historicalLocalGitCommit.getSHA();

					if (sha.equals(firstPortalBuildDataPortalBranchSHA)) {
						retestLocalGitCommit = historicalLocalGitCommit;

						break;
					}
				}

				for (PortalBuildData portalBuildData : portalBuildDataList) {
					if (portalBuildData != null) {
						gitCommitGroup = new GitCommitGroup(portalBuildData);

						gitCommitGroups.add(gitCommitGroup);
					}
					else {
						gitCommitGroup = new GitCommitGroup(null);

						gitCommitGroups.add(gitCommitGroup);
					}

					if (retestLocalGitCommit == null) {
						continue;
					}

					gitCommitGroup.add(retestLocalGitCommit);
				}

				return gitCommitGroups;
			}
		}

		for (int i = 0; i < historicalLocalGitCommits.size(); i++) {
			LocalGitCommit historicalLocalGitCommit =
				historicalLocalGitCommits.get(i);

			String sha = historicalLocalGitCommit.getSHA();

			PortalBuildData portalBuildData = null;

			for (PortalBuildData currentPortalBuildData : portalBuildDataList) {
				if (sha.equals(currentPortalBuildData.getPortalBranchSHA())) {
					portalBuildData = currentPortalBuildData;

					break;
				}
			}

			if (portalBuildData != null) {
				portalBuildDataList.remove(portalBuildData);

				gitCommitGroup = new GitCommitGroup(portalBuildData);

				gitCommitGroups.add(gitCommitGroup);
			}
			else if (i == 0) {
				gitCommitGroup = new GitCommitGroup(null);

				gitCommitGroups.add(gitCommitGroup);
			}

			gitCommitGroup.add(historicalLocalGitCommit);
		}

		return gitCommitGroups;
	}

	protected Element getCommitLinkCellElement(
		LocalGitCommit localGitCommit, boolean header) {

		String prefix = "";

		if (header) {
			prefix = "*";
		}

		String gitHubCommitURL = _workspaceGitRepository.getGitHubURL();

		gitHubCommitURL = gitHubCommitURL.replaceAll(
			"/tree/.+", "/commit/" + localGitCommit.getSHA());

		return Dom4JUtil.getNewElement(
			"td", null,
			Dom4JUtil.getNewAnchorElement(
				gitHubCommitURL, prefix + localGitCommit.getAbbreviatedSHA()));
	}

	protected Element getCommitMessageCellElement(
		LocalGitCommit localGitCommit) {

		return Dom4JUtil.getNewElement(
			"td", null,
			StringEscapeUtils.escapeXml10(localGitCommit.getMessage()));
	}

	protected Element getEmptyCellElement() {
		return Dom4JUtil.getNewElement("td");
	}

	@Override
	protected Element getJenkinsReportBodyElement() {
		Element subheadingElement = null;

		JSONObject jobJSONObject = getBuildJSONObject();

		String description = jobJSONObject.optString("description");

		if (!description.isEmpty()) {
			subheadingElement = Dom4JUtil.getNewElement("h2");

			try {
				Dom4JUtil.addRawXMLToElement(subheadingElement, description);
			}
			catch (DocumentException documentException) {
				throw new RuntimeException(
					"Unable to parse description HTML " + description,
					documentException);
			}
		}

		String buildURL = getBuildURL();

		Element headingElement = Dom4JUtil.getNewElement(
			"h1", null, "Jenkins report for ",
			Dom4JUtil.getNewAnchorElement(buildURL, buildURL));

		return Dom4JUtil.getNewElement(
			"body", null, headingElement, subheadingElement,
			getJenkinsReportSummaryElement(), getJenkinsReportTimelineElement(),
			getJenkinsReportTableElement(),
			Dom4JUtil.getNewElement(
				"p", null,
				Dom4JUtil.getNewElement(
					"em", null, "Indicates HEAD Commit (*)")));
	}

	@Override
	protected Element getJenkinsReportHeadElement() {
		return Dom4JUtil.getNewElement(
			"head", null, getJenkinsReportHeadJQueryElement(),
			getJenkinsReportHeadScriptElement(),
			getJenkinsReportHeadStyleElement());
	}

	protected Element getJenkinsReportHeadJQueryElement() {
		Element jqueryElement = Dom4JUtil.getNewElement("script");

		jqueryElement.addAttribute("src", _URL_JQUERY);
		jqueryElement.addAttribute("type", "text/javascript");
		jqueryElement.addText("");

		return jqueryElement;
	}

	protected Element getJenkinsReportHeadScriptElement() {
		Element scriptElement = Dom4JUtil.getNewElement("script");

		scriptElement.addAttribute("type", "text/javascript");

		StringBuilder sb = new StringBuilder();

		sb.append("$(document).ready(function() {\n");
		sb.append("$('[data-toggle=\"toggle\"]').change(function() {\n");
		sb.append("$(this).parents().next('.hidden-row').toggle();\n");
		sb.append("var label = $(this).parent('td').find('label');\n");
		sb.append("var text = label.text();\n");
		sb.append("if (text == '+') { text = '-' }\n");
		sb.append("else { text = '+' }\n");
		sb.append("label.text(text);\n");
		sb.append("});\n");
		sb.append("});\n");

		scriptElement.addText(sb.toString());

		return scriptElement;
	}

	protected Element getJenkinsReportHeadStyleElement() {
		StringBuilder sb = new StringBuilder();

		sb.append("body {\n");
		sb.append("font-family: sans-serif;\n");
		sb.append("}\n");

		sb.append("canvas {\n");
		sb.append("display: block;\n");
		sb.append("height: 300px;\n");
		sb.append("width: 1900px;\n");
		sb.append("}\n");

		sb.append("table {\n");
		sb.append("width: 1500px;\n");
		sb.append("}\n");

		sb.append("table > caption, td, th {\n");
		sb.append("padding: 3px;\n");
		sb.append("text-align: left;\n");
		sb.append("}\n");

		sb.append("th {\n");
		sb.append("background-color: #CCCCCC;\n");
		sb.append("font-weight: bold;\n");
		sb.append("}\n");

		sb.append("td {\n");
		sb.append("background-color: #EEEEEE;\n");
		sb.append("max-width: 250px;\n");
		sb.append("overflow: hidden;\n");
		sb.append("text-overflow: ellipsis;\n");
		sb.append("white-space: nowrap;\n");
		sb.append("}\n");

		sb.append("td:nth-child(1) {\n");
		sb.append("text-align: center;\n");
		sb.append("width: 20px;\n");
		sb.append("}\n");

		sb.append("td:nth-child(3) {\n");
		sb.append("width: 250px;\n");
		sb.append("}\n");

		sb.append(".hidden-row {\n");
		sb.append("display: none;\n");
		sb.append("}\n");

		sb.append(".result-row tr td {\n");
		sb.append("background-color: #DDDDDD;\n");
		sb.append("}\n");

		sb.append(".result-row tr td label {\n");
		sb.append("cursor: pointer;\n");
		sb.append("display: block;\n");
		sb.append("}\n");

		sb.append("[data-toggle=\"toggle\"] {\n");
		sb.append("display: none;\n");
		sb.append("}\n");

		return Dom4JUtil.getNewElement("style", null, sb.toString());
	}

	protected Element getJenkinsReportTableBodyElement() {
		Element tableBodyElement = Dom4JUtil.getNewElement("tbody");

		List<GitCommitGroup> gitCommitGroups = getCommitGroups();

		for (int i = 0; i < gitCommitGroups.size(); i++) {
			GitCommitGroup gitCommitGroup = gitCommitGroups.get(i);

			PortalBuildData portalBuildData =
				gitCommitGroup.getPortalBuildData();

			Element commitGroupHeaderElement = Dom4JUtil.getNewElement(
				"tbody", tableBodyElement);

			commitGroupHeaderElement.addAttribute("class", "result-row");

			GitCommitGroup nextGitCommitGroup = null;

			if (gitCommitGroups.size() > (i + 1)) {
				nextGitCommitGroup = gitCommitGroups.get(i + 1);
			}

			boolean firstCommit = false;

			if (i == 0) {
				firstCommit = true;
			}

			Dom4JUtil.addToElement(
				commitGroupHeaderElement,
				getCommitGroupHeaderRowElement(
					gitCommitGroup.get(0), portalBuildData, gitCommitGroup,
					nextGitCommitGroup, firstCommit));

			if (gitCommitGroup.size() <= 1) {
				continue;
			}

			Element commitGroupElement = Dom4JUtil.getNewElement(
				"tbody", tableBodyElement);

			commitGroupElement.addAttribute("class", "hidden-row");

			for (int j = 1; j < gitCommitGroup.size(); j++) {
				Dom4JUtil.addToElement(
					commitGroupElement,
					getCommitGroupRowElement(gitCommitGroup.get(j)));
			}
		}

		return tableBodyElement;
	}

	@Override
	protected Element getJenkinsReportTableColumnHeadersElement() {
		Element toggleElement = Dom4JUtil.getNewElement("th", null, "");

		Element commitSHAElement = Dom4JUtil.getNewElement(
			"th", null, "Commit SHA");

		Element commitDateElement = Dom4JUtil.getNewElement(
			"th", null, "Commit Date");

		Element commitMessageElement = Dom4JUtil.getNewElement(
			"th", null, "Commit Message");

		Element commitDiffElement = Dom4JUtil.getNewElement(
			"th", null, "Commit Diffs");

		Element buildLinkElement = Dom4JUtil.getNewElement(
			"th", null, "Build Link");

		Element buildTimeElement = Dom4JUtil.getNewElement(
			"th", null, "Build Time");

		Element buildStatusElement = Dom4JUtil.getNewElement(
			"th", null, "Build Status");

		Element buildResultElement = Dom4JUtil.getNewElement(
			"th", null, "Build Result");

		Element tableColumnHeaderElement = Dom4JUtil.getNewElement("tr");

		Dom4JUtil.addToElement(
			tableColumnHeaderElement, toggleElement, commitSHAElement,
			commitDateElement, commitMessageElement, commitDiffElement,
			buildLinkElement, buildTimeElement, buildStatusElement,
			buildResultElement);

		return tableColumnHeaderElement;
	}

	protected Element getJenkinsReportTableElement() {
		Element topLevelTableElement = Dom4JUtil.getNewElement("table");

		String gitHubCommitsURL = _workspaceGitRepository.getGitHubURL();

		gitHubCommitsURL = gitHubCommitsURL.replace("/tree/", "/commits/");

		Element captionElement = Dom4JUtil.getNewElement(
			"caption", topLevelTableElement);

		Dom4JUtil.getNewElement(
			"h2", captionElement, "Commit history of ",
			Dom4JUtil.getNewAnchorElement(gitHubCommitsURL, gitHubCommitsURL));

		Dom4JUtil.addToElement(
			topLevelTableElement, getJenkinsReportTableColumnHeadersElement(),
			getJenkinsReportTableBodyElement());

		return topLevelTableElement;
	}

	protected static class GitCommitGroup extends ArrayList<LocalGitCommit> {

		public GitCommitGroup(PortalBuildData portalBuildData) {
			this.portalBuildData = portalBuildData;
		}

		public PortalBuildData getPortalBuildData() {
			return portalBuildData;
		}

		protected PortalBuildData portalBuildData;

	}

	private static final String _DATE_FORMAT_COMMIT = "yyyy-MM-dd h:mm:ss aa z";

	private static final String _URL_JQUERY =
		"https://ajax.aspnetcdn.com/ajax/jQuery/jquery-3.3.1.min.js";

	private List<PortalBuildData> _downstreamPortalBuildDataList;
	private WorkspaceGitRepository _workspaceGitRepository;

}