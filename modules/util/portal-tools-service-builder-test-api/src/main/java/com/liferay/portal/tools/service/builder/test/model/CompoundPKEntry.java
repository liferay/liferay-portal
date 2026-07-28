/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the CompoundPKEntry service. Represents a row in the &quot;CompoundPKEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see CompoundPKEntryModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryImpl"
)
@ProviderType
public interface CompoundPKEntry extends CompoundPKEntryModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<CompoundPKEntry, Long> COMPANY_ID_ACCESSOR =
		new Accessor<CompoundPKEntry, Long>() {

			@Override
			public Long get(CompoundPKEntry compoundPKEntry) {
				return compoundPKEntry.getCompanyId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<CompoundPKEntry> getTypeClass() {
				return CompoundPKEntry.class;
			}

		};
	public static final Accessor<CompoundPKEntry, Long> CLASS_NAME_ID_ACCESSOR =
		new Accessor<CompoundPKEntry, Long>() {

			@Override
			public Long get(CompoundPKEntry compoundPKEntry) {
				return compoundPKEntry.getClassNameId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<CompoundPKEntry> getTypeClass() {
				return CompoundPKEntry.class;
			}

		};

}
// LIFERAY-SERVICE-BUILDER-HASH:2125291218