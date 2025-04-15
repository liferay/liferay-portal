/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.portlet.action;

import com.liferay.content.dashboard.item.ContentDashboardItem;
import com.liferay.content.dashboard.item.ContentDashboardItemFactory;
import com.liferay.content.dashboard.item.ContentDashboardItemVersion;
import com.liferay.content.dashboard.item.VersionableContentDashboardItem;
import com.liferay.content.dashboard.web.internal.constants.ContentDashboardPortletKeys;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItemFactoryRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefan Tanasie
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN,
		"mvc.command.name=/content_dashboard/get_content_dashboard_item_versions"
	},
	service = MVCResourceCommand.class
)
public class GetContentDashboardItemVersionsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		try {
			JSONObject jsonObject = _getContentDashboardItemVersionsJSONObject(
				resourceRequest);

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse, jsonObject);
		}
		catch (Exception exception) {
			if (_log.isInfoEnabled()) {
				_log.info(exception);
			}

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"error",
					ResourceBundleUtil.getString(
						ResourceBundleUtil.getBundle(
							_portal.getLocale(resourceRequest), getClass()),
						"an-unexpected-error-occurred")));
		}
	}

	private JSONArray _getContentDashboardItemVersionsJSONArray(
		List<ContentDashboardItemVersion> contentDashboardItemVersions,
		int displayVersions) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		if (ListUtil.isEmpty(contentDashboardItemVersions)) {
			return jsonArray;
		}

		contentDashboardItemVersions = ListUtil.subList(
			contentDashboardItemVersions, 0, displayVersions);

		for (ContentDashboardItemVersion contentDashboardItemVersion :
				contentDashboardItemVersions) {

			jsonArray.put(contentDashboardItemVersion.toJSONObject());
		}

		return jsonArray;
	}

	private JSONObject _getContentDashboardItemVersionsJSONObject(
			ResourceRequest resourceRequest)
		throws PortalException {

		String className = ParamUtil.getString(resourceRequest, "className");

		ContentDashboardItemFactory<?> contentDashboardItemFactory =
			_contentDashboardItemFactoryRegistry.getContentDashboardItemFactory(
				className);

		if (contentDashboardItemFactory == null) {
			return _jsonFactory.createJSONObject();
		}

		long classPK = GetterUtil.getLong(
			ParamUtil.getLong(resourceRequest, "classPK"));

		ContentDashboardItem<?> contentDashboardItem =
			contentDashboardItemFactory.create(classPK);

		if ((contentDashboardItem == null) ||
			!(contentDashboardItem instanceof
				VersionableContentDashboardItem)) {

			return _jsonFactory.createJSONObject();
		}

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		int displayVersions = ParamUtil.getInteger(
			resourceRequest, "maxDisplayVersions", 10);

		VersionableContentDashboardItem<?> versionableContentDashboardItem =
			(VersionableContentDashboardItem<?>)contentDashboardItem;

		List<ContentDashboardItemVersion> contentDashboardItemVersions =
			versionableContentDashboardItem.getAllContentDashboardItemVersions(
				httpServletRequest);

		return JSONUtil.put(
			"versions",
			_getContentDashboardItemVersionsJSONArray(
				contentDashboardItemVersions, displayVersions)
		).put(
			"viewVersionsURL",
			() -> {
				if (ListUtil.isEmpty(contentDashboardItemVersions) ||
					(contentDashboardItemVersions.size() <= displayVersions)) {

					return StringPool.BLANK;
				}

				return versionableContentDashboardItem.getViewVersionsURL(
					httpServletRequest);
			}
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetContentDashboardItemVersionsMVCResourceCommand.class);

	@Reference
	private ContentDashboardItemFactoryRegistry
		_contentDashboardItemFactoryRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}