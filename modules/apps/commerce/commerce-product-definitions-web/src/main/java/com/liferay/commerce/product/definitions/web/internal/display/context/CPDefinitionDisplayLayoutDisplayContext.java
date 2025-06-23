/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.display.context;

import com.liferay.asset.display.page.item.selector.AssetDisplayPageSelectorCriterion;
import com.liferay.commerce.product.configuration.CPDisplayLayoutConfiguration;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.item.selector.CPDefinitionItemSelectorCriterion;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDisplayLayout;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CPDisplayLayoutService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.layout.item.selector.LayoutItemSelectorCriterion;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

/**
 * @author Alessio Antonio Rendina
 */
public class CPDefinitionDisplayLayoutDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public CPDefinitionDisplayLayoutDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest,
		CommerceChannelLocalService commerceChannelLocalService,
		CPDisplayLayoutService cpDisplayLayoutService,
		GroupLocalService groupLocalService, ItemSelector itemSelector,
		LayoutLocalService layoutLocalService,
		LayoutPageTemplateEntryLocalService
			layoutPageTemplateEntryLocalService) {

		super(actionHelper, httpServletRequest);

		_commerceChannelLocalService = commerceChannelLocalService;
		_cpDisplayLayoutService = cpDisplayLayoutService;
		_groupLocalService = groupLocalService;
		_itemSelector = itemSelector;
		_layoutLocalService = layoutLocalService;
		_layoutPageTemplateEntryLocalService =
			layoutPageTemplateEntryLocalService;
	}

	public String getAddProductDisplayPageURL() throws Exception {
		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CommerceChannel.class.getName(),
				PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/commerce_channels/edit_cp_definition_cp_display_layout"
		).setParameter(
			"commerceChannelId", getCommerceChannelId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public CommerceChannel getCommerceChannel() {
		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		return _commerceChannelLocalService.fetchCommerceChannel(
			commerceChannelId);
	}

	public long getCommerceChannelId() {
		CommerceChannel commerceChannel = getCommerceChannel();

		if (commerceChannel == null) {
			return 0;
		}

		return commerceChannel.getCommerceChannelId();
	}

	public CPDisplayLayout getCPDisplayLayout() throws PortalException {
		if (_cpDisplayLayout != null) {
			return _cpDisplayLayout;
		}

		long cpDisplayLayoutId = ParamUtil.getLong(
			cpRequestHelper.getRequest(), "cpDisplayLayoutId");

		_cpDisplayLayout = _cpDisplayLayoutService.fetchCPDisplayLayout(
			cpDisplayLayoutId);

		return _cpDisplayLayout;
	}

	public CreationMenu getCreationMenu() throws Exception {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(getAddProductDisplayPageURL());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add-display-layout"));
				dropdownItem.setTarget("sidePanel");
			}
		).build();
	}

	public Layout getDefaultProductLayout() throws Exception {
		CommerceChannel commerceChannel = getCommerceChannel();

		CPDisplayLayoutConfiguration cpDisplayLayoutConfiguration =
			ConfigurationProviderUtil.getConfiguration(
				CPDisplayLayoutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CPConstants.RESOURCE_NAME_CP_DISPLAY_LAYOUT));

		String layoutUuid = cpDisplayLayoutConfiguration.productLayoutUuid();

		Layout layout = null;

		if (Validator.isNotNull(layoutUuid)) {
			layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
				layoutUuid, commerceChannel.getSiteGroupId(), false);

			if (layout == null) {
				layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
					layoutUuid, commerceChannel.getSiteGroupId(), true);
			}
		}

		return layout;
	}

	public String getLayoutBreadcrumb(CPDisplayLayout cpDisplayLayout)
		throws PortalException {

		if (cpDisplayLayout == null) {
			return StringPool.BLANK;
		}

		String layoutUuid = cpDisplayLayout.getLayoutUuid();

		if (Validator.isNull(layoutUuid)) {
			return StringPool.BLANK;
		}

		CommerceChannel commerceChannel = getCommerceChannel();

		Layout selLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layoutUuid, commerceChannel.getSiteGroupId(), false);

		if (selLayout == null) {
			selLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
				layoutUuid, commerceChannel.getSiteGroupId(), true);
		}

		if (selLayout != null) {
			return selLayout.getBreadcrumb(cpRequestHelper.getLocale());
		}

		return StringPool.BLANK;
	}

	public String getLayoutItemSelectorUrl() throws PortalException {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(
				cpRequestHelper.getRenderRequest());

		LayoutItemSelectorCriterion layoutItemSelectorCriterion =
			new LayoutItemSelectorCriterion();

		layoutItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.<ItemSelectorReturnType>singletonList(
				new UUIDItemSelectorReturnType()));

		CommerceChannel commerceChannel = getCommerceChannel();

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory,
				_groupLocalService.getGroup(commerceChannel.getSiteGroupId()),
				commerceChannel.getSiteGroupId(), "selectLayout",
				layoutItemSelectorCriterion));
	}

	public String getLayoutPageTemplateEntryItemSelectorURL()
		throws PortalException {

		CommerceChannel commerceChannel = getCommerceChannel();

		AssetDisplayPageSelectorCriterion assetDisplayPageSelectorCriterion =
			new AssetDisplayPageSelectorCriterion();

		assetDisplayPageSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.singletonList(new UUIDItemSelectorReturnType()));
		assetDisplayPageSelectorCriterion.setClassNameId(
			PortalUtil.getClassNameId(CPDefinition.class));

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(
					cpRequestHelper.getRenderRequest()),
				_groupLocalService.getGroup(commerceChannel.getSiteGroupId()),
				commerceChannel.getSiteGroupId(),
				"selectLayoutPageTemplateEntry",
				assetDisplayPageSelectorCriterion));
	}

	public String getLayoutPageTemplateEntryName(
		CPDisplayLayout cpDisplayLayout) {

		if (cpDisplayLayout == null) {
			return StringPool.BLANK;
		}

		String layoutPageTemplateEntryUuid =
			cpDisplayLayout.getLayoutPageTemplateEntryUuid();

		if (Validator.isNull(layoutPageTemplateEntryUuid)) {
			return StringPool.BLANK;
		}

		CommerceChannel commerceChannel = getCommerceChannel();

		LayoutPageTemplateEntry selLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByUuidAndGroupId(
					layoutPageTemplateEntryUuid,
					commerceChannel.getSiteGroupId());

		if (selLayoutPageTemplateEntry != null) {
			return selLayoutPageTemplateEntry.getName();
		}

		return StringPool.BLANK;
	}

	public String getProductItemSelectorUrl() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(
				cpRequestHelper.getRenderRequest());

		CPDefinitionItemSelectorCriterion cpDefinitionItemSelectorCriterion =
			new CPDefinitionItemSelectorCriterion();

		cpDefinitionItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.<ItemSelectorReturnType>singletonList(
				new UUIDItemSelectorReturnType()));

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory, "productDefinitionsSelectItem",
				cpDefinitionItemSelectorCriterion)
		).setParameter(
			CPField.COMMERCE_CHANNEL_GROUP_ID,
			() -> {
				CommerceChannel commerceChannel = getCommerceChannel();

				return commerceChannel.getGroupId();
			}
		).setParameter(
			"ignoreCommerceAccountGroup", Boolean.TRUE
		).setParameter(
			"singleSelection", Boolean.TRUE
		).buildString();
	}

	private final CommerceChannelLocalService _commerceChannelLocalService;
	private CPDisplayLayout _cpDisplayLayout;
	private final CPDisplayLayoutService _cpDisplayLayoutService;
	private final GroupLocalService _groupLocalService;
	private final ItemSelector _itemSelector;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

}