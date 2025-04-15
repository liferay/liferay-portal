/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPOptionValueKeyException;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.service.CPOptionValueService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
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
		"jakarta.portlet.name=" + CPPortletKeys.CP_OPTIONS,
		"mvc.command.name=/cp_options/edit_cp_option_value"
	},
	service = MVCActionCommand.class
)
public class EditCPOptionValueMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long cpOptionValueId = ParamUtil.getLong(
			actionRequest, "cpOptionValueId");

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

		String key = ParamUtil.getString(actionRequest, "key");

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
			CPOptionValue.class.getName(), actionRequest);

		try {
			_cpOptionValueService.updateCPOptionValue(
				cpOptionValueId, nameMap, priority, key, serviceContext);
		}
		catch (Exception exception) {
			if (exception instanceof CPOptionValueKeyException) {
				hideDefaultErrorMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcRenderCommandName", "/cp_options/edit_cp_option_value");
			}
			else {
				_log.error(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCPOptionValueMVCActionCommand.class);

	@Reference
	private CPOptionValueService _cpOptionValueService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private Localization _localization;

}