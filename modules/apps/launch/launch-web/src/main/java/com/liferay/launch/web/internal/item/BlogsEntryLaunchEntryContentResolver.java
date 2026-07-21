/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.item;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.util.AssetRendererFactoryLookup;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Blogs entries have no version history (unlike Journal articles or Document
 * Library files), so classVersion is ignored and this always resolves the
 * entry's current state.
 *
 * @author David Truong
 */
@Component(
	property = "launch.entry.content.resolver.class.name=com.liferay.blogs.model.BlogsEntry",
	service = LaunchEntryContentResolver.class
)
public class BlogsEntryLaunchEntryContentResolver
	implements LaunchEntryContentResolver {

	@Override
	public LaunchEntryContent resolve(
			long classPK, String classVersion, Locale locale)
		throws PortalException {

		BlogsEntry blogsEntry = _blogsEntryLocalService.getBlogsEntry(classPK);

		AssetRendererFactory<?> assetRendererFactory =
			_assetRendererFactoryLookup.getAssetRendererFactoryByClassName(
				BlogsEntry.class.getName());

		return new LaunchEntryContent(
			blogsEntry.getGroupId(), blogsEntry.getModifiedDate(),
			blogsEntry.getStatus(), blogsEntry.getTitle(),
			assetRendererFactory.getTypeName(locale), blogsEntry.getUserName());
	}

	@Reference
	private AssetRendererFactoryLookup _assetRendererFactoryLookup;

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

}