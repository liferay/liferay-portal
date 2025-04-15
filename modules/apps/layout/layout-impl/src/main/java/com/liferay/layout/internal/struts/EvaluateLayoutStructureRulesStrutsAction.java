/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.struts;

import com.liferay.layout.helper.structure.LayoutStructureRulesHelper;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureRule;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.context.RequestContextMapper;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "path=/portal/evaluate_layout_structure_rules",
	service = StrutsAction.class
)
public class EvaluateLayoutStructureRulesStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long plid = ParamUtil.getLong(httpServletRequest, "plid");

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				plid,
				ParamUtil.getLong(
					httpServletRequest, "segmentsExperienceId",
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(plid)));

		String[] layoutStructureRuleIds = ParamUtil.getStringValues(
			httpServletRequest, "layoutStructureRuleIds");

		List<LayoutStructureRule> layoutStructureRules = ListUtil.filter(
			layoutStructure.getLayoutStructureRules(),
			layoutStructureRule -> ArrayUtil.contains(
				layoutStructureRuleIds, layoutStructureRule.getId()));

		JSONArray jsonArray =
			_layoutStructureRulesHelper.processLayoutStructureRules(
				themeDisplay.getScopeGroupId(),
				_getFieldValuesMap(httpServletRequest), layoutStructureRules,
				themeDisplay.getPermissionChecker(),
				_segmentsEntryRetriever.getSegmentsEntryIds(
					themeDisplay.getScopeGroupId(), themeDisplay.getUserId(),
					_requestContextMapper.map(httpServletRequest),
					new long[0]));

		ServletResponseUtil.write(httpServletResponse, jsonArray.toString());

		return null;
	}

	private Map<String, Object> _getFieldValuesMap(
		HttpServletRequest httpServletRequest) {

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				ParamUtil.getString(httpServletRequest, "fieldValues"));

			return jsonObject.toMap();
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return Collections.emptyMap();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EvaluateLayoutStructureRulesStrutsAction.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutStructureProvider _layoutStructureProvider;

	@Reference
	private LayoutStructureRulesHelper _layoutStructureRulesHelper;

	@Reference
	private RequestContextMapper _requestContextMapper;

	@Reference
	private SegmentsEntryRetriever _segmentsEntryRetriever;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}