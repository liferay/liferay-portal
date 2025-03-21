/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.availability.estimate.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.exception.NoSuchAvailabilityEstimateException;
import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.service.CommerceAvailabilityEstimateService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_AVAILABILITY_ESTIMATE,
		"mvc.command.name=/commerce_availability_estimate/edit_commerce_availability_estimate"
	},
	service = MVCActionCommand.class
)
public class EditCommerceAvailabilityEstimateMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceAvailabilityEstimates(actionRequest);
			}
			else if (cmd.equals(Constants.ADD) ||
					 cmd.equals(Constants.UPDATE)) {

				_updateCommerceAvailabilityEstimate(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchAvailabilityEstimateException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				throw exception;
			}
		}
	}

	private void _deleteCommerceAvailabilityEstimates(
			ActionRequest actionRequest)
		throws PortalException {

		long[] deleteCommerceAvailabilityEstimateIds = null;

		long commerceAvailabilityEstimateId = ParamUtil.getLong(
			actionRequest, "commerceAvailabilityEstimateId");

		if (commerceAvailabilityEstimateId > 0) {
			deleteCommerceAvailabilityEstimateIds = new long[] {
				commerceAvailabilityEstimateId
			};
		}
		else {
			deleteCommerceAvailabilityEstimateIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		for (long deleteCommerceAvailabilityEstimateId :
				deleteCommerceAvailabilityEstimateIds) {

			_commerceAvailabilityEstimateService.
				deleteCommerceAvailabilityEstimate(
					deleteCommerceAvailabilityEstimateId);
		}
	}

	private void _updateCommerceAvailabilityEstimate(
			ActionRequest actionRequest)
		throws PortalException {

		long commerceAvailabilityEstimateId = ParamUtil.getLong(
			actionRequest, "commerceAvailabilityEstimateId");

		Map<Locale, String> titleMap = _localization.getLocalizationMap(
			actionRequest, "title");
		double priority = ParamUtil.getDouble(actionRequest, "priority");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CommerceAvailabilityEstimate.class.getName(), actionRequest);

		if (commerceAvailabilityEstimateId <= 0) {
			_commerceAvailabilityEstimateService.
				addCommerceAvailabilityEstimate(
					titleMap, priority, serviceContext);
		}
		else {
			_commerceAvailabilityEstimateService.
				updateCommerceAvailabilityEstimate(
					commerceAvailabilityEstimateId, titleMap, priority,
					serviceContext);
		}
	}

	@Reference
	private CommerceAvailabilityEstimateService
		_commerceAvailabilityEstimateService;

	@Reference
	private Localization _localization;

}