package ${configYAML.apiPackagePath}.client.dto.${escapedVersion};

<#list globalEnumSchemas?keys as globalEnumSchemaName>
	import ${configYAML.apiPackagePath}.client.constant.${escapedVersion}.${globalEnumSchemaName};
</#list>

<#list allExternalSchemas?keys as externalSchemaName>
	import ${configYAML.apiPackagePath}.client.dto.${escapedVersion}.${externalSchemaName};
</#list>

<#list allSchemas?keys as schemaName>
	import ${configYAML.apiPackagePath}.client.dto.${escapedVersion}.${schemaName};
</#list>

import ${configYAML.apiPackagePath}.client.function.UnsafeSupplier;
import ${configYAML.apiPackagePath}.client.serdes.${escapedVersion}.${schemaName}SerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author ${configYAML.author}
 * @generated
 */
@Generated("")

<#assign dtoParentClassName = freeMarkerTool.getDTOParentClassName(openAPIYAML, schemaName)! />

public <#if schema.discriminator?has_content>abstract</#if> class ${schemaName} <#if dtoParentClassName?has_content>extends ${dtoParentClassName}</#if> implements Cloneable, Serializable {

	public static ${schemaName} toDTO(String json) {
		return ${schemaName}SerDes.toDTO(json);
	}

	<#assign
		enumSchemas = freeMarkerTool.getDTOEnumSchemas(configYAML, openAPIYAML, schema)
		properties = freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, schema, allSchemas)
	/>

	<#list properties?keys as propertyName>
		<#assign capitalizedPropertyName = propertyName?cap_first />

		<#if enumSchemas?keys?seq_contains(properties[propertyName])>
			<#assign capitalizedPropertyName = properties[propertyName] />
		</#if>

		<#assign propertyType = properties[propertyName]?replace("com.liferay.portal.vulcan.", "${configYAML.apiPackagePath}.client.") />

		public ${propertyType} get${capitalizedPropertyName}() {
			return ${propertyName};
		}

		<#if enumSchemas?keys?seq_contains(propertyType)>
			public String get${capitalizedPropertyName}AsString() {
				if (${propertyName} == null) {
					return null;
				}

				return ${propertyName}.toString();
			}
		</#if>

		public void set${capitalizedPropertyName}(${propertyType} ${propertyName}) {
			this.${propertyName} = ${propertyName};
		}

		public void set${capitalizedPropertyName}(UnsafeSupplier<${propertyType}, Exception> ${propertyName}UnsafeSupplier) {
			try {
				${propertyName} = ${propertyName}UnsafeSupplier.get();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		protected ${propertyType} ${propertyName};
	</#list>

	@Override
	public ${schemaName} clone() throws CloneNotSupportedException {
		return (${schemaName})super.clone();
	}

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

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ${schemaName}SerDes.toJSON(this);
	}

	<#list enumSchemas?keys as enumName>
		public static enum ${enumName} {

		<#list enumSchemas[enumName].enumValues as enumValue>
			${freeMarkerTool.getEnumFieldName(enumValue)}("${enumValue}")

			<#if enumValue_has_next>
				,
			</#if>
		</#list>;

		public static ${enumName} create(String value) {
			for (${enumName} ${freeMarkerTool.getSchemaVarName(enumName)} : values()) {
				if (Objects.equals(${freeMarkerTool.getSchemaVarName(enumName)}.getValue(), value) || Objects.equals(${freeMarkerTool.getSchemaVarName(enumName)}.name(), value)) {
					return ${freeMarkerTool.getSchemaVarName(enumName)};
				}
			}

			return null;
		}

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

}