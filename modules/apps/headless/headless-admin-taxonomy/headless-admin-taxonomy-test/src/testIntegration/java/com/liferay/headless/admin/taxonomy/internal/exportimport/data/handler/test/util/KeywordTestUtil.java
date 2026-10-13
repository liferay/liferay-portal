/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.exportimport.data.handler.test.util;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Date;

/**
 * @author Alberto Javier Moreno Lage
 */
public class KeywordTestUtil {

	public static AssetTag updateAssetTag(AssetTag assetTag) throws Exception {
		return AssetTagLocalServiceUtil.updateTag(
			assetTag.getExternalReferenceCode(), assetTag.getUserId(),
			assetTag.getTagId(),
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			ServiceContextTestUtil.getServiceContext(
				assetTag.getGroupId(), assetTag.getUserId()));
	}

	public static AssetTag updateAssetTagCreatorUserId(
		AssetTag assetTag, long userId) {

		assetTag.setUserId(userId);

		return AssetTagLocalServiceUtil.updateAssetTag(assetTag);
	}

	public static AssetTag updateAssetTagModifiedDate(
		AssetTag assetTag, Date modifiedDate) {

		assetTag.setModifiedDate(modifiedDate);

		return AssetTagLocalServiceUtil.updateAssetTag(assetTag);
	}

}