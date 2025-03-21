package ${configYAML.apiPackagePath}.client.constant.${escapedVersion};

import jakarta.annotation.Generated;

import java.util.Objects;

/**
 * @author ${configYAML.author}
 * @generated
 */
@Generated("")
public enum ${schemaName} {

	<#list schema.enumValues as enumValue>
		${freeMarkerTool.getEnumFieldName(enumValue)}("${enumValue}")

		<#if enumValue_has_next>
			,
		</#if>
	</#list>;

	public static ${schemaName} create(String value) {
		for (${schemaName} ${schemaVarName} : values()) {
			if (Objects.equals(${schemaVarName}.getValue(), value)) {
				return ${schemaVarName};
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

	private ${schemaName}(String value) {
		_value = value;
	}

	private final String _value;

}