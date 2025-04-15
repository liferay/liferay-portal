/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.resource.v1_0;

import com.liferay.commerce.exception.NoSuchOrderTypeException;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.commerce.order.rule.service.COREntryRelService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.term.model.CommerceTermEntryRel;
import com.liferay.commerce.term.service.CommerceTermEntryRelService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleOrderType;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderType;
import com.liferay.headless.commerce.admin.order.dto.v1_0.TermOrderType;
import com.liferay.headless.commerce.admin.order.internal.odata.entity.v1_0.OrderTypeEntityModel;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderTypeResource;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.headless.commerce.core.util.ExpandoUtil;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/order-type.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = OrderTypeResource.class
)
public class OrderTypeResourceImpl extends BaseOrderTypeResourceImpl {

	@Override
	public void deleteOrderType(Long id) throws Exception {
		_commerceOrderTypeService.deleteCommerceOrderType(id);
	}

	@Override
	public void deleteOrderTypeByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.
				fetchCommerceOrderTypeByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrderType == null) {
			throw new NoSuchOrderTypeException(
				"Unable to find order type with external reference code " +
					externalReferenceCode);
		}

		_commerceOrderTypeService.deleteCommerceOrderType(
			commerceOrderType.getCommerceOrderTypeId());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@NestedField(parentClass = OrderRuleOrderType.class, value = "orderType")
	@Override
	public OrderType getOrderRuleOrderTypeOrderType(Long id) throws Exception {
		COREntryRel corEntryRel = _corEntryRelService.getCOREntryRel(id);

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.getCommerceOrderType(
				corEntryRel.getClassPK());

		return _toOrderType(commerceOrderType.getCommerceOrderTypeId());
	}

	@Override
	public OrderType getOrderType(Long id) throws Exception {
		return _toOrderType(GetterUtil.getLong(id));
	}

	@Override
	public OrderType getOrderTypeByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.
				fetchCommerceOrderTypeByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrderType == null) {
			throw new NoSuchOrderTypeException(
				"Unable to find order type with external reference code " +
					externalReferenceCode);
		}

		return _toOrderType(commerceOrderType.getCommerceOrderTypeId());
	}

	@Override
	public Page<OrderType> getOrderTypesPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			null, booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CommerceOrderType.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					"status", WorkflowConstants.STATUS_ANY);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toOrderType(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@NestedField(parentClass = TermOrderType.class, value = "orderType")
	@Override
	public OrderType getTermOrderTypeOrderType(Long id) throws Exception {
		CommerceTermEntryRel commerceTermEntryRel =
			_commerceTermEntryRelService.getCommerceTermEntryRel(id);

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.getCommerceOrderType(
				commerceTermEntryRel.getClassPK());

		return _toOrderType(commerceOrderType.getCommerceOrderTypeId());
	}

	@Override
	public OrderType patchOrderType(Long id, OrderType orderType)
		throws Exception {

		return _toOrderType(
			_updateOrderType(
				_commerceOrderTypeService.getCommerceOrderType(id), orderType));
	}

	@Override
	public OrderType patchOrderTypeByExternalReferenceCode(
			String externalReferenceCode, OrderType orderType)
		throws Exception {

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.
				fetchCommerceOrderTypeByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrderType == null) {
			throw new NoSuchOrderTypeException(
				"Unable to find order type with external reference code " +
					externalReferenceCode);
		}

		return _toOrderType(_updateOrderType(commerceOrderType, orderType));
	}

	@Override
	public OrderType postOrderType(OrderType orderType) throws Exception {
		CommerceOrderType commerceOrderType = _addCommerceOrderType(
			orderType.getExternalReferenceCode(), orderType);

		return _toOrderType(commerceOrderType.getCommerceOrderTypeId());
	}

	@Override
	public OrderType putOrderTypeByExternalReferenceCode(
			String externalReferenceCode, OrderType orderType)
		throws Exception {

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.
				fetchCommerceOrderTypeByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrderType == null) {
			commerceOrderType = _addCommerceOrderType(
				externalReferenceCode, orderType);

			return _toOrderType(commerceOrderType.getCommerceOrderTypeId());
		}

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			orderType.getDisplayDate(), serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			orderType.getExpirationDate(), serviceContext.getTimeZone());

		commerceOrderType = _commerceOrderTypeService.updateCommerceOrderType(
			GetterUtil.getString(orderType.getExternalReferenceCode()),
			commerceOrderType.getCommerceOrderTypeId(),
			LanguageUtils.getLocalizedMap(orderType.getName()),
			LanguageUtils.getLocalizedMap(orderType.getDescription()),
			GetterUtil.getBoolean(orderType.getActive()),
			displayDateConfig.getMonth(), displayDateConfig.getDay(),
			displayDateConfig.getYear(), displayDateConfig.getHour(),
			displayDateConfig.getMinute(),
			GetterUtil.getInteger(orderType.getDisplayOrder()),
			expirationDateConfig.getMonth(), expirationDateConfig.getDay(),
			expirationDateConfig.getYear(), expirationDateConfig.getHour(),
			expirationDateConfig.getMinute(),
			GetterUtil.getBoolean(orderType.getNeverExpire()), serviceContext);

		Map<String, ?> customFields = orderType.getCustomFields();

		if ((customFields != null) && !customFields.isEmpty()) {
			ExpandoUtil.updateExpando(
				serviceContext.getCompanyId(), CommerceOrderType.class,
				commerceOrderType.getPrimaryKey(), customFields);
		}

		return _toOrderType(commerceOrderType);
	}

	private CommerceOrderType _addCommerceOrderType(
			String externalReferenceCode, OrderType orderType)
		throws Exception {

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			orderType.getDisplayDate(), serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			orderType.getExpirationDate(), serviceContext.getTimeZone());

		return _commerceOrderTypeService.addCommerceOrderType(
			externalReferenceCode,
			LanguageUtils.getLocalizedMap(orderType.getName()),
			LanguageUtils.getLocalizedMap(orderType.getDescription()),
			GetterUtil.getBoolean(orderType.getActive()),
			displayDateConfig.getMonth(), displayDateConfig.getDay(),
			displayDateConfig.getYear(), displayDateConfig.getHour(),
			displayDateConfig.getMinute(),
			GetterUtil.getInteger(orderType.getDisplayOrder()),
			expirationDateConfig.getMonth(), expirationDateConfig.getDay(),
			expirationDateConfig.getYear(), expirationDateConfig.getHour(),
			expirationDateConfig.getMinute(),
			GetterUtil.getBoolean(orderType.getNeverExpire(), true),
			serviceContext);
	}

	private Map<String, Map<String, String>> _getActions(
			CommerceOrderType commerceOrderType)
		throws Exception {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			addAction(
				"DELETE", commerceOrderType.getCommerceOrderTypeId(),
				"deleteOrderType", _commerceOrderTypeModelResourcePermission)
		).put(
			"get",
			addAction(
				"VIEW", commerceOrderType.getCommerceOrderTypeId(),
				"getOrderType", _commerceOrderTypeModelResourcePermission)
		).put(
			"permissions",
			addAction(
				"PERMISSIONS", commerceOrderType.getCommerceOrderTypeId(),
				"patchOrderType", _commerceOrderTypeModelResourcePermission)
		).put(
			"update",
			addAction(
				"UPDATE", commerceOrderType.getCommerceOrderTypeId(),
				"patchOrderType", _commerceOrderTypeModelResourcePermission)
		).build();
	}

	private OrderType _toOrderType(CommerceOrderType commerceOrderType)
		throws Exception {

		return _toOrderType(commerceOrderType.getCommerceOrderTypeId());
	}

	private OrderType _toOrderType(Long commerceOrderTypeId) throws Exception {
		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.getCommerceOrderType(commerceOrderTypeId);

		return _orderTypeDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(commerceOrderType), _dtoConverterRegistry,
				commerceOrderTypeId, contextAcceptLanguage.getPreferredLocale(),
				contextUriInfo, contextUser));
	}

	private CommerceOrderType _updateOrderType(
			CommerceOrderType commerceOrderType, OrderType orderType)
		throws Exception {

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			orderType.getDisplayDate(), serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			orderType.getExpirationDate(), serviceContext.getTimeZone());

		commerceOrderType = _commerceOrderTypeService.updateCommerceOrderType(
			GetterUtil.getString(
				orderType.getExternalReferenceCode(),
				commerceOrderType.getExternalReferenceCode()),
			commerceOrderType.getCommerceOrderTypeId(),
			LanguageUtils.getLocalizedMap(orderType.getName()),
			LanguageUtils.getLocalizedMap(orderType.getDescription()),
			GetterUtil.get(orderType.getActive(), commerceOrderType.isActive()),
			displayDateConfig.getMonth(), displayDateConfig.getDay(),
			displayDateConfig.getYear(), displayDateConfig.getHour(),
			displayDateConfig.getMinute(),
			GetterUtil.getInteger(
				orderType.getDisplayOrder(),
				commerceOrderType.getDisplayOrder()),
			expirationDateConfig.getMonth(), expirationDateConfig.getDay(),
			expirationDateConfig.getYear(), expirationDateConfig.getHour(),
			expirationDateConfig.getMinute(),
			GetterUtil.getBoolean(orderType.getNeverExpire(), true),
			serviceContext);

		Map<String, ?> customFields = orderType.getCustomFields();

		if ((customFields != null) && !customFields.isEmpty()) {
			ExpandoUtil.updateExpando(
				serviceContext.getCompanyId(), CommerceOrderType.class,
				commerceOrderType.getPrimaryKey(), customFields);
		}

		return commerceOrderType;
	}

	private static final EntityModel _entityModel = new OrderTypeEntityModel();

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrderType)"
	)
	private ModelResourcePermission<CommerceOrderType>
		_commerceOrderTypeModelResourcePermission;

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private CommerceTermEntryRelService _commerceTermEntryRelService;

	@Reference
	private COREntryRelService _corEntryRelService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter.OrderTypeDTOConverter)"
	)
	private DTOConverter<CommerceOrderType, OrderType> _orderTypeDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}