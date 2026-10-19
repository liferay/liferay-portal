/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.llms.web.internal.struts;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.configuration.manager.LLMSConfigurationManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jonathan McCann
 */
@Component(
	property = {"layout.required=false", "path=/portal/llms"},
	service = StrutsAction.class
)
public class LLMSStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			Company company = _portal.getCompany(httpServletRequest);

			if (!FeatureFlagManagerUtil.isEnabled(
					company.getCompanyId(), "LPD-80518")) {

				httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

				return null;
			}

			boolean enabled = false;
			String content = null;

			VirtualHost virtualHost = _virtualHostLocalService.fetchVirtualHost(
				GetterUtil.getString(
					_portal.getForwardedHost(httpServletRequest)));

			if ((virtualHost != null) && (virtualHost.getLayoutSetId() > 0)) {
				LayoutSet layoutSet = _layoutSetLocalService.fetchLayoutSet(
					virtualHost.getLayoutSetId());

				if (layoutSet != null) {
					enabled = _llmsConfigurationManager.isGroupEnabled(
						layoutSet.getCompanyId(), layoutSet.getGroupId());
					content = _llmsConfigurationManager.getGroupContent(
						layoutSet.getCompanyId(), layoutSet.getGroupId());
				}
			}
			else {
				enabled = _llmsConfigurationManager.isCompanyEnabled(
					company.getCompanyId());
				content = _llmsConfigurationManager.getCompanyContent(
					company.getCompanyId());
			}

			if (!enabled) {
				httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

				return null;
			}

			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse, null,
				content.getBytes(StringPool.UTF8),
				ContentTypes.TEXT_PLAIN_UTF8);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			_portal.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception,
				httpServletRequest, httpServletResponse);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LLMSStrutsAction.class);

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private LLMSConfigurationManager _llmsConfigurationManager;

	@Reference
	private Portal _portal;

	@Reference
	private VirtualHostLocalService _virtualHostLocalService;

}