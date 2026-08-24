/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.events;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.fips.configuration.FIPSSessionConfiguration;
import com.liferay.portal.security.fips.constants.FIPSConstants;
import com.liferay.portal.security.fips.util.FIPSUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Manuele Castro
 */
@Component(property = "key=login.events.post", service = LifecycleAction.class)
public class LoginPostAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		if (!PropsValues.FIPS_ENABLED) {
			return;
		}

		try {
			FIPSSessionConfiguration fipsSessionConfiguration =
				_configurationProvider.getCompanyConfiguration(
					FIPSSessionConfiguration.class,
					_portal.getCompanyId(httpServletRequest));

			HttpSession httpSession = httpServletRequest.getSession();

			long maximumAgeMinutes = FIPSUtil.toMinutes(
				fipsSessionConfiguration.maximumAge(),
				fipsSessionConfiguration.maximumAgeTimeUnit());

			httpSession.setAttribute(
				FIPSConstants.FIPS_SESSION_MAXIMUM_AGE,
				System.currentTimeMillis() + (maximumAgeMinutes * Time.MINUTE));

			long idleTimeoutMinutes = FIPSUtil.toMinutes(
				fipsSessionConfiguration.idleTimeout(),
				fipsSessionConfiguration.idleTimeoutTimeUnit());

			httpSession.setMaxInactiveInterval((int)idleTimeoutMinutes * 60);
		}
		catch (Exception exception) {
			throw new ActionException(exception);
		}
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

}