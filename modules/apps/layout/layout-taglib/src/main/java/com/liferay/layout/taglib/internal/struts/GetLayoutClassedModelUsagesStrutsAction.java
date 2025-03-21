/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.struts;

import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.taglib.internal.helper.LayoutClassedModelUsagesHelper;
import com.liferay.layout.util.comparator.LayoutClassedModelUsageModifiedDateComparator;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = "path=/portal/get_layout_classed_model_usages",
	service = StrutsAction.class
)
public class GetLayoutClassedModelUsagesStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String className = ParamUtil.getString(httpServletRequest, "className");

		long classNameId = _portal.getClassNameId(className);

		long classPK = ParamUtil.getLong(httpServletRequest, "classPK");

		int layoutClassedModelUsagesCount =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesCount(classNameId, classPK);

		int delta = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.SEARCH_CONTAINER_PAGE_DEFAULT_DELTA), 20);

		int totalNumberOfPages = (int)Math.ceil(
			layoutClassedModelUsagesCount / (double)delta);

		JSONArray usagesJSONArray = _jsonFactory.createJSONArray();

		if (layoutClassedModelUsagesCount == 0) {
			ServletResponseUtil.write(
				httpServletResponse,
				JSONUtil.put(
					"totalNumberOfPages", totalNumberOfPages
				).put(
					"usages", usagesJSONArray
				).toString());

			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		int pageIndex = ParamUtil.getInteger(
			httpServletRequest, "pageIndex", 1);

		if (pageIndex < 1) {
			pageIndex = 1;
		}

		if (pageIndex > totalNumberOfPages) {
			pageIndex = totalNumberOfPages;
		}

		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.getLayoutClassedModelUsages(
				classNameId, classPK, delta * (pageIndex - 1),
				delta * pageIndex,
				LayoutClassedModelUsageModifiedDateComparator.getInstance(
					false));

		for (LayoutClassedModelUsage layoutClassedModelUsage :
				layoutClassedModelUsages) {

			usagesJSONArray.put(
				JSONUtil.put(
					"id", layoutClassedModelUsage.getLayoutClassedModelUsageId()
				).put(
					"name",
					_layoutClassedModelUsagesHelper.getName(
						layoutClassedModelUsage, themeDisplay.getLocale())
				).put(
					"type",
					_language.get(
						themeDisplay.getLocale(),
						_layoutClassedModelUsagesHelper.getTypeLabel(
							layoutClassedModelUsage))
				).put(
					"url",
					() -> {
						if (!_layoutClassedModelUsagesHelper.isShowPreview(
								layoutClassedModelUsage)) {

							return null;
						}

						return _layoutClassedModelUsagesHelper.getPreviewURL(
							layoutClassedModelUsage, httpServletRequest);
					}
				));
		}

		ServletResponseUtil.write(
			httpServletResponse,
			JSONUtil.put(
				"totalNumberOfPages", totalNumberOfPages
			).put(
				"usages", usagesJSONArray
			).toString());

		return null;
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private LayoutClassedModelUsagesHelper _layoutClassedModelUsagesHelper;

	@Reference
	private Portal _portal;

}