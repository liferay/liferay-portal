/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.persistence.impl;

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
import com.liferay.portal.security.audit.storage.exception.NoSuchEventException;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.model.AuditEventTable;
import com.liferay.portal.security.audit.storage.model.impl.AuditEventImpl;
import com.liferay.portal.security.audit.storage.model.impl.AuditEventModelImpl;
import com.liferay.portal.security.audit.storage.service.persistence.AuditEventPersistence;
import com.liferay.portal.security.audit.storage.service.persistence.AuditEventUtil;
import com.liferay.portal.security.audit.storage.service.persistence.impl.constants.AuditPersistenceConstants;

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
 * The persistence implementation for the audit event service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AuditEventPersistence.class)
public class AuditEventPersistenceImpl
	extends BasePersistenceImpl<AuditEvent, NoSuchEventException>
	implements AuditEventPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AuditEventUtil</code> to access the audit event persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AuditEventImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<AuditEvent, NoSuchEventException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the audit events where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AuditEventModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of audit events
	 * @param end the upper bound of the range of audit events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching audit events
	 */
	@Override
	public List<AuditEvent> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AuditEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			dummyFinderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first audit event in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audit event
	 * @throws NoSuchEventException if a matching audit event could not be found
	 */
	@Override
	public AuditEvent findByCompanyId_First(
			long companyId, OrderByComparator<AuditEvent> orderByComparator)
		throws NoSuchEventException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			dummyFinderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first audit event in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audit event, or <code>null</code> if a matching audit event could not be found
	 */
	@Override
	public AuditEvent fetchByCompanyId_First(
		long companyId, OrderByComparator<AuditEvent> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			dummyFinderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the audit events where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			dummyFinderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of audit events where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching audit events
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			dummyFinderCache, new Object[] {companyId});
	}

	public AuditEventPersistenceImpl() {
		setModelClass(AuditEvent.class);

		setModelImplClass(AuditEventImpl.class);
		setModelPKClass(long.class);

		setTable(AuditEventTable.INSTANCE);
	}

	/**
	 * Creates a new audit event with the primary key. Does not add the audit event to the database.
	 *
	 * @param auditEventId the primary key for the new audit event
	 * @return the new audit event
	 */
	@Override
	public AuditEvent create(long auditEventId) {
		AuditEvent auditEvent = new AuditEventImpl();

		auditEvent.setNew(true);
		auditEvent.setPrimaryKey(auditEventId);

		auditEvent.setCompanyId(CompanyThreadLocal.getCompanyId());

		return auditEvent;
	}

	/**
	 * Removes the audit event with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param auditEventId the primary key of the audit event
	 * @return the audit event that was removed
	 * @throws NoSuchEventException if a audit event with the primary key could not be found
	 */
	@Override
	public AuditEvent remove(long auditEventId) throws NoSuchEventException {
		return remove((Serializable)auditEventId);
	}

	@Override
	protected AuditEvent removeImpl(AuditEvent auditEvent) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(auditEvent)) {
				auditEvent = (AuditEvent)session.get(
					AuditEventImpl.class, auditEvent.getPrimaryKeyObj());
			}

			if (auditEvent != null) {
				session.delete(auditEvent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (auditEvent != null) {
			clearCache(auditEvent);
		}

		return auditEvent;
	}

	@Override
	public AuditEvent updateImpl(AuditEvent auditEvent) {
		boolean isNew = auditEvent.isNew();

		if (!(auditEvent instanceof AuditEventModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(auditEvent.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(auditEvent);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in auditEvent proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AuditEvent implementation " +
					auditEvent.getClass());
		}

		AuditEventModelImpl auditEventModelImpl =
			(AuditEventModelImpl)auditEvent;

		if (isNew && (auditEvent.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				auditEvent.setCreateDate(date);
			}
			else {
				auditEvent.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(auditEvent);
			}
			else {
				auditEvent = (AuditEvent)session.merge(auditEvent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(auditEvent, false);

		if (isNew) {
			auditEvent.setNew(false);
		}

		auditEvent.resetOriginalValues();

		return auditEvent;
	}

	/**
	 * Returns the audit event with the primary key or throws a <code>NoSuchEventException</code> if it could not be found.
	 *
	 * @param auditEventId the primary key of the audit event
	 * @return the audit event
	 * @throws NoSuchEventException if a audit event with the primary key could not be found
	 */
	@Override
	public AuditEvent findByPrimaryKey(long auditEventId)
		throws NoSuchEventException {

		return findByPrimaryKey((Serializable)auditEventId);
	}

	/**
	 * Returns the audit event with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param auditEventId the primary key of the audit event
	 * @return the audit event, or <code>null</code> if a audit event with the primary key could not be found
	 */
	@Override
	public AuditEvent fetchByPrimaryKey(long auditEventId) {
		return fetchByPrimaryKey((Serializable)auditEventId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return dummyEntityCache;
	}

	@Override
	protected String getPKDBName() {
		return "auditEventId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_AUDITEVENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return AuditEventModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the audit event persistence.
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
				_SQL_SELECT_AUDITEVENT_WHERE, _SQL_COUNT_AUDITEVENT_WHERE,
				AuditEventModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"auditEvent.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, AuditEvent::getCompanyId));

		AuditEventUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AuditEventUtil.setPersistence(null);

		dummyEntityCache.removeCache(AuditEventImpl.class.getName());
	}

	@Override
	@Reference(
		target = AuditPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = AuditPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = AuditPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		AuditEventModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_AUDITEVENT =
		"SELECT auditEvent FROM AuditEvent auditEvent";

	private static final String _SQL_SELECT_AUDITEVENT_WHERE =
		"SELECT auditEvent FROM AuditEvent auditEvent WHERE ";

	private static final String _SQL_COUNT_AUDITEVENT_WHERE =
		"SELECT COUNT(auditEvent) FROM AuditEvent auditEvent WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return dummyFinderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1911865743