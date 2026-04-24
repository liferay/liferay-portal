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
public class DocumentLibraryStoreRule implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		String impl = PropsValues.DL_STORE_IMPL;

		if (PropsValues.CLUSTER_LINK_ENABLED &&
			impl.contains("FileSystemStore")) {

			return Collections.singletonList(
				Result.builder()
					.status(Result.Status.FAIL)
					.severity(Result.Severity.CRITICAL)
					.category(getCategory())
					.currentValue(impl)
					.recommendedValue("S3Store, DBStore, or similar")
					.messageKey("dl-store-clustering-fail")
					.docsLink("https://learn.liferay.com/")
					.build());
		}

		return Collections.singletonList(
			Result.builder()
				.status(Result.Status.PASS)
				.severity(Result.Severity.LOW)
				.category(getCategory())
				.currentValue(impl)
				.recommendedValue("Appropriate store for environment")
				.messageKey("dl-store-pass")
				.docsLink("https://learn.liferay.com/")
				.build());
	}

	@Override
	public String getCategory() {
		return "clustering";
	}

	@Override
	public String getKey() {
		return "dl-store";
	}

}
