/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.helper.SaveFormInstanceMVCCommandHelper;
import com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util.BaseDDMFormMVCResourceCommand;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.AuthToken;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/dynamic_data_mapping_form/save_form_instance"
	},
	service = MVCResourceCommand.class
)
public class SaveFormInstanceMVCResourceCommand
	extends BaseDDMFormMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException {

		try {
			_authToken.checkCSRFToken(
				_portal.getHttpServletRequest(resourceRequest),
				SaveFormInstanceMVCResourceCommand.class.getName());

			DDMFormInstance formInstance = _saveFormInstanceInTransaction(
				resourceRequest, resourceResponse);

			writeResponse(resourceRequest, resourceResponse, formInstance);
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(throwable);
			}

			resourceResponse.setProperty(
				ResourceResponse.HTTP_STATUS_CODE,
				String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
		}
	}

	@Reference
	protected SaveFormInstanceMVCCommandHelper saveFormInstanceMVCCommandHelper;

	private DDMFormInstance _saveFormInstanceInTransaction(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Throwable {

		return TransactionInvokerUtil.invoke(
			_transactionConfig,
			() -> saveFormInstanceMVCCommandHelper.saveFormInstance(
				resourceRequest, resourceResponse));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SaveFormInstanceMVCResourceCommand.class);

	private static final TransactionConfig _transactionConfig;

	static {
		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setPropagation(Propagation.REQUIRES_NEW);
		builder.setRollbackForClasses(Exception.class);

		_transactionConfig = builder.build();
	}

	@Reference
	private AuthToken _authToken;

	@Reference
	private Portal _portal;

}