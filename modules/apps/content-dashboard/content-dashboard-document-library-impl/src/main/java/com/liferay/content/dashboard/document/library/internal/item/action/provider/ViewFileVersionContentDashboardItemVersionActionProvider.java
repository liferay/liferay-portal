/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.action.provider;

import com.liferay.content.dashboard.document.library.internal.item.action.ViewFileVersionContentDashboardItemVersionAction;
import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemVersionActionProvider;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "service.ranking:Integer=200",
	service = ContentDashboardItemVersionActionProvider.class
)
public class ViewFileVersionContentDashboardItemVersionActionProvider
	implements ContentDashboardItemVersionActionProvider<FileVersion> {

	@Override
	public ContentDashboardItemVersionAction
		getContentDashboardItemVersionAction(
			FileVersion fileVersion, HttpServletRequest httpServletRequest) {

		if (!isShow(fileVersion, httpServletRequest)) {
			return null;
		}

		return new ViewFileVersionContentDashboardItemVersionAction(
			fileVersion, httpServletRequest, _language, _portal,
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest));
	}

	@Override
	public boolean isShow(
		FileVersion fileVersion, HttpServletRequest httpServletRequest) {

		return true;
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}