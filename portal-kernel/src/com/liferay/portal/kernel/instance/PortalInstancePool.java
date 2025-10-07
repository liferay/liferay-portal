/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.instance;

import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tina Tian
 */
public class PortalInstancePool {

	public static void add(Company company) {
		_portalInstances.put(company.getCompanyId(), company.getWebId());
	}

	public static void enableCache() {
		_cacheEnabled = true;
	}

	public static long getCompanyId(String webId) {
		if (_cacheEnabled) {
			for (Map.Entry<Long, String> entry : _portalInstances.entrySet()) {
				if (Objects.equals(entry.getValue(), webId)) {
					return entry.getKey();
				}
			}

			throw new IllegalArgumentException(
				"Unable to get company ID with web ID" + webId);
		}

		try {
			return _getCompanyIdBySQL(webId);
		}
		catch (SQLException sqlException) {
			_log.error(
				"Unable to get the company ID for web ID " + webId + " by SQL",
				sqlException);

			throw new RuntimeException(sqlException);
		}
	}

	public static long[] getCompanyIds() {
		if (_cacheEnabled) {
			return ArrayUtil.toLongArray(_portalInstances.keySet());
		}

		try {
			return _getCompanyIdsBySQL();
		}
		catch (SQLException sqlException) {
			_log.error("Unable to get the company IDs by SQL", sqlException);

			throw new RuntimeException(sqlException);
		}
	}

	public static long[] getCompanyIdsBySQL(Connection connection) {
		try {
			return _getCompanyIdsBySQL(connection);
		}
		catch (SQLException sqlException) {
			_log.error("Unable to get the company IDs by SQL", sqlException);

			throw new RuntimeException(sqlException);
		}
	}

	public static long getDefaultCompanyId() {
		if (_cacheEnabled) {
			for (Map.Entry<Long, String> entry : _portalInstances.entrySet()) {
				if (Objects.equals(
						PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID),
						entry.getValue())) {

					return entry.getKey();
				}
			}

			throw new IllegalStateException("Unable to get default company ID");
		}

		try {
			return _getDefaultCompanyIdBySQL();
		}
		catch (SQLException sqlException) {
			_log.error(
				"Unable to get the default company ID by SQL", sqlException);

			throw new RuntimeException(sqlException);
		}
	}

	public static long getDefaultCompanyIdBySQL(Connection connection) {
		try {
			return _getDefaultCompanyIdBySQL(connection);
		}
		catch (SQLException sqlException) {
			_log.error(
				"Unable to get the default company ID by SQL", sqlException);

			throw new RuntimeException(sqlException);
		}
	}

	public static String getWebId(long companyId) {
		if (_cacheEnabled) {
			return _portalInstances.get(companyId);
		}

		try {
			return _getWebIdBySQL(companyId);
		}
		catch (SQLException sqlException) {
			_log.error(
				"Unable to get the web ID for company with companyID " +
					companyId + " by SQL",
				sqlException);

			throw new RuntimeException(sqlException);
		}
	}

	public static String[] getWebIds() {
		if (_cacheEnabled) {
			return ArrayUtil.toStringArray(_portalInstances.values());
		}

		try {
			return _getWebIdsBySQL();
		}
		catch (SQLException sqlException) {
			_log.error("Unable to get the web IDs by SQL", sqlException);

			throw new RuntimeException(sqlException);
		}
	}

	public static void remove(long companyId) {
		_portalInstances.remove(companyId);
	}

	private static long _getCompanyIdBySQL(String webId) throws SQLException {
		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select companyId from Company where webId = ?")) {

			preparedStatement.setString(1, webId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getLong(1);
				}
			}
		}

		throw new IllegalArgumentException("Invalid web ID" + webId);
	}

	private static long[] _getCompanyIdsBySQL() throws SQLException {
		try (Connection connection = DataAccess.getConnection()) {
			return _getCompanyIdsBySQL(connection);
		}
	}

	private static long[] _getCompanyIdsBySQL(Connection connection)
		throws SQLException {

		List<Long> companyIds = new ArrayList<>();

		long defaultCompanyId = _getDefaultCompanyIdBySQL(connection);

		if (defaultCompanyId != 0) {
			companyIds.add(defaultCompanyId);
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select companyId from Company");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long companyId = resultSet.getLong("companyId");

				if (companyId != defaultCompanyId) {
					companyIds.add(companyId);
				}
			}
		}

		return ArrayUtil.toArray(companyIds.toArray(new Long[0]));
	}

	private static long _getDefaultCompanyIdBySQL() throws SQLException {
		try (Connection connection = DataAccess.getConnection()) {
			return _getDefaultCompanyIdBySQL(connection);
		}
	}

	private static long _getDefaultCompanyIdBySQL(Connection connection)
		throws SQLException {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select companyId from Company where webId = ?")) {

			preparedStatement.setString(
				1, PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getLong(1);
				}
			}
		}

		return 0;
	}

	private static String _getWebIdBySQL(long companyId) throws SQLException {
		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select webId from Company where companyId = ?")) {

			preparedStatement.setLong(1, companyId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getString(1);
				}
			}
		}

		throw new IllegalArgumentException("Invalid company ID" + companyId);
	}

	private static String[] _getWebIdsBySQL() throws SQLException {
		List<String> webIds = new ArrayList<>();

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select webId from Company");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String webId = resultSet.getString("webId");

				webIds.add(webId);
			}
		}

		return webIds.toArray(new String[0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalInstancePool.class);

	private static volatile boolean _cacheEnabled;
	private static final Map<Long, String> _portalInstances =
		new ConcurrentHashMap<>();

}