/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.metadata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.mule.internal.oas.constants.OASConstants;

import java.io.InputStream;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.metadata.api.model.ArrayType;
import org.mule.metadata.api.model.BinaryType;
import org.mule.metadata.api.model.BooleanType;
import org.mule.metadata.api.model.DateTimeType;
import org.mule.metadata.api.model.DateType;
import org.mule.metadata.api.model.MetadataFormat;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.api.model.NumberType;
import org.mule.metadata.api.model.ObjectFieldType;
import org.mule.metadata.api.model.ObjectKeyType;
import org.mule.metadata.api.model.ObjectType;
import org.mule.metadata.api.model.StringType;
import org.mule.metadata.api.model.impl.DefaultArrayType;
import org.mule.metadata.api.model.impl.DefaultObjectType;

/**
 * @author Matija Petanjek
 */
public class MetadataTypeBuilderTest {

	@Before
	public void setUp() throws Exception {
		metadataTypeBuilder = Mockito.spy(MetadataTypeBuilder.class);

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"com/liferay/mule/internal/metadata/openapi.json");

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode openAPISpecJsonNode = objectMapper.readTree(inputStream);

		BaseTypeBuilder baseTypeBuilder = new BaseTypeBuilder(
			MetadataFormat.JSON);

		Mockito.doReturn(
			baseTypeBuilder.objectType()
		).when(
			metadataTypeBuilder
		).getObjectTypeBuilder(
			Mockito.any(), Mockito.anyString()
		);

		Mockito.doReturn(
			baseTypeBuilder.arrayType()
		).when(
			metadataTypeBuilder
		).getArrayTypeBuilder(
			Mockito.any(), Mockito.anyString()
		);

		Mockito.doReturn(
			openAPISpecJsonNode
		).when(
			metadataTypeBuilder
		).getOASJsonNode(
			Mockito.any()
		);

