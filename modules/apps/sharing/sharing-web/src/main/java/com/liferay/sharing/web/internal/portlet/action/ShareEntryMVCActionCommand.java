/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.portlet.action;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sharing.constants.SharingPortletKeys;
import com.liferay.sharing.service.SharingEntryService;
import com.liferay.sharing.web.internal.display.SharingEntryPermissionDisplayAction;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SharingPortletKeys.SHARING,
		"mvc.command.name=/sharing/share_entry"
	},
	service = MVCActionCommand.class
)
public class ShareEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String[] userEmailAddresses = ParamUtil.getStringValues(
			actionRequest, "userEmailAddress");

		long classNameId = ParamUtil.getLong(actionRequest, "classNameId");
		long classPK = ParamUtil.getLong(actionRequest, "classPK");
		boolean shareable = ParamUtil.getBoolean(actionRequest, "shareable");

		String sharingEntryPermissionDisplayActionId = ParamUtil.getString(
			actionRequest, "sharingEntryPermissionDisplayActionId");

		SharingEntryPermissionDisplayAction
			sharingEntryPermissionDisplayAction =
				SharingEntryPermissionDisplayAction.parseFromActionId(
					sharingEntryPermissionDisplayActionId);

		Date expirationDate = ParamUtil.getDate(
			actionRequest, "expirationDate",
			DateFormatFactoryUtil.getDate(themeDisplay.getLocale()), null);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					for (String curUserEmailAddresses : userEmailAddresses) {
						User user = _userLocalService.fetchUserByEmailAddress(
							themeDisplay.getCompanyId(), curUserEmailAddresses);

						if ((user != null) &&
							(user.getUserId() != themeDisplay.getUserId())) {

							_sharingEntryService.addOrUpdateSharingEntry(
								null, 0, user.getUserId(), classNameId, classPK,
								themeDisplay.getScopeGroupId(), shareable,
								sharingEntryPermissionDisplayAction.
									getSharingEntryActions(),
								expirationDate, serviceContext);
						}
					}

					return null;
				});

			hideDefaultSuccessMessage(actionRequest);

			JSONObject jsonObject = JSONUtil.put(
				"successMessage",
				_language.get(
					themeDisplay.getLocale(),
					"the-item-was-shared-successfully"));

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}
		catch (Throwable throwable) {
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			String errorMessage =
				"an-unexpected-error-occurred-while-sharing-the-item";

			if (throwable instanceof PrincipalException) {
				errorMessage = "you-do-not-have-permission-to-share-this-item";
			}

			JSONObject jsonObject = JSONUtil.put(
				"errorMessage",
				_language.get(themeDisplay.getLocale(), errorMessage));

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SharingEntryService _sharingEntryService;

	@Reference
	private UserLocalService _userLocalService;

}