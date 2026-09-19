/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the CommerceTaxFixedRateAddressRel service. Represents a row in the &quot;CommerceTaxFixedRateAddressRel&quot; database table, with each column mapped to a property of this class.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceTaxFixedRateAddressRelModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelImpl"
)
@ProviderType
public interface CommerceTaxFixedRateAddressRel
	extends CommerceTaxFixedRateAddressRelModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<CommerceTaxFixedRateAddressRel, Long>
		COMMERCE_TAX_FIXED_RATE_ADDRESS_REL_ID_ACCESSOR =
			new Accessor<CommerceTaxFixedRateAddressRel, Long>() {

				@Override
				public Long get(
					CommerceTaxFixedRateAddressRel
						commerceTaxFixedRateAddressRel) {

					return commerceTaxFixedRateAddressRel.
						getCommerceTaxFixedRateAddressRelId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<CommerceTaxFixedRateAddressRel> getTypeClass() {
					return CommerceTaxFixedRateAddressRel.class;
				}

			};

	public com.liferay.commerce.product.model.CPTaxCategory getCPTaxCategory()
		throws com.liferay.portal.kernel.exception.PortalException;

	public com.liferay.commerce.tax.model.CommerceTaxMethod
			getCommerceTaxMethod()
		throws com.liferay.portal.kernel.exception.PortalException;

	public com.liferay.portal.kernel.model.Country getCountry()
		throws com.liferay.portal.kernel.exception.PortalException;

	public com.liferay.portal.kernel.model.Region getRegion()
		throws com.liferay.portal.kernel.exception.PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2084845436