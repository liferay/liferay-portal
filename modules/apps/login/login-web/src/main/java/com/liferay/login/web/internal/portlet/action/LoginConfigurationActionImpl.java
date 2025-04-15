/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.portlet.action;

import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.ReadOnlyException;

import java.io.IOException;

import java.util.Enumeration;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 * @author Julio Camarero
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LoginPortletKeys.FAST_LOGIN,
		"jakarta.portlet.name=" + LoginPortletKeys.LOGIN
	},
	service = ConfigurationAction.class
)
public class LoginConfigurationActionImpl extends DefaultConfigurationAction {

	@Override
	public void postProcess(
		long companyId, PortletRequest portletRequest,
		PortletPreferences portletPreferences) {

		String languageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		try {
			removeDefaultValue(
				portletRequest, portletPreferences,
				"emailPasswordResetBody_" + languageId,
				StringUtil.read(
					PortalClassLoaderUtil.getClassLoader(),
					PropsValues.ADMIN_EMAIL_PASSWORD_RESET_BODY));
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to read the content for " +
					PropsValues.ADMIN_EMAIL_PASSWORD_RESET_BODY,
				ioException);
		}

		try {
			removeDefaultValue(
				portletRequest, portletPreferences,
				"emailPasswordResetSubject_" + languageId,
				StringUtil.read(
					PortalClassLoaderUtil.getClassLoader(),
					PropsValues.ADMIN_EMAIL_PASSWORD_RESET_SUBJECT));
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to read the content for " +
					PropsValues.ADMIN_EMAIL_PASSWORD_RESET_SUBJECT,
				ioException);
		}

		String[] discardLegacyKeys = ParamUtil.getStringValues(
			portletRequest, "discardLegacyKey");

		Enumeration<String> enumeration = portletPreferences.getNames();

		try {
			while (enumeration.hasMoreElements()) {
				String name = enumeration.nextElement();

				for (String discardLegacyKey : discardLegacyKeys) {
					if (name.startsWith(discardLegacyKey + "_")) {
						portletPreferences.reset(name);
					}
				}
			}
		}
		catch (ReadOnlyException readOnlyException) {
			throw new SystemException(readOnlyException);
		}
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		validateEmailFrom(actionRequest);

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LoginConfigurationActionImpl.class);

}