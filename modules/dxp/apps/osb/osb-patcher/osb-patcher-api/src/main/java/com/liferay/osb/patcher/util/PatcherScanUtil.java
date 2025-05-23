/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author Zsolt Balogh
 */
public class PatcherScanUtil {

	public static void refinePatcherFixIds(List<Long> patcherFixIds)
		throws Exception {

		removeOverlappingTicketFixIds(patcherFixIds);

		List<Long> originalPatcherFixIds = ListUtil.copy(patcherFixIds);

		replaceParentsWithChildPatcherFixIds(patcherFixIds);

		if (patcherFixIds.equals(originalPatcherFixIds)) {
			return;
		}

		refinePatcherFixIds(patcherFixIds);
	}

	public static Map<Long, List<Long>> scanPatcherFixIdsBy7xProjectVersions(
			PatcherBuild patcherBuild)
		throws Exception {

		List<Long> patcherProjectVersionIds = new ArrayList<>();

		patcherProjectVersionIds.add(patcherBuild.getPatcherProjectVersionId());

		if (!PatcherProjectVersionUtil.isCombinedBranchPatcherProjectVersion(
				patcherBuild.getPatcherProjectVersionId())) {

			PatcherProjectVersion siblingPatcherProjectVersion =
				PatcherProjectVersionUtil.getSiblingPatcherProjectVersion(
					patcherBuild.getPatcherProjectVersionId());

			patcherProjectVersionIds.add(
				siblingPatcherProjectVersion.getPatcherProjectVersionId());
		}

		List<String> patcherBuildTickets = PatcherUtil.sortTokens(
			PatcherUtil.getTickets(patcherBuild.getName()));

		Map<Long, List<Long>> patcherProjectVersionIdPatcherFixIdsMap =
			scanPatcherFixIdsByProjectVersionIds(
				patcherProjectVersionIds, patcherBuildTickets, true);

		if (!patcherBuildTickets.isEmpty()) {
			PatcherProjectVersion patcherProjectVersion =
				PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
					patcherBuild.getPatcherProjectVersionId());

			Map<Long, List<Long>> otherPatcherProjectVersionIdPatcherFixIdsMap =
				scanPatcherFixIdsByOtherProjectVersions(
					patcherProjectVersion, patcherBuildTickets);

			patcherProjectVersionIdPatcherFixIdsMap.putAll(
				otherPatcherProjectVersionIdPatcherFixIdsMap);
		}

		if (!patcherBuildTickets.isEmpty()) {
			StringBundler sb = new StringBundler(7);

			sb.append(
				LanguageUtil.get(
					LocaleUtil.getMostRelevantLocale(),
					"failed-building-a-patch-for-tickets"));
			sb.append("<br />");
			sb.append(
				StringUtil.replace(
					patcherBuild.getName(), StringPool.COMMA,
					StringPool.COMMA_AND_SPACE));
			sb.append("<br /><br />");
			sb.append(
				LanguageUtil.get(
					LocaleUtil.getMostRelevantLocale(),
					"there-was-no-match-found-in-our-fix-catalog-for-the-" +
						"following-tokens"));
			sb.append("<br />");
			sb.append(
				StringUtil.merge(
					patcherBuildTickets, StringPool.COMMA_AND_SPACE));

			_log.error(sb.toString());

			throw new Exception(sb.toString());
		}

