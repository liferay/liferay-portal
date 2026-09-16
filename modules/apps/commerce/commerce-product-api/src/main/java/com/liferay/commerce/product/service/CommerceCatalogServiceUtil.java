/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;

/**
 * Provides the remote service utility for CommerceCatalog. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CommerceCatalogServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see CommerceCatalogService
 * @generated
 */
public class CommerceCatalogServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CommerceCatalogServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommerceCatalog addCommerceCatalog(
			String externalReferenceCode, long accountEntryId, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceCatalog(
			externalReferenceCode, accountEntryId, name, commerceCurrencyCode,
			catalogDefaultLanguageId, serviceContext);
	}

	public static CommerceCatalog deleteCommerceCatalog(long commerceCatalogId)
		throws PortalException {

		return getService().deleteCommerceCatalog(commerceCatalogId);
	}

	public static CommerceCatalog fetchCommerceCatalog(long commerceCatalogId)
		throws PortalException {

		return getService().fetchCommerceCatalog(commerceCatalogId);
	}

	public static CommerceCatalog fetchCommerceCatalogByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCommerceCatalogByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CommerceCatalog fetchCommerceCatalogByGroupId(long groupId)
		throws PortalException {

		return getService().fetchCommerceCatalogByGroupId(groupId);
	}

	public static CommerceCatalog getCommerceCatalog(long commerceCatalogId)
		throws PortalException {

		return getService().getCommerceCatalog(commerceCatalogId);
	}

	public static List<CommerceCatalog> getCommerceCatalogs(
		long companyId, int start, int end) {

		return getService().getCommerceCatalogs(companyId, start, end);
	}

	public static CommerceCatalog getOrAddEmptyCommerceCatalog(
			String externalReferenceCode, String commerceCurrencyCode)
		throws PortalException {

		return getService().getOrAddEmptyCommerceCatalog(
			externalReferenceCode, commerceCurrencyCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<CommerceCatalog> search(
			long companyId, String keywords, int start, int end,
			com.liferay.portal.kernel.search.Sort sort)
		throws PortalException {

		return getService().search(companyId, keywords, start, end, sort);
	}

	public static int searchCommerceCatalogsCount(
			long companyId, String keywords)
		throws PortalException {

		return getService().searchCommerceCatalogsCount(companyId, keywords);
	}

	public static CommerceCatalog updateCommerceCatalog(
			long commerceCatalogId, long accountEntryId, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId)
		throws PortalException {

		return getService().updateCommerceCatalog(
			commerceCatalogId, accountEntryId, name, commerceCurrencyCode,
			catalogDefaultLanguageId);
	}

	public static CommerceCatalog updateCommerceCatalogExternalReferenceCode(
			String externalReferenceCode, long commerceCatalogId)
		throws PortalException {

		return getService().updateCommerceCatalogExternalReferenceCode(
			externalReferenceCode, commerceCatalogId);
	}

	public static CommerceCatalogService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceCatalogService> _serviceSnapshot =
		new Snapshot<>(
			CommerceCatalogServiceUtil.class, CommerceCatalogService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1724127820