package com.liferay.production.readiness.rule.impl;

import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.util.Collections;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;

/**
 * @author lily
 */
@Component(service = ProductionReadinessRule.class)
public class JavaVersionRule implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		String javaVersion = System.getProperty("java.version");

		if (javaVersion.startsWith("1.8") || javaVersion.startsWith("8")) {
			return Collections.singletonList(
				Result.builder()
					.status(Result.Status.FAIL)
					.severity(Result.Severity.HIGH)
					.category(getCategory())
					.currentValue(javaVersion)
					.recommendedValue("11 or 17")
					.messageKey("java-version-fail")
					.messageParameters(new Object[] {javaVersion})
					.docsLink("https://learn.liferay.com/")
					.build());
		}

		return Collections.singletonList(
			Result.builder()
				.status(Result.Status.PASS)
				.severity(Result.Severity.LOW)
				.category(getCategory())
				.currentValue(javaVersion)
				.recommendedValue("11 or 17")
				.messageKey("java-version-pass")
				.messageParameters(new Object[] {javaVersion})
				.docsLink("https://learn.liferay.com/")
				.build());
	}

	@Override
	public String getCategory() {
		return "server";
	}

	@Override
	public String getKey() {
		return "java-version";
	}

}
