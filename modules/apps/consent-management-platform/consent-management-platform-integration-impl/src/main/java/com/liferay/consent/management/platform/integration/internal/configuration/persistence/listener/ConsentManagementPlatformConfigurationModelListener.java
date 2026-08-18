/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.consent.management.platform.integration.internal.configuration.persistence.listener;

import com.liferay.consent.management.platform.integration.configuration.ConsentManagementPlatformConfiguration;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Dictionary;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = "model.class.name=com.liferay.consent.management.platform.integration.configuration.ConsentManagementPlatformConfiguration",
	service = ConfigurationModelListener.class
)
public class ConsentManagementPlatformConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		_validateElementNames(
			GetterUtil.getString(properties.get("consentMappingScript")),
			properties);
		_validateElementNames(
			GetterUtil.getString(properties.get("scriptTag")), properties);
	}

	private String _getInvalidElementName(String html) {
		int index = 0;
		Matcher matcher = _elementPattern.matcher(html);

		while ((index = html.indexOf(CharPool.LESS_THAN, index)) != -1) {
			matcher.region(index, html.length());

			if (!matcher.lookingAt()) {
				index++;

				continue;
			}

			String elementName = matcher.group(1);

			if (!StringUtil.equalsIgnoreCase(elementName, "link") &&
				!StringUtil.equalsIgnoreCase(elementName, "script")) {

				return elementName;
			}

			boolean scriptStartTag = false;

			if (StringUtil.equalsIgnoreCase(elementName, "script") &&
				!StringUtil.startsWith(matcher.group(), "</")) {

				scriptStartTag = true;
			}

			index = matcher.end();

			if (!scriptStartTag) {
				continue;
			}

			Matcher scriptEndMatcher = _scriptEndPattern.matcher(html);

			if (scriptEndMatcher.find(index)) {
				index = scriptEndMatcher.start();
			}
			else {
				index = html.length();
			}
		}

		return null;
	}

	private void _validateElementNames(
			String html, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		if (Validator.isNull(html)) {
			return;
		}

		String elementName = _getInvalidElementName(html);

		if (elementName == null) {
			return;
		}

		throw new ConfigurationModelListenerException(
			ResourceBundleUtil.getString(
				ResourceBundleUtil.getModuleAndPortalResourceBundle(
					LocaleUtil.getMostRelevantLocale(), getClass()),
				"the-x-element-is-not-allowed-in-a-consent-management-" +
					"platform-script",
				elementName),
			ConsentManagementPlatformConfiguration.class, getClass(),
			properties);
	}

	private static final Pattern _elementPattern = Pattern.compile(
		"</?([a-z][a-z0-9]*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern _scriptEndPattern = Pattern.compile(
		"</script(?=[\\s>])", Pattern.CASE_INSENSITIVE);

}