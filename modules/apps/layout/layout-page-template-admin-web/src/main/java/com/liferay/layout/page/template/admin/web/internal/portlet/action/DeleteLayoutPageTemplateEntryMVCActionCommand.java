/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/delete_layout_page_template_entry"
	},
	service = MVCActionCommand.class
)
public class DeleteLayoutPageTemplateEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		List<Long> deleteLayoutPageTemplateEntryIdsList = new ArrayList<>();
		Set<Class<?>> exceptionClasses = new HashSet<>();

		long[] deleteLayoutPageTemplateEntryIds = null;

		long layoutPageTemplateEntryId = ParamUtil.getLong(
			actionRequest, "layoutPageTemplateEntryId");

		if (layoutPageTemplateEntryId > 0) {
			deleteLayoutPageTemplateEntryIds = new long[] {
				layoutPageTemplateEntryId
			};
		}
		else {
			deleteLayoutPageTemplateEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		for (long deleteLayoutPageTemplateEntryId :
				deleteLayoutPageTemplateEntryIds) {

			try {
				_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
					deleteLayoutPageTemplateEntryId);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				deleteLayoutPageTemplateEntryIdsList.add(
					deleteLayoutPageTemplateEntryId);

				exceptionClasses.add(exception.getClass());

				Throwable throwable = exception.getCause();

				if (throwable != null) {
					exceptionClasses.add(throwable.getClass());
				}
			}
		}

		if (deleteLayoutPageTemplateEntryIds.length ==
				deleteLayoutPageTemplateEntryIdsList.size()) {

			SessionErrors.add(actionRequest, PortalException.class);

			for (Class<?> clazz : exceptionClasses) {
				SessionErrors.add(actionRequest, clazz);
			}

			hideDefaultErrorMessage(actionRequest);

			sendRedirect(actionRequest, actionResponse);

			return;
		}

		String tabs1 = ParamUtil.getString(actionRequest, "tabs1");

		if (!Objects.equals(tabs1, "display-page-templates")) {
			sendRedirect(actionRequest, actionResponse);

			return;
		}

		hideDefaultSuccessMessage(actionRequest);

		MultiSessionMessages.add(
			actionRequest, "displayPageTemplateDeleted",
			_language.format(
				_portal.getHttpServletRequest(actionRequest),
				"you-successfully-deleted-x-display-page-templates",
				new Object[] {
					deleteLayoutPageTemplateEntryIds.length -
						deleteLayoutPageTemplateEntryIdsList.size()
				}));

		sendRedirect(actionRequest, actionResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteLayoutPageTemplateEntryMVCActionCommand.class);

	@Reference
	private Language _language;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private Portal _portal;

}