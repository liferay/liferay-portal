/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service.persistence.impl;

import com.liferay.commerce.notification.exception.NoSuchNotificationTemplateCommerceAccountGroupRelException;
import com.liferay.commerce.notification.model.CommerceNotificationTemplateCommerceAccountGroupRel;
import com.liferay.commerce.notification.model.CommerceNotificationTemplateCommerceAccountGroupRelTable;
import com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateCommerceAccountGroupRelImpl;
import com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateCommerceAccountGroupRelModelImpl;
import com.liferay.commerce.notification.service.persistence.CommerceNotificationTemplateCommerceAccountGroupRelPersistence;
import com.liferay.commerce.notification.service.persistence.CommerceNotificationTemplateCommerceAccountGroupRelUtil;
import com.liferay.commerce.notification.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
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
 * The persistence implementation for the commerce notification template commerce account group rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @deprecated
 * @generated
 */
@Component(
	service = CommerceNotificationTemplateCommerceAccountGroupRelPersistence.class
)
@Deprecated
public class CommerceNotificationTemplateCommerceAccountGroupRelPersistenceImpl
	extends BasePersistenceImpl
		<CommerceNotificationTemplateCommerceAccountGroupRel,
		 NoSuchNotificationTemplateCommerceAccountGroupRelException>
	implements CommerceNotificationTemplateCommerceAccountGroupRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceNotificationTemplateCommerceAccountGroupRelUtil</code> to access the commerce notification template commerce account group rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceNotificationTemplateCommerceAccountGroupRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceNotificationTemplateCommerceAccountGroupRel,
		 NoSuchNotificationTemplateCommerceAccountGroupRelException>
			_collectionPersistenceFinderByCommerceNotificationTemplateId;

	/**
	 * Returns an ordered range of all the commerce notification template commerce account group rels where commerceNotificationTemplateId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateCommerceAccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceNotificationTemplateId the commerce notification template ID
	 * @param start the lower bound of the range of commerce notification template commerce account group rels
	 * @param end the upper bound of the range of commerce notification template commerce account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification template commerce account group rels
	 */
	@Override
	public List<CommerceNotificationTemplateCommerceAccountGroupRel>
		findByCommerceNotificationTemplateId(
			long commerceNotificationTemplateId, int start, int end,
			OrderByComparator
				<CommerceNotificationTemplateCommerceAccountGroupRel>
					orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceNotificationTemplateId.
			find(
				finderCache, new Object[] {commerceNotificationTemplateId},
				start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce notification template commerce account group rel in the ordered set where commerceNotificationTemplateId = &#63;.
	 *
	 * @param commerceNotificationTemplateId the commerce notification template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template commerce account group rel
	 * @throws NoSuchNotificationTemplateCommerceAccountGroupRelException if a matching commerce notification template commerce account group rel could not be found
	 */
	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRel
			findByCommerceNotificationTemplateId_First(
				long commerceNotificationTemplateId,
				OrderByComparator
					<CommerceNotificationTemplateCommerceAccountGroupRel>
						orderByComparator)
		throws NoSuchNotificationTemplateCommerceAccountGroupRelException {

		return _collectionPersistenceFinderByCommerceNotificationTemplateId.
			findFirst(
				finderCache, new Object[] {commerceNotificationTemplateId},
				orderByComparator);
	}

	/**
	 * Returns the first commerce notification template commerce account group rel in the ordered set where commerceNotificationTemplateId = &#63;.
	 *
	 * @param commerceNotificationTemplateId the commerce notification template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template commerce account group rel, or <code>null</code> if a matching commerce notification template commerce account group rel could not be found
	 */
	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRel
		fetchByCommerceNotificationTemplateId_First(
			long commerceNotificationTemplateId,
			OrderByComparator
				<CommerceNotificationTemplateCommerceAccountGroupRel>
					orderByComparator) {

		return _collectionPersistenceFinderByCommerceNotificationTemplateId.
			fetchFirst(
				finderCache, new Object[] {commerceNotificationTemplateId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce notification template commerce account group rels where commerceNotificationTemplateId = &#63; from the database.
	 *
	 * @param commerceNotificationTemplateId the commerce notification template ID
	 */
	@Override
	public void removeByCommerceNotificationTemplateId(
		long commerceNotificationTemplateId) {

		_collectionPersistenceFinderByCommerceNotificationTemplateId.remove(
			finderCache, new Object[] {commerceNotificationTemplateId});
	}

	/**
	 * Returns the number of commerce notification template commerce account group rels where commerceNotificationTemplateId = &#63;.
	 *
	 * @param commerceNotificationTemplateId the commerce notification template ID
	 * @return the number of matching commerce notification template commerce account group rels
	 */
	@Override
	public int countByCommerceNotificationTemplateId(
		long commerceNotificationTemplateId) {

		return _collectionPersistenceFinderByCommerceNotificationTemplateId.
			count(finderCache, new Object[] {commerceNotificationTemplateId});
	}

	private CollectionPersistenceFinder
		<CommerceNotificationTemplateCommerceAccountGroupRel,
		 NoSuchNotificationTemplateCommerceAccountGroupRelException>
			_collectionPersistenceFinderByCommerceAccountGroupId;

	/**
	 * Returns an ordered range of all the commerce notification template commerce account group rels where commerceAccountGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateCommerceAccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountGroupId the commerce account group ID
	 * @param start the lower bound of the range of commerce notification template commerce account group rels
	 * @param end the upper bound of the range of commerce notification template commerce account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification template commerce account group rels
	 */
	@Override
	public List<CommerceNotificationTemplateCommerceAccountGroupRel>
		findByCommerceAccountGroupId(
			long commerceAccountGroupId, int start, int end,
			OrderByComparator
				<CommerceNotificationTemplateCommerceAccountGroupRel>
					orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceAccountGroupId.find(
			finderCache, new Object[] {commerceAccountGroupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce notification template commerce account group rel in the ordered set where commerceAccountGroupId = &#63;.
	 *
	 * @param commerceAccountGroupId the commerce account group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template commerce account group rel
	 * @throws NoSuchNotificationTemplateCommerceAccountGroupRelException if a matching commerce notification template commerce account group rel could not be found
	 */
	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRel
			findByCommerceAccountGroupId_First(
				long commerceAccountGroupId,
				OrderByComparator
					<CommerceNotificationTemplateCommerceAccountGroupRel>
						orderByComparator)
		throws NoSuchNotificationTemplateCommerceAccountGroupRelException {

		return _collectionPersistenceFinderByCommerceAccountGroupId.findFirst(
			finderCache, new Object[] {commerceAccountGroupId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce notification template commerce account group rel in the ordered set where commerceAccountGroupId = &#63;.
	 *
	 * @param commerceAccountGroupId the commerce account group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template commerce account group rel, or <code>null</code> if a matching commerce notification template commerce account group rel could not be found
	 */
	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRel
		fetchByCommerceAccountGroupId_First(
			long commerceAccountGroupId,
			OrderByComparator
				<CommerceNotificationTemplateCommerceAccountGroupRel>
					orderByComparator) {

		return _collectionPersistenceFinderByCommerceAccountGroupId.fetchFirst(
			finderCache, new Object[] {commerceAccountGroupId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce notification template commerce account group rels where commerceAccountGroupId = &#63; from the database.
	 *
	 * @param commerceAccountGroupId the commerce account group ID
	 */
	@Override
	public void removeByCommerceAccountGroupId(long commerceAccountGroupId) {
		_collectionPersistenceFinderByCommerceAccountGroupId.remove(
			finderCache, new Object[] {commerceAccountGroupId});
	}

	/**
	 * Returns the number of commerce notification template commerce account group rels where commerceAccountGroupId = &#63;.
	 *
	 * @param commerceAccountGroupId the commerce account group ID
	 * @return the number of matching commerce notification template commerce account group rels
	 */
	@Override
	public int countByCommerceAccountGroupId(long commerceAccountGroupId) {
		return _collectionPersistenceFinderByCommerceAccountGroupId.count(
			finderCache, new Object[] {commerceAccountGroupId});
	}

	private UniquePersistenceFinder
		<CommerceNotificationTemplateCommerceAccountGroupRel,
		 NoSuchNotificationTemplateCommerceAccountGroupRelException>
			_uniquePersistenceFinderByC_C;

	/**
	 * Returns the commerce notification template commerce account group rel where commerceNotificationTemplateId = &#63; and commerceAccountGroupId = &#63; or throws a <code>NoSuchNotificationTemplateCommerceAccountGroupRelException</code> if it could not be found.
	 *
	 * @param commerceNotificationTemplateId the commerce notification template ID
	 * @param commerceAccountGroupId the commerce account group ID
	 * @return the matching commerce notification template commerce account group rel
	 * @throws NoSuchNotificationTemplateCommerceAccountGroupRelException if a matching commerce notification template commerce account group rel could not be found
	 */
	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRel findByC_C(
			long commerceNotificationTemplateId, long commerceAccountGroupId)
		throws NoSuchNotificationTemplateCommerceAccountGroupRelException {

		return _uniquePersistenceFinderByC_C.find(
			finderCache,
			new Object[] {
				commerceNotificationTemplateId, commerceAccountGroupId
			});
	}

	/**
	 * Returns the commerce notification template commerce account group rel where commerceNotificationTemplateId = &#63; and commerceAccountGroupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceNotificationTemplateId the commerce notification template ID
	 * @param commerceAccountGroupId the commerce account group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce notification template commerce account group rel, or <code>null</code> if a matching commerce notification template commerce account group rel could not be found
	 */
	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRel fetchByC_C(
		long commerceNotificationTemplateId, long commerceAccountGroupId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			finderCache,
			new Object[] {
				commerceNotificationTemplateId, commerceAccountGroupId
			},
			useFinderCache);
	}

	/**
	 * Removes the commerce notification template commerce account group rel where commerceNotificationTemplateId = &#63; and commerceAccountGroupId = &#63; from the database.
	 *
	 * @param commerceNotificationTemplateId the commerce notification template ID
	 * @param commerceAccountGroupId the commerce account group ID
	 * @return the commerce notification template commerce account group rel that was removed
	 */
	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRel removeByC_C(
			long commerceNotificationTemplateId, long commerceAccountGroupId)
		throws NoSuchNotificationTemplateCommerceAccountGroupRelException {

		CommerceNotificationTemplateCommerceAccountGroupRel
			commerceNotificationTemplateCommerceAccountGroupRel = findByC_C(
				commerceNotificationTemplateId, commerceAccountGroupId);

		return remove(commerceNotificationTemplateCommerceAccountGroupRel);
	}

	/**
	 * Returns the number of commerce notification template commerce account group rels where commerceNotificationTemplateId = &#63; and commerceAccountGroupId = &#63;.
	 *
	 * @param commerceNotificationTemplateId the commerce notification template ID
	 * @param commerceAccountGroupId the commerce account group ID
	 * @return the number of matching commerce notification template commerce account group rels
	 */
	@Override
	public int countByC_C(
		long commerceNotificationTemplateId, long commerceAccountGroupId) {

		return _uniquePersistenceFinderByC_C.count(
			finderCache,
			new Object[] {
				commerceNotificationTemplateId, commerceAccountGroupId
			});
	}

	public CommerceNotificationTemplateCommerceAccountGroupRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"commerceNotificationTemplateCommerceAccountGroupRelId",
			"CNTemplateCAccountGroupRelId");

		setDBColumnNames(dbColumnNames);

		setModelClass(
			CommerceNotificationTemplateCommerceAccountGroupRel.class);

		setModelImplClass(
			CommerceNotificationTemplateCommerceAccountGroupRelImpl.class);
		setModelPKClass(long.class);

		setTable(
			CommerceNotificationTemplateCommerceAccountGroupRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce notification template commerce account group rel with the primary key. Does not add the commerce notification template commerce account group rel to the database.
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRelId the primary key for the new commerce notification template commerce account group rel
	 * @return the new commerce notification template commerce account group rel
	 */
	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRel create(
		long commerceNotificationTemplateCommerceAccountGroupRelId) {

		CommerceNotificationTemplateCommerceAccountGroupRel
			commerceNotificationTemplateCommerceAccountGroupRel =
				new CommerceNotificationTemplateCommerceAccountGroupRelImpl();

		commerceNotificationTemplateCommerceAccountGroupRel.setNew(true);
		commerceNotificationTemplateCommerceAccountGroupRel.setPrimaryKey(
			commerceNotificationTemplateCommerceAccountGroupRelId);

		commerceNotificationTemplateCommerceAccountGroupRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceNotificationTemplateCommerceAccountGroupRel;
	}

	/**
	 * Removes the commerce notification template commerce account group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRelId the primary key of the commerce notification template commerce account group rel
	 * @return the commerce notification template commerce account group rel that was removed
	 * @throws NoSuchNotificationTemplateCommerceAccountGroupRelException if a commerce notification template commerce account group rel with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRel remove(
			long commerceNotificationTemplateCommerceAccountGroupRelId)
		throws NoSuchNotificationTemplateCommerceAccountGroupRelException {

		return remove(
			(Serializable)
				commerceNotificationTemplateCommerceAccountGroupRelId);
	}

	@Override
	protected CommerceNotificationTemplateCommerceAccountGroupRel removeImpl(
		CommerceNotificationTemplateCommerceAccountGroupRel
			commerceNotificationTemplateCommerceAccountGroupRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(
					commerceNotificationTemplateCommerceAccountGroupRel)) {

				commerceNotificationTemplateCommerceAccountGroupRel =
					(CommerceNotificationTemplateCommerceAccountGroupRel)
						session.get(
							CommerceNotificationTemplateCommerceAccountGroupRelImpl.class,
							commerceNotificationTemplateCommerceAccountGroupRel.
								getPrimaryKeyObj());
			}

			if (commerceNotificationTemplateCommerceAccountGroupRel != null) {
				session.delete(
					commerceNotificationTemplateCommerceAccountGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceNotificationTemplateCommerceAccountGroupRel != null) {
			clearCache(commerceNotificationTemplateCommerceAccountGroupRel);
		}

		return commerceNotificationTemplateCommerceAccountGroupRel;
	}

	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRel updateImpl(
		CommerceNotificationTemplateCommerceAccountGroupRel
			commerceNotificationTemplateCommerceAccountGroupRel) {

		boolean isNew =
			commerceNotificationTemplateCommerceAccountGroupRel.isNew();

		if (!(commerceNotificationTemplateCommerceAccountGroupRel instanceof
				CommerceNotificationTemplateCommerceAccountGroupRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceNotificationTemplateCommerceAccountGroupRel.
						getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceNotificationTemplateCommerceAccountGroupRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceNotificationTemplateCommerceAccountGroupRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceNotificationTemplateCommerceAccountGroupRel implementation " +
					commerceNotificationTemplateCommerceAccountGroupRel.
						getClass());
		}

		CommerceNotificationTemplateCommerceAccountGroupRelModelImpl
			commerceNotificationTemplateCommerceAccountGroupRelModelImpl =
				(CommerceNotificationTemplateCommerceAccountGroupRelModelImpl)
					commerceNotificationTemplateCommerceAccountGroupRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew &&
			(commerceNotificationTemplateCommerceAccountGroupRel.
				getCreateDate() == null)) {

			if (serviceContext == null) {
				commerceNotificationTemplateCommerceAccountGroupRel.
					setCreateDate(date);
			}
			else {
				commerceNotificationTemplateCommerceAccountGroupRel.
					setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!commerceNotificationTemplateCommerceAccountGroupRelModelImpl.
				hasSetModifiedDate()) {

			if (serviceContext == null) {
				commerceNotificationTemplateCommerceAccountGroupRel.
					setModifiedDate(date);
			}
			else {
				commerceNotificationTemplateCommerceAccountGroupRel.
					setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(
					commerceNotificationTemplateCommerceAccountGroupRel);
			}
			else {
				commerceNotificationTemplateCommerceAccountGroupRel =
					(CommerceNotificationTemplateCommerceAccountGroupRel)
						session.merge(
							commerceNotificationTemplateCommerceAccountGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(
			commerceNotificationTemplateCommerceAccountGroupRel, false);

		if (isNew) {
			commerceNotificationTemplateCommerceAccountGroupRel.setNew(false);
		}

		commerceNotificationTemplateCommerceAccountGroupRel.
			resetOriginalValues();

		return commerceNotificationTemplateCommerceAccountGroupRel;
	}

	/**
	 * Returns the commerce notification template commerce account group rel with the primary key or throws a <code>NoSuchNotificationTemplateCommerceAccountGroupRelException</code> if it could not be found.
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRelId the primary key of the commerce notification template commerce account group rel
	 * @return the commerce notification template commerce account group rel
	 * @throws NoSuchNotificationTemplateCommerceAccountGroupRelException if a commerce notification template commerce account group rel with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRel findByPrimaryKey(
			long commerceNotificationTemplateCommerceAccountGroupRelId)
		throws NoSuchNotificationTemplateCommerceAccountGroupRelException {

		return findByPrimaryKey(
			(Serializable)
				commerceNotificationTemplateCommerceAccountGroupRelId);
	}

	/**
	 * Returns the commerce notification template commerce account group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRelId the primary key of the commerce notification template commerce account group rel
	 * @return the commerce notification template commerce account group rel, or <code>null</code> if a commerce notification template commerce account group rel with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRel
		fetchByPrimaryKey(
			long commerceNotificationTemplateCommerceAccountGroupRelId) {

		return fetchByPrimaryKey(
			(Serializable)
				commerceNotificationTemplateCommerceAccountGroupRelId);
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
		return "CNTemplateCAccountGroupRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceNotificationTemplateCommerceAccountGroupRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCENOTIFICATIONTEMPLATECOMMERCEACCOUNTGROUPREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceNotificationTemplateCommerceAccountGroupRelModelImpl.
			TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce notification template commerce account group rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCommerceNotificationTemplateId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceNotificationTemplateId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceNotificationTemplateId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceNotificationTemplateId",
					new String[] {Long.class.getName()},
					new String[] {"commerceNotificationTemplateId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceNotificationTemplateId",
					new String[] {Long.class.getName()},
					new String[] {"commerceNotificationTemplateId"}, false),
				_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATECOMMERCEACCOUNTGROUPREL_WHERE,
				_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATECOMMERCEACCOUNTGROUPREL_WHERE,
				CommerceNotificationTemplateCommerceAccountGroupRelModelImpl.
					ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceNotificationTemplateCommerceAccountGroupRel.",
					"commerceNotificationTemplateId", FinderColumn.Type.LONG,
					"=", true, true,
					CommerceNotificationTemplateCommerceAccountGroupRel::
						getCommerceNotificationTemplateId));

		_collectionPersistenceFinderByCommerceAccountGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceAccountGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceAccountGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceAccountGroupId",
					new String[] {Long.class.getName()},
					new String[] {"commerceAccountGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceAccountGroupId",
					new String[] {Long.class.getName()},
					new String[] {"commerceAccountGroupId"}, false),
				_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATECOMMERCEACCOUNTGROUPREL_WHERE,
				_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATECOMMERCEACCOUNTGROUPREL_WHERE,
				CommerceNotificationTemplateCommerceAccountGroupRelModelImpl.
					ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceNotificationTemplateCommerceAccountGroupRel.",
					"commerceAccountGroupId", FinderColumn.Type.LONG, "=", true,
					true,
					CommerceNotificationTemplateCommerceAccountGroupRel::
						getCommerceAccountGroupId));

		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {
					"commerceNotificationTemplateId", "commerceAccountGroupId"
				},
				0, 0, false,
				CommerceNotificationTemplateCommerceAccountGroupRel::
					getCommerceNotificationTemplateId,
				CommerceNotificationTemplateCommerceAccountGroupRel::
					getCommerceAccountGroupId),
			_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATECOMMERCEACCOUNTGROUPREL_WHERE,
			"",
			new FinderColumn<>(
				"commerceNotificationTemplateCommerceAccountGroupRel.",
				"commerceNotificationTemplateId", FinderColumn.Type.LONG, "=",
				true, true,
				CommerceNotificationTemplateCommerceAccountGroupRel::
					getCommerceNotificationTemplateId),
			new FinderColumn<>(
				"commerceNotificationTemplateCommerceAccountGroupRel.",
				"commerceAccountGroupId", FinderColumn.Type.LONG, "=", true,
				true,
				CommerceNotificationTemplateCommerceAccountGroupRel::
					getCommerceAccountGroupId));

		CommerceNotificationTemplateCommerceAccountGroupRelUtil.setPersistence(
			this);
	}

	@Deactivate
	public void deactivate() {
		CommerceNotificationTemplateCommerceAccountGroupRelUtil.setPersistence(
			null);

		entityCache.removeCache(
			CommerceNotificationTemplateCommerceAccountGroupRelImpl.class.
				getName());
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
		CommerceNotificationTemplateCommerceAccountGroupRelModelImpl.
			ENTITY_ALIAS + ".";

	private static final String
		_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATECOMMERCEACCOUNTGROUPREL =
			"SELECT commerceNotificationTemplateCommerceAccountGroupRel FROM CommerceNotificationTemplateCommerceAccountGroupRel commerceNotificationTemplateCommerceAccountGroupRel";

	private static final String
		_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATECOMMERCEACCOUNTGROUPREL_WHERE =
			"SELECT commerceNotificationTemplateCommerceAccountGroupRel FROM CommerceNotificationTemplateCommerceAccountGroupRel commerceNotificationTemplateCommerceAccountGroupRel WHERE ";

	private static final String
		_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATECOMMERCEACCOUNTGROUPREL_WHERE =
			"SELECT COUNT(commerceNotificationTemplateCommerceAccountGroupRel) FROM CommerceNotificationTemplateCommerceAccountGroupRel commerceNotificationTemplateCommerceAccountGroupRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceNotificationTemplateCommerceAccountGroupRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceNotificationTemplateCommerceAccountGroupRelPersistenceImpl.
			class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"commerceNotificationTemplateCommerceAccountGroupRelId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-128403208