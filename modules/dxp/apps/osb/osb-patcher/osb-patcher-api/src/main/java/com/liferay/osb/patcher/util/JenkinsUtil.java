/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.jenkins.results.parser.LoadBalancerUtil;
import com.liferay.osb.patcher.constants.JenkinsConstants;
import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.constants.PatcherBuildConstants;
import com.liferay.osb.patcher.constants.PatcherConstants;
import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.constants.PatcherProjectVersionConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixComponentLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zsolt Balogh
 */
public class JenkinsUtil {

	public static Map<String, String>
			getAgentJenkinsPatcherBuildRequestParameters(
				PatcherProjectVersion patcherProjectVersion,
				PatcherFix patcherFix, String patcherFixIds)
		throws Exception {

		Map<String, String> jenkinsRequestParameters = new HashMap<>();

		jenkinsRequestParameters.put(
			"osb.patcher.fixId", String.valueOf(patcherFix.getPatcherFixId()));
		jenkinsRequestParameters.put("osb.patcher.fixIds", patcherFixIds);
		jenkinsRequestParameters.put(
			"osb.patcher.type",
			PortletPropsValues.
				OSB_PATCHER_SHARED_REQUEST_BUILD_PATCH_PATCHER_TYPE);

		if (!patcherProjectVersion.isCombinedBranch()) {
			String siblingCommittish = StringPool.BLANK;

			PatcherFix siblingMainPatcherFix =
				PatcherFixUtil.fetchSiblingChildPatcherBuildMainFix(patcherFix);

			if (siblingMainPatcherFix != null) {
				siblingCommittish =
					PortletPropsValues.OSB_PATCHER_GIT_TAG_PREFIX +
						siblingMainPatcherFix.getPatcherFixId();
			}
			else {
				PatcherProjectVersion siblingPatcherProjectVersion =
					PatcherProjectVersionUtil.getSiblingPatcherProjectVersion(
						patcherProjectVersion.getCommittish());

				siblingCommittish =
					siblingPatcherProjectVersion.getCommittish();
			}

			jenkinsRequestParameters.put(
				"osb.patcher.siblingCommittish", siblingCommittish);
		}

		return getAgentJenkinsRequestParameters(
			patcherProjectVersion, patcherFix, jenkinsRequestParameters);
	}

	public static Map<String, String>
			getAgentJenkinsPatcherFixRequestParameters(
				PatcherProjectVersion patcherProjectVersion,
				PatcherFix patcherFix)
		throws Exception {

		Map<String, String> jenkinsRequestParameters = new HashMap<>();

		jenkinsRequestParameters.put(
			"osb.patcher.committish",
			String.valueOf(patcherFix.getCommittish()));

		if (patcherFix.getStatus() == WorkflowConstants.STATUS_FIX_REBASING) {
			List<Long> parentPatcherFixIds =
				PatcherFixRelUtil.getParentPatcherFixIds(
					patcherFix.getPatcherFixId());

			long rebaseFromPatcherFixId = parentPatcherFixIds.get(0);

			jenkinsRequestParameters.put(
				"osb.patcher.fixId", String.valueOf(rebaseFromPatcherFixId));

			jenkinsRequestParameters.put("osb.patcher.rebase", "true");
		}

		jenkinsRequestParameters.put(
			"osb.patcher.fixIds", String.valueOf(patcherFix.getPatcherFixId()));
		jenkinsRequestParameters.put(
			"osb.patcher.gitRemoteURL", patcherFix.getGitRemoteURL());
		jenkinsRequestParameters.put(
			"osb.patcher.type",
			PortletPropsValues.OSB_PATCHER_SHARED_REQUEST_ADD_FIX_PATCHER_TYPE);

		return getAgentJenkinsRequestParameters(
			patcherProjectVersion, patcherFix, jenkinsRequestParameters);
	}

