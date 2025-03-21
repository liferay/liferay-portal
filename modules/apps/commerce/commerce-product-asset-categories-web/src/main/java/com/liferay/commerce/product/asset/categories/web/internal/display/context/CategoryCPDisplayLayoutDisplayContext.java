/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.commerce.product.configuration.CPDisplayLayoutConfiguration;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.model.CPDisplayLayout;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CPDisplayLayoutService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.layout.item.selector.criterion.LayoutItemSelectorCriterion;
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
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CategoryCPDisplayLayoutDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public CategoryCPDisplayLayoutDisplayContext(
		ActionHelper actionHelper,
		AssetCategoryLocalService assetCategoryLocalService,
		HttpServletRequest httpServletRequest,
		CommerceChannelLocalService commerceChannelLocalService,
		CPDisplayLayoutService cpDisplayLayoutService,
		GroupLocalService groupLocalService, ItemSelector itemSelector,
		LayoutLocalService layoutLocalService) {

		super(actionHelper, httpServletRequest);

		_assetCategoryLocalService = assetCategoryLocalService;
		_commerceChannelLocalService = commerceChannelLocalService;
		_cpDisplayLayoutService = cpDisplayLayoutService;
		_groupLocalService = groupLocalService;
		_itemSelector = itemSelector;
		_layoutLocalService = layoutLocalService;
	}

	public String getAddCategoryDisplayPageURL() throws Exception {
		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CommerceChannel.class.getName(),
				PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/commerce_channels/edit_asset_category_cp_display_layout"
		).setParameter(
			"commerceChannelId", getCommerceChannelId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public AssetCategory getAssetCategory(long assetCategoryId)
		throws PortalException {

		return _assetCategoryLocalService.getAssetCategory(assetCategoryId);
	}

	public String getCategorySelectorURL(RenderResponse renderResponse) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new InfoItemItemSelectorReturnType());
		itemSelectorCriterion.setItemType(AssetCategory.class.getName());

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(
					liferayPortletRequest),
				themeDisplay.getScopeGroup(), themeDisplay.getScopeGroupId(),
				renderResponse.getNamespace() + "selectCategory",
				itemSelectorCriterion)
		).setParameter(
			"vocabularyIds",
			() -> {
				List<AssetVocabulary> vocabularies =
					AssetVocabularyServiceUtil.getGroupVocabularies(
						themeDisplay.getCompanyGroupId());

				return ListUtil.toString(
					vocabularies, AssetVocabulary.VOCABULARY_ID_ACCESSOR);
			}
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
				dropdownItem.setHref(getAddCategoryDisplayPageURL());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add-display-layout"));
				dropdownItem.setTarget("sidePanel");
			}
		).build();
	}

	public Layout getDefaultAssetCategoryLayout() throws Exception {
		CommerceChannel commerceChannel = getCommerceChannel();

		CPDisplayLayoutConfiguration cpDisplayLayoutConfiguration =
			ConfigurationProviderUtil.getConfiguration(
				CPDisplayLayoutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CPConstants.RESOURCE_NAME_CP_DISPLAY_LAYOUT));

		String layoutUuid =
			cpDisplayLayoutConfiguration.assetCategoryLayoutUuid();

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

	public String getItemSelectorUrl(RenderRequest renderRequest)
		throws PortalException {

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(renderRequest);

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
				commerceChannel.getSiteGroupId(), "selectDisplayPage",
				layoutItemSelectorCriterion));
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

	@Override
	public PortletURL getPortletURL() {
		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		String delta = ParamUtil.getString(httpServletRequest, "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String deltaEntry = ParamUtil.getString(
			httpServletRequest, "deltaEntry");

		if (Validator.isNotNull(deltaEntry)) {
			portletURL.setParameter("deltaEntry", deltaEntry);
		}

		String displayStyle = ParamUtil.getString(
			httpServletRequest, "displayStyle");

		if (Validator.isNotNull(displayStyle)) {
			portletURL.setParameter("displayStyle", displayStyle);
		}

		String keywords = ParamUtil.getString(httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		portletURL.setParameter(
			"commerceChannelId", String.valueOf(getCommerceChannelId()));

		return portletURL;
	}

	private final AssetCategoryLocalService _assetCategoryLocalService;
	private final CommerceChannelLocalService _commerceChannelLocalService;
	private CPDisplayLayout _cpDisplayLayout;
	private final CPDisplayLayoutService _cpDisplayLayoutService;
	private final GroupLocalService _groupLocalService;
	private final ItemSelector _itemSelector;
	private final LayoutLocalService _layoutLocalService;

}