/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceMode;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.service.base.PortletServiceBaseImpl;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
@JSONWebService(mode = JSONWebServiceMode.MANUAL)
public class PortletServiceImpl extends PortletServiceBaseImpl {

	@Override
	public JSONArray getWARPortlets() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		List<Portlet> portlets = portletLocalService.getPortlets();

		for (Portlet portlet : portlets) {
			PortletApp portletApp = portlet.getPortletApp();

			if (!portletApp.isWARFile()) {
				continue;
			}

			jsonArray.put(
				JSONUtil.put(
					"portlet_name", portlet.getPortletName()
				).put(
					"servlet_context_name", portletApp.getServletContextName()
				));
		}

		return jsonArray;
	}

	@JSONWebService
	@Override
	public boolean hasPortlet(long companyId, String portletId) {
		return portletLocalService.hasPortlet(companyId, portletId);
	}

	@Override
	public Portlet updatePortlet(
			long companyId, String portletId, String roles, boolean active)
		throws PortalException {

		if (!_roleLocalService.hasUserRole(
				getUserId(), companyId, RoleConstants.ADMINISTRATOR, true)) {

			throw new PrincipalException();
		}

		return portletLocalService.updatePortlet(
			companyId, portletId, roles, active);
	}

	@BeanReference(type = RoleLocalService.class)
	private RoleLocalService _roleLocalService;

}