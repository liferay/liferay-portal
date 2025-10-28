/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.system;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceOrderItemTable;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderItemResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.PrecisionDecimalObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.system.BaseSystemObjectDefinitionManager;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(service = SystemObjectDefinitionManager.class)
public class CommerceOrderItemSystemObjectDefinitionManager
	extends BaseSystemObjectDefinitionManager {

	@Override
	public long addBaseModel(
			boolean checkPermissions, User user, Map<String, Object> values)
		throws Exception {

		OrderItemResource orderItemResource = _buildOrderItemResource(
			checkPermissions, user);

		OrderItem orderItem = orderItemResource.postOrderIdOrderItem(
			GetterUtil.getLong(values.get("orderId")), _toOrderItem(values));

		setExtendedProperties(
			OrderItem.class.getName(), orderItem, user, values);

		return orderItem.getId();
	}

	@Override
	public BaseModel<?> deleteBaseModel(BaseModel<?> baseModel)
		throws PortalException {

		return _commerceOrderItemLocalService.deleteCommerceOrderItem(
			(CommerceOrderItem)baseModel);
	}

	@Override
	public BaseModel<?> fetchBaseModelByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _commerceOrderItemLocalService.
			fetchCommerceOrderItemByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public BaseModel<?> getBaseModelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return _commerceOrderItemLocalService.
			getCommerceOrderItemByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public String getBaseModelExternalReferenceCode(long primaryKey)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.getCommerceOrderItem(primaryKey);

		return commerceOrderItem.getExternalReferenceCode();
	}

	@Override
	public String getExternalReferenceCode() {
		return "L_COMMERCE_ORDER_ITEM";
	}

	@Override
	public JaxRsApplicationDescriptor getJaxRsApplicationDescriptor() {
		return new JaxRsApplicationDescriptor(
			"Liferay.Headless.Commerce.Admin.Order",
			"headless-commerce-admin-order", "orderItems", "v1.0");
	}

	@Override
	public Map<String, String> getLabelKeys() {
		return HashMapBuilder.put(
			"label", "commerce-order-item"
		).put(
			"pluralLabel", "commerce-order-items"
		).build();
	}

	@Override
	public Class<?> getModelClass() {
		return CommerceOrderItem.class;
	}

	@Override
	public List<ObjectField> getObjectFields() {
		return Arrays.asList(
			new LongIntegerObjectFieldBuilder(
			).dbColumnName(
				"orderId"
			).labelMap(
				createLabelMap("order-id")
			).name(
				"orderId"
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("sku")
			).name(
				"sku"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("name")
			).name(
				"name"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("unit-of-measure")
			).name(
				"unitOfMeasureKey"
			).required(
				true
			).system(
				true
			).build(),
			new PrecisionDecimalObjectFieldBuilder(
			).labelMap(
				createLabelMap("unit-price")
			).name(
				"unitPrice"
			).required(
				true
			).system(
				true
			).build());
	}

	@Override
	public Page<?> getPage(
			User user, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		OrderItemResource orderItemResource = _buildOrderItemResource(
			true, user);

		return orderItemResource.getOrderItemsPage(
			search, filter, pagination, sorts);
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return CommerceOrderItemTable.INSTANCE.commerceOrderItemId;
	}

	@Override
	public String getScope() {
		return ObjectDefinitionConstants.SCOPE_COMPANY;
	}

	@Override
	public Table getTable() {
		return CommerceOrderItemTable.INSTANCE;
	}

	@Override
	public int getVersion() {
		return 4;
	}

	@Override
	public void updateBaseModel(
			long primaryKey, User user, Map<String, Object> values)
		throws Exception {

		OrderItemResource orderItemResource = _buildOrderItemResource(
			false, user);

		orderItemResource.patchOrderItem(primaryKey, _toOrderItem(values));

		setExtendedProperties(
			OrderItem.class.getName(), JSONUtil.put("id", primaryKey), user,
			values);
	}

	private OrderItemResource _buildOrderItemResource(
		boolean checkPermissions, User user) {

		OrderItemResource.Builder builder = _orderItemResourceFactory.create();

		return builder.checkPermissions(
			checkPermissions
		).preferredLocale(
			user.getLocale()
		).user(
			user
		).build();
	}

	private OrderItem _toOrderItem(Map<String, Object> values) {
		return new OrderItem() {
			{
				setName(() -> getLanguageIdMap("name", values));
				setOrderId(() -> GetterUtil.getLong(values.get("orderId")));
				setSku(() -> GetterUtil.getString(values.get("sku")));
				setUnitOfMeasure(
					() -> GetterUtil.getString(values.get("unitOfMeasureKey")));
				setUnitPrice(
					() -> {
						String unitPriceString = GetterUtil.getString(
							values.get("unitPrice"));

						if (Validator.isNull(unitPriceString)) {
							return null;
						}

						return new BigDecimal(unitPriceString);
					});
			}
		};
	}

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private OrderItemResource.Factory _orderItemResourceFactory;

}