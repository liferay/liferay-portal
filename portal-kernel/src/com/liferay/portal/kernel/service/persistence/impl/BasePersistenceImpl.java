/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence.impl;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.ast.ASTNode;
import com.liferay.petra.sql.dsl.expression.Alias;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.ScalarDSLQueryAlias;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.spi.ast.BaseASTNode;
import com.liferay.petra.sql.dsl.spi.ast.DefaultASTNodeListener;
import com.liferay.petra.sql.dsl.spi.expression.AggregateExpression;
import com.liferay.petra.sql.dsl.spi.expression.DSLFunction;
import com.liferay.petra.sql.dsl.spi.expression.DSLFunctionType;
import com.liferay.petra.sql.dsl.spi.expression.Scalar;
import com.liferay.petra.sql.dsl.spi.expression.TableStar;
import com.liferay.petra.sql.dsl.spi.expression.step.ElseEnd;
import com.liferay.petra.sql.dsl.spi.query.QueryTable;
import com.liferay.petra.sql.dsl.spi.query.Select;
import com.liferay.petra.sql.dsl.spi.query.SetOperation;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.orm.Dialect;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.DataLimitExceededException;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.internal.spring.transaction.ReadOnlyTransactionThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ModelListenerRegistrationUtil;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyFactory;

import java.io.Serializable;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import javax.sql.DataSource;

/**
 * The base implementation for all persistence classes. This class should never
 * need to be used directly.
 *
 * <p>
 * Caching information and settings can be found in
 * <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Peter Fellwock
 */
