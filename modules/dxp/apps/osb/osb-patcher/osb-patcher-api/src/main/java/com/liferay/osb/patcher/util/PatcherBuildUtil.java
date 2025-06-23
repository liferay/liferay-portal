/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.osb.patcher.configuration.PatcherConfiguration;
import com.liferay.osb.patcher.constants.PatcherBuildConstants;
import com.liferay.osb.patcher.constants.PatcherConstants;
import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixRelLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.osb.patcher.util.comparator.PatcherBuildCreateDateComparator;
import com.liferay.osb.patcher.util.comparator.PatcherBuildKeyVersionComparator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zsolt Balogh
 */
public class PatcherBuildUtil {

	public static PatcherBuild addPatcherFixPackMainBuild(
			User user, long patcherProjectVersionId, String name,
			String accountEntryCode, int status,
			List<Long> relatedPatcherFixIds, ThemeDisplay themeDisplay)
		throws Exception {

		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherBuilds(
				patcherProjectVersionId, name, true, accountEntryCode);

		if (!patcherBuilds.isEmpty()) {
			List<Long> patcherBuildPatcherFixIds = new ArrayList<>();

			PatcherBuild patcherBuild = patcherBuilds.get(0);

			List<PatcherFix> patcherBuildPatcherFixes =
				PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixes(
					patcherBuild.getPatcherBuildId());

			for (PatcherFix patcherBuildPatcherFix : patcherBuildPatcherFixes) {
				if (patcherBuildPatcherFix.getPatcherFixId() ==
						patcherBuild.getPatcherFixId()) {

					continue;
				}

				patcherBuildPatcherFixIds.add(
					patcherBuildPatcherFix.getPatcherFixId());
			}

			if (PatcherUtil.equals(
					patcherBuildPatcherFixIds, relatedPatcherFixIds)) {

				PatcherFix mainPatcherFix =
					PatcherFixLocalServiceUtil.getPatcherFix(
						patcherBuild.getPatcherFixId());

				if (Validator.isNull(mainPatcherFix.getGitHash())) {
					mainPatcherFix.setStatus(
						WorkflowConstants.STATUS_FIX_ADDING);

					PatcherFixLocalServiceUtil.updatePatcherFix(mainPatcherFix);

					workflowPatcherBuildMerging(
						user, patcherBuild, isMergeOnly(patcherBuild),
						themeDisplay);

					patcherBuild =
						PatcherBuildLocalServiceUtil.updatePatcherBuild(
							patcherBuild);

					JenkinsUtil.sendAgentJenkinsRequest(user, patcherBuild);
				}

				return patcherBuild;
			}
		}

		PatcherBuild patcherBuild =
			PatcherBuildLocalServiceUtil.createPatcherBuild(0);

		PatcherAccount patcherAccount =
			PatcherAccountLocalServiceUtil.fetchPatcherAccount(
				accountEntryCode);

		if (patcherAccount == null) {
			addPatcherAccountPatcherBuild(
				patcherBuild.getPatcherBuildId(), accountEntryCode,
				themeDisplay);

			patcherAccount = PatcherAccountLocalServiceUtil.getPatcherAccount(
				accountEntryCode);
		}

		patcherBuild.setPatcherAccountId(patcherAccount.getPatcherAccountId());

		patcherBuild.setPatcherBuildId(CounterLocalServiceUtil.increment());
		patcherBuild.setPatcherProductVersionId(
			PatcherProjectVersionUtil.getPatcherProductVersionId(
				patcherProjectVersionId));
		patcherBuild.setPatcherProjectVersionId(patcherProjectVersionId);
		patcherBuild.setHotfixId(
			generateHotfixId(
				accountEntryCode, patcherBuild.getSupportTicket(),
				patcherProjectVersionId));
		patcherBuild.setName(name);
		patcherBuild.setKey(
			generateKey(patcherProjectVersionId, name, accountEntryCode));
		patcherBuild.setType(PatcherBuildConstants.TYPE_FIX_PACK);
		patcherBuild.setQaStatus(WorkflowConstants.STATUS_PENDING);

		setStatus(user, patcherBuild, status);

		patcherBuild = setLatestPatcherBuild(
			patcherBuild, patcherBuild.getKey(),
			patcherBuild.getSupportTicket());

		patcherBuild.setPatcherBuildId(patcherBuild.getPatcherBuildId());

		patcherBuild = PatcherBuildLocalServiceUtil.updatePatcherBuild(
			patcherBuild);

		updatePatcherBuildFixes(
			user, patcherBuild, relatedPatcherFixIds, themeDisplay);

		JenkinsUtil.sendAgentJenkinsRequest(user, patcherBuild);

		return patcherBuild;
	}

	public static boolean containsIncompleteRebasePatcherFix(
			PatcherBuild patcherBuild)
		throws Exception {

		return PatcherFixUtil.containsIncompleteRebasePatcherFix(
			PatcherFixUtil.getPatcherFixIds(patcherBuild));
	}

	public static boolean containsOtherPatcherProjectVersionIds(
			long patcherBuildProjectVersionId,
			Set<Long> patcherProjectVersionIds)
		throws Exception {

		for (long patcherProjectVersionId : patcherProjectVersionIds) {
			if ((patcherBuildProjectVersionId == patcherProjectVersionId) ||
				PatcherProjectVersionUtil.isSiblingPatcherProjectVersionIds(
					patcherBuildProjectVersionId, patcherProjectVersionId)) {

				continue;
			}

			return true;
		}

		return false;
	}

	public static void deletePatcherBuildAndChildBuilds(
			PatcherBuild patcherBuild)
		throws Exception {

		if (patcherBuild.getKeyVersion() !=
				PatcherBuildConstants.KEY_VERSION_DEFAULT) {

			PatcherBuild oldPatcherBuild = fetchPatcherBuildByNextKeyVersion(
				patcherBuild, true);

			if (oldPatcherBuild != null) {
				oldPatcherBuild.setLatestKeyBuild(true);
				oldPatcherBuild.setLatestSupportTicketBuild(true);

				PatcherBuildLocalServiceUtil.updatePatcherBuild(
					oldPatcherBuild);
			}
		}

		List<PatcherBuild> childPatcherBuilds =
			PatcherBuildRelUtil.getChildPatcherBuilds(patcherBuild);

		if (!childPatcherBuilds.isEmpty()) {
			for (PatcherBuild childPatcherBuild : childPatcherBuilds) {
				PatcherBuildRelUtil.deletePatcherBuildRelsByChildPatcherBuildId(
					childPatcherBuild.getPatcherBuildId());

				deletePatcherBuildAndChildBuilds(childPatcherBuild);
			}
		}

		PatcherAccountLocalServiceUtil.clearPatcherBuildPatcherAccounts(
			patcherBuild.getPatcherBuildId());

		PatcherFixLocalServiceUtil.clearPatcherBuildPatcherFixes(
			patcherBuild.getPatcherBuildId());

		PatcherBuildLocalServiceUtil.deletePatcherBuild(patcherBuild);

		PatcherFix mainPatcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
			patcherBuild.getPatcherFixId());

