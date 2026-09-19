/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.internal.yaml.openapi;

import java.util.List;
import java.util.Map;

/**
 * @author Peter Shin
 */
public class Schema {

	public Schema() {
	}

	public Schema(boolean freeFormObject) {
		if (freeFormObject) {
			setType("?");
		}
	}

	public Schema getAdditionalPropertySchema() {
		return _additionalPropertySchema;
	}

	public List<Schema> getAllOfSchemas() {
		return _allOfSchemas;
	}

	public List<Schema> getAnyOfSchemas() {
		return _anyOfSchemas;
	}

	public String getDefault() {
		return _default;
	}

	public String getDescription() {
		return _description;
	}

	public Discriminator getDiscriminator() {
		return _discriminator;
	}

	public List<String> getEnumValues() {
		return _enumValues;
	}

	public Object getExample() {
		return _example;
	}

	public String getFormat() {
		return _format;
	}

	public Items getItems() {
		return _items;
	}

	public Integer getMaxLength() {
		return _maxLength;
	}

	public Double getMaximum() {
		return _maximum;
	}

	public Integer getMinLength() {
		return _minLength;
	}

	public Double getMinimum() {
		return _minimum;
	}

	public String getName() {
		return _name;
	}

	public List<Schema> getOneOfSchemas() {
		return _oneOfSchemas;
	}

	public Map<String, Schema> getPropertySchemas() {
		return _propertySchemas;
	}

	public String getReference() {
		return _reference;
	}

	public List<String> getRequiredPropertySchemaNames() {
		return _requiredPropertySchemaNames;
	}

	public String getType() {
		return _type;
	}

	public XML getXml() {
		return _xml;
	}

	public boolean isDeprecated() {
		return _deprecated;
	}

	public boolean isJsonMap() {
		return _jsonMap;
	}

	public boolean isJsonString() {
		return _jsonString;
	}

	public boolean isMergeProperties() {
		return _mergeProperties;
	}

	public boolean isReadOnly() {
		return _readOnly;
	}

	public boolean isWriteOnly() {
		return _writeOnly;
	}

	public void setAdditionalPropertySchema(Schema additionalPropertySchema) {
		_additionalPropertySchema = additionalPropertySchema;
	}

	public void setAllOfSchemas(List<Schema> allOfSchemas) {
		_allOfSchemas = allOfSchemas;
	}

	public void setAnyOfSchemas(List<Schema> anyOfSchemas) {
		_anyOfSchemas = anyOfSchemas;
	}

	public void setDefault(String d) {
		_default = d;
	}

	public void setDeprecated(boolean deprecated) {
		_deprecated = deprecated;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setDiscriminator(Discriminator discriminator) {
		_discriminator = discriminator;
	}

	public void setEnumValues(List<String> enumValues) {
		_enumValues = enumValues;
	}

	public void setExample(Object example) {
		_example = example;
	}

	public void setFormat(String format) {
		_format = format;
	}

	public void setItems(Items items) {
		_items = items;
	}

	public void setJsonMap(boolean jsonMap) {
		_jsonMap = jsonMap;
	}

	public void setJsonString(boolean jsonString) {
		_jsonString = jsonString;
	}

	public void setMaxLength(Integer maxLength) {
		_maxLength = maxLength;
	}

	public void setMaximum(Double maximum) {
		_maximum = maximum;
	}

	public void setMergeProperties(boolean mergeProperties) {
		_mergeProperties = mergeProperties;
	}

	public void setMinLength(Integer minLength) {
		_minLength = minLength;
	}

	public void setMinimum(Double minimum) {
		_minimum = minimum;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setOneOfSchemas(List<Schema> oneOfSchemas) {
		_oneOfSchemas = oneOfSchemas;
	}

	public void setPropertySchemas(Map<String, Schema> propertySchemas) {
		_propertySchemas = propertySchemas;
	}

	public void setReadOnly(boolean readOnly) {
		_readOnly = readOnly;
	}

	public void setReference(String reference) {
		_reference = reference;
	}

	public void setRequiredPropertySchemaNames(
		List<String> requiredPropertySchemaNames) {

		_requiredPropertySchemaNames = requiredPropertySchemaNames;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setWriteOnly(boolean writeOnly) {
		_writeOnly = writeOnly;
	}

	public void setXML(XML xml) {
		_xml = xml;
	}

	private Schema _additionalPropertySchema;
	private List<Schema> _allOfSchemas;
	private List<Schema> _anyOfSchemas;
	private String _default;
	private boolean _deprecated;
	private String _description;
	private Discriminator _discriminator;
	private List<String> _enumValues;
	private Object _example;
	private String _format;
	private Items _items;
	private boolean _jsonMap;
	private boolean _jsonString;
	private Integer _maxLength;
	private Double _maximum;
	private boolean _mergeProperties = true;
	private Integer _minLength;
	private Double _minimum;
	private String _name;
	private List<Schema> _oneOfSchemas;
	private Map<String, Schema> _propertySchemas;
	private boolean _readOnly;
	private String _reference;
	private List<String> _requiredPropertySchemaNames;
	private String _type;
	private boolean _writeOnly;
	private XML _xml;

}