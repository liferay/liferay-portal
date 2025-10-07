/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.wizard;

import com.liferay.talend.LiferayDefinition;
import com.liferay.talend.common.oas.OASExplorer;
import com.liferay.talend.common.oas.constants.OASConstants;
import com.liferay.talend.common.schema.SchemaBuilder;
import com.liferay.talend.internal.oas.LiferayOASSource;
import com.liferay.talend.properties.connection.LiferayConnectionProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.json.JsonObject;

import org.apache.avro.Schema;
import org.apache.commons.lang3.reflect.TypeLiteral;

import org.talend.components.api.exception.ComponentException;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.service.Repository;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public class LiferaySchemaListProperties extends ComponentPropertiesImpl {

	public LiferaySchemaListProperties(String name) {
		super(name);
	}

	public ValidationResult afterFormFinishMain(
		Repository<Properties> repository) {

		LiferayOASSource liferayOASSource =
			LiferayDefinition.getLiferayOASSource(
				connection.getEffectiveLiferayConnectionProperties());

		if (!liferayOASSource.isValid()) {
			return liferayOASSource.getValidationResult();
		}

		String connectionRepositoryLocation = repository.storeProperties(
			connection, connection.name.getValue(), repositoryLocation, null);

		for (NamedThing namedThing : selectedSchemaNames.getValue()) {
			String operationPath = namedThing.getName();

			SchemaProperties schemaProperties = new SchemaProperties("schema");

			SchemaBuilder schemaBuilder = new SchemaBuilder();

			Schema operationPathSchema = schemaBuilder.inferSchema(
				operationPath, OASConstants.OPERATION_GET,
				liferayOASSource.getOASJsonObject());

			schemaProperties.schema.setValue(operationPathSchema);

			repository.storeProperties(
				schemaProperties, namedThing.getDisplayName(),
				connectionRepositoryLocation, "schema");
		}

		return ValidationResult.OK;
	}

	public void beforeFormPresentMain() {
		LiferayOASSource liferayOASSource =
			LiferayDefinition.getLiferayOASSource(
				connection.getEffectiveLiferayConnectionProperties());

		if (!liferayOASSource.isValid()) {
			throw new IllegalStateException(
				"Liferay open API specificatio0n source unavailable");
		}

		try {
			Map<String, String> operationPathSchemaNames =
				_getOperationPathSchemaNames(
					liferayOASSource.getOASJsonObject(),
					OASConstants.OPERATION_GET);

			for (Map.Entry<String, String> entry :
					operationPathSchemaNames.entrySet()) {

				String operationPath = entry.getKey();

				if (operationPath.endsWith("{id}")) {
					schemaNames.add(
						new SimpleNamedThing(entry.getKey(), entry.getValue()));
				}
			}
		}
		catch (Exception exception) {
			throw new ComponentException(exception);
		}

		selectedSchemaNames.setPossibleValues(schemaNames);

		Form mainForm = getForm(Form.MAIN);

		mainForm.setAllowBack(true);
		mainForm.setAllowFinish(true);
	}

	public LiferaySchemaListProperties setConnection(
		LiferayConnectionProperties liferayConnectionProperties) {

		connection = liferayConnectionProperties;

		return this;
	}

	public LiferaySchemaListProperties setRepositoryLocation(
		String repositoryLocation) {

		this.repositoryLocation = repositoryLocation;

		return this;
	}

	@Override
	public void setupLayout() {
		super.setupLayout();

		Form form = Form.create(this, Form.MAIN);

		Widget selectedSchemaNamesWidget = Widget.widget(selectedSchemaNames);

		selectedSchemaNamesWidget.setWidgetType(
			Widget.NAME_SELECTION_AREA_WIDGET_TYPE);

		form.addRow(selectedSchemaNamesWidget);
	}

	public LiferayConnectionProperties connection;
	public String repositoryLocation;
	public List<NamedThing> schemaNames = new ArrayList<>();
	public Property<List<NamedThing>> selectedSchemaNames =
		PropertyFactory.newProperty(
			new TypeLiteral<List<NamedThing>>() {
			},
			"selectedSchemaNames");

	private Map<String, String> _getOperationPathSchemaNames(
		JsonObject oasJsonObject, String operation) {

		Map<String, String> operationPathSchemas = new TreeMap<>();

		SchemaBuilder schemaBuilder = new SchemaBuilder();

		OASExplorer oasExplorer = new OASExplorer();

		Set<String> operationPaths = oasExplorer.getOperationPaths(
			oasJsonObject, operation);

		for (String operationPath : operationPaths) {
			operationPathSchemas.put(
				operationPath,
				schemaBuilder.extractEndpointSchemaName(
					operationPath, operation, oasJsonObject));
		}

		return operationPathSchemas;
	}

}