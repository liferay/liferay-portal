/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.Account;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leslie Wong
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.engine.client.model.Account",
	service = DTOConverter.class
)
public class AccountDTOConverter
	implements DTOConverter
		<com.liferay.osb.faro.engine.client.model.Account, Account> {

	@Override
	public String getContentType() {
		return Account.class.getSimpleName();
	}

	@Override
	public Account toDTO(
		DTOConverterContext dtoConverterContext,
		com.liferay.osb.faro.engine.client.model.Account account) {

		if (account == null) {
			return null;
		}

		return new Account() {
			{
				setAccountName(account::getAccountName);
				setAccountType(account::getAccountType);
				setActivitiesCount(account::getActivitiesCount);
				setAnnualRevenue(account::getAnnualRevenue);
				setCountry(account::getCountry);
				setDateModified(account::getModifiedDate);
				setFirstActivityDate(account::getFirstActivityDate);
				setId(account::getId);
				setIndustry(account::getIndustry);
				setLastActivityDate(account::getLastActivityDate);
				setLifecycleStage(
					() -> {
						List
							<com.liferay.osb.faro.engine.client.model.Account.
								LifecycleStage> lifecycleStages =
									account.getLifecycleStages();

						if (ListUtil.isEmpty(lifecycleStages)) {
							return null;
						}

						com.liferay.osb.faro.engine.client.model.Account.
							LifecycleStage lifecycleStage = lifecycleStages.get(
								0);

						return lifecycleStage.getStageType();
					});
				setNumberOfEmployees(account::getNumberOfEmployees);
				setWebsite(account::getWebsite);
			}
		};
	}

}