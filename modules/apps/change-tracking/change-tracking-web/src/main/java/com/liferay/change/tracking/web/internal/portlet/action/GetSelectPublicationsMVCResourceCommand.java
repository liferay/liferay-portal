/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTCollectionTable;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.change.tracking.web.internal.display.context.DisplayContextUtil;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTCollectionPermission;
import com.liferay.change.tracking.web.internal.util.PublicationsPortletURLUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.UserTable;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/get_select_publications"
	},
	service = MVCResourceCommand.class
)
public class GetSelectPublicationsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long ctCollectionId = CTConstants.CT_COLLECTION_ID_PRODUCTION;

		CTPreferences ctPreferences =
			_ctPreferencesLocalService.fetchCTPreferences(
				themeDisplay.getCompanyId(), themeDisplay.getUserId());

		if (ctPreferences != null) {
			ctCollectionId = ctPreferences.getCtCollectionId();
		}

		Set<Long> ctCollectionIds = new HashSet<>();
		JSONArray entriesJSONArray = _jsonFactory.createJSONArray();

		List<CTCollection> ctCollections =
			_ctCollectionService.getCTCollections(
				themeDisplay.getCompanyId(),
				new int[] {WorkflowConstants.STATUS_DRAFT},
				ParamUtil.getString(resourceRequest, "keywords"),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (CTCollection ctCollection : ctCollections) {
			ctCollectionIds.add(ctCollection.getCtCollectionId());

			Date modifiedDate = ctCollection.getModifiedDate();

			boolean readOnly = !CTCollectionPermission.contains(
				themeDisplay.getPermissionChecker(), ctCollection,
				ActionKeys.UPDATE);

			JSONObject entryJSONObject = JSONUtil.put(
				"description", ctCollection.getDescription()
			).put(
				"modifiedDate", modifiedDate.getTime()
			).put(
				"name", ctCollection.getName()
			).put(
				"readOnly", readOnly
			).put(
				"userId", ctCollection.getUserId()
			).put(
				"viewURL",
				PublicationsPortletURLUtil.getHref(
					resourceResponse.createRenderURL(), "mvcRenderCommandName",
					"/change_tracking/view_changes", "ctCollectionId",
					String.valueOf(ctCollection.getCtCollectionId()))
			);

			if ((ctCollection.getCtCollectionId() != ctCollectionId) &&
				!readOnly) {

				entryJSONObject.put(
					"checkoutURL",
					PublicationsPortletURLUtil.getHref(
						resourceResponse.createActionURL(),
						ActionRequest.ACTION_NAME,
						"/change_tracking/checkout_ct_collection",
						"ctCollectionId",
						String.valueOf(ctCollection.getCtCollectionId())));
			}

			entriesJSONArray.put(entryJSONObject);
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"entries", entriesJSONArray
			).put(
				"userInfo",
				() -> {
					if (ctCollectionIds.isEmpty()) {
						return null;
					}

					return DisplayContextUtil.getUserInfoJSONObject(
						CTCollectionTable.INSTANCE.userId.eq(
							UserTable.INSTANCE.userId),
						CTCollectionTable.INSTANCE, themeDisplay,
						_userLocalService,
						CTCollectionTable.INSTANCE.ctCollectionId.in(
							ctCollectionIds.toArray(new Long[0])));
				}
			));
	}

	@Reference
	private CTCollectionService _ctCollectionService;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private UserLocalService _userLocalService;

}