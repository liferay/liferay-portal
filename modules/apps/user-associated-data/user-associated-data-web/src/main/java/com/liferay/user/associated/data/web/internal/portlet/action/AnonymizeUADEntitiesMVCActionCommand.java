/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.portlet.action;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;
import com.liferay.user.associated.data.anonymizer.UADAnonymousUserProvider;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.web.internal.display.UADHierarchyDisplay;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noah Sherrill
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
		"mvc.command.name=/user_associated_data/anonymize_uad_entities"
	},
	service = MVCActionCommand.class
)
public class AnonymizeUADEntitiesMVCActionCommand
	extends BaseUADMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		User selectedUser = getSelectedUser(actionRequest);

		User anonymousUser = _uadAnonymousUserProvider.getAnonymousUser(
			selectedUser.getCompanyId());

		String applicationKey = ParamUtil.getString(
			actionRequest, "applicationKey");

		UADHierarchyDisplay uadHierarchyDisplay =
			uadRegistry.getUADHierarchyDisplay(applicationKey);

		for (String entityType : getEntityTypes(actionRequest)) {
			String[] primaryKeys = getPrimaryKeys(actionRequest, entityType);

			UADAnonymizer<Object> entityUADAnonymizer =
				(UADAnonymizer<Object>)getUADAnonymizer(
					actionRequest, entityType);
			UADDisplay<Object> entityUADDisplay =
				(UADDisplay<Object>)getUADDisplay(actionRequest, entityType);

			for (String primaryKey : primaryKeys) {
				_anonymize(
					anonymousUser, entityUADAnonymizer, entityUADDisplay,
					primaryKey, selectedUser.getUserId(), uadHierarchyDisplay);
			}
		}

		doReviewableRedirect(actionRequest, actionResponse);
	}

	private void _anonymize(
			User anonymousUser, UADAnonymizer<Object> entityUADAnonymizer,
			UADDisplay<Object> entityUADDisplay, String primaryKey,
			long selectedUserId, UADHierarchyDisplay uadHierarchyDisplay)
		throws Exception {

		Object entity = entityUADDisplay.get(primaryKey);

		if (uadHierarchyDisplay != null) {
			if (uadHierarchyDisplay.isUserOwned(entity, selectedUserId)) {
				entityUADAnonymizer.autoAnonymize(
					entity, selectedUserId, anonymousUser);
			}

			Map<String, List<Serializable>> containerItemPKsMap =
				uadHierarchyDisplay.getContainerItemPKsMap(
					entityUADDisplay.getTypeKey(),
					uadHierarchyDisplay.getPrimaryKey(entity), selectedUserId);

			for (Map.Entry<String, List<Serializable>> entry :
					containerItemPKsMap.entrySet()) {

				String typeKey = entry.getKey();

				UADAnonymizer<Object> containerItemUADAnonymizer =
					(UADAnonymizer<Object>)uadRegistry.getUADAnonymizer(
						typeKey);
				UADDisplay<Object> containerItemUADDisplay =
					(UADDisplay<Object>)uadRegistry.getUADDisplay(typeKey);

				doMultipleAction(
					entry.getValue(),
					containerItemPK -> {
						Object containerItem = containerItemUADDisplay.get(
							containerItemPK);

						containerItemUADAnonymizer.autoAnonymize(
							containerItem, selectedUserId, anonymousUser);
					});
			}
		}
		else {
			entityUADAnonymizer.autoAnonymize(
				entity, selectedUserId, anonymousUser);
		}
	}

	@Reference
	private UADAnonymousUserProvider _uadAnonymousUserProvider;

}