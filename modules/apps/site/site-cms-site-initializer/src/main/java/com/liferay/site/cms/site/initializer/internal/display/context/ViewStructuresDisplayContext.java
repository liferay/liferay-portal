/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Sam Ziemer
 */
public class ViewStructuresDisplayContext {

	public ViewStructuresDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		StringBundler sb = new StringBundler(6);

		sb.append("/o/object-admin/v1.0/object-definitions?filter=");
		sb.append("(objectFolderExternalReferenceCode eq '");
		sb.append(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES);
		sb.append("' or objectFolderExternalReferenceCode eq '");
		sb.append(ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES);
		sb.append("')");

		return sb.toString();
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return Collections.emptyList();
	}

	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					_getHref(
						ObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "content"));
			}
		).addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					_getHref(
						ObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_FILE_TYPES));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "file"));
			}
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		return List.of(
			new FDSActionDropdownItem(
				HttpComponentsUtil.addParameters(
					PortalUtil.getLayoutFullURL(
						LayoutLocalServiceUtil.getLayoutByFriendlyURL(
							_themeDisplay.getScopeGroupId(), false,
							"/structure-builder"),
						_themeDisplay),
					"objectDefinitionId", "{id}"),
				"pencil", "edit", LanguageUtil.get(_httpServletRequest, "edit"),
				"get", "update", null),
			new FDSActionDropdownItem(
				HttpComponentsUtil.addParameters(
					PortalUtil.getLayoutFullURL(
						LayoutLocalServiceUtil.getLayoutByFriendlyURL(
							_themeDisplay.getScopeGroupId(), false,
							"/structure-usages"),
						_themeDisplay),
					"objectDefinitionId", "{id}"),
				"list-ul", "viewUsages",
				LanguageUtil.get(_httpServletRequest, "view-usages"), "get",
				null, null),
			new FDSActionDropdownItem(
				"", "copy", "copy",
				LanguageUtil.get(_httpServletRequest, "make-a-copy"), null,
				null, null),
			new FDSActionDropdownItem(
				ResourceURLBuilder.createResourceURL(
					PortletURLFactoryUtil.create(
						_httpServletRequest,
						ObjectPortletKeys.OBJECT_DEFINITIONS,
						PortletRequest.RESOURCE_PHASE)
				).setParameter(
					"objectDefinitionId", "{id}"
				).setResourceID(
					"/object_definitions/export_object_definition"
				).buildString(),
				"export", "export",
				LanguageUtil.get(_httpServletRequest, "export-as-json"), "get",
				"exportObjectDefinition", null),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortletURLFactoryUtil.create(
						_httpServletRequest,
						ObjectPortletKeys.OBJECT_DEFINITIONS,
						PortletRequest.ACTION_PHASE)
				).setActionName(
					"/object_definitions/import_object_definition"
				).setParameter(
					"externalReferenceCode", "{externalReferenceCode}"
				).buildString(),
				"import", "import",
				LanguageUtil.get(_httpServletRequest, "import-and-override"),
				"get", "update", null),
			new FDSActionDropdownItem(
				"", "password-policies", "permissions",
				LanguageUtil.get(_httpServletRequest, "permissions"), "get",
				"permissions", "modal-permissions"),
			new FDSActionDropdownItem(
				ResourceURLBuilder.createResourceURL(
					PortletURLFactoryUtil.create(
						_httpServletRequest,
						ObjectPortletKeys.OBJECT_DEFINITIONS,
						PortletRequest.RESOURCE_PHASE)
				).setParameter(
					"objectDefinitionId", "{id}"
				).setResourceID(
					"/object_definitions/get_object_definition_delete_info"
				).buildString(),
				"trash", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), "delete",
				"delete", null));
	}

	private String _getHref(String objectFolderExternalReferenceCode) {
		try {
			return HttpComponentsUtil.addParameters(
				PortalUtil.getLayoutFullURL(
					LayoutLocalServiceUtil.getLayoutByFriendlyURL(
						_themeDisplay.getScopeGroupId(), false,
						"/structure-builder"),
					_themeDisplay),
				"objectFolderExternalReferenceCode",
				objectFolderExternalReferenceCode);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewStructuresDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}