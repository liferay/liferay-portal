/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.servlet.filter;

import com.liferay.commerce.context.CommerceGroupThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = {
		"servlet-context-name=", "servlet-filter-name=Commerce Context Filter",
		"url-pattern=/o/frontend-taglib-clay/app/*",
		"url-pattern=/o/headless-batch-engine/*",
		"url-pattern=/o/headless-commerce-admin-account/*",
		"url-pattern=/o/headless-commerce-admin-catalog/*",
		"url-pattern=/o/headless-commerce-admin-channel/*",
		"url-pattern=/o/headless-commerce-admin-inventory/*",
		"url-pattern=/o/headless-commerce-admin-order/*",
		"url-pattern=/o/headless-commerce-admin-pricing/*",
		"url-pattern=/o/headless-commerce-admin-shipment/*",
		"url-pattern=/o/headless-commerce-admin-site-setting/*",
		"url-pattern=/o/headless-commerce-delivery-cart/*",
		"url-pattern=/o/headless-commerce-delivery-catalog/*",
		"url-pattern=/o/headless-commerce-machine-learning/*",
		"url-pattern=/o/headless-commerce-punchout/*"
	},
	service = Filter.class
)
public class CommerceContextFilter extends BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		try {
			HttpSession httpSession = httpServletRequest.getSession();

			long groupId = GetterUtil.getLong(
				httpSession.getAttribute(WebKeys.VISITED_GROUP_ID_RECENT));

			CommerceGroupThreadLocal.set(
				_groupLocalService.fetchGroup(groupId));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceContextFilter.class);

	@Reference
	private GroupLocalService _groupLocalService;

}