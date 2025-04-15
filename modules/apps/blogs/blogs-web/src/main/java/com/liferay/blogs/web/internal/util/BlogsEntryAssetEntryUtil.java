/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.web.internal.constants.BlogsWebConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alejandro Tardín
 */
public class BlogsEntryAssetEntryUtil {

	public static AssetEntry getAssetEntry(
			HttpServletRequest httpServletRequest, BlogsEntry blogsEntry)
		throws PortalException {

		String key =
			BlogsWebConstants.BLOGS_ENTRY_ASSET_ENTRY + StringPool.UNDERLINE +
				blogsEntry.getEntryId();

		AssetEntry assetEntry = (AssetEntry)httpServletRequest.getAttribute(
			key);

		if (assetEntry == null) {
			AssetEntryLocalService assetEntryLocalService =
				_assetEntryLocalServiceSnapshot.get();

			assetEntry = assetEntryLocalService.getEntry(
				BlogsEntry.class.getName(), blogsEntry.getEntryId());

			httpServletRequest.setAttribute(key, assetEntry);
		}

		return assetEntry;
	}

	private static final Snapshot<AssetEntryLocalService>
		_assetEntryLocalServiceSnapshot = new Snapshot<>(
			BlogsEntryAssetEntryUtil.class, AssetEntryLocalService.class);

}