package ${configYAML.apiPackagePath}.dto.${escapedVersion};

<#list globalEnumSchemas?keys as globalEnumSchemaName>
	import ${configYAML.apiPackagePath}.constant.${escapedVersion}.${globalEnumSchemaName};
</#list>

<#list allExternalSchemas?keys as externalSchemaName>
	<#if javaDataTypeMap?keys?seq_contains(externalSchemaName)>
		import ${javaDataTypeMap[externalSchemaName]};
	</#if>
</#list>

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.jackson.databind.deser.JSONStringStdDeserializer;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author ${configYAML.author}
 * @generated
 */

<#if schema.discriminator?has_content>
	@JsonSubTypes(
		{
			<#list schema.discriminator.mapping as mappingName, mappingSchema>
				@JsonSubTypes.Type(name = "${mappingName}", value=${freeMarkerTool.getReferenceName(mappingSchema)}.class)

				<#if mappingName_has_next>
					,
				</#if>
			</#list>
		}
	)

	@JsonTypeInfo(
		include= JsonTypeInfo.As.PROPERTY, property="${schema.discriminator.propertyName}",
		use= JsonTypeInfo.Id.NAME, visible = true
	)
</#if>

@Generated("")
@GraphQLName(
	<#if schema.description?has_content>
		description = "${schema.description?j_string}", value = "${schemaName}"
	<#else>
		"${schemaName}"
	</#if>
)
@JsonFilter("Liferay.Vulcan")
<#if schema.requiredPropertySchemaNames?has_content>
	@io.swagger.v3.oas.annotations.media.Schema(
		<#if schema.deprecated>
			deprecated = ${schema.deprecated?c},
		</#if>
		requiredProperties =
			{
				<#list schema.requiredPropertySchemaNames as requiredProperty>
					"${requiredProperty}"
					<#if requiredProperty_has_next>
						,
					</#if>
				</#list>
			}
		<#if schema.description??>
			, description = "${schema.description?j_string}"
		</#if>
	)
</#if>

@XmlRootElement(name = "${schemaName}")

<#assign dtoParentClassName = freeMarkerTool.getDTOParentClassName(openAPIYAML, schemaName)! />

