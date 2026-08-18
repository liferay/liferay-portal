/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.configuration.persistence.listener;

import com.liferay.cookies.configuration.banner.CookiesBannerConfiguration;
import com.liferay.cookies.configuration.consent.CookiesConsentConfiguration;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = {
		"model.class.name=com.liferay.cookies.configuration.banner.CookiesBannerConfiguration",
		"model.class.name=com.liferay.cookies.configuration.consent.CookiesConsentConfiguration"
	},
	service = ConfigurationModelListener.class
)
public class CookiesPolicyLinkConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		_validatePolicyLink(
			CookiesBannerConfiguration.class, "privacyPolicyLink", properties);
		_validatePolicyLink(
			CookiesConsentConfiguration.class, "cookiePolicyLink", properties);
	}

	private void _validatePolicyLink(
			Class<?> configurationClass, String name,
			Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		String policyLink = GetterUtil.getString(properties.get(name));

		if (Validator.isNull(policyLink) ||
			(StringUtil.startsWith(policyLink, CharPool.SLASH) &&
			 !StringUtil.startsWith(policyLink, StringPool.DOUBLE_SLASH)) ||
			StringUtil.startsWith(policyLink, Http.HTTP_WITH_SLASH) ||
			StringUtil.startsWith(policyLink, Http.HTTPS_WITH_SLASH)) {

			return;
		}

		throw new ConfigurationModelListenerException(
			ResourceBundleUtil.getString(
				ResourceBundleUtil.getModuleAndPortalResourceBundle(
					LocaleUtil.getMostRelevantLocale(), getClass()),
				"please-enter-a-valid-url"),
			configurationClass, getClass(), properties);
	}

}