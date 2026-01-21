/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Jürgen Kappler
 */
@ExtendedObjectClassDefinition(category = "web-content")
@Meta.OCD(
	id = "com.liferay.journal.web.internal.configuration.JournalWebConfiguration",
	localization = "content/Language", name = "journal-web-configuration-name"
)
public interface JournalWebConfiguration {

	@Meta.AD(
		deflt = "false",
		description = "if-checked,-the-default-language-of-web-content-articles-will-be-changeable",
		name = "changeable-default-language", required = false
	)
	public boolean changeableDefaultLanguage();

	@Meta.AD(
		deflt = "true", name = "journal-article-force-autogenerate-id",
		required = false
	)
	public boolean journalArticleForceAutogenerateId();

	@Meta.AD(
		deflt = "false", name = "journal-article-show-id", required = false
	)
	public boolean journalArticleShowId();

	@Meta.AD(
		deflt = "true",
		description = "journal-browse-by-structures-sorted-by-name-help",
		name = "journal-browse-by-structures-sorted-by-name", required = false
	)
	public boolean journalBrowseByStructuresSortedByName();

	@Meta.AD(
		deflt = "true", name = "journal-feed-force-autogenerate-id",
		required = false
	)
	public boolean journalFeedForceAutogenerateId();

	@Meta.AD(deflt = "7", name = "max-add-menu-items", required = false)
	public int maxAddMenuItems();

	@Meta.AD(
		deflt = "false", name = "show-ancestor-scopes-by-default",
		required = false
	)
	public boolean showAncestorScopesByDefault();

	@Meta.AD(deflt = "false", name = "show-feeds", required = false)
	public boolean showFeeds();

	@Meta.AD(
		deflt = "true", description = "structure-field-indexable-enable-help",
		name = "structure-field-indexable-enable", required = false
	)
	public boolean structureFieldIndexableEnable();

}