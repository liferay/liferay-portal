/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.constants.PatcherConstants;
import com.liferay.osb.patcher.constants.PatcherProjectVersionConstants;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.osb.patcher.util.comparator.PatcherProjectVersionNameComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;

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

		List<PatcherProjectVersion> patcherProjectVersions =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions(
				patcherProjectVersion.getPatcherProductVersionId(),
				patcherProjectVersion.getRepositoryName(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

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
		String patcherProjectVersionCommittish, int direction) {

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

		return PatcherProjectVersionLocalServiceUtil.
			fetchPatcherProjectVersionByCommittish(neighborCommittish);
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
				PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions(
					patcherProductVersionId,
					PortletPropsValues.OSB_PATCHER_LIFERAY_PORTAL_REPOSITORY,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					PatcherProjectVersionNameComparator.getInstance(true));

			for (PatcherProjectVersion patcherProjectVersion :
					patcherProjectVersions) {

				patcherProjectVersion.setFixedIssues(null);
			}

			patcherProjectVersionsMap.put(
				patcherProductVersionId, patcherProjectVersions);
		}

		return patcherProjectVersionsMap;
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

		return PatcherProjectVersionLocalServiceUtil.
			fetchPatcherProjectVersionByCommittish(
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
		PatcherProjectVersion patcherProjectVersion2) {

		if ((patcherProjectVersion1.isCombinedBranch() ==
				patcherProjectVersion2.isCombinedBranch()) &&
			(isPreDe28PatcherProjectVersion(patcherProjectVersion1) ==
				isPreDe28PatcherProjectVersion(patcherProjectVersion2))) {

			return true;
		}

		return false;
	}

	public static boolean isPreDe28PatcherProjectVersion(
		PatcherProjectVersion patcherProjectVersion) {

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