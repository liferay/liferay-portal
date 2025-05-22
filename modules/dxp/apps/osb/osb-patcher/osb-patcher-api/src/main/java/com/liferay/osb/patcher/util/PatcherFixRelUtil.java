/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.AlloyController;
import com.liferay.alloy.mvc.AlloyServiceInvoker;
import com.liferay.compat.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixRel;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixRelLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
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
			AlloyController alloyController, long childPatcherFixId,
			List<Long> parentPatcherFixIds)
		throws Exception {

		for (long parentPatcherFixId : parentPatcherFixIds) {
			addPatcherFixRel(
				alloyController, childPatcherFixId, parentPatcherFixId);
		}
	}

	public static void deletePatcherFixRelsByChildPatcherFixId(
			long childPatcherFixId)
		throws Exception {

		AlloyServiceInvoker patcherFixRelAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFixRel.class.getName());

		List<PatcherFixRel> patcherFixRels =
			patcherFixRelAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {"childPatcherFixId", childPatcherFixId});

		for (PatcherFixRel patcherFixRel : patcherFixRels) {
			PatcherFixRelLocalServiceUtil.deletePatcherFixRel(patcherFixRel);
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

			List<Long> parentPatcherFixIds =
				PatcherFixRelUtil.getParentPatcherFixIds(patcherFixId);

			if ((parentPatcherFixIds.size() != 2) ||
				!allPatcherFixIds.containsAll(parentPatcherFixIds)) {

				continue;
			}

			patcherFix = PatcherBuildUtil.getLatestPatcherFix(patcherFix);

			childPatcherFixIds.add(patcherFix.getPatcherFixId());
		}

		return childPatcherFixIds;
	}

	public static List<Long> getChildPatcherFixIds(long parentPatcherFixId)
		throws Exception {

		AlloyServiceInvoker patcherFixRelAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFixRel.class.getName());

		return patcherFixRelAlloyServiceInvoker.executeDynamicQuery(
			buildChildPatcherFixIdDynamicQuery(parentPatcherFixId));
	}

	public static List<PatcherFix> getChildPatcherFixPatcherFixes(
			PatcherFix parentPatcherFix)
		throws Exception {

		AlloyServiceInvoker patcherFixRelAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFixRel.class.getName());

		List<PatcherFix> patcherFixes = new ArrayList<>();

		List<Long> patcherFixIds =
			patcherFixRelAlloyServiceInvoker.executeDynamicQuery(
				buildChildPatcherFixIdDynamicQuery(
					parentPatcherFix.getPatcherFixId()));

		for (long patcherFixId : patcherFixIds) {
			patcherFixes.add(
				PatcherFixLocalServiceUtil.getPatcherFix(patcherFixId));
		}

		return patcherFixes;
	}

	public static List<PatcherFix> getParentPatcherFixes(
			PatcherFix childPatcherFix)
		throws Exception {

		AlloyServiceInvoker patcherFixRelAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFixRel.class.getName());

		List<PatcherFix> patcherFixes = new ArrayList<>();

		List<Long> patcherFixIds =
			patcherFixRelAlloyServiceInvoker.executeDynamicQuery(
				buildParentPatcherFixIdDynamicQuery(
					childPatcherFix.getPatcherFixId()));

		for (long patcherFixId : patcherFixIds) {
			patcherFixes.add(
				PatcherFixLocalServiceUtil.getPatcherFix(patcherFixId));
		}

		return patcherFixes;
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

			List<Long> parentPatcherFixIdPatcherFixIds =
				PatcherFixRelUtil.getParentPatcherFixIds(patcherFixId);

			if ((parentPatcherFixIdPatcherFixIds.size() != 2) ||
				!allPatcherFixIds.containsAll(
					parentPatcherFixIdPatcherFixIds)) {

				continue;
			}

			parentPatcherFixIds.addAll(parentPatcherFixIdPatcherFixIds);
		}

		return parentPatcherFixIds;
	}

	public static List<Long> getParentPatcherFixIds(long childPatcherFixId)
		throws Exception {

		AlloyServiceInvoker patcherFixRelAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFixRel.class.getName());

		return patcherFixRelAlloyServiceInvoker.executeDynamicQuery(
			buildParentPatcherFixIdDynamicQuery(childPatcherFixId));
	}

	public static List<PatcherFix> getPatcherFixAncestors(PatcherFix patcherFix)
		throws Exception {

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

	public static boolean hasObsoletePatcherFixAncestor(PatcherFix patcherFix)
		throws Exception {

		List<PatcherFix> patcherFixAncestors =
			PatcherFixRelUtil.getPatcherFixAncestors(patcherFix);

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

	public static boolean hasParentPatcherFixes(PatcherFix patcherFix)
		throws Exception {

		List<PatcherFix> parentPatcherFixes = getParentPatcherFixes(patcherFix);

		return !parentPatcherFixes.isEmpty();
	}

	protected static PatcherFixRel addPatcherFixRel(
			AlloyController alloyController, long childPatcherFixId,
			long parentPatcherFixId)
		throws Exception {

		PatcherFixRel patcherFixRel =
			PatcherFixRelLocalServiceUtil.createPatcherFixRel(0);

		patcherFixRel.setChildPatcherFixId(childPatcherFixId);
		patcherFixRel.setParentPatcherFixId(parentPatcherFixId);

		alloyController.updateModelIgnoreRequest(patcherFixRel);

		return patcherFixRel;
	}

	protected static DynamicQuery buildChildPatcherFixIdDynamicQuery(
			long parentPatcherFixId)
		throws Exception {

		AlloyServiceInvoker patcherFixRelAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFixRel.class.getName());

		DynamicQuery patcherFixRelDynamicQuery =
			patcherFixRelAlloyServiceInvoker.buildDynamicQuery(
				new Object[] {"parentPatcherFixId", parentPatcherFixId});

		Projection childPatcherFixIdProjection = ProjectionFactoryUtil.property(
			"childPatcherFixId");

		patcherFixRelDynamicQuery.setProjection(childPatcherFixIdProjection);

		return patcherFixRelDynamicQuery;
	}

	protected static DynamicQuery buildParentPatcherFixIdDynamicQuery(
			long childPatcherFixId)
		throws Exception {

		AlloyServiceInvoker patcherFixRelAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFixRel.class.getName());

		DynamicQuery patcherFixRelDynamicQuery =
			patcherFixRelAlloyServiceInvoker.buildDynamicQuery(
				new Object[] {"childPatcherFixId", childPatcherFixId});

		Projection parentPatcherFixIdProjection =
			ProjectionFactoryUtil.property("parentPatcherFixId");

		patcherFixRelDynamicQuery.setProjection(parentPatcherFixIdProjection);

		return patcherFixRelDynamicQuery;
	}

}