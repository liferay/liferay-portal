/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.data.source;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.data.source.CPDataSource;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryLocalService;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "commerce.product.data.source.name=" + CSDiagramEntryCPDataSourceImpl.NAME,
	service = CPDataSource.class
)
public class CSDiagramEntryCPDataSourceImpl implements CPDataSource {

	public static final String NAME = "csDiagramEntryDataSource";

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "related-diagrams");
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public CPDataSourceResult getResult(
			HttpServletRequest httpServletRequest, int start, int end)
		throws Exception {

		CPCatalogEntry cpCatalogEntry =
			(CPCatalogEntry)httpServletRequest.getAttribute(
				CPWebKeys.CP_CATALOG_ENTRY);

		if (cpCatalogEntry == null) {
			return new CPDataSourceResult(new ArrayList<>(), 0);
		}

		List<CPCatalogEntry> cpCatalogEntries = new ArrayList<>();

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		long commerceAccountId = CommerceUtil.getCommerceAccountId(
			commerceContext);

		List<CSDiagramEntry> csDiagramEntries =
			_csDiagramEntryLocalService.getCPDefinitionRelatedCSDiagramEntries(
				cpCatalogEntry.getCPDefinitionId());

		for (CSDiagramEntry csDiagramEntry : csDiagramEntries) {
			if (_commerceProductViewPermission.contains(
					PermissionThreadLocal.getPermissionChecker(),
					commerceAccountId,
					commerceContext.getCommerceChannelGroupId(),
					csDiagramEntry.getCPDefinitionId())) {

				cpCatalogEntries.add(
					_cpDefinitionHelper.getCPCatalogEntry(
						commerceAccountId,
						commerceContext.getCommerceChannelGroupId(),
						csDiagramEntry.getCPDefinitionId(),
						_portal.getLocale(httpServletRequest)));
			}
		}

		if (cpCatalogEntries.size() > end) {
			cpCatalogEntries = cpCatalogEntries.subList(start, end);
		}

		return new CPDataSourceResult(
			cpCatalogEntries, cpCatalogEntries.size());
	}

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CSDiagramEntryLocalService _csDiagramEntryLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}