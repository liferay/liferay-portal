/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.AlloyController;
import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixModel;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherFixRel;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.osb.patcher.util.comparator.PatcherFixCreateDateComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
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
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Calendar;
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
		newPatcherFix.setKey(generateKey(patcherProjectVersionId, name));
		newPatcherFix.setKeyVersion(keyVersion);
		newPatcherFix.setType(type);
		newPatcherFix.setLatestFix(true);
		newPatcherFix.setStatus(status);

		alloyController.updateModelIgnoreRequest(newPatcherFix);

		PatcherFixRelUtil.addPatcherFixRel(
			newPatcherFix.getPatcherFixId(), parentPatcherFixIds);

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
						existingPatcherFix.getPatcherFixId(),
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
			PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixes(
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
			PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixes(
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

		PatcherFixRelLocalServiceUtil.deletePatcherFixRelsByChildPatcherFixId(
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
		PatcherFix patcherFix, boolean older) {

		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherFixes(
				patcherFix.getKey(), patcherFix.getKeyVersion(),
				PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC, older);

		if (patcherFixes.isEmpty()) {
			return null;
		}

		return patcherFixes.get(0);
	}

	public static PatcherFix fetchSiblingChildPatcherBuildMainFix(
			PatcherFix mainPatcherFix)
		throws Exception {

		List<PatcherBuild> childPatcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherBuilds(
				mainPatcherFix.getPatcherFixId(), true);

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
		long patcherProjectVersionId, int status) {

		return PatcherFixLocalServiceUtil.getPatcherFixes(
			patcherProjectVersionId, true,
			PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC, status);
	}

	public static List<PatcherFix> getFilteredPatcherFixes(
		long patcherProjectVersionId, String name, boolean latestFix) {

		return PatcherFixLocalServiceUtil.getPatcherFixes(
			patcherProjectVersionId, latestFix, name,
			PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC);
	}

	public static List<PatcherFix> getFilteredPatcherFixes(
		String key, boolean latestFix) {

		return PatcherFixLocalServiceUtil.getPatcherFixes(
			key, latestFix, PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC);
	}

	public static Date getOldestPatcherFixCreateDate(long patcherFixPackId) {
		List<PatcherFix> patcherFixPackPatcherFixs =
			PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixes(
				patcherFixPackId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				PatcherFixCreateDateComparator.getInstance(true));

		if (patcherFixPackPatcherFixs.isEmpty()) {
			return null;
		}

		PatcherFix patcherFix = patcherFixPackPatcherFixs.get(0);

		return patcherFix.getCreateDate();
	}

	public static List<PatcherFix> getParentPatcherFixes(
		PatcherFix patcherFix) {

		return TransformUtil.transform(
			PatcherFixRelLocalServiceUtil.getPatcherFixRelsByChildPatcherFixId(
				patcherFix.getPatcherFixId()),
			patcherFixRel -> PatcherFixLocalServiceUtil.fetchPatcherFix(
				patcherFixRel.getParentPatcherFixId()));
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

		List<PatcherFix> patcherFixes = getFilteredPatcherFixes(
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
			PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixes(
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
				PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixes(
					patcherFixPackVersion.getPatcherBuildId()));
		}

		return new ArrayList<>();
	}

	public static List<Long> getPreviousVersionsPatcherFixIds(
		PatcherFix patcherFix) {

		return TransformUtil.transform(
			PatcherFixLocalServiceUtil.getPatcherFixes(
				patcherFix.getKey(), patcherFix.getKeyVersion(),
				PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC, true),
			PatcherFixModel::getPatcherFixId);
	}

	public static List<PatcherFix> getRebasePatcherFixes(
		long patcherProjectVersionId) {

		return PatcherFixLocalServiceUtil.getPatcherFixes(
			patcherProjectVersionId, true, PatcherFixConstants.TYPE_REBASE);
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
		catch (Exception exception) {
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
		return PatcherBuildLocalServiceUtil.hasPatcherFixes(patcherFixId);
	}

	public static void notifyUsersInactivePatcherFixes(
			AlloyController alloyController)
		throws Exception {

		Calendar calendar = new GregorianCalendar();

		calendar.add(Calendar.HOUR, -1);

		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherFixes(
				calendar.getTime(), false,
				new int[] {
					PatcherFixConstants.TYPE_PATCH,
					PatcherFixConstants.TYPE_WORKAROUND
				},
				WorkflowConstants.STATUS_FIX_ADDING);

		if (patcherFixes.isEmpty()) {
			return;
		}

		for (PatcherFix patcherFix : patcherFixes) {
			User user = UserLocalServiceUtil.getUser(patcherFix.getUserId());

			EmailUtil.sendPatcherTimeoutEmail(
				patcherFix, user.getEmailAddress(),
				alloyController.getThemeDisplay());

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
			JSONFactoryUtil.looseDeserialize(
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

		List<PatcherFixRel> patcherFixRels =
			PatcherFixRelLocalServiceUtil.getPatcherFixRelsByParentPatcherFixId(
				patcherFix.getPatcherFixId());

		if (!patcherFixRels.isEmpty()) {
			throw new Exception(
				"the-fix-cannot-be-deleted-because-another-fix-depends-on-it");
		}
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

	protected static List<Long> getPatcherFixIds(PatcherBuild patcherBuild)
		throws Exception {

		List<Long> patcherFixIds = new ArrayList<>();

		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixes(
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
				StringBundler.concat(
					"The fix ", patcherFix.getPatcherFixId(), " is related to ",
					patcherBuilds.size(), " build(s)."),
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
						themeDisplay.getUser(), patcherBuild, themeDisplay);
				}
				else if ((status ==
							WorkflowConstants.STATUS_BUILD_MERGING_ONLY) ||
						 (status == WorkflowConstants.STATUS_BUILD_MERGING)) {

					JenkinsUtil.sendAgentJenkinsRequest(
						themeDisplay.getUser(), patcherBuild, themeDisplay);
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
				StringBundler.concat(
					"The fix ", patcherFix.getPatcherFixId(), " is related to ",
					patcherBuilds.size(), " build(s)."),
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
				themeDisplay.getUser(), patcherBuild, themeDisplay);

			PatcherUtil.addMessage(
				StringBundler.concat(
					"The build ", patcherBuild.getPatcherBuildId(),
					" with name ", patcherBuild.getName(), " was resaved."),
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
			StringBundler.concat(
				"The fix ", patcherFix.getPatcherFixId(), " with name ",
				patcherFix.getName(), " was successfully added."),
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
				StringBundler.concat(
					"The fix ", patcherFix.getPatcherFixId(), " with name ",
					patcherFix.getName(), " was successfully added."),
				messages);

			updatePatcherFixPatcherBuilds(
				alloyController, patcherFix, messages);
		}
		else {
			patcherFix.setStatus(WorkflowConstants.STATUS_FIX_FAILED);

			alloyController.updateModelIgnoreRequest(patcherFix);

			PatcherUtil.addMessage(
				StringBundler.concat(
					"The fix ", patcherFix.getPatcherFixId(), " with name ",
					patcherFix.getName(), " failed to add."),
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