/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.display.context;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.opensaml.integration.field.expression.handler.UserFieldExpressionHandler;
import com.liferay.saml.opensaml.integration.field.expression.handler.registry.UserFieldExpressionHandlerRegistry;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.web.internal.exception.UserAttributeMappingException;
import com.liferay.saml.web.internal.exception.UserIdentifierExpressionException;

import jakarta.portlet.PortletRequest;

import java.io.IOException;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Stian Sigvartsen
 */
public class AttributeMappingDisplayContext {

	public AttributeMappingDisplayContext(
			PortletRequest portletRequest,
			SamlSpIdpConnection samlSpIdpConnection, ThemeDisplay themeDisplay,
			UserFieldExpressionHandlerRegistry
				userFieldExpressionHandlerRegistry)
		throws IOException {

		_portletRequest = portletRequest;
		_userFieldExpressionHandlerRegistry =
			userFieldExpressionHandlerRegistry;

		_locale = themeDisplay.getLocale();

		Properties userAttributeMappingsProperties = null;

		if (samlSpIdpConnection != null) {
			_userIdentifierExpression =
				samlSpIdpConnection.getUserIdentifierExpression();

			userAttributeMappingsProperties =
				samlSpIdpConnection.getNormalizedUserAttributeMappings();
		}
		else {
			userAttributeMappingsProperties = new Properties();
		}

		Set<String> userFieldExpressionHandlerPrefixes = new HashSet<>(
			userFieldExpressionHandlerRegistry.
				getFieldExpressionHandlerPrefixes());

		userFieldExpressionHandlerPrefixes.forEach(
			prefix -> _entries.put(prefix, new ArrayList<>()));

		userFieldExpressionHandlerPrefixes.removeIf(
			this::_loadFromPortletRequest);

		for (String propertyName :
				userAttributeMappingsProperties.stringPropertyNames()) {

			if (Validator.isBlank(propertyName)) {
				continue;
			}

			String propertyValue = GetterUtil.getString(
				userAttributeMappingsProperties.get(propertyName));

			int prefixEndIndex = propertyValue.indexOf(CharPool.COLON);

			String prefix = StringPool.BLANK;

			if (prefixEndIndex != -1) {
				prefix = propertyValue.substring(0, prefixEndIndex);
			}

			if (!userFieldExpressionHandlerPrefixes.contains(prefix)) {
				continue;
			}

			List<Map.Entry<String, String>> list = _entries.get(prefix);

			list.add(
				new AbstractMap.SimpleEntry(
					propertyValue.substring(prefixEndIndex + 1), propertyName));
		}

		for (String prefix : userFieldExpressionHandlerPrefixes) {
			List<Map.Entry<String, String>> list = _entries.get(prefix);

			if (list.isEmpty()) {
				list.add(
					new AbstractMap.SimpleEntry(
						StringPool.BLANK, StringPool.BLANK));
			}

			_indexes.computeIfAbsent(
				prefix,
				k -> {
					int[] indexes = new int[list.size()];

					for (int i = 0; i < list.size(); i++) {
						indexes[i] = i;
					}

					return indexes;
				});
		}
	}

	public int[] getIndexes(String prefix) {
		return _indexes.get(prefix);
	}

	public List<Map.Entry<String, String>> getMapEntries(String prefix) {
		return _entries.get(prefix);
	}

	public Object[] getMessageArguments(
		UserAttributeMappingException userAttributeMappingException) {

		UserFieldExpressionHandler userFieldExpressionHandler =
			_userFieldExpressionHandlerRegistry.getFieldExpressionHandler(
				userAttributeMappingException.getPrefix());

		String message = null;

		if (UserAttributeMappingException.ErrorType.
				DUPLICATE_FIELD_EXPRESSION ==
					userAttributeMappingException.getErrorType()) {

			message = ResourceBundleUtil.getString(
				ResourceBundleUtil.getBundle(
					_locale, AttributeMappingDisplayContext.class),
				"each-user-field-can-only-be-mapped-to-one-saml-attribute");
		}
		else {
			message = ResourceBundleUtil.getString(
				ResourceBundleUtil.getBundle(
					_locale, AttributeMappingDisplayContext.class),
				"all-attribute-mappings-must-specify-a-saml-attribute");
		}

		return new String[] {
			userFieldExpressionHandler.getSectionLabel(_locale), message
		};
	}

	public String getMessageKey(
		UserAttributeMappingException userAttributeMappingException) {

		return "x-colon-y";
	}

	public String getMessageKey(
		UserIdentifierExpressionException userIdentifierExpressionException) {

		return "no-attribute-mapping-selected-for-user-matching";
	}

	public List<Map.Entry<String, UserFieldExpressionHandler>>
		getOrderedUserFieldExpressionHandlers() {

		return _userFieldExpressionHandlerRegistry.
			getOrderedFieldExpressionHandlers();
	}

	public String[] getPrefixes() {
		Set<String> prefixes = _entries.keySet();

		return prefixes.toArray(new String[0]);
	}

	public String getUserIdentifierExpression() {
		return _userIdentifierExpression;
	}

	private boolean _loadFromPortletRequest(String prefix) {
		String userAttributeMappingsIndexesParam = ParamUtil.getString(
			_portletRequest,
			"attribute:" + prefix + ":userAttributeMappingsIndexes");

		if (Validator.isBlank(userAttributeMappingsIndexesParam)) {
			return false;
		}

		int[] userAttributeMappingsIndexes = null;

		List<Map.Entry<String, String>> prefixEntries = _entries.get(prefix);

		if (Validator.isNotNull(userAttributeMappingsIndexesParam)) {
			userAttributeMappingsIndexes = StringUtil.split(
				userAttributeMappingsIndexesParam, 0);

			for (int prefixEntriesIndex : userAttributeMappingsIndexes) {
				prefixEntries.add(
					new AbstractMap.SimpleEntry<>(
						ParamUtil.getString(
							_portletRequest,
							StringBundler.concat(
								"attribute:", prefix,
								":userAttributeMappingFieldExpression-",
								prefixEntriesIndex)),
						ParamUtil.getString(
							_portletRequest,
							StringBundler.concat(
								"attribute:", prefix,
								":userAttributeMappingSamlAttribute-",
								prefixEntriesIndex))));
			}
		}
		else if (prefixEntries.isEmpty()) {
			prefixEntries.add(
				new AbstractMap.SimpleEntry(
					StringPool.BLANK, StringPool.BLANK));
			userAttributeMappingsIndexes = new int[] {0};
		}
		else {
			userAttributeMappingsIndexes = new int[prefixEntries.size()];

			for (int i = 0; i < prefixEntries.size(); i++) {
				userAttributeMappingsIndexes[i] = i;
			}
		}

		_indexes.putIfAbsent(prefix, userAttributeMappingsIndexes);

		return true;
	}

	private final Map<String, List<Map.Entry<String, String>>> _entries =
		new HashMap<>();
	private final Map<String, int[]> _indexes = new HashMap<>();
	private final Locale _locale;
	private final PortletRequest _portletRequest;
	private final UserFieldExpressionHandlerRegistry
		_userFieldExpressionHandlerRegistry;
	private String _userIdentifierExpression = StringPool.BLANK;

}