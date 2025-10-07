/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.metadata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import com.liferay.mule.internal.connection.LiferayConnection;
import com.liferay.mule.internal.oas.OASFormat;
import com.liferay.mule.internal.oas.OASType;
import com.liferay.mule.internal.oas.constants.OASConstants;
import com.liferay.mule.internal.util.JsonNodeReader;
import com.liferay.mule.internal.util.StringUtil;

import java.io.IOException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.mule.metadata.api.builder.ArrayTypeBuilder;
import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.metadata.api.builder.ObjectFieldTypeBuilder;
import org.mule.metadata.api.builder.ObjectTypeBuilder;
import org.mule.metadata.api.model.MetadataFormat;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.FailureCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matija Petanjek
 */
public class MetadataTypeBuilder {

	public MetadataType buildBatchMetadataType(
			MetadataContext metadataContext, String className)
		throws ConnectionException, MetadataResolvingException {

		JsonNode oasJsonNode = getOASJsonNode(metadataContext);

		JsonNode schemaJsonNode = getSchemaJsonNodeByClassName(
			jsonNodeReader.getDescendantJsonNode(
				oasJsonNode, OASConstants.PATH_COMPONENTS_SCHEMAS),
			className);

		ObjectTypeBuilder objectTypeBuilder = getObjectTypeBuilder(
			metadataContext, className);

		resolveObjectMetadataType(
			objectTypeBuilder, oasJsonNode,
			schemaJsonNode.get(OASConstants.PROPERTIES),
			fetchRequiredJsonNode(schemaJsonNode), className);

		ArrayTypeBuilder arrayTypeBuilder = getArrayTypeBuilder(
			metadataContext, className);

		return arrayTypeBuilder.of(
			objectTypeBuilder.build()
		).build();
	}

	public MetadataType buildMetadataType(
			MetadataContext metadataContext, String endpoint, String operation,
			String endpointReferencePath)
		throws ConnectionException, MetadataResolvingException {

		JsonNode oasJsonNode = getOASJsonNode(metadataContext);

		JsonNode endpointReferenceJsonNode = fetchEndpointReferenceJsonNode(
			oasJsonNode, endpoint, operation, endpointReferencePath);

		if (endpointReferenceJsonNode.isNull()) {
			return resolveAnyMetadataType(metadataContext);
		}

		String schemaName = getSchemaName(
			endpointReferenceJsonNode.textValue());

		JsonNode schemaJsonNode = getSchemaJsonNodeBySchemaName(
			oasJsonNode, schemaName);

		String schemaType = getSchemaType(schemaJsonNode);

		JsonNode propertiesJsonNode = schemaJsonNode.get(
			OASConstants.PROPERTIES);

		if (schemaType.equals(OASConstants.ARRAY)) {
			ArrayTypeBuilder arrayTypeBuilder = getArrayTypeBuilder(
				metadataContext, schemaName);

			resolveArrayMetadataType(
				arrayTypeBuilder, oasJsonNode,
				jsonNodeReader.getDescendantJsonNode(
					propertiesJsonNode, OASConstants.PATH_ITEMS_ITEMS_REF));

			return arrayTypeBuilder.build();
		}

		ObjectTypeBuilder objectTypeBuilder = getObjectTypeBuilder(
			metadataContext, schemaName);

		resolveObjectMetadataType(
			objectTypeBuilder, oasJsonNode, propertiesJsonNode,
			fetchRequiredJsonNode(schemaJsonNode), schemaName);

		return objectTypeBuilder.build();
	}

	protected ArrayTypeBuilder getArrayTypeBuilder(
		MetadataContext metadataContext, String label) {

		BaseTypeBuilder baseTypeBuilder = metadataContext.getTypeBuilder();

		return baseTypeBuilder.create(
			MetadataFormat.JSON
		).arrayType(
		).label(
			label
		);
	}

