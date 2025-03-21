/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.warehouse.web.internal.display.context;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.product.servlet.taglib.ui.constants.CPDefinitionScreenNavigationConstants;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andrea Di Giorgi
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CommerceInventoryWarehouseItemsDisplayContext {

	public CommerceInventoryWarehouseItemsDisplayContext(
		CommerceInventoryWarehouseItemService
			commerceInventoryWarehouseItemService,
		CommerceInventoryWarehouseService commerceInventoryWarehouseService,
		CommerceQuantityFormatter commerceQuantityFormatter,
		CPInstanceService cpInstanceService,
		HttpServletRequest httpServletRequest, Portal portal) {

		_commerceInventoryWarehouseItemService =
			commerceInventoryWarehouseItemService;
		_commerceInventoryWarehouseService = commerceInventoryWarehouseService;
		_commerceQuantityFormatter = commerceQuantityFormatter;
		_cpInstanceService = cpInstanceService;
		_portal = portal;

		_cpRequestHelper = new CPRequestHelper(httpServletRequest);
	}

	public String getBackURL() {
		RenderRequest renderRequest = _cpRequestHelper.getRenderRequest();

		String lifecycle = (String)renderRequest.getAttribute(
			LiferayPortletRequest.LIFECYCLE_PHASE);

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				renderRequest, CPPortletKeys.CP_DEFINITIONS, lifecycle)
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_definition"
		).setParameter(
			"cpDefinitionId",
			() -> {
				CPInstance cpInstance = getCPInstance();

				return cpInstance.getCPDefinitionId();
			}
		).setParameter(
			"screenNavigationCategoryKey",
			CPDefinitionScreenNavigationConstants.CATEGORY_KEY_SKUS
		).buildString();
	}

	public CommerceInventoryWarehouseItem getCommerceInventoryWarehouseItem(
			CommerceInventoryWarehouse commerceInventoryWarehouse,
			String unitOfMeasureKey)
		throws PortalException {

		CPInstance cpInstance = getCPInstance();

		return _commerceInventoryWarehouseItemService.
			fetchCommerceInventoryWarehouseItem(
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				cpInstance.getSku(), unitOfMeasureKey);
	}

	public List<CommerceInventoryWarehouse> getCommerceInventoryWarehouses()
		throws PortalException {

		return _getCommerceInventoryWarehouses();
	}

	public CPInstance getCPInstance() throws PortalException {
		if (_cpInstance == null) {
			long cpInstanceId = ParamUtil.getLong(
				_cpRequestHelper.getRenderRequest(), "cpInstanceId");

			_cpInstance = _cpInstanceService.getCPInstance(cpInstanceId);
		}

		return _cpInstance;
	}

	public List<String> getCPInstanceUnitOfMeasureKeys()
		throws PortalException {

		CPInstance cpInstance = getCPInstance();

		List<String> cpInstanceUnitOfMeasureKeys = TransformUtil.transform(
			cpInstance.getCPInstanceUnitOfMeasures(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
			cpInstanceUnitOfMeasure -> cpInstanceUnitOfMeasure.getKey());

		if (cpInstanceUnitOfMeasureKeys.isEmpty()) {
			return Arrays.asList(StringPool.BLANK);
		}

		return cpInstanceUnitOfMeasureKeys;
	}

	public String getFormattedQuantity(
		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem) {

		if (commerceInventoryWarehouseItem == null) {
			return StringPool.BLANK;
		}

		BigDecimal formattedQuantity = _commerceQuantityFormatter.format(
			_cpRequestHelper.getCompanyId(),
			commerceInventoryWarehouseItem.getQuantity(),
			commerceInventoryWarehouseItem.getSku(),
			commerceInventoryWarehouseItem.getUnitOfMeasureKey());

		return formattedQuantity.toString();
	}

	public String getUpdateCommerceInventoryWarehouseItemTaglibOnClick(
		long commerceInventoryWarehouseId,
		long commerceInventoryWarehouseItemId, long mvccVersion, int index) {

		RenderResponse renderResponse = _cpRequestHelper.getRenderResponse();

		return StringBundler.concat(
			renderResponse.getNamespace(),
			"updateCommerceInventoryWarehouseItem(",
			commerceInventoryWarehouseId, StringPool.COMMA_AND_SPACE,
			commerceInventoryWarehouseItemId, StringPool.COMMA_AND_SPACE,
			mvccVersion, StringPool.COMMA_AND_SPACE, index,
			StringPool.CLOSE_PARENTHESIS, StringPool.SEMICOLON);
	}

	public boolean hasManageCommerceInventoryWarehousePermission() {
		return true;
	}

	private List<CommerceInventoryWarehouse> _getCommerceInventoryWarehouses()
		throws PortalException {

		if (_commerceInventoryWarehouses != null) {
			return _commerceInventoryWarehouses;
		}

		_commerceInventoryWarehouses =
			_commerceInventoryWarehouseService.search(
				_cpRequestHelper.getCompanyId(), null, null, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		return _commerceInventoryWarehouses;
	}

	private final CommerceInventoryWarehouseItemService
		_commerceInventoryWarehouseItemService;
	private List<CommerceInventoryWarehouse> _commerceInventoryWarehouses;
	private final CommerceInventoryWarehouseService
		_commerceInventoryWarehouseService;
	private final CommerceQuantityFormatter _commerceQuantityFormatter;
	private CPInstance _cpInstance;
	private final CPInstanceService _cpInstanceService;
	private final CPRequestHelper _cpRequestHelper;
	private final Portal _portal;

}