/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.display.context;

import com.liferay.account.admin.web.internal.display.AddressDisplay;
import com.liferay.account.constants.AccountListTypeConstants;
import com.liferay.portal.kernel.account.configuration.manager.AccountEntryAddressSubtypeConfigurationManagerUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.RenderResponse;

import java.util.Map;

/**
 * @author Balazs Breier
 */
public class AccountEntryAddressDisplayContext {

	public AccountEntryAddressDisplayContext(
		AddressDisplay addressDisplay, Portal portal,
		RenderResponse renderResponse) {

		_addressDisplay = addressDisplay;
		_portal = portal;

		_liferayPortletResponse = portal.getLiferayPortletResponse(
			renderResponse);
	}

	public Map<String, Object> getContext() {
		return HashMapBuilder.<String, Object>put(
			"addressSubtypeConfiguration",
			() -> {
				Portlet portlet = _liferayPortletResponse.getPortlet();

				long companyId = portlet.getCompanyId();

				return HashMapBuilder.<String, Object>put(
					"billing",
					AccountEntryAddressSubtypeConfigurationManagerUtil.
						getAddressSubtypeListTypeDefinitionExternalReferenceCode(
							companyId,
							AccountListTypeConstants.
								ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING)
				).put(
					"billingAndShipping",
					AccountEntryAddressSubtypeConfigurationManagerUtil.
						getAddressSubtypeListTypeDefinitionExternalReferenceCode(
							companyId,
							AccountListTypeConstants.
								ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING_AND_SHIPPING)
				).put(
					"shipping",
					AccountEntryAddressSubtypeConfigurationManagerUtil.
						getAddressSubtypeListTypeDefinitionExternalReferenceCode(
							companyId,
							AccountListTypeConstants.
								ACCOUNT_ENTRY_ADDRESS_TYPE_SHIPPING)
				).build();
			}
		).put(
			"initialAddressType", _addressDisplay.getListTypeName()
		).put(
			"initialValue", _addressDisplay.getSubtype()
		).put(
			"namespace", _liferayPortletResponse.getNamespace()
		).build();
	}

	private final AddressDisplay _addressDisplay;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final Portal _portal;

}