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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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

		if (Objects.equals(
				selectionStyle,
				AssetPublisherSelectionStyleConstants.TYPE_DYNAMIC)) {

			UnicodeProperties unicodeProperties = new UnicodeProperties(true);

			Enumeration<String> enumeration = portletPreferences.getNames();

			while (enumeration.hasMoreElements()) {
				String name = enumeration.nextElement();

				String value = StringUtil.merge(
					portletPreferences.getValues(name, null));

				if (Validator.isNull(value)) {
					continue;
				}

				if (!name.equals("scopeIds")) {
					unicodeProperties.put(name, value);

					continue;
				}

				List<Long> groupIds = new ArrayList<>();

				String[] parts = value.split(StringPool.COMMA);

				for (String part : parts) {
					if (part.equals("Group_default")) {
						groupIds.add(serviceContext.getScopeGroupId());
					}
					else if (part.startsWith("Group_")) {
						long groupId = GetterUtil.getLong(
							StringUtil.removeSubstring(part, "Group_"), -1);

						if (groupId != -1) {
							groupIds.add(groupId);
						}
					}
				}

				if (groupIds.isEmpty()) {
					continue;
				}

				name = "groupIds";
				value = ListUtil.toString(groupIds, StringPool.BLANK);

				unicodeProperties.put(name, value);
			}

			return _assetListEntryService.addDynamicAssetListEntry(
				null, themeDisplay.getScopeGroupId(), title,
				unicodeProperties.toString(), serviceContext);
		}

		if (!Objects.equals(
				selectionStyle,
				AssetPublisherSelectionStyleConstants.TYPE_MANUAL)) {

			return null;
		}

		return _assetListEntryService.addManualAssetListEntry(
			null, themeDisplay.getScopeGroupId(), title,
			ListUtil.toLongArray(
				_assetPublisherHelper.getAssetEntries(
					actionRequest, portletPreferences,
					themeDisplay.getPermissionChecker(),
					_assetPublisherHelper.getGroupIds(
						portletPreferences, themeDisplay.getScopeGroupId(),
						themeDisplay.getLayout()),
					true, true),
				AssetEntry::getEntryId),
			serviceContext);
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
	private Language _language;

}