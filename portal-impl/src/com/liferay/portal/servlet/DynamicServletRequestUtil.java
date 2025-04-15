/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class DynamicServletRequestUtil {

	public static HttpServletRequest createDynamicServletRequest(
		HttpServletRequest httpServletRequest, Portlet portlet,
		Map<String, String[]> parameterMap, boolean mergeParameters) {

		DynamicServletRequest dynamicServletRequest = null;

		if (portlet.isPrivateRequestAttributes()) {
			String portletNamespace = PortalUtil.getPortletNamespace(
				portlet.getPortletName());

			dynamicServletRequest = new NamespaceServletRequest(
				httpServletRequest, portletNamespace, portletNamespace);
		}
		else {
			dynamicServletRequest = new DynamicServletRequest(
				httpServletRequest);
		}

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String name = entry.getKey();

			String[] values = entry.getValue();

			String[] oldValues = dynamicServletRequest.getParameterValues(name);

			if (mergeParameters && (oldValues != null)) {
				values = ArrayUtil.append(values, oldValues);
			}

			dynamicServletRequest.setParameterValues(name, values);
		}

		return dynamicServletRequest;
	}

}