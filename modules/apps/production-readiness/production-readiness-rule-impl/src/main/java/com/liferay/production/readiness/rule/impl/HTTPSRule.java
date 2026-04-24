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
public class HTTPSRule implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		String protocol = PropsValues.WEB_SERVER_PROTOCOL;

		if ("https".equalsIgnoreCase(protocol)) {
			return Collections.singletonList(
				Result.builder()
					.status(Result.Status.PASS)
					.severity(Result.Severity.MEDIUM)
					.category(getCategory())
					.currentValue(protocol)
					.recommendedValue("https")
					.messageKey("https-rule-pass")
					.docsLink("https://learn.liferay.com/")
					.build());
		}

		return Collections.singletonList(
			Result.builder()
				.status(Result.Status.FAIL)
				.severity(Result.Severity.CRITICAL)
				.category(getCategory())
				.currentValue(protocol)
				.recommendedValue("https")
				.messageKey("https-rule-fail")
				.docsLink("https://learn.liferay.com/")
				.build());
	}

	@Override
	public String getCategory() {
		return "security";
	}

	@Override
	public String getKey() {
		return "https-protocol";
	}

}
