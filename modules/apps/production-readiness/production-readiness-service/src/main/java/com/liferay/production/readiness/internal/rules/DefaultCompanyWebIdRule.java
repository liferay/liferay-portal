package com.liferay.production.readiness.internal.rules;

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
				return Collections.singletonList(new Result(
					Result.Status.FAIL, Result.Severity.MEDIUM, getCategory(),
					company.getWebId(), "a-custom-web-id",
					"default-company-web-id-fail", null,
					"https://learn.liferay.com/"));
			}

			return Collections.singletonList(new Result(
				Result.Status.PASS, Result.Severity.LOW, getCategory(),
				company.getWebId(), "a-custom-web-id",
				"default-company-web-id-pass", null,
				"https://learn.liferay.com/"));
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
