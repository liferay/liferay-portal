/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.properties.resource;

import com.liferay.talend.LiferayDefinition;
import com.liferay.talend.common.daikon.DaikonUtil;
import com.liferay.talend.common.headless.HeadlessUtil;
import com.liferay.talend.common.oas.OASExplorer;
import com.liferay.talend.common.oas.OASParameter;
import com.liferay.talend.common.oas.OASSource;
import com.liferay.talend.common.schema.SchemaBuilder;
import com.liferay.talend.common.schema.SchemaUtils;
import com.liferay.talend.common.util.StringUtil;
import com.liferay.talend.internal.oas.LiferayOASSource;
import com.liferay.talend.properties.connection.LiferayConnectionProperties;
import com.liferay.talend.properties.parameters.RequestParameter;
import com.liferay.talend.properties.parameters.RequestParameterProperties;

import java.net.URI;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.json.JsonObject;

import javax.ws.rs.core.UriBuilder;

import org.apache.avro.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.EnumProperty;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.StringProperty;

/**
 * @author Igor Beslic
 */
public class LiferayResourceProperties extends ComponentPropertiesImpl {

	public LiferayResourceProperties(String name) {
		super(name);
	}

	public ValidationResult afterEndpoint() {
		LiferayOASSource liferayOASSource =
			LiferayDefinition.getLiferayOASSource(this);

		if (!liferayOASSource.isValid()) {
			return liferayOASSource.getValidationResult();
		}

		return _afterEndpoint(liferayOASSource);
	}

	public ValidationResult afterOperations() {
		LiferayOASSource liferayOASSource = _getLiferayOASSource();

		if (!liferayOASSource.isValid()) {
			return liferayOASSource.getValidationResult();
		}

		Operation operation = operations.getValue();

		if (operation == Operation.Unavailable) {
			_resetProperties();

			return liferayOASSource.getValidationResult();
		}

		_setupRequestParameterProperties();

		_updateRequestParameterProperties();

		_updateSchemas(liferayOASSource);

		return ValidationResult.OK;
	}

	public ValidationResult beforeEndpoint() {
		ValidationResult validationResult = _validateOpenAPIModule();

		if (validationResult.getStatus() == ValidationResult.Result.ERROR) {
			return validationResult;
		}

		LiferayOASSource liferayOASSource = _getLiferayOASSource();

		if (!liferayOASSource.isValid()) {
			return liferayOASSource.getValidationResult();
		}

		String message = getI18nMessage("error.validation.resources");

		try {
			Set<String> endpoints = StringUtil.stripPrefix(
				_getOpenAPIModuleVersionPath(), _getPaths(liferayOASSource));

			if (!endpoints.isEmpty()) {
				endpoint.setPossibleNamedThingValues(
					DaikonUtil.toNamedThings(endpoints));

				return liferayOASSource.getValidationResult();
			}
		}
		catch (Exception exception) {
			message = exception.getMessage();
		}

		return new ValidationResult(ValidationResult.Result.ERROR, message);
	}

	public String getEndpointUrl() {
		URI endpointURI = _getEndpointURI();

		return endpointURI.toASCIIString();
	}

	public Schema getInboundSchema() {
		Property<Schema> inboundSchemaProperty = inboundSchemaProperties.schema;

		return inboundSchemaProperty.getValue();
	}

	public LiferayConnectionProperties getLiferayConnectionProperties() {
		return connection.getEffectiveLiferayConnectionProperties();
	}

	public String getOpenAPIUrl() {
		URI openAPIURI = _getEndpointOASURI();

		return openAPIURI.toASCIIString();
	}

	public Schema getOutboundSchema() {
		Property<Schema> outboundSchemaProperty =
			outboundSchemaProperties.schema;

		return outboundSchemaProperty.getValue();
	}

	public void setAllowedOperations(String... operations) {
		if ((operations == null) || (operations.length == 0)) {
			return;
		}

		_allowedOperations = new Operation[operations.length];

		for (int i = 0; i < operations.length; i++) {
			_allowedOperations[i] = Operation.toOperation(operations[i]);
		}
	}

	public void setDisplayOperations(boolean display) {
		_displayOperations = display;
	}

	public void setInboundSchema(Schema schema) {
		Property<Schema> inboundSchemaProperty = inboundSchemaProperties.schema;

		inboundSchemaProperty.setValue(schema);
	}

	public void setIncludeLiferayOASParameters(boolean include) {
		_includeLiferayOASParameters = include;
	}

	public void setOutboundSchema(Schema schema) {
		Property<Schema> outboundSchemaProperty =
			outboundSchemaProperties.schema;

		outboundSchemaProperty.setValue(schema);
	}

