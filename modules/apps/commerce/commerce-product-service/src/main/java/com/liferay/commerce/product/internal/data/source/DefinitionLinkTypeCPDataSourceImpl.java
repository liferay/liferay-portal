/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.data.source;

import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPQuery;
import com.liferay.commerce.product.configuration.CPDefinitionLinkTypeConfiguration;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.data.source.CPDataSource;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.product.util.CPDefinitionLinkSearchUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	configurationPid = "com.liferay.commerce.product.configuration.CPDefinitionLinkTypeConfiguration",
	property = "commerce.product.data.source.name=definitionLinkDataSource",
	service = CPDataSource.class
)
public class DefinitionLinkTypeCPDataSourceImpl implements CPDataSource {

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "product-relations") + StringPool.SPACE +
			_cpDefinitionLinkTypeConfiguration.type();
	}

	@Override
	public String getName() {
		return _cpDefinitionLinkTypeConfiguration.type();
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

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		SearchContext searchContext =
			CPDefinitionLinkSearchUtil.getCPDefinitionLinkSearchContext(
				commerceContext.getAccountEntry(), _accountGroupLocalService,
				_portal.getCompanyId(httpServletRequest),
				cpCatalogEntry.getCPDefinitionId(),
				_cpDefinitionLinkTypeConfiguration.type());

		return _cpDefinitionHelper.search(
			_portal.getScopeGroupId(httpServletRequest), searchContext,
			new CPQuery(), start, end);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_cpDefinitionLinkTypeConfiguration =
			ConfigurableUtil.createConfigurable(
				CPDefinitionLinkTypeConfiguration.class, properties);
	}

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	private volatile CPDefinitionLinkTypeConfiguration
		_cpDefinitionLinkTypeConfiguration;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}