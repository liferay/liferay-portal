/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.security.audit.storage.model.AuditEventAccountEntryRel;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the audit event account entry rel service. This utility wraps <code>com.liferay.portal.security.audit.storage.service.persistence.impl.AuditEventAccountEntryRelPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AuditEventAccountEntryRelPersistence
 * @generated
 */
public class AuditEventAccountEntryRelUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<AuditEventAccountEntryRel> auditEventAccountEntryRels) {

		getPersistence().cacheResult(auditEventAccountEntryRels);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		AuditEventAccountEntryRel auditEventAccountEntryRel) {

		getPersistence().cacheResult(auditEventAccountEntryRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(
		AuditEventAccountEntryRel auditEventAccountEntryRel) {

		getPersistence().clearCache(auditEventAccountEntryRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, AuditEventAccountEntryRel>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<AuditEventAccountEntryRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<AuditEventAccountEntryRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<AuditEventAccountEntryRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<AuditEventAccountEntryRel> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static AuditEventAccountEntryRel update(
		AuditEventAccountEntryRel auditEventAccountEntryRel) {

		return getPersistence().update(auditEventAccountEntryRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static AuditEventAccountEntryRel update(
		AuditEventAccountEntryRel auditEventAccountEntryRel,
		ServiceContext serviceContext) {

		return getPersistence().update(
			auditEventAccountEntryRel, serviceContext);
	}

	/**
	 * Returns an ordered range of all the audit event account entry rels where auditEventId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.audit.storage.model.impl.AuditEventAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param auditEventId the audit event ID
	 * @param start the lower bound of the range of audit event account entry rels
	 * @param end the upper bound of the range of audit event account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching audit event account entry rels
	 */
	public static List<AuditEventAccountEntryRel> findByAuditEventId(
		long auditEventId, int start, int end,
		OrderByComparator<AuditEventAccountEntryRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByAuditEventId(
			auditEventId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first audit event account entry rel in the ordered set where auditEventId = &#63;.
	 *
	 * @param auditEventId the audit event ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audit event account entry rel
	 * @throws NoSuchEventAccountEntryRelException if a matching audit event account entry rel could not be found
	 */
	public static AuditEventAccountEntryRel findByAuditEventId_First(
			long auditEventId,
			OrderByComparator<AuditEventAccountEntryRel> orderByComparator)
		throws com.liferay.portal.security.audit.storage.exception.
			NoSuchEventAccountEntryRelException {

		return getPersistence().findByAuditEventId_First(
			auditEventId, orderByComparator);
	}

	/**
	 * Returns the first audit event account entry rel in the ordered set where auditEventId = &#63;.
	 *
	 * @param auditEventId the audit event ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audit event account entry rel, or <code>null</code> if a matching audit event account entry rel could not be found
	 */
	public static AuditEventAccountEntryRel fetchByAuditEventId_First(
		long auditEventId,
		OrderByComparator<AuditEventAccountEntryRel> orderByComparator) {

		return getPersistence().fetchByAuditEventId_First(
			auditEventId, orderByComparator);
	}

	/**
	 * Removes all the audit event account entry rels where auditEventId = &#63; from the database.
	 *
	 * @param auditEventId the audit event ID
	 */
	public static void removeByAuditEventId(long auditEventId) {
		getPersistence().removeByAuditEventId(auditEventId);
	}

	/**
	 * Returns the number of audit event account entry rels where auditEventId = &#63;.
	 *
	 * @param auditEventId the audit event ID
	 * @return the number of matching audit event account entry rels
	 */
	public static int countByAuditEventId(long auditEventId) {
		return getPersistence().countByAuditEventId(auditEventId);
	}

	/**
	 * Returns an ordered range of all the audit event account entry rels where companyId = &#63; and createDate = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.audit.storage.model.impl.AuditEventAccountEntryRelModelImpl</code>.
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
	public static List<AuditEventAccountEntryRel> findByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId, int start,
		int end, OrderByComparator<AuditEventAccountEntryRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_CD_AEI(
			companyId, createDate, accountEntryId, start, end,
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
	public static AuditEventAccountEntryRel findByC_CD_AEI_First(
			long companyId, Date createDate, long accountEntryId,
			OrderByComparator<AuditEventAccountEntryRel> orderByComparator)
		throws com.liferay.portal.security.audit.storage.exception.
			NoSuchEventAccountEntryRelException {

		return getPersistence().findByC_CD_AEI_First(
			companyId, createDate, accountEntryId, orderByComparator);
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
	public static AuditEventAccountEntryRel fetchByC_CD_AEI_First(
		long companyId, Date createDate, long accountEntryId,
		OrderByComparator<AuditEventAccountEntryRel> orderByComparator) {

		return getPersistence().fetchByC_CD_AEI_First(
			companyId, createDate, accountEntryId, orderByComparator);
	}

	/**
	 * Removes all the audit event account entry rels where companyId = &#63; and createDate = &#63; and accountEntryId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param accountEntryId the account entry ID
	 */
	public static void removeByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId) {

		getPersistence().removeByC_CD_AEI(
			companyId, createDate, accountEntryId);
	}

	/**
	 * Returns the number of audit event account entry rels where companyId = &#63; and createDate = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param accountEntryId the account entry ID
	 * @return the number of matching audit event account entry rels
	 */
	public static int countByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId) {

		return getPersistence().countByC_CD_AEI(
			companyId, createDate, accountEntryId);
	}

	/**
	 * Creates a new audit event account entry rel with the primary key. Does not add the audit event account entry rel to the database.
	 *
	 * @param auditEventAccountEntryRelId the primary key for the new audit event account entry rel
	 * @return the new audit event account entry rel
	 */
	public static AuditEventAccountEntryRel create(
		long auditEventAccountEntryRelId) {

		return getPersistence().create(auditEventAccountEntryRelId);
	}

	/**
	 * Removes the audit event account entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param auditEventAccountEntryRelId the primary key of the audit event account entry rel
	 * @return the audit event account entry rel that was removed
	 * @throws NoSuchEventAccountEntryRelException if a audit event account entry rel with the primary key could not be found
	 */
	public static AuditEventAccountEntryRel remove(
			long auditEventAccountEntryRelId)
		throws com.liferay.portal.security.audit.storage.exception.
			NoSuchEventAccountEntryRelException {

		return getPersistence().remove(auditEventAccountEntryRelId);
	}

	public static AuditEventAccountEntryRel updateImpl(
		AuditEventAccountEntryRel auditEventAccountEntryRel) {

		return getPersistence().updateImpl(auditEventAccountEntryRel);
	}

	/**
	 * Returns the audit event account entry rel with the primary key or throws a <code>NoSuchEventAccountEntryRelException</code> if it could not be found.
	 *
	 * @param auditEventAccountEntryRelId the primary key of the audit event account entry rel
	 * @return the audit event account entry rel
	 * @throws NoSuchEventAccountEntryRelException if a audit event account entry rel with the primary key could not be found
	 */
	public static AuditEventAccountEntryRel findByPrimaryKey(
			long auditEventAccountEntryRelId)
		throws com.liferay.portal.security.audit.storage.exception.
			NoSuchEventAccountEntryRelException {

		return getPersistence().findByPrimaryKey(auditEventAccountEntryRelId);
	}

	/**
	 * Returns the audit event account entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param auditEventAccountEntryRelId the primary key of the audit event account entry rel
	 * @return the audit event account entry rel, or <code>null</code> if a audit event account entry rel with the primary key could not be found
	 */
	public static AuditEventAccountEntryRel fetchByPrimaryKey(
		long auditEventAccountEntryRelId) {

		return getPersistence().fetchByPrimaryKey(auditEventAccountEntryRelId);
	}

	/**
	 * Returns all the audit event account entry rels where auditEventId = &#63;.
	 *
	 * @param auditEventId the audit event ID
	 * @return the matching audit event account entry rels
	 */
	public static List<AuditEventAccountEntryRel> findByAuditEventId(
		long auditEventId) {

		return getPersistence().findByAuditEventId(auditEventId);
	}

	/**
	 * Returns a range of all the audit event account entry rels where auditEventId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.audit.storage.model.impl.AuditEventAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param auditEventId the audit event ID
	 * @param start the lower bound of the range of audit event account entry rels
	 * @param end the upper bound of the range of audit event account entry rels (not inclusive)
	 * @return the range of matching audit event account entry rels
	 */
	public static List<AuditEventAccountEntryRel> findByAuditEventId(
		long auditEventId, int start, int end) {

		return getPersistence().findByAuditEventId(auditEventId, start, end);
	}

	/**
	 * Returns an ordered range of all the audit event account entry rels where auditEventId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.audit.storage.model.impl.AuditEventAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param auditEventId the audit event ID
	 * @param start the lower bound of the range of audit event account entry rels
	 * @param end the upper bound of the range of audit event account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching audit event account entry rels
	 */
	public static List<AuditEventAccountEntryRel> findByAuditEventId(
		long auditEventId, int start, int end,
		OrderByComparator<AuditEventAccountEntryRel> orderByComparator) {

		return getPersistence().findByAuditEventId(
			auditEventId, start, end, orderByComparator);
	}

	/**
	 * Returns all the audit event account entry rels where companyId = &#63; and createDate = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param accountEntryId the account entry ID
	 * @return the matching audit event account entry rels
	 */
	public static List<AuditEventAccountEntryRel> findByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId) {

		return getPersistence().findByC_CD_AEI(
			companyId, createDate, accountEntryId);
	}

	/**
	 * Returns a range of all the audit event account entry rels where companyId = &#63; and createDate = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.audit.storage.model.impl.AuditEventAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of audit event account entry rels
	 * @param end the upper bound of the range of audit event account entry rels (not inclusive)
	 * @return the range of matching audit event account entry rels
	 */
	public static List<AuditEventAccountEntryRel> findByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId, int start,
		int end) {

		return getPersistence().findByC_CD_AEI(
			companyId, createDate, accountEntryId, start, end);
	}

	/**
	 * Returns an ordered range of all the audit event account entry rels where companyId = &#63; and createDate = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.audit.storage.model.impl.AuditEventAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of audit event account entry rels
	 * @param end the upper bound of the range of audit event account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching audit event account entry rels
	 */
	public static List<AuditEventAccountEntryRel> findByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId, int start,
		int end,
		OrderByComparator<AuditEventAccountEntryRel> orderByComparator) {

		return getPersistence().findByC_CD_AEI(
			companyId, createDate, accountEntryId, start, end,
			orderByComparator);
	}

	public static AuditEventAccountEntryRelPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		AuditEventAccountEntryRelPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile AuditEventAccountEntryRelPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1046712304