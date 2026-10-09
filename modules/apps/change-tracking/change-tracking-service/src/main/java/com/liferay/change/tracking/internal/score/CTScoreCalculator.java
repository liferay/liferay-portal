/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.score;

import com.liferay.change.tracking.internal.CTServiceRegistry;
import com.liferay.change.tracking.model.CTEntryTable;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.store.model.CTSContent;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnection;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = CTScoreCalculator.class)
public class CTScoreCalculator {

	public int calculate(long modelClassNameId) {
		Integer score = _portalCache.get(modelClassNameId);

		if (score != null) {
			return score;
		}

		score = 4;

		if (modelClassNameId == _classNameLocalService.getClassNameId(
				CTSContent.class)) {

			score += 20;
		}
		else if (modelClassNameId == _classNameLocalService.getClassNameId(
					JournalArticle.class)) {

			score++;
		}

		int countMultiplier = _countTable(modelClassNameId) / _COUNT_DIVISOR;

		if (countMultiplier == 0) {
			countMultiplier = 1;
		}

		score *= countMultiplier;

		DB db = DBManagerUtil.getDB();

		DBType dbType = db.getDBType();

		if (dbType.equals(DBType.ORACLE) || dbType.equals(DBType.SQLSERVER)) {
			score *= 2;
		}

		_portalCache.put(modelClassNameId, score);

		return score;
	}

	public int calculateCTCollectionScore(long ctCollectionId) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			List<Object[]> results = _ctEntryLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					CTEntryTable.INSTANCE.modelClassNameId,
					DSLFunctionFactoryUtil.count(
						CTEntryTable.INSTANCE.ctEntryId
					).as(
						"ctEntriesCount"
					)
				).from(
					CTEntryTable.INSTANCE
				).where(
					CTEntryTable.INSTANCE.ctCollectionId.eq(ctCollectionId)
				).groupBy(
					CTEntryTable.INSTANCE.modelClassNameId
				));

			int score = 0;

			for (Object[] result : results) {
				Number ctEntriesCount = (Number)result[1];

				score += calculate((Long)result[0]) * ctEntriesCount.intValue();
			}

			return score;
		}
	}

	public String getScoreSizeClassification(int score) {
		if (score > _THRESHOLD_LARGE) {
			return "large";
		}
		else if (score > _THRESHOLD_MEDIUM) {
			return "medium";
		}

		return "small";
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_portalCache = (PortalCache<Long, Integer>)_singleVMPool.getPortalCache(
			CTScoreCalculator.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_portalCache.removeAll();
	}

	private int _countTable(long modelClassNameId) {
		CTService<?> ctService = _ctServiceRegistry.getCTService(
			modelClassNameId);

		if (ctService == null) {
			return 0;
		}

		return ctService.updateWithUnsafeFunction(
			ctPersistence -> {
				Set<String> primaryKeyNames = ctPersistence.getCTColumnNames(
					CTColumnResolutionType.PK);

				if (primaryKeyNames.size() != 1) {
					throw new IllegalArgumentException(
						StringBundler.concat(
							"{primaryKeyNames=", primaryKeyNames,
							", tableName=", ctPersistence.getTableName(), "}"));
				}

				Iterator<String> iterator = primaryKeyNames.iterator();

				String primaryKeyName = iterator.next();

				Connection connection = _currentConnection.getConnection(
					ctPersistence.getDataSource());

				try (PreparedStatement preparedStatement =
						connection.prepareStatement(
							StringBundler.concat(
								"select count(", primaryKeyName,
								") as count from ",
								ctPersistence.getTableName()));

					ResultSet resultSet = preparedStatement.executeQuery()) {

					if (resultSet.next()) {
						return (int)resultSet.getLong("count");
					}

					return 0;
				}
				catch (SQLException sqlException) {
					if (_log.isWarnEnabled()) {
						_log.warn(sqlException);
					}
				}

				return 0;
			});
	}

	private static final int _COUNT_DIVISOR = 50000000;

	private static final int _THRESHOLD_LARGE = 20000;

	private static final int _THRESHOLD_MEDIUM = 10000;

	private static final Log _log = LogFactoryUtil.getLog(
		CTScoreCalculator.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private CTServiceRegistry _ctServiceRegistry;

	@Reference
	private CurrentConnection _currentConnection;

	private PortalCache<Long, Integer> _portalCache;

	@Reference
	private SingleVMPool _singleVMPool;

}