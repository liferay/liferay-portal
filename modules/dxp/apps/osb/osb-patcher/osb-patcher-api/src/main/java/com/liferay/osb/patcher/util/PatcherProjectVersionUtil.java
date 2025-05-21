/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.AlloyServiceInvoker;
import com.liferay.osb.patcher.constants.PatcherConstants;
import com.liferay.osb.patcher.constants.PatcherProjectVersionConstants;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zsolt Balogh
 */
public class PatcherProjectVersionUtil {

	public static PatcherProjectVersion fetchPatcherProjectVersionByCommittish(
			String committish)
		throws Exception {

		AlloyServiceInvoker patcherProjectVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProjectVersion.class.getName());

		List<PatcherProjectVersion> patcherProjectVersions =
			patcherProjectVersionAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {"committish", committish});

		if (!patcherProjectVersions.isEmpty()) {
			return patcherProjectVersions.get(0);
		}

		return null;
	}

	public static PatcherProjectVersion fetchPatcherProjectVersionByName(
			String name)
		throws Exception {

		AlloyServiceInvoker patcherProjectVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProjectVersion.class.getName());

		List<PatcherProjectVersion> patcherProjectVersions =
			patcherProjectVersionAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {"name", StringUtil.toUpperCase(name)});

		if (!patcherProjectVersions.isEmpty()) {
			return patcherProjectVersions.get(0);
		}

		return null;
	}

	public static List<PatcherProjectVersion>
			fetchPatcherProjectVersionByPatcherProductVersionId(
				long patcherProductVersionId)
		throws Exception {

		AlloyServiceInvoker patcherProjectVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProjectVersion.class.getName());

		return patcherProjectVersionAlloyServiceInvoker.executeDynamicQuery(
			new Object[] {"patcherProductVersionId", patcherProductVersionId});
	}

	public static List<String> getCumulativePatcherProjectVersionFixedIssues(
			long patcherProjectVersionId)
		throws Exception {

		if (patcherProjectVersionId == 0) {
			return new ArrayList<>();
		}

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherProjectVersionId);

		return getCumulativePatcherProjectVersionFixedIssues(
			patcherProjectVersion);
	}

	public static List<String> getCumulativePatcherProjectVersionFixedIssues(
			PatcherProjectVersion patcherProjectVersion)
		throws Exception {

		List<String> fixedIssues = new ArrayList<>();

		Pattern pattern = Pattern.compile(PatcherConstants.FIX_PACK_TAG_REGEX);

		Matcher matcher = pattern.matcher(
			patcherProjectVersion.getCommittish());

		if (!matcher.find()) {
			return PatcherUtil.getTokens(
				patcherProjectVersion.getFixedIssues());
		}

		int fixPackVersionNumber = Integer.parseInt(matcher.group(3));
		int portalVersionNumber = Integer.parseInt(matcher.group(4));

		AlloyServiceInvoker patcherProjectVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProjectVersion.class.getName());

		List<PatcherProjectVersion> patcherProjectVersions =
			patcherProjectVersionAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {
					"patcherProductVersionId",
					patcherProjectVersion.getPatcherProductVersionId(),
					"repositoryName", patcherProjectVersion.getRepositoryName()
				});

		for (PatcherProjectVersion curPatcherProjectVersion :
				patcherProjectVersions) {

			matcher = pattern.matcher(curPatcherProjectVersion.getCommittish());

			if (matcher.find()) {
				int curFixPackVersionNumber = Integer.parseInt(
					matcher.group(3));
				int curPortalVersionNumber = Integer.parseInt(matcher.group(4));

				if ((curPortalVersionNumber != portalVersionNumber) ||
					(curFixPackVersionNumber > fixPackVersionNumber)) {

					continue;
				}

				fixedIssues.addAll(
					PatcherUtil.getTokens(
						curPatcherProjectVersion.getFixedIssues()));
			}
		}

		ListUtil.distinct(fixedIssues);

		return fixedIssues;
	}

	public static PatcherProjectVersion getNeighborPatcherProjectVersion(
			String patcherProjectVersionCommittish, int direction)
		throws Exception {

		String neighborCommittish = null;

		Pattern pattern = Pattern.compile(PatcherConstants.FIX_PACK_TAG_REGEX);

		Matcher matcher = pattern.matcher(patcherProjectVersionCommittish);

		if (matcher.find()) {
			String fixPackDE = matcher.group(1);

			int fixPackVersionNumber = Integer.parseInt(matcher.group(3));

			fixPackVersionNumber += direction;

			int portalVersionNumber = Integer.parseInt(matcher.group(4));

			neighborCommittish =
				fixPackDE + fixPackVersionNumber + "-" + portalVersionNumber;
		}
		else if (patcherProjectVersionCommittish.contains("q")) {
			int year = Integer.valueOf(
				patcherProjectVersionCommittish.substring(0, 4));
			int quarter = Integer.valueOf(
				patcherProjectVersionCommittish.substring(6, 7));
			int patch = Integer.valueOf(
				patcherProjectVersionCommittish.substring(
					patcherProjectVersionCommittish.lastIndexOf('.') + 1));

			patch += direction;

			neighborCommittish = "" + year + ".q" + quarter + "." + patch;
		}
		else if (patcherProjectVersionCommittish.contains("u")) {
			int index = patcherProjectVersionCommittish.lastIndexOf('u') + 1;

			String prefix = patcherProjectVersionCommittish.substring(0, index);
			int update = Integer.valueOf(
				patcherProjectVersionCommittish.substring(index));

			update += direction;

			neighborCommittish = prefix + update;
		}

		return PatcherProjectVersionUtil.fetchPatcherProjectVersionByCommittish(
			neighborCommittish);
	}

	public static long getPatcherProductVersionId(long patcherProjectVersionId)
		throws Exception {

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherProjectVersionId);

		return patcherProjectVersion.getPatcherProductVersionId();
	}

	public static Map<Long, List<PatcherProjectVersion>>
			getPatcherProductVersionIdPatcherProjectVersions()
		throws Exception {

		Map<Long, List<PatcherProjectVersion>> patcherProjectVersionsMap =
			new HashMap<>();

		List<PatcherProductVersion> patcherProductVersions =
			PatcherProductVersionLocalServiceUtil.getPatcherProductVersions(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (PatcherProductVersion patcherProductVersion :
				patcherProductVersions) {

			long patcherProductVersionId =
				patcherProductVersion.getPatcherProductVersionId();

			List<PatcherProjectVersion> patcherProjectVersions =
				getPatcherProjectVersions(
					"name", patcherProductVersionId,
					PortletPropsValues.OSB_PATCHER_LIFERAY_PORTAL_REPOSITORY,
					true, false);

			for (PatcherProjectVersion patcherProjectVersion :
					patcherProjectVersions) {

				patcherProjectVersion.setFixedIssues(null);
			}

			patcherProjectVersionsMap.put(
				patcherProductVersionId, patcherProjectVersions);
		}

		return patcherProjectVersionsMap;
	}

	public static PatcherProjectVersion getPatcherProjectVersion(String name)
		throws Exception {

		AlloyServiceInvoker patcherProjectVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProjectVersion.class.getName());

		List<PatcherProjectVersion> patcherProjectVersions =
			patcherProjectVersionAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {"name", StringUtil.toUpperCase(name)});

		return patcherProjectVersions.get(0);
	}

	public static List<PatcherProjectVersion> getPatcherProjectVersions(
			String columnName, boolean columnAscending)
		throws Exception {

		List<PatcherProjectVersion> patcherProjectVersions =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(
			PatcherProjectVersionModelImpl.TABLE_NAME, columnName,
			columnAscending);

		return ListUtil.sort(patcherProjectVersions, obc);
	}

	public static List<PatcherProjectVersion> getPatcherProjectVersions(
			String columnName, long patcherProductVersionId,
			boolean columnAscending)
		throws Exception {

		AlloyServiceInvoker patcherProjectVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProjectVersion.class.getName());

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(
			PatcherProjectVersionModelImpl.TABLE_NAME, columnName,
			columnAscending);

		return patcherProjectVersionAlloyServiceInvoker.executeDynamicQuery(
			new Object[] {"patcherProductVersionId", patcherProductVersionId},
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, obc);
	}

	public static List<PatcherProjectVersion> getPatcherProjectVersions(
			String columnName, long patcherProductVersionId,
			String repositoryName, boolean columnAscending, boolean publicOnly)
		throws Exception {

		AlloyServiceInvoker patcherProjectVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProjectVersion.class.getName());

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(
			PatcherProjectVersionModelImpl.TABLE_NAME, columnName,
			columnAscending);

		List<PatcherProjectVersion> patcherProjectVersions =
			patcherProjectVersionAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {
					"patcherProductVersionId", patcherProductVersionId,
					"repositoryName", repositoryName
				},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, obc);

		if (!publicOnly) {
			return patcherProjectVersions;
		}

		List<PatcherProjectVersion> filteredPatcherProjectVersions =
			new ArrayList<>();

		for (PatcherProjectVersion patcherProjectVersion :
				patcherProjectVersions) {

			String name = patcherProjectVersion.getName();

			if (!name.contains(
					PatcherProjectVersionConstants.PRIVATE_NAME_SUFFIX)) {

				filteredPatcherProjectVersions.add(patcherProjectVersion);
			}
		}

		return filteredPatcherProjectVersions;
	}

	public static List<PatcherProjectVersion> getPatcherProjectVersions(
			String columnName, String repositoryName, boolean columnAscending)
		throws Exception {

		AlloyServiceInvoker patcherProjectVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProjectVersion.class.getName());

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(
			PatcherProjectVersionModelImpl.TABLE_NAME, columnName,
			columnAscending);

		return patcherProjectVersionAlloyServiceInvoker.executeDynamicQuery(
			new Object[] {"repositoryName", repositoryName}, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, obc);
	}

	public static long getRootPatcherProjectVersionId(
			long patcherProjectVersionId)
		throws Exception {

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherProjectVersionId);

		if (patcherProjectVersion.getRootPatcherProjectVersionId() > 0) {
			return patcherProjectVersion.getRootPatcherProjectVersionId();
		}

		return patcherProjectVersionId;
	}

	public static String getRootPatcherProjectVersionName(
			PatcherProjectVersion patcherProjectVersion)
		throws Exception {

		if (patcherProjectVersion.getRootPatcherProjectVersionId() != 0) {
			PatcherProjectVersion rootPatcherProjectVersion =
				PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
					patcherProjectVersion.getRootPatcherProjectVersionId());

			if (rootPatcherProjectVersion != null) {
				return rootPatcherProjectVersion.getName();
			}
		}

		return StringPool.BLANK;
	}

	public static List<PatcherProjectVersion> getRootPatcherProjectVersions()
		throws Exception {

		AlloyServiceInvoker patcherProjectVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProjectVersion.class.getName());

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(
			PatcherProjectVersionModelImpl.TABLE_NAME, "name", true);

		return patcherProjectVersionAlloyServiceInvoker.executeDynamicQuery(
			new Object[] {"rootPatcherProjectVersionId", 0L}, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, obc);
	}

	public static PatcherProjectVersion getSiblingPatcherProjectVersion(
			long patcherProjectVersionId)
		throws Exception {

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherProjectVersionId);

		return getSiblingPatcherProjectVersion(
			patcherProjectVersion.getCommittish());
	}

	public static PatcherProjectVersion getSiblingPatcherProjectVersion(
			String patcherProjectVersionCommittish)
		throws Exception {

		String siblingPatcherProjectVersionCommittish;

		if (isPrivatePatcherProjectVersionCommittish(
				patcherProjectVersionCommittish)) {

			siblingPatcherProjectVersionCommittish =
				patcherProjectVersionCommittish.replace(
					PatcherProjectVersionConstants.PRIVATE_NAME_SUFFIX,
					StringPool.BLANK);
		}
		else {
			siblingPatcherProjectVersionCommittish =
				patcherProjectVersionCommittish +
					PatcherProjectVersionConstants.PRIVATE_NAME_SUFFIX;
		}

		return fetchPatcherProjectVersionByCommittish(
			siblingPatcherProjectVersionCommittish);
	}

	public static boolean isCombinedBranchPatcherProjectVersion(
			long patcherProjectVersionId)
		throws Exception {

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherProjectVersionId);

		return patcherProjectVersion.isCombinedBranch();
	}

	public static boolean isMatchingPatcherProjectVersions(
			PatcherProjectVersion patcherProjectVersion1,
			PatcherProjectVersion patcherProjectVersion2)
		throws Exception {

		if ((patcherProjectVersion1.isCombinedBranch() ==
				patcherProjectVersion2.isCombinedBranch()) &&
			(isPreDe28PatcherProjectVersion(patcherProjectVersion1) ==
				isPreDe28PatcherProjectVersion(patcherProjectVersion2))) {

			return true;
		}

		return false;
	}

	public static boolean isPreDe28PatcherProjectVersion(
			PatcherProjectVersion patcherProjectVersion)
		throws Exception {

		String patcherProjectVersionCommittish =
			patcherProjectVersion.getCommittish();

		Pattern pattern = Pattern.compile(PatcherConstants.FIX_PACK_TAG_REGEX);

		Matcher matcher = pattern.matcher(patcherProjectVersionCommittish);

		if (!matcher.find()) {
			return false;
		}

		String fixPackComponent = matcher.group(2);

		int fixPackVersionNumber = Integer.parseInt(matcher.group(3));

		if (fixPackComponent.equals("de") && (fixPackVersionNumber < 28)) {
			return true;
		}

		return false;
	}

	public static boolean isPrivatePatcherProjectVersion(
			long patcherProjectVersionId)
		throws Exception {

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherProjectVersionId);

		return isPrivatePatcherProjectVersionCommittish(
			patcherProjectVersion.getCommittish());
	}

	public static boolean isPrivatePatcherProjectVersionCommittish(
			String patcherProjectVersionCommittish)
		throws Exception {

		return patcherProjectVersionCommittish.contains(
			PatcherProjectVersionConstants.PRIVATE_NAME_SUFFIX);
	}

	public static boolean isSiblingPatcherProjectVersionIds(
			long patcherProjectVersionId1, long patcherProjectVersionId2)
		throws Exception {

		PatcherProjectVersion siblingPatcherProjectVersion =
			getSiblingPatcherProjectVersion(patcherProjectVersionId1);

		if ((siblingPatcherProjectVersion != null) &&
			(siblingPatcherProjectVersion.getPatcherProjectVersionId() ==
				patcherProjectVersionId2)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherProjectVersionUtil.class);

}