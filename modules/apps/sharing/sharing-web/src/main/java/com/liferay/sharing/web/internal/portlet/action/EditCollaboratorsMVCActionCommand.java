/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.sharing.service.SharingEntryService;
import com.liferay.sharing.web.internal.constants.SharingPortletKeys;
import com.liferay.sharing.web.internal.display.SharingEntryPermissionDisplayAction;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.text.DateFormat;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SharingPortletKeys.MANAGE_COLLABORATORS,
		"mvc.command.name=/sharing/edit_collaborators"
	},
	service = MVCActionCommand.class
)
public class EditCollaboratorsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			themeDisplay.getLocale(), getClass());

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					_manageCollaborators(
						actionRequest, actionResponse, resourceBundle);

					return null;
				});
		}
		catch (Throwable throwable) {
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			String errorMessage =
				"an-unexpected-error-occurred-while-updating-permissions";

			if (throwable instanceof PrincipalException) {
				errorMessage =
					"you-do-not-have-permission-to-update-these-permissions";
			}

			JSONObject jsonObject = JSONUtil.put(
				"errorMessage",
				_language.get(themeDisplay.getLocale(), errorMessage));

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}
	}

	private DateFormat _getDateFormat(Locale locale) {
		return DateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd", locale);
	}

	private Map<Long, Collection<SharingEntryAction>> _getSharingEntryActions(
		ActionRequest actionRequest) {

		Map<Long, Collection<SharingEntryAction>> sharingEntryActions =
			new HashMap<>();

		String[] sharingEntryIdActionIdPairs = ParamUtil.getParameterValues(
			actionRequest, "sharingEntryIdActionIdPairs", new String[0], false);

		for (String sharingEntryIdActionIdPair : sharingEntryIdActionIdPairs) {
			String[] parts = StringUtil.split(sharingEntryIdActionIdPair);

			long sharingEntryId = Long.valueOf(parts[0]);

			SharingEntryPermissionDisplayAction
				sharingEntryPermissionDisplayAction =
					SharingEntryPermissionDisplayAction.parseFromActionId(
						parts[1]);

			sharingEntryActions.put(
				sharingEntryId,
				sharingEntryPermissionDisplayAction.getSharingEntryActions());
		}

		return sharingEntryActions;
	}

	private Map<Long, Date> _getSharingEntryExpirationDates(
		ActionRequest actionRequest, ResourceBundle resourceBundle) {

		Map<Long, Date> expirationDates = new HashMap<>();

		String[] sharingEntryIdExpirationDatePairs =
			ParamUtil.getParameterValues(
				actionRequest, "sharingEntryIdExpirationDatePairs",
				new String[0], false);

		for (String sharingEntryIdExpirationDatePair :
				sharingEntryIdExpirationDatePairs) {

			String[] parts = StringUtil.split(sharingEntryIdExpirationDatePair);

			long sharingEntryId = Long.valueOf(parts[0]);

			Date expirationDate = null;

			if (parts.length > 1) {
				expirationDate = GetterUtil.getDate(
					parts[1], _getDateFormat(resourceBundle.getLocale()), null);
			}

			expirationDates.put(sharingEntryId, expirationDate);
		}

		return expirationDates;
	}

	private Set<Long> _getSharingEntryIdsToDelete(ActionRequest actionRequest) {
		Set<Long> sharingEntryIdsToDelete = new HashSet<>();

		long[] deleteSharingEntryIds = ParamUtil.getLongValues(
			actionRequest, "deleteSharingEntryIds");

		for (Long sharingEntryId : deleteSharingEntryIds) {
			sharingEntryIdsToDelete.add(sharingEntryId);
		}

		return sharingEntryIdsToDelete;
	}

	private Map<Long, Boolean> _getSharingEntryShareables(
		ActionRequest actionRequest) {

		Map<Long, Boolean> shareables = new HashMap<>();

		String[] sharingEntryIdShareablePairs = ParamUtil.getParameterValues(
			actionRequest, "sharingEntryIdShareablePairs", new String[0],
			false);

		for (String sharingEntryIdShareablePair :
				sharingEntryIdShareablePairs) {

			String[] parts = StringUtil.split(sharingEntryIdShareablePair);

			long sharingEntryId = Long.valueOf(parts[0]);

			boolean shareable = GetterUtil.getBoolean(parts[1]);

			shareables.put(sharingEntryId, shareable);
		}

		return shareables;
	}

	private void _manageCollaborators(
			ActionRequest actionRequest, ActionResponse actionResponse,
			ResourceBundle resourceBundle)
		throws IOException, PortalException {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		Map<Long, Collection<SharingEntryAction>> sharingEntryActions =
			_getSharingEntryActions(actionRequest);

		Set<Long> toEditSharingEntryIds = new HashSet<>(
			sharingEntryActions.keySet());

		Map<Long, Date> sharingEntryExpirationDates =
			_getSharingEntryExpirationDates(actionRequest, resourceBundle);

		toEditSharingEntryIds.addAll(sharingEntryExpirationDates.keySet());

		Map<Long, Boolean> sharingEntryShareables = _getSharingEntryShareables(
			actionRequest);

		toEditSharingEntryIds.addAll(sharingEntryShareables.keySet());

		Set<Long> sharingEntryIdsToDelete = _getSharingEntryIdsToDelete(
			actionRequest);

		toEditSharingEntryIds.removeAll(sharingEntryIdsToDelete);

		for (Long sharingEntryId : toEditSharingEntryIds) {
			SharingEntry sharingEntry =
				_sharingEntryLocalService.getSharingEntry(sharingEntryId);

			_sharingEntryService.updateSharingEntry(
				sharingEntryId,
				sharingEntryActions.getOrDefault(
					sharingEntryId,
					SharingEntryAction.getSharingEntryActions(
						sharingEntry.getActionIds())),
				sharingEntryShareables.getOrDefault(
					sharingEntryId, sharingEntry.isShareable()),
				sharingEntryExpirationDates.getOrDefault(
					sharingEntryId, sharingEntry.getExpirationDate()),
				serviceContext);
		}

		for (long sharingEntryId : sharingEntryIdsToDelete) {
			_sharingEntryService.deleteSharingEntry(
				sharingEntryId, serviceContext);
		}

		hideDefaultSuccessMessage(actionRequest);

		JSONObject jsonObject = JSONUtil.put(
			"successMessage",
			_language.get(resourceBundle, "permissions-changed"));

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

	@Reference
	private SharingEntryService _sharingEntryService;

}