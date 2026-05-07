/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the OAuthClientPRLocalMetadata service. Represents a row in the &quot;OAuthClientPRLocalMetadata&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientPRLocalMetadataModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataImpl"
)
@ProviderType
public interface OAuthClientPRLocalMetadata
	extends OAuthClientPRLocalMetadataModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<OAuthClientPRLocalMetadata, Long>
		O_AUTH_CLIENT_PR_LOCAL_METADATA_ID_ACCESSOR =
			new Accessor<OAuthClientPRLocalMetadata, Long>() {

				@Override
				public Long get(
					OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

					return oAuthClientPRLocalMetadata.
						getOAuthClientPRLocalMetadataId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<OAuthClientPRLocalMetadata> getTypeClass() {
					return OAuthClientPRLocalMetadata.class;
				}

			};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1624933733