/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.storage.service.persistence.impl;

import com.liferay.analytics.message.storage.exception.NoSuchMessageException;
import com.liferay.analytics.message.storage.model.AnalyticsMessage;
import com.liferay.analytics.message.storage.model.AnalyticsMessageTable;
import com.liferay.analytics.message.storage.model.impl.AnalyticsMessageImpl;
import com.liferay.analytics.message.storage.model.impl.AnalyticsMessageModelImpl;
import com.liferay.analytics.message.storage.service.persistence.AnalyticsMessagePersistence;
import com.liferay.analytics.message.storage.service.persistence.AnalyticsMessageUtil;
import com.liferay.analytics.message.storage.service.persistence.impl.constants.AnalyticsPersistenceConstants;
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
 * The persistence implementation for the analytics message service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AnalyticsMessagePersistence.class)
public class AnalyticsMessagePersistenceImpl
	extends BasePersistenceImpl<AnalyticsMessage, NoSuchMessageException>
	implements AnalyticsMessagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AnalyticsMessageUtil</code> to access the analytics message persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AnalyticsMessageImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AnalyticsMessage, NoSuchMessageException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the analytics messages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of analytics messages
	 * @param end the upper bound of the range of analytics messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching analytics messages
	 */
	@Override
	public List<AnalyticsMessage> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AnalyticsMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first analytics message in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics message
	 * @throws NoSuchMessageException if a matching analytics message could not be found
	 */
	@Override
	public AnalyticsMessage findByCompanyId_First(
			long companyId,
			OrderByComparator<AnalyticsMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first analytics message in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics message, or <code>null</code> if a matching analytics message could not be found
	 */
	@Override
	public AnalyticsMessage fetchByCompanyId_First(
		long companyId, OrderByComparator<AnalyticsMessage> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the analytics messages where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of analytics messages where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching analytics messages
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	public AnalyticsMessagePersistenceImpl() {
		setModelClass(AnalyticsMessage.class);

		setModelImplClass(AnalyticsMessageImpl.class);
		setModelPKClass(long.class);

		setTable(AnalyticsMessageTable.INSTANCE);
	}

	/**
	 * Creates a new analytics message with the primary key. Does not add the analytics message to the database.
	 *
	 * @param analyticsMessageId the primary key for the new analytics message
	 * @return the new analytics message
	 */
	@Override
	public AnalyticsMessage create(long analyticsMessageId) {
		AnalyticsMessage analyticsMessage = new AnalyticsMessageImpl();

		analyticsMessage.setNew(true);
		analyticsMessage.setPrimaryKey(analyticsMessageId);

		analyticsMessage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return analyticsMessage;
	}

	/**
	 * Removes the analytics message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param analyticsMessageId the primary key of the analytics message
	 * @return the analytics message that was removed
	 * @throws NoSuchMessageException if a analytics message with the primary key could not be found
	 */
	@Override
	public AnalyticsMessage remove(long analyticsMessageId)
		throws NoSuchMessageException {

		return remove((Serializable)analyticsMessageId);
	}

	@Override
	protected AnalyticsMessage removeImpl(AnalyticsMessage analyticsMessage) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(analyticsMessage)) {
				analyticsMessage = (AnalyticsMessage)session.get(
					AnalyticsMessageImpl.class,
					analyticsMessage.getPrimaryKeyObj());
			}

			if ((analyticsMessage != null) &&
				ctPersistenceHelper.isRemove(analyticsMessage)) {

				session.delete(analyticsMessage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (analyticsMessage != null) {
			clearCache(analyticsMessage);
		}

		return analyticsMessage;
	}

	@Override
	public AnalyticsMessage updateImpl(AnalyticsMessage analyticsMessage) {
		boolean isNew = analyticsMessage.isNew();

		if (!(analyticsMessage instanceof AnalyticsMessageModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(analyticsMessage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					analyticsMessage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in analyticsMessage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AnalyticsMessage implementation " +
					analyticsMessage.getClass());
		}

		AnalyticsMessageModelImpl analyticsMessageModelImpl =
			(AnalyticsMessageModelImpl)analyticsMessage;

		if (isNew && (analyticsMessage.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				analyticsMessage.setCreateDate(date);
			}
			else {
				analyticsMessage.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(analyticsMessage)) {
				if (!isNew) {
					session.evict(
						AnalyticsMessageImpl.class,
						analyticsMessage.getPrimaryKeyObj());
				}

				session.save(analyticsMessage);
			}
			else {
				analyticsMessage = (AnalyticsMessage)session.merge(
					analyticsMessage);
			}

			session.flush();
			session.clear();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(analyticsMessage, false);

		if (isNew) {
			analyticsMessage.setNew(false);
		}

		analyticsMessage.resetOriginalValues();

		return analyticsMessage;
	}

	/**
	 * Returns the analytics message with the primary key or throws a <code>NoSuchMessageException</code> if it could not be found.
	 *
	 * @param analyticsMessageId the primary key of the analytics message
	 * @return the analytics message
	 * @throws NoSuchMessageException if a analytics message with the primary key could not be found
	 */
	@Override
	public AnalyticsMessage findByPrimaryKey(long analyticsMessageId)
		throws NoSuchMessageException {

		return findByPrimaryKey((Serializable)analyticsMessageId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the analytics message with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param analyticsMessageId the primary key of the analytics message
	 * @return the analytics message, or <code>null</code> if a analytics message with the primary key could not be found
	 */
	@Override
	public AnalyticsMessage fetchByPrimaryKey(long analyticsMessageId) {
		return fetchByPrimaryKey((Serializable)analyticsMessageId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "analyticsMessageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ANALYTICSMESSAGE;
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
		return AnalyticsMessageModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AnalyticsMessage";
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
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctMergeColumnNames.add("body");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("analyticsMessageId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the analytics message persistence.
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
				_SQL_SELECT_ANALYTICSMESSAGE_WHERE,
				_SQL_COUNT_ANALYTICSMESSAGE_WHERE,
				AnalyticsMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"analyticsMessage.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, AnalyticsMessage::getCompanyId));

		AnalyticsMessageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AnalyticsMessageUtil.setPersistence(null);

		entityCache.removeCache(AnalyticsMessageImpl.class.getName());
	}

	@Override
	@Reference(
		target = AnalyticsPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = AnalyticsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = AnalyticsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		AnalyticsMessageModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ANALYTICSMESSAGE =
		"SELECT analyticsMessage FROM AnalyticsMessage analyticsMessage";

	private static final String _SQL_SELECT_ANALYTICSMESSAGE_WHERE =
		"SELECT analyticsMessage FROM AnalyticsMessage analyticsMessage WHERE ";

	private static final String _SQL_COUNT_ANALYTICSMESSAGE_WHERE =
		"SELECT COUNT(analyticsMessage) FROM AnalyticsMessage analyticsMessage WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1611696876