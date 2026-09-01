/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A single search hit returned by GET /search and POST /search. The properties below appear on every result; the embedded property carries asset-specific payload and so its shape varies by entryClassName.",
	value = "SearchResult"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A single search hit returned by GET /search and POST /search. The properties below appear on every result; the embedded property carries asset-specific payload and so its shape varies by entryClassName."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SearchResult")
public class SearchResult implements Serializable {

	public static SearchResult toDTO(String json) {
		return ObjectMapperUtil.readValue(SearchResult.class, json);
	}

	public static SearchResult unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SearchResult.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Map of HATEOAS-style action descriptors keyed by action name (such as view, update, delete). Each action contains metadata like the HTTP method, URL, and required permission. Only actions the requesting user is permitted to perform are returned. Read-only - populated by the API, ignored on requests."
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
		description = "Map of HATEOAS-style action descriptors keyed by action name (such as view, update, delete). Each action contains metadata like the HTTP method, URL, and required permission. Only actions the requesting user is permitted to perform are returned. Read-only - populated by the API, ignored on requests."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The time the item was created."
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

	@GraphQLField(description = "The time the item was created.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The last time the item was changed."
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

	@GraphQLField(description = "The last time the item was changed.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The date when the content item is scheduled for editorial review. This field is part of the Liferay content lifecycle and is populated by Web Content Article and other content types that support a review workflow. The value may be null when no review date is set for the item."
	)
	public Date getDateReview() {
		if (_dateReviewSupplier != null) {
			dateReview = _dateReviewSupplier.get();

			_dateReviewSupplier = null;
		}

		return dateReview;
	}

	public void setDateReview(Date dateReview) {
		this.dateReview = dateReview;

		_dateReviewSupplier = null;
	}

	@JsonIgnore
	public void setDateReview(
		UnsafeSupplier<Date, Exception> dateReviewUnsafeSupplier) {

		_dateReviewSupplier = () -> {
			try {
				return dateReviewUnsafeSupplier.get();
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
		description = "The date when the content item is scheduled for editorial review. This field is part of the Liferay content lifecycle and is populated by Web Content Article and other content types that support a review workflow. The value may be null when no review date is set for the item."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date dateReview;

	@JsonIgnore
	private Supplier<Date> _dateReviewSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Free-text description of the item. The value depends on the locale specified by the 'Accept-Language' request header where applicable."
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

	@GraphQLField(
		description = "Free-text description of the item. The value depends on the locale specified by the 'Accept-Language' request header where applicable."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Asset-specific nested data. The shape depends on entryClassName - blog entries include authorName and assetTagNames, documents include extension and size, custom Objects include their declared fields, and so on. The embedded field is populated only when nestedFields=embedded is requested. Nested fields of the embedded entity are resolved only for entries requested with the 'embedded.' prefix together with nestedFieldsDepth=2."
	)
	@Valid
	public Object getEmbedded() {
		if (_embeddedSupplier != null) {
			embedded = _embeddedSupplier.get();

			_embeddedSupplier = null;
		}

		return embedded;
	}

	public void setEmbedded(Object embedded) {
		this.embedded = embedded;

		_embeddedSupplier = null;
	}

	@JsonIgnore
	public void setEmbedded(
		UnsafeSupplier<Object, Exception> embeddedUnsafeSupplier) {

		_embeddedSupplier = () -> {
			try {
				return embeddedUnsafeSupplier.get();
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
		description = "Asset-specific nested data. The shape depends on entryClassName - blog entries include authorName and assetTagNames, documents include extension and size, custom Objects include their declared fields, and so on. The embedded field is populated only when nestedFields=embedded is requested. Nested fields of the embedded entity are resolved only for entries requested with the 'embedded.' prefix together with nestedFieldsDepth=2."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object embedded;

	@JsonIgnore
	private Supplier<Object> _embeddedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Fully qualified class name of the indexed entity (for example, com.liferay.blogs.model.BlogsEntry for a blog entry, com.liferay.journal.model.JournalArticle for a Web Content Article). This value can be used directly as input to the entryClassNames query parameter in subsequent /search requests to narrow searches to that type."
	)
	public String getEntryClassName() {
		if (_entryClassNameSupplier != null) {
			entryClassName = _entryClassNameSupplier.get();

			_entryClassNameSupplier = null;
		}

		return entryClassName;
	}

	public void setEntryClassName(String entryClassName) {
		this.entryClassName = entryClassName;

		_entryClassNameSupplier = null;
	}

	@JsonIgnore
	public void setEntryClassName(
		UnsafeSupplier<String, Exception> entryClassNameUnsafeSupplier) {

		_entryClassNameSupplier = () -> {
			try {
				return entryClassNameUnsafeSupplier.get();
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
		description = "Fully qualified class name of the indexed entity (for example, com.liferay.blogs.model.BlogsEntry for a blog entry, com.liferay.journal.model.JournalArticle for a Web Content Article). This value can be used directly as input to the entryClassNames query parameter in subsequent /search requests to narrow searches to that type."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String entryClassName;

	@JsonIgnore
	private Supplier<String> _entryClassNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The headless REST API URL to fetch the full entity. Can be called directly, or the response is included automatically when nestedFields=embedded is set."
	)
	public String getItemURL() {
		if (_itemURLSupplier != null) {
			itemURL = _itemURLSupplier.get();

			_itemURLSupplier = null;
		}

		return itemURL;
	}

	public void setItemURL(String itemURL) {
		this.itemURL = itemURL;

		_itemURLSupplier = null;
	}

	@JsonIgnore
	public void setItemURL(
		UnsafeSupplier<String, Exception> itemURLUnsafeSupplier) {

		_itemURLSupplier = () -> {
			try {
				return itemURLUnsafeSupplier.get();
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
		description = "The headless REST API URL to fetch the full entity. Can be called directly, or the response is included automatically when nestedFields=embedded is set."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String itemURL;

	@JsonIgnore
	private Supplier<String> _itemURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Relevance score assigned by the search engine. Higher values indicate stronger relevance to the query. Typical values fall in the range 0.0 to ~10.0 depending on query complexity and blueprint configuration. This value is useful for threshold filtering (e.g., dropping results below a chosen score) or custom result ranking logic (combining `score` with other signals)."
	)
	@Valid
	public Float getScore() {
		if (_scoreSupplier != null) {
			score = _scoreSupplier.get();

			_scoreSupplier = null;
		}

		return score;
	}

	public void setScore(Float score) {
		this.score = score;

		_scoreSupplier = null;
	}

	@JsonIgnore
	public void setScore(UnsafeSupplier<Float, Exception> scoreUnsafeSupplier) {
		_scoreSupplier = () -> {
			try {
				return scoreUnsafeSupplier.get();
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
		description = "Relevance score assigned by the search engine. Higher values indicate stronger relevance to the query. Typical values fall in the range 0.0 to ~10.0 depending on query complexity and blueprint configuration. This value is useful for threshold filtering (e.g., dropping results below a chosen score) or custom result ranking logic (combining `score` with other signals)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Float score;

	@JsonIgnore
	private Supplier<Float> _scoreSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Title of the item. The value depends on the locale specified by the 'Accept-Language' request header where applicable."
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

	@GraphQLField(
		description = "Title of the item. The value depends on the locale specified by the 'Accept-Language' request header where applicable."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String title;

	@JsonIgnore
	private Supplier<String> _titleSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SearchResult)) {
			return false;
		}

		SearchResult searchResult = (SearchResult)object;

		return Objects.equals(toString(), searchResult.toString());
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

		Date dateReview = getDateReview();

		if (dateReview != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateReview\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateReview));

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

		Object embedded = getEmbedded();

		if (embedded != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"embedded\": ");

			if (embedded instanceof Collection) {
				sb.append(
					JSONFactoryUtil.createJSONArray((Collection<?>)embedded));
			}
			else if (embedded instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)embedded));
			}
			else if (embedded instanceof Object[]) {
				sb.append(
					JSONFactoryUtil.createJSONArray(
						Arrays.asList((Object[])embedded)));
			}
			else if (embedded instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)embedded));
				sb.append("\"");
			}
			else {
				sb.append(embedded);
			}
		}

		String entryClassName = getEntryClassName();

		if (entryClassName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entryClassName\": ");

			sb.append("\"");

			sb.append(_escape(entryClassName));

			sb.append("\"");
		}

		String itemURL = getItemURL();

		if (itemURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemURL\": ");

			sb.append("\"");

			sb.append(_escape(itemURL));

			sb.append("\"");
		}

		Float score = getScore();

		if (score != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"score\": ");

			sb.append(score);
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

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.search.rest.dto.v1_0.SearchResult",
		name = "x-class-name"
	)
	public String xClassName;

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
// LIFERAY-REST-BUILDER-HASH:1133214251