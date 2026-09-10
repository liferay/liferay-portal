/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(description = "Represents the site being created.", value = "Site")
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Represents the site being created.",
	requiredProperties = {"name"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Site")
public class Site implements Serializable {

	public static Site toDTO(String json) {
		return ObjectMapperUtil.readValue(Site.class, json);
	}

	public static Site unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Site.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getActive() {
		if (_activeSupplier != null) {
			active = _activeSupplier.get();

			_activeSupplier = null;
		}

		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;

		_activeSupplier = null;
	}

	@JsonIgnore
	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		_activeSupplier = () -> {
			try {
				return activeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public AnalyticsConfiguration getAnalyticsConfiguration() {
		if (_analyticsConfigurationSupplier != null) {
			analyticsConfiguration = _analyticsConfigurationSupplier.get();

			_analyticsConfigurationSupplier = null;
		}

		return analyticsConfiguration;
	}

	public void setAnalyticsConfiguration(
		AnalyticsConfiguration analyticsConfiguration) {

		this.analyticsConfiguration = analyticsConfiguration;

		_analyticsConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setAnalyticsConfiguration(
		UnsafeSupplier<AnalyticsConfiguration, Exception>
			analyticsConfigurationUnsafeSupplier) {

		_analyticsConfigurationSupplier = () -> {
			try {
				return analyticsConfigurationUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected AnalyticsConfiguration analyticsConfiguration;

	@JsonIgnore
	private Supplier<AnalyticsConfiguration> _analyticsConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether to enable auto tagging of assets on this site."
	)
	public Boolean getAssetAutoTaggingEnabled() {
		if (_assetAutoTaggingEnabledSupplier != null) {
			assetAutoTaggingEnabled = _assetAutoTaggingEnabledSupplier.get();

			_assetAutoTaggingEnabledSupplier = null;
		}

		return assetAutoTaggingEnabled;
	}

	public void setAssetAutoTaggingEnabled(Boolean assetAutoTaggingEnabled) {
		this.assetAutoTaggingEnabled = assetAutoTaggingEnabled;

		_assetAutoTaggingEnabledSupplier = null;
	}

	@JsonIgnore
	public void setAssetAutoTaggingEnabled(
		UnsafeSupplier<Boolean, Exception>
			assetAutoTaggingEnabledUnsafeSupplier) {

		_assetAutoTaggingEnabledSupplier = () -> {
			try {
				return assetAutoTaggingEnabledUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Whether to enable auto tagging of assets on this site."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean assetAutoTaggingEnabled;

	@JsonIgnore
	private Supplier<Boolean> _assetAutoTaggingEnabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether to allow subsites to display content from this site."
	)
	public Boolean getContentSharingWithChildrenEnabled() {
		if (_contentSharingWithChildrenEnabledSupplier != null) {
			contentSharingWithChildrenEnabled =
				_contentSharingWithChildrenEnabledSupplier.get();

			_contentSharingWithChildrenEnabledSupplier = null;
		}

		return contentSharingWithChildrenEnabled;
	}

	public void setContentSharingWithChildrenEnabled(
		Boolean contentSharingWithChildrenEnabled) {

		this.contentSharingWithChildrenEnabled =
			contentSharingWithChildrenEnabled;

		_contentSharingWithChildrenEnabledSupplier = null;
	}

	@JsonIgnore
	public void setContentSharingWithChildrenEnabled(
		UnsafeSupplier<Boolean, Exception>
			contentSharingWithChildrenEnabledUnsafeSupplier) {

		_contentSharingWithChildrenEnabledSupplier = () -> {
			try {
				return contentSharingWithChildrenEnabledUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Whether to allow subsites to display content from this site."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean contentSharingWithChildrenEnabled;

	@JsonIgnore
	private Supplier<Boolean> _contentSharingWithChildrenEnabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "en_US")
	public String getDefaultLanguageId() {
		if (_defaultLanguageIdSupplier != null) {
			defaultLanguageId = _defaultLanguageIdSupplier.get();

			_defaultLanguageIdSupplier = null;
		}

		return defaultLanguageId;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		this.defaultLanguageId = defaultLanguageId;

		_defaultLanguageIdSupplier = null;
	}

	@JsonIgnore
	public void setDefaultLanguageId(
		UnsafeSupplier<String, Exception> defaultLanguageIdUnsafeSupplier) {

		_defaultLanguageIdSupplier = () -> {
			try {
				return defaultLanguageIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String defaultLanguageId;

	@JsonIgnore
	private Supplier<String> _defaultLanguageIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getDescription_i18n() {
		if (_description_i18nSupplier != null) {
			description_i18n = _description_i18nSupplier.get();

			_description_i18nSupplier = null;
		}

		return description_i18n;
	}

	public void setDescription_i18n(Map<String, String> description_i18n) {
		this.description_i18n = description_i18n;

		_description_i18nSupplier = null;
	}

	@JsonIgnore
	public void setDescription_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			description_i18nUnsafeSupplier) {

		_description_i18nSupplier = () -> {
			try {
				return description_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> description_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _description_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDescriptiveName() {
		if (_descriptiveNameSupplier != null) {
			descriptiveName = _descriptiveNameSupplier.get();

			_descriptiveNameSupplier = null;
		}

		return descriptiveName;
	}

	public void setDescriptiveName(String descriptiveName) {
		this.descriptiveName = descriptiveName;

		_descriptiveNameSupplier = null;
	}

	@JsonIgnore
	public void setDescriptiveName(
		UnsafeSupplier<String, Exception> descriptiveNameUnsafeSupplier) {

		_descriptiveNameSupplier = () -> {
			try {
				return descriptiveNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String descriptiveName;

	@JsonIgnore
	private Supplier<String> _descriptiveNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getDescriptiveName_i18n() {
		if (_descriptiveName_i18nSupplier != null) {
			descriptiveName_i18n = _descriptiveName_i18nSupplier.get();

			_descriptiveName_i18nSupplier = null;
		}

		return descriptiveName_i18n;
	}

	public void setDescriptiveName_i18n(
		Map<String, String> descriptiveName_i18n) {

		this.descriptiveName_i18n = descriptiveName_i18n;

		_descriptiveName_i18nSupplier = null;
	}

	@JsonIgnore
	public void setDescriptiveName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			descriptiveName_i18nUnsafeSupplier) {

		_descriptiveName_i18nSupplier = () -> {
			try {
				return descriptiveName_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, String> descriptiveName_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _descriptiveName_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether directory indexing is enabled."
	)
	public Boolean getDirectoryIndexingEnabled() {
		if (_directoryIndexingEnabledSupplier != null) {
			directoryIndexingEnabled = _directoryIndexingEnabledSupplier.get();

			_directoryIndexingEnabledSupplier = null;
		}

		return directoryIndexingEnabled;
	}

	public void setDirectoryIndexingEnabled(Boolean directoryIndexingEnabled) {
		this.directoryIndexingEnabled = directoryIndexingEnabled;

		_directoryIndexingEnabledSupplier = null;
	}

	@JsonIgnore
	public void setDirectoryIndexingEnabled(
		UnsafeSupplier<Boolean, Exception>
			directoryIndexingEnabledUnsafeSupplier) {

		_directoryIndexingEnabledSupplier = () -> {
			try {
				return directoryIndexingEnabledUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Whether directory indexing is enabled.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean directoryIndexingEnabled;

	@JsonIgnore
	private Supplier<Boolean> _directoryIndexingEnabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The site's external reference code."
	)
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The site's external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFriendlyUrlPath() {
		if (_friendlyUrlPathSupplier != null) {
			friendlyUrlPath = _friendlyUrlPathSupplier.get();

			_friendlyUrlPathSupplier = null;
		}

		return friendlyUrlPath;
	}

	public void setFriendlyUrlPath(String friendlyUrlPath) {
		this.friendlyUrlPath = friendlyUrlPath;

		_friendlyUrlPathSupplier = null;
	}

	@JsonIgnore
	public void setFriendlyUrlPath(
		UnsafeSupplier<String, Exception> friendlyUrlPathUnsafeSupplier) {

		_friendlyUrlPathSupplier = () -> {
			try {
				return friendlyUrlPathUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String friendlyUrlPath;

	@JsonIgnore
	private Supplier<String> _friendlyUrlPathSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether to use the default language set by defaultLanguageId."
	)
	public Boolean getInheritLocales() {
		if (_inheritLocalesSupplier != null) {
			inheritLocales = _inheritLocalesSupplier.get();

			_inheritLocalesSupplier = null;
		}

		return inheritLocales;
	}

	public void setInheritLocales(Boolean inheritLocales) {
		this.inheritLocales = inheritLocales;

		_inheritLocalesSupplier = null;
	}

	@JsonIgnore
	public void setInheritLocales(
		UnsafeSupplier<Boolean, Exception> inheritLocalesUnsafeSupplier) {

		_inheritLocalesSupplier = () -> {
			try {
				return inheritLocalesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Whether to use the default language set by defaultLanguageId."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean inheritLocales;

	@JsonIgnore
	private Supplier<Boolean> _inheritLocalesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getKey() {
		if (_keySupplier != null) {
			key = _keySupplier.get();

			_keySupplier = null;
		}

		return key;
	}

	public void setKey(String key) {
		this.key = key;

		_keySupplier = null;
	}

	@JsonIgnore
	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		_keySupplier = () -> {
			try {
				return keyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String key;

	@JsonIgnore
	private Supplier<String> _keySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of languages for which the role has a translation."
	)
	public String[] getLocales() {
		if (_localesSupplier != null) {
			locales = _localesSupplier.get();

			_localesSupplier = null;
		}

		return locales;
	}

	public void setLocales(String[] locales) {
		this.locales = locales;

		_localesSupplier = null;
	}

	@JsonIgnore
	public void setLocales(
		UnsafeSupplier<String[], Exception> localesUnsafeSupplier) {

		_localesSupplier = () -> {
			try {
				return localesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "A list of languages for which the role has a translation."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] locales;

	@JsonIgnore
	private Supplier<String[]> _localesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The site's logo URL."
	)
	public String getLogo() {
		if (_logoSupplier != null) {
			logo = _logoSupplier.get();

			_logoSupplier = null;
		}

		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;

		_logoSupplier = null;
	}

	@JsonIgnore
	public void setLogo(UnsafeSupplier<String, Exception> logoUnsafeSupplier) {
		_logoSupplier = () -> {
			try {
				return logoUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The site's logo URL.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String logo;

	@JsonIgnore
	private Supplier<String> _logoSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getManualMembership() {
		if (_manualMembershipSupplier != null) {
			manualMembership = _manualMembershipSupplier.get();

			_manualMembershipSupplier = null;
		}

		return manualMembership;
	}

	public void setManualMembership(Boolean manualMembership) {
		this.manualMembership = manualMembership;

		_manualMembershipSupplier = null;
	}

	@JsonIgnore
	public void setManualMembership(
		UnsafeSupplier<Boolean, Exception> manualMembershipUnsafeSupplier) {

		_manualMembershipSupplier = () -> {
			try {
				return manualMembershipUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean manualMembership;

	@JsonIgnore
	private Supplier<Boolean> _manualMembershipSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Sets the maps API provider to use when displaying geolocalized assets."
	)
	@JsonGetter("mapProviderKey")
	@Valid
	public MapProviderKey getMapProviderKey() {
		if (_mapProviderKeySupplier != null) {
			mapProviderKey = _mapProviderKeySupplier.get();

			_mapProviderKeySupplier = null;
		}

		return mapProviderKey;
	}

	@JsonIgnore
	public String getMapProviderKeyAsString() {
		MapProviderKey mapProviderKey = getMapProviderKey();

		if (mapProviderKey == null) {
			return null;
		}

		return mapProviderKey.toString();
	}

	public void setMapProviderKey(MapProviderKey mapProviderKey) {
		this.mapProviderKey = mapProviderKey;

		_mapProviderKeySupplier = null;
	}

	@JsonIgnore
	public void setMapProviderKey(
		UnsafeSupplier<MapProviderKey, Exception>
			mapProviderKeyUnsafeSupplier) {

		_mapProviderKeySupplier = () -> {
			try {
				return mapProviderKeyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Sets the maps API provider to use when displaying geolocalized assets."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected MapProviderKey mapProviderKey;

	@JsonIgnore
	private Supplier<MapProviderKey> _mapProviderKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getMembershipRestriction() {
		if (_membershipRestrictionSupplier != null) {
			membershipRestriction = _membershipRestrictionSupplier.get();

			_membershipRestrictionSupplier = null;
		}

		return membershipRestriction;
	}

	public void setMembershipRestriction(Integer membershipRestriction) {
		this.membershipRestriction = membershipRestriction;

		_membershipRestrictionSupplier = null;
	}

	@JsonIgnore
	public void setMembershipRestriction(
		UnsafeSupplier<Integer, Exception>
			membershipRestrictionUnsafeSupplier) {

		_membershipRestrictionSupplier = () -> {
			try {
				return membershipRestrictionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer membershipRestriction;

	@JsonIgnore
	private Supplier<Integer> _membershipRestrictionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The default value is open."
	)
	@JsonGetter("membershipType")
	@Valid
	public MembershipType getMembershipType() {
		if (_membershipTypeSupplier != null) {
			membershipType = _membershipTypeSupplier.get();

			_membershipTypeSupplier = null;
		}

		return membershipType;
	}

	@JsonIgnore
	public String getMembershipTypeAsString() {
		MembershipType membershipType = getMembershipType();

		if (membershipType == null) {
			return null;
		}

		return membershipType.toString();
	}

	public void setMembershipType(MembershipType membershipType) {
		this.membershipType = membershipType;

		_membershipTypeSupplier = null;
	}

	@JsonIgnore
	public void setMembershipType(
		UnsafeSupplier<MembershipType, Exception>
			membershipTypeUnsafeSupplier) {

		_membershipTypeSupplier = () -> {
			try {
				return membershipTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The default value is open.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected MembershipType membershipType;

	@JsonIgnore
	private Supplier<MembershipType> _membershipTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether to allow users to mention other users."
	)
	public Boolean getMentionsEnabled() {
		if (_mentionsEnabledSupplier != null) {
			mentionsEnabled = _mentionsEnabledSupplier.get();

			_mentionsEnabledSupplier = null;
		}

		return mentionsEnabled;
	}

	public void setMentionsEnabled(Boolean mentionsEnabled) {
		this.mentionsEnabled = mentionsEnabled;

		_mentionsEnabledSupplier = null;
	}

	@JsonIgnore
	public void setMentionsEnabled(
		UnsafeSupplier<Boolean, Exception> mentionsEnabledUnsafeSupplier) {

		_mentionsEnabledSupplier = () -> {
			try {
				return mentionsEnabledUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Whether to allow users to mention other users."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean mentionsEnabled;

	@JsonIgnore
	private Supplier<Boolean> _mentionsEnabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getName_i18n() {
		if (_name_i18nSupplier != null) {
			name_i18n = _name_i18nSupplier.get();

			_name_i18nSupplier = null;
		}

		return name_i18n;
	}

	public void setName_i18n(Map<String, String> name_i18n) {
		this.name_i18n = name_i18n;

		_name_i18nSupplier = null;
	}

	@JsonIgnore
	public void setName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			name_i18nUnsafeSupplier) {

		_name_i18nSupplier = () -> {
			try {
				return name_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> name_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _name_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getParentSiteExternalReferenceCode() {
		if (_parentSiteExternalReferenceCodeSupplier != null) {
			parentSiteExternalReferenceCode =
				_parentSiteExternalReferenceCodeSupplier.get();

			_parentSiteExternalReferenceCodeSupplier = null;
		}

		return parentSiteExternalReferenceCode;
	}

	public void setParentSiteExternalReferenceCode(
		String parentSiteExternalReferenceCode) {

		this.parentSiteExternalReferenceCode = parentSiteExternalReferenceCode;

		_parentSiteExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setParentSiteExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentSiteExternalReferenceCodeUnsafeSupplier) {

		_parentSiteExternalReferenceCodeSupplier = () -> {
			try {
				return parentSiteExternalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String parentSiteExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _parentSiteExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public com.liferay.portal.vulcan.permission.Permission[] getPermissions() {
		if (_permissionsSupplier != null) {
			permissions = _permissionsSupplier.get();

			_permissionsSupplier = null;
		}

		return permissions;
	}

	public void setPermissions(
		com.liferay.portal.vulcan.permission.Permission[] permissions) {

		this.permissions = permissions;

		_permissionsSupplier = null;
	}

	@JsonIgnore
	public void setPermissions(
		UnsafeSupplier
			<com.liferay.portal.vulcan.permission.Permission[], Exception>
				permissionsUnsafeSupplier) {

		_permissionsSupplier = () -> {
			try {
				return permissionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected com.liferay.portal.vulcan.permission.Permission[] permissions;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.permission.Permission[]>
		_permissionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public RatingsTypes getRatingsTypes() {
		if (_ratingsTypesSupplier != null) {
			ratingsTypes = _ratingsTypesSupplier.get();

			_ratingsTypesSupplier = null;
		}

		return ratingsTypes;
	}

	public void setRatingsTypes(RatingsTypes ratingsTypes) {
		this.ratingsTypes = ratingsTypes;

		_ratingsTypesSupplier = null;
	}

	@JsonIgnore
	public void setRatingsTypes(
		UnsafeSupplier<RatingsTypes, Exception> ratingsTypesUnsafeSupplier) {

		_ratingsTypesSupplier = () -> {
			try {
				return ratingsTypesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected RatingsTypes ratingsTypes;

	@JsonIgnore
	private Supplier<RatingsTypes> _ratingsTypesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether to allow users to share items with other users."
	)
	public Boolean getSharingEnabled() {
		if (_sharingEnabledSupplier != null) {
			sharingEnabled = _sharingEnabledSupplier.get();

			_sharingEnabledSupplier = null;
		}

		return sharingEnabled;
	}

	public void setSharingEnabled(Boolean sharingEnabled) {
		this.sharingEnabled = sharingEnabled;

		_sharingEnabledSupplier = null;
	}

	@JsonIgnore
	public void setSharingEnabled(
		UnsafeSupplier<Boolean, Exception> sharingEnabledUnsafeSupplier) {

		_sharingEnabledSupplier = () -> {
			try {
				return sharingEnabledUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Whether to allow users to share items with other users."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean sharingEnabled;

	@JsonIgnore
	private Supplier<Boolean> _sharingEnabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTemplateKey() {
		if (_templateKeySupplier != null) {
			templateKey = _templateKeySupplier.get();

			_templateKeySupplier = null;
		}

		return templateKey;
	}

	public void setTemplateKey(String templateKey) {
		this.templateKey = templateKey;

		_templateKeySupplier = null;
	}

	@JsonIgnore
	public void setTemplateKey(
		UnsafeSupplier<String, Exception> templateKeyUnsafeSupplier) {

		_templateKeySupplier = () -> {
			try {
				return templateKeyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String templateKey;

	@JsonIgnore
	private Supplier<String> _templateKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("templateType")
	@Valid
	public TemplateType getTemplateType() {
		if (_templateTypeSupplier != null) {
			templateType = _templateTypeSupplier.get();

			_templateTypeSupplier = null;
		}

		return templateType;
	}

	@JsonIgnore
	public String getTemplateTypeAsString() {
		TemplateType templateType = getTemplateType();

		if (templateType == null) {
			return null;
		}

		return templateType.toString();
	}

	public void setTemplateType(TemplateType templateType) {
		this.templateType = templateType;

		_templateTypeSupplier = null;
	}

	@JsonIgnore
	public void setTemplateType(
		UnsafeSupplier<TemplateType, Exception> templateTypeUnsafeSupplier) {

		_templateTypeSupplier = () -> {
			try {
				return templateTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected TemplateType templateType;

	@JsonIgnore
	private Supplier<TemplateType> _templateTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether to enable the Recycle Bin."
	)
	public Boolean getTrashEnabled() {
		if (_trashEnabledSupplier != null) {
			trashEnabled = _trashEnabledSupplier.get();

			_trashEnabledSupplier = null;
		}

		return trashEnabled;
	}

	public void setTrashEnabled(Boolean trashEnabled) {
		this.trashEnabled = trashEnabled;

		_trashEnabledSupplier = null;
	}

	@JsonIgnore
	public void setTrashEnabled(
		UnsafeSupplier<Boolean, Exception> trashEnabledUnsafeSupplier) {

		_trashEnabledSupplier = () -> {
			try {
				return trashEnabledUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Whether to enable the Recycle Bin.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean trashEnabled;

	@JsonIgnore
	private Supplier<Boolean> _trashEnabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The time, in minutes, that entries are kept in the Recycle Bin. Entries that have been in the Recycle Bin for more than this time are automatically deleted."
	)
	public Integer getTrashEntriesMaxAge() {
		if (_trashEntriesMaxAgeSupplier != null) {
			trashEntriesMaxAge = _trashEntriesMaxAgeSupplier.get();

			_trashEntriesMaxAgeSupplier = null;
		}

		return trashEntriesMaxAge;
	}

	public void setTrashEntriesMaxAge(Integer trashEntriesMaxAge) {
		this.trashEntriesMaxAge = trashEntriesMaxAge;

		_trashEntriesMaxAgeSupplier = null;
	}

	@JsonIgnore
	public void setTrashEntriesMaxAge(
		UnsafeSupplier<Integer, Exception> trashEntriesMaxAgeUnsafeSupplier) {

		_trashEntriesMaxAgeSupplier = () -> {
			try {
				return trashEntriesMaxAgeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The time, in minutes, that entries are kept in the Recycle Bin. Entries that have been in the Recycle Bin for more than this time are automatically deleted."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer trashEntriesMaxAge;

	@JsonIgnore
	private Supplier<Integer> _trashEntriesMaxAgeSupplier;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
		}

		AnalyticsConfiguration analyticsConfiguration =
			getAnalyticsConfiguration();

		if (analyticsConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"analyticsConfiguration\": ");

			sb.append(String.valueOf(analyticsConfiguration));
		}

		Boolean assetAutoTaggingEnabled = getAssetAutoTaggingEnabled();

		if (assetAutoTaggingEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetAutoTaggingEnabled\": ");

			sb.append(assetAutoTaggingEnabled);
		}

		Boolean contentSharingWithChildrenEnabled =
			getContentSharingWithChildrenEnabled();

		if (contentSharingWithChildrenEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentSharingWithChildrenEnabled\": ");

			sb.append(contentSharingWithChildrenEnabled);
		}

		String defaultLanguageId = getDefaultLanguageId();

		if (defaultLanguageId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultLanguageId\": ");

			sb.append("\"");

			sb.append(_escape(defaultLanguageId));

			sb.append("\"");
		}

		String description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(description));

			sb.append("\"");
		}

		Map<String, String> description_i18n = getDescription_i18n();

		if (description_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(description_i18n));
		}

		String descriptiveName = getDescriptiveName();

		if (descriptiveName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptiveName\": ");

			sb.append("\"");

			sb.append(_escape(descriptiveName));

			sb.append("\"");
		}

		Map<String, String> descriptiveName_i18n = getDescriptiveName_i18n();

		if (descriptiveName_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptiveName_i18n\": ");

			sb.append(_toJSON(descriptiveName_i18n));
		}

		Boolean directoryIndexingEnabled = getDirectoryIndexingEnabled();

		if (directoryIndexingEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"directoryIndexingEnabled\": ");

			sb.append(directoryIndexingEnabled);
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		String friendlyUrlPath = getFriendlyUrlPath();

		if (friendlyUrlPath != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath\": ");

			sb.append("\"");

			sb.append(_escape(friendlyUrlPath));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Boolean inheritLocales = getInheritLocales();

		if (inheritLocales != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inheritLocales\": ");

			sb.append(inheritLocales);
		}

		String key = getKey();

		if (key != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(key));

			sb.append("\"");
		}

		String[] locales = getLocales();

		if (locales != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"locales\": ");

			sb.append("[");

			for (int i = 0; i < locales.length; i++) {
				sb.append("\"");

				sb.append(_escape(locales[i]));

				sb.append("\"");

				if ((i + 1) < locales.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String logo = getLogo();

		if (logo != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logo\": ");

			sb.append("\"");

			sb.append(_escape(logo));

			sb.append("\"");
		}

		Boolean manualMembership = getManualMembership();

		if (manualMembership != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"manualMembership\": ");

			sb.append(manualMembership);
		}

		MapProviderKey mapProviderKey = getMapProviderKey();

		if (mapProviderKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mapProviderKey\": ");

			sb.append("\"");
			sb.append(mapProviderKey);
			sb.append("\"");
		}

		Integer membershipRestriction = getMembershipRestriction();

		if (membershipRestriction != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"membershipRestriction\": ");

			sb.append(membershipRestriction);
		}

		MembershipType membershipType = getMembershipType();

		if (membershipType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"membershipType\": ");

			sb.append("\"");
			sb.append(membershipType);
			sb.append("\"");
		}

		Boolean mentionsEnabled = getMentionsEnabled();

		if (mentionsEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mentionsEnabled\": ");

			sb.append(mentionsEnabled);
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		Map<String, String> name_i18n = getName_i18n();

		if (name_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(name_i18n));
		}

		String parentSiteExternalReferenceCode =
			getParentSiteExternalReferenceCode();

		if (parentSiteExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentSiteExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(parentSiteExternalReferenceCode));

			sb.append("\"");
		}

		com.liferay.portal.vulcan.permission.Permission[] permissions =
			getPermissions();

		if (permissions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < permissions.length; i++) {
				sb.append(permissions[i]);

				if ((i + 1) < permissions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		RatingsTypes ratingsTypes = getRatingsTypes();

		if (ratingsTypes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratingsTypes\": ");

			sb.append(String.valueOf(ratingsTypes));
		}

		Boolean sharingEnabled = getSharingEnabled();

		if (sharingEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sharingEnabled\": ");

			sb.append(sharingEnabled);
		}

		String templateKey = getTemplateKey();

		if (templateKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"templateKey\": ");

			sb.append("\"");

			sb.append(_escape(templateKey));

			sb.append("\"");
		}

		TemplateType templateType = getTemplateType();

		if (templateType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"templateType\": ");

			sb.append("\"");
			sb.append(templateType);
			sb.append("\"");
		}

		Boolean trashEnabled = getTrashEnabled();

		if (trashEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trashEnabled\": ");

			sb.append(trashEnabled);
		}

		Integer trashEntriesMaxAge = getTrashEntriesMaxAge();

		if (trashEntriesMaxAge != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trashEntriesMaxAge\": ");

			sb.append(trashEntriesMaxAge);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.Site",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("MapProviderKey")
	public static enum MapProviderKey {

		GOOGLE_MAPS("GoogleMaps"), OPEN_STREET_MAP("OpenStreetMap");

		@JsonCreator
		public static MapProviderKey create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (MapProviderKey mapProviderKey : values()) {
				if (Objects.equals(mapProviderKey.getValue(), value)) {
					return mapProviderKey;
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

		private MapProviderKey(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("MembershipType")
	public static enum MembershipType {

		OPEN("open"), PRIVATE("private"), RESTRICTED("restricted");

		@JsonCreator
		public static MembershipType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (MembershipType membershipType : values()) {
				if (Objects.equals(membershipType.getValue(), value)) {
					return membershipType;
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

		private MembershipType(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("TemplateType")
	public static enum TemplateType {

		SITE_INITIALIZER("site-initializer"), SITE_TEMPLATE("site-template");

		@JsonCreator
		public static TemplateType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (TemplateType templateType : values()) {
				if (Objects.equals(templateType.getValue(), value)) {
					return templateType;
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

		private TemplateType(String value) {
			_value = value;
		}

		private final String _value;

	}

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-1703855843