/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info.collection.provider;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.catalog.CPQuery;
import com.liferay.commerce.product.configuration.CPDefinitionLinkTypeConfiguration;
import com.liferay.commerce.product.helper.CPDefinitionHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.util.CPDefinitionLinkSearchUtil;
import com.liferay.commerce.util.CommerceContextThreadLocal;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.info.sort.Sort;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 * @author Alessio Antonio Rendina
 */
@Component(
	configurationPid = "com.liferay.commerce.product.configuration.CPDefinitionLinkTypeConfiguration",
	service = RelatedInfoItemCollectionProvider.class
)
public class CPDefinitionLinkInfoItemCollectionProvider
	implements RelatedInfoItemCollectionProvider<CPDefinition, CPDefinition> {

	@Override
	public InfoPage<CPDefinition> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		Object relatedItem = collectionQuery.getRelatedItem();
		Pagination pagination = collectionQuery.getPagination();

		if (!(relatedItem instanceof CPDefinition)) {
			return InfoPage.of(Collections.emptyList(), pagination, 0);
		}

		try {
			AccountEntry accountEntry = null;

			CommerceContext commerceContext = CommerceContextThreadLocal.get();

			if (commerceContext != null) {
				accountEntry = commerceContext.getAccountEntry();
			}

			CPDefinition cpDefinition = (CPDefinition)relatedItem;

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			SearchContext searchContext =
				CPDefinitionLinkSearchUtil.getCPDefinitionLinkSearchContext(
					accountEntry, _accountGroupLocalService,
					serviceContext.getCompanyId(),
					cpDefinition.getCPDefinitionId(),
					_cpDefinitionLinkTypeConfiguration.type());

			CPQuery cpQuery = _setOrder(
				new CPQuery(), collectionQuery.getSort());

			return InfoPage.of(
				_cpDefinitionHelper.searchCPDefinitions(
					serviceContext.getScopeGroupId(), searchContext, cpQuery,
					pagination.getStart(), pagination.getEnd()),
				pagination,
				(int)_cpDefinitionHelper.searchCount(
					serviceContext.getScopeGroupId(), searchContext, cpQuery));
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	@Override
	public String getKey() {
		return CPDefinitionLinkInfoItemCollectionProvider.class.getName() +
			StringPool.UNDERLINE + _cpDefinitionLinkTypeConfiguration.type();
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, _cpDefinitionLinkTypeConfiguration.type());
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_cpDefinitionLinkTypeConfiguration =
			ConfigurableUtil.createConfigurable(
				CPDefinitionLinkTypeConfiguration.class, properties);
	}

	private CPQuery _setOrder(CPQuery cpQuery, Sort sort) {
		if (sort == null) {
			return cpQuery;
		}

		cpQuery.setOrderByCol1(sort.getFieldName());

		if (sort.isReverse()) {
			cpQuery.setOrderByType1("DESC");
		}

		return cpQuery;
	}

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	private volatile CPDefinitionLinkTypeConfiguration
		_cpDefinitionLinkTypeConfiguration;

	@Reference
	private Language _language;

}