/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.AlloyController;
import com.liferay.alloy.mvc.AlloyServiceInvoker;
import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherFixRel;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.model.impl.PatcherFixModelImpl;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Zsolt Balogh
 */
public class PatcherFixUtil {

	public static PatcherFix addNewPatcherFix(
			AlloyController alloyController, User user, double keyVersion,
			List<Long> parentPatcherFixIds, long patcherProjectVersionId,
			String name, int type, int status)
		throws Exception {

		PatcherFix newPatcherFix = PatcherFixLocalServiceUtil.createPatcherFix(
			0);

		newPatcherFix.setUserId(user.getUserId());
		newPatcherFix.setUserName(user.getFullName());
		newPatcherFix.setPatcherProductVersionId(
			PatcherProjectVersionUtil.getPatcherProductVersionId(
				patcherProjectVersionId));
		newPatcherFix.setPatcherProjectVersionId(patcherProjectVersionId);
		newPatcherFix.setName(name);
		newPatcherFix.setKey(
			PatcherFixUtil.generateKey(patcherProjectVersionId, name));
		newPatcherFix.setKeyVersion(keyVersion);
		newPatcherFix.setType(type);
		newPatcherFix.setLatestFix(true);
		newPatcherFix.setStatus(status);

		alloyController.updateModelIgnoreRequest(newPatcherFix);

		PatcherFixRelUtil.addPatcherFixRel(
			alloyController, newPatcherFix.getPatcherFixId(),
			parentPatcherFixIds);

		return newPatcherFix;
	}

	public static PatcherFix addPatcherFix(
			AlloyController alloyController, User user,
			List<Long> parentPatcherFixIds, long patcherProjectVersionId,
			String name, int type, int status)
		throws Exception {

		double keyVersion = PatcherFixConstants.KEY_VERSION_DEFAULT;

		List<PatcherFix> patcherFixes = getFilteredPatcherFixes(
			patcherProjectVersionId, name, true);

		if (!patcherFixes.isEmpty()) {
			PatcherFix existingPatcherFix = patcherFixes.get(0);

			if (status == WorkflowConstants.STATUS_FIX_CONFLICT) {
				if (!PatcherFixRelUtil.hasParentPatcherFixes(
						existingPatcherFix)) {

					PatcherFixRelUtil.addPatcherFixRel(
						alloyController, existingPatcherFix.getPatcherFixId(),
						parentPatcherFixIds);
				}

				return existingPatcherFix;
			}

			List<Long> existingParentPatcherFixIds =
				PatcherFixRelUtil.getParentPatcherFixIds(
					existingPatcherFix.getPatcherFixId());

			PatcherFixPack patcherFixPack =
				PatcherFixPackUtil.fetchPatcherFixPack(
					existingPatcherFix.getName(),
					existingPatcherFix.getPatcherProjectVersionId());

			if (PatcherUtil.equals(
					existingParentPatcherFixIds, parentPatcherFixIds) ||
				(existingPatcherFix.getFixPackStatus() ==
					WorkflowConstants.STATUS_FIX_FIX_PACK_READY) ||
				((patcherFixPack != null) &&
				 (existingPatcherFix.getStatus() !=
					 WorkflowConstants.STATUS_FIX_COMPLETE))) {

				return existingPatcherFix;
			}

			existingPatcherFix.setLatestFix(false);

			alloyController.updateModelIgnoreRequest(existingPatcherFix);

			keyVersion = BigDecimalUtil.add(
				existingPatcherFix.getKeyVersion(), 0.1);
		}

		return addNewPatcherFix(
			alloyController, user, keyVersion, parentPatcherFixIds,
			patcherProjectVersionId, name, type, status);
	}

	public static boolean containsIncompleteRebasePatcherFix(
			List<Long> patcherFixIds)
		throws Exception {

		for (long patcherFixId : patcherFixIds) {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				patcherFixId);

			if ((patcherFix.getType() == PatcherFixConstants.TYPE_REBASE) &&
				(patcherFix.getStatus() !=
					WorkflowConstants.STATUS_FIX_COMPLETE)) {

				return true;
			}
		}

