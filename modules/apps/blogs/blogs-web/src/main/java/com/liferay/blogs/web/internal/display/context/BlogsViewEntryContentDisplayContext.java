/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.display.context;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

/**
 * @author Roberto Díaz
 */
public class BlogsViewEntryContentDisplayContext {

	public BlogsViewEntryContentDisplayContext(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getViewEntryURL(BlogsEntry entry) throws PortalException {
		String friendlyURL =
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					BlogsEntry.class.getName(),
					new ClassPKInfoItemIdentifier(entry.getEntryId())),
				_themeDisplay);

		if (friendlyURL != null) {
			return friendlyURL;
		}

		ObjectValuePair<String, String> objectValuePair = null;

		if (Validator.isNotNull(entry.getUrlTitle())) {
			objectValuePair = new ObjectValuePair<>(
				"urlTitle", entry.getUrlTitle());
		}
		else {
			objectValuePair = new ObjectValuePair<>(
				"entryId", String.valueOf(entry.getEntryId()));
		}

		return PortletURLBuilder.createLiferayPortletURL(
			_liferayPortletResponse, PortletRequest.RENDER_PHASE
		).setMVCRenderCommandName(
			"/blogs/view_entry"
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).setParameter(
			objectValuePair.getKey(), objectValuePair.getValue()
		).buildString();
	}

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}