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
 * The extended model interface for the AudiencesEntryGroupRel service. Represents a row in the &quot;AudiencesEntryGroupRel&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see AudiencesEntryGroupRelModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.audiences.model.impl.AudiencesEntryGroupRelImpl"
)
@ProviderType
public interface AudiencesEntryGroupRel
	extends AudiencesEntryGroupRelModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<AudiencesEntryGroupRel, Long>
		AUDIENCES_ENTRY_GROUP_REL_ID_ACCESSOR =
			new Accessor<AudiencesEntryGroupRel, Long>() {

				@Override
				public Long get(AudiencesEntryGroupRel audiencesEntryGroupRel) {
					return audiencesEntryGroupRel.getAudiencesEntryGroupRelId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<AudiencesEntryGroupRel> getTypeClass() {
					return AudiencesEntryGroupRel.class;
				}

			};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1239357385