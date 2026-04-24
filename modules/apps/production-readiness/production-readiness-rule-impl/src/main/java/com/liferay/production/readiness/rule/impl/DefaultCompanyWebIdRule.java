package com.liferay.production.readiness.rule.impl;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
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
public class DefaultCompanyWebIdRule implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		try {
			Company company = _companyLocalService.getCompany(companyId);

			if ("liferay.com".equals(company.getWebId())) {
				return Collections.singletonList(
					Result.builder()
						.status(Result.Status.FAIL)
						.severity(Result.Severity.MEDIUM)
						.category(getCategory())
						.currentValue(company.getWebId())
						.recommendedValue("a-custom-web-id")
						.messageKey("default-company-web-id-fail")
						.docsLink("https://learn.liferay.com/")
						.build());
			}

			return Collections.singletonList(
				Result.builder()
					.status(Result.Status.PASS)
					.severity(Result.Severity.LOW)
					.category(getCategory())
					.currentValue(company.getWebId())
					.recommendedValue("a-custom-web-id")
					.messageKey("default-company-web-id-pass")
					.docsLink("https://learn.liferay.com/")
					.build());
		}
		catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@Override
	public String getCategory() {
		return "setup";
	}

	@Override
	public String getKey() {
		return "default-company-web-id";
	}

	@Reference
	private CompanyLocalService _companyLocalService;

}
