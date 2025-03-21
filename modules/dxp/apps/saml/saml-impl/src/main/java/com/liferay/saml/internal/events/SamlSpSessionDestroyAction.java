/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.internal.events;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.SessionAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.saml.persistence.model.SamlSpSession;
import com.liferay.saml.persistence.service.SamlSpSessionLocalService;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(
	property = "key=" + PropsKeys.SERVLET_SESSION_DESTROY_EVENTS,
	service = LifecycleAction.class
)
public class SamlSpSessionDestroyAction extends SessionAction {

	@Override
	public void run(HttpSession httpSession) throws ActionException {
		Long userId = (Long)httpSession.getAttribute(WebKeys.USER_ID);

		if (userId == null) {
			return;
		}

		long userCompanyId = 0;

		try {
			userCompanyId = _companyLocalService.getCompanyIdByUserId(userId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		if (userCompanyId == 0) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					userCompanyId)) {

			_run(httpSession);
		}
	}

	private void _run(HttpSession httpSession) throws ActionException {
		if (!_samlProviderConfigurationHelper.isEnabled() ||
			!_samlProviderConfigurationHelper.isRoleSp()) {

			return;
		}

		SamlSpSession samlSpSession =
			_samlSpSessionLocalService.fetchSamlSpSessionByJSessionId(
				httpSession.getId());

		if (samlSpSession == null) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"HTTP session expiring SAML SP session " +
					samlSpSession.getSamlSpSessionKey());
		}

		try {
			_samlSpSessionLocalService.deleteSamlSpSession(
				samlSpSession.getSamlSpSessionId());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SamlSpSessionDestroyAction.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	@Reference
	private SamlSpSessionLocalService _samlSpSessionLocalService;

}