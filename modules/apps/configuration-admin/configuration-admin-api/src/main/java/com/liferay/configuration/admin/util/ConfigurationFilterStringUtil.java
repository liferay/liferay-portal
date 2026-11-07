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

		StringBundler sb = new StringBundler(11);

		_appendCompanyScopedFilterString(sb, companyId, virtualInstanceId);

		return sb.toString();
	}

	public static String getCompanyScopedFilterString(
		Serializable companyId, String pid, String virtualInstanceId) {

		StringBundler sb = new StringBundler(31);

		sb.append("(&");

		_appendScopedFilterString(sb, pid);
		_appendCompanyScopedFilterString(sb, companyId, virtualInstanceId);

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	public static String getGroupScopedFilterString(
		Serializable companyId, Serializable groupId,
		String siteExternalReferenceCode) {

		StringBundler sb = new StringBundler(13);

		_appendGroupScopedFilterString(
			sb, companyId, groupId, siteExternalReferenceCode);

		return sb.toString();
	}

	public static String getGroupScopedFilterString(
		Serializable companyId, Serializable groupId, String pid,
		String siteExternalReferenceCode) {

		StringBundler sb = new StringBundler(33);

		sb.append("(&");

		_appendScopedFilterString(sb, pid);
		_appendGroupScopedFilterString(
			sb, companyId, groupId, siteExternalReferenceCode);

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	public static String getPortletScopedFilterString(
		Serializable portletInstanceId) {

		StringBundler sb = new StringBundler(5);

		_appendPortletScopedFilterString(sb, portletInstanceId);

		return sb.toString();
	}

	public static String getPortletScopedFilterString(
		String pid, Serializable portletInstanceId) {

		StringBundler sb = new StringBundler(25);

		sb.append("(&");

		_appendScopedFilterString(sb, pid);
		_appendPortletScopedFilterString(sb, portletInstanceId);

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	public static String getSystemScopedFilterString() {
		StringBundler sb = new StringBundler(9);

		_appendSystemScopedFilterString(sb);

		return sb.toString();
	}

	public static String getSystemScopedFilterString(String pid) {
		StringBundler sb = new StringBundler(22);

		if (pid.contains("~")) {
			sb.append("(&(&");

			_appendPropertyFilterString(
				sb, ConfigurationAdmin.SERVICE_FACTORYPID,
				ConfigurationPidUtil.getRawPid(pid));
		}
		else {
			sb.append("(&(|");

			_appendPropertyFilterString(
				sb, ConfigurationAdmin.SERVICE_FACTORYPID, pid);
		}

		_appendPropertyFilterString(sb, Constants.SERVICE_PID, pid);

		sb.append(StringPool.CLOSE_PARENTHESIS);

		_appendSystemScopedFilterString(sb);

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	private static void _appendCompanyScopedFilterString(
		StringBundler sb, Serializable companyId, String virtualInstanceId) {

		sb.append("(&(|(");
		sb.append(ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey());
		sb.append(StringPool.EQUAL);
		sb.append(_toString(companyId));
		sb.append(")(dxp.lxc.liferay.com.virtualInstanceId=");
		sb.append(
			GetterUtil.get(
				SystemBundleUtil.escapeFilterValue(virtualInstanceId), "*"));

		if (Objects.equals(
				virtualInstanceId, PropsValues.COMPANY_DEFAULT_WEB_ID)) {

			sb.append(")(dxp.lxc.liferay.com.virtualInstanceId=default))(!(");
		}
		else {
			sb.append("))(!(");
		}

		sb.append(ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey());
		sb.append("=*))(!(siteExternalReferenceCode=*))(!(");
		sb.append(
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
				getPropertyKey());
		sb.append("=*)))");
	}

	private static void _appendGroupScopedFilterString(
		StringBundler sb, Serializable companyId, Serializable groupId,
		String siteExternalReferenceCode) {

		if ((companyId == null) ||
			(GetterUtil.getLong(companyId) == CompanyConstants.SYSTEM)) {

			throw new IllegalArgumentException(
				"A valid company is expected when building a group scoped " +
					"configuration filter string");
		}

		sb.append("(&(|(");
		sb.append(ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey());
		sb.append(StringPool.EQUAL);
		sb.append(_toString(groupId));
		sb.append(")(siteExternalReferenceCode=");
		sb.append(
			GetterUtil.get(
				SystemBundleUtil.escapeFilterValue(siteExternalReferenceCode),
				"*"));
		sb.append("))(");
		sb.append(ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey());
		sb.append(StringPool.EQUAL);
		sb.append(_toString(companyId));
		sb.append(")(!(");
		sb.append(
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
				getPropertyKey());
		sb.append("=*)))");
	}

	private static void _appendPortletScopedFilterString(
		StringBundler sb, Serializable portletInstanceId) {

		sb.append("(&(");
		sb.append(
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
				getPropertyKey());
		sb.append(StringPool.EQUAL);
		sb.append(_toString(portletInstanceId));
		sb.append("))");
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

	private static void _appendScopedFilterString(
		StringBundler sb, String pid) {

		boolean factoryEntry = pid.contains("~");

		if (factoryEntry) {
			sb.append("(&(|");
		}
		else {
			sb.append("(|");
		}

		String rawPid = ConfigurationPidUtil.getRawPid(pid);

		_appendPropertyFilterString(
			sb, ConfigurationAdmin.SERVICE_FACTORYPID, rawPid);
		_appendPropertyFilterString(
			sb, ConfigurationAdmin.SERVICE_FACTORYPID, rawPid + ".scoped");

		sb.append(StringPool.CLOSE_PARENTHESIS);

		if (factoryEntry) {
			_appendPropertyFilterString(sb, Constants.SERVICE_PID, pid);

			sb.append(StringPool.CLOSE_PARENTHESIS);
		}
	}

	private static void _appendSystemScopedFilterString(StringBundler sb) {
		sb.append("(&(|(!(");
		sb.append(ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey());
		sb.append("=*))(");
		sb.append(ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey());
		sb.append("=0))(!(dxp.lxc.liferay.com.virtualInstanceId=*))(!(");
		sb.append(ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey());
		sb.append("=*))(!(siteExternalReferenceCode=*))(!(");
		sb.append(
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
				getPropertyKey());
		sb.append("=*)))");
	}

	private static String _toString(Serializable value) {
		if (value == null) {
			return StringPool.STAR;
		}

		return SystemBundleUtil.escapeFilterValue(String.valueOf(value));
	}

}