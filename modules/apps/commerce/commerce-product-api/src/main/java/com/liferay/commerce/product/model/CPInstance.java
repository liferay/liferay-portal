/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the CPInstance service. Represents a row in the &quot;CPInstance&quot; database table, with each column mapped to a property of this class.
 *
 * @author Marco Leo
 * @see CPInstanceModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.commerce.product.model.impl.CPInstanceImpl"
)
@ProviderType
public interface CPInstance extends CPInstanceModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.commerce.product.model.impl.CPInstanceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<CPInstance, Long> CP_INSTANCE_ID_ACCESSOR =
		new Accessor<CPInstance, Long>() {

			@Override
			public Long get(CPInstance cpInstance) {
				return cpInstance.getCPInstanceId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<CPInstance> getTypeClass() {
				return CPInstance.class;
			}

		};

	public CPInstanceUnitOfMeasure fetchCPInstanceUnitOfMeasure(String key);

	public CPDefinition getCPDefinition()
		throws com.liferay.portal.kernel.exception.PortalException;

	public CPInstanceUnitOfMeasure getCPInstanceUnitOfMeasure(String key)
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<CPInstanceUnitOfMeasure> getCPInstanceUnitOfMeasures(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPInstanceUnitOfMeasure> orderByComparator);

	public CPSubscriptionInfo getCPSubscriptionInfo()
		throws com.liferay.portal.kernel.exception.PortalException;

	public CommerceCatalog getCommerceCatalog()
		throws com.liferay.portal.kernel.exception.PortalException;

	public com.liferay.portal.kernel.util.UnicodeProperties
		getDeliverySubscriptionTypeSettingsUnicodeProperties();

	public com.liferay.portal.kernel.util.UnicodeProperties
		getSubscriptionTypeSettingsUnicodeProperties();

	public boolean hasCPInstanceUnitOfMeasures();

	public void setDeliverySubscriptionTypeSettingsUnicodeProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			deliverySubscriptionTypeSettingsUnicodeProperties);

	public void setSubscriptionTypeSettingsUnicodeProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			subscriptionTypeSettingsUnicodeProperties);

}
// LIFERAY-SERVICE-BUILDER-HASH:655342155