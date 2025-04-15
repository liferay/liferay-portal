/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.importer.type;

import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.exception.CommerceOrderImporterTypeException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.content.web.internal.importer.type.util.CommerceOrderImporterTypeUtil;
import com.liferay.commerce.order.importer.item.CommerceOrderImporterItem;
import com.liferay.commerce.order.importer.item.CommerceOrderImporterItemImpl;
import com.liferay.commerce.order.importer.type.CommerceOrderImporterType;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.availability.CPAvailabilityChecker;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "commerce.order.importer.type.key=" + CommerceOrdersCommerceOrderImporterTypeImpl.KEY,
	service = CommerceOrderImporterType.class
)
public class CommerceOrdersCommerceOrderImporterTypeImpl
	implements CommerceOrderImporterType {

	public static final String KEY = "orders";

	@Override
	public Object getCommerceOrderImporterItem(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		long selectedCommerceOrderId = ParamUtil.getLong(
			httpServletRequest, getCommerceOrderImporterItemParamName());

		if (selectedCommerceOrderId <= 0) {
			return null;
		}

		return _commerceOrderService.getCommerceOrder(selectedCommerceOrderId);
	}

	@Override
	public String getCommerceOrderImporterItemParamName() {
		return "selectedCommerceOrderId";
	}

	@Override
	public List<CommerceOrderImporterItem> getCommerceOrderImporterItems(
			CommerceOrder commerceOrder, FDSPagination fdsPagination,
			Object object)
		throws Exception {

		if ((object == null) || !(object instanceof CommerceOrder)) {
			throw new CommerceOrderImporterTypeException();
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());
		CommerceOrder selectedCommerceOrder = (CommerceOrder)object;

		return CommerceOrderImporterTypeUtil.getCommerceOrderImporterItems(
			_commerceContextFactory, commerceOrder,
			_getCommerceOrderImporterItemImpls(
				commerceChannel.getGroupId(), selectedCommerceOrder,
				fdsPagination),
			_commerceOrderItemService, _commerceOrderPriceCalculation,
			_commerceOrderService, _userLocalService);
	}

	@Override
	public int getCommerceOrderImporterItemsCount(Object object)
		throws Exception {

		CommerceOrder commerceOrder = (CommerceOrder)object;

		return _commerceOrderItemService.getCommerceOrderItemsCount(
			commerceOrder.getCommerceOrderId());
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.format(resourceBundle, "import-from-x", KEY);
	}

	@Override
	public void render(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/pending_commerce_orders/importer_type/commerce_orders.jsp");
	}

	@Override
	public void renderCommerceOrderPreview(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/pending_commerce_orders/importer_type/common/preview.jsp");
	}

	private CommerceOrderImporterItemImpl[] _getCommerceOrderImporterItemImpls(
			long commerceChannelGroupId, CommerceOrder commerceOrder,
			FDSPagination fdsPagination)
		throws Exception {

		int start = QueryUtil.ALL_POS;
		int end = QueryUtil.ALL_POS;

		if (fdsPagination != null) {
			start = fdsPagination.getStartPosition();
			end = fdsPagination.getEndPosition();
		}

		return TransformUtil.transformToArray(
			_commerceOrderItemService.getCommerceOrderItems(
				commerceOrder.getCommerceOrderId(), start, end),
			commerceOrderItem -> _toCommerceOrderImporterItemImpl(
				commerceOrder.getCommerceAccountId(), commerceChannelGroupId,
				commerceOrder.getCommerceOrderTypeId(), commerceOrderItem),
			CommerceOrderImporterItemImpl.class);
	}

	private CommerceOrderImporterItemImpl _toCommerceOrderImporterItemImpl(
			long accountEntryId, long commerceChannelGroupId,
			long commerceOrderTypeId, CommerceOrderItem commerceOrderItem)
		throws Exception {

		CommerceOrderImporterItemImpl commerceOrderImporterItemImpl =
			new CommerceOrderImporterItemImpl();

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			commerceOrderItem.getCPInstanceId());

		if (cpInstance == null) {
			commerceOrderImporterItemImpl.setNameMap(
				commerceOrderItem.getNameMap());
			commerceOrderImporterItemImpl.setErrorMessages(
				new String[] {"the-product-is-no-longer-available"});
		}
		else {
			CPInstance firstAvailableReplacementCPInstance =
				_cpInstanceHelper.fetchFirstAvailableReplacementCPInstance(
					accountEntryId, commerceChannelGroupId, commerceOrderTypeId,
					cpInstance.getCPInstanceId());

			if ((firstAvailableReplacementCPInstance != null) &&
				!_cpAvailabilityChecker.check(
					accountEntryId, commerceChannelGroupId, cpInstance,
					StringPool.BLANK, commerceOrderItem.getQuantity())) {

				commerceOrderImporterItemImpl.setReplacingSKU(
					cpInstance.getSku());

				cpInstance = firstAvailableReplacementCPInstance;
			}

			commerceOrderImporterItemImpl.setCPInstanceId(
				cpInstance.getCPInstanceId());
			commerceOrderImporterItemImpl.setSku(cpInstance.getSku());

			CPDefinition cpDefinition = cpInstance.getCPDefinition();

			commerceOrderImporterItemImpl.setCPDefinitionId(
				cpDefinition.getCPDefinitionId());
			commerceOrderImporterItemImpl.setNameMap(cpDefinition.getNameMap());
		}

		String json = commerceOrderItem.getJson();

		if (Validator.isNull(json)) {
			json = "[]";
		}

		commerceOrderImporterItemImpl.setJSON(json);
		commerceOrderImporterItemImpl.setQuantity(
			commerceOrderItem.getQuantity());
		commerceOrderImporterItemImpl.setUnitOfMeasureKey(
			commerceOrderItem.getUnitOfMeasureKey());

		return commerceOrderImporterItemImpl;
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CPAvailabilityChecker _cpAvailabilityChecker;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Language _language;

	@Reference
	private UserLocalService _userLocalService;

}