/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.defaultpermissions.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.defaultpermissions.web.internal.search.PortalDefaultPermissionsSearch;
import com.liferay.portal.defaultpermissions.web.internal.search.PortalDefaultPermissionsSearchEntry;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.defaultpermissions.resource.PortalDefaultPermissionsModelResourceRegistry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Stefano Motta
 */
public class GroupViewPortalDefaultPermissionsConfigurationDisplayContext
	extends BaseViewPortalDefaultPermissionsConfigurationDisplayContext {

	public GroupViewPortalDefaultPermissionsConfigurationDisplayContext(
		HttpServletRequest httpServletRequest, Language language,
		PortalDefaultPermissionsModelResourceRegistry
			portalDefaultPermissionsModelResourceRegistry) {

		_httpServletRequest = httpServletRequest;
		_language = language;
		_portalDefaultPermissionsModelResourceRegistry =
			portalDefaultPermissionsModelResourceRegistry;

		_liferayPortletRequest = PortalUtil.getLiferayPortletRequest(
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST));
		_liferayPortletResponse = PortalUtil.getLiferayPortletResponse(
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE));
	}

	@Override
	public List<DropdownItem> getActionDropdownItems(
		PortalDefaultPermissionsSearchEntry
			portalDefaultPermissionsSearchEntry) {

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "editDefaultPermissions");
				dropdownItem.putData(
					"editDefaultPermissionsURL",
					getEditURL(
						portalDefaultPermissionsSearchEntry.getClassName()));
				dropdownItem.putData(
					"qa-id",
					"edit-" + portalDefaultPermissionsSearchEntry.getLabel());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit"));
				dropdownItem.setTarget("modal-permissions");
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "resetDefaultPermissions");
				dropdownItem.putData(
					"qa-id",
					"reset-" + portalDefaultPermissionsSearchEntry.getLabel());
				dropdownItem.putData(
					"resetDefaultPermissionsURL",
					getResetURL(
						portalDefaultPermissionsSearchEntry.getClassName()));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "reset"));
			}
		).build();
	}

	@Override
	public String getEditURL(String className) {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest,
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/configuration/edit_portal_default_permissions_configuration"
		).setParameter(
			"modelResource", className
		).setParameter(
			"scope", ExtendedObjectClassDefinition.Scope.GROUP
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	@Override
	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setKeywords(
			() -> {
				String keywords = ParamUtil.getString(
					_httpServletRequest, "keywords");

				if (Validator.isNotNull(keywords)) {
					return keywords;
				}

				return null;
			}
		).setParameter(
			"delta",
			() -> {
				String delta = ParamUtil.getString(
					_httpServletRequest, "delta");

				if (Validator.isNotNull(delta)) {
					return delta;
				}

				return null;
			}
		).buildPortletURL();
	}

	public String getResetURL(String className) {
		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				_httpServletRequest,
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/configuration/edit_portal_default_permissions_configuration"
		).setCMD(
			"reset"
		).setParameter(
			"modelResource", className
		).setParameter(
			"scope", ExtendedObjectClassDefinition.Scope.GROUP
		).buildString();
	}

	@Override
	public SearchContainer<PortalDefaultPermissionsSearchEntry>
		getSearchContainer() {

		SearchContainer<PortalDefaultPermissionsSearchEntry> searchContainer =
			new PortalDefaultPermissionsSearch(
				_liferayPortletRequest, getPortletURL());

		DisplayTerms searchTerms = searchContainer.getSearchTerms();

		List<PortalDefaultPermissionsSearchEntry>
			portalDefaultPermissionSearchEntries =
				_createPortalDefaultPermissionSearchEntryList();

		portalDefaultPermissionSearchEntries = filter(
			portalDefaultPermissionSearchEntries, searchTerms.getKeywords(),
			searchTerms.getKeywords());

		searchContainer.setResultsAndTotal(
			ListUtil.sort(
				portalDefaultPermissionSearchEntries,
				searchContainer.getOrderByComparator()));

		return searchContainer;
	}

	private List<PortalDefaultPermissionsSearchEntry>
		_createPortalDefaultPermissionSearchEntryList() {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return TransformUtil.transform(
			_portalDefaultPermissionsModelResourceRegistry.
				getPortalDefaultPermissionsModelResources(),
			portalDefaultPermissionsModelResource -> {
				if (!ExtendedObjectClassDefinition.Scope.GROUP.equals(
						portalDefaultPermissionsModelResource.getScope())) {

					return null;
				}

				return new PortalDefaultPermissionsSearchEntry(
					portalDefaultPermissionsModelResource.getClassName(),
					_language.get(
						themeDisplay.getLocale(),
						portalDefaultPermissionsModelResource.getLabel()));
			});
	}

	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final PortalDefaultPermissionsModelResourceRegistry
		_portalDefaultPermissionsModelResourceRegistry;

}