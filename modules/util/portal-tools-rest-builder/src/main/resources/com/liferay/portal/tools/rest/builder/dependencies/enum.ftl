package ${configYAML.apiPackagePath}.constant.${escapedVersion};

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

	@JsonCreator
	public static ${schemaName} fromString(String value) {
		if ((value == null) || value.equals("")) {
			return null;
		}

		for (${schemaName} ${schemaVarName} : values()) {
			if (Objects.equals(${schemaVarName}.getValue(), value)) {
				return ${schemaVarName};
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

	private ${schemaName}(String value) {
		_value = value;
	}

	private final String _value;

}