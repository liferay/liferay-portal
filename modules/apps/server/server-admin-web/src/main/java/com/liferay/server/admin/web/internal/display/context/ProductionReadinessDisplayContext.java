/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.display.context;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;
import com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore;
import com.liferay.production.readiness.ignore.service.ProductionReadinessIgnoreLocalService;

import jakarta.portlet.RenderRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author lily
 */
public class ProductionReadinessDisplayContext {

	public ProductionReadinessDisplayContext(RenderRequest renderRequest) {
		_renderRequest = renderRequest;

		_themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_initRules();
	}

	public String getFilter() {
		return ParamUtil.getString(_renderRequest, "filter", "all");
	}

	public Map<String, List<RuleResult>> getGroupedRuleResults() {
		String filter = getFilter();

		if (filter.equals("all")) {
			return _groupedRuleResults;
		}

		Map<String, List<RuleResult>> filteredGroupedRuleResults =
			new TreeMap<>();

		_groupedRuleResults.forEach(
			(category, ruleResults) -> {
				List<RuleResult> filteredRuleResults = ruleResults.stream(
				).filter(
					ruleResult -> {
						if (filter.equals("passed")) {
							return !ruleResult.isIgnored() &&
								(ruleResult.getResult().getStatus() ==
									Result.Status.PASS);
						}

						if (filter.equals("failed")) {
							return !ruleResult.isIgnored() &&
								(ruleResult.getResult().getStatus() ==
									Result.Status.FAIL);
						}

						if (filter.equals("ignored")) {
							return ruleResult.isIgnored();
						}

						return true;
					}
				).collect(
					Collectors.toList()
				);

				if (!filteredRuleResults.isEmpty()) {
					filteredGroupedRuleResults.put(
						category, filteredRuleResults);
				}
			});

		return filteredGroupedRuleResults;
	}

	public int getIgnoredCount() {
		return _ignoredCount;
	}

	public int getPassedCount() {
		return _passedCount;
	}

	public int getFailedCount() {
		return _failedCount;
	}

	public int getTotalCount() {
		return _totalCount;
	}

	private void _initRules() {
		Bundle bundle = FrameworkUtil.getBundle(ProductionReadinessRule.class);

		if (bundle == null) {
			_groupedRuleResults = Collections.emptyMap();

			return;
		}

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceTracker<ProductionReadinessRule, ProductionReadinessRule>
			rulesServiceTracker = new ServiceTracker<>(
				bundleContext, ProductionReadinessRule.class, null);

		rulesServiceTracker.open();

		ServiceTracker<ProductionReadinessIgnoreLocalService, ProductionReadinessIgnoreLocalService>
			ignoreServiceTracker = new ServiceTracker<>(
				bundleContext, ProductionReadinessIgnoreLocalService.class,
				null);

		ignoreServiceTracker.open();

		try {
			ProductionReadinessIgnoreLocalService ignoreService =
				ignoreServiceTracker.getService();

			Map<String, ProductionReadinessIgnore> ignoreMap;

			if (ignoreService != null) {
				List<ProductionReadinessIgnore> ignores =
					ignoreService.getProductionReadinessIgnores(
						_themeDisplay.getCompanyId());

				ignoreMap = ignores.stream(
				).collect(
					Collectors.toMap(ProductionReadinessIgnore::getRuleKey, i -> i)
				);
			}
			else {
				ignoreMap = Collections.emptyMap();
			}

			_groupedRuleResults = new TreeMap<>();

			for (ProductionReadinessRule rule :
					rulesServiceTracker.getServices(
						new ProductionReadinessRule[0])) {

				Collection<Result> results = rule.check(
					_themeDisplay.getCompanyId());

				ProductionReadinessIgnore ignore = ignoreMap.get(rule.getKey());

				for (Result result : results) {
					RuleResult ruleResult = new RuleResult(
						rule, result, ignore != null);

					_groupedRuleResults.computeIfAbsent(
						result.getCategory(), k -> new ArrayList<>()
					).add(
						ruleResult
					);

					_totalCount++;

					if (ruleResult.isIgnored()) {
						_ignoredCount++;
					}
					else if (result.getStatus() == Result.Status.PASS) {
						_passedCount++;
					}
					else {
						_failedCount++;
					}
				}
			}
		}
		catch (Exception e) {
			_groupedRuleResults = Collections.emptyMap();
		}
		finally {
			rulesServiceTracker.close();
			ignoreServiceTracker.close();
		}
	}

	public static class RuleResult {

		public RuleResult(
			ProductionReadinessRule rule, Result result, boolean ignored) {

			_rule = rule;
			_result = result;
			_ignored = ignored;
		}

		public Result getResult() {
			return _result;
		}

		public ProductionReadinessRule getRule() {
			return _rule;
		}

		public boolean isIgnored() {
			return _ignored;
		}

		private final boolean _ignored;
		private final Result _result;
		private final ProductionReadinessRule _rule;

	}

	private int _failedCount;
	private Map<String, List<RuleResult>> _groupedRuleResults;
	private int _ignoredCount;
	private int _passedCount;
	private final RenderRequest _renderRequest;
	private final ThemeDisplay _themeDisplay;
	private int _totalCount;

}