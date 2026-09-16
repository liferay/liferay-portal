/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageTransition;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.resource.v1_0.AccountLifecycleStageTransitionResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Leslie Wong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/account-lifecycle-stage-transition.properties",
	scope = ServiceScope.PROTOTYPE,
	service = AccountLifecycleStageTransitionResource.class
)
public class AccountLifecycleStageTransitionResourceImpl
	extends BaseAccountLifecycleStageTransitionResourceImpl {

	@Override
	public Page<AccountLifecycleStageTransition>
			getWorkspaceGroupAccountLifecycleStageTransitionsPage(
				Long groupId, String accountLifecycleId, String country,
				String fromLifecycleStage, String industry, String rangeEnd,
				String rangeKey, String rangeStart, Long segmentId,
				String toLifecycleStage, Pagination pagination, Sort[] sorts)
		throws Exception {

		Results
			<com.liferay.osb.faro.engine.client.model.
				AccountLifecycleStageTransition> results =
					_contactsEngineClient.getAccountLifecycleStageTransitions(
						_faroProjectLocalService.getFaroProjectByGroupId(
							groupId),
						country, fromLifecycleStage, accountLifecycleId,
						industry, rangeEnd, TimeRange.getRangeKey(rangeKey),
						rangeStart, segmentId, toLifecycleStage,
						FaroPaginationUtil.getCur(pagination),
						FaroPaginationUtil.getDelta(pagination),
						FaroPaginationUtil.toOrderByFields(sorts));

		return Page.of(
			transform(
				results.getItems(),
				accountLifecycleStageTransition ->
					_accountLifecycleStageTransitionDTOConverter.toDTO(
						new FaroDTOConverterContext(
							contextAcceptLanguage.isAcceptAllLanguages(),
							accountLifecycleStageTransition.getAccountId(),
							contextAcceptLanguage.getPreferredLocale()),
						accountLifecycleStageTransition)),
			pagination, results.getTotal());
	}

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.AccountLifecycleStageTransitionDTOConverter)"
	)
	private DTOConverter
		<com.liferay.osb.faro.engine.client.model.
			AccountLifecycleStageTransition,
		 AccountLifecycleStageTransition>
			_accountLifecycleStageTransitionDTOConverter;

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}