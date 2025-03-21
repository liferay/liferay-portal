/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.asset.display.internal.portlet;

import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.portlet.AssetDisplayPageEntryFormProcessor;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 * @author Roberto Díaz
 */
@Component(service = AssetDisplayPageEntryFormProcessor.class)
public class AssetDisplayPageFormProcessorImpl
	implements AssetDisplayPageEntryFormProcessor {

	@Override
	public void process(
			String className, long classPK, PortletRequest portletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		int displayPageType = ParamUtil.getInteger(
			portletRequest, "displayPageType",
			AssetDisplayPageConstants.TYPE_DEFAULT);

		String layoutUuid = ParamUtil.getString(portletRequest, "layoutUuid");

		long assetDisplayPageId = ParamUtil.getLong(
			portletRequest, "assetDisplayPageId");

		if (displayPageType == AssetDisplayPageConstants.TYPE_NONE) {
			assetDisplayPageId = 0;
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			portletRequest);

		_process(
			themeDisplay.getUserId(), themeDisplay.getScopeGroupId(), className,
			classPK, displayPageType, layoutUuid, assetDisplayPageId,
			serviceContext);
	}

	@Override
	public void process(
			String className, long classPK, ServiceContext serviceContext)
		throws PortalException {

		int displayPageType = ParamUtil.getInteger(
			serviceContext, "displayPageType",
			AssetDisplayPageConstants.TYPE_DEFAULT);

		String layoutUuid = ParamUtil.getString(serviceContext, "layoutUuid");

		long assetDisplayPageId = ParamUtil.getLong(
			serviceContext, "assetDisplayPageId");

		if (displayPageType == AssetDisplayPageConstants.TYPE_NONE) {
			assetDisplayPageId = 0;
		}

		_process(
			serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			className, classPK, displayPageType, layoutUuid, assetDisplayPageId,
			serviceContext);
	}

	private void _process(
			long userId, long groupId, String className, long classPK,
			int displayPageType, String layoutUuid, long assetDisplayPageId,
			ServiceContext serviceContext)
		throws PortalException {

		long classNameId = _portal.getClassNameId(className);

		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
				groupId, classNameId, classPK);

		if ((displayPageType == AssetDisplayPageConstants.TYPE_DEFAULT) ||
			((displayPageType == AssetDisplayPageConstants.TYPE_SPECIFIC) &&
			 Validator.isNotNull(layoutUuid))) {

			if (assetDisplayPageEntry != null) {
				_assetDisplayPageEntryLocalService.deleteAssetDisplayPageEntry(
					groupId, classNameId, classPK);
			}

			return;
		}

		if (displayPageType == AssetDisplayPageConstants.TYPE_NONE) {
			assetDisplayPageId = 0;
		}

		if (assetDisplayPageEntry == null) {
			_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
				userId, groupId, classNameId, classPK, assetDisplayPageId,
				displayPageType, serviceContext);

			return;
		}

		_assetDisplayPageEntryLocalService.updateAssetDisplayPageEntry(
			assetDisplayPageEntry.getAssetDisplayPageEntryId(),
			assetDisplayPageId, displayPageType);
	}

	@Reference
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Reference
	private Portal _portal;

}