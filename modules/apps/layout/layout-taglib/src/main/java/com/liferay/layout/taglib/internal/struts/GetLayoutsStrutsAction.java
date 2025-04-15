/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.struts;

import com.liferay.layout.taglib.internal.util.LayoutUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(property = "path=/portal/get_layouts", service = StrutsAction.class)
public class GetLayoutsStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		boolean checkDisplayPage = ParamUtil.getBoolean(
			httpServletRequest, "checkDisplayPage");
		boolean enableCurrentPage = ParamUtil.getBoolean(
			httpServletRequest, "enableCurrentPage");
		long groupId = ParamUtil.getLong(httpServletRequest, "groupId");
		String itemSelectorReturnType = ParamUtil.getString(
			httpServletRequest, "itemSelectorReturnType");
		boolean privateLayout = ParamUtil.getBoolean(
			httpServletRequest, "privateLayout");
		long parentLayoutId = ParamUtil.getLong(
			httpServletRequest, "parentLayoutId");
		String[] selectedLayoutUuids = ParamUtil.getStringValues(
			httpServletRequest, "layoutUuid");
		long selPlid = ParamUtil.getLong(
			httpServletRequest, "selPlid", LayoutConstants.DEFAULT_PLID);

		int start = ParamUtil.getInteger(httpServletRequest, "start");

		start = Math.max(0, start);

		int pageSize = GetterUtil.getInteger(
			PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN);

		int end = ParamUtil.getInteger(
			httpServletRequest, "end", start + pageSize);

		int startEndMax = Math.max(start, end);

		if (pageSize <= 0) {
			start = QueryUtil.ALL_POS;
			end = QueryUtil.ALL_POS;
		}

		JSONObject layoutsJSONObject = LayoutUtil.getLayoutsJSONObject(
			checkDisplayPage, enableCurrentPage, groupId, httpServletRequest,
			itemSelectorReturnType, privateLayout, parentLayoutId,
			selectedLayoutUuids, selPlid, start, end);

		ServletResponseUtil.write(
			httpServletResponse,
			JSONUtil.put(
				"hasMoreElements",
				() -> {
					if (pageSize <= 0) {
						return false;
					}

					if (layoutsJSONObject.getInt("total") > startEndMax) {
						return true;
					}

					return false;
				}
			).put(
				"items", layoutsJSONObject.get("items")
			).toString());

		return null;
	}

}