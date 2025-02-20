/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class DefaultActionableDynamicQuery implements ActionableDynamicQuery {

	public static final TransactionConfig REQUIRES_NEW_TRANSACTION_CONFIG;

	static {
		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setPropagation(Propagation.REQUIRES_NEW);
		builder.setRollbackForClasses(
			PortalException.class, SystemException.class);

		REQUIRES_NEW_TRANSACTION_CONFIG = builder.build();
	}

	@Override
	public AddCriteriaMethod getAddCriteriaMethod() {
		return _addCriteriaMethod;
	}

	@Override
	public AddOrderCriteriaMethod getAddOrderCriteriaMethod() {
		return _addOrderCriteriaMethod;
	}

	@Override
	public PerformActionMethod<?> getPerformActionMethod() {
		return _performActionMethod;
	}

	@Override
	public PerformCountMethod getPerformCountMethod() {
		return _performCountMethod;
	}

	@Override
	public boolean isParallel() {
		return _parallel;
	}

	@Override
	public void performActions() throws PortalException {
		try {
			long previousPrimaryKey = -1;

			while (true) {
				long lastPrimaryKey = doPerformActions(previousPrimaryKey);

				if (lastPrimaryKey < 0) {
					return;
				}

				intervalCompleted(previousPrimaryKey, lastPrimaryKey);

				previousPrimaryKey = lastPrimaryKey;
			}
		}
		finally {
			_offset = 0;

			actionsCompleted();
		}
	}

	@Override
	public long performCount() throws PortalException {
		if (_performCountMethod != null) {
			return _performCountMethod.performCount();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			_modelClass, _classLoader);

		addDefaultCriteria(dynamicQuery);

		addCriteria(dynamicQuery);

		return (Long)executeDynamicQuery(
			_dynamicQueryCountMethod, dynamicQuery, getCountProjection());
	}

	@Override
	public void setAddCriteriaMethod(AddCriteriaMethod addCriteriaMethod) {
		_addCriteriaMethod = addCriteriaMethod;
	}

	@Override
	public void setAddOrderCriteriaMethod(
		AddOrderCriteriaMethod addOrderCriteriaMethod) {

		_addOrderCriteriaMethod = addOrderCriteriaMethod;
	}

	@Override
	public void setBaseLocalService(BaseLocalService baseLocalService) {
		_baseLocalService = baseLocalService;

		Class<?> clazz = _baseLocalService.getClass();

		try {
			_dynamicQueryMethod = clazz.getMethod(
				"dynamicQuery", DynamicQuery.class);
			_dynamicQueryCountMethod = clazz.getMethod(
				"dynamicQueryCount", DynamicQuery.class, Projection.class);
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new SystemException(noSuchMethodException);
		}
	}

	@Override
	public void setClassLoader(ClassLoader classLoader) {
		_classLoader = classLoader;
	}

	@Override
	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	@Override
	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	@Override
	public void setGroupIdPropertyName(String groupIdPropertyName) {
		_groupIdPropertyName = groupIdPropertyName;
	}

	@Override
	public void setInterval(int interval) {
		_interval = interval;
	}

	@Override
	public void setModelClass(Class<?> modelClass) {
		_modelClass = modelClass;
	}

	@Override
	public void setParallel(boolean parallel) {
		_parallel = parallel;
	}

	@Override
	public void setPerformActionMethod(
		PerformActionMethod<?> performActionMethod) {

		_performActionMethod = performActionMethod;
	}

	@Override
	public void setPerformCountMethod(PerformCountMethod performCountMethod) {
		_performCountMethod = performCountMethod;
	}

	@Override
	public void setPrimaryKeyPropertyName(String primaryKeyPropertyName) {
		_primaryKeyPropertyName = primaryKeyPropertyName;
	}

	@Override
	public void setTransactionConfig(TransactionConfig transactionConfig) {
		_transactionConfig = transactionConfig;
	}

	/**
	 * @throws PortalException
	 */
	protected void actionsCompleted() throws PortalException {
	}

	protected void addCriteria(DynamicQuery dynamicQuery) {
		if (_addCriteriaMethod != null) {
			_addCriteriaMethod.addCriteria(dynamicQuery);
		}
	}

	protected void addDefaultCriteria(DynamicQuery dynamicQuery) {
		if (_companyId > 0) {
			Property property = PropertyFactoryUtil.forName("companyId");

			dynamicQuery.add(property.eq(_companyId));
		}

		if (_groupId > 0) {
			Property property = PropertyFactoryUtil.forName(
				_groupIdPropertyName);

			dynamicQuery.add(property.eq(_groupId));
		}
	}

	protected void addOrderCriteria(DynamicQuery dynamicQuery) {
		if (_addOrderCriteriaMethod == null) {
			dynamicQuery.addOrder(
				OrderFactoryUtil.asc(_primaryKeyPropertyName));
		}
		else {
			_addOrderCriteriaMethod.addOrderCriteria(dynamicQuery);
		}
	}

	protected long doPerformActions(long previousPrimaryKey)
		throws PortalException {

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			_modelClass, _classLoader);

		if (_addOrderCriteriaMethod == null) {
			Property property = PropertyFactoryUtil.forName(
				_primaryKeyPropertyName);

			dynamicQuery.add(property.gt(previousPrimaryKey));

			dynamicQuery.setLimit(0, _interval);
		}
		else {
			dynamicQuery.setLimit(_offset, _interval + _offset);
		}

		addDefaultCriteria(dynamicQuery);

		addCriteria(dynamicQuery);

		addOrderCriteria(dynamicQuery);

		Callable<Long> callable = () -> {
			List<Object> objects = (List<Object>)executeDynamicQuery(
				_dynamicQueryMethod, dynamicQuery);

			_offset += objects.size();

			if (objects.isEmpty()) {
				return -1L;
			}

			PortalExecutorManager portalExecutorManager =
				_portalExecutorManagerSnapshot.get();

			ExecutorService executorService =
				portalExecutorManager.getPortalExecutor(
					DefaultActionableDynamicQuery.class.getName());

			if (_parallel && (executorService != null)) {
				List<Future<Void>> futures = new ArrayList<>(objects.size());

				for (Object object : objects) {
					futures.add(
						executorService.submit(
							new CompanyInheritableThreadLocalCallable<>(
								() -> {
									performAction(object);

									return null;
								})));
				}

				for (Future<Void> future : futures) {
					future.get();
				}
			}
			else {
				for (Object object : objects) {
					performAction(object);
				}
			}

			if (objects.size() < _interval) {
				return -1L;
			}

			BaseModel<?> baseModel = (BaseModel<?>)objects.get(
				objects.size() - 1);

			return (Long)baseModel.getPrimaryKeyObj();
		};

		TransactionConfig transactionConfig = getTransactionConfig();

		try {
			if (transactionConfig == null) {
				return callable.call();
			}

			return TransactionInvokerUtil.invoke(transactionConfig, callable);
		}
		catch (Throwable throwable) {
			if (throwable instanceof PortalException) {
				throw (PortalException)throwable;
			}

			if (throwable instanceof SystemException) {
				throw (SystemException)throwable;
			}

			throw new SystemException(throwable);
		}
	}

	protected Object executeDynamicQuery(
			Method dynamicQueryMethod, Object... arguments)
		throws PortalException {

		try {
			return dynamicQueryMethod.invoke(_baseLocalService, arguments);
		}
		catch (InvocationTargetException invocationTargetException) {
			Throwable throwable = invocationTargetException.getCause();

			if (throwable instanceof PortalException) {
				throw (PortalException)throwable;
			}
			else if (throwable instanceof SystemException) {
				throw (SystemException)throwable;
			}

			throw new SystemException(invocationTargetException);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	protected long getCompanyId() {
		return _companyId;
	}

	protected Projection getCountProjection() {
		return ProjectionFactoryUtil.rowCount();
	}

	protected int getInterval() {
		return _interval;
	}

	protected Class<?> getModelClass() {
		return _modelClass;
	}

	protected TransactionConfig getTransactionConfig() {
		return _transactionConfig;
	}

	protected void intervalCompleted(long startPrimaryKey, long endPrimaryKey)
		throws PortalException {
	}

	protected void performAction(Object object) throws PortalException {
		if (_performActionMethod != null) {
			_performActionMethod.performAction(object);
		}
	}

	private static final Snapshot<PortalExecutorManager>
		_portalExecutorManagerSnapshot = new Snapshot<>(
			DefaultActionableDynamicQuery.class, PortalExecutorManager.class);

	private AddCriteriaMethod _addCriteriaMethod;
	private AddOrderCriteriaMethod _addOrderCriteriaMethod;
	private BaseLocalService _baseLocalService;
	private ClassLoader _classLoader;
	private long _companyId;
	private Method _dynamicQueryCountMethod;
	private Method _dynamicQueryMethod;
	private long _groupId;
	private String _groupIdPropertyName = "groupId";
	private int _interval = Indexer.DEFAULT_INTERVAL;
	private Class<?> _modelClass;
	private int _offset;
	private boolean _parallel;

	@SuppressWarnings("rawtypes")
	private PerformActionMethod _performActionMethod;

	private PerformCountMethod _performCountMethod;
	private String _primaryKeyPropertyName;
	private TransactionConfig _transactionConfig;

}