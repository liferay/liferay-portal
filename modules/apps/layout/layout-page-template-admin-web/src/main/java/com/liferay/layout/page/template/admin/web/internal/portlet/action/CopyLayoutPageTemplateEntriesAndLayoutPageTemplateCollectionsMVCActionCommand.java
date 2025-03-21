/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
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
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/copy_layout_page_template_entries_and_layout_page_template_collections"
	},
	service = MVCActionCommand.class
)
public class
	CopyLayoutPageTemplateEntriesAndLayoutPageTemplateCollectionsMVCActionCommand
		extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] layoutPageTemplateCollectionsId = ParamUtil.getLongValues(
			actionRequest, "layoutPageTemplateCollectionsIds");
		long[] layoutPageTemplateEntriesId = ParamUtil.getLongValues(
			actionRequest, "layoutPageTemplateEntriesIds");
		long layoutParentPageTemplateCollectionId = ParamUtil.getLong(
			actionRequest, "layoutParentPageTemplateCollectionId");
		boolean copyPermissions = ParamUtil.getBoolean(
			actionRequest, "copyPermissions");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		Callable<Void>
			copyLayoutPageTemplateEntryAndLayoutPageTemplateCollectionCallable =
				new CopyLayoutPageTemplateEntriesAndLayoutPageTemplateCollectionsMVCActionCommand.CopyLayoutPageTemplateEntryAndLayoutPageTemplateCollectionCallable(
					copyPermissions, layoutPageTemplateCollectionsId,
					layoutPageTemplateEntriesId,
					layoutParentPageTemplateCollectionId, serviceContext,
					themeDisplay);

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				copyLayoutPageTemplateEntryAndLayoutPageTemplateCollectionCallable);
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

	private Void _copyLayoutPageTemplateEntryAndLayoutPageTemplateCollection(
			boolean copyPermissions, long[] layoutPageTemplateCollectionsId,
			long[] layoutPageTemplateEntriesId,
			long layoutParentPageTemplateCollectionId,
			ServiceContext serviceContext, ThemeDisplay themeDisplay)
		throws Exception {

		for (long layoutPageTemplateCollectionId :
				layoutPageTemplateCollectionsId) {

			_layoutPageTemplateCollectionService.
				copyLayoutPageTemplateCollection(
					themeDisplay.getScopeGroupId(),
					layoutPageTemplateCollectionId,
					layoutParentPageTemplateCollectionId, copyPermissions,
					serviceContext);
		}

		for (long layoutPageTemplateEntryId : layoutPageTemplateEntriesId) {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.copyLayoutPageTemplateEntry(
					themeDisplay.getScopeGroupId(),
					layoutParentPageTemplateCollectionId,
					layoutPageTemplateEntryId, copyPermissions, serviceContext);

			LayoutPageTemplateEntry sourceLayoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
					layoutPageTemplateEntryId);

			Layout sourceLayout = _layoutLocalService.getLayout(
				sourceLayoutPageTemplateEntry.getPlid());

			Layout targetLayout = _layoutLocalService.getLayout(
				layoutPageTemplateEntry.getPlid());

			_layoutLocalService.copyLayoutContent(sourceLayout, targetLayout);
			_layoutLocalService.copyLayoutContent(
				sourceLayout, targetLayout.fetchDraftLayout());
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CopyLayoutPageTemplateEntriesAndLayoutPageTemplateCollectionsMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	private class
		CopyLayoutPageTemplateEntryAndLayoutPageTemplateCollectionCallable
			implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			return _copyLayoutPageTemplateEntryAndLayoutPageTemplateCollection(
				_copyPermissions, _layoutPageTemplateCollectionsId,
				_layoutPageTemplateEntriesId,
				_layoutParentPageTemplateCollectionId, _serviceContext,
				_themeDisplay);
		}

		private CopyLayoutPageTemplateEntryAndLayoutPageTemplateCollectionCallable(
			boolean copyPermissions, long[] layoutPageTemplateCollectionsId,
			long[] layoutPageTemplateEntriesId,
			long layoutParentPageTemplateCollectionId,
			ServiceContext serviceContext, ThemeDisplay themeDisplay) {

			_copyPermissions = copyPermissions;
			_layoutPageTemplateCollectionsId = layoutPageTemplateCollectionsId;
			_layoutPageTemplateEntriesId = layoutPageTemplateEntriesId;
			_layoutParentPageTemplateCollectionId =
				layoutParentPageTemplateCollectionId;
			_serviceContext = serviceContext;
			_themeDisplay = themeDisplay;
		}

		private final boolean _copyPermissions;
		private final long[] _layoutPageTemplateCollectionsId;
		private final long[] _layoutPageTemplateEntriesId;
		private final long _layoutParentPageTemplateCollectionId;
		private final ServiceContext _serviceContext;
		private final ThemeDisplay _themeDisplay;

	}

}