/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;
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
		"mvc.command.name=/user_associated_data/delete_uad_entities"
	},
	service = MVCActionCommand.class
)
public class DeleteUADEntitiesMVCActionCommand extends BaseUADMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String applicationKey = ParamUtil.getString(
			actionRequest, "applicationKey");
		String parentContainerTypeKey = ParamUtil.getString(
			actionRequest, "parentContainerTypeKey");

		UADHierarchyDisplay uadHierarchyDisplay =
			uadRegistry.getUADHierarchyDisplay(applicationKey);

		String redirect = null;

		if ((uadHierarchyDisplay != null) &&
			Validator.isNotNull(parentContainerTypeKey)) {

			redirect = uadHierarchyDisplay.getParentContainerURL(
				actionRequest,
				_portal.getLiferayPortletResponse(actionResponse));
		}

		for (String entityType : getEntityTypes(actionRequest)) {
			String[] primaryKeys = getPrimaryKeys(actionRequest, entityType);

			UADAnonymizer<Object> entityUADAnonymizer =
				(UADAnonymizer<Object>)getUADAnonymizer(
					actionRequest, entityType);
			UADDisplay<?> entityUADDisplay = getUADDisplay(
				actionRequest, entityType);

			for (String primaryKey : primaryKeys) {
				_delete(
					actionRequest, actionResponse, entityUADAnonymizer,
					entityUADDisplay, primaryKey,
					getSelectedUserId(actionRequest), uadHierarchyDisplay);
			}
		}

		if (redirect != null) {
			long parentContainerId = ParamUtil.getLong(
				actionRequest, "parentContainerId");

			UADDisplay<?> uadDisplay = uadRegistry.getUADDisplay(
				parentContainerTypeKey);

			try {
				uadDisplay.get(parentContainerId);
			}
			catch (Exception exception) {
				if (NoSuchModelException.class.isAssignableFrom(
						exception.getClass())) {

					sendRedirect(actionRequest, actionResponse, redirect);

					return;
				}

				throw exception;
			}
		}

		doReviewableRedirect(actionRequest, actionResponse);
	}

	private void _delete(
			ActionRequest actionRequest, ActionResponse actionResponse,
			UADAnonymizer<Object> entityUADAnonymizer,
			UADDisplay<?> entityUADDisplay, String primaryKey,
			long selectedUserId, UADHierarchyDisplay uadHierarchyDisplay)
		throws Exception {

		Object entity = entityUADDisplay.get(primaryKey);

		if (uadHierarchyDisplay != null) {
			if (uadHierarchyDisplay.isUserOwned(entity, selectedUserId)) {
				try {
					entityUADAnonymizer.delete(entity);
				}
				catch (Exception exception) {
					handleExceptions(
						actionRequest, actionResponse, exception,
						entityUADAnonymizer);
				}
			}
			else {
				Map<String, List<Serializable>> containerItemPKsMap =
					uadHierarchyDisplay.getContainerItemPKsMap(
						entityUADDisplay.getTypeKey(),
						uadHierarchyDisplay.getPrimaryKey(entity),
						selectedUserId);

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
							try {
								Object containerItem =
									containerItemUADDisplay.get(
										containerItemPK);

								containerItemUADAnonymizer.delete(
									containerItem);
							}
							catch (Exception exception) {
								handleExceptions(
									actionRequest, actionResponse, exception,
									containerItemUADAnonymizer);
							}
						});
				}
			}
		}
		else {
			entityUADAnonymizer.delete(entity);
		}
	}

	@Reference
	private Portal _portal;

}