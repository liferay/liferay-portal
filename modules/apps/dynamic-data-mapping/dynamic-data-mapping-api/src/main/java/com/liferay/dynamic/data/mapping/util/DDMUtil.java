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
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;

import jakarta.portlet.PortletRequest;

import java.io.Serializable;

import java.util.Locale;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Eduardo Lundgren
 * @author Marcellus Tavares
 * @author Leonardo Barros
 */
public class DDMUtil {

	public static DDMForm getDDMForm(long classNameId, long classPK)
		throws PortalException {

		return getDDM().getDDMForm(classNameId, classPK);
	}

	public static DDMForm getDDMForm(PortletRequest portletRequest)
		throws PortalException {

		return getDDM().getDDMForm(portletRequest);
	}

	public static DDMForm getDDMForm(String serializedJSONDDMForm)
		throws PortalException {

		return getDDM().getDDMForm(serializedJSONDDMForm);
	}

	public static JSONArray getDDMFormFieldsJSONArray(
		DDMStructure ddmStructure, String script) {

		return getDDM().getDDMFormFieldsJSONArray(ddmStructure, script);
	}

	public static JSONArray getDDMFormFieldsJSONArray(
		DDMStructureVersion ddmStructureVersion, String script) {

		return getDDM().getDDMFormFieldsJSONArray(ddmStructureVersion, script);
	}

	public static String getDDMFormJSONString(DDMForm ddmForm) {
		return getDDM().getDDMFormJSONString(ddmForm);
	}

	public static DDMFormValues getDDMFormValues(
			DDMForm ddmForm, String serializedJSONDDMFormValues)
		throws PortalException {

		return getDDM().getDDMFormValues(ddmForm, serializedJSONDDMFormValues);
	}

	public static DDMFormValues getDDMFormValues(
			long ddmStructureId, String fieldNamespace,
			ServiceContext serviceContext)
		throws PortalException {

		return getDDM().getDDMFormValues(
			ddmStructureId, fieldNamespace, serviceContext);
	}

	public static String getDDMFormValuesJSONString(
		DDMFormValues ddmFormValues) {

		return getDDM().getDDMFormValuesJSONString(ddmFormValues);
	}

	public static DDMFormLayout getDefaultDDMFormLayout(DDMForm ddmForm) {
		return getDDM().getDefaultDDMFormLayout(ddmForm);
	}

	public static Serializable getDisplayFieldValue(
			ThemeDisplay themeDisplay, Serializable fieldValue, String type)
		throws Exception {

		return getDDM().getDisplayFieldValue(themeDisplay, fieldValue, type);
	}

	public static Fields getFields(
			long ddmStructureId, DDMFormValues ddmFormValues)
		throws PortalException {

		return getDDM().getFields(ddmStructureId, ddmFormValues);
	}

	public static Fields getFields(
			long ddmStructureId, long ddmTemplateId,
			ServiceContext serviceContext)
		throws PortalException {

		return getDDM().getFields(
			ddmStructureId, ddmTemplateId, serviceContext);
	}

	public static Fields getFields(
			long ddmStructureId, long ddmTemplateId, String fieldNamespace,
			ServiceContext serviceContext)
		throws PortalException {

		return getDDM().getFields(
			ddmStructureId, ddmTemplateId, fieldNamespace, serviceContext);
	}

	public static Fields getFields(
			long ddmStructureId, ServiceContext serviceContext)
		throws PortalException {

		return getDDM().getFields(ddmStructureId, serviceContext);
	}

	public static Fields getFields(
			long ddmStructureId, String fieldNamespace,
			ServiceContext serviceContext)
		throws PortalException {

		return getDDM().getFields(
			ddmStructureId, fieldNamespace, serviceContext);
	}

	public static Serializable getIndexedFieldValue(
			Serializable fieldValue, String type)
		throws Exception {

		return getDDM().getIndexedFieldValue(fieldValue, type);
	}

	public static OrderByComparator<DDMStructure> getStructureOrderByComparator(
		String orderByCol, String orderByType) {

		return getDDM().getStructureOrderByComparator(orderByCol, orderByType);
	}

	public static OrderByComparator<DDMTemplate> getTemplateOrderByComparator(
		String orderByCol, String orderByType) {

		return getDDM().getTemplateOrderByComparator(orderByCol, orderByType);
	}

	public static Fields mergeFields(Fields newFields, Fields existingFields) {
		return getDDM().mergeFields(newFields, existingFields);
	}

	public static DDMForm updateDDMFormDefaultLocale(
		DDMForm ddmForm, Locale newDefaultLocale) {

		return getDDM().updateDDMFormDefaultLocale(ddmForm, newDefaultLocale);
	}

	protected static DDM getDDM() {
		return _serviceTracker.getService();
	}

	private static final ServiceTracker<DDM, DDM> _serviceTracker =
		ServiceTrackerFactory.open(
			FrameworkUtil.getBundle(DDMUtil.class), DDM.class);

}