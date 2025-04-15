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
	description = "Represents a blog post. See [BlogPosting](https://www.schema.org/BlogPosting) for more information.",
	value = "BlogPosting"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Represents a blog post. See [BlogPosting](https://www.schema.org/BlogPosting) for more information.",
	requiredProperties = {"articleBody", "headline"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "BlogPosting")
public class BlogPosting implements Serializable {

	public static BlogPosting toDTO(String json) {
		return ObjectMapperUtil.readValue(BlogPosting.class, json);
	}

	public static BlogPosting unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(BlogPosting.class, json);
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
		description = "The blog post's average rating."
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

	@GraphQLField(description = "The blog post's average rating.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected AggregateRating aggregateRating;

	@JsonIgnore
	private Supplier<AggregateRating> _aggregateRatingSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The blog post's subtitle."
	)
	public String getAlternativeHeadline() {
		if (_alternativeHeadlineSupplier != null) {
			alternativeHeadline = _alternativeHeadlineSupplier.get();

			_alternativeHeadlineSupplier = null;
		}

		return alternativeHeadline;
	}

	public void setAlternativeHeadline(String alternativeHeadline) {
		this.alternativeHeadline = alternativeHeadline;

		_alternativeHeadlineSupplier = null;
	}

	@JsonIgnore
	public void setAlternativeHeadline(
		UnsafeSupplier<String, Exception> alternativeHeadlineUnsafeSupplier) {

		_alternativeHeadlineSupplier = () -> {
			try {
				return alternativeHeadlineUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The blog post's subtitle.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String alternativeHeadline;

	@JsonIgnore
	private Supplier<String> _alternativeHeadlineSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The blog post's body (content)."
	)
	public String getArticleBody() {
		if (_articleBodySupplier != null) {
			articleBody = _articleBodySupplier.get();

			_articleBodySupplier = null;
		}

		return articleBody;
	}

	public void setArticleBody(String articleBody) {
		this.articleBody = articleBody;

		_articleBodySupplier = null;
	}

	@JsonIgnore
	public void setArticleBody(
		UnsafeSupplier<String, Exception> articleBodyUnsafeSupplier) {

		_articleBodySupplier = () -> {
			try {
				return articleBodyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The blog post's body (content).")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String articleBody;

	@JsonIgnore
	private Supplier<String> _articleBodySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The blog post's author."
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

	@GraphQLField(description = "The blog post's author.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the custom fields associated with the blog post."
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

	@GraphQLField(
		description = "A list of the custom fields associated with the blog post."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected com.liferay.portal.vulcan.custom.field.CustomField[] customFields;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.custom.field.CustomField[]>
		_customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The blog post's creation date."
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

	@GraphQLField(description = "The blog post's creation date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The blog post's most recent modification date."
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
		description = "The blog post's most recent modification date."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The blog post's publication date."
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

	@GraphQLField(description = "The blog post's publication date.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date datePublished;

	@JsonIgnore
	private Supplier<Date> _datePublishedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The blog post's description."
	)
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

	@GraphQLField(description = "The blog post's description.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The blog post's media format (e.g., HTML, BBCode, etc.)."
	)
	public String getEncodingFormat() {
		if (_encodingFormatSupplier != null) {
			encodingFormat = _encodingFormatSupplier.get();

			_encodingFormatSupplier = null;
		}

		return encodingFormat;
	}

	public void setEncodingFormat(String encodingFormat) {
		this.encodingFormat = encodingFormat;

		_encodingFormatSupplier = null;
	}

	@JsonIgnore
	public void setEncodingFormat(
		UnsafeSupplier<String, Exception> encodingFormatUnsafeSupplier) {

		_encodingFormatSupplier = () -> {
			try {
				return encodingFormatUnsafeSupplier.get();
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
		description = "The blog post's media format (e.g., HTML, BBCode, etc.)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String encodingFormat;

	@JsonIgnore
	private Supplier<String> _encodingFormatSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The blog post's external reference code."
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

	@GraphQLField(description = "The blog post's external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The blog post's relative URL."
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

	@GraphQLField(description = "The blog post's relative URL.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String friendlyUrlPath;

	@JsonIgnore
	private Supplier<String> _friendlyUrlPathSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The blog post's main title."
	)
	public String getHeadline() {
		if (_headlineSupplier != null) {
			headline = _headlineSupplier.get();

			_headlineSupplier = null;
		}

		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;

		_headlineSupplier = null;
	}

	@JsonIgnore
	public void setHeadline(
		UnsafeSupplier<String, Exception> headlineUnsafeSupplier) {

		_headlineSupplier = () -> {
			try {
				return headlineUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The blog post's main title.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String headline;

	@JsonIgnore
	private Supplier<String> _headlineSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The blog post's identifier."
	)
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

	@GraphQLField(description = "The blog post's identifier.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The blog post's cover image."
	)
	@Valid
	public Image getImage() {
		if (_imageSupplier != null) {
			image = _imageSupplier.get();

			_imageSupplier = null;
		}

		return image;
	}

	public void setImage(Image image) {
		this.image = image;

		_imageSupplier = null;
	}

	@JsonIgnore
	public void setImage(UnsafeSupplier<Image, Exception> imageUnsafeSupplier) {
		_imageSupplier = () -> {
			try {
				return imageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The blog post's cover image.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Image image;

	@JsonIgnore
	private Supplier<Image> _imageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of keywords describing the blog post."
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

	@GraphQLField(description = "A list of keywords describing the blog post.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] keywords;

	@JsonIgnore
	private Supplier<String[]> _keywordsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of comments this blog post has received."
	)
	public Integer getNumberOfComments() {
		if (_numberOfCommentsSupplier != null) {
			numberOfComments = _numberOfCommentsSupplier.get();

			_numberOfCommentsSupplier = null;
		}

		return numberOfComments;
	}

	public void setNumberOfComments(Integer numberOfComments) {
		this.numberOfComments = numberOfComments;

		_numberOfCommentsSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfComments(
		UnsafeSupplier<Integer, Exception> numberOfCommentsUnsafeSupplier) {

		_numberOfCommentsSupplier = () -> {
			try {
				return numberOfCommentsUnsafeSupplier.get();
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
		description = "The number of comments this blog post has received."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer numberOfComments;

	@JsonIgnore
	private Supplier<Integer> _numberOfCommentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of related contents to this blog post."
	)
	@Valid
	public RelatedContent[] getRelatedContents() {
		if (_relatedContentsSupplier != null) {
			relatedContents = _relatedContentsSupplier.get();

			_relatedContentsSupplier = null;
		}

		return relatedContents;
	}

	public void setRelatedContents(RelatedContent[] relatedContents) {
		this.relatedContents = relatedContents;

		_relatedContentsSupplier = null;
	}

	@JsonIgnore
	public void setRelatedContents(
		UnsafeSupplier<RelatedContent[], Exception>
			relatedContentsUnsafeSupplier) {

		_relatedContentsSupplier = () -> {
			try {
				return relatedContentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of related contents to this blog post.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected RelatedContent[] relatedContents;

	@JsonIgnore
	private Supplier<RelatedContent[]> _relatedContentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of rendered blogs posts, which results from using a display page to process the blogs post and return HTML."
	)
	@Valid
	public RenderedContent[] getRenderedContents() {
		if (_renderedContentsSupplier != null) {
			renderedContents = _renderedContentsSupplier.get();

			_renderedContentsSupplier = null;
		}

		return renderedContents;
	}

	public void setRenderedContents(RenderedContent[] renderedContents) {
		this.renderedContents = renderedContents;

		_renderedContentsSupplier = null;
	}

	@JsonIgnore
	public void setRenderedContents(
		UnsafeSupplier<RenderedContent[], Exception>
			renderedContentsUnsafeSupplier) {

		_renderedContentsSupplier = () -> {
			try {
				return renderedContentsUnsafeSupplier.get();
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
		description = "A list of rendered blogs posts, which results from using a display page to process the blogs post and return HTML."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected RenderedContent[] renderedContents;

	@JsonIgnore
	private Supplier<RenderedContent[]> _renderedContentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the site to which this blog post is scoped."
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
		description = "The ID of the site to which this blog post is scoped."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long siteId;

	@JsonIgnore
	private Supplier<Long> _siteIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The categories associated with this blog post."
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

	@GraphQLField(
		description = "The categories associated with this blog post."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected TaxonomyCategoryBrief[] taxonomyCategoryBriefs;

	@JsonIgnore
	private Supplier<TaxonomyCategoryBrief[]> _taxonomyCategoryBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A write-only field that adds `TaxonomyCategory` instances to the blog post."
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

	@GraphQLField(
		description = "A write-only field that adds `TaxonomyCategory` instances to the blog post."
	)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected Long[] taxonomyCategoryIds;

	@JsonIgnore
	private Supplier<Long[]> _taxonomyCategoryIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A write-only property that specifies the default permissions."
	)
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

	@GraphQLField(
		description = "A write-only property that specifies the default permissions."
	)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected ViewableBy viewableBy;

	@JsonIgnore
	private Supplier<ViewableBy> _viewableBySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BlogPosting)) {
			return false;
		}

		BlogPosting blogPosting = (BlogPosting)object;

		return Objects.equals(toString(), blogPosting.toString());
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

		String alternativeHeadline = getAlternativeHeadline();

		if (alternativeHeadline != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"alternativeHeadline\": ");

			sb.append("\"");

			sb.append(_escape(alternativeHeadline));

			sb.append("\"");
		}

		String articleBody = getArticleBody();

		if (articleBody != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"articleBody\": ");

			sb.append("\"");

			sb.append(_escape(articleBody));

			sb.append("\"");
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

		String encodingFormat = getEncodingFormat();

		if (encodingFormat != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"encodingFormat\": ");

			sb.append("\"");

			sb.append(_escape(encodingFormat));

			sb.append("\"");
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

		String headline = getHeadline();

		if (headline != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"headline\": ");

			sb.append("\"");

			sb.append(_escape(headline));

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

		Image image = getImage();

		if (image != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"image\": ");

			sb.append(String.valueOf(image));
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

		Integer numberOfComments = getNumberOfComments();

		if (numberOfComments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfComments\": ");

			sb.append(numberOfComments);
		}

		RelatedContent[] relatedContents = getRelatedContents();

		if (relatedContents != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedContents\": ");

			sb.append("[");

			for (int i = 0; i < relatedContents.length; i++) {
				sb.append(String.valueOf(relatedContents[i]));

				if ((i + 1) < relatedContents.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		RenderedContent[] renderedContents = getRenderedContents();

		if (renderedContents != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"renderedContents\": ");

			sb.append("[");

			for (int i = 0; i < renderedContents.length; i++) {
				sb.append(String.valueOf(renderedContents[i]));

				if ((i + 1) < renderedContents.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.BlogPosting",
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