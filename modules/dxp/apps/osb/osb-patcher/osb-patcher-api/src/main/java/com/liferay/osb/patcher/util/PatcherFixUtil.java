/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.configuration.PatcherConfiguration;
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
import com.liferay.osb.patcher.service.PatcherFixRelLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.osb.patcher.util.comparator.PatcherFixCreateDateComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.ListUtil;
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

	public static PatcherFix addPatcherFix(
			User user, List<Long> parentPatcherFixIds,
			long patcherProjectVersionId, String name, int type, int status)
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

			existingPatcherFix = PatcherFixLocalServiceUtil.updateLatestFix(
				existingPatcherFix.getPatcherFixId(), false);

			keyVersion = BigDecimalUtil.add(
				existingPatcherFix.getKeyVersion(), 0.1);
		}

		return PatcherFixLocalServiceUtil.addPatcherFix(
			user.getUserId(), patcherProjectVersionId, keyVersion, name, type,
			status, parentPatcherFixIds);
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

	public static boolean containsPatcherFixComment(long patcherBuildId) {
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

	public static boolean containsPatcherFixWorkaround(long patcherBuildId) {
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

	public static PatcherFix fetchLongestTicketPatcherFix(
		List<PatcherFix> patcherFixes) {

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

	public static PatcherFix fetchPatcherFixByLatestFix(String key) {
		List<PatcherFix> patcherFixes = getFilteredPatcherFixes(key, true);

		if (!patcherFixes.isEmpty()) {
			return patcherFixes.get(0);
		}

		return null;
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
			StringUtil.merge(PatcherUtil.sortTokens(name), StringPool.COMMA));
	}

	public static String generateKey(
			long patcherProjectVersionId, String key, String name)
		throws Exception {

		return PatcherUtil.generatePatcherKey(
			PatcherFix.class.getName(), patcherProjectVersionId, key,
			StringUtil.merge(PatcherUtil.sortTokens(name), StringPool.COMMA));
	}

	public static Map<String, Set<String>> getComponentDependencies(
		String dependencies) {

		Map<String, Set<String>> componentDependencies = new HashMap<>();

		List<String> phrases = StringUtil.split(dependencies);

		for (String phrase : phrases) {
			String[] componentNames =
				com.liferay.portal.kernel.util.StringUtil.split(phrase, "->");

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
		long patcherProjectVersionId, boolean includeAnyStatusRebaseFixes) {

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

		StringBundler sb = new StringBundler(8);

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, patcherFix.getCompanyId());

		sb.append(patcherConfiguration.githubURL());

		sb.append(StringPool.SLASH);

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherFix.getPatcherProjectVersionId());

		sb.append(patcherProjectVersion.getRepositoryName());

		sb.append("/compare/");
		sb.append(patcherProjectVersion.getCommittish());
		sb.append(StringPool.TRIPLE_PERIOD);
		sb.append(patcherConfiguration.patcherGitTagPrefix());
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
		List<PatcherFix> patcherFixesSelection) {

		PatcherFixRadix patcherFixRadix = new PatcherFixRadix();

		for (PatcherFix patcherFix : patcherFixesSelection) {
			List<String> tickets = StringUtil.split(patcherFix.getName());

			if (!containsAllTickets(patcherBuildTickets, tickets)) {
				continue;
			}

			patcherFixRadix.addPatcherFix(tickets.size(), patcherFix);
		}

		return patcherFixRadix;
	}

	public static PatcherFixRadix getPatcherFixRadix(
		long patcherProjectVersionId, List<String> patcherBuildTickets,
		List<PatcherFix> patcherFixesSelection, String[] ticketsFilter) {

		PatcherFixRadix patcherFixRadix = new PatcherFixRadix();

		for (PatcherFix patcherFix : patcherFixesSelection) {
			List<String> tickets1 = StringUtil.split(patcherFix.getName());

			if (!containsAllTickets(patcherBuildTickets, tickets1)) {
				continue;
			}

			List<String> tickets2 = PatcherUtil.sortTokens(
				patcherFix.getName());

			if (!containsAnyTickets(tickets2, ticketsFilter)) {
				continue;
			}

			patcherFixRadix.addPatcherFix(tickets1.size(), patcherFix);
		}

		return patcherFixRadix;
	}

	public static List<PatcherFix> getPreviousFixPackBuildFixes(
			PatcherFixPack patcherFixPack)
		throws Exception {

		PatcherFixPack patcherFixPackVersion =
			PatcherFixPackUtil.fetchPatcherFixPackVersion(patcherFixPack, true);

		if (patcherFixPackVersion != null) {
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
		PatcherFix patcherFix, List<PatcherFix> patcherFixPackPatcherFixes) {

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
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

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

	public static boolean isMainPatcherFix(long patcherFixId) {
		return PatcherBuildLocalServiceUtil.hasPatcherFixes(patcherFixId);
	}

	public static void notifyUsersInactivePatcherFixes() throws Exception {
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
			EmailUtil.sendPatcherTimeoutEmail(
				patcherFix,
				UserLocalServiceUtil.getUser(patcherFix.getUserId()));

			PatcherFixLocalServiceUtil.updateNotified(
				patcherFix.getPatcherFixId(), true);
		}
	}

	@Transactional(
		isolation = Isolation.PORTAL, propagation = Propagation.REQUIRES_NEW,
		rollbackFor = Exception.class
	)
	public static void processOSBPatcherFixAddJenkinsStatus(
			long patcherFixId, String servletStatus, User user)
		throws Exception {

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
			patcherFixId);

		validateOSBPatcherFixAddJenkinsStatus(patcherFix, servletStatus);

		JSONObject servletStatusJSONObject = JSONFactoryUtil.createJSONObject(
			servletStatus);

		if (servletStatusJSONObject.has("statusURL")) {
			updatePatcherFixJenkinsResult(
				servletStatusJSONObject, patcherFixId);

			return;
		}

		String outcome = servletStatusJSONObject.getString("outcome");

		OSBPatcherServletOutcome osbPatcherServletOutcome =
			JSONFactoryUtil.looseDeserialize(
				outcome, OSBPatcherServletOutcome.class);

		List<String> messages = new ArrayList<>();

		if (patcherFix.getType() == PatcherFixConstants.TYPE_REBASE) {
			updatePatcherFixRebaseStatus(
				patcherFixId, osbPatcherServletOutcome.getStatus(),
				osbPatcherServletOutcome.getResult(), messages, user);
		}
		else {
			updatePatcherFixStatus(
				patcherFixId, osbPatcherServletOutcome.getStatus(),
				osbPatcherServletOutcome.getResult(), messages, user);
		}
	}

	public static List<PatcherFix> toPatcherFixes(List<Long> patcherFixIds) {
		return TransformUtil.transform(
			patcherFixIds, PatcherFixLocalServiceUtil::getPatcherFix);
	}

	public static PatcherFix updateObsolete(long patcherFixId, boolean obsolete)
		throws Exception {

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.updateObsolete(
			patcherFixId, obsolete);

		List<PatcherFix> patcherFixDescendants =
			PatcherFixRelUtil.getPatcherFixDescendants(patcherFix);

		for (PatcherFix patcherFixDescendant : patcherFixDescendants) {
			if (obsolete) {
				PatcherFixLocalServiceUtil.updateObsolete(
					patcherFixDescendant.getPatcherFixId(), true);

				continue;
			}

			PatcherFixLocalServiceUtil.updateObsolete(
				patcherFixDescendant.getPatcherFixId(),
				PatcherFixRelUtil.hasObsoletePatcherFixAncestor(
					patcherFixDescendant));
		}

		return patcherFix;
	}

	public static void updatePatcherFixJenkinsResult(
			JSONObject jenkinsStatusJSONObject, long patcherFixId)
		throws Exception {

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
			patcherFixId);

		if (patcherFix == null) {
			return;
		}

		PatcherFixLocalServiceUtil.updateJenkinsResult(
			patcherFixId,
			JenkinsUtil.getJenkinsResult(
				JenkinsUtil.toJenkinsResult(
					jenkinsStatusJSONObject.getString("status"),
					jenkinsStatusJSONObject.getString("statusURL")),
				patcherFix.getJenkinsResults()));
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
		List<String> patcherBuildTickets, List<String> tickets) {

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

	protected static List<Long> getPatcherFixIds(PatcherBuild patcherBuild) {
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
			long patcherFixId, int osbPatcherServletOutcomeStatus,
			List<String> messages, User user)
		throws Exception {

		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherFixPatcherBuilds(
				patcherFixId);

		if (!patcherBuilds.isEmpty()) {
			PatcherUtil.addMessage(
				StringBundler.concat(
					"The fix ", patcherFixId, " is related to ",
					patcherBuilds.size(), " build(s)."),
				messages);
		}

		for (PatcherBuild patcherBuild : patcherBuilds) {
			int status = PatcherBuildUtil.getNextPatcherBuildWorkflowStatus(
				patcherBuild, PatcherBuildUtil.isMergeOnly(patcherBuild));

			if (osbPatcherServletOutcomeStatus ==
					OSBPatcherServletOutcome.STATUS_SUCCESS) {

				if (PatcherBuildUtil.containsIncompletePatcherFix(
						patcherBuild)) {

					continue;
				}

				patcherBuild = PatcherBuildLocalServiceUtil.updateStatus(
					user.getUserId(), patcherBuild.getPatcherBuildId(), status);

				PatcherBuildUtil.workflowParentPatcherBuild(user, patcherBuild);

				if (status == WorkflowConstants.STATUS_BUILD_COMPILING) {
					JenkinsUtil.sendDistJenkinsRequest(user, patcherBuild);
				}
				else if ((status ==
							WorkflowConstants.STATUS_BUILD_MERGING_ONLY) ||
						 (status == WorkflowConstants.STATUS_BUILD_MERGING)) {

					JenkinsUtil.sendAgentJenkinsRequest(user, patcherBuild);
				}
			}
			else if (osbPatcherServletOutcomeStatus ==
						OSBPatcherServletOutcome.STATUS_CONFLICT) {

				if (patcherBuild.getStatus() ==
						WorkflowConstants.STATUS_BUILD_FAILED) {

					continue;
				}

				patcherBuild = PatcherBuildLocalServiceUtil.updateStatus(
					user.getUserId(), patcherBuild.getPatcherBuildId(), status);

				PatcherBuildUtil.workflowParentPatcherBuild(user, patcherBuild);
			}
			else {
				patcherBuild = PatcherBuildLocalServiceUtil.updateStatus(
					user.getUserId(), patcherBuild.getPatcherBuildId(), status);

				PatcherBuildUtil.workflowParentPatcherBuild(user, patcherBuild);
			}
		}
	}

	protected static void updatePatcherFixPatcherBuilds(
			long patcherFixId, List<String> messages, User user)
		throws Exception {

		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherFixPatcherBuilds(
				patcherFixId);

		if (!patcherBuilds.isEmpty()) {
			PatcherUtil.addMessage(
				StringBundler.concat(
					"The fix ", patcherFixId, " is related to ",
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

			PatcherBuildUtil.updatePatcherBuildFixes(
				user, patcherBuild, patcherFixIds);

			JenkinsUtil.sendAgentJenkinsRequest(user, patcherBuild);

			PatcherUtil.addMessage(
				StringBundler.concat(
					"The build ", patcherBuild.getPatcherBuildId(),
					" with name ", patcherBuild.getName(), " was resaved."),
				messages);
		}
	}

	protected static void updatePatcherFixRebaseStatus(
			long patcherFixId, int osbPatcherServletOutcomeStatus,
			String osbPatcherServletOutcomeResult, List<String> messages,
			User user)
		throws Exception {

		PatcherFix patcherFix = null;

		if (osbPatcherServletOutcomeStatus ==
				OSBPatcherServletOutcome.STATUS_SUCCESS) {

			patcherFix = PatcherFixLocalServiceUtil.updatePatcherFix(
				user.getUserId(), patcherFixId, osbPatcherServletOutcomeResult,
				WorkflowConstants.STATUS_FIX_COMPLETE);
		}
		else if (osbPatcherServletOutcomeStatus ==
					OSBPatcherServletOutcome.STATUS_CONFLICT) {

			patcherFix = PatcherFixLocalServiceUtil.updateStatus(
				user.getUserId(), patcherFixId,
				WorkflowConstants.STATUS_FIX_REBASE_CONFLICT);
		}
		else {
			patcherFix = PatcherFixLocalServiceUtil.updateStatus(
				user.getUserId(), patcherFixId,
				WorkflowConstants.STATUS_FIX_FAILED);
		}

		PatcherUtil.addMessage(
			StringBundler.concat(
				"The fix ", patcherFix.getPatcherFixId(), " with name ",
				patcherFix.getName(), " was successfully added."),
			messages);

		updatePatcherFixPatcherBuildRebaseStatus(
			patcherFixId, osbPatcherServletOutcomeStatus, messages, user);
	}

	protected static void updatePatcherFixStatus(
			long patcherFixId, int osbPatcherServletOutcomeStatus,
			String osbPatcherServletOutcomeResult, List<String> messages,
			User user)
		throws Exception {

		if (osbPatcherServletOutcomeStatus ==
				OSBPatcherServletOutcome.STATUS_SUCCESS) {

			PatcherFix patcherFix = PatcherFixLocalServiceUtil.updatePatcherFix(
				user.getUserId(), patcherFixId, osbPatcherServletOutcomeResult,
				WorkflowConstants.STATUS_FIX_COMPLETE);

			PatcherUtil.addMessage(
				StringBundler.concat(
					"The fix ", patcherFix.getPatcherFixId(), " with name ",
					patcherFix.getName(), " was successfully added."),
				messages);

			updatePatcherFixPatcherBuilds(patcherFixId, messages, user);
		}
		else {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.updateStatus(
				user.getUserId(), patcherFixId,
				WorkflowConstants.STATUS_FIX_FAILED);

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

		if (patcherFix == null) {
			throw new Exception("the-base-model-is-null");
		}

		JenkinsUtil.validateJenkinsRequestKey(
			patcherFix, jenkinsStatusJSONString, patcherFix.getRequestKey());
	}

	private static final Log _log = LogFactoryUtil.getLog(PatcherFixUtil.class);

}