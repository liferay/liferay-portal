/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the CPConfigurationEntry service. Represents a row in the &quot;CPConfigurationEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Marco Leo
 * @see CPConfigurationEntryModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.commerce.product.model.impl.CPConfigurationEntryImpl"
)
@ProviderType
public interface CPConfigurationEntry
	extends CPConfigurationEntryModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.commerce.product.model.impl.CPConfigurationEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<CPConfigurationEntry, Long>
		CP_CONFIGURATION_ENTRY_ID_ACCESSOR =
			new Accessor<CPConfigurationEntry, Long>() {

				@Override
				public Long get(CPConfigurationEntry cpConfigurationEntry) {
					return cpConfigurationEntry.getCPConfigurationEntryId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<CPConfigurationEntry> getTypeClass() {
					return CPConfigurationEntry.class;
				}

			};

	public java.math.BigDecimal[] getAllowedOrderQuantitiesArray();

	public CPTaxCategory getCPTaxCategory()
		throws com.liferay.portal.kernel.exception.PortalException;

	public CPConfigurationList getParentCPConfigurationList()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean isMaster()
		throws com.liferay.portal.kernel.exception.PortalException;

}