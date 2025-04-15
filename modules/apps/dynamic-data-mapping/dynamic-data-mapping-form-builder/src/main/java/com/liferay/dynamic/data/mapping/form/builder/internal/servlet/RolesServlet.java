/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.builder.internal.servlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"dynamic.data.mapping.form.builder.servlet=true",
		"osgi.http.whiteboard.context.path=/dynamic-data-mapping-form-builder-roles",
		"osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.builder.internal.servlet.RolesServlet",
		"osgi.http.whiteboard.servlet.pattern=/dynamic-data-mapping-form-builder-roles/*"
	},
	service = Servlet.class
)
public class RolesServlet extends BaseDDMFormBuilderServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		JSONArray jsonArray = _getRolesJSONArray();

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		ServletResponseUtil.write(httpServletResponse, jsonArray.toString());
	}

	protected JSONObject toJSONObject(Role role) {
		JSONObject jsonObject = _jsonFactory.createJSONObject();

		jsonObject.put(
			"id", role.getRoleId()
		).put(
			"name", role.getName()
		);

		return jsonObject;
	}

	private JSONArray _getRolesJSONArray() {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		try {
			List<Role> roles = _roleService.getRoles(
				CompanyThreadLocal.getCompanyId(),
				new int[] {
					RoleConstants.TYPE_ORGANIZATION, RoleConstants.TYPE_REGULAR,
					RoleConstants.TYPE_SITE
				});

			for (Role role : roles) {
				jsonArray.put(toJSONObject(role));
			}

			return jsonArray;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return jsonArray;
	}

	private static final Log _log = LogFactoryUtil.getLog(RolesServlet.class);

	private static final long serialVersionUID = 1L;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private RoleService _roleService;

}