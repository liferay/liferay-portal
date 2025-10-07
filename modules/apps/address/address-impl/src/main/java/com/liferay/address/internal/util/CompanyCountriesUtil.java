/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.internal.util;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.RegionLocalization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.InputStream;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author João Victor Alves
 */
public class CompanyCountriesUtil {

	public static void addCountry(
			Company company, CounterLocalService counterLocalService,
			JSONObject countryJSONObject,
			CountryLocalService countryLocalService, Connection connection)
		throws Exception {

		try {
			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCompanyId(company.getCompanyId());

			User guestUser = company.getGuestUser();

			serviceContext.setUserId(guestUser.getUserId());

			Country country = countryLocalService.addCountry(
				countryJSONObject.getString("a2"),
				countryJSONObject.getString("a3"), true, true,
				countryJSONObject.getString("idd"),
				countryJSONObject.getString("name"),
				countryJSONObject.getString("number"), 0, true, false,
				countryJSONObject.getBoolean("zipRequired"), serviceContext);

			Map<String, String> titleMap = new HashMap<>();

			for (Locale locale :
					LanguageUtil.getCompanyAvailableLocales(
						company.getCompanyId())) {

				titleMap.put(
					LanguageUtil.getLanguageId(locale),
					country.getName(locale));
			}

			countryLocalService.updateCountryLocalizations(country, titleMap);

			processCountryRegions(country, connection, counterLocalService);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	public static JSONArray getJSONArray(String path) throws Exception {
		ClassLoader classLoader = CompanyCountriesUtil.class.getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
			if (inputStream == null) {
				return null;
			}

			return JSONFactoryUtil.createJSONArray(
				StringUtil.read(inputStream));
		}
	}

	public static void populateCompanyCountries(
			Company company, CounterLocalService counterLocalService,
			CountryLocalService countryLocalService, Connection connection)
		throws Exception {

		int count = countryLocalService.getCompanyCountriesCount(
			company.getCompanyId());

		if (count > 0) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Skipping country initialization. Countries are ",
						"already initialized for company ",
						company.getCompanyId(), "."));
			}

			return;
		}

		updateRegionCounter(connection, counterLocalService);
		updateRegionLocalizationCounter(connection, counterLocalService);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Initializing countries for company " + company.getCompanyId());
		}

		JSONArray countriesJSONArray = getJSONArray(
			"com/liferay/address/dependencies/countries.json");

		for (int i = 0; i < countriesJSONArray.length(); i++) {
			JSONObject countryJSONObject = countriesJSONArray.getJSONObject(i);

			try {
				addCountry(
					company, counterLocalService, countryJSONObject,
					countryLocalService, connection);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	public static void processCountryRegions(
			Country country, Connection connection,
			CounterLocalService counterLocalService)
		throws Exception {

		String a2 = country.getA2();

		String path =
			"com/liferay/address/dependencies/regions/" + a2 + ".json";

		JSONArray regionsJSONArray = getJSONArray(path);

		if (regionsJSONArray == null) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Regions found for country " + a2);
		}

		if (regionsJSONArray.length() == 0) {
			return;
		}

		try (PreparedStatement regionPreparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"insert into Region (mvccVersion, ctCollectionId, ",
						"uuid_, regionId, companyId, userId, createDate, ",
						"modifiedDate, countryId, active_, name, position, ",
						"regionCode) values (0, 0, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
						"0, ?)"));
			PreparedStatement regionLocalizationPreparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"insert into RegionLocalization (mvccVersion, ",
						"ctCollectionId, regionLocalizationId, companyId, ",
						"regionId, languageId, title) values (0, 0, ?, ?, ?, ",
						"?, ?)"))) {

			int length = regionsJSONArray.length();

			long startRegionId =
				counterLocalService.increment(Region.class.getName(), length) -
					length;

			List<RegionLocalizationData> regionLocalizationDataList =
				new ArrayList<>();

			for (int i = 0; i < length; i++) {
				JSONObject regionJSONObject = regionsJSONArray.getJSONObject(i);

				long regionId = startRegionId + i + 1;

				_addRegionBatch(
					regionPreparedStatement, country.getCompanyId(),
					country.getCountryId(), regionJSONObject.getString("name"),
					regionJSONObject.getString("regionCode"), regionId,
					country.getUserId());

				JSONObject localizationsJSONObject =
					regionJSONObject.getJSONObject("localizations");

				if (localizationsJSONObject == null) {
					for (Locale locale :
							LanguageUtil.getCompanyAvailableLocales(
								country.getCompanyId())) {

						regionLocalizationDataList.add(
							new RegionLocalizationData(
								LanguageUtil.getLanguageId(locale), regionId,
								regionJSONObject.getString("name")));
					}
				}
				else {
					for (String key : localizationsJSONObject.keySet()) {
						regionLocalizationDataList.add(
							new RegionLocalizationData(
								key, regionId,
								localizationsJSONObject.getString(key)));
					}
				}
			}

			long regionLocalizationId = counterLocalService.increment(
				RegionLocalization.class.getName(),
				regionLocalizationDataList.size());

			long startRegionLocalizationId =
				regionLocalizationId - regionLocalizationDataList.size();

			for (RegionLocalizationData regionLocalizationData :
					regionLocalizationDataList) {

				_addRegionLocalizationBatch(
					regionLocalizationPreparedStatement, country.getCompanyId(),
					regionLocalizationData._languageId,
					regionLocalizationData._regionId,
					++startRegionLocalizationId, regionLocalizationData._title);
			}

			regionPreparedStatement.executeBatch();

			regionLocalizationPreparedStatement.executeBatch();
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	public static void updateRegionCounter(
			Connection connection, CounterLocalService counterLocalService)
		throws Exception {

		_updateCounter(
			Region.class.getName(), connection, counterLocalService, "regionId",
			"Region");
	}

	public static void updateRegionLocalizationCounter(
			Connection connection, CounterLocalService counterLocalService)
		throws Exception {

		_updateCounter(
			RegionLocalization.class.getName(), connection, counterLocalService,
			"regionLocalizationId", "RegionLocalization");
	}

	private static void _addRegionBatch(
			PreparedStatement preparedStatement, long companyId, long countryId,
			String name, String regionCode, long regionId, long userId)
		throws SQLException {

		preparedStatement.setString(1, PortalUUIDUtil.generate());
		preparedStatement.setLong(2, regionId);
		preparedStatement.setLong(3, companyId);
		preparedStatement.setLong(4, userId);
		preparedStatement.setDate(5, new Date(System.currentTimeMillis()));
		preparedStatement.setDate(6, new Date(System.currentTimeMillis()));
		preparedStatement.setLong(7, countryId);
		preparedStatement.setBoolean(8, true);
		preparedStatement.setString(9, name);
		preparedStatement.setString(10, regionCode);

		preparedStatement.addBatch();
	}

	private static void _addRegionLocalizationBatch(
			PreparedStatement preparedStatement, long companyId,
			String languageId, long regionId, long regionLocalizationId,
			String title)
		throws SQLException {

		preparedStatement.setLong(1, regionLocalizationId);
		preparedStatement.setLong(2, companyId);
		preparedStatement.setLong(3, regionId);
		preparedStatement.setString(4, languageId);
		preparedStatement.setString(5, title);

		preparedStatement.addBatch();
	}

	private static void _updateCounter(
			String className, Connection connection,
			CounterLocalService counterLocalService, String primaryKey,
			String tableName)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select currentId from Counter where name = ?")) {

			preparedStatement.setString(1, className);

			long currentId = 0;

			try (ResultSet resultSet1 = preparedStatement.executeQuery()) {
				if (resultSet1.next()) {
					currentId = resultSet1.getLong("currentId");
				}
			}

			try (Statement statement = connection.createStatement();
				ResultSet resultSet2 = statement.executeQuery(
					StringBundler.concat(
						"select max(", primaryKey, ") from ", tableName))) {

				if (resultSet2.next()) {
					long increment = Math.max(
						0, resultSet2.getLong(1) - currentId);

					if (increment > 0) {
						counterLocalService.increment(
							className, (int)increment);
					}
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyCountriesUtil.class);

	private static class RegionLocalizationData {

		private RegionLocalizationData(
			String languageId, long regionId, String title) {

			_languageId = languageId;
			_regionId = regionId;
			_title = title;
		}

		private final String _languageId;
		private final long _regionId;
		private final String _title;

	}

}