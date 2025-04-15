/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;

import jakarta.portlet.PortletRequest;

import java.io.Serializable;

import java.util.Locale;

/**
 * @author Eduardo Lundgren
 * @author Marcellus Tavares
 * @author Leonardo Barros
 */
public interface DDM {

	public static final String FIELD_EMPTY_VALUE = "_FIELD_EMPTY_VALUE_";

	public static final String FIELDS_DISPLAY_NAME = "_fieldsDisplay";

	public static final String INSTANCE_SEPARATOR = "_INSTANCE_";

	public DDMForm getDDMForm(long classNameId, long classPK)
		throws PortalException;

	public DDMForm getDDMForm(PortletRequest portletRequest)
		throws PortalException;

	public DDMForm getDDMForm(String serializedJSONDDMForm)
		throws PortalException;

	public JSONArray getDDMFormFieldsJSONArray(
		DDMStructure ddmStructure, String script);

	public JSONArray getDDMFormFieldsJSONArray(
		DDMStructureVersion ddmStructureVersion, String script);

	public String getDDMFormJSONString(DDMForm ddmForm);

	public DDMFormValues getDDMFormValues(
			DDMForm ddmForm, String serializedJSONDDMFormValues)
		throws PortalException;

	public DDMFormValues getDDMFormValues(
			long ddmStructureId, long ddmTemplateId, String fieldNamespace,
			ServiceContext serviceContext)
		throws PortalException;

	public DDMFormValues getDDMFormValues(
			long ddmStructureId, String fieldNamespace,
			ServiceContext serviceContext)
		throws PortalException;

	public String getDDMFormValuesJSONString(DDMFormValues ddmFormValues);

	public DDMFormLayout getDefaultDDMFormLayout(DDMForm ddmForm);

	public Serializable getDisplayFieldValue(
			ThemeDisplay themeDisplay, Serializable fieldValue, String type)
		throws Exception;

	public Fields getFields(long ddmStructureId, DDMFormValues ddmFormValues)
		throws PortalException;

	public Fields getFields(
			long ddmStructureId, long ddmTemplateId,
			ServiceContext serviceContext)
		throws PortalException;

	public Fields getFields(
			long ddmStructureId, long ddmTemplateId, String fieldNamespace,
			ServiceContext serviceContext)
		throws PortalException;

	public Fields getFields(long ddmStructureId, ServiceContext serviceContext)
		throws PortalException;

	public Fields getFields(
			long ddmStructureId, String fieldNamespace,
			ServiceContext serviceContext)
		throws PortalException;

	public Serializable getIndexedFieldValue(
			Serializable fieldValue, String type)
		throws Exception;

	public OrderByComparator<DDMStructure> getStructureOrderByComparator(
		String orderByCol, String orderByType);

	public OrderByComparator<DDMTemplate> getTemplateOrderByComparator(
		String orderByCol, String orderByType);

	public Fields mergeFields(Fields newFields, Fields existingFields);

	public DDMForm updateDDMFormDefaultLocale(
		DDMForm ddmForm, Locale newDefaultLocale);

}