/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.internal.renderer.v2_0;

import com.liferay.data.engine.renderer.DataLayoutRenderer;
import com.liferay.data.engine.renderer.DataLayoutRendererContext;
import com.liferay.data.engine.rest.internal.dto.v2_0.util.MapToDDMFormValuesConverterUtil;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcela Cunha
 */
@Component(service = DataLayoutRenderer.class)
public class DataLayoutRendererImpl implements DataLayoutRenderer {

	@Override
	public String render(
			Long dataLayoutId,
			DataLayoutRendererContext dataLayoutRendererContext)
		throws Exception {

		DDMStructureLayout ddmStructureLayout =
			_ddmStructureLayoutLocalService.getStructureLayout(dataLayoutId);

		DDMStructureVersion ddmStructureVersion =
			_ddmStructureVersionLocalService.getDDMStructureVersion(
				ddmStructureLayout.getStructureVersionId());

		DDMStructure ddmStructure = ddmStructureVersion.getStructure();

		DDMForm ddmForm = ddmStructure.getDDMForm();

		long groupId = ParamUtil.getLong(
			dataLayoutRendererContext.getHttpServletRequest(), "groupId",
			ddmStructure.getGroupId());

		return _ddmFormRenderer.render(
			ddmForm, ddmStructureLayout.getDDMFormLayout(),
			_toDDMFormRenderingContext(
				dataLayoutId, dataLayoutRendererContext, ddmForm, groupId));
	}

	private DDMFormRenderingContext _toDDMFormRenderingContext(
			Long dataLayoutId,
			DataLayoutRendererContext dataLayoutRendererContext,
			DDMForm ddmForm, long groupId)
		throws Exception {

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		if (Validator.isNotNull(dataLayoutRendererContext.getContentType())) {
			ddmFormRenderingContext.addProperty(
				"contentType", dataLayoutRendererContext.getContentType());
		}

		if (Validator.isNotNull(
				dataLayoutRendererContext.getDefaultLanguageId())) {

			ddmFormRenderingContext.addProperty(
				"defaultLanguageId",
				dataLayoutRendererContext.getDefaultLanguageId());
		}

		ddmFormRenderingContext.addProperty(
			"persistDefaultValues",
			dataLayoutRendererContext.isPersistDefaultValues());
		ddmFormRenderingContext.addProperty(
			"persisted", dataLayoutRendererContext.isPersisted());
		ddmFormRenderingContext.setContainerId(
			dataLayoutRendererContext.getContainerId());
		ddmFormRenderingContext.setDDMFormValues(
			MapToDDMFormValuesConverterUtil.toDDMFormValues(
				dataLayoutRendererContext.getDataRecordValues(), ddmForm,
				null));
		ddmFormRenderingContext.setDDMStructureLayoutId(dataLayoutId);
		ddmFormRenderingContext.setDisableFieldRepetition(
			dataLayoutRendererContext.isDisableFieldRepetition());
		ddmFormRenderingContext.setGroupId(groupId);
		ddmFormRenderingContext.setEditOnlyInDefaultLanguage(true);
		ddmFormRenderingContext.setHttpServletRequest(
			dataLayoutRendererContext.getHttpServletRequest());
		ddmFormRenderingContext.setHttpServletResponse(
			dataLayoutRendererContext.getHttpServletResponse());

		Locale locale = null;

		String languageId = ParamUtil.get(
			dataLayoutRendererContext.getHttpServletRequest(), "languageId",
			dataLayoutRendererContext.getLanguageId());

		if (Validator.isNull(languageId)) {
			locale = ddmForm.getDefaultLocale();
		}
		else {
			locale = LocaleUtil.fromLanguageId(languageId);
		}

		ddmFormRenderingContext.setLocale(locale);
		ddmFormRenderingContext.setPortletNamespace(
			dataLayoutRendererContext.getPortletNamespace());
		ddmFormRenderingContext.setReadOnly(
			dataLayoutRendererContext.isReadOnly());
		ddmFormRenderingContext.setShowSubmitButton(false);
		ddmFormRenderingContext.setSubmittable(
			dataLayoutRendererContext.isSubmittable());
		ddmFormRenderingContext.setViewMode(true);

		return ddmFormRenderingContext;
	}

	@Reference
	private DDMFormRenderer _ddmFormRenderer;

	@Reference
	private DDMStructureLayoutLocalService _ddmStructureLayoutLocalService;

	@Reference
	private DDMStructureVersionLocalService _ddmStructureVersionLocalService;

}