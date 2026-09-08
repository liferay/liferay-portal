/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.client.dto.v1_0;

import com.liferay.mcp.server.rest.client.function.UnsafeSupplier;
import com.liferay.mcp.server.rest.client.serdes.v1_0.ToolSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class Tool implements Cloneable, Serializable {

	public static Tool toDTO(String json) {
		return ToolSerDes.toDTO(json);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

	public Map<String, ?> getInputSchema() {
		return inputSchema;
	}

	public void setInputSchema(Map<String, ?> inputSchema) {
		this.inputSchema = inputSchema;
	}

	public void setInputSchema(
		UnsafeSupplier<Map<String, ?>, Exception> inputSchemaUnsafeSupplier) {

		try {
			inputSchema = inputSchemaUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, ?> inputSchema;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public Map<String, ?> getOutputSchema() {
		return outputSchema;
	}

	public void setOutputSchema(Map<String, ?> outputSchema) {
		this.outputSchema = outputSchema;
	}

	public void setOutputSchema(
		UnsafeSupplier<Map<String, ?>, Exception> outputSchemaUnsafeSupplier) {

		try {
			outputSchema = outputSchemaUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, ?> outputSchema;

	@Override
	public Tool clone() throws CloneNotSupportedException {
		return (Tool)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Tool)) {
			return false;
		}

		Tool tool = (Tool)object;

		return Objects.equals(toString(), tool.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ToolSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1228513677