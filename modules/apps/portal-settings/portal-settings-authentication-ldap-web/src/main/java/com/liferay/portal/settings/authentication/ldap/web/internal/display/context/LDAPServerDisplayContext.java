/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.ldap.web.internal.display.context;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;

import java.util.Map;

/**
 * @author Caio Farias
 */
public class LDAPServerDisplayContext {

	public LDAPServerDisplayContext(
		LDAPServerConfiguration ldapServerConfiguration, long ldapServerId) {

		_ldapServerConfiguration = ldapServerConfiguration;
		_ldapServerId = ldapServerId;
	}

	public String getBaseProviderURL() {
		String baseProviderURL = _ldapServerConfiguration.baseProviderURL();

		if (!PropsValues.FIPS_ENABLED) {
			return baseProviderURL;
		}

		if (_ldapServerId == 0) {
			return "ldaps://localhost:10636";
		}

		if (StringUtil.startsWith(baseProviderURL, "ldap://")) {
			return "ldaps://" + baseProviderURL.substring("ldap://".length());
		}

		return baseProviderURL;
	}

	public String getGroupMapping(String name) {
		if (_groupMappings == null) {
			_groupMappings = _getMappings(
				_ldapServerConfiguration.groupMappings());
		}

		return MapUtil.getString(_groupMappings, name);
	}

	public String getUserMapping(String name) {
		if (_userMappings == null) {
			_userMappings = _getMappings(
				_ldapServerConfiguration.userMappings());
		}

		return MapUtil.getString(_userMappings, name);
	}

	private Map<String, String> _getMappings(String[] mappings) {
		HashMapBuilder.HashMapWrapper<String, String> hashMapWrapper =
			HashMapBuilder.create(mappings.length);

		for (String mapping : mappings) {
			int index = mapping.indexOf(CharPool.EQUAL);

			if (index == -1) {
				continue;
			}

			hashMapWrapper.put(
				StringUtil.trim(mapping.substring(0, index)),
				StringUtil.trim(mapping.substring(index + 1)));
		}

		return hashMapWrapper.build();
	}

	private Map<String, String> _groupMappings;
	private final LDAPServerConfiguration _ldapServerConfiguration;
	private final long _ldapServerId;
	private Map<String, String> _userMappings;

}