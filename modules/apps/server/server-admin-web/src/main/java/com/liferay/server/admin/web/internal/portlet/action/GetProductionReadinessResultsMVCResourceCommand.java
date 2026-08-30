/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.server.admin.web.internal.production.readiness.ProductionReadinessCheckUtil;
import com.liferay.server.admin.web.internal.production.readiness.ProductionReadinessIgnoredRuleUtil;
import com.liferay.server.admin.web.internal.production.readiness.ProductionReadinessResult;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.PrintWriter;

import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortletKeys.SERVER_ADMIN,
		"mvc.command.name=/server_admin/get_production_readiness_results"
	},
	service = MVCResourceCommand.class
)
public class GetProductionReadinessResultsMVCResourceCommand
	extends BaseProductionReadinessMVCResourceCommand {

	@Override
	protected boolean isCSRFProtected() {
		return false;
	}

	@Override
	protected void serveProductionReadinessResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONArray resultsJSONArray = _jsonFactory.createJSONArray();

		int failed = 0;
		int ignored = 0;
		int passed = 0;

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Locale locale = themeDisplay.getLocale();

		Set<String> ignoredRules =
			ProductionReadinessIgnoredRuleUtil.getIgnoredRules();

		for (ProductionReadinessResult productionReadinessResult :
				ProductionReadinessCheckUtil.check()) {

			if (ignoredRules.contains(productionReadinessResult.getKey())) {
				ignored++;
			}
			else if (productionReadinessResult.isPass()) {
				passed++;
			}
			else {
				failed++;
			}

			resultsJSONArray.put(
				_toJSONObject(ignoredRules, locale, productionReadinessResult));
		}

		JSONObject responseJSONObject = JSONUtil.put(
			"results", resultsJSONArray
		).put(
			"summary",
			JSONUtil.put(
				"failed", failed
			).put(
				"ignored", ignored
			).put(
				"passed", passed
			)
		);

		resourceResponse.setContentType(ContentTypes.APPLICATION_JSON);

		PrintWriter printWriter = resourceResponse.getWriter();

		printWriter.write(responseJSONObject.toString());
	}

	private JSONObject _toJSONObject(
		Set<String> ignoredRules, Locale locale,
		ProductionReadinessResult productionReadinessResult) {

		return JSONUtil.put(
			"category", productionReadinessResult.getCategory()
		).put(
			"categoryLabel",
			LanguageUtil.get(
				locale,
				"production-readiness-category-" +
					productionReadinessResult.getCategory())
		).put(
			"currentValue", productionReadinessResult.getCurrentValue()
		).put(
			"ignored", ignoredRules.contains(productionReadinessResult.getKey())
		).put(
			"message",
			LanguageUtil.format(
				locale, productionReadinessResult.getMessageKey(),
				productionReadinessResult.getMessageParameters(), false)
		).put(
			"name",
			LanguageUtil.get(
				locale,
				"production-readiness-rule-" +
					productionReadinessResult.getKey())
		).put(
			"recommendedValue", productionReadinessResult.getRecommendedValue()
		).put(
			"ruleKey", productionReadinessResult.getKey()
		).put(
			"status", productionReadinessResult.isPass() ? "PASS" : "FAIL"
		);
	}

	@Reference
	private JSONFactory _jsonFactory;

}