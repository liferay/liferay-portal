/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.model;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.impl.DefaultLayoutTypeAccessPolicyImpl;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;

import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
public abstract class BaseLayoutTypeAccessPolicy
	extends DefaultLayoutTypeAccessPolicyImpl {

	@Override
	protected boolean hasAccessPermission(
			HttpServletRequest httpServletRequest, Layout layout,
			Portlet portlet)
		throws PortalException {

		if (layout.getMasterLayoutPlid() == 0) {
			return super.hasAccessPermission(
				httpServletRequest, layout, portlet);
		}

		Layout masterLayout = layoutLocalService.fetchLayout(
			layout.getMasterLayoutPlid());

		if (masterLayout == null) {
			return super.hasAccessPermission(
				httpServletRequest, layout, portlet);
		}

		PortletPreferences portletPreferences =
			portletPreferencesLocalService.fetchPreferences(
				portletPreferencesFactory.getPortletPreferencesIds(
					httpServletRequest, masterLayout, portlet.getPortletId()));

		if (portletPreferences == null) {
			return super.hasAccessPermission(
				httpServletRequest, layout, portlet);
		}

		return super.hasAccessPermission(
			httpServletRequest, masterLayout, portlet);
	}

	@Reference
	protected LayoutLocalService layoutLocalService;

	@Reference
	protected PortletPreferencesFactory portletPreferencesFactory;

	@Reference
	protected PortletPreferencesLocalService portletPreferencesLocalService;

}