/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.partition.migration.validator.util;

import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.tools.db.partition.migration.validator.Company;
import com.liferay.portal.tools.db.partition.migration.validator.Release;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author Luis Ortiz
 */
public abstract class BaseTestCase {

	protected static void mockGetCatalogs(List<String> catalogNames)
		throws SQLException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);

		Mockito.when(
			_databaseMetaData.getCatalogs()
		).thenReturn(
			resultSet
		);

		List<Integer> nextCounter = new ArrayList<>();

		nextCounter.add(0);

		Mockito.when(
			resultSet.next()
		).thenAnswer(
			(Answer<Boolean>)invocationOnMock -> {
				int counter = nextCounter.get(0);

				if (counter >= catalogNames.size()) {
					return false;
				}

				nextCounter.set(0, ++counter);

				return true;
			}
		);

		List<Integer> catalogNameCounter = new ArrayList<>();

		catalogNameCounter.add(0);

		Mockito.when(
			resultSet.getString("TABLE_CAT")
		).thenAnswer(
			(Answer<String>)invocationOnMock -> {
				int counter = catalogNameCounter.get(0);

				if (counter >= catalogNames.size()) {
					throw new IndexOutOfBoundsException();
				}

				catalogNameCounter.set(0, ++counter);

				return catalogNames.get(counter - 1);
			}
		);
	}

	protected static void mockGetColumns(List<String> tableNames)
		throws SQLException {

		ResultSet resultSet1 = Mockito.mock(ResultSet.class);

		Mockito.when(
			_databaseMetaData.getColumns(
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.any(), Mockito.nullable(String.class))
		).thenReturn(
			resultSet1
		);

		Mockito.when(
			resultSet1.next()
		).thenReturn(
			true
		);

		ResultSet resultSet2 = Mockito.mock(ResultSet.class);

		Mockito.when(
			_databaseMetaData.getTables(
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.isNull(), Mockito.any(String[].class))
		).thenReturn(
			resultSet2
		);

		List<Integer> nextCounter = new ArrayList<>();

		nextCounter.add(0);

		Mockito.when(
			resultSet2.next()
		).thenAnswer(
			(Answer<Boolean>)invocationOnMock -> {
				int counter = nextCounter.get(0);

				if (counter >= tableNames.size()) {
					return false;
				}

				nextCounter.set(0, ++counter);

				return true;
			}
		);

		List<Integer> tableNameCounter = new ArrayList<>();

		tableNameCounter.add(0);

		Mockito.when(
			resultSet2.getString("TABLE_NAME")
		).thenAnswer(
			(Answer<String>)invocationOnMock -> {
				int counter = tableNameCounter.get(0);

				if (counter >= tableNames.size()) {
					throw new IndexOutOfBoundsException();
				}

				tableNameCounter.set(0, ++counter);

				return tableNames.get(counter - 1);
			}
		);
	}

	protected static void mockGetCompanies(List<Company> companies)
		throws SQLException {

		mockGetCompanyNames(companies);

		Mockito.when(
			_databaseMetaData.getCatalogs()
		).thenReturn(
			Mockito.mock(ResultSet.class)
		);

		PreparedStatement preparedStatement = Mockito.mock(
			PreparedStatement.class);

		Mockito.when(
			connection.prepareStatement(
				"select Company.companyId, webId, hostname from Company left " +
					"join VirtualHost on Company.companyId = " +
						"VirtualHost.companyId")
		).thenReturn(
			preparedStatement
		);

		ResultSet resultSet = Mockito.mock(ResultSet.class);

		Mockito.when(
			preparedStatement.executeQuery()
		).thenReturn(
			resultSet
		);

		List<Integer> nextCounter = new ArrayList<>();

		nextCounter.add(0);

		Mockito.when(
			resultSet.next()
		).thenAnswer(
			(Answer<Boolean>)invocationOnMock -> {
				int counter = nextCounter.get(0);

				if (counter >= companies.size()) {
					return false;
				}

				nextCounter.set(0, ++counter);

				return true;
			}
		);

		List<Integer> companyIdCounter = new ArrayList<>();

		companyIdCounter.add(0);

		Mockito.when(
			resultSet.getLong("companyId")
		).thenAnswer(
			(Answer<Long>)invocationOnMock -> {
				int counter = companyIdCounter.get(0);

				if (counter >= companies.size()) {
					throw new IndexOutOfBoundsException();
				}

				companyIdCounter.set(0, ++counter);

				Company company = companies.get(counter - 1);

				return company.getCompanyId();
			}
		);

		List<Integer> webIdCounter = new ArrayList<>();

		webIdCounter.add(0);

		Mockito.when(
			resultSet.getString("webId")
		).thenAnswer(
			(Answer<String>)invocationOnMock -> {
				int counter = webIdCounter.get(0);

				if (counter >= companies.size()) {
					throw new IndexOutOfBoundsException();
				}

				webIdCounter.set(0, ++counter);

				Company company = companies.get(counter - 1);

				return company.getWebId();
			}
		);

		List<Integer> virtualHostnameCounter = new ArrayList<>();

		virtualHostnameCounter.add(0);

		Mockito.when(
			resultSet.getString("hostname")
		).thenAnswer(
			(Answer<String>)invocationOnMock -> {
				int counter = virtualHostnameCounter.get(0);

				if (counter >= companies.size()) {
					throw new IndexOutOfBoundsException();
				}

				virtualHostnameCounter.set(0, ++counter);

				Company company = companies.get(counter - 1);

				return company.getVirtualHostname();
			}
		);
	}

	protected static void mockGetCompanyIds(List<Long> companyIds)
		throws SQLException {

		PreparedStatement preparedStatement = Mockito.mock(
			PreparedStatement.class);

		Mockito.when(
			connection.prepareStatement("select companyId from Company")
		).thenReturn(
			preparedStatement
		);

		ResultSet resultSet = Mockito.mock(ResultSet.class);

		Mockito.when(
			preparedStatement.executeQuery()
		).thenReturn(
			resultSet
		);

		List<Integer> nextCounter = new ArrayList<>();

		nextCounter.add(0);

		Mockito.when(
			resultSet.next()
		).thenAnswer(
			(Answer<Boolean>)invocationOnMock -> {
				int counter = nextCounter.get(0);

				if (counter >= companyIds.size()) {
					return false;
				}

				nextCounter.set(0, ++counter);

				return true;
			}
		);

		List<Integer> companyIdCounter = new ArrayList<>();

		companyIdCounter.add(0);

		Mockito.when(
			resultSet.getLong("companyId")
		).thenAnswer(
			(Answer<Long>)invocationOnMock -> {
				int counter = companyIdCounter.get(0);

				if (counter >= companyIds.size()) {
					throw new IndexOutOfBoundsException();
				}

				companyIdCounter.set(0, ++counter);

				return companyIds.get(counter - 1);
			}
		);
	}

	protected static void mockGetCompanyInfos(List<Long> companyIds)
		throws SQLException {

		PreparedStatement preparedStatement = Mockito.mock(
			PreparedStatement.class);

		Mockito.when(
			connection.prepareStatement("select companyId from CompanyInfo")
		).thenReturn(
			preparedStatement
		);

		ResultSet resultSet = Mockito.mock(ResultSet.class);

		Mockito.when(
			preparedStatement.executeQuery()
		).thenReturn(
			resultSet
		);

		List<Integer> nextCounter = new ArrayList<>();

		nextCounter.add(0);

		Mockito.when(
			resultSet.next()
		).thenAnswer(
			(Answer<Boolean>)invocationOnMock -> {
				int counter = nextCounter.get(0);

				if (counter >= companyIds.size()) {
					return false;
				}

				nextCounter.set(0, ++counter);

				return true;
			}
		);

		List<Integer> companyIdCounter = new ArrayList<>();

		companyIdCounter.add(0);

		Mockito.when(
			resultSet.getLong("companyId")
		).thenAnswer(
			(Answer<Long>)invocationOnMock -> {
				int counter = companyIdCounter.get(0);

				if (counter >= companyIds.size()) {
					throw new IndexOutOfBoundsException();
				}

				companyIdCounter.set(0, ++counter);

				return companyIds.get(counter - 1);
			}
		);
	}

	protected static void mockGetCompanyNames(List<Company> companies)
		throws SQLException {

		mockGetCompanyNames(companies, "CompanyInfo");
	}

	protected static void mockGetCompanyNames(
			List<Company> companies, String tableName)
		throws SQLException {

		PreparedStatement preparedStatement = Mockito.mock(
			PreparedStatement.class);

		Mockito.when(
			connection.prepareStatement(
				"select companyId, name from " + tableName)
		).thenReturn(
			preparedStatement
		);

		ResultSet resultSet = Mockito.mock(ResultSet.class);

		Mockito.when(
			preparedStatement.executeQuery()
		).thenReturn(
			resultSet
		);

		List<Integer> nextCounter = new ArrayList<>();

		nextCounter.add(0);

		Mockito.when(
			resultSet.next()
		).thenAnswer(
			(Answer<Boolean>)invocationOnMock -> {
				int counter = nextCounter.get(0);

				if (counter >= companies.size()) {
					return false;
				}

				nextCounter.set(0, ++counter);

				return true;
			}
		);

		List<Integer> companyIdCounter = new ArrayList<>();

		companyIdCounter.add(0);

		Mockito.when(
			resultSet.getLong("companyId")
		).thenAnswer(
			(Answer<Long>)invocationOnMock -> {
				int counter = companyIdCounter.get(0);

				if (counter >= companies.size()) {
					throw new IndexOutOfBoundsException();
				}

				companyIdCounter.set(0, ++counter);

				Company company = companies.get(counter - 1);

				return company.getCompanyId();
			}
		);

		List<Integer> companyNameCounter = new ArrayList<>();

		companyNameCounter.add(0);

		Mockito.when(
			resultSet.getString("name")
		).thenAnswer(
			(Answer<String>)invocationOnMock -> {
				int counter = companyNameCounter.get(0);

				if (counter >= companies.size()) {
					throw new IndexOutOfBoundsException();
				}

				companyNameCounter.set(0, ++counter);

				Company company = companies.get(counter - 1);

				return company.getCompanyName();
			}
		);
	}

	protected static void mockGetConnection(
			String password, String url, String user)
		throws SQLException {

		_driverManagerMockedStatic.when(
			() -> DriverManager.getConnection(url, user, password)
		).thenReturn(
			connection
		);

		Mockito.when(
			connection.getMetaData()
		).thenReturn(
			_databaseMetaData
		);

		Mockito.when(
			_databaseMetaData.getURL()
		).thenReturn(
			url
		);
	}

	protected static void mockGetReleases(List<Release> releases)
		throws SQLException {

		PreparedStatement preparedStatement = Mockito.mock(
			PreparedStatement.class);

		Mockito.when(
			connection.prepareStatement(
				"select servletContextName, schemaVersion, state_, verified " +
					"from Release_")
		).thenReturn(
			preparedStatement
		);

		ResultSet resultSet = Mockito.mock(ResultSet.class);

		Mockito.when(
			preparedStatement.executeQuery()
		).thenReturn(
			resultSet
		);

		List<Integer> nextCounter = new ArrayList<>();

		nextCounter.add(0);

		Mockito.when(
			resultSet.next()
		).thenAnswer(
			(Answer<Boolean>)invocationOnMock -> {
				int counter = nextCounter.get(0);

				if (counter >= releases.size()) {
					return false;
				}

				nextCounter.set(0, ++counter);

				return true;
			}
		);

		List<Integer> servletContextNameCounter = new ArrayList<>();

		servletContextNameCounter.add(0);

		Mockito.when(
			resultSet.getString("servletContextName")
		).thenAnswer(
			(Answer<String>)invocationOnMock -> {
				int counter = servletContextNameCounter.get(0);

				if (counter >= releases.size()) {
					throw new IndexOutOfBoundsException();
				}

				servletContextNameCounter.set(0, ++counter);

				Release release = releases.get(counter - 1);

				return release.getServletContextName();
			}
		);

		List<Integer> schemaVersionCounter = new ArrayList<>();

		schemaVersionCounter.add(0);

		Mockito.when(
			resultSet.getString("schemaVersion")
		).thenAnswer(
			(Answer<String>)invocationOnMock -> {
				int counter = schemaVersionCounter.get(0);

				if (counter >= releases.size()) {
					throw new IndexOutOfBoundsException();
				}

				schemaVersionCounter.set(0, ++counter);

				Release release = releases.get(counter - 1);

				Version version = release.getSchemaVersion();

				return version.toString();
			}
		);

		List<Integer> stateCounter = new ArrayList<>();

		stateCounter.add(0);

		Mockito.when(
			resultSet.getInt("state_")
		).thenAnswer(
			(Answer<Integer>)invocationOnMock -> {
				int counter = stateCounter.get(0);

				if (counter >= releases.size()) {
					throw new IndexOutOfBoundsException();
				}

				stateCounter.set(0, ++counter);

				Release release = releases.get(counter - 1);

				return release.getState();
			}
		);

		List<Integer> verifiedCounter = new ArrayList<>();

		verifiedCounter.add(0);

		Mockito.when(
			resultSet.getBoolean("verified")
		).thenAnswer(
			(Answer<Boolean>)invocationOnMock -> {
				int counter = verifiedCounter.get(0);

				if (counter >= releases.size()) {
					throw new IndexOutOfBoundsException();
				}

				verifiedCounter.set(0, ++counter);

				Release release = releases.get(counter - 1);

				return release.getVerified();
			}
		);
	}

	protected static void mockGetTables(boolean defaultPartition)
		throws SQLException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);

		Mockito.when(
			_databaseMetaData.getTables(
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.eq("Company"), Mockito.any(String[].class))
		).thenReturn(
			resultSet
		);

		Mockito.when(
			resultSet.next()
		).thenReturn(
			defaultPartition
		);
	}

	protected static final Connection connection = Mockito.mock(
		Connection.class);

	private static final DatabaseMetaData _databaseMetaData = Mockito.mock(
		DatabaseMetaData.class);
	private static final MockedStatic<DriverManager>
		_driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class);

}