/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.portlet.action;

import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsPortletKeys;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KaleoFormsPortletKeys.KALEO_FORMS_ADMIN,
		"mvc.command.name=/kaleo_forms_admin/delete_record"
	},
	service = MVCActionCommand.class
)
public class DeleteRecordMVCActionCommand
	extends BaseKaleoFormsMVCActionCommand {

	/**
	 * Deletes the <code>DDLRecord</code> (in the
	 * <code>com.liferay.dynamic.data.lists.api</code> module) associated with
	 * the record IDs from the action request. This method also deletes the
	 * <code>WorkflowInstanceLink</code>s (in
	 * <code>com.liferay.portal.kernel</code>) associated with each record ID
	 * and matching name of the <code>KaleoProcess</code> implementation (in the
	 * <code>com.liferay.portal.workflow.kaleo.forms.api</code> module). This
	 * method uses <code>TransactionInvokerUtil</code> (in
	 * <code>com.liferay.portal.kernel</code>) to ensure all tasks are performed
	 * in a single transaction.
	 *
	 * @param  actionRequest the request from which to get the request
	 *         parameters
	 * @param  actionResponse the response to receive the render parameters
	 * @throws Exception if an exception occurred
	 */
	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDLRecord.class.getName(),
			portal.getUploadPortletRequest(actionRequest));

		checkKaleoProcessPermission(serviceContext, ActionKeys.DELETE);

		final ThemeDisplay themeDisplay =
			(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

		long[] ddlRecordIds = _getDDLRecordIds(actionRequest);

		for (final long ddlRecordId : ddlRecordIds) {
			try {
				Callable<Void> callable = new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						ddlRecordLocalService.deleteRecord(ddlRecordId);

						_workflowInstanceLinkLocalService.
							deleteWorkflowInstanceLinks(
								themeDisplay.getCompanyId(),
								themeDisplay.getScopeGroupId(),
								KaleoProcess.class.getName(), ddlRecordId);

						return null;
					}

				};

				TransactionInvokerUtil.invoke(_transactionConfig, callable);
			}
			catch (Throwable throwable) {
				if (throwable instanceof PortalException) {
					throw (PortalException)throwable;
				}

				throw new SystemException(throwable);
			}
		}
	}

	/**
	 * Returns an array of the DDL record IDs obtained from the action request.
	 *
	 * @param  actionRequest the request from which to get the request
	 *         parameters
	 * @return an array of the DDL record IDs
	 */
	private long[] _getDDLRecordIds(ActionRequest actionRequest) {
		long ddlRecordId = ParamUtil.getLong(actionRequest, "ddlRecordId");

		if (ddlRecordId > 0) {
			return new long[] {ddlRecordId};
		}

		return StringUtil.split(
			ParamUtil.getString(actionRequest, "ddlRecordIds"), 0L);
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}