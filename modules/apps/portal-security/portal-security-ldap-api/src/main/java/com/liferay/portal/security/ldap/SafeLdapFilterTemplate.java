/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.ldap.validator.LDAPFilterException;
import com.liferay.portal.security.ldap.validator.LDAPFilterValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author Tomas Polesovsky
 */
public class SafeLdapFilterTemplate extends SafeLdapFilter {

	public SafeLdapFilterTemplate(
			String template, String[] replaceKeys,
			LDAPFilterValidator ldapFilterValidator)
		throws LDAPFilterException {

		super(new StringBundler(template), Collections.emptyList());

		if (replaceKeys == null) {
			replaceKeys = new String[0];
		}

		_replaceKeys = replaceKeys;

		_ldapFilterValidator = ldapFilterValidator;

		_ldapFilterValidator.validate(template);

		_validateReplaceKeyInObject(template);
	}

	public String[] getReplaceKeys() {
		return Arrays.copyOf(_replaceKeys, _replaceKeys.length);
	}

	public SafeLdapFilterTemplate replace(String[] keys, String[] values)
		throws LDAPFilterException {

		if (keys == null) {
			throw new IllegalArgumentException("Parameter keys array is null");
		}

		if (values == null) {
			throw new IllegalArgumentException(
				"Parameter values array is null");
		}

		if (keys.length > values.length) {
			throw new IllegalArgumentException(
				"Parameters keys and values must have the same length");
		}

		for (String key : keys) {
			boolean found = false;

			for (String replaceKey : _replaceKeys) {
				if (!replaceKey.equals(key)) {
					continue;
				}

				found = true;
			}

			if (!found) {
				throw new IllegalArgumentException(
					"Parameter key " + key +
						" is not supported by the template");
			}
		}

		String[] placeholderValues = new String[values.length];

		Arrays.fill(placeholderValues, ARGUMENT_PLACEHOLDER);

		StringBundler oldFilterSB = getFilterStringBundler();
		StringBundler newFilterSB = new StringBundler();
		List<Object> newArguments = new ArrayList<>();
		Object[] oldArguments = getArguments();
		int argumentsPos = 0;

		for (int i = 0; i < oldFilterSB.index(); i++) {
			String filterString = oldFilterSB.stringAt(i);

			if (filterString == null) {
				continue;
			}

			if (Objects.equals(filterString, ARGUMENT_PLACEHOLDER)) {
				newFilterSB.append(ARGUMENT_PLACEHOLDER);

				newArguments.add(oldArguments[argumentsPos]);

				argumentsPos++;

				continue;
			}

			TreeMap<Integer, String> valuesTreeMap = new TreeMap<>();

			for (int j = 0; j < keys.length; j++) {
				String key = keys[j];

				int pos = filterString.indexOf(key);

				while (pos > -1) {
					valuesTreeMap.put(pos, values[j]);

					pos = filterString.indexOf(key, pos + key.length());
				}
			}

			if (valuesTreeMap.isEmpty()) {
				newFilterSB.append(filterString);
			}
			else {
				newArguments.addAll(valuesTreeMap.values());

				String replacedKeys = StringUtil.replace(
					filterString, keys, placeholderValues);

				int pos = replacedKeys.indexOf(ARGUMENT_PLACEHOLDER);

				int lastPos = 0;

				while (pos > -1) {
					newFilterSB.append(replacedKeys.substring(lastPos, pos));
					newFilterSB.append(ARGUMENT_PLACEHOLDER);

					lastPos = pos + ARGUMENT_PLACEHOLDER.length();

					pos = replacedKeys.indexOf(ARGUMENT_PLACEHOLDER, lastPos);
				}

				if (lastPos < replacedKeys.length()) {
					newFilterSB.append(replacedKeys.substring(lastPos));
				}
			}
		}

		_ldapFilterValidator.validate(newFilterSB.toString());

		return new SafeLdapFilterTemplate(
			newFilterSB, newArguments, _replaceKeys, _ldapFilterValidator);
	}

	protected SafeLdapFilterTemplate(
		StringBundler filterSB, List<Object> arguments, String[] replaceKeys,
		LDAPFilterValidator ldapFilterValidator) {

		super(filterSB, arguments);

		_replaceKeys = replaceKeys;
		_ldapFilterValidator = ldapFilterValidator;
	}

	private void _validateReplaceKeyInObject(String template)
		throws LDAPFilterException {

		int pos = template.lastIndexOf(StringPool.EQUAL);

		while (pos > -1) {
			String expression = template.substring(
				template.lastIndexOf(StringPool.OPEN_PARENTHESIS, pos), pos);

			if (expression.contains(StringPool.EQUAL)) {

				// subexpression object=a<=b>=c

				pos = template.lastIndexOf(StringPool.EQUAL, pos - 1);

				continue;
			}

			for (String replaceKey : _replaceKeys) {
				if (expression.contains(replaceKey)) {
					throw new LDAPFilterException(
						StringBundler.concat(
							"Expression '", expression, "' cannot contain '",
							replaceKey, "' inside template '", template, "'"));
				}
			}

			pos = template.lastIndexOf(StringPool.EQUAL, pos - 1);
		}
	}

	private final LDAPFilterValidator _ldapFilterValidator;
	private final String[] _replaceKeys;

}