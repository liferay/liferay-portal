/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the CommerceSubscriptionEntry service. Represents a row in the &quot;CommerceSubscriptionEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceSubscriptionEntryModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.commerce.model.impl.CommerceSubscriptionEntryImpl"
)
@ProviderType
public interface CommerceSubscriptionEntry
	extends CommerceSubscriptionEntryModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<CommerceSubscriptionEntry, Long>
		COMMERCE_SUBSCRIPTION_ENTRY_ID_ACCESSOR =
			new Accessor<CommerceSubscriptionEntry, Long>() {

				@Override
				public Long get(
					CommerceSubscriptionEntry commerceSubscriptionEntry) {

					return commerceSubscriptionEntry.
						getCommerceSubscriptionEntryId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<CommerceSubscriptionEntry> getTypeClass() {
					return CommerceSubscriptionEntry.class;
				}

			};

	public com.liferay.commerce.product.model.CPDefinition fetchCPDefinition()
		throws com.liferay.portal.kernel.exception.PortalException;

	public com.liferay.commerce.product.model.CPInstance fetchCPInstance();

	public CommerceOrderItem fetchCommerceOrderItem();

	public long getCPDefinitionId()
		throws com.liferay.portal.kernel.exception.PortalException;

	public long getCPInstanceId();

	public com.liferay.portal.kernel.util.UnicodeProperties
		getDeliverySubscriptionTypeSettingsUnicodeProperties();

	public com.liferay.portal.kernel.util.UnicodeProperties
		getSubscriptionTypeSettingsUnicodeProperties();

	public void setDeliverySubscriptionTypeSettingsUnicodeProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			deliverySubscriptionTypeSettingsUnicodeProperties);

	public void setSubscriptionTypeSettingsUnicodeProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			subscriptionTypeSettingsUnicodeProperties);

}
// LIFERAY-SERVICE-BUILDER-HASH:1034934230