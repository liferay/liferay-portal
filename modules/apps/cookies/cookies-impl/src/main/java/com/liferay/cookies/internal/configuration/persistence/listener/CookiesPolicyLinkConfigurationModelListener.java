/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.configuration.persistence.listener;

import com.liferay.cookies.configuration.banner.CookiesBannerConfiguration;
import com.liferay.cookies.configuration.consent.CookiesConsentConfiguration;
import com.liferay.petra.string.CharPool;
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
			CookiesBannerConfiguration.class, properties, "privacyPolicyLink");
		_validatePolicyLink(
			CookiesConsentConfiguration.class, properties, "cookiePolicyLink");
	}

	private boolean _isSiteRelative(String policyLink) {
		if (!StringUtil.startsWith(policyLink, CharPool.SLASH)) {
			return false;
		}

		if (policyLink.length() == 1) {
			return true;
		}

		// A browser resolves both "//liferay.com" and "/\liferay.com" to an
		// authority

		char c = policyLink.charAt(1);

		if ((c == CharPool.BACK_SLASH) || (c == CharPool.SLASH)) {
			return false;
		}

		return true;
	}

	private void _validatePolicyLink(
			Class<?> configurationClass, Dictionary<String, Object> properties,
			String propertyName)
		throws ConfigurationModelListenerException {

		// A browser removes every tab, line feed, and carriage return from a
		// URL before it resolves it

		String policyLink = StringUtil.removeChars(
			GetterUtil.getString(properties.get(propertyName)),
			CharPool.NEW_LINE, CharPool.RETURN, CharPool.TAB);

		if (Validator.isNull(policyLink) || _isSiteRelative(policyLink) ||
			StringUtil.startsWith(policyLink, Http.HTTP_WITH_SLASH) ||
			StringUtil.startsWith(policyLink, Http.HTTPS_WITH_SLASH)) {

			return;
		}

		throw new ConfigurationModelListenerException(
			ResourceBundleUtil.getString(
				ResourceBundleUtil.getModuleAndPortalResourceBundle(
					LocaleUtil.getMostRelevantLocale(), getClass()),
				"please-enter-an-http-url-an-https-url-or-a-path-that-begins-" +
					"with-a-single-slash"),
			configurationClass, getClass(), properties);
	}

}