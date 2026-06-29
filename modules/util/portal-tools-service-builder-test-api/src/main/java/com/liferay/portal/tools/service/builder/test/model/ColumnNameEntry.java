/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the ColumnNameEntry service. Represents a row in the &quot;ColumnNameEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see ColumnNameEntryModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.portal.tools.service.builder.test.model.impl.ColumnNameEntryImpl"
)
@ProviderType
public interface ColumnNameEntry extends ColumnNameEntryModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.tools.service.builder.test.model.impl.ColumnNameEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<ColumnNameEntry, Long>
		COLUMN_NAME_ENTRY_ID_ACCESSOR = new Accessor<ColumnNameEntry, Long>() {

			@Override
			public Long get(ColumnNameEntry columnNameEntry) {
				return columnNameEntry.getColumnNameEntryId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<ColumnNameEntry> getTypeClass() {
				return ColumnNameEntry.class;
			}

		};

}
// LIFERAY-SERVICE-BUILDER-HASH:791538869