	public static Map<String, String> getAgentJenkinsRequestParameters(
			PatcherProjectVersion patcherProjectVersion, PatcherFix patcherFix,
			Map<String, String> jenkinsRequestParameters)
		throws Exception {

		jenkinsRequestParameters.put(
			"osb.patcher.baseCommittish",
			patcherProjectVersion.getCommittish());
		jenkinsRequestParameters.put(
			"osb.patcher.patcherFixId",
			String.valueOf(patcherFix.getPatcherFixId()));
		jenkinsRequestParameters.put(
			"osb.patcher.productVersion",
			PatcherProductVersionUtil.fetchPatcherProductVersionName(
				patcherProjectVersion.getPatcherProductVersionId()));
		jenkinsRequestParameters.put(
			"osb.patcher.repository",
			patcherProjectVersion.getRepositoryName());

		PatcherProductVersion patcherProductVersion =
			PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(
				patcherProjectVersion.getPatcherProductVersionId());

		jenkinsRequestParameters.put(
			"patcher.fix.delivery.method",
			PatcherProductVersionConstants.getTypeLabel(
				patcherProductVersion.getFixDeliveryMethod()));
		jenkinsRequestParameters.put(
			"patcher.module.folder.name",
			patcherProductVersion.getModuleFolderName());

		jenkinsRequestParameters.put(
			"patcher.request.key", patcherFix.getRequestKey());
		jenkinsRequestParameters.put("run.agent", "true");

		return jenkinsRequestParameters;
	}

	public static Map<String, String> getDistJenkinsRequestParameters(
			PatcherBuild patcherBuild)
		throws Exception {

		Map<String, String> jenkinsRequestParameters = new HashMap<>();

		PatcherAccount patcherAccount =
			PatcherAccountLocalServiceUtil.getPatcherAccount(
				patcherBuild.getPatcherAccountId());

		jenkinsRequestParameters.put(
			"fix.pack.built.for", patcherAccount.getAccountEntryCode());

		long fixPackId = patcherBuild.getHotfixId();

		PatcherFixPack patcherFixPack = PatcherFixPackUtil.fetchPatcherFixPack(
			patcherBuild);

		if (patcherFixPack != null) {
			PatcherFixComponent patcherFixComponent =
				PatcherFixComponentLocalServiceUtil.getPatcherFixComponent(
					patcherFixPack.getPatcherFixComponentId());

			jenkinsRequestParameters.put(
				"fix.pack.component", patcherFixComponent.getName());

			jenkinsRequestParameters.put(
				"fix.pack.requirements",
				PatcherFixPackUtil.getPatcherFixPackRequirements(
					patcherFixPack));

			fixPackId = patcherFixPack.getVersion();
		}

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherBuild.getPatcherProjectVersionId());

		jenkinsRequestParameters.put(
			"fix.pack.productVersion",
			PatcherProductVersionUtil.fetchPatcherProductVersionName(
				patcherProjectVersion.getPatcherProductVersionId()));

		if (patcherProjectVersion.getPatcherProductVersionId() !=
				PatcherProductVersionUtil.getPatcherProductVersionId(
					PatcherProductVersionConstants.
						LABEL_PRODUCT_VERSION_PORTAL_6X)) {

			jenkinsRequestParameters.put(
				"fix.pack.hotfixed.issues", patcherBuild.getName());

			if (!patcherProjectVersion.isCombinedBranch()) {
				PatcherFix mainPatcherFix =
					PatcherFixLocalServiceUtil.getPatcherFix(
						patcherBuild.getPatcherFixId());

				patcherProjectVersion =
					PatcherProjectVersionLocalServiceUtil.
						getPatcherProjectVersion(
							mainPatcherFix.getPatcherProjectVersionId());
			}

			PatcherProductVersion patcherProductVersion =
				PatcherProductVersionLocalServiceUtil.
					fetchPatcherProductVersion(
						patcherProjectVersion.getPatcherProductVersionId());

			jenkinsRequestParameters.put(
				"patcher.fix.delivery.method",
				PatcherProductVersionConstants.getTypeLabel(
					patcherProductVersion.getFixDeliveryMethod()));
			jenkinsRequestParameters.put(
				"patcher.module.folder.name",
				patcherProductVersion.getModuleFolderName());
		}

		jenkinsRequestParameters.put(
			"fix.pack.baseCommittish", patcherProjectVersion.getCommittish());
		jenkinsRequestParameters.put(
			"fix.pack.customer.ticket", patcherBuild.getSupportTicket());
		jenkinsRequestParameters.put(
			"fix.pack.fixed.issues",
			StringUtil.merge(PatcherBuildUtil.getFixedIssues(patcherBuild)));
		jenkinsRequestParameters.put("fix.pack.id", String.valueOf(fixPackId));

