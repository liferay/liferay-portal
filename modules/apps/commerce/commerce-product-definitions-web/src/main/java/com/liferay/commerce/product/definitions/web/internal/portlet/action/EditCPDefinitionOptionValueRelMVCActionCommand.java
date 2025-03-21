/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPDefinitionOptionValueRelCPInstanceException;
import com.liferay.commerce.product.exception.CPDefinitionOptionValueRelKeyException;
import com.liferay.commerce.product.exception.CPDefinitionOptionValueRelPriceException;
import com.liferay.commerce.product.exception.CPDefinitionOptionValueRelQuantityException;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cp_definition_option_value_rel"
	},
	service = MVCActionCommand.class
)
public class EditCPDefinitionOptionValueRelMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCPDefinitionOptionValueRel(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCPDefinitionOptionValueRels(actionRequest);
			}
			else if (cmd.equals("deleteSku")) {
				_resetCPInstanceAndQuantity(actionRequest);
			}
			else if (cmd.equals("updatePreselected")) {
				_updatePreselected(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof
					CPDefinitionOptionValueRelCPInstanceException ||
				exception instanceof CPDefinitionOptionValueRelKeyException ||
				exception instanceof CPDefinitionOptionValueRelPriceException ||
				exception instanceof
					CPDefinitionOptionValueRelQuantityException) {

				hideDefaultErrorMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcRenderCommandName",
					"/cp_definitions/edit_cp_definition_option_value_rel");
			}
			else {
				_log.error(exception);
			}
		}
	}

	private CPDefinitionOptionValueRel _deleteCPDefinitionOptionValueRels(
			ActionRequest actionRequest)
		throws Exception {

		long cpDefinitionOptionValueRelId = ParamUtil.getLong(
			actionRequest, "cpDefinitionOptionValueRelId");

		if (cpDefinitionOptionValueRelId <= 0) {
			return null;
		}

		return _cpDefinitionOptionValueRelService.
			deleteCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);
	}

	private CPDefinitionOptionValueRel _resetCPInstanceAndQuantity(
			ActionRequest actionRequest)
		throws PortalException {

		long cpDefinitionOptionValueRelId = ParamUtil.getLong(
			actionRequest, "cpDefinitionOptionValueRelId");

		return _cpDefinitionOptionValueRelService.
			resetCPInstanceCPDefinitionOptionValueRel(
				cpDefinitionOptionValueRelId);
	}

	private CPDefinitionOptionValueRel _updateCPDefinitionOptionValueRel(
			ActionRequest actionRequest)
		throws Exception {

		long cpDefinitionOptionValueRelId = ParamUtil.getLong(
			actionRequest, "cpDefinitionOptionValueRelId");

		String key = ParamUtil.getString(actionRequest, "key");
		String label = ParamUtil.getString(actionRequest, "label");

		Map<Locale, String> nameMap = null;

		if (Validator.isNotNull(label)) {
			nameMap = HashMapBuilder.put(
				LocaleUtil.getDefault(), label
			).build();
		}
		else {
			nameMap = _localization.getLocalizationMap(actionRequest, "name");
		}

		double priority = ParamUtil.getDouble(actionRequest, "priority");

		if (Validator.isNull(key)) {
			String date = ParamUtil.getString(actionRequest, "date");
			String duration = ParamUtil.getString(actionRequest, "duration");
			String durationType = ParamUtil.getString(
				actionRequest, "durationType");
			String time = ParamUtil.getString(actionRequest, "time");
			String timeZone = ParamUtil.getString(actionRequest, "timeZone");

			key = StringUtil.replace(
				_friendlyURLNormalizer.normalizeWithPeriodsAndSlashes(
					StringBundler.concat(
						date, StringPool.DASH, time, StringPool.DASH, duration,
						StringPool.DASH, durationType, StringPool.DASH,
						timeZone)),
				'_', '-');
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPDefinitionOptionValueRel.class.getName(), actionRequest);

		if (cpDefinitionOptionValueRelId <= 0) {

			// Add commerce product definition option value rel

			long cpDefinitionOptionRelId = ParamUtil.getLong(
				actionRequest, "cpDefinitionOptionRelId");

			return _cpDefinitionOptionValueRelService.
				addCPDefinitionOptionValueRel(
					cpDefinitionOptionRelId, key, nameMap, priority,
					serviceContext);
		}

		// Update commerce product definition option value rel

		long cpInstanceId = 0;
		String unitOfMeasureKey = StringPool.BLANK;

		String composedCPInstanceId = ParamUtil.getString(
			actionRequest, "cpInstanceId");

		if (composedCPInstanceId.contains(StringPool.DASH)) {
			String[] idParts = composedCPInstanceId.split(StringPool.DASH);

			cpInstanceId = GetterUtil.getLong(idParts[0]);

			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
				_cpInstanceUnitOfMeasureLocalService.
					fetchCPInstanceUnitOfMeasure(
						GetterUtil.getLong(idParts[1]));

			if (cpInstanceUnitOfMeasure != null) {
				unitOfMeasureKey = cpInstanceUnitOfMeasure.getKey();
			}
		}
		else {
			cpInstanceId = ParamUtil.getLong(actionRequest, "cpInstanceId");
		}

		return _cpDefinitionOptionValueRelService.
			updateCPDefinitionOptionValueRel(
				cpDefinitionOptionValueRelId, cpInstanceId, key, nameMap,
				ParamUtil.getBoolean(actionRequest, "preselected"),
				_commercePriceFormatter.parse(
					actionRequest, false,
					CPDefinitionOptionValueRel.class.getName(), "price"),
				priority,
				_commerceOrderItemQuantityFormatter.parse(
					actionRequest, CPDefinitionOptionValueRel.class.getName(),
					"quantity"),
				unitOfMeasureKey, serviceContext);
	}

	private CPDefinitionOptionValueRel _updatePreselected(
			ActionRequest actionRequest)
		throws PortalException {

		long cpDefinitionOptionValueRelId = ParamUtil.getLong(
			actionRequest, "cpDefinitionOptionValueRelId");

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelService.getCPDefinitionOptionValueRel(
				cpDefinitionOptionValueRelId);

		if (cpDefinitionOptionValueRel.isPreselected()) {
			return _cpDefinitionOptionValueRelService.
				updateCPDefinitionOptionValueRelPreselected(
					cpDefinitionOptionValueRelId, false);
		}

		return _cpDefinitionOptionValueRelService.
			updateCPDefinitionOptionValueRelPreselected(
				cpDefinitionOptionValueRelId, true);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCPDefinitionOptionValueRelMVCActionCommand.class);

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CPDefinitionOptionValueRelService
		_cpDefinitionOptionValueRelService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private Localization _localization;

}