		return patcherProjectVersionIdPatcherFixIdsMap;
	}

	public static List<Long> scanPatcherFixIdsByProjectVersionId(
			String patcherBuildName, long patcherProjectVersionId,
			List<PatcherFix> patcherFixesSelection)
		throws Exception {

		List<Long> patcherFixIds = new ArrayList<>();

		List<String> tickets = PatcherUtil.getTickets(patcherBuildName);

		List<Long> patcherFixPackFixIds = getPatcherFixIdsByFixPackName(
			patcherBuildName, patcherProjectVersionId, tickets);

		if (!tickets.isEmpty()) {
			patcherFixIds = scanPatcherFixIdsByTickets(
				patcherBuildName, patcherFixPackFixIds, patcherProjectVersionId,
				StringUtil.merge(tickets), patcherFixesSelection);
		}
		else {
			patcherFixIds = patcherFixPackFixIds;
		}

		refinePatcherFixIds(patcherFixIds);

		return patcherFixIds;
	}

	public static Map<Long, List<Long>> scanPatcherFixIdsByProjectVersionIds(
			List<Long> patcherProjectVersionIds,
			List<String> patcherBuildTickets,
			boolean includeAnyStatusRebaseFixes)
		throws Exception {

		Map<Long, List<Long>> patcherProjectVersionIdPatcherFixIdsMap =
			new HashMap<>();

		for (long patcherProjectVersionId : patcherProjectVersionIds) {
			List<PatcherFix> patcherFixesSelection =
				PatcherFixUtil.getPatcherFixesSelection(
					patcherProjectVersionId, includeAnyStatusRebaseFixes);

			List<String> foundTickets = scanPatcherFixTicketsByProjectVersionId(
				patcherBuildTickets, patcherProjectVersionId,
				patcherFixesSelection);

			if (foundTickets.isEmpty()) {
				continue;
			}

			List<Long> patcherFixIds = scanPatcherFixIdsByProjectVersionId(
				StringUtil.merge(foundTickets), patcherProjectVersionId,
				patcherFixesSelection);

			patcherProjectVersionIdPatcherFixIdsMap.put(
				patcherProjectVersionId, patcherFixIds);

			patcherBuildTickets.removeAll(foundTickets);
		}

		return patcherProjectVersionIdPatcherFixIdsMap;
	}

	protected static List<Long> getPatcherFixIdsByFixPackName(
			String patcherBuildName, long patcherProjectVersionId,
			List<String> tickets)
		throws Exception {

		List<Long> patcherFixIds = new ArrayList<>();

		List<String> patcherFixPackNames =
			PatcherFixPackUtil.getPatcherFixPackNames(patcherBuildName);

		List<PatcherFixPack> patcherFixPacks =
			PatcherFixPackUtil.getPatcherFixPacks(
				patcherFixPackNames, patcherProjectVersionId);

		List<Long> patcherFixPackPatcherFixIds =
			PatcherFixUtil.getPatcherFixIds(patcherFixPacks);

		for (long patcherFixPackFixId : patcherFixPackPatcherFixIds) {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				patcherFixPackFixId);

			if (tickets.containsAll(
					PatcherUtil.getTickets(patcherFix.getName()))) {

				continue;
			}

			patcherFixIds.add(patcherFixPackFixId);
		}

		return patcherFixIds;
	}

	protected static void removeOverlappingTicketFixIds(
			List<Long> patcherFixIds)
		throws Exception {

		List<Long> curPatcherFixIds = ListUtil.copy(patcherFixIds);

		for (long curPatcherFixId1 : curPatcherFixIds) {
			PatcherFix curPatcherFix1 =
				PatcherFixLocalServiceUtil.getPatcherFix(curPatcherFixId1);

			List<String> curPatcherFix1Tickets = PatcherUtil.getTickets(
				curPatcherFix1.getName());

			for (long curPatcherFixId2 : curPatcherFixIds) {
				if (curPatcherFixId1 == curPatcherFixId2) {
					continue;
				}

				PatcherFix curPatcherFix2 =
					PatcherFixLocalServiceUtil.getPatcherFix(curPatcherFixId2);

				if (curPatcherFix1Tickets.containsAll(
						PatcherUtil.getTickets(curPatcherFix2.getName()))) {

					patcherFixIds.remove(curPatcherFixId2);
				}
			}
		}
	}

	protected static void replaceParentsWithChildPatcherFixIds(
			List<Long> patcherFixIds)
		throws Exception {

		List<Long> curPatcherFixIds = ListUtil.copy(patcherFixIds);

		for (long curPatcherFixId : curPatcherFixIds) {
			if (!patcherFixIds.contains(curPatcherFixId)) {
				continue;
			}

			List<Long> childPatcherFixIds =
				PatcherFixRelUtil.getChildPatcherFixIds(curPatcherFixId);

			if (PatcherFixUtil.containsNewerVersionPatcherFixIds(
					patcherFixIds, curPatcherFixId) ||
				!Collections.disjoint(patcherFixIds, childPatcherFixIds)) {

				patcherFixIds.remove(curPatcherFixId);

				continue;
			}

			patcherFixIds.addAll(
				PatcherFixRelUtil.getChildPatcherFixIds(
					patcherFixIds, childPatcherFixIds));
			patcherFixIds.removeAll(
				PatcherFixRelUtil.getParentPatcherFixIds(
					patcherFixIds, childPatcherFixIds));
		}
	}

	protected static Map<Long, List<Long>>
			scanPatcherFixIdsByOtherProjectVersions(
				PatcherProjectVersion patcherProjectVersion,
				List<String> patcherBuildTickets)
		throws Exception {

		List<Long> patcherProjectVersionIds = new ArrayList<>();

		if (PatcherProductVersionUtil.isMarketplaceAppProduct(
				patcherProjectVersion.getPatcherProductVersionId())) {

			return scanPatcherFixIdsByProjectVersionIds(
				patcherProjectVersionIds, patcherBuildTickets, false);
		}

		PatcherProjectVersion newerPatcherProjectVersion =
			patcherProjectVersion;
		PatcherProjectVersion olderPatcherProjectVersion =
			patcherProjectVersion;

		do {
			if (olderPatcherProjectVersion != null) {
				olderPatcherProjectVersion =
					PatcherProjectVersionUtil.getNeighborPatcherProjectVersion(
						olderPatcherProjectVersion.getCommittish(), -1);

				if ((olderPatcherProjectVersion != null) &&
					PatcherProjectVersionUtil.isMatchingPatcherProjectVersions(
						patcherProjectVersion, olderPatcherProjectVersion)) {

					patcherProjectVersionIds.add(
						olderPatcherProjectVersion.
							getPatcherProjectVersionId());

					if (!olderPatcherProjectVersion.isCombinedBranch()) {
						PatcherProjectVersion siblingPatcherProjectVersion =
							PatcherProjectVersionUtil.
								getSiblingPatcherProjectVersion(
									olderPatcherProjectVersion.
										getPatcherProjectVersionId());

						patcherProjectVersionIds.add(
							siblingPatcherProjectVersion.
								getPatcherProjectVersionId());
					}
				}
			}

			if (newerPatcherProjectVersion != null) {
				newerPatcherProjectVersion =
					PatcherProjectVersionUtil.getNeighborPatcherProjectVersion(
						newerPatcherProjectVersion.getCommittish(), 1);

				if ((newerPatcherProjectVersion != null) &&
					PatcherProjectVersionUtil.isMatchingPatcherProjectVersions(
						patcherProjectVersion, newerPatcherProjectVersion)) {

					patcherProjectVersionIds.add(
						newerPatcherProjectVersion.
							getPatcherProjectVersionId());

					if (!newerPatcherProjectVersion.isCombinedBranch()) {
						PatcherProjectVersion siblingPatcherProjectVersion =
							PatcherProjectVersionUtil.
								getSiblingPatcherProjectVersion(
									newerPatcherProjectVersion.
										getPatcherProjectVersionId());

						patcherProjectVersionIds.add(
							siblingPatcherProjectVersion.
								getPatcherProjectVersionId());
					}
				}
			}

			if (patcherProjectVersionIds.size() > 100) {
				break;
			}
		}
		while ((olderPatcherProjectVersion != null) ||
			   (newerPatcherProjectVersion != null));

		PatcherProductVersion patcherProductVersion =
			PatcherProductVersionLocalServiceUtil.getPatcherProductVersion(
				patcherProjectVersion.getPatcherProductVersionId());

		String patcherProductVersionName = patcherProductVersion.getName();

		if (patcherProductVersionName.equals("DXP 7.4") ||
			patcherProductVersionName.equals("Quarterly Releases")) {

			final int fixedIssuesLength = patcherProjectVersion.getFixedIssues(
			).length();

			PatcherProductVersion quarterlyProduct =
				PatcherProductVersionUtil.fetchPatcherProductVersion(
					"Quarterly Releases");
			PatcherProductVersion updateProduct =
				PatcherProductVersionUtil.fetchPatcherProductVersion("DXP 7.4");

			List<PatcherProjectVersion> quarterlyPatcherProjectVersions =
				PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions(
					quarterlyProduct.getPatcherProductVersionId());
			List<PatcherProjectVersion> updatePatcherProjectVersions =
				PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions(
					updateProduct.getPatcherProductVersionId());

			Comparator<PatcherProjectVersion> ticketListDistanceComparator =
				new Comparator<PatcherProjectVersion>() {

					@Override
					public int compare(
						PatcherProjectVersion version1,
						PatcherProjectVersion version2) {

						int length1 = Math.abs(
							version1.getFixedIssues(
							).length() - fixedIssuesLength);
						int length2 = Math.abs(
							version2.getFixedIssues(
							).length() - fixedIssuesLength);

						if (length1 == length2) {
							return Long.compare(
								version1.getPatcherProjectVersionId(),
								version2.getPatcherProjectVersionId());
						}

						return Integer.compare(length1, length2);
					}

				};

			TreeSet<PatcherProjectVersion> orderedPatcherProjectVersions =
				new TreeSet<>(ticketListDistanceComparator);

			orderedPatcherProjectVersions.addAll(
				quarterlyPatcherProjectVersions);
			orderedPatcherProjectVersions.addAll(updatePatcherProjectVersions);

			for (PatcherProjectVersion orderedPatcherProjectVersion :
					orderedPatcherProjectVersions) {

				long patcherProjectVersionId =
					orderedPatcherProjectVersion.getPatcherProjectVersionId();

				if (!patcherProjectVersionIds.contains(
						patcherProjectVersionId)) {

					patcherProjectVersionIds.add(patcherProjectVersionId);
				}
			}
		}

		return scanPatcherFixIdsByProjectVersionIds(
			patcherProjectVersionIds, patcherBuildTickets, false);
	}

	protected static List<Long> scanPatcherFixIdsByTickets(
			String patcherBuildName, List<Long> patcherFixPackFixIds,
			long patcherProjectVersionId, String tickets,
			List<PatcherFix> patcherFixesSelection)
		throws Exception {

		List<Long> patcherFixIds = new ArrayList<>();

		List<String> patcherBuildTickets = PatcherUtil.sortTokens(tickets);

		PatcherFixRadix patcherFixRadix = PatcherFixUtil.getPatcherFixRadix(
			patcherProjectVersionId, patcherBuildTickets,
			patcherFixesSelection);

		StringBundler sb = new StringBundler(
			(25 * patcherBuildTickets.size()) + 3);

		sb.append(
			"<div style=\"border:1px solid gray; height:300px; " +
				"overflow-y:scroll;\">");

		while (!patcherBuildTickets.isEmpty()) {
			PatcherFix patcherFix = PatcherFixUtil.getPatcherFix(
				patcherFixRadix, patcherBuildTickets);

			if (patcherFix == null) {
				patcherFixPackFixIds = scanWithPatcherFixPackFixTickets(
					patcherBuildTickets, patcherFixPackFixIds,
					patcherProjectVersionId, tickets, patcherFixesSelection);

				if (patcherFixPackFixIds != null) {
					patcherFixIds.addAll(patcherFixPackFixIds);

					return patcherFixIds;
				}

				sb.append("NO MATCH FOUND");
				sb.append("</div>");

				String msg = sb.toString();

				sb = new StringBundler(12);

				sb.append(
					LanguageUtil.get(
						LocaleUtil.getMostRelevantLocale(),
						"failed-building-a-patch-for-tickets"));
				sb.append("<br />");
				sb.append(
					StringUtil.replace(
						patcherBuildName, StringPool.COMMA,
						StringPool.COMMA_AND_SPACE));
				sb.append("<br /><br />");
				sb.append(
					LanguageUtil.get(
						LocaleUtil.getMostRelevantLocale(),
						"there-was-no-match-found-in-our-fix-catalog-for-the-" +
							"following-tokens"));
				sb.append("<br />");
				sb.append(
					StringUtil.merge(
						patcherBuildTickets, StringPool.COMMA_AND_SPACE));
				sb.append("<br /><br />");
				sb.append(
					LanguageUtil.get(
						LocaleUtil.getMostRelevantLocale(), "process"));
				sb.append(StringPool.COLON);
				sb.append("<br />");
				sb.append(msg);

				_log.error(sb.toString());

				throw new Exception(sb.toString());
			}

			PatcherFix latestPatcherFix = PatcherBuildUtil.getLatestPatcherFix(
				patcherFix);

			if (latestPatcherFix.getType() ==
					PatcherFixConstants.TYPE_EXCLUDED) {

				String msg = sb.toString();

				sb = new StringBundler(12);

				sb.append("PICKED UP FIX ID WITH EXCLUDED ANCESTOR(S)");
				sb.append("<br />");
				sb.append("FIX ID");
				sb.append(StringPool.COLON);
				sb.append(patcherFix.getPatcherFixId());
				sb.append("<br />");
				sb.append("<br />");
				sb.append("EXCLUDED ANCESTOR(S)");
				sb.append(StringPool.COLON);
				sb.append("<br />");

				List<Long> excludedAncestorIds = new ArrayList<>();

				List<PatcherFix> patcherFixAncestors =
					PatcherFixRelUtil.getPatcherFixAncestors(patcherFix);

				for (PatcherFix patcherFixAncestor : patcherFixAncestors) {
					if (patcherFixAncestor.getType() ==
							PatcherFixConstants.TYPE_EXCLUDED) {

						excludedAncestorIds.add(
							patcherFixAncestor.getPatcherFixId());
					}
				}

				sb.append(
					StringUtil.merge(
						excludedAncestorIds, StringPool.COMMA_AND_SPACE));

				sb.append(msg);

				throw new Exception(sb.toString());
			}

			patcherFixIds.add(latestPatcherFix.getPatcherFixId());

			List<String> patcherFixTickets = PatcherUtil.getTokens(
				latestPatcherFix.getName());

			patcherBuildTickets.removeAll(patcherFixTickets);

			sb.append("FOUND");
			sb.append(StringPool.COLON);
			sb.append("<br />");
			sb.append("ID");
			sb.append(StringPool.COLON);
			sb.append(StringPool.SPACE);
			sb.append(latestPatcherFix.getPatcherFixId());
			sb.append("<br />");
			sb.append("NAME");
			sb.append(StringPool.COLON);
			sb.append(StringPool.SPACE);
			sb.append(StringPool.QUOTE);
			sb.append(
				StringUtil.replace(
					latestPatcherFix.getName(), StringPool.COMMA,
					StringPool.COMMA_AND_SPACE));
			sb.append(StringPool.QUOTE);
			sb.append("<br />");
			sb.append("REMOVING TICKETS FROM PHRASE");
			sb.append(StringPool.DOUBLE_PERIOD);
			sb.append("<br /><br />");
			sb.append("NEW PHRASE");
			sb.append(StringPool.COLON);
			sb.append("<br />");
			sb.append(StringPool.QUOTE);
			sb.append(
				StringUtil.merge(
					patcherBuildTickets, StringPool.COMMA_AND_SPACE));
			sb.append(StringPool.QUOTE);
			sb.append("<br /><br />");
		}

		patcherFixIds.addAll(patcherFixPackFixIds);

		return patcherFixIds;
	}

	protected static List<String> scanPatcherFixTicketsByProjectVersionId(
			List<String> patcherBuildTickets, long patcherProjectVersionId,
			List<PatcherFix> patcherFixesSelection)
		throws Exception {

		List<String> patcherFixTickets = new ArrayList<>();

		for (PatcherFix patcherFix : patcherFixesSelection) {
			String[] tickets = StringUtil.split(patcherFix.getName());

			if (!PatcherFixUtil.containsAllTickets(
					patcherBuildTickets, tickets)) {

				continue;
			}

			patcherFixTickets.addAll(
				PatcherUtil.getTickets(patcherFix.getName()));
		}

		return patcherFixTickets;
	}

	protected static List<Long> scanWithPatcherFixPackFixTickets(
			List<String> missingTickets, List<Long> patcherFixPackFixIds,
			long patcherProjectVersionId, String patcherBuildTickets,
			List<PatcherFix> patcherFixesSelection)
		throws Exception {

		Map<String, PatcherFix> PatcherFixPackFixNamePatcherFixPackFixMap =
			new HashMap<>();

		List<String> tickets = PatcherUtil.sortTokens(patcherBuildTickets);

		for (long patcherFixPackFixId : patcherFixPackFixIds) {
			PatcherFix patcherFixPackFix =
				PatcherFixLocalServiceUtil.getPatcherFix(patcherFixPackFixId);

			PatcherFixPackFixNamePatcherFixPackFixMap.put(
				patcherFixPackFix.getName(), patcherFixPackFix);

			tickets.addAll(PatcherUtil.getTickets(patcherFixPackFix.getName()));
		}

		PatcherFixRadix patcherFixRadix = PatcherFixUtil.getPatcherFixRadix(
			patcherProjectVersionId, tickets, patcherFixesSelection,
			missingTickets.toArray(new String[0]));

		while (!missingTickets.isEmpty()) {
			PatcherFix foundPatcherFix = PatcherFixUtil.getPatcherFix(
				patcherFixRadix, tickets);

			if (foundPatcherFix == null) {
				return null;
			}

			List<String> foundPatcherFixTickets = PatcherUtil.sortTokens(
				foundPatcherFix.getName());

			foundPatcherFixTickets.removeAll(missingTickets);

			if (PatcherFixPackFixNamePatcherFixPackFixMap.containsKey(
					StringUtil.merge(foundPatcherFixTickets))) {

				PatcherFix patcherFixPackFix =
					PatcherFixPackFixNamePatcherFixPackFixMap.get(
						StringUtil.merge(foundPatcherFixTickets));

				patcherFixPackFixIds.remove(
					patcherFixPackFix.getPatcherFixId());

				PatcherFix latestFoundPatcherFix =
					PatcherBuildUtil.getLatestPatcherFix(foundPatcherFix);

				if (latestFoundPatcherFix.getType() ==
						PatcherFixConstants.TYPE_EXCLUDED) {

					return null;
				}

				patcherFixPackFixIds.add(
					latestFoundPatcherFix.getPatcherFixId());

				missingTickets.removeAll(
					PatcherUtil.sortTokens(latestFoundPatcherFix.getName()));
			}
		}

		return patcherFixPackFixIds;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherScanUtil.class);

}