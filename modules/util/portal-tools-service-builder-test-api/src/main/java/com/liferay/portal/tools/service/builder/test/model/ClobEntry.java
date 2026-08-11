/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the ClobEntry service. Represents a row in the &quot;ClobEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see ClobEntryModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.portal.tools.service.builder.test.model.impl.ClobEntryImpl"
)
@ProviderType
public interface ClobEntry extends ClobEntryModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.tools.service.builder.test.model.impl.ClobEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<ClobEntry, Long> CLOB_ENTRY_ID_ACCESSOR =
		new Accessor<ClobEntry, Long>() {

			@Override
			public Long get(ClobEntry clobEntry) {
				return clobEntry.getClobEntryId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<ClobEntry> getTypeClass() {
				return ClobEntry.class;
			}

		};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1998237266