/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.info.display.url.provider;

import com.liferay.asset.info.display.url.provider.AssetInfoEditURLProvider;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.info.display.url.provider.InfoEditURLProvider;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "model.class.name=com.liferay.blogs.model.BlogsEntry",
	service = InfoEditURLProvider.class
)
public class BlogsAssetInfoEditURLProvider
	implements InfoEditURLProvider<BlogsEntry> {

	@Override
	public String getURL(
			BlogsEntry blogsEntry, HttpServletRequest httpServletRequest)
		throws Exception {

		return _assetInfoEditURLProvider.getURL(
			BlogsEntry.class.getName(), blogsEntry.getEntryId(),
			httpServletRequest);
	}

	@Reference
	private AssetInfoEditURLProvider _assetInfoEditURLProvider;

}