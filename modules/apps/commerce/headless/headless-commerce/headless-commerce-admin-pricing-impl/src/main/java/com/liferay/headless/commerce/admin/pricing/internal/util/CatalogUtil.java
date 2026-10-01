/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util;

import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.commerce.product.exception.NoSuchCatalogException;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.headless.commerce.core.util.CommerceCurrencyUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Alessio Antonio Rendina
 */
public class CatalogUtil {

	public static CommerceCatalog getCommerceCatalog(
			long catalogId, String catalogCurrencyCode,
			String catalogCurrencyExternalReferenceCode,
			String catalogExternalReferenceCode, String catalogName,
			CommerceCatalogService commerceCatalogService,
			CommerceCurrencyService commerceCurrencyService,
			ServiceContext serviceContext)
		throws PortalException {

		CommerceCatalog commerceCatalog =
			commerceCatalogService.fetchCommerceCatalogByExternalReferenceCode(
				GetterUtil.getString(catalogExternalReferenceCode),
				serviceContext.getCompanyId());

		if (commerceCatalog != null) {
			return commerceCatalog;
		}

		if ((catalogId > 0) && !LazyReferencingThreadLocal.isEnabled()) {
			commerceCatalog = commerceCatalogService.fetchCommerceCatalog(
				catalogId);

			if ((commerceCatalog != null) &&
				(commerceCatalog.getCompanyId() ==
					serviceContext.getCompanyId())) {

				return commerceCatalog;
			}
		}

		if (!LazyReferencingThreadLocal.isEnabled() ||
			Validator.isNull(catalogExternalReferenceCode)) {

			throw new NoSuchCatalogException(
				"Unable to find catalog with external reference code " +
					catalogExternalReferenceCode);
		}

		CommerceCurrency commerceCurrency = _getCommerceCurrency(
			catalogCurrencyCode, catalogCurrencyExternalReferenceCode,
			commerceCurrencyService, serviceContext);

		return commerceCatalogService.getOrAddEmptyCommerceCatalog(
			catalogExternalReferenceCode, catalogName,
			commerceCurrency.getCode());
	}

	private static CommerceCurrency _getCommerceCurrency(
			String catalogCurrencyCode,
			String catalogCurrencyExternalReferenceCode,
			CommerceCurrencyService commerceCurrencyService,
			ServiceContext serviceContext)
		throws PortalException {

		CommerceCurrency commerceCurrency =
			CommerceCurrencyUtil.fetchCommerceCurrency(
				serviceContext.getCompanyId(), catalogCurrencyCode,
				catalogCurrencyExternalReferenceCode, 0);

		if (commerceCurrency != null) {
			return commerceCurrency;
		}

		if (Validator.isNull(catalogCurrencyExternalReferenceCode)) {
			throw new NoSuchCurrencyException(
				"Unable to find currency with external reference code " +
					catalogCurrencyExternalReferenceCode);
		}

		return commerceCurrencyService.getOrAddEmptyCommerceCurrency(
			catalogCurrencyExternalReferenceCode, catalogCurrencyCode);
	}

}