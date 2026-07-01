/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.storage.service.persistence.impl;

import com.liferay.analytics.message.storage.exception.NoSuchDeleteMessageException;
import com.liferay.analytics.message.storage.model.AnalyticsDeleteMessage;
import com.liferay.analytics.message.storage.model.AnalyticsDeleteMessageTable;
import com.liferay.analytics.message.storage.model.impl.AnalyticsDeleteMessageImpl;
import com.liferay.analytics.message.storage.model.impl.AnalyticsDeleteMessageModelImpl;
import com.liferay.analytics.message.storage.service.persistence.AnalyticsDeleteMessagePersistence;
import com.liferay.analytics.message.storage.service.persistence.AnalyticsDeleteMessageUtil;
import com.liferay.analytics.message.storage.service.persistence.impl.constants.AnalyticsPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
 * The persistence implementation for the analytics delete message service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AnalyticsDeleteMessagePersistence.class)
public class AnalyticsDeleteMessagePersistenceImpl
	extends BasePersistenceImpl
		<AnalyticsDeleteMessage, NoSuchDeleteMessageException>
	implements AnalyticsDeleteMessagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AnalyticsDeleteMessageUtil</code> to access the analytics delete message persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AnalyticsDeleteMessageImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AnalyticsDeleteMessage, NoSuchDeleteMessageException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the analytics delete messages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsDeleteMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of analytics delete messages
	 * @param end the upper bound of the range of analytics delete messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching analytics delete messages
	 */
	@Override
	public List<AnalyticsDeleteMessage> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AnalyticsDeleteMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first analytics delete message in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics delete message
	 * @throws NoSuchDeleteMessageException if a matching analytics delete message could not be found
	 */
	@Override
	public AnalyticsDeleteMessage findByCompanyId_First(
			long companyId,
			OrderByComparator<AnalyticsDeleteMessage> orderByComparator)
		throws NoSuchDeleteMessageException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first analytics delete message in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics delete message, or <code>null</code> if a matching analytics delete message could not be found
	 */
	@Override
	public AnalyticsDeleteMessage fetchByCompanyId_First(
		long companyId,
		OrderByComparator<AnalyticsDeleteMessage> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the analytics delete messages where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of analytics delete messages where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching analytics delete messages
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<AnalyticsDeleteMessage, NoSuchDeleteMessageException>
			_collectionPersistenceFinderByC_GtM;

	/**
	 * Returns all the analytics delete messages where companyId = &#63; and modifiedDate &gt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @return the matching analytics delete messages
	 */
	@Override
	public List<AnalyticsDeleteMessage> findByC_GtM(
		long companyId, Date modifiedDate) {

		return findByC_GtM(
			companyId, modifiedDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the analytics delete messages where companyId = &#63; and modifiedDate &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsDeleteMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of analytics delete messages
	 * @param end the upper bound of the range of analytics delete messages (not inclusive)
	 * @return the range of matching analytics delete messages
	 */
	@Override
	public List<AnalyticsDeleteMessage> findByC_GtM(
		long companyId, Date modifiedDate, int start, int end) {

		return findByC_GtM(companyId, modifiedDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the analytics delete messages where companyId = &#63; and modifiedDate &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsDeleteMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of analytics delete messages
	 * @param end the upper bound of the range of analytics delete messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching analytics delete messages
	 */
	@Override
	public List<AnalyticsDeleteMessage> findByC_GtM(
		long companyId, Date modifiedDate, int start, int end,
		OrderByComparator<AnalyticsDeleteMessage> orderByComparator) {

		return findByC_GtM(
			companyId, modifiedDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the analytics delete messages where companyId = &#63; and modifiedDate &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsDeleteMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of analytics delete messages
	 * @param end the upper bound of the range of analytics delete messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching analytics delete messages
	 */
	@Override
	public List<AnalyticsDeleteMessage> findByC_GtM(
		long companyId, Date modifiedDate, int start, int end,
		OrderByComparator<AnalyticsDeleteMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_GtM.find(
			finderCache, new Object[] {companyId, modifiedDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first analytics delete message in the ordered set where companyId = &#63; and modifiedDate &gt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics delete message
	 * @throws NoSuchDeleteMessageException if a matching analytics delete message could not be found
	 */
	@Override
	public AnalyticsDeleteMessage findByC_GtM_First(
			long companyId, Date modifiedDate,
			OrderByComparator<AnalyticsDeleteMessage> orderByComparator)
		throws NoSuchDeleteMessageException {

		return _collectionPersistenceFinderByC_GtM.findFirst(
			finderCache, new Object[] {companyId, modifiedDate},
			orderByComparator);
	}

	/**
	 * Returns the first analytics delete message in the ordered set where companyId = &#63; and modifiedDate &gt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics delete message, or <code>null</code> if a matching analytics delete message could not be found
	 */
	@Override
	public AnalyticsDeleteMessage fetchByC_GtM_First(
		long companyId, Date modifiedDate,
		OrderByComparator<AnalyticsDeleteMessage> orderByComparator) {

		return _collectionPersistenceFinderByC_GtM.fetchFirst(
			finderCache, new Object[] {companyId, modifiedDate},
			orderByComparator);
	}

	/**
	 * Removes all the analytics delete messages where companyId = &#63; and modifiedDate &gt; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 */
	@Override
	public void removeByC_GtM(long companyId, Date modifiedDate) {
		_collectionPersistenceFinderByC_GtM.remove(
			finderCache, new Object[] {companyId, modifiedDate});
	}

	/**
	 * Returns the number of analytics delete messages where companyId = &#63; and modifiedDate &gt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @return the number of matching analytics delete messages
	 */
	@Override
	public int countByC_GtM(long companyId, Date modifiedDate) {
		return _collectionPersistenceFinderByC_GtM.count(
			finderCache, new Object[] {companyId, modifiedDate});
	}

	private CollectionPersistenceFinder
		<AnalyticsDeleteMessage, NoSuchDeleteMessageException>
			_collectionPersistenceFinderByC_LtM;

	/**
	 * Returns all the analytics delete messages where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @return the matching analytics delete messages
	 */
	@Override
	public List<AnalyticsDeleteMessage> findByC_LtM(
		long companyId, Date modifiedDate) {

		return findByC_LtM(
			companyId, modifiedDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the analytics delete messages where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsDeleteMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of analytics delete messages
	 * @param end the upper bound of the range of analytics delete messages (not inclusive)
	 * @return the range of matching analytics delete messages
	 */
	@Override
	public List<AnalyticsDeleteMessage> findByC_LtM(
		long companyId, Date modifiedDate, int start, int end) {

		return findByC_LtM(companyId, modifiedDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the analytics delete messages where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsDeleteMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of analytics delete messages
	 * @param end the upper bound of the range of analytics delete messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching analytics delete messages
	 */
	@Override
	public List<AnalyticsDeleteMessage> findByC_LtM(
		long companyId, Date modifiedDate, int start, int end,
		OrderByComparator<AnalyticsDeleteMessage> orderByComparator) {

		return findByC_LtM(
			companyId, modifiedDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the analytics delete messages where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsDeleteMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of analytics delete messages
	 * @param end the upper bound of the range of analytics delete messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching analytics delete messages
	 */
	@Override
	public List<AnalyticsDeleteMessage> findByC_LtM(
		long companyId, Date modifiedDate, int start, int end,
		OrderByComparator<AnalyticsDeleteMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_LtM.find(
			finderCache, new Object[] {companyId, modifiedDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first analytics delete message in the ordered set where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics delete message
	 * @throws NoSuchDeleteMessageException if a matching analytics delete message could not be found
	 */
	@Override
	public AnalyticsDeleteMessage findByC_LtM_First(
			long companyId, Date modifiedDate,
			OrderByComparator<AnalyticsDeleteMessage> orderByComparator)
		throws NoSuchDeleteMessageException {

		return _collectionPersistenceFinderByC_LtM.findFirst(
			finderCache, new Object[] {companyId, modifiedDate},
			orderByComparator);
	}

	/**
	 * Returns the first analytics delete message in the ordered set where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics delete message, or <code>null</code> if a matching analytics delete message could not be found
	 */
	@Override
	public AnalyticsDeleteMessage fetchByC_LtM_First(
		long companyId, Date modifiedDate,
		OrderByComparator<AnalyticsDeleteMessage> orderByComparator) {

		return _collectionPersistenceFinderByC_LtM.fetchFirst(
			finderCache, new Object[] {companyId, modifiedDate},
			orderByComparator);
	}

	/**
	 * Removes all the analytics delete messages where companyId = &#63; and modifiedDate &lt; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 */
	@Override
	public void removeByC_LtM(long companyId, Date modifiedDate) {
		_collectionPersistenceFinderByC_LtM.remove(
			finderCache, new Object[] {companyId, modifiedDate});
	}

	/**
	 * Returns the number of analytics delete messages where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @return the number of matching analytics delete messages
	 */
	@Override
	public int countByC_LtM(long companyId, Date modifiedDate) {
		return _collectionPersistenceFinderByC_LtM.count(
			finderCache, new Object[] {companyId, modifiedDate});
	}

	public AnalyticsDeleteMessagePersistenceImpl() {
		setModelClass(AnalyticsDeleteMessage.class);

		setModelImplClass(AnalyticsDeleteMessageImpl.class);
		setModelPKClass(long.class);

		setTable(AnalyticsDeleteMessageTable.INSTANCE);
	}

	/**
	 * Creates a new analytics delete message with the primary key. Does not add the analytics delete message to the database.
	 *
	 * @param analyticsDeleteMessageId the primary key for the new analytics delete message
	 * @return the new analytics delete message
	 */
	@Override
	public AnalyticsDeleteMessage create(long analyticsDeleteMessageId) {
		AnalyticsDeleteMessage analyticsDeleteMessage =
			new AnalyticsDeleteMessageImpl();

		analyticsDeleteMessage.setNew(true);
		analyticsDeleteMessage.setPrimaryKey(analyticsDeleteMessageId);

		analyticsDeleteMessage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return analyticsDeleteMessage;
	}

	/**
	 * Removes the analytics delete message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param analyticsDeleteMessageId the primary key of the analytics delete message
	 * @return the analytics delete message that was removed
	 * @throws NoSuchDeleteMessageException if a analytics delete message with the primary key could not be found
	 */
	@Override
	public AnalyticsDeleteMessage remove(long analyticsDeleteMessageId)
		throws NoSuchDeleteMessageException {

		return remove((Serializable)analyticsDeleteMessageId);
	}

	@Override
	protected AnalyticsDeleteMessage removeImpl(
		AnalyticsDeleteMessage analyticsDeleteMessage) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(analyticsDeleteMessage)) {
				analyticsDeleteMessage = (AnalyticsDeleteMessage)session.get(
					AnalyticsDeleteMessageImpl.class,
					analyticsDeleteMessage.getPrimaryKeyObj());
			}

			if ((analyticsDeleteMessage != null) &&
				ctPersistenceHelper.isRemove(analyticsDeleteMessage)) {

				session.delete(analyticsDeleteMessage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (analyticsDeleteMessage != null) {
			clearCache(analyticsDeleteMessage);
		}

		return analyticsDeleteMessage;
	}

	@Override
	public AnalyticsDeleteMessage updateImpl(
		AnalyticsDeleteMessage analyticsDeleteMessage) {

		boolean isNew = analyticsDeleteMessage.isNew();

		if (!(analyticsDeleteMessage instanceof
				AnalyticsDeleteMessageModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(analyticsDeleteMessage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					analyticsDeleteMessage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in analyticsDeleteMessage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AnalyticsDeleteMessage implementation " +
					analyticsDeleteMessage.getClass());
		}

		AnalyticsDeleteMessageModelImpl analyticsDeleteMessageModelImpl =
			(AnalyticsDeleteMessageModelImpl)analyticsDeleteMessage;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (analyticsDeleteMessage.getCreateDate() == null)) {
			if (serviceContext == null) {
				analyticsDeleteMessage.setCreateDate(date);
			}
			else {
				analyticsDeleteMessage.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!analyticsDeleteMessageModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				analyticsDeleteMessage.setModifiedDate(date);
			}
			else {
				analyticsDeleteMessage.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(analyticsDeleteMessage)) {
				if (!isNew) {
					session.evict(
						AnalyticsDeleteMessageImpl.class,
						analyticsDeleteMessage.getPrimaryKeyObj());
				}

				session.save(analyticsDeleteMessage);
			}
			else {
				analyticsDeleteMessage = (AnalyticsDeleteMessage)session.merge(
					analyticsDeleteMessage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(analyticsDeleteMessage, false);

		if (isNew) {
			analyticsDeleteMessage.setNew(false);
		}

		analyticsDeleteMessage.resetOriginalValues();

		return analyticsDeleteMessage;
	}

	/**
	 * Returns the analytics delete message with the primary key or throws a <code>NoSuchDeleteMessageException</code> if it could not be found.
	 *
	 * @param analyticsDeleteMessageId the primary key of the analytics delete message
	 * @return the analytics delete message
	 * @throws NoSuchDeleteMessageException if a analytics delete message with the primary key could not be found
	 */
	@Override
	public AnalyticsDeleteMessage findByPrimaryKey(
			long analyticsDeleteMessageId)
		throws NoSuchDeleteMessageException {

		return findByPrimaryKey((Serializable)analyticsDeleteMessageId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the analytics delete message with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param analyticsDeleteMessageId the primary key of the analytics delete message
	 * @return the analytics delete message, or <code>null</code> if a analytics delete message with the primary key could not be found
	 */
	@Override
	public AnalyticsDeleteMessage fetchByPrimaryKey(
		long analyticsDeleteMessageId) {

		return fetchByPrimaryKey((Serializable)analyticsDeleteMessageId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "analyticsDeleteMessageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ANALYTICSDELETEMESSAGE;
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
		return AnalyticsDeleteMessageModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AnalyticsDeleteMessage";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("className");
		ctStrictColumnNames.add("classPK");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("analyticsDeleteMessageId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the analytics delete message persistence.
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
				_SQL_SELECT_ANALYTICSDELETEMESSAGE_WHERE,
				_SQL_COUNT_ANALYTICSDELETEMESSAGE_WHERE,
				AnalyticsDeleteMessageModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"analyticsDeleteMessage.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					AnalyticsDeleteMessage::getCompanyId));

		_collectionPersistenceFinderByC_GtM = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_GtM",
				new String[] {
					Long.class.getName(), Date.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "modifiedDate"}, true),
			null,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_GtM",
				new String[] {Long.class.getName(), Date.class.getName()},
				new String[] {"companyId", "modifiedDate"}, false),
			_SQL_SELECT_ANALYTICSDELETEMESSAGE_WHERE,
			_SQL_COUNT_ANALYTICSDELETEMESSAGE_WHERE,
			AnalyticsDeleteMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"analyticsDeleteMessage.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, AnalyticsDeleteMessage::getCompanyId),
			new FinderColumn<>(
				"analyticsDeleteMessage.", "modifiedDate",
				FinderColumn.Type.DATE, ">", true, true,
				AnalyticsDeleteMessage::getModifiedDate));

		_collectionPersistenceFinderByC_LtM = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LtM",
				new String[] {
					Long.class.getName(), Date.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "modifiedDate"}, true),
			null,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LtM",
				new String[] {Long.class.getName(), Date.class.getName()},
				new String[] {"companyId", "modifiedDate"}, false),
			_SQL_SELECT_ANALYTICSDELETEMESSAGE_WHERE,
			_SQL_COUNT_ANALYTICSDELETEMESSAGE_WHERE,
			AnalyticsDeleteMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"analyticsDeleteMessage.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, AnalyticsDeleteMessage::getCompanyId),
			new FinderColumn<>(
				"analyticsDeleteMessage.", "modifiedDate",
				FinderColumn.Type.DATE, "<", true, true,
				AnalyticsDeleteMessage::getModifiedDate));

		AnalyticsDeleteMessageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AnalyticsDeleteMessageUtil.setPersistence(null);

		entityCache.removeCache(AnalyticsDeleteMessageImpl.class.getName());
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
		AnalyticsDeleteMessageModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ANALYTICSDELETEMESSAGE =
		"SELECT analyticsDeleteMessage FROM AnalyticsDeleteMessage analyticsDeleteMessage";

	private static final String _SQL_SELECT_ANALYTICSDELETEMESSAGE_WHERE =
		"SELECT analyticsDeleteMessage FROM AnalyticsDeleteMessage analyticsDeleteMessage WHERE ";

	private static final String _SQL_COUNT_ANALYTICSDELETEMESSAGE_WHERE =
		"SELECT COUNT(analyticsDeleteMessage) FROM AnalyticsDeleteMessage analyticsDeleteMessage WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-88108649