		if (PatcherFixUtil.isDeletable(mainPatcherFix)) {
			PatcherFixUtil.deletePatcherFix(mainPatcherFix);
		}
	}

	public static PatcherBuild fetchLastModifiedPatcherBuild(
		long patcherAccountId, long patcherProductVersionId) {

		List<PatcherBuild> patcherAccountPatcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherBuilds(
				patcherAccountId, patcherProductVersionId, 0, 1,
				PatcherBuildCreateDateComparator.getInstance(false));

		if (patcherAccountPatcherBuilds.isEmpty()) {
			return null;
		}

		return patcherAccountPatcherBuilds.get(0);
	}

	public static PatcherBuild fetchPatcherBuildByLatestKeyBuild(String key) {
		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherBuilds(key, true);

		if (!patcherBuilds.isEmpty()) {
			return patcherBuilds.get(0);
		}

		return null;
	}

	public static PatcherBuild fetchPatcherBuildByLatestSupportTicketBuild(
		String supportTicket) {

		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherBuilds(true, supportTicket);

		if (!patcherBuilds.isEmpty()) {
			return patcherBuilds.get(0);
		}

		return null;
	}

	public static PatcherBuild fetchPatcherBuildByNextKeyVersion(
		PatcherBuild patcherBuild, boolean older) {

		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherBuildsByKey(
				patcherBuild.getKey(), patcherBuild.getKeyVersion(), older);

		if (patcherBuilds.isEmpty()) {
			return null;
		}

		return patcherBuilds.get(0);
	}

	public static List<PatcherBuild> fetchPatcherBuildsByKey(String key) {
		return PatcherBuildLocalServiceUtil.getPatcherBuilds(
			key, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			PatcherBuildKeyVersionComparator.getInstance(false));
	}

	public static PatcherBuild fetchPatcherBuildSupportTicketVersion(
		PatcherBuild patcherBuild, boolean older) {

		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherBuildsByKey(
				patcherBuild.getSupportTicket(),
				patcherBuild.getSupportTicketVersion(), older);

		if (patcherBuilds.isEmpty()) {
			return null;
		}

		return patcherBuilds.get(0);
	}

	public static long generateHotfixId(
			String accountEntryCode, String supportTicket,
			long patcherProjectVersionId)
		throws Exception {

		if (StringUtil.equalsIgnoreCase(
				accountEntryCode,
				PatcherBuildConstants.
					PATCHER_BUILD_ACCOUNT_ENTRY_NAME_LIFERAY_SECURITY)) {

			return CounterLocalServiceUtil.increment(
				StringBundler.concat(
					PatcherBuild.class.getName(), "#Security#", supportTicket,
					StringPool.POUND,
					PatcherProjectVersionUtil.getPatcherProductVersionId(
						patcherProjectVersionId)));
		}

		long rootPatcherProjectVersionId =
			PatcherProjectVersionUtil.getRootPatcherProjectVersionId(
				patcherProjectVersionId);

		return CounterLocalServiceUtil.increment(
			PatcherBuild.class.getName() + StringPool.POUND +
				rootPatcherProjectVersionId);
	}

	public static String generateKey(
			long patcherProjectVersionId, String name, String accountEntryCode)
		throws Exception {

		return PatcherUtil.generatePatcherKey(
			PatcherBuild.class.getName(), patcherProjectVersionId,
			StringUtil.merge(PatcherUtil.sortTokens(name)), accountEntryCode);
	}

	public static String generateKey(
			long patcherProjectVersionId, String name, String accountEntryCode,
			String key)
		throws Exception {

		return PatcherUtil.generatePatcherKey(
			PatcherBuild.class.getName(), patcherProjectVersionId,
			StringUtil.merge(PatcherUtil.sortTokens(name)), accountEntryCode,
			key);
	}

	public static List<PatcherBuild> getEquivalentPatcherBuilds(
		long patcherProjectVersionId, String tickets) {

		DynamicQuery patcherBuildDynamicQuery =
			PatcherBuildLocalServiceUtil.dynamicQuery();

		Property nameProperty = PropertyFactoryUtil.forName("name");

		List<String> patcherBuildTokens = PatcherUtil.sortTokens(
			PatcherUtil.preparePatcherName(tickets));

		patcherBuildDynamicQuery.add(
			nameProperty.eq(StringUtil.merge(patcherBuildTokens)));

		Property patcherProjectVersionIdProperty = PropertyFactoryUtil.forName(
			"patcherProjectVersionId");

		patcherBuildDynamicQuery.add(
			patcherProjectVersionIdProperty.eq(patcherProjectVersionId));

		Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

		Property statusProperty = PropertyFactoryUtil.forName("status");

		disjunction.add(
			statusProperty.eq(WorkflowConstants.STATUS_BUILD_COMPLETE));
		disjunction.add(
			statusProperty.eq(WorkflowConstants.STATUS_BUILD_READY_TO_RELEASE));
		disjunction.add(
			statusProperty.eq(WorkflowConstants.STATUS_BUILD_RELEASED));

		patcherBuildDynamicQuery.add(disjunction);

		return PatcherBuildLocalServiceUtil.dynamicQuery(
			patcherBuildDynamicQuery);
	}

	public static List<String> getFixedIssues(PatcherBuild patcherBuild)
		throws Exception {

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherBuild.getPatcherProjectVersionId());

		List<String> fixedIssues = new ArrayList<>();

		if (patcherProjectVersion.getPatcherProductVersionId() ==
				PatcherProductVersionUtil.getPatcherProductVersionId(
					PatcherProductVersionConstants.
						LABEL_PRODUCT_VERSION_PORTAL_6X)) {

			List<PatcherFix> patcherFixes =
				PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixes(
					patcherBuild.getPatcherBuildId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			for (PatcherFix patcherFix : patcherFixes) {
				if ((patcherBuild.getPatcherFixId() ==
						patcherFix.getPatcherFixId()) &&
					(patcherFixes.size() != 1)) {

					continue;
				}

				fixedIssues.add(patcherFix.getName());
			}
		}
		else {
			fixedIssues.addAll(PatcherUtil.getTokens(patcherBuild.getName()));

			fixedIssues.addAll(
				PatcherProjectVersionUtil.
					getCumulativePatcherProjectVersionFixedIssues(
						patcherProjectVersion));
		}

		return PatcherUtil.sortTokens(fixedIssues);
	}

	public static long getHotfixIdByFileName(String fileName) {
		Pattern pattern = Pattern.compile(
			PatcherConstants.LIFERAY_HOTFIX_ID_REGEX);

		Matcher matcher = pattern.matcher(fileName);

		if (matcher.find()) {
			return GetterUtil.getLong(matcher.group(1));
		}

		return -1L;
	}

	public static List<PatcherFix> getIncompletePatcherFixes(
			List<PatcherFix> patcherFixes)
		throws Exception {

		return getIncompletePatcherFixes(patcherFixes, 0);
	}

	public static List<PatcherFix> getIncompletePatcherFixes(
			List<PatcherFix> patcherFixes, long skipPatcherFixId)
		throws Exception {

		List<PatcherFix> incompletePatcherFixes = new ArrayList<>();

		for (PatcherFix patcherFix : patcherFixes) {
			if ((skipPatcherFixId == patcherFix.getPatcherFixId()) &&
				(patcherFixes.size() > 1)) {

				continue;
			}

			if (PatcherFixUtil.isIncomplete(patcherFix)) {
				incompletePatcherFixes.add(patcherFix);
			}
		}

		return incompletePatcherFixes;
	}

	public static List<PatcherFix> getIncompletePatcherFixes(
			PatcherBuild patcherBuild)
		throws Exception {

		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixes(
				patcherBuild.getPatcherBuildId());

		return getIncompletePatcherFixes(
			patcherFixes, patcherBuild.getPatcherFixId());
	}

	public static PatcherBuild getLatestEquivalentPatcherBuild(
			long patcherProjectVersionId, String tickets)
		throws Exception {

		List<PatcherBuild> patcherBuilds = getEquivalentPatcherBuilds(
			patcherProjectVersionId, tickets);

		if (!patcherBuilds.isEmpty()) {
			for (PatcherBuild patcherBuild : patcherBuilds) {
				if (isLatestPatcherBuild(patcherBuild)) {
					return patcherBuild;
				}
			}
		}

		return null;
	}

	public static String getLiferayHotfixFileName(String fileName) {
		Pattern pattern = Pattern.compile(
			PatcherConstants.LIFERAY_HOTFIX_FILE_NAME_REGEX);

		Matcher matcher = pattern.matcher(fileName);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return StringPool.BLANK;
	}

	public static int getNextPatcherBuildWorkflowStatus(
			PatcherBuild patcherBuild, boolean mergeOnly)
		throws Exception {

		List<Long> patcherFixIds = getRelatedPatcherBuildsPatcherFixIds(
			patcherBuild);

		PatcherFix weightedStatusPatcherFix =
			PatcherFixUtil.getWeightedStatusPatcherFix(patcherFixIds);

		if (weightedStatusPatcherFix.getStatus() ==
				WorkflowConstants.STATUS_FIX_FAILED) {

			if (mergeOnly) {
				return WorkflowConstants.STATUS_BUILD_FAILED_MERGING_ONLY;
			}

			return WorkflowConstants.STATUS_BUILD_FAILED;
		}
		else if (weightedStatusPatcherFix.getType() ==
					PatcherFixConstants.TYPE_REBASE) {

			if (weightedStatusPatcherFix.getStatus() ==
					WorkflowConstants.STATUS_FIX_REBASE_CONFLICT) {

				if (mergeOnly) {
					return WorkflowConstants.
						STATUS_BUILD_REBASE_CONFLICT_MERGING_ONLY;
				}

				return WorkflowConstants.STATUS_BUILD_REBASE_CONFLICT;
			}
			else if ((weightedStatusPatcherFix.getStatus() ==
						WorkflowConstants.STATUS_FIX_REBASING) ||
					 (weightedStatusPatcherFix.getStatus() ==
						 WorkflowConstants.STATUS_FIX_ADDING)) {

				if (mergeOnly) {
					return WorkflowConstants.STATUS_BUILD_REBASING_MERGING_ONLY;
				}

				return WorkflowConstants.STATUS_BUILD_REBASING;
			}
		}

		if (weightedStatusPatcherFix.getStatus() ==
				WorkflowConstants.STATUS_FIX_CONFLICT) {

			if (mergeOnly) {
				return WorkflowConstants.STATUS_BUILD_CONFLICT_MERGING_ONLY;
			}

			return WorkflowConstants.STATUS_BUILD_CONFLICT;
		}
		else if ((weightedStatusPatcherFix.getStatus() ==
					WorkflowConstants.STATUS_FIX_ADDING) ||
				 !isPatcherBuildDescendantsContainGitHash(patcherBuild)) {

			if (mergeOnly) {
				return WorkflowConstants.STATUS_BUILD_MERGING_ONLY;
			}

			return WorkflowConstants.STATUS_BUILD_MERGING;
		}

		if (mergeOnly && Validator.isNotNull(patcherBuild.getFileName())) {
			return WorkflowConstants.STATUS_BUILD_COMPLETE;
		}

		if (mergeOnly) {
			return WorkflowConstants.STATUS_BUILD_COMPLETE_MERGING_ONLY;
		}

		return WorkflowConstants.STATUS_BUILD_COMPILING;
	}

	public static String getPatcherAccountEntryCode(long patcherAccountId)
		throws Exception {

		PatcherAccount patcherAccount =
			PatcherAccountLocalServiceUtil.getPatcherAccount(patcherAccountId);

		return patcherAccount.getAccountEntryCode();
	}

	public static List<Long> getPatcherAccountPatcherBuildIds(
		long patcherAccountId) {

		List<Long> patcherAccountPatcherBuildIds = new ArrayList<>();

		List<PatcherBuild> patcherAccountPatcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherAccountPatcherBuilds(
				patcherAccountId);

		for (PatcherBuild patcherAccountPatcherBuild :
				patcherAccountPatcherBuilds) {

			patcherAccountPatcherBuildIds.add(
				patcherAccountPatcherBuild.getPatcherBuildId());
		}

		return patcherAccountPatcherBuildIds;
	}

	public static String getQAStatusLabel(long patcherBuildId) {
		PatcherBuild patcherBuild =
			PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherBuildId);

		if (patcherBuild == null) {
			return StringPool.BLANK;
		}

		return WorkflowConstants.getStatusLabel(patcherBuild.getQaStatus());
	}

	public static String getQuarterReleaseBuildPath(String path) {
		return com.liferay.petra.string.StringUtil.replace(
			path, "fix-packs", "portal/hotfix");
	}

	public static List<PatcherBuild> getRelatedPatcherBuilds(
		PatcherBuild patcherBuild) {

		List<PatcherBuild> relatedPatcherBuilds = new ArrayList<>();

		List<PatcherBuild> childPatcherBuilds =
			PatcherBuildRelUtil.getChildPatcherBuilds(patcherBuild);

		if (!childPatcherBuilds.isEmpty()) {
			relatedPatcherBuilds = childPatcherBuilds;
		}
		else {
			relatedPatcherBuilds.add(patcherBuild);
		}

		return relatedPatcherBuilds;
	}

	public static List<Long> getRelatedPatcherBuildsPatcherFixIds(
		PatcherBuild patcherBuild) {

		List<Long> relatedPatcherBuildFixIds = new ArrayList<>();

		List<PatcherBuild> relatedPatcherBuilds = getRelatedPatcherBuilds(
			patcherBuild);

		for (PatcherBuild relatedPatcherBuild : relatedPatcherBuilds) {
			List<Long> patcherFixIds = PatcherFixUtil.getPatcherFixIds(
				relatedPatcherBuild);

			if (patcherFixIds.size() > 1) {
				patcherFixIds.remove(relatedPatcherBuild.getPatcherFixId());
			}

			relatedPatcherBuildFixIds.addAll(patcherFixIds);
		}

		return relatedPatcherBuildFixIds;
	}

	public static String getSupportTicketURL(String supportTicket)
		throws Exception {

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, CompanyThreadLocal.getCompanyId());

		if (Validator.isNumber(supportTicket)) {
			return patcherConfiguration.helpCenterURL() +
				StringPool.FORWARD_SLASH + supportTicket;
		}

		return patcherConfiguration.lesaURL() + StringPool.FORWARD_SLASH +
			supportTicket;
	}

	public static boolean hasEquivalentPatcherBuild(
		long patcherProjectVersionId, String tickets) {

		List<PatcherBuild> patcherBuilds = getEquivalentPatcherBuilds(
			patcherProjectVersionId, tickets);

		return !patcherBuilds.isEmpty();
	}

	public static boolean isComplete(PatcherBuild patcherBuild) {
		if ((patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_COMPLETE) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_COMPLETE_MERGING_ONLY)) {

			return true;
		}

		return false;
	}

	public static boolean isCompleteOrReady(PatcherBuild patcherBuild) {
		if (isComplete(patcherBuild) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_READY_TO_RELEASE)) {

			return true;
		}

		return false;
	}

	public static boolean isCompleteOrReleased(PatcherBuild patcherBuild) {
		if (isComplete(patcherBuild) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_RELEASED)) {

			return true;
		}

		return false;
	}

	public static boolean isCompleteReadyOrReleased(PatcherBuild patcherBuild) {
		if (isCompleteOrReleased(patcherBuild) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_READY_TO_RELEASE)) {

			return true;
		}

		return false;
	}

	public static boolean isLatestPatcherBuild(PatcherBuild patcherBuild)
		throws Exception {

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, CompanyThreadLocal.getCompanyId());

		if (patcherConfiguration.patcherScanningEnabled()) {
			return patcherBuild.isLatestSupportTicketBuild();
		}

		return patcherBuild.isLatestKeyBuild();
	}

	public static boolean isMergeComplete(PatcherBuild patcherBuild) {
		if ((patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_COMPILING) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_COMPLETE_MERGING_ONLY)) {

			return true;
		}

		return false;
	}

	public static boolean isMergeConflict(PatcherBuild patcherBuild) {
		if ((patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_CONFLICT) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_CONFLICT_MERGING_ONLY)) {

			return true;
		}

		return false;
	}

	public static boolean isMergeOnly(PatcherBuild patcherBuild) {
		if ((patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_COMPLETE_MERGING_ONLY) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_CONFLICT_MERGING_ONLY) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_FAILED_MERGING_ONLY) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_MERGING_ONLY) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_REBASE_CONFLICT_MERGING_ONLY) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_REBASING_MERGING_ONLY)) {

			return true;
		}

		return false;
	}

	public static boolean isMerging(PatcherBuild patcherBuild) {
		if ((patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_MERGING) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_MERGING_ONLY)) {

			return true;
		}

		return false;
	}

	public static boolean isObsolete(long patcherBuildId) throws Exception {
		List<PatcherFix> patcherFixes =
			PatcherBuildRelUtil.getChildPatcherBuildsMainFixes(
				PatcherBuildLocalServiceUtil.getPatcherBuild(patcherBuildId));

		for (PatcherFix patcherFix : patcherFixes) {
			if (patcherFix.isObsolete()) {
				return true;
			}
		}

		return false;
	}

	public static boolean isPatcherBuildDescendantsContainGitHash(
			PatcherBuild patcherBuild)
		throws Exception {

		List<PatcherBuild> patcherBuilds = new ArrayList<>();

		if (PatcherBuildRelUtil.hasChildPatcherBuilds(patcherBuild)) {
			patcherBuilds = PatcherBuildRelUtil.getChildPatcherBuilds(
				patcherBuild);
		}
		else {
			patcherBuilds.add(patcherBuild);
		}

		for (PatcherBuild curPatcherBuild : patcherBuilds) {
			PatcherFix mainPatcherFix =
				PatcherFixLocalServiceUtil.getPatcherFix(
					curPatcherBuild.getPatcherFixId());

			if (Validator.isNull(mainPatcherFix.getGitHash())) {
				return false;
			}
		}

		return true;
	}

	public static boolean isRebaseConflict(PatcherBuild patcherBuild) {
		if ((patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_REBASE_CONFLICT) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_REBASE_CONFLICT_MERGING_ONLY)) {

			return true;
		}

		return false;
	}

	public static boolean isSmokeTestOnly(PatcherBuild patcherBuild) {
		if ((patcherBuild.getQaStatus() ==
				WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY) ||
			(patcherBuild.getQaStatus() ==
				WorkflowConstants.
					STATUS_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY) ||
			(patcherBuild.getQaStatus() ==
				WorkflowConstants.
					STATUS_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY) ||
			(patcherBuild.getQaStatus() ==
				WorkflowConstants.
					STATUS_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY) ||
			(patcherBuild.getQaStatus() ==
				WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY) ||
			(patcherBuild.getQaStatus() ==
				WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY) ||
			(patcherBuild.getQaStatus() ==
				WorkflowConstants.STATUS_BUILD_QA_PENDING_SMOKE_ONLY) ||
			(patcherBuild.getQaStatus() ==
				WorkflowConstants.STATUS_BUILD_QA_TESTING_SKIPPED_SMOKE_ONLY)) {

			return true;
		}

		return false;
	}

	public static boolean isTestingFailed(PatcherBuild patcherBuild) {
		if ((patcherBuild.getQaStatus() ==
				WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY) ||
			(patcherBuild.getQaStatus() ==
				WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY)) {

			return true;
		}

		return false;
	}

	public static boolean isTestingPassed(PatcherBuild patcherBuild) {
		if ((patcherBuild.getQaStatus() ==
				WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED) ||
			(patcherBuild.getQaStatus() ==
				WorkflowConstants.
					STATUS_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY) ||
			(patcherBuild.getQaStatus() ==
				WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY) ||
			(patcherBuild.getQaStatus() ==
				WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY)) {

			return true;
		}

		return false;
	}

	public static void notifyUsersInactivePatcherBuilds(
			ThemeDisplay themeDisplay)
		throws Exception {

		Calendar calendar = new GregorianCalendar();

		calendar.add(Calendar.HOUR, -3);

		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherBuilds(
				calendar.getTime(), false,
				new int[] {
					WorkflowConstants.STATUS_BUILD_COMPILING,
					WorkflowConstants.STATUS_BUILD_MERGING,
					WorkflowConstants.STATUS_BUILD_MERGING_ONLY
				});

		if (patcherBuilds.isEmpty()) {
			return;
		}

		for (PatcherBuild patcherBuild : patcherBuilds) {
			User user = UserLocalServiceUtil.getUser(patcherBuild.getUserId());

			EmailUtil.sendPatcherTimeoutEmail(
				patcherBuild, user.getEmailAddress(), themeDisplay,
				patcherBuild.getUserId());

			patcherBuild.setNotified(true);

			PatcherBuildLocalServiceUtil.updatePatcherBuild(patcherBuild);
		}
	}

	@Transactional(
		isolation = Isolation.PORTAL, propagation = Propagation.REQUIRES_NEW,
		rollbackFor = Exception.class
	)
	public static void processOSBPatcherBuildCompileJenkinsStatus(
			User user, long patcherBuildId, String jenkinsStatusJSONString)
		throws Exception {

		PatcherBuild patcherBuild =
			PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherBuildId);

		validateOSBPatcherBuildCompileJenkinsStatus(
			patcherBuild, jenkinsStatusJSONString);

		JSONObject jenkinsStatusJSONObject = JSONFactoryUtil.createJSONObject(
			jenkinsStatusJSONString);

		if (!jenkinsStatusJSONObject.has("exitValue") &&
			jenkinsStatusJSONObject.has("statusURL")) {

			PatcherFixUtil.updatePatcherFixJenkinsResult(
				jenkinsStatusJSONObject, patcherBuild.getPatcherFixId());

			return;
		}

		String fileName = StringPool.BLANK;
		String sourceName = StringPool.BLANK;

		int exitValue = jenkinsStatusJSONObject.getInt("exitValue");

		if (exitValue == 0) {
			setStatus(
				user, patcherBuild, WorkflowConstants.STATUS_BUILD_COMPLETE);

			workflowCompletedPatcherBuildQAStatus(patcherBuild);

			fileName = jenkinsStatusJSONObject.getString("fileName");
			sourceName = jenkinsStatusJSONObject.getString("sourceName");
		}
		else {
			setStatus(
				user, patcherBuild, WorkflowConstants.STATUS_BUILD_FAILED);
		}

		patcherBuild.setFileName(fileName);
		patcherBuild.setSourceName(sourceName);

		patcherBuild = PatcherBuildLocalServiceUtil.updatePatcherBuild(
			patcherBuild);

		PatcherFixUtil.updatePatcherFixJenkinsResult(
			jenkinsStatusJSONObject, patcherBuild.getPatcherFixId());

		sendTestJenkinsRequest(user, patcherBuild);
	}

	@Transactional(
		isolation = Isolation.PORTAL, propagation = Propagation.REQUIRES_NEW,
		rollbackFor = Exception.class
	)
	public static void processOSBPatcherBuildMergeJenkinsStatus(
			User user, long patcherFixId, String jenkinsStatusJSONString,
			ThemeDisplay themeDisplay)
		throws Exception {

		validateOSBPatcherBuildMergeJenkinsStatus(
			patcherFixId, jenkinsStatusJSONString);

		JSONObject jenkinsStatusJSONObject = JSONFactoryUtil.createJSONObject(
			jenkinsStatusJSONString);

		if (jenkinsStatusJSONObject.has("statusURL")) {
			List<PatcherBuild> patcherBuilds =
				PatcherBuildLocalServiceUtil.getPatcherBuildsByPatcherFixId(
					patcherFixId);

			for (PatcherBuild patcherBuild : patcherBuilds) {
				if (!isLatestPatcherBuild(patcherBuild)) {
					continue;
				}

				PatcherFixUtil.updatePatcherFixJenkinsResult(
					jenkinsStatusJSONObject, patcherBuild.getPatcherFixId());
			}

			return;
		}

		String outcome = jenkinsStatusJSONObject.getString("outcome");

		OSBPatcherServletOutcome osbPatcherServletOutcome =
			JSONFactoryUtil.looseDeserialize(
				outcome, OSBPatcherServletOutcome.class);

		List<String> messages = new ArrayList<>();

		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherBuildsByPatcherFixId(
				patcherFixId);

		for (PatcherBuild patcherBuild : patcherBuilds) {
			if (!isLatestPatcherBuild(patcherBuild)) {
				continue;
			}

			updatePatcherBuildStatus(
				user, patcherBuild, osbPatcherServletOutcome.getStatus(),
				osbPatcherServletOutcome.getResult(), messages, themeDisplay);
		}
	}

	@Transactional(
		isolation = Isolation.PORTAL, propagation = Propagation.REQUIRES_NEW,
		rollbackFor = Exception.class
	)
	public static void processOSBPatcherBuildTestJenkinsStatus(
			User user, long patcherBuildId, String jenkinsStatusJSONString)
		throws Exception {

		PatcherBuild patcherBuild =
			PatcherBuildLocalServiceUtil.getPatcherBuild(patcherBuildId);

		validateOSBPatcherBuildTestJenkinsStatus(
			patcherBuild, jenkinsStatusJSONString);

		JSONObject jenkinsStatusJSONObject = JSONFactoryUtil.createJSONObject(
			jenkinsStatusJSONString);

		JSONArray resultsJSONArray = jenkinsStatusJSONObject.getJSONArray(
			"results");

		for (int i = 0; i < resultsJSONArray.length(); i++) {
			PatcherFixUtil.updatePatcherFixJenkinsResult(
				jenkinsStatusJSONObject, patcherBuild.getPatcherFixId());
		}

		if (jenkinsStatusJSONObject.has("status")) {
			String status = jenkinsStatusJSONObject.getString("status");

			if (StringUtil.equalsIgnoreCase(status, "fail")) {
				if (isSmokeTestOnly(patcherBuild)) {
					patcherBuild.setQaStatus(
						WorkflowConstants.
							STATUS_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY);
				}
				else {
					patcherBuild.setQaStatus(
						WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED);
				}
			}
			else if (StringUtil.equalsIgnoreCase(status, "pass")) {
				if (isSmokeTestOnly(patcherBuild)) {
					patcherBuild.setQaStatus(
						WorkflowConstants.
							STATUS_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY);
				}
				else {
					patcherBuild.setQaStatus(
						WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED);
				}
			}
		}

		PatcherBuildLocalServiceUtil.updatePatcherBuild(patcherBuild);
	}

	public static void reindexRelatedModels(PatcherBuild patcherBuild)
		throws Exception {

		Indexer<PatcherAccount> indexer = IndexerRegistryUtil.getIndexer(
			PatcherAccount.class);

		List<PatcherAccount> patcherBuildPatcherAccounts =
			PatcherAccountLocalServiceUtil.getPatcherBuildPatcherAccounts(
				patcherBuild.getPatcherBuildId());

		for (PatcherAccount patcherBuildPatcherAccount :
				patcherBuildPatcherAccounts) {

			indexer.reindex(patcherBuildPatcherAccount);
		}
	}

	public static void releasePatcherBuild(PatcherBuild patcherBuild)
		throws Exception {

		String hotfixFileName = getLiferayHotfixFileName(
			patcherBuild.getFileName());

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, patcherBuild.getCompanyId());

		String path =
			patcherConfiguration.hotfixMountPath() + StringPool.FORWARD_SLASH +
				patcherBuild.getFileName();

		String quarterReleasePath = getQuarterReleaseBuildPath(path);

		try {
			HelpCenterUtil.addAttachmentComment(
				hotfixFileName, patcherBuild, quarterReleasePath);
		}
		catch (FileNotFoundException fileNotFoundException) {
			if (_log.isDebugEnabled()) {
				_log.debug(fileNotFoundException);
			}

			HelpCenterUtil.addAttachmentComment(
				hotfixFileName, patcherBuild, path);
		}
	}

	public static void removePreviousMainFixVersionsFromBuildsFixes(
		long patcherBuildId, PatcherFix patcherFix,
		List<Long> patcherBuildPatcherFixIds) {

		List<Long> previousVersionPatcherFixIds =
			PatcherFixUtil.getPreviousVersionsPatcherFixIds(patcherFix);

		for (long previousVersionPatcherFixId : previousVersionPatcherFixIds) {
			if (patcherBuildPatcherFixIds.contains(
					previousVersionPatcherFixId)) {

				PatcherFixLocalServiceUtil.deletePatcherBuildPatcherFix(
					patcherBuildId, patcherFix.getPatcherFixId());
			}
		}
	}

	public static void saveParentPatcherBuild(
			PatcherBuild parentPatcherBuild, String accountEntryCode,
			ThemeDisplay themeDisplay)
		throws Exception {

		parentPatcherBuild = setLatestPatcherBuild(
			parentPatcherBuild, parentPatcherBuild.getKey(),
			parentPatcherBuild.getSupportTicket());

		if (parentPatcherBuild.isNew()) {
			addPatcherAccountPatcherBuild(
				parentPatcherBuild.getPatcherBuildId(), accountEntryCode,
				themeDisplay);

			PatcherAccount patcherAccount =
				PatcherAccountLocalServiceUtil.getPatcherAccount(
					accountEntryCode);

			parentPatcherBuild.setPatcherAccountId(
				patcherAccount.getPatcherAccountId());
		}

		if (!PatcherProjectVersionUtil.isCombinedBranchPatcherProjectVersion(
				parentPatcherBuild.getPatcherProjectVersionId())) {

			parentPatcherBuild.setPatcherFixId(0L);
		}

		parentPatcherBuild.setPatcherBuildId(
			parentPatcherBuild.getPatcherBuildId());

		PatcherBuildLocalServiceUtil.updatePatcherBuild(parentPatcherBuild);
	}

	public static void sendTestJenkinsRequest(
			User user, PatcherBuild patcherBuild)
		throws Exception {

		if (patcherBuild.getType() == PatcherBuildConstants.TYPE_OFFICIAL) {
			JenkinsUtil.sendTestJenkinsRequest(user, patcherBuild);
		}
	}

	public static PatcherBuild setLatestPatcherBuild(
			PatcherBuild patcherBuild, String key, String supportTicket)
		throws Exception {

		if (!patcherBuild.isNew() ||
			PatcherBuildRelUtil.hasChildPatcherBuilds(patcherBuild)) {

			return patcherBuild;
		}

		patcherBuild.setKeyVersion(PatcherBuildConstants.KEY_VERSION_DEFAULT);
		patcherBuild.setLatestKeyBuild(true);
		patcherBuild.setLatestSupportTicketBuild(true);
		patcherBuild.setSupportTicketVersion(
			PatcherBuildConstants.SUPPORT_TICKET_VERSION_DEFAULT);

		PatcherBuild latestKeyBuild = fetchPatcherBuildByLatestKeyBuild(key);

		if (PatcherBuildRelUtil.hasParentPatcherBuilds(patcherBuild)) {
			if (latestKeyBuild != null) {
				patcherBuild.setKeyVersion(
					BigDecimalUtil.add(latestKeyBuild.getKeyVersion(), 0.1));

				latestKeyBuild.setLatestBuild(false);

				latestKeyBuild =
					PatcherBuildLocalServiceUtil.updatePatcherBuild(
						latestKeyBuild);
			}

			patcherBuild.setLatestSupportTicketBuild(false);
			patcherBuild.setSupportTicketVersion(
				PatcherBuildConstants.SUPPORT_TICKET_VERSION_DEFAULT);

			return patcherBuild;
		}

		PatcherBuild latestSupportTicketBuild =
			fetchPatcherBuildByLatestSupportTicketBuild(supportTicket);

		if ((latestKeyBuild != null) && (latestSupportTicketBuild != null) &&
			(latestKeyBuild.getPatcherBuildId() ==
				latestSupportTicketBuild.getPatcherBuildId())) {

			patcherBuild.setKeyVersion(
				BigDecimalUtil.add(latestKeyBuild.getKeyVersion(), 0.1));
			patcherBuild.setSupportTicketVersion(
				BigDecimalUtil.add(
					latestSupportTicketBuild.getSupportTicketVersion(), 0.1));

			latestKeyBuild.setLatestBuild(false);
			latestKeyBuild.setLatestSupportTicketBuild(false);

			latestKeyBuild = PatcherBuildLocalServiceUtil.updatePatcherBuild(
				latestKeyBuild);
		}
		else {
			if (latestKeyBuild != null) {
				patcherBuild.setKeyVersion(
					BigDecimalUtil.add(latestKeyBuild.getKeyVersion(), 0.1));

				latestKeyBuild.setLatestKeyBuild(false);

				PatcherBuildLocalServiceUtil.updatePatcherBuild(latestKeyBuild);
			}

			if (latestSupportTicketBuild != null) {
				patcherBuild.setSupportTicketVersion(
					BigDecimalUtil.add(
						latestSupportTicketBuild.getSupportTicketVersion(),
						0.1));

				latestSupportTicketBuild.setLatestSupportTicketBuild(false);

				PatcherBuildLocalServiceUtil.updatePatcherBuild(
					latestSupportTicketBuild);
			}
		}

		return patcherBuild;
	}

	public static void setStatus(
			User user, PatcherBuild patcherBuild, int status)
		throws Exception {

		patcherBuild.setStatus(status);

		workflowParentPatcherBuild(user, patcherBuild);
	}

	public static void workflowCompletedPatcherBuildQAStatus(
		PatcherBuild patcherBuild) {

		if (patcherBuild.getType() == PatcherBuildConstants.TYPE_OFFICIAL) {
			if (isSmokeTestOnly(patcherBuild)) {
				patcherBuild.setQaStatus(
					WorkflowConstants.
						STATUS_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY);
			}
			else {
				patcherBuild.setQaStatus(
					WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED);
			}
		}
		else {
			if (isSmokeTestOnly(patcherBuild)) {
				patcherBuild.setQaStatus(
					WorkflowConstants.
						STATUS_BUILD_QA_TESTING_SKIPPED_SMOKE_ONLY);
			}
			else {
				patcherBuild.setQaStatus(
					WorkflowConstants.STATUS_BUILD_QA_TESTING_SKIPPED);
			}
		}
	}

	public static void workflowParentPatcherBuild(
			User user, PatcherBuild childPatcherBuild)
		throws Exception {

		if (!childPatcherBuild.isChildBuild()) {
			return;
		}

		List<PatcherBuild> parentPatcherBuilds =
			PatcherBuildRelUtil.getParentPatcherBuilds(childPatcherBuild);

		if (parentPatcherBuilds.isEmpty()) {
			return;
		}

		PatcherBuild parentPatcherBuild = parentPatcherBuilds.get(0);

		int status = getNextPatcherBuildWorkflowStatus(
			parentPatcherBuild, isMergeOnly(parentPatcherBuild));

		parentPatcherBuild.setStatus(status);

		if (parentPatcherBuild.getPatcherFixId() == 0) {
			PatcherBuild siblingChildPatcherBuild =
				PatcherBuildRelUtil.fetchSiblingChildPatcherBuild(
					childPatcherBuild);

			if ((siblingChildPatcherBuild == null) ||
				PatcherProjectVersionUtil.isPrivatePatcherProjectVersion(
					childPatcherBuild.getPatcherProjectVersionId())) {

				parentPatcherBuild.setPatcherFixId(
					childPatcherBuild.getPatcherFixId());
			}
			else {
				parentPatcherBuild.setPatcherFixId(
					siblingChildPatcherBuild.getPatcherFixId());
			}
		}

		parentPatcherBuild = PatcherBuildLocalServiceUtil.updatePatcherBuild(
			parentPatcherBuild);

		if (status == WorkflowConstants.STATUS_BUILD_COMPILING) {
			JenkinsUtil.sendDistJenkinsRequest(user, parentPatcherBuild);
		}
		else if (status == WorkflowConstants.STATUS_BUILD_COMPLETE) {
			workflowCompletedPatcherBuildQAStatus(parentPatcherBuild);

			sendTestJenkinsRequest(user, parentPatcherBuild);
		}
	}

	public static List<PatcherFix> workflowPatcherBuildIncompleteFixesToPending(
			PatcherBuild patcherBuild)
		throws Exception {

		List<PatcherFix> pendingPatcherFixes = new ArrayList<>();

		List<PatcherFix> incompletePatcherFixes = getIncompletePatcherFixes(
			patcherBuild);

		for (PatcherFix incompletePatcherFix : incompletePatcherFixes) {
			if ((incompletePatcherFix.getStatus() ==
					WorkflowConstants.STATUS_FIX_REBASE_CONFLICT) ||
				(incompletePatcherFix.getStatus() ==
					WorkflowConstants.STATUS_FIX_CONFLICT)) {

				continue;
			}

			if (incompletePatcherFix.getStatus() ==
					WorkflowConstants.STATUS_FIX_FAILED) {

				if (Validator.isNull(incompletePatcherFix.getGitHash()) ||
					Validator.isNull(incompletePatcherFix.getGitRemoteURL())) {

					incompletePatcherFix.setStatus(
						WorkflowConstants.STATUS_FIX_REBASING);
				}
				else {
					incompletePatcherFix.setStatus(
						WorkflowConstants.STATUS_FIX_ADDING);
				}

				incompletePatcherFix =
					PatcherFixLocalServiceUtil.updatePatcherFix(
						incompletePatcherFix);
			}

			pendingPatcherFixes.add(incompletePatcherFix);
		}

		return pendingPatcherFixes;
	}

	public static void workflowPatcherBuildMerging(
			User user, PatcherBuild patcherBuild, boolean mergeOnly,
			ThemeDisplay themeDisplay)
		throws Exception {

		if (mergeOnly) {
			setStatus(
				user, patcherBuild,
				WorkflowConstants.STATUS_BUILD_MERGING_ONLY);
		}
		else {
			setStatus(
				user, patcherBuild, WorkflowConstants.STATUS_BUILD_MERGING);
		}
	}

	public static List<BaseModel<?>>
			workflowRelatedPatcherBuildsToPendingStatus(
				PatcherBuild parentPatcherBuild, boolean mergeOnly)
		throws Exception {

		List<BaseModel<?>> sendToJenkinsBaseModels = new ArrayList<>();

		List<PatcherBuild> patcherBuilds = new ArrayList<>();

		if (PatcherBuildRelUtil.hasChildPatcherBuilds(parentPatcherBuild)) {
			patcherBuilds = PatcherBuildRelUtil.getChildPatcherBuilds(
				parentPatcherBuild);
		}

		patcherBuilds.add(parentPatcherBuild);

		for (PatcherBuild patcherBuild : patcherBuilds) {
			List<PatcherFix> pendingPatcherFixes =
				workflowPatcherBuildIncompleteFixesToPending(patcherBuild);

			sendToJenkinsBaseModels.addAll(pendingPatcherFixes);

			int status = 0;

			if (patcherBuild.isChildBuild()) {
				status = getNextPatcherBuildWorkflowStatus(patcherBuild, true);
			}
			else {
				status = getNextPatcherBuildWorkflowStatus(
					patcherBuild, mergeOnly);
			}

			patcherBuild.setStatus(status);

			patcherBuild = PatcherBuildLocalServiceUtil.updatePatcherBuild(
				patcherBuild);

			if (patcherBuild.isChildBuild() ||
				!PatcherBuildRelUtil.hasChildPatcherBuilds(patcherBuild)) {

				sendToJenkinsBaseModels.add(patcherBuild);
			}
		}

		return sendToJenkinsBaseModels;
	}

	protected static List<PatcherFix> addChildPatcherFixes(
			User user, PatcherBuild patcherBuild, long patcherFixId,
			List<Long> conflictPatcherFixIds, List<String> messages)
		throws Exception {

		List<PatcherFix> childPatcherFixes = new ArrayList<>();

		for (long conflictPatcherFixId : conflictPatcherFixIds) {
			PatcherUtil.addMessage(
				StringBundler.concat(
					"The fixes ", patcherFixId, " and ", conflictPatcherFixId,
					" conflict."),
				messages);

			List<Long> parentPatcherFixIds = new ArrayList<>();

			parentPatcherFixIds.add(patcherFixId);
			parentPatcherFixIds.add(conflictPatcherFixId);

			String sortedTickets = getSortedTickets(parentPatcherFixIds);

			PatcherFix childPatcherFix = PatcherFixUtil.addPatcherFix(
				user, parentPatcherFixIds,
				patcherBuild.getPatcherProjectVersionId(), sortedTickets,
				PatcherFixConstants.TYPE_GENERATED,
				WorkflowConstants.STATUS_FIX_CONFLICT);

			childPatcherFixes.add(childPatcherFix);

			PatcherUtil.addMessage(
				StringBundler.concat(
					"The conflict fix ", childPatcherFix.getPatcherFixId(),
					" with name ", childPatcherFix.getName(), " was created."),
				messages);
		}

		return childPatcherFixes;
	}

	protected static void addPatcherAccountPatcherBuild(
			long patcherBuildId, String accountEntryCode,
			ThemeDisplay themeDisplay)
		throws Exception {

		PatcherAccount patcherAccount =
			PatcherAccountLocalServiceUtil.fetchPatcherAccount(
				accountEntryCode);

		if (patcherAccount != null) {
			PatcherBuildLocalServiceUtil.addPatcherAccountPatcherBuild(
				patcherAccount.getPatcherAccountId(), patcherBuildId);

			return;
		}

		patcherAccount = PatcherAccountLocalServiceUtil.createPatcherAccount(0);

		patcherAccount.setPatcherAccountId(CounterLocalServiceUtil.increment());
		patcherAccount.setAccountEntryId(
			HelpCenterUtil.fetchAccountEntryId(accountEntryCode));
		patcherAccount.setAccountEntryCode(accountEntryCode);

		patcherAccount = PatcherAccountLocalServiceUtil.updatePatcherAccount(
			patcherAccount);

		PatcherUtil.pollIndexState(
			PatcherAccount.class.getName(),
			patcherAccount.getPatcherAccountId(), themeDisplay);

		PatcherBuildLocalServiceUtil.addPatcherAccountPatcherBuild(
			patcherAccount.getPatcherAccountId(), patcherBuildId);
	}

	protected static boolean containsIncompletePatcherFix(
			PatcherBuild patcherBuild)
		throws Exception {

		List<PatcherFix> incompletePatcherFixes = getIncompletePatcherFixes(
			patcherBuild);

		return !incompletePatcherFixes.isEmpty();
	}

	protected static PatcherFix getLatestPatcherFix(PatcherFix patcherFix) {
		if (patcherFix.getType() == PatcherFixConstants.TYPE_EXCLUDED) {
			return patcherFix;
		}

		if (!patcherFix.isLatestFix()) {
			patcherFix = PatcherFixUtil.fetchPatcherFixByLatestFix(
				patcherFix.getKey());

			return getLatestPatcherFix(patcherFix);
		}

		List<PatcherFix> parentPatcherFixes =
			PatcherFixUtil.getParentPatcherFixes(patcherFix);

		for (PatcherFix parentPatcherFix : parentPatcherFixes) {
			PatcherFix latestPatcherFix = getLatestPatcherFix(parentPatcherFix);

			if ((parentPatcherFix != latestPatcherFix) ||
				(latestPatcherFix.getType() ==
					PatcherFixConstants.TYPE_EXCLUDED)) {

				return latestPatcherFix;
			}
		}

		return patcherFix;
	}

	protected static List<Long> getLongList(Object object) throws Exception {
		List<Long> longArray = new ArrayList<>();

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			String.valueOf(object));

		String[] strings = ArrayUtil.toStringArray(jsonArray);

		for (String string : strings) {
			longArray.add(Long.valueOf(string));
		}

		return longArray;
	}

	protected static String getSortedTickets(List<Long> patcherFixIds) {
		List<String> tickets = new ArrayList<>();

		for (long patcherFixId : patcherFixIds) {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
				patcherFixId);

			tickets.addAll(PatcherUtil.getTokens(patcherFix.getName()));
		}

		tickets = PatcherUtil.sortTokens(tickets);

		return StringUtil.merge(tickets);
	}

	protected static boolean
			isPreviousPatcherBuildMainFixEqualsCurrentBuildMainFix(
				PatcherBuild patcherBuild)
		throws Exception {

		PatcherBuild oldPatcherBuild = fetchPatcherBuildByNextKeyVersion(
			patcherBuild, true);

		if (oldPatcherBuild != null) {
			PatcherFix oldMainPatcherFix =
				PatcherFixLocalServiceUtil.fetchPatcherFix(
					oldPatcherBuild.getPatcherFixId());

			PatcherFix currentMainPatcherFix =
				PatcherFixLocalServiceUtil.getPatcherFix(
					patcherBuild.getPatcherFixId());

			if ((oldMainPatcherFix != null) &&
				(oldMainPatcherFix.getPatcherFixId() ==
					currentMainPatcherFix.getPatcherFixId())) {

				return true;
			}
		}

		return false;
	}

	protected static Map<Long, List<Long>>
			rebaseOtherProjectVersionPatcherFixes(
				User user,
				Map<Long, List<Long>> patcherProjectVersionIdPatcherFixIdsMap,
				long patcherBuildProjectVersionId)
		throws Exception {

		List<PatcherFix> rebasePatcherFixes = new ArrayList<>();
		List<Long> rebasedPatcherProjectVersionIds = new ArrayList<>();

		for (Map.Entry<Long, List<Long>> entry :
				patcherProjectVersionIdPatcherFixIdsMap.entrySet()) {

			long patcherProjectVersionId = entry.getKey();

			if ((patcherProjectVersionId == patcherBuildProjectVersionId) ||
				PatcherProjectVersionUtil.isSiblingPatcherProjectVersionIds(
					patcherProjectVersionId, patcherBuildProjectVersionId)) {

				continue;
			}

			long rebaseToPatcherProjectVersionId = patcherBuildProjectVersionId;

			if (PatcherProjectVersionUtil.isPrivatePatcherProjectVersion(
					patcherProjectVersionId) &&
				!PatcherProjectVersionUtil.isPrivatePatcherProjectVersion(
					patcherBuildProjectVersionId)) {

				PatcherProjectVersion privatePatcherProjectVersion =
					PatcherProjectVersionUtil.getSiblingPatcherProjectVersion(
						patcherBuildProjectVersionId);

				rebaseToPatcherProjectVersionId =
					privatePatcherProjectVersion.getPatcherProjectVersionId();
			}

			List<Long> patcherFixIds = entry.getValue();

			for (long patcherFixId : patcherFixIds) {
				PatcherFix patcherFix =
					PatcherFixLocalServiceUtil.getPatcherFix(patcherFixId);

				rebasedPatcherProjectVersionIds.add(
					patcherFix.getPatcherProjectVersionId());

				PatcherFix rebasePatcherFix = PatcherFixUtil.addPatcherFix(
					user, ListUtil.fromArray(patcherFixId),
					rebaseToPatcherProjectVersionId, patcherFix.getName(),
					PatcherFixConstants.TYPE_REBASE,
					WorkflowConstants.STATUS_FIX_REBASING);

				rebasePatcherFixes.add(rebasePatcherFix);
			}
		}

		for (long rebasedPatcherProjectVersionId :
				rebasedPatcherProjectVersionIds) {

			patcherProjectVersionIdPatcherFixIdsMap.remove(
				rebasedPatcherProjectVersionId);
		}

		for (PatcherFix rebasePatcherFix : rebasePatcherFixes) {
			List<Long> patcherFixIds = new ArrayList<>();

			if (patcherProjectVersionIdPatcherFixIdsMap.containsKey(
					rebasePatcherFix.getPatcherProjectVersionId())) {

				patcherFixIds = patcherProjectVersionIdPatcherFixIdsMap.get(
					rebasePatcherFix.getPatcherProjectVersionId());
			}

			patcherFixIds.add(rebasePatcherFix.getPatcherFixId());

			patcherProjectVersionIdPatcherFixIdsMap.put(
				rebasePatcherFix.getPatcherProjectVersionId(), patcherFixIds);
		}

		return patcherProjectVersionIdPatcherFixIdsMap;
	}

	protected static void updatePatcherBuildFixes(
			User user, PatcherBuild patcherBuild, List<Long> patcherFixIds,
			ThemeDisplay themeDisplay)
		throws Exception {

		PatcherFixLocalServiceUtil.clearPatcherBuildPatcherFixes(
			patcherBuild.getPatcherBuildId());

		for (long patcherFixId : patcherFixIds) {
			PatcherFixLocalServiceUtil.addPatcherBuildPatcherFix(
				patcherBuild.getPatcherBuildId(), patcherFixId);
		}

		if (!patcherBuild.isChildBuild() && (patcherFixIds.size() == 1) &&
			(patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK)) {

			patcherBuild.setPatcherFixId(patcherFixIds.get(0));

			updatePatcherBuildStatusMergeComplete(user, patcherBuild);

			return;
		}

		int type = PatcherFixConstants.TYPE_GENERATED;

		if (patcherBuild.getType() == PatcherBuildConstants.TYPE_FIX_PACK) {
			type = PatcherFixConstants.TYPE_FIX_PACK;
		}
		else if (patcherBuild.isChildBuild()) {
			type = PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC;
		}

		PatcherFix mainPatcherFix = null;

		if (patcherBuild.isChildBuild()) {
			if (patcherBuild.getPatcherFixId() != 0) {
				mainPatcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
					patcherBuild.getPatcherFixId());

				List<Long> mainFixParentPatcherFixIds =
					PatcherFixRelUtil.getParentPatcherFixIds(
						mainPatcherFix.getPatcherFixId());

				if (!mainFixParentPatcherFixIds.containsAll(patcherFixIds)) {
					PatcherFixRelLocalServiceUtil.
						deletePatcherFixRelsByChildPatcherFixId(
							mainPatcherFix.getPatcherFixId());

					PatcherFixRelUtil.addPatcherFixRel(
						mainPatcherFix.getPatcherFixId(), patcherFixIds);
				}

				mainPatcherFix.setGitHash(StringPool.BLANK);
				mainPatcherFix.setJenkinsResults(StringPool.BLANK);
				mainPatcherFix.setStatus(WorkflowConstants.STATUS_FIX_ADDING);

				mainPatcherFix = PatcherFixLocalServiceUtil.updatePatcherFix(
					mainPatcherFix);
			}
			else {
				mainPatcherFix = PatcherFixUtil.addNewPatcherFix(
					user, PatcherFixConstants.KEY_VERSION_DEFAULT,
					patcherFixIds, patcherBuild.getPatcherProjectVersionId(),
					patcherBuild.getName(), type,
					WorkflowConstants.STATUS_FIX_ADDING);
			}
		}
		else {
			mainPatcherFix = PatcherFixUtil.addPatcherFix(
				user, patcherFixIds, patcherBuild.getPatcherProjectVersionId(),
				patcherBuild.getName(), type,
				WorkflowConstants.STATUS_FIX_ADDING);
		}

		patcherBuild.setPatcherFixId(mainPatcherFix.getPatcherFixId());

		patcherBuild = PatcherBuildLocalServiceUtil.updatePatcherBuild(
			patcherBuild);

		PatcherFixLocalServiceUtil.addPatcherBuildPatcherFix(
			patcherBuild.getPatcherBuildId(), mainPatcherFix.getPatcherFixId());

		if (!patcherBuild.isChildBuild()) {
			removePreviousMainFixVersionsFromBuildsFixes(
				patcherBuild.getPatcherBuildId(), mainPatcherFix,
				patcherFixIds);

			if (isPreviousPatcherBuildMainFixEqualsCurrentBuildMainFix(
					patcherBuild)) {

				updatePatcherBuildStatusMergeComplete(user, patcherBuild);
			}
		}
	}

	protected static void updatePatcherBuildsPatcherFixes(
		PatcherBuild patcherBuild, List<PatcherFix> childPatcherFixes,
		List<String> messages) {

		PatcherFix longestTicketPatcherFix =
			PatcherFixUtil.fetchLongestTicketPatcherFix(childPatcherFixes);

		PatcherBuildLocalServiceUtil.addPatcherFixPatcherBuild(
			longestTicketPatcherFix.getPatcherFixId(),
			patcherBuild.getPatcherBuildId());

		PatcherUtil.addMessage(
			StringBundler.concat(
				"The fix ", longestTicketPatcherFix.getPatcherFixId(),
				" was added to the build ", patcherBuild.getPatcherBuildId()),
			messages);

		List<Long> parentPatcherFixIds =
			PatcherFixRelUtil.getParentPatcherFixIds(
				longestTicketPatcherFix.getPatcherFixId());

		for (long parentPatcherFixId : parentPatcherFixIds) {
			PatcherBuildLocalServiceUtil.deletePatcherFixPatcherBuild(
				parentPatcherFixId, patcherBuild.getPatcherBuildId());
		}

		List<String> patcherFixTickets = PatcherUtil.getTickets(
			longestTicketPatcherFix.getName());

		List<PatcherFix> patcherBuildPatcherFixes =
			PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixes(
				patcherBuild.getPatcherBuildId());

		for (PatcherFix patcherBuildPatcherFix : patcherBuildPatcherFixes) {
			if (patcherBuildPatcherFix.getPatcherFixId() ==
					longestTicketPatcherFix.getPatcherFixId()) {

				continue;
			}

			List<String> patcherBuildPatcherFixTickets = PatcherUtil.getTickets(
				patcherBuildPatcherFix.getName());

			if (patcherFixTickets.containsAll(patcherBuildPatcherFixTickets)) {
				PatcherBuildLocalServiceUtil.deletePatcherFixPatcherBuild(
					patcherBuildPatcherFix.getPatcherFixId(),
					patcherBuild.getPatcherBuildId());
			}
		}
	}

	protected static void updatePatcherBuildStatus(
			User user, PatcherBuild patcherBuild,
			int osbPatcherServletOutcomeStatus,
			String osbPatcherServletOutcomeResult, List<String> messages,
			ThemeDisplay themeDisplay)
		throws Exception {

		if (osbPatcherServletOutcomeStatus ==
				OSBPatcherServletOutcome.STATUS_SUCCESS) {

			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				patcherBuild.getPatcherFixId());

			patcherFix.setGitHash(osbPatcherServletOutcomeResult);

			patcherFix.setStatus(WorkflowConstants.STATUS_FIX_COMPLETE);

			PatcherFixLocalServiceUtil.updatePatcherFix(patcherFix);

			updatePatcherBuildStatusMergeComplete(user, patcherBuild);

			PatcherUtil.addMessage(
				StringBundler.concat(
					"The patch for build ", patcherBuild.getPatcherBuildId(),
					" with name ", patcherBuild.getName(), " was successful."),
				messages);
		}
		else if (osbPatcherServletOutcomeStatus ==
					OSBPatcherServletOutcome.STATUS_CONFLICT) {

			PatcherUtil.addMessage(
				StringBundler.concat(
					"The patch for build ", patcherBuild.getPatcherBuildId(),
					" with name ", patcherBuild.getName(), " has a conflict."),
				messages);

			JSONDeserializer<Map<String, Object>> jsonDeserializer =
				JSONFactoryUtil.createJSONDeserializer();

			Map<String, Object> conflictPatcherFixIdsMap =
				jsonDeserializer.deserialize(osbPatcherServletOutcomeResult);

			for (Map.Entry<String, Object> conflictPatcherFixIdsEntry :
					conflictPatcherFixIdsMap.entrySet()) {

				long patcherFixId = GetterUtil.getLong(
					conflictPatcherFixIdsEntry.getKey());

				List<Long> conflictPatcherFixIds = getLongList(
					conflictPatcherFixIdsEntry.getValue());

				List<PatcherFix> childPatcherFixes = addChildPatcherFixes(
					user, patcherBuild, patcherFixId, conflictPatcherFixIds,
					messages);

				updatePatcherBuildsPatcherFixes(
					patcherBuild, childPatcherFixes, messages);

				reindexRelatedModels(patcherBuild);
			}

			List<PatcherFix> patcherBuildPatcherFixes =
				PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixes(
					patcherBuild.getPatcherBuildId());

			if ((patcherBuildPatcherFixes.size() > 1) &&
				!containsIncompletePatcherFix(patcherBuild)) {

				List<PatcherBuild> patcherBuilds = new ArrayList<>();

				PatcherProjectVersion patcherProjectVersion =
					PatcherProjectVersionLocalServiceUtil.
						getPatcherProjectVersion(
							patcherBuild.getPatcherProjectVersionId());

				if (!patcherProjectVersion.isCombinedBranch() &&
					!patcherBuild.isChildBuild()) {

					patcherBuilds = PatcherBuildRelUtil.getChildPatcherBuilds(
						patcherBuild);
				}
				else {
					patcherBuilds.add(patcherBuild);
				}

				for (PatcherBuild curPatcherBuild : patcherBuilds) {
					JenkinsUtil.sendAgentJenkinsRequest(user, curPatcherBuild);
				}

				return;
			}

			PatcherFix mainPatcherFix =
				PatcherFixLocalServiceUtil.getPatcherFix(
					patcherBuild.getPatcherFixId());

			mainPatcherFix.setStatus(WorkflowConstants.STATUS_FIX_CONFLICT);

			PatcherFixLocalServiceUtil.updatePatcherFix(mainPatcherFix);

			if ((patcherBuild.getStatus() ==
					WorkflowConstants.STATUS_BUILD_MERGING_ONLY) ||
				(patcherBuild.getStatus() ==
					WorkflowConstants.STATUS_BUILD_CONFLICT_MERGING_ONLY)) {

				setStatus(
					user, patcherBuild,
					WorkflowConstants.STATUS_BUILD_CONFLICT_MERGING_ONLY);
			}
			else {
				setStatus(
					user, patcherBuild,
					WorkflowConstants.STATUS_BUILD_CONFLICT);
			}

			patcherBuild = PatcherBuildLocalServiceUtil.updatePatcherBuild(
				patcherBuild);
		}
		else {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				patcherBuild.getPatcherFixId());

			patcherFix.setStatus(WorkflowConstants.STATUS_FIX_FAILED);

			PatcherFixLocalServiceUtil.updatePatcherFix(patcherFix);

			setStatus(
				user, patcherBuild, WorkflowConstants.STATUS_BUILD_FAILED);

			patcherBuild = PatcherBuildLocalServiceUtil.updatePatcherBuild(
				patcherBuild);

			PatcherUtil.addMessage(
				StringBundler.concat(
					"The patch for build ", patcherBuild.getPatcherBuildId(),
					" with name ", patcherBuild.getName(), " failed."),
				messages);
		}
	}

	protected static void updatePatcherBuildStatusMergeComplete(
			User user, PatcherBuild patcherBuild)
		throws Exception {

		if (isMergeOnly(patcherBuild)) {
			setStatus(
				user, patcherBuild,
				WorkflowConstants.STATUS_BUILD_COMPLETE_MERGING_ONLY);

			patcherBuild = PatcherBuildLocalServiceUtil.updatePatcherBuild(
				patcherBuild);
		}
		else {
			setStatus(
				user, patcherBuild, WorkflowConstants.STATUS_BUILD_COMPILING);

			patcherBuild = PatcherBuildLocalServiceUtil.updatePatcherBuild(
				patcherBuild);

			JenkinsUtil.sendDistJenkinsRequest(user, patcherBuild);
		}
	}

	protected static void validateOSBPatcherBuildCompileJenkinsStatus(
			PatcherBuild patcherBuild, String jenkinsStatusJSONString)
		throws Exception {

		if (patcherBuild == null) {
			throw new Exception("the-base-model-is-null");
		}

		JenkinsUtil.validateJenkinsRequestKey(
			patcherBuild, jenkinsStatusJSONString,
			patcherBuild.getRequestKey());
	}

	protected static void validateOSBPatcherBuildMergeJenkinsStatus(
			long patcherFixId, String jenkinsStatusJSONString)
		throws Exception {

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
			patcherFixId);

		if (patcherFix == null) {
			throw new Exception("the-base-model-is-null");
		}

		JenkinsUtil.validateJenkinsRequestKey(
			patcherFix, jenkinsStatusJSONString, patcherFix.getRequestKey());
	}

	protected static void validateOSBPatcherBuildTestJenkinsStatus(
			PatcherBuild patcherBuild, String jenkinsStatusJSONString)
		throws Exception {

		if (patcherBuild == null) {
			throw new Exception("the-base-model-is-null");
		}

		JenkinsUtil.validateJenkinsRequestKey(
			patcherBuild, jenkinsStatusJSONString,
			patcherBuild.getRequestKey());

		JSONObject jenkinsStatusJSONObject = JSONFactoryUtil.createJSONObject(
			jenkinsStatusJSONString);

		JSONArray resultsJSONArray = jenkinsStatusJSONObject.getJSONArray(
			"results");

		for (int i = 0; i < resultsJSONArray.length(); i++) {
			JSONObject resultJSONObject = resultsJSONArray.getJSONObject(i);

			String resultStatus = resultJSONObject.getString("status");

			validateTestStatus(resultStatus);

			String resultStatusURL = resultJSONObject.getString("statusURL");

			if (!Validator.isUrl(resultStatusURL)) {
				throw new Exception("the-status-url-is-not-valid");
			}
		}

		if (jenkinsStatusJSONObject.has("status")) {
			String status = jenkinsStatusJSONObject.getString("status");

			validateTestStatus(status);
		}
	}

	protected static void validateTestStatus(String status) throws Exception {
		if (!(StringUtil.equalsIgnoreCase(status, "fail") ||
			  StringUtil.equalsIgnoreCase(status, "pass") ||
			  StringUtil.equalsIgnoreCase(status, "pending"))) {

			throw new Exception("the-status-is-not-valid");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherBuildUtil.class);

}