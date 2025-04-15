/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.admin.rest.client.dto.v1_0;

import com.liferay.saml.admin.rest.client.function.UnsafeSupplier;
import com.liferay.saml.admin.rest.client.serdes.v1_0.SamlProviderSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
public class SamlProvider implements Cloneable, Serializable {

	public static SamlProvider toDTO(String json) {
		return SamlProviderSerDes.toDTO(json);
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setEnabled(
		UnsafeSupplier<Boolean, Exception> enabledUnsafeSupplier) {

		try {
			enabled = enabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enabled;

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public void setEntityId(
		UnsafeSupplier<String, Exception> entityIdUnsafeSupplier) {

		try {
			entityId = entityIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String entityId;

	public Idp getIdp() {
		return idp;
	}

	public void setIdp(Idp idp) {
		this.idp = idp;
	}

	public void setIdp(UnsafeSupplier<Idp, Exception> idpUnsafeSupplier) {
		try {
			idp = idpUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Idp idp;

	public String getKeyStoreCredentialPassword() {
		return keyStoreCredentialPassword;
	}

	public void setKeyStoreCredentialPassword(
		String keyStoreCredentialPassword) {

		this.keyStoreCredentialPassword = keyStoreCredentialPassword;
	}

	public void setKeyStoreCredentialPassword(
		UnsafeSupplier<String, Exception>
			keyStoreCredentialPasswordUnsafeSupplier) {

		try {
			keyStoreCredentialPassword =
				keyStoreCredentialPasswordUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String keyStoreCredentialPassword;

	public Role getRole() {
		return role;
	}

	public String getRoleAsString() {
		if (role == null) {
			return null;
		}

		return role.toString();
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setRole(UnsafeSupplier<Role, Exception> roleUnsafeSupplier) {
		try {
			role = roleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Role role;

	public Boolean getSignMetadata() {
		return signMetadata;
	}

	public void setSignMetadata(Boolean signMetadata) {
		this.signMetadata = signMetadata;
	}

	public void setSignMetadata(
		UnsafeSupplier<Boolean, Exception> signMetadataUnsafeSupplier) {

		try {
			signMetadata = signMetadataUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean signMetadata;

	public Sp getSp() {
		return sp;
	}

	public void setSp(Sp sp) {
		this.sp = sp;
	}

	public void setSp(UnsafeSupplier<Sp, Exception> spUnsafeSupplier) {
		try {
			sp = spUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Sp sp;

	public Boolean getSslRequired() {
		return sslRequired;
	}

	public void setSslRequired(Boolean sslRequired) {
		this.sslRequired = sslRequired;
	}

	public void setSslRequired(
		UnsafeSupplier<Boolean, Exception> sslRequiredUnsafeSupplier) {

		try {
			sslRequired = sslRequiredUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean sslRequired;

	@Override
	public SamlProvider clone() throws CloneNotSupportedException {
		return (SamlProvider)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SamlProvider)) {
			return false;
		}

		SamlProvider samlProvider = (SamlProvider)object;

		return Objects.equals(toString(), samlProvider.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SamlProviderSerDes.toJSON(this);
	}

	public static enum Role {

		IDP("idp"), SP("sp");

		public static Role create(String value) {
			for (Role role : values()) {
				if (Objects.equals(role.getValue(), value) ||
					Objects.equals(role.name(), value)) {

					return role;
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

		private Role(String value) {
			_value = value;
		}

		private final String _value;

	}

}