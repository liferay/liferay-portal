/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.security.audit.storage.exception.NoSuchEventAccountEntryRelException;
import com.liferay.portal.security.audit.storage.model.AuditEventAccountEntryRel;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the audit event account entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AuditEventAccountEntryRelUtil
 * @generated
 */
@ProviderType
public interface AuditEventAccountEntryRelPersistence
	extends BasePersistence<AuditEventAccountEntryRel> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link AuditEventAccountEntryRelUtil} to access the audit event account entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

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
	public java.util.List<AuditEventAccountEntryRel> findByAuditEventId(
		long auditEventId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AuditEventAccountEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first audit event account entry rel in the ordered set where auditEventId = &#63;.
	 *
	 * @param auditEventId the audit event ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audit event account entry rel
	 * @throws NoSuchEventAccountEntryRelException if a matching audit event account entry rel could not be found
	 */
	public AuditEventAccountEntryRel findByAuditEventId_First(
			long auditEventId,
			com.liferay.portal.kernel.util.OrderByComparator
				<AuditEventAccountEntryRel> orderByComparator)
		throws NoSuchEventAccountEntryRelException;

	/**
	 * Returns the first audit event account entry rel in the ordered set where auditEventId = &#63;.
	 *
	 * @param auditEventId the audit event ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audit event account entry rel, or <code>null</code> if a matching audit event account entry rel could not be found
	 */
	public AuditEventAccountEntryRel fetchByAuditEventId_First(
		long auditEventId,
		com.liferay.portal.kernel.util.OrderByComparator
			<AuditEventAccountEntryRel> orderByComparator);

	/**
	 * Removes all the audit event account entry rels where auditEventId = &#63; from the database.
	 *
	 * @param auditEventId the audit event ID
	 */
	public void removeByAuditEventId(long auditEventId);

	/**
	 * Returns the number of audit event account entry rels where auditEventId = &#63;.
	 *
	 * @param auditEventId the audit event ID
	 * @return the number of matching audit event account entry rels
	 */
	public int countByAuditEventId(long auditEventId);

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
	public java.util.List<AuditEventAccountEntryRel> findByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AuditEventAccountEntryRel> orderByComparator,
		boolean useFinderCache);

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
	public AuditEventAccountEntryRel findByC_CD_AEI_First(
			long companyId, Date createDate, long accountEntryId,
			com.liferay.portal.kernel.util.OrderByComparator
				<AuditEventAccountEntryRel> orderByComparator)
		throws NoSuchEventAccountEntryRelException;

	/**
	 * Returns the first audit event account entry rel in the ordered set where companyId = &#63; and createDate = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audit event account entry rel, or <code>null</code> if a matching audit event account entry rel could not be found
	 */
	public AuditEventAccountEntryRel fetchByC_CD_AEI_First(
		long companyId, Date createDate, long accountEntryId,
		com.liferay.portal.kernel.util.OrderByComparator
			<AuditEventAccountEntryRel> orderByComparator);

	/**
	 * Removes all the audit event account entry rels where companyId = &#63; and createDate = &#63; and accountEntryId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param accountEntryId the account entry ID
	 */
	public void removeByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId);

	/**
	 * Returns the number of audit event account entry rels where companyId = &#63; and createDate = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param accountEntryId the account entry ID
	 * @return the number of matching audit event account entry rels
	 */
	public int countByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId);

	/**
	 * Creates a new audit event account entry rel with the primary key. Does not add the audit event account entry rel to the database.
	 *
	 * @param auditEventAccountEntryRelId the primary key for the new audit event account entry rel
	 * @return the new audit event account entry rel
	 */
	public AuditEventAccountEntryRel create(long auditEventAccountEntryRelId);

	/**
	 * Removes the audit event account entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param auditEventAccountEntryRelId the primary key of the audit event account entry rel
	 * @return the audit event account entry rel that was removed
	 * @throws NoSuchEventAccountEntryRelException if a audit event account entry rel with the primary key could not be found
	 */
	public AuditEventAccountEntryRel remove(long auditEventAccountEntryRelId)
		throws NoSuchEventAccountEntryRelException;

	public AuditEventAccountEntryRel updateImpl(
		AuditEventAccountEntryRel auditEventAccountEntryRel);

	/**
	 * Returns the audit event account entry rel with the primary key or throws a <code>NoSuchEventAccountEntryRelException</code> if it could not be found.
	 *
	 * @param auditEventAccountEntryRelId the primary key of the audit event account entry rel
	 * @return the audit event account entry rel
	 * @throws NoSuchEventAccountEntryRelException if a audit event account entry rel with the primary key could not be found
	 */
	public AuditEventAccountEntryRel findByPrimaryKey(
			long auditEventAccountEntryRelId)
		throws NoSuchEventAccountEntryRelException;

	/**
	 * Returns the audit event account entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param auditEventAccountEntryRelId the primary key of the audit event account entry rel
	 * @return the audit event account entry rel, or <code>null</code> if a audit event account entry rel with the primary key could not be found
	 */
	public AuditEventAccountEntryRel fetchByPrimaryKey(
		long auditEventAccountEntryRelId);

	/**
	 * Returns all the audit event account entry rels where auditEventId = &#63;.
	 *
	 * @param auditEventId the audit event ID
	 * @return the matching audit event account entry rels
	 */
	public default java.util.List<AuditEventAccountEntryRel> findByAuditEventId(
		long auditEventId) {

		return findByAuditEventId(
			auditEventId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<AuditEventAccountEntryRel> findByAuditEventId(
		long auditEventId, int start, int end) {

		return findByAuditEventId(auditEventId, start, end, null, true);
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
	public default java.util.List<AuditEventAccountEntryRel> findByAuditEventId(
		long auditEventId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AuditEventAccountEntryRel> orderByComparator) {

		return findByAuditEventId(
			auditEventId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the audit event account entry rels where companyId = &#63; and createDate = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param accountEntryId the account entry ID
	 * @return the matching audit event account entry rels
	 */
	public default java.util.List<AuditEventAccountEntryRel> findByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId) {

		return findByC_CD_AEI(
			companyId, createDate, accountEntryId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<AuditEventAccountEntryRel> findByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId, int start,
		int end) {

		return findByC_CD_AEI(
			companyId, createDate, accountEntryId, start, end, null, true);
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
	public default java.util.List<AuditEventAccountEntryRel> findByC_CD_AEI(
		long companyId, Date createDate, long accountEntryId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AuditEventAccountEntryRel> orderByComparator) {

		return findByC_CD_AEI(
			companyId, createDate, accountEntryId, start, end,
			orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-27442272