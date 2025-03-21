/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.account.constants.AccountConstants;
import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.exception.NoSuchCatalogException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Catalog;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.internal.odata.entity.v1_0.CatalogEntityModel;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.CatalogResource;
import com.liferay.headless.commerce.core.util.CommerceCurrencyUtil;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/catalog.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = CatalogResource.class
)
@CTAware
public class CatalogResourceImpl extends BaseCatalogResourceImpl {

	@Override
	public Response deleteCatalog(Long id) throws Exception {
		_commerceCatalogService.deleteCommerceCatalog(id);

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public Response deleteCatalogByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceCatalog commerceCatalog =
			_commerceCatalogService.fetchCommerceCatalogByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceCatalog == null) {
			throw new NoSuchCatalogException(
				"Unable to find catalog with external reference code " +
					externalReferenceCode);
		}

		_commerceCatalogService.deleteCommerceCatalog(
			commerceCatalog.getCommerceCatalogId());

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public Catalog getCatalog(Long id) throws Exception {
		return _toCatalog(_commerceCatalogService.getCommerceCatalog(id));
	}

	@Override
	public Catalog getCatalogByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceCatalog commerceCatalog =
			_commerceCatalogService.fetchCommerceCatalogByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceCatalog == null) {
			throw new NoSuchCatalogException(
				"Unable to find catalog with external reference code " +
					externalReferenceCode);
		}

		return _toCatalog(commerceCatalog);
	}

	@Override
	public Page<Catalog> getCatalogsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CommerceCatalog.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toCatalog(
				_commerceCatalogService.getCommerceCatalog(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public Catalog getProductByExternalReferenceCodeCatalog(
			String externalReferenceCode, Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		return _toCatalog(cpDefinition.getCommerceCatalog());
	}

	@NestedField(parentClass = Product.class, value = "catalog")
	@Override
	public Catalog getProductIdCatalog(
			@NestedFieldId(value = "productId") Long id, Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _toCatalog(cpDefinition.getCommerceCatalog());
	}

	@Override
	public Response patchCatalog(Long id, Catalog catalog) throws Exception {
		_updateCommerceCatalog(
			catalog, _commerceCatalogService.getCommerceCatalog(id));

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchCatalogByExternalReferenceCode(
			String externalReferenceCode, Catalog catalog)
		throws Exception {

		CommerceCatalog commerceCatalog =
			_commerceCatalogService.fetchCommerceCatalogByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceCatalog == null) {
			throw new NoSuchCatalogException(
				"Unable to find catalog with external reference code " +
					externalReferenceCode);
		}

		_updateCommerceCatalog(catalog, commerceCatalog);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Catalog postCatalog(Catalog catalog) throws Exception {
		CommerceCatalog commerceCatalog =
			_commerceCatalogService.fetchCommerceCatalogByExternalReferenceCode(
				catalog.getExternalReferenceCode(),
				contextCompany.getCompanyId());

		if (commerceCatalog == null) {
			CommerceCurrency commerceCurrency =
				CommerceCurrencyUtil.getCommerceCurrency(
					contextCompany.getCompanyId(), catalog.getCurrencyCode(),
					catalog.getCurrencyExternalReferenceCode(),
					GetterUtil.getLong(catalog.getCurrencyId()));

			commerceCatalog = _commerceCatalogService.addCommerceCatalog(
				catalog.getExternalReferenceCode(),
				GetterUtil.get(
					catalog.getAccountId(),
					AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT),
				catalog.getName(), commerceCurrency.getCode(),
				catalog.getDefaultLanguageId(),
				_serviceContextHelper.getServiceContext());
		}
		else {
			CommerceCurrency commerceCurrency =
				_commerceCurrencyLocalService.getCommerceCurrency(
					contextCompany.getCompanyId(),
					commerceCatalog.getCommerceCurrencyCode());

			try {
				commerceCurrency = CommerceCurrencyUtil.getCommerceCurrency(
					contextCompany.getCompanyId(), catalog.getCurrencyCode(),
					catalog.getCurrencyExternalReferenceCode(),
					GetterUtil.getLong(catalog.getCurrencyId()));
			}
			catch (NoSuchCurrencyException noSuchCurrencyException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchCurrencyException);
				}
			}

			commerceCatalog = _commerceCatalogService.updateCommerceCatalog(
				commerceCatalog.getCommerceCatalogId(),
				GetterUtil.get(
					catalog.getAccountId(),
					commerceCatalog.getAccountEntryId()),
				GetterUtil.get(catalog.getName(), commerceCatalog.getName()),
				commerceCurrency.getCode(),
				GetterUtil.get(
					catalog.getDefaultLanguageId(),
					commerceCatalog.getCatalogDefaultLanguageId()));
		}

		return _toCatalog(commerceCatalog);
	}

	@Override
	public Catalog putCatalogByExternalReferenceCode(
			String externalReferenceCode, Catalog catalog)
		throws Exception {

		CommerceCatalog commerceCatalog =
			_commerceCatalogService.fetchCommerceCatalogByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		CommerceCurrency commerceCurrency =
			CommerceCurrencyUtil.getCommerceCurrency(
				contextCompany.getCompanyId(), catalog.getCurrencyCode(),
				catalog.getCurrencyExternalReferenceCode(),
				GetterUtil.getLong(catalog.getCurrencyId()));

		if (commerceCatalog == null) {
			commerceCatalog = _commerceCatalogService.addCommerceCatalog(
				catalog.getExternalReferenceCode(),
				GetterUtil.get(
					catalog.getAccountId(),
					AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT),
				catalog.getName(), commerceCurrency.getCode(),
				catalog.getDefaultLanguageId(),
				_serviceContextHelper.getServiceContext());
		}
		else {
			commerceCatalog = _commerceCatalogService.updateCommerceCatalog(
				commerceCatalog.getCommerceCatalogId(),
				GetterUtil.getLong(catalog.getAccountId()),
				GetterUtil.getString(catalog.getName()),
				commerceCurrency.getCode(),
				GetterUtil.getString(catalog.getDefaultLanguageId()));
		}

		return _toCatalog(commerceCatalog);
	}

	private Map<String, Map<String, String>> _getActions(
		CommerceCatalog commerceCatalog) {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			addAction(
				"DELETE", commerceCatalog.getCommerceCatalogId(),
				"deleteCatalog", commerceCatalog.getUserId(),
				"com.liferay.commerce.product.model.CommerceCatalog",
				commerceCatalog.getGroupId())
		).put(
			"get",
			addAction(
				"VIEW", commerceCatalog.getCommerceCatalogId(), "getCatalog",
				commerceCatalog.getUserId(),
				"com.liferay.commerce.product.model.CommerceCatalog",
				commerceCatalog.getGroupId())
		).put(
			"update",
			addAction(
				"UPDATE", commerceCatalog.getCommerceCatalogId(),
				"patchCatalog", commerceCatalog.getUserId(),
				"com.liferay.commerce.product.model.CommerceCatalog",
				commerceCatalog.getGroupId())
		).build();
	}

	private Catalog _toCatalog(CommerceCatalog commerceCatalog)
		throws Exception {

		return _catalogDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(commerceCatalog), _dtoConverterRegistry,
				commerceCatalog.getCommerceCatalogId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private void _updateCommerceCatalog(
			Catalog catalog, CommerceCatalog commerceCatalog)
		throws Exception {

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.getCommerceCurrency(
				contextCompany.getCompanyId(),
				commerceCatalog.getCommerceCurrencyCode());

		try {
			commerceCurrency = CommerceCurrencyUtil.getCommerceCurrency(
				contextCompany.getCompanyId(), catalog.getCurrencyCode(),
				catalog.getCurrencyExternalReferenceCode(),
				GetterUtil.getLong(catalog.getCurrencyId()));
		}
		catch (NoSuchCurrencyException noSuchCurrencyException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchCurrencyException);
			}
		}

		_commerceCatalogService.updateCommerceCatalog(
			commerceCatalog.getCommerceCatalogId(),
			GetterUtil.get(
				catalog.getAccountId(), commerceCatalog.getAccountEntryId()),
			GetterUtil.get(catalog.getName(), commerceCatalog.getName()),
			commerceCurrency.getCode(),
			GetterUtil.get(
				catalog.getDefaultLanguageId(),
				commerceCatalog.getCatalogDefaultLanguageId()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CatalogResourceImpl.class);

	private static final EntityModel _entityModel = new CatalogEntityModel();

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.CatalogDTOConverter)"
	)
	private DTOConverter<CommerceCatalog, Catalog> _catalogDTOConverter;

	@Reference
	private CommerceCatalogService _commerceCatalogService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}