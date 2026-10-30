/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.model.impl;

import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryAudienceEntryRelLocalServiceUtil;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Locale;

/**
 * @author Pavel Savinov
 */
public class AssetListEntryImpl extends AssetListEntryBaseImpl {

	@Override
	public String getTypeLabel() {
		return AssetListEntryTypeConstants.getTypeLabel(getType());
	}

	@Override
	public String getTypeSettings(long segmentsEntryId) {
		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			AssetListEntrySegmentsEntryRelLocalServiceUtil.
				fetchAssetListEntrySegmentsEntryRel(
					getAssetListEntryId(), segmentsEntryId);

		if (assetListEntrySegmentsEntryRel != null) {
			return assetListEntrySegmentsEntryRel.getTypeSettings();
		}

		return null;
	}

	@Override
	public String getTypeSettings(String audienceEntryERC) {
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel =
			AssetListEntryAudienceEntryRelLocalServiceUtil.
				fetchAssetListEntryAudienceEntryRel(
					getAssetListEntryId(), audienceEntryERC);

		if (assetListEntryAudienceEntryRel != null) {
			return assetListEntryAudienceEntryRel.getTypeSettings();
		}

		return null;
	}

	@Override
	public String getUnambiguousTitle(Locale locale) throws PortalException {
		Group group = GroupLocalServiceUtil.getGroup(getGroupId());

		return StringUtil.appendParentheticalSuffix(
			getTitle(), group.getName(locale));
	}

}