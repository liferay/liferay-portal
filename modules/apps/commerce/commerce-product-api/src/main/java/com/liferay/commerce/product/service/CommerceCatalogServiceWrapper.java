/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CommerceCatalogService}.
 *
 * @author Marco Leo
 * @see CommerceCatalogService
 * @generated
 */
public class CommerceCatalogServiceWrapper
	implements CommerceCatalogService, ServiceWrapper<CommerceCatalogService> {

	public CommerceCatalogServiceWrapper() {
		this(null);
	}

	public CommerceCatalogServiceWrapper(
		CommerceCatalogService commerceCatalogService) {

		_commerceCatalogService = commerceCatalogService;
	}

	@Override
	public CommerceCatalog addCommerceCatalog(
			String externalReferenceCode, long accountEntryId, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogService.addCommerceCatalog(
			externalReferenceCode, accountEntryId, name, commerceCurrencyCode,
			catalogDefaultLanguageId, serviceContext);
	}

	@Override
	public CommerceCatalog deleteCommerceCatalog(long commerceCatalogId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogService.deleteCommerceCatalog(commerceCatalogId);
	}

	@Override
	public CommerceCatalog fetchCommerceCatalog(long commerceCatalogId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogService.fetchCommerceCatalog(commerceCatalogId);
	}

	@Override
	public CommerceCatalog fetchCommerceCatalogByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogService.
			fetchCommerceCatalogByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CommerceCatalog fetchCommerceCatalogByGroupId(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogService.fetchCommerceCatalogByGroupId(groupId);
	}

	@Override
	public CommerceCatalog getCommerceCatalog(long commerceCatalogId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogService.getCommerceCatalog(commerceCatalogId);
	}

	@Override
	public java.util.List<CommerceCatalog> getCommerceCatalogs(
		long companyId, int start, int end) {

		return _commerceCatalogService.getCommerceCatalogs(
			companyId, start, end);
	}

	@Override
	public CommerceCatalog getOrAddEmptyCommerceCatalog(
			String externalReferenceCode, String commerceCurrencyCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogService.getOrAddEmptyCommerceCatalog(
			externalReferenceCode, commerceCurrencyCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceCatalogService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<CommerceCatalog> search(
			long companyId, String keywords, int start, int end,
			com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogService.search(
			companyId, keywords, start, end, sort);
	}

	@Override
	public int searchCommerceCatalogsCount(long companyId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogService.searchCommerceCatalogsCount(
			companyId, keywords);
	}

	@Override
	public CommerceCatalog updateCommerceCatalog(
			long commerceCatalogId, long accountEntryId, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogService.updateCommerceCatalog(
			commerceCatalogId, accountEntryId, name, commerceCurrencyCode,
			catalogDefaultLanguageId);
	}

	@Override
	public CommerceCatalog updateCommerceCatalogExternalReferenceCode(
			String externalReferenceCode, long commerceCatalogId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogService.
			updateCommerceCatalogExternalReferenceCode(
				externalReferenceCode, commerceCatalogId);
	}

	@Override
	public CommerceCatalogService getWrappedService() {
		return _commerceCatalogService;
	}

	@Override
	public void setWrappedService(
		CommerceCatalogService commerceCatalogService) {

		_commerceCatalogService = commerceCatalogService;
	}

	private CommerceCatalogService _commerceCatalogService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1510564833