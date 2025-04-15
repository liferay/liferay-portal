/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPDefinitionSpecificationOptionValueKeyException;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionSpecificationOptionValueException;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CPSpecificationOptionListTypeDefinitionRel;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueService;
import com.liferay.commerce.product.service.CPSpecificationOptionListTypeDefinitionRelLocalService;
import com.liferay.commerce.product.service.CPSpecificationOptionService;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cp_definition_specification_option_value"
	},
	service = MVCActionCommand.class
)
public class EditCPDefinitionSpecificationOptionValueMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) ||
				cmd.equals(Constants.ADD_MULTIPLE)) {

				_addCPDefinitionSpecificationOptionValues(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCPDefinitionSpecificationOptionValues(actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				_updateCPDefinitionSpecificationOptionValue(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof
					NoSuchCPDefinitionSpecificationOptionValueException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof
						CPDefinitionSpecificationOptionValueKeyException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else {
				throw exception;
			}
		}
	}

	private void _addCPDefinitionSpecificationOptionValues(
			ActionRequest actionRequest)
		throws Exception {

		long[] addCPSpecificationOptionIds = null;

		long cpDefinitionId = ParamUtil.getLong(
			actionRequest, "cpDefinitionId");

		long cpSpecificationOptionId = ParamUtil.getLong(
			actionRequest, "cpSpecificationOptionId");

		if (cpSpecificationOptionId > 0) {
			addCPSpecificationOptionIds = new long[] {cpSpecificationOptionId};
		}
		else {
			addCPSpecificationOptionIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "cpSpecificationOptionIds"),
				0L);
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPDefinitionSpecificationOptionValue.class.getName(),
			actionRequest);

		for (int i = 0; i < addCPSpecificationOptionIds.length; i++) {
			cpSpecificationOptionId = addCPSpecificationOptionIds[i];

			CPSpecificationOption cpSpecificationOption =
				_cpSpecificationOptionService.getCPSpecificationOption(
					cpSpecificationOptionId);

			_cpDefinitionSpecificationOptionValueService.
				addCPDefinitionSpecificationOptionValue(
					StringPool.BLANK, cpDefinitionId, cpSpecificationOptionId,
					cpSpecificationOption.getCPOptionCategoryId(), i, null,
					cpSpecificationOption.isVisible(), serviceContext);
		}
	}

	private void _deleteCPDefinitionSpecificationOptionValues(
			ActionRequest actionRequest)
		throws Exception {

		long[] deleteCPDefinitionSpecificationOptionValueIds = null;

		long cpDefinitionSpecificationOptionValueId = ParamUtil.getLong(
			actionRequest, "cpDefinitionSpecificationOptionValueId");

		if (cpDefinitionSpecificationOptionValueId > 0) {
			deleteCPDefinitionSpecificationOptionValueIds = new long[] {
				cpDefinitionSpecificationOptionValueId
			};
		}
		else {
			deleteCPDefinitionSpecificationOptionValueIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest,
					"deleteCPDefinitionSpecificationOptionValueIds"),
				0L);
		}

		for (long deleteCPDefinitionSpecificationOptionValueId :
				deleteCPDefinitionSpecificationOptionValueIds) {

			_cpDefinitionSpecificationOptionValueService.
				deleteCPDefinitionSpecificationOptionValue(
					deleteCPDefinitionSpecificationOptionValueId);
		}
	}

	private List<ListTypeEntry> _getListTypeEntries(
		long cpSpecificationOptionId) {

		long[] listTypeDefinitionIds = TransformUtil.transformToLongArray(
			_cpSpecificationOptionListTypeDefinitionRelLocalService.
				getCPSpecificationOptionListTypeDefinitionRels(
					cpSpecificationOptionId),
			CPSpecificationOptionListTypeDefinitionRel::
				getListTypeDefinitionId);

		if (ArrayUtil.isEmpty(listTypeDefinitionIds)) {
			return Collections.emptyList();
		}

		return _listTypeEntryLocalService.getListTypeEntries(
			listTypeDefinitionIds);
	}

	private Map<Locale, String> _getValueMap(
			ActionRequest actionRequest,
			long cpDefinitionSpecificationOptionValueId)
		throws Exception {

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				_cpDefinitionSpecificationOptionValueService.
					getCPDefinitionSpecificationOptionValue(
						cpDefinitionSpecificationOptionValueId);

		List<ListTypeEntry> listTypeEntries = _getListTypeEntries(
			cpDefinitionSpecificationOptionValue.getCPSpecificationOptionId());

		if (ListUtil.isEmpty(listTypeEntries)) {
			return _localization.getLocalizationMap(actionRequest, "value");
		}

		String value = ParamUtil.getString(
			actionRequest, "listTypeEntriesSelect");

		for (ListTypeEntry listTypeEntry : listTypeEntries) {
			if (value.equals(listTypeEntry.getKey())) {
				return listTypeEntry.getNameMap();
			}
		}

		return new HashMap<>();
	}

	private CPDefinitionSpecificationOptionValue
			_updateCPDefinitionSpecificationOptionValue(
				ActionRequest actionRequest)
		throws Exception {

		long cpDefinitionSpecificationOptionValueId = ParamUtil.getLong(
			actionRequest, "cpDefinitionSpecificationOptionValueId");

		long cpOptionCategoryId = ParamUtil.getLong(
			actionRequest, "CPOptionCategoryId");
		String key = ParamUtil.getString(actionRequest, "key");
		double priority = ParamUtil.getDouble(actionRequest, "priority");
		Map<Locale, String> valueMap = _getValueMap(
			actionRequest, cpDefinitionSpecificationOptionValueId);
		boolean visible = ParamUtil.getBoolean(actionRequest, "visible");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPDefinitionSpecificationOptionValue.class.getName(),
			actionRequest);

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				_cpDefinitionSpecificationOptionValueService.
					getCPDefinitionSpecificationOptionValue(
						cpDefinitionSpecificationOptionValueId);

		return _cpDefinitionSpecificationOptionValueService.
			updateCPDefinitionSpecificationOptionValue(
				cpDefinitionSpecificationOptionValue.getExternalReferenceCode(),
				cpDefinitionSpecificationOptionValueId, cpOptionCategoryId, key,
				priority, valueMap, visible, serviceContext);
	}

	@Reference
	private CPDefinitionSpecificationOptionValueService
		_cpDefinitionSpecificationOptionValueService;

	@Reference
	private CPSpecificationOptionListTypeDefinitionRelLocalService
		_cpSpecificationOptionListTypeDefinitionRelLocalService;

	@Reference
	private CPSpecificationOptionService _cpSpecificationOptionService;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private Localization _localization;

}