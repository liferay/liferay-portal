/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.model.listener;

import com.liferay.headless.builder.application.APIApplication;
import com.liferay.headless.builder.constants.HeadlessBuilderConstants;
import com.liferay.headless.builder.internal.helper.ObjectEntryHelper;
import com.liferay.headless.builder.internal.helper.ValidationHelper;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.listener.RelevantObjectEntryModelListener;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio Jiménez del Coso
 */
@Component(service = RelevantObjectEntryModelListener.class)
public class APIEndpointRelevantObjectEntryModelListener
	extends BaseModelListener<ObjectEntry>
	implements RelevantObjectEntryModelListener {

	@Override
	public String getObjectDefinitionExternalReferenceCode() {
		return "L_API_ENDPOINT";
	}

	@Override
	public void onBeforeCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		_validate(objectEntry);
	}

	@Override
	public void onBeforeUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		if (!_equals(
				originalObjectEntry.getValues(), objectEntry.getValues(),
				"httpMethod", "path", "pathParameter",
				"pathParameterDescription", "retrieveType",
				"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
				"r_requestAPISchemaToAPIEndpoints_l_apiSchemaId",
				"r_responseAPISchemaToAPIEndpoints_l_apiSchemaId")) {

			_validate(objectEntry);
		}
	}

	private boolean _equals(
		Map<String, Serializable> map1, Map<String, Serializable> map2,
		String... keys) {

		for (String key : keys) {
			if (!Objects.equals(map1.get(key), map2.get(key))) {
				return false;
			}
		}

		return true;
	}

	private boolean _isValidPathParameter(
			long companyId, String pathParameter, long responseAPISchemaId)
		throws Exception {

		if (Objects.equals(
				pathParameter, HeadlessBuilderConstants.PATH_PARAMETER_ERC) ||
			Objects.equals(
				pathParameter, HeadlessBuilderConstants.PATH_PARAMETER_ID)) {

			return true;
		}

		ObjectEntry responseAPISchemaObjectEntry =
			_objectEntryLocalService.getObjectEntry(responseAPISchemaId);

		Map<String, Serializable> values =
			responseAPISchemaObjectEntry.getValues();

		List<String> uniqueObjectFields =
			_objectEntryHelper.getUniqueObjectFieldNames(
				companyId, (String)values.get("mainObjectDefinitionERC"));

		return uniqueObjectFields.contains(pathParameter);
	}

	private void _validate(ObjectEntry objectEntry) {
		try {
			Map<String, Serializable> values = objectEntry.getValues();

			long apiApplicationId = GetterUtil.getLong(
				values.get(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId"));

			if (!_validationHelper.isValidObjectEntry(
					"L_API_APPLICATION", apiApplicationId)) {

				throw new ObjectEntryValuesException.InvalidObjectField(
					null,
					"An API endpoint must be related to an API application",
					"an-api-endpoint-must-be-related-to-an-api-application");
			}

			long responseAPISchemaId = GetterUtil.getLong(
				values.get("r_responseAPISchemaToAPIEndpoints_l_apiSchemaId"));

			APIApplication.Endpoint.Scope scope =
				APIApplication.Endpoint.Scope.parse(
					(String)values.get("scope"));

			if (responseAPISchemaId != 0) {
				_validateAPISchema(
					apiApplicationId, responseAPISchemaId, scope);
			}

			long requestAPISchemaId = GetterUtil.getLong(
				values.get("r_requestAPISchemaToAPIEndpoints_l_apiSchemaId"));

			if (requestAPISchemaId != 0) {
				_validateAPISchema(apiApplicationId, requestAPISchemaId, scope);
			}

			Http.Method method = Http.Method.valueOf(
				StringUtil.toUpperCase((String)values.get("httpMethod")));

			if (Objects.equals(method, Http.Method.GET)) {
				_validateGetAPIEndpoint(objectEntry, responseAPISchemaId);
			}
			else if (Objects.equals(method, Http.Method.POST)) {
				_validatePostAPIEndpoint(objectEntry);
			}

			String pathParameter = (String)values.get("pathParameter");

			if (Validator.isNull(pathParameter) &&
				Validator.isNotNull(
					(String)values.get("pathParameterDescription"))) {

				throw new ObjectEntryValuesException.InvalidObjectField(
					null,
					"Path parameter description cannot be set with empty " +
						"path parameter property",
					"path-parameter-description-cannot-be-set-with-empty-" +
						"path-parameter-property");
			}

			String filterString = StringBundler.concat(
				"id ne '", objectEntry.getObjectEntryId(),
				"' and httpMethod eq '", values.get("httpMethod"),
				"' and path eq '", values.get("path"),
				"' and r_apiApplicationToAPIEndpoints_l_apiApplicationId eq '",
				values.get("r_apiApplicationToAPIEndpoints_l_apiApplicationId"),
				"'");
			ObjectDefinition apiEndpointObjectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					objectEntry.getObjectDefinitionId());

			int count = _objectEntryLocalService.getValuesListCount(
				new Long[] {objectEntry.getGroupId()},
				objectEntry.getCompanyId(), objectEntry.getUserId(),
				objectEntry.getObjectDefinitionId(),
				_filterFactory.create(
					filterString, apiEndpointObjectDefinition),
				false, null);

			if (count > 0) {
				throw new ObjectEntryValuesException.InvalidObjectField(
					null,
					"There is an API endpoint with the same HTTP method and " +
						"path",
					"there-is-an-api-endpoint-with-the-same-http-method-and-" +
						"path");
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _validateAPISchema(
			long apiApplicationId, long apiSchemaId,
			APIApplication.Endpoint.Scope scope)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			apiSchemaId);

		if (objectEntry == null) {
			throw new ObjectEntryValuesException.InvalidObjectField(
				null, "An API endpoint must be related to an API schema",
				"an-api-endpoint-must-be-related-to-an-api-schema");
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if (!Objects.equals(
				objectDefinition.getExternalReferenceCode(), "L_API_SCHEMA")) {

			throw new ObjectEntryValuesException.InvalidObjectField(
				null, "An API endpoint must be related to an API schema",
				"an-api-endpoint-must-be-related-to-an-api-schema");
		}

		Map<String, Serializable> apiSchemaValues = objectEntry.getValues();

		if (!Objects.equals(
				apiApplicationId,
				apiSchemaValues.get(
					"r_apiApplicationToAPISchemas_l_apiApplicationId"))) {

			throw new ObjectEntryValuesException.InvalidObjectField(
				null,
				"The API endpoint and the API schema must be related to the " +
					"same API Application",
				"the-api-endpoint-and-the-api-schema-must-be-related-to-the-" +
					"same-api-application");
		}

		Map<String, Serializable> values = objectEntry.getValues();

		ObjectDefinition mainObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					(String)values.get("mainObjectDefinitionERC"),
					objectEntry.getCompanyId());

		if (!Objects.equals(
				mainObjectDefinition.getScope(), scope.getValue())) {

			throw new ObjectEntryValuesException.InvalidObjectField(
				null,
				"The API endpoint and the API schema must have the same scope",
				"the-api-endpoint-and-the-api-schema-must-have-the-same-scope");
		}
	}

	private void _validateGetAPIEndpoint(
			ObjectEntry objectEntry, long responseAPISchemaId)
		throws Exception {

		Map<String, Serializable> values = objectEntry.getValues();

		String pathParameter = (String)values.get("pathParameter");
		String pathString = (String)values.get("path");

		if (Objects.equals(
				APIApplication.Endpoint.RetrieveType.parse(
					(String)values.get("retrieveType")),
				APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT)) {

			if (!Validator.isBlank(pathParameter) &&
				(responseAPISchemaId == 0)) {

				throw new ObjectEntryValuesException.InvalidObjectField(
					null,
					"Path parameter cannot be set without a response schema",
					"path-parameter-cannot-be-set-without-a-response-schema");
			}

			_validateSingleElementPath(
				objectEntry, pathParameter, pathString, responseAPISchemaId);
		}
		else {
			if (!Validator.isBlank(pathParameter)) {
				throw new ObjectEntryValuesException.InvalidObjectField(
					null,
					"Path parameters are not supported by GET API endpoints " +
						"with the \"collection\" retrieve type",
					"path-parameters-are-not-supported-by-get-api-endpoints-" +
						"with-the-collection-retrieve-type");
			}

			_validatePath(objectEntry, pathString);
		}
	}

	private void _validatePath(ObjectEntry objectEntry, String pathString)
		throws Exception {

		Matcher matcher = _pathPattern.matcher(pathString);

		if (!matcher.matches()) {
			User user = _userLocalService.getUser(objectEntry.getUserId());

			ObjectField objectField = _objectFieldLocalService.getObjectField(
				objectEntry.getObjectDefinitionId(), "path");

			String message =
				"%s can have a maximum of 255 alphanumeric characters";
			String messageKey =
				"x-can-have-a-maximum-of-255-alphanumeric-characters";

			if (!pathString.startsWith(StringPool.FORWARD_SLASH)) {
				message = "%s must start with the \"/\" character";
				messageKey = "x-must-start-with-the-x-character";
			}

			if (!StringUtil.isLowerCase(pathString)) {
				message = "%s must contain only lower case characters";
				messageKey = "x-must-contain-only-lower-case-characters";
			}

			String label = objectField.getLabel(user.getLocale());

			throw new ObjectEntryValuesException.InvalidObjectField(
				Arrays.asList(label, "\"/\""), String.format(message, label),
				messageKey);
		}
	}

	private void _validatePostAPIEndpoint(ObjectEntry objectEntry)
		throws Exception {

		Map<String, Serializable> values = objectEntry.getValues();

		if (Objects.equals(
				APIApplication.Endpoint.RetrieveType.parse(
					(String)values.get("retrieveType")),
				APIApplication.Endpoint.RetrieveType.COLLECTION)) {

			throw new ObjectEntryValuesException.InvalidObjectField(
				Collections.singletonList("singleElement"),
				"POST API endpoints retrieve type must be \"singleElement\"",
				"post-api-endpoints-retrieve-type-must-be-x");
		}

		String pathParameter = (String)values.get("pathParameter");

		if (!Validator.isBlank(pathParameter)) {
			throw new ObjectEntryValuesException.InvalidObjectField(
				null, "Path parameters are not supported by POST API endpoints",
				"path-parameters-are-not-supported-by-post-api-endpoints");
		}

		String pathString = (String)values.get("path");

		_validatePath(objectEntry, pathString);
	}

	private void _validateSingleElementPath(
			ObjectEntry objectEntry, String pathParameter, String pathString,
			long responseAPISchemaId)
		throws Exception {

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectEntry.getObjectDefinitionId(), "path");

		User user = _userLocalService.getUser(objectEntry.getUserId());

		if (!pathString.startsWith(StringPool.FORWARD_SLASH)) {
			throw new ObjectEntryValuesException.InvalidObjectField(
				Arrays.asList(objectField.getLabel(user.getLocale()), "\"/\""),
				"%s must start with the %s character",
				"x-must-start-with-the-x-character");
		}

		String pathInParameterString = StringUtil.extractLast(
			pathString, StringPool.FORWARD_SLASH);

		if (!StringUtil.isLowerCase(
				StringUtil.extractFirst(pathString, pathInParameterString))) {

			throw new ObjectEntryValuesException.InvalidObjectField(
				Collections.singletonList(
					objectField.getLabel(user.getLocale())),
				"%s must contain only lower case characters",
				"x-must-contain-only-lower-case-characters");
		}

		if (!Validator.isBlank(pathParameter) && (responseAPISchemaId != 0) &&
			!_isValidPathParameter(
				objectEntry.getCompanyId(), pathParameter,
				responseAPISchemaId)) {

			throw new ObjectEntryValuesException.InvalidObjectField(
				null,
				"Path parameter must be an external reference code, ID, or " +
					"unique field",
				"path-parameter-must-be-an-external-reference-code,-id,-or-" +
					"unique-field");
		}

		Map<String, Serializable> values = objectEntry.getValues();

		if (Objects.equals(
				APIApplication.Endpoint.Scope.parse(
					(String)values.get("scope")),
				APIApplication.Endpoint.Scope.SITE) &&
			Objects.equals(
				pathParameter, HeadlessBuilderConstants.PATH_PARAMETER_ID)) {

			throw new ObjectEntryValuesException.InvalidObjectField(
				Collections.singletonList(
					objectField.getLabel(user.getLocale())),
				"Single element ID endpoint cannot be scoped by site",
				"single-element-id-endpoint-cannot-be-scoped-by-site");
		}

		Matcher singleElementPathMatcher = _singleElementPathPattern.matcher(
			pathString);

		if (!singleElementPathMatcher.matches()) {
			throw new ObjectEntryValuesException.InvalidObjectField(
				Arrays.asList(objectField.getLabel(user.getLocale())),
				"%s can have a maximum of 255 alphanumeric characters",
				"x-can-have-a-maximum-of-255-alphanumeric-characters");
		}

		Matcher curlyBraceMatcher = _curlyBracePattern.matcher(
			pathInParameterString);

		if (!curlyBraceMatcher.matches()) {
			throw new ObjectEntryValuesException.InvalidObjectField(
				Arrays.asList(objectField.getLabel(user.getLocale())),
				"%s must contain a path parameter between curly braces",
				"x-must-contain-a-path-parameter-between-curly-braces");
		}
	}

	private static final Pattern _curlyBracePattern = Pattern.compile(
		"^\\{[a-zA-Z0-9]+\\}$");
	private static final Pattern _pathPattern = Pattern.compile(
		"/[a-z0-9][a-z0-9-/]{0,253}");
	private static final Pattern _singleElementPathPattern = Pattern.compile(
		"/[a-zA-Z0-9][a-zA-Z0-9-/-{\\-}]{0,253}");

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryHelper _objectEntryHelper;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private ValidationHelper _validationHelper;

}