/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.blogs.internal.item.action.provider;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.content.dashboard.blogs.internal.item.action.PreviewImageBlogsEntryContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemActionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = ContentDashboardItemActionProvider.class)
public class PreviewImageBlogsEntryContentDashboardItemActionProvider
	implements ContentDashboardItemActionProvider<BlogsEntry> {

	@Override
	public ContentDashboardItemAction getContentDashboardItemAction(
		BlogsEntry blogsEntry, HttpServletRequest httpServletRequest) {

		if (!isShow(blogsEntry, httpServletRequest)) {
			return null;
		}

		InfoItemFieldValuesProvider<BlogsEntry> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, BlogsEntry.class.getName());

		return new PreviewImageBlogsEntryContentDashboardItemAction(
			blogsEntry, infoItemFieldValuesProvider, _language);
	}

	@Override
	public String getKey() {
		return "preview-image";
	}

	@Override
	public ContentDashboardItemAction.Type getType() {
		return ContentDashboardItemAction.Type.PREVIEW_IMAGE;
	}

	@Override
	public boolean isShow(
		BlogsEntry blogsEntry, HttpServletRequest httpServletRequest) {

		if (blogsEntry.isInTrash()) {
			return false;
		}

		InfoItemFieldValuesProvider<BlogsEntry> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, BlogsEntry.class.getName());

		ContentDashboardItemAction contentDashboardItemAction =
			new PreviewImageBlogsEntryContentDashboardItemAction(
				blogsEntry, infoItemFieldValuesProvider, _language);

		return Validator.isNotNull(contentDashboardItemAction.getURL());
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

}