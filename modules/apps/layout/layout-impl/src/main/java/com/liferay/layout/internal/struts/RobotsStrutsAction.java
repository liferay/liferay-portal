/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.struts;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.util.RobotsUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(property = "path=/portal/robots", service = StrutsAction.class)
public class RobotsStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			String host = GetterUtil.getString(
				_portal.getForwardedHost(httpServletRequest));

			LayoutSet layoutSet = null;

			VirtualHost virtualHost = _virtualHostLocalService.fetchVirtualHost(
				host);

			if ((virtualHost != null) && (virtualHost.getLayoutSetId() > 0)) {
				layoutSet = _layoutSetLocalService.fetchLayoutSet(host);
			}
			else {
				Company company = _portal.getCompany(httpServletRequest);

				if (host.equals(company.getVirtualHostname()) &&
					Validator.isNotNull(
						PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME)) {

					Group defaultGroup = _groupLocalService.getGroup(
						company.getCompanyId(),
						PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);

					layoutSet = defaultGroup.getPublicLayoutSet();
				}
			}

			String robots = RobotsUtil.getRobots(
				layoutSet, httpServletRequest.isSecure());

			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse, null,
				robots.getBytes(StringPool.UTF8), ContentTypes.TEXT_PLAIN_UTF8);
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
		RobotsStrutsAction.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private VirtualHostLocalService _virtualHostLocalService;

}