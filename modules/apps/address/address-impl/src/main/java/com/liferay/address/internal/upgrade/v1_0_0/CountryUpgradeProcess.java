/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.internal.upgrade.v1_0_0;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CountryConstants;
import com.liferay.portal.kernel.model.CountryLocalization;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.RegionLocalization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Mariano Álvaro Sáiz
 */
public class CountryUpgradeProcess extends UpgradeProcess {

	public CountryUpgradeProcess(
		CompanyLocalService companyLocalService,
		CounterLocalService counterLocalService) {

		Class<?> clazz = getClass();

		_classLoader = clazz.getClassLoader();

		_companyLocalService = companyLocalService;
		_counterLocalService = counterLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_incrementCounters();

		List<IndexMetadata> indexMetadatas = _dropIndexes();

		try {
			processConcurrently(
				ArrayUtil.toArray(
					CompanyThreadLocal.isLocked() ?
						new long[] {CompanyThreadLocal.getCompanyId()} :
							PortalInstancePool.getCompanyIds()),
				companyId -> {
					try {
						new CompanyUpgradeProcess(
							_companyLocalService.fetchCompany(companyId)
						).populateCompanyCountries();
					}
					catch (Exception exception) {
						_log.error(
							"Unable to populate countries for company " +
								companyId,
							exception);
					}
				},
				"Unable to populate countries");

			_incrementCounters();
		}
		finally {
			_addIndexes(indexMetadatas);
		}
	}

	private void _addIndexes(List<IndexMetadata> indexMetadatas)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		List<IndexMetadata> addIndexMetadatas = new ArrayList<>();

		for (IndexMetadata indexMetadata : indexMetadatas) {
			if (!hasIndex(
					indexMetadata.getTableName(),
					indexMetadata.getIndexName())) {

				addIndexMetadatas.add(indexMetadata);
			}
		}

