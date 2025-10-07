/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.properties.batch;

import com.liferay.talend.LiferayDefinition;
import com.liferay.talend.common.daikon.DaikonUtil;
import com.liferay.talend.common.oas.OASException;
import com.liferay.talend.common.oas.OASExplorer;
import com.liferay.talend.common.schema.SchemaBuilder;
import com.liferay.talend.common.schema.constants.BatchSchemaConstants;
import com.liferay.talend.internal.oas.LiferayOASSource;
import com.liferay.talend.properties.resource.LiferayResourceProperties;
import com.liferay.talend.tliferaybatchfile.TLiferayBatchFileDefinition;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.json.JsonObject;

import org.apache.avro.Schema;

import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.api.properties.ComponentReferenceProperties;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.property.StringProperty;

/**
 * @author Igor Beslic
 */
public class LiferayBatchFileProperties
	extends FixedConnectorsComponentProperties {

	public LiferayBatchFileProperties(String name) {
		super(name);

		batchFilePath.setRequired();
	}

	public LiferayBatchFileProperties(
		String filePath, Schema entitySchema, String name,
		JsonObject oasJsonObject) {

		this(name);

		batchFilePath.setValue(filePath);

		resource.setInboundSchema(entitySchema);

		_oasJsonObject = oasJsonObject;
	}

	public ValidationResult afterEntity() {
		SchemaBuilder schemaBuilder = new SchemaBuilder();

		resource.setInboundSchema(
			schemaBuilder.getEntitySchema(
				_getEntityName(), _getOASJsonObject()));

		return ValidationResult.OK;
	}

	public void afterLiferayBatchFileReferenceProperties() {
		refreshLayout(getForm(Form.REFERENCE));
	}

	public ValidationResult beforeEntity() {
		OASExplorer oasExplorer = new OASExplorer();

		try {
			Set<String> entitySchemaNames = oasExplorer.getEntitySchemaNames(
				_getOASJsonObject());

			if (entitySchemaNames.isEmpty()) {
				return new ValidationResult(
					ValidationResult.Result.ERROR,
					"Unable to find any exposed resources");
			}

			entity.setPossibleNamedThingValues(
				DaikonUtil.toNamedThings(
					_initializeEntityClassNames(entitySchemaNames)));
		}
		catch (Exception exception) {
			return new ValidationResult(
				ValidationResult.Result.ERROR, exception.getMessage());
		}

		return null;
	}

	public String getBatchFilePath() {
		return batchFilePath.getStringValue();
	}

	public LiferayBatchFileProperties getEffectiveLiferayBatchFileProperties() {
		LiferayBatchFileProperties liferayBatchFileProperties =
			liferayBatchFileReferenceProperties.getReference();

		if (liferayBatchFileProperties != null) {
			return liferayBatchFileProperties;
		}

		return this;
	}

	public String getEntityClassName() {
		return entity.getValue();
	}

	public Schema getEntitySchema() {
		return resource.getInboundSchema();
	}

	public boolean isLiferayBatchFileReferenceProperties() {
		Property<String> componentInstanceIdProperty =
			liferayBatchFileReferenceProperties.componentInstanceId;

		String componentInstanceId =
			componentInstanceIdProperty.getStringValue();

		if ((componentInstanceId != null) &&
			componentInstanceId.startsWith(
				TLiferayBatchFileDefinition.COMPONENT_NAME)) {

			return true;
		}

		return false;
	}

	@Override
	public void refreshLayout(Form form) {
		super.refreshLayout(form);

		if (!Objects.equals(form.getName(), Form.REFERENCE)) {
			return;
		}

		if (!isLiferayBatchFileReferenceProperties()) {
			form.setHidden(false);

			return;
		}

		form.setHidden(true);

		Widget widget = form.getWidget(
			liferayBatchFileReferenceProperties.getName());

		widget.setVisible();
	}

	@Override
	public void setupLayout() {
		_setupLayout(new Form(this, Form.MAIN));

		_setupLayout(new Form(this, Form.REFERENCE));
	}

	@Override
	public void setupProperties() {
		resource.setOutboundSchema(BatchSchemaConstants.SCHEMA);
	}

	public Property<String> batchFilePath = PropertyFactory.newProperty(
		"batchFilePath");
	public StringProperty entity = new StringProperty("entity");
	public ComponentReferenceProperties<LiferayBatchFileProperties>
		liferayBatchFileReferenceProperties =
			new ComponentReferenceProperties<>(
				"liferayBatchFileReferenceProperties",
				TLiferayBatchFileDefinition.COMPONENT_NAME);
	public LiferayResourceProperties resource = new LiferayResourceProperties(
		"resource");

	@Override
	protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(
		boolean outputConnection) {

		if (!outputConnection) {
			return Collections.singleton(
				new PropertyPathConnector(
					Connector.MAIN_NAME, "resource.inboundSchemaProperties"));
		}

		Set<PropertyPathConnector> schemaPropertiesConnectors = new HashSet<>();

		schemaPropertiesConnectors.add(
			new PropertyPathConnector(
				Connector.MAIN_NAME, "resource.outboundSchemaProperties"));
		schemaPropertiesConnectors.add(
			new PropertyPathConnector(
				Connector.REJECT_NAME, "resource.rejectSchemaProperties"));

		return Collections.unmodifiableSet(schemaPropertiesConnectors);
	}

	private String _getEntityName() {
		return entity.getPossibleValuesDisplayName(entity.getValue());
	}

	private JsonObject _getOASJsonObject() {
		if (_oasJsonObject != null) {
			return _oasJsonObject;
		}

		LiferayOASSource liferayOASSource =
			LiferayDefinition.getLiferayOASSource(resource);

		if (!liferayOASSource.isValid()) {
			throw new OASException("Unable to obtain OpenAPI specification");
		}

		_oasJsonObject = liferayOASSource.getOASJsonObject(
			resource.getOpenAPIUrl());

		return _oasJsonObject;
	}

	private Map<String, String> _initializeEntityClassNames(Set<String> names) {
		SortedMap<String, String> entityClassNames = new TreeMap<>();

		for (String name : names) {
			String entityClassName = _oasExplorer.getEntityClassName(
				name, _getOASJsonObject());

			if (entityClassName != null) {
				entityClassNames.put(name, entityClassName);

				continue;
			}

			entityClassNames.put(name, name);
		}

		return Collections.unmodifiableSortedMap(entityClassNames);
	}

	private void _setupLayout(Form form) {
		if (Objects.equals(form.getName(), Form.REFERENCE)) {
			Widget referencedComponentWidget = Widget.widget(
				liferayBatchFileReferenceProperties);

			referencedComponentWidget.setWidgetType(
				Widget.COMPONENT_REFERENCE_WIDGET_TYPE);

			form.addRow(referencedComponentWidget);
		}

		form.addRow(resource.connection.getForm(Form.REFERENCE));
		form.addRow(resource.getForm("EndpointInfo"));

		Widget entitySelectWidget = Widget.widget(entity);

		entitySelectWidget.setCallAfter(true);
		entitySelectWidget.setLongRunning(true);
		entitySelectWidget.setWidgetType(
			Widget.NAME_SELECTION_REFERENCE_WIDGET_TYPE);

		form.addRow(entitySelectWidget);

		Widget bulkFilePathWidget = Widget.widget(batchFilePath);

		bulkFilePathWidget.setWidgetType(Widget.FILE_WIDGET_TYPE);

		form.addRow(bulkFilePathWidget);
	}

	private final transient OASExplorer _oasExplorer = new OASExplorer();
	private transient JsonObject _oasJsonObject;

}