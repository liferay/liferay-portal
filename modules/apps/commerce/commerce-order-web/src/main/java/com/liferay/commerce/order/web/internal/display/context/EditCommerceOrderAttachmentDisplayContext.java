/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context;

import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.order.web.internal.display.context.helper.CommerceOrderRequestHelper;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Tancredi Covioli
 */
public class EditCommerceOrderAttachmentDisplayContext {

	public EditCommerceOrderAttachmentDisplayContext(
		CommerceOrderAttachment commerceOrderAttachment, long commerceOrderId,
		HttpServletRequest httpServletRequest,
		ListTypeDefinitionLocalService listTypeDefinitionLocalService,
		ListTypeEntryLocalService listTypeEntryLocalService) {

		_commerceOrderAttachment = commerceOrderAttachment;
		_commerceOrderId = commerceOrderId;
		_listTypeDefinitionLocalService = listTypeDefinitionLocalService;
		_listTypeEntryLocalService = listTypeEntryLocalService;

		_commerceOrderRequestHelper = new CommerceOrderRequestHelper(
			httpServletRequest);
	}

	public String getAddURL() {
		return _getBaseURL();
	}

	public List<ListTypeEntry> getAttachmentTypes() {
		try {
			ListTypeDefinition listTypeDefinition =
				_listTypeDefinitionLocalService.
					fetchListTypeDefinitionByExternalReferenceCode(
						"L_COMMERCE_ORDER_ATTACHMENT_TYPES",
						_commerceOrderRequestHelper.getCompanyId());

			if (listTypeDefinition == null) {
				return Collections.emptyList();
			}

			return _listTypeEntryLocalService.getListTypeEntries(
				listTypeDefinition.getListTypeDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return Collections.emptyList();
		}
	}

	public long getCommerceOrderAttachmentId() {
		if (_commerceOrderAttachment == null) {
			return 0;
		}

		return _commerceOrderAttachment.getCommerceOrderAttachmentId();
	}

	public long getCommerceOrderId() {
		return _commerceOrderId;
	}

	public String getEditURL() {
		if (_commerceOrderAttachment == null) {
			return _getBaseURL();
		}

		return StringBundler.concat(
			_getBaseURL(), StringPool.SLASH,
			_commerceOrderAttachment.getCommerceOrderAttachmentId());
	}

	public long getFileEntryId() {
		if (_commerceOrderAttachment == null) {
			return 0;
		}

		return _commerceOrderAttachment.getFileEntryId();
	}

	public String getFileEntryName() {
		long fileEntryId = getFileEntryId();

		if (fileEntryId <= 0) {
			return StringPool.BLANK;
		}

		try {
			FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(
				fileEntryId);

			return fileEntry.getFileName();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}
	}

	public String getMode() {
		if (_commerceOrderAttachment == null) {
			return "add";
		}

		return "edit";
	}

	public String getPriority() {
		if (_commerceOrderAttachment == null) {
			return String.valueOf(0.0);
		}

		return String.valueOf(_commerceOrderAttachment.getPriority());
	}

	public boolean getRestricted() {
		if (_commerceOrderAttachment == null) {
			return false;
		}

		return _commerceOrderAttachment.isRestricted();
	}

	public String getTitle() {
		if (_commerceOrderAttachment == null) {
			return StringPool.BLANK;
		}

		return _commerceOrderAttachment.getTitle();
	}

	public String getType() {
		if (_commerceOrderAttachment == null) {
			return StringPool.BLANK;
		}

		return _commerceOrderAttachment.getType();
	}

	public boolean hasViewRestrictedCommerceOrderAttachmentsPermission() {
		ThemeDisplay themeDisplay =
			_commerceOrderRequestHelper.getThemeDisplay();

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		return permissionChecker.hasPermission(
			_commerceOrderRequestHelper.getScopeGroupId(),
			CommerceOrderConstants.RESOURCE_NAME, _commerceOrderId,
			CommerceOrderActionKeys.VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS);
	}

	private String _getBaseURL() {
		return StringBundler.concat(
			Portal.PATH_MODULE, "/headless-commerce-admin-order/v1.0/orders/",
			_commerceOrderId, "/attachments");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCommerceOrderAttachmentDisplayContext.class);

	private final CommerceOrderAttachment _commerceOrderAttachment;
	private final long _commerceOrderId;
	private final CommerceOrderRequestHelper _commerceOrderRequestHelper;
	private final ListTypeDefinitionLocalService
		_listTypeDefinitionLocalService;
	private final ListTypeEntryLocalService _listTypeEntryLocalService;

}