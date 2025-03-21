/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.site.cms.site.initializer.internal.configuration.CMSSiteInitializerConfiguration;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Sam Ziemer
 */
public class StructuresSectionDisplayContext extends BaseSectionDisplayContext {

	public StructuresSectionDisplayContext(
		CMSSiteInitializerConfiguration cmsSiteInitializerConfiguration,
		HttpServletRequest httpServletRequest) {

		super(cmsSiteInitializerConfiguration, httpServletRequest);
	}

	@Override
	public String getAPIURL() {
		return "/o/object-admin/v1.0/object-definitions?filter=" +
			"objectFolderExternalReferenceCode eq 'L_CMS_CONTENT_STRUCTURES' " +
				"or objectFolderExternalReferenceCode eq 'L_CMS_FILE_TYPES'";
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(_getHref("L_CMS_CONTENT_STRUCTURES"));
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "content"));
			}
		).addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(_getHref("L_CMS_FILE_TYPES"));
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "file"));
			}
		).build();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		return List.of(
			new FDSActionDropdownItem(
				HttpComponentsUtil.addParameters(
					PortalUtil.getLayoutFullURL(
						LayoutLocalServiceUtil.getLayoutByFriendlyURL(
							themeDisplay.getScopeGroupId(), false,
							"/structure-builder"),
						themeDisplay),
					"objectDefinitionId", "{id}"),
				"pencil", "edit", LanguageUtil.get(httpServletRequest, "edit"),
				"get", null, null),
			new FDSActionDropdownItem(
				"", "copy", "copy",
				LanguageUtil.get(httpServletRequest, "make-a-copy"), null, null,
				null),
			new FDSActionDropdownItem(
				ResourceURLBuilder.createResourceURL(
					PortletURLFactoryUtil.create(
						httpServletRequest,
						ObjectPortletKeys.OBJECT_DEFINITIONS,
						PortletRequest.RESOURCE_PHASE)
				).setParameter(
					"objectDefinitionId", "{id}"
				).setResourceID(
					"/object_definitions/export_object_definition"
				).buildString(),
				"export", "export",
				LanguageUtil.get(httpServletRequest, "export-as-json"), "get",
				"exportObjectDefinition", null),
			new FDSActionDropdownItem(
				"", "import", "import",
				LanguageUtil.get(httpServletRequest, "import-and-override"),
				null, null, null),
			new FDSActionDropdownItem(
				"", "password-policies", "permissions",
				LanguageUtil.get(httpServletRequest, "permissions"), "get",
				"permissions", "modal-permissions"),
			new FDSActionDropdownItem(
				"", "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "headless"));
	}

	@Override
	protected String getCMSSectionFilterString() {
		return StringPool.BLANK;
	}

	private String _getHref(String objectFolderExternalReferenceCode) {
		try {
			return HttpComponentsUtil.addParameters(
				PortalUtil.getLayoutFullURL(
					LayoutLocalServiceUtil.getLayoutByFriendlyURL(
						themeDisplay.getScopeGroupId(), false,
						"/structure-builder"),
					themeDisplay),
				"objectFolderExternalReferenceCode",
				objectFolderExternalReferenceCode);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StructuresSectionDisplayContext.class);

}