		db.addIndexes(connection, addIndexMetadatas);
	}

	private List<IndexMetadata> _dropIndexes() throws Exception {
		DB db = DBManagerUtil.getDB();

		List<IndexMetadata> indexMetadatas = new ArrayList<>();

		indexMetadatas.addAll(
			db.getIndexMetadatas(
				connection, "CountryLocalization", null, false));
		indexMetadatas.addAll(
			db.getIndexMetadatas(connection, "Region", null, false));
		indexMetadatas.addAll(
			db.getIndexMetadatas(
				connection, "RegionLocalization", null, false));

		List<IndexMetadata> droppedIndexMetadatas = new ArrayList<>();

		try {
			for (IndexMetadata indexMetadata : indexMetadatas) {
				db.runSQL(indexMetadata.getDropSQL());

				droppedIndexMetadatas.add(indexMetadata);
			}
		}
		catch (Exception exception) {
			_addIndexes(droppedIndexMetadatas);

			throw exception;
		}

		return indexMetadatas;
	}

	private void _incrementCounter(
			String tableName, String columnName, String className)
		throws Exception {

		try (Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(
				StringBundler.concat(
					"select max(", columnName, ") from ", tableName))) {

			if (resultSet.next()) {
				increment(className, (int)resultSet.getLong(1));
			}
		}
	}

	private void _incrementCounters() throws Exception {
		_incrementCounter(
			"CountryLocalization", "countryLocalizationId",
			CountryLocalization.class.getName());

		_countryLocalizationCounter = new AtomicLong(
			_counterLocalService.getCurrentId(
				CountryLocalization.class.getName()));

		_incrementCounter("Region", "regionId", Region.class.getName());

		_regionCounter = new AtomicLong(
			_counterLocalService.getCurrentId(Region.class.getName()));

		_incrementCounter(
			"RegionLocalization", "regionLocalizationId",
			RegionLocalization.class.getName());

		_regionLocalizationCounter = new AtomicLong(
			_counterLocalService.getCurrentId(
				RegionLocalization.class.getName()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CountryUpgradeProcess.class);

	private final ClassLoader _classLoader;
	private final CompanyLocalService _companyLocalService;
	private final CounterLocalService _counterLocalService;
	private AtomicLong _countryLocalizationCounter;
	private AtomicLong _regionCounter;
	private AtomicLong _regionLocalizationCounter;

	private class CompanyUpgradeProcess {

		public CompanyUpgradeProcess(Company company) {
			_company = company;
		}

		public void populateCompanyCountries() throws Exception {
			if (_hasCountries(_company.getCompanyId())) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Skipping country initialization because ",
							"countries were already initialized for company ",
							_company.getCompanyId()));
				}

				return;
			}

			_availableLocales = LanguageUtil.getCompanyAvailableLocales(
				_company.getCompanyId());
			_createDate = new Date(System.currentTimeMillis());
			_guestUser = _company.getGuestUser();

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Initializing countries for company " +
						_company.getCompanyId());
			}

			JSONArray countriesJSONArray = _getJSONArray(
				"com/liferay/address/dependencies/countries.json");

			try (PreparedStatement preparedStatement1 =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						StringBundler.concat(
							"insert into Country (mvccVersion, uuid_, ",
							"defaultLanguageId, countryId, companyId, userId, ",
							"userName, createDate, modifiedDate, a2, a3, ",
							"active_, billingAllowed, groupFilterEnabled, ",
							"idd_, name, number_, position, shippingAllowed, ",
							"subjectToVAT, zipRequired, lastPublishDate) ",
							"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
							"?, ?, ?, ?, ? , ?, ?, ?)"));
				PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						StringBundler.concat(
							"insert into CountryLocalization (mvccVersion, ",
							"countryLocalizationId, companyId, countryId, ",
							"languageId, title) values (?, ?, ?, ?, ?, ?)"));
				PreparedStatement preparedStatement3 =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						StringBundler.concat(
							"insert into Region (mvccVersion, uuid_, ",
							"defaultLanguageId, regionId, companyId, userId, ",
							"userName, createDate, modifiedDate, countryId, ",
							"active_, name, position, regionCode, ",
							"lastPublishDate) values (?, ?, ?, ?, ?, ?, ?, ?, ",
							"?, ?, ?, ?, ?, ?, ?)"));
				PreparedStatement preparedStatement4 =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						StringBundler.concat(
							"insert into RegionLocalization (mvccVersion, ",
							"regionLocalizationId, companyId, regionId, ",
							"languageId, title) values (?, ?, ?, ?, ?, ?)"))) {

				_preparedStatement1 = preparedStatement1;
				_preparedStatement2 = preparedStatement2;
				_preparedStatement3 = preparedStatement3;
				_preparedStatement4 = preparedStatement4;

				for (int i = 0; i < countriesJSONArray.length(); i++) {
					JSONObject countryJSONObject =
						countriesJSONArray.getJSONObject(i);

					_addCountry(countryJSONObject);
				}

				_preparedStatement1.executeBatch();

				_preparedStatement2.executeBatch();

				_preparedStatement3.executeBatch();

				_preparedStatement4.executeBatch();
			}
		}

		private void _addCountry(JSONObject countryJSONObject)
			throws Exception {

			long countryId = increment();

			_preparedStatement1.setLong(1, 0L);
			_preparedStatement1.setString(2, PortalUUIDUtil.generate());
			_preparedStatement1.setString(
				3,
				UpgradeProcessUtil.getDefaultLanguageId(
					_company.getCompanyId()));
			_preparedStatement1.setLong(4, countryId);
			_preparedStatement1.setLong(5, _company.getCompanyId());
			_preparedStatement1.setLong(6, _guestUser.getUserId());
			_preparedStatement1.setString(7, _guestUser.getFullName());
			_preparedStatement1.setDate(8, _createDate);
			_preparedStatement1.setDate(9, _createDate);
			_preparedStatement1.setString(
				10, countryJSONObject.getString("a2"));
			_preparedStatement1.setString(
				11, countryJSONObject.getString("a3"));
			_preparedStatement1.setBoolean(12, true);
			_preparedStatement1.setBoolean(13, true);
			_preparedStatement1.setBoolean(14, false);
			_preparedStatement1.setString(
				15, countryJSONObject.getString("idd"));
			_preparedStatement1.setString(
				16, countryJSONObject.getString("name"));
			_preparedStatement1.setString(
				17, countryJSONObject.getString("number"));
			_preparedStatement1.setDouble(18, 0.0);
			_preparedStatement1.setBoolean(19, true);
			_preparedStatement1.setBoolean(20, false);
			_preparedStatement1.setBoolean(
				21, countryJSONObject.getBoolean("zipRequired"));
			_preparedStatement1.setDate(22, _createDate);

			_preparedStatement1.addBatch();

			for (Locale locale : _availableLocales) {
				_preparedStatement2.setLong(1, 0L);
				_preparedStatement2.setLong(
					2, _countryLocalizationCounter.incrementAndGet());
				_preparedStatement2.setLong(3, _company.getCompanyId());
				_preparedStatement2.setLong(4, countryId);
				_preparedStatement2.setString(5, _getLanguageId(locale));
				_preparedStatement2.setString(
					6,
					_getLocalizedName(
						locale, countryJSONObject.getString("name")));

				_preparedStatement2.addBatch();
			}

			_processRegions(countryJSONObject.getString("a2"), countryId);
		}

		private void _addRegion(long countryId, JSONObject regionJSONObject)
			throws Exception {

			long regionId = _regionCounter.incrementAndGet();
			String regionName = regionJSONObject.getString("name");

			_preparedStatement3.setLong(1, 0L);
			_preparedStatement3.setString(2, PortalUUIDUtil.generate());
			_preparedStatement3.setString(
				3,
				UpgradeProcessUtil.getDefaultLanguageId(
					_company.getCompanyId()));
			_preparedStatement3.setLong(4, regionId);
			_preparedStatement3.setLong(5, _company.getCompanyId());
			_preparedStatement3.setLong(6, _guestUser.getUserId());
			_preparedStatement3.setString(7, _guestUser.getFullName());
			_preparedStatement3.setDate(8, _createDate);
			_preparedStatement3.setDate(9, _createDate);
			_preparedStatement3.setLong(10, countryId);
			_preparedStatement3.setBoolean(11, true);
			_preparedStatement3.setString(
				12, regionJSONObject.getString("name"));
			_preparedStatement3.setDouble(13, 0.0);
			_preparedStatement3.setString(
				14, regionJSONObject.getString("regionCode"));
			_preparedStatement3.setDate(15, _createDate);

			_preparedStatement3.addBatch();

			JSONObject localizationsJSONObject = regionJSONObject.getJSONObject(
				"localizations");

			Map<String, String> titleMap = new HashMap<>();

			if (localizationsJSONObject == null) {
				for (Locale locale : _availableLocales) {
					titleMap.put(_getLanguageId(locale), regionName);
				}
			}
			else {
				for (String key : localizationsJSONObject.keySet()) {
					titleMap.put(key, localizationsJSONObject.getString(key));
				}
			}

			for (Map.Entry<String, String> entryMap : titleMap.entrySet()) {
				_preparedStatement4.setLong(1, 0L);
				_preparedStatement4.setLong(
					2, _regionLocalizationCounter.incrementAndGet());
				_preparedStatement4.setLong(3, _company.getCompanyId());
				_preparedStatement4.setLong(4, regionId);
				_preparedStatement4.setString(5, entryMap.getKey());
				_preparedStatement4.setString(6, entryMap.getValue());

				_preparedStatement4.addBatch();
			}
		}

		private JSONArray _getJSONArray(String path) throws Exception {
			return JSONFactoryUtil.createJSONArray(
				StringUtil.read(_classLoader, path, false));
		}

		private String _getLanguageId(Locale locale) {
			return _languageIds.computeIfAbsent(
				locale, key -> LanguageUtil.getLanguageId(key));
		}

		private String _getLocalizedName(Locale locale, String name) {
			String localizedName = LanguageUtil.get(
				locale, CountryConstants.NAME_PREFIX + name);

			if (!localizedName.startsWith(CountryConstants.NAME_PREFIX)) {
				return localizedName;
			}

			return name;
		}

		private boolean _hasCountries(long companyId) throws Exception {
			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select count(*) from Country where companyId = ?")) {

				preparedStatement.setLong(1, companyId);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						int count = resultSet.getInt(1);

						if (count > 0) {
							return true;
						}
					}

					return false;
				}
			}
		}

		private void _processRegions(String a2, long countryId)
			throws Exception {

			String path =
				"com/liferay/address/dependencies/regions/" + a2 + ".json";

			if (_classLoader.getResource(path) == null) {
				return;
			}

			JSONArray regionsJSONArray = _getJSONArray(path);

			if (_log.isDebugEnabled()) {
				_log.debug("Found regions for country " + a2);
			}

			for (int i = 0; i < regionsJSONArray.length(); i++) {
				JSONObject regionJSONObject = regionsJSONArray.getJSONObject(i);

				_addRegion(countryId, regionJSONObject);
			}
		}

		private Set<Locale> _availableLocales;
		private final Company _company;
		private Date _createDate;
		private User _guestUser;
		private final Map<Locale, String> _languageIds = new HashMap<>();
		private PreparedStatement _preparedStatement1;
		private PreparedStatement _preparedStatement2;
		private PreparedStatement _preparedStatement3;
		private PreparedStatement _preparedStatement4;

	}

}