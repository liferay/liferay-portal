/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PricingAccountGroup;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "dto.class.name=com.liferay.account.model.AccountGroup",
	service = DTOConverter.class
)
public class PricingAccountGroupDTOConverter
	implements DTOConverter<AccountGroup, PricingAccountGroup> {

	@Override
	public String getContentType() {
		return PricingAccountGroup.class.getSimpleName();
	}

	@Override
	public PricingAccountGroup toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		AccountGroup accountGroup = _accountGroupService.getAccountGroup(
			(Long)dtoConverterContext.getId());

		return new PricingAccountGroup() {
			{
				setId(accountGroup::getAccountGroupId);
				setName(accountGroup::getName);
			}
		};
	}

	@Reference
	private AccountGroupService _accountGroupService;

}