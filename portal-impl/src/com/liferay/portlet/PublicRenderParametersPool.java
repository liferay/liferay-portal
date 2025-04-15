/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.servlet.PortalSessionContext;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Young
 */
public class PublicRenderParametersPool {

	public static Map<String, String[]> get(
		HttpServletRequest httpServletRequest, long plid) {

		if (PropsValues.PORTLET_PUBLIC_RENDER_PARAMETER_DISTRIBUTION_LAYOUT) {
			return RenderParametersPool.getOrCreate(
				httpServletRequest, plid, _PUBLIC_RENDER_PARAMETERS);
		}

		HttpSession httpSession = httpServletRequest.getSession();

		HttpSession portalHttpSession = PortalSessionContext.get(
			httpSession.getId());

		if (portalHttpSession != null) {
			httpSession = portalHttpSession;
		}

		Map<Long, Map<String, String[]>> publicRenderParametersPool =
			(Map<Long, Map<String, String[]>>)httpSession.getAttribute(
				WebKeys.PUBLIC_RENDER_PARAMETERS_POOL);

		if (publicRenderParametersPool == null) {
			publicRenderParametersPool = new ConcurrentHashMap<>();

			httpSession.setAttribute(
				WebKeys.PUBLIC_RENDER_PARAMETERS_POOL,
				publicRenderParametersPool);
		}

		try {
			Layout layout = LayoutLocalServiceUtil.getLayout(plid);

			LayoutSet layoutSet = layout.getLayoutSet();

			return publicRenderParametersPool.computeIfAbsent(
				layoutSet.getLayoutSetId(), key -> new HashMap<>());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return new HashMap<>();
		}
	}

	private static final String _PUBLIC_RENDER_PARAMETERS =
		"PUBLIC_RENDER_PARAMETERS";

	private static final Log _log = LogFactoryUtil.getLog(
		PublicRenderParametersPool.class);

}