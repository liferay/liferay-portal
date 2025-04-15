/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.portlet.action;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;
import com.liferay.user.associated.data.anonymizer.UADAnonymousUserProvider;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.user.associated.data.web.internal.registry.UADRegistry;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
		"mvc.command.name=/user_associated_data/anonymize_nonreviewable_uad_data"
	},
	service = MVCActionCommand.class
)
public class AnonymizeNonreviewableUADDataMVCActionCommand
	extends BaseUADMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Collection<UADAnonymizer<?>> uadAnonymizers =
			_uadRegistry.getNonreviewableUADAnonymizers();

		for (UADAnonymizer<?> uadAnonymizer : uadAnonymizers) {
			User selectedUser = getSelectedUser(actionRequest);

			uadAnonymizer.autoAnonymizeAll(
				selectedUser.getUserId(),
				_uadAnonymousUserProvider.getAnonymousUser(
					selectedUser.getCompanyId()));
		}

		doNonreviewableRedirect(actionRequest, actionResponse);
	}

	@Reference
	private UADAnonymousUserProvider _uadAnonymousUserProvider;

	@Reference
	private UADRegistry _uadRegistry;

}