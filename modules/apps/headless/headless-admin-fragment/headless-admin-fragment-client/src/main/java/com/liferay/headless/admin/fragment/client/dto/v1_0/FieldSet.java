/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.FieldSetSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FieldSet implements Cloneable, Serializable {

	public static FieldSet toDTO(String json) {
		return FieldSetSerDes.toDTO(json);
	}

	public Boolean getCollapsed() {
		return collapsed;
	}

	public void setCollapsed(Boolean collapsed) {
		this.collapsed = collapsed;
	}

	public void setCollapsed(
		UnsafeSupplier<Boolean, Exception> collapsedUnsafeSupplier) {

		try {
			collapsed = collapsedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean collapsed;

	public ConfigurationRole getConfigurationRole() {
		return configurationRole;
	}

	public String getConfigurationRoleAsString() {
		if (configurationRole == null) {
			return null;
		}

		return configurationRole.toString();
	}

	public void setConfigurationRole(ConfigurationRole configurationRole) {
		this.configurationRole = configurationRole;
	}

	public void setConfigurationRole(
		UnsafeSupplier<ConfigurationRole, Exception>
			configurationRoleUnsafeSupplier) {

		try {
			configurationRole = configurationRoleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ConfigurationRole configurationRole;

	public String getCustomComponentModule() {
		return customComponentModule;
	}

	public void setCustomComponentModule(String customComponentModule) {
		this.customComponentModule = customComponentModule;
	}

	public void setCustomComponentModule(
		UnsafeSupplier<String, Exception> customComponentModuleUnsafeSupplier) {

		try {
			customComponentModule = customComponentModuleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String customComponentModule;

	public Field[] getFields() {
		return fields;
	}

	public void setFields(Field[] fields) {
		this.fields = fields;
	}

	public void setFields(
		UnsafeSupplier<Field[], Exception> fieldsUnsafeSupplier) {

		try {
			fields = fieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Field[] fields;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		try {
			label = labelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String label;

	@Override
	public FieldSet clone() throws CloneNotSupportedException {
		return (FieldSet)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FieldSet)) {
			return false;
		}

		FieldSet fieldSet = (FieldSet)object;

		return Objects.equals(toString(), fieldSet.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FieldSetSerDes.toJSON(this);
	}

	public static enum ConfigurationRole {

		ADVANCED("advanced"), STYLE("style");

		public static ConfigurationRole create(String value) {
			for (ConfigurationRole configurationRole : values()) {
				if (Objects.equals(configurationRole.getValue(), value) ||
					Objects.equals(configurationRole.name(), value)) {

					return configurationRole;
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

		private ConfigurationRole(String value) {
			_value = value;
		}

		private final String _value;

	}

}
// LIFERAY-REST-BUILDER-HASH:552100560