/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.portlet.action;

import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryService;
import com.liferay.asset.list.web.internal.handler.AssetListEntryExceptionRequestHandlerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetListPortletKeys.ASSET_LIST,
		"mvc.command.name=/asset_list/add_asset_list_entry"
	},
	service = MVCActionCommand.class
)
public class AddAssetListEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String title = ParamUtil.getString(actionRequest, "title");
		int type = ParamUtil.getInteger(actionRequest, "type");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		try {
			AssetListEntry assetListEntry = null;

			if (type == AssetListEntryTypeConstants.TYPE_DYNAMIC) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)actionRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				assetListEntry =
					_assetListEntryService.addDynamicAssetListEntry(
						null, serviceContext.getScopeGroupId(), title,
						UnicodePropertiesBuilder.create(
							true
						).put(
							"groupIds",
							String.valueOf(themeDisplay.getScopeGroupId())
						).buildString(),
						serviceContext);
			}
			else {
				assetListEntry = _assetListEntryService.addAssetListEntry(
					null, serviceContext.getScopeGroupId(), title, type,
					serviceContext);
			}

			JSONObject jsonObject = JSONUtil.put(
				"redirectURL",
				getRedirectURL(actionRequest, actionResponse, assetListEntry));

			if (SessionErrors.contains(
					actionRequest, "assetListEntryNameInvalid")) {

				addSuccessMessage(actionRequest, actionResponse);
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}
		catch (PortalException portalException) {
			SessionErrors.add(actionRequest, "assetListEntryNameInvalid");

			hideDefaultErrorMessage(actionRequest);

			AssetListEntryExceptionRequestHandlerUtil.handlePortalException(
				actionRequest, actionResponse, portalException);
		}
	}

	protected String getRedirectURL(
		ActionRequest actionRequest, ActionResponse actionResponse,
		AssetListEntry assetListEntry) {

		return PortletURLBuilder.createRenderURL(
			_portal.getLiferayPortletResponse(actionResponse)
		).setMVCPath(
			"/edit_asset_list_entry.jsp"
		).setBackURL(
			() -> {
				String backURL = ParamUtil.getString(actionRequest, "backURL");

				if (backURL != null) {
					return backURL;
				}

				return null;
			}
		).setParameter(
			"assetListEntryId", assetListEntry.getAssetListEntryId()
		).setParameter(
			"backURLTitle", ParamUtil.getString(actionRequest, "backURLTitle")
		).buildString();
	}

	@Reference
	private AssetListEntryService _assetListEntryService;

	@Reference
	private Portal _portal;

}