	@Override
	public void setupLayout() {
		Form mainForm = new Form(this, Form.MAIN);

		Widget openAPIModuleWidget = Widget.widget(openAPIModule);

		openAPIModuleWidget.setWidgetType(Widget.DEFAULT_WIDGET_TYPE);

		mainForm.addRow(openAPIModuleWidget);

		Widget endpointReferenceWidget = Widget.widget(endpoint);

		endpointReferenceWidget.setLongRunning(true);
		endpointReferenceWidget.setWidgetType(
			Widget.NAME_SELECTION_REFERENCE_WIDGET_TYPE);

		mainForm.addRow(endpointReferenceWidget);

		if (_displayOperations) {
			Widget operationsWidget = Widget.widget(operations);

			operationsWidget.setLongRunning(true);
			operationsWidget.setWidgetType(Widget.ENUMERATION_WIDGET_TYPE);

			mainForm.addRow(operationsWidget);
		}

		Widget requestParametersWidget = Widget.widget(parameters);

		requestParametersWidget.setWidgetType(Widget.TABLE_WIDGET_TYPE);

		mainForm.addRow(requestParametersWidget);

		mainForm.addRow(outboundSchemaProperties.getForm(Form.REFERENCE));

		_setupEndpointInfoForm();
	}

	@Override
	public void setupProperties() {
		endpoint.setRequired(true);

		openAPIModule.setRequired(true);

		operations.setPossibleValues(_allowedOperations);

		if (_displayOperations) {
			operations.setRequired(true);
		}

		operations.setValue(_allowedOperations[0]);

		_setupRequestParameterProperties();
	}

	public ValidationResult validateOpenAPIModule() {
		ValidationResult validationResult = _validateOpenAPIModule();

		if (validationResult.getStatus() == ValidationResult.Result.ERROR) {
			return validationResult;
		}

		openAPIModule.setValue(
			HeadlessUtil.sanitizeOpenAPIModuleURI(openAPIModule.getValue()));

		return ValidationResult.OK;
	}

	public LiferayConnectionProperties connection =
		new LiferayConnectionProperties("connection");
	public StringProperty endpoint = new StringProperty("endpoint");
	public SchemaProperties entitySchemaProperties = new SchemaProperties(
		"entitySchemaProperties");
	public SchemaProperties inboundSchemaProperties = new SchemaProperties(
		"inboundSchemaProperties");
	public StringProperty openAPIModule = new StringProperty("openAPIModule");
	public Property<Operation> operations = new EnumProperty<>(
		Operation.class, "operations");

	public SchemaProperties outboundSchemaProperties = new SchemaProperties(
		"outboundSchemaProperties") {

		public ValidationResult afterSchema() {
			entitySchemaProperties.schema.setValue(
				outboundSchemaProperties.schema.getValue());
			inboundSchemaProperties.schema.setValue(
				outboundSchemaProperties.schema.getValue());
			rejectSchemaProperties.schema.setValue(
				SchemaUtils.createRejectSchema(
					outboundSchemaProperties.schema.getValue()));

			return ValidationResult.OK;
		}

	};

	public RequestParameterProperties parameters =
		new RequestParameterProperties("parameters");
	public SchemaProperties rejectSchemaProperties = new SchemaProperties(
		"rejectSchemaProperties");

	private ValidationResult _afterEndpoint(OASSource oasSource) {
		if (_isSingleOperationEndpointPath()) {
			try {
				operations.setValue(_allowedOperations[0]);

				_updateSchemas(oasSource);
			}
			catch (TalendRuntimeException talendRuntimeException) {
				_logger.error(
					"Unable to generate schema", talendRuntimeException);

				return new ValidationResult(
					ValidationResult.Result.ERROR,
					getI18nMessage("error.validation.schema"));
			}

			_setupRequestParameterProperties();

			_updateRequestParameterProperties();

			return ValidationResult.OK;
		}

		try {
			_setupOperations();
		}
		catch (TalendRuntimeException talendRuntimeException) {
			endpoint.setValue(null);

			operations.setValue(null);

			operations.setPossibleValues((List<?>)null);

			return new ValidationResult(
				ValidationResult.Result.ERROR,
				"Unable to resolve http operations");
		}

		return ValidationResult.OK;
	}

	private String _getEndpointBase() {
		StringBuilder sb = new StringBuilder();

		sb.append("/o");
		sb.append(openAPIModule.getValue());

		return sb.toString();
	}

	private URI _getEndpointOASURI() {
		UriBuilder uriBuilder = UriBuilder.fromPath(_getEndpointBase());

		uriBuilder.path("/openapi.json");

		return uriBuilder.build();
	}

	private URI _getEndpointURI() {
		UriBuilder uriBuilder = UriBuilder.fromPath(_getEndpointBase());

		uriBuilder.path(endpoint.getValue());

		if (!parameters.isEmpty()) {
			List<RequestParameter> requestParameters =
				parameters.getRequestParameters();

			for (RequestParameter requestParameter : requestParameters) {
				requestParameter.apply(uriBuilder);
			}
		}

		return uriBuilder.build();
	}

	private LiferayOASSource _getLiferayOASSource() {
		if ((_liferayOASSource != null) && _liferayOASSource.isValid()) {
			return _liferayOASSource;
		}

		_liferayOASSource = LiferayDefinition.getLiferayOASSource(this);

		return _liferayOASSource;
	}

	private String _getOpenAPIEntityOperationsPath() {
		String openAPIModuleVersionPath = _getOpenAPIModuleVersionPath();

		return openAPIModuleVersionPath.concat(endpoint.getValue());
	}

