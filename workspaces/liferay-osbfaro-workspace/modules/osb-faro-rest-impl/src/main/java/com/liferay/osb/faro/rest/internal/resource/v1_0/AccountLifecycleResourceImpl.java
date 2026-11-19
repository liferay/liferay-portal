/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycle;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.resource.v1_0.AccountLifecycleResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Nilton Vieira
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/account-lifecycle.properties",
	scope = ServiceScope.PROTOTYPE, service = AccountLifecycleResource.class
)
public class AccountLifecycleResourceImpl
	extends BaseAccountLifecycleResourceImpl {

	@Override
	public Page<AccountLifecycle> getWorkspaceGroupChannelAccountLifecyclesPage(
			Long groupId, String channelId, String search,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		Results<com.liferay.osb.faro.engine.client.model.AccountLifecycle>
			results = _contactsEngineClient.getAccountLifecycles(
				_faroProjectLocalService.getFaroProjectByGroupId(groupId),
				GetterUtil.getLong(channelId), null, null, search,
				FaroPaginationUtil.getCur(pagination),
				FaroPaginationUtil.getDelta(pagination),
				StringUtil.replaceFirst(
					GetterUtil.getString(
						FaroPaginationUtil.toSortString(sorts), "name:asc"),
					"dateModified:", "modifiedDate:"));

		return Page.of(
			transform(
				results.getItems(),
				accountLifecycle -> _accountLifecycleDTOConverter.toDTO(
					new FaroDTOConverterContext(
						contextAcceptLanguage.isAcceptAllLanguages(),
						accountLifecycle.getId(),
						contextAcceptLanguage.getPreferredLocale()),
					accountLifecycle)),
			pagination, results.getTotal());
	}

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.AccountLifecycleDTOConverter)"
	)
	private DTOConverter
		<com.liferay.osb.faro.engine.client.model.AccountLifecycle,
		 AccountLifecycle> _accountLifecycleDTOConverter;

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}