		String gitRevision = StringPool.BLANK;

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
			patcherBuild.getPatcherFixId());

		if (patcherFix != null) {
			gitRevision = patcherFix.getGitHash();
		}

		jenkinsRequestParameters.put("git.revision", gitRevision);

		jenkinsRequestParameters.put(
			"patcher.build.id",
			String.valueOf(patcherBuild.getPatcherBuildId()));
		jenkinsRequestParameters.put(
			"patcher.build.name", patcherBuild.getName());
		jenkinsRequestParameters.put(
			"patcher.combined.branch",
			String.valueOf(patcherProjectVersion.isCombinedBranch()));
		jenkinsRequestParameters.put(
			"patcher.request.key", patcherBuild.getRequestKey());

		boolean patcherSecurityPatch = StringUtil.equalsIgnoreCase(
			patcherAccount.getAccountEntryCode(),
			PatcherBuildConstants.
				PATCHER_BUILD_ACCOUNT_ENTRY_NAME_LIFERAY_SECURITY);

		jenkinsRequestParameters.put(
			"patcher.security.patch", String.valueOf(patcherSecurityPatch));

		jenkinsRequestParameters.put("run.dist", "true");

		return jenkinsRequestParameters;
	}

	public static List<Map<String, String>> getJenkinsResults(
			PatcherBuild patcherBuild)
		throws Exception {

		if (!PatcherProjectVersionUtil.isCombinedBranchPatcherProjectVersion(
				patcherBuild.getPatcherProjectVersionId()) &&
			!patcherBuild.isChildBuild()) {

			return getChildPatcherBuildsJenkinsResults(patcherBuild);
		}

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
			patcherBuild.getPatcherFixId());

		return getJenkinsResults(patcherFix);
	}

	public static List<Map<String, String>> getJenkinsResults(
			PatcherFix patcherFix)
		throws Exception {

		if (patcherFix == null) {
			return new ArrayList<>();
		}

		String jenkinsResults = HtmlUtil.unescape(
			patcherFix.getJenkinsResults());

		return getJenkinsResults(jenkinsResults, false);
	}

	public static String getJenkinsURL() throws Exception {
		if (PortletPropsValues.JENKINS_LOAD_BALANCER_ENABLED &&
			Validator.isNotNull(
				PortletPropsValues.JENKINS_LOAD_BALANCER_BASE_INVOCATION_URL)) {

			String mostAvailableMasterURL =
				LoadBalancerUtil.getMostAvailableMasterURL(
					"base.invocation.url",
					PortletPropsValues.
						JENKINS_LOAD_BALANCER_BASE_INVOCATION_URL,
					"invoked.job.batch.size", "1");

			mostAvailableMasterURL = mostAvailableMasterURL.replace(
				"http", "https");

			return mostAvailableMasterURL + ".liferay.com";
		}

		return PortletPropsValues.JENKINS_URL;
	}

	public static String getJobName(JSONObject jenkinsResult) throws Exception {
		return getJobName(jenkinsResult.toString());
	}

	public static String getJobName(String jenkinsResult) throws Exception {
		String jobName = StringPool.BLANK;

		Pattern pattern = Pattern.compile(
			JenkinsConstants.JENKINS_JOB_NAME_REGEX);

		Matcher matcher = pattern.matcher(getStatusURL(jenkinsResult));

		if (matcher.find()) {
			jobName = matcher.group(1);
		}

		return jobName;
	}

	public static String getStatusURL(JSONObject jsonObject) throws Exception {
		return jsonObject.getString("statusURL");
	}

	public static String getStatusURL(String jenkinsResult) throws Exception {
		if (Validator.isNull(jenkinsResult)) {
			return StringPool.BLANK;
		}

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(jenkinsResult);

		return getStatusURL(jsonObject);
	}

	public static Map<String, String> getTestJenkinsRequestParameters(
			PatcherBuild patcherBuild)
		throws Exception {

		Map<String, String> jenkinsRequestParameters = new HashMap<>();

		jenkinsRequestParameters.put(
			"patcher.build.file.name",
			HttpComponentsUtil.encodePath(patcherBuild.getFileName()));
		jenkinsRequestParameters.put(
			"patcher.build.id",
			String.valueOf(patcherBuild.getPatcherBuildId()));

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherBuild.getPatcherProjectVersionId());

		jenkinsRequestParameters.put(
			"patcher.build.patcher.portal.version",
			patcherProjectVersion.getName());

		jenkinsRequestParameters.put(
			"patcher.build.smoke.test.only",
			String.valueOf(PatcherBuildUtil.isSmokeTestOnly(patcherBuild)));
		jenkinsRequestParameters.put(
			"patcher.build.support.ticket.url",
			PatcherBuildUtil.getSupportTicketURL(
				patcherBuild.getSupportTicket()));
		jenkinsRequestParameters.put(
			"patcher.request.key", patcherBuild.getRequestKey());
		jenkinsRequestParameters.put("run.tests.frontend", "true");

		return jenkinsRequestParameters;
	}

	public static boolean isValidJenkinsRequirement(String requirement) {
		Pattern requirementsPattern = Pattern.compile(
			PatcherConstants.REQUIREMENTS_REGEX);

		Matcher matcher = requirementsPattern.matcher(requirement);

		return matcher.find();
	}

	public static boolean isValidJenkinsSetup() throws Exception {
		return Validator.isNull(validateJenkinsSetup());
	}

	public static boolean isValidSendAgentJenkinsRequest(
			ThemeDisplay themeDisplay, PatcherFix patcherFix)
		throws Exception {

		if ((patcherFix == null) ||
			((themeDisplay != null) &&
			 !PatcherPermission.contains(
				 themeDisplay, patcherFix, PatcherActionKeys.SEND_REQUEST,
				 patcherFix.getUserId()))) {

			return false;
		}

		return true;
	}

	public static boolean isValidSendDistJenkinsRequest(
			ThemeDisplay themeDisplay, PatcherBuild patcherBuild)
		throws Exception {

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
			patcherBuild.getPatcherFixId());

		if ((patcherFix == null) || Validator.isNull(patcherFix.getGitHash())) {
			return false;
		}

		if (patcherBuild.getType() == PatcherBuildConstants.TYPE_FIX_PACK) {
			PatcherFixPack patcherFixPack =
				PatcherFixPackUtil.fetchPatcherFixPack(patcherBuild);

			if ((themeDisplay != null) && (patcherFixPack != null) &&
				PatcherPermission.contains(
					themeDisplay, patcherFixPack,
					PatcherActionKeys.SEND_REQUEST,
					patcherFixPack.getUserId())) {

				return true;
			}

			return false;
		}

		return !PatcherBuildUtil.isMergeOnly(patcherBuild);
	}

	public static boolean isValidSendTestJenkinsRequest(
			ThemeDisplay themeDisplay, PatcherBuild patcherBuild)
		throws Exception {

		if ((themeDisplay == null) ||
			!PatcherPermission.contains(
				themeDisplay, patcherBuild, PatcherActionKeys.SEND_REQUEST,
				patcherBuild.getUserId())) {

			return false;
		}

		if ((patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_COMPLETE) &&
			Validator.isNotNull(patcherBuild.getFileName())) {

			return true;
		}

		return false;
	}

	public static String parseJobName(String statusURL) {
		String jobName = StringPool.BLANK;

		Pattern pattern = Pattern.compile(
			JenkinsConstants.JENKINS_JOB_NAME_REGEX);

		Matcher matcher = pattern.matcher(statusURL);

		if (matcher.find()) {
			jobName = matcher.group(1);
		}

		return jobName;
	}

	public static void putJenkinsResult(
			PatcherFix patcherFix, JSONObject newJenkinsResultJSONObject)
		throws Exception {

		String patcherFixJenkinsResults = patcherFix.getJenkinsResults();

		if (Validator.isNull(patcherFixJenkinsResults)) {
			JSONArray newJenkinsResultJSONArray =
				JSONFactoryUtil.createJSONArray();

			newJenkinsResultJSONArray.put(newJenkinsResultJSONObject);

			patcherFix.setJenkinsResults(newJenkinsResultJSONArray.toString());

			return;
		}

		boolean newJenkinsResultJobNameExists = false;

		JSONArray newJenkinsResultsJSONArray =
			JSONFactoryUtil.createJSONArray();

		JSONArray existingJenkinsResultsJSONArray =
			JSONFactoryUtil.createJSONArray(patcherFixJenkinsResults);

		for (int i = 0; i < existingJenkinsResultsJSONArray.length(); i++) {
			JSONObject existingJenkinsResultJSONObject =
				existingJenkinsResultsJSONArray.getJSONObject(i);

			String newJobName = getJobName(newJenkinsResultJSONObject);

			String existingJobName = getJobName(
				existingJenkinsResultJSONObject);

			if (StringUtil.equalsIgnoreCase(newJobName, existingJobName)) {
				newJenkinsResultJobNameExists = true;

				newJenkinsResultsJSONArray.put(newJenkinsResultJSONObject);

				continue;
			}

			newJenkinsResultsJSONArray.put(existingJenkinsResultJSONObject);
		}

		if (newJenkinsResultJobNameExists) {
			patcherFix.setJenkinsResults(newJenkinsResultsJSONArray.toString());

			return;
		}

		existingJenkinsResultsJSONArray.put(newJenkinsResultJSONObject);

		patcherFix.setJenkinsResults(
			existingJenkinsResultsJSONArray.toString());
	}

	public static void sendAgentJenkinsRequest(
			User user, BaseModel<?> baseModel, ThemeDisplay themeDisplay)
		throws Exception {

		if (baseModel instanceof PatcherBuild) {
			PatcherBuild patcherBuild = (PatcherBuild)baseModel;

			if (PatcherBuildUtil.containsIncompletePatcherFix(patcherBuild) ||
				PatcherBuildUtil.isComplete(patcherBuild) ||
				PatcherBuildUtil.isMergeComplete(patcherBuild)) {

				return;
			}

			PatcherFix mainPatcherFix =
				PatcherFixLocalServiceUtil.getPatcherFix(
					patcherBuild.getPatcherFixId());

			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				"yyyyMMddHHmmss");

			mainPatcherFix.setRequestKey(
				PatcherUtil.generatePatcherKey(
					PatcherFix.class.getName(),
					mainPatcherFix.getPatcherFixId(),
					dateFormat.format(new Date())));

			PatcherFixLocalServiceUtil.updatePatcherFix(mainPatcherFix);

			sendAgentJenkinsPatcherBuildRequest(
				themeDisplay, user, patcherBuild);
		}
		else if (baseModel instanceof PatcherFix) {
			PatcherFix patcherFix = (PatcherFix)baseModel;

			if (!isValidSendAgentJenkinsRequest(themeDisplay, patcherFix)) {
				return;
			}

			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				"yyyyMMddHHmmss");

			patcherFix.setRequestKey(
				PatcherUtil.generatePatcherKey(
					PatcherFix.class.getName(), patcherFix.getPatcherFixId(),
					dateFormat.format(new Date())));

			patcherFix = PatcherFixLocalServiceUtil.updatePatcherFix(
				patcherFix);

			sendAgentJenkinsPatcherFixRequest(themeDisplay, user, patcherFix);
		}
	}

	public static void sendDistJenkinsRequest(
			User user, PatcherBuild patcherBuild, ThemeDisplay themeDisplay)
		throws Exception {

		if (!isValidSendDistJenkinsRequest(themeDisplay, patcherBuild)) {
			return;
		}

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmmss");

		patcherBuild.setRequestKey(
			PatcherUtil.generatePatcherKey(
				PatcherBuild.class.getName(), patcherBuild.getPatcherBuildId(),
				dateFormat.format(new Date())));

		patcherBuild = PatcherBuildLocalServiceUtil.updatePatcherBuild(
			patcherBuild);

		Http.Options options = new Http.Options();

		Map<String, String> distJenkinsRequestParameters =
			getDistJenkinsRequestParameters(patcherBuild);

		for (Map.Entry<String, String> distJenkinsRequestParameter :
				distJenkinsRequestParameters.entrySet()) {

			options.addPart(
				distJenkinsRequestParameter.getKey(),
				distJenkinsRequestParameter.getValue());
		}

		sendJenkinsRequest(user, options);
	}

	public static void sendJenkinsRequest(User user, Http.Options options)
		throws Exception {

		if (!PortletPropsValues.OSB_PATCHER_JENKINS_REQUESTS_ENABLED) {
			return;
		}

		String credentials =
			PortletPropsValues.JENKINS_ADMIN_USERNAME + StringPool.COLON +
				PortletPropsValues.JENKINS_ADMIN_USER_TOKEN;

		options.addHeader(
			"Authorization", "Basic " + Base64.encode(credentials.getBytes()));

		options.addPart("patcher.user.id", String.valueOf(user.getUserId()));
		options.addPart("token", PortletPropsValues.JENKINS_TOKEN);

		String jenkinsURL = getJenkinsURL();

		options.setLocation(
			jenkinsURL + PortletPropsValues.JENKINS_BUILD_WITH_PARAMETERS_PATH);

		options.setPost(true);

		HttpUtil.URLtoString(options);
	}

	public static void sendTestJenkinsRequest(
			User user, PatcherBuild patcherBuild, ThemeDisplay themeDisplay)
		throws Exception {

		if (!PortletPropsValues.OSB_PATCHER_TESTS_ENABLED ||
			!isValidSendTestJenkinsRequest(themeDisplay, patcherBuild)) {

			return;
		}

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmmss");

		patcherBuild.setRequestKey(
			PatcherUtil.generatePatcherKey(
				PatcherBuild.class.getName(), patcherBuild.getPatcherBuildId(),
				dateFormat.format(new Date())));

		patcherBuild = PatcherBuildLocalServiceUtil.updatePatcherBuild(
			patcherBuild);

		Http.Options options = new Http.Options();

		Map<String, String> testJenkinsRequestParameters =
			getTestJenkinsRequestParameters(patcherBuild);

		for (Map.Entry<String, String> testJenkinsRequestParameter :
				testJenkinsRequestParameters.entrySet()) {

			options.addPart(
				testJenkinsRequestParameter.getKey(),
				testJenkinsRequestParameter.getValue());
		}

		sendJenkinsRequest(user, options);
	}

	public static JSONObject toJenkinsResult(String status, String statusURL)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		jsonObject.put("jobName", parseJobName(statusURL));
		jsonObject.put("status", status);
		jsonObject.put("statusURL", statusURL);

		return jsonObject;
	}

	public static String validateJenkinsSetup() throws Exception {
		if (Validator.isNull(
				PortletPropsValues.JENKINS_BUILD_WITH_PARAMETERS_PATH)) {

			return "the-build-cannot-send-request-because-the-" +
				"jenkins-build-with-parameters-path-is-not-set";
		}

		if (Validator.isNull(PortletPropsValues.JENKINS_TOKEN)) {
			return "the-build-cannot-send-request-because-the-" +
				"jenkins-token-is-not-set";
		}

		if (Validator.isNull(PortletPropsValues.JENKINS_URL)) {
			return "the-build-cannot-send-request-because-the-" +
				"jenkins-url-is-not-set";
		}

		return StringPool.BLANK;
	}

	protected static List<Map<String, String>>
			getChildPatcherBuildsJenkinsResults(PatcherBuild patcherBuild)
		throws Exception {

		List<Map<String, String>> childPatcherBuildsJenkinsResults =
			new ArrayList<>();

		List<PatcherBuild> childPatcherBuilds =
			PatcherBuildRelUtil.getChildPatcherBuilds(patcherBuild);

		for (PatcherBuild childPatcherBuild : childPatcherBuilds) {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
				childPatcherBuild.getPatcherFixId());

			if (patcherFix == null) {
				continue;
			}

			String jenkinsResults = HtmlUtil.unescape(
				patcherFix.getJenkinsResults());

			List<Map<String, String>> curJenkinsResults = getJenkinsResults(
				jenkinsResults,
				PatcherProjectVersionUtil.isPrivatePatcherProjectVersion(
					childPatcherBuild.getPatcherProjectVersionId()));

			childPatcherBuildsJenkinsResults.addAll(curJenkinsResults);
		}

		return childPatcherBuildsJenkinsResults;
	}

	protected static List<Map<String, String>> getJenkinsResults(
			String jenkinsResults, boolean appendPrivateSuffix)
		throws Exception {

		List<Map<String, String>> jenkinsResultsMaps = new ArrayList<>();

		JSONArray jenkinsResultsJSONArray = JSONFactoryUtil.createJSONArray(
			jenkinsResults);

		for (int i = 0; i < jenkinsResultsJSONArray.length(); i++) {
			Map<String, String> jenkinsResultMap = new HashMap<>();

			JSONObject jenkinsResultJSONObject =
				jenkinsResultsJSONArray.getJSONObject(i);

			Iterator<String> jenkinsResultIterator =
				jenkinsResultJSONObject.keys();

			while (jenkinsResultIterator.hasNext()) {
				String jenkinsResultKey = jenkinsResultIterator.next();

				String jenkinsResultValue = jenkinsResultJSONObject.getString(
					jenkinsResultKey);

				if (appendPrivateSuffix &&
					StringUtil.equalsIgnoreCase(
						jenkinsResultKey,
						JenkinsConstants.JENKINS_RESULTS_KEY_JOB_NAME) &&
					Validator.isNotNull(jenkinsResultValue) &&
					jenkinsResultValue.contains(
						JenkinsConstants.JENKINS_JOB_NAME_AGENT)) {

					jenkinsResultValue +=
						PatcherProjectVersionConstants.PRIVATE_NAME_SUFFIX;
				}

				jenkinsResultMap.put(jenkinsResultKey, jenkinsResultValue);
			}

			jenkinsResultsMaps.add(jenkinsResultMap);
		}

		return jenkinsResultsMaps;
	}

	protected static void sendAgentJenkinsPatcherBuildRequest(
			ThemeDisplay themeDisplay, User user, PatcherBuild patcherBuild)
		throws Exception {

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherBuild.getPatcherProjectVersionId());

		PatcherFix mainPatcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
			patcherBuild.getPatcherFixId());

		List<Long> patcherFixIds = new ArrayList<>();

		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixes(
				patcherBuild.getPatcherBuildId());

		for (PatcherFix patcherFix : patcherFixes) {
			if (patcherFix.getPatcherFixId() ==
					mainPatcherFix.getPatcherFixId()) {

				continue;
			}

			patcherFixIds.add(patcherFix.getPatcherFixId());
		}

		sendAgentJenkinsPatcherBuildRequest(
			themeDisplay, user, patcherProjectVersion, mainPatcherFix,
			StringUtil.merge(patcherFixIds));
	}

	protected static void sendAgentJenkinsPatcherBuildRequest(
			ThemeDisplay themeDisplay, User user,
			PatcherProjectVersion patcherProjectVersion, PatcherFix patcherFix,
			String patcherFixIds)
		throws Exception {

		if (!isValidSendAgentJenkinsRequest(themeDisplay, patcherFix)) {
			return;
		}

		Http.Options options = new Http.Options();

		Map<String, String> agentJenkinsRequestParameters =
			getAgentJenkinsPatcherBuildRequestParameters(
				patcherProjectVersion, patcherFix, patcherFixIds);

		for (Map.Entry<String, String> agentJenkinsRequestParameter :
				agentJenkinsRequestParameters.entrySet()) {

			options.addPart(
				agentJenkinsRequestParameter.getKey(),
				agentJenkinsRequestParameter.getValue());
		}

		sendJenkinsRequest(user, options);
	}

	protected static void sendAgentJenkinsPatcherFixRequest(
			ThemeDisplay themeDisplay, User user, PatcherFix patcherFix)
		throws Exception {

		Http.Options options = new Http.Options();

		Map<String, String> agentJenkinsRequestParameters =
			getAgentJenkinsPatcherFixRequestParameters(
				PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
					patcherFix.getPatcherProjectVersionId()),
				patcherFix);

		for (Map.Entry<String, String> agentJenkinsRequestParameter :
				agentJenkinsRequestParameters.entrySet()) {

			options.addPart(
				agentJenkinsRequestParameter.getKey(),
				agentJenkinsRequestParameter.getValue());
		}

		sendJenkinsRequest(user, options);
	}

	protected static void validateJenkinsRequestKey(
			BaseModel<?> baseModel, String jenkinsStatusJSONString)
		throws Exception {

		if (baseModel == null) {
			throw new Exception("the-base-model-is-null");
		}

		String baseModelRequestKey = BaseModelUtil.fetchBaseModelRequestKey(
			baseModel);

		if (Validator.isNull(baseModelRequestKey)) {
			throw new Exception(
				"The base model with ID " +
					GetterUtil.getLong(baseModel.getPrimaryKeyObj()) +
						" does not have a request key");
		}

		JSONObject jenkinsStatusJSONObject = JSONFactoryUtil.createJSONObject(
			jenkinsStatusJSONString);

		String jenkinsStatusRequestKey = jenkinsStatusJSONObject.getString(
			"patcherRequestKey");

		if (Validator.isNull(jenkinsStatusRequestKey) ||
			!StringUtil.equalsIgnoreCase(
				jenkinsStatusRequestKey, baseModelRequestKey)) {

			StringBundler sb = new StringBundler(6);

			sb.append("The base model ID ");
			sb.append(GetterUtil.getLong(baseModel.getPrimaryKeyObj()));
			sb.append(" with request key ");
			sb.append(baseModelRequestKey);
			sb.append(" is not contained in the status file ");
			sb.append(jenkinsStatusJSONString);

			throw new Exception(sb.toString());
		}
	}

}