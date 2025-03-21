/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.exception.ClientExtensionEntryTypeSettingsException;
import com.liferay.client.extension.type.CustomElementCET;
import com.liferay.client.extension.type.internal.CustomElementCETImpl;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Iván Zaera Avellón
 */
public class CustomElementCETImplFactoryImpl
	extends BaseCETImplFactoryImpl<CustomElementCET> {

	public CustomElementCETImplFactoryImpl() {
		super(CustomElementCET.class);
	}

	@Override
	public CustomElementCET create(
		String baseURL, long companyId, Date createDate, String description,
		String externalReferenceCode, Date modifiedDate, String name,
		Properties properties, boolean readOnly, String sourceCodeURL,
		int status, UnicodeProperties typeSettingsUnicodeProperties) {

		return new CustomElementCETImpl(
			baseURL, companyId, createDate, description, externalReferenceCode,
			modifiedDate, name, properties, readOnly, sourceCodeURL, status,
			typeSettingsUnicodeProperties);
	}

	@Override
	public UnicodeProperties getUnicodeProperties(
		PortletRequest portletRequest) {

		return UnicodePropertiesBuilder.create(
			true
		).put(
			"cssURLs",
			StringUtil.merge(
				ParamUtil.getStringValues(portletRequest, "cssURLs"),
				StringPool.NEW_LINE)
		).put(
			"friendlyURLMapping",
			ParamUtil.getString(portletRequest, "friendlyURLMapping")
		).put(
			"htmlElementName",
			ParamUtil.getString(portletRequest, "htmlElementName")
		).put(
			"instanceable", ParamUtil.getBoolean(portletRequest, "instanceable")
		).put(
			"portletCategoryName",
			ParamUtil.getString(portletRequest, "portletCategoryName")
		).put(
			"urls",
			StringUtil.merge(
				ParamUtil.getStringValues(portletRequest, "urls"),
				StringPool.NEW_LINE)
		).put(
			"useESM", ParamUtil.getBoolean(portletRequest, "useESM")
		).build();
	}

	@Override
	public void validate(
			CustomElementCET newCustomElementCET,
			CustomElementCET oldCustomElementCET)
		throws PortalException {

		String cssURLs = newCustomElementCET.getCSSURLs();

		if (Validator.isNotNull(cssURLs)) {
			for (String cssURL : cssURLs.split(StringPool.NEW_LINE)) {
				if (!Validator.isUrl(cssURL, true)) {
					throw new ClientExtensionEntryTypeSettingsException(
						"Invalid CSS URL: " + cssURL, "css-url-x-is-invalid",
						cssURL);
				}
			}
		}

		String friendlyURLMapping = newCustomElementCET.getFriendlyURLMapping();

		Matcher matcher = _friendlyURLMappingPattern.matcher(
			friendlyURLMapping);

		if (!matcher.matches()) {
			throw new ClientExtensionEntryTypeSettingsException(
				"Invalid friendly URL mapping: " + friendlyURLMapping,
				"friendly-url-mapping-x-is-invalid", friendlyURLMapping);
		}

		String htmlElementName = newCustomElementCET.getHTMLElementName();

		if (Validator.isNull(htmlElementName)) {
			throw new ClientExtensionEntryTypeSettingsException(
				"HTML element name is null",
				"please-enter-an-html-element-name");
		}

		char[] htmlElementNameCharArray = htmlElementName.toCharArray();

		if (!Validator.isChar(htmlElementNameCharArray[0]) ||
			!Character.isLowerCase(htmlElementNameCharArray[0])) {

			throw new ClientExtensionEntryTypeSettingsException(
				"HTML element name must start with a lowercase letter",
				"html-element-name-must-start-with-a-lowercase-letter");
		}

		boolean containsDash = false;

		for (char c : htmlElementNameCharArray) {
			if (c == CharPool.DASH) {
				containsDash = true;
			}

			if ((Validator.isChar(c) && Character.isLowerCase(c)) ||
				Validator.isNumber(String.valueOf(c)) || (c == CharPool.DASH) ||
				(c == CharPool.PERIOD) || (c == CharPool.UNDERLINE)) {
			}
			else {
				throw new ClientExtensionEntryTypeSettingsException(
					"HTML element name contains an invalid character: " + c,
					"html-element-name-contains-invalid-character-x", c);
			}
		}

		if (!containsDash) {
			throw new ClientExtensionEntryTypeSettingsException(
				"HTML element name must contain at least one hyphen",
				"html-element-name-must-contain-at-least-one-hyphen");
		}

		if (_reservedHTMLElementNames.contains(htmlElementName)) {
			throw new ClientExtensionEntryTypeSettingsException(
				"Reserved custom element HTML element name: " + htmlElementName,
				"x-is-a-reserved-html-element-name", htmlElementName);
		}

		String urls = newCustomElementCET.getURLs();

		if (Validator.isNull(urls)) {
			throw new ClientExtensionEntryTypeSettingsException(
				"At least one JavaScript URL is required",
				"please-enter-at-least-one-javascript-url");
		}

		for (String url : urls.split(StringPool.NEW_LINE)) {
			if (!Validator.isUrl(url, true)) {
				throw new ClientExtensionEntryTypeSettingsException(
					"Invalid JavaScript URL: " + url,
					"javascript-url-x-is-invalid", url);
			}
		}

		if ((oldCustomElementCET != null) &&
			(newCustomElementCET.isInstanceable() !=
				oldCustomElementCET.isInstanceable())) {

			throw new ClientExtensionEntryTypeSettingsException(
				"The instanceable value cannot be changed",
				"the-instanceable-value-cannot-be-changed");
		}
	}

	private static final Pattern _friendlyURLMappingPattern = Pattern.compile(
		"[A-Za-z0-9-_]*");

	private final Set<String> _reservedHTMLElementNames = SetUtil.fromArray(
		"annotation-xml", "color-profile", "font-face", "font-face-format",
		"font-face-name", "font-face-src", "font-face-uri", "missing-glyph");

}