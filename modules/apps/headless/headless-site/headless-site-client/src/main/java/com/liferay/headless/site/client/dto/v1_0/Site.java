/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.site.client.dto.v1_0;

import com.liferay.headless.site.client.function.UnsafeSupplier;
import com.liferay.headless.site.client.serdes.v1_0.SiteSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class Site implements Cloneable, Serializable {

	public static Site toDTO(String json) {
		return SiteSerDes.toDTO(json);
	}

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

	public Map<String, String> getDescription() {
		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<Map<String, String>, Exception>
			descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> description;

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

	public String getFriendlyUrlPath() {
		return friendlyUrlPath;
	}

	public void setFriendlyUrlPath(String friendlyUrlPath) {
		this.friendlyUrlPath = friendlyUrlPath;
	}

	public void setFriendlyUrlPath(
		UnsafeSupplier<String, Exception> friendlyUrlPathUnsafeSupplier) {

		try {
			friendlyUrlPath = friendlyUrlPathUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String friendlyUrlPath;

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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		try {
			key = keyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String key;

	public Boolean getManualMembership() {
		return manualMembership;
	}

	public void setManualMembership(Boolean manualMembership) {
		this.manualMembership = manualMembership;
	}

	public void setManualMembership(
		UnsafeSupplier<Boolean, Exception> manualMembershipUnsafeSupplier) {

		try {
			manualMembership = manualMembershipUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean manualMembership;

	public Integer getMembershipRestriction() {
		return membershipRestriction;
	}

	public void setMembershipRestriction(Integer membershipRestriction) {
		this.membershipRestriction = membershipRestriction;
	}

	public void setMembershipRestriction(
		UnsafeSupplier<Integer, Exception>
			membershipRestrictionUnsafeSupplier) {

		try {
			membershipRestriction = membershipRestrictionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer membershipRestriction;

	public MembershipType getMembershipType() {
		return membershipType;
	}

	public String getMembershipTypeAsString() {
		if (membershipType == null) {
			return null;
		}

		return membershipType.toString();
	}

	public void setMembershipType(MembershipType membershipType) {
		this.membershipType = membershipType;
	}

	public void setMembershipType(
		UnsafeSupplier<MembershipType, Exception>
			membershipTypeUnsafeSupplier) {

		try {
			membershipType = membershipTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MembershipType membershipType;

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

	public String getParentSiteExternalReferenceCode() {
		return parentSiteExternalReferenceCode;
	}

	public void setParentSiteExternalReferenceCode(
		String parentSiteExternalReferenceCode) {

		this.parentSiteExternalReferenceCode = parentSiteExternalReferenceCode;
	}

	public void setParentSiteExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentSiteExternalReferenceCodeUnsafeSupplier) {

		try {
			parentSiteExternalReferenceCode =
				parentSiteExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String parentSiteExternalReferenceCode;

	public String getParentSiteKey() {
		return parentSiteKey;
	}

	public void setParentSiteKey(String parentSiteKey) {
		this.parentSiteKey = parentSiteKey;
	}

	public void setParentSiteKey(
		UnsafeSupplier<String, Exception> parentSiteKeyUnsafeSupplier) {

		try {
			parentSiteKey = parentSiteKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String parentSiteKey;

	public String getTemplateKey() {
		return templateKey;
	}

	public void setTemplateKey(String templateKey) {
		this.templateKey = templateKey;
	}

	public void setTemplateKey(
		UnsafeSupplier<String, Exception> templateKeyUnsafeSupplier) {

		try {
			templateKey = templateKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String templateKey;

	public TemplateType getTemplateType() {
		return templateType;
	}

	public String getTemplateTypeAsString() {
		if (templateType == null) {
			return null;
		}

		return templateType.toString();
	}

	public void setTemplateType(TemplateType templateType) {
		this.templateType = templateType;
	}

	public void setTemplateType(
		UnsafeSupplier<TemplateType, Exception> templateTypeUnsafeSupplier) {

		try {
			templateType = templateTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TemplateType templateType;

	public Map<String, String> getTypeSettings() {
		return typeSettings;
	}

	public void setTypeSettings(Map<String, String> typeSettings) {
		this.typeSettings = typeSettings;
	}

	public void setTypeSettings(
		UnsafeSupplier<Map<String, String>, Exception>
			typeSettingsUnsafeSupplier) {

		try {
			typeSettings = typeSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> typeSettings;

	@Override
	public Site clone() throws CloneNotSupportedException {
		return (Site)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Site)) {
			return false;
		}

		Site site = (Site)object;

		return Objects.equals(toString(), site.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SiteSerDes.toJSON(this);
	}

	public static enum MembershipType {

		OPEN("open"), PRIVATE("private"), RESTRICTED("restricted");

		public static MembershipType create(String value) {
			for (MembershipType membershipType : values()) {
				if (Objects.equals(membershipType.getValue(), value) ||
					Objects.equals(membershipType.name(), value)) {

					return membershipType;
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

		private MembershipType(String value) {
			_value = value;
		}

		private final String _value;

	}

	public static enum TemplateType {

		SITE_INITIALIZER("site-initializer"), SITE_TEMPLATE("site-template");

		public static TemplateType create(String value) {
			for (TemplateType templateType : values()) {
				if (Objects.equals(templateType.getValue(), value) ||
					Objects.equals(templateType.name(), value)) {

					return templateType;
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

		private TemplateType(String value) {
			_value = value;
		}

		private final String _value;

	}

}