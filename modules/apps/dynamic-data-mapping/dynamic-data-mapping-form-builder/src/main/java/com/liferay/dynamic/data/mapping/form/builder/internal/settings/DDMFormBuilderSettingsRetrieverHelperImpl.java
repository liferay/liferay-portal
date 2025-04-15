/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.builder.internal.settings;

import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionRegistry;
import com.liferay.dynamic.data.mapping.form.builder.internal.util.DDMExpressionFunctionMetadata;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.spi.converter.SPIDDMFormRuleConverter;
import com.liferay.dynamic.data.mapping.spi.form.builder.settings.DDMFormBuilderSettingsRetrieverHelper;
import com.liferay.dynamic.data.mapping.util.comparator.StructureNameComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(service = DDMFormBuilderSettingsRetrieverHelper.class)
public class DDMFormBuilderSettingsRetrieverHelperImpl
	implements DDMFormBuilderSettingsRetrieverHelper {

	@Override
	public String getDDMDataProviderInstanceParameterSettingsURL() {
		String servletContextPath = _getServletContextPath(
			_ddmDataProviderInstanceParameterSettingsServlet);

		return servletContextPath.concat(
			"/dynamic-data-mapping-form-builder-provider-instance-parameter-" +
				"settings/");
	}

	@Override
	public String getDDMDataProviderInstancesURL() {
		String servletContextPath = _getServletContextPath(
			_ddmDataProviderInstancesServlet);

		return servletContextPath.concat(
			"/dynamic-data-mapping-form-builder-data-provider-instances/");
	}

	@Override
	public String getDDMFieldSetDefinitionURL() {
		String servletContextPath = _getServletContextPath(
			_ddmFieldSetDefinitionServlet);

		return servletContextPath.concat(
			"/dynamic-data-mapping-form-builder-fieldset-definition/");
	}

	@Override
	public String getDDMFieldSettingsDDMFormContextURL() {
		String servletContextPath = _getServletContextPath(
			_ddmFieldSettingsDDMFormContextServlet);

		return servletContextPath.concat(
			"/dynamic-data-mapping-form-builder-field-settings-form-context/");
	}

	@Override
	public String getDDMFormContextProviderURL() {
		String servletContextPath = _getServletContextPath(
			_ddmFormContextProviderServlet);

		return servletContextPath.concat(
			"/dynamic-data-mapping-form-context-provider/");
	}

	@Override
	public String getDDMFunctionsURL() {
		String servletContextPath = _getServletContextPath(
			_ddmFormFunctionsServlet);

		return servletContextPath.concat(
			"/dynamic-data-mapping-form-builder-functions/");
	}

	@Override
	public JSONArray getFieldSetsMetadataJSONArray(
		long companyId, long scopeGroupId, long fieldSetClassNameId,
		Locale locale) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		if (fieldSetClassNameId == 0) {
			return jsonArray;
		}

		List<DDMStructure> ddmStructures = _ddmStructureService.search(
			companyId, new long[] {scopeGroupId}, fieldSetClassNameId,
			StringPool.BLANK, DDMStructureConstants.TYPE_FRAGMENT,
			WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new StructureNameComparator(true));

		for (DDMStructure ddmStructure : ddmStructures) {
			JSONObject jsonObject = _jsonFactory.createJSONObject();

			jsonObject.put(
				"description", ddmStructure.getDescription(locale, true)
			).put(
				"icon", "forms"
			).put(
				"id", ddmStructure.getStructureId()
			).put(
				"name", ddmStructure.getName(locale, true)
			);

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	@Override
	public String getRolesURL() {
		String servletContextPath = _getServletContextPath(_rolesServlet);

		return servletContextPath.concat(
			"/dynamic-data-mapping-form-builder-roles/");
	}

	@Override
	public String getSerializedDDMExpressionFunctionsMetadata(Locale locale) {
		JSONSerializer jsonSerializer = _jsonFactory.createJSONSerializer();

		return jsonSerializer.serializeDeep(
			_getDDMExpressionFunctionsMetadata(locale));
	}

	@Override
	public String getSerializedDDMFormRules(DDMForm ddmForm) {
		JSONSerializer jsonSerializer = _jsonFactory.createJSONSerializer();

		return jsonSerializer.serializeDeep(
			_spiDDMFormRuleConverter.convert(ddmForm.getDDMFormRules()));
	}

	protected void populateCustomDDMExpressionFunctionsMetadata(
		Map<String, List<DDMExpressionFunctionMetadata>>
			ddmExpressionFunctionMetadatasMap,
		Locale locale) {

		Map<String, DDMExpressionFunction> customDDMExpressionFunctions =
			_ddmExpressionFunctionRegistry.getCustomDDMExpressionFunctions();

		for (Map.Entry<String, DDMExpressionFunction> entry :
				customDDMExpressionFunctions.entrySet()) {

			Method method = null;

			DDMExpressionFunction ddmExpressionFunction = entry.getValue();

			Class<?> clazz = ddmExpressionFunction.getClass();

			for (Method curMethod : clazz.getMethods()) {
				if (Objects.equals(curMethod.getName(), "apply") &&
					Objects.equals(curMethod.getReturnType(), Boolean.class)) {

					method = curMethod;

					break;
				}
			}

			if (method == null) {
				continue;
			}

			int parameterCount = method.getParameterCount();

			if (parameterCount > 2) {
				continue;
			}

			String label = ddmExpressionFunction.getLabel(locale);

			if (Validator.isNull(label)) {
				label = entry.getKey();
			}

			_addDDMExpressionFunctionMetadata(
				ddmExpressionFunctionMetadatasMap,
				new DDMExpressionFunctionMetadata(
					entry.getKey(), label, _TYPE_BOOLEAN,
					_getParameterClassNames(parameterCount, _TYPE_NUMBER)));
			_addDDMExpressionFunctionMetadata(
				ddmExpressionFunctionMetadatasMap,
				new DDMExpressionFunctionMetadata(
					entry.getKey(), label, _TYPE_BOOLEAN,
					_getParameterClassNames(parameterCount, _TYPE_TEXT)));
		}
	}

	protected void populateDDMExpressionFunctionsMetadata(
		Map<String, List<DDMExpressionFunctionMetadata>>
			ddmExpressionFunctionMetadatasMap,
		ResourceBundle resourceBundle) {

		_addDDMExpressionFunctionMetadata(
			ddmExpressionFunctionMetadatasMap,
			new DDMExpressionFunctionMetadata(
				"belongs-to", _language.get(resourceBundle, "belongs-to"),
				_TYPE_BOOLEAN, new String[] {_TYPE_USER, _TYPE_LIST}));
		_addDDMExpressionFunctionMetadata(
			ddmExpressionFunctionMetadatasMap,
			new DDMExpressionFunctionMetadata(
				"equals-to", _language.get(resourceBundle, "is-equal-to"),
				_TYPE_BOOLEAN, new String[] {_TYPE_BOOLEAN, _TYPE_BOOLEAN}));

		for (Map.Entry<String, String> entry : _binaryFunctions.entrySet()) {
			_addDDMExpressionFunctionMetadata(
				ddmExpressionFunctionMetadatasMap,
				new DDMExpressionFunctionMetadata(
					entry.getKey(),
					_language.get(resourceBundle, entry.getValue()),
					_TYPE_BOOLEAN, new String[] {_TYPE_NUMBER, _TYPE_NUMBER}));
			_addDDMExpressionFunctionMetadata(
				ddmExpressionFunctionMetadatasMap,
				new DDMExpressionFunctionMetadata(
					entry.getKey(),
					_language.get(resourceBundle, entry.getValue()),
					_TYPE_BOOLEAN, new String[] {_TYPE_TEXT, _TYPE_TEXT}));
		}

		for (Map.Entry<String, String> entry :
				_numberBinaryFunctions.entrySet()) {

			_addDDMExpressionFunctionMetadata(
				ddmExpressionFunctionMetadatasMap,
				new DDMExpressionFunctionMetadata(
					entry.getKey(),
					_language.get(resourceBundle, entry.getValue()),
					_TYPE_BOOLEAN, new String[] {_TYPE_NUMBER, _TYPE_NUMBER}));
		}

		for (Map.Entry<String, String> entry :
				_textBinaryFunctions.entrySet()) {

			_addDDMExpressionFunctionMetadata(
				ddmExpressionFunctionMetadatasMap,
				new DDMExpressionFunctionMetadata(
					entry.getKey(),
					_language.get(resourceBundle, entry.getValue()),
					_TYPE_BOOLEAN, new String[] {_TYPE_TEXT, _TYPE_TEXT}));
		}

		for (Map.Entry<String, String> entry : _unaryFunctions.entrySet()) {
			_addDDMExpressionFunctionMetadata(
				ddmExpressionFunctionMetadatasMap,
				new DDMExpressionFunctionMetadata(
					entry.getKey(),
					_language.get(resourceBundle, entry.getValue()),
					_TYPE_BOOLEAN, new String[] {_TYPE_NUMBER}));
			_addDDMExpressionFunctionMetadata(
				ddmExpressionFunctionMetadatasMap,
				new DDMExpressionFunctionMetadata(
					entry.getKey(),
					_language.get(resourceBundle, entry.getValue()),
					_TYPE_BOOLEAN, new String[] {_TYPE_TEXT}));
		}
	}

	private void _addDDMExpressionFunctionMetadata(
		Map<String, List<DDMExpressionFunctionMetadata>>
			ddmExpressionFunctionMetadatasMap,
		DDMExpressionFunctionMetadata ddmExpressionFunctionMetadata) {

		String firstParameterClassName =
			ddmExpressionFunctionMetadata.getParameterClassNames()[0];

		List<DDMExpressionFunctionMetadata> ddmExpressionFunctionMetadatas =
			ddmExpressionFunctionMetadatasMap.get(firstParameterClassName);

		if (ddmExpressionFunctionMetadatas == null) {
			ddmExpressionFunctionMetadatas = new ArrayList<>();

			ddmExpressionFunctionMetadatasMap.put(
				firstParameterClassName, ddmExpressionFunctionMetadatas);
		}

		ddmExpressionFunctionMetadatas.add(ddmExpressionFunctionMetadata);
	}

	private Map<String, List<DDMExpressionFunctionMetadata>>
		_getDDMExpressionFunctionsMetadata(Locale locale) {

		Map<String, List<DDMExpressionFunctionMetadata>>
			ddmExpressionFunctionMetadatasMap = new HashMap<>();

		populateCustomDDMExpressionFunctionsMetadata(
			ddmExpressionFunctionMetadatasMap, locale);
		populateDDMExpressionFunctionsMetadata(
			ddmExpressionFunctionMetadatasMap, _getResourceBundle(locale));

		return ddmExpressionFunctionMetadatasMap;
	}

	private String[] _getParameterClassNames(
		int parameterCount, String parameterClassName) {

		String[] parameterClassNames = new String[parameterCount];

		Arrays.fill(parameterClassNames, parameterClassName);

		return parameterClassNames;
	}

	private ResourceBundle _getResourceBundle(Locale locale) {
		ResourceBundle portalResourceBundle = _portal.getResourceBundle(locale);

		ResourceBundle portletResourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return new AggregateResourceBundle(
			portletResourceBundle, portalResourceBundle);
	}

	private String _getServletContextPath(Servlet servlet) {
		String proxyPath = _portal.getPathProxy();

		ServletConfig servletConfig = servlet.getServletConfig();

		ServletContext servletContext = servletConfig.getServletContext();

		return proxyPath.concat(servletContext.getContextPath());
	}

	private static final String _TYPE_BOOLEAN = "boolean";

	private static final String _TYPE_LIST = "list";

	private static final String _TYPE_NUMBER = "number";

	private static final String _TYPE_TEXT = "text";

	private static final String _TYPE_USER = "user";

	private static final Map<String, String> _binaryFunctions =
		LinkedHashMapBuilder.put(
			"equals-to", "is-equal-to"
		).put(
			"not-equals-to", "is-not-equal-to"
		).build();
	private static final Map<String, String> _numberBinaryFunctions =
		LinkedHashMapBuilder.put(
			"greater-than", "is-greater-than"
		).put(
			"greater-than-equals", "is-greater-than-or-equal-to"
		).put(
			"less-than", "is-less-than"
		).put(
			"less-than-equals", "is-less-than-or-equal-to"
		).build();
	private static final Map<String, String> _textBinaryFunctions =
		LinkedHashMapBuilder.put(
			"contains", "contains"
		).put(
			"not-contains", "does-not-contain"
		).build();
	private static final Map<String, String> _unaryFunctions =
		LinkedHashMapBuilder.put(
			"is-empty", "is-empty"
		).put(
			"not-is-empty", "is-not-empty"
		).build();

	@Reference(
		target = "(osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.builder.internal.servlet.DDMDataProviderInstanceParameterSettingsServlet)"
	)
	private Servlet _ddmDataProviderInstanceParameterSettingsServlet;

	@Reference(
		target = "(osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.builder.internal.servlet.DDMDataProviderInstancesServlet)"
	)
	private Servlet _ddmDataProviderInstancesServlet;

	@Reference
	private DDMExpressionFunctionRegistry _ddmExpressionFunctionRegistry;

	@Reference(
		target = "(osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.builder.internal.servlet.DDMFieldSetDefinitionServlet)"
	)
	private Servlet _ddmFieldSetDefinitionServlet;

	@Reference(
		target = "(osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.builder.internal.servlet.DDMFieldSettingsDDMFormContextServlet)"
	)
	private Servlet _ddmFieldSettingsDDMFormContextServlet;

	@Reference(
		target = "(osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.renderer.internal.servlet.DDMFormContextProviderServlet)"
	)
	private Servlet _ddmFormContextProviderServlet;

	@Reference(
		target = "(osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.builder.internal.servlet.DDMFormFunctionsServlet)"
	)
	private Servlet _ddmFormFunctionsServlet;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.builder.internal.servlet.RolesServlet)"
	)
	private Servlet _rolesServlet;

	@Reference
	private SPIDDMFormRuleConverter _spiDDMFormRuleConverter;

}