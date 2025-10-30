package ${configYAML.apiPackagePath}.client.serdes.${escapedVersion};

<#list globalEnumSchemas?keys as globalEnumSchemaName>
	import ${configYAML.apiPackagePath}.client.constant.${escapedVersion}.${globalEnumSchemaName};
</#list>

<#list allExternalSchemas?keys as externalSchemaName>
	import ${configYAML.apiPackagePath}.client.dto.${escapedVersion}.${externalSchemaName};
</#list>

<#list allSchemas?keys as schemaName>
	import ${configYAML.apiPackagePath}.client.dto.${escapedVersion}.${schemaName};
</#list>

import ${configYAML.apiPackagePath}.client.json.BaseJSONParser;

import ${configYAML.javaEEPackage}.annotation.Generated;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author ${configYAML.author}
 * @generated
 */
@Generated("")
public class ${schemaName}SerDes {

	public static ${schemaName} toDTO(String json) {
		${schemaName}JSONParser ${schemaVarName}JSONParser = new ${schemaName}JSONParser();

		return ${schemaVarName}JSONParser.parseToDTO(json);
	}

	public static ${schemaName}[] toDTOs(String json) {
		${schemaName}JSONParser ${schemaVarName}JSONParser = new ${schemaName}JSONParser();

		return ${schemaVarName}JSONParser.parseToDTOs(json);
	}

	public static String toJSON(${schemaName} ${schemaVarName}) {
		if (${schemaVarName} == null) {
			return "null";
		}

		<#assign
			enumSchemas = freeMarkerTool.getDTOEnumSchemas(configYAML, openAPIYAML, schema)
			properties = freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, schema, allSchemas)

			dtoParentClassName = freeMarkerTool.getDTOParentClassName(openAPIYAML, schemaName)!
		/>

		<#if schema.discriminator?has_content>
			<#assign propertyName = schema.discriminator.propertyName />

			${schemaName}.${propertyName?cap_first} ${propertyName} = ${schemaVarName}.get${propertyName?cap_first}();

			if (${propertyName} != null) {
				String ${propertyName}String = ${propertyName}.toString();

				<#list schema.discriminator.mapping as mappingName, mappingSchema>
					if (${propertyName}String.equals("${mappingName}")) {
						return ${freeMarkerTool.getReferenceName(mappingSchema)}SerDes.toJSON((${freeMarkerTool.getReferenceName(mappingSchema)})${schemaVarName});
					}
				</#list>

				throw new IllegalArgumentException("Unknown ${propertyName} " + ${propertyName}String);
			}
			else {
				throw new IllegalArgumentException("Missing ${propertyName} parameter");
			}
		<#else>
			StringBuilder sb = new StringBuilder();

			sb.append("{");

			<#if dtoParentClassName?has_content>
				<#assign
					dtoParentSchema = allSchemas[dtoParentClassName]

