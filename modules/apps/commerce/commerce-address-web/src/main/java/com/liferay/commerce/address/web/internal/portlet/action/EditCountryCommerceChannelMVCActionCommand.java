/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.address.web.internal.portlet.action;

import com.liferay.commerce.address.web.internal.constants.CommerceAddressPortletKeys;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommerceAddressPortletKeys.COUNTRIES_MANAGEMENT_ADMIN,
		"mvc.command.name=/country/edit_country_commerce_channel"
	},
	service = MVCActionCommand.class
)
public class EditCountryCommerceChannelMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals("updateChannels")) {
				Callable<Object> commerceCountryChannelsCallable =
					new CommerceCountryChannelsCallable(actionRequest);

				TransactionInvokerUtil.invoke(
					_transactionConfig, commerceCountryChannelsCallable);
			}
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);

			throw new Exception(throwable);
		}
	}

	private void _updateChannels(ActionRequest actionRequest) throws Exception {
		long countryId = ParamUtil.getLong(actionRequest, "countryId");

		_commerceChannelRelService.deleteCommerceChannelRels(
			Country.class.getName(), countryId);

		boolean channelFilterEnabled = ParamUtil.getBoolean(
			actionRequest, "channelFilterEnabled");

		if (channelFilterEnabled) {
			long[] commerceChannelIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "commerceChannelIds"), 0L);

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				CommerceChannelRel.class.getName(), actionRequest);

			for (long commerceChannelId : commerceChannelIds) {
				if (commerceChannelId == 0) {
					continue;
				}

				_commerceChannelRelService.addCommerceChannelRel(
					Country.class.getName(), countryId, commerceChannelId,
					serviceContext);
			}
		}

		_countryService.updateGroupFilterEnabled(
			countryId, channelFilterEnabled);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCountryCommerceChannelMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private CountryService _countryService;

	private class CommerceCountryChannelsCallable implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			_updateChannels(_actionRequest);

			return null;
		}

		private CommerceCountryChannelsCallable(ActionRequest actionRequest) {
			_actionRequest = actionRequest;
		}

		private final ActionRequest _actionRequest;

	}

}