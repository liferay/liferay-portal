/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.display.context;

import com.liferay.commerce.channel.web.internal.item.selector.CommerceCurrencyItemSelectorCriterion;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRelModel;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;

/**
 * @author Fabio Monaco
 */
public class CommerceChannelCommerceCurrencyDisplayContext {

	public CommerceChannelCommerceCurrencyDisplayContext(
		ModelResourcePermission<CommerceChannel>
			commerceChannelModelResourcePermission,
		CommerceChannelRelService commerceChannelRelService,
		CommerceChannelService commerceChannelService,
		HttpServletRequest httpServletRequest, ItemSelector itemSelector) {

		_commerceChannelModelResourcePermission =
			commerceChannelModelResourcePermission;
		_commerceChannelRelService = commerceChannelRelService;
		_commerceChannelService = commerceChannelService;
		_itemSelector = itemSelector;

		_cpRequestHelper = new CPRequestHelper(httpServletRequest);
	}

	public CommerceChannel getCommerceChannel() throws PortalException {
		if (_commerceChannel != null) {
			return _commerceChannel;
		}

		long commerceChannelId = ParamUtil.getLong(
			_cpRequestHelper.getRenderRequest(), "commerceChannelId");

		if (commerceChannelId > 0) {
			_commerceChannel = _commerceChannelService.getCommerceChannel(
				commerceChannelId);
		}

		return _commerceChannel;
	}

	public long getCommerceChannelId() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		if (commerceChannel == null) {
			return 0;
		}

		return commerceChannel.getCommerceChannelId();
	}

	public CreationMenu getCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (hasPermission(getCommerceChannelId(), ActionKeys.UPDATE)) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					LiferayPortletResponse liferayPortletResponse =
						_cpRequestHelper.getLiferayPortletResponse();

					dropdownItem.setHref(
						liferayPortletResponse.getNamespace() +
							"addCommerceChannelCommerceCurrency");

					dropdownItem.setLabel(
						LanguageUtil.get(
							_cpRequestHelper.getRequest(), "add-currency"));
					dropdownItem.setTarget("event");
				});
		}

		return creationMenu;
	}

	public HashMap<String, Object> getJSContext() {
		return HashMapBuilder.<String, Object>put(
			"url",
			() -> {
				RequestBackedPortletURLFactory requestBackedPortletURLFactory =
					RequestBackedPortletURLFactoryUtil.create(
						_cpRequestHelper.getRenderRequest());

				ItemSelectorCriterion itemSelectorCriterion =
					new CommerceCurrencyItemSelectorCriterion();

				itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
					new UUIDItemSelectorReturnType());

				long commerceChannelId = getCommerceChannelId();

				return PortletURLBuilder.create(
					_itemSelector.getItemSelectorURL(
						requestBackedPortletURLFactory, "currencySelectItem",
						itemSelectorCriterion)
				).setParameter(
					"checkedCommerceCurrencyIds",
					StringUtil.merge(
						_getCheckedCommerceCurrencyIds(commerceChannelId))
				).setParameter(
					"commerceChannelId", commerceChannelId
				).buildString();
			}
		).build();
	}

	public boolean hasPermission(long commerceChannelId, String actionId)
		throws PortalException {

		return _commerceChannelModelResourcePermission.contains(
			_cpRequestHelper.getPermissionChecker(), commerceChannelId,
			actionId);
	}

	private long[] _getCheckedCommerceCurrencyIds(long commerceChannelId)
		throws PortalException {

		return TransformUtil.transformToLongArray(
			_commerceChannelRelService.getCommerceCurrencyCommerceChannelRels(
				commerceChannelId, StringPool.BLANK, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			CommerceChannelRelModel::getClassPK);
	}

	private CommerceChannel _commerceChannel;
	private final ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;
	private final CommerceChannelRelService _commerceChannelRelService;
	private final CommerceChannelService _commerceChannelService;
	private final CPRequestHelper _cpRequestHelper;
	private final ItemSelector _itemSelector;

}