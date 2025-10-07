/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.properties.input;

import com.liferay.talend.properties.connection.LiferayConnectionProperties;
import com.liferay.talend.properties.resource.LiferayResourceProperties;
import com.liferay.talend.properties.resource.Operation;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;

import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

/**
 * @author Igor Beslic
 */
public class LiferayInputProperties extends FixedConnectorsComponentProperties {

	public LiferayInputProperties(String name) {
		super(name);
	}

	public LiferayInputProperties(
		String name, Operation operation, String openAPIModule, String hostURL,
		String endpoint, List<String> parameterNamesColumn,
		List<String> parameterLocationsColumn,
		List<String> parameterValuesColumn) {

		super(name);

		resource.connection.hostURL.setValue(hostURL);

		resource.endpoint.setValue(endpoint);

		resource.openAPIModule.setValue(openAPIModule);

		resource.parameters.parameterLocationColumn.setValue(
			parameterLocationsColumn);
		resource.parameters.parameterNameColumn.setValue(parameterNamesColumn);
		resource.parameters.parameterValueColumn.setValue(
			parameterValuesColumn);
	}

	public String getEndpointUrl() {
		return resource.getEndpointUrl();
	}

	public int getItemsPerPage() {
		LiferayConnectionProperties liferayConnectionProperties =
			resource.getLiferayConnectionProperties();

		return liferayConnectionProperties.getItemsPerPage();
	}

	public Schema getOutboundSchema() {
		return resource.getOutboundSchema();
	}

	@Override
	public void setupLayout() {
		Form mainForm = new Form(this, Form.MAIN);

		mainForm.addRow(resource.connection.getForm(Form.REFERENCE));
		mainForm.addRow(resource.getForm(Form.MAIN));
		mainForm.setRefreshUI(true);

		Form advancedForm = new Form(this, Form.ADVANCED);

		advancedForm.addRow(resource.connection.getForm(Form.ADVANCED));

		advancedForm.addRow(
			_createDesignTimeProperty(
				PropertyFactory.newProperty(String.class, "reference"),
				_getReference()));
		advancedForm.addRow(
			_createDesignTimeProperty(
				PropertyFactory.newProperty(String.class, "version"),
				_getVersion()));
		advancedForm.setRefreshUI(true);
	}

	@Override
	public void setupProperties() {
		resource.setIncludeLiferayOASParameters(true);

		resource.setupProperties();
	}

	public LiferayResourceProperties resource = new LiferayResourceProperties(
		"resource");

	@Override
	protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(
		boolean outputConnection) {

		if (!outputConnection) {
			return Collections.emptySet();
		}

		Set<PropertyPathConnector> schemaPropertiesConnectors = new HashSet<>();

		schemaPropertiesConnectors.add(
			new PropertyPathConnector(
				Connector.MAIN_NAME, "resource.outboundSchemaProperties"));

		return Collections.unmodifiableSet(schemaPropertiesConnectors);
	}

	private Widget _createDesignTimeProperty(
		Property<String> property, String value) {

		property.addFlag(Property.Flags.DESIGN_TIME_ONLY);
		property.setTaggedValue("ADD_QUOTES", false);
		property.setValue(value);

		Widget widget = Widget.widget(property);

		widget.setLongRunning(false);
		widget.setReadonly(true);

		widget.setWidgetType(Widget.DEFAULT_WIDGET_TYPE);

		return widget;
	}

	private String _getReference() {
		return String.format(
			"%s@%s", getClass(), System.identityHashCode(this));
	}

	private String _getVersion() {
		return "0.7.0-SNAPSHOT";
	}

}