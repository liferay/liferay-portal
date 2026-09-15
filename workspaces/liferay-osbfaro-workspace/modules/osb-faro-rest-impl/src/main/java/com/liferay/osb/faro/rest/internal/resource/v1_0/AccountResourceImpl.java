/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.AccountLifecycle;
import com.liferay.osb.faro.engine.client.model.AccountLifecycleStage;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.dto.v1_0.Account;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.resource.v1_0.AccountResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Leslie Wong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/account.properties",
	scope = ServiceScope.PROTOTYPE, service = AccountResource.class
)
public class AccountResourceImpl extends BaseAccountResourceImpl {

	@Override
	public Account getWorkspaceGroupAccount(Long groupId, String accountId)
		throws Exception {

		return _accountDTOConverter.toDTO(
			new FaroDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), accountId,
				contextAcceptLanguage.getPreferredLocale()),
			_contactsEngineClient.getAccount(
				_faroProjectLocalService.getFaroProjectByGroupId(groupId),
				accountId, null));
	}

	@Override
	public Page<Account> getWorkspaceGroupChannelAccountsPage(
			Long groupId, String channelId, String lifecycleStage,
			String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		FaroProject faroProject =
			_faroProjectLocalService.getFaroProjectByGroupId(groupId);

		Results<com.liferay.osb.faro.engine.client.model.Account> results =
			_contactsEngineClient.getAccounts(
				faroProject, channelId,
				_getLifecycleStageFilterString(faroProject, lifecycleStage),
				search, FaroPaginationUtil.getCur(pagination),
				FaroPaginationUtil.getDelta(pagination), null);

		return Page.of(
			transform(
				results.getItems(),
				account -> _accountDTOConverter.toDTO(
					new FaroDTOConverterContext(
						contextAcceptLanguage.isAcceptAllLanguages(),
						account.getId(),
						contextAcceptLanguage.getPreferredLocale()),
					account)),
			pagination, results.getTotal());
	}

	private String _getLifecycleStageFilterString(
		FaroProject faroProject, String lifecycleStage) {

		if (Validator.isNull(lifecycleStage)) {
			return null;
		}

		List<AccountLifecycle> accountLifecycles =
			_contactsEngineClient.getAccountLifecycles(faroProject);

		if (ListUtil.isEmpty(accountLifecycles)) {
			throw new IllegalArgumentException(
				"No account lifecycle is configured for the workspace");
		}

		AccountLifecycle accountLifecycle = accountLifecycles.get(0);

		accountLifecycle = _contactsEngineClient.getAccountLifecycle(
			faroProject, accountLifecycle.getId());

		List<AccountLifecycleStage> accountLifecycleStages =
			accountLifecycle.getStages();

		if (ListUtil.isNotEmpty(accountLifecycleStages)) {
			for (AccountLifecycleStage accountLifecycleStage :
					accountLifecycleStages) {

				if (Objects.equals(
						accountLifecycleStage.getStageType(), lifecycleStage)) {

					return StringBundler.concat(
						"(lifecycleStatus in ('", accountLifecycleStage.getId(),
						"'))");
				}
			}
		}

		throw new IllegalArgumentException(
			StringBundler.concat(
				"No lifecycle stage was found with the name \"", lifecycleStage,
				"\""));
	}

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.AccountDTOConverter)"
	)
	private DTOConverter
		<com.liferay.osb.faro.engine.client.model.Account, Account>
			_accountDTOConverter;

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}

// LIFERAY-REST-BUILDER-HASH:126436837