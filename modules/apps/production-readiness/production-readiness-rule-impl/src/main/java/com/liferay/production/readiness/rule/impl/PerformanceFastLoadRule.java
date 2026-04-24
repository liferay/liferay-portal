package com.liferay.production.readiness.rule.impl;

import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author lily
 */
@Component(service = ProductionReadinessRule.class)
public class PerformanceFastLoadRule implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		List<Result> results = new ArrayList<>();

		_check(
			results, "theme-css-fast-load", PropsValues.THEME_CSS_FAST_LOAD,
			true);
		_check(
			results, "theme-images-fast-load", PropsValues.THEME_IMAGES_FAST_LOAD,
			true);
		_check(
			results, "javascript-fast-load", PropsValues.JAVASCRIPT_FAST_LOAD,
			true);
		_check(
			results, "layout-template-cache-enabled",
			PropsValues.LAYOUT_TEMPLATE_CACHE_ENABLED, true);
		_check(
			results, "browser-cache-disabled", PropsValues.BROWSER_CACHE_DISABLED,
			false);

		return results;
	}

	@Override
	public String getCategory() {
		return "performance";
	}

	@Override
	public String getKey() {
		return "performance-fast-load";
	}

	private void _check(
		List<Result> results, String name, boolean currentValue,
		boolean recommendedValue) {

		if (currentValue == recommendedValue) {
			results.add(
				Result.builder()
					.status(Result.Status.PASS)
					.severity(Result.Severity.LOW)
					.category(getCategory())
					.currentValue(String.valueOf(currentValue))
					.recommendedValue(String.valueOf(recommendedValue))
					.messageKey("performance-" + name + "-pass")
					.docsLink("https://learn.liferay.com/")
					.build());
		}
		else {
			results.add(
				Result.builder()
					.status(Result.Status.FAIL)
					.severity(Result.Severity.HIGH)
					.category(getCategory())
					.currentValue(String.valueOf(currentValue))
					.recommendedValue(String.valueOf(recommendedValue))
					.messageKey("performance-" + name + "-fail")
					.docsLink("https://learn.liferay.com/")
					.build());
		}
	}

}
