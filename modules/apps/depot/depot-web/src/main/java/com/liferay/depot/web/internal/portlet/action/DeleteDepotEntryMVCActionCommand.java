/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.portlet.action;

import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.exception.DepotEntryStagedException;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.document.library.kernel.exception.RequiredFileEntryTypeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DepotPortletKeys.DEPOT_ADMIN,
		"mvc.command.name=/depot/delete_depot_entry"
	},
	service = MVCActionCommand.class
)
public class DeleteDepotEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_deleteDepotEntry(actionRequest);
		}
		catch (DepotEntryStagedException depotEntryStagedException) {
			if (_log.isDebugEnabled()) {
				_log.debug(depotEntryStagedException);
			}

			SessionErrors.add(actionRequest, DepotEntryStagedException.class);
		}
		catch (SystemException systemException) {
			if (_isRequiredFileEntryTypeException(systemException)) {
				SessionErrors.add(
					actionRequest, RequiredFileEntryTypeException.class);
			}
			else {
				throw systemException;
			}
		}
	}

	private void _deleteDepotEntry(ActionRequest actionRequest)
		throws PortalException {

		long depotEntryId = ParamUtil.getLong(actionRequest, "depotEntryId");

		if (depotEntryId > 0) {
			_depotEntryService.deleteDepotEntry(depotEntryId);
		}
		else {
			long[] deleteDepotEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");

			for (long deleteDepotEntryId : deleteDepotEntryIds) {
				_depotEntryService.deleteDepotEntry(deleteDepotEntryId);
			}
		}
	}

	private boolean _isRequiredFileEntryTypeException(Exception exception) {
		Throwable throwable = exception.getCause();

		while ((throwable != null) &&
			   !(throwable instanceof RequiredFileEntryTypeException)) {

			throwable = throwable.getCause();
		}

		if (throwable == null) {
			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteDepotEntryMVCActionCommand.class);

	@Reference
	private DepotEntryService _depotEntryService;

}