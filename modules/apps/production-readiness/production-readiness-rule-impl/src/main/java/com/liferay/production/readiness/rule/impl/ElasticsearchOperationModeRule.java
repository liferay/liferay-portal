package com.liferay.production.readiness.rule.impl;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.search.elasticsearch8.configuration.ElasticsearchConfiguration;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.util.Collections;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author lily
 */
@Component(service = ProductionReadinessRule.class)
public class ElasticsearchOperationModeRule implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		try {
			ElasticsearchConfiguration elasticsearchConfiguration =
				_configurationProvider.getCompanyConfiguration(
					ElasticsearchConfiguration.class, companyId);

			if (elasticsearchConfiguration.productionModeEnabled()) {
				return Collections.singletonList(
					Result.builder()
						.status(Result.Status.PASS)
						.severity(Result.Severity.LOW)
						.category(getCategory())
						.currentValue("production")
						.recommendedValue("production")
						.messageKey("elasticsearch-mode-pass")
						.docsLink("https://learn.liferay.com/")
						.build());
			}

			return Collections.singletonList(
				Result.builder()
					.status(Result.Status.FAIL)
					.severity(Result.Severity.CRITICAL)
					.category(getCategory())
					.currentValue("embedded/development")
					.recommendedValue("production")
					.messageKey("elasticsearch-mode-fail")
					.docsLink("https://learn.liferay.com/")
					.build());
		}
		catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@Override
	public String getCategory() {
		return "search";
	}

	@Override
	public String getKey() {
		return "elasticsearch-mode";
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}
