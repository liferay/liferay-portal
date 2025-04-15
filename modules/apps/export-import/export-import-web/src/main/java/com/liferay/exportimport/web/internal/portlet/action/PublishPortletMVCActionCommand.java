/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.exception.StructureDuplicateStructureKeyException;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.exportimport.kernel.exception.LARFileException;
import com.liferay.exportimport.kernel.exception.LARFileNameException;
import com.liferay.exportimport.kernel.exception.LARFileSizeException;
import com.liferay.exportimport.kernel.exception.LARTypeException;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortletIdException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ExportImportPortletKeys.EXPORT_IMPORT,
		"mvc.command.name=/export_import/publish_portlet"
	},
	service = MVCActionCommand.class
)
public class PublishPortletMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Portlet portlet = null;

		try {
			portlet = ActionUtil.getPortlet(actionRequest);
		}
		catch (PrincipalException principalException) {
			SessionErrors.add(actionRequest, principalException.getClass());

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		actionRequest = ActionUtil.getWrappedActionRequest(actionRequest, null);

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (Validator.isNull(cmd)) {
			SessionMessages.add(
				actionRequest,
				_portal.getPortletId(actionRequest) +
					SessionMessages.KEY_SUFFIX_FORCE_SEND_REDIRECT);

			hideDefaultSuccessMessage(actionRequest);

			return;
		}

		try {
			if (cmd.equals("copy_from_live")) {
				_staging.copyFromLive(actionRequest, portlet);
			}
			else if (cmd.equals(Constants.PUBLISH_TO_LIVE)) {
				hideDefaultSuccessMessage(actionRequest);

				_staging.publishToLive(actionRequest, portlet);
			}
		}
		catch (Exception exception) {
			if (exception instanceof LARFileException ||
				exception instanceof LARFileNameException ||
				exception instanceof LARFileSizeException ||
				exception instanceof LARTypeException ||
				exception instanceof LocaleException ||
				exception instanceof NoSuchLayoutException ||
				exception instanceof PortletIdException ||
				exception instanceof PrincipalException ||
				exception instanceof StructureDuplicateStructureKeyException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				_log.error(exception);

				SessionErrors.add(
					actionRequest,
					PublishPortletMVCActionCommand.class.getName());
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PublishPortletMVCActionCommand.class);

	@Reference
	private Portal _portal;

	@Reference
	private Staging _staging;

}