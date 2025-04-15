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

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a message on a message board. Properties follow the [Discussion Forum Posting](https://schema.org/DiscussionForumPosting) specification.",
	value = "MessageBoardMessage"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "MessageBoardMessage")
public class MessageBoardMessage implements Serializable {

	public static MessageBoardMessage toDTO(String json) {
		return ObjectMapperUtil.readValue(MessageBoardMessage.class, json);
	}

	public static MessageBoardMessage unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			MessageBoardMessage.class, json);
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
		description = "The message's average rating."
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

	@GraphQLField(description = "The message's average rating.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected AggregateRating aggregateRating;

	@JsonIgnore
	private Supplier<AggregateRating> _aggregateRatingSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the message's author is anonymous."
	)
	public Boolean getAnonymous() {
		if (_anonymousSupplier != null) {
			anonymous = _anonymousSupplier.get();

			_anonymousSupplier = null;
		}

		return anonymous;
	}

	public void setAnonymous(Boolean anonymous) {
		this.anonymous = anonymous;

		_anonymousSupplier = null;
	}

	@JsonIgnore
	public void setAnonymous(
		UnsafeSupplier<Boolean, Exception> anonymousUnsafeSupplier) {

		_anonymousSupplier = () -> {
			try {
				return anonymousUnsafeSupplier.get();
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
		description = "A flag that indicates whether the message's author is anonymous."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean anonymous;

	@JsonIgnore
	private Supplier<Boolean> _anonymousSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The message's main content."
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

	@GraphQLField(description = "The message's main content.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String articleBody;

	@JsonIgnore
	private Supplier<String> _articleBodySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The message's author."
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

	@GraphQLField(description = "The message's author.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The message's creator statistics (rank, join date, number of posts, ...)"
	)
	@Valid
	public CreatorStatistics getCreatorStatistics() {
		if (_creatorStatisticsSupplier != null) {
			creatorStatistics = _creatorStatisticsSupplier.get();

			_creatorStatisticsSupplier = null;
		}

		return creatorStatistics;
	}

	public void setCreatorStatistics(CreatorStatistics creatorStatistics) {
		this.creatorStatistics = creatorStatistics;

		_creatorStatisticsSupplier = null;
	}

	@JsonIgnore
	public void setCreatorStatistics(
		UnsafeSupplier<CreatorStatistics, Exception>
			creatorStatisticsUnsafeSupplier) {

		_creatorStatisticsSupplier = () -> {
			try {
				return creatorStatisticsUnsafeSupplier.get();
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
		description = "The message's creator statistics (rank, join date, number of posts, ...)"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected CreatorStatistics creatorStatistics;

	@JsonIgnore
	private Supplier<CreatorStatistics> _creatorStatisticsSupplier;

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
		description = "The date the message was created."
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

	@GraphQLField(description = "The date the message was created.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The last time the content or metadata of the message was changed."
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
		description = "The last time the content or metadata of the message was changed."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The message's media format (e.g., HTML, BBCode, etc.)."
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
		description = "The message's media format (e.g., HTML, BBCode, etc.)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String encodingFormat;

	@JsonIgnore
	private Supplier<String> _encodingFormatSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The message's external reference code."
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

	@GraphQLField(description = "The message's external reference code.")
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
	public Boolean getHasCompanyMx() {
		if (_hasCompanyMxSupplier != null) {
			hasCompanyMx = _hasCompanyMxSupplier.get();

			_hasCompanyMxSupplier = null;
		}

		return hasCompanyMx;
	}

	public void setHasCompanyMx(Boolean hasCompanyMx) {
		this.hasCompanyMx = hasCompanyMx;

		_hasCompanyMxSupplier = null;
	}

	@JsonIgnore
	public void setHasCompanyMx(
		UnsafeSupplier<Boolean, Exception> hasCompanyMxUnsafeSupplier) {

		_hasCompanyMxSupplier = () -> {
			try {
				return hasCompanyMxUnsafeSupplier.get();
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
	protected Boolean hasCompanyMx;

	@JsonIgnore
	private Supplier<Boolean> _hasCompanyMxSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The message's main title."
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

	@GraphQLField(description = "The message's main title.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String headline;

	@JsonIgnore
	private Supplier<String> _headlineSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The message's ID."
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

	@GraphQLField(description = "The message's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of keywords describing the message."
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

	@GraphQLField(description = "A list of keywords describing the message.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] keywords;

	@JsonIgnore
	private Supplier<String[]> _keywordsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the Message Board Section to which this message is scoped."
	)
	public Long getMessageBoardSectionId() {
		if (_messageBoardSectionIdSupplier != null) {
			messageBoardSectionId = _messageBoardSectionIdSupplier.get();

			_messageBoardSectionIdSupplier = null;
		}

		return messageBoardSectionId;
	}

	public void setMessageBoardSectionId(Long messageBoardSectionId) {
		this.messageBoardSectionId = messageBoardSectionId;

		_messageBoardSectionIdSupplier = null;
	}

	@JsonIgnore
	public void setMessageBoardSectionId(
		UnsafeSupplier<Long, Exception> messageBoardSectionIdUnsafeSupplier) {

		_messageBoardSectionIdSupplier = () -> {
			try {
				return messageBoardSectionIdUnsafeSupplier.get();
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
		description = "The ID of the Message Board Section to which this message is scoped."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long messageBoardSectionId;

	@JsonIgnore
	private Supplier<Long> _messageBoardSectionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the Message Board Thread to which this message is scoped."
	)
	public Long getMessageBoardThreadId() {
		if (_messageBoardThreadIdSupplier != null) {
			messageBoardThreadId = _messageBoardThreadIdSupplier.get();

			_messageBoardThreadIdSupplier = null;
		}

		return messageBoardThreadId;
	}

	public void setMessageBoardThreadId(Long messageBoardThreadId) {
		this.messageBoardThreadId = messageBoardThreadId;

		_messageBoardThreadIdSupplier = null;
	}

	@JsonIgnore
	public void setMessageBoardThreadId(
		UnsafeSupplier<Long, Exception> messageBoardThreadIdUnsafeSupplier) {

		_messageBoardThreadIdSupplier = () -> {
			try {
				return messageBoardThreadIdUnsafeSupplier.get();
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
		description = "The ID of the Message Board Thread to which this message is scoped."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long messageBoardThreadId;

	@JsonIgnore
	private Supplier<Long> _messageBoardThreadIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getModified() {
		if (_modifiedSupplier != null) {
			modified = _modifiedSupplier.get();

			_modifiedSupplier = null;
		}

		return modified;
	}

	public void setModified(Boolean modified) {
		this.modified = modified;

		_modifiedSupplier = null;
	}

	@JsonIgnore
	public void setModified(
		UnsafeSupplier<Boolean, Exception> modifiedUnsafeSupplier) {

		_modifiedSupplier = () -> {
			try {
				return modifiedUnsafeSupplier.get();
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
	protected Boolean modified;

	@JsonIgnore
	private Supplier<Boolean> _modifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of the message's attachments."
	)
	public Integer getNumberOfMessageBoardAttachments() {
		if (_numberOfMessageBoardAttachmentsSupplier != null) {
			numberOfMessageBoardAttachments =
				_numberOfMessageBoardAttachmentsSupplier.get();

			_numberOfMessageBoardAttachmentsSupplier = null;
		}

		return numberOfMessageBoardAttachments;
	}

	public void setNumberOfMessageBoardAttachments(
		Integer numberOfMessageBoardAttachments) {

		this.numberOfMessageBoardAttachments = numberOfMessageBoardAttachments;

		_numberOfMessageBoardAttachmentsSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfMessageBoardAttachments(
		UnsafeSupplier<Integer, Exception>
			numberOfMessageBoardAttachmentsUnsafeSupplier) {

		_numberOfMessageBoardAttachmentsSupplier = () -> {
			try {
				return numberOfMessageBoardAttachmentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The number of the message's attachments.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer numberOfMessageBoardAttachments;

	@JsonIgnore
	private Supplier<Integer> _numberOfMessageBoardAttachmentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of the message's child messages."
	)
	public Integer getNumberOfMessageBoardMessages() {
		if (_numberOfMessageBoardMessagesSupplier != null) {
			numberOfMessageBoardMessages =
				_numberOfMessageBoardMessagesSupplier.get();

			_numberOfMessageBoardMessagesSupplier = null;
		}

		return numberOfMessageBoardMessages;
	}

	public void setNumberOfMessageBoardMessages(
		Integer numberOfMessageBoardMessages) {

		this.numberOfMessageBoardMessages = numberOfMessageBoardMessages;

		_numberOfMessageBoardMessagesSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfMessageBoardMessages(
		UnsafeSupplier<Integer, Exception>
			numberOfMessageBoardMessagesUnsafeSupplier) {

		_numberOfMessageBoardMessagesSupplier = () -> {
			try {
				return numberOfMessageBoardMessagesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The number of the message's child messages.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer numberOfMessageBoardMessages;

	@JsonIgnore
	private Supplier<Integer> _numberOfMessageBoardMessagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the message's parent, if it exists."
	)
	public Long getParentMessageBoardMessageId() {
		if (_parentMessageBoardMessageIdSupplier != null) {
			parentMessageBoardMessageId =
				_parentMessageBoardMessageIdSupplier.get();

			_parentMessageBoardMessageIdSupplier = null;
		}

		return parentMessageBoardMessageId;
	}

	public void setParentMessageBoardMessageId(
		Long parentMessageBoardMessageId) {

		this.parentMessageBoardMessageId = parentMessageBoardMessageId;

		_parentMessageBoardMessageIdSupplier = null;
	}

	@JsonIgnore
	public void setParentMessageBoardMessageId(
		UnsafeSupplier<Long, Exception>
			parentMessageBoardMessageIdUnsafeSupplier) {

		_parentMessageBoardMessageIdSupplier = () -> {
			try {
				return parentMessageBoardMessageIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The ID of the message's parent, if it exists.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long parentMessageBoardMessageId;

	@JsonIgnore
	private Supplier<Long> _parentMessageBoardMessageIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of related contents to this message."
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

	@GraphQLField(description = "A list of related contents to this message.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected RelatedContent[] relatedContents;

	@JsonIgnore
	private Supplier<RelatedContent[]> _relatedContentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the message is answering a question."
	)
	public Boolean getShowAsAnswer() {
		if (_showAsAnswerSupplier != null) {
			showAsAnswer = _showAsAnswerSupplier.get();

			_showAsAnswerSupplier = null;
		}

		return showAsAnswer;
	}

	public void setShowAsAnswer(Boolean showAsAnswer) {
		this.showAsAnswer = showAsAnswer;

		_showAsAnswerSupplier = null;
	}

	@JsonIgnore
	public void setShowAsAnswer(
		UnsafeSupplier<Boolean, Exception> showAsAnswerUnsafeSupplier) {

		_showAsAnswerSupplier = () -> {
			try {
				return showAsAnswerUnsafeSupplier.get();
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
		description = "A flag that indicates whether the message is answering a question."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean showAsAnswer;

	@JsonIgnore
	private Supplier<Boolean> _showAsAnswerSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the site to which this message is scoped."
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
		description = "The ID of the site to which this message is scoped."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long siteId;

	@JsonIgnore
	private Supplier<Long> _siteIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The message's status."
	)
	public String getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	public void setStatus(String status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<String, Exception> statusUnsafeSupplier) {

		_statusSupplier = () -> {
			try {
				return statusUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The message's status.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String status;

	@JsonIgnore
	private Supplier<String> _statusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the user making the requests is subscribed to this message."
	)
	public Boolean getSubscribed() {
		if (_subscribedSupplier != null) {
			subscribed = _subscribedSupplier.get();

			_subscribedSupplier = null;
		}

		return subscribed;
	}

	public void setSubscribed(Boolean subscribed) {
		this.subscribed = subscribed;

		_subscribedSupplier = null;
	}

	@JsonIgnore
	public void setSubscribed(
		UnsafeSupplier<Boolean, Exception> subscribedUnsafeSupplier) {

		_subscribedSupplier = () -> {
			try {
				return subscribedUnsafeSupplier.get();
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
		description = "A flag that indicates whether the user making the requests is subscribed to this message."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean subscribed;

	@JsonIgnore
	private Supplier<Boolean> _subscribedSupplier;

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

		if (!(object instanceof MessageBoardMessage)) {
			return false;
		}

		MessageBoardMessage messageBoardMessage = (MessageBoardMessage)object;

		return Objects.equals(toString(), messageBoardMessage.toString());
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

		Boolean anonymous = getAnonymous();

		if (anonymous != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"anonymous\": ");

			sb.append(anonymous);
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

		CreatorStatistics creatorStatistics = getCreatorStatistics();

		if (creatorStatistics != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creatorStatistics\": ");

			sb.append(String.valueOf(creatorStatistics));
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

		Boolean hasCompanyMx = getHasCompanyMx();

		if (hasCompanyMx != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hasCompanyMx\": ");

			sb.append(hasCompanyMx);
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

		Long messageBoardSectionId = getMessageBoardSectionId();

		if (messageBoardSectionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"messageBoardSectionId\": ");

			sb.append(messageBoardSectionId);
		}

		Long messageBoardThreadId = getMessageBoardThreadId();

		if (messageBoardThreadId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"messageBoardThreadId\": ");

			sb.append(messageBoardThreadId);
		}

		Boolean modified = getModified();

		if (modified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modified\": ");

			sb.append(modified);
		}

		Integer numberOfMessageBoardAttachments =
			getNumberOfMessageBoardAttachments();

		if (numberOfMessageBoardAttachments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfMessageBoardAttachments\": ");

			sb.append(numberOfMessageBoardAttachments);
		}

		Integer numberOfMessageBoardMessages =
			getNumberOfMessageBoardMessages();

		if (numberOfMessageBoardMessages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfMessageBoardMessages\": ");

			sb.append(numberOfMessageBoardMessages);
		}

		Long parentMessageBoardMessageId = getParentMessageBoardMessageId();

		if (parentMessageBoardMessageId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentMessageBoardMessageId\": ");

			sb.append(parentMessageBoardMessageId);
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

		Boolean showAsAnswer = getShowAsAnswer();

		if (showAsAnswer != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showAsAnswer\": ");

			sb.append(showAsAnswer);
		}

		Long siteId = getSiteId();

		if (siteId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(siteId);
		}

		String status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(status));

			sb.append("\"");
		}

		Boolean subscribed = getSubscribed();

		if (subscribed != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscribed\": ");

			sb.append(subscribed);
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
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.MessageBoardMessage",
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