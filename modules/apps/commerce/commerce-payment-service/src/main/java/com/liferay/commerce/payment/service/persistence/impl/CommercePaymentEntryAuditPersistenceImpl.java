/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.persistence.impl;

import com.liferay.commerce.payment.exception.NoSuchPaymentEntryAuditException;
import com.liferay.commerce.payment.model.CommercePaymentEntryAudit;
import com.liferay.commerce.payment.model.CommercePaymentEntryAuditTable;
import com.liferay.commerce.payment.model.impl.CommercePaymentEntryAuditImpl;
import com.liferay.commerce.payment.model.impl.CommercePaymentEntryAuditModelImpl;
import com.liferay.commerce.payment.service.persistence.CommercePaymentEntryAuditPersistence;
import com.liferay.commerce.payment.service.persistence.CommercePaymentEntryAuditUtil;
import com.liferay.commerce.payment.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
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
 * The persistence implementation for the commerce payment entry audit service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommercePaymentEntryAuditPersistence.class)
public class CommercePaymentEntryAuditPersistenceImpl
	extends BasePersistenceImpl
		<CommercePaymentEntryAudit, NoSuchPaymentEntryAuditException>
	implements CommercePaymentEntryAuditPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommercePaymentEntryAuditUtil</code> to access the commerce payment entry audit persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommercePaymentEntryAuditImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<CommercePaymentEntryAudit, NoSuchPaymentEntryAuditException>
			_collectionPersistenceFinderByCommercePaymentEntryId;

	/**
	 * Returns an ordered range of all the commerce payment entry audits where commercePaymentEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param start the lower bound of the range of commerce payment entry audits
	 * @param end the upper bound of the range of commerce payment entry audits (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment entry audits
	 */
	@Override
	public List<CommercePaymentEntryAudit> findByCommercePaymentEntryId(
		long commercePaymentEntryId, int start, int end,
		OrderByComparator<CommercePaymentEntryAudit> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommercePaymentEntryId.find(
			finderCache, new Object[] {commercePaymentEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce payment entry audit in the ordered set where commercePaymentEntryId = &#63;.
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry audit
	 * @throws NoSuchPaymentEntryAuditException if a matching commerce payment entry audit could not be found
	 */
	@Override
	public CommercePaymentEntryAudit findByCommercePaymentEntryId_First(
			long commercePaymentEntryId,
			OrderByComparator<CommercePaymentEntryAudit> orderByComparator)
		throws NoSuchPaymentEntryAuditException {

		return _collectionPersistenceFinderByCommercePaymentEntryId.findFirst(
			finderCache, new Object[] {commercePaymentEntryId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce payment entry audit in the ordered set where commercePaymentEntryId = &#63;.
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry audit, or <code>null</code> if a matching commerce payment entry audit could not be found
	 */
	@Override
	public CommercePaymentEntryAudit fetchByCommercePaymentEntryId_First(
		long commercePaymentEntryId,
		OrderByComparator<CommercePaymentEntryAudit> orderByComparator) {

		return _collectionPersistenceFinderByCommercePaymentEntryId.fetchFirst(
			finderCache, new Object[] {commercePaymentEntryId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce payment entry audits that the user has permissions to view where commercePaymentEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param start the lower bound of the range of commerce payment entry audits
	 * @param end the upper bound of the range of commerce payment entry audits (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entry audits that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntryAudit> filterFindByCommercePaymentEntryId(
		long commercePaymentEntryId, int start, int end,
		OrderByComparator<CommercePaymentEntryAudit> orderByComparator) {

		return _collectionPersistenceFinderByCommercePaymentEntryId.filterFind(
			finderCache, new Object[] {commercePaymentEntryId}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the commerce payment entry audits where commercePaymentEntryId = &#63; from the database.
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 */
	@Override
	public void removeByCommercePaymentEntryId(long commercePaymentEntryId) {
		_collectionPersistenceFinderByCommercePaymentEntryId.remove(
			finderCache, new Object[] {commercePaymentEntryId});
	}

	/**
	 * Returns the number of commerce payment entry audits where commercePaymentEntryId = &#63;.
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @return the number of matching commerce payment entry audits
	 */
	@Override
	public int countByCommercePaymentEntryId(long commercePaymentEntryId) {
		return _collectionPersistenceFinderByCommercePaymentEntryId.count(
			finderCache, new Object[] {commercePaymentEntryId});
	}

	/**
	 * Returns the number of commerce payment entry audits that the user has permission to view where commercePaymentEntryId = &#63;.
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @return the number of matching commerce payment entry audits that the user has permission to view
	 */
	@Override
	public int filterCountByCommercePaymentEntryId(
		long commercePaymentEntryId) {

		return _collectionPersistenceFinderByCommercePaymentEntryId.filterCount(
			finderCache, new Object[] {commercePaymentEntryId});
	}

	public CommercePaymentEntryAuditPersistenceImpl() {
		setModelClass(CommercePaymentEntryAudit.class);

		setModelImplClass(CommercePaymentEntryAuditImpl.class);
		setModelPKClass(long.class);

		setTable(CommercePaymentEntryAuditTable.INSTANCE);
	}

	/**
	 * Creates a new commerce payment entry audit with the primary key. Does not add the commerce payment entry audit to the database.
	 *
	 * @param commercePaymentEntryAuditId the primary key for the new commerce payment entry audit
	 * @return the new commerce payment entry audit
	 */
	@Override
	public CommercePaymentEntryAudit create(long commercePaymentEntryAuditId) {
		CommercePaymentEntryAudit commercePaymentEntryAudit =
			new CommercePaymentEntryAuditImpl();

		commercePaymentEntryAudit.setNew(true);
		commercePaymentEntryAudit.setPrimaryKey(commercePaymentEntryAuditId);

		commercePaymentEntryAudit.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commercePaymentEntryAudit;
	}

	/**
	 * Removes the commerce payment entry audit with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commercePaymentEntryAuditId the primary key of the commerce payment entry audit
	 * @return the commerce payment entry audit that was removed
	 * @throws NoSuchPaymentEntryAuditException if a commerce payment entry audit with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntryAudit remove(long commercePaymentEntryAuditId)
		throws NoSuchPaymentEntryAuditException {

		return remove((Serializable)commercePaymentEntryAuditId);
	}

	@Override
	protected CommercePaymentEntryAudit removeImpl(
		CommercePaymentEntryAudit commercePaymentEntryAudit) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commercePaymentEntryAudit)) {
				commercePaymentEntryAudit =
					(CommercePaymentEntryAudit)session.get(
						CommercePaymentEntryAuditImpl.class,
						commercePaymentEntryAudit.getPrimaryKeyObj());
			}

			if (commercePaymentEntryAudit != null) {
				session.delete(commercePaymentEntryAudit);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commercePaymentEntryAudit != null) {
			clearCache(commercePaymentEntryAudit);
		}

		return commercePaymentEntryAudit;
	}

	@Override
	public CommercePaymentEntryAudit updateImpl(
		CommercePaymentEntryAudit commercePaymentEntryAudit) {

		boolean isNew = commercePaymentEntryAudit.isNew();

		if (!(commercePaymentEntryAudit instanceof
				CommercePaymentEntryAuditModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commercePaymentEntryAudit.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commercePaymentEntryAudit);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commercePaymentEntryAudit proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommercePaymentEntryAudit implementation " +
					commercePaymentEntryAudit.getClass());
		}

		CommercePaymentEntryAuditModelImpl commercePaymentEntryAuditModelImpl =
			(CommercePaymentEntryAuditModelImpl)commercePaymentEntryAudit;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commercePaymentEntryAudit.getCreateDate() == null)) {
			if (serviceContext == null) {
				commercePaymentEntryAudit.setCreateDate(date);
			}
			else {
				commercePaymentEntryAudit.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commercePaymentEntryAuditModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commercePaymentEntryAudit.setModifiedDate(date);
			}
			else {
				commercePaymentEntryAudit.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commercePaymentEntryAudit);
			}
			else {
				commercePaymentEntryAudit =
					(CommercePaymentEntryAudit)session.merge(
						commercePaymentEntryAudit);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commercePaymentEntryAudit, false);

		if (isNew) {
			commercePaymentEntryAudit.setNew(false);
		}

		commercePaymentEntryAudit.resetOriginalValues();

		return commercePaymentEntryAudit;
	}

	/**
	 * Returns the commerce payment entry audit with the primary key or throws a <code>NoSuchPaymentEntryAuditException</code> if it could not be found.
	 *
	 * @param commercePaymentEntryAuditId the primary key of the commerce payment entry audit
	 * @return the commerce payment entry audit
	 * @throws NoSuchPaymentEntryAuditException if a commerce payment entry audit with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntryAudit findByPrimaryKey(
			long commercePaymentEntryAuditId)
		throws NoSuchPaymentEntryAuditException {

		return findByPrimaryKey((Serializable)commercePaymentEntryAuditId);
	}

	/**
	 * Returns the commerce payment entry audit with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commercePaymentEntryAuditId the primary key of the commerce payment entry audit
	 * @return the commerce payment entry audit, or <code>null</code> if a commerce payment entry audit with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntryAudit fetchByPrimaryKey(
		long commercePaymentEntryAuditId) {

		return fetchByPrimaryKey((Serializable)commercePaymentEntryAuditId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "commercePaymentEntryAuditId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommercePaymentEntryAuditModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce payment entry audit persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCommercePaymentEntryId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommercePaymentEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commercePaymentEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommercePaymentEntryId",
					new String[] {Long.class.getName()},
					new String[] {"commercePaymentEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommercePaymentEntryId",
					new String[] {Long.class.getName()},
					new String[] {"commercePaymentEntryId"}, false),
				_SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT_WHERE,
				_SQL_COUNT_COMMERCEPAYMENTENTRYAUDIT_WHERE,
				CommercePaymentEntryAuditModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commercePaymentEntryAudit.", "commercePaymentEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePaymentEntryAudit::getCommercePaymentEntryId));

		CommercePaymentEntryAuditUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommercePaymentEntryAuditUtil.setPersistence(null);

		entityCache.removeCache(CommercePaymentEntryAuditImpl.class.getName());
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
		CommercePaymentEntryAuditModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT =
		"SELECT commercePaymentEntryAudit FROM CommercePaymentEntryAudit commercePaymentEntryAudit";

	private static final String _SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT_WHERE =
		"SELECT commercePaymentEntryAudit FROM CommercePaymentEntryAudit commercePaymentEntryAudit WHERE ";

	private static final String _SQL_COUNT_COMMERCEPAYMENTENTRYAUDIT_WHERE =
		"SELECT COUNT(commercePaymentEntryAudit) FROM CommercePaymentEntryAudit commercePaymentEntryAudit WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:834087148