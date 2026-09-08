/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.internal.dto.v1_0.converter;

import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.service.AccountEntryOrganizationRelService;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountOrganization;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.account.model.AccountEntryOrganizationRel"
	},
	service = DTOConverter.class
)
public class AccountOrganizationDTOConverter
	implements DTOConverter<AccountEntryOrganizationRel, AccountOrganization> {

	@Override
	public String getContentType() {
		return AccountOrganization.class.getSimpleName();
	}

	@Override
	public AccountOrganization toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		AccountEntryOrganizationRel accountEntryOrganizationRel =
			_accountEntryOrganizationRelService.
				fetchAccountEntryOrganizationRel(
					(long)dtoConverterContext.getId());

		Organization organization =
			accountEntryOrganizationRel.getOrganization();

		return new AccountOrganization() {
			{
				setAccountId(accountEntryOrganizationRel::getAccountEntryId);
				setName(organization::getName);
				setOrganizationId(organization::getOrganizationId);
				setTreePath(organization::getTreePath);
			}
		};
	}

	@Reference
	private AccountEntryOrganizationRelService
		_accountEntryOrganizationRelService;

}