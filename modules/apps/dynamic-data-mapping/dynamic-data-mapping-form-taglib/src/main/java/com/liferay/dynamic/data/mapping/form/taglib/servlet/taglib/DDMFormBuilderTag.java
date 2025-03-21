/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.taglib.servlet.taglib;

import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormBuilderContextFactory;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormBuilderContextRequest;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormBuilderContextResponse;
import com.liferay.dynamic.data.mapping.form.builder.settings.DDMFormBuilderSettingsRequest;
import com.liferay.dynamic.data.mapping.form.builder.settings.DDMFormBuilderSettingsResponse;
import com.liferay.dynamic.data.mapping.form.builder.settings.DDMFormBuilderSettingsRetriever;
import com.liferay.dynamic.data.mapping.form.taglib.servlet.taglib.base.BaseDDMFormBuilderTag;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalServiceUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Rafael Praxedes
 */
public class DDMFormBuilderTag extends BaseDDMFormBuilderTag {

	public String getDDMFormBuilderContext(
		HttpServletRequest httpServletRequest) {

		String serializedFormBuilderContext = ParamUtil.getString(
			httpServletRequest, "serializedFormBuilderContext");

		if (Validator.isNotNull(serializedFormBuilderContext)) {
			return serializedFormBuilderContext;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		long ddmStructureId = GetterUtil.getLong(getDdmStructureId());

		DDMStructure ddmStructure =
			DDMStructureLocalServiceUtil.fetchDDMStructure(ddmStructureId);

		long ddmStructureVersionId = GetterUtil.getLong(
			getDdmStructureVersionId());

		DDMStructureVersion ddmStructureVersion =
			DDMStructureVersionLocalServiceUtil.fetchDDMStructureVersion(
				ddmStructureVersionId);

		Locale locale = themeDisplay.getSiteDefaultLocale();

		if ((ddmStructure != null) || (ddmStructureVersion != null)) {
			DDMForm ddmForm = getDDMForm(ddmStructureId, ddmStructureVersionId);

			locale = ddmForm.getDefaultLocale();
		}

		DDMFormBuilderContextRequest ddmFormBuilderContextRequest =
			DDMFormBuilderContextRequest.with(
				ddmStructure, themeDisplay.getRequest(),
				themeDisplay.getResponse(), locale, true);

		ddmFormBuilderContextRequest.addProperty(
			"ddmStructureVersion", ddmStructureVersion);

		DDMFormBuilderContextFactory ddmFormBuilderContextFactory =
			_ddmFormBuilderContextFactorySnapshot.get();

		DDMFormBuilderContextResponse ddmFormBuilderContextResponse =
			ddmFormBuilderContextFactory.create(ddmFormBuilderContextRequest);

		return jsonSerializer.serializeDeep(
			ddmFormBuilderContextResponse.getContext());
	}

	protected DDMForm getDDMForm(
		long ddmStructureId, long ddmStructureVersionId) {

		if (ddmStructureVersionId > 0) {
			DDMStructureVersion ddmStructureVersion =
				DDMStructureVersionLocalServiceUtil.fetchDDMStructureVersion(
					ddmStructureVersionId);

			if (ddmStructureVersion != null) {
				return ddmStructureVersion.getDDMForm();
			}
		}

		if (ddmStructureId > 0) {
			DDMStructure ddmStructure =
				DDMStructureLocalServiceUtil.fetchDDMStructure(ddmStructureId);

			if (ddmStructure != null) {
				return ddmStructure.getDDMForm();
			}
		}

		return new DDMForm();
	}

	protected DDMFormBuilderSettingsResponse getDDMFormBuilderSettings(
		HttpServletRequest httpServletRequest) {

		DDMFormBuilderSettingsRetriever ddmFormBuilderSettingsRetriever =
			_ddmFormBuilderSettingsRetrieverSnapshot.get();

		if (ddmFormBuilderSettingsRetriever == null) {
			throw new IllegalStateException();
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return ddmFormBuilderSettingsRetriever.getSettings(
			DDMFormBuilderSettingsRequest.with(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
				getFieldSetClassNameId(),
				getDDMForm(
					GetterUtil.getLong(getDdmStructureId()),
					GetterUtil.getLong(getDdmStructureVersionId())),
				themeDisplay.getLocale()));
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		DDMFormBuilderSettingsResponse ddmFormBuilderSettingsResponse =
			getDDMFormBuilderSettings(httpServletRequest);

		setNamespacedAttribute(
			httpServletRequest, "dataProviderInstancesURL",
			ddmFormBuilderSettingsResponse.getDataProviderInstancesURL());
		setNamespacedAttribute(
			httpServletRequest, "dataProviderInstanceParameterSettingsURL",
			ddmFormBuilderSettingsResponse.
				getDataProviderInstanceParameterSettingsURL());
		setNamespacedAttribute(
			httpServletRequest, "evaluatorURL",
			ddmFormBuilderSettingsResponse.getFormContextProviderURL());
		setNamespacedAttribute(
			httpServletRequest, "fieldSets",
			ddmFormBuilderSettingsResponse.getFieldSets());
		setNamespacedAttribute(
			httpServletRequest, "fieldSetDefinitionURL",
			ddmFormBuilderSettingsResponse.getFieldSetDefinitionURL());
		setNamespacedAttribute(
			httpServletRequest, "fieldSettingsDDMFormContextURL",
			ddmFormBuilderSettingsResponse.getFieldSettingsDDMFormContextURL());

		setNamespacedAttribute(
			httpServletRequest, "formBuilderContext",
			getDDMFormBuilderContext(httpServletRequest));
		setNamespacedAttribute(
			httpServletRequest, "functionsMetadata",
			ddmFormBuilderSettingsResponse.getFunctionsMetadata());
		setNamespacedAttribute(
			httpServletRequest, "functionsURL",
			ddmFormBuilderSettingsResponse.getFunctionsURL());

		NPMResolver npmResolver = _npmResolverSnapshot.get();

		setNamespacedAttribute(
			httpServletRequest, "npmResolvedPackageName",
			npmResolver.resolveModuleName("dynamic-data-mapping-form-builder"));

		setNamespacedAttribute(
			httpServletRequest, "rolesURL",
			ddmFormBuilderSettingsResponse.getRolesURL());
		setNamespacedAttribute(
			httpServletRequest, "serializedDDMFormRules",
			ddmFormBuilderSettingsResponse.getSerializedDDMFormRules());
	}

	private static final Snapshot<DDMFormBuilderContextFactory>
		_ddmFormBuilderContextFactorySnapshot = new Snapshot<>(
			DDMFormBuilderTag.class, DDMFormBuilderContextFactory.class);
	private static final Snapshot<DDMFormBuilderSettingsRetriever>
		_ddmFormBuilderSettingsRetrieverSnapshot = new Snapshot<>(
			DDMFormBuilderTag.class, DDMFormBuilderSettingsRetriever.class);
	private static final Snapshot<NPMResolver> _npmResolverSnapshot =
		new Snapshot<>(DDMFormBuilderTag.class, NPMResolver.class);

}