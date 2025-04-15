/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTComment;
import com.liferay.change.tracking.model.CTCommentTable;
import com.liferay.change.tracking.service.CTCommentLocalService;
import com.liferay.change.tracking.web.internal.display.context.DisplayContextUtil;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTCollectionPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.UserTable;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.text.Format;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/get_ct_comments"
	},
	service = MVCResourceCommand.class
)
public class GetCTCommentsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			getCTCommentsJSONObject(resourceRequest));
	}

	protected JSONObject getCTCommentsJSONObject(
			ResourceRequest resourceRequest)
		throws PortalException {

		JSONArray commentsJSONArray = jsonFactory.createJSONArray();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long ctCollectionId = ParamUtil.getLong(
			resourceRequest, "ctCollectionId");

		if (!CTCollectionPermission.contains(
				themeDisplay.getPermissionChecker(), ctCollectionId,
				ActionKeys.VIEW)) {

			return JSONUtil.put(
				"errorMessage",
				language.get(
					themeDisplay.getLocale(),
					"you-do-not-have-the-required-permissions-to-access-this-" +
						"content"));
		}

		Map<Long, List<CTComment>> ctCommentsMap =
			ctCommentLocalService.getCTCollectionCTComments(ctCollectionId);

		long ctEntryId = ParamUtil.getLong(resourceRequest, "ctEntryId");

		List<CTComment> ctComments = ctCommentsMap.getOrDefault(
			ctEntryId, Collections.emptyList());

		for (CTComment ctComment : ctComments) {
			Format format = FastDateFormatFactoryUtil.getDateTime(
				themeDisplay.getLocale(), themeDisplay.getTimeZone());

			Date createDate = ctComment.getCreateDate();

			String timeDescription = language.getTimeDescription(
				themeDisplay.getLocale(),
				System.currentTimeMillis() - createDate.getTime(), true);

			commentsJSONArray.put(
				JSONUtil.put(
					"createDate", format.format(createDate)
				).put(
					"createTime", createDate.getTime()
				).put(
					"ctCommentId", ctComment.getCtCommentId()
				).put(
					"timeDescription",
					language.format(
						themeDisplay.getLocale(), "x-ago",
						new String[] {timeDescription}, false)
				).put(
					"userId", ctComment.getUserId()
				).put(
					"value", ctComment.getValue()
				));
		}

		return JSONUtil.put(
			"comments", commentsJSONArray
		).put(
			"userInfo",
			() -> {
				if (ctComments.isEmpty()) {
					return null;
				}

				return DisplayContextUtil.getUserInfoJSONObject(
					CTCommentTable.INSTANCE.userId.eq(
						UserTable.INSTANCE.userId),
					CTCommentTable.INSTANCE, themeDisplay, userLocalService,
					CTCommentTable.INSTANCE.ctCollectionId.eq(
						ctCollectionId
					).and(
						CTCommentTable.INSTANCE.ctEntryId.eq(ctEntryId)
					));
			}
		);
	}

	@Reference
	protected CTCommentLocalService ctCommentLocalService;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected Language language;

	@Reference
	protected UserLocalService userLocalService;

}