					enumSchemas = enumSchemas + freeMarkerTool.getDTOEnumSchemas(configYAML, openAPIYAML, dtoParentSchema)
					properties = properties + freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, dtoParentSchema, allSchemas)
				/>
			</#if>

			<#list properties?keys as propertyName>
				<#assign propertyType = properties[propertyName] />

				<#if stringUtil.equals(propertyType, "Date") || stringUtil.equals(propertyType, "Date[]")>
					DateFormat liferayToJSONDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX");

					<#break>
				</#if>
			</#list>

			<#list properties?keys as propertyName>
				<#assign
					capitalizedPropertyName = propertyName?cap_first
					propertySchema = freeMarkerTool.getDTOPropertySchema(configYAML, propertyName, schema, allSchemas)!
				/>

				<#if enumSchemas?keys?seq_contains(properties[propertyName])>
					<#assign capitalizedPropertyName = properties[propertyName] />
				</#if>

				<#if dtoParentClassName?has_content && !propertySchema?has_content>
					<#assign propertySchema = freeMarkerTool.getDTOPropertySchema(configYAML, propertyName, dtoParentSchema, allSchemas) />
				</#if>

				if (${schemaVarName}.get${capitalizedPropertyName}() != null) {
					if (sb.length() > 1) {
						sb.append(", ");
					}

					<#if propertySchema.name??>
						<#assign key = propertySchema.name />
					<#else>
						<#assign key = propertyName />
					</#if>

					sb.append("\"${key}\": ");

					<#assign propertyType = properties[propertyName] />

					<#if enumSchemas?keys?seq_contains(propertyType)>
						sb.append("\"");
						sb.append(${schemaVarName}.get${capitalizedPropertyName}());
						sb.append("\"");
					<#elseif allSchemas[propertyType]??>
						sb.append(String.valueOf(${schemaVarName}.get${capitalizedPropertyName}()));
					<#elseif stringUtil.equals(propertyType, "Object")>
						if (${schemaVarName}.get${capitalizedPropertyName}() instanceof String) {
							sb.append("\"");
							sb.append((String)${schemaVarName}.get${capitalizedPropertyName}());
							sb.append("\"");
						}
						else {
							sb.append(${schemaVarName}.get${capitalizedPropertyName}());
						}
					<#else>
						<#if propertyType?contains("[]")>
							sb.append("[");

							for (int i = 0; i < ${schemaVarName}.get${capitalizedPropertyName}().length; i++) {
								<#if stringUtil.equals(propertyType, "Date[]") || enumSchemas?keys?seq_contains(propertyType)>
									sb.append("\"");

									<#if stringUtil.equals(propertyType, "Date[]")>
										sb.append(liferayToJSONDateFormat.format(${schemaVarName}.get${capitalizedPropertyName}()[i]));
									<#else>
										sb.append(${schemaVarName}.get${capitalizedPropertyName}()[i]);
									</#if>

									sb.append("\"");
								<#elseif stringUtil.startsWith(propertyType, "Map<") || stringUtil.equals(propertyType, "Object[]") || stringUtil.equals(propertyType, "String[]")>
									sb.append(_toJSON(${schemaVarName}.get${capitalizedPropertyName}()[i]));
								<#elseif allSchemas[propertyType?remove_ending("[]")]??>
									sb.append(String.valueOf(${schemaVarName}.get${capitalizedPropertyName}()[i]));
								<#else>
									sb.append(${schemaVarName}.get${capitalizedPropertyName}()[i]);
								</#if>

								if ((i + 1) < ${schemaVarName}.get${capitalizedPropertyName}().length) {
									sb.append(", ");
								}
							}

							sb.append("]");
						<#else>
							<#if stringUtil.equals(propertyType, "Date") || stringUtil.equals(propertyType, "Object") || stringUtil.equals(propertyType, "String")>
								sb.append("\"");

								<#if stringUtil.equals(propertyType, "Date")>
									sb.append(liferayToJSONDateFormat.format(${schemaVarName}.get${capitalizedPropertyName}()));
								<#elseif stringUtil.equals(propertyType, "Object") || stringUtil.equals(propertyType, "String")>
									sb.append(_escape(${schemaVarName}.get${capitalizedPropertyName}()));
								</#if>

								sb.append("\"");
							<#elseif stringUtil.startsWith(propertyType, "Map<")>
								sb.append(_toJSON(${schemaVarName}.get${capitalizedPropertyName}()));
							<#else>
								sb.append(${schemaVarName}.get${capitalizedPropertyName}());
							</#if>
						</#if>
					</#if>
				}
			</#list>

			sb.append("}");

			return sb.toString();
		</#if>
	}

	public static Map<String, Object> toMap(String json) {
		${schemaName}JSONParser ${schemaVarName}JSONParser = new ${schemaName}JSONParser();

		return ${schemaVarName}JSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(${schemaName} ${schemaVarName}) {
		if (${schemaVarName} == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		<#list properties?keys as propertyName>
			<#assign propertyType = properties[propertyName] />

			<#if stringUtil.equals(propertyType, "Date") || stringUtil.equals(propertyType, "Date[]")>
				DateFormat liferayToJSONDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX");

				<#break>
			</#if>
		</#list>

		<#list properties?keys as propertyName>
			<#assign
				capitalizedPropertyName = propertyName?cap_first
				propertySchema = freeMarkerTool.getDTOPropertySchema(configYAML, propertyName, schema, allSchemas)!
			/>

			<#if enumSchemas?keys?seq_contains(properties[propertyName])>
				<#assign capitalizedPropertyName = properties[propertyName] />
			</#if>

			<#if dtoParentClassName?has_content && !propertySchema?has_content>
				<#assign propertySchema = freeMarkerTool.getDTOPropertySchema(configYAML, propertyName, dtoParentSchema, allSchemas) />
			</#if>

			<#if propertySchema.name??>
				<#assign key = propertySchema.name />
			<#else>
				<#assign key = propertyName />
			</#if>

			if (${schemaVarName}.get${capitalizedPropertyName}() == null) {
				map.put("${key}", null);
			}
			else {
				<#if allSchemas[properties[propertyName]]??>
					map.put("${key}", String.valueOf(${schemaVarName}.get${capitalizedPropertyName}()));
				<#elseif stringUtil.equals(properties[propertyName], "Date")>
					map.put("${key}", liferayToJSONDateFormat.format(${schemaVarName}.get${capitalizedPropertyName}()));
				<#else>
					map.put("${key}", String.valueOf(${schemaVarName}.get${capitalizedPropertyName}()));
				</#if>
			}
		</#list>

		return map;
	}

	public static class ${schemaName}JSONParser extends BaseJSONParser<${schemaName}> {

		@Override
		protected ${schemaName} createDTO() {
			<#if schema.discriminator?has_content>
				return null;
			<#else>
				return new ${schemaName}();
			</#if>
		}

		@Override
		protected ${schemaName}[] createDTOArray(int size) {
			return new ${schemaName}[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			<#list properties?keys as propertyName>
				<#assign propertySchema = freeMarkerTool.getDTOPropertySchema(configYAML, propertyName, schema, allSchemas)! />

				<#if dtoParentClassName?has_content && !propertySchema?has_content>
					<#assign propertySchema = freeMarkerTool.getDTOPropertySchema(configYAML, propertyName, dtoParentSchema, allSchemas) />
				</#if>

				<#if !propertyName?is_first>
					else
				</#if>

				<#if propertySchema.name??>
					<#assign fieldName = propertySchema.name />
				<#else>
					<#assign fieldName = propertyName />
				</#if>

				if (Objects.equals(jsonParserFieldName, "${fieldName}")) {
					<#assign propertyType = properties[propertyName]?replace("com.liferay.portal.vulcan.", "${configYAML.apiPackagePath}.client.") />

					<#if stringUtil.startsWith(propertyType, "Map<") || stringUtil.equals(propertyType, "Object[]")>
						return true;
					<#else>
						return false;
					</#if>
				}
			</#list>

			return false;
		}

		<#if schema.discriminator?has_content>
			<#assign propertyName = schema.discriminator.propertyName />

			@Override
			public ${schemaName} parseToDTO(String json) {
				Map<String, Object> jsonMap = parseToMap(json);

				Object ${propertyName} = jsonMap.get("${propertyName}");

				if (${propertyName} != null) {
					String ${propertyName}String = ${propertyName}.toString();

					<#list schema.discriminator.mapping as mappingName, mappingSchema>
						if (${propertyName}String.equals("${mappingName}")) {
							return ${freeMarkerTool.getReferenceName(mappingSchema)}.toDTO(json);
						}
					</#list>

					throw new IllegalArgumentException("Unknown ${propertyName} " + ${propertyName}String);
				}
				else {
					throw new IllegalArgumentException("Missing ${propertyName} parameter");
				}
			}
		</#if>

		@Override
		protected void setField(${schemaName} ${schemaVarName}, String jsonParserFieldName, Object jsonParserFieldValue) {
			<#list properties?keys as propertyName>
				<#assign propertySchema = freeMarkerTool.getDTOPropertySchema(configYAML, propertyName, schema, allSchemas)! />

				<#if dtoParentClassName?has_content && !propertySchema?has_content>
					<#assign propertySchema = freeMarkerTool.getDTOPropertySchema(configYAML, propertyName, dtoParentSchema, allSchemas) />
				</#if>

				<#if !propertyName?is_first>
					else
				</#if>

				<#if propertySchema.name??>
					<#assign fieldName = propertySchema.name />
				<#else>
					<#assign fieldName = propertyName />
				</#if>

				if (Objects.equals(jsonParserFieldName, "${fieldName}")) {
					if (jsonParserFieldValue != null) {
						<#assign capitalizedPropertyName = propertyName?cap_first />

						<#if enumSchemas?keys?seq_contains(properties[propertyName])>
							<#assign capitalizedPropertyName = properties[propertyName] />
						</#if>

						<#assign propertyType = properties[propertyName]?replace("com.liferay.portal.vulcan.", "${configYAML.apiPackagePath}.client.") />

						<#if stringUtil.equals(propertyType, "BigDecimal")>
							${schemaVarName}.set${capitalizedPropertyName}(new BigDecimal((String)jsonParserFieldValue));
						<#elseif stringUtil.equals(propertyType, "BigDecimal[]")>
							${schemaVarName}.set${capitalizedPropertyName}(toBigDecimals((Object[])jsonParserFieldValue));
						<#elseif stringUtil.equals(propertyType, "Date")>
							${schemaVarName}.set${capitalizedPropertyName}(toDate((String)jsonParserFieldValue));
						<#elseif stringUtil.equals(propertyType, "Date[]")>
							${schemaVarName}.set${capitalizedPropertyName}(toDates((Object[])jsonParserFieldValue));
						<#elseif stringUtil.equals(propertyType, "Double")>
							${schemaVarName}.set${capitalizedPropertyName}(Double.valueOf((String)jsonParserFieldValue));
						<#elseif stringUtil.equals(propertyType, "Float")>
							${schemaVarName}.set${capitalizedPropertyName}(Float.valueOf((String)jsonParserFieldValue));
						<#elseif stringUtil.equals(propertyType, "Integer")>
							${schemaVarName}.set${capitalizedPropertyName}(Integer.valueOf((String)jsonParserFieldValue));
						<#elseif stringUtil.equals(propertyType, "Integer[]")>
							${schemaVarName}.set${capitalizedPropertyName}(toIntegers((Object[])jsonParserFieldValue));
						<#elseif stringUtil.equals(propertyType, "Long")>
							${schemaVarName}.set${capitalizedPropertyName}(Long.valueOf((String)jsonParserFieldValue));
						<#elseif stringUtil.equals(propertyType, "Long[]")>
							${schemaVarName}.set${capitalizedPropertyName}(toLongs((Object[])jsonParserFieldValue));
						<#elseif stringUtil.equals(propertyType, "Number")>
							${schemaVarName}.set${capitalizedPropertyName}(Integer.valueOf((String)jsonParserFieldValue));
						<#elseif stringUtil.equals(propertyType, "Number[]")>
							${schemaVarName}.set${capitalizedPropertyName}(toIntegers((Object[])jsonParserFieldValue));
						<#elseif stringUtil.equals(propertyType, "String[]")>
							${schemaVarName}.set${capitalizedPropertyName}(toStrings((Object[])jsonParserFieldValue));
						<#elseif stringUtil.equals(propertyType, "${configYAML.apiPackagePath}.client.custom.field.CustomField") || stringUtil.equals(propertyType, "${configYAML.apiPackagePath}.client.permission.Permission")>
							${schemaVarName}.set${capitalizedPropertyName}(${propertyType}.toDTO((String)jsonParserFieldValue));
						<#elseif stringUtil.equals(propertyType, "${configYAML.apiPackagePath}.client.custom.field.CustomField[]") || stringUtil.equals(propertyType, "${configYAML.apiPackagePath}.client.permission.Permission[]")>
							Object[] jsonParserFieldValues = (Object[])jsonParserFieldValue;

							${propertyType?remove_ending("[]")}[] ${propertyName}Array = new ${propertyType?remove_ending("[]")}[jsonParserFieldValues.length];

							for (int i = 0; i < ${propertyName}Array.length; i++) {
								${propertyName}Array[i] = ${propertyType?remove_ending("[]")}.toDTO((String)jsonParserFieldValues[i]);
							}

							${schemaVarName}.set${capitalizedPropertyName}(${propertyName}Array);
						<#elseif enumSchemas?keys?seq_contains(properties[propertyName])>
							${schemaVarName}.set${capitalizedPropertyName}(${schemaName}.${propertyType}.create((String)jsonParserFieldValue));
						<#elseif globalEnumSchemas?keys?seq_contains(propertyType)>
							${schemaVarName}.set${capitalizedPropertyName}(${propertyType}.create((String)jsonParserFieldValue));
						<#elseif allExternalSchemas?keys?seq_contains(propertyType) || allSchemas?keys?seq_contains(propertyType)>
							${schemaVarName}.set${capitalizedPropertyName}(${propertyType}SerDes.toDTO((String)jsonParserFieldValue));
						<#elseif propertyType?ends_with("[]") && (allExternalSchemas?keys?seq_contains(propertyType?remove_ending("[]")) || allSchemas?keys?seq_contains(propertyType?remove_ending("[]")))>
							Object[] jsonParserFieldValues = (Object[])jsonParserFieldValue;

							${propertyType?remove_ending("[]")}[] ${propertyName}Array = new ${propertyType?remove_ending("[]")}[jsonParserFieldValues.length];

							for (int i = 0; i < ${propertyName}Array.length; i++) {
								${propertyName}Array[i] = ${propertyType?remove_ending("[]")}SerDes.toDTO((String)jsonParserFieldValues[i]);
							}

							${schemaVarName}.set${capitalizedPropertyName}(${propertyName}Array);
						<#else>
							${schemaVarName}.set${capitalizedPropertyName}((${propertyType})jsonParserFieldValue);
						</#if>
					}
				}
			</#list>
		}
	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value == null) {
			return "null";
		}

		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}