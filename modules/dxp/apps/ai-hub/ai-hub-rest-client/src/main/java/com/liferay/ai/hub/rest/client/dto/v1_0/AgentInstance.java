/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.client.dto.v1_0;

import com.liferay.ai.hub.rest.client.function.UnsafeSupplier;
import com.liferay.ai.hub.rest.client.serdes.v1_0.AgentInstanceSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
public class AgentInstance implements Cloneable, Serializable {

	public static AgentInstance toDTO(String json) {
		return AgentInstanceSerDes.toDTO(json);
	}

	public String getAgentDefinitionExternalReferenceCode() {
		return agentDefinitionExternalReferenceCode;
	}

	public void setAgentDefinitionExternalReferenceCode(
		String agentDefinitionExternalReferenceCode) {

		this.agentDefinitionExternalReferenceCode =
			agentDefinitionExternalReferenceCode;
	}

	public void setAgentDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			agentDefinitionExternalReferenceCodeUnsafeSupplier) {

		try {
			agentDefinitionExternalReferenceCode =
				agentDefinitionExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String agentDefinitionExternalReferenceCode;

	public Boolean getAsynchronous() {
		return asynchronous;
	}

	public void setAsynchronous(Boolean asynchronous) {
		this.asynchronous = asynchronous;
	}

	public void setAsynchronous(
		UnsafeSupplier<Boolean, Exception> asynchronousUnsafeSupplier) {

		try {
			asynchronous = asynchronousUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean asynchronous;

	public Map<String, ?> getContext() {
		return context;
	}

	public void setContext(Map<String, ?> context) {
		this.context = context;
	}

	public void setContext(
		UnsafeSupplier<Map<String, ?>, Exception> contextUnsafeSupplier) {

		try {
			context = contextUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, ?> context;

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

	public InstructionDefinitionScope getInstructionDefinitionScope() {
		return instructionDefinitionScope;
	}

	public String getInstructionDefinitionScopeAsString() {
		if (instructionDefinitionScope == null) {
			return null;
		}

		return instructionDefinitionScope.toString();
	}

	public void setInstructionDefinitionScope(
		InstructionDefinitionScope instructionDefinitionScope) {

		this.instructionDefinitionScope = instructionDefinitionScope;
	}

	public void setInstructionDefinitionScope(
		UnsafeSupplier<InstructionDefinitionScope, Exception>
			instructionDefinitionScopeUnsafeSupplier) {

		try {
			instructionDefinitionScope =
				instructionDefinitionScopeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected InstructionDefinitionScope instructionDefinitionScope;

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setOutput(
		UnsafeSupplier<String, Exception> outputUnsafeSupplier) {

		try {
			output = outputUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String output;

	public String getSseEventSinkKey() {
		return sseEventSinkKey;
	}

	public void setSseEventSinkKey(String sseEventSinkKey) {
		this.sseEventSinkKey = sseEventSinkKey;
	}

	public void setSseEventSinkKey(
		UnsafeSupplier<String, Exception> sseEventSinkKeyUnsafeSupplier) {

		try {
			sseEventSinkKey = sseEventSinkKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sseEventSinkKey;

	@Override
	public AgentInstance clone() throws CloneNotSupportedException {
		return (AgentInstance)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AgentInstance)) {
			return false;
		}

		AgentInstance agentInstance = (AgentInstance)object;

		return Objects.equals(toString(), agentInstance.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AgentInstanceSerDes.toJSON(this);
	}

	public static enum InstructionDefinitionScope {

		CLICK_TO_CHAT("clickToChat"), CMS("cms"), EVERYWHERE("everywhere");

		public static InstructionDefinitionScope create(String value) {
			for (InstructionDefinitionScope instructionDefinitionScope :
					values()) {

				if (Objects.equals(
						instructionDefinitionScope.getValue(), value) ||
					Objects.equals(instructionDefinitionScope.name(), value)) {

					return instructionDefinitionScope;
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

		private InstructionDefinitionScope(String value) {
			_value = value;
		}

		private final String _value;

	}

}
// LIFERAY-REST-BUILDER-HASH:624860553