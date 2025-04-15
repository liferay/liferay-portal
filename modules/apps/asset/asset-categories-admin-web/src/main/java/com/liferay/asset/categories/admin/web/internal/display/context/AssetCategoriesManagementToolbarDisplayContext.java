/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class AssetCategoriesManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public AssetCategoriesManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			AssetCategoriesDisplayContext assetCategoriesDisplayContext)
		throws PortalException {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			assetCategoriesDisplayContext.getCategoriesSearchContainer());

		_assetCategoriesDisplayContext = assetCategoriesDisplayContext;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						this::_isSetDisplayPageTemplateEnabled,
						dropdownItem -> {
							dropdownItem.putData(
								"action", "setCategoryDisplayPageTemplate");
							dropdownItem.putData(
								"setCategoryDisplayPageTemplateURL",
								PortletURLBuilder.createRenderURL(
									liferayPortletResponse
								).setMVCPath(
									"/set_asset_category_" +
										"display_page_template.jsp"
								).setRedirect(
									currentURLObj
								).setParameter(
									"parentCategoryId",
									_assetCategoriesDisplayContext.
										getCategoryId()
								).setParameter(
									"vocabularyId",
									_assetCategoriesDisplayContext.
										getVocabularyId()
								).buildString());
							dropdownItem.setIcon("page");
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest,
									"assign-display-page-template"));
							dropdownItem.setQuickAction(true);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData(
								"action", "deleteSelectedCategories");
							dropdownItem.setIcon("trash");
							dropdownItem.setLabel(
								LanguageUtil.get(httpServletRequest, "delete"));
							dropdownItem.setQuickAction(true);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	public String getAvailableActions(AssetCategory category)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		if (_assetCategoriesDisplayContext.hasPermission(
				category, ActionKeys.UPDATE)) {

			availableActions.add("setCategoryDisplayPageTemplate");
		}

		if (_assetCategoriesDisplayContext.hasPermission(
				category, ActionKeys.DELETE)) {

			availableActions.add("deleteSelectedCategories");
		}

		return StringUtil.merge(availableActions, StringPool.COMMA);
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setNavigation(
			"all"
		).setParameter(
			"categoryId", "0"
		).buildString();
	}

	@Override
	public String getComponentId() {
		return "assetCategoriesManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCPath(
						"/edit_asset_category.jsp"
					).setParameter(
						"parentCategoryId",
						() -> {
							long categoryId =
								_assetCategoriesDisplayContext.getCategoryId();

							if (categoryId <= 0) {
								return null;
							}

							return _assetCategoriesDisplayContext.
								getCategoryId();
						}
					).setParameter(
						"vocabularyId",
						_assetCategoriesDisplayContext.getVocabularyId()
					).buildPortletURL());

				String label = "add-category";

				if (_assetCategoriesDisplayContext.getCategoryId() > 0) {
					label = "add-subcategory";
				}

				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, label));
			}
		).build();
	}

	@Override
	public String getDefaultEventHandler() {
		return "assetCategoriesManagementToolbarDefaultEventHandler";
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		if (!_isNavigationCategory()) {
			return null;
		}

		AssetCategory category = _assetCategoriesDisplayContext.getCategory();

		if (category == null) {
			return null;
		}

		return LabelItemListBuilder.add(
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							currentURLObj, liferayPortletResponse)
					).setNavigation(
						(String)null
					).setParameter(
						"categoryId", "0"
					).buildString());

				labelItem.setCloseable(true);

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				labelItem.setLabel(category.getTitle(themeDisplay.getLocale()));
			}
		).build();
	}

	@Override
	public List<DropdownItem> getFilterNavigationDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(_isNavigationAll());
				dropdownItem.setHref(getPortletURL(), "navigation", "all");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "all"));
			}
		).add(
			_assetCategoriesDisplayContext::isFlattenedNavigationAllowed,
			dropdownItem -> {
				dropdownItem.putData("action", "selectCategory");
				dropdownItem.putData(
					"categoriesSelectorURL", _getCategoriesSelectorURL());
				dropdownItem.putData(
					"viewCategoriesURL", _getViewCategoriesURL());
				dropdownItem.setActive(_isNavigationCategory());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "category"));
			}
		).build();
	}

	@Override
	public String getSearchContainerId() {
		return "assetCategories";
	}

	@Override
	public Boolean isSelectable() {
		return _assetCategoriesDisplayContext.isShowCategoriesSelectButton();
	}

	@Override
	public Boolean isShowCreationMenu() {
		return _assetCategoriesDisplayContext.isShowCategoriesAddButton();
	}

	@Override
	protected String getDisplayStyle() {
		return _assetCategoriesDisplayContext.getDisplayStyle();
	}

	@Override
	protected String[] getDisplayViews() {
		if (_assetCategoriesDisplayContext.isFlattenedNavigationAllowed()) {
			return new String[0];
		}

		return new String[] {"list", "descriptive"};
	}

	@Override
	protected String[] getOrderByKeys() {
		if (_assetCategoriesDisplayContext.isFlattenedNavigationAllowed()) {
			return new String[] {"path"};
		}

		return new String[] {"create-date"};
	}

	private String _getCategoriesSelectorURL() throws Exception {
		ItemSelector itemSelector =
			(ItemSelector)httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(liferayPortletRequest);

		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new InfoItemItemSelectorReturnType());
		itemSelectorCriterion.setItemType(AssetCategory.class.getName());

		return PortletURLBuilder.create(
			itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory, themeDisplay.getScopeGroup(),
				themeDisplay.getScopeGroupId(),
				liferayPortletResponse.getNamespace() + "selectCategory",
				itemSelectorCriterion)
		).setParameter(
			"vocabularyIds", _assetCategoriesDisplayContext.getVocabularyId()
		).buildString();
	}

	private String _getViewCategoriesURL() throws PortalException {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCPath(
			"/view.jsp"
		).setNavigation(
			"category"
		).setParameter(
			"vocabularyId", _assetCategoriesDisplayContext.getVocabularyId()
		).buildString();
	}

	private boolean _isNavigationAll() {
		if (!_assetCategoriesDisplayContext.isFlattenedNavigationAllowed() ||
			Objects.equals(getNavigation(), "all")) {

			return true;
		}

		return false;
	}

	private boolean _isNavigationCategory() {
		if (_assetCategoriesDisplayContext.isFlattenedNavigationAllowed() &&
			Objects.equals(getNavigation(), "category")) {

			return true;
		}

		return false;
	}

	private boolean _isSetDisplayPageTemplateEnabled() {
		if (_setDisplayPageTemplateEnabled != null) {
			return _setDisplayPageTemplateEnabled;
		}

		boolean setDisplayPageTemplateEnabled = true;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		if (group.isCompany() || group.isDepot()) {
			setDisplayPageTemplateEnabled = false;
		}

		_setDisplayPageTemplateEnabled = setDisplayPageTemplateEnabled;

		return _setDisplayPageTemplateEnabled;
	}

	private final AssetCategoriesDisplayContext _assetCategoriesDisplayContext;
	private Boolean _setDisplayPageTemplateEnabled;

}