/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.display.context.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.servlet.taglib.ui.JavaScriptMenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.sharing.display.context.util.SharingJavaScriptFactory;
import com.liferay.sharing.display.context.util.SharingMenuItemFactory;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = SharingMenuItemFactory.class)
public class SharingMenuItemFactoryImpl implements SharingMenuItemFactory {

	@Override
	public MenuItem createManageCollaboratorsMenuItem(
			String className, long classPK,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		JavaScriptMenuItem javaScriptMenuItem = new JavaScriptMenuItem();

		javaScriptMenuItem.setKey("#manage-collaborators");
		javaScriptMenuItem.setLabel(
			SharingItemFactoryUtil.getManageCollaboratorsLabel(
				httpServletRequest));
		javaScriptMenuItem.setOnClick(
			_sharingJavaScriptFactory.createManageCollaboratorsOnClickMethod(
				className, classPK, httpServletRequest));

		return javaScriptMenuItem;
	}

	@Override
	public MenuItem createShareMenuItem(
			String className, long classPK,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		JavaScriptMenuItem javaScriptMenuItem = new JavaScriptMenuItem();

		javaScriptMenuItem.setKey("#share");
		javaScriptMenuItem.setLabel(
			SharingItemFactoryUtil.getSharingLabel(httpServletRequest));
		javaScriptMenuItem.setOnClick(
			_sharingJavaScriptFactory.createSharingOnClickMethod(
				className, classPK, httpServletRequest));

		return javaScriptMenuItem;
	}

	@Reference
	private SharingJavaScriptFactory _sharingJavaScriptFactory;

}