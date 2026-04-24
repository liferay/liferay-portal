package com.liferay.production.readiness.rule.impl;

import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Collections;
import java.util.Collection;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Component;

/**
 * @author lily
 */
@Component(service = ProductionReadinessRule.class)
public class DatabaseTypeRule implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		try {
			DataSource dataSource = InfrastructureUtil.getDataSource();

			try (Connection connection = dataSource.getConnection()) {
				DatabaseMetaData databaseMetaData = connection.getMetaData();

				String databaseProductName =
					databaseMetaData.getDatabaseProductName();

				if (databaseProductName.contains("HSQL")) {
					return Collections.singletonList(
						Result.builder()
							.status(Result.Status.FAIL)
							.severity(Result.Severity.CRITICAL)
							.category(getCategory())
							.currentValue(databaseProductName)
							.recommendedValue(
								"MySQL, PostgreSQL, Oracle, or SQL Server")
							.messageKey("database-type-fail")
							.messageParameters(new Object[] {databaseProductName})
							.docsLink("https://learn.liferay.com/")
							.build());
				}

				return Collections.singletonList(
					Result.builder()
						.status(Result.Status.PASS)
						.severity(Result.Severity.LOW)
						.category(getCategory())
						.currentValue(databaseProductName)
						.recommendedValue(
							"MySQL, PostgreSQL, Oracle, or SQL Server")
						.messageKey("database-type-pass")
						.messageParameters(new Object[] {databaseProductName})
						.docsLink("https://learn.liferay.com/")
						.build());
			}
		}
		catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@Override
	public String getCategory() {
		return "server";
	}

	@Override
	public String getKey() {
		return "database-type";
	}

}
