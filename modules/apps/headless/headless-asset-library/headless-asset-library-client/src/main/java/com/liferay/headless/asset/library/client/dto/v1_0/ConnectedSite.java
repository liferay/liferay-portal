/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.client.dto.v1_0;

import com.liferay.headless.asset.library.client.function.UnsafeSupplier;
import com.liferay.headless.asset.library.client.serdes.v1_0.ConnectedSiteSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
public class ConnectedSite implements Cloneable, Serializable {

	public static ConnectedSite toDTO(String json) {
		return ConnectedSiteSerDes.toDTO(json);
	}

	public String getDescriptiveName() {
		return descriptiveName;
	}

	public void setDescriptiveName(String descriptiveName) {
		this.descriptiveName = descriptiveName;
	}

	public void setDescriptiveName(
		UnsafeSupplier<String, Exception> descriptiveNameUnsafeSupplier) {

		try {
			descriptiveName = descriptiveNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String descriptiveName;

	public Map<String, String> getDescriptiveName_i18n() {
		return descriptiveName_i18n;
	}

	public void setDescriptiveName_i18n(
		Map<String, String> descriptiveName_i18n) {

		this.descriptiveName_i18n = descriptiveName_i18n;
	}

	public void setDescriptiveName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			descriptiveName_i18nUnsafeSupplier) {

		try {
			descriptiveName_i18n = descriptiveName_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> descriptiveName_i18n;

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

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public void setLogo(UnsafeSupplier<String, Exception> logoUnsafeSupplier) {
		try {
			logo = logoUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String logo;

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

	public Map<String, String> getName_i18n() {
		return name_i18n;
	}

	public void setName_i18n(Map<String, String> name_i18n) {
		this.name_i18n = name_i18n;
	}

	public void setName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			name_i18nUnsafeSupplier) {

		try {
			name_i18n = name_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> name_i18n;

	public Boolean getSearchable() {
		return searchable;
	}

	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
	}

	public void setSearchable(
		UnsafeSupplier<Boolean, Exception> searchableUnsafeSupplier) {

		try {
			searchable = searchableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean searchable;

	public StagingType getStagingType() {
		return stagingType;
	}

	public String getStagingTypeAsString() {
		if (stagingType == null) {
			return null;
		}

		return stagingType.toString();
	}

	public void setStagingType(StagingType stagingType) {
		this.stagingType = stagingType;
	}

	public void setStagingType(
		UnsafeSupplier<StagingType, Exception> stagingTypeUnsafeSupplier) {

		try {
			stagingType = stagingTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected StagingType stagingType;

	public Type getType() {
		return type;
	}

	public String getTypeAsString() {
		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Type type;

	@Override
	public ConnectedSite clone() throws CloneNotSupportedException {
		return (ConnectedSite)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ConnectedSite)) {
			return false;
		}

		ConnectedSite connectedSite = (ConnectedSite)object;

		return Objects.equals(toString(), connectedSite.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ConnectedSiteSerDes.toJSON(this);
	}

	public static enum StagingType {

		LIVE("LIVE"), STAGING("STAGING");

		public static StagingType create(String value) {
			for (StagingType stagingType : values()) {
				if (Objects.equals(stagingType.getValue(), value) ||
					Objects.equals(stagingType.name(), value)) {

					return stagingType;
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

		private StagingType(String value) {
			_value = value;
		}

		private final String _value;

	}

	public static enum Type {

		DESIGN_LIBRARY("DesignLibrary"), SITE("Site"),
		SITE_TEMPLATE("SiteTemplate");

		public static Type create(String value) {
			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value) ||
					Objects.equals(type.name(), value)) {

					return type;
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

		private Type(String value) {
			_value = value;
		}

		private final String _value;

	}

}
// LIFERAY-REST-BUILDER-HASH:-1035538388