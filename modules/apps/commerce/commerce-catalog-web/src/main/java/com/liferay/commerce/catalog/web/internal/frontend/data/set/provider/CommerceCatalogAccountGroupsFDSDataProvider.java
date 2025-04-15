/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.catalog.web.internal.frontend.data.set.provider;

import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.account.service.AccountGroupService;
import com.liferay.commerce.catalog.web.internal.constants.CommerceCatalogFDSNames;
import com.liferay.commerce.catalog.web.internal.model.CatalogAccountGroup;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gianmarco Brunialti Masera
 */
@Component(
	property = "fds.data.provider.key=" + CommerceCatalogFDSNames.CATALOG_ACCOUNT_GROUPS,
	service = FDSDataProvider.class
)
public class CommerceCatalogAccountGroupsFDSDataProvider
	implements FDSDataProvider<CatalogAccountGroup> {

	@Override
	public List<CatalogAccountGroup> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long commerceCatalogId = ParamUtil.getLong(
			httpServletRequest, "commerceCatalogId");

		return TransformUtil.transform(
			_accountGroupRelLocalService.getAccountGroupRels(
				CommerceCatalog.class.getName(), commerceCatalogId,
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(), null),
			accountGroupRel -> {
				AccountGroup accountGroup =
					_accountGroupService.getAccountGroup(
						accountGroupRel.getAccountGroupId());

				return new CatalogAccountGroup(accountGroup.getName());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceCatalogId = ParamUtil.getLong(
			httpServletRequest, "commerceCatalogId");

		return _accountGroupRelLocalService.getAccountGroupRelsCount(
			CommerceCatalog.class.getName(), commerceCatalogId);
	}

	@Reference
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Reference
	private AccountGroupService _accountGroupService;

}