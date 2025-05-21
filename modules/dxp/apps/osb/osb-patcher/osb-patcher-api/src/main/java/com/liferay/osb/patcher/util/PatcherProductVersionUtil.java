/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.AlloyServiceInvoker;
import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author Zsolt Balogh
 */
public class PatcherProductVersionUtil {

	public static PatcherProductVersion fetchPatcherProductVersion(String name)
		throws Exception {

		AlloyServiceInvoker patcherProductVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProductVersion.class.getName());

		List<PatcherProductVersion> patcherProductVersions =
			patcherProductVersionAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {"name", name});

		if (!patcherProductVersions.isEmpty()) {
			return patcherProductVersions.get(0);
		}

		return null;
	}

	public static String fetchPatcherProductVersionName(
			long patcherProductVersionId)
		throws Exception {

		PatcherProductVersion patcherProductVersion =
			PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(
				patcherProductVersionId);

		if (patcherProductVersion != null) {
			return patcherProductVersion.getName();
		}

		return StringPool.BLANK;
	}

	public static List<Long> getMarketplaceReleasePatcherProductVersionIds()
		throws Exception {

		AlloyServiceInvoker patcherProductVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProductVersion.class.getName());

		List<BaseModel> patcherProductVersions =
			patcherProductVersionAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {
					"fixDeliveryMethod",
					PatcherProductVersionConstants.
						TYPE_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE
				});

		return BaseModelUtil.fetchBaseModelsPrimaryIds(patcherProductVersions);
	}

	public static long getPatcherProductVersionId(String name)
		throws Exception {

		AlloyServiceInvoker patcherProductVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProductVersion.class.getName());

		List<PatcherProductVersion> patcherProductVersions =
			patcherProductVersionAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {"name", name});

		PatcherProductVersion patcherProductVersion =
			patcherProductVersions.get(0);

		return patcherProductVersion.getPatcherProductVersionId();
	}

	public static List<PatcherProductVersion> getPatcherProductVersions()
		throws Exception {

		List<PatcherProductVersion> patcherProductVersions =
			PatcherProductVersionLocalServiceUtil.getPatcherProductVersions(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(
			PatcherProductVersionModelImpl.TABLE_NAME, "name", true);

		return ListUtil.sort(patcherProductVersions, obc);
	}

	public static List<PatcherProductVersion> getPatcherProductVersions(
			int fixDeliveryMethod)
		throws Exception {

		AlloyServiceInvoker patcherProductVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProductVersion.class.getName());

		return patcherProductVersionAlloyServiceInvoker.executeDynamicQuery(
			new Object[] {"fixDeliveryMethod", fixDeliveryMethod});
	}

	public static List<PatcherProductVersion> getPatcherProductVersions(
			PatcherAccount patcherAccount)
		throws Exception {

		AlloyServiceInvoker patcherProductVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProductVersion.class.getName());

		DynamicQuery patcherProductVersionDynamicQuery =
			patcherProductVersionAlloyServiceInvoker.buildDynamicQuery();

		Property patcherProductVersionIdProperty = PropertyFactoryUtil.forName(
			"patcherProductVersionId");

		AlloyServiceInvoker patcherBuildAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherBuild.class.getName());

		DynamicQuery patcherBuildDynamicQuery =
			patcherBuildAlloyServiceInvoker.buildDynamicQuery();

		Property patcherBuildIdProperty = PropertyFactoryUtil.forName(
			"patcherBuildId");

		List<Long> patcherAccountPatcherBuildIds =
			PatcherBuildUtil.getPatcherAccountPatcherBuildIds(
				patcherAccount.getPatcherAccountId());

		if (patcherAccountPatcherBuildIds.isEmpty()) {
			return Collections.emptyList();
		}

		patcherBuildDynamicQuery.add(
			patcherBuildIdProperty.in(patcherAccountPatcherBuildIds));

		Projection distinctPatcherProductVersionIdProjection =
			ProjectionFactoryUtil.distinct(
				ProjectionFactoryUtil.property("patcherProductVersionId"));

		patcherBuildDynamicQuery.setProjection(
			distinctPatcherProductVersionIdProjection);

		patcherProductVersionDynamicQuery.add(
			patcherProductVersionIdProperty.in(patcherBuildDynamicQuery));

		return patcherProductVersionAlloyServiceInvoker.executeDynamicQuery(
			patcherProductVersionDynamicQuery);
	}

	public static boolean isMarketplaceAppProduct(long patcherProductVersionId)
		throws Exception {

		PatcherProductVersion patcherProductVersion =
			PatcherProductVersionLocalServiceUtil.getPatcherProductVersion(
				patcherProductVersionId);

		if (patcherProductVersion.getFixDeliveryMethod() ==
				PatcherProductVersionConstants.
					TYPE_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE) {

			return true;
		}

		return false;
	}

}