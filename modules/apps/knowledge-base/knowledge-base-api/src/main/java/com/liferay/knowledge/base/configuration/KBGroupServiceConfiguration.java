/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Roberto Díaz
 */
@ExtendedObjectClassDefinition(
	category = "knowledge-base",
	scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.knowledge.base.configuration.KBGroupServiceConfiguration",
	localization = "content/Language",
	name = "knowledge-base-group-service-configuration-name"
)
public interface KBGroupServiceConfiguration {

	@Meta.AD(
		deflt = "true", name = "article-increment-priority-enabled",
		required = false
	)
	public boolean articleIncrementPriorityEnabled();

	@Meta.AD(
		deflt = "${server-property://com.liferay.portal/admin.email.from.address}",
		name = "email-from-address", required = false
	)
	public String emailFromAddress();

	@Meta.AD(
		deflt = "${server-property://com.liferay.portal/admin.email.from.name}",
		name = "email-from-name", required = false
	)
	public String emailFromName();

	@Meta.AD(
		deflt = "${resource:com/liferay/knowledge/base/dependencies/email_kb_article_added_body.tmpl}",
		name = "email-kb-article-added-body", required = false
	)
	public String emailKBArticleAddedBody();

	@Meta.AD(
		deflt = "true", name = "email-kb-article-added-enabled",
		required = false
	)
	public boolean emailKBArticleAddedEnabled();

	@Meta.AD(
		deflt = "${resource:com/liferay/knowledge/base/dependencies/email_kb_article_added_subject.tmpl}",
		name = "email-kb-article-added-subject", required = false
	)
	public String emailKBArticleAddedSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/knowledge/base/dependencies/email_kb_article_expired_body.tmpl}",
		name = "email-kb-article-expired-body", required = false
	)
	public String emailKBArticleExpiredBody();

	@Meta.AD(
		deflt = "true", name = "email-kb-article-expired-enabled",
		required = false
	)
	public boolean emailKBArticleExpiredEnabled();

	@Meta.AD(
		deflt = "${resource:com/liferay/knowledge/base/dependencies/email_kb_article_expired_subject.tmpl}",
		name = "email-kb-article-expired-subject", required = false
	)
	public String emailKBArticleExpiredSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/knowledge/base/dependencies/email_kb_article_review_body.tmpl}",
		name = "email-kb-article-review-body", required = false
	)
	public String emailKBArticleReviewBody();

	@Meta.AD(
		deflt = "true", name = "email-kb-article-review-enabled",
		required = false
	)
	public boolean emailKBArticleReviewEnabled();

	@Meta.AD(
		deflt = "${resource:com/liferay/knowledge/base/dependencies/email_kb_article_review_subject.tmpl}",
		name = "email-kb-article-review-subject", required = false
	)
	public String emailKBArticleReviewSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/knowledge/base/dependencies/email_kb_suggestion_in_progress_body.tmpl}",
		name = "email-kb-article-suggestion-in-progress-body", required = false
	)
	public String emailKBArticleSuggestionInProgressBody();

	@Meta.AD(
		deflt = "true",
		name = "email-kb-article-suggestion-in-progress-enabled",
		required = false
	)
	public boolean emailKBArticleSuggestionInProgressEnabled();

	@Meta.AD(
		deflt = "${resource:com/liferay/knowledge/base/dependencies/email_kb_suggestion_in_progress_subject.tmpl}",
		name = "email-kb-article-suggestion-in-progress-subject",
		required = false
	)
	public String emailKBArticleSuggestionInProgressSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/knowledge/base/dependencies/email_kb_suggestion_received_body.tmpl}",
		name = "email-kb-article-suggestion-received-body", required = false
	)
	public String emailKBArticleSuggestionReceivedBody();

	@Meta.AD(
		deflt = "true", name = "email-kb-article-suggestion-received-enabled",
		required = false
	)
	public boolean emailKBArticleSuggestionReceivedEnabled();

	@Meta.AD(
		deflt = "${resource:com/liferay/knowledge/base/dependencies/email_kb_suggestion_received_subject.tmpl}",
		name = "email-kb-article-suggestion-received-subject", required = false
	)
	public String emailKBArticleSuggestionReceivedSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/knowledge/base/dependencies/email_kb_suggestion_resolved_body.tmpl}",
		name = "email-kb-article-suggestion-resolved-body", required = false
	)
	public String emailKBArticleSuggestionResolvedBody();

	@Meta.AD(
		deflt = "true", name = "email-kb-article-suggestion-resolved-enabled",
		required = false
	)
	public boolean emailKBArticleSuggestionResolvedEnabled();

	@Meta.AD(
		deflt = "${resource:com/liferay/knowledge/base/dependencies/email_kb_suggestion_resolved_subject.tmpl}",
		name = "email-kb-article-suggestion-resolved-subject", required = false
	)
	public String emailKBArticleSuggestionResolvedSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/knowledge/base/dependencies/email_kb_article_updated_body.tmpl}",
		name = "email-kb-article-updated-body", required = false
	)
	public String emailKBArticleUpdatedBody();

	@Meta.AD(
		deflt = "true", name = "email-kb-article-updated-enabled",
		required = false
	)
	public boolean emailKBArticleUpdatedEnabled();

	@Meta.AD(
		deflt = "${resource:com/liferay/knowledge/base/dependencies/email_kb_article_updated_subject.tmpl}",
		name = "email-kb-article-updated-subject", required = false
	)
	public String emailKBArticleUpdatedSubject();

	@Meta.AD(deflt = "true", name = "enable-rss", required = false)
	public boolean enableRss();

	@Meta.AD(deflt = "ckeditor", name = "get-editor-name", required = false)
	public String getEditorName();

	@Meta.AD(
		deflt = ".markdown|.md", name = "markdown-importer-article-extensions",
		required = false
	)
	public String[] markdownImporterArticleExtensions();

	@Meta.AD(
		deflt = "intro.markdown", name = "markdown-importer-article-intro",
		required = false
	)
	public String markdownImporterArticleIntro();

	@Meta.AD(
		deflt = ".bmp|.gif|.jpeg|.jpg|.png",
		name = "markdown-importer-image-file-extensions", required = false
	)
	public String[] markdownImporterImageFileExtensions();

	@Meta.AD(
		deflt = "/images", name = "markdown-importer-image-folder",
		required = false
	)
	public String markdownImporterImageFolder();

	@Meta.AD(
		deflt = "${server-property://com.liferay.portal/search.container.page.default.delta}",
		name = "rss-delta", required = false
	)
	public String rssDelta();

	@Meta.AD(
		deflt = "${server-property://com.liferay.portal/rss.feed.display.style.default}",
		name = "rss-display-style", required = false
	)
	public String rssDisplayStyle();

	@Meta.AD(
		deflt = "${server-property://com.liferay.portal/rss.feed.type.default}",
		name = "rss-feed-type", required = false
	)
	public String rssFeedType();

	@Meta.AD(
		deflt = "edit-on-github", name = "source-url-edit-message-key",
		required = false
	)
	public String sourceURLEditMessageKey();

	@Meta.AD(deflt = "false", name = "source-url-enabled", required = false)
	public boolean sourceURLEnabled();

}