		return false;
	}

	public static boolean containsNewerVersionPatcherFixIds(
			List<Long> patcherFixIds, long patcherFixId)
		throws Exception {

		for (long curPatcherFixId : patcherFixIds) {
			if (curPatcherFixId == patcherFixId) {
				continue;
			}

			PatcherFix curPatcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				curPatcherFixId);

			String curKey = curPatcherFix.getKey();

			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				patcherFixId);

			if (curKey.equals(patcherFix.getKey()) &&
				(curPatcherFix.getKeyVersion() > patcherFix.getKeyVersion())) {

				return true;
			}
		}

		return false;
	}

	public static boolean containsPatcherFixComment(long patcherBuildId)
		throws Exception {

		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixs(
				patcherBuildId);

		for (PatcherFix patcherFix : patcherFixes) {
			if (Validator.isNotNull(patcherFix.getComments())) {
				return true;
			}
		}

		return false;
	}

	public static boolean containsPatcherFixWorkaround(long patcherBuildId)
		throws Exception {

		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixs(
				patcherBuildId);

		for (PatcherFix patcherFix : patcherFixes) {
			if (patcherFix.getType() == PatcherFixConstants.TYPE_WORKAROUND) {
				return true;
			}
		}

		return false;
	}

	public static void deletePatcherFix(
			AlloyController alloyController, PatcherFix patcherFix)
		throws Exception {

		if (patcherFix.getKeyVersion() !=
				PatcherFixConstants.KEY_VERSION_DEFAULT) {

			PatcherFix oldPatcherFix = fetchPatcherFixByNextKeyVersion(
				patcherFix, true);

			if (oldPatcherFix != null) {
				oldPatcherFix.setLatestFix(true);

				boolean patcherFixExcluded = false;

				if (patcherFix.getType() == PatcherFixConstants.TYPE_EXCLUDED) {
					patcherFixExcluded = true;
				}

				if (patcherFixExcluded) {
					oldPatcherFix.setType(PatcherFixConstants.TYPE_EXCLUDED);
				}

				updateObsolete(
					alloyController, oldPatcherFix, patcherFixExcluded);

				alloyController.updateModel(oldPatcherFix);
			}
		}

		PatcherFixRelUtil.deletePatcherFixRelsByChildPatcherFixId(
			patcherFix.getPatcherFixId());

		PatcherFixLocalServiceUtil.deletePatcherFix(patcherFix);
	}

	public static PatcherFix fetchLongestTicketPatcherFix(
			List<PatcherFix> patcherFixes)
		throws Exception {

		PatcherFix longestTicketPatcherFix = null;

		int longestTicketSize = 0;

		for (PatcherFix patcherFix : patcherFixes) {
			List<String> tickets = PatcherUtil.getTickets(patcherFix.getName());

			if (longestTicketSize < tickets.size()) {
				longestTicketSize = tickets.size();

				longestTicketPatcherFix = patcherFix;
			}
		}

		return longestTicketPatcherFix;
	}

	public static PatcherFix fetchPatcherFixByLatestFix(String key)
		throws Exception {

		List<PatcherFix> patcherFixes = getFilteredPatcherFixes(key, true);

		if (!patcherFixes.isEmpty()) {
			return patcherFixes.get(0);
		}

		return null;
	}

	public static PatcherFix fetchPatcherFixByNextKeyVersion(
			PatcherFix patcherFix, boolean older)
		throws Exception {

		AlloyServiceInvoker patcherFixAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFix.class.getName());

		DynamicQuery patcherFixKeyVersionDynamicQuery =
			buildPatcherFixKeyVersionDynamicQuery(patcherFix, older);

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(
			PatcherFixModelImpl.TABLE_NAME, "keyVersion", !older);

		List<PatcherFix> patcherFixes =
			patcherFixAlloyServiceInvoker.executeDynamicQuery(
				patcherFixKeyVersionDynamicQuery, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, obc);

		if (patcherFixes.isEmpty()) {
			return null;
		}

		return patcherFixes.get(0);
	}

	public static PatcherFix fetchSiblingChildPatcherBuildMainFix(
			PatcherFix mainPatcherFix)
		throws Exception {

		AlloyServiceInvoker patcherBuildAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherBuild.class.getName());

		List<PatcherBuild> childPatcherBuilds =
			patcherBuildAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {
					"patcherFixId", mainPatcherFix.getPatcherFixId(),
					"childBuild", true
				});

		if (childPatcherBuilds.isEmpty()) {
			return null;
		}

		PatcherBuild childPatcherBuild = childPatcherBuilds.get(0);

		PatcherBuild siblingChildPatcherBuild =
			PatcherBuildRelUtil.fetchSiblingChildPatcherBuild(
				childPatcherBuild);

		if (siblingChildPatcherBuild == null) {
			return null;
		}

		return PatcherFixLocalServiceUtil.getPatcherFix(
			siblingChildPatcherBuild.getPatcherFixId());
	}

	public static String generateKey(long patcherProjectVersionId, String name)
		throws Exception {

		return PatcherUtil.generatePatcherKey(
			PatcherFix.class.getName(), patcherProjectVersionId,
			StringUtil.merge(PatcherUtil.sortTokens(name)));
	}

	public static String generateKey(
			long patcherProjectVersionId, String key, String name)
		throws Exception {

		return PatcherUtil.generatePatcherKey(
			PatcherFix.class.getName(), patcherProjectVersionId, key,
			StringUtil.merge(PatcherUtil.sortTokens(name)));
	}

	public static Map<String, Set<String>> getComponentDependencies(
		String dependencies) {

		Map<String, Set<String>> componentDependencies = new HashMap<>();

		String[] phrases = StringUtil.split(dependencies);

		for (String phrase : phrases) {
			String[] componentNames = StringUtil.split(phrase, "->");

			String dependentComponentName = componentNames[0];

			Set<String> prerequisiteComponentNames = new HashSet<>();

			if (componentDependencies.containsKey(dependentComponentName)) {
				prerequisiteComponentNames = componentDependencies.get(
					dependentComponentName);
			}

			prerequisiteComponentNames.add(componentNames[1]);

			componentDependencies.put(
				dependentComponentName, prerequisiteComponentNames);
		}

		return componentDependencies;
	}

	public static List<PatcherFix> getFilteredPatcherFixes(
			long patcherProjectVersionId, int status)
		throws Exception {

		if (status == WorkflowConstants.STATUS_ANY) {
			return getFilteredPatcherFixesByAttributes(
				new Object[] {
					"patcherProjectVersionId", patcherProjectVersionId,
					"latestFix", true
				});
		}

		return getFilteredPatcherFixesByAttributes(
			new Object[] {
				"patcherProjectVersionId", patcherProjectVersionId, "latestFix",
				true, "status", status
			});
	}

	public static List<PatcherFix> getFilteredPatcherFixes(
			long patcherProjectVersionId, String name, boolean latestFix)
		throws Exception {

		return getFilteredPatcherFixesByAttributes(
			new Object[] {
				"patcherProjectVersionId", patcherProjectVersionId, "name",
				name, "latestFix", latestFix
			});
	}

	public static List<PatcherFix> getFilteredPatcherFixes(
			String key, boolean latestFix)
		throws Exception {

		return getFilteredPatcherFixesByAttributes(
			new Object[] {"key", key, "latestFix", latestFix});
	}

	public static Date getOldestPatcherFixCreateDate(long patcherFixPackId)
		throws Exception {

		List<PatcherFix> patcherFixPackPatcherFixs =
			PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixs(
				patcherFixPackId);

		if (patcherFixPackPatcherFixs.isEmpty()) {
			return null;
		}

		if (patcherFixPackPatcherFixs.size() == 1) {
			PatcherFix patcherFix = patcherFixPackPatcherFixs.get(0);

			return patcherFix.getCreateDate();
		}

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(
			PatcherFixModelImpl.TABLE_NAME, "createDate", true);

		patcherFixPackPatcherFixs = ListUtil.sort(
			patcherFixPackPatcherFixs, obc);

		PatcherFix patcherFix = patcherFixPackPatcherFixs.get(0);

		return patcherFix.getCreateDate();
	}

	public static List<PatcherFix> getParentPatcherFixes(PatcherFix patcherFix)
		throws Exception {

		AlloyServiceInvoker patcherFixAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFix.class.getName());

		DynamicQuery patcherFixDynamicQuery =
			patcherFixAlloyServiceInvoker.buildDynamicQuery();

		Property patcherFixIdProperty = PropertyFactoryUtil.forName(
			"patcherFixId");

		List<Long> parentPatcherFixIds =
			PatcherFixRelUtil.getParentPatcherFixIds(
				patcherFix.getPatcherFixId());

		if (parentPatcherFixIds.isEmpty()) {
			return Collections.emptyList();
		}

		patcherFixDynamicQuery.add(
			patcherFixIdProperty.in(parentPatcherFixIds));

		return patcherFixAlloyServiceInvoker.executeDynamicQuery(
			patcherFixDynamicQuery);
	}

	public static List<Long> getPatcherBuildFixIdsByFixStatus(
			PatcherBuild patcherBuild, long patcherFixStatus)
		throws Exception {

		List<Long> patcherFixIds = new ArrayList<>();

		List<Long> fixIds =
			PatcherBuildUtil.getRelatedPatcherBuildsPatcherFixIds(patcherBuild);

		List<PatcherFix> patcherFixes = toPatcherFixes(fixIds);

		for (PatcherFix patcherFix : patcherFixes) {
			if (patcherFix.getStatus() == patcherFixStatus) {
				patcherFixIds.add(patcherFix.getPatcherFixId());
			}
		}

		if (patcherFixIds.size() > 1) {
			patcherFixIds.remove(patcherBuild.getPatcherFixId());
		}

		ListUtil.distinct(patcherFixIds);

		return patcherFixIds;
	}

	public static PatcherFix getPatcherFix(
		PatcherFixRadix patcherFixRadix, List<String> patcherBuildTickets) {

		PatcherFix patcherFix = patcherFixRadix.getPatcherFix();

		while (patcherFix != null) {
			if (containsAllTickets(
					patcherBuildTickets,
					StringUtil.split(patcherFix.getName()))) {

				return patcherFix;
			}

			patcherFix = patcherFixRadix.getPatcherFix();
		}

		return null;
	}

	public static List<PatcherFix> getPatcherFixesSelection(
			long patcherProjectVersionId, boolean includeAnyStatusRebaseFixes)
		throws Exception {

		List<PatcherFix> filteredPatcherFixes = new ArrayList<>();

		List<PatcherFix> patcherFixes = PatcherFixUtil.getFilteredPatcherFixes(
			patcherProjectVersionId, WorkflowConstants.STATUS_FIX_COMPLETE);

		if (includeAnyStatusRebaseFixes) {
			patcherFixes = ListUtil.copy(patcherFixes);

			patcherFixes.addAll(getRebasePatcherFixes(patcherProjectVersionId));

			ListUtil.distinct(patcherFixes);
		}

		for (PatcherFix patcherFix : patcherFixes) {
			if ((patcherFix.getType() != PatcherFixConstants.TYPE_PATCH) &&
				(patcherFix.getType() != PatcherFixConstants.TYPE_WORKAROUND) &&
				(patcherFix.getType() != PatcherFixConstants.TYPE_REBASE)) {

				continue;
			}

			if (patcherFix.isObsolete()) {
				continue;
			}

			filteredPatcherFixes.add(patcherFix);
		}

		return filteredPatcherFixes;
	}

	public static String getPatcherFixGitHubURL(long patcherFixId)
		throws Exception {

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
			patcherFixId);

		if (patcherFix == null) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(12);

		sb.append(PortletPropsValues.GITHUB_URL);
		sb.append(StringPool.SLASH);

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherFix.getPatcherProjectVersionId());

		sb.append(patcherProjectVersion.getRepositoryName());

		sb.append(StringPool.SLASH);
		sb.append("compare");
		sb.append(StringPool.SLASH);
		sb.append(patcherProjectVersion.getCommittish());
		sb.append(StringPool.PERIOD);
		sb.append(StringPool.PERIOD);
		sb.append(StringPool.PERIOD);
		sb.append(PortletPropsValues.OSB_PATCHER_GIT_TAG_PREFIX);
		sb.append(patcherFix.getPatcherFixId());

		return sb.toString();
	}

	public static List<Long> getPatcherFixIds(
			List<PatcherFixPack> patcherFixPacks)
		throws Exception {

		List<Long> patcherFixIds = new ArrayList<>();

		for (PatcherFixPack patcherFixPack : patcherFixPacks) {
			patcherFixIds.addAll(getPatcherFixIds(patcherFixPack));
		}

		return patcherFixIds;
	}

	public static List<Long> getPatcherFixIds(PatcherFixPack patcherFixPack)
		throws Exception {

		List<Long> patcherFixIds = new ArrayList<>();

		List<PatcherFix> patcherFixPackPatcherFixes =
			PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixs(
				patcherFixPack.getPatcherFixPackId());

		for (PatcherFix patcherFixPackPatcherFix : patcherFixPackPatcherFixes) {
			patcherFixIds.add(patcherFixPackPatcherFix.getPatcherFixId());
		}

		List<PatcherFix> previousFixPackBuildFixes =
			getPreviousFixPackBuildFixes(patcherFixPack);

		for (PatcherFix previousFixPackBuildFix : previousFixPackBuildFixes) {
			patcherFixIds.add(previousFixPackBuildFix.getPatcherFixId());
		}

		return patcherFixIds;
	}

	public static PatcherFixRadix getPatcherFixRadix(
			long patcherProjectVersionId, List<String> patcherBuildTickets,
			List<PatcherFix> patcherFixesSelection)
		throws Exception {

		PatcherFixRadix patcherFixRadix = new PatcherFixRadix();

		for (PatcherFix patcherFix : patcherFixesSelection) {
			String[] tickets = StringUtil.split(patcherFix.getName());

			if (!containsAllTickets(patcherBuildTickets, tickets)) {
				continue;
			}

			patcherFixRadix.addPatcherFix(tickets.length, patcherFix);
		}

		return patcherFixRadix;
	}

	public static PatcherFixRadix getPatcherFixRadix(
			long patcherProjectVersionId, List<String> patcherBuildTickets,
			List<PatcherFix> patcherFixesSelection, String[] ticketsFilter)
		throws Exception {

		PatcherFixRadix patcherFixRadix = new PatcherFixRadix();

		for (PatcherFix patcherFix : patcherFixesSelection) {
			String[] tickets1 = StringUtil.split(patcherFix.getName());

			if (!containsAllTickets(patcherBuildTickets, tickets1)) {
				continue;
			}

			List<String> tickets2 = PatcherUtil.sortTokens(
				patcherFix.getName());

			if (!containsAnyTickets(tickets2, ticketsFilter)) {
				continue;
			}

			patcherFixRadix.addPatcherFix(tickets1.length, patcherFix);
		}

		return patcherFixRadix;
	}

	public static List<PatcherFix> getPreviousFixPackBuildFixes(
			PatcherFixPack patcherFixPack)
		throws Exception {

		PatcherFixPack patcherFixPackVersion =
			PatcherFixPackUtil.fetchPatcherFixPackVersion(patcherFixPack, true);

		if (Validator.isNotNull(patcherFixPackVersion)) {
			return ListUtil.copy(
				PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixs(
					patcherFixPackVersion.getPatcherBuildId()));
		}

		return new ArrayList<>();
	}

	public static List<Long> getPreviousVersionsPatcherFixIds(
			PatcherFix patcherFix)
		throws Exception {

		List<Long> patcherFixIds = new ArrayList<>();

		AlloyServiceInvoker patcherFixAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFix.class.getName());

		DynamicQuery previousVersionsPatcherFixesDynamicQuery =
			buildPreviousVersionsPatcherFixesQuery(patcherFix);

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(
			PatcherFixModelImpl.TABLE_NAME, "keyVersion", false);

		List<PatcherFix> patcherFixes =
			patcherFixAlloyServiceInvoker.executeDynamicQuery(
				previousVersionsPatcherFixesDynamicQuery, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, obc);

		if (!patcherFixes.isEmpty()) {
			for (PatcherFix curPatcherFix : patcherFixes) {
				patcherFixIds.add(curPatcherFix.getPatcherFixId());
			}
		}

		return patcherFixIds;
	}

	public static List<PatcherFix> getRebasePatcherFixes(
			long patcherProjectVersionId)
		throws Exception {

		return getFilteredPatcherFixesByAttributes(
			new Object[] {
				"patcherProjectVersionId", patcherProjectVersionId, "latestFix",
				true, "type", PatcherFixConstants.TYPE_REBASE
			});
	}

	public static PatcherFix getWeightedStatusPatcherFix(
			List<Long> patcherFixIds)
		throws Exception {

		Map<Integer, PatcherFix> statusPatcherFixMap = new HashMap<>();

		for (long patcherFixId : patcherFixIds) {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				patcherFixId);

			if (patcherFix.getStatus() == WorkflowConstants.STATUS_FIX_FAILED) {
				return patcherFix;
			}

			statusPatcherFixMap.put(patcherFix.getStatus(), patcherFix);
		}

		if (statusPatcherFixMap.containsKey(
				WorkflowConstants.STATUS_FIX_REBASE_CONFLICT)) {

			return statusPatcherFixMap.get(
				WorkflowConstants.STATUS_FIX_REBASE_CONFLICT);
		}
		else if (statusPatcherFixMap.containsKey(
					WorkflowConstants.STATUS_FIX_REBASING)) {

			return statusPatcherFixMap.get(
				WorkflowConstants.STATUS_FIX_REBASING);
		}
		else if (statusPatcherFixMap.containsKey(
					WorkflowConstants.STATUS_FIX_CONFLICT)) {

			return statusPatcherFixMap.get(
				WorkflowConstants.STATUS_FIX_CONFLICT);
		}
		else if (statusPatcherFixMap.containsKey(
					WorkflowConstants.STATUS_FIX_ADDING)) {

			return statusPatcherFixMap.get(WorkflowConstants.STATUS_FIX_ADDING);
		}

		return statusPatcherFixMap.get(WorkflowConstants.STATUS_FIX_COMPLETE);
	}

	public static boolean isCoveredPatcherFixTickets(
			PatcherFix patcherFix, List<PatcherFix> patcherFixPackPatcherFixes)
		throws Exception {

		for (PatcherFix patcherFixPackPatcherFix : patcherFixPackPatcherFixes) {
			List<String> patcherFixPackPatcherFixTickets =
				PatcherUtil.getTickets(patcherFixPackPatcherFix.getName());

			if (patcherFixPackPatcherFixTickets.containsAll(
					PatcherUtil.getTickets(patcherFix.getName()))) {

				return true;
			}
		}

		return false;
	}

	public static boolean isDeletable(PatcherFix patcherFix) {
		if (patcherFix == null) {
			return false;
		}

		try {
			validateDelete(patcherFix);
		}
		catch (Exception e) {
			return false;
		}

		return true;
	}

	public static boolean isIncomplete(PatcherFix patcherFix) throws Exception {
		if (patcherFix.getStatus() != WorkflowConstants.STATUS_FIX_COMPLETE) {
			return true;
		}

		return false;
	}

	public static boolean isMainPatcherFix(long patcherFixId) throws Exception {
		AlloyServiceInvoker patcherBuildAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherBuild.class.getName());

		List<Long> patcherBuildIds =
			patcherBuildAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {"patcherFixId", patcherFixId});

		return !patcherBuildIds.isEmpty();
	}

	public static void notifyUsersInactivePatcherFixes(
			AlloyController alloyController)
		throws Exception {

		AlloyServiceInvoker patcherFixAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFix.class.getName());

		List<PatcherFix> patcherFixes =
			patcherFixAlloyServiceInvoker.executeDynamicQuery(
				buildInactivePatcherFixesDynamicQuery());

		if (patcherFixes.isEmpty()) {
			return;
		}

		for (PatcherFix patcherFix : patcherFixes) {
			User user = UserLocalServiceUtil.getUser(patcherFix.getUserId());

			EmailUtil.sendPatcherTimeoutEmail(
				alloyController, patcherFix, user.getEmailAddress());

			patcherFix.setNotified(true);

			alloyController.updateModelIgnoreRequest(patcherFix);
		}
	}

	@Transactional(
		isolation = Isolation.PORTAL, propagation = Propagation.REQUIRES_NEW,
		rollbackFor = Exception.class
	)
	public static void processOSBPatcherFixAddJenkinsStatus(
			AlloyController alloyController, long patcherFixId,
			String servletStatus)
		throws Exception {

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
			patcherFixId);

		validateOSBPatcherFixAddJenkinsStatus(patcherFix, servletStatus);

		JSONObject servletStatusJSONObject = JSONFactoryUtil.createJSONObject(
			servletStatus);

		if (servletStatusJSONObject.has("statusURL")) {
			updatePatcherFixJenkinsResult(
				alloyController, servletStatusJSONObject, patcherFix);

			return;
		}

		String outcome = servletStatusJSONObject.getString("outcome");

		OSBPatcherServletOutcome osbPatcherServletOutcome =
			JSONFactoryUtil.looseDeserializeSafe(
				outcome, OSBPatcherServletOutcome.class);

		List<String> messages = new ArrayList<>();

		if (patcherFix.getType() == PatcherFixConstants.TYPE_REBASE) {
			updatePatcherFixRebaseStatus(
				alloyController, patcherFix,
				osbPatcherServletOutcome.getStatus(),
				osbPatcherServletOutcome.getResult(), messages);
		}
		else {
			updatePatcherFixStatus(
				alloyController, patcherFix,
				osbPatcherServletOutcome.getStatus(),
				osbPatcherServletOutcome.getResult(), messages);
		}
	}

	public static List<PatcherFix> toPatcherFixes(List<Long> patcherFixIds)
		throws Exception {

		List<PatcherFix> patcherFixes = new ArrayList<>();

		for (long patcherFixId : patcherFixIds) {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				patcherFixId);

			patcherFixes.add(patcherFix);
		}

		return patcherFixes;
	}

	public static void updateObsolete(
			AlloyController alloyController, PatcherFix patcherFix,
			boolean obsolete)
		throws Exception {

		alloyController.updateModelIgnoreRequest(
			patcherFix, "obsolete", obsolete);

		List<PatcherFix> patcherFixDescendants =
			PatcherFixRelUtil.getPatcherFixDescendants(patcherFix);

		for (PatcherFix patcherFixDescendant : patcherFixDescendants) {
			if (obsolete) {
				alloyController.updateModelIgnoreRequest(
					patcherFixDescendant, "obsolete", true);

				continue;
			}

			alloyController.updateModelIgnoreRequest(
				patcherFixDescendant, "obsolete",
				PatcherFixRelUtil.hasObsoletePatcherFixAncestor(
					patcherFixDescendant));
		}
	}

	public static void updatePatcherFixJenkinsResult(
			AlloyController alloyController, JSONObject jenkinsStatusJSONObject,
			long patcherFixId)
		throws Exception {

		if (patcherFixId == 0) {
			return;
		}

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
			patcherFixId);

		updatePatcherFixJenkinsResult(
			alloyController, jenkinsStatusJSONObject, patcherFix);
	}

	public static void updatePatcherFixJenkinsResult(
			AlloyController alloyController, JSONObject jenkinsStatusJSONObject,
			PatcherFix patcherFix)
		throws Exception {

		if (patcherFix == null) {
			return;
		}

		String status = jenkinsStatusJSONObject.getString("status");
		String statusURL = jenkinsStatusJSONObject.getString("statusURL");

		JSONObject jenkinsResultJSONObject = JenkinsUtil.toJenkinsResult(
			status, statusURL);

		JenkinsUtil.putJenkinsResult(patcherFix, jenkinsResultJSONObject);

		alloyController.updateModelIgnoreRequest(patcherFix);
	}

	public static void validateDelete(PatcherFix patcherFix) throws Exception {
		if (!patcherFix.isLatestFix()) {
			throw new Exception(
				"the-fix-cannot-be-deleted-because-the-current-fix-is-not-" +
					"the-latest");
		}

		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherFixPatcherBuilds(
				patcherFix.getPatcherFixId());

		if (!patcherBuilds.isEmpty()) {
			throw new Exception(
				"the-fix-cannot-be-deleted-because-it-has-associated-builds");
		}

		AlloyServiceInvoker patcherFixRelAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFixRel.class.getName());

		List<PatcherFixRel> patcherFixRels =
			patcherFixRelAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {
					"parentPatcherFixId", patcherFix.getPatcherFixId()
				});

		if (!patcherFixRels.isEmpty()) {
			throw new Exception(
				"the-fix-cannot-be-deleted-because-another-fix-depends-on-it");
		}
	}

	protected static DynamicQuery buildInactivePatcherFixesDynamicQuery()
		throws Exception {

		AlloyServiceInvoker patcherFixAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFix.class.getName());

		DynamicQuery patcherFixesDynamicQuery =
			patcherFixAlloyServiceInvoker.buildDynamicQuery(
				new Object[] {
					"notified", false, "status",
					WorkflowConstants.STATUS_FIX_ADDING
				});

		Calendar calendar = new GregorianCalendar();

		calendar.add(Calendar.HOUR, -1);

		Property modifiedDateProperty = PropertyFactoryUtil.forName(
			"modifiedDate");

		patcherFixesDynamicQuery.add(
			modifiedDateProperty.lt(calendar.getTime()));

		Property typeProperty = PropertyFactoryUtil.forName("type");

		patcherFixesDynamicQuery.add(
			typeProperty.in(
				new int[] {
					PatcherFixConstants.TYPE_PATCH,
					PatcherFixConstants.TYPE_WORKAROUND
				}));

		return patcherFixesDynamicQuery;
	}

	protected static DynamicQuery buildPatcherFixKeyVersionDynamicQuery(
			PatcherFix patcherFix, boolean older)
		throws Exception {

		AlloyServiceInvoker patcherFixAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFix.class.getName());

		DynamicQuery patcherFixDynamicQuery =
			patcherFixAlloyServiceInvoker.buildDynamicQuery(
				new Object[] {"key", patcherFix.getKey()});

		Property keyVersionProperty = PropertyFactoryUtil.forName("keyVersion");

		if (older) {
			patcherFixDynamicQuery.add(
				keyVersionProperty.lt(patcherFix.getKeyVersion()));
		}
		else {
			patcherFixDynamicQuery.add(
				keyVersionProperty.gt(patcherFix.getKeyVersion()));
		}

		Property typeProperty = PropertyFactoryUtil.forName("type");

		patcherFixDynamicQuery.add(
			typeProperty.ne(PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC));

		return patcherFixDynamicQuery;
	}

	protected static DynamicQuery buildPreviousVersionsPatcherFixesQuery(
			PatcherFix patcherFix)
		throws Exception {

		AlloyServiceInvoker patcherFixAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFix.class.getName());

		DynamicQuery patcherFixDynamicQuery =
			patcherFixAlloyServiceInvoker.buildDynamicQuery(
				new Object[] {"key", patcherFix.getKey()});

		Property keyVersionProperty = PropertyFactoryUtil.forName("keyVersion");

		patcherFixDynamicQuery.add(
			keyVersionProperty.lt(patcherFix.getKeyVersion()));

		Property typeProperty = PropertyFactoryUtil.forName("type");

		patcherFixDynamicQuery.add(
			typeProperty.ne(PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC));

		return patcherFixDynamicQuery;
	}

	protected static boolean containsAllTickets(
		List<String> patcherBuildTickets, String[] tickets) {

		for (String ticket : tickets) {
			if (!patcherBuildTickets.contains(ticket)) {
				return false;
			}
		}

		return true;
	}

	protected static boolean containsAnyTickets(
		List<String> patcherBuildTickets, String[] tickets) {

		for (String ticket : tickets) {
			if (patcherBuildTickets.contains(ticket)) {
				return true;
			}
		}

		return false;
	}

	protected static List<PatcherFix> getFilteredPatcherFixesByAttributes(
			Object... attributes)
		throws Exception {

		if ((attributes.length == 0) || ((attributes.length % 2) != 0)) {
			throw new Exception("attributes-length-is-not-an-even-number");
		}

		AlloyServiceInvoker patcherFixAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFix.class.getName());

		DynamicQuery patcherFixDynamicQuery =
			patcherFixAlloyServiceInvoker.buildDynamicQuery(attributes);

		Property typeProperty = PropertyFactoryUtil.forName("type");

		patcherFixDynamicQuery.add(
			typeProperty.ne(PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC));

		return patcherFixAlloyServiceInvoker.executeDynamicQuery(
			patcherFixDynamicQuery);
	}

	protected static List<Long> getPatcherFixIds(PatcherBuild patcherBuild)
		throws Exception {

		List<Long> patcherFixIds = new ArrayList<>();

		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixs(
				patcherBuild.getPatcherBuildId());

		for (PatcherFix patcherFix : patcherFixes) {
			patcherFixIds.add(patcherFix.getPatcherFixId());
		}

		return patcherFixIds;
	}

	protected static void updatePatcherFixPatcherBuildRebaseStatus(
			AlloyController alloyController, PatcherFix patcherFix,
			int OSBPatcherServletOutcomeStatus, List<String> messages)
		throws Exception {

		ThemeDisplay themeDisplay = alloyController.getThemeDisplay();

		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherFixPatcherBuilds(
				patcherFix.getPatcherFixId());

		if (!patcherBuilds.isEmpty()) {
			PatcherUtil.addMessage(
				"The fix " + patcherFix.getPatcherFixId() + " is related to " +
					patcherBuilds.size() + " build(s).",
				messages);
		}

		for (PatcherBuild patcherBuild : patcherBuilds) {
			int status = PatcherBuildUtil.getNextPatcherBuildWorkflowStatus(
				patcherBuild, PatcherBuildUtil.isMergeOnly(patcherBuild));

			if (OSBPatcherServletOutcomeStatus ==
					OSBPatcherServletOutcome.STATUS_SUCCESS) {

				if (PatcherBuildUtil.containsIncompletePatcherFix(
						patcherBuild)) {

					continue;
				}

				PatcherBuildUtil.setStatus(
					alloyController, themeDisplay.getUser(), patcherBuild,
					status);

				alloyController.updateModelIgnoreRequest(patcherBuild);

				if (status == WorkflowConstants.STATUS_BUILD_COMPILING) {
					JenkinsUtil.sendDistJenkinsRequest(
						alloyController, themeDisplay.getUser(), patcherBuild);
				}
				else if ((status ==
							WorkflowConstants.STATUS_BUILD_MERGING_ONLY) ||
						 (status == WorkflowConstants.STATUS_BUILD_MERGING)) {

					JenkinsUtil.sendAgentJenkinsRequest(
						alloyController, themeDisplay.getUser(), patcherBuild);
				}

				continue;
			}
			else if (OSBPatcherServletOutcomeStatus ==
						OSBPatcherServletOutcome.STATUS_CONFLICT) {

				if (patcherBuild.getStatus() ==
						WorkflowConstants.STATUS_BUILD_FAILED) {

					continue;
				}

				PatcherBuildUtil.setStatus(
					alloyController, themeDisplay.getUser(), patcherBuild,
					status);
			}
			else {
				PatcherBuildUtil.setStatus(
					alloyController, themeDisplay.getUser(), patcherBuild,
					status);
			}

			alloyController.updateModelIgnoreRequest(patcherBuild);
		}
	}

	protected static void updatePatcherFixPatcherBuilds(
			AlloyController alloyController, PatcherFix patcherFix,
			List<String> messages)
		throws Exception {

		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherFixPatcherBuilds(
				patcherFix.getPatcherFixId());

		if (!patcherBuilds.isEmpty()) {
			PatcherUtil.addMessage(
				"The fix " + patcherFix.getPatcherFixId() + " is related to " +
					patcherBuilds.size() + " build(s).",
				messages);
		}

		for (PatcherBuild patcherBuild : patcherBuilds) {
			if (PatcherBuildUtil.containsIncompletePatcherFix(patcherBuild)) {
				continue;
			}

			List<Long> patcherFixIds = getPatcherFixIds(patcherBuild);

			patcherFixIds.remove(patcherBuild.getPatcherFixId());

			PatcherScanUtil.refinePatcherFixIds(patcherFixIds);

			PatcherFix mainPatcherFix =
				PatcherFixLocalServiceUtil.getPatcherFix(
					patcherBuild.getPatcherFixId());

			if (mainPatcherFix.getStatus() !=
					WorkflowConstants.STATUS_FIX_CONFLICT) {

				patcherFixIds.add(patcherBuild.getPatcherFixId());
			}

			ThemeDisplay themeDisplay = alloyController.getThemeDisplay();

			PatcherBuildUtil.updatePatcherBuildFixes(
				alloyController, themeDisplay.getUser(), patcherBuild,
				patcherFixIds);

			JenkinsUtil.sendAgentJenkinsRequest(
				alloyController, themeDisplay.getUser(), patcherBuild);

			PatcherUtil.addMessage(
				"The build " + patcherBuild.getPatcherBuildId() +
					" with name " + patcherBuild.getName() + " was resaved.",
				messages);
		}
	}

	protected static void updatePatcherFixRebaseStatus(
			AlloyController alloyController, PatcherFix patcherFix,
			int OSBPatcherServletOutcomeStatus,
			String OSBPatcherServletOutcomeResult, List<String> messages)
		throws Exception {

		if (OSBPatcherServletOutcomeStatus ==
				OSBPatcherServletOutcome.STATUS_SUCCESS) {

			patcherFix.setGitHash(OSBPatcherServletOutcomeResult);

			patcherFix.setStatus(WorkflowConstants.STATUS_FIX_COMPLETE);
		}
		else if (OSBPatcherServletOutcomeStatus ==
					OSBPatcherServletOutcome.STATUS_CONFLICT) {

			patcherFix.setStatus(WorkflowConstants.STATUS_FIX_REBASE_CONFLICT);
		}
		else {
			patcherFix.setStatus(WorkflowConstants.STATUS_FIX_FAILED);
		}

		alloyController.updateModelIgnoreRequest(patcherFix);

		PatcherUtil.pollIndexState(
			alloyController, PatcherFix.class.getName(),
			patcherFix.getPatcherFixId(), "status",
			WorkflowConstants.STATUS_FIX_COMPLETE);

		PatcherUtil.addMessage(
			"The fix " + patcherFix.getPatcherFixId() + " with name " +
				patcherFix.getName() + " was successfully added.",
			messages);

		updatePatcherFixPatcherBuildRebaseStatus(
			alloyController, patcherFix, OSBPatcherServletOutcomeStatus,
			messages);
	}

	protected static void updatePatcherFixStatus(
			AlloyController alloyController, PatcherFix patcherFix,
			int OSBPatcherServletOutcomeStatus,
			String OSBPatcherServletOutcomeResult, List<String> messages)
		throws Exception {

		if (OSBPatcherServletOutcomeStatus ==
				OSBPatcherServletOutcome.STATUS_SUCCESS) {

			patcherFix.setGitHash(OSBPatcherServletOutcomeResult);

			patcherFix.setStatus(WorkflowConstants.STATUS_FIX_COMPLETE);

			alloyController.updateModelIgnoreRequest(patcherFix);

			PatcherUtil.pollIndexState(
				alloyController, PatcherFix.class.getName(),
				patcherFix.getPatcherFixId(), "status",
				WorkflowConstants.STATUS_FIX_COMPLETE);

			PatcherUtil.addMessage(
				"The fix " + patcherFix.getPatcherFixId() + " with name " +
					patcherFix.getName() + " was successfully added.",
				messages);

			updatePatcherFixPatcherBuilds(
				alloyController, patcherFix, messages);
		}
		else {
			patcherFix.setStatus(WorkflowConstants.STATUS_FIX_FAILED);

			alloyController.updateModelIgnoreRequest(patcherFix);

			PatcherUtil.addMessage(
				"The fix " + patcherFix.getPatcherFixId() + " with name " +
					patcherFix.getName() + " failed to add.",
				messages);
		}
	}

	protected static void validateOSBPatcherFixAddJenkinsStatus(
			PatcherFix patcherFix, String jenkinsStatusJSONString)
		throws Exception {

		JenkinsUtil.validateJenkinsRequestKey(
			patcherFix, jenkinsStatusJSONString);
	}

	private static final Log _log = LogFactoryUtil.getLog(PatcherFixUtil.class);

}