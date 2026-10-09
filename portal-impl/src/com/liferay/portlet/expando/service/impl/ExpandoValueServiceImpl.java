/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.impl;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.permission.ExpandoColumnPermissionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceMode;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.expando.service.base.ExpandoValueServiceBaseImpl;

import java.io.Serializable;

import java.util.Collection;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class ExpandoValueServiceImpl extends ExpandoValueServiceBaseImpl {

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Object data)
		throws PortalException {

		ExpandoColumnPermissionUtil.check(
			getPermissionChecker(),
			_expandoColumnLocalService.getColumn(
				companyId, _classNameLocalService.getClassNameId(className),
				tableName, columnName),
			ActionKeys.UPDATE);

		return expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, String data)
		throws PortalException {

		ExpandoColumn expandoColumn = _expandoColumnLocalService.getColumn(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, columnName);

		ExpandoColumnPermissionUtil.check(
			getPermissionChecker(), expandoColumn, ActionKeys.UPDATE);

		return expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public void addValues(
			long companyId, String className, String tableName, long classPK,
			Map<String, Serializable> attributeValues)
		throws PortalException {

		for (Map.Entry<String, Serializable> entry :
				attributeValues.entrySet()) {

			addValue(
				companyId, className, tableName, entry.getKey(), classPK,
				entry.getValue());
		}
	}

	@Override
	public Map<String, Serializable> getData(
			long companyId, String className, String tableName,
			Collection<String> columnNames, long classPK)
		throws PortalException {

		Map<String, Serializable> attributeValues =
			expandoValueLocalService.getData(
				companyId, className, tableName, columnNames, classPK);

		for (String columnName : columnNames) {
			ExpandoColumn expandoColumn = _expandoColumnLocalService.getColumn(
				companyId, _classNameLocalService.getClassNameId(className),
				tableName, columnName);

			if (ExpandoColumnPermissionUtil.contains(
					getPermissionChecker(), expandoColumn, ActionKeys.VIEW)) {

				continue;
			}

			attributeValues.remove(columnName);
		}

		return attributeValues;
	}

	@Override
	public Serializable getData(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws PortalException {

		ExpandoColumn column = _expandoColumnLocalService.getColumn(
			companyId, className, tableName, columnName);

		if ((column != null) &&
			ExpandoColumnPermissionUtil.contains(
				getPermissionChecker(), column, ActionKeys.VIEW)) {

			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK);
		}

		return null;
	}

	@Override
	public JSONObject getJSONData(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws PortalException {

		ExpandoColumn column = _expandoColumnLocalService.getColumn(
			companyId, className, tableName, columnName);

		if ((column == null) ||
			!ExpandoColumnPermissionUtil.contains(
				getPermissionChecker(), column, ActionKeys.VIEW)) {

			return null;
		}

		String data = String.valueOf(
			expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK));

		if (Validator.isNull(data)) {
			return null;
		}

		if (data.startsWith(StringPool.OPEN_CURLY_BRACE)) {
			return JSONFactoryUtil.createJSONObject(data);
		}

		return JSONUtil.put("data", data);
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = ExpandoColumnLocalService.class)
	private ExpandoColumnLocalService _expandoColumnLocalService;

}