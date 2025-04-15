/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.frontend.data.set.provider;

import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.commerce.product.definitions.web.internal.constants.CommerceProductFDSNames;
import com.liferay.commerce.product.definitions.web.internal.model.CProductAccountGroup;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceProductFDSNames.PRODUCT_ACCOUNT_GROUPS,
	service = FDSDataProvider.class
)
public class CommerceProductAccountGroupFDSDataProvider
	implements FDSDataProvider<CProductAccountGroup> {

	@Override
	public List<CProductAccountGroup> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long cpDefinitionId = ParamUtil.getLong(
			httpServletRequest, "cpDefinitionId");

		return TransformUtil.transform(
			_accountGroupRelLocalService.getAccountGroupRels(
				TransformUtil.transformToLongArray(
					_getAccountGroups(httpServletRequest),
					AccountGroup::getAccountGroupId),
				CPDefinition.class.getName(), cpDefinitionId,
				fdsKeywords.getKeywords(), fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition()),
			accountGroupRel -> {
				AccountGroup accountGroup =
					_accountGroupLocalService.getAccountGroup(
						accountGroupRel.getAccountGroupId());

				return new CProductAccountGroup(
					accountGroupRel.getAccountGroupRelId(),
					accountGroup.getName());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long cpDefinitionId = ParamUtil.getLong(
			httpServletRequest, "cpDefinitionId");

		return _accountGroupRelLocalService.getAccountGroupRelsCount(
			TransformUtil.transformToLongArray(
				_getAccountGroups(httpServletRequest),
				AccountGroup::getAccountGroupId),
			CPDefinition.class.getName(), cpDefinitionId,
			fdsKeywords.getKeywords());
	}

	private List<AccountGroup> _getAccountGroups(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		long cpDefinitionId = ParamUtil.getLong(
			httpServletRequest, "cpDefinitionId");

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		LinkedHashMap<String, Object> params = new LinkedHashMap<>();

		long permissionUserId = ParamUtil.getLong(
			httpServletRequest, "permissionUserId");

		if (permissionUserId > 0) {
			params.put("permissionUserId", permissionUserId);
		}

		BaseModelSearchResult<AccountGroup> baseModelSearchResult =
			_accountGroupLocalService.searchAccountGroups(
				cpDefinition.getCompanyId(), null, params, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		return baseModelSearchResult.getBaseModels();
	}

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

}