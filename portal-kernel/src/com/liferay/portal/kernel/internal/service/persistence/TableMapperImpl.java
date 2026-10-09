/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.service.persistence;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.dao.jdbc.MappingSqlQuery;
import com.liferay.portal.kernel.dao.jdbc.MappingSqlQueryFactoryUtil;
import com.liferay.portal.kernel.dao.jdbc.ParamSetter;
import com.liferay.portal.kernel.dao.jdbc.RowMapper;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdate;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdateFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.internal.cache.DummyPortalCache;
import com.liferay.portal.kernel.internal.dao.orm.TableMapperArgumentResolver;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ModelListenerRegistrationUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
public class TableMapperImpl<L extends BaseModel<L>, R extends BaseModel<R>>
	implements TableMapper<L, R> {

	public TableMapperImpl(
		String tableName, String companyColumnName, String leftColumnName,
		String rightColumnName, Class<L> leftModelClass,
		Class<R> rightModelClass, BasePersistence<L> leftBasePersistence,
		BasePersistence<R> rightBasePersistence, boolean cacheless) {

		_tableName = tableName;
		this.leftColumnName = leftColumnName;
		this.rightColumnName = rightColumnName;
		this.leftModelClass = leftModelClass;
		this.rightModelClass = rightModelClass;
		this.leftBasePersistence = leftBasePersistence;
		this.rightBasePersistence = rightBasePersistence;
		this.cacheless = cacheless;

		String leftToRightPortalCacheName = StringBundler.concat(
			TableMapper.class.getName(), "-", tableName, "-", leftColumnName,
			"-To-", rightColumnName);
		String rightToLeftPortalCacheName = StringBundler.concat(
			TableMapper.class.getName(), "-", tableName, "-", rightColumnName,
			"-To-", leftColumnName);

		if (cacheless) {
			leftToRightPortalCache = new DummyPortalCache<>(
				PortalCacheManagerNames.MULTI_VM, leftToRightPortalCacheName);
			rightToLeftPortalCache = new DummyPortalCache<>(
				PortalCacheManagerNames.MULTI_VM, rightToLeftPortalCacheName);
		}
		else {
			leftToRightPortalCache = PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.MULTI_VM, leftToRightPortalCacheName);
			rightToLeftPortalCache = PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.MULTI_VM, rightToLeftPortalCacheName);
		}

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			ArgumentsResolver.class, new TableMapperArgumentResolver(tableName),
			HashMapDictionaryBuilder.put(
				"class.name", tableName
			).put(
				"table.name", tableName
			).build());

		init(tableName, companyColumnName, leftColumnName, rightColumnName);
	}

	@Override
	public boolean addTableMapping(
		long companyId, long leftPrimaryKey, long rightPrimaryKey) {

		if (containsTableMapping(leftPrimaryKey, rightPrimaryKey, false)) {
			return false;
		}

		leftToRightPortalCache.remove(leftPrimaryKey);
		rightToLeftPortalCache.remove(rightPrimaryKey);

		FinderCacheUtil.clearDSLQueryCache(_tableName);

		_addTableMapping(companyId, leftPrimaryKey, rightPrimaryKey);

		return true;
	}

	@Override
	public long[] addTableMappings(
		long companyId, long leftPrimaryKey, long[] rightPrimaryKeys) {

		List<Long> addedRightPrimaryKeys = new ArrayList<>();
		long[] currentRightPrimaryKeys = getPrimaryKeys(
			leftToRightPortalCache, getRightPrimaryKeysSqlQuery, leftPrimaryKey,
			false);

		for (long rightPrimaryKey : rightPrimaryKeys) {
			if (Arrays.binarySearch(currentRightPrimaryKeys, rightPrimaryKey) >=
					0) {

				continue;
			}

			addedRightPrimaryKeys.add(rightPrimaryKey);

			rightToLeftPortalCache.remove(rightPrimaryKey);

			_addTableMapping(companyId, leftPrimaryKey, rightPrimaryKey);
		}

		if (!addedRightPrimaryKeys.isEmpty()) {
			leftToRightPortalCache.remove(leftPrimaryKey);

			FinderCacheUtil.clearDSLQueryCache(_tableName);
		}

		return ArrayUtil.toLongArray(addedRightPrimaryKeys);
	}

	@Override
	public long[] addTableMappings(
		long companyId, long[] leftPrimaryKeys, long rightPrimaryKey) {

		List<Long> addedLeftPrimaryKeys = new ArrayList<>();
		long[] currentLeftPrimaryKeys = getPrimaryKeys(
			rightToLeftPortalCache, getLeftPrimaryKeysSqlQuery, rightPrimaryKey,
			false);

		for (long leftPrimaryKey : leftPrimaryKeys) {
			if (Arrays.binarySearch(currentLeftPrimaryKeys, leftPrimaryKey) >=
					0) {

				continue;
			}

			addedLeftPrimaryKeys.add(leftPrimaryKey);

			leftToRightPortalCache.remove(leftPrimaryKey);

			_addTableMapping(companyId, leftPrimaryKey, rightPrimaryKey);
		}

		if (!addedLeftPrimaryKeys.isEmpty()) {
			rightToLeftPortalCache.remove(rightPrimaryKey);

			FinderCacheUtil.clearDSLQueryCache(_tableName);
		}

		return ArrayUtil.toLongArray(addedLeftPrimaryKeys);
	}

	@Override
	public boolean containsTableMapping(
		long leftPrimaryKey, long rightPrimaryKey) {

		return containsTableMapping(leftPrimaryKey, rightPrimaryKey, true);
	}

	@Override
	public int deleteLeftPrimaryKeyTableMappings(long leftPrimaryKey) {
		int count = deleteTableMappings(
			leftModelClass, rightModelClass, leftToRightPortalCache,
			rightToLeftPortalCache, getRightPrimaryKeysSqlQuery,
			deleteLeftPrimaryKeyTableMappingsSqlUpdate, leftPrimaryKey);

		if (count > 0) {
			FinderCacheUtil.clearDSLQueryCache(_tableName);
		}

		return count;
	}

	@Override
	public int deleteRightPrimaryKeyTableMappings(long rightPrimaryKey) {
		int count = deleteTableMappings(
			rightModelClass, leftModelClass, rightToLeftPortalCache,
			leftToRightPortalCache, getLeftPrimaryKeysSqlQuery,
			deleteRightPrimaryKeyTableMappingsSqlUpdate, rightPrimaryKey);

		if (count > 0) {
			FinderCacheUtil.clearDSLQueryCache(_tableName);
		}

		return count;
	}

	@Override
	public boolean deleteTableMapping(
		long leftPrimaryKey, long rightPrimaryKey) {

		if (!containsTableMapping(leftPrimaryKey, rightPrimaryKey, false)) {
			return false;
		}

		leftToRightPortalCache.remove(leftPrimaryKey);
		rightToLeftPortalCache.remove(rightPrimaryKey);

		FinderCacheUtil.clearDSLQueryCache(_tableName);

		return _deleteTableMapping(leftPrimaryKey, rightPrimaryKey);
	}

	@Override
	public long[] deleteTableMappings(
		long leftPrimaryKey, long[] rightPrimaryKeys) {

		boolean clearCache = false;
		long[] currentRightPrimaryKeys = getPrimaryKeys(
			leftToRightPortalCache, getRightPrimaryKeysSqlQuery, leftPrimaryKey,
			false);
		List<Long> deletedRightPrimaryKeys = new ArrayList<>();

		for (long rightPrimaryKey : rightPrimaryKeys) {
			if (Arrays.binarySearch(currentRightPrimaryKeys, rightPrimaryKey) <
					0) {

				continue;
			}

			clearCache = true;

			rightToLeftPortalCache.remove(rightPrimaryKey);

			if (!_deleteTableMapping(leftPrimaryKey, rightPrimaryKey)) {
				continue;
			}

			deletedRightPrimaryKeys.add(rightPrimaryKey);
		}

		if (clearCache) {
			leftToRightPortalCache.remove(leftPrimaryKey);

			FinderCacheUtil.clearDSLQueryCache(_tableName);
		}

		return ArrayUtil.toLongArray(deletedRightPrimaryKeys);
	}

	@Override
	public long[] deleteTableMappings(
		long[] leftPrimaryKeys, long rightPrimaryKey) {

		boolean clearCache = false;
		long[] currentLeftPrimaryKeys = getPrimaryKeys(
			rightToLeftPortalCache, getLeftPrimaryKeysSqlQuery, rightPrimaryKey,
			false);
		List<Long> deletedLeftPrimaryKeys = new ArrayList<>();

		for (long leftPrimaryKey : leftPrimaryKeys) {
			if (Arrays.binarySearch(currentLeftPrimaryKeys, leftPrimaryKey) <
					0) {

				continue;
			}

			leftToRightPortalCache.remove(leftPrimaryKey);

			clearCache = true;

			if (!_deleteTableMapping(leftPrimaryKey, rightPrimaryKey)) {
				continue;
			}

			deletedLeftPrimaryKeys.add(leftPrimaryKey);
		}

		if (clearCache) {
			rightToLeftPortalCache.remove(rightPrimaryKey);

			FinderCacheUtil.clearDSLQueryCache(_tableName);
		}

		return ArrayUtil.toLongArray(deletedLeftPrimaryKeys);
	}

	@Override
	public void destroy() {
		PortalCacheHelperUtil.removePortalCache(
			PortalCacheManagerNames.MULTI_VM,
			leftToRightPortalCache.getPortalCacheName());
		PortalCacheHelperUtil.removePortalCache(
			PortalCacheManagerNames.MULTI_VM,
			rightToLeftPortalCache.getPortalCacheName());

		_serviceRegistration.unregister();
	}

	@Override
	public List<L> getLeftBaseModels(
		long rightPrimaryKey, int start, int end,
		OrderByComparator<L> orderByComparator) {

		return getBaseModels(
			rightToLeftPortalCache, getLeftPrimaryKeysSqlQuery, rightPrimaryKey,
			leftBasePersistence, start, end, orderByComparator);
	}

	@Override
	public long[] getLeftPrimaryKeys(long rightPrimaryKey) {
		return getPrimaryKeys(
			rightToLeftPortalCache, getLeftPrimaryKeysSqlQuery, rightPrimaryKey,
			true);
	}

	@Override
	public TableMapper<R, L> getReverseTableMapper() {
		return reverseTableMapper;
	}

	@Override
	public List<R> getRightBaseModels(
		long leftPrimaryKey, int start, int end,
		OrderByComparator<R> orderByComparator) {

		return getBaseModels(
			leftToRightPortalCache, getRightPrimaryKeysSqlQuery, leftPrimaryKey,
			rightBasePersistence, start, end, orderByComparator);
	}

	@Override
	public long[] getRightPrimaryKeys(long leftPrimaryKey) {
		return getPrimaryKeys(
			leftToRightPortalCache, getRightPrimaryKeysSqlQuery, leftPrimaryKey,
			true);
	}

	@Override
	public boolean matches(String leftColumnName, String rightColumnName) {
		if (this.leftColumnName.equals(leftColumnName) &&
			this.rightColumnName.equals(rightColumnName)) {

			return true;
		}

		return false;
	}

	protected static <M extends BaseModel<M>, S extends BaseModel<S>> int
		deleteTableMappings(
			Class<M> masterModelClass, Class<S> slaveModelClass,
			PortalCache<Long, long[]> masterToSlavePortalCache,
			PortalCache<Long, long[]> slaveToMasterPortalCache,
			MappingSqlQuery<Long> mappingSqlQuery, SqlUpdate deleteSqlUpdate,
			long masterPrimaryKey) {

		ModelListener<M>[] masterModelListeners =
			ModelListenerRegistrationUtil.getModelListeners(masterModelClass);
		ModelListener<S>[] slaveModelListeners =
			ModelListenerRegistrationUtil.getModelListeners(slaveModelClass);

		long[] slavePrimaryKeys = getPrimaryKeys(
			masterToSlavePortalCache, mappingSqlQuery, masterPrimaryKey, false);

		if ((masterModelListeners.length > 0) ||
			(slaveModelListeners.length > 0)) {

			for (long slavePrimaryKey : slavePrimaryKeys) {
				for (ModelListener<M> masterModelListener :
						masterModelListeners) {

					masterModelListener.onBeforeRemoveAssociation(
						masterPrimaryKey, slaveModelClass.getName(),
						slavePrimaryKey);
				}

				for (ModelListener<S> slaveModelListener :
						slaveModelListeners) {

					slaveModelListener.onBeforeRemoveAssociation(
						slavePrimaryKey, masterModelClass.getName(),
						masterPrimaryKey);
				}
			}
		}

		masterToSlavePortalCache.remove(masterPrimaryKey);

		for (long slavePrimaryKey : slavePrimaryKeys) {
			slaveToMasterPortalCache.remove(slavePrimaryKey);
		}

		int rowCount = 0;

		if (slavePrimaryKeys.length > 0) {
			try {
				rowCount = deleteSqlUpdate.update(masterPrimaryKey);
			}
			catch (Exception exception) {
				throw new SystemException(exception);
			}
		}

		if ((masterModelListeners.length > 0) ||
			(slaveModelListeners.length > 0)) {

			for (long slavePrimaryKey : slavePrimaryKeys) {
				for (ModelListener<M> masterModelListener :
						masterModelListeners) {

					masterModelListener.onAfterRemoveAssociation(
						masterPrimaryKey, slaveModelClass.getName(),
						slavePrimaryKey);
				}

				for (ModelListener<S> slaveModelListener :
						slaveModelListeners) {

					slaveModelListener.onAfterRemoveAssociation(
						slavePrimaryKey, masterModelClass.getName(),
						masterPrimaryKey);
				}
			}
		}

		return rowCount;
	}

	protected static <T extends BaseModel<T>> List<T> getBaseModels(
		PortalCache<Long, long[]> portalCache,
		MappingSqlQuery<Long> mappingSqlQuery, long masterPrimaryKey,
		BasePersistence<T> slaveBasePersistence, int start, int end,
		OrderByComparator<T> orderByComparator) {

		long[] slavePrimaryKeys = getPrimaryKeys(
			portalCache, mappingSqlQuery, masterPrimaryKey, true);

		if (slavePrimaryKeys.length == 0) {
			return Collections.emptyList();
		}

		List<T> slaveBaseModels = new ArrayList<>(slavePrimaryKeys.length);

		try {
			for (long slavePrimaryKey : slavePrimaryKeys) {
				slaveBaseModels.add(
					slaveBasePersistence.findByPrimaryKey(slavePrimaryKey));
			}
		}
		catch (NoSuchModelException noSuchModelException) {
			throw new SystemException(noSuchModelException);
		}

		if (orderByComparator != null) {
			Collections.sort(slaveBaseModels, orderByComparator);
		}

		return ListUtil.subList(slaveBaseModels, start, end);
	}

	protected static long[] getPrimaryKeys(
		PortalCache<Long, long[]> portalCache,
		MappingSqlQuery<Long> mappingSqlQuery, long masterPrimaryKey,
		boolean updateCache) {

		long[] primaryKeys = portalCache.get(masterPrimaryKey);

		if (primaryKeys == null) {
			List<Long> primaryKeysList = null;

			try {
				primaryKeysList = mappingSqlQuery.execute(masterPrimaryKey);
			}
			catch (Exception exception) {
				throw new SystemException(exception);
			}

			primaryKeys = new long[primaryKeysList.size()];

			for (int i = 0; i < primaryKeys.length; i++) {
				primaryKeys[i] = primaryKeysList.get(i);
			}

			Arrays.sort(primaryKeys);

			if (updateCache) {
				PortalCacheHelperUtil.putWithoutReplicator(
					portalCache, masterPrimaryKey, primaryKeys);
			}
		}

		return primaryKeys;
	}

	protected boolean containsTableMapping(
		long leftPrimaryKey, long rightPrimaryKey, boolean updateCache) {

		if (cacheless) {
			List<Integer> counts = null;

			try {
				counts = containsTableMappingSQL.execute(
					leftPrimaryKey, rightPrimaryKey);
			}
			catch (Exception exception) {
				throw new SystemException(exception);
			}

			if (counts.isEmpty()) {
				return false;
			}

			int count = counts.get(0);

			if (count == 0) {
				return false;
			}

			return true;
		}

		long[] rightPrimaryKeys = getPrimaryKeys(
			leftToRightPortalCache, getRightPrimaryKeysSqlQuery, leftPrimaryKey,
			updateCache);

		if (Arrays.binarySearch(rightPrimaryKeys, rightPrimaryKey) < 0) {
			return false;
		}

		return true;
	}

	protected void init(
		String tableName, String companyColumnName, String leftColumnName,
		String rightColumnName) {

		DataSource dataSource = leftBasePersistence.getDataSource();

		addTableMappingSqlUpdate = SqlUpdateFactoryUtil.getSqlUpdate(
			dataSource,
			StringBundler.concat(
				"INSERT INTO ", tableName, " (", companyColumnName, ", ",
				leftColumnName, ", ", rightColumnName, ") VALUES (?, ?, ?)"),
			ParamSetter.BIGINT, ParamSetter.BIGINT, ParamSetter.BIGINT);

		if (cacheless) {
			containsTableMappingSQL =
				MappingSqlQueryFactoryUtil.getMappingSqlQuery(
					dataSource,
					StringBundler.concat(
						"SELECT * FROM ", tableName, " WHERE ", leftColumnName,
						" = ? AND ", rightColumnName, " = ?"),
					RowMapper.COUNT, ParamSetter.BIGINT, ParamSetter.BIGINT);
		}

		deleteLeftPrimaryKeyTableMappingsSqlUpdate =
			SqlUpdateFactoryUtil.getSqlUpdate(
				dataSource,
				StringBundler.concat(
					"DELETE FROM ", tableName, " WHERE ", leftColumnName,
					" = ?"),
				ParamSetter.BIGINT);
		deleteRightPrimaryKeyTableMappingsSqlUpdate =
			SqlUpdateFactoryUtil.getSqlUpdate(
				dataSource,
				StringBundler.concat(
					"DELETE FROM ", tableName, " WHERE ", rightColumnName,
					" = ?"),
				ParamSetter.BIGINT);
		deleteTableMappingSqlUpdate = SqlUpdateFactoryUtil.getSqlUpdate(
			dataSource,
			StringBundler.concat(
				"DELETE FROM ", tableName, " WHERE ", leftColumnName,
				" = ? AND ", rightColumnName, " = ?"),
			ParamSetter.BIGINT, ParamSetter.BIGINT);
		getLeftPrimaryKeysSqlQuery =
			MappingSqlQueryFactoryUtil.getMappingSqlQuery(
				dataSource,
				StringBundler.concat(
					"SELECT ", leftColumnName, " FROM ", tableName, " WHERE ",
					rightColumnName, " = ?"),
				RowMapper.PRIMARY_KEY, ParamSetter.BIGINT);
		getRightPrimaryKeysSqlQuery =
			MappingSqlQueryFactoryUtil.getMappingSqlQuery(
				dataSource,
				StringBundler.concat(
					"SELECT ", rightColumnName, " FROM ", tableName, " WHERE ",
					leftColumnName, " = ?"),
				RowMapper.PRIMARY_KEY, ParamSetter.BIGINT);

		reverseTableMapper = new ReverseTableMapper<>(this);
	}

	protected SqlUpdate addTableMappingSqlUpdate;
	protected final boolean cacheless;
	protected MappingSqlQuery<Integer> containsTableMappingSQL;
	protected SqlUpdate deleteLeftPrimaryKeyTableMappingsSqlUpdate;
	protected SqlUpdate deleteRightPrimaryKeyTableMappingsSqlUpdate;
	protected SqlUpdate deleteTableMappingSqlUpdate;
	protected MappingSqlQuery<Long> getLeftPrimaryKeysSqlQuery;
	protected MappingSqlQuery<Long> getRightPrimaryKeysSqlQuery;
	protected final BasePersistence<L> leftBasePersistence;
	protected final String leftColumnName;
	protected final Class<L> leftModelClass;
	protected final PortalCache<Long, long[]> leftToRightPortalCache;
	protected TableMapper<R, L> reverseTableMapper;
	protected final BasePersistence<R> rightBasePersistence;
	protected final String rightColumnName;
	protected final Class<R> rightModelClass;
	protected final PortalCache<Long, long[]> rightToLeftPortalCache;

	private void _addTableMapping(
		long companyId, long leftPrimaryKey, long rightPrimaryKey) {

		ModelListener<L>[] leftModelListeners =
			ModelListenerRegistrationUtil.getModelListeners(leftModelClass);

		for (ModelListener<L> leftModelListener : leftModelListeners) {
			leftModelListener.onBeforeAddAssociation(
				leftPrimaryKey, rightModelClass.getName(), rightPrimaryKey);
		}

		ModelListener<R>[] rightModelListeners =
			ModelListenerRegistrationUtil.getModelListeners(rightModelClass);

		for (ModelListener<R> rightModelListener : rightModelListeners) {
			rightModelListener.onBeforeAddAssociation(
				rightPrimaryKey, leftModelClass.getName(), leftPrimaryKey);
		}

		try {
			addTableMappingSqlUpdate.update(
				companyId, leftPrimaryKey, rightPrimaryKey);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}

		for (ModelListener<L> leftModelListener : leftModelListeners) {
			leftModelListener.onAfterAddAssociation(
				leftPrimaryKey, rightModelClass.getName(), rightPrimaryKey);
		}

		for (ModelListener<R> rightModelListener : rightModelListeners) {
			rightModelListener.onAfterAddAssociation(
				rightPrimaryKey, leftModelClass.getName(), leftPrimaryKey);
		}
	}

	private boolean _deleteTableMapping(
		long leftPrimaryKey, long rightPrimaryKey) {

		ModelListener<L>[] leftModelListeners =
			ModelListenerRegistrationUtil.getModelListeners(leftModelClass);

		for (ModelListener<L> leftModelListener : leftModelListeners) {
			leftModelListener.onBeforeRemoveAssociation(
				leftPrimaryKey, rightModelClass.getName(), rightPrimaryKey);
		}

		ModelListener<R>[] rightModelListeners =
			ModelListenerRegistrationUtil.getModelListeners(rightModelClass);

		for (ModelListener<R> rightModelListener : rightModelListeners) {
			rightModelListener.onBeforeRemoveAssociation(
				rightPrimaryKey, leftModelClass.getName(), leftPrimaryKey);
		}

		int rowCount = 0;

		try {
			rowCount = deleteTableMappingSqlUpdate.update(
				leftPrimaryKey, rightPrimaryKey);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}

		if (rowCount > 0) {
			for (ModelListener<L> leftModelListener : leftModelListeners) {
				leftModelListener.onAfterRemoveAssociation(
					leftPrimaryKey, rightModelClass.getName(), rightPrimaryKey);
			}

			for (ModelListener<R> rightModelListener : rightModelListeners) {
				rightModelListener.onAfterRemoveAssociation(
					rightPrimaryKey, leftModelClass.getName(), leftPrimaryKey);
			}

			return true;
		}

		return false;
	}

	private final ServiceRegistration<?> _serviceRegistration;
	private final String _tableName;

}