public <#if schema.discriminator?has_content>abstract</#if> class ${schemaName} <#if dtoParentClassName?has_content>extends ${dtoParentClassName}</#if> implements Serializable {

	public static ${schemaName} toDTO(String json) {
		return ObjectMapperUtil.readValue(${schemaName}.class, json);
	}

	public static ${schemaName} unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(${schemaName}.class, json);
	}

	<#assign
		enumSchemas = freeMarkerTool.getDTOEnumSchemas(configYAML, openAPIYAML, schema)
		jsonMapPropertyNames = []
		properties = freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, schema, allSchemas)
	/>

	<#list properties?keys as propertyName>
		<#assign
			propertySchema = freeMarkerTool.getDTOPropertySchema(configYAML, propertyName, schema, allSchemas)
			propertyType = properties[propertyName]
			sizeParameters = []
		/>

		<#if propertySchema.maximum??>
			@DecimalMax("${propertySchema.maximum}")
		</#if>

		<#if propertySchema.minimum??>
			@DecimalMin("${propertySchema.minimum}")
		</#if>

		<#if propertySchema.maxLength??>
			<#assign sizeParameters = sizeParameters + ["max = ${propertySchema.maxLength}"] />
		</#if>

		<#if propertySchema.minLength??>
			<#assign sizeParameters = sizeParameters + ["min = ${propertySchema.minLength}"] />
		</#if>

		<#if sizeParameters?has_content>
			@Size(${sizeParameters?join(", ")})
		</#if>

		@io.swagger.v3.oas.annotations.media.Schema(
			<#if propertySchema.deprecated>
				deprecated = ${propertySchema.deprecated?c}
			</#if>

			<#if propertySchema.description??>
				<#if propertySchema.deprecated>
					,
				</#if>
				description = "${propertySchema.description?j_string}"
			</#if>

			<#if propertySchema.example??>
				<#if propertySchema.deprecated || propertySchema.description??>
					,
				</#if>

				example = "${freeMarkerTool.getObjectFieldStringValue(propertyType, propertySchema.example)}"
			</#if>
		)

		<#if !["Boolean", "Boolean[]", "Date", "Date[]", "Double", "Double[]", "Integer", "Integer[]", "Long", "Long[]", "String", "String[]"]?seq_contains(propertyType)>
			@Valid
		</#if>

		<#assign capitalizedPropertyName = propertyName?cap_first />

		<#if enumSchemas?keys?seq_contains(propertyType)>
			<#assign capitalizedPropertyName = propertyType />

			@JsonGetter("${propertyName}")
		</#if>

		<#if propertySchema.isJsonMap()>
			<#assign jsonMapPropertyNames = jsonMapPropertyNames + [propertyName] />

			public ${propertyType} get${capitalizedPropertyName}() {
				if (${propertyName} == null) {
					return null;
				}

				${propertyName}.replaceAll(
					(key, value) -> {
						if (!(value instanceof UnsafeSupplier<?, ?>)) {
							return value;
						}

						try {
							UnsafeSupplier<?, ?> unsafeSupplier = (UnsafeSupplier<?, ?>)value;

							return unsafeSupplier.get();
						}
						catch (Throwable throwable) {
							throw new RuntimeException(throwable);
						}
					}
				);

				return ${propertyName};
			}
		<#else>
			public ${propertyType} get${capitalizedPropertyName}() {
				if (_${propertyName}Supplier != null) {
					${propertyName} = _${propertyName}Supplier.get();

					_${propertyName}Supplier = null;
				}

				return ${propertyName};
			}
		</#if>

		<#if enumSchemas?keys?seq_contains(propertyType)>
			@JsonIgnore
			public String get${capitalizedPropertyName}AsString() {
				${propertyType} ${propertyName} = get${capitalizedPropertyName}();

				if (${propertyName} == null) {
					return null;
				}

				return ${propertyName}.toString();
			}
		</#if>

		public void set${capitalizedPropertyName}(${propertyType} ${propertyName}) {
			<#if propertySchema.jsonMap>
				if (${propertyName} == null) {
					this.${propertyName} = null;

					return;
				}

				${propertyType} ${propertyName}Map = new LinkedHashMap<>(${propertyName});

				${propertyName}Map.replaceAll(
					(key, value) -> {
						if (!(value instanceof UnsafeSupplier<?, ?>)) {
							return value;
						}

						return new CachedUnsafeSupplier((UnsafeSupplier<?, ?>)value);
					});

				this.${propertyName} = Collections.synchronizedMap(${propertyName}Map);
			<#else>
				this.${propertyName} = ${propertyName};

				_${propertyName}Supplier = null;
			</#if>
		}

		@JsonIgnore
		public void set${capitalizedPropertyName}(UnsafeSupplier<${propertyType}, Exception> ${propertyName}UnsafeSupplier) {
			<#if propertySchema.jsonMap>
				if (${propertyName}UnsafeSupplier == null) {
					set${capitalizedPropertyName}((${propertyType}) null);

					return;
				}

				try {
					set${capitalizedPropertyName}(${propertyName}UnsafeSupplier.get());
				}
				catch (RuntimeException runtimeException) {
					throw runtimeException;
				}
				catch (Exception exception) {
					throw new RuntimeException(exception);
				}
			<#else>
				_${propertyName}Supplier = () -> {
					try {
						return ${propertyName}UnsafeSupplier.get();
					}
					catch (RuntimeException runtimeException) {
						throw runtimeException;
					}
					catch (Exception exception) {
						throw new RuntimeException(exception);
					}
				};
			</#if>
		}

		<#if propertySchema.deprecated>
			@Deprecated
		</#if>
		@GraphQLField(
			<#if propertySchema.description??>
				description = "${propertySchema.description?j_string}"
			</#if>
		)
		<#if propertySchema.jsonMap>
			@JsonAnySetter
		</#if>
		<#if freeMarkerTool.isVersionCompatible(configYAML, 3) && propertySchema.jsonString>
			@JsonDeserialize(using = JSONStringStdDeserializer.class)
		</#if>
		@JsonProperty(
			<#if propertySchema.readOnly>
				access = JsonProperty.Access.READ_ONLY
			<#elseif propertySchema.writeOnly>
				access = JsonProperty.Access.WRITE_ONLY
			<#else>
				access = JsonProperty.Access.READ_WRITE
			</#if>

			<#if propertySchema.name?? && !stringUtil.equals(propertyName, propertySchema.name)>
				, value = "${propertySchema.name}"
			</#if>
		)
		<#if propertySchema.xml??>
			@XmlElement(name = "${propertySchema.xml.name}")
		</#if>
		<#if schema.requiredPropertySchemaNames?? && schema.requiredPropertySchemaNames?seq_contains(propertyName)>
			<#if stringUtil.equals(propertyType, "String")>
				@NotEmpty
			<#else>
				@NotNull
			</#if>
		</#if>

		<#if propertySchema.jsonMap>
			@JsonAnyGetter
			protected ${propertyType} ${propertyName} = Collections.synchronizedMap(new LinkedHashMap<>());
		<#else>
			protected ${propertyType} ${propertyName};

			@JsonIgnore
			private Supplier<${propertyType}> _${propertyName}Supplier;
		</#if>
	</#list>

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ${schemaName})) {
			return false;
		}

		${schemaName} ${schemaVarName} = (${schemaName})object;

		return Objects.equals(toString(), ${schemaVarName}.toString());
	}

	<#if jsonMapPropertyNames?has_content>
		public Object getPropertyValue(String propertyName) {
			<#list properties?keys as propertyName>
				<#if jsonMapPropertyNames?seq_contains(propertyName)>
					<#continue>
				</#if>

				<#assign capitalizedPropertyName = propertyName?cap_first />

				<#if enumSchemas?keys?seq_contains(propertyType)>
					<#assign capitalizedPropertyName = propertyType />
				</#if>

				if (Objects.equals(propertyName, "${propertyName}")) {
					return get${capitalizedPropertyName}();
				}
				else
			</#list>

			{
				<#list jsonMapPropertyNames as propertyName>
					if (${propertyName}.containsKey(propertyName)) {
						Object value = ${propertyName}.get(propertyName);

						if (!(value instanceof UnsafeSupplier<?, ?>)) {
							return value;
						}

						UnsafeSupplier<?, ?> unsafeSupplier = (UnsafeSupplier<?, ?>)value;

						try {
							return unsafeSupplier.get();
						}
						catch (Throwable throwable) {
							throw new RuntimeException(throwable);
						}
					}
				</#list>
			}

			return null;
		}

		private final class CachedUnsafeSupplier<T, E extends Throwable> implements UnsafeSupplier<T, E> {

			public CachedUnsafeSupplier(UnsafeSupplier<T, E> unsafeSupplier) {
				_unsafeSupplier = unsafeSupplier;
			}

			public T get() throws E {
				if (_set) {
					return _value;
				}

				synchronized (_unsafeSupplier) {
					_value = _unsafeSupplier.get();

					_set = true;
				}

				return _value;
			}

			private boolean _set;
			private final UnsafeSupplier<T, E> _unsafeSupplier;
			private T _value;

		}
	</#if>

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		<#assign
			toStringEnumSchemas = enumSchemas
			toStringProperties = properties
		/>

		<#if dtoParentClassName?has_content>
			<#assign
				dtoParentSchema = allSchemas[dtoParentClassName]

				toStringEnumSchemas = toStringEnumSchemas + freeMarkerTool.getDTOEnumSchemas(configYAML, openAPIYAML, dtoParentSchema)
				toStringProperties = toStringProperties + freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, dtoParentSchema, allSchemas)
			/>
		</#if>

		<#list toStringProperties?keys as propertyName>
			<#assign propertyType = toStringProperties[propertyName] />

			<#if stringUtil.equals(propertyType, "Date") || stringUtil.equals(propertyType, "Date[]")>
				DateFormat liferayToJSONDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

				<#break>
			</#if>
		</#list>

		<#list toStringProperties?keys as propertyName>
			<#assign
				capitalizedPropertyName = propertyName?cap_first
				propertyType = toStringProperties[propertyName]

				propertySchema = freeMarkerTool.getDTOPropertySchema(configYAML, propertyName, schema, allSchemas)!
			/>

			<#if dtoParentClassName?has_content && !propertySchema?has_content>
				<#assign propertySchema = freeMarkerTool.getDTOPropertySchema(configYAML, propertyName, dtoParentSchema, allSchemas) />
			</#if>

			<#if toStringEnumSchemas?keys?seq_contains(propertyType)>
				<#assign capitalizedPropertyName = propertyType />
			</#if>

			${propertyType} ${propertyName} = get${capitalizedPropertyName}();

			if (${propertyName} != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				<#if propertySchema.name?? && !stringUtil.equals(propertyName, propertySchema.name)>
					<#assign key = propertySchema.name />
				<#else>
					<#assign key = propertyName />
				</#if>

				sb.append("\"${key}\": ");

				<#if allSchemas[propertyType]??>
					sb.append(String.valueOf(${propertyName}));
				<#elseif stringUtil.equals(propertyType, "Object")>
					if (${propertyName} instanceof Map) {
						sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)${propertyName}));
					}
					else if (${propertyName} instanceof String) {
						sb.append("\"");
						sb.append(_escape((String)${propertyName}));
						sb.append("\"");
					}
					else {
						sb.append(${propertyName});
					}
				<#else>
					<#if propertyType?contains("[]")>
						sb.append("[");

						for (int i = 0; i < ${propertyName}.length; i++) {
							<#if stringUtil.equals(propertyType, "Date[]") || stringUtil.equals(propertyType, "Object[]") || stringUtil.equals(propertyType, "String[]") || toStringEnumSchemas?keys?seq_contains(propertyType)>
								sb.append("\"");

								<#if stringUtil.equals(propertyType, "Date[]")>
									sb.append(liferayToJSONDateFormat.format(${propertyName}[i]));
								<#elseif stringUtil.equals(propertyType, "Object[]") || stringUtil.equals(propertyType, "String[]")>
									sb.append(_escape(${propertyName}[i]));
								<#else>
									sb.append(${propertyName}[i]);
								</#if>

								sb.append("\"");
							<#elseif stringUtil.startsWith(propertyType, "Map<")>
								sb.append(_toJSON(${propertyName}[i]));
							<#elseif allSchemas[propertyType?remove_ending("[]")]??>
								sb.append(String.valueOf(${propertyName}[i]));
							<#else>
								sb.append(${propertyName}[i]);
							</#if>

							if ((i + 1) < ${propertyName}.length) {
								sb.append(", ");
							}
						}

						sb.append("]");
					<#else>
						<#if stringUtil.equals(propertyType, "Date") || stringUtil.equals(propertyType, "String") || toStringEnumSchemas?keys?seq_contains(propertyType)>
							sb.append("\"");

							<#if stringUtil.equals(propertyType, "Date")>
								sb.append(liferayToJSONDateFormat.format(${propertyName}));
							<#elseif stringUtil.equals(propertyType, "String")>
								sb.append(_escape(${propertyName}));
							<#else>
								sb.append(${propertyName});
							</#if>

							sb.append("\"");
						<#elseif stringUtil.startsWith(propertyType, "Map<")>
							sb.append(_toJSON(${propertyName}));
						<#else>
							sb.append(${propertyName});
						</#if>
					</#if>
				</#if>
			}
		</#list>

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY, defaultValue = "${configYAML.apiPackagePath}.dto.${escapedVersion}.${schemaName}", name = "x-class-name")
	public String xClassName;

	<#list enumSchemas?keys as enumName>
		@GraphQLName("${enumName}")
		public static enum ${enumName} {

		<#list enumSchemas[enumName].enumValues as enumValue>
			${freeMarkerTool.getEnumFieldName(enumValue)}("${enumValue}")

			<#if enumValue_has_next>
				,
			</#if>
		</#list>;

		@JsonCreator
		public static ${enumName} create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (${enumName} ${freeMarkerTool.getSchemaVarName(enumName)} : values()) {
				if (Objects.equals(${freeMarkerTool.getSchemaVarName(enumName)}.getValue(), value)) {
					return ${freeMarkerTool.getSchemaVarName(enumName)};
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private ${enumName}(String value) {
			_value = value;
		}

		private final String _value;

		}
	</#list>

	private static String _escape(Object object) {
		return StringUtil.replace(String.valueOf(object), _JSON_ESCAPE_STRINGS[0], _JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
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
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[]) value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>) value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}