/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.display.context;

import com.liferay.dispatch.constants.DispatchScreenNavigationConstants;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchLogService;
import com.liferay.dispatch.web.internal.display.context.helper.DispatchRequestHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import java.util.Date;

/**
 * @author guywandji
 * @author Alessio Antonio Rendina
 */
public class DispatchLogDisplayContext {

	public DispatchLogDisplayContext(
		DispatchLogService dispatchLogService, RenderRequest renderRequest) {

		_dispatchLogService = dispatchLogService;

		_dispatchRequestHelper = new DispatchRequestHelper(renderRequest);
	}

	public DispatchLog getDispatchLog() throws PortalException {
		return _dispatchLogService.getDispatchLog(
			ParamUtil.getLong(
				_dispatchRequestHelper.getRequest(), "dispatchLogId"));
	}

	public DispatchTrigger getDispatchTrigger() {
		return _dispatchRequestHelper.getDispatchTrigger();
	}

	public long getExecutionTimeMills() throws PortalException {
		DispatchLog dispatchLog = getDispatchLog();

		DispatchTaskStatus dispatchTaskStatus = DispatchTaskStatus.valueOf(
			dispatchLog.getStatus());

		Date startDate = dispatchLog.getStartDate();

		if (dispatchTaskStatus == DispatchTaskStatus.IN_PROGRESS) {
			return System.currentTimeMillis() - startDate.getTime();
		}

		Date endDate = dispatchLog.getEndDate();

		return endDate.getTime() - startDate.getTime();
	}

	public PortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			_dispatchRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String delta = ParamUtil.getString(
			_dispatchRequestHelper.getRequest(), "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String deltaEntry = ParamUtil.getString(
			_dispatchRequestHelper.getRequest(), "deltaEntry");

		if (Validator.isNotNull(deltaEntry)) {
			portletURL.setParameter("deltaEntry", deltaEntry);
		}

		String dispatchTriggerId = ParamUtil.getString(
			_dispatchRequestHelper.getRequest(), "dispatchTriggerId");

		if (Validator.isNotNull(dispatchTriggerId)) {
			portletURL.setParameter("dispatchTriggerId", dispatchTriggerId);
		}

		portletURL.setParameter(
			"mvcRenderCommandName", "/dispatch/edit_dispatch_trigger");

		String redirect = ParamUtil.getString(
			_dispatchRequestHelper.getRequest(), "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		portletURL.setParameter(
			"screenNavigationCategoryKey",
			DispatchScreenNavigationConstants.CATEGORY_KEY_DISPATCH_LOGS);

		return portletURL;
	}

	private final DispatchLogService _dispatchLogService;
	private final DispatchRequestHelper _dispatchRequestHelper;

}