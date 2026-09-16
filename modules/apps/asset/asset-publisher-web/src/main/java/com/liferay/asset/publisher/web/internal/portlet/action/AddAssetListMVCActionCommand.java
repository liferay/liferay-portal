/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.portlet.action;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.list.exception.AssetListEntryTitleException;
import com.liferay.asset.list.exception.DuplicateAssetListEntryTitleException;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.publisher.web.internal.constants.AssetPublisherSelectionStyleConstants;
import com.liferay.asset.publisher.web.internal.util.AssetListTypeSettingsUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletPreferences;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetPublisherPortletKeys.ASSET_PUBLISHER,
		"mvc.command.name=/asset_publisher/add_asset_list"
	},
	service = MVCActionCommand.class
)
public class AddAssetListMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			String portletResource = ParamUtil.getString(
				actionRequest, "portletResource");

			PortletPreferences portletPreferences =
				PortletPreferencesFactoryUtil.getExistingPortletSetup(
					themeDisplay.getLayout(), portletResource);

			AssetListEntry assetListEntry = _getAssetListEntry(
				actionRequest, portletPreferences, themeDisplay);

			if (assetListEntry != null) {
				portletPreferences.setValue(
					"assetListEntryExternalReferenceCode",
					assetListEntry.getExternalReferenceCode());

				if (assetListEntry.getGroupId() !=
						themeDisplay.getScopeGroupId()) {

					Group group = _groupLocalService.getGroup(
						assetListEntry.getGroupId());

					portletPreferences.setValue(
						"assetListEntryGroupExternalReferenceCode",
						group.getExternalReferenceCode());
				}

				portletPreferences.setValue(
					"selectionStyle",
					AssetPublisherSelectionStyleConstants.TYPE_ASSET_LIST);

				portletPreferences.store();
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"redirectURL",
					ParamUtil.getString(actionRequest, "redirect")));

			hideDefaultSuccessMessage(actionRequest);

			MultiSessionMessages.add(
				actionRequest, portletResource + "requestProcessed");
		}
		catch (PortalException portalException) {
			hideDefaultErrorMessage(actionRequest);

			_handlePortalException(
				actionRequest, actionResponse, portalException, themeDisplay);
		}
	}

	private AssetListEntry _getAssetListEntry(
			ActionRequest actionRequest, PortletPreferences portletPreferences,
			ThemeDisplay themeDisplay)
		throws Exception {

		String selectionStyle = portletPreferences.getValue(
			"selectionStyle", "dynamic");
		String title = ParamUtil.getString(actionRequest, "title");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		long scopeGroupId = _getScopeGroupId(themeDisplay);

		if (Objects.equals(
				selectionStyle,
				AssetPublisherSelectionStyleConstants.TYPE_DYNAMIC)) {

			return _assetListEntryService.addDynamicAssetListEntry(
				null, scopeGroupId, title,
				AssetListTypeSettingsUtil.getTypeSettings(
					serviceContext.getScopeGroupId(), portletPreferences),
				serviceContext);
		}

		if (!Objects.equals(
				selectionStyle,
				AssetPublisherSelectionStyleConstants.TYPE_MANUAL)) {

			return null;
		}

		return _assetListEntryService.addManualAssetListEntry(
			null, scopeGroupId, title,
			ListUtil.toLongArray(
				_assetPublisherHelper.getAssetEntries(
					actionRequest, portletPreferences,
					themeDisplay.getPermissionChecker(),
					_assetPublisherHelper.getGroupIds(
						portletPreferences, scopeGroupId,
						themeDisplay.getLayout()),
					true, true),
				AssetEntry::getEntryId),
			serviceContext);
	}

	private long _getScopeGroupId(ThemeDisplay themeDisplay) {
		Layout layout = themeDisplay.getLayout();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if ((layoutPageTemplateEntry != null) &&
			(layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

			return layoutPageTemplateEntry.getGroupId();
		}

		return themeDisplay.getScopeGroupId();
	}

	private void _handlePortalException(
			ActionRequest actionRequest, ActionResponse actionResponse,
			PortalException portalException, ThemeDisplay themeDisplay)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(portalException);
		}

		String errorMessage = "an-unexpected-error-occurred";

		if (portalException instanceof AssetListEntryTitleException) {
			errorMessage = "please-enter-a-valid-title";
		}
		else if (portalException instanceof
					DuplicateAssetListEntryTitleException) {

			errorMessage = "a-collection-with-that-title-already-exists";
		}
		else {
			_log.error(portalException);
		}

		JSONObject jsonObject = JSONUtil.put(
			"error", _language.get(themeDisplay.getRequest(), errorMessage));

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddAssetListMVCActionCommand.class);

	@Reference
	private AssetListEntryService _assetListEntryService;

	@Reference
	private AssetPublisherHelper _assetPublisherHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

}