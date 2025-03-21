/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.display.context;

import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil;
import com.liferay.portal.kernel.template.comparator.TemplateHandlerComparator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.template.web.internal.security.permissions.resource.DDMTemplatePermission;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class WidgetTemplatesManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public WidgetTemplatesManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		WidgetTemplatesTemplateDisplayContext
			widgetTemplatesTemplateDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			widgetTemplatesTemplateDisplayContext.getTemplateSearchContainer());

		_widgetTemplatesTemplateDisplayContext =
			widgetTemplatesTemplateDisplayContext;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteSelectedDDMTemplates");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public String getAvailableActions(DDMTemplate ddmTemplate)
		throws PortalException {

		if (DDMTemplatePermission.contains(
				_themeDisplay.getPermissionChecker(), ddmTemplate,
				ActionKeys.DELETE)) {

			return "deleteSelectedDDMTemplates";
		}

		return StringPool.BLANK;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public String getComponentId() {
		return "templateManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		if (!_widgetTemplatesTemplateDisplayContext.isAddButtonEnabled()) {
			return null;
		}

		List<TemplateHandler> addAllowedTemplateHandlers =
			_getAddAllowedTemplateHandlers();

		if (addAllowedTemplateHandlers.isEmpty()) {
			return null;
		}

		CreationMenu creationMenu = new CreationMenu();

		PortletURL addDDMTemplateURL = PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/template/edit_ddm_template"
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).setTabs1(
			_widgetTemplatesTemplateDisplayContext.getTabs1()
		).setParameter(
			"groupId", _themeDisplay.getScopeGroupId()
		).setParameter(
			"type", DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY
		).buildPortletURL();

		for (TemplateHandler addAllowedTemplateHandler :
				addAllowedTemplateHandlers) {

			creationMenu.addPrimaryDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						addDDMTemplateURL, "classNameId",
						String.valueOf(
							PortalUtil.getClassNameId(
								addAllowedTemplateHandler.getClassName())),
						"classPK", "0", "resourceClassNameId",
						String.valueOf(
							_widgetTemplatesTemplateDisplayContext.
								getResourceClassNameId()));
					dropdownItem.setLabel(
						LanguageUtil.get(
							httpServletRequest,
							addAllowedTemplateHandler.getName(
								_themeDisplay.getLocale())));
				});
		}

		return creationMenu;
	}

	@Override
	public String getDefaultEventHandler() {
		return "TEMPLATE_MANAGEMENT_TOOLBAR_DEFAULT_EVENT_HANDLER";
	}

	@Override
	public String getSearchContainerId() {
		return "ddmTemplates";
	}

	protected boolean containsAddPortletDisplayTemplatePermission(
		String resourceName) {

		try {
			return PortletPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), _themeDisplay.getLayout(),
				resourceName, ActionKeys.ADD_PORTLET_DISPLAY_TEMPLATE, false,
				false);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to check permission for resource name " +
						resourceName,
					portalException);
			}
		}

		return false;
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"modified-date", "id"};
	}

	private List<TemplateHandler> _getAddAllowedTemplateHandlers() {
		List<TemplateHandler> addAllowedTemplateHandlers = new ArrayList<>();

		for (long classNameId :
				_widgetTemplatesTemplateDisplayContext.getClassNameIds()) {

			TemplateHandler templateHandler =
				TemplateHandlerRegistryUtil.getTemplateHandler(classNameId);

			if (containsAddPortletDisplayTemplatePermission(
					templateHandler.getResourceName())) {

				addAllowedTemplateHandlers.add(templateHandler);
			}
		}

		Collections.sort(
			addAllowedTemplateHandlers,
			new TemplateHandlerComparator(_themeDisplay.getLocale()));

		return addAllowedTemplateHandlers;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WidgetTemplatesManagementToolbarDisplayContext.class);

	private final ThemeDisplay _themeDisplay;
	private final WidgetTemplatesTemplateDisplayContext
		_widgetTemplatesTemplateDisplayContext;

}