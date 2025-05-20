/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.AlloyController;
import com.liferay.alloy.mvc.AlloyServiceInvoker;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherBuildRel;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherBuildRelLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zsolt Balogh
 */
public class PatcherBuildRelUtil {

	public static void addPatcherBuildRel(
			AlloyController alloyController, long childPatcherBuildId,
			List<Long> parentPatcherBuildIds)
		throws Exception {

		for (long parentPatcherBuildId : parentPatcherBuildIds) {
			addPatcherBuildRel(
				alloyController, childPatcherBuildId, parentPatcherBuildId);
		}
	}

	public static PatcherBuildRel addPatcherBuildRel(
			AlloyController alloyController, long childPatcherBuildId,
			long parentPatcherBuildId)
		throws Exception {

		PatcherBuildRel patcherBuildRel =
			PatcherBuildRelLocalServiceUtil.createPatcherBuildRel(0);

		alloyController.updateModelIgnoreRequest(
			patcherBuildRel, "childPatcherBuildId", childPatcherBuildId,
			"parentPatcherBuildId", parentPatcherBuildId);

		return patcherBuildRel;
	}

	public static void deletePatcherBuildRelsByChildPatcherBuildId(
			long childPatcherBuildId)
		throws Exception {

		AlloyServiceInvoker patcherBuildRelAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherBuildRel.class.getName());

		List<PatcherBuildRel> patcherBuildRels =
			patcherBuildRelAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {"childPatcherBuildId", childPatcherBuildId});

		for (PatcherBuildRel patcherBuildRel : patcherBuildRels) {
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
			PatcherBuild patcherBuild)
		throws Exception {

		return getPatcherBuilds(
			"parentPatcherBuildId", patcherBuild.getPatcherBuildId(),
			"childPatcherBuildId");
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
			PatcherBuild patcherBuild)
		throws Exception {

		return getPatcherBuilds(
			"childPatcherBuildId", patcherBuild.getPatcherBuildId(),
			"parentPatcherBuildId");
	}

	public static boolean hasChildPatcherBuilds(PatcherBuild patcherBuild)
		throws Exception {

		return hasPatcherBuilds(
			"parentPatcherBuildId", patcherBuild.getPatcherBuildId());
	}

	public static boolean hasParentPatcherBuilds(PatcherBuild patcherBuild)
		throws Exception {

		return hasPatcherBuilds(
			"childPatcherBuildId", patcherBuild.getPatcherBuildId());
	}

	protected static List<PatcherBuild> getPatcherBuilds(
			String propertyName, long propertyValue, String projectionName)
		throws Exception {

		List<PatcherBuild> patcherBuilds = new ArrayList<>();

		AlloyServiceInvoker patcherBuildRelAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherBuildRel.class.getName());

		DynamicQuery patcherBuildRelDynamicQuery =
			patcherBuildRelAlloyServiceInvoker.buildDynamicQuery(
				new Object[] {propertyName, propertyValue});

		Projection patcherBuildProjection = ProjectionFactoryUtil.property(
			projectionName);

		patcherBuildRelDynamicQuery.setProjection(patcherBuildProjection);

		List<Long> patcherBuildIds =
			patcherBuildRelAlloyServiceInvoker.executeDynamicQuery(
				patcherBuildRelDynamicQuery);

		for (long patcherBuildId : patcherBuildIds) {
			patcherBuilds.add(
				PatcherBuildLocalServiceUtil.getPatcherBuild(patcherBuildId));
		}

		return patcherBuilds;
	}

	protected static boolean hasPatcherBuilds(
			String propertyName, long propertyValue)
		throws Exception {

		AlloyServiceInvoker patcherBuildRelAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherBuildRel.class.getName());

		long patcherBuildRelsCount =
			patcherBuildRelAlloyServiceInvoker.executeDynamicQueryCount(
				new Object[] {propertyName, propertyValue});

		if (patcherBuildRelsCount > 0) {
			return true;
		}

		return false;
	}

}