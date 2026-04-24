package com.liferay.production.readiness.rule.impl;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
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
public class DefaultAdminPasswordRule implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		try {
			User user = _userLocalService.fetchUserByEmailAddress(
				companyId, "test@liferay.com");

			if (user == null) {
				return Collections.emptyList();
			}

			// Default password hash for "test" with default settings

			if (user.getPassword().equals("qU69S9ae79O7s+97Y8u23u/M3E==")) {
				return Collections.singletonList(
					Result.builder()
						.status(Result.Status.FAIL)
						.severity(Result.Severity.CRITICAL)
						.category(getCategory())
						.currentValue("default")
						.recommendedValue("custom")
						.messageKey("default-admin-password-fail")
						.docsLink("https://learn.liferay.com/")
						.build());
			}

			return Collections.singletonList(
				Result.builder()
					.status(Result.Status.PASS)
					.severity(Result.Severity.LOW)
					.category(getCategory())
					.currentValue("custom")
					.recommendedValue("custom")
					.messageKey("default-admin-password-pass")
					.docsLink("https://learn.liferay.com/")
					.build());
		}
		catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@Override
	public String getCategory() {
		return "security";
	}

	@Override
	public String getKey() {
		return "default-admin-password";
	}

	@Reference
	private UserLocalService _userLocalService;

}
