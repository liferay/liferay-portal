/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.secret;

import com.liferay.configuration.admin.util.ConfigurationPidUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Caio Farias
 */
public class SecretIdentifierUtil {

	public static long getConfigurationCompanyId(
		Dictionary<String, ?> properties) {

		return GetterUtil.getLong(
			properties.get(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey()),
			CompanyConstants.SYSTEM);
	}

	public static String getSecretIdentifier(
		String key, String pid, Dictionary<String, ?> properties) {

		String configurationPid = _getConfigurationPid(pid, properties);

		String portletInstanceId = GetterUtil.getString(
			properties.get(
				ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
					getPropertyKey()));

		if (Validator.isNotNull(portletInstanceId)) {
			return _getSecretIdentifier(
				key, configurationPid,
				ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE,
				portletInstanceId);
		}

		long groupId = GetterUtil.getLong(
			properties.get(
				ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey()));

		if (groupId > 0) {
			return _getSecretIdentifier(
				key, configurationPid,
				ExtendedObjectClassDefinition.Scope.GROUP, groupId);
		}

		long companyId = getConfigurationCompanyId(properties);

		if (companyId != CompanyConstants.SYSTEM) {
			return _getSecretIdentifier(
				key, configurationPid,
				ExtendedObjectClassDefinition.Scope.COMPANY, companyId);
		}

		return _getSecretIdentifier(
			key, configurationPid, ExtendedObjectClassDefinition.Scope.SYSTEM,
			CompanyConstants.SYSTEM);
	}

	private static String _getConfigurationPid(
		String pid, Dictionary<String, ?> properties) {

		String factoryPid = GetterUtil.getString(
			properties.get(ConfigurationAdmin.SERVICE_FACTORYPID));

		if (factoryPid.endsWith(".scoped")) {
			return ConfigurationPidUtil.getRawPid(factoryPid);
		}

		return StringUtil.replace(pid, CharPool.TILDE, CharPool.SLASH);
	}

	private static String _getSecretIdentifier(
		String key, String pid, ExtendedObjectClassDefinition.Scope scope,
		Object scopePrimaryKey) {

		return StringBundler.concat(
			"config/", pid, StringPool.SLASH, scope.getValue(),
			StringPool.SLASH, scopePrimaryKey, StringPool.SLASH, key);
	}

}