/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the OpenIdConnectUser service. Represents a row in the &quot;OpenIdConnectUser&quot; database table, with each column mapped to a property of this class.
 *
 * @author Arthur Chan
 * @see OpenIdConnectUserModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserImpl"
)
@ProviderType
public interface OpenIdConnectUser
	extends OpenIdConnectUserModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<OpenIdConnectUser, Long>
		OPEN_ID_CONNECT_USER_ID_ACCESSOR =
			new Accessor<OpenIdConnectUser, Long>() {

				@Override
				public Long get(OpenIdConnectUser openIdConnectUser) {
					return openIdConnectUser.getOpenIdConnectUserId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<OpenIdConnectUser> getTypeClass() {
					return OpenIdConnectUser.class;
				}

			};

}