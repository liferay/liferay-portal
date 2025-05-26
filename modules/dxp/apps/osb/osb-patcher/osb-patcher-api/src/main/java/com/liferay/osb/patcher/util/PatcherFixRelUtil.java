/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixRelModel;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixRelLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Zsolt Balogh
 */
public class PatcherFixRelUtil {

	public static void addPatcherFixRel(
		long childPatcherFixId, List<Long> parentPatcherFixIds) {

		for (long parentPatcherFixId : parentPatcherFixIds) {
			PatcherFixRelLocalServiceUtil.addPatcherFixRel(
				childPatcherFixId, parentPatcherFixId);
		}
	}

	public static List<Long> getChildPatcherFixIds(
			List<Long> allPatcherFixIds, List<Long> patcherFixIds)
		throws Exception {

		List<Long> childPatcherFixIds = new ArrayList<>();

		for (long patcherFixId : patcherFixIds) {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
				patcherFixId);

			if ((patcherFix == null) ||
				(patcherFix.getStatus() !=
					WorkflowConstants.STATUS_FIX_COMPLETE) ||
				(patcherFix.getType() != PatcherFixConstants.TYPE_PATCH) ||
				PatcherFixPackUtil.containsPatcherFixPackName(
					patcherFix.getName())) {

				continue;
			}

			List<Long> parentPatcherFixIds = getParentPatcherFixIds(
				patcherFixId);

			if ((parentPatcherFixIds.size() != 2) ||
				!allPatcherFixIds.containsAll(parentPatcherFixIds)) {

				continue;
			}

			patcherFix = PatcherBuildUtil.getLatestPatcherFix(patcherFix);

			childPatcherFixIds.add(patcherFix.getPatcherFixId());
		}

		return childPatcherFixIds;
	}

	public static List<Long> getChildPatcherFixIds(long parentPatcherFixId) {
		return TransformUtil.transform(
			PatcherFixRelLocalServiceUtil.getPatcherFixRelsByParentPatcherFixId(
				parentPatcherFixId),
			PatcherFixRelModel::getChildPatcherFixId);
	}

	public static List<PatcherFix> getChildPatcherFixPatcherFixes(
		PatcherFix parentPatcherFix) {

		return TransformUtil.transform(
			PatcherFixRelLocalServiceUtil.getPatcherFixRelsByParentPatcherFixId(
				parentPatcherFix.getPatcherFixId()),
			patcherFixRel -> PatcherFixLocalServiceUtil.getPatcherFix(
				patcherFixRel.getChildPatcherFixId()));
	}

	public static List<PatcherFix> getParentPatcherFixes(
		PatcherFix childPatcherFix) {

		return TransformUtil.transform(
			PatcherFixRelLocalServiceUtil.getPatcherFixRelsByChildPatcherFixId(
				childPatcherFix.getPatcherFixId()),
			patcherFixRel -> PatcherFixLocalServiceUtil.getPatcherFix(
				patcherFixRel.getParentPatcherFixId()));
	}

	public static List<Long> getParentPatcherFixIds(
			List<Long> allPatcherFixIds, List<Long> patcherFixIds)
		throws Exception {

		List<Long> parentPatcherFixIds = new ArrayList<>();

		for (long patcherFixId : patcherFixIds) {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
				patcherFixId);

			if ((patcherFix == null) ||
				(patcherFix.getStatus() !=
					WorkflowConstants.STATUS_FIX_COMPLETE) ||
				(patcherFix.getType() != PatcherFixConstants.TYPE_PATCH) ||
				PatcherFixPackUtil.containsPatcherFixPackName(
					patcherFix.getName())) {

				continue;
			}

			List<Long> parentPatcherFixIdPatcherFixIds = getParentPatcherFixIds(
				patcherFixId);

			if ((parentPatcherFixIdPatcherFixIds.size() != 2) ||
				!allPatcherFixIds.containsAll(
					parentPatcherFixIdPatcherFixIds)) {

				continue;
			}

			parentPatcherFixIds.addAll(parentPatcherFixIdPatcherFixIds);
		}

		return parentPatcherFixIds;
	}

	public static List<Long> getParentPatcherFixIds(long childPatcherFixId) {
		return TransformUtil.transform(
			PatcherFixRelLocalServiceUtil.getPatcherFixRelsByChildPatcherFixId(
				childPatcherFixId),
			PatcherFixRelModel::getParentPatcherFixId);
	}

	public static List<PatcherFix> getPatcherFixAncestors(
		PatcherFix patcherFix) {

		Set<PatcherFix> patcherFixAncestors = new HashSet<>();

		List<PatcherFix> parentPatcherFixes = getParentPatcherFixes(patcherFix);

		for (PatcherFix parentPatcherFix : parentPatcherFixes) {
			patcherFixAncestors.add(parentPatcherFix);

			patcherFixAncestors.addAll(
				getPatcherFixAncestors(parentPatcherFix));
		}

		return ListUtil.fromCollection(patcherFixAncestors);
	}

	public static List<PatcherFix> getPatcherFixDescendants(
			PatcherFix patcherFix)
		throws Exception {

		Set<PatcherFix> patcherFixDescendants = new HashSet<>();

		List<PatcherFix> childPatcherFixPatcherFixes =
			getChildPatcherFixPatcherFixes(patcherFix);

		for (PatcherFix childPatcherFixPatcherFix :
				childPatcherFixPatcherFixes) {

			if (childPatcherFixPatcherFix.equals(patcherFix)) {
				throw new Exception(
					"circular-reference-detected-the-" +
						"parent-fix-is-equal-to-the-child-fix");
			}

			patcherFixDescendants.add(childPatcherFixPatcherFix);

			patcherFixDescendants.addAll(
				getPatcherFixDescendants(childPatcherFixPatcherFix));
		}

		return ListUtil.fromCollection(patcherFixDescendants);
	}

	public static boolean hasObsoletePatcherFixAncestor(PatcherFix patcherFix) {
		List<PatcherFix> patcherFixAncestors = getPatcherFixAncestors(
			patcherFix);

		for (PatcherFix patcherFixAncestor : patcherFixAncestors) {
			if (patcherFixAncestor == patcherFix) {
				continue;
			}

			if (patcherFixAncestor.isObsolete()) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasParentPatcherFixes(PatcherFix patcherFix) {
		List<PatcherFix> parentPatcherFixes = getParentPatcherFixes(patcherFix);

		return !parentPatcherFixes.isEmpty();
	}

}