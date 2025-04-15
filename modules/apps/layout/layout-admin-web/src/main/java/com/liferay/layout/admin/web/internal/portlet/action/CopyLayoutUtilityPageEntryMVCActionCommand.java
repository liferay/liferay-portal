/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bárbara Cabrera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/copy_layout_utility_page_entry"
	},
	service = MVCActionCommand.class
)
public class CopyLayoutUtilityPageEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Callable<LayoutUtilityPageEntry> copyLayoutUtilityPageEntryCallable =
			new CopyLayoutUtilityPageEntryCallable(actionRequest);

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig, copyLayoutUtilityPageEntryCallable);
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(throwable, throwable);
			}

			SessionErrors.add(actionRequest, PortalException.class);
		}

		if (!SessionErrors.isEmpty(actionRequest)) {
			hideDefaultErrorMessage(actionRequest);

			sendRedirect(actionRequest, actionResponse);
		}
	}

	private LayoutUtilityPageEntry _copyLayoutUtilityPageEntry(
			ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long layoutUtilityPageEntryId = ParamUtil.getLong(
			actionRequest, "layoutUtilityPageEntryId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryService.copyLayoutUtilityPageEntry(
				themeDisplay.getScopeGroupId(), layoutUtilityPageEntryId,
				serviceContext);

		LayoutUtilityPageEntry sourceLayoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.getLayoutUtilityPageEntry(
				layoutUtilityPageEntryId);

		Layout sourceLayout = _layoutLocalService.getLayout(
			sourceLayoutUtilityPageEntry.getPlid());

		Layout targetLayout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		_layoutLocalService.copyLayoutContent(sourceLayout, targetLayout);
		_layoutLocalService.copyLayoutContent(
			sourceLayout, targetLayout.fetchDraftLayout());

		return layoutUtilityPageEntry;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CopyLayoutUtilityPageEntryMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Reference
	private LayoutUtilityPageEntryService _layoutUtilityPageEntryService;

	private class CopyLayoutUtilityPageEntryCallable
		implements Callable<LayoutUtilityPageEntry> {

		@Override
		public LayoutUtilityPageEntry call() throws Exception {
			return _copyLayoutUtilityPageEntry(_actionRequest);
		}

		private CopyLayoutUtilityPageEntryCallable(
			ActionRequest actionRequest) {

			_actionRequest = actionRequest;
		}

		private final ActionRequest _actionRequest;

	}

}