/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.importer.type;

import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.exception.CommerceOrderImporterTypeException;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.content.web.internal.importer.type.util.CommerceOrderImporterTypeUtil;
import com.liferay.commerce.order.importer.item.CommerceOrderImporterItem;
import com.liferay.commerce.order.importer.item.CommerceOrderImporterItemImpl;
import com.liferay.commerce.order.importer.type.CommerceOrderImporterType;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.discovery.CPConfigurationListDiscovery;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.model.CommerceWishListItem;
import com.liferay.commerce.wish.list.service.CommerceWishListItemService;
import com.liferay.commerce.wish.list.service.CommerceWishListService;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "commerce.order.importer.type.key=" + CommerceWishListsCommerceOrderImporterTypeImpl.KEY,
	service = CommerceOrderImporterType.class
)
public class CommerceWishListsCommerceOrderImporterTypeImpl
	implements CommerceOrderImporterType {

	public static final String KEY = "wish-lists";

	@Override
	public Object getCommerceOrderImporterItem(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceWishListId = ParamUtil.getLong(
			httpServletRequest, getCommerceOrderImporterItemParamName());

		if (commerceWishListId <= 0) {
			return null;
		}

		return _commerceWishListService.getCommerceWishList(commerceWishListId);
	}

	@Override
	public String getCommerceOrderImporterItemParamName() {
		return "commerceWishListId";
	}

	@Override
	public List<CommerceOrderImporterItem> getCommerceOrderImporterItems(
			CommerceOrder commerceOrder, FDSPagination fdsPagination,
			Object object)
		throws Exception {

		if ((object == null) || !(object instanceof CommerceWishList)) {
			throw new CommerceOrderImporterTypeException();
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		return CommerceOrderImporterTypeUtil.getCommerceOrderImporterItems(
			_commerceContextFactory, commerceOrder,
			_getCommerceOrderImporterItemImpls(
				commerceChannel.getGroupId(), commerceOrder,
				(CommerceWishList)object, fdsPagination),
			_commerceOrderItemService, _commerceOrderPriceCalculation,
			_commerceOrderService, _userLocalService);
	}

	@Override
	public int getCommerceOrderImporterItemsCount(Object object)
		throws Exception {

		CommerceWishList commerceWishList = (CommerceWishList)object;

		return _commerceWishListItemService.getCommerceWishListItemsCount(
			commerceWishList.getCommerceWishListId());
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
			"/pending_commerce_orders/importer_type/commerce_wish_lists.jsp");
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
			CommerceWishList commerceWishList, FDSPagination fdsPagination)
		throws Exception {

		int start = QueryUtil.ALL_POS;
		int end = QueryUtil.ALL_POS;

		if (fdsPagination != null) {
			start = fdsPagination.getStartPosition();
			end = fdsPagination.getEndPosition();
		}

		return TransformUtil.transformToArray(
			_commerceWishListItemService.getCommerceWishListItems(
				commerceWishList.getCommerceWishListId(), start, end, null),
			commerceWishListItem -> _toCommerceOrderImporterItemImpl(
				commerceChannelGroupId, commerceOrder, commerceWishListItem),
			CommerceOrderImporterItemImpl.class);
	}

	private CommerceOrderImporterItemImpl _toCommerceOrderImporterItemImpl(
			long commerceChannelGroupId, CommerceOrder commerceOrder,
			CommerceWishListItem commerceWishListItem)
		throws Exception {

		CommerceOrderImporterItemImpl commerceOrderImporterItemImpl =
			new CommerceOrderImporterItemImpl();

		CPInstance cpInstance = _cpInstanceLocalService.fetchCProductInstance(
			commerceWishListItem.getCProductId(),
			commerceWishListItem.getCPInstanceUuid());

		if (cpInstance == null) {
			CPDefinition cpDefinition = commerceWishListItem.getCPDefinition();

			commerceOrderImporterItemImpl.setNameMap(cpDefinition.getNameMap());

			commerceOrderImporterItemImpl.setErrorMessages(
				new String[] {"the-product-is-no-longer-available"});
			commerceOrderImporterItemImpl.setQuantity(BigDecimal.ONE);
			commerceOrderImporterItemImpl.setUnitOfMeasureKey(StringPool.BLANK);
		}
		else {
			CPInstance firstAvailableReplacementCPInstance =
				_cpInstanceHelper.fetchFirstAvailableReplacementCPInstance(
					commerceOrder.getCommerceAccountId(),
					commerceChannelGroupId,
					commerceOrder.getCommerceOrderTypeId(),
					cpInstance.getCPInstanceId());

			if (firstAvailableReplacementCPInstance != null) {
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

			long cpConfigurationListId = 0;

			if (FeatureFlagManagerUtil.isEnabled("LPD-10889")) {
				CommerceChannel commerceChannel =
					_commerceChannelLocalService.getCommerceChannelByGroupId(
						commerceOrder.getGroupId());

				CPConfigurationList cpConfigurationList =
					_cpConfigurationListDiscovery.getCPConfigurationList(
						cpInstance.getCompanyId(), cpInstance.getGroupId(),
						commerceOrder.getCommerceAccountId(),
						commerceChannel.getCommerceChannelId(),
						commerceOrder.getCommerceOrderTypeId());

				cpConfigurationListId =
					cpConfigurationList.getCPConfigurationListId();
			}

			commerceOrderImporterItemImpl.setQuantity(
				_cpDefinitionInventoryEngine.getMinOrderQuantity(
					cpConfigurationListId, cpInstance));

			commerceOrderImporterItemImpl.setUnitOfMeasureKey(StringPool.BLANK);
		}

		String json = commerceWishListItem.getJson();

		if (Validator.isNull(json)) {
			json = "[]";
		}

		commerceOrderImporterItemImpl.setJSON(json);

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
	private CommerceWishListItemService _commerceWishListItemService;

	@Reference
	private CommerceWishListService _commerceWishListService;

	@Reference
	private CPConfigurationListDiscovery _cpConfigurationListDiscovery;

	@Reference
	private CPDefinitionInventoryEngine _cpDefinitionInventoryEngine;

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