/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.BasePortletProvider;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashPortletKeys;
import com.liferay.trash.model.TrashEntry;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides an implementation of <code>ViewPortletProvider</code> (in
 * <code>com.liferay.portal.kernel</code>) for the Recycle Bin portlet. This
 * implementation is aimed to generate instances of <code>PortletURL</code> (in
 * <code>jakarta.portlet</code> entities to provide URLs to view Recycle Bin
 * entries.
 *
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"model.class.name=com.liferay.trash.kernel.model.TrashEntry",
		"model.class.name=com.liferay.trash.model.TrashEntry"
	},
	service = PortletProvider.class
)
public class TrashViewPortletProvider extends BasePortletProvider {

	@Override
	public String getPortletName() {
		return TrashPortletKeys.TRASH;
	}

	@Override
	public PortletURL getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String portletId = PortletProviderUtil.getPortletId(
			TrashEntry.class.getName(), PortletProvider.Action.VIEW);

		if (!themeDisplay.isSignedIn() ||
			!_trashHelper.isTrashEnabled(themeDisplay.getScopeGroupId()) ||
			!PortletPermissionUtil.hasControlPanelAccessPermission(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), portletId)) {

			return null;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, portletId, PortletRequest.RENDER_PHASE)
		).setRedirect(
			themeDisplay.getURLCurrent()
		).buildPortletURL();
	}

	@Override
	public Action[] getSupportedActions() {
		return _supportedActions;
	}

	@Reference
	private Portal _portal;

	private final Action[] _supportedActions = {Action.VIEW};

	@Reference
	private TrashHelper _trashHelper;

}