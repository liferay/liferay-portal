/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationListService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfiguration;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationList;
import com.liferay.headless.commerce.admin.catalog.internal.odata.entity.v1_0.ProductConfigurationListEntityModel;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductConfigurationListResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductConfigurationResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.custom.field.CustomField;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Zoltán Takács
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/product-configuration-list.properties",
	scope = ServiceScope.PROTOTYPE,
	service = ProductConfigurationListResource.class
)
public class ProductConfigurationListResourceImpl
	extends BaseProductConfigurationListResourceImpl {

	@Override
	public void deleteProductConfigurationList(Long id) throws Exception {
		_cpConfigurationListService.deleteCPConfigurationList(id);
	}

	@Override
	public void deleteProductConfigurationListByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListService.
				getCPConfigurationListByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		deleteProductConfigurationList(
			cpConfigurationList.getCPConfigurationListId());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public ProductConfigurationList getProductConfigurationList(Long id)
		throws Exception {

		return _toProductConfigurationList(id);
	}

	@Override
	public ProductConfigurationList
			getProductConfigurationListByExternalReferenceCode(
				String externalReferenceCode)
		throws Exception {

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListService.
				getCPConfigurationListByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return getProductConfigurationList(
			cpConfigurationList.getCPConfigurationListId());
	}

	@Override
	public Page<ProductConfigurationList> getProductConfigurationListsPage(
			Long catalogId, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			null, booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CPConfigurationList.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());

				if (GetterUtil.getLong(catalogId) > 0) {
					CommerceCatalog commerceCatalog =
						_commerceCatalogLocalService.getCommerceCatalog(
							catalogId);

					searchContext.setGroupIds(
						new long[] {commerceCatalog.getGroupId()});
				}
				else {
					searchContext.setGroupIds(
						transformToLongArray(
							_commerceCatalogLocalService.search(
								contextCompany.getCompanyId()),
							CommerceCatalog::getGroupId));
				}
			},
			sorts,
			document -> _toProductConfigurationList(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public ProductConfigurationList patchProductConfigurationList(
			Long id, ProductConfigurationList productConfigurationList)
		throws Exception {

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListService.getCPConfigurationList(id);

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			cpConfigurationList.getGroupId());

		serviceContext.setExpandoBridgeAttributes(
			_getExpandoBridgeAttributes(
				CPConfigurationList.class.getName(),
				productConfigurationList.getCustomFields()));

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			GetterUtil.getDate(
				productConfigurationList.getDisplayDate(),
				DateFormatFactoryUtil.getDate(
					contextAcceptLanguage.getPreferredLocale(),
					contextUser.getTimeZone()),
				cpConfigurationList.getDisplayDate()),
			serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			GetterUtil.getDate(
				productConfigurationList.getExpirationDate(),
				DateFormatFactoryUtil.getDate(
					contextAcceptLanguage.getPreferredLocale(),
					contextUser.getTimeZone()),
				cpConfigurationList.getExpirationDate()),
			serviceContext.getTimeZone());

		cpConfigurationList =
			_cpConfigurationListService.updateCPConfigurationList(
				GetterUtil.getString(
					productConfigurationList.getExternalReferenceCode(),
					cpConfigurationList.getExternalReferenceCode()),
				cpConfigurationList.getCPConfigurationListId(),
				cpConfigurationList.getGroupId(),
				cpConfigurationList.getParentCPConfigurationListId(),
				cpConfigurationList.isMaster(),
				GetterUtil.getString(
					productConfigurationList.getName(),
					cpConfigurationList.getName()),
				GetterUtil.getDouble(
					productConfigurationList.getPriority(),
					cpConfigurationList.getPriority()),
				displayDateConfig.getMonth(), displayDateConfig.getDay(),
				displayDateConfig.getYear(), displayDateConfig.getHour(),
				displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
				expirationDateConfig.getDay(), expirationDateConfig.getYear(),
				expirationDateConfig.getHour(),
				expirationDateConfig.getMinute(),
				GetterUtil.getBoolean(
					productConfigurationList.getNeverExpire(), true),
				serviceContext);

		ProductConfiguration[] productConfigurations =
			productConfigurationList.getProductConfigurations();

		if ((productConfigurations != null) &&
			ArrayUtil.isNotEmpty(productConfigurations)) {

			_productConfigurationResource.setContextAcceptLanguage(
				contextAcceptLanguage);
			_productConfigurationResource.setContextCompany(contextCompany);
			_productConfigurationResource.setContextUriInfo(contextUriInfo);

			for (ProductConfiguration productConfiguration :
					productConfigurations) {

				_productConfigurationResource.patchProductConfiguration(
					productConfiguration.getId(), productConfiguration);
			}
		}

		return _toProductConfigurationList(cpConfigurationList);
	}

	@Override
	public ProductConfigurationList
			patchProductConfigurationListByExternalReferenceCode(
				String externalReferenceCode,
				ProductConfigurationList productConfigurationList)
		throws Exception {

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListService.
				getCPConfigurationListByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return patchProductConfigurationList(
			cpConfigurationList.getCPConfigurationListId(),
			productConfigurationList);
	}

	@Override
	public ProductConfigurationList postProductConfigurationList(
			ProductConfigurationList productConfigurationList)
		throws Exception {

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.
				fetchCommerceCatalogByExternalReferenceCode(
					GetterUtil.getString(
						productConfigurationList.
							getCatalogExternalReferenceCode()),
					contextCompany.getCompanyId());

		if (commerceCatalog == null) {
			commerceCatalog = _commerceCatalogLocalService.getCommerceCatalog(
				GetterUtil.getLong(productConfigurationList.getCatalogId()));
		}

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			commerceCatalog.getGroupId());

		serviceContext.setExpandoBridgeAttributes(
			_getExpandoBridgeAttributes(
				CPConfigurationList.class.getName(),
				productConfigurationList.getCustomFields()));

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			productConfigurationList.getDisplayDate(),
			serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			productConfigurationList.getExpirationDate(),
			serviceContext.getTimeZone());

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListService.addOrUpdateCPConfigurationList(
				GetterUtil.getString(
					productConfigurationList.getExternalReferenceCode()),
				contextCompany.getCompanyId(), commerceCatalog.getGroupId(),
				GetterUtil.getLong(
					productConfigurationList.
						getParentProductConfigurationListId()),
				GetterUtil.getBoolean(productConfigurationList.getMaster()),
				GetterUtil.getString(productConfigurationList.getName()),
				GetterUtil.getDouble(productConfigurationList.getPriority()),
				displayDateConfig.getMonth(), displayDateConfig.getDay(),
				displayDateConfig.getYear(), displayDateConfig.getHour(),
				displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
				expirationDateConfig.getDay(), expirationDateConfig.getYear(),
				expirationDateConfig.getHour(),
				expirationDateConfig.getMinute(),
				GetterUtil.getBoolean(
					productConfigurationList.getNeverExpire(), true),
				serviceContext);

		ProductConfiguration[] productConfigurations =
			productConfigurationList.getProductConfigurations();

		if ((productConfigurations != null) &&
			ArrayUtil.isNotEmpty(productConfigurations)) {

			_productConfigurationResource.setContextAcceptLanguage(
				contextAcceptLanguage);
			_productConfigurationResource.setContextCompany(contextCompany);
			_productConfigurationResource.setContextUriInfo(contextUriInfo);

			for (ProductConfiguration productConfiguration :
					productConfigurations) {

				_productConfigurationResource.
					postProductConfigurationListIdProductConfiguration(
						cpConfigurationList.getCPConfigurationListId(),
						productConfiguration);
			}
		}

		return _toProductConfigurationList(cpConfigurationList);
	}

	private Map<String, Map<String, String>> _getActions(
		CPConfigurationList cpConfigurationList) {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			() -> {
				if (cpConfigurationList.isMaster()) {
					return null;
				}

				return addAction(
					"UPDATE", cpConfigurationList.getCPConfigurationListId(),
					"deleteProductConfigurationList",
					_cpConfigurationListModelResourcePermission);
			}
		).put(
			"get",
			() -> addAction(
				"VIEW", cpConfigurationList.getCPConfigurationListId(),
				"getProductConfigurationList",
				_cpConfigurationListModelResourcePermission)
		).put(
			"update",
			() -> addAction(
				"UPDATE", cpConfigurationList.getCPConfigurationListId(),
				"patchProductConfigurationList",
				_cpConfigurationListModelResourcePermission)
		).build();
	}

	private Map<String, Serializable> _getExpandoBridgeAttributes(
		String className, CustomField[] customFields) {

		Map<String, Serializable> expandoBridgeAttributes =
			CustomFieldsUtil.toMap(
				className, contextCompany.getCompanyId(), customFields,
				contextAcceptLanguage.getPreferredLocale());

		if (expandoBridgeAttributes == null) {
			expandoBridgeAttributes = new HashMap<>();
		}

		return expandoBridgeAttributes;
	}

	private ProductConfigurationList _toProductConfigurationList(
			CPConfigurationList cpConfigurationList)
		throws Exception {

		return _productConfigurationListDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(cpConfigurationList), _dtoConverterRegistry,
				contextHttpServletRequest,
				cpConfigurationList.getCPConfigurationListId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private ProductConfigurationList _toProductConfigurationList(
			long cpConfigurationListId)
		throws Exception {

		return _toProductConfigurationList(
			_cpConfigurationListService.getCPConfigurationList(
				cpConfigurationListId));
	}

	private static final EntityModel _entityModel =
		new ProductConfigurationListEntityModel();

	@Reference
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPConfigurationList)"
	)
	private ModelResourcePermission<CPConfigurationList>
		_cpConfigurationListModelResourcePermission;

	@Reference
	private CPConfigurationListService _cpConfigurationListService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.ProductConfigurationListDTOConverter)"
	)
	private DTOConverter<CPConfigurationList, ProductConfigurationList>
		_productConfigurationListDTOConverter;

	@Reference
	private ProductConfigurationResource _productConfigurationResource;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}