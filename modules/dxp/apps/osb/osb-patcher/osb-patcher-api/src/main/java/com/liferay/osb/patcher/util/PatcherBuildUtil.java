/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.AlloyController;
import com.liferay.alloy.mvc.AlloyServiceInvoker;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.osb.patcher.constants.PatcherBuildConstants;
import com.liferay.osb.patcher.constants.PatcherConstants;
import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl;
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
			AlloyController alloyController, User user,
			long patcherProjectVersionId, String name, String accountEntryCode,
			int status, List<Long> relatedPatcherFixIds)
		throws Exception {

		double keyVersion = PatcherBuildConstants.KEY_VERSION_DEFAULT;

		AlloyServiceInvoker alloyServiceInvoker = new AlloyServiceInvoker(
			PatcherBuild.class.getName());

		List<PatcherBuild> patcherBuilds =
			alloyServiceInvoker.executeDynamicQuery(
				new Object[] {
					"patcherProjectVersionId", patcherProjectVersionId, "name",
					name, "accountEntryCode", accountEntryCode,
					"latestKeyBuild", true
				});

		if (!patcherBuilds.isEmpty()) {
			List<Long> patcherBuildPatcherFixIds = new ArrayList<>();

			PatcherBuild patcherBuild = patcherBuilds.get(0);

			List<PatcherFix> patcherBuildPatcherFixes =
				PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixs(
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

					alloyController.updateModelIgnoreRequest(mainPatcherFix);

					workflowPatcherBuildMerging(
						alloyController, user, patcherBuild,
						isMergeOnly(patcherBuild));

					alloyController.updateModelIgnoreRequest(patcherBuild);

					JenkinsUtil.sendAgentJenkinsRequest(
						alloyController, user, patcherBuild);
				}

				return patcherBuild;
			}
		}

		PatcherBuild patcherBuild =
			PatcherBuildLocalServiceUtil.createPatcherBuild(0);

		PatcherAccount patcherAccount = PatcherAccountUtil.fetchPatcherAccount(
			accountEntryCode);

		if (Validator.isNull(patcherAccount)) {
			addPatcherAccountPatcherBuild(
				alloyController, patcherBuild.getPatcherBuildId(),
				accountEntryCode);

			patcherAccount = PatcherAccountUtil.getPatcherAccount(
				accountEntryCode);
		}

		patcherBuild.setPatcherAccountId(patcherAccount.getPatcherAccountId());

		patcherBuild.setPatcherBuildId(alloyController.increment());
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

		setStatus(alloyController, user, patcherBuild, status);

		patcherBuild = setLatestPatcherBuild(
			alloyController, patcherBuild, patcherBuild.getKey(),
			patcherBuild.getSupportTicket());

		alloyController.updateModelIgnoreRequest(
			patcherBuild, "patcherBuildId", patcherBuild.getPatcherBuildId());

		updatePatcherBuildFixes(
			alloyController, user, patcherBuild, relatedPatcherFixIds);

		JenkinsUtil.sendAgentJenkinsRequest(
			alloyController, user, patcherBuild);

		return patcherBuild;
	}

	public static boolean containsIncompleteRebasePatcherFix(
			PatcherBuild patcherBuild)
		throws Exception {

		List<Long> patcherFixIds = PatcherFixUtil.getPatcherFixIds(
			patcherBuild);

		return PatcherFixUtil.containsIncompleteRebasePatcherFix(patcherFixIds);
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
			AlloyController alloyController, PatcherBuild patcherBuild)
		throws Exception {

		if (patcherBuild.getKeyVersion() !=
				PatcherBuildConstants.KEY_VERSION_DEFAULT) {

			PatcherBuild oldPatcherBuild =
				PatcherBuildUtil.fetchPatcherBuildByNextKeyVersion(
					patcherBuild, true);

			if (oldPatcherBuild != null) {
				oldPatcherBuild.setLatestKeyBuild(true);
				oldPatcherBuild.setLatestSupportTicketBuild(true);

				alloyController.updateModelIgnoreRequest(oldPatcherBuild);
			}
		}

		List<PatcherBuild> childPatcherBuilds =
			PatcherBuildRelUtil.getChildPatcherBuilds(patcherBuild);

		if (!childPatcherBuilds.isEmpty()) {
			for (PatcherBuild childPatcherBuild : childPatcherBuilds) {
				PatcherBuildRelUtil.deletePatcherBuildRelsByChildPatcherBuildId(
					childPatcherBuild.getPatcherBuildId());

				deletePatcherBuildAndChildBuilds(
					alloyController, childPatcherBuild);
			}
		}

		PatcherAccountLocalServiceUtil.clearPatcherBuildPatcherAccounts(
			patcherBuild.getPatcherBuildId());

		PatcherFixLocalServiceUtil.clearPatcherBuildPatcherFixs(
			patcherBuild.getPatcherBuildId());

		PatcherBuildLocalServiceUtil.deletePatcherBuild(patcherBuild);

		PatcherFix mainPatcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
			patcherBuild.getPatcherFixId());

		if (PatcherFixUtil.isDeletable(mainPatcherFix)) {
			PatcherFixUtil.deletePatcherFix(alloyController, mainPatcherFix);
		}
	}

	public static PatcherBuild fetchLastModifiedPatcherBuild(
			long patcherAccountId, long patcherProductVersionId)
		throws Exception {

		AlloyServiceInvoker patcherBuildAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherBuild.class.getName());

		DynamicQuery patcherBuildDynamicQuery =
			patcherBuildAlloyServiceInvoker.buildDynamicQuery(
				new Object[] {
					"patcherProductVersionId", patcherProductVersionId
				});

		Property patcherBuildIdProperty = PropertyFactoryUtil.forName(
			"patcherBuildId");

		List<Long> patcherAccountPatcherBuildIds =
			getPatcherAccountPatcherBuildIds(patcherAccountId);

		if (patcherAccountPatcherBuildIds.isEmpty()) {
			return null;
		}

		patcherBuildDynamicQuery.add(
			patcherBuildIdProperty.in(patcherAccountPatcherBuildIds));

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(
			PatcherBuildModelImpl.TABLE_NAME, "modifiedDate", false);

		List<PatcherBuild> patcherAccountPatcherBuilds =
			patcherBuildAlloyServiceInvoker.executeDynamicQuery(
				patcherBuildDynamicQuery, 0, 1, obc);

		if (patcherAccountPatcherBuilds.isEmpty()) {
			return null;
		}

		return patcherAccountPatcherBuilds.get(0);
	}

	public static PatcherBuild fetchPatcherBuildByLatestKeyBuild(String key)
		throws Exception {

		AlloyServiceInvoker alloyServiceInvoker = new AlloyServiceInvoker(
			PatcherBuild.class.getName());

		List<PatcherBuild> patcherBuilds =
			alloyServiceInvoker.executeDynamicQuery(
				new Object[] {"key", key, "latestKeyBuild", true});

		if (!patcherBuilds.isEmpty()) {
			return patcherBuilds.get(0);
		}

		return null;
	}

	public static PatcherBuild fetchPatcherBuildByLatestSupportTicketBuild(
			String supportTicket)
		throws Exception {

		AlloyServiceInvoker alloyServiceInvoker = new AlloyServiceInvoker(
			PatcherBuild.class.getName());

		List<PatcherBuild> patcherBuilds =
			alloyServiceInvoker.executeDynamicQuery(
				new Object[] {
					"supportTicket", supportTicket, "latestSupportTicketBuild",
					true
				});

		if (!patcherBuilds.isEmpty()) {
			return patcherBuilds.get(0);
		}

		return null;
	}

	public static PatcherBuild fetchPatcherBuildByNextKeyVersion(
			PatcherBuild patcherBuild, boolean older)
		throws Exception {

		AlloyServiceInvoker patcherBuildAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherBuild.class.getName());

		DynamicQuery patcherBuildKeyVersionDynamicQuery =
			buildPatcherBuildKeyVersionDynamicQuery(patcherBuild, older);

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(
			PatcherBuildModelImpl.TABLE_NAME, "keyVersion", !older);

		List<PatcherBuild> patcherBuilds =
			patcherBuildAlloyServiceInvoker.executeDynamicQuery(
				patcherBuildKeyVersionDynamicQuery, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, obc);

		if (patcherBuilds.isEmpty()) {
			return null;
		}

		return patcherBuilds.get(0);
	}

	public static List<PatcherBuild> fetchPatcherBuildsByKey(String key)
		throws Exception {

		AlloyServiceInvoker patcherBuildAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherBuild.class.getName());

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(
			PatcherBuildModelImpl.TABLE_NAME, "keyVersion", false);

		return patcherBuildAlloyServiceInvoker.executeDynamicQuery(
			new Object[] {"key", key}, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			obc);
	}

	public static PatcherBuild fetchPatcherBuildSupportTicketVersion(
			PatcherBuild patcherBuild, boolean older)
		throws Exception {

		AlloyServiceInvoker patcherBuildAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherBuild.class.getName());

		DynamicQuery patcherBuildSupportTicketVersionDynamicQuery =
			buildPatcherBuildSupportTicketVersionDynamicQuery(
				patcherBuild, older);

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(
			PatcherBuildModelImpl.TABLE_NAME, "supportTicketVersion", !older);

		List<PatcherBuild> patcherBuilds =
			patcherBuildAlloyServiceInvoker.executeDynamicQuery(
				patcherBuildSupportTicketVersionDynamicQuery, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, obc);

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

			long patcherProductVersionId =
				PatcherProjectVersionUtil.getPatcherProductVersionId(
					patcherProjectVersionId);

			return CounterLocalServiceUtil.increment(
				PatcherBuild.class.getName() + StringPool.POUND + "Security" +
					StringPool.POUND + supportTicket + StringPool.POUND +
						patcherProjectVersionId);
		}

		return CounterLocalServiceUtil.increment(
			PatcherBuild.class.getName() + StringPool.POUND +
				PatcherProjectVersionUtil.getRootPatcherProjectVersionId(
					patcherProjectVersionId));
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
			long patcherProjectVersionId, String tickets)
		throws Exception {

		String patcherBuildName = PatcherUtil.preparePatcherName(tickets);

		List<String> patcherBuildTokens = PatcherUtil.sortTokens(
			patcherBuildName);

		DynamicQuery patcherBuildDynamicQuery =
			PatcherBuildLocalServiceUtil.dynamicQuery();

		Property nameProperty = PropertyFactoryUtil.forName("name");

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

		AlloyServiceInvoker alloyServiceInvoker = new AlloyServiceInvoker(
			PatcherBuild.class.getName());

		return alloyServiceInvoker.executeDynamicQuery(
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
				PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixs(
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
			PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixs(
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
			PatcherConstants.LIFERAY_HOTFIX_FILENAME_REGEX);

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
			long patcherAccountId)
		throws Exception {

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

	public static String getQAStatusLabel(long patcherBuildId)
		throws Exception {

		PatcherBuild patcherBuild =
			PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherBuildId);

		if (patcherBuild == null) {
			return StringPool.BLANK;
		}

		return WorkflowConstants.getStatusLabel(patcherBuild.getQaStatus());
	}

	public static String getQuarterReleaseBuildPath(String path) {
		return path.replace("fix-packs", "portal/hotfix");
	}

	public static List<PatcherBuild> getRelatedPatcherBuilds(
			PatcherBuild patcherBuild)
		throws Exception {

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
			PatcherBuild patcherBuild)
		throws Exception {

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

		if (Validator.isNumber(supportTicket)) {
			return PortletPropsValues.HELP_CENTER_URL +
				StringPool.FORWARD_SLASH + supportTicket;
		}

		return PortletPropsValues.LESA_URL + StringPool.FORWARD_SLASH +
			supportTicket;
	}

	public static boolean hasEquivalentPatcherBuild(
			long patcherProjectVersionId, String tickets)
		throws Exception {

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

	public static boolean isLatestPatcherBuild(PatcherBuild patcherBuild) {
		if (PortletPropsValues.OSB_PATCHER_SCANNING_ENABLED) {
			return patcherBuild.isLatestSupportTicketBuild();
		}

		return patcherBuild.isLatestKeyBuild();
	}

	public static boolean isMergeComplete(PatcherBuild patcherBuild)
		throws Exception {

		if ((patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_COMPILING) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_COMPLETE_MERGING_ONLY)) {

			return true;
		}

		return false;
	}

	public static boolean isMergeConflict(PatcherBuild patcherBuild)
		throws Exception {

		if ((patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_CONFLICT) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_CONFLICT_MERGING_ONLY)) {

			return true;
		}

		return false;
	}

	public static boolean isMergeOnly(PatcherBuild patcherBuild)
		throws Exception {

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

	public static boolean isMerging(PatcherBuild patcherBuild)
		throws Exception {

		if ((patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_MERGING) ||
			(patcherBuild.getStatus() ==
				WorkflowConstants.STATUS_BUILD_MERGING_ONLY)) {

			return true;
		}

		return false;
	}

	public static boolean isObsolete(long patcherBuildId) throws Exception {
		PatcherBuild patcherBuild =
			PatcherBuildLocalServiceUtil.getPatcherBuild(patcherBuildId);

		List<PatcherFix> patcherFixes =
			PatcherBuildRelUtil.getChildPatcherBuildsMainFixes(patcherBuild);

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

	public static boolean isRebaseConflict(PatcherBuild patcherBuild)
		throws Exception {

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
			AlloyController alloyController)
		throws Exception {

		AlloyServiceInvoker patcherBuildAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherBuild.class.getName());

		List<PatcherBuild> patcherBuilds =
			patcherBuildAlloyServiceInvoker.executeDynamicQuery(
				buildInactivePatcherBuildsDynamicQuery());

		if (patcherBuilds.isEmpty()) {
			return;
		}

		for (PatcherBuild patcherBuild : patcherBuilds) {
			User user = UserLocalServiceUtil.getUser(patcherBuild.getUserId());

			EmailUtil.sendPatcherTimeoutEmail(
				alloyController, patcherBuild, user.getEmailAddress());

			patcherBuild.setNotified(true);

			alloyController.updateModelIgnoreRequest(patcherBuild);
		}
	}

	@Transactional(
		isolation = Isolation.PORTAL, propagation = Propagation.REQUIRES_NEW,
		rollbackFor = Exception.class
	)
	public static void processOSBPatcherBuildCompileJenkinsStatus(
			AlloyController alloyController, User user, long patcherBuildId,
			String jenkinsStatusJSONString)
		throws Exception {

		PatcherBuild patcherBuild =
			PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherBuildId);

		validateOSBPatcherBuildCompileJenkinsStatus(
			patcherBuild, jenkinsStatusJSONString);

		String fileName = StringPool.BLANK;
		String sourceName = StringPool.BLANK;

		JSONObject jenkinsStatusJSONObject = JSONFactoryUtil.createJSONObject(
			jenkinsStatusJSONString);

		if (!jenkinsStatusJSONObject.has("exitValue") &&
			jenkinsStatusJSONObject.has("statusURL")) {

			PatcherFixUtil.updatePatcherFixJenkinsResult(
				alloyController, jenkinsStatusJSONObject,
				patcherBuild.getPatcherFixId());

			return;
		}

		int exitValue = jenkinsStatusJSONObject.getInt("exitValue");

		if (exitValue == 0) {
			setStatus(
				alloyController, user, patcherBuild,
				WorkflowConstants.STATUS_BUILD_COMPLETE);

			workflowCompletedPatcherBuildQAStatus(
				alloyController, user, patcherBuild);

			fileName = jenkinsStatusJSONObject.getString("fileName");
			sourceName = jenkinsStatusJSONObject.getString("sourceName");
		}
		else {
			setStatus(
				alloyController, user, patcherBuild,
				WorkflowConstants.STATUS_BUILD_FAILED);
		}

		patcherBuild.setFileName(fileName);
		patcherBuild.setSourceName(sourceName);

		alloyController.updateModelIgnoreRequest(patcherBuild);

		PatcherFixUtil.updatePatcherFixJenkinsResult(
			alloyController, jenkinsStatusJSONObject,
			patcherBuild.getPatcherFixId());

		sendTestJenkinsRequest(alloyController, user, patcherBuild);
	}

	@Transactional(
		isolation = Isolation.PORTAL, propagation = Propagation.REQUIRES_NEW,
		rollbackFor = Exception.class
	)
	public static void processOSBPatcherBuildMergeJenkinsStatus(
			AlloyController alloyController, User user, long patcherFixId,
			String jenkinsStatusJSONString)
		throws Exception {

		validateOSBPatcherBuildMergeJenkinsStatus(
			patcherFixId, jenkinsStatusJSONString);

		JSONObject jenkinsStatusJSONObject = JSONFactoryUtil.createJSONObject(
			jenkinsStatusJSONString);

		if (jenkinsStatusJSONObject.has("statusURL")) {
			AlloyServiceInvoker alloyServiceInvoker = new AlloyServiceInvoker(
				PatcherBuild.class.getName());

			List<PatcherBuild> patcherBuilds =
				alloyServiceInvoker.executeDynamicQuery(
					new Object[] {"patcherFixId", patcherFixId});

			for (PatcherBuild patcherBuild : patcherBuilds) {
				if (!isLatestPatcherBuild(patcherBuild)) {
					continue;
				}

				PatcherFixUtil.updatePatcherFixJenkinsResult(
					alloyController, jenkinsStatusJSONObject,
					patcherBuild.getPatcherFixId());
			}

			return;
		}

		String outcome = jenkinsStatusJSONObject.getString("outcome");

		OSBPatcherServletOutcome osbPatcherServletOutcome =
			JSONFactoryUtil.looseDeserializeSafe(
				outcome, OSBPatcherServletOutcome.class);

		List<String> messages = new ArrayList<>();

		AlloyServiceInvoker alloyServiceInvoker = new AlloyServiceInvoker(
			PatcherBuild.class.getName());

		List<PatcherBuild> patcherBuilds =
			alloyServiceInvoker.executeDynamicQuery(
				new Object[] {"patcherFixId", patcherFixId});

		for (PatcherBuild patcherBuild : patcherBuilds) {
			if (!isLatestPatcherBuild(patcherBuild)) {
				continue;
			}

			updatePatcherBuildStatus(
				alloyController, user, patcherBuild,
				osbPatcherServletOutcome.getStatus(),
				osbPatcherServletOutcome.getResult(), messages);
		}
	}

	@Transactional(
		isolation = Isolation.PORTAL, propagation = Propagation.REQUIRES_NEW,
		rollbackFor = Exception.class
	)
	public static void processOSBPatcherBuildTestJenkinsStatus(
			AlloyController alloyController, User user, long patcherBuildId,
			String jenkinsStatusJSONString)
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
			JSONObject resultJSONObject = resultsJSONArray.getJSONObject(i);

			PatcherFixUtil.updatePatcherFixJenkinsResult(
				alloyController, jenkinsStatusJSONObject,
				patcherBuild.getPatcherFixId());
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

		alloyController.updateModelIgnoreRequest(patcherBuild);
	}

	public static void reindexRelatedModels(
			AlloyController alloyController, PatcherBuild patcherBuild)
		throws Exception {

		List<PatcherAccount> patcherBuildPatcherAccounts =
			PatcherAccountLocalServiceUtil.getPatcherBuildPatcherAccounts(
				patcherBuild.getPatcherBuildId());

		for (PatcherAccount patcherBuildPatcherAccount :
				patcherBuildPatcherAccounts) {

			alloyController.indexModel(patcherBuildPatcherAccount);
		}
	}

	public static void releasePatcherBuild(PatcherBuild patcherBuild)
		throws Exception {

		String hotfixFileName = getLiferayHotfixFileName(
			patcherBuild.getFileName());

		String path =
			PortletPropsValues.HOTFIX_MOUNT_PATH + StringPool.FORWARD_SLASH +
				patcherBuild.getFileName();

		String quarterReleasePath = getQuarterReleaseBuildPath(path);

		try {
			HelpCenterUtil.addAttachmentComment(
				hotfixFileName, patcherBuild, quarterReleasePath);
		}
		catch (FileNotFoundException e) {
			HelpCenterUtil.addAttachmentComment(
				hotfixFileName, patcherBuild, path);
		}
	}

	public static void removePreviousMainFixVersionsFromBuildsFixes(
			long patcherBuildId, PatcherFix patcherFix,
			List<Long> patcherBuildPatcherFixIds)
		throws Exception {

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
			AlloyController alloyController, PatcherBuild parentPatcherBuild,
			String accountEntryCode)
		throws Exception {

		parentPatcherBuild = setLatestPatcherBuild(
			alloyController, parentPatcherBuild, parentPatcherBuild.getKey(),
			parentPatcherBuild.getSupportTicket());

		if (parentPatcherBuild.isNew()) {
			addPatcherAccountPatcherBuild(
				alloyController, parentPatcherBuild.getPatcherBuildId(),
				accountEntryCode);

			PatcherAccount patcherAccount =
				PatcherAccountUtil.getPatcherAccount(accountEntryCode);

			parentPatcherBuild.setPatcherAccountId(
				patcherAccount.getPatcherAccountId());
		}

		if (!PatcherProjectVersionUtil.isCombinedBranchPatcherProjectVersion(
				parentPatcherBuild.getPatcherProjectVersionId())) {

			parentPatcherBuild.setPatcherFixId(0L);
		}

		alloyController.updateModelIgnoreRequest(
			parentPatcherBuild, "patcherBuildId",
			parentPatcherBuild.getPatcherBuildId());
	}

	@Transactional(
		isolation = Isolation.PORTAL, propagation = Propagation.REQUIRES_NEW,
		rollbackFor = Exception.class
	)
	public static void savePatcherBuild(
			AlloyController alloyController, User user,
			PatcherBuild parentPatcherBuild,
			Map<Long, List<Long>> patcherProjectVersionIdPatcherFixIdsMap,
			boolean mergeOnly, String accountEntryCode)
		throws Exception {

		alloyController.setUser(user);

		saveParentPatcherBuild(
			alloyController, parentPatcherBuild, accountEntryCode);

		patcherProjectVersionIdPatcherFixIdsMap =
			rebaseOtherProjectVersionPatcherFixes(
				alloyController, user, patcherProjectVersionIdPatcherFixIdsMap,
				parentPatcherBuild.getPatcherProjectVersionId());

		for (Map.Entry<Long, List<Long>> entry :
				patcherProjectVersionIdPatcherFixIdsMap.entrySet()) {

			List<Long> patcherFixIds = entry.getValue();
			long patcherProjectVersionId = entry.getKey();

			PatcherBuild patcherBuild = parentPatcherBuild;

			if (!PatcherProjectVersionUtil.
					isCombinedBranchPatcherProjectVersion(
						parentPatcherBuild.getPatcherProjectVersionId()) &&
				(parentPatcherBuild.getType() !=
					PatcherBuildConstants.TYPE_FIX_PACK) &&
				!PatcherBuildRelUtil.hasParentPatcherBuilds(
					parentPatcherBuild)) {

				patcherBuild = saveChildPatcherBuild(
					alloyController, parentPatcherBuild, patcherFixIds,
					patcherProjectVersionId);
			}

			updatePatcherBuildFixes(
				alloyController, user, patcherBuild, patcherFixIds);
		}

		List<BaseModel<?>> sendToJenkinsBaseModels =
			workflowRelatedPatcherBuildsToPendingStatus(
				alloyController, parentPatcherBuild, mergeOnly);

		for (BaseModel<?> sendToJenkinsBaseModel : sendToJenkinsBaseModels) {
			long status = BaseModelUtil.fetchBaseModelStatus(
				sendToJenkinsBaseModel);

			if ((sendToJenkinsBaseModel instanceof PatcherBuild) &&
				(status == WorkflowConstants.STATUS_BUILD_COMPILING)) {

				JenkinsUtil.sendDistJenkinsRequest(
					alloyController, user,
					(PatcherBuild)sendToJenkinsBaseModel);
			}
			else {
				JenkinsUtil.sendAgentJenkinsRequest(
					alloyController, user, sendToJenkinsBaseModel);
			}
		}
	}

	public static void sendTestJenkinsRequest(
			AlloyController alloyController, User user,
			PatcherBuild patcherBuild)
		throws Exception {

		if (patcherBuild.getType() == PatcherBuildConstants.TYPE_OFFICIAL) {
			JenkinsUtil.sendTestJenkinsRequest(
				alloyController, user, patcherBuild);
		}
	}

	public static PatcherBuild setLatestPatcherBuild(
			AlloyController alloyController, PatcherBuild patcherBuild,
			String key, String supportTicket)
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

				alloyController.updateModelIgnoreRequest(
					latestKeyBuild, "latestKeyBuild", false);
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

			alloyController.updateModelIgnoreRequest(
				latestKeyBuild, "latestKeyBuild", false,
				"latestSupportTicketBuild", false);
		}
		else {
			if (latestKeyBuild != null) {
				patcherBuild.setKeyVersion(
					BigDecimalUtil.add(latestKeyBuild.getKeyVersion(), 0.1));

				alloyController.updateModelIgnoreRequest(
					latestKeyBuild, "latestKeyBuild", false);
			}

			if (latestSupportTicketBuild != null) {
				patcherBuild.setSupportTicketVersion(
					BigDecimalUtil.add(
						latestSupportTicketBuild.getSupportTicketVersion(),
						0.1));

				alloyController.updateModelIgnoreRequest(
					latestSupportTicketBuild, "latestSupportTicketBuild",
					false);
			}
		}

		return patcherBuild;
	}

	public static void setStatus(
			AlloyController alloyController, User user,
			PatcherBuild patcherBuild, int status)
		throws Exception {

		patcherBuild.setStatus(status);

		workflowParentPatcherBuild(alloyController, user, patcherBuild);
	}

	public static void workflowCompletedPatcherBuildQAStatus(
			AlloyController alloyController, User user,
			PatcherBuild patcherBuild)
		throws Exception {

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
			AlloyController alloyController, User user,
			PatcherBuild childPatcherBuild)
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

		alloyController.updateModelIgnoreRequest(parentPatcherBuild);

		if (status == WorkflowConstants.STATUS_BUILD_COMPILING) {
			JenkinsUtil.sendDistJenkinsRequest(
				alloyController, user, parentPatcherBuild);
		}
		else if (status == WorkflowConstants.STATUS_BUILD_COMPLETE) {
			workflowCompletedPatcherBuildQAStatus(
				alloyController, user, parentPatcherBuild);

			sendTestJenkinsRequest(alloyController, user, parentPatcherBuild);
		}
	}

	public static List<PatcherFix> workflowPatcherBuildIncompleteFixesToPending(
			AlloyController alloyController, PatcherBuild patcherBuild)
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

				alloyController.updateModelIgnoreRequest(incompletePatcherFix);
			}

			pendingPatcherFixes.add(incompletePatcherFix);
		}

		return pendingPatcherFixes;
	}

	public static void workflowPatcherBuildMerging(
			AlloyController alloyController, User user,
			PatcherBuild patcherBuild, boolean mergeOnly)
		throws Exception {

		if (mergeOnly) {
			setStatus(
				alloyController, user, patcherBuild,
				WorkflowConstants.STATUS_BUILD_MERGING_ONLY);
		}
		else {
			setStatus(
				alloyController, user, patcherBuild,
				WorkflowConstants.STATUS_BUILD_MERGING);
		}
	}

	public static List<BaseModel<?>>
			workflowRelatedPatcherBuildsToPendingStatus(
				AlloyController alloyController,
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
				workflowPatcherBuildIncompleteFixesToPending(
					alloyController, patcherBuild);

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

			alloyController.updateModelIgnoreRequest(patcherBuild);

			if (patcherBuild.isChildBuild() ||
				!PatcherBuildRelUtil.hasChildPatcherBuilds(patcherBuild)) {

				sendToJenkinsBaseModels.add(patcherBuild);
			}
		}

		return sendToJenkinsBaseModels;
	}

	protected static List<PatcherFix> addChildPatcherFixes(
			AlloyController alloyController, User user,
			PatcherBuild patcherBuild, long patcherFixId,
			List<Long> conflictPatcherFixIds, List<String> messages)
		throws Exception {

		List<PatcherFix> childPatcherFixes = new ArrayList<>();

		for (long conflictPatcherFixId : conflictPatcherFixIds) {
			PatcherUtil.addMessage(
				"The fixes " + patcherFixId + " and " + conflictPatcherFixId +
					" conflict.",
				messages);

			List<Long> parentPatcherFixIds = new ArrayList<>();

			parentPatcherFixIds.add(patcherFixId);
			parentPatcherFixIds.add(conflictPatcherFixId);

			String sortedTickets = getSortedTickets(parentPatcherFixIds);

			PatcherFix childPatcherFix = PatcherFixUtil.addPatcherFix(
				alloyController, user, parentPatcherFixIds,
				patcherBuild.getPatcherProjectVersionId(), sortedTickets,
				PatcherFixConstants.TYPE_GENERATED,
				WorkflowConstants.STATUS_FIX_CONFLICT);

			childPatcherFixes.add(childPatcherFix);

			PatcherUtil.addMessage(
				"The conflict fix " + childPatcherFix.getPatcherFixId() +
					" with name " + childPatcherFix.getName() + " was created.",
				messages);
		}

		return childPatcherFixes;
	}

	protected static void addPatcherAccountPatcherBuild(
			AlloyController alloyController, long patcherBuildId,
			String accountEntryCode)
		throws Exception {

		AlloyServiceInvoker patcherAccountAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherAccount.class.getName());

		List<PatcherAccount> patcherAccounts =
			patcherAccountAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {"accountEntryCode", accountEntryCode});

		if (!patcherAccounts.isEmpty()) {
			PatcherAccount patcherAccount = patcherAccounts.get(0);

			PatcherBuildLocalServiceUtil.addPatcherAccountPatcherBuild(
				patcherAccount.getPatcherAccountId(), patcherBuildId);

			return;
		}

		PatcherAccount patcherAccount =
			PatcherAccountLocalServiceUtil.createPatcherAccount(0);

		patcherAccount.setPatcherAccountId(alloyController.increment());
		patcherAccount.setAccountEntryId(
			HelpCenterUtil.fetchAccountEntryId(accountEntryCode));
		patcherAccount.setAccountEntryCode(accountEntryCode);

		alloyController.updateModelIgnoreRequest(patcherAccount);

		PatcherUtil.pollIndexState(
			alloyController, PatcherAccount.class.getName(),
			patcherAccount.getPatcherAccountId());

		PatcherBuildLocalServiceUtil.addPatcherAccountPatcherBuild(
			patcherAccount.getPatcherAccountId(), patcherBuildId);
	}

	protected static DynamicQuery buildInactivePatcherBuildsDynamicQuery()
		throws Exception {

		AlloyServiceInvoker patcherBuildAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherBuild.class.getName());

		DynamicQuery patcherBuildsDynamicQuery =
			patcherBuildAlloyServiceInvoker.buildDynamicQuery(
				new Object[] {"notified", false});

		Calendar calendar = new GregorianCalendar();

		calendar.add(Calendar.HOUR, -3);

		Property modifiedDateProperty = PropertyFactoryUtil.forName(
			"modifiedDate");

		patcherBuildsDynamicQuery.add(
			modifiedDateProperty.lt(calendar.getTime()));

		Property statusProperty = PropertyFactoryUtil.forName("status");

		patcherBuildsDynamicQuery.add(
			statusProperty.in(
				new int[] {
					WorkflowConstants.STATUS_BUILD_COMPILING,
					WorkflowConstants.STATUS_BUILD_MERGING,
					WorkflowConstants.STATUS_BUILD_MERGING_ONLY
				}));

		return patcherBuildsDynamicQuery;
	}

	protected static DynamicQuery buildPatcherBuildKeyVersionDynamicQuery(
			PatcherBuild patcherBuild, boolean older)
		throws Exception {

		AlloyServiceInvoker patcherBuildAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherBuild.class.getName());

		DynamicQuery patcherBuildDynamicQuery =
			patcherBuildAlloyServiceInvoker.buildDynamicQuery(
				new Object[] {"key", patcherBuild.getKey()});

		Property keyVersionProperty = PropertyFactoryUtil.forName("keyVersion");

		if (older) {
			patcherBuildDynamicQuery.add(
				keyVersionProperty.lt(patcherBuild.getKeyVersion()));
		}
		else {
			patcherBuildDynamicQuery.add(
				keyVersionProperty.gt(patcherBuild.getKeyVersion()));
		}

		return patcherBuildDynamicQuery;
	}

	protected static DynamicQuery
			buildPatcherBuildSupportTicketVersionDynamicQuery(
				PatcherBuild patcherBuild, boolean older)
		throws Exception {

		AlloyServiceInvoker patcherBuildAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherBuild.class.getName());

		DynamicQuery patcherBuildDynamicQuery =
			patcherBuildAlloyServiceInvoker.buildDynamicQuery(
				new Object[] {
					"supportTicket", patcherBuild.getSupportTicket()
				});

		Property supportTicketVersionProperty = PropertyFactoryUtil.forName(
			"supportTicketVersion");

		if (older) {
			patcherBuildDynamicQuery.add(
				supportTicketVersionProperty.lt(
					patcherBuild.getSupportTicketVersion()));
		}
		else {
			patcherBuildDynamicQuery.add(
				supportTicketVersionProperty.gt(
					patcherBuild.getSupportTicketVersion()));
		}

		return patcherBuildDynamicQuery;
	}

	protected static boolean containsIncompletePatcherFix(
			PatcherBuild patcherBuild)
		throws Exception {

		List<PatcherFix> incompletePatcherFixes = getIncompletePatcherFixes(
			patcherBuild);

		return !incompletePatcherFixes.isEmpty();
	}

	protected static PatcherFix getLatestPatcherFix(PatcherFix patcherFix)
		throws Exception {

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

	protected static String getSortedTickets(List<Long> patcherFixIds)
		throws Exception {

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
				AlloyController alloyController, User user,
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
					alloyController, user,
					ListUtil.toList(new long[] {patcherFixId}),
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

	protected static PatcherBuild saveChildPatcherBuild(
			AlloyController alloyController, PatcherBuild parentPatcherBuild,
			List<Long> patcherFixIds, long patcherProjectVersionId)
		throws Exception {

		StringBundler sb = new StringBundler(patcherFixIds.size() * 2);

		for (long patcherFixId : patcherFixIds) {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				patcherFixId);

			sb.append(patcherFix.getName());
			sb.append(StringPool.COMMA);
		}

		String patcherBuildName = StringUtil.merge(
			PatcherUtil.sortTokens(sb.toString()));

		PatcherAccount patcherAccount =
			PatcherAccountLocalServiceUtil.getPatcherAccount(
				parentPatcherBuild.getPatcherAccountId());

		String key = generateKey(
			patcherProjectVersionId, patcherBuildName,
			patcherAccount.getAccountEntryCode(), parentPatcherBuild.getKey());

		if (PatcherBuildRelUtil.hasChildPatcherBuilds(parentPatcherBuild)) {
			List<PatcherBuild> childPatcherBuilds =
				PatcherBuildRelUtil.getChildPatcherBuilds(parentPatcherBuild);

			for (PatcherBuild childPatcherBuild : childPatcherBuilds) {
				if (StringUtil.equalsIgnoreCase(
						childPatcherBuild.getKey(), key)) {

					childPatcherBuild.setQaStatus(
						parentPatcherBuild.getQaStatus());

					return childPatcherBuild;
				}
			}
		}

		PatcherBuild childPatcherBuild =
			PatcherBuildLocalServiceUtil.createPatcherBuild(0);

		childPatcherBuild.setPatcherAccountId(
			patcherAccount.getPatcherAccountId());
		childPatcherBuild.setPatcherProductVersionId(
			parentPatcherBuild.getPatcherProductVersionId());
		childPatcherBuild.setPatcherProjectVersionId(patcherProjectVersionId);
		childPatcherBuild.setName(patcherBuildName);
		childPatcherBuild.setKey(key);
		childPatcherBuild.setType(parentPatcherBuild.getType());
		childPatcherBuild.setSupportTicket(
			parentPatcherBuild.getSupportTicket());
		childPatcherBuild.setChildBuild(true);
		childPatcherBuild.setQaStatus(parentPatcherBuild.getQaStatus());

		childPatcherBuild = setLatestPatcherBuild(
			alloyController, childPatcherBuild, key,
			parentPatcherBuild.getSupportTicket());

		alloyController.updateModelIgnoreRequest(childPatcherBuild);

		PatcherBuildRelUtil.addPatcherBuildRel(
			alloyController, childPatcherBuild.getPatcherBuildId(),
			parentPatcherBuild.getPatcherBuildId());

		return childPatcherBuild;
	}

	protected static void updatePatcherBuildFixes(
			AlloyController alloyController, User user,
			PatcherBuild patcherBuild, List<Long> patcherFixIds)
		throws Exception {

		PatcherFixLocalServiceUtil.clearPatcherBuildPatcherFixs(
			patcherBuild.getPatcherBuildId());

		for (long patcherFixId : patcherFixIds) {
			PatcherFixLocalServiceUtil.addPatcherBuildPatcherFix(
				patcherBuild.getPatcherBuildId(), patcherFixId);
		}

		if (!patcherBuild.isChildBuild() && (patcherFixIds.size() == 1) &&
			(patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK)) {

			patcherBuild.setPatcherFixId(patcherFixIds.get(0));

			updatePatcherBuildStatusMergeComplete(
				alloyController, user, patcherBuild);

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
					PatcherFixRelUtil.deletePatcherFixRelsByChildPatcherFixId(
						mainPatcherFix.getPatcherFixId());

					PatcherFixRelUtil.addPatcherFixRel(
						alloyController, mainPatcherFix.getPatcherFixId(),
						patcherFixIds);
				}

				mainPatcherFix.setGitHash(StringPool.BLANK);
				mainPatcherFix.setJenkinsResults(StringPool.BLANK);
				mainPatcherFix.setStatus(WorkflowConstants.STATUS_FIX_ADDING);

				alloyController.updateModelIgnoreRequest(mainPatcherFix);
			}
			else {
				mainPatcherFix = PatcherFixUtil.addNewPatcherFix(
					alloyController, user,
					PatcherFixConstants.KEY_VERSION_DEFAULT, patcherFixIds,
					patcherBuild.getPatcherProjectVersionId(),
					patcherBuild.getName(), type,
					WorkflowConstants.STATUS_FIX_ADDING);
			}
		}
		else {
			mainPatcherFix = PatcherFixUtil.addPatcherFix(
				alloyController, user, patcherFixIds,
				patcherBuild.getPatcherProjectVersionId(),
				patcherBuild.getName(), type,
				WorkflowConstants.STATUS_FIX_ADDING);
		}

		patcherBuild.setPatcherFixId(mainPatcherFix.getPatcherFixId());

		alloyController.updateModelIgnoreRequest(patcherBuild);

		PatcherFixLocalServiceUtil.addPatcherBuildPatcherFix(
			patcherBuild.getPatcherBuildId(), mainPatcherFix.getPatcherFixId());

		if (!patcherBuild.isChildBuild()) {
			removePreviousMainFixVersionsFromBuildsFixes(
				patcherBuild.getPatcherBuildId(), mainPatcherFix,
				patcherFixIds);

			if (isPreviousPatcherBuildMainFixEqualsCurrentBuildMainFix(
					patcherBuild)) {

				updatePatcherBuildStatusMergeComplete(
					alloyController, user, patcherBuild);
			}
		}
	}

	protected static void updatePatcherBuildsPatcherFixes(
			PatcherBuild patcherBuild, List<PatcherFix> childPatcherFixes,
			List<String> messages)
		throws Exception {

		PatcherFix longestTicketPatcherFix =
			PatcherFixUtil.fetchLongestTicketPatcherFix(childPatcherFixes);

		PatcherBuildLocalServiceUtil.addPatcherFixPatcherBuild(
			longestTicketPatcherFix.getPatcherFixId(),
			patcherBuild.getPatcherBuildId());

		PatcherUtil.addMessage(
			"The fix " + longestTicketPatcherFix.getPatcherFixId() +
				" was added to the build " + patcherBuild.getPatcherBuildId(),
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
			PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixs(
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
			AlloyController alloyController, User user,
			PatcherBuild patcherBuild, int OSBPatcherServletOutcomeStatus,
			String OSBPatcherServletOutcomeResult, List<String> messages)
		throws Exception {

		if (OSBPatcherServletOutcomeStatus ==
				OSBPatcherServletOutcome.STATUS_SUCCESS) {

			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				patcherBuild.getPatcherFixId());

			patcherFix.setGitHash(OSBPatcherServletOutcomeResult);

			patcherFix.setStatus(WorkflowConstants.STATUS_FIX_COMPLETE);

			alloyController.updateModelIgnoreRequest(patcherFix);

			updatePatcherBuildStatusMergeComplete(
				alloyController, user, patcherBuild);

			PatcherUtil.addMessage(
				"The patch for build " + patcherBuild.getPatcherBuildId() +
					" with name " + patcherBuild.getName() + " was successful.",
				messages);
		}
		else if (OSBPatcherServletOutcomeStatus ==
					OSBPatcherServletOutcome.STATUS_CONFLICT) {

			PatcherUtil.addMessage(
				"The patch for build " + patcherBuild.getPatcherBuildId() +
					" with name " + patcherBuild.getName() + " has a conflict.",
				messages);

			flexjson.JSONDeserializer jsonDeserializer =
				new flexjson.JSONDeserializer<HashMap<String, Object>>();

			HashMap<String, Object> conflictPatcherFixIdsMap =
				(HashMap<String, Object>)jsonDeserializer.deserialize(
					OSBPatcherServletOutcomeResult, HashMap.class);

			for (Map.Entry<String, Object> conflictPatcherFixIdsEntry :
					conflictPatcherFixIdsMap.entrySet()) {

				long patcherFixId = GetterUtil.getLong(
					conflictPatcherFixIdsEntry.getKey());

				List<Long> conflictPatcherFixIds = getLongList(
					conflictPatcherFixIdsEntry.getValue());

				List<PatcherFix> childPatcherFixes = addChildPatcherFixes(
					alloyController, user, patcherBuild, patcherFixId,
					conflictPatcherFixIds, messages);

				updatePatcherBuildsPatcherFixes(
					patcherBuild, childPatcherFixes, messages);

				reindexRelatedModels(alloyController, patcherBuild);
			}

			List<PatcherFix> patcherBuildPatcherFixes =
				PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixs(
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
					JenkinsUtil.sendAgentJenkinsRequest(
						alloyController, user, curPatcherBuild);
				}

				return;
			}

			PatcherFix mainPatcherFix =
				PatcherFixLocalServiceUtil.getPatcherFix(
					patcherBuild.getPatcherFixId());

			mainPatcherFix.setStatus(WorkflowConstants.STATUS_FIX_CONFLICT);

			alloyController.updateModelIgnoreRequest(mainPatcherFix);

			if ((patcherBuild.getStatus() ==
					WorkflowConstants.STATUS_BUILD_MERGING_ONLY) ||
				(patcherBuild.getStatus() ==
					WorkflowConstants.STATUS_BUILD_CONFLICT_MERGING_ONLY)) {

				setStatus(
					alloyController, user, patcherBuild,
					WorkflowConstants.STATUS_BUILD_CONFLICT_MERGING_ONLY);
			}
			else {
				setStatus(
					alloyController, user, patcherBuild,
					WorkflowConstants.STATUS_BUILD_CONFLICT);
			}

			alloyController.updateModelIgnoreRequest(patcherBuild);
		}
		else {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				patcherBuild.getPatcherFixId());

			patcherFix.setStatus(WorkflowConstants.STATUS_FIX_FAILED);

			alloyController.updateModelIgnoreRequest(patcherFix);

			setStatus(
				alloyController, user, patcherBuild,
				WorkflowConstants.STATUS_BUILD_FAILED);

			alloyController.updateModelIgnoreRequest(patcherBuild);

			PatcherUtil.addMessage(
				"The patch for build " + patcherBuild.getPatcherBuildId() +
					" with name " + patcherBuild.getName() + " failed.",
				messages);
		}
	}

	protected static void updatePatcherBuildStatusMergeComplete(
			AlloyController alloyController, User user,
			PatcherBuild patcherBuild)
		throws Exception {

		if (isMergeOnly(patcherBuild)) {
			setStatus(
				alloyController, user, patcherBuild,
				WorkflowConstants.STATUS_BUILD_COMPLETE_MERGING_ONLY);

			alloyController.updateModelIgnoreRequest(patcherBuild);
		}
		else {
			setStatus(
				alloyController, user, patcherBuild,
				WorkflowConstants.STATUS_BUILD_COMPILING);

			alloyController.updateModelIgnoreRequest(patcherBuild);

			JenkinsUtil.sendDistJenkinsRequest(
				alloyController, user, patcherBuild);
		}
	}

	protected static void validateOSBPatcherBuildCompileJenkinsStatus(
			PatcherBuild patcherBuild, String jenkinsStatusJSONString)
		throws Exception {

		JenkinsUtil.validateJenkinsRequestKey(
			patcherBuild, jenkinsStatusJSONString);
	}

	protected static void validateOSBPatcherBuildMergeJenkinsStatus(
			long patcherFixId, String jenkinsStatusJSONString)
		throws Exception {

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
			patcherFixId);

		JenkinsUtil.validateJenkinsRequestKey(
			patcherFix, jenkinsStatusJSONString);
	}

	protected static void validateOSBPatcherBuildTestJenkinsStatus(
			PatcherBuild patcherBuild, String jenkinsStatusJSONString)
		throws Exception {

		JenkinsUtil.validateJenkinsRequestKey(
			patcherBuild, jenkinsStatusJSONString);

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