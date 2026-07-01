/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence.impl;

import com.liferay.commerce.discount.exception.NoSuchDiscountRuleException;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.model.CommerceDiscountRuleTable;
import com.liferay.commerce.discount.model.impl.CommerceDiscountRuleImpl;
import com.liferay.commerce.discount.model.impl.CommerceDiscountRuleModelImpl;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountRulePersistence;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountRuleUtil;
import com.liferay.commerce.discount.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce discount rule service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CommerceDiscountRulePersistence.class)
public class CommerceDiscountRulePersistenceImpl
	extends BasePersistenceImpl
		<CommerceDiscountRule, NoSuchDiscountRuleException>
	implements CommerceDiscountRulePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceDiscountRuleUtil</code> to access the commerce discount rule persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceDiscountRuleImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceDiscountRule, NoSuchDiscountRuleException>
			_collectionPersistenceFinderByCommerceDiscountId;

	/**
	 * Returns an ordered range of all the commerce discount rules where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRuleModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount rules
	 * @param end the upper bound of the range of commerce discount rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rules
	 */
	@Override
	public List<CommerceDiscountRule> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end,
		OrderByComparator<CommerceDiscountRule> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceDiscountId.find(
			finderCache, new Object[] {commerceDiscountId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount rule in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rule
	 * @throws NoSuchDiscountRuleException if a matching commerce discount rule could not be found
	 */
	@Override
	public CommerceDiscountRule findByCommerceDiscountId_First(
			long commerceDiscountId,
			OrderByComparator<CommerceDiscountRule> orderByComparator)
		throws NoSuchDiscountRuleException {

		return _collectionPersistenceFinderByCommerceDiscountId.findFirst(
			finderCache, new Object[] {commerceDiscountId}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount rule in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rule, or <code>null</code> if a matching commerce discount rule could not be found
	 */
	@Override
	public CommerceDiscountRule fetchByCommerceDiscountId_First(
		long commerceDiscountId,
		OrderByComparator<CommerceDiscountRule> orderByComparator) {

		return _collectionPersistenceFinderByCommerceDiscountId.fetchFirst(
			finderCache, new Object[] {commerceDiscountId}, orderByComparator);
	}

	/**
	 * Removes all the commerce discount rules where commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 */
	@Override
	public void removeByCommerceDiscountId(long commerceDiscountId) {
		_collectionPersistenceFinderByCommerceDiscountId.remove(
			finderCache, new Object[] {commerceDiscountId});
	}

	/**
	 * Returns the number of commerce discount rules where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount rules
	 */
	@Override
	public int countByCommerceDiscountId(long commerceDiscountId) {
		return _collectionPersistenceFinderByCommerceDiscountId.count(
			finderCache, new Object[] {commerceDiscountId});
	}

	public CommerceDiscountRulePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceDiscountRule.class);

		setModelImplClass(CommerceDiscountRuleImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceDiscountRuleTable.INSTANCE);
	}

	/**
	 * Creates a new commerce discount rule with the primary key. Does not add the commerce discount rule to the database.
	 *
	 * @param commerceDiscountRuleId the primary key for the new commerce discount rule
	 * @return the new commerce discount rule
	 */
	@Override
	public CommerceDiscountRule create(long commerceDiscountRuleId) {
		CommerceDiscountRule commerceDiscountRule =
			new CommerceDiscountRuleImpl();

		commerceDiscountRule.setNew(true);
		commerceDiscountRule.setPrimaryKey(commerceDiscountRuleId);

		commerceDiscountRule.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceDiscountRule;
	}

	/**
	 * Removes the commerce discount rule with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceDiscountRuleId the primary key of the commerce discount rule
	 * @return the commerce discount rule that was removed
	 * @throws NoSuchDiscountRuleException if a commerce discount rule with the primary key could not be found
	 */
	@Override
	public CommerceDiscountRule remove(long commerceDiscountRuleId)
		throws NoSuchDiscountRuleException {

		return remove((Serializable)commerceDiscountRuleId);
	}

	@Override
	protected CommerceDiscountRule removeImpl(
		CommerceDiscountRule commerceDiscountRule) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceDiscountRule)) {
				commerceDiscountRule = (CommerceDiscountRule)session.get(
					CommerceDiscountRuleImpl.class,
					commerceDiscountRule.getPrimaryKeyObj());
			}

			if (commerceDiscountRule != null) {
				session.delete(commerceDiscountRule);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceDiscountRule != null) {
			clearCache(commerceDiscountRule);
		}

		return commerceDiscountRule;
	}

	@Override
	public CommerceDiscountRule updateImpl(
		CommerceDiscountRule commerceDiscountRule) {

		boolean isNew = commerceDiscountRule.isNew();

		if (!(commerceDiscountRule instanceof CommerceDiscountRuleModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceDiscountRule.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceDiscountRule);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceDiscountRule proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceDiscountRule implementation " +
					commerceDiscountRule.getClass());
		}

		CommerceDiscountRuleModelImpl commerceDiscountRuleModelImpl =
			(CommerceDiscountRuleModelImpl)commerceDiscountRule;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceDiscountRule.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceDiscountRule.setCreateDate(date);
			}
			else {
				commerceDiscountRule.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceDiscountRuleModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceDiscountRule.setModifiedDate(date);
			}
			else {
				commerceDiscountRule.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceDiscountRule);
			}
			else {
				commerceDiscountRule = (CommerceDiscountRule)session.merge(
					commerceDiscountRule);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceDiscountRule, false);

		if (isNew) {
			commerceDiscountRule.setNew(false);
		}

		commerceDiscountRule.resetOriginalValues();

		return commerceDiscountRule;
	}

	/**
	 * Returns the commerce discount rule with the primary key or throws a <code>NoSuchDiscountRuleException</code> if it could not be found.
	 *
	 * @param commerceDiscountRuleId the primary key of the commerce discount rule
	 * @return the commerce discount rule
	 * @throws NoSuchDiscountRuleException if a commerce discount rule with the primary key could not be found
	 */
	@Override
	public CommerceDiscountRule findByPrimaryKey(long commerceDiscountRuleId)
		throws NoSuchDiscountRuleException {

		return findByPrimaryKey((Serializable)commerceDiscountRuleId);
	}

	/**
	 * Returns the commerce discount rule with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceDiscountRuleId the primary key of the commerce discount rule
	 * @return the commerce discount rule, or <code>null</code> if a commerce discount rule with the primary key could not be found
	 */
	@Override
	public CommerceDiscountRule fetchByPrimaryKey(long commerceDiscountRuleId) {
		return fetchByPrimaryKey((Serializable)commerceDiscountRuleId);
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
		return "commerceDiscountRuleId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEDISCOUNTRULE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceDiscountRuleModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce discount rule persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCommerceDiscountId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceDiscountId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceDiscountId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceDiscountId",
					new String[] {Long.class.getName()},
					new String[] {"commerceDiscountId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceDiscountId",
					new String[] {Long.class.getName()},
					new String[] {"commerceDiscountId"}, false),
				_SQL_SELECT_COMMERCEDISCOUNTRULE_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNTRULE_WHERE,
				CommerceDiscountRuleModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceDiscountRule.", "commerceDiscountId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceDiscountRule::getCommerceDiscountId));

		CommerceDiscountRuleUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceDiscountRuleUtil.setPersistence(null);

		entityCache.removeCache(CommerceDiscountRuleImpl.class.getName());
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
		CommerceDiscountRuleModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEDISCOUNTRULE =
		"SELECT commerceDiscountRule FROM CommerceDiscountRule commerceDiscountRule";

	private static final String _SQL_SELECT_COMMERCEDISCOUNTRULE_WHERE =
		"SELECT commerceDiscountRule FROM CommerceDiscountRule commerceDiscountRule WHERE ";

	private static final String _SQL_COUNT_COMMERCEDISCOUNTRULE_WHERE =
		"SELECT COUNT(commerceDiscountRule) FROM CommerceDiscountRule commerceDiscountRule WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:584126556