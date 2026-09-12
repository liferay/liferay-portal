/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.model.impl;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.view.count.ViewCountManagerUtil;
import com.liferay.portlet.asset.util.DeletedAssetObjectThreadLocal;

import java.util.Collections;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Juan Fernández
 */
public class AssetEntryImpl extends AssetEntryBaseImpl {

	@Override
	public AssetRenderer<?> getAssetRenderer() {
		if (DeletedAssetObjectThreadLocal.isDeletedAssetObject(
				getClassNameId(), getClassPK())) {

			return null;
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				getClassName());

		try {
			return assetRendererFactory.getAssetRenderer(getClassPK());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get asset renderer", exception);
			}
		}

		return null;
	}

	@Override
	public AssetRendererFactory<?> getAssetRendererFactory() {
		return AssetRendererFactoryRegistryUtil.
			getAssetRendererFactoryByClassName(getClassName());
	}

	@Override
	public List<AssetCategory> getCategories() {
		return AssetCategoryLocalServiceUtil.getEntryCategories(getEntryId());
	}

	@Override
	public long[] getCategoryIds() {
		return ListUtil.toLongArray(
			getCategories(), AssetCategory.CATEGORY_ID_ACCESSOR);
	}

	@Override
	public String[] getTagNames() {
		return ListUtil.toArray(getTags(), AssetTag.NAME_ACCESSOR);
	}

	@Override
	public List<AssetTag> getTags() {
		if (AssetTagLocalServiceUtil.getCompanyTagsCount(getCompanyId()) == 0) {
			return Collections.emptyList();
		}

		return AssetTagLocalServiceUtil.getEntryTags(getEntryId());
	}

	@Override
	public long getViewCount() {
		return ViewCountManagerUtil.getViewCount(
			getCompanyId(),
			ClassNameLocalServiceUtil.getClassNameId(AssetEntry.class),
			getPrimaryKey());
	}

	private static final Log _log = LogFactoryUtil.getLog(AssetEntryImpl.class);

}