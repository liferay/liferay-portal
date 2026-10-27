/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.servlet.filter;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Buarque
 */
@Component(
	property = {
		"before-filter=URL Rewrite Filter", "dispatcher=FORWARD",
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Faro Project Filter", "url-pattern=/web/guest/*",
		"url-pattern=/workspace/*"
	},
	service = Filter.class
)
public class FaroProjectFilter extends BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		String groupKey = _getGroupKey(httpServletRequest);

		if (groupKey != null) {
			FaroProject faroProject = _fetchFaroProject(groupKey);

			if (faroProject == null) {
				httpServletResponse.sendRedirect(StringPool.FORWARD_SLASH);

				return;
			}

			if (!Objects.equals(
					faroProject.getSubscriptionName(),
					ProductConstants.
						PRODUCT_ENTRY_NAME_DATA_PLATFORM_PRIVATE_BETA)) {

				filterChain.doFilter(httpServletRequest, httpServletResponse);

				return;
			}

			if ((faroProject.getIndividualsCountSinceLastAnniversary() >=
					faroProject.getIndividualsLimit()) ||
				(faroProject.getPageViewsCountSinceLastAnniversary() >=
					faroProject.getPageViewsLimit())) {

				httpServletResponse.sendRedirect(StringPool.FORWARD_SLASH);

				return;
			}
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	private FaroProject _fetchFaroProject(String groupKey) {
		long groupId = 0;

		Group group = _groupLocalService.fetchFriendlyURLGroup(
			_portal.getDefaultCompanyId(), StringPool.SLASH + groupKey);

		if (group == null) {
			groupId = GetterUtil.getLong(groupKey);
		}
		else {
			groupId = group.getGroupId();
		}

		return _faroProjectLocalService.fetchFaroProjectByGroupId(groupId);
	}

	private String _getGroupKey(HttpServletRequest httpServletRequest) {
		String uri = httpServletRequest.getRequestURI();

		Matcher matcher = _pattern.matcher(uri);

		if (matcher.find()) {
			return matcher.group(1);
		}

		String redirect = httpServletRequest.getParameter("redirect");

		if (redirect != null) {
			matcher = _pattern.matcher(redirect);

			if (matcher.find()) {
				return matcher.group(1);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroProjectFilter.class);

	private static final Pattern _pattern = Pattern.compile(
		"/workspace/([^/]+).*");

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}