		Mockito.doReturn(
			null
		).when(
			metadataTypeBuilder
		).resolveAnyMetadataType(
			Mockito.any()
		);
	}

	@Test
	public void testBuildMetadataType_BigDecimalField() throws Exception {
		MetadataType fieldMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"bigDecimalField");

		Assert.assertTrue(fieldMetadataType instanceof NumberType);
	}

	@Test
	public void testBuildMetadataType_BinaryField() throws Exception {
		MetadataType fieldMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"binaryField");

		Assert.assertTrue(fieldMetadataType instanceof BinaryType);
	}

	@Test
	public void testBuildMetadataType_BooleanField() throws Exception {
		MetadataType fieldMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"booleanField");

		Assert.assertTrue(fieldMetadataType instanceof BooleanType);
	}

	@Test
	public void testBuildMetadataType_ByteField() throws Exception {
		MetadataType fieldMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"byteField");

		Assert.assertTrue(fieldMetadataType instanceof NumberType);
	}

	@Test
	public void testBuildMetadataType_DateField() throws Exception {
		MetadataType fieldMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"dateField");

		Assert.assertTrue(fieldMetadataType instanceof DateType);
	}

	@Test
	public void testBuildMetadataType_DateTimeField() throws Exception {
		MetadataType fieldMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"dateTimeField");

		Assert.assertTrue(fieldMetadataType instanceof DateTimeType);
	}

	@Test
	public void testBuildMetadataType_DictionaryField() throws Exception {
		MetadataType fieldMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"dictionaryField");

		Assert.assertTrue(fieldMetadataType instanceof ObjectType);

		Optional<String> descriptionOptional =
			fieldMetadataType.getDescription();

		Assert.assertEquals("Dictionary", descriptionOptional.get());
	}

	@Test
	public void testBuildMetadataType_DoubleField() throws Exception {
		MetadataType fieldMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"doubleField");

		Assert.assertTrue(fieldMetadataType instanceof NumberType);
	}

	@Test
	public void testBuildMetadataType_EntityArrayField() throws Exception {
		MetadataType entityArrayMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"entityArrayField");

		Assert.assertTrue(entityArrayMetadataType instanceof ArrayType);

		DefaultArrayType entityArrayType =
			(DefaultArrayType)entityArrayMetadataType;

		MetadataType entityMetadataType = entityArrayType.getType();

		Assert.assertTrue(entityMetadataType instanceof ObjectType);

		DefaultObjectType entityObjectType =
			(DefaultObjectType)entityMetadataType;

		Collection<ObjectFieldType> fields = entityObjectType.getFields();

		Assert.assertEquals(fields.toString(), 1, fields.size());

		Optional<ObjectFieldType> entityObjectFieldTypeOptional =
			entityObjectType.getFieldByName("Entity");

		Assert.assertTrue(entityObjectFieldTypeOptional.isPresent());
	}

	@Test
	public void testBuildMetadataType_FloatField() throws Exception {
		MetadataType fieldMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"floatField");

		Assert.assertTrue(fieldMetadataType instanceof NumberType);
	}

	@Test
	public void testBuildMetadataType_IntegerField() throws Exception {
		MetadataType fieldMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"integerField");

		Assert.assertTrue(fieldMetadataType instanceof NumberType);
	}

	@Test
	public void testBuildMetadataType_LongField() throws Exception {
		MetadataType fieldMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"longField");

		Assert.assertTrue(fieldMetadataType instanceof NumberType);
	}

	@Test
	public void testBuildMetadataType_NestedEntityArrayField()
		throws Exception {

		MetadataType nestedEntityArrayMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"nestedEntityArrayField");

		Assert.assertTrue(nestedEntityArrayMetadataType instanceof ArrayType);

		DefaultArrayType nestedEntityArrayDefaultArrayType =
			(DefaultArrayType)nestedEntityArrayMetadataType;

		MetadataType arrayItemMetadataType =
			nestedEntityArrayDefaultArrayType.getType();

		MetadataType nestedEntityMetadataType = getEntityMetadataType(
			"/nestedEntities/{id}");

		Assert.assertEquals(nestedEntityMetadataType, arrayItemMetadataType);
	}

	@Test
	public void testBuildMetadataType_NestedEntityField() throws Exception {
		MetadataType nestedEntityMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"nestedEntityField");

		Assert.assertTrue(nestedEntityMetadataType instanceof ObjectType);

		DefaultObjectType nestedEntityDefaultObjectType =
			(DefaultObjectType)nestedEntityMetadataType;

		Collection<ObjectFieldType> objectFieldTypes =
			nestedEntityDefaultObjectType.getFields();

		Assert.assertEquals(
			objectFieldTypes.toString(), 2, objectFieldTypes.size());

		Iterator<ObjectFieldType> iterator = objectFieldTypes.iterator();

		ObjectFieldType objectFieldType = iterator.next();

		Assert.assertEquals(
			"nestedEntityStringField", getObjectFieldName(objectFieldType));

		Assert.assertTrue(objectFieldType.getValue() instanceof StringType);
	}

	@Test
	public void testBuildMetadataType_ObjectField() throws Exception {
		MetadataType fieldMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"objectField");

		Assert.assertTrue(fieldMetadataType instanceof ObjectType);
	}

	@Test
	public void testBuildMetadataType_PageEntity() throws Exception {
		MetadataType pageEntityMetadataType = getEntityMetadataType(
			"/entities", OASConstants.OPERATION_GET);

		Assert.assertTrue(pageEntityMetadataType instanceof ObjectType);

		MetadataType fieldMetadataType = getFieldMetadataType(
			pageEntityMetadataType, "items");

		Assert.assertTrue(fieldMetadataType instanceof ArrayType);

		DefaultArrayType defaultArrayType = (DefaultArrayType)fieldMetadataType;

		MetadataType arrayItemMetadataType = defaultArrayType.getType();

		Assert.assertTrue(arrayItemMetadataType instanceof ObjectType);
		Assert.assertEquals(
			getEntityMetadataType("/entities/{id}"), arrayItemMetadataType);
	}

	@Test
	public void testBuildMetadataType_ParentEntityField() throws Exception {
		MetadataType fieldMetadataType = getFieldMetadataType(
			getEntityMetadataType("/entities/{id}", OASConstants.OPERATION_GET),
			"parentEntityField");

		Assert.assertTrue(fieldMetadataType instanceof ObjectType);
	}

	@Test
	public void testBuildMetadataType_RequiredFields() throws Exception {
		MetadataType entityMetadataType = getEntityMetadataType(
			"/entities/{id}", OASConstants.OPERATION_GET);

		DefaultObjectType defaultObjectType =
			(DefaultObjectType)entityMetadataType;

		for (ObjectFieldType objectFieldType : defaultObjectType.getFields()) {
			String name = getObjectFieldName(objectFieldType);

			if (name.equals("booleanField") || name.equals("longField")) {
				Assert.assertTrue(objectFieldType.isRequired());
			}
			else {
				Assert.assertFalse(objectFieldType.isRequired());
			}
		}
	}

	@Test
	public void testBuildMetadataType_StringField() throws Exception {
		Assert.assertTrue(
			getFieldMetadataType(
				getEntityMetadataType(
					"/entities/{id}", OASConstants.OPERATION_GET),
				"stringField") instanceof StringType);
	}

	@Test
	public void testBuildMetadataTypeWhenNoResponseContent() throws Exception {
		metadataTypeBuilder.buildMetadataType(
			null, "/entities/{id}", OASConstants.OPERATION_DELETE,
			OASConstants.
				PATH_RESPONSES_DEFAULT_CONTENT_APPLICATION_JSON_SCHEMA_PATTERN);

		Mockito.verify(
			metadataTypeBuilder, Mockito.times(1)
		).resolveAnyMetadataType(
			Mockito.anyObject()
		);
	}

	private MetadataType getEntityMetadataType(String endpoint)
		throws Exception {

		setUp();

		return getEntityMetadataType(endpoint, OASConstants.OPERATION_GET);
	}

	private MetadataType getEntityMetadataType(String path, String httpMethod)
		throws Exception {

		return metadataTypeBuilder.buildMetadataType(
			null, path, httpMethod,
			OASConstants.
				PATH_RESPONSES_DEFAULT_CONTENT_APPLICATION_JSON_SCHEMA_PATTERN);
	}

	private MetadataType getFieldMetadataType(
		MetadataType entityMetadataType, String fieldName) {

		DefaultObjectType defaultObjectType =
			(DefaultObjectType)entityMetadataType;

		Optional<ObjectFieldType> objectFieldTypeOptional =
			defaultObjectType.getFieldByName(fieldName);

		ObjectFieldType objectFieldType = objectFieldTypeOptional.orElse(null);

		Assert.assertNotNull(objectFieldType);

		return objectFieldType.getValue();
	}

	private String getObjectFieldName(ObjectFieldType objectFieldType) {
		ObjectKeyType objectKeyType = objectFieldType.getKey();

		QName qName = objectKeyType.getName();

		return qName.toString();
	}

	private MetadataTypeBuilder metadataTypeBuilder;

}