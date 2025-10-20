/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.system;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderTable;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.IntegerObjectFieldBuilder;
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
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
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
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@Component(service = SystemObjectDefinitionManager.class)
public class CommerceOrderSystemObjectDefinitionManager
	extends BaseSystemObjectDefinitionManager {

	@Override
	public long addBaseModel(User user, Map<String, Object> values)
		throws Exception {

		OrderResource orderResource = _buildOrderResource(false, user);

		Order order = orderResource.postOrder(_toOrder(values));

		setExtendedProperties(Order.class.getName(), order, user, values);

		return order.getId();
	}

	@Override
	public BaseModel<?> deleteBaseModel(BaseModel<?> baseModel)
		throws PortalException {

		return _commerceOrderLocalService.deleteCommerceOrder(
			(CommerceOrder)baseModel);
	}

	@Override
	public BaseModel<?> fetchBaseModelByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _commerceOrderLocalService.
			fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public BaseModel<?> getBaseModelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return _commerceOrderLocalService.
			getCommerceOrderByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public String getBaseModelExternalReferenceCode(long primaryKey)
		throws PortalException {

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(primaryKey);

		return commerceOrder.getExternalReferenceCode();
	}

	@Override
	public String getExternalReferenceCode() {
		return "L_COMMERCE_ORDER";
	}

	@Override
	public JaxRsApplicationDescriptor getJaxRsApplicationDescriptor() {
		return new JaxRsApplicationDescriptor(
			"Liferay.Headless.Commerce.Admin.Order",
			"headless-commerce-admin-order", "orders", "v1.0");
	}

	@Override
	public Map<String, String> getLabelKeys() {
		return HashMapBuilder.put(
			"label", "commerce-order"
		).put(
			"pluralLabel", "commerce-orders"
		).build();
	}

	@Override
	public Class<?> getModelClass() {
		return CommerceOrder.class;
	}

	@Override
	public List<ObjectField> getObjectFields() {
		return Arrays.asList(
			new LongIntegerObjectFieldBuilder(
			).labelMap(
				createLabelMap("account-id")
			).name(
				"accountId"
			).required(
				true
			).system(
				true
			).build(),
			new LongIntegerObjectFieldBuilder(
			).labelMap(
				createLabelMap("channel-id")
			).name(
				"channelId"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("currency-code")
			).name(
				"currencyCode"
			).required(
				true
			).system(
				true
			).build(),
			new DateObjectFieldBuilder(
			).labelMap(
				createLabelMap("order-date")
			).name(
				"orderDate"
			).system(
				true
			).build(),
			new IntegerObjectFieldBuilder(
			).labelMap(
				createLabelMap("order-status")
			).name(
				"orderStatus"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).dbColumnName(
				"orderTypeExternalReferenceCode"
			).labelMap(
				createLabelMap("order-type-external-reference-code")
			).name(
				"orderTypeExternalReferenceCode"
			).system(
				true
			).build(),
			new LongIntegerObjectFieldBuilder(
			).dbColumnName(
				"orderTypeId"
			).labelMap(
				createLabelMap("order-type-id")
			).name(
				"orderTypeId"
			).system(
				true
			).build(),
			new IntegerObjectFieldBuilder(
			).labelMap(
				createLabelMap("payment-status")
			).name(
				"paymentStatus"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("payment-method-key")
			).name(
				"paymentMethodKey"
			).system(
				true
			).build(),
			new PrecisionDecimalObjectFieldBuilder(
			).labelMap(
				createLabelMap("shipping-amount")
			).name(
				"shippingAmount"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("formatted-shipping-amount")
			).name(
				"shippingAmountFormatted"
			).readOnly(
				"true"
			).build(),
			new PrecisionDecimalObjectFieldBuilder(
			).labelMap(
				createLabelMap("tax-amount")
			).name(
				"taxAmount"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("formatted-tax-amount")
			).name(
				"taxAmountFormatted"
			).readOnly(
				"true"
			).build(),
			new PrecisionDecimalObjectFieldBuilder(
			).labelMap(
				createLabelMap("total")
			).name(
				"total"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("formatted-total-amount")
			).name(
				"totalFormatted"
			).readOnly(
				"true"
			).build());
	}

	@Override
	public Page<?> getPage(
			User user, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		OrderResource orderResource = _buildOrderResource(true, user);

		return orderResource.getOrdersPage(search, filter, pagination, sorts);
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return CommerceOrderTable.INSTANCE.commerceOrderId;
	}

	@Override
	public String getScope() {
		return ObjectDefinitionConstants.SCOPE_COMPANY;
	}

	@Override
	public Table getTable() {
		return CommerceOrderTable.INSTANCE;
	}

	@Override
	public int getVersion() {
		return 6;
	}

	@Override
	public void updateBaseModel(
			long primaryKey, User user, Map<String, Object> values)
		throws Exception {

		OrderResource orderResource = _buildOrderResource(false, user);

		orderResource.patchOrder(primaryKey, _toOrder(values));

		setExtendedProperties(
			Order.class.getName(), JSONUtil.put("id", primaryKey), user,
			values);
	}

	private OrderResource _buildOrderResource(
		boolean checkPermissions, User user) {

		OrderResource.Builder builder = _orderResourceFactory.create();

		return builder.checkPermissions(
			checkPermissions
		).preferredLocale(
			user.getLocale()
		).user(
			user
		).build();
	}

	private Order _toOrder(Map<String, Object> values) {
		return new Order() {
			{
				setAccountId(() -> GetterUtil.getLong(values.get("accountId")));
				setChannelId(() -> GetterUtil.getLong(values.get("channelId")));
				setCurrencyCode(
					() -> GetterUtil.getString(values.get("currencyCode")));
				setExternalReferenceCode(
					() -> GetterUtil.getString(
						values.get("externalReferenceCode")));
				setOrderDate(
					() -> GetterUtil.getDate(
						values.get("orderDate"),
						DateFormatFactoryUtil.getSimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss'Z'")));
				setOrderStatus(
					() -> GetterUtil.getInteger(values.get("orderStatus")));
				setOrderTypeExternalReferenceCode(
					() -> GetterUtil.getString(
						values.get("orderTypeExternalReferenceCode")));
				setOrderTypeId(
					() -> GetterUtil.getLong(values.get("orderTypeId")));
				setPaymentMethod(
					() -> GetterUtil.getString(values.get("paymentMethodKey")));
				setPaymentStatus(
					() -> GetterUtil.getInteger(values.get("paymentStatus")));
				setShippingAmount(
					() -> {
						String shippingAmountString = GetterUtil.getString(
							values.get("shippingAmount"));

						if (Validator.isNull(shippingAmountString)) {
							return null;
						}

						return new BigDecimal(shippingAmountString);
					});
				setShippingAmountFormatted(
					() -> GetterUtil.getString(
						values.get("shippingAmountFormatted")));
				setTaxAmount(
					() -> {
						String taxAmountString = GetterUtil.getString(
							values.get("taxAmount"));

						if (Validator.isNull(taxAmountString)) {
							return null;
						}

						return new BigDecimal(taxAmountString);
					});
				setTaxAmountFormatted(
					() -> GetterUtil.getString(
						values.get("taxAmountFormatted")));
				setTotal(
					() -> {
						String totalString = GetterUtil.getString(
							values.get("total"));

						if (Validator.isNull(totalString)) {
							return null;
						}

						return new BigDecimal(totalString);
					});
				setTotalFormatted(
					() -> GetterUtil.getString(values.get("totalFormatted")));
			}
		};
	}

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private OrderResource.Factory _orderResourceFactory;

}