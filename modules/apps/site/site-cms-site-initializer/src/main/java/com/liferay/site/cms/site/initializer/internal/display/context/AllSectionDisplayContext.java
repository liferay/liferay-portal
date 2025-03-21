/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.site.cms.site.initializer.internal.configuration.CMSSiteInitializerConfiguration;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Jürgen Kappler
 */
public class AllSectionDisplayContext extends BaseSectionDisplayContext {

	public AllSectionDisplayContext(
		CMSSiteInitializerConfiguration cmsSiteInitializerConfiguration,
		HttpServletRequest httpServletRequest,
		ObjectDefinitionService objectDefinitionService) {

		super(cmsSiteInitializerConfiguration, httpServletRequest);

		_objectDefinitionService = objectDefinitionService;
	}

	@Override
	public CreationMenu getCreationMenu() {
		return new CreationMenu() {
			{
				for (ObjectDefinition objectDefinition :
						_objectDefinitionService.getCMSObjectDefinitions(
							themeDisplay.getCompanyId(),
							getObjectDefinitionFolderExternalReferenceCodes())) {

					addPrimaryDropdownItem(
						dropdownItem -> {
							dropdownItem.setHref(
								getAddStructuredContentItemURL(
									objectDefinition.getObjectDefinitionId()));
							dropdownItem.setIcon("forms");
							dropdownItem.setLabel(
								objectDefinition.getLabel(
									themeDisplay.getLocale()));
						});
				}
			}
		};
	}

	@Override
	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				httpServletRequest, "click-new-to-create-your-first-asset")
		).put(
			"image", "/states/cms_empty_state.svg"
		).put(
			"title", LanguageUtil.get(httpServletRequest, "no-assets-yet")
		).build();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				_getPermissionsURL(), "password-policies", "permissions",
				LanguageUtil.get(httpServletRequest, "permissions"), "get",
				"permissions", "modal-permissions"),
			new FDSActionDropdownItem(
				LanguageUtil.get(
					httpServletRequest,
					"are-you-sure-you-want-to-delete-this-entry"),
				null, "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "headless"));
	}

	@Override
	public String[] getObjectDefinitionFolderExternalReferenceCodes() {
		return ArrayUtil.append(
			cmsSiteInitializerConfiguration.
				contentsObjectDefinitionFolderExternalReferenceCodes(),
			cmsSiteInitializerConfiguration.
				filesObjectDefinitionFolderExternalReferenceCodes());
	}

	@Override
	protected String getCMSSectionFilterString() {
		return "cmsSection in ('contents', 'files')";
	}

	private String _getPermissionsURL() throws Exception {

		// TODO "modelResourceDescription"

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest,
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setRedirect(
			""
		).setParameter(
			"modelResource", "{entryClassName}"
		).setParameter(
			"resourcePrimKey", "{embedded.id}"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private final ObjectDefinitionService _objectDefinitionService;

}