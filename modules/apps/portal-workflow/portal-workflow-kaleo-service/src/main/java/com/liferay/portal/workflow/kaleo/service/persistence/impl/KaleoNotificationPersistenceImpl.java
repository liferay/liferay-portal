/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.workflow.kaleo.exception.NoSuchNotificationException;
import com.liferay.portal.workflow.kaleo.model.KaleoNotification;
import com.liferay.portal.workflow.kaleo.model.KaleoNotificationTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoNotificationImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoNotificationModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNotificationPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNotificationUtil;
import com.liferay.portal.workflow.kaleo.service.persistence.impl.constants.KaleoPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the kaleo notification service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoNotificationPersistence.class)
public class KaleoNotificationPersistenceImpl
	extends BasePersistenceImpl<KaleoNotification, NoSuchNotificationException>
	implements KaleoNotificationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoNotificationUtil</code> to access the kaleo notification persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoNotificationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<KaleoNotification, NoSuchNotificationException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo notifications where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo notifications
	 * @param end the upper bound of the range of kaleo notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo notifications
	 */
	@Override
	public List<KaleoNotification> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoNotification> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo notification in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo notification
	 * @throws NoSuchNotificationException if a matching kaleo notification could not be found
	 */
	@Override
	public KaleoNotification findByCompanyId_First(
			long companyId,
			OrderByComparator<KaleoNotification> orderByComparator)
		throws NoSuchNotificationException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo notification in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo notification, or <code>null</code> if a matching kaleo notification could not be found
	 */
	@Override
	public KaleoNotification fetchByCompanyId_First(
		long companyId,
		OrderByComparator<KaleoNotification> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo notifications where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo notifications where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo notifications
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<KaleoNotification, NoSuchNotificationException>
			_collectionPersistenceFinderByKaleoDefinitionVersionId;

	/**
	 * Returns an ordered range of all the kaleo notifications where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo notifications
	 * @param end the upper bound of the range of kaleo notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo notifications
	 */
	@Override
	public List<KaleoNotification> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoNotification> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.find(
			finderCache, new Object[] {kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo notification in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo notification
	 * @throws NoSuchNotificationException if a matching kaleo notification could not be found
	 */
	@Override
	public KaleoNotification findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoNotification> orderByComparator)
		throws NoSuchNotificationException {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.findFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo notification in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo notification, or <code>null</code> if a matching kaleo notification could not be found
	 */
	@Override
	public KaleoNotification fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoNotification> orderByComparator) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.
			fetchFirst(
				finderCache, new Object[] {kaleoDefinitionVersionId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo notifications where kaleoDefinitionVersionId = &#63; from the database.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 */
	@Override
	public void removeByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId) {

		_collectionPersistenceFinderByKaleoDefinitionVersionId.remove(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	/**
	 * Returns the number of kaleo notifications where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo notifications
	 */
	@Override
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {
		return _collectionPersistenceFinderByKaleoDefinitionVersionId.count(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	private CollectionPersistenceFinder
		<KaleoNotification, NoSuchNotificationException>
			_collectionPersistenceFinderByKCN_KCPK;

	/**
	 * Returns an ordered range of all the kaleo notifications where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param start the lower bound of the range of kaleo notifications
	 * @param end the upper bound of the range of kaleo notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo notifications
	 */
	@Override
	public List<KaleoNotification> findByKCN_KCPK(
		String kaleoClassName, long kaleoClassPK, int start, int end,
		OrderByComparator<KaleoNotification> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKCN_KCPK.find(
			finderCache, new Object[] {kaleoClassName, kaleoClassPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo notification in the ordered set where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo notification
	 * @throws NoSuchNotificationException if a matching kaleo notification could not be found
	 */
	@Override
	public KaleoNotification findByKCN_KCPK_First(
			String kaleoClassName, long kaleoClassPK,
			OrderByComparator<KaleoNotification> orderByComparator)
		throws NoSuchNotificationException {

		return _collectionPersistenceFinderByKCN_KCPK.findFirst(
			finderCache, new Object[] {kaleoClassName, kaleoClassPK},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo notification in the ordered set where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo notification, or <code>null</code> if a matching kaleo notification could not be found
	 */
	@Override
	public KaleoNotification fetchByKCN_KCPK_First(
		String kaleoClassName, long kaleoClassPK,
		OrderByComparator<KaleoNotification> orderByComparator) {

		return _collectionPersistenceFinderByKCN_KCPK.fetchFirst(
			finderCache, new Object[] {kaleoClassName, kaleoClassPK},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo notifications where kaleoClassName = &#63; and kaleoClassPK = &#63; from the database.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 */
	@Override
	public void removeByKCN_KCPK(String kaleoClassName, long kaleoClassPK) {
		_collectionPersistenceFinderByKCN_KCPK.remove(
			finderCache, new Object[] {kaleoClassName, kaleoClassPK});
	}

	/**
	 * Returns the number of kaleo notifications where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @return the number of matching kaleo notifications
	 */
	@Override
	public int countByKCN_KCPK(String kaleoClassName, long kaleoClassPK) {
		return _collectionPersistenceFinderByKCN_KCPK.count(
			finderCache, new Object[] {kaleoClassName, kaleoClassPK});
	}

	private CollectionPersistenceFinder
		<KaleoNotification, NoSuchNotificationException>
			_collectionPersistenceFinderByKCN_KDVI;

	/**
	 * Returns an ordered range of all the kaleo notifications where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo notifications
	 * @param end the upper bound of the range of kaleo notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo notifications
	 */
	@Override
	public List<KaleoNotification> findByKCN_KDVI(
		String kaleoClassName, long kaleoDefinitionVersionId, int start,
		int end, OrderByComparator<KaleoNotification> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKCN_KDVI.find(
			finderCache,
			new Object[] {kaleoClassName, kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo notification in the ordered set where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo notification
	 * @throws NoSuchNotificationException if a matching kaleo notification could not be found
	 */
	@Override
	public KaleoNotification findByKCN_KDVI_First(
			String kaleoClassName, long kaleoDefinitionVersionId,
			OrderByComparator<KaleoNotification> orderByComparator)
		throws NoSuchNotificationException {

		return _collectionPersistenceFinderByKCN_KDVI.findFirst(
			finderCache,
			new Object[] {kaleoClassName, kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo notification in the ordered set where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo notification, or <code>null</code> if a matching kaleo notification could not be found
	 */
	@Override
	public KaleoNotification fetchByKCN_KDVI_First(
		String kaleoClassName, long kaleoDefinitionVersionId,
		OrderByComparator<KaleoNotification> orderByComparator) {

		return _collectionPersistenceFinderByKCN_KDVI.fetchFirst(
			finderCache,
			new Object[] {kaleoClassName, kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo notifications where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63; from the database.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 */
	@Override
	public void removeByKCN_KDVI(
		String kaleoClassName, long kaleoDefinitionVersionId) {

		_collectionPersistenceFinderByKCN_KDVI.remove(
			finderCache,
			new Object[] {kaleoClassName, kaleoDefinitionVersionId});
	}

	/**
	 * Returns the number of kaleo notifications where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo notifications
	 */
	@Override
	public int countByKCN_KDVI(
		String kaleoClassName, long kaleoDefinitionVersionId) {

		return _collectionPersistenceFinderByKCN_KDVI.count(
			finderCache,
			new Object[] {kaleoClassName, kaleoDefinitionVersionId});
	}

	public KaleoNotificationPersistenceImpl() {
		setModelClass(KaleoNotification.class);

		setModelImplClass(KaleoNotificationImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoNotificationTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo notification with the primary key. Does not add the kaleo notification to the database.
	 *
	 * @param kaleoNotificationId the primary key for the new kaleo notification
	 * @return the new kaleo notification
	 */
	@Override
	public KaleoNotification create(long kaleoNotificationId) {
		KaleoNotification kaleoNotification = new KaleoNotificationImpl();

		kaleoNotification.setNew(true);
		kaleoNotification.setPrimaryKey(kaleoNotificationId);

		kaleoNotification.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoNotification;
	}

	/**
	 * Removes the kaleo notification with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoNotificationId the primary key of the kaleo notification
	 * @return the kaleo notification that was removed
	 * @throws NoSuchNotificationException if a kaleo notification with the primary key could not be found
	 */
	@Override
	public KaleoNotification remove(long kaleoNotificationId)
		throws NoSuchNotificationException {

		return remove((Serializable)kaleoNotificationId);
	}

	@Override
	protected KaleoNotification removeImpl(
		KaleoNotification kaleoNotification) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoNotification)) {
				kaleoNotification = (KaleoNotification)session.get(
					KaleoNotificationImpl.class,
					kaleoNotification.getPrimaryKeyObj());
			}

			if ((kaleoNotification != null) &&
				ctPersistenceHelper.isRemove(kaleoNotification)) {

				session.delete(kaleoNotification);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoNotification != null) {
			clearCache(kaleoNotification);
		}

		return kaleoNotification;
	}

	@Override
	public KaleoNotification updateImpl(KaleoNotification kaleoNotification) {
		boolean isNew = kaleoNotification.isNew();

		if (!(kaleoNotification instanceof KaleoNotificationModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoNotification.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoNotification);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoNotification proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoNotification implementation " +
					kaleoNotification.getClass());
		}

		KaleoNotificationModelImpl kaleoNotificationModelImpl =
			(KaleoNotificationModelImpl)kaleoNotification;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoNotification.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoNotification.setCreateDate(date);
			}
			else {
				kaleoNotification.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoNotificationModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoNotification.setModifiedDate(date);
			}
			else {
				kaleoNotification.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoNotification)) {
				if (!isNew) {
					session.evict(
						KaleoNotificationImpl.class,
						kaleoNotification.getPrimaryKeyObj());
				}

				session.save(kaleoNotification);
			}
			else {
				kaleoNotification = (KaleoNotification)session.merge(
					kaleoNotification);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoNotification, false);

		if (isNew) {
			kaleoNotification.setNew(false);
		}

		kaleoNotification.resetOriginalValues();

		return kaleoNotification;
	}

	/**
	 * Returns the kaleo notification with the primary key or throws a <code>NoSuchNotificationException</code> if it could not be found.
	 *
	 * @param kaleoNotificationId the primary key of the kaleo notification
	 * @return the kaleo notification
	 * @throws NoSuchNotificationException if a kaleo notification with the primary key could not be found
	 */
	@Override
	public KaleoNotification findByPrimaryKey(long kaleoNotificationId)
		throws NoSuchNotificationException {

		return findByPrimaryKey((Serializable)kaleoNotificationId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo notification with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoNotificationId the primary key of the kaleo notification
	 * @return the kaleo notification, or <code>null</code> if a kaleo notification with the primary key could not be found
	 */
	@Override
	public KaleoNotification fetchByPrimaryKey(long kaleoNotificationId) {
		return fetchByPrimaryKey((Serializable)kaleoNotificationId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoNotificationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEONOTIFICATION;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return KaleoNotificationModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoNotification";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("kaleoClassName");
		ctMergeColumnNames.add("kaleoClassPK");
		ctMergeColumnNames.add("kaleoDefinitionId");
		ctMergeColumnNames.add("kaleoDefinitionVersionId");
		ctMergeColumnNames.add("kaleoNodeName");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("executionType");
		ctMergeColumnNames.add("template");
		ctMergeColumnNames.add("templateLanguage");
		ctMergeColumnNames.add("notificationTypes");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("kaleoNotificationId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the kaleo notification persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_KALEONOTIFICATION_WHERE,
				_SQL_COUNT_KALEONOTIFICATION_WHERE,
				KaleoNotificationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"kaleoNotification.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, KaleoNotification::getCompanyId));

		_collectionPersistenceFinderByKaleoDefinitionVersionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByKaleoDefinitionVersionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoDefinitionVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoDefinitionVersionId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoDefinitionVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoDefinitionVersionId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoDefinitionVersionId"}, false),
				_SQL_SELECT_KALEONOTIFICATION_WHERE,
				_SQL_COUNT_KALEONOTIFICATION_WHERE,
				KaleoNotificationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"kaleoNotification.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoNotification::getKaleoDefinitionVersionId));

		_collectionPersistenceFinderByKCN_KCPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKCN_KCPK",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoClassName", "kaleoClassPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByKCN_KCPK",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"kaleoClassName", "kaleoClassPK"}, 0, 1, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKCN_KCPK",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"kaleoClassName", "kaleoClassPK"}, 0, 1,
					false, null),
				_SQL_SELECT_KALEONOTIFICATION_WHERE,
				_SQL_COUNT_KALEONOTIFICATION_WHERE,
				KaleoNotificationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"kaleoNotification.", "kaleoClassName",
					FinderColumn.Type.STRING, "=", true, true,
					KaleoNotification::getKaleoClassName),
				new FinderColumn<>(
					"kaleoNotification.", "kaleoClassPK",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoNotification::getKaleoClassPK));

		_collectionPersistenceFinderByKCN_KDVI =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKCN_KDVI",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoClassName", "kaleoDefinitionVersionId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByKCN_KDVI",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"kaleoClassName", "kaleoDefinitionVersionId"},
					0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKCN_KDVI",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"kaleoClassName", "kaleoDefinitionVersionId"},
					0, 1, false, null),
				_SQL_SELECT_KALEONOTIFICATION_WHERE,
				_SQL_COUNT_KALEONOTIFICATION_WHERE,
				KaleoNotificationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"kaleoNotification.", "kaleoClassName",
					FinderColumn.Type.STRING, "=", true, true,
					KaleoNotification::getKaleoClassName),
				new FinderColumn<>(
					"kaleoNotification.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoNotification::getKaleoDefinitionVersionId));

		KaleoNotificationUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoNotificationUtil.setPersistence(null);

		entityCache.removeCache(KaleoNotificationImpl.class.getName());
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		KaleoNotificationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEONOTIFICATION =
		"SELECT kaleoNotification FROM KaleoNotification kaleoNotification";

	private static final String _SQL_SELECT_KALEONOTIFICATION_WHERE =
		"SELECT kaleoNotification FROM KaleoNotification kaleoNotification WHERE ";

	private static final String _SQL_COUNT_KALEONOTIFICATION_WHERE =
		"SELECT COUNT(kaleoNotification) FROM KaleoNotification kaleoNotification WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KaleoNotification exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-885160823