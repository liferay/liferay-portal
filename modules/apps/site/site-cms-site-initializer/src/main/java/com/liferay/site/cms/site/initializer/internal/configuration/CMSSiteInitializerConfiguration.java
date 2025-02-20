/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Sam Ziemer
 */
@ExtendedObjectClassDefinition(category = "assets", generateUI = false)
@Meta.OCD(
	id = "com.liferay.site.cms.site.initializer.internal.configuration.CMSSiteInitializerConfiguration",
	localization = "content/Language",
	name = "cms-site-initializer-configuration-name"
)
public interface CMSSiteInitializerConfiguration {

	@Meta.AD(
		deflt = "com.liferay.asset.kernel.model.AssetVocabulary",
		name = "categorization-class-names", required = false
	)
	public String[] categorizationClassNames();

	@Meta.AD(
		deflt = "com.liferay.blogs.model.BlogsEntry|com.liferay.bookmarks.model.BookmarksEntry|com.liferay.bookmarks.model.BookmarksFolder|com.liferay.document.library.kernel.model.DLFileShortcut|com.liferay.document.library.kernel.model.DLFolder|com.liferay.dynamic.data.mapping.model.DDMFormInstance|com.liferay.journal.model.JournalArticle|com.liferay.journal.model.JournalFolder|com.liferay.knowledge.base.model.KBArticle|com.liferay.knowledge.base.model.KBFolder|com.liferay.message.boards.model.MBCategory|com.liferay.message.boards.model.MBThread",
		name = "contents-class-names", required = false
	)
	public String[] contentsClassNames();

	@Meta.AD(
		deflt = "com.liferay.document.library.kernel.model.DLFileEntry",
		name = "files-class-names", required = false
	)
	public String[] filesClassNames();

	@Meta.AD(
		deflt = "com.liferay.dynamic.data.mapping.model.DDMStructure",
		name = "structures-class-names", required = false
	)
	public String[] structuresClassNames();

}