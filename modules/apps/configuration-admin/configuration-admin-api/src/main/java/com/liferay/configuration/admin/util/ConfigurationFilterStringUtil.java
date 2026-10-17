/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Thiago Buarque
 */
public class ConfigurationFilterStringUtil {

	public static String getCompanyScopedFilterString(
		Serializable companyId, String virtualInstanceId) {

		return StringBundler.concat(
			"(&(|(",
			ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
			StringPool.EQUAL, _toString(companyId),
			")(dxp.lxc.liferay.com.virtualInstanceId=",
			GetterUtil.get(
				SystemBundleUtil.escapeFilterValue(virtualInstanceId), "*"),
			"))(!(", ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey(),
			"=*))(!(siteExternalReferenceCode=*))(!(",
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
				getPropertyKey(),
			"=*)))");
	}

	public static String getCompanyScopedFilterString(
		Serializable companyId, String pid, String virtualInstanceId) {

		return StringBundler.concat(
			StringPool.OPEN_PARENTHESIS, StringPool.AMPERSAND,
			_getScopedFilterString(pid),
			getCompanyScopedFilterString(companyId, virtualInstanceId),
			StringPool.CLOSE_PARENTHESIS);
	}

	public static String getGroupScopedFilterString(
		Serializable companyId, Serializable groupId,
		String siteExternalReferenceCode) {

		if ((companyId == null) ||
			(GetterUtil.getLong(companyId) == CompanyConstants.SYSTEM)) {

			throw new IllegalArgumentException(
				"A valid company is expected when building a group scoped " +
					"configuration filter string");
		}

		return StringBundler.concat(
			"(&(|(", ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey(),
			StringPool.EQUAL, _toString(groupId),
			")(siteExternalReferenceCode=",
			GetterUtil.get(
				SystemBundleUtil.escapeFilterValue(siteExternalReferenceCode),
				"*"),
			"))(", ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
			StringPool.EQUAL, _toString(companyId), ")(!(",
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
				getPropertyKey(),
			"=*)))");
	}

	public static String getGroupScopedFilterString(
		Serializable companyId, Serializable groupId, String pid,
		String siteExternalReferenceCode) {

		return StringBundler.concat(
			StringPool.OPEN_PARENTHESIS, StringPool.AMPERSAND,
			_getScopedFilterString(pid),
			getGroupScopedFilterString(
				companyId, groupId, siteExternalReferenceCode),
			StringPool.CLOSE_PARENTHESIS);
	}

	public static String getPortletScopedFilterString(
		Serializable portletInstanceId) {

		return StringBundler.concat(
			"(&(",
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
				getPropertyKey(),
			"=", _toString(portletInstanceId), "))");
	}

	public static String getPortletScopedFilterString(
		String pid, Serializable portletInstanceId) {

		return StringBundler.concat(
			StringPool.OPEN_PARENTHESIS, StringPool.AMPERSAND,
			_getScopedFilterString(pid),
			getPortletScopedFilterString(portletInstanceId),
			StringPool.CLOSE_PARENTHESIS);
	}

	public static String getScopedFilterString(
		Serializable companyId, String pid,
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK) {

		if (scope.equals(ExtendedObjectClassDefinition.Scope.COMPANY)) {
			return getCompanyScopedFilterString(scopePK, pid, null);
		}
		else if (scope.equals(ExtendedObjectClassDefinition.Scope.GROUP)) {
			return getGroupScopedFilterString(companyId, scopePK, pid, null);
		}
		else if (scope.equals(
					ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE)) {

			return getPortletScopedFilterString(pid, scopePK);
		}

		return getSystemScopedFilterString(pid);
	}

	public static String getSystemScopedFilterString() {
		return StringBundler.concat(
			"(&(|(!(",
			ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
			"=*))(",
			ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
			"=0))(!(dxp.lxc.liferay.com.virtualInstanceId=*))(!(",
			ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey(),
			"=*))(!(siteExternalReferenceCode=*))(!(",
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
				getPropertyKey(),
			"=*)))");
	}

	public static String getSystemScopedFilterString(String pid) {
		String filterString = StringBundler.concat(
			StringPool.OPEN_PARENTHESIS, StringPool.PIPE,
			_getPropertyFilterString(
				ConfigurationAdmin.SERVICE_FACTORYPID, pid),
			_getPropertyFilterString(Constants.SERVICE_PID, pid),
			StringPool.CLOSE_PARENTHESIS);

		if (pid.contains("~")) {
			filterString = StringBundler.concat(
				StringPool.OPEN_PARENTHESIS, StringPool.AMPERSAND,
				_getPropertyFilterString(
					ConfigurationAdmin.SERVICE_FACTORYPID,
					ConfigurationPidUtil.getRawPid(pid)),
				_getPropertyFilterString(Constants.SERVICE_PID, pid),
				StringPool.CLOSE_PARENTHESIS);
		}

		return StringBundler.concat(
			StringPool.OPEN_PARENTHESIS, StringPool.AMPERSAND, filterString,
			getSystemScopedFilterString(), StringPool.CLOSE_PARENTHESIS);
	}

	private static String _getPropertyFilterString(String key, String value) {
		if (Validator.isNull(key) || Validator.isNull(value)) {
			return StringPool.BLANK;
		}

		return StringBundler.concat(
			StringPool.OPEN_PARENTHESIS, key, StringPool.EQUAL,
			SystemBundleUtil.escapeFilterValue(value),
			StringPool.CLOSE_PARENTHESIS);
	}

	private static String _getScopedFilterString(String pid) {
		String rawPid = ConfigurationPidUtil.getRawPid(pid);

		String filterString = StringBundler.concat(
			StringPool.OPEN_PARENTHESIS, StringPool.PIPE,
			_getPropertyFilterString(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid),
			_getPropertyFilterString(
				ConfigurationAdmin.SERVICE_FACTORYPID, rawPid + ".scoped"),
			StringPool.CLOSE_PARENTHESIS);

		if (pid.contains("~")) {
			filterString = StringBundler.concat(
				StringPool.OPEN_PARENTHESIS, StringPool.AMPERSAND, filterString,
				_getPropertyFilterString(Constants.SERVICE_PID, pid),
				StringPool.CLOSE_PARENTHESIS);
		}

		return filterString;
	}

	private static String _toString(Serializable value) {
		if (value == null) {
			return StringPool.STAR;
		}

		return SystemBundleUtil.escapeFilterValue(String.valueOf(value));
	}

}