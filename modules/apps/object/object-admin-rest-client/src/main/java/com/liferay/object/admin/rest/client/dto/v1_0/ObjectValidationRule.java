/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.dto.v1_0;

import com.liferay.object.admin.rest.client.function.UnsafeSupplier;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectValidationRuleSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectValidationRule implements Cloneable, Serializable {

	public static ObjectValidationRule toDTO(String json) {
		return ObjectValidationRuleSerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		try {
			active = activeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean active;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCreated;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		try {
			dateModified = dateModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateModified;

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public void setEngine(
		UnsafeSupplier<String, Exception> engineUnsafeSupplier) {

		try {
			engine = engineUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String engine;

	public String getEngineLabel() {
		return engineLabel;
	}

	public void setEngineLabel(String engineLabel) {
		this.engineLabel = engineLabel;
	}

	public void setEngineLabel(
		UnsafeSupplier<String, Exception> engineLabelUnsafeSupplier) {

		try {
			engineLabel = engineLabelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String engineLabel;

	public Map<String, String> getErrorLabel() {
		return errorLabel;
	}

	public void setErrorLabel(Map<String, String> errorLabel) {
		this.errorLabel = errorLabel;
	}

	public void setErrorLabel(
		UnsafeSupplier<Map<String, String>, Exception>
			errorLabelUnsafeSupplier) {

		try {
			errorLabel = errorLabelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> errorLabel;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public Map<String, String> getName() {
		return name;
	}

	public void setName(Map<String, String> name) {
		this.name = name;
	}

	public void setName(
		UnsafeSupplier<Map<String, String>, Exception> nameUnsafeSupplier) {

		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> name;

	public String getObjectDefinitionExternalReferenceCode() {
		return objectDefinitionExternalReferenceCode;
	}

	public void setObjectDefinitionExternalReferenceCode(
		String objectDefinitionExternalReferenceCode) {

		this.objectDefinitionExternalReferenceCode =
			objectDefinitionExternalReferenceCode;
	}

	public void setObjectDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			objectDefinitionExternalReferenceCodeUnsafeSupplier) {

		try {
			objectDefinitionExternalReferenceCode =
				objectDefinitionExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectDefinitionExternalReferenceCode;

	public Long getObjectDefinitionId() {
		return objectDefinitionId;
	}

	public void setObjectDefinitionId(Long objectDefinitionId) {
		this.objectDefinitionId = objectDefinitionId;
	}

	public void setObjectDefinitionId(
		UnsafeSupplier<Long, Exception> objectDefinitionIdUnsafeSupplier) {

		try {
			objectDefinitionId = objectDefinitionIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long objectDefinitionId;

	public ObjectValidationRuleSetting[] getObjectValidationRuleSettings() {
		return objectValidationRuleSettings;
	}

	public void setObjectValidationRuleSettings(
		ObjectValidationRuleSetting[] objectValidationRuleSettings) {

		this.objectValidationRuleSettings = objectValidationRuleSettings;
	}

	public void setObjectValidationRuleSettings(
		UnsafeSupplier<ObjectValidationRuleSetting[], Exception>
			objectValidationRuleSettingsUnsafeSupplier) {

		try {
			objectValidationRuleSettings =
				objectValidationRuleSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectValidationRuleSetting[] objectValidationRuleSettings;

	public OutputType getOutputType() {
		return outputType;
	}

	public String getOutputTypeAsString() {
		if (outputType == null) {
			return null;
		}

		return outputType.toString();
	}

	public void setOutputType(OutputType outputType) {
		this.outputType = outputType;
	}

	public void setOutputType(
		UnsafeSupplier<OutputType, Exception> outputTypeUnsafeSupplier) {

		try {
			outputType = outputTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected OutputType outputType;

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void setScript(
		UnsafeSupplier<String, Exception> scriptUnsafeSupplier) {

		try {
			script = scriptUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String script;

	public Boolean getSystem() {
		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;
	}

	public void setSystem(
		UnsafeSupplier<Boolean, Exception> systemUnsafeSupplier) {

		try {
			system = systemUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean system;

	@Override
	public ObjectValidationRule clone() throws CloneNotSupportedException {
		return (ObjectValidationRule)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectValidationRule)) {
			return false;
		}

		ObjectValidationRule objectValidationRule =
			(ObjectValidationRule)object;

		return Objects.equals(toString(), objectValidationRule.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectValidationRuleSerDes.toJSON(this);
	}

	public static enum OutputType {

		FULL_VALIDATION("fullValidation"),
		PARTIAL_VALIDATION("partialValidation");

		public static OutputType create(String value) {
			for (OutputType outputType : values()) {
				if (Objects.equals(outputType.getValue(), value) ||
					Objects.equals(outputType.name(), value)) {

					return outputType;
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

		private OutputType(String value) {
			_value = value;
		}

		private final String _value;

	}

}