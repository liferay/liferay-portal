/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.persistence.impl;

import com.liferay.commerce.inventory.exception.NoSuchInventoryAuditException;
import com.liferay.commerce.inventory.model.CommerceInventoryAudit;
import com.liferay.commerce.inventory.model.CommerceInventoryAuditTable;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryAuditImpl;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryAuditModelImpl;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryAuditPersistence;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryAuditUtil;
import com.liferay.commerce.inventory.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the commerce inventory audit service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommerceInventoryAuditPersistence.class)
public class CommerceInventoryAuditPersistenceImpl
	extends BasePersistenceImpl
		<CommerceInventoryAudit, NoSuchInventoryAuditException>
	implements CommerceInventoryAuditPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceInventoryAuditUtil</code> to access the commerce inventory audit persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceInventoryAuditImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceInventoryAudit, NoSuchInventoryAuditException>
			_collectionPersistenceFinderByLtCreateDate;

	/**
	 * Returns all the commerce inventory audits where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @return the matching commerce inventory audits
	 */
	@Override
	public List<CommerceInventoryAudit> findByLtCreateDate(Date createDate) {
		return findByLtCreateDate(
			createDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory audits where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of commerce inventory audits
	 * @param end the upper bound of the range of commerce inventory audits (not inclusive)
	 * @return the range of matching commerce inventory audits
	 */
	@Override
	public List<CommerceInventoryAudit> findByLtCreateDate(
		Date createDate, int start, int end) {

		return findByLtCreateDate(createDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory audits where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of commerce inventory audits
	 * @param end the upper bound of the range of commerce inventory audits (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory audits
	 */
	@Override
	public List<CommerceInventoryAudit> findByLtCreateDate(
		Date createDate, int start, int end,
		OrderByComparator<CommerceInventoryAudit> orderByComparator) {

		return findByLtCreateDate(
			createDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory audits where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of commerce inventory audits
	 * @param end the upper bound of the range of commerce inventory audits (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory audits
	 */
	@Override
	public List<CommerceInventoryAudit> findByLtCreateDate(
		Date createDate, int start, int end,
		OrderByComparator<CommerceInventoryAudit> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtCreateDate.find(
			finderCache, new Object[] {createDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory audit in the ordered set where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory audit
	 * @throws NoSuchInventoryAuditException if a matching commerce inventory audit could not be found
	 */
	@Override
	public CommerceInventoryAudit findByLtCreateDate_First(
			Date createDate,
			OrderByComparator<CommerceInventoryAudit> orderByComparator)
		throws NoSuchInventoryAuditException {

		return _collectionPersistenceFinderByLtCreateDate.findFirst(
			finderCache, new Object[] {createDate}, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory audit in the ordered set where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory audit, or <code>null</code> if a matching commerce inventory audit could not be found
	 */
	@Override
	public CommerceInventoryAudit fetchByLtCreateDate_First(
		Date createDate,
		OrderByComparator<CommerceInventoryAudit> orderByComparator) {

		return _collectionPersistenceFinderByLtCreateDate.fetchFirst(
			finderCache, new Object[] {createDate}, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory audits where createDate &lt; &#63; from the database.
	 *
	 * @param createDate the create date
	 */
	@Override
	public void removeByLtCreateDate(Date createDate) {
		_collectionPersistenceFinderByLtCreateDate.remove(
			finderCache, new Object[] {createDate});
	}

	/**
	 * Returns the number of commerce inventory audits where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @return the number of matching commerce inventory audits
	 */
	@Override
	public int countByLtCreateDate(Date createDate) {
		return _collectionPersistenceFinderByLtCreateDate.count(
			finderCache, new Object[] {createDate});
	}

	private CollectionPersistenceFinder
		<CommerceInventoryAudit, NoSuchInventoryAuditException>
			_collectionPersistenceFinderByC_S_U;

	/**
	 * Returns an ordered range of all the commerce inventory audits where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory audits
	 * @param end the upper bound of the range of commerce inventory audits (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory audits
	 */
	@Override
	public List<CommerceInventoryAudit> findByC_S_U(
		long companyId, String sku, String unitOfMeasureKey, int start, int end,
		OrderByComparator<CommerceInventoryAudit> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S_U.find(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory audit in the ordered set where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory audit
	 * @throws NoSuchInventoryAuditException if a matching commerce inventory audit could not be found
	 */
	@Override
	public CommerceInventoryAudit findByC_S_U_First(
			long companyId, String sku, String unitOfMeasureKey,
			OrderByComparator<CommerceInventoryAudit> orderByComparator)
		throws NoSuchInventoryAuditException {

		return _collectionPersistenceFinderByC_S_U.findFirst(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey},
			orderByComparator);
	}

	/**
	 * Returns the first commerce inventory audit in the ordered set where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory audit, or <code>null</code> if a matching commerce inventory audit could not be found
	 */
	@Override
	public CommerceInventoryAudit fetchByC_S_U_First(
		long companyId, String sku, String unitOfMeasureKey,
		OrderByComparator<CommerceInventoryAudit> orderByComparator) {

		return _collectionPersistenceFinderByC_S_U.fetchFirst(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey},
			orderByComparator);
	}

	/**
	 * Removes all the commerce inventory audits where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 */
	@Override
	public void removeByC_S_U(
		long companyId, String sku, String unitOfMeasureKey) {

		_collectionPersistenceFinderByC_S_U.remove(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey});
	}

	/**
	 * Returns the number of commerce inventory audits where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the number of matching commerce inventory audits
	 */
	@Override
	public int countByC_S_U(
		long companyId, String sku, String unitOfMeasureKey) {

		return _collectionPersistenceFinderByC_S_U.count(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey});
	}

	public CommerceInventoryAuditPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("commerceInventoryAuditId", "CIAuditId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceInventoryAudit.class);

		setModelImplClass(CommerceInventoryAuditImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceInventoryAuditTable.INSTANCE);
	}

	/**
	 * Creates a new commerce inventory audit with the primary key. Does not add the commerce inventory audit to the database.
	 *
	 * @param commerceInventoryAuditId the primary key for the new commerce inventory audit
	 * @return the new commerce inventory audit
	 */
	@Override
	public CommerceInventoryAudit create(long commerceInventoryAuditId) {
		CommerceInventoryAudit commerceInventoryAudit =
			new CommerceInventoryAuditImpl();

		commerceInventoryAudit.setNew(true);
		commerceInventoryAudit.setPrimaryKey(commerceInventoryAuditId);

		commerceInventoryAudit.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceInventoryAudit;
	}

	/**
	 * Removes the commerce inventory audit with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceInventoryAuditId the primary key of the commerce inventory audit
	 * @return the commerce inventory audit that was removed
	 * @throws NoSuchInventoryAuditException if a commerce inventory audit with the primary key could not be found
	 */
	@Override
	public CommerceInventoryAudit remove(long commerceInventoryAuditId)
		throws NoSuchInventoryAuditException {

		return remove((Serializable)commerceInventoryAuditId);
	}

	@Override
	protected CommerceInventoryAudit removeImpl(
		CommerceInventoryAudit commerceInventoryAudit) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceInventoryAudit)) {
				commerceInventoryAudit = (CommerceInventoryAudit)session.get(
					CommerceInventoryAuditImpl.class,
					commerceInventoryAudit.getPrimaryKeyObj());
			}

			if (commerceInventoryAudit != null) {
				session.delete(commerceInventoryAudit);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceInventoryAudit != null) {
			clearCache(commerceInventoryAudit);
		}

		return commerceInventoryAudit;
	}

	@Override
	public CommerceInventoryAudit updateImpl(
		CommerceInventoryAudit commerceInventoryAudit) {

		boolean isNew = commerceInventoryAudit.isNew();

		if (!(commerceInventoryAudit instanceof
				CommerceInventoryAuditModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceInventoryAudit.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceInventoryAudit);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceInventoryAudit proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceInventoryAudit implementation " +
					commerceInventoryAudit.getClass());
		}

		CommerceInventoryAuditModelImpl commerceInventoryAuditModelImpl =
			(CommerceInventoryAuditModelImpl)commerceInventoryAudit;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceInventoryAudit.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceInventoryAudit.setCreateDate(date);
			}
			else {
				commerceInventoryAudit.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceInventoryAuditModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceInventoryAudit.setModifiedDate(date);
			}
			else {
				commerceInventoryAudit.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceInventoryAudit);
			}
			else {
				commerceInventoryAudit = (CommerceInventoryAudit)session.merge(
					commerceInventoryAudit);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceInventoryAudit, false);

		if (isNew) {
			commerceInventoryAudit.setNew(false);
		}

		commerceInventoryAudit.resetOriginalValues();

		return commerceInventoryAudit;
	}

	/**
	 * Returns the commerce inventory audit with the primary key or throws a <code>NoSuchInventoryAuditException</code> if it could not be found.
	 *
	 * @param commerceInventoryAuditId the primary key of the commerce inventory audit
	 * @return the commerce inventory audit
	 * @throws NoSuchInventoryAuditException if a commerce inventory audit with the primary key could not be found
	 */
	@Override
	public CommerceInventoryAudit findByPrimaryKey(
			long commerceInventoryAuditId)
		throws NoSuchInventoryAuditException {

		return findByPrimaryKey((Serializable)commerceInventoryAuditId);
	}

	/**
	 * Returns the commerce inventory audit with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceInventoryAuditId the primary key of the commerce inventory audit
	 * @return the commerce inventory audit, or <code>null</code> if a commerce inventory audit with the primary key could not be found
	 */
	@Override
	public CommerceInventoryAudit fetchByPrimaryKey(
		long commerceInventoryAuditId) {

		return fetchByPrimaryKey((Serializable)commerceInventoryAuditId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "CIAuditId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceInventoryAuditId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEINVENTORYAUDIT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceInventoryAuditModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce inventory audit persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByLtCreateDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLtCreateDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"createDate"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByLtCreateDate", new String[] {Date.class.getName()},
					new String[] {"createDate"}, false),
				_SQL_SELECT_COMMERCEINVENTORYAUDIT_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYAUDIT_WHERE,
				CommerceInventoryAuditModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryAudit.", "createDate",
					FinderColumn.Type.DATE, "<", true, true,
					CommerceInventoryAudit::getCreateDate));

		_collectionPersistenceFinderByC_S_U = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S_U",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "sku", "unitOfMeasureKey"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S_U",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "sku", "unitOfMeasureKey"}, 0, 6,
				true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S_U",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "sku", "unitOfMeasureKey"}, 0, 6,
				false, null),
			_SQL_SELECT_COMMERCEINVENTORYAUDIT_WHERE,
			_SQL_COUNT_COMMERCEINVENTORYAUDIT_WHERE,
			CommerceInventoryAuditModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"commerceInventoryAudit.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, CommerceInventoryAudit::getCompanyId),
			new FinderColumn<>(
				"commerceInventoryAudit.", "sku", FinderColumn.Type.STRING, "=",
				true, true, CommerceInventoryAudit::getSku),
			new FinderColumn<>(
				"commerceInventoryAudit.", "unitOfMeasureKey",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryAudit::getUnitOfMeasureKey));

		CommerceInventoryAuditUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceInventoryAuditUtil.setPersistence(null);

		entityCache.removeCache(CommerceInventoryAuditImpl.class.getName());
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
		CommerceInventoryAuditModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEINVENTORYAUDIT =
		"SELECT commerceInventoryAudit FROM CommerceInventoryAudit commerceInventoryAudit";

	private static final String _SQL_SELECT_COMMERCEINVENTORYAUDIT_WHERE =
		"SELECT commerceInventoryAudit FROM CommerceInventoryAudit commerceInventoryAudit WHERE ";

	private static final String _SQL_COUNT_COMMERCEINVENTORYAUDIT_WHERE =
		"SELECT COUNT(commerceInventoryAudit) FROM CommerceInventoryAudit commerceInventoryAudit WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceInventoryAudit exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"commerceInventoryAuditId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:93549677