	protected JsonNode getOASJsonNode(MetadataContext metadataContext)
		throws ConnectionException, MetadataResolvingException {

		Optional<LiferayConnection> liferayConnectionOptional =
			metadataContext.getConnection();

		try {
			LiferayConnection liferayConnection =
				liferayConnectionOptional.get();

			return jsonNodeReader.fromHttpResponse(
				liferayConnection.getOpenAPISpecHttpResponse());
		}
		catch (IOException ioException) {
			logger.error(
				"Unable to read OpenAPI document from Liferay Portal instance",
				ioException);

			throw new MetadataResolvingException(
				ioException.getMessage(),
				FailureCode.NO_DYNAMIC_METADATA_AVAILABLE, ioException);
		}
		catch (TimeoutException timeoutException) {
			logger.error(
				"Unable to establish connection to Liferay Portal instance",
				timeoutException);

			throw new MetadataResolvingException(
				timeoutException.getMessage(), FailureCode.CONNECTION_FAILURE,
				timeoutException);
		}
	}

	protected ObjectTypeBuilder getObjectTypeBuilder(
		MetadataContext metadataContext, String label) {

		BaseTypeBuilder baseTypeBuilder = metadataContext.getTypeBuilder();

		return baseTypeBuilder.create(
			MetadataFormat.JSON
		).objectType(
		).label(
			label
		);
	}

	protected MetadataType resolveAnyMetadataType(
		MetadataContext metadataContext) {

		BaseTypeBuilder baseTypeBuilder = metadataContext.getTypeBuilder();

		return baseTypeBuilder.anyType(
		).build();
	}

	private JsonNode fetchComponentsReferenceJsonNode(
		JsonNode propertyJsonNode) {

		return propertyJsonNode.get(OASConstants.REF);
	}

	private JsonNode fetchEndpointReferenceJsonNode(
		JsonNode openAPISpecJsonNode, String endpoint, String operation,
		String referencePath) {

		String path = StringUtil.replace(
			referencePath, "ENDPOINT_TPL", endpoint, "OPERATION_TPL",
			operation);

		return jsonNodeReader.fetchDescendantJsonNode(
			openAPISpecJsonNode, path);
	}

	private JsonNode fetchRequiredJsonNode(JsonNode schemaJsonNode) {
		return jsonNodeReader.fetchDescendantJsonNode(
			schemaJsonNode, OASConstants.REQUIRED);
	}

	private MetadataType getMetadataType(JsonNode propertyJsonNode) {
		JsonNode typeJsonNode = propertyJsonNode.get(OASConstants.TYPE);

		OASType oasType = OASType.fromDefinition(typeJsonNode.textValue());

		JsonNode formatJsonNode = propertyJsonNode.get(OASConstants.FORMAT);

		String oasFormatValue = null;

		if (formatJsonNode != null) {
			oasFormatValue = formatJsonNode.textValue();
		}

		OASFormat oasFormat = OASFormat.fromOpenAPITypeAndFormat(
			oasType, oasFormatValue);

		BaseTypeBuilder baseTypeBuilder = BaseTypeBuilder.create(
			MetadataFormat.JSON);

		if (oasFormat == OASFormat.BIGDECIMAL) {
			baseTypeBuilder.numberType();
		}
		else if (oasFormat == OASFormat.BINARY) {
			baseTypeBuilder.binaryType();
		}
		else if (oasFormat == OASFormat.BOOLEAN) {
			baseTypeBuilder.booleanType();
		}
		else if (oasFormat == OASFormat.BYTE) {
			baseTypeBuilder.numberType();
		}
		else if (oasFormat == OASFormat.DATE) {
			baseTypeBuilder.dateType();
		}
		else if (oasFormat == OASFormat.DATE_TIME) {
			baseTypeBuilder.dateTimeType();
		}
		else if (oasFormat == OASFormat.DICTIONARY) {
			baseTypeBuilder.stringType();
		}
		else if (oasFormat == OASFormat.DOUBLE) {
			baseTypeBuilder.numberType();
		}
		else if (oasFormat == OASFormat.FLOAT) {
			baseTypeBuilder.numberType();
		}
		else if (oasFormat == OASFormat.INT32) {
			baseTypeBuilder.numberType();
		}
		else if (oasFormat == OASFormat.INT64) {
			baseTypeBuilder.numberType();
		}
		else if (oasFormat == OASFormat.OBJECT) {
			baseTypeBuilder.objectType();
		}
		else if (oasFormat == OASFormat.STRING) {
			baseTypeBuilder.stringType();
		}
		else {
			baseTypeBuilder.anyType();
		}

		return baseTypeBuilder.build();
	}

