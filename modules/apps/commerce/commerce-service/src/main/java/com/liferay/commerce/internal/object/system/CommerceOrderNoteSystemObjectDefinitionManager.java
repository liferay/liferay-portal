/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.system;

import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderNote;
import com.liferay.commerce.model.CommerceOrderNoteTable;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceOrderNoteLocalService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderNote;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderNoteResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.BooleanObjectFieldBuilder;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.system.BaseSystemObjectDefinitionManager;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fabio Monaco
 */
@Component(service = SystemObjectDefinitionManager.class)
public class CommerceOrderNoteSystemObjectDefinitionManager
	extends BaseSystemObjectDefinitionManager {

	@Override
	public long addBaseModel(
			boolean checkPermissions, User user, Map<String, Object> values)
		throws Exception {

		OrderNoteResource orderNoteResource = _buildOrderNoteResource(
			checkPermissions, user);

		OrderNote orderNote = orderNoteResource.postOrderIdOrderNote(
			GetterUtil.getLong(values.get("orderId")), _toOrderNote(values));

		setExtendedProperties(
			OrderNote.class.getName(), orderNote, user, values);

		return orderNote.getId();
	}

	@Override
	public BaseModel<?> deleteBaseModel(BaseModel<?> baseModel)
		throws PortalException {

		return _commerceOrderNoteLocalService.deleteCommerceOrderNote(
			(CommerceOrderNote)baseModel);
	}

	@Override
	public BaseModel<?> fetchBaseModelByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _commerceOrderNoteLocalService.
			fetchCommerceOrderNoteByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public BaseModel<?> getBaseModelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return _commerceOrderNoteLocalService.
			getCommerceOrderNoteByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public String getExternalReferenceCode() {
		return "L_COMMERCE_ORDER_NOTE";
	}

	@Override
	public JaxRsApplicationDescriptor getJaxRsApplicationDescriptor() {
		return new JaxRsApplicationDescriptor(
			"Liferay.Headless.Commerce.Admin.Order",
			"headless-commerce-admin-order", "orderNote", "v1.0");
	}

	@Override
	public Map<String, String> getLabelKeys() {
		return HashMapBuilder.put(
			"label", "commerce-order-note"
		).put(
			"pluralLabel", "commerce-order-notes"
		).build();
	}

	@Override
	public Class<?> getModelClass() {
		return CommerceOrderNote.class;
	}

	@Override
	public List<ObjectField> getObjectFields() {
		return Arrays.asList(
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("content")
			).name(
				"content"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("order-external-reference-code")
			).name(
				"orderExternalReferenceCode"
			).required(
				true
			).system(
				true
			).build(),
			new LongIntegerObjectFieldBuilder(
			).labelMap(
				createLabelMap("order-id")
			).name(
				"orderId"
			).required(
				true
			).system(
				true
			).build(),
			new BooleanObjectFieldBuilder(
			).labelMap(
				createLabelMap("restricted")
			).name(
				"restricted"
			).system(
				true
			).build());
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return CommerceOrderNoteTable.INSTANCE.commerceOrderNoteId;
	}

	@Override
	public String getScope() {
		return ObjectDefinitionConstants.SCOPE_COMPANY;
	}

	@Override
	public Table getTable() {
		return CommerceOrderNoteTable.INSTANCE;
	}

	@Override
	public Map<String, Object> getVariables(
		String contentType, ObjectDefinition objectDefinition,
		boolean oldValues, JSONObject payloadJSONObject) {

		Map<String, Object> variables = super.getVariables(
			contentType, objectDefinition, oldValues, payloadJSONObject);

		Object commerceOrderId = variables.get("commerceOrderId");

		if (commerceOrderId != null) {
			CommerceOrder commerceOrder =
				_commerceOrderLocalService.fetchCommerceOrder(
					GetterUtil.getLong(commerceOrderId));

			if (commerceOrder == null) {
				return variables;
			}

			variables.put(
				"orderExternalReferenceCode",
				commerceOrder.getExternalReferenceCode());
			variables.put("orderId", commerceOrder.getCommerceOrderId());
		}

		return variables;
	}

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public boolean hasModelResourcePermission(
			long objectDefinitionId, PermissionChecker permissionChecker,
			long primaryKey, String actionId)
		throws PortalException {

		CommerceOrderNote commerceOrderNote =
			_commerceOrderNoteLocalService.getCommerceOrderNote(primaryKey);

		if (_commerceOrderModelResourcePermission.contains(
				permissionChecker, commerceOrderNote.getCommerceOrderId(),
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_NOTES) ||
			_commerceOrderModelResourcePermission.contains(
				permissionChecker, commerceOrderNote.getCommerceOrderId(),
				CommerceOrderActionKeys.
					MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES)) {

			return true;
		}

		return _commerceOrderModelResourcePermission.contains(
			permissionChecker, commerceOrderNote.getCommerceOrderId(),
			actionId);
	}

	@Override
	public void updateBaseModel(
			long primaryKey, User user, Map<String, Object> values)
		throws Exception {

		OrderNoteResource orderNoteResource = _buildOrderNoteResource(
			true, user);

		orderNoteResource.patchOrderNote(primaryKey, _toOrderNote(values));

		setExtendedProperties(
			OrderNote.class.getName(), JSONUtil.put("id", primaryKey), user,
			values);
	}

	private OrderNoteResource _buildOrderNoteResource(
		boolean checkPermissions, User user) {

		OrderNoteResource.Builder builder = _orderNoteResourceFactory.create();

		return builder.checkPermissions(
			checkPermissions
		).preferredLocale(
			user.getLocale()
		).user(
			user
		).build();
	}

	private OrderNote _toOrderNote(Map<String, Object> values) {
		return new OrderNote() {
			{
				setAuthor(() -> GetterUtil.getString(values.get("author")));
				setContent(() -> GetterUtil.getString(values.get("content")));
				setExternalReferenceCode(
					() -> GetterUtil.getString(
						values.get("externalReferenceCode")));
				setId(() -> GetterUtil.getLong(values.get("orderNoteId")));
				setOrderExternalReferenceCode(
					() -> GetterUtil.getString(
						values.get("orderExternalReferenceCode")));
				setOrderId(() -> GetterUtil.getLong(values.get("orderId")));
				setRestricted(
					() -> GetterUtil.getBoolean(values.get("restricted")));
			}
		};
	}

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private CommerceOrderNoteLocalService _commerceOrderNoteLocalService;

	@Reference
	private OrderNoteResource.Factory _orderNoteResourceFactory;

}