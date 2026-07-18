/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.account.exception.AccountEntryTypeException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.base.CommerceCatalogServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceCatalog"
	},
	service = AopService.class
)
public class CommerceCatalogServiceImpl extends CommerceCatalogServiceBaseImpl {

	@Override
	public CommerceCatalog addCommerceCatalog(
			String externalReferenceCode, long accountEntryId, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId,
			ServiceContext serviceContext)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceCatalogModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null, CPActionKeys.ADD_COMMERCE_CATALOG);

		if ((accountEntryId == 0) && !_hasViewCommerceCatalogsPermission()) {
			throw new AccountEntryTypeException();
		}

		_checkAccountEntry(accountEntryId);

		return commerceCatalogLocalService.addCommerceCatalog(
			externalReferenceCode, accountEntryId, name, commerceCurrencyCode,
			catalogDefaultLanguageId, false, serviceContext);
	}

	@Override
	public CommerceCatalog deleteCommerceCatalog(long commerceCatalogId)
		throws PortalException {

		_commerceCatalogModelResourcePermission.check(
			getPermissionChecker(), commerceCatalogId, ActionKeys.DELETE);

		return commerceCatalogLocalService.deleteCommerceCatalog(
			commerceCatalogId);
	}

	@Override
	public CommerceCatalog fetchCommerceCatalog(long commerceCatalogId)
		throws PortalException {

		CommerceCatalog commerceCatalog =
			commerceCatalogPersistence.fetchByPrimaryKey(commerceCatalogId);

		if (commerceCatalog != null) {
			_commerceCatalogModelResourcePermission.check(
				getPermissionChecker(), commerceCatalog, ActionKeys.VIEW);
		}

		return commerceCatalog;
	}

	@Override
	public CommerceCatalog fetchCommerceCatalogByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CommerceCatalog commerceCatalog =
			commerceCatalogLocalService.
				fetchCommerceCatalogByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commerceCatalog != null) {
			_commerceCatalogModelResourcePermission.check(
				getPermissionChecker(), commerceCatalog, ActionKeys.VIEW);
		}

		return commerceCatalog;
	}

	@Override
	public CommerceCatalog fetchCommerceCatalogByGroupId(long groupId)
		throws PortalException {

		CommerceCatalog commerceCatalog =
			commerceCatalogLocalService.fetchCommerceCatalogByGroupId(groupId);

		if (commerceCatalog != null) {
			_commerceCatalogModelResourcePermission.check(
				getPermissionChecker(), commerceCatalog, ActionKeys.VIEW);
		}

		return commerceCatalog;
	}

	@Override
	public CommerceCatalog getCommerceCatalog(long commerceCatalogId)
		throws PortalException {

		_commerceCatalogModelResourcePermission.check(
			getPermissionChecker(), commerceCatalogId, ActionKeys.VIEW);

		return commerceCatalogPersistence.findByPrimaryKey(commerceCatalogId);
	}

	@Override
	public List<CommerceCatalog> getCommerceCatalogs(
		long companyId, int start, int end) {

		return commerceCatalogPersistence.filterFindByCompanyId(
			companyId, start, end);
	}

	@Override
	public List<CommerceCatalog> search(
			long companyId, String keywords, int start, int end, Sort sort)
		throws PortalException {

		return commerceCatalogLocalService.search(
			companyId, keywords, start, end, sort);
	}

	@Override
	public int searchCommerceCatalogsCount(long companyId, String keywords)
		throws PortalException {

		return commerceCatalogLocalService.searchCommerceCatalogsCount(
			companyId, keywords);
	}

	@Override
	public CommerceCatalog updateCommerceCatalog(
			long commerceCatalogId, long accountEntryId, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId)
		throws PortalException {

		_commerceCatalogModelResourcePermission.check(
			getPermissionChecker(), commerceCatalogId, ActionKeys.UPDATE);

		if (!_hasViewCommerceCatalogsPermission()) {
			CommerceCatalog commerceCatalog =
				commerceCatalogPersistence.findByPrimaryKey(commerceCatalogId);

			accountEntryId = commerceCatalog.getAccountEntryId();
		}
		else {
			_checkAccountEntry(accountEntryId);
		}

		return commerceCatalogLocalService.updateCommerceCatalog(
			commerceCatalogId, accountEntryId, name, commerceCurrencyCode,
			catalogDefaultLanguageId);
	}

	@Override
	public CommerceCatalog updateCommerceCatalogExternalReferenceCode(
			String externalReferenceCode, long commerceCatalogId)
		throws PortalException {

		_commerceCatalogModelResourcePermission.check(
			getPermissionChecker(), commerceCatalogId, ActionKeys.UPDATE);

		return commerceCatalogLocalService.
			updateCommerceCatalogExternalReferenceCode(
				externalReferenceCode, commerceCatalogId);
	}

	private void _checkAccountEntry(long accountEntryId)
		throws PortalException {

		if (accountEntryId > 0) {
			AccountEntry accountEntry =
				_accountEntryLocalService.getAccountEntry(accountEntryId);

			_accountEntryModelResourcePermission.check(
				getPermissionChecker(), accountEntry.getAccountEntryId(),
				ActionKeys.VIEW);
		}
	}

	private boolean _hasViewCommerceCatalogsPermission()
		throws PrincipalException {

		PortletResourcePermission portletResourcePermission =
			_commerceCatalogModelResourcePermission.
				getPortletResourcePermission();

		return portletResourcePermission.contains(
			getPermissionChecker(), null, CPActionKeys.VIEW_COMMERCE_CATALOGS);
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceCatalog)"
	)
	private ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;

}