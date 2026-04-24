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
public class ClusterLinkRule implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		boolean enabled = PropsValues.CLUSTER_LINK_ENABLED;

		if (enabled) {
			return Collections.singletonList(
				Result.builder()
					.status(Result.Status.PASS)
					.severity(Result.Severity.LOW)
					.category(getCategory())
					.currentValue("enabled")
					.recommendedValue("enabled for HA")
					.messageKey("cluster-link-pass")
					.docsLink("https://learn.liferay.com/")
					.build());
		}

		return Collections.singletonList(
			Result.builder()
				.status(Result.Status.PASS)
				.severity(Result.Severity.LOW)
				.category(getCategory())
				.currentValue("disabled")
				.recommendedValue("enabled for HA")
				.messageKey("cluster-link-info")
				.docsLink("https://learn.liferay.com/")
				.build());
	}

	@Override
	public String getCategory() {
		return "clustering";
	}

	@Override
	public String getKey() {
		return "cluster-link";
	}

}
