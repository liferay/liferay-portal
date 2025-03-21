/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.servlet.taglib;

import com.liferay.portal.kernel.exception.ContactNameException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserEmailAddressException.MustNotUseCompanyMx;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.exception.AuthnAgeException;
import com.liferay.saml.runtime.exception.SubjectException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(service = DynamicInclude.class)
public class SamlBottomJSPDynamicInclude extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		SamlProviderConfiguration samlProviderConfiguration =
			_samlProviderConfigurationHelper.getSamlProviderConfiguration();

		if (!samlProviderConfiguration.enabled()) {
			return;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		String error = (String)httpSession.getAttribute(
			SamlWebKeys.SAML_SSO_ERROR);
		String samlSsoErrorEntityId = (String)httpSession.getAttribute(
			com.liferay.saml.web.internal.constants.SamlWebKeys.
				SAML_SSO_ERROR_ENTITY_ID);
		String samlSubjectNameId = (String)httpSession.getAttribute(
			SamlWebKeys.SAML_SUBJECT_NAME_ID);

		httpSession.removeAttribute(SamlWebKeys.SAML_SSO_ERROR);
		httpSession.removeAttribute(
			com.liferay.saml.web.internal.constants.SamlWebKeys.
				SAML_SSO_ERROR_ENTITY_ID);
		httpSession.removeAttribute(SamlWebKeys.SAML_SUBJECT_NAME_ID);

		if (Validator.isBlank(error) ||
			Validator.isBlank(samlSsoErrorEntityId) ||
			Validator.isBlank(samlSubjectNameId)) {

			return;
		}

		if (ArrayUtil.contains(_ERRORS, error)) {
			SessionMessages.add(httpServletRequest, error);
		}

		httpServletRequest.setAttribute(
			com.liferay.saml.web.internal.constants.SamlWebKeys.
				SAML_SSO_ERROR_ENTITY_ID,
			samlSsoErrorEntityId);
		httpServletRequest.setAttribute(
			SamlWebKeys.SAML_SUBJECT_NAME_ID, samlSubjectNameId);

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	public void register(
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry) {

		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#pre");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/com.liferay.portal/saml_error.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final String[] _ERRORS = {
		AuthnAgeException.class.getSimpleName(),
		ContactNameException.class.getSimpleName(),
		MustNotUseCompanyMx.class.getSimpleName(),
		PrincipalException.MustBeAuthenticated.class.getSimpleName(),
		SubjectException.class.getSimpleName(),
		UserEmailAddressException.class.getSimpleName(),
		UserScreenNameException.class.getSimpleName()
	};

	private static final Log _log = LogFactoryUtil.getLog(
		SamlBottomJSPDynamicInclude.class);

	@Reference
	private Portal _portal;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.saml.web)")
	private ServletContext _servletContext;

}