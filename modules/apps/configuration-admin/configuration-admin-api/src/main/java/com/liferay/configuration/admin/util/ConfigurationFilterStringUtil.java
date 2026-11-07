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
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Objects;

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
			StringPool.CLOSE_PARENTHESIS,
			_getDefaultVirtualInstanceIdFilterString(virtualInstanceId), ")(!(",
			ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey(),
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
		StringBundler sb = new StringBundler(12);

		sb.append("(|");

		_appendPropertyFilterString(
			sb, ConfigurationAdmin.SERVICE_FACTORYPID, pid);
		_appendPropertyFilterString(sb, Constants.SERVICE_PID, pid);

		sb.append(StringPool.CLOSE_PARENTHESIS);

		String filterString = sb.toString();

		if (pid.contains("~")) {
			sb = new StringBundler(12);

			sb.append("(&");

			_appendPropertyFilterString(
				sb, ConfigurationAdmin.SERVICE_FACTORYPID,
				ConfigurationPidUtil.getRawPid(pid));
			_appendPropertyFilterString(sb, Constants.SERVICE_PID, pid);

			sb.append(StringPool.CLOSE_PARENTHESIS);

			filterString = sb.toString();
		}

		return StringBundler.concat(
			StringPool.OPEN_PARENTHESIS, StringPool.AMPERSAND, filterString,
			getSystemScopedFilterString(), StringPool.CLOSE_PARENTHESIS);
	}

	private static String _getDefaultVirtualInstanceIdFilterString(
		String virtualInstanceId) {

		if (Objects.equals(
				virtualInstanceId, PropsValues.COMPANY_DEFAULT_WEB_ID)) {

			return "(dxp.lxc.liferay.com.virtualInstanceId=default)";
		}

		return StringPool.BLANK;
	}

	private static void _appendPropertyFilterString(
		StringBundler sb, String key, String value) {

		if (Validator.isNull(key) || Validator.isNull(value)) {
			return;
		}

		sb.append(StringPool.OPEN_PARENTHESIS);
		sb.append(key);
		sb.append(StringPool.EQUAL);
		sb.append(SystemBundleUtil.escapeFilterValue(value));
		sb.append(StringPool.CLOSE_PARENTHESIS);
	}

	private static String _getScopedFilterString(String pid) {
		String rawPid = ConfigurationPidUtil.getRawPid(pid);

		StringBundler sb = new StringBundler(12);

		sb.append("(|");

		_appendPropertyFilterString(
			sb, ConfigurationAdmin.SERVICE_FACTORYPID, rawPid);
		_appendPropertyFilterString(
			sb, ConfigurationAdmin.SERVICE_FACTORYPID, rawPid + ".scoped");

		sb.append(StringPool.CLOSE_PARENTHESIS);

		String filterString = sb.toString();

		if (pid.contains("~")) {
			sb = new StringBundler(8);

			sb.append("(&");
			sb.append(filterString);

			_appendPropertyFilterString(sb, Constants.SERVICE_PID, pid);

			sb.append(StringPool.CLOSE_PARENTHESIS);

			filterString = sb.toString();
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