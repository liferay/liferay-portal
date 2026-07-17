/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.exception.PatcherScanException;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;

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

		long patcherProjectVersionId =
			patcherBuild.getPatcherProjectVersionId();

		patcherProjectVersionIds.add(patcherProjectVersionId);

		if (!PatcherProjectVersionUtil.isCombinedBranchPatcherProjectVersion(
				patcherProjectVersionId)) {

			PatcherProjectVersion siblingPatcherProjectVersion =
				PatcherProjectVersionUtil.getSiblingPatcherProjectVersion(
					patcherProjectVersionId);

			patcherProjectVersionIds.add(
				siblingPatcherProjectVersion.getPatcherProjectVersionId());
		}

		List<String> patcherBuildTickets = PatcherUtil.sortTokens(
			PatcherUtil.getTickets(patcherBuild.getName()));

		Map<Long, List<Long>> patcherProjectVersionIdPatcherFixIdsMap =
			scanPatcherFixIdsByProjectVersionIds(
				patcherProjectVersionIds, patcherBuildTickets, true);

		if (!patcherBuildTickets.isEmpty()) {
			Map<Long, List<Long>> otherPatcherProjectVersionIdPatcherFixIdsMap =
				scanPatcherFixIdsByOtherProjectVersions(
					PatcherProjectVersionLocalServiceUtil.
						getPatcherProjectVersion(patcherProjectVersionId),
					patcherBuildTickets);

			patcherProjectVersionIdPatcherFixIdsMap.putAll(
				otherPatcherProjectVersionIdPatcherFixIdsMap);

			Object[] arguments = {
				patcherBuild.getPatcherProductVersionId(),
				patcherProjectVersionId,
				StringUtil.merge(
					patcherBuildTickets, StringPool.COMMA_AND_SPACE),
				StringPool.BLANK
			};

			throw new PatcherScanException(
				"failed-building-a-patch-for-fixes-x", arguments);
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
				StringUtil.merge(tickets, StringPool.COMMA),
				patcherFixesSelection);
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
				StringUtil.merge(foundTickets, StringPool.COMMA),
				patcherProjectVersionId, patcherFixesSelection);

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

		List<Long> patcherFixPackPatcherFixIds =
			PatcherFixUtil.getPatcherFixIds(
				PatcherFixPackUtil.getPatcherFixPacks(
					PatcherFixPackUtil.getPatcherFixPackNames(patcherBuildName),
					patcherProjectVersionId));

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

			String fixedIssues = patcherProjectVersion.getFixedIssues();

			PatcherProductVersion quarterlyProduct =
				PatcherProductVersionUtil.fetchPatcherProductVersion(
					"Quarterly Releases");

			List<PatcherProjectVersion> quarterlyPatcherProjectVersions =
				PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions(
					quarterlyProduct.getPatcherProductVersionId());

			PatcherProductVersion updateProduct =
				PatcherProductVersionUtil.fetchPatcherProductVersion("DXP 7.4");

			List<PatcherProjectVersion> updatePatcherProjectVersions =
				PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions(
					updateProduct.getPatcherProductVersionId());

			Comparator<PatcherProjectVersion> ticketListDistanceComparator =
				new Comparator<PatcherProjectVersion>() {

					@Override
					public int compare(
						PatcherProjectVersion version1,
						PatcherProjectVersion version2) {

						String fixedIssues1 = version1.getFixedIssues();

						int length1 = Math.abs(
							fixedIssues1.length() - fixedIssues.length());

						String fixedIssues2 = version2.getFixedIssues();

						int length2 = Math.abs(
							fixedIssues2.length() - fixedIssues.length());

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

		List<Map<String, Object>> trace = new ArrayList<>();

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

				PatcherProjectVersion patcherProjectVersion =
					PatcherProjectVersionLocalServiceUtil.
						getPatcherProjectVersion(patcherProjectVersionId);

				Object[] arguments = {
					patcherProjectVersion.getPatcherProductVersionId(),
					patcherProjectVersionId,
					StringUtil.merge(
						patcherBuildTickets, StringPool.COMMA_AND_SPACE),
					trace
				};

				throw new PatcherScanException(
					"failed-building-a-patch-for-fixes-x", arguments);
			}

			PatcherFix latestPatcherFix = PatcherBuildUtil.getLatestPatcherFix(
				patcherFix);

			if (latestPatcherFix.getType() ==
					PatcherFixConstants.TYPE_EXCLUDED) {

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

				Object[] arguments = {
					patcherFix.getPatcherFixId(),
					StringUtil.merge(
						excludedAncestorIds, StringPool.COMMA_AND_SPACE),
					trace
				};

				throw new PatcherScanException(
					"picked-up-fix-id-with-excluded-ancestors", arguments);
			}

			patcherFixIds.add(latestPatcherFix.getPatcherFixId());

			List<String> patcherFixTickets = PatcherUtil.getTokens(
				latestPatcherFix.getName());

			patcherBuildTickets.removeAll(patcherFixTickets);

			trace.add(
				HashMapBuilder.<String, Object>put(
					"patcherFixId", latestPatcherFix.getPatcherFixId()
				).put(
					"patcherFixName", latestPatcherFix.getName()
				).put(
					"remainingTickets",
					StringUtil.merge(
						patcherBuildTickets, StringPool.COMMA_AND_SPACE)
				).build());
		}

		patcherFixIds.addAll(patcherFixPackFixIds);

		return patcherFixIds;
	}

	protected static List<String> scanPatcherFixTicketsByProjectVersionId(
		List<String> patcherBuildTickets, long patcherProjectVersionId,
		List<PatcherFix> patcherFixesSelection) {

		List<String> patcherFixTickets = new ArrayList<>();

		for (PatcherFix patcherFix : patcherFixesSelection) {
			if (!PatcherFixUtil.containsAllTickets(
					patcherBuildTickets,
					StringUtil.split(patcherFix.getName()))) {

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

		Map<String, PatcherFix> patcherFixPackFixNamePatcherFixPackFixMap =
			new HashMap<>();

		List<String> tickets = PatcherUtil.sortTokens(patcherBuildTickets);

		for (long patcherFixPackFixId : patcherFixPackFixIds) {
			PatcherFix patcherFixPackFix =
				PatcherFixLocalServiceUtil.getPatcherFix(patcherFixPackFixId);

			patcherFixPackFixNamePatcherFixPackFixMap.put(
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

			PatcherFix patcherFixPackFix =
				patcherFixPackFixNamePatcherFixPackFixMap.get(
					StringUtil.merge(foundPatcherFixTickets, StringPool.COMMA));

			if (patcherFixPackFix != null) {
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

}