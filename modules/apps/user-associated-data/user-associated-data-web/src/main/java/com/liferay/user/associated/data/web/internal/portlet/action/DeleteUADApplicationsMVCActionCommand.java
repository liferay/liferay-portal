/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.portlet.action;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.user.associated.data.display.UADDisplay;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
		"mvc.command.name=/user_associated_data/delete_uad_applications"
	},
	service = MVCActionCommand.class
)
public class DeleteUADApplicationsMVCActionCommand
	extends BaseUADMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] groupIds = ParamUtil.getLongValues(actionRequest, "groupIds");

		long selectedUserId = getSelectedUserId(actionRequest);

		for (String applicationKey : getApplicationKeys(actionRequest)) {
			for (UADDisplay<?> uadDisplay :
					uadRegistry.getApplicationUADDisplays(applicationKey)) {

				UADAnonymizer<Object> uadAnonymizer =
					(UADAnonymizer<Object>)uadRegistry.getUADAnonymizer(
						uadDisplay.getTypeKey());

				UADDisplay<Object> objectUADDisplay =
					(UADDisplay<Object>)uadDisplay;

				List<Object> entities = objectUADDisplay.search(
					selectedUserId, groupIds, null, null, null,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

				for (Object entity : entities) {
					try {
						uadAnonymizer.delete(entity);
					}
					catch (Exception exception) {
						handleExceptions(
							actionRequest, actionResponse, exception,
							uadAnonymizer);
					}
				}
			}
		}

		doReviewableRedirect(actionRequest, actionResponse);
	}

}