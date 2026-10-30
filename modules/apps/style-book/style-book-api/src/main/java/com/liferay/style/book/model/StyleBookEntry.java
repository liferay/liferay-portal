/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the StyleBookEntry service. Represents a row in the &quot;StyleBookEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookEntryModel
 * @generated
 */
@ImplementationClassName("com.liferay.style.book.model.impl.StyleBookEntryImpl")
@ProviderType
public interface StyleBookEntry extends PersistedModel, StyleBookEntryModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.style.book.model.impl.StyleBookEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<StyleBookEntry, Long>
		STYLE_BOOK_ENTRY_ID_ACCESSOR = new Accessor<StyleBookEntry, Long>() {

			@Override
			public Long get(StyleBookEntry styleBookEntry) {
				return styleBookEntry.getStyleBookEntryId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<StyleBookEntry> getTypeClass() {
				return StyleBookEntry.class;
			}

		};

	public String getImagePreviewURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay);

	public void populateZipWriter(
			com.liferay.portal.kernel.zip.ZipWriter zipWriter, String path)
		throws Exception;

}
// LIFERAY-SERVICE-BUILDER-HASH:1136805504