/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the AudiencesEntry service. Represents a row in the &quot;AudiencesEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see AudiencesEntryModel
 * @generated
 */
@ImplementationClassName("com.liferay.audiences.model.impl.AudiencesEntryImpl")
@ProviderType
public interface AudiencesEntry extends AudiencesEntryModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.audiences.model.impl.AudiencesEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<AudiencesEntry, Long>
		AUDIENCES_ENTRY_ID_ACCESSOR = new Accessor<AudiencesEntry, Long>() {

			@Override
			public Long get(AudiencesEntry audiencesEntry) {
				return audiencesEntry.getAudiencesEntryId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<AudiencesEntry> getTypeClass() {
				return AudiencesEntry.class;
			}

		};

	public java.util.List<String> getGroupERCs();

}
// LIFERAY-SERVICE-BUILDER-HASH:348584731