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
import com.liferay.portal.workflow.kaleo.exception.NoSuchNotificationRecipientException;
import com.liferay.portal.workflow.kaleo.model.KaleoNotificationRecipient;
import com.liferay.portal.workflow.kaleo.model.KaleoNotificationRecipientTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoNotificationRecipientImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoNotificationRecipientModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNotificationRecipientPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNotificationRecipientUtil;
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
 * The persistence implementation for the kaleo notification recipient service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoNotificationRecipientPersistence.class)
public class KaleoNotificationRecipientPersistenceImpl
	extends BasePersistenceImpl
		<KaleoNotificationRecipient, NoSuchNotificationRecipientException>
	implements KaleoNotificationRecipientPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoNotificationRecipientUtil</code> to access the kaleo notification recipient persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoNotificationRecipientImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<KaleoNotificationRecipient, NoSuchNotificationRecipientException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo notification recipients where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoNotificationRecipientModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo notification recipients
	 * @param end the upper bound of the range of kaleo notification recipients (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo notification recipients
	 */
	@Override
	public List<KaleoNotificationRecipient> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoNotificationRecipient> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo notification recipient in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo notification recipient
	 * @throws NoSuchNotificationRecipientException if a matching kaleo notification recipient could not be found
	 */
	@Override
	public KaleoNotificationRecipient findByCompanyId_First(
			long companyId,
			OrderByComparator<KaleoNotificationRecipient> orderByComparator)
		throws NoSuchNotificationRecipientException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo notification recipient in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo notification recipient, or <code>null</code> if a matching kaleo notification recipient could not be found
	 */
	@Override
	public KaleoNotificationRecipient fetchByCompanyId_First(
		long companyId,
		OrderByComparator<KaleoNotificationRecipient> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo notification recipients where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo notification recipients where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo notification recipients
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<KaleoNotificationRecipient, NoSuchNotificationRecipientException>
			_collectionPersistenceFinderByKaleoDefinitionVersionId;

	/**
	 * Returns an ordered range of all the kaleo notification recipients where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoNotificationRecipientModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo notification recipients
	 * @param end the upper bound of the range of kaleo notification recipients (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo notification recipients
	 */
	@Override
	public List<KaleoNotificationRecipient> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoNotificationRecipient> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.find(
			finderCache, new Object[] {kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo notification recipient in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo notification recipient
	 * @throws NoSuchNotificationRecipientException if a matching kaleo notification recipient could not be found
	 */
	@Override
	public KaleoNotificationRecipient findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoNotificationRecipient> orderByComparator)
		throws NoSuchNotificationRecipientException {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.findFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo notification recipient in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo notification recipient, or <code>null</code> if a matching kaleo notification recipient could not be found
	 */
	@Override
	public KaleoNotificationRecipient fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoNotificationRecipient> orderByComparator) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.
			fetchFirst(
				finderCache, new Object[] {kaleoDefinitionVersionId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo notification recipients where kaleoDefinitionVersionId = &#63; from the database.
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
	 * Returns the number of kaleo notification recipients where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo notification recipients
	 */
	@Override
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {
		return _collectionPersistenceFinderByKaleoDefinitionVersionId.count(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	private CollectionPersistenceFinder
		<KaleoNotificationRecipient, NoSuchNotificationRecipientException>
			_collectionPersistenceFinderByKaleoNotificationId;

	/**
	 * Returns an ordered range of all the kaleo notification recipients where kaleoNotificationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoNotificationRecipientModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoNotificationId the kaleo notification ID
	 * @param start the lower bound of the range of kaleo notification recipients
	 * @param end the upper bound of the range of kaleo notification recipients (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo notification recipients
	 */
	@Override
	public List<KaleoNotificationRecipient> findByKaleoNotificationId(
		long kaleoNotificationId, int start, int end,
		OrderByComparator<KaleoNotificationRecipient> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoNotificationId.find(
			finderCache, new Object[] {kaleoNotificationId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo notification recipient in the ordered set where kaleoNotificationId = &#63;.
	 *
	 * @param kaleoNotificationId the kaleo notification ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo notification recipient
	 * @throws NoSuchNotificationRecipientException if a matching kaleo notification recipient could not be found
	 */
	@Override
	public KaleoNotificationRecipient findByKaleoNotificationId_First(
			long kaleoNotificationId,
			OrderByComparator<KaleoNotificationRecipient> orderByComparator)
		throws NoSuchNotificationRecipientException {

		return _collectionPersistenceFinderByKaleoNotificationId.findFirst(
			finderCache, new Object[] {kaleoNotificationId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo notification recipient in the ordered set where kaleoNotificationId = &#63;.
	 *
	 * @param kaleoNotificationId the kaleo notification ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo notification recipient, or <code>null</code> if a matching kaleo notification recipient could not be found
	 */
	@Override
	public KaleoNotificationRecipient fetchByKaleoNotificationId_First(
		long kaleoNotificationId,
		OrderByComparator<KaleoNotificationRecipient> orderByComparator) {

		return _collectionPersistenceFinderByKaleoNotificationId.fetchFirst(
			finderCache, new Object[] {kaleoNotificationId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo notification recipients where kaleoNotificationId = &#63; from the database.
	 *
	 * @param kaleoNotificationId the kaleo notification ID
	 */
	@Override
	public void removeByKaleoNotificationId(long kaleoNotificationId) {
		_collectionPersistenceFinderByKaleoNotificationId.remove(
			finderCache, new Object[] {kaleoNotificationId});
	}

	/**
	 * Returns the number of kaleo notification recipients where kaleoNotificationId = &#63;.
	 *
	 * @param kaleoNotificationId the kaleo notification ID
	 * @return the number of matching kaleo notification recipients
	 */
	@Override
	public int countByKaleoNotificationId(long kaleoNotificationId) {
		return _collectionPersistenceFinderByKaleoNotificationId.count(
			finderCache, new Object[] {kaleoNotificationId});
	}

	public KaleoNotificationRecipientPersistenceImpl() {
		setModelClass(KaleoNotificationRecipient.class);

		setModelImplClass(KaleoNotificationRecipientImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoNotificationRecipientTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo notification recipient with the primary key. Does not add the kaleo notification recipient to the database.
	 *
	 * @param kaleoNotificationRecipientId the primary key for the new kaleo notification recipient
	 * @return the new kaleo notification recipient
	 */
	@Override
	public KaleoNotificationRecipient create(
		long kaleoNotificationRecipientId) {

		KaleoNotificationRecipient kaleoNotificationRecipient =
			new KaleoNotificationRecipientImpl();

		kaleoNotificationRecipient.setNew(true);
		kaleoNotificationRecipient.setPrimaryKey(kaleoNotificationRecipientId);

		kaleoNotificationRecipient.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return kaleoNotificationRecipient;
	}

	/**
	 * Removes the kaleo notification recipient with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoNotificationRecipientId the primary key of the kaleo notification recipient
	 * @return the kaleo notification recipient that was removed
	 * @throws NoSuchNotificationRecipientException if a kaleo notification recipient with the primary key could not be found
	 */
	@Override
	public KaleoNotificationRecipient remove(long kaleoNotificationRecipientId)
		throws NoSuchNotificationRecipientException {

		return remove((Serializable)kaleoNotificationRecipientId);
	}

	@Override
	protected KaleoNotificationRecipient removeImpl(
		KaleoNotificationRecipient kaleoNotificationRecipient) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoNotificationRecipient)) {
				kaleoNotificationRecipient =
					(KaleoNotificationRecipient)session.get(
						KaleoNotificationRecipientImpl.class,
						kaleoNotificationRecipient.getPrimaryKeyObj());
			}

			if ((kaleoNotificationRecipient != null) &&
				ctPersistenceHelper.isRemove(kaleoNotificationRecipient)) {

				session.delete(kaleoNotificationRecipient);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoNotificationRecipient != null) {
			clearCache(kaleoNotificationRecipient);
		}

		return kaleoNotificationRecipient;
	}

	@Override
	public KaleoNotificationRecipient updateImpl(
		KaleoNotificationRecipient kaleoNotificationRecipient) {

		boolean isNew = kaleoNotificationRecipient.isNew();

		if (!(kaleoNotificationRecipient instanceof
				KaleoNotificationRecipientModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoNotificationRecipient.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoNotificationRecipient);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoNotificationRecipient proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoNotificationRecipient implementation " +
					kaleoNotificationRecipient.getClass());
		}

		KaleoNotificationRecipientModelImpl
			kaleoNotificationRecipientModelImpl =
				(KaleoNotificationRecipientModelImpl)kaleoNotificationRecipient;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoNotificationRecipient.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoNotificationRecipient.setCreateDate(date);
			}
			else {
				kaleoNotificationRecipient.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoNotificationRecipientModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoNotificationRecipient.setModifiedDate(date);
			}
			else {
				kaleoNotificationRecipient.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoNotificationRecipient)) {
				if (!isNew) {
					session.evict(
						KaleoNotificationRecipientImpl.class,
						kaleoNotificationRecipient.getPrimaryKeyObj());
				}

				session.save(kaleoNotificationRecipient);
			}
			else {
				kaleoNotificationRecipient =
					(KaleoNotificationRecipient)session.merge(
						kaleoNotificationRecipient);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoNotificationRecipient, false);

		if (isNew) {
			kaleoNotificationRecipient.setNew(false);
		}

		kaleoNotificationRecipient.resetOriginalValues();

		return kaleoNotificationRecipient;
	}

	/**
	 * Returns the kaleo notification recipient with the primary key or throws a <code>NoSuchNotificationRecipientException</code> if it could not be found.
	 *
	 * @param kaleoNotificationRecipientId the primary key of the kaleo notification recipient
	 * @return the kaleo notification recipient
	 * @throws NoSuchNotificationRecipientException if a kaleo notification recipient with the primary key could not be found
	 */
	@Override
	public KaleoNotificationRecipient findByPrimaryKey(
			long kaleoNotificationRecipientId)
		throws NoSuchNotificationRecipientException {

		return findByPrimaryKey((Serializable)kaleoNotificationRecipientId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo notification recipient with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoNotificationRecipientId the primary key of the kaleo notification recipient
	 * @return the kaleo notification recipient, or <code>null</code> if a kaleo notification recipient with the primary key could not be found
	 */
	@Override
	public KaleoNotificationRecipient fetchByPrimaryKey(
		long kaleoNotificationRecipientId) {

		return fetchByPrimaryKey((Serializable)kaleoNotificationRecipientId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoNotificationRecipientId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEONOTIFICATIONRECIPIENT;
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
		return KaleoNotificationRecipientModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoNotificationRecipient";
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
		ctMergeColumnNames.add("kaleoDefinitionId");
		ctMergeColumnNames.add("kaleoDefinitionVersionId");
		ctMergeColumnNames.add("kaleoNotificationId");
		ctMergeColumnNames.add("recipientClassName");
		ctMergeColumnNames.add("recipientClassPK");
		ctMergeColumnNames.add("recipientRoleType");
		ctMergeColumnNames.add("recipientScript");
		ctMergeColumnNames.add("recipientScriptLanguage");
		ctMergeColumnNames.add("recipientScriptContexts");
		ctMergeColumnNames.add("address");
		ctMergeColumnNames.add("notificationReceptionType");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("kaleoNotificationRecipientId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the kaleo notification recipient persistence.
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
				_SQL_SELECT_KALEONOTIFICATIONRECIPIENT_WHERE,
				_SQL_COUNT_KALEONOTIFICATIONRECIPIENT_WHERE,
				KaleoNotificationRecipientModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoNotificationRecipient.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoNotificationRecipient::getCompanyId));

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
				_SQL_SELECT_KALEONOTIFICATIONRECIPIENT_WHERE,
				_SQL_COUNT_KALEONOTIFICATIONRECIPIENT_WHERE,
				KaleoNotificationRecipientModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoNotificationRecipient.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoNotificationRecipient::getKaleoDefinitionVersionId));

		_collectionPersistenceFinderByKaleoNotificationId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByKaleoNotificationId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoNotificationId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoNotificationId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoNotificationId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoNotificationId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoNotificationId"}, false),
				_SQL_SELECT_KALEONOTIFICATIONRECIPIENT_WHERE,
				_SQL_COUNT_KALEONOTIFICATIONRECIPIENT_WHERE,
				KaleoNotificationRecipientModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoNotificationRecipient.", "kaleoNotificationId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoNotificationRecipient::getKaleoNotificationId));

		KaleoNotificationRecipientUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoNotificationRecipientUtil.setPersistence(null);

		entityCache.removeCache(KaleoNotificationRecipientImpl.class.getName());
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
		KaleoNotificationRecipientModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEONOTIFICATIONRECIPIENT =
		"SELECT kaleoNotificationRecipient FROM KaleoNotificationRecipient kaleoNotificationRecipient";

	private static final String _SQL_SELECT_KALEONOTIFICATIONRECIPIENT_WHERE =
		"SELECT kaleoNotificationRecipient FROM KaleoNotificationRecipient kaleoNotificationRecipient WHERE ";

	private static final String _SQL_COUNT_KALEONOTIFICATIONRECIPIENT_WHERE =
		"SELECT COUNT(kaleoNotificationRecipient) FROM KaleoNotificationRecipient kaleoNotificationRecipient WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KaleoNotificationRecipient exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:103849498