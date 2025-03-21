/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context.util;

import com.liferay.dynamic.data.mapping.form.web.internal.display.context.helper.FormInstancePermissionCheckerHelper;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Carolina Barbosa
 */
public class DDMFormAdminActionDropdownItemsProvider {

	public DDMFormAdminActionDropdownItemsProvider(
		String autocompleteUserURL, DDMFormInstance ddmFormInstance,
		String exportFormURL,
		FormInstancePermissionCheckerHelper formInstancePermissionCheckerHelper,
		boolean formPublished, HttpServletRequest httpServletRequest,
		boolean invalidDDMFormInstance, JSONObject localizedNameJSONObject,
		String publishedFormURL, RenderResponse renderResponse,
		long scopeGroupId, String shareFormInstanceURL) {

		_autocompleteUserURL = autocompleteUserURL;
		_ddmFormInstance = ddmFormInstance;
		_exportFormURL = exportFormURL;
		_formInstancePermissionCheckerHelper =
			formInstancePermissionCheckerHelper;
		_formPublished = formPublished;
		_httpServletRequest = httpServletRequest;
		_invalidDDMFormInstance = invalidDDMFormInstance;
		_localizedNameJSONObject = localizedNameJSONObject;
		_publishedFormURL = publishedFormURL;
		_renderResponse = renderResponse;
		_scopeGroupId = scopeGroupId;
		_shareFormInstanceURL = shareFormInstanceURL;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							_formInstancePermissionCheckerHelper.isShowEditIcon(
								_ddmFormInstance),
						_getEditActionUnsafeConsumer()
					).add(
						() ->
							_formInstancePermissionCheckerHelper.
								isShowViewEntriesIcon(_ddmFormInstance),
						_getViewEntriesActionUnsafeConsumer()
					).add(
						() ->
							_formPublished &&
							_formInstancePermissionCheckerHelper.
								isShowShareIcon(_ddmFormInstance),
						_getShareActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							_formInstancePermissionCheckerHelper.
								isShowDuplicateIcon(),
						_getDuplicateActionUnsafeConsumer()
					).add(
						() ->
							_formInstancePermissionCheckerHelper.
								isShowExportIcon(_ddmFormInstance) &&
							!Objects.equals(
								_ddmFormInstance.getStorageType(), "object"),
						_getExportActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							_formInstancePermissionCheckerHelper.
								isShowPermissionsIcon(_ddmFormInstance),
						_getPermissionsActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							_formInstancePermissionCheckerHelper.
								isShowDeleteIcon(_ddmFormInstance),
						_getDeleteActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setData(
				HashMapBuilder.<String, Object>put(
					"action", "delete"
				).put(
					"deleteFormInstanceURL",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/dynamic_data_mapping_form/delete_form_instance"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"formInstanceId", _ddmFormInstance.getFormInstanceId()
					).buildString()
				).build());
			dropdownItem.setIcon("times-circle");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDuplicateActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setDisabled(_invalidDDMFormInstance);
			dropdownItem.setHref(
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/dynamic_data_mapping_form/copy_form_instance"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"formInstanceId", _ddmFormInstance.getFormInstanceId()
				).setParameter(
					"groupId", _scopeGroupId
				).buildString());
			dropdownItem.setIcon("copy");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "make-a-copy"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setDisabled(_invalidDDMFormInstance);
			dropdownItem.setHref(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/admin/edit_form_instance"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"formInstanceId", _ddmFormInstance.getFormInstanceId()
				).buildString());
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getExportActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setData(
				HashMapBuilder.<String, Object>put(
					"action", "exportForm"
				).put(
					"exportFormURL", _exportFormURL
				).build());
			dropdownItem.setDisabled(_invalidDDMFormInstance);
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "export-entries"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getPermissionsActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setData(
				HashMapBuilder.<String, Object>put(
					"action", "permissions"
				).put(
					"permissionsFormInstanceURL",
					PermissionsURLTag.doTag(
						StringPool.BLANK, DDMFormInstance.class.getName(),
						_ddmFormInstance.getName(_themeDisplay.getLocale()),
						null,
						String.valueOf(_ddmFormInstance.getFormInstanceId()),
						LiferayWindowState.POP_UP.toString(), null,
						_httpServletRequest)
				).put(
					"useDialog", Boolean.TRUE.toString()
				).build());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getShareActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setData(
				HashMapBuilder.<String, Object>put(
					"action", "shareForm"
				).put(
					"autocompleteUserURL", _autocompleteUserURL
				).put(
					"localizedName", _localizedNameJSONObject
				).put(
					"portletNamespace", _renderResponse.getNamespace()
				).put(
					"shareFormInstanceURL", _shareFormInstanceURL
				).put(
					"url", _publishedFormURL
				).build());
			dropdownItem.setDisabled(_invalidDDMFormInstance);
			dropdownItem.setIcon("share");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "share"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewEntriesActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setDisabled(_invalidDDMFormInstance);
			dropdownItem.setHref(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCPath(
					"/admin/view_form_instance_records.jsp"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"formInstanceId", _ddmFormInstance.getFormInstanceId()
				).buildString());
			dropdownItem.setIcon("list-ul");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "view-entries"));
		};
	}

	private final String _autocompleteUserURL;
	private final DDMFormInstance _ddmFormInstance;
	private final String _exportFormURL;
	private final FormInstancePermissionCheckerHelper
		_formInstancePermissionCheckerHelper;
	private final boolean _formPublished;
	private final HttpServletRequest _httpServletRequest;
	private final boolean _invalidDDMFormInstance;
	private final JSONObject _localizedNameJSONObject;
	private final String _publishedFormURL;
	private final RenderResponse _renderResponse;
	private final long _scopeGroupId;
	private final String _shareFormInstanceURL;
	private final ThemeDisplay _themeDisplay;

}