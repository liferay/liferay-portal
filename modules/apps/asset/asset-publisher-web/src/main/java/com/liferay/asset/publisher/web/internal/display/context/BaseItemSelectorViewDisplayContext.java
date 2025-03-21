/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.display.context;

import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseItemSelectorViewDisplayContext
	implements ItemSelectorViewDisplayContext {

	public BaseItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest,
		AssetPublisherHelper assetPublisherHelper, PortletURL portletURL) {

		_assetPublisherHelper = assetPublisherHelper;

		this.httpServletRequest = httpServletRequest;
		this.portletURL = portletURL;
	}

	public long getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		_groupId = ParamUtil.getLong(httpServletRequest, "groupId");

		return _groupId;
	}

	@Override
	public long[] getSelectedGroupIds() {
		long plid = ParamUtil.getLong(httpServletRequest, "plid");

		Layout layout = LayoutLocalServiceUtil.fetchLayout(plid);

		if (layout == null) {
			return new long[0];
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String portletResource = ParamUtil.getString(
			httpServletRequest, "portletResource");

		PortletPreferences portletPreferences =
			themeDisplay.getStrictLayoutPortletSetup(layout, portletResource);

		return _assetPublisherHelper.getGroupIds(
			portletPreferences, themeDisplay.getScopeGroupId(),
			themeDisplay.getLayout());
	}

	@Override
	public boolean isShowSearch() {
		return true;
	}

	protected PortletRequest getPortletRequest() {
		return (PortletRequest)httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);
	}

	protected final HttpServletRequest httpServletRequest;
	protected final PortletURL portletURL;

	private final AssetPublisherHelper _assetPublisherHelper;
	private Long _groupId;

}