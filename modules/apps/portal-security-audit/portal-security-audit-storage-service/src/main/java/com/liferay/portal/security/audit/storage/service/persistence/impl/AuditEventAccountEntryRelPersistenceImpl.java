/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.security.audit.storage.exception.NoSuchEventAccountEntryRelException;
import com.liferay.portal.security.audit.storage.model.AuditEventAccountEntryRel;
import com.liferay.portal.security.audit.storage.model.AuditEventAccountEntryRelTable;
import com.liferay.portal.security.audit.storage.model.impl.AuditEventAccountEntryRelImpl;
import com.liferay.portal.security.audit.storage.model.impl.AuditEventAccountEntryRelModelImpl;
import com.liferay.portal.security.audit.storage.service.persistence.AuditEventAccountEntryRelPersistence;
import com.liferay.portal.security.audit.storage.service.persistence.AuditEventAccountEntryRelUtil;
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
 * The persistence implementation for the audit event account entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AuditEventAccountEntryRelPersistence.class)
public class AuditEventAccountEntryRelPersistenceImpl
	extends BasePersistenceImpl
		<AuditEventAccountEntryRel, NoSuchEventAccountEntryRelException>
	implements AuditEventAccountEntryRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AuditEventAccountEntryRelUtil</code> to access the audit event account entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AuditEventAccountEntryRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AuditEventAccountEntryRel, NoSuchEventAccountEntryRelException>
			_collectionPersistenceFinderByAuditEventId;

	/**
	 * Returns an ordered range of all the audit event account entry rels where auditEventId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AuditEventAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param auditEventId the audit event ID
	 * @param start the lower bound of the range of audit event account entry rels
	 * @param end the upper bound of the range of audit event account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching audit event account entry rels
	 */
	@Override
	public List<AuditEventAccountEntryRel> findByAuditEventId(
		long auditEventId, int start, int end,
		OrderByComparator<AuditEventAccountEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAuditEventId.find(
			dummyFinderCache, new Object[] {auditEventId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first audit event account entry rel in the ordered set where auditEventId = &#63;.
	 *
	 * @param auditEventId the audit event ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audit event account entry rel
	 * @throws NoSuchEventAccountEntryRelException if a matching audit event account entry rel could not be found
	 */
	@Override
	public AuditEventAccountEntryRel findByAuditEventId_First(
			long auditEventId,
			OrderByComparator<AuditEventAccountEntryRel> orderByComparator)
		throws NoSuchEventAccountEntryRelException {

		return _collectionPersistenceFinderByAuditEventId.findFirst(
			dummyFinderCache, new Object[] {auditEventId}, orderByComparator);
	}

	/**
	 * Returns the first audit event account entry rel in the ordered set where auditEventId = &#63;.
	 *
	 * @param auditEventId the audit event ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audit event account entry rel, or <code>null</code> if a matching audit event account entry rel could not be found
	 */
	@Override
	public AuditEventAccountEntryRel fetchByAuditEventId_First(
		long auditEventId,
		OrderByComparator<AuditEventAccountEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByAuditEventId.fetchFirst(
			dummyFinderCache, new Object[] {auditEventId}, orderByComparator);
	}

	/**
	 * Removes all the audit event account entry rels where auditEventId = &#63; from the database.
	 *
	 * @param auditEventId the audit event ID
	 */
	@Override
	public void removeByAuditEventId(long auditEventId) {
		_collectionPersistenceFinderByAuditEventId.remove(
			dummyFinderCache, new Object[] {auditEventId});
	}

	/**
	 * Returns the number of audit event account entry rels where auditEventId = &#63;.
	 *
	 * @param auditEventId the audit event ID
	 * @return the number of matching audit event account entry rels
	 */
	@Override
	public int countByAuditEventId(long auditEventId) {
		return _collectionPersistenceFinderByAuditEventId.count(
			dummyFinderCache, new Object[] {auditEventId});
	}

	private CollectionPersistenceFinder
		<AuditEventAccountEntryRel, NoSuchEventAccountEntryRelException>
			_collectionPersistenceFinderByC_CD_AEI;

	/**
	 * Returns an ordered range of all the audit event account entry rels where companyId = &#63; and createDate = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AuditEventAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of audit event account entry rels
	 * @param end the upper bound of the range of audit event account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching audit event account entry rels
	 */
	@Override
	public List<AuditEventAccountEntryRel> findByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId, int start,
		int end, OrderByComparator<AuditEventAccountEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CD_AEI.find(
			dummyFinderCache,
			new Object[] {companyId, createDate, accountEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first audit event account entry rel in the ordered set where companyId = &#63; and createDate = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audit event account entry rel
	 * @throws NoSuchEventAccountEntryRelException if a matching audit event account entry rel could not be found
	 */
	@Override
	public AuditEventAccountEntryRel findByC_CD_AEI_First(
			long companyId, Date createDate, long accountEntryId,
			OrderByComparator<AuditEventAccountEntryRel> orderByComparator)
		throws NoSuchEventAccountEntryRelException {

		return _collectionPersistenceFinderByC_CD_AEI.findFirst(
			dummyFinderCache,
			new Object[] {companyId, createDate, accountEntryId},
			orderByComparator);
	}

	/**
	 * Returns the first audit event account entry rel in the ordered set where companyId = &#63; and createDate = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audit event account entry rel, or <code>null</code> if a matching audit event account entry rel could not be found
	 */
	@Override
	public AuditEventAccountEntryRel fetchByC_CD_AEI_First(
		long companyId, Date createDate, long accountEntryId,
		OrderByComparator<AuditEventAccountEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByC_CD_AEI.fetchFirst(
			dummyFinderCache,
			new Object[] {companyId, createDate, accountEntryId},
			orderByComparator);
	}

	/**
	 * Removes all the audit event account entry rels where companyId = &#63; and createDate = &#63; and accountEntryId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param accountEntryId the account entry ID
	 */
	@Override
	public void removeByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId) {

		_collectionPersistenceFinderByC_CD_AEI.remove(
			dummyFinderCache,
			new Object[] {companyId, createDate, accountEntryId});
	}

	/**
	 * Returns the number of audit event account entry rels where companyId = &#63; and createDate = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param accountEntryId the account entry ID
	 * @return the number of matching audit event account entry rels
	 */
	@Override
	public int countByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId) {

		return _collectionPersistenceFinderByC_CD_AEI.count(
			dummyFinderCache,
			new Object[] {companyId, createDate, accountEntryId});
	}

	public AuditEventAccountEntryRelPersistenceImpl() {
		setModelClass(AuditEventAccountEntryRel.class);

		setModelImplClass(AuditEventAccountEntryRelImpl.class);
		setModelPKClass(long.class);

		setTable(AuditEventAccountEntryRelTable.INSTANCE);
	}

	/**
	 * Creates a new audit event account entry rel with the primary key. Does not add the audit event account entry rel to the database.
	 *
	 * @param auditEventAccountEntryRelId the primary key for the new audit event account entry rel
	 * @return the new audit event account entry rel
	 */
	@Override
	public AuditEventAccountEntryRel create(long auditEventAccountEntryRelId) {
		AuditEventAccountEntryRel auditEventAccountEntryRel =
			new AuditEventAccountEntryRelImpl();

		auditEventAccountEntryRel.setNew(true);
		auditEventAccountEntryRel.setPrimaryKey(auditEventAccountEntryRelId);

		auditEventAccountEntryRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return auditEventAccountEntryRel;
	}

	/**
	 * Removes the audit event account entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param auditEventAccountEntryRelId the primary key of the audit event account entry rel
	 * @return the audit event account entry rel that was removed
	 * @throws NoSuchEventAccountEntryRelException if a audit event account entry rel with the primary key could not be found
	 */
	@Override
	public AuditEventAccountEntryRel remove(long auditEventAccountEntryRelId)
		throws NoSuchEventAccountEntryRelException {

		return remove((Serializable)auditEventAccountEntryRelId);
	}

	@Override
	protected AuditEventAccountEntryRel removeImpl(
		AuditEventAccountEntryRel auditEventAccountEntryRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(auditEventAccountEntryRel)) {
				auditEventAccountEntryRel =
					(AuditEventAccountEntryRel)session.get(
						AuditEventAccountEntryRelImpl.class,
						auditEventAccountEntryRel.getPrimaryKeyObj());
			}

			if (auditEventAccountEntryRel != null) {
				session.delete(auditEventAccountEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (auditEventAccountEntryRel != null) {
			clearCache(auditEventAccountEntryRel);
		}

		return auditEventAccountEntryRel;
	}

	@Override
	public AuditEventAccountEntryRel updateImpl(
		AuditEventAccountEntryRel auditEventAccountEntryRel) {

		boolean isNew = auditEventAccountEntryRel.isNew();

		if (!(auditEventAccountEntryRel instanceof
				AuditEventAccountEntryRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(auditEventAccountEntryRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					auditEventAccountEntryRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in auditEventAccountEntryRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AuditEventAccountEntryRel implementation " +
					auditEventAccountEntryRel.getClass());
		}

		AuditEventAccountEntryRelModelImpl auditEventAccountEntryRelModelImpl =
			(AuditEventAccountEntryRelModelImpl)auditEventAccountEntryRel;

		if (isNew && (auditEventAccountEntryRel.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				auditEventAccountEntryRel.setCreateDate(date);
			}
			else {
				auditEventAccountEntryRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(auditEventAccountEntryRel);
			}
			else {
				auditEventAccountEntryRel =
					(AuditEventAccountEntryRel)session.merge(
						auditEventAccountEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(auditEventAccountEntryRel, false);

		if (isNew) {
			auditEventAccountEntryRel.setNew(false);
		}

		auditEventAccountEntryRel.resetOriginalValues();

		return auditEventAccountEntryRel;
	}

	/**
	 * Returns the audit event account entry rel with the primary key or throws a <code>NoSuchEventAccountEntryRelException</code> if it could not be found.
	 *
	 * @param auditEventAccountEntryRelId the primary key of the audit event account entry rel
	 * @return the audit event account entry rel
	 * @throws NoSuchEventAccountEntryRelException if a audit event account entry rel with the primary key could not be found
	 */
	@Override
	public AuditEventAccountEntryRel findByPrimaryKey(
			long auditEventAccountEntryRelId)
		throws NoSuchEventAccountEntryRelException {

		return findByPrimaryKey((Serializable)auditEventAccountEntryRelId);
	}

	/**
	 * Returns the audit event account entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param auditEventAccountEntryRelId the primary key of the audit event account entry rel
	 * @return the audit event account entry rel, or <code>null</code> if a audit event account entry rel with the primary key could not be found
	 */
	@Override
	public AuditEventAccountEntryRel fetchByPrimaryKey(
		long auditEventAccountEntryRelId) {

		return fetchByPrimaryKey((Serializable)auditEventAccountEntryRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return dummyEntityCache;
	}

	@Override
	protected String getPKDBName() {
		return "auditEventAccountEntryRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_AUDITEVENTACCOUNTENTRYREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return AuditEventAccountEntryRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the audit event account entry rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByAuditEventId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByAuditEventId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"auditEventId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByAuditEventId", new String[] {Long.class.getName()},
					new String[] {"auditEventId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByAuditEventId", new String[] {Long.class.getName()},
					new String[] {"auditEventId"}, false),
				_SQL_SELECT_AUDITEVENTACCOUNTENTRYREL_WHERE,
				_SQL_COUNT_AUDITEVENTACCOUNTENTRYREL_WHERE,
				AuditEventAccountEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"auditEventAccountEntryRel.", "auditEventId",
					FinderColumn.Type.LONG, "=", true, true,
					AuditEventAccountEntryRel::getAuditEventId));

		_collectionPersistenceFinderByC_CD_AEI =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CD_AEI",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "createDate", "accountEntryId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CD_AEI",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Long.class.getName()
					},
					new String[] {"companyId", "createDate", "accountEntryId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_CD_AEI",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Long.class.getName()
					},
					new String[] {"companyId", "createDate", "accountEntryId"},
					false),
				_SQL_SELECT_AUDITEVENTACCOUNTENTRYREL_WHERE,
				_SQL_COUNT_AUDITEVENTACCOUNTENTRYREL_WHERE,
				AuditEventAccountEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"auditEventAccountEntryRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					AuditEventAccountEntryRel::getCompanyId),
				new FinderColumn<>(
					"auditEventAccountEntryRel.", "createDate",
					FinderColumn.Type.DATE, "=", true, true,
					AuditEventAccountEntryRel::getCreateDate),
				new FinderColumn<>(
					"auditEventAccountEntryRel.", "accountEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					AuditEventAccountEntryRel::getAccountEntryId));

		AuditEventAccountEntryRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AuditEventAccountEntryRelUtil.setPersistence(null);

		dummyEntityCache.removeCache(
			AuditEventAccountEntryRelImpl.class.getName());
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
		AuditEventAccountEntryRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_AUDITEVENTACCOUNTENTRYREL =
		"SELECT auditEventAccountEntryRel FROM AuditEventAccountEntryRel auditEventAccountEntryRel";

	private static final String _SQL_SELECT_AUDITEVENTACCOUNTENTRYREL_WHERE =
		"SELECT auditEventAccountEntryRel FROM AuditEventAccountEntryRel auditEventAccountEntryRel WHERE ";

	private static final String _SQL_COUNT_AUDITEVENTACCOUNTENTRYREL_WHERE =
		"SELECT COUNT(auditEventAccountEntryRel) FROM AuditEventAccountEntryRel auditEventAccountEntryRel WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return dummyFinderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:502109531