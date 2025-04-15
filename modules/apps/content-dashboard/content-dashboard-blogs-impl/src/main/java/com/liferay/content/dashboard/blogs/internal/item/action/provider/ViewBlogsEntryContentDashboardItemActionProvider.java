/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.blogs.internal.item.action.provider;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.content.dashboard.blogs.internal.item.action.ViewBlogsEntryContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemActionProvider;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = ContentDashboardItemActionProvider.class)
public class ViewBlogsEntryContentDashboardItemActionProvider
	implements ContentDashboardItemActionProvider<BlogsEntry> {

	@Override
	public ContentDashboardItemAction getContentDashboardItemAction(
		BlogsEntry blogsEntry, HttpServletRequest httpServletRequest) {

		if (!isShow(blogsEntry, httpServletRequest)) {
			return null;
		}

		return _getContentDashboardItemAction(httpServletRequest, blogsEntry);
	}

	@Override
	public String getKey() {
		return "view";
	}

	@Override
	public ContentDashboardItemAction.Type getType() {
		return ContentDashboardItemAction.Type.VIEW;
	}

	@Override
	public boolean isShow(
		BlogsEntry blogsEntry, HttpServletRequest httpServletRequest) {

		if (blogsEntry.isDraft() || blogsEntry.isInTrash()) {
			return false;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			_getContentDashboardItemAction(httpServletRequest, blogsEntry);

		return Validator.isNotNull(contentDashboardItemAction.getURL());
	}

	private ContentDashboardItemAction _getContentDashboardItemAction(
		HttpServletRequest httpServletRequest, BlogsEntry blogsEntry) {

		return new ViewBlogsEntryContentDashboardItemAction(
			_assetDisplayPageFriendlyURLProvider, blogsEntry,
			httpServletRequest, _language);
	}

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private Language _language;

}