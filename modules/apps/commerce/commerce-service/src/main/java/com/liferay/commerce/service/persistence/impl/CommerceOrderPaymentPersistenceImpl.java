/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.exception.NoSuchOrderPaymentException;
import com.liferay.commerce.model.CommerceOrderPayment;
import com.liferay.commerce.model.CommerceOrderPaymentTable;
import com.liferay.commerce.model.impl.CommerceOrderPaymentImpl;
import com.liferay.commerce.model.impl.CommerceOrderPaymentModelImpl;
import com.liferay.commerce.service.persistence.CommerceOrderPaymentPersistence;
import com.liferay.commerce.service.persistence.CommerceOrderPaymentUtil;
import com.liferay.commerce.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the commerce order payment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceOrderPaymentPersistence.class)
public class CommerceOrderPaymentPersistenceImpl
	extends BasePersistenceImpl
		<CommerceOrderPayment, NoSuchOrderPaymentException>
	implements CommerceOrderPaymentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceOrderPaymentUtil</code> to access the commerce order payment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceOrderPaymentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceOrderPayment, NoSuchOrderPaymentException>
			_collectionPersistenceFinderByCommerceOrderId;

	/**
	 * Returns an ordered range of all the commerce order payments where commerceOrderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderPaymentModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param start the lower bound of the range of commerce order payments
	 * @param end the upper bound of the range of commerce order payments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order payments
	 */
	@Override
	public List<CommerceOrderPayment> findByCommerceOrderId(
		long commerceOrderId, int start, int end,
		OrderByComparator<CommerceOrderPayment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceOrderId.find(
			finderCache, new Object[] {commerceOrderId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order payment in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order payment
	 * @throws NoSuchOrderPaymentException if a matching commerce order payment could not be found
	 */
	@Override
	public CommerceOrderPayment findByCommerceOrderId_First(
			long commerceOrderId,
			OrderByComparator<CommerceOrderPayment> orderByComparator)
		throws NoSuchOrderPaymentException {

		return _collectionPersistenceFinderByCommerceOrderId.findFirst(
			finderCache, new Object[] {commerceOrderId}, orderByComparator);
	}

	/**
	 * Returns the first commerce order payment in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order payment, or <code>null</code> if a matching commerce order payment could not be found
	 */
	@Override
	public CommerceOrderPayment fetchByCommerceOrderId_First(
		long commerceOrderId,
		OrderByComparator<CommerceOrderPayment> orderByComparator) {

		return _collectionPersistenceFinderByCommerceOrderId.fetchFirst(
			finderCache, new Object[] {commerceOrderId}, orderByComparator);
	}

	/**
	 * Removes all the commerce order payments where commerceOrderId = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 */
	@Override
	public void removeByCommerceOrderId(long commerceOrderId) {
		_collectionPersistenceFinderByCommerceOrderId.remove(
			finderCache, new Object[] {commerceOrderId});
	}

	/**
	 * Returns the number of commerce order payments where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @return the number of matching commerce order payments
	 */
	@Override
	public int countByCommerceOrderId(long commerceOrderId) {
		return _collectionPersistenceFinderByCommerceOrderId.count(
			finderCache, new Object[] {commerceOrderId});
	}

	public CommerceOrderPaymentPersistenceImpl() {
		setModelClass(CommerceOrderPayment.class);

		setModelImplClass(CommerceOrderPaymentImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceOrderPaymentTable.INSTANCE);
	}

	/**
	 * Creates a new commerce order payment with the primary key. Does not add the commerce order payment to the database.
	 *
	 * @param commerceOrderPaymentId the primary key for the new commerce order payment
	 * @return the new commerce order payment
	 */
	@Override
	public CommerceOrderPayment create(long commerceOrderPaymentId) {
		CommerceOrderPayment commerceOrderPayment =
			new CommerceOrderPaymentImpl();

		commerceOrderPayment.setNew(true);
		commerceOrderPayment.setPrimaryKey(commerceOrderPaymentId);

		commerceOrderPayment.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceOrderPayment;
	}

	/**
	 * Removes the commerce order payment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceOrderPaymentId the primary key of the commerce order payment
	 * @return the commerce order payment that was removed
	 * @throws NoSuchOrderPaymentException if a commerce order payment with the primary key could not be found
	 */
	@Override
	public CommerceOrderPayment remove(long commerceOrderPaymentId)
		throws NoSuchOrderPaymentException {

		return remove((Serializable)commerceOrderPaymentId);
	}

	@Override
	protected CommerceOrderPayment removeImpl(
		CommerceOrderPayment commerceOrderPayment) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceOrderPayment)) {
				commerceOrderPayment = (CommerceOrderPayment)session.get(
					CommerceOrderPaymentImpl.class,
					commerceOrderPayment.getPrimaryKeyObj());
			}

			if (commerceOrderPayment != null) {
				session.delete(commerceOrderPayment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceOrderPayment != null) {
			clearCache(commerceOrderPayment);
		}

		return commerceOrderPayment;
	}

	@Override
	public CommerceOrderPayment updateImpl(
		CommerceOrderPayment commerceOrderPayment) {

		boolean isNew = commerceOrderPayment.isNew();

		if (!(commerceOrderPayment instanceof CommerceOrderPaymentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceOrderPayment.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceOrderPayment);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceOrderPayment proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceOrderPayment implementation " +
					commerceOrderPayment.getClass());
		}

		CommerceOrderPaymentModelImpl commerceOrderPaymentModelImpl =
			(CommerceOrderPaymentModelImpl)commerceOrderPayment;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceOrderPayment.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceOrderPayment.setCreateDate(date);
			}
			else {
				commerceOrderPayment.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceOrderPaymentModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceOrderPayment.setModifiedDate(date);
			}
			else {
				commerceOrderPayment.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceOrderPayment);
			}
			else {
				commerceOrderPayment = (CommerceOrderPayment)session.merge(
					commerceOrderPayment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceOrderPayment, false);

		if (isNew) {
			commerceOrderPayment.setNew(false);
		}

		commerceOrderPayment.resetOriginalValues();

		return commerceOrderPayment;
	}

	/**
	 * Returns the commerce order payment with the primary key or throws a <code>NoSuchOrderPaymentException</code> if it could not be found.
	 *
	 * @param commerceOrderPaymentId the primary key of the commerce order payment
	 * @return the commerce order payment
	 * @throws NoSuchOrderPaymentException if a commerce order payment with the primary key could not be found
	 */
	@Override
	public CommerceOrderPayment findByPrimaryKey(long commerceOrderPaymentId)
		throws NoSuchOrderPaymentException {

		return findByPrimaryKey((Serializable)commerceOrderPaymentId);
	}

	/**
	 * Returns the commerce order payment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceOrderPaymentId the primary key of the commerce order payment
	 * @return the commerce order payment, or <code>null</code> if a commerce order payment with the primary key could not be found
	 */
	@Override
	public CommerceOrderPayment fetchByPrimaryKey(long commerceOrderPaymentId) {
		return fetchByPrimaryKey((Serializable)commerceOrderPaymentId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "commerceOrderPaymentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEORDERPAYMENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceOrderPaymentModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce order payment persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCommerceOrderId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceOrderId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceOrderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceOrderId",
					new String[] {Long.class.getName()},
					new String[] {"commerceOrderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceOrderId",
					new String[] {Long.class.getName()},
					new String[] {"commerceOrderId"}, false),
				_SQL_SELECT_COMMERCEORDERPAYMENT_WHERE,
				_SQL_COUNT_COMMERCEORDERPAYMENT_WHERE,
				CommerceOrderPaymentModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceOrderPayment.", "commerceOrderId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceOrderPayment::getCommerceOrderId));

		CommerceOrderPaymentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceOrderPaymentUtil.setPersistence(null);

		entityCache.removeCache(CommerceOrderPaymentImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CommerceOrderPaymentModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEORDERPAYMENT =
		"SELECT commerceOrderPayment FROM CommerceOrderPayment commerceOrderPayment";

	private static final String _SQL_SELECT_COMMERCEORDERPAYMENT_WHERE =
		"SELECT commerceOrderPayment FROM CommerceOrderPayment commerceOrderPayment WHERE ";

	private static final String _SQL_COUNT_COMMERCEORDERPAYMENT_WHERE =
		"SELECT COUNT(commerceOrderPayment) FROM CommerceOrderPayment commerceOrderPayment WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceOrderPayment exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-598673207