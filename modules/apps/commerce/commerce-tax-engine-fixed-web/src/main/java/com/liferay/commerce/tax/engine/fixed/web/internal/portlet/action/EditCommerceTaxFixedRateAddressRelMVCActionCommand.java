/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.tax.engine.fixed.configuration.CommerceTaxByAddressTypeConfiguration;
import com.liferay.commerce.tax.engine.fixed.exception.NoSuchTaxFixedRateAddressRelException;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateAddressRelService;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.text.NumberFormat;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_TAX_METHODS,
		"mvc.command.name=/commerce_tax_methods/edit_commerce_tax_fixed_rate_address_rel"
	},
	service = MVCActionCommand.class
)
public class EditCommerceTaxFixedRateAddressRelMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCommerceTaxFixedRateAddressRel(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceTaxFixedRateAddressRels(actionRequest);
			}
			else if (cmd.equals("updateConfiguration")) {
				_updateConfiguration(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchTaxFixedRateAddressRelException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw exception;
			}
		}
	}

	private void _deleteCommerceTaxFixedRateAddressRels(
			ActionRequest actionRequest)
		throws PortalException {

		long[] deleteCommerceTaxFixedRateAddressRelIds = null;

		long commerceTaxFixedRateAddressRelId = ParamUtil.getLong(
			actionRequest, "commerceTaxFixedRateAddressRelId");

		if (commerceTaxFixedRateAddressRelId > 0) {
			deleteCommerceTaxFixedRateAddressRelIds = new long[] {
				commerceTaxFixedRateAddressRelId
			};
		}
		else {
			deleteCommerceTaxFixedRateAddressRelIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "deleteCommerceTaxFixedRateAddressRelIds"),
				0L);
		}

		for (long deleteCommerceTaxFixedRateAddressRelId :
				deleteCommerceTaxFixedRateAddressRelIds) {

			_commerceTaxFixedRateAddressRelService.
				deleteCommerceTaxFixedRateAddressRel(
					deleteCommerceTaxFixedRateAddressRelId);
		}
	}

	private void _updateCommerceTaxFixedRateAddressRel(
			ActionRequest actionRequest)
		throws Exception {

		long commerceTaxFixedRateAddressRelId = ParamUtil.getLong(
			actionRequest, "commerceTaxFixedRateAddressRelId");

		long countryId = ParamUtil.getLong(actionRequest, "countryId");
		long regionId = ParamUtil.getLong(actionRequest, "regionId");
		String zip = ParamUtil.getString(actionRequest, "zip");
		String localizedRate = ParamUtil.getString(actionRequest, "rate");

		NumberFormat numberFormat = NumberFormat.getNumberInstance(
			_portal.getLocale(actionRequest));

		Number rate = numberFormat.parse(localizedRate);

		if (commerceTaxFixedRateAddressRelId > 0) {
			_commerceTaxFixedRateAddressRelService.
				updateCommerceTaxFixedRateAddressRel(
					commerceTaxFixedRateAddressRelId, countryId, regionId, zip,
					rate.doubleValue());
		}
		else {
			long commerceTaxMethodId = ParamUtil.getLong(
				actionRequest, "commerceTaxMethodId");
			long cpTaxCategoryId = ParamUtil.getLong(
				actionRequest, "CPTaxCategoryId");

			CommerceTaxMethod commerceTaxMethod =
				_commerceTaxMethodService.getCommerceTaxMethod(
					commerceTaxMethodId);

			_commerceTaxFixedRateAddressRelService.
				addCommerceTaxFixedRateAddressRel(
					commerceTaxMethod.getGroupId(),
					commerceTaxMethod.getCommerceTaxMethodId(), cpTaxCategoryId,
					countryId, regionId, zip, rate.doubleValue());
		}
	}

	private void _updateConfiguration(ActionRequest actionRequest)
		throws Exception {

		long commerceTaxMethodId = ParamUtil.getLong(
			actionRequest, "commerceTaxMethodId");

		CommerceTaxMethod commerceTaxMethod =
			_commerceTaxMethodService.getCommerceTaxMethod(commerceTaxMethodId);

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				commerceTaxMethod.getGroupId(),
				CommerceTaxByAddressTypeConfiguration.class.getName()));

		boolean applyToShipping = ParamUtil.getBoolean(
			actionRequest, "applyTaxTo");

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		modifiableSettings.setValue(
			"taxAppliedToShippingAddress", String.valueOf(applyToShipping));

		modifiableSettings.store();
	}

	@Reference
	private CommerceTaxFixedRateAddressRelService
		_commerceTaxFixedRateAddressRelService;

	@Reference
	private CommerceTaxMethodService _commerceTaxMethodService;

	@Reference
	private Portal _portal;

}