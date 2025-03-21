/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.display.context;

import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.template.constants.TemplatePortletKeys;
import com.liferay.template.info.item.capability.TemplateInfoItemCapability;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.web.internal.security.permissions.resource.TemplateEntryPermission;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class InformationTemplatesManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public InformationTemplatesManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		InformationTemplatesTemplateDisplayContext
			informationTemplatesTemplateDisplayContext,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			informationTemplatesTemplateDisplayContext.
				getTemplateSearchContainer());

		_informationTemplatesTemplateDisplayContext =
			informationTemplatesTemplateDisplayContext;

		_infoItemServiceRegistry =
			(InfoItemServiceRegistry)liferayPortletRequest.getAttribute(
				InfoItemServiceRegistry.class.getName());

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteSelectedTemplateEntries");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	@Override
	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"addTemplateEntryURL",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/template/add_template_entry"
			).setRedirect(
				_themeDisplay.getURLCurrent()
			).buildString()
		).put(
			"itemTypes", _getItemTypesJSONArray()
		).build();
	}

	public String getAvailableActions(TemplateEntry templateEntry)
		throws PortalException {

		if (TemplateEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), templateEntry,
				ActionKeys.DELETE)) {

			return "deleteSelectedTemplateEntries";
		}

		return StringPool.BLANK;
	}

	@Override
	public String getComponentId() {
		return "templateManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		if (!_informationTemplatesTemplateDisplayContext.isAddButtonEnabled() ||
			!containsAddPortletDisplayTemplatePermission()) {

			return null;
		}

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "addInformationTemplateEntry");
				dropdownItem.setLabel(
					LanguageUtil.get(_themeDisplay.getLocale(), "add"));
			}
		).build();
	}

	@Override
	public String getDefaultEventHandler() {
		return "TEMPLATE_MANAGEMENT_TOOLBAR_DEFAULT_EVENT_HANDLER";
	}

	@Override
	public String getSearchContainerId() {
		return "templateEntries";
	}

	protected boolean containsAddPortletDisplayTemplatePermission() {
		try {
			return PortletPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), _themeDisplay.getLayout(),
				TemplatePortletKeys.TEMPLATE, DDMActionKeys.ADD_TEMPLATE, false,
				false);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to check permission for resource name " +
						TemplatePortletKeys.TEMPLATE,
					portalException);
			}
		}

		return false;
	}

	private JSONArray _getItemTypesJSONArray() {
		JSONArray itemTypesJSONArray = JSONFactoryUtil.createJSONArray();

		if (!containsAddPortletDisplayTemplatePermission()) {
			return itemTypesJSONArray;
		}

		for (InfoItemClassDetails infoItemClassDetails :
				_infoItemServiceRegistry.getInfoItemClassDetails(
					_themeDisplay.getScopeGroupId(),
					TemplateInfoItemCapability.KEY,
					_themeDisplay.getPermissionChecker())) {

			InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFormVariationsProvider.class,
					infoItemClassDetails.getClassName());

			if (infoItemFormVariationsProvider != null) {
				List<InfoItemFormVariation> infoItemFormVariations =
					new ArrayList<>(
						infoItemFormVariationsProvider.
							getInfoItemFormVariations(
								_themeDisplay.getScopeGroupId()));

				if (infoItemFormVariations.isEmpty()) {
					continue;
				}

				JSONArray itemSubtypesJSONArray =
					JSONFactoryUtil.createJSONArray();

				InfoPermissionProvider infoPermissionProvider =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoPermissionProvider.class,
						infoItemClassDetails.getClassName());

				if (infoPermissionProvider != null) {
					infoItemFormVariations = ListUtil.filter(
						infoItemFormVariations,
						infoItemFormVariation ->
							infoPermissionProvider.hasViewPermission(
								infoItemFormVariation.getKey(),
								_themeDisplay.getScopeGroupId(),
								_themeDisplay.getPermissionChecker()));
				}

				infoItemFormVariations = ListUtil.sort(
					infoItemFormVariations,
					Comparator.comparing(
						infoItemFormVariation -> infoItemFormVariation.getLabel(
							_themeDisplay.getLocale())));

				for (InfoItemFormVariation infoItemFormVariation :
						infoItemFormVariations) {

					itemSubtypesJSONArray.put(
						JSONUtil.put(
							"label",
							infoItemFormVariation.getLabel(
								_themeDisplay.getLocale())
						).put(
							"value", infoItemFormVariation.getKey()
						));
				}

				itemTypesJSONArray.put(
					JSONUtil.put(
						"label",
						infoItemClassDetails.getLabel(_themeDisplay.getLocale())
					).put(
						"subtypes", itemSubtypesJSONArray
					).put(
						"value", infoItemClassDetails.getClassName()
					));
			}
			else {
				itemTypesJSONArray.put(
					JSONUtil.put(
						"label",
						infoItemClassDetails.getLabel(_themeDisplay.getLocale())
					).put(
						"value", infoItemClassDetails.getClassName()
					));
			}
		}

		return itemTypesJSONArray;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InformationTemplatesManagementToolbarDisplayContext.class);

	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private final InformationTemplatesTemplateDisplayContext
		_informationTemplatesTemplateDisplayContext;
	private final ThemeDisplay _themeDisplay;

}