	private String _getOpenAPIModuleVersionPath() {
		String openAPIModuleValue = openAPIModule.getValue();

		String version = openAPIModuleValue.substring(
			openAPIModuleValue.lastIndexOf("/"));

		if (version.matches("/v[0-9.]+")) {
			return version;
		}

		return "";
	}

	private String[] _getOperations() {
		String[] operations = new String[_allowedOperations.length];

		for (int i = 0; i < operations.length; i++) {
			operations[i] = _allowedOperations[i].getHttpMethod();
		}

		return operations;
	}

	private Set<String> _getPaths(OASSource oasSource) {
		OASExplorer oasExplorer = new OASExplorer();

		return oasExplorer.getOperationPaths(
			oasSource.getOASJsonObject(getOpenAPIUrl()), _getOperations());
	}

	private boolean _isAllowedOperation(Operation operation) {
		for (Operation supportedOperation : _allowedOperations) {
			if (operation == supportedOperation) {
				return true;
			}
		}

		return false;
	}

	private boolean _isSingleOperationEndpointPath() {
		if (_allowedOperations.length == 1) {
			return true;
		}

		return false;
	}

	private void _resetProperties() {
		entitySchemaProperties.schema.setValue(SchemaProperties.EMPTY_SCHEMA);

		inboundSchemaProperties.schema.setValue(SchemaProperties.EMPTY_SCHEMA);

		outboundSchemaProperties.schema.setValue(SchemaProperties.EMPTY_SCHEMA);

		rejectSchemaProperties.schema.setValue(SchemaProperties.EMPTY_SCHEMA);

		operations.setValue(null);

		_setupRequestParameterProperties();
	}

	private void _setupEndpointInfoForm() {
		Form mainForm = new Form(this, "EndpointInfo");

		Widget openAPIModuleWidget = Widget.widget(openAPIModule);

		openAPIModuleWidget.setWidgetType(Widget.DEFAULT_WIDGET_TYPE);

		mainForm.addRow(openAPIModuleWidget);

		mainForm.addRow(inboundSchemaProperties.getForm(Form.REFERENCE));
	}

	private void _setupOperations() throws TalendRuntimeException {
		_resetProperties();

		OASExplorer oasExplorer = new OASExplorer();

		LiferayOASSource liferayOASSource = _getLiferayOASSource();

		Set<String> pathOperations = oasExplorer.getPathOperations(
			_getOpenAPIEntityOperationsPath(),
			liferayOASSource.getOASJsonObject(getOpenAPIUrl()));

		List<Operation> possibleOperations = new ArrayList<>();

		for (String endpointOperation : pathOperations) {
			Operation operation = Operation.toOperation(endpointOperation);

			if (_isAllowedOperation(operation)) {
				possibleOperations.add(operation);
			}
		}

		if (possibleOperations.isEmpty()) {
			possibleOperations.add(Operation.Unavailable);
		}

		operations.setPossibleValues(possibleOperations);
		operations.setValue(possibleOperations.get(0));

		afterOperations();
	}

	private void _setupRequestParameterProperties() {
		parameters.removeAll();

		if (_includeLiferayOASParameters) {
			parameters.addParameters(OASParameter.liferayOASParameters);
		}
	}

	private void _updateRequestParameterProperties() {
		if (endpoint.getValue() == null) {
			return;
		}

		Operation operation = operations.getValue();

		OASExplorer oasExplorer = new OASExplorer();

		LiferayOASSource liferayOASSource = _getLiferayOASSource();

		parameters.addParameters(
			oasExplorer.getPathOperationOASParameters(
				_getOpenAPIEntityOperationsPath(), operation.getHttpMethod(),
				liferayOASSource.getOASJsonObject(getOpenAPIUrl())));
	}

	private void _updateSchemas(OASSource oasSource) {
		String openAPIEntityOperationPath = _getOpenAPIEntityOperationsPath();
		JsonObject oasJsonObject = oasSource.getOASJsonObject(getOpenAPIUrl());

		SchemaBuilder schemaBuilder = new SchemaBuilder();

		Operation operation = operations.getValue();

		Schema endpointSchema = schemaBuilder.inferSchema(
			openAPIEntityOperationPath, operation.getHttpMethod(),
			oasJsonObject);

		outboundSchemaProperties.schema.setValue(endpointSchema);

		if (!_displayOperations) {
			return;
		}

		entitySchemaProperties.schema.setValue(endpointSchema);

		inboundSchemaProperties.schema.setValue(endpointSchema);

		rejectSchemaProperties.schema.setValue(
			SchemaUtils.createRejectSchema(endpointSchema));
	}

	private ValidationResult _validateOpenAPIModule() {
		if (StringUtil.isEmpty(openAPIModule.getValue())) {
			return new ValidationResult(
				ValidationResult.Result.ERROR,
				"OpenAPIModule property is required");
		}

		return ValidationResult.OK;
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		LiferayResourceProperties.class);

	private transient Operation[] _allowedOperations = {Operation.Get};
	private transient boolean _displayOperations;
	private transient boolean _includeLiferayOASParameters;
	private transient LiferayOASSource _liferayOASSource;

}