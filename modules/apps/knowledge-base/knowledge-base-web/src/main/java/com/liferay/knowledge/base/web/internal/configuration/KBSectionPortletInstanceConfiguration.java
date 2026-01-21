/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Roberto Díaz
 */
@ExtendedObjectClassDefinition(
	category = "knowledge-base",
	scope = ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE
)
@Meta.OCD(
	id = "com.liferay.knowledge.base.web.internal.configuration.KBSectionPortletInstanceConfiguration",
	localization = "content/Language",
	name = "knowledge-base-section-portlet-instance-configuration-name"
)
public interface KBSectionPortletInstanceConfiguration {

	@Meta.AD(name = "admin-kb-article-sections", required = false)
	public String[] adminKBArticleSections();

	@Meta.AD(name = "admin-kb-article-sections-default", required = false)
	public String[] adminKBArticleSectionsDefault();

	@Meta.AD(
		deflt = "true", name = "enable-kb-article-asset-links", required = false
	)
	public boolean enableKBArticleAssetLinks();

	@Meta.AD(
		deflt = "false", name = "enable-kb-article-description",
		required = false
	)
	public boolean enableKBArticleDescription();

	@Meta.AD(
		deflt = "true", name = "enable-kb-article-history", required = false
	)
	public boolean enableKBArticleHistory();

	@Meta.AD(deflt = "true", name = "enable-kb-article-print", required = false)
	public boolean enableKBArticlePrint();

	@Meta.AD(
		deflt = "true", name = "enable-kb-article-ratings", required = false
	)
	public boolean enableKBArticleRatings();

	@Meta.AD(
		deflt = "true", name = "enable-kb-article-subscriptions",
		required = false
	)
	public boolean enableKBArticleSubscriptions();

	@Meta.AD(
		deflt = "true", name = "enable-kb-article-view-count-increment",
		required = false
	)
	public boolean enableKBArticleViewCountIncrement();

	@Meta.AD(
		deflt = "title", name = "kb-article-display-style", required = false
	)
	public String kbArticleDisplayStyle();

	@Meta.AD(deflt = "general", name = "kb-articles-sections", required = false)
	public String[] kbArticlesSections();

	@Meta.AD(
		deflt = "true", name = "show-kb-article-asset-entries", required = false
	)
	public boolean showKBArticleAssetEntries();

	@Meta.AD(
		deflt = "true", name = "show-kb-article-attachments", required = false
	)
	public boolean showKBArticleAttachments();

	@Meta.AD(
		deflt = "true", name = "show-kb-articles-pagination", required = false
	)
	public boolean showKBArticlesPagination();

	@Meta.AD(
		deflt = "true", name = "show-kb-articles-sections-title",
		required = false
	)
	public boolean showKBArticlesSectionsTitle();

	@Meta.AD(
		deflt = "menu", name = "social-bookmarks-display-style",
		required = false
	)
	public String socialBookmarksDisplayStyle();

	@Meta.AD(
		deflt = StringPool.STAR, name = "social-bookmarks-types",
		required = false
	)
	public String socialBookmarksTypes();

}