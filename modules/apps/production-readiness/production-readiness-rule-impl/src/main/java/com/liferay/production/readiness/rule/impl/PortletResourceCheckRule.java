package com.liferay.production.readiness.rule.impl;

import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.util.Collections;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;

/**
 * @author lily
 */
@Component(service = ProductionReadinessRule.class)
public class PortletResourceCheckRule implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		boolean enabled =
			PropsValues.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_ENABLED;

		if (enabled) {
			return Collections.singletonList(
				Result.builder()
					.status(Result.Status.PASS)
					.severity(Result.Severity.LOW)
					.category(getCategory())
					.currentValue("enabled")
					.recommendedValue("enabled")
					.messageKey("portlet-resource-check-pass")
					.docsLink("https://learn.liferay.com/")
					.build());
		}

		return Collections.singletonList(
			Result.builder()
				.status(Result.Status.FAIL)
				.severity(Result.Severity.HIGH)
				.category(getCategory())
				.currentValue("disabled")
				.recommendedValue("enabled")
				.messageKey("portlet-resource-check-fail")
				.docsLink("https://learn.liferay.com/")
				.build());
	}

	@Override
	public String getCategory() {
		return "security";
	}

	@Override
	public String getKey() {
		return "portlet-resource-check";
	}

}
