/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the SamlIbSloMessage service. Represents a row in the &quot;SamlIbSloMessage&quot; database table, with each column mapped to a property of this class.
 *
 * @author Mika Koivisto
 * @see SamlIbSloMessageModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.saml.persistence.model.impl.SamlIbSloMessageImpl"
)
@ProviderType
public interface SamlIbSloMessage
	extends PersistedModel, SamlIbSloMessageModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.saml.persistence.model.impl.SamlIbSloMessageImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<SamlIbSloMessage, Long>
		SAML_IB_SLO_MESSAGE_ID_ACCESSOR =
			new Accessor<SamlIbSloMessage, Long>() {

				@Override
				public Long get(SamlIbSloMessage samlIbSloMessage) {
					return samlIbSloMessage.getSamlIbSloMessageId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<SamlIbSloMessage> getTypeClass() {
					return SamlIbSloMessage.class;
				}

			};

}