/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a site page. This is modeled internally as a `Layout`.",
	value = "SitePage"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Represents a site page. This is modeled internally as a `Layout`.",
	requiredProperties = {"title"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SitePage")
public class SitePage implements Serializable {

	public static SitePage toDTO(String json) {
		return ObjectMapperUtil.readValue(SitePage.class, json);
	}

	public static SitePage unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SitePage.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Block of actions allowed by the user making the request."
	)
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
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
		description = "Block of actions allowed by the user making the request."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's average rating."
	)
	@Valid
	public AggregateRating getAggregateRating() {
		if (_aggregateRatingSupplier != null) {
			aggregateRating = _aggregateRatingSupplier.get();

			_aggregateRatingSupplier = null;
		}

		return aggregateRating;
	}

	public void setAggregateRating(AggregateRating aggregateRating) {
		this.aggregateRating = aggregateRating;

		_aggregateRatingSupplier = null;
	}

	@JsonIgnore
	public void setAggregateRating(
		UnsafeSupplier<AggregateRating, Exception>
			aggregateRatingUnsafeSupplier) {

		_aggregateRatingSupplier = () -> {
			try {
				return aggregateRatingUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's average rating.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected AggregateRating aggregateRating;

	@JsonIgnore
	private Supplier<AggregateRating> _aggregateRatingSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The list of languages the page has a translation for."
	)
	public String[] getAvailableLanguages() {
		if (_availableLanguagesSupplier != null) {
			availableLanguages = _availableLanguagesSupplier.get();

			_availableLanguagesSupplier = null;
		}

		return availableLanguages;
	}

	public void setAvailableLanguages(String[] availableLanguages) {
		this.availableLanguages = availableLanguages;

		_availableLanguagesSupplier = null;
	}

	@JsonIgnore
	public void setAvailableLanguages(
		UnsafeSupplier<String[], Exception> availableLanguagesUnsafeSupplier) {

		_availableLanguagesSupplier = () -> {
			try {
				return availableLanguagesUnsafeSupplier.get();
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
		description = "The list of languages the page has a translation for."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String[] availableLanguages;

	@JsonIgnore
	private Supplier<String[]> _availableLanguagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's creator."
	)
	@Valid
	public Creator getCreator() {
		if (_creatorSupplier != null) {
			creator = _creatorSupplier.get();

			_creatorSupplier = null;
		}

		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;

		_creatorSupplier = null;
	}

	@JsonIgnore
	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		_creatorSupplier = () -> {
			try {
				return creatorUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's creator.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Custom fields associated with the page."
	)
	@Valid
	public com.liferay.portal.vulcan.custom.field.CustomField[]
		getCustomFields() {

		if (_customFieldsSupplier != null) {
			customFields = _customFieldsSupplier.get();

			_customFieldsSupplier = null;
		}

		return customFields;
	}

	public void setCustomFields(
		com.liferay.portal.vulcan.custom.field.CustomField[] customFields) {

		this.customFields = customFields;

		_customFieldsSupplier = null;
	}

	@JsonIgnore
	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.portal.vulcan.custom.field.CustomField[], Exception>
				customFieldsUnsafeSupplier) {

		_customFieldsSupplier = () -> {
			try {
				return customFieldsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Custom fields associated with the page.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected com.liferay.portal.vulcan.custom.field.CustomField[] customFields;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.custom.field.CustomField[]>
		_customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's creation date."
	)
	public Date getDateCreated() {
		if (_dateCreatedSupplier != null) {
			dateCreated = _dateCreatedSupplier.get();

			_dateCreatedSupplier = null;
		}

		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;

		_dateCreatedSupplier = null;
	}

	@JsonIgnore
	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		_dateCreatedSupplier = () -> {
			try {
				return dateCreatedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's creation date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The last time any field of the page was changed."
	)
	public Date getDateModified() {
		if (_dateModifiedSupplier != null) {
			dateModified = _dateModifiedSupplier.get();

			_dateModifiedSupplier = null;
		}

		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;

		_dateModifiedSupplier = null;
	}

	@JsonIgnore
	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		_dateModifiedSupplier = () -> {
			try {
				return dateModifiedUnsafeSupplier.get();
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
		description = "The last time any field of the page was changed."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's most recent publication date."
	)
	public Date getDatePublished() {
		if (_datePublishedSupplier != null) {
			datePublished = _datePublishedSupplier.get();

			_datePublishedSupplier = null;
		}

		return datePublished;
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;

		_datePublishedSupplier = null;
	}

	@JsonIgnore
	public void setDatePublished(
		UnsafeSupplier<Date, Exception> datePublishedUnsafeSupplier) {

		_datePublishedSupplier = () -> {
			try {
				return datePublishedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's most recent publication date.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date datePublished;

	@JsonIgnore
	private Supplier<Date> _datePublishedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Experience of the page that it's being retrieved."
	)
	@Valid
	public Experience getExperience() {
		if (_experienceSupplier != null) {
			experience = _experienceSupplier.get();

			_experienceSupplier = null;
		}

		return experience;
	}

	public void setExperience(Experience experience) {
		this.experience = experience;

		_experienceSupplier = null;
	}

	@JsonIgnore
	public void setExperience(
		UnsafeSupplier<Experience, Exception> experienceUnsafeSupplier) {

		_experienceSupplier = () -> {
			try {
				return experienceUnsafeSupplier.get();
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
		description = "Experience of the page that it's being retrieved."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Experience experience;

	@JsonIgnore
	private Supplier<Experience> _experienceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A relative URL to the page's rendered content."
	)
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

	@GraphQLField(
		description = "A relative URL to the page's rendered content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String friendlyUrlPath;

	@JsonIgnore
	private Supplier<String> _friendlyUrlPathSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized relative URLs to the page's rendered content."
	)
	@Valid
	public Map<String, String> getFriendlyUrlPath_i18n() {
		if (_friendlyUrlPath_i18nSupplier != null) {
			friendlyUrlPath_i18n = _friendlyUrlPath_i18nSupplier.get();

			_friendlyUrlPath_i18nSupplier = null;
		}

		return friendlyUrlPath_i18n;
	}

	public void setFriendlyUrlPath_i18n(
		Map<String, String> friendlyUrlPath_i18n) {

		this.friendlyUrlPath_i18n = friendlyUrlPath_i18n;

		_friendlyUrlPath_i18nSupplier = null;
	}

	@JsonIgnore
	public void setFriendlyUrlPath_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			friendlyUrlPath_i18nUnsafeSupplier) {

		_friendlyUrlPath_i18nSupplier = () -> {
			try {
				return friendlyUrlPath_i18nUnsafeSupplier.get();
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
		description = "The localized relative URLs to the page's rendered content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> friendlyUrlPath_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _friendlyUrlPath_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(description = "The page ID.")
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

	@GraphQLField(description = "The page ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of keywords describing the page."
	)
	public String[] getKeywords() {
		if (_keywordsSupplier != null) {
			keywords = _keywordsSupplier.get();

			_keywordsSupplier = null;
		}

		return keywords;
	}

	public void setKeywords(String[] keywords) {
		this.keywords = keywords;

		_keywordsSupplier = null;
	}

	@JsonIgnore
	public void setKeywords(
		UnsafeSupplier<String[], Exception> keywordsUnsafeSupplier) {

		_keywordsSupplier = () -> {
			try {
				return keywordsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of keywords describing the page.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] keywords;

	@JsonIgnore
	private Supplier<String[]> _keywordsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Optional field with the structure of all the elements of the page. Can be embedded with nestedFields when retrieving the collection of site pages. When retrieving a single site page, it will automatically be included."
	)
	@Valid
	public PageDefinition getPageDefinition() {
		if (_pageDefinitionSupplier != null) {
			pageDefinition = _pageDefinitionSupplier.get();

			_pageDefinitionSupplier = null;
		}

		return pageDefinition;
	}

	public void setPageDefinition(PageDefinition pageDefinition) {
		this.pageDefinition = pageDefinition;

		_pageDefinitionSupplier = null;
	}

	@JsonIgnore
	public void setPageDefinition(
		UnsafeSupplier<PageDefinition, Exception>
			pageDefinitionUnsafeSupplier) {

		_pageDefinitionSupplier = () -> {
			try {
				return pageDefinitionUnsafeSupplier.get();
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
		description = "Optional field with the structure of all the elements of the page. Can be embedded with nestedFields when retrieving the collection of site pages. When retrieving a single site page, it will automatically be included."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PageDefinition pageDefinition;

	@JsonIgnore
	private Supplier<PageDefinition> _pageDefinitionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's permissions."
	)
	@Valid
	public PagePermission[] getPagePermissions() {
		if (_pagePermissionsSupplier != null) {
			pagePermissions = _pagePermissionsSupplier.get();

			_pagePermissionsSupplier = null;
		}

		return pagePermissions;
	}

	public void setPagePermissions(PagePermission[] pagePermissions) {
		this.pagePermissions = pagePermissions;

		_pagePermissionsSupplier = null;
	}

	@JsonIgnore
	public void setPagePermissions(
		UnsafeSupplier<PagePermission[], Exception>
			pagePermissionsUnsafeSupplier) {

		_pagePermissionsSupplier = () -> {
			try {
				return pagePermissionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's permissions.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PagePermission[] pagePermissions;

	@JsonIgnore
	private Supplier<PagePermission[]> _pagePermissionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Settings of the page, such as SEO or OpenGraph."
	)
	@Valid
	public PageSettings getPageSettings() {
		if (_pageSettingsSupplier != null) {
			pageSettings = _pageSettingsSupplier.get();

			_pageSettingsSupplier = null;
		}

		return pageSettings;
	}

	public void setPageSettings(PageSettings pageSettings) {
		this.pageSettings = pageSettings;

		_pageSettingsSupplier = null;
	}

	@JsonIgnore
	public void setPageSettings(
		UnsafeSupplier<PageSettings, Exception> pageSettingsUnsafeSupplier) {

		_pageSettingsSupplier = () -> {
			try {
				return pageSettingsUnsafeSupplier.get();
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
		description = "Settings of the page, such as SEO or OpenGraph."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PageSettings pageSettings;

	@JsonIgnore
	private Supplier<PageSettings> _pageSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of the page."
	)
	public String getPageType() {
		if (_pageTypeSupplier != null) {
			pageType = _pageTypeSupplier.get();

			_pageTypeSupplier = null;
		}

		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;

		_pageTypeSupplier = null;
	}

	@JsonIgnore
	public void setPageType(
		UnsafeSupplier<String, Exception> pageTypeUnsafeSupplier) {

		_pageTypeSupplier = () -> {
			try {
				return pageTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The type of the page.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String pageType;

	@JsonIgnore
	private Supplier<String> _pageTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The parent page or null if it is a top level page."
	)
	@Valid
	public ParentSitePage getParentSitePage() {
		if (_parentSitePageSupplier != null) {
			parentSitePage = _parentSitePageSupplier.get();

			_parentSitePageSupplier = null;
		}

		return parentSitePage;
	}

	public void setParentSitePage(ParentSitePage parentSitePage) {
		this.parentSitePage = parentSitePage;

		_parentSitePageSupplier = null;
	}

	@JsonIgnore
	public void setParentSitePage(
		UnsafeSupplier<ParentSitePage, Exception>
			parentSitePageUnsafeSupplier) {

		_parentSitePageSupplier = () -> {
			try {
				return parentSitePageUnsafeSupplier.get();
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
		description = "The parent page or null if it is a top level page."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ParentSitePage parentSitePage;

	@JsonIgnore
	private Supplier<ParentSitePage> _parentSitePageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Metadata of the page such as it's master page and template."
	)
	@Valid
	public RenderedPage getRenderedPage() {
		if (_renderedPageSupplier != null) {
			renderedPage = _renderedPageSupplier.get();

			_renderedPageSupplier = null;
		}

		return renderedPage;
	}

	public void setRenderedPage(RenderedPage renderedPage) {
		this.renderedPage = renderedPage;

		_renderedPageSupplier = null;
	}

	@JsonIgnore
	public void setRenderedPage(
		UnsafeSupplier<RenderedPage, Exception> renderedPageUnsafeSupplier) {

		_renderedPageSupplier = () -> {
			try {
				return renderedPageUnsafeSupplier.get();
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
		description = "Metadata of the page such as it's master page and template."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected RenderedPage renderedPage;

	@JsonIgnore
	private Supplier<RenderedPage> _renderedPageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the site to which this page is scoped."
	)
	public Long getSiteId() {
		if (_siteIdSupplier != null) {
			siteId = _siteIdSupplier.get();

			_siteIdSupplier = null;
		}

		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;

		_siteIdSupplier = null;
	}

	@JsonIgnore
	public void setSiteId(
		UnsafeSupplier<Long, Exception> siteIdUnsafeSupplier) {

		_siteIdSupplier = () -> {
			try {
				return siteIdUnsafeSupplier.get();
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
		description = "The ID of the site to which this page is scoped."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long siteId;

	@JsonIgnore
	private Supplier<Long> _siteIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The categories associated with this page."
	)
	@Valid
	public TaxonomyCategoryBrief[] getTaxonomyCategoryBriefs() {
		if (_taxonomyCategoryBriefsSupplier != null) {
			taxonomyCategoryBriefs = _taxonomyCategoryBriefsSupplier.get();

			_taxonomyCategoryBriefsSupplier = null;
		}

		return taxonomyCategoryBriefs;
	}

	public void setTaxonomyCategoryBriefs(
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs) {

		this.taxonomyCategoryBriefs = taxonomyCategoryBriefs;

		_taxonomyCategoryBriefsSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryBriefs(
		UnsafeSupplier<TaxonomyCategoryBrief[], Exception>
			taxonomyCategoryBriefsUnsafeSupplier) {

		_taxonomyCategoryBriefsSupplier = () -> {
			try {
				return taxonomyCategoryBriefsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The categories associated with this page.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected TaxonomyCategoryBrief[] taxonomyCategoryBriefs;

	@JsonIgnore
	private Supplier<TaxonomyCategoryBrief[]> _taxonomyCategoryBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		deprecated = true,
		description = "A write-only field that adds `TaxonomyCategory` instances to the page. Deprecated as of Cavanaugh (7.4.x), replaced by `taxonomyCategoryBriefs.taxonomyCategoryReference`"
	)
	public Long[] getTaxonomyCategoryIds() {
		if (_taxonomyCategoryIdsSupplier != null) {
			taxonomyCategoryIds = _taxonomyCategoryIdsSupplier.get();

			_taxonomyCategoryIdsSupplier = null;
		}

		return taxonomyCategoryIds;
	}

	public void setTaxonomyCategoryIds(Long[] taxonomyCategoryIds) {
		this.taxonomyCategoryIds = taxonomyCategoryIds;

		_taxonomyCategoryIdsSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryIds(
		UnsafeSupplier<Long[], Exception> taxonomyCategoryIdsUnsafeSupplier) {

		_taxonomyCategoryIdsSupplier = () -> {
			try {
				return taxonomyCategoryIdsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField(
		description = "A write-only field that adds `TaxonomyCategory` instances to the page. Deprecated as of Cavanaugh (7.4.x), replaced by `taxonomyCategoryBriefs.taxonomyCategoryReference`"
	)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected Long[] taxonomyCategoryIds;

	@JsonIgnore
	private Supplier<Long[]> _taxonomyCategoryIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's title."
	)
	public String getTitle() {
		if (_titleSupplier != null) {
			title = _titleSupplier.get();

			_titleSupplier = null;
		}

		return title;
	}

	public void setTitle(String title) {
		this.title = title;

		_titleSupplier = null;
	}

	@JsonIgnore
	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		_titleSupplier = () -> {
			try {
				return titleUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's title.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String title;

	@JsonIgnore
	private Supplier<String> _titleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized page's titles."
	)
	@Valid
	public Map<String, String> getTitle_i18n() {
		if (_title_i18nSupplier != null) {
			title_i18n = _title_i18nSupplier.get();

			_title_i18nSupplier = null;
		}

		return title_i18n;
	}

	public void setTitle_i18n(Map<String, String> title_i18n) {
		this.title_i18n = title_i18n;

		_title_i18nSupplier = null;
	}

	@JsonIgnore
	public void setTitle_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			title_i18nUnsafeSupplier) {

		_title_i18nSupplier = () -> {
			try {
				return title_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized page's titles.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> title_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _title_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A valid external identifier to reference this page."
	)
	public String getUuid() {
		if (_uuidSupplier != null) {
			uuid = _uuidSupplier.get();

			_uuidSupplier = null;
		}

		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;

		_uuidSupplier = null;
	}

	@JsonIgnore
	public void setUuid(UnsafeSupplier<String, Exception> uuidUnsafeSupplier) {
		_uuidSupplier = () -> {
			try {
				return uuidUnsafeSupplier.get();
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
		description = "A valid external identifier to reference this page."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String uuid;

	@JsonIgnore
	private Supplier<String> _uuidSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("viewableBy")
	@Valid
	public ViewableBy getViewableBy() {
		if (_viewableBySupplier != null) {
			viewableBy = _viewableBySupplier.get();

			_viewableBySupplier = null;
		}

		return viewableBy;
	}

	@JsonIgnore
	public String getViewableByAsString() {
		ViewableBy viewableBy = getViewableBy();

		if (viewableBy == null) {
			return null;
		}

		return viewableBy.toString();
	}

	public void setViewableBy(ViewableBy viewableBy) {
		this.viewableBy = viewableBy;

		_viewableBySupplier = null;
	}

	@JsonIgnore
	public void setViewableBy(
		UnsafeSupplier<ViewableBy, Exception> viewableByUnsafeSupplier) {

		_viewableBySupplier = () -> {
			try {
				return viewableByUnsafeSupplier.get();
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
	protected ViewableBy viewableBy;

	@JsonIgnore
	private Supplier<ViewableBy> _viewableBySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SitePage)) {
			return false;
		}

		SitePage sitePage = (SitePage)object;

		return Objects.equals(toString(), sitePage.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		AggregateRating aggregateRating = getAggregateRating();

		if (aggregateRating != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"aggregateRating\": ");

			sb.append(String.valueOf(aggregateRating));
		}

		String[] availableLanguages = getAvailableLanguages();

		if (availableLanguages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguages\": ");

			sb.append("[");

			for (int i = 0; i < availableLanguages.length; i++) {
				sb.append("\"");

				sb.append(_escape(availableLanguages[i]));

				sb.append("\"");

				if ((i + 1) < availableLanguages.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(creator));
		}

		com.liferay.portal.vulcan.custom.field.CustomField[] customFields =
			getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < customFields.length; i++) {
				sb.append(customFields[i]);

				if ((i + 1) < customFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Date dateCreated = getDateCreated();

		if (dateCreated != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateCreated));

			sb.append("\"");
		}

		Date dateModified = getDateModified();

		if (dateModified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateModified));

			sb.append("\"");
		}

		Date datePublished = getDatePublished();

		if (datePublished != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePublished\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(datePublished));

			sb.append("\"");
		}

		Experience experience = getExperience();

		if (experience != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"experience\": ");

			sb.append(String.valueOf(experience));
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

		Map<String, String> friendlyUrlPath_i18n = getFriendlyUrlPath_i18n();

		if (friendlyUrlPath_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath_i18n\": ");

			sb.append(_toJSON(friendlyUrlPath_i18n));
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String[] keywords = getKeywords();

		if (keywords != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < keywords.length; i++) {
				sb.append("\"");

				sb.append(_escape(keywords[i]));

				sb.append("\"");

				if ((i + 1) < keywords.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PageDefinition pageDefinition = getPageDefinition();

		if (pageDefinition != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageDefinition\": ");

			sb.append(String.valueOf(pageDefinition));
		}

		PagePermission[] pagePermissions = getPagePermissions();

		if (pagePermissions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pagePermissions\": ");

			sb.append("[");

			for (int i = 0; i < pagePermissions.length; i++) {
				sb.append(String.valueOf(pagePermissions[i]));

				if ((i + 1) < pagePermissions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PageSettings pageSettings = getPageSettings();

		if (pageSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSettings\": ");

			sb.append(String.valueOf(pageSettings));
		}

		String pageType = getPageType();

		if (pageType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageType\": ");

			sb.append("\"");

			sb.append(_escape(pageType));

			sb.append("\"");
		}

		ParentSitePage parentSitePage = getParentSitePage();

		if (parentSitePage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentSitePage\": ");

			sb.append(String.valueOf(parentSitePage));
		}

		RenderedPage renderedPage = getRenderedPage();

		if (renderedPage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"renderedPage\": ");

			sb.append(String.valueOf(renderedPage));
		}

		Long siteId = getSiteId();

		if (siteId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(siteId);
		}

		TaxonomyCategoryBrief[] taxonomyCategoryBriefs =
			getTaxonomyCategoryBriefs();

		if (taxonomyCategoryBriefs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryBriefs\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyCategoryBriefs.length; i++) {
				sb.append(String.valueOf(taxonomyCategoryBriefs[i]));

				if ((i + 1) < taxonomyCategoryBriefs.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long[] taxonomyCategoryIds = getTaxonomyCategoryIds();

		if (taxonomyCategoryIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryIds\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyCategoryIds.length; i++) {
				sb.append(taxonomyCategoryIds[i]);

				if ((i + 1) < taxonomyCategoryIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String title = getTitle();

		if (title != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(title));

			sb.append("\"");
		}

		Map<String, String> title_i18n = getTitle_i18n();

		if (title_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title_i18n\": ");

			sb.append(_toJSON(title_i18n));
		}

		String uuid = getUuid();

		if (uuid != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(uuid));

			sb.append("\"");
		}

		ViewableBy viewableBy = getViewableBy();

		if (viewableBy != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewableBy\": ");

			sb.append("\"");

			sb.append(viewableBy);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.SitePage",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("ViewableBy")
	public static enum ViewableBy {

		ANYONE("Anyone"), MEMBERS("Members"), OWNER("Owner");

		@JsonCreator
		public static ViewableBy create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (ViewableBy viewableBy : values()) {
				if (Objects.equals(viewableBy.getValue(), value)) {
					return viewableBy;
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

		private ViewableBy(String value) {
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