package com.liferay.production.readiness.internal.rules;

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
					return Collections.singletonList(new Result(
						Result.Status.FAIL, Result.Severity.CRITICAL,
						getCategory(), databaseProductName,
						"MySQL, PostgreSQL, Oracle, or SQL Server",
						"database-type-fail", null,
						"https://learn.liferay.com/"));
				}

				return Collections.singletonList(new Result(
					Result.Status.PASS, Result.Severity.LOW, getCategory(),
					databaseProductName,
					"MySQL, PostgreSQL, Oracle, or SQL Server",
					"database-type-pass", null,
					"https://learn.liferay.com/"));
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
