/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProductVersionModel;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author Zsolt Balogh
 */
public class PatcherProductVersionUtil {

	public static PatcherProductVersion fetchPatcherProductVersion(
		String name) {

		return PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(
			name);
	}

	public static String fetchPatcherProductVersionName(
		long patcherProductVersionId) {

		PatcherProductVersion patcherProductVersion =
			PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(
				patcherProductVersionId);

		if (patcherProductVersion != null) {
			return patcherProductVersion.getName();
		}

		return StringPool.BLANK;
	}

	public static List<Long> getMarketplaceReleasePatcherProductVersionIds() {
		return TransformUtil.transform(
			PatcherProductVersionLocalServiceUtil.getPatcherProductVersions(
				PatcherProductVersionConstants.
					TYPE_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE),
			PatcherProductVersionModel::getPatcherProductVersionId);
	}

	public static long getPatcherProductVersionId(String name) {
		PatcherProductVersion patcherProductVersion =
			PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(
				name);

		return patcherProductVersion.getPatcherProductVersionId();
	}

	public static List<PatcherProductVersion> getPatcherProductVersions() {
		return PatcherProductVersionLocalServiceUtil.
			getPatcherProductVersions();
	}

	public static List<PatcherProductVersion> getPatcherProductVersions(
		int fixDeliveryMethod) {

		return PatcherProductVersionLocalServiceUtil.getPatcherProductVersions(
			fixDeliveryMethod);
	}

	public static List<PatcherProductVersion> getPatcherProductVersions(
			PatcherAccount patcherAccount)
		throws Exception {

		List<Long> patcherAccountPatcherBuildIds =
			PatcherBuildUtil.getPatcherAccountPatcherBuildIds(
				patcherAccount.getPatcherAccountId());

		if (patcherAccountPatcherBuildIds.isEmpty()) {
			return Collections.emptyList();
		}

		DynamicQuery patcherProductVersionDynamicQuery =
			PatcherProductVersionLocalServiceUtil.dynamicQuery();

		Property patcherProductVersionIdProperty = PropertyFactoryUtil.forName(
			"patcherProductVersionId");

		DynamicQuery patcherBuildDynamicQuery =
			PatcherBuildLocalServiceUtil.dynamicQuery();

		Property patcherBuildIdProperty = PropertyFactoryUtil.forName(
			"patcherBuildId");

		patcherBuildDynamicQuery.add(
			patcherBuildIdProperty.in(patcherAccountPatcherBuildIds));

		Projection distinctPatcherProductVersionIdProjection =
			ProjectionFactoryUtil.distinct(
				ProjectionFactoryUtil.property("patcherProductVersionId"));

		patcherBuildDynamicQuery.setProjection(
			distinctPatcherProductVersionIdProjection);

		patcherProductVersionDynamicQuery.add(
			patcherProductVersionIdProperty.in(patcherBuildDynamicQuery));

		return PatcherProductVersionLocalServiceUtil.dynamicQuery(
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