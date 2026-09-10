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
@GraphQLName(
	description = "Site applications where the default rating system can be modified to a desired ratings type.",
	value = "RatingsTypes"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Site applications where the default rating system can be modified to a desired ratings type."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "RatingsTypes")
public class RatingsTypes implements Serializable {

	public static RatingsTypes toDTO(String json) {
		return ObjectMapperUtil.readValue(RatingsTypes.class, json);
	}

	public static RatingsTypes unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(RatingsTypes.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of rating system to use"
	)
	@JsonGetter("blogPosting")
	@Valid
	public BlogPosting getBlogPosting() {
		if (_blogPostingSupplier != null) {
			blogPosting = _blogPostingSupplier.get();

			_blogPostingSupplier = null;
		}

		return blogPosting;
	}

	@JsonIgnore
	public String getBlogPostingAsString() {
		BlogPosting blogPosting = getBlogPosting();

		if (blogPosting == null) {
			return null;
		}

		return blogPosting.toString();
	}

	public void setBlogPosting(BlogPosting blogPosting) {
		this.blogPosting = blogPosting;

		_blogPostingSupplier = null;
	}

	@JsonIgnore
	public void setBlogPosting(
		UnsafeSupplier<BlogPosting, Exception> blogPostingUnsafeSupplier) {

		_blogPostingSupplier = () -> {
			try {
				return blogPostingUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The type of rating system to use")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BlogPosting blogPosting;

	@JsonIgnore
	private Supplier<BlogPosting> _blogPostingSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of rating system to use"
	)
	@JsonGetter("bookmarksEntry")
	@Valid
	public BookmarksEntry getBookmarksEntry() {
		if (_bookmarksEntrySupplier != null) {
			bookmarksEntry = _bookmarksEntrySupplier.get();

			_bookmarksEntrySupplier = null;
		}

		return bookmarksEntry;
	}

	@JsonIgnore
	public String getBookmarksEntryAsString() {
		BookmarksEntry bookmarksEntry = getBookmarksEntry();

		if (bookmarksEntry == null) {
			return null;
		}

		return bookmarksEntry.toString();
	}

	public void setBookmarksEntry(BookmarksEntry bookmarksEntry) {
		this.bookmarksEntry = bookmarksEntry;

		_bookmarksEntrySupplier = null;
	}

	@JsonIgnore
	public void setBookmarksEntry(
		UnsafeSupplier<BookmarksEntry, Exception>
			bookmarksEntryUnsafeSupplier) {

		_bookmarksEntrySupplier = () -> {
			try {
				return bookmarksEntryUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The type of rating system to use")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BookmarksEntry bookmarksEntry;

	@JsonIgnore
	private Supplier<BookmarksEntry> _bookmarksEntrySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of rating system to use"
	)
	@JsonGetter("comment")
	@Valid
	public Comment getComment() {
		if (_commentSupplier != null) {
			comment = _commentSupplier.get();

			_commentSupplier = null;
		}

		return comment;
	}

	@JsonIgnore
	public String getCommentAsString() {
		Comment comment = getComment();

		if (comment == null) {
			return null;
		}

		return comment.toString();
	}

	public void setComment(Comment comment) {
		this.comment = comment;

		_commentSupplier = null;
	}

	@JsonIgnore
	public void setComment(
		UnsafeSupplier<Comment, Exception> commentUnsafeSupplier) {

		_commentSupplier = () -> {
			try {
				return commentUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The type of rating system to use")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Comment comment;

	@JsonIgnore
	private Supplier<Comment> _commentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of rating system to use"
	)
	@JsonGetter("document")
	@Valid
	public Document getDocument() {
		if (_documentSupplier != null) {
			document = _documentSupplier.get();

			_documentSupplier = null;
		}

		return document;
	}

	@JsonIgnore
	public String getDocumentAsString() {
		Document document = getDocument();

		if (document == null) {
			return null;
		}

		return document.toString();
	}

	public void setDocument(Document document) {
		this.document = document;

		_documentSupplier = null;
	}

	@JsonIgnore
	public void setDocument(
		UnsafeSupplier<Document, Exception> documentUnsafeSupplier) {

		_documentSupplier = () -> {
			try {
				return documentUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The type of rating system to use")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Document document;

	@JsonIgnore
	private Supplier<Document> _documentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of rating system to use"
	)
	@JsonGetter("knowledgeBaseArticle")
	@Valid
	public KnowledgeBaseArticle getKnowledgeBaseArticle() {
		if (_knowledgeBaseArticleSupplier != null) {
			knowledgeBaseArticle = _knowledgeBaseArticleSupplier.get();

			_knowledgeBaseArticleSupplier = null;
		}

		return knowledgeBaseArticle;
	}

	@JsonIgnore
	public String getKnowledgeBaseArticleAsString() {
		KnowledgeBaseArticle knowledgeBaseArticle = getKnowledgeBaseArticle();

		if (knowledgeBaseArticle == null) {
			return null;
		}

		return knowledgeBaseArticle.toString();
	}

	public void setKnowledgeBaseArticle(
		KnowledgeBaseArticle knowledgeBaseArticle) {

		this.knowledgeBaseArticle = knowledgeBaseArticle;

		_knowledgeBaseArticleSupplier = null;
	}

	@JsonIgnore
	public void setKnowledgeBaseArticle(
		UnsafeSupplier<KnowledgeBaseArticle, Exception>
			knowledgeBaseArticleUnsafeSupplier) {

		_knowledgeBaseArticleSupplier = () -> {
			try {
				return knowledgeBaseArticleUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The type of rating system to use")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected KnowledgeBaseArticle knowledgeBaseArticle;

	@JsonIgnore
	private Supplier<KnowledgeBaseArticle> _knowledgeBaseArticleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of rating system to use"
	)
	@JsonGetter("messageBoardMessage")
	@Valid
	public MessageBoardMessage getMessageBoardMessage() {
		if (_messageBoardMessageSupplier != null) {
			messageBoardMessage = _messageBoardMessageSupplier.get();

			_messageBoardMessageSupplier = null;
		}

		return messageBoardMessage;
	}

	@JsonIgnore
	public String getMessageBoardMessageAsString() {
		MessageBoardMessage messageBoardMessage = getMessageBoardMessage();

		if (messageBoardMessage == null) {
			return null;
		}

		return messageBoardMessage.toString();
	}

	public void setMessageBoardMessage(
		MessageBoardMessage messageBoardMessage) {

		this.messageBoardMessage = messageBoardMessage;

		_messageBoardMessageSupplier = null;
	}

	@JsonIgnore
	public void setMessageBoardMessage(
		UnsafeSupplier<MessageBoardMessage, Exception>
			messageBoardMessageUnsafeSupplier) {

		_messageBoardMessageSupplier = () -> {
			try {
				return messageBoardMessageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The type of rating system to use")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected MessageBoardMessage messageBoardMessage;

	@JsonIgnore
	private Supplier<MessageBoardMessage> _messageBoardMessageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of rating system to use"
	)
	@JsonGetter("sitePage")
	@Valid
	public SitePage getSitePage() {
		if (_sitePageSupplier != null) {
			sitePage = _sitePageSupplier.get();

			_sitePageSupplier = null;
		}

		return sitePage;
	}

	@JsonIgnore
	public String getSitePageAsString() {
		SitePage sitePage = getSitePage();

		if (sitePage == null) {
			return null;
		}

		return sitePage.toString();
	}

	public void setSitePage(SitePage sitePage) {
		this.sitePage = sitePage;

		_sitePageSupplier = null;
	}

	@JsonIgnore
	public void setSitePage(
		UnsafeSupplier<SitePage, Exception> sitePageUnsafeSupplier) {

		_sitePageSupplier = () -> {
			try {
				return sitePageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The type of rating system to use")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected SitePage sitePage;

	@JsonIgnore
	private Supplier<SitePage> _sitePageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of rating system to use"
	)
	@JsonGetter("structuredContent")
	@Valid
	public StructuredContent getStructuredContent() {
		if (_structuredContentSupplier != null) {
			structuredContent = _structuredContentSupplier.get();

			_structuredContentSupplier = null;
		}

		return structuredContent;
	}

	@JsonIgnore
	public String getStructuredContentAsString() {
		StructuredContent structuredContent = getStructuredContent();

		if (structuredContent == null) {
			return null;
		}

		return structuredContent.toString();
	}

	public void setStructuredContent(StructuredContent structuredContent) {
		this.structuredContent = structuredContent;

		_structuredContentSupplier = null;
	}

	@JsonIgnore
	public void setStructuredContent(
		UnsafeSupplier<StructuredContent, Exception>
			structuredContentUnsafeSupplier) {

		_structuredContentSupplier = () -> {
			try {
				return structuredContentUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The type of rating system to use")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected StructuredContent structuredContent;

	@JsonIgnore
	private Supplier<StructuredContent> _structuredContentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of rating system to use"
	)
	@JsonGetter("wikiPage")
	@Valid
	public WikiPage getWikiPage() {
		if (_wikiPageSupplier != null) {
			wikiPage = _wikiPageSupplier.get();

			_wikiPageSupplier = null;
		}

		return wikiPage;
	}

	@JsonIgnore
	public String getWikiPageAsString() {
		WikiPage wikiPage = getWikiPage();

		if (wikiPage == null) {
			return null;
		}

		return wikiPage.toString();
	}

	public void setWikiPage(WikiPage wikiPage) {
		this.wikiPage = wikiPage;

		_wikiPageSupplier = null;
	}

	@JsonIgnore
	public void setWikiPage(
		UnsafeSupplier<WikiPage, Exception> wikiPageUnsafeSupplier) {

		_wikiPageSupplier = () -> {
			try {
				return wikiPageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The type of rating system to use")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected WikiPage wikiPage;

	@JsonIgnore
	private Supplier<WikiPage> _wikiPageSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RatingsTypes)) {
			return false;
		}

		RatingsTypes ratingsTypes = (RatingsTypes)object;

		return Objects.equals(toString(), ratingsTypes.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		BlogPosting blogPosting = getBlogPosting();

		if (blogPosting != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"blogPosting\": ");

			sb.append("\"");
			sb.append(blogPosting);
			sb.append("\"");
		}

		BookmarksEntry bookmarksEntry = getBookmarksEntry();

		if (bookmarksEntry != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bookmarksEntry\": ");

			sb.append("\"");
			sb.append(bookmarksEntry);
			sb.append("\"");
		}

		Comment comment = getComment();

		if (comment != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comment\": ");

			sb.append("\"");
			sb.append(comment);
			sb.append("\"");
		}

		Document document = getDocument();

		if (document != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"document\": ");

			sb.append("\"");
			sb.append(document);
			sb.append("\"");
		}

		KnowledgeBaseArticle knowledgeBaseArticle = getKnowledgeBaseArticle();

		if (knowledgeBaseArticle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"knowledgeBaseArticle\": ");

			sb.append("\"");
			sb.append(knowledgeBaseArticle);
			sb.append("\"");
		}

		MessageBoardMessage messageBoardMessage = getMessageBoardMessage();

		if (messageBoardMessage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"messageBoardMessage\": ");

			sb.append("\"");
			sb.append(messageBoardMessage);
			sb.append("\"");
		}

		SitePage sitePage = getSitePage();

		if (sitePage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sitePage\": ");

			sb.append("\"");
			sb.append(sitePage);
			sb.append("\"");
		}

		StructuredContent structuredContent = getStructuredContent();

		if (structuredContent != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"structuredContent\": ");

			sb.append("\"");
			sb.append(structuredContent);
			sb.append("\"");
		}

		WikiPage wikiPage = getWikiPage();

		if (wikiPage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"wikiPage\": ");

			sb.append("\"");
			sb.append(wikiPage);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.RatingsTypes",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("BlogPosting")
	public static enum BlogPosting {

		LIKE("like"), STACKED_STARS("stacked-stars"), STARS("stars"),
		THUMBS("thumbs");

		@JsonCreator
		public static BlogPosting create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (BlogPosting blogPosting : values()) {
				if (Objects.equals(blogPosting.getValue(), value)) {
					return blogPosting;
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

		private BlogPosting(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("BookmarksEntry")
	public static enum BookmarksEntry {

		LIKE("like"), STACKED_STARS("stacked-stars"), STARS("stars"),
		THUMBS("thumbs");

		@JsonCreator
		public static BookmarksEntry create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (BookmarksEntry bookmarksEntry : values()) {
				if (Objects.equals(bookmarksEntry.getValue(), value)) {
					return bookmarksEntry;
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

		private BookmarksEntry(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("Comment")
	public static enum Comment {

		LIKE("like"), STACKED_STARS("stacked-stars"), STARS("stars"),
		THUMBS("thumbs");

		@JsonCreator
		public static Comment create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Comment comment : values()) {
				if (Objects.equals(comment.getValue(), value)) {
					return comment;
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

		private Comment(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("Document")
	public static enum Document {

		LIKE("like"), STACKED_STARS("stacked-stars"), STARS("stars"),
		THUMBS("thumbs");

		@JsonCreator
		public static Document create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Document document : values()) {
				if (Objects.equals(document.getValue(), value)) {
					return document;
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

		private Document(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("KnowledgeBaseArticle")
	public static enum KnowledgeBaseArticle {

		LIKE("like"), STACKED_STARS("stacked-stars"), STARS("stars"),
		THUMBS("thumbs");

		@JsonCreator
		public static KnowledgeBaseArticle create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (KnowledgeBaseArticle knowledgeBaseArticle : values()) {
				if (Objects.equals(knowledgeBaseArticle.getValue(), value)) {
					return knowledgeBaseArticle;
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

		private KnowledgeBaseArticle(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("MessageBoardMessage")
	public static enum MessageBoardMessage {

		LIKE("like"), STACKED_STARS("stacked-stars"), STARS("stars"),
		THUMBS("thumbs");

		@JsonCreator
		public static MessageBoardMessage create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (MessageBoardMessage messageBoardMessage : values()) {
				if (Objects.equals(messageBoardMessage.getValue(), value)) {
					return messageBoardMessage;
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

		private MessageBoardMessage(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("SitePage")
	public static enum SitePage {

		LIKE("like"), STACKED_STARS("stacked-stars"), STARS("stars"),
		THUMBS("thumbs");

		@JsonCreator
		public static SitePage create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (SitePage sitePage : values()) {
				if (Objects.equals(sitePage.getValue(), value)) {
					return sitePage;
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

		private SitePage(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("StructuredContent")
	public static enum StructuredContent {

		LIKE("like"), STACKED_STARS("stacked-stars"), STARS("stars"),
		THUMBS("thumbs");

		@JsonCreator
		public static StructuredContent create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (StructuredContent structuredContent : values()) {
				if (Objects.equals(structuredContent.getValue(), value)) {
					return structuredContent;
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

		private StructuredContent(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("WikiPage")
	public static enum WikiPage {

		LIKE("like"), STACKED_STARS("stacked-stars"), STARS("stars"),
		THUMBS("thumbs");

		@JsonCreator
		public static WikiPage create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (WikiPage wikiPage : values()) {
				if (Objects.equals(wikiPage.getValue(), value)) {
					return wikiPage;
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

		private WikiPage(String value) {
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
// LIFERAY-REST-BUILDER-HASH:-671741788