/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.measurement.unit.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.DuplicateCPMeasurementUnitKeyException;
import com.liferay.commerce.product.exception.NoSuchCPMeasurementUnitException;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.product.service.CPMeasurementUnitService;
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
		"jakarta.portlet.name=" + CPPortletKeys.CP_MEASUREMENT_UNIT,
		"mvc.command.name=/cp_measurement_unit/edit_cp_measurement_unit"
	},
	service = MVCActionCommand.class
)
public class EditCPMeasurementUnitMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCPMeasurementUnit(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCPMeasurementUnits(actionRequest);
			}
			else if (cmd.equals("setPrimary")) {
				_setPrimary(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchCPMeasurementUnitException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof
						DuplicateCPMeasurementUnitKeyException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcRenderCommandName",
					"/cp_measurement_unit/edit_cp_measurement_unit");
			}
			else {
				throw exception;
			}
		}
	}

	private void _deleteCPMeasurementUnits(ActionRequest actionRequest)
		throws PortalException {

		long[] deleteCPMeasurementUnitIds = null;

		long cpMeasurementUnitId = ParamUtil.getLong(
			actionRequest, "cpMeasurementUnitId");

		if (cpMeasurementUnitId > 0) {
			deleteCPMeasurementUnitIds = new long[] {cpMeasurementUnitId};
		}
		else {
			deleteCPMeasurementUnitIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		for (long deleteCPMeasurementUnitId : deleteCPMeasurementUnitIds) {
			_cpMeasurementUnitService.deleteCPMeasurementUnit(
				deleteCPMeasurementUnitId);
		}
	}

	private void _setPrimary(ActionRequest actionRequest)
		throws PortalException {

		long cpMeasurementUnitId = ParamUtil.getLong(
			actionRequest, "cpMeasurementUnitId");

		boolean primary = ParamUtil.getBoolean(actionRequest, "primary");

		_cpMeasurementUnitService.setPrimary(cpMeasurementUnitId, primary);
	}

	private CPMeasurementUnit _updateCPMeasurementUnit(
			ActionRequest actionRequest)
		throws PortalException {

		long cpMeasurementUnitId = ParamUtil.getLong(
			actionRequest, "cpMeasurementUnitId");

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");
		String key = ParamUtil.getString(actionRequest, "key");
		double rate = ParamUtil.getDouble(actionRequest, "rate");
		boolean primary = ParamUtil.getBoolean(actionRequest, "primary");
		double priority = ParamUtil.getDouble(actionRequest, "priority");
		int type = ParamUtil.getInteger(actionRequest, "type");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPMeasurementUnit.class.getName(), actionRequest);

		CPMeasurementUnit cpMeasurementUnit = null;

		if (cpMeasurementUnitId <= 0) {
			cpMeasurementUnit = _cpMeasurementUnitService.addCPMeasurementUnit(
				null, nameMap, key, rate, primary, priority, type,
				serviceContext);
		}
		else {
			cpMeasurementUnit =
				_cpMeasurementUnitService.updateCPMeasurementUnit(
					null, cpMeasurementUnitId, nameMap, key, rate, primary,
					priority, type, serviceContext);
		}

		return cpMeasurementUnit;
	}

	@Reference
	private CPMeasurementUnitService _cpMeasurementUnitService;

	@Reference
	private Localization _localization;

}