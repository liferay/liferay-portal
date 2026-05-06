/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the OAuth2Authorization service. Represents a row in the &quot;OAuth2Authorization&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2AuthorizationModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationImpl"
)
@ProviderType
public interface OAuth2Authorization
	extends OAuth2AuthorizationModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<OAuth2Authorization, Long>
		O_AUTH2_AUTHORIZATION_ID_ACCESSOR =
			new Accessor<OAuth2Authorization, Long>() {

				@Override
				public Long get(OAuth2Authorization oAuth2Authorization) {
					return oAuth2Authorization.getOAuth2AuthorizationId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<OAuth2Authorization> getTypeClass() {
					return OAuth2Authorization.class;
				}

			};

	public java.util.List<String> getAudiencesList();

	public void setAudiencesList(java.util.List<String> audiencesList);

}
// LIFERAY-SERVICE-BUILDER-HASH:1898234286