/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.SiteSerDes;

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

	public AnalyticsConfiguration getAnalyticsConfiguration() {
		return analyticsConfiguration;
	}

	public void setAnalyticsConfiguration(
		AnalyticsConfiguration analyticsConfiguration) {

		this.analyticsConfiguration = analyticsConfiguration;
	}

	public void setAnalyticsConfiguration(
		UnsafeSupplier<AnalyticsConfiguration, Exception>
			analyticsConfigurationUnsafeSupplier) {

		try {
			analyticsConfiguration = analyticsConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AnalyticsConfiguration analyticsConfiguration;

	public Boolean getAssetAutoTaggingEnabled() {
		return assetAutoTaggingEnabled;
	}

	public void setAssetAutoTaggingEnabled(Boolean assetAutoTaggingEnabled) {
		this.assetAutoTaggingEnabled = assetAutoTaggingEnabled;
	}

	public void setAssetAutoTaggingEnabled(
		UnsafeSupplier<Boolean, Exception>
			assetAutoTaggingEnabledUnsafeSupplier) {

		try {
			assetAutoTaggingEnabled =
				assetAutoTaggingEnabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean assetAutoTaggingEnabled;

	public Boolean getContentSharingWithChildrenEnabled() {
		return contentSharingWithChildrenEnabled;
	}

	public void setContentSharingWithChildrenEnabled(
		Boolean contentSharingWithChildrenEnabled) {

		this.contentSharingWithChildrenEnabled =
			contentSharingWithChildrenEnabled;
	}

	public void setContentSharingWithChildrenEnabled(
		UnsafeSupplier<Boolean, Exception>
			contentSharingWithChildrenEnabledUnsafeSupplier) {

		try {
			contentSharingWithChildrenEnabled =
				contentSharingWithChildrenEnabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean contentSharingWithChildrenEnabled;

	public Creator getCreator() {
		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		try {
			creator = creatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Creator creator;

	public String getDefaultLanguageId() {
		return defaultLanguageId;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		this.defaultLanguageId = defaultLanguageId;
	}

	public void setDefaultLanguageId(
		UnsafeSupplier<String, Exception> defaultLanguageIdUnsafeSupplier) {

		try {
			defaultLanguageId = defaultLanguageIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String defaultLanguageId;

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

	public Map<String, String> getDescription_i18n() {
		return description_i18n;
	}

	public void setDescription_i18n(Map<String, String> description_i18n) {
		this.description_i18n = description_i18n;
	}

	public void setDescription_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			description_i18nUnsafeSupplier) {

		try {
			description_i18n = description_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> description_i18n;

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

	public Boolean getDirectoryIndexingEnabled() {
		return directoryIndexingEnabled;
	}

	public void setDirectoryIndexingEnabled(Boolean directoryIndexingEnabled) {
		this.directoryIndexingEnabled = directoryIndexingEnabled;
	}

	public void setDirectoryIndexingEnabled(
		UnsafeSupplier<Boolean, Exception>
			directoryIndexingEnabledUnsafeSupplier) {

		try {
			directoryIndexingEnabled =
				directoryIndexingEnabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean directoryIndexingEnabled;

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

	public Boolean getInheritLocales() {
		return inheritLocales;
	}

	public void setInheritLocales(Boolean inheritLocales) {
		this.inheritLocales = inheritLocales;
	}

	public void setInheritLocales(
		UnsafeSupplier<Boolean, Exception> inheritLocalesUnsafeSupplier) {

		try {
			inheritLocales = inheritLocalesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean inheritLocales;

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

	public String[] getLocales() {
		return locales;
	}

	public void setLocales(String[] locales) {
		this.locales = locales;
	}

	public void setLocales(
		UnsafeSupplier<String[], Exception> localesUnsafeSupplier) {

		try {
			locales = localesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] locales;

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

	public MapProviderKey getMapProviderKey() {
		return mapProviderKey;
	}

	public String getMapProviderKeyAsString() {
		if (mapProviderKey == null) {
			return null;
		}

		return mapProviderKey.toString();
	}

	public void setMapProviderKey(MapProviderKey mapProviderKey) {
		this.mapProviderKey = mapProviderKey;
	}

	public void setMapProviderKey(
		UnsafeSupplier<MapProviderKey, Exception>
			mapProviderKeyUnsafeSupplier) {

		try {
			mapProviderKey = mapProviderKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MapProviderKey mapProviderKey;

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

	public Boolean getMentionsEnabled() {
		return mentionsEnabled;
	}

	public void setMentionsEnabled(Boolean mentionsEnabled) {
		this.mentionsEnabled = mentionsEnabled;
	}

	public void setMentionsEnabled(
		UnsafeSupplier<Boolean, Exception> mentionsEnabledUnsafeSupplier) {

		try {
			mentionsEnabled = mentionsEnabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean mentionsEnabled;

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

	public com.liferay.headless.admin.site.client.permission.Permission[]
		getPermissions() {

		return permissions;
	}

	public void setPermissions(
		com.liferay.headless.admin.site.client.permission.Permission[]
			permissions) {

		this.permissions = permissions;
	}

	public void setPermissions(
		UnsafeSupplier
			<com.liferay.headless.admin.site.client.permission.Permission[],
			 Exception> permissionsUnsafeSupplier) {

		try {
			permissions = permissionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected com.liferay.headless.admin.site.client.permission.Permission[]
		permissions;

	public RatingsTypes getRatingsTypes() {
		return ratingsTypes;
	}

	public void setRatingsTypes(RatingsTypes ratingsTypes) {
		this.ratingsTypes = ratingsTypes;
	}

	public void setRatingsTypes(
		UnsafeSupplier<RatingsTypes, Exception> ratingsTypesUnsafeSupplier) {

		try {
			ratingsTypes = ratingsTypesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected RatingsTypes ratingsTypes;

	public Boolean getSharingEnabled() {
		return sharingEnabled;
	}

	public void setSharingEnabled(Boolean sharingEnabled) {
		this.sharingEnabled = sharingEnabled;
	}

	public void setSharingEnabled(
		UnsafeSupplier<Boolean, Exception> sharingEnabledUnsafeSupplier) {

		try {
			sharingEnabled = sharingEnabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean sharingEnabled;

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

	public Boolean getTrashEnabled() {
		return trashEnabled;
	}

	public void setTrashEnabled(Boolean trashEnabled) {
		this.trashEnabled = trashEnabled;
	}

	public void setTrashEnabled(
		UnsafeSupplier<Boolean, Exception> trashEnabledUnsafeSupplier) {

		try {
			trashEnabled = trashEnabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean trashEnabled;

	public Integer getTrashEntriesMaxAge() {
		return trashEntriesMaxAge;
	}

	public void setTrashEntriesMaxAge(Integer trashEntriesMaxAge) {
		this.trashEntriesMaxAge = trashEntriesMaxAge;
	}

	public void setTrashEntriesMaxAge(
		UnsafeSupplier<Integer, Exception> trashEntriesMaxAgeUnsafeSupplier) {

		try {
			trashEntriesMaxAge = trashEntriesMaxAgeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer trashEntriesMaxAge;

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

	public static enum MapProviderKey {

		GOOGLE_MAPS("GoogleMaps"), OPEN_STREET_MAP("OpenStreetMap");

		public static MapProviderKey create(String value) {
			for (MapProviderKey mapProviderKey : values()) {
				if (Objects.equals(mapProviderKey.getValue(), value) ||
					Objects.equals(mapProviderKey.name(), value)) {

					return mapProviderKey;
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

		private MapProviderKey(String value) {
			_value = value;
		}

		private final String _value;

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
// LIFERAY-REST-BUILDER-HASH:1131312237