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

import java.util.function.Function;

import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Caio Farias
 */
public class SecretIdentifierUtil {

	public static String buildConfigurationIdentifier(
		String key, String pid, ExtendedObjectClassDefinition.Scope scope,
		Object scopePrimaryKey) {

		return StringBundler.concat(
			"config/", pid, StringPool.SLASH, scope.getValue(),
			StringPool.SLASH, scopePrimaryKey, StringPool.SLASH, key);
	}

	public static String buildConfigurationIdentifier(
		String key, String pid, Function<String, Object> propertyFunction) {

		String configurationPid = StringUtil.replace(
			pid, CharPool.TILDE, CharPool.SLASH);
		String factoryPid = GetterUtil.getString(
			propertyFunction.apply(ConfigurationAdmin.SERVICE_FACTORYPID));

		if (factoryPid.endsWith(".scoped")) {
			configurationPid = ConfigurationPidUtil.getRawPid(factoryPid);
		}

		String portletInstanceId = GetterUtil.getString(
			propertyFunction.apply(
				ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
					getPropertyKey()));

		if (Validator.isNotNull(portletInstanceId)) {
			return buildConfigurationIdentifier(
				key, configurationPid,
				ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE,
				portletInstanceId);
		}

		long groupId = GetterUtil.getLong(
			propertyFunction.apply(
				ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey()));

		if (groupId > 0) {
			return buildConfigurationIdentifier(
				key, configurationPid,
				ExtendedObjectClassDefinition.Scope.GROUP, groupId);
		}

		long companyId = getConfigurationCompanyId(propertyFunction);

		if (companyId != CompanyConstants.SYSTEM) {
			return buildConfigurationIdentifier(
				key, configurationPid,
				ExtendedObjectClassDefinition.Scope.COMPANY, companyId);
		}

		return buildConfigurationIdentifier(
			key, configurationPid, ExtendedObjectClassDefinition.Scope.SYSTEM,
			CompanyConstants.SYSTEM);
	}

	public static long getConfigurationCompanyId(
		Function<String, Object> propertyFunction) {

		return GetterUtil.getLong(
			propertyFunction.apply(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey()),
			CompanyConstants.SYSTEM);
	}

}