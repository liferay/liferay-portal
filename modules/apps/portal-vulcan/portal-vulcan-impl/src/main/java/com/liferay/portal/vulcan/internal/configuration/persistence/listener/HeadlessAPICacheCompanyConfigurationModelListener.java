/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.configuration.persistence.listener;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.internal.configuration.HeadlessAPICacheCompanyConfiguration;

import java.util.Dictionary;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "model.class.name=com.liferay.portal.vulcan.internal.configuration.HeadlessAPICacheCompanyConfiguration",
	service = ConfigurationModelListener.class
)
public class HeadlessAPICacheCompanyConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> dictionary)
		throws ConfigurationModelListenerException {

		String cacheControl = GetterUtil.getString(
			dictionary.get("cacheControl"), "public");

		if (!ArrayUtil.contains(_CACHE_CONTROLS, cacheControl)) {
			throw new ConfigurationModelListenerException(
				ResourceBundleUtil.getString(
					_getResourceBundle(),
					"cache-control-must-be-public-or-private"),
				HeadlessAPICacheCompanyConfiguration.class, getClass(),
				dictionary);
		}

		int maxAge = GetterUtil.getInteger(dictionary.get("maxAge"));

		if ((maxAge < 0) || (maxAge > _MAXIMUM_MAX_AGE)) {
			throw new ConfigurationModelListenerException(
				ResourceBundleUtil.getString(
					_getResourceBundle(),
					"headless-api-cache-max-age-out-of-range"),
				HeadlessAPICacheCompanyConfiguration.class, getClass(),
				dictionary);
		}

		String path = GetterUtil.getString(dictionary.get("path"));

		if (Validator.isBlank(path)) {
			throw new ConfigurationModelListenerException(
				ResourceBundleUtil.getString(
					_getResourceBundle(),
					"headless-api-cacheable-endpoint-path-required"),
				HeadlessAPICacheCompanyConfiguration.class, getClass(),
				dictionary);
		}

		if (!path.startsWith(StringPool.SLASH)) {
			throw new ConfigurationModelListenerException(
				ResourceBundleUtil.getString(
					_getResourceBundle(),
					"headless-api-cacheable-endpoint-path-must-start-with-a-" +
						"slash"),
				HeadlessAPICacheCompanyConfiguration.class, getClass(),
				dictionary);
		}
	}

	private ResourceBundle _getResourceBundle() {
		Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

		if (locale == null) {
			locale = LocaleUtil.getDefault();
		}

		return ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());
	}

	private static final String[] _CACHE_CONTROLS = {"private", "public"};

	private static final int _MAXIMUM_MAX_AGE = 86400;

}