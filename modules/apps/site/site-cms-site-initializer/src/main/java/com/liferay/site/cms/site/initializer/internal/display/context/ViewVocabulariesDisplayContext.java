/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter.VocabularyAssetTypesSelectionFDSFilter;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Noor Najjar
 */
public class ViewVocabulariesDisplayContext {

	public ViewVocabulariesDisplayContext(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		_httpServletRequest = httpServletRequest;
		_themeDisplay = themeDisplay;
	}

	public String getAPIURL() {
		return "/o/headless-admin-taxonomy/v1.0/sites/" +
			_themeDisplay.getScopeGroupId() + "/taxonomy-vocabularies";
	}

	public List<AssetRendererFactory<?>> getAvailableAssetRendererFactories() {
		return ListUtil.filter(
			AssetRendererFactoryRegistryUtil.getAssetRendererFactories(
				_themeDisplay.getCompanyId()),
			AssetRendererFactory::isCategorizable);
	}

	public List<Map<String, String>> getClassNameIdOptions() {
		List<Map<String, String>> selectOptions = new ArrayList<>();

		List<AssetRendererFactory<?>> availableAssetRendererFactories =
			getAvailableAssetRendererFactories();

		for (AssetRendererFactory<?> availableAssetRendererFactory :
				availableAssetRendererFactories) {

			selectOptions.add(
				HashMapBuilder.put(
					"icon", availableAssetRendererFactory.getIconCssClass()
				).put(
					"label",
					ResourceActionsUtil.getModelResource(
						_themeDisplay.getLocale(),
						availableAssetRendererFactory.getClassName())
				).put(
					"restricted", Boolean.FALSE.toString()
				).put(
					"value",
					String.valueOf(
						availableAssetRendererFactory.getClassNameId())
				).build());
		}

		return selectOptions;
	}

	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortalUtil.getLayoutFullURL(
						LayoutLocalServiceUtil.getLayoutByFriendlyURL(
							_themeDisplay.getScopeGroupId(), false,
							"/categorization/new_vocabulary"),
						_themeDisplay));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "new-vocabulary"));
			}
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws PortalException {

		String fullLayoutURL = PortalUtil.getLayoutFullURL(
			LayoutLocalServiceUtil.getLayoutByFriendlyURL(
				_themeDisplay.getScopeGroupId(), false,
				"/categorization/edit_vocabulary"),
			_themeDisplay);

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				fullLayoutURL + "?vocabularyId={id}", "pencil", "edit",
				LanguageUtil.get(_httpServletRequest, "edit"), "get", "update",
				null),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest,
						"com_liferay_portlet_configuration_web_portlet_" +
							"PortletConfigurationPortlet",
						ActionRequest.RENDER_PHASE)
				).setMVCPath(
					"/edit_permissions.jsp"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"modelResource", AssetVocabulary.class.getName()
				).setParameter(
					"modelResourceDescription", "{name}"
				).setParameter(
					"resourcePrimKey", "{id}"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"password-policies", "permissions",
				LanguageUtil.get(_httpServletRequest, "permissions"), "get",
				null, "modal-permissions"),
			new FDSActionDropdownItem(
				null, "times-circle", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), null, "delete",
				null));
	}

	public List<FDSFilter> getFDSFilters() {
		return ListUtil.fromArray(
			new VocabularyAssetTypesSelectionFDSFilter(
				getClassNameIdOptions()));
	}

	public Map<String, Object> getReactData() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"activeTab", "vocabularies"
		).put(
			"tagsURL",
			PortalUtil.getLayoutFullURL(
				LayoutLocalServiceUtil.getLayoutByFriendlyURL(
					_themeDisplay.getScopeGroupId(), false,
					"/categorization/view_tags"),
				_themeDisplay)
		).put(
			"vocabulariesURL",
			PortalUtil.getLayoutFullURL(
				LayoutLocalServiceUtil.getLayoutByFriendlyURL(
					_themeDisplay.getScopeGroupId(), false,
					"/categorization/view_vocabularies"),
				_themeDisplay)
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}