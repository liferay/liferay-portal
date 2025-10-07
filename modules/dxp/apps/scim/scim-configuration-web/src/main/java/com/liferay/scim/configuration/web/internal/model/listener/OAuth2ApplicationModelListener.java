/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.configuration.web.internal.model.listener;

import com.liferay.oauth2.provider.exception.RequiredOAuth2ApplicationException;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.scim.rest.util.ScimClientUtil;
import com.liferay.scim.rest.util.ScimThreadLocal;

import java.util.Dictionary;
import java.util.Objects;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christian Moura
 */
@Component(service = ModelListener.class)
public class OAuth2ApplicationModelListener
	extends BaseModelListener<OAuth2Application> {

	@Override
	public void onBeforeRemove(OAuth2Application oAuth2Application) {
		if (ScimThreadLocal.isResetInProcess()) {
			return;
		}

		Configuration[] configurations = null;

		try {
			configurations = _configurationAdmin.listConfigurations(
				StringBundler.concat(
					"(&(", ConfigurationAdmin.SERVICE_FACTORYPID,
					"=com.liferay.scim.rest.internal.configuration.",
					"ScimClientOAuth2ApplicationConfiguration)(companyId=",
					oAuth2Application.getCompanyId(), "))"));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		if (ArrayUtil.isEmpty(configurations)) {
			return;
		}

		Configuration configuration = configurations[0];

		Dictionary<String, Object> properties = configuration.getProperties();

		String oAuth2ApplicationName = GetterUtil.getString(
			properties.get("oAuth2ApplicationName"));

		String scimClientId = ScimClientUtil.generateScimClientId(
			oAuth2ApplicationName);

		if (Objects.equals(oAuth2Application.getClientId(), scimClientId)) {
			ReflectionUtil.throwException(
				new RequiredOAuth2ApplicationException(
					_language.format(
						LocaleUtil.getDefault(),
						"oauth2-application-x-is-required-by-x",
						new String[] {oAuth2Application.getName(), "SCIM"},
						false)));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2ApplicationModelListener.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private Language _language;

}