	private JsonNode getSchemaJsonNodeByClassName(
		JsonNode schemasJsonNode, String className) {

		Iterator<Map.Entry<String, JsonNode>> schemasIterator =
			schemasJsonNode.fields();

		while (schemasIterator.hasNext()) {
			Map.Entry<String, JsonNode> schemaEntry = schemasIterator.next();

			JsonNode schemaJsonNode = schemaEntry.getValue();

			JsonNode classNameJsonNode = jsonNodeReader.fetchDescendantJsonNode(
				schemaJsonNode,
				OASConstants.PATH_PROPERTIES_X_CLASS_NAME_DEFAULT);

			if (className.equals(classNameJsonNode.textValue())) {
				return schemaJsonNode;
			}
		}

		return NullNode.getInstance();
	}

	private JsonNode getSchemaJsonNodeBySchemaName(
		JsonNode openAPISpecJsonNode, String schemaName) {

		String path = StringUtil.replace(
			OASConstants.PATH_COMPONENTS_SCHEMAS_PATTERN, "SCHEMA_TPL",
			schemaName);

		return jsonNodeReader.getDescendantJsonNode(openAPISpecJsonNode, path);
	}

	private String getSchemaName(String reference) {
		return reference.replaceAll(OASConstants.PATH_SCHEMA_REFERENCE, "");
	}

	private String getSchemaType(JsonNode schemaJsonNode) {
		JsonNode typeJsonNode = schemaJsonNode.get("type");

		return typeJsonNode.textValue();
	}

	private void resolveAdditionalPropertiesMetadataType(
		ObjectFieldTypeBuilder objectFieldTypeBuilder) {

		objectFieldTypeBuilder.value(
		).objectType(
		).description(
			"Dictionary"
		);
	}

	private void resolveArrayMetadataType(
		ArrayTypeBuilder arrayTypeBuilder, JsonNode oasJsonNode,
		JsonNode referenceJsonNode) {

		ObjectTypeBuilder objectTypeBuilder = arrayTypeBuilder.of(
		).objectType();

		String schemaName = getSchemaName(referenceJsonNode.textValue());

		JsonNode schemaJsonNode = getSchemaJsonNodeBySchemaName(
			oasJsonNode, schemaName);

		resolveObjectMetadataType(
			objectTypeBuilder, oasJsonNode,
			schemaJsonNode.get(OASConstants.PROPERTIES),
			fetchRequiredJsonNode(schemaJsonNode), schemaName);
	}

	private void resolveNestedArrayMetadataType(
		ObjectFieldTypeBuilder objectFieldTypeBuilder, JsonNode oasJsonNode,
		JsonNode propertyJsonNode) {

		ArrayTypeBuilder nestedArrayTypeBuilder = objectFieldTypeBuilder.value(
		).arrayType();

		if (jsonNodeReader.hasPath(
				propertyJsonNode, OASConstants.PATH_ITEMS_REF)) {

			JsonNode referenceJsonNode = jsonNodeReader.getDescendantJsonNode(
				propertyJsonNode, OASConstants.PATH_ITEMS_REF);

			resolveArrayMetadataType(
				nestedArrayTypeBuilder, oasJsonNode, referenceJsonNode);
		}
		else {
			nestedArrayTypeBuilder.of(
				getMetadataType(propertyJsonNode.get(OASConstants.ITEMS)));
		}
	}

	private void resolveNestedObjectMetadataType(
		ObjectFieldTypeBuilder objectFieldTypeBuilder, JsonNode oasJsonNode,
		JsonNode propertyJsonNode) {

		ObjectTypeBuilder nestedObjectTypeBuilder =
			objectFieldTypeBuilder.value(
			).objectType();

		String schemaName = getSchemaName(
			fetchComponentsReferenceJsonNode(
				propertyJsonNode
			).asText());

		JsonNode nestedObjectSchemaJsonNode = getSchemaJsonNodeBySchemaName(
			oasJsonNode, schemaName);

		JsonNode nestedObjectPropertiesJsonNode =
			nestedObjectSchemaJsonNode.get(OASConstants.PROPERTIES);

		JsonNode nestedObjectRequiredJsonNode = fetchRequiredJsonNode(
			nestedObjectSchemaJsonNode);

		resolveObjectMetadataType(
			nestedObjectTypeBuilder, oasJsonNode,
			nestedObjectPropertiesJsonNode, nestedObjectRequiredJsonNode,
			schemaName);
	}

	private void resolveObjectMetadataType(
		ObjectTypeBuilder objectTypeBuilder, JsonNode oasJsonNode,
		JsonNode propertiesJsonNode, JsonNode requiredJsonNode,
		String schemaName) {

		if (unresolvedSchemaNames.contains(schemaName)) {
			objectTypeBuilder.addField(
			).key(
				schemaName
			).repeated(
				true
			).value(
			).objectType();

			return;
		}

		unresolvedSchemaNames.add(schemaName);

		Iterator<Map.Entry<String, JsonNode>> propertiesIterator =
			propertiesJsonNode.fields();

		while (propertiesIterator.hasNext()) {
			Map.Entry<String, JsonNode> propertyEntry =
				propertiesIterator.next();

			ObjectFieldTypeBuilder objectFieldTypeBuilder =
				objectTypeBuilder.addField();

			setObjectFieldKey(objectFieldTypeBuilder, propertyEntry);
			setObjectFieldRequired(
				objectFieldTypeBuilder, propertyEntry.getKey(),
				requiredJsonNode);
			setObjectFieldValue(
				objectFieldTypeBuilder, propertyEntry, oasJsonNode);
		}

		unresolvedSchemaNames.remove(schemaName);
	}

	private void setObjectFieldKey(
		ObjectFieldTypeBuilder objectFieldTypeBuilder,
		Map.Entry<String, JsonNode> propertyEntry) {

		objectFieldTypeBuilder.key(propertyEntry.getKey());
	}

	private void setObjectFieldRequired(
		ObjectFieldTypeBuilder objectFieldTypeBuilder, String propertyName,
		JsonNode requiredJsonNode) {

		for (JsonNode propertyNameJsonNode : requiredJsonNode) {
			if (propertyName.equals(propertyNameJsonNode.textValue())) {
				objectFieldTypeBuilder.required(true);

				return;
			}
		}

		objectFieldTypeBuilder.required(false);
	}

	private void setObjectFieldValue(
		ObjectFieldTypeBuilder objectFieldTypeBuilder,
		Map.Entry<String, JsonNode> propertyEntry, JsonNode oasJsonNode) {

		JsonNode propertyJsonNode = propertyEntry.getValue();

		JsonNode typeJsonNode = propertyJsonNode.get(OASConstants.TYPE);

		if (typeJsonNode == null) {
			resolveNestedObjectMetadataType(
				objectFieldTypeBuilder, oasJsonNode, propertyJsonNode);

			return;
		}
		else if (Objects.equals(
					typeJsonNode.textValue(), OASConstants.OBJECT) &&
				 propertyJsonNode.has(OASConstants.ADDITIONAL_PROPERTIES)) {

			resolveAdditionalPropertiesMetadataType(objectFieldTypeBuilder);

			return;
		}
		else if (Objects.equals(typeJsonNode.textValue(), OASConstants.ARRAY)) {
			resolveNestedArrayMetadataType(
				objectFieldTypeBuilder, oasJsonNode, propertyJsonNode);

			return;
		}

		objectFieldTypeBuilder.value(getMetadataType(propertyJsonNode));
	}

	private static final Logger logger = LoggerFactory.getLogger(
		MetadataTypeBuilder.class);

	private final JsonNodeReader jsonNodeReader = new JsonNodeReader();
	private final Set<String> unresolvedSchemaNames = new HashSet<>();

}