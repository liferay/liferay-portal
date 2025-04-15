/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal.upload;

import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.item.selector.ItemSelectorReturnTypeResolverHandler;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = ItemSelectorUploadResponseHandler.class)
public class ItemSelectorUploadResponseHandlerImpl
	implements ItemSelectorUploadResponseHandler {

	@Override
	public JSONObject onFailure(
			PortletRequest portletRequest, PortalException portalException)
		throws PortalException {

		return _defaultUploadResponseHandler.onFailure(
			portletRequest, portalException);
	}

	@Override
	public JSONObject onSuccess(
			UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
		throws PortalException {

		JSONObject jsonObject = _defaultUploadResponseHandler.onSuccess(
			uploadPortletRequest, fileEntry);

		return _resolveValue(uploadPortletRequest, fileEntry, jsonObject);
	}

	private JSONObject _resolveValue(
			UploadPortletRequest uploadPortletRequest, FileEntry fileEntry,
			JSONObject jsonObject)
		throws PortalException {

		String returnType = ParamUtil.getString(
			uploadPortletRequest, "returnType");

		ItemSelectorReturnTypeResolver<?, Object>
			itemSelectorReturnTypeResolver =
				(ItemSelectorReturnTypeResolver<?, Object>)
					_itemSelectorReturnTypeResolverHandler.
						getItemSelectorReturnTypeResolver(
							returnType, FileEntry.class.getName());

		if (itemSelectorReturnTypeResolver != null) {
			try {
				JSONObject fileJSONObject = jsonObject.getJSONObject("file");

				ThemeDisplay themeDisplay =
					(ThemeDisplay)uploadPortletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				String resolvedValue = itemSelectorReturnTypeResolver.getValue(
					fileEntry, themeDisplay);

				fileJSONObject.put("resolvedValue", resolvedValue);
			}
			catch (Exception exception) {
				throw new PortalException(exception);
			}
		}

		SessionMessages.add(
			uploadPortletRequest,
			_portal.getPortletId(uploadPortletRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);

		return jsonObject;
	}

	@Reference(target = "(upload.response.handler.system.default=true)")
	private UploadResponseHandler _defaultUploadResponseHandler;

	@Reference
	private ItemSelectorReturnTypeResolverHandler
		_itemSelectorReturnTypeResolverHandler;

	@Reference
	private Portal _portal;

}