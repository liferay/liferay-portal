/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.web.internal.portlet.action;

import com.liferay.address.web.internal.constants.AddressPortletKeys;
import com.liferay.portal.kernel.exception.CountryA2Exception;
import com.liferay.portal.kernel.exception.CountryA3Exception;
import com.liferay.portal.kernel.exception.CountryNameException;
import com.liferay.portal.kernel.exception.CountryNumberException;
import com.liferay.portal.kernel.exception.CountryTitleException;
import com.liferay.portal.kernel.exception.DuplicateCountryException;
import com.liferay.portal.kernel.exception.NoSuchCountryException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AddressPortletKeys.COUNTRIES_MANAGEMENT_ADMIN,
		"mvc.command.name=/address/edit_country"
	},
	service = MVCActionCommand.class
)
public class EditCountryMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String a2 = ParamUtil.getString(actionRequest, "a2");
		String a3 = ParamUtil.getString(actionRequest, "a3");
		boolean active = ParamUtil.getBoolean(actionRequest, "active");
		boolean billingAllowed = ParamUtil.getBoolean(
			actionRequest, "billingAllowed");
		String idd = ParamUtil.getString(actionRequest, "idd");
		String name = ParamUtil.getString(actionRequest, "name");
		String number = ParamUtil.getString(actionRequest, "number");
		double position = ParamUtil.getDouble(actionRequest, "position");
		boolean shippingAllowed = ParamUtil.getBoolean(
			actionRequest, "shippingAllowed");
		boolean subjectToVAT = ParamUtil.getBoolean(
			actionRequest, "subjectToVAT");

		try {
			Country country = null;

			String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

			if (cmd.equals(Constants.ADD)) {
				country = _countryService.addCountry(
					a2, a3, active, billingAllowed, idd, name, number, position,
					shippingAllowed, subjectToVAT, false,
					ServiceContextFactory.getInstance(
						Country.class.getName(), actionRequest));

				actionRequest.setAttribute(
					WebKeys.REDIRECT,
					HttpComponentsUtil.setParameter(
						ParamUtil.getString(actionRequest, "redirect"),
						actionResponse.getNamespace() + "countryId",
						country.getCountryId()));
			}
			else if (cmd.equals(Constants.UPDATE)) {
				long countryId = ParamUtil.getLong(actionRequest, "countryId");

				country = _countryService.updateCountry(
					countryId, a2, a3, active, billingAllowed, idd, name,
					number, position, shippingAllowed, subjectToVAT);
			}

			if (country != null) {
				Map<String, String> titleMap = new HashMap<>();

				Map<Locale, String> titleLocalizationMap =
					_localization.getLocalizationMap(actionRequest, "title");

				for (Map.Entry<Locale, String> entry :
						titleLocalizationMap.entrySet()) {

					titleMap.put(
						_language.getLanguageId(entry.getKey()),
						entry.getValue());
				}

				_countryLocalService.updateCountryLocalizations(
					country, titleMap);
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof NoSuchCountryException ||
				throwable instanceof PrincipalException) {

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (throwable instanceof CountryA2Exception ||
					 throwable instanceof CountryA3Exception ||
					 throwable instanceof CountryNameException ||
					 throwable instanceof CountryNumberException ||
					 throwable instanceof CountryTitleException ||
					 throwable instanceof DuplicateCountryException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				sendRedirect(actionRequest, actionResponse);
			}

			throw new PortletException(throwable);
		}
	}

	@Reference
	private CountryLocalService _countryLocalService;

	@Reference
	private CountryService _countryService;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

}