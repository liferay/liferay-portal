/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.score;

import com.liferay.change.tracking.internal.CTServiceRegistry;
import com.liferay.change.tracking.store.model.CTSContent;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
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

	@Activate
	protected void activate(BundleContext bundleContext) {
		_portalCache = (PortalCache<Long, Integer>)_multiVMPool.getPortalCache(
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
								"select count(", primaryKeyName, ") from ",
								ctPersistence.getTableName()));
					ResultSet resultSet = preparedStatement.executeQuery()) {

					if (resultSet.next()) {
						return resultSet.getInt(1);
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

	private static final Log _log = LogFactoryUtil.getLog(
		CTScoreCalculator.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CTServiceRegistry _ctServiceRegistry;

	@Reference
	private CurrentConnection _currentConnection;

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<Long, Integer> _portalCache;

}