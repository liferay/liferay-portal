/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.filter.provider;

import com.liferay.content.dashboard.document.library.internal.item.filter.FileExtensionContentDashboardItemFilter;
import com.liferay.content.dashboard.item.action.exception.ContentDashboardItemActionException;
import com.liferay.content.dashboard.item.filter.ContentDashboardItemFilter;
import com.liferay.content.dashboard.item.filter.provider.ContentDashboardItemFilterProvider;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	property = "service.ranking:Integer=140",
	service = ContentDashboardItemFilterProvider.class
)
public class FileExtensionContentDashboardItemFilterProvider
	implements ContentDashboardItemFilterProvider {

	@Override
	public ContentDashboardItemFilter getContentDashboardItemFilter(
			HttpServletRequest httpServletRequest)
		throws ContentDashboardItemActionException {

		return new FileExtensionContentDashboardItemFilter(
			httpServletRequest, _itemSelector, _language, _portal);
	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	public ContentDashboardItemFilter.Type getType() {
		return ContentDashboardItemFilter.Type.ITEM_SELECTOR;
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest) {
		return true;
	}

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}