/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherBuildRel;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherBuildRelLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zsolt Balogh
 */
public class PatcherBuildRelUtil {

	public static void deletePatcherBuildRelsByChildPatcherBuildId(
		long childPatcherBuildId) {

		for (PatcherBuildRel patcherBuildRel :
				PatcherBuildRelLocalServiceUtil.
					getPatcherBuildRelsByChildPatcherBuildId(
						childPatcherBuildId)) {

			PatcherBuildRelLocalServiceUtil.deletePatcherBuildRel(
				patcherBuildRel);
		}
	}

	public static PatcherBuild fetchSiblingChildPatcherBuild(
			PatcherBuild childPatcherBuild)
		throws Exception {

		List<PatcherBuild> parentPatcherBuilds = getParentPatcherBuilds(
			childPatcherBuild);

		PatcherBuild parentPatcherBuild = parentPatcherBuilds.get(0);

		List<PatcherBuild> childPatcherBuilds = getChildPatcherBuilds(
			parentPatcherBuild);

		for (PatcherBuild curChildPatcherBuild : childPatcherBuilds) {
			if (curChildPatcherBuild.getPatcherBuildId() ==
					childPatcherBuild.getPatcherBuildId()) {

				continue;
			}

			return curChildPatcherBuild;
		}

		return null;
	}

	public static List<PatcherBuild> getChildPatcherBuilds(
		PatcherBuild patcherBuild) {

		return TransformUtil.transform(
			PatcherBuildRelLocalServiceUtil.
				getPatcherBuildRelsByParentPatcherBuildId(
					patcherBuild.getPatcherBuildId()),
			patcherBuildRel -> PatcherBuildLocalServiceUtil.getPatcherBuild(
				patcherBuildRel.getChildPatcherBuildId()));
	}

	public static List<PatcherFix> getChildPatcherBuildsMainFixes(
			PatcherBuild patcherBuild)
		throws Exception {

		List<PatcherFix> patcherFixes = new ArrayList<>();

		List<PatcherBuild> childPatcherBuilds = getChildPatcherBuilds(
			patcherBuild);

		for (PatcherBuild childPatcherBuild : childPatcherBuilds) {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				childPatcherBuild.getPatcherFixId());

			patcherFixes.add(patcherFix);
		}

		if (patcherFixes.isEmpty()) {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				patcherBuild.getPatcherFixId());

			patcherFixes.add(patcherFix);
		}

		return patcherFixes;
	}

	public static List<PatcherBuild> getParentPatcherBuilds(
		PatcherBuild patcherBuild) {

		return TransformUtil.transform(
			PatcherBuildRelLocalServiceUtil.
				getPatcherBuildRelsByChildPatcherBuildId(
					patcherBuild.getPatcherBuildId()),
			patcherBuildRel -> PatcherBuildLocalServiceUtil.getPatcherBuild(
				patcherBuildRel.getParentPatcherBuildId()));
	}

	public static boolean hasChildPatcherBuilds(PatcherBuild patcherBuild) {
		int count =
			PatcherBuildRelLocalServiceUtil.
				getPatcherBuildRelsByParentPatcherBuildIdCount(
					patcherBuild.getPatcherBuildId());

		if (count > 0) {
			return true;
		}

		return false;
	}

	public static boolean hasParentPatcherBuilds(PatcherBuild patcherBuild) {
		int count =
			PatcherBuildRelLocalServiceUtil.
				getPatcherBuildRelsByChildPatcherBuildIdCount(
					patcherBuild.getPatcherBuildId());

		if (count > 0) {
			return true;
		}

		return false;
	}

}