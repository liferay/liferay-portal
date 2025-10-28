/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.helper;

import com.liferay.change.tracking.internal.mapping.CTMappingTableInfoImpl;
import com.liferay.change.tracking.mapping.CTMappingTableInfo;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnectionUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Preston Crary
 */
public class CTTableMapperHelper {

	public CTTableMapperHelper(
		CTService<?> ctService, String tableName, String leftColumnName,
		Class<?> leftModelClass) {

		_ctService = ctService;
		_tableName = tableName;
		_leftColumnName = leftColumnName;
		_leftModelClass = leftModelClass;
	}

	public void delete(long ctCollectionId) throws Exception {
		_ctService.updateWithUnsafeFunction(
			ctPersistence -> {
				Connection connection = CurrentConnectionUtil.getConnection(
					ctPersistence.getDataSource());

				try (PreparedStatement preparedStatement =
						connection.prepareStatement(
							"delete from " + _tableName +
								" where ctCollectionId = ?")) {

					preparedStatement.setLong(1, ctCollectionId);

					preparedStatement.executeUpdate();
				}

				return null;
			});
	}

	public CTMappingTableInfo getCTMappingTableInfo(long ctCollectionId) {
		List<Map.Entry<Long, Long>> addedMappings = _getCTMappingChangeList(
			ctCollectionId, true);
		List<Map.Entry<Long, Long>> removedMappings = _getCTMappingChangeList(
			ctCollectionId, false);

		if (addedMappings.isEmpty() && removedMappings.isEmpty()) {
			return null;
		}

		return new CTMappingTableInfoImpl(
			_tableName, _leftColumnName, _leftModelClass, _rightColumnName,
			_rightModelClass, addedMappings, removedMappings);
	}

	public Class<?> getLeftModelClass() {
		return _leftModelClass;
	}

	public Class<?> getRightModelClass() {
		return _rightModelClass;
	}

	public void publish(
			long ctCollectionId, PortalCacheManager<?, ?> portalCacheManager)
		throws Exception {

		if (_rightColumnName == null) {
			throw new NullPointerException(
				StringBundler.concat(
					"Missing column name for ", _tableName,
					" with column name ", _leftColumnName));
		}

		int count = _ctService.updateWithUnsafeFunction(
			ctPersistence -> _publish(ctPersistence, ctCollectionId));

		if (count != 0) {
			_clearCache(
				portalCacheManager, _tableName, _leftColumnName,
				_rightColumnName);

			_clearCache(
				portalCacheManager, _tableName, _rightColumnName,
				_leftColumnName);
		}
	}

	public void setRightColumnName(String rightColumnName) {
		_rightColumnName = rightColumnName;
	}

	public void setRightModelClass(Class<?> rightModelClass) {
		_rightModelClass = rightModelClass;
	}

	public void undo(long fromCTCollectionId, long toCTCollectionId)
		throws Exception {

		if (_rightColumnName == null) {
			throw new NullPointerException(
				StringBundler.concat(
					"Missing column name for ", _tableName,
					" with column name ", _leftColumnName));
		}

		_ctService.updateWithUnsafeFunction(
			ctPersistence -> {
				Connection connection = CurrentConnectionUtil.getConnection(
					ctPersistence.getDataSource());

				try (PreparedStatement preparedStatement =
						connection.prepareStatement(
							StringBundler.concat(
								"insert into ", _tableName, " (companyId, ",
								_leftColumnName, ", ", _rightColumnName,
								", ctCollectionId, ctChangeType) select ",
								"t1.companyId, t1.", _leftColumnName, ", t1.",
								_rightColumnName, ", ", toCTCollectionId,
								" as ctCollectionId, ? as ctChangeType from ",
								_tableName, " t1 where t1.ctCollectionId = ? ",
								"and t1.ctChangeType = ?"))) {

					preparedStatement.setBoolean(1, true);
					preparedStatement.setLong(2, fromCTCollectionId);
					preparedStatement.setBoolean(3, false);

					preparedStatement.executeUpdate();

					preparedStatement.setBoolean(1, false);
					preparedStatement.setLong(2, fromCTCollectionId);
					preparedStatement.setBoolean(3, true);

					preparedStatement.executeUpdate();
				}

				return null;
			});
	}

	private void _clearCache(
		PortalCacheManager<?, ?> portalCacheManager, String tableName,
		String leftColumnName, String rightColumnName) {

		String portalCacheName = StringBundler.concat(
			TableMapper.class.getName(), "-", tableName, "-", leftColumnName,
			"-To-", rightColumnName);

		PortalCache<?, ?> portalCache = portalCacheManager.fetchPortalCache(
			portalCacheName);

		if (portalCache != null) {
			portalCache.removeAll();
		}
	}

	private List<Map.Entry<Long, Long>> _getCTMappingChangeList(
		long ctCollectionId, boolean ctChangeType) {

		CTPersistence<?> ctPersistence = _ctService.getCTPersistence();

		List<Map.Entry<Long, Long>> mappingChanges = new ArrayList<>();

		Session session = ctPersistence.getCurrentSession();

		session.apply(
			connection -> {
				try (PreparedStatement preparedStatement =
						connection.prepareStatement(
							StringBundler.concat(
								"select ", _leftColumnName, ", ",
								_rightColumnName, " from ", _tableName,
								" where ctCollectionId = ? and ctChangeType = ",
								"?"))) {

					preparedStatement.setLong(1, ctCollectionId);
					preparedStatement.setBoolean(2, ctChangeType);

					try (ResultSet resultSet =
							preparedStatement.executeQuery()) {

						while (resultSet.next()) {
							mappingChanges.add(
								new AbstractMap.SimpleImmutableEntry<>(
									resultSet.getLong(1),
									resultSet.getLong(2)));
						}
					}
				}
			});

		return mappingChanges;
	}

	private int _publish(CTPersistence<?> ctPersistence, long ctCollectionId)
		throws Exception {

		Connection connection = CurrentConnectionUtil.getConnection(
			ctPersistence.getDataSource());

		List<Map.Entry<Long, Long>> entries = new ArrayList<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ", _leftColumnName, ", ", _rightColumnName,
					" from ", _tableName, " where ctCollectionId = ?"))) {

			preparedStatement.setLong(1, ctCollectionId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					entries.add(
						new AbstractMap.SimpleImmutableEntry<>(
							resultSet.getLong(1), resultSet.getLong(2)));
				}
			}
		}

		int count = 0;

		if (!entries.isEmpty()) {
			StringBundler sb = new StringBundler((8 * entries.size()) + 3);

			sb.append("delete from ");
			sb.append(_tableName);
			sb.append(" where ctCollectionId = 0 and ((");

			for (Map.Entry<Long, Long> entry : entries) {
				sb.append(_leftColumnName);
				sb.append(" = ");
				sb.append(entry.getKey());
				sb.append(" and ");
				sb.append(_rightColumnName);
				sb.append(" = ");
				sb.append(entry.getValue());
				sb.append(") or (");
			}

			sb.setStringAt("))", sb.index() - 1);

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(sb.toString())) {

				count += preparedStatement.executeUpdate();
			}
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into ", _tableName, " (companyId, ",
					_leftColumnName, ", ", _rightColumnName,
					", ctCollectionId) select t1.companyId, t1.",
					_leftColumnName, ", t1.", _rightColumnName,
					", 0 as ctCollectionId from ", _tableName, " t1 left join ",
					_tableName, " t2 on t2.ctCollectionId = 0 and t2.",
					_leftColumnName, " = t1.", _leftColumnName, " and t2.",
					_rightColumnName, " = t1.", _rightColumnName,
					" where t1.ctCollectionId = ? and t1.ctChangeType = ?"))) {

			preparedStatement.setLong(1, ctCollectionId);
			preparedStatement.setBoolean(2, true);

			count += preparedStatement.executeUpdate();
		}

		return count;
	}

	private final CTService<?> _ctService;
	private final String _leftColumnName;
	private final Class<?> _leftModelClass;
	private String _rightColumnName;
	private Class<?> _rightModelClass;
	private final String _tableName;

}