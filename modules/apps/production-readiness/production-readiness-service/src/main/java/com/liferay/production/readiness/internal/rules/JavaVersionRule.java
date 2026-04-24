package com.liferay.production.readiness.internal.rules;

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
			return Collections.singletonList(new Result(
				Result.Status.FAIL, Result.Severity.HIGH, getCategory(),
				javaVersion, "11 or 17", "java-version-fail", null,
				"https://learn.liferay.com/"));
		}

		return Collections.singletonList(new Result(
			Result.Status.PASS, Result.Severity.LOW, getCategory(),
			javaVersion, "11 or 17", "java-version-pass", null,
			"https://learn.liferay.com/"));
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
