/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.NoSuchChannelException;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_CHANNELS,
		"mvc.command.name=/commerce_channels/edit_commerce_channel_country"
	},
	service = MVCActionCommand.class
)
public class EditCommerceChannelCountryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceChannelRel(
					ParamUtil.getLong(actionRequest, "commerceChannelRelId"));
			}
			else if (cmd.equals(Constants.ADD_MULTIPLE)) {
				_addCommerceChannelRels(
					actionRequest,
					ParamUtil.getLong(actionRequest, "commerceChannelId"),
					ParamUtil.getLongValues(actionRequest, "countryIds"));
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchChannelException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				throw exception;
			}
		}
	}

	private void _addCommerceChannelRels(
			ActionRequest actionRequest, long commerceChannelId,
			long[] countryIds)
		throws Exception {

		_commerceChannelRelService.addCommerceChannelRels(
			Country.class.getName(), countryIds, commerceChannelId,
			ServiceContextFactory.getInstance(actionRequest));

		for (long countryId : countryIds) {
			_countryService.updateGroupFilterEnabled(countryId, true);
		}
	}

	private void _deleteCommerceChannelRel(long commerceChannelRelId)
		throws Exception {

		CommerceChannelRel commerceChannelRel =
			_commerceChannelRelService.getCommerceChannelRel(
				commerceChannelRelId);

		_commerceChannelRelService.deleteCommerceChannelRel(
			commerceChannelRelId);

		int count = _commerceChannelRelService.getCommerceChannelRelsCount(
			Country.class.getName(), commerceChannelRel.getClassPK(),
			StringPool.BLANK);

		if (count > 0) {
			return;
		}

		_countryService.updateGroupFilterEnabled(
			commerceChannelRel.getClassPK(), false);
	}

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private CountryService _countryService;

}