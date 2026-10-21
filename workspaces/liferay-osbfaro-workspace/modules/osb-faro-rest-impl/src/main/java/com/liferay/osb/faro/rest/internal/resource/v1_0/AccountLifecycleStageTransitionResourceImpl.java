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
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageTransition;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.resource.v1_0.AccountLifecycleStageTransitionResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.Validator;
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

		FaroProject faroProject =
			_faroProjectLocalService.getFaroProjectByGroupId(groupId);

		AccountLifecycle accountLifecycle = null;

		if (Validator.isNotNull(fromLifecycleStage) ||
			Validator.isNotNull(toLifecycleStage)) {

			accountLifecycle = _contactsEngineClient.getAccountLifecycle(
				faroProject, accountLifecycleId);
		}

		Results
			<com.liferay.osb.faro.engine.client.model.
				AccountLifecycleStageTransition> results =
					_contactsEngineClient.getAccountLifecycleStageTransitions(
						faroProject, country,
						_getAccountLifecycleStageId(
							accountLifecycle, fromLifecycleStage),
						accountLifecycleId, industry, rangeEnd,
						TimeRange.getRangeKey(rangeKey), rangeStart, segmentId,
						_getAccountLifecycleStageId(
							accountLifecycle, toLifecycleStage),
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

	private String _getAccountLifecycleStageId(
		AccountLifecycle accountLifecycle, String accountLifecycleStageName) {

		if (Validator.isNull(accountLifecycleStageName)) {
			return null;
		}

		AccountLifecycleStage accountLifecycleStage =
			accountLifecycle.getAccountLifecycleStage(
				accountLifecycleStageName);

		if (accountLifecycleStage == null) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"No account lifecycle stage was found with the name \"",
					accountLifecycleStageName, "\""));
		}

		return accountLifecycleStage.getId();
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