public class BasePersistenceImpl<T extends BaseModel<T>>
	implements BasePersistence<T>, SessionFactory {

	public static final String COUNT_COLUMN_NAME = "COUNT_VALUE";

	public void cacheResult(T model) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearCache() {
	}

	@Override
	public void clearCache(List<T> model) {
	}

	@Override
	public void clearCache(T model) {
	}

	@Override
	public void closeSession(Session session) {
		_sessionFactory.closeSession(session);
	}

	@Override
	public long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return countWithDynamicQuery(
			dynamicQuery, ProjectionFactoryUtil.rowCount());
	}

	@Override
	public long countWithDynamicQuery(
		DynamicQuery dynamicQuery, Projection projection) {

		if (projection == null) {
			projection = ProjectionFactoryUtil.rowCount();
		}

		dynamicQuery.setProjection(projection);

		List<Long> results = findWithDynamicQuery(dynamicQuery);

		if (results.isEmpty()) {
			return 0;
		}

		Long firstResult = results.get(0);

		return firstResult.longValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R> R dslQuery(DSLQuery dslQuery) {
		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		StringBundler sb = new StringBundler();

		dslQuery.toSQL(sb::append, defaultASTNodeListener);

		Select select = null;

		ASTNode astNode = dslQuery;

		while (astNode instanceof BaseASTNode) {
			if (astNode instanceof Select) {
				select = (Select)astNode;

				astNode = _unwrapQueryTable(select);

				if (astNode == null) {
					break;
				}
			}

			BaseASTNode baseASTNode = (BaseASTNode)astNode;

			if (baseASTNode instanceof SetOperation) {
				SetOperation setOperation = (SetOperation)astNode;

				astNode = setOperation.getLeftDSLQuery();
			}
			else {
				astNode = baseASTNode.getChild();
			}
		}

		if (select == null) {
			throw new IllegalArgumentException(
				"No Select found for " + dslQuery);
		}

		String[] tableNames = defaultASTNodeListener.getTableNames();

		ProjectionType projectionType = _getProjectionType(
			tableNames, select.getExpressions());

		FinderCache finderCache = getFinderCache();

		FinderPath finderPath = new FinderPath(
			FinderPath.encodeDSLQueryCacheName(tableNames), "dslQuery",
			ArrayUtil.append(
				sb.getStrings(), _getAliasTypes(select.getExpressions())),
			new String[0], projectionType == ProjectionType.MODELS);

		Object[] arguments = _getArguments(defaultASTNodeListener);

		Object cacheResult = finderCache.getResult(finderPath, arguments, this);

		if (cacheResult != null) {
			return (R)cacheResult;
		}

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				sb.toString(), true, tableNames);

			List<Object> scalarValues =
				defaultASTNodeListener.getScalarValues();

			if (!scalarValues.isEmpty()) {
				QueryPos queryPos = QueryPos.getInstance(sqlQuery);

				for (Object value : scalarValues) {
					queryPos.add(value);
				}
			}

			if (projectionType == ProjectionType.COUNT) {
				sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);
			}
			else if (projectionType == ProjectionType.MODELS) {
				sqlQuery.addEntity(_table.getTableName(), _modelImplClass);
			}
			else {
				for (Expression<?> expression : select.getExpressions()) {
					if (expression instanceof Alias) {
						Alias<?> alias = (Alias<?>)expression;

						sqlQuery.addScalar(
							alias.getName(), _getType(alias.getExpression()));
					}
					else if (expression instanceof Column) {
						Column<?, ?> column = (Column<?, ?>)expression;

						sqlQuery.addScalar(column.getName(), _getType(column));
					}
					else if (expression instanceof ScalarDSLQueryAlias) {
						ScalarDSLQueryAlias<?> scalarDSLQueryAlias =
							(ScalarDSLQueryAlias<?>)expression;

						sqlQuery.addScalar(
							scalarDSLQueryAlias.getName(),
							_types.get(scalarDSLQueryAlias.getJavaType()));
					}
					else {
						throw new IllegalArgumentException(
							"Unnamed projection expression " + expression);
					}
				}
			}

			Object result = null;

			if (projectionType == ProjectionType.COUNT) {
				List<?> results = sqlQuery.list();

				if (results.isEmpty()) {
					result = 0L;
				}
				else {
					result = results.get(0);
				}
			}
			else {
				result = QueryUtil.list(
					sqlQuery, getDialect(), defaultASTNodeListener.getStart(),
					defaultASTNodeListener.getEnd());
			}

			finderCache.putResult(finderPath, arguments, result);

			return (R)result;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public T fetchByPrimaryKey(Serializable primaryKey) {
		EntityCache entityCache = getEntityCache();

		Serializable serializable = entityCache.getResult(
			_modelImplClass, primaryKey);

		if (serializable == nullModel) {
			return null;
		}

		T model = (T)serializable;

		if (model == null) {
			Session session = null;

			try {
				session = openSession();

				model = (T)session.get(_modelImplClass, primaryKey);

				if (model == null) {
					entityCache.putResult(
						_modelImplClass, primaryKey, nullModel);
				}
				else {
					cacheResult(model);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return model;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<Serializable, T> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			T model = fetchByPrimaryKey(primaryKey);

			if (model == null) {
				return Collections.emptyMap();
			}

			return Collections.singletonMap(primaryKey, model);
		}

		Map<Serializable, T> map = new HashMap<>();

		if (_modelPKType == ModelPKType.COMPOUND) {
			for (Serializable primaryKey : primaryKeys) {
				T model = fetchByPrimaryKey(primaryKey);

				if (model != null) {
					map.put(primaryKey, model);
				}
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		EntityCache entityCache = getEntityCache();

		for (Serializable primaryKey : primaryKeys) {
			Serializable serializable = entityCache.getResult(
				_modelImplClass, primaryKey);

			if (serializable != nullModel) {
				if (serializable == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, (T)serializable);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		if ((databaseInMaxParameters > 0) &&
			(uncachedPrimaryKeys.size() > databaseInMaxParameters)) {

			Iterator<Serializable> iterator = uncachedPrimaryKeys.iterator();

			while (iterator.hasNext()) {
				Set<Serializable> page = new HashSet<>();

				for (int i = 0;
					 (i < databaseInMaxParameters) && iterator.hasNext(); i++) {

					page.add(iterator.next());
				}

				map.putAll(fetchByPrimaryKeys(page));
			}

			return map;
		}

		StringBundler sb = new StringBundler(
			(2 * uncachedPrimaryKeys.size()) + 4);

		sb.append(getSelectSQL());
		sb.append(" WHERE ");
		sb.append(getPKDBName());
		sb.append(" IN (");

		if (_modelPKType == ModelPKType.STRING) {
			for (int i = 0; i < uncachedPrimaryKeys.size(); i++) {
				sb.append("?");

				sb.append(",");
			}
		}
		else {
			for (Serializable primaryKey : uncachedPrimaryKeys) {
				sb.append((long)primaryKey);

				sb.append(",");
			}
		}

		sb.setIndex(sb.index() - 1);

		sb.append(")");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			if (_modelPKType == ModelPKType.STRING) {
				QueryPos queryPos = QueryPos.getInstance(query);

				for (Serializable primaryKey : uncachedPrimaryKeys) {
					queryPos.add(primaryKey);
				}
			}

			for (T model : (List<T>)query.list()) {
				map.put(model.getPrimaryKeyObj(), model);

				cacheResult(model);

				uncachedPrimaryKeys.remove(model.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(_modelImplClass, primaryKey, nullModel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return map;
	}

	@Override
	public T findByPrimaryKey(Serializable primaryKey)
		throws NoSuchModelException {

		throw new UnsupportedOperationException();
	}

	@Override
	public <V> List<V> findWithDynamicQuery(DynamicQuery dynamicQuery) {
		Session session = null;

		try {
			session = openSession();

			dynamicQuery.compile(session);

			return dynamicQuery.list();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public <V> List<V> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			dynamicQuery.setLimit(start, end);

			dynamicQuery.compile(session);

			return dynamicQuery.list();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public <V> List<V> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<V> orderByComparator) {

		OrderFactoryUtil.addOrderByComparator(dynamicQuery, orderByComparator);

		return findWithDynamicQuery(dynamicQuery, start, end);
	}

	@Override
	public void flush() {
		try {
			Session session = _sessionFactory.getCurrentSession();

			if (session != null) {
				session.flush();
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	@Override
	public Set<String> getBadColumnNames() {
		return Collections.emptySet();
	}

	public Set<String> getCompoundPKColumnNames() {
		return Collections.emptySet();
	}

	@Override
	public Session getCurrentSession() throws ORMException {
		return _sessionFactory.getCurrentSession();
	}

	@Override
	public DataSource getDataSource() {
		return _dataSource;
	}

	@Override
	public DB getDB() {
		if (_db == null) {
			_db = DBManagerUtil.getDB(getDialect(), _dataSource);
		}

		return _db;
	}

	@Override
	public Dialect getDialect() {
		return _sessionFactory.getDialect();
	}

	@Override
	public ModelListener<T>[] getListeners() {
		return ModelListenerRegistrationUtil.getModelListeners(getModelClass());
	}

	@Override
	public Class<T> getModelClass() {
		return _modelClass;
	}

	@Override
	public Session openNewSession(Connection connection) throws ORMException {
		return _sessionFactory.openNewSession(connection);
	}

	@Override
	public Session openSession() throws ORMException {
		return _sessionFactory.openSession();
	}

	@Override
	public SystemException processException(Exception exception) {
		if (!(exception instanceof ORMException)) {
			_log.error("Caught unexpected exception", exception);
		}
		else if (_log.isDebugEnabled()) {
			_log.debug(exception);
		}

		return new SystemException(exception);
	}

	@Override
	public void registerListener(ModelListener<T> modelListener) {
		ModelListenerRegistrationUtil.register(modelListener);
	}

	@Override
	public T remove(Serializable primaryKey) throws NoSuchModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public T remove(T model) {
		return removeByFunction(model, this::removeImpl);
	}

	@Override
	public T removeByFunction(T model, Function<T, T> function) {
		if (ReadOnlyTransactionThreadLocal.isReadOnly()) {
			throw new IllegalStateException(
				"Remove called with read only transaction");
		}

		while (model instanceof ModelWrapper) {
			ModelWrapper<T> modelWrapper = (ModelWrapper<T>)model;

			model = modelWrapper.getWrappedModel();
		}

		ModelListener<T>[] modelListeners = getListeners();

		for (ModelListener<T> modelListener : modelListeners) {
			modelListener.onBeforeRemove(model);
		}

		T removedModel = function.apply(model);

		if (removedModel != null) {
			model = removedModel;
		}

		for (ModelListener<T> modelListener : modelListeners) {
			modelListener.onAfterRemove(model);
		}

		return model;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public void setConfiguration(Configuration configuration) {
		String modelClassName = _modelClass.getName();

		entityCacheEnabled = GetterUtil.getBoolean(
			configuration.get(
				"value.object.entity.cache.enabled.".concat(modelClassName)),
			entityCacheEnabled);
		finderCacheEnabled = GetterUtil.getBoolean(
			configuration.get(
				"value.object.finder.cache.enabled.".concat(modelClassName)),
			finderCacheEnabled);
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		_dataSource = dataSource;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		_sessionFactory = sessionFactory;

		DBType dbType = DBManagerUtil.getDBType(_dataSource);

		_databaseOrderByMaxColumns = GetterUtil.getInteger(
			PropsUtil.get(
				PropsKeys.DATABASE_ORDER_BY_MAX_COLUMNS,
				new Filter(dbType.getName())));

		databaseInMaxParameters = GetterUtil.getInteger(
			PropsUtil.get(
				PropsKeys.DATABASE_IN_MAX_PARAMETERS,
				new Filter(dbType.getName())));
	}

	@Override
	public void unregisterListener(ModelListener<T> modelListener) {
		ModelListenerRegistrationUtil.unregister(modelListener);
	}

	@Override
	public T update(T model) {
		if (ReadOnlyTransactionThreadLocal.isReadOnly()) {
			throw new IllegalStateException(
				"Update called with read only transaction");
		}

		Class<?> clazz = model.getModelClass();

		while (model instanceof ModelWrapper) {
			ModelWrapper<T> modelWrapper = (ModelWrapper<T>)model;

			model = modelWrapper.getWrappedModel();
		}

		boolean isNew = model.isNew();

		if (isNew && (_dataLimitModelMaxCount > 0)) {
			AuditedModel auditedModel = (AuditedModel)model;

			FromStep fromStep = DSLQueryFactoryUtil.count();

			JoinStep joinStep = fromStep.from(_table);

			GroupByStep groupByStep = joinStep.where(
				_table.getColumn(
					"companyId", Long.class
				).eq(
					auditedModel.getCompanyId()
				));

			int modelCount = dslQueryCount(groupByStep);

			if (modelCount >= _dataLimitModelMaxCount) {
				throw new DataLimitExceededException(
					"Unable to exceed maximum number of allowed " +
						clazz.getName());
			}
		}

		T oldModel = null;

		if (!isNew) {
			oldModel = model.cloneWithOriginalValues();
		}

		ModelListener<T>[] modelListeners = getListeners();

		for (ModelListener<T> modelListener : modelListeners) {
			if (isNew) {
				modelListener.onBeforeCreate(model);
			}
			else {
				modelListener.onBeforeUpdate(oldModel, model);
			}
		}

		model = updateImpl(model);

		for (ModelListener<T> modelListener : modelListeners) {
			if (isNew) {
				modelListener.onAfterCreate(model);
			}
			else {
				modelListener.onAfterUpdate(oldModel, model);
			}
		}

		return model;
	}

	@Override
	public T update(T model, ServiceContext serviceContext) {
		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			return update(model);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	protected static String removeConjunction(String sql) {
		int pos = sql.indexOf(" AND ");

		if (pos != -1) {
			sql = sql.substring(0, pos);
		}

		return sql;
	}

	protected void appendOrderByComparator(
		StringBundler sb, String entityAlias,
		OrderByComparator<T> orderByComparator) {

		appendOrderByComparator(sb, entityAlias, orderByComparator, false);
	}

	protected void appendOrderByComparator(
		StringBundler sb, String entityAlias,
		OrderByComparator<T> orderByComparator, boolean sqlQuery) {

		sb.append(ORDER_BY_CLAUSE);

		String[] orderByFields = orderByComparator.getOrderByFields();

		int length = orderByFields.length;

		if ((_databaseOrderByMaxColumns > 0) &&
			(_databaseOrderByMaxColumns < length)) {

			length = _databaseOrderByMaxColumns;
		}

		for (int i = 0; i < length; i++) {
			sb.append(getColumnName(entityAlias, orderByFields[i], sqlQuery));

			if ((i + 1) < length) {
				if (orderByComparator.isAscending(orderByFields[i])) {
					sb.append(ORDER_BY_ASC_HAS_NEXT);
				}
				else {
					sb.append(ORDER_BY_DESC_HAS_NEXT);
				}
			}
			else {
				if (orderByComparator.isAscending(orderByFields[i])) {
					sb.append(ORDER_BY_ASC);
				}
				else {
					sb.append(ORDER_BY_DESC);
				}
			}
		}
	}

	protected ClassLoader getClassLoader() {
		Class<?> clazz = getClass();

		return clazz.getClassLoader();
	}

	protected String getColumnName(
		String entityAlias, String fieldName, boolean sqlQuery) {

		String columnName = _dbColumnNames.getOrDefault(fieldName, fieldName);

		if (sqlQuery) {
			fieldName = columnName;
		}
		else {
			Set<String> compoundPKColumnNames = getCompoundPKColumnNames();

			if (compoundPKColumnNames.contains(fieldName)) {
				fieldName = "id.".concat(fieldName);
			}
		}

		fieldName = entityAlias.concat(fieldName);

		Map<String, Integer> tableColumnsMap = getTableColumnsMap();

		Integer type = tableColumnsMap.get(columnName);

		if (type == null) {
			throw new IllegalArgumentException(
				"Unknown column name " + columnName);
		}

		if (type == Types.CLOB) {
			fieldName = StringBundler.concat(
				CAST_CLOB_TEXT_OPEN, fieldName, StringPool.CLOSE_PARENTHESIS);
		}

		return fieldName;
	}

	protected EntityCache getEntityCache() {
		throw new UnsupportedOperationException();
	}

	protected FinderCache getFinderCache() {
		throw new UnsupportedOperationException();
	}

	protected String getPKDBName() {
		throw new UnsupportedOperationException();
	}

	protected String getSelectSQL() {
		throw new UnsupportedOperationException();
	}

	protected Map<String, Integer> getTableColumnsMap() {
		throw new UnsupportedOperationException();
	}

	protected boolean isPermissionsInMemoryFilterEnabled() {
		if (_permissionsInMemoryFilterEnabled == null) {
			Class<?> modelClass = getModelClass();

			_permissionsInMemoryFilterEnabled = GetterUtil.getBoolean(
				PropsUtil.get(
					"permissions.in.memory.filter.enabled",
					new Filter(modelClass.getName())),
				_PERMISSIONS_IN_MEMORY_FILTER_ENABLED);
		}

		return _permissionsInMemoryFilterEnabled;
	}

	/**
	 * Removes the model instance from the database. {@link #update(BaseModel,
	 * boolean)} depends on this method to implement the remove operation; it
	 * only notifies the model listeners.
	 *
	 * @param  model the model instance to remove
	 * @return the model instance that was removed
	 */
	protected T removeImpl(T model) {
		throw new UnsupportedOperationException();
	}

	protected void setDBColumnNames(Map<String, String> dbColumnNames) {
		_dbColumnNames = dbColumnNames;
	}

	protected void setEntityCacheEnabled(boolean entityCacheEnabled) {
		this.entityCacheEnabled = entityCacheEnabled;
	}

	protected void setModelClass(Class<T> modelClass) {
		_modelClass = modelClass;

		long dataLimitModelMaxCount = GetterUtil.getLong(
			PropsUtil.get(
				"data.limit.model.max.count",
				new Filter(modelClass.getName())));

		if (AuditedModel.class.isAssignableFrom(modelClass) &&
			(dataLimitModelMaxCount > 0)) {

			_dataLimitModelMaxCount = dataLimitModelMaxCount;
		}
	}

	protected void setModelImplClass(Class<? extends T> modelImplClass) {
		_modelImplClass = modelImplClass;
	}

	protected void setModelPKClass(Class<? extends Serializable> clazz) {
		if (clazz.isPrimitive()) {
			_modelPKType = ModelPKType.NUMBER;
		}
		else if (String.class.isAssignableFrom(clazz)) {
			_modelPKType = ModelPKType.STRING;
		}
	}

	protected void setTable(Table<?> table) {
		_table = table;
	}

	/**
	 * Updates the model instance in the database or adds it if it does not yet
	 * exist. {@link #remove(BaseModel)} depends on this method to implement the
	 * update operation; it only notifies the model listeners.
	 *
	 * @param  model the model instance to update
	 * @return the model instance that was updated
	 */
	protected T updateImpl(T model) {
		throw new UnsupportedOperationException();
	}

	protected static final String CAST_CLOB_TEXT_OPEN = "CAST_CLOB_TEXT(";

	protected static final Object[] FINDER_ARGS_EMPTY = new Object[0];

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	protected static final Comparator<String> NULL_SAFE_STRING_COMPARATOR =
		Comparator.nullsLast(Comparator.naturalOrder());

	protected static final String ORDER_BY_ASC = " ASC";

	protected static final String ORDER_BY_ASC_HAS_NEXT = " ASC, ";

	protected static final String ORDER_BY_CLAUSE = " ORDER BY ";

	protected static final String ORDER_BY_DESC = " DESC";

	protected static final String ORDER_BY_DESC_HAS_NEXT = " DESC, ";

	protected static final String WHERE_AND = " AND ";

	protected static final String WHERE_GREATER_THAN = " >= ? ";

	protected static final String WHERE_GREATER_THAN_HAS_NEXT = " >= ? AND ";

	protected static final String WHERE_LESSER_THAN = " <= ? ";

	protected static final String WHERE_LESSER_THAN_HAS_NEXT = " <= ? AND ";

	protected static final String WHERE_OR = " OR ";

	protected static EntityCache dummyEntityCache =
		ProxyFactory.newDummyInstance(EntityCache.class);
	protected static FinderCache dummyFinderCache =
		ProxyFactory.newDummyInstance(FinderCache.class);
	protected static final NullModel nullModel = new NullModel();

	protected int databaseInMaxParameters;
	protected Map<String, String> dbColumnNames;

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	protected boolean entityCacheEnabled = true;

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	protected boolean finderCacheEnabled = true;

	private String[] _getAliasTypes(
		Collection<? extends Expression<?>> expressions) {

		List<String> aliasTypes = new ArrayList<>();

		for (Expression<?> expression : expressions) {
			Type type = null;

			if (expression instanceof Alias) {
				Alias<?> alias = (Alias<?>)expression;

				type = _getType(alias.getExpression());
			}

			if (type != null) {
				aliasTypes.add(String.valueOf(type));
			}
		}

		return aliasTypes.toArray(new String[0]);
	}

	private Object[] _getArguments(
		DefaultASTNodeListener defaultASTNodeListener) {

		List<Object> arguments = new ArrayList<>();

		for (Object object : defaultASTNodeListener.getScalarValues()) {
			if (object instanceof Date) {
				Date date = (Date)object;

				arguments.add(date.getTime());
			}
			else {
				arguments.add(object);
			}
		}

		int start = defaultASTNodeListener.getStart();
		int end = defaultASTNodeListener.getEnd();

		if ((start != QueryUtil.ALL_POS) || (end != QueryUtil.ALL_POS)) {
			arguments.add(start);
			arguments.add(end);
		}

		return arguments.toArray(new Object[0]);
	}

	private ProjectionType _getProjectionType(
		String[] tableNames, Collection<? extends Expression<?>> expressions) {

		if (expressions.isEmpty() && (tableNames.length == 1)) {
			if (Objects.equals(tableNames[0], _table.getTableName())) {
				return ProjectionType.MODELS;
			}
		}
		else if (expressions.size() == 1) {
			Iterator<? extends Expression<?>> iterator = expressions.iterator();

			Expression<?> expression = iterator.next();

			if (expression instanceof TableStar) {
				TableStar tableStar = (TableStar)expression;

				if (Objects.equals(_table, tableStar.getTable())) {
					return ProjectionType.MODELS;
				}
			}
			else if (expression instanceof Alias<?>) {
				Alias<?> alias = (Alias<?>)expression;

				if (COUNT_COLUMN_NAME.equals(alias.getName())) {
					return ProjectionType.COUNT;
				}
			}
		}

		return ProjectionType.COLUMNS;
	}

	private Type _getType(Expression<?> expression) {
		if (expression instanceof Column) {
			Column<?, ?> column = (Column<?, ?>)expression;

			Class<?> javaTypeClass = column.getJavaType();

			Type type = _types.get(javaTypeClass);

			if (type != null) {
				return type;
			}
		}

		if (expression instanceof Alias) {
			Alias<?> alias = (Alias<?>)expression;

			return _getType(alias.getExpression());
		}

		if (expression instanceof AggregateExpression) {
			AggregateExpression<?> aggregateExpression =
				(AggregateExpression<?>)expression;

			if (Objects.equals(aggregateExpression.getName(), "count")) {
				return Type.LONG;
			}

			return _getType(aggregateExpression.getExpression());
		}

		if (expression instanceof DSLFunction) {
			DSLFunction<?> dslFunction = (DSLFunction<?>)expression;

			DSLFunctionType dslFunctionType = dslFunction.getDslFunctionType();

			if ((dslFunctionType == DSLFunctionType.CAST_CLOB_TEXT) ||
				(dslFunctionType == DSLFunctionType.CAST_TEXT) ||
				(dslFunctionType == DSLFunctionType.CONCAT) ||
				(dslFunctionType == DSLFunctionType.LOWER)) {

				return Type.STRING;
			}

			if ((dslFunctionType == DSLFunctionType.BITWISE_AND) ||
				(dslFunctionType == DSLFunctionType.CAST_LONG)) {

				return Type.LONG;
			}

			if (dslFunctionType == DSLFunctionType.FLOAT_DIVISION) {
				return Type.FLOAT;
			}

			return _getType(dslFunction.getExpressions()[0]);
		}

		if (expression instanceof ElseEnd<?>) {
			ElseEnd<?> elseEnd = (ElseEnd<?>)expression;

			return _getType(elseEnd.getElseExpression());
		}

		if (expression instanceof Scalar<?>) {
			Scalar<?> scalar = (Scalar<?>)expression;

			Object value = scalar.getValue();

			return _types.get(value.getClass());
		}

		throw new IllegalArgumentException(expression.toString());
	}

	private ASTNode _unwrapQueryTable(Select select) {
		Collection<? extends Expression<?>> expressions =
			select.getExpressions();

		if (expressions.size() != 1) {
			return null;
		}

		Iterator<? extends Expression<?>> iterator = expressions.iterator();

		Expression<?> expression = iterator.next();

		if (!(expression instanceof TableStar)) {
			return null;
		}

		TableStar tableStar = (TableStar)expression;

		Table table = tableStar.getTable();

		if (!(table instanceof QueryTable)) {
			return null;
		}

		QueryTable queryTable = (QueryTable)table;

		return queryTable.getDslQuery();
	}

	private static final boolean _PERMISSIONS_IN_MEMORY_FILTER_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get("permissions.in.memory.filter.enabled"), true);

	private static final Log _log = LogFactoryUtil.getLog(
		BasePersistenceImpl.class);

	private static final Map<Class<?>, Type> _types =
		HashMapBuilder.<Class<?>, Type>put(
			BigDecimal.class, Type.BIG_DECIMAL
		).put(
			Boolean.class, Type.BOOLEAN
		).put(
			Date.class, Type.DATE
		).put(
			Double.class, Type.DOUBLE
		).put(
			Float.class, Type.FLOAT
		).put(
			Integer.class, Type.INTEGER
		).put(
			Long.class, Type.LONG
		).put(
			Short.class, Type.SHORT
		).put(
			String.class, Type.STRING
		).put(
			Timestamp.class, Type.TIMESTAMP
		).build();

	private int _databaseOrderByMaxColumns;
	private long _dataLimitModelMaxCount;
	private DataSource _dataSource;
	private DB _db;
	private Map<String, String> _dbColumnNames = Collections.emptyMap();
	private Class<T> _modelClass;
	private Class<? extends T> _modelImplClass;
	private ModelPKType _modelPKType = ModelPKType.COMPOUND;
	private Boolean _permissionsInMemoryFilterEnabled;
	private SessionFactory _sessionFactory;
	private Table<?> _table;

	private static class NullModel
		implements BaseModel<NullModel>, CacheModel<NullModel>, MVCCModel {

		@Override
		public Object clone() {
			return this;
		}

		@Override
		public NullModel cloneWithOriginalValues() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int compareTo(NullModel nullModel) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ExpandoBridge getExpandoBridge() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Map<String, Object> getModelAttributes() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<?> getModelClass() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getModelClassName() {
			throw new UnsupportedOperationException();
		}

		@Override
		public long getMvccVersion() {
			return -1;
		}

		@Override
		public Serializable getPrimaryKeyObj() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isCachedModel() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @deprecated As of Athanasius (7.3.x), with no direct replacement
		 */
		@Deprecated
		@Override
		public boolean isEntityCacheEnabled() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isEscapedModel() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @deprecated As of Athanasius (7.3.x), with no direct replacement
		 */
		@Deprecated
		@Override
		public boolean isFinderCacheEnabled() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isNew() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void resetOriginalValues() {
		}

		@Override
		public void setCachedModel(boolean cachedModel) {
		}

		@Override
		public void setExpandoBridgeAttributes(BaseModel<?> baseModel) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setModelAttributes(Map<String, Object> attributes) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setMvccVersion(long mvccVersion) {
		}

		@Override
		public void setNew(boolean n) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setPrimaryKeyObj(Serializable primaryKeyObj) {
			throw new UnsupportedOperationException();
		}

		@Override
		public CacheModel<NullModel> toCacheModel() {
			return nullModel;
		}

		@Override
		public NullModel toEntityModel() {
			return nullModel;
		}

		@Override
		public NullModel toEscapedModel() {
			throw new UnsupportedOperationException();
		}

		@Override
		public NullModel toUnescapedModel() {
			throw new UnsupportedOperationException();
		}

	}

	private enum ModelPKType {

		COMPOUND, NUMBER, STRING

	}

	private enum ProjectionType {

		COLUMNS, COUNT, MODELS

	}

}