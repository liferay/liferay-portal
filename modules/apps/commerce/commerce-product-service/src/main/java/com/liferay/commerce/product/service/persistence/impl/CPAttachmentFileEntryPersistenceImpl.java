/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.DuplicateCPAttachmentFileEntryExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPAttachmentFileEntryException;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPAttachmentFileEntryTable;
import com.liferay.commerce.product.model.impl.CPAttachmentFileEntryImpl;
import com.liferay.commerce.product.model.impl.CPAttachmentFileEntryModelImpl;
import com.liferay.commerce.product.service.persistence.CPAttachmentFileEntryPersistence;
import com.liferay.commerce.product.service.persistence.CPAttachmentFileEntryUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the cp attachment file entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPAttachmentFileEntryPersistence.class)
public class CPAttachmentFileEntryPersistenceImpl
	extends BasePersistenceImpl
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
	implements CPAttachmentFileEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPAttachmentFileEntryUtil</code> to access the cp attachment file entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPAttachmentFileEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp attachment file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByUuid_First(
			String uuid,
			OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws NoSuchCPAttachmentFileEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cp attachment file entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp attachment file entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the cp attachment file entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPAttachmentFileEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchCPAttachmentFileEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the cp attachment file entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the cp attachment file entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp attachment file entry that was removed
	 */
	@Override
	public CPAttachmentFileEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPAttachmentFileEntryException {

		CPAttachmentFileEntry cpAttachmentFileEntry = findByUUID_G(
			uuid, groupId);

		return remove(cpAttachmentFileEntry);
	}

	/**
	 * Returns the number of cp attachment file entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp attachment file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws NoSuchCPAttachmentFileEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp attachment file entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of cp attachment file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_collectionPersistenceFinderByFileEntryId;

	/**
	 * Returns an ordered range of all the cp attachment file entries where fileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryId the file entry ID
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByFileEntryId(
		long fileEntryId, int start, int end,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFileEntryId.find(
			finderCache, new Object[] {fileEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByFileEntryId_First(
			long fileEntryId,
			OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws NoSuchCPAttachmentFileEntryException {

		return _collectionPersistenceFinderByFileEntryId.findFirst(
			finderCache, new Object[] {fileEntryId}, orderByComparator);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByFileEntryId_First(
		long fileEntryId,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByFileEntryId.fetchFirst(
			finderCache, new Object[] {fileEntryId}, orderByComparator);
	}

	/**
	 * Removes all the cp attachment file entries where fileEntryId = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 */
	@Override
	public void removeByFileEntryId(long fileEntryId) {
		_collectionPersistenceFinderByFileEntryId.remove(
			finderCache, new Object[] {fileEntryId});
	}

	/**
	 * Returns the number of cp attachment file entries where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByFileEntryId(long fileEntryId) {
		return _collectionPersistenceFinderByFileEntryId.count(
			finderCache, new Object[] {fileEntryId});
	}

	private CollectionPersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the cp attachment file entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws NoSuchCPAttachmentFileEntryException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the cp attachment file entries where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of cp attachment file entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_collectionPersistenceFinderByLtD_S;

	/**
	 * Returns all the cp attachment file entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByLtD_S(
		Date displayDate, int status) {

		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp attachment file entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @return the range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp attachment file entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp attachment file entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtD_S.find(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws NoSuchCPAttachmentFileEntryException {

		return _collectionPersistenceFinderByLtD_S.findFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.fetchFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Removes all the cp attachment file entries where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByLtD_S(Date displayDate, int status) {
		_collectionPersistenceFinderByLtD_S.remove(
			finderCache, new Object[] {displayDate, status});
	}

	/**
	 * Returns the number of cp attachment file entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.count(
			finderCache, new Object[] {displayDate, status});
	}

	private CollectionPersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_collectionPersistenceFinderByG_C_F;

	/**
	 * Returns an ordered range of all the cp attachment file entries where groupId = &#63; and classNameId = &#63; and fileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param fileEntryId the file entry ID
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByG_C_F(
		long groupId, long classNameId, long fileEntryId, int start, int end,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_F.find(
			finderCache, new Object[] {groupId, classNameId, fileEntryId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where groupId = &#63; and classNameId = &#63; and fileEntryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByG_C_F_First(
			long groupId, long classNameId, long fileEntryId,
			OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws NoSuchCPAttachmentFileEntryException {

		return _collectionPersistenceFinderByG_C_F.findFirst(
			finderCache, new Object[] {groupId, classNameId, fileEntryId},
			orderByComparator);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where groupId = &#63; and classNameId = &#63; and fileEntryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByG_C_F_First(
		long groupId, long classNameId, long fileEntryId,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_F.fetchFirst(
			finderCache, new Object[] {groupId, classNameId, fileEntryId},
			orderByComparator);
	}

	/**
	 * Removes all the cp attachment file entries where groupId = &#63; and classNameId = &#63; and fileEntryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param fileEntryId the file entry ID
	 */
	@Override
	public void removeByG_C_F(
		long groupId, long classNameId, long fileEntryId) {

		_collectionPersistenceFinderByG_C_F.remove(
			finderCache, new Object[] {groupId, classNameId, fileEntryId});
	}

	/**
	 * Returns the number of cp attachment file entries where groupId = &#63; and classNameId = &#63; and fileEntryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param fileEntryId the file entry ID
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByG_C_F(long groupId, long classNameId, long fileEntryId) {
		return _collectionPersistenceFinderByG_C_F.count(
			finderCache, new Object[] {groupId, classNameId, fileEntryId});
	}

	private CollectionPersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_collectionPersistenceFinderByC_C_F;

	/**
	 * Returns an ordered range of all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and fileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param fileEntryId the file entry ID
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_F(
		long classNameId, long classPK, long fileEntryId, int start, int end,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_F.find(
			finderCache, new Object[] {classNameId, classPK, fileEntryId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63; and fileEntryId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByC_C_F_First(
			long classNameId, long classPK, long fileEntryId,
			OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws NoSuchCPAttachmentFileEntryException {

		return _collectionPersistenceFinderByC_C_F.findFirst(
			finderCache, new Object[] {classNameId, classPK, fileEntryId},
			orderByComparator);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63; and fileEntryId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByC_C_F_First(
		long classNameId, long classPK, long fileEntryId,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_F.fetchFirst(
			finderCache, new Object[] {classNameId, classPK, fileEntryId},
			orderByComparator);
	}

	/**
	 * Removes all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and fileEntryId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param fileEntryId the file entry ID
	 */
	@Override
	public void removeByC_C_F(
		long classNameId, long classPK, long fileEntryId) {

		_collectionPersistenceFinderByC_C_F.remove(
			finderCache, new Object[] {classNameId, classPK, fileEntryId});
	}

	/**
	 * Returns the number of cp attachment file entries where classNameId = &#63; and classPK = &#63; and fileEntryId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param fileEntryId the file entry ID
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByC_C_F(long classNameId, long classPK, long fileEntryId) {
		return _collectionPersistenceFinderByC_C_F.count(
			finderCache, new Object[] {classNameId, classPK, fileEntryId});
	}

	private CollectionPersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_collectionPersistenceFinderByC_C_C;

	/**
	 * Returns an ordered range of all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and cdnURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param cdnURL the cdn url
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_C(
		long classNameId, long classPK, String cdnURL, int start, int end,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C.find(
			finderCache, new Object[] {classNameId, classPK, cdnURL}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63; and cdnURL = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param cdnURL the cdn url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByC_C_C_First(
			long classNameId, long classPK, String cdnURL,
			OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws NoSuchCPAttachmentFileEntryException {

		return _collectionPersistenceFinderByC_C_C.findFirst(
			finderCache, new Object[] {classNameId, classPK, cdnURL},
			orderByComparator);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63; and cdnURL = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param cdnURL the cdn url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByC_C_C_First(
		long classNameId, long classPK, String cdnURL,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C.fetchFirst(
			finderCache, new Object[] {classNameId, classPK, cdnURL},
			orderByComparator);
	}

	/**
	 * Removes all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and cdnURL = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param cdnURL the cdn url
	 */
	@Override
	public void removeByC_C_C(long classNameId, long classPK, String cdnURL) {
		_collectionPersistenceFinderByC_C_C.remove(
			finderCache, new Object[] {classNameId, classPK, cdnURL});
	}

	/**
	 * Returns the number of cp attachment file entries where classNameId = &#63; and classPK = &#63; and cdnURL = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param cdnURL the cdn url
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByC_C_C(long classNameId, long classPK, String cdnURL) {
		return _collectionPersistenceFinderByC_C_C.count(
			finderCache, new Object[] {classNameId, classPK, cdnURL});
	}

	private CollectionPersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_collectionPersistenceFinderByC_C_LtD_S;

	/**
	 * Returns all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_LtD_S(
		long classNameId, long classPK, Date displayDate, int status) {

		return findByC_C_LtD_S(
			classNameId, classPK, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @return the range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_LtD_S(
		long classNameId, long classPK, Date displayDate, int status, int start,
		int end) {

		return findByC_C_LtD_S(
			classNameId, classPK, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_LtD_S(
		long classNameId, long classPK, Date displayDate, int status, int start,
		int end, OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return findByC_C_LtD_S(
			classNameId, classPK, displayDate, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_LtD_S(
		long classNameId, long classPK, Date displayDate, int status, int start,
		int end, OrderByComparator<CPAttachmentFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_LtD_S.find(
			finderCache,
			new Object[] {classNameId, classPK, displayDate, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByC_C_LtD_S_First(
			long classNameId, long classPK, Date displayDate, int status,
			OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws NoSuchCPAttachmentFileEntryException {

		return _collectionPersistenceFinderByC_C_LtD_S.findFirst(
			finderCache,
			new Object[] {classNameId, classPK, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByC_C_LtD_S_First(
		long classNameId, long classPK, Date displayDate, int status,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_LtD_S.fetchFirst(
			finderCache,
			new Object[] {classNameId, classPK, displayDate, status},
			orderByComparator);
	}

	/**
	 * Removes all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByC_C_LtD_S(
		long classNameId, long classPK, Date displayDate, int status) {

		_collectionPersistenceFinderByC_C_LtD_S.remove(
			finderCache,
			new Object[] {classNameId, classPK, displayDate, status});
	}

	/**
	 * Returns the number of cp attachment file entries where classNameId = &#63; and classPK = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByC_C_LtD_S(
		long classNameId, long classPK, Date displayDate, int status) {

		return _collectionPersistenceFinderByC_C_LtD_S.count(
			finderCache,
			new Object[] {classNameId, classPK, displayDate, status});
	}

	private CollectionPersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_collectionPersistenceFinderByC_C_T_ST;

	/**
	 * Returns an ordered range of all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_T_ST(
		long classNameId, long classPK, int type, int status, int start,
		int end, OrderByComparator<CPAttachmentFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_T_ST.find(
			finderCache, new Object[] {classNameId, classPK, type, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByC_C_T_ST_First(
			long classNameId, long classPK, int type, int status,
			OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws NoSuchCPAttachmentFileEntryException {

		return _collectionPersistenceFinderByC_C_T_ST.findFirst(
			finderCache, new Object[] {classNameId, classPK, type, status},
			orderByComparator);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByC_C_T_ST_First(
		long classNameId, long classPK, int type, int status,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_T_ST.fetchFirst(
			finderCache, new Object[] {classNameId, classPK, type, status},
			orderByComparator);
	}

	/**
	 * Removes all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByC_C_T_ST(
		long classNameId, long classPK, int type, int status) {

		_collectionPersistenceFinderByC_C_T_ST.remove(
			finderCache, new Object[] {classNameId, classPK, type, status});
	}

	/**
	 * Returns the number of cp attachment file entries where classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByC_C_T_ST(
		long classNameId, long classPK, int type, int status) {

		return _collectionPersistenceFinderByC_C_T_ST.count(
			finderCache, new Object[] {classNameId, classPK, type, status});
	}

	private CollectionPersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_collectionPersistenceFinderByC_C_T_NotST;

	/**
	 * Returns all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and type = &#63; and status &ne; &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @return the matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_T_NotST(
		long classNameId, long classPK, int type, int status) {

		return findByC_C_T_NotST(
			classNameId, classPK, type, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and type = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @return the range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_T_NotST(
		long classNameId, long classPK, int type, int status, int start,
		int end) {

		return findByC_C_T_NotST(
			classNameId, classPK, type, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and type = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_T_NotST(
		long classNameId, long classPK, int type, int status, int start,
		int end, OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return findByC_C_T_NotST(
			classNameId, classPK, type, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and type = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_T_NotST(
		long classNameId, long classPK, int type, int status, int start,
		int end, OrderByComparator<CPAttachmentFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_T_NotST.find(
			finderCache, new Object[] {classNameId, classPK, type, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63; and status &ne; &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByC_C_T_NotST_First(
			long classNameId, long classPK, int type, int status,
			OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws NoSuchCPAttachmentFileEntryException {

		return _collectionPersistenceFinderByC_C_T_NotST.findFirst(
			finderCache, new Object[] {classNameId, classPK, type, status},
			orderByComparator);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63; and status &ne; &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByC_C_T_NotST_First(
		long classNameId, long classPK, int type, int status,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_T_NotST.fetchFirst(
			finderCache, new Object[] {classNameId, classPK, type, status},
			orderByComparator);
	}

	/**
	 * Removes all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and type = &#63; and status &ne; &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByC_C_T_NotST(
		long classNameId, long classPK, int type, int status) {

		_collectionPersistenceFinderByC_C_T_NotST.remove(
			finderCache, new Object[] {classNameId, classPK, type, status});
	}

	/**
	 * Returns the number of cp attachment file entries where classNameId = &#63; and classPK = &#63; and type = &#63; and status &ne; &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByC_C_T_NotST(
		long classNameId, long classPK, int type, int status) {

		return _collectionPersistenceFinderByC_C_T_NotST.count(
			finderCache, new Object[] {classNameId, classPK, type, status});
	}

	private CollectionPersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_collectionPersistenceFinderByC_C_G_T_ST;

	/**
	 * Returns an ordered range of all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and galleryEnabled = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param galleryEnabled the gallery enabled
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_G_T_ST(
		long classNameId, long classPK, boolean galleryEnabled, int type,
		int status, int start, int end,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_G_T_ST.find(
			finderCache,
			new Object[] {classNameId, classPK, galleryEnabled, type, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63; and galleryEnabled = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param galleryEnabled the gallery enabled
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByC_C_G_T_ST_First(
			long classNameId, long classPK, boolean galleryEnabled, int type,
			int status,
			OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws NoSuchCPAttachmentFileEntryException {

		return _collectionPersistenceFinderByC_C_G_T_ST.findFirst(
			finderCache,
			new Object[] {classNameId, classPK, galleryEnabled, type, status},
			orderByComparator);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63; and galleryEnabled = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param galleryEnabled the gallery enabled
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByC_C_G_T_ST_First(
		long classNameId, long classPK, boolean galleryEnabled, int type,
		int status,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_G_T_ST.fetchFirst(
			finderCache,
			new Object[] {classNameId, classPK, galleryEnabled, type, status},
			orderByComparator);
	}

	/**
	 * Removes all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and galleryEnabled = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param galleryEnabled the gallery enabled
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByC_C_G_T_ST(
		long classNameId, long classPK, boolean galleryEnabled, int type,
		int status) {

		_collectionPersistenceFinderByC_C_G_T_ST.remove(
			finderCache,
			new Object[] {classNameId, classPK, galleryEnabled, type, status});
	}

	/**
	 * Returns the number of cp attachment file entries where classNameId = &#63; and classPK = &#63; and galleryEnabled = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param galleryEnabled the gallery enabled
	 * @param type the type
	 * @param status the status
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByC_C_G_T_ST(
		long classNameId, long classPK, boolean galleryEnabled, int type,
		int status) {

		return _collectionPersistenceFinderByC_C_G_T_ST.count(
			finderCache,
			new Object[] {classNameId, classPK, galleryEnabled, type, status});
	}

	private CollectionPersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_collectionPersistenceFinderByC_C_G_T_NotST;

	/**
	 * Returns all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and galleryEnabled = &#63; and type = &#63; and status &ne; &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param galleryEnabled the gallery enabled
	 * @param type the type
	 * @param status the status
	 * @return the matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_G_T_NotST(
		long classNameId, long classPK, boolean galleryEnabled, int type,
		int status) {

		return findByC_C_G_T_NotST(
			classNameId, classPK, galleryEnabled, type, status,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and galleryEnabled = &#63; and type = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param galleryEnabled the gallery enabled
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @return the range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_G_T_NotST(
		long classNameId, long classPK, boolean galleryEnabled, int type,
		int status, int start, int end) {

		return findByC_C_G_T_NotST(
			classNameId, classPK, galleryEnabled, type, status, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and galleryEnabled = &#63; and type = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param galleryEnabled the gallery enabled
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_G_T_NotST(
		long classNameId, long classPK, boolean galleryEnabled, int type,
		int status, int start, int end,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return findByC_C_G_T_NotST(
			classNameId, classPK, galleryEnabled, type, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and galleryEnabled = &#63; and type = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param galleryEnabled the gallery enabled
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp attachment file entries
	 */
	@Override
	public List<CPAttachmentFileEntry> findByC_C_G_T_NotST(
		long classNameId, long classPK, boolean galleryEnabled, int type,
		int status, int start, int end,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_G_T_NotST.find(
			finderCache,
			new Object[] {classNameId, classPK, galleryEnabled, type, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63; and galleryEnabled = &#63; and type = &#63; and status &ne; &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param galleryEnabled the gallery enabled
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByC_C_G_T_NotST_First(
			long classNameId, long classPK, boolean galleryEnabled, int type,
			int status,
			OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws NoSuchCPAttachmentFileEntryException {

		return _collectionPersistenceFinderByC_C_G_T_NotST.findFirst(
			finderCache,
			new Object[] {classNameId, classPK, galleryEnabled, type, status},
			orderByComparator);
	}

	/**
	 * Returns the first cp attachment file entry in the ordered set where classNameId = &#63; and classPK = &#63; and galleryEnabled = &#63; and type = &#63; and status &ne; &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param galleryEnabled the gallery enabled
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByC_C_G_T_NotST_First(
		long classNameId, long classPK, boolean galleryEnabled, int type,
		int status,
		OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_G_T_NotST.fetchFirst(
			finderCache,
			new Object[] {classNameId, classPK, galleryEnabled, type, status},
			orderByComparator);
	}

	/**
	 * Removes all the cp attachment file entries where classNameId = &#63; and classPK = &#63; and galleryEnabled = &#63; and type = &#63; and status &ne; &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param galleryEnabled the gallery enabled
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByC_C_G_T_NotST(
		long classNameId, long classPK, boolean galleryEnabled, int type,
		int status) {

		_collectionPersistenceFinderByC_C_G_T_NotST.remove(
			finderCache,
			new Object[] {classNameId, classPK, galleryEnabled, type, status});
	}

	/**
	 * Returns the number of cp attachment file entries where classNameId = &#63; and classPK = &#63; and galleryEnabled = &#63; and type = &#63; and status &ne; &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param galleryEnabled the gallery enabled
	 * @param type the type
	 * @param status the status
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByC_C_G_T_NotST(
		long classNameId, long classPK, boolean galleryEnabled, int type,
		int status) {

		return _collectionPersistenceFinderByC_C_G_T_NotST.count(
			finderCache,
			new Object[] {classNameId, classPK, galleryEnabled, type, status});
	}

	private UniquePersistenceFinder
		<CPAttachmentFileEntry, NoSuchCPAttachmentFileEntryException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the cp attachment file entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPAttachmentFileEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPAttachmentFileEntryException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the cp attachment file entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the cp attachment file entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp attachment file entry that was removed
	 */
	@Override
	public CPAttachmentFileEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPAttachmentFileEntryException {

		CPAttachmentFileEntry cpAttachmentFileEntry = findByERC_C(
			externalReferenceCode, companyId);

		return remove(cpAttachmentFileEntry);
	}

	/**
	 * Returns the number of cp attachment file entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp attachment file entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CPAttachmentFileEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPAttachmentFileEntry.class);

		setModelImplClass(CPAttachmentFileEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CPAttachmentFileEntryTable.INSTANCE);
	}

	/**
	 * Creates a new cp attachment file entry with the primary key. Does not add the cp attachment file entry to the database.
	 *
	 * @param CPAttachmentFileEntryId the primary key for the new cp attachment file entry
	 * @return the new cp attachment file entry
	 */
	@Override
	public CPAttachmentFileEntry create(long CPAttachmentFileEntryId) {
		CPAttachmentFileEntry cpAttachmentFileEntry =
			new CPAttachmentFileEntryImpl();

		cpAttachmentFileEntry.setNew(true);
		cpAttachmentFileEntry.setPrimaryKey(CPAttachmentFileEntryId);

		String uuid = PortalUUIDUtil.generate();

		cpAttachmentFileEntry.setUuid(uuid);

		cpAttachmentFileEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpAttachmentFileEntry;
	}

	/**
	 * Removes the cp attachment file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPAttachmentFileEntryId the primary key of the cp attachment file entry
	 * @return the cp attachment file entry that was removed
	 * @throws NoSuchCPAttachmentFileEntryException if a cp attachment file entry with the primary key could not be found
	 */
	@Override
	public CPAttachmentFileEntry remove(long CPAttachmentFileEntryId)
		throws NoSuchCPAttachmentFileEntryException {

		return remove((Serializable)CPAttachmentFileEntryId);
	}

	@Override
	protected CPAttachmentFileEntry removeImpl(
		CPAttachmentFileEntry cpAttachmentFileEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpAttachmentFileEntry)) {
				cpAttachmentFileEntry = (CPAttachmentFileEntry)session.get(
					CPAttachmentFileEntryImpl.class,
					cpAttachmentFileEntry.getPrimaryKeyObj());
			}

			if ((cpAttachmentFileEntry != null) &&
				ctPersistenceHelper.isRemove(cpAttachmentFileEntry)) {

				session.delete(cpAttachmentFileEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpAttachmentFileEntry != null) {
			clearCache(cpAttachmentFileEntry);
		}

		return cpAttachmentFileEntry;
	}

	@Override
	public CPAttachmentFileEntry updateImpl(
		CPAttachmentFileEntry cpAttachmentFileEntry) {

		boolean isNew = cpAttachmentFileEntry.isNew();

		if (!(cpAttachmentFileEntry instanceof
				CPAttachmentFileEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpAttachmentFileEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpAttachmentFileEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpAttachmentFileEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPAttachmentFileEntry implementation " +
					cpAttachmentFileEntry.getClass());
		}

		CPAttachmentFileEntryModelImpl cpAttachmentFileEntryModelImpl =
			(CPAttachmentFileEntryModelImpl)cpAttachmentFileEntry;

		if (Validator.isNull(cpAttachmentFileEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpAttachmentFileEntry.setUuid(uuid);
		}

		if (Validator.isNull(
				cpAttachmentFileEntry.getExternalReferenceCode())) {

			cpAttachmentFileEntry.setExternalReferenceCode(
				cpAttachmentFileEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					cpAttachmentFileEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					cpAttachmentFileEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = cpAttachmentFileEntry.getCompanyId();

					long groupId = cpAttachmentFileEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = cpAttachmentFileEntry.getPrimaryKey();
					}

					try {
						cpAttachmentFileEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CPAttachmentFileEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								cpAttachmentFileEntry.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CPAttachmentFileEntry ercCPAttachmentFileEntry = fetchByERC_C(
				cpAttachmentFileEntry.getExternalReferenceCode(),
				cpAttachmentFileEntry.getCompanyId());

			if (isNew) {
				if (ercCPAttachmentFileEntry != null) {
					throw new DuplicateCPAttachmentFileEntryExternalReferenceCodeException(
						"Duplicate cp attachment file entry with external reference code " +
							cpAttachmentFileEntry.getExternalReferenceCode() +
								" and company " +
									cpAttachmentFileEntry.getCompanyId());
				}
			}
			else {
				if ((ercCPAttachmentFileEntry != null) &&
					(cpAttachmentFileEntry.getCPAttachmentFileEntryId() !=
						ercCPAttachmentFileEntry.
							getCPAttachmentFileEntryId())) {

					throw new DuplicateCPAttachmentFileEntryExternalReferenceCodeException(
						"Duplicate cp attachment file entry with external reference code " +
							cpAttachmentFileEntry.getExternalReferenceCode() +
								" and company " +
									cpAttachmentFileEntry.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpAttachmentFileEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpAttachmentFileEntry.setCreateDate(date);
			}
			else {
				cpAttachmentFileEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpAttachmentFileEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpAttachmentFileEntry.setModifiedDate(date);
			}
			else {
				cpAttachmentFileEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpAttachmentFileEntry)) {
				if (!isNew) {
					session.evict(
						CPAttachmentFileEntryImpl.class,
						cpAttachmentFileEntry.getPrimaryKeyObj());
				}

				session.save(cpAttachmentFileEntry);
			}
			else {
				cpAttachmentFileEntry = (CPAttachmentFileEntry)session.merge(
					cpAttachmentFileEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpAttachmentFileEntry, false);

		if (isNew) {
			cpAttachmentFileEntry.setNew(false);
		}

		cpAttachmentFileEntry.resetOriginalValues();

		return cpAttachmentFileEntry;
	}

	/**
	 * Returns the cp attachment file entry with the primary key or throws a <code>NoSuchCPAttachmentFileEntryException</code> if it could not be found.
	 *
	 * @param CPAttachmentFileEntryId the primary key of the cp attachment file entry
	 * @return the cp attachment file entry
	 * @throws NoSuchCPAttachmentFileEntryException if a cp attachment file entry with the primary key could not be found
	 */
	@Override
	public CPAttachmentFileEntry findByPrimaryKey(long CPAttachmentFileEntryId)
		throws NoSuchCPAttachmentFileEntryException {

		return findByPrimaryKey((Serializable)CPAttachmentFileEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp attachment file entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPAttachmentFileEntryId the primary key of the cp attachment file entry
	 * @return the cp attachment file entry, or <code>null</code> if a cp attachment file entry with the primary key could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchByPrimaryKey(
		long CPAttachmentFileEntryId) {

		return fetchByPrimaryKey((Serializable)CPAttachmentFileEntryId);
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
		return "CPAttachmentFileEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPATTACHMENTFILEENTRY;
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
		return CPAttachmentFileEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPAttachmentFileEntry";
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
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("fileEntryId");
		ctMergeColumnNames.add("cdnEnabled");
		ctMergeColumnNames.add("cdnURL");
		ctMergeColumnNames.add("displayDate");
		ctMergeColumnNames.add("expirationDate");
		ctMergeColumnNames.add("galleryEnabled");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("json");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPAttachmentFileEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the cp attachment file entry persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE,
			_SQL_COUNT_CPATTACHMENTFILEENTRY_WHERE,
			CPAttachmentFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPAttachmentFileEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CPAttachmentFileEntry::getUuid),
				CPAttachmentFileEntry::getGroupId),
			_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE, "",
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPAttachmentFileEntry::getUuid),
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, CPAttachmentFileEntry::getGroupId));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE,
				_SQL_COUNT_CPATTACHMENTFILEENTRY_WHERE,
				CPAttachmentFileEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPAttachmentFileEntry::getUuid),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPAttachmentFileEntry::getCompanyId));

		_collectionPersistenceFinderByFileEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByFileEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"fileEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFileEntryId", new String[] {Long.class.getName()},
					new String[] {"fileEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFileEntryId", new String[] {Long.class.getName()},
					new String[] {"fileEntryId"}, false),
				_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE,
				_SQL_COUNT_CPATTACHMENTFILEENTRY_WHERE,
				CPAttachmentFileEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "fileEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					CPAttachmentFileEntry::getFileEntryId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, false),
			_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE,
			_SQL_COUNT_CPATTACHMENTFILEENTRY_WHERE,
			CPAttachmentFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, CPAttachmentFileEntry::getClassNameId),
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "classPK", FinderColumn.Type.LONG,
				"=", true, true, CPAttachmentFileEntry::getClassPK));

		_collectionPersistenceFinderByLtD_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtD_S",
				new String[] {
					Date.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"displayDate", "status"}, true),
			null,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtD_S",
				new String[] {Date.class.getName(), Integer.class.getName()},
				new String[] {"displayDate", "status"}, false),
			_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE,
			_SQL_COUNT_CPATTACHMENTFILEENTRY_WHERE,
			CPAttachmentFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "displayDate", FinderColumn.Type.DATE,
				"<", true, true, CPAttachmentFileEntry::getDisplayDate),
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "status", FinderColumn.Type.INTEGER,
				"=", true, true, CPAttachmentFileEntry::getStatus));

		_collectionPersistenceFinderByG_C_F = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_F",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "classNameId", "fileEntryId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_F",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "classNameId", "fileEntryId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_F",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "classNameId", "fileEntryId"}, false),
			_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE,
			_SQL_COUNT_CPATTACHMENTFILEENTRY_WHERE,
			CPAttachmentFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, CPAttachmentFileEntry::getGroupId),
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, CPAttachmentFileEntry::getClassNameId),
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "fileEntryId", FinderColumn.Type.LONG,
				"=", true, true, CPAttachmentFileEntry::getFileEntryId));

		_collectionPersistenceFinderByC_C_F = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_F",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK", "fileEntryId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_F",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"classNameId", "classPK", "fileEntryId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_F",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"classNameId", "classPK", "fileEntryId"}, false),
			_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE,
			_SQL_COUNT_CPATTACHMENTFILEENTRY_WHERE,
			CPAttachmentFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, CPAttachmentFileEntry::getClassNameId),
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "classPK", FinderColumn.Type.LONG,
				"=", true, true, CPAttachmentFileEntry::getClassPK),
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "fileEntryId", FinderColumn.Type.LONG,
				"=", true, true, CPAttachmentFileEntry::getFileEntryId));

		_collectionPersistenceFinderByC_C_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK", "cdnURL"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"classNameId", "classPK", "cdnURL"}, 0, 4, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"classNameId", "classPK", "cdnURL"}, 0, 4, false,
				null),
			_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE,
			_SQL_COUNT_CPATTACHMENTFILEENTRY_WHERE,
			CPAttachmentFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, CPAttachmentFileEntry::getClassNameId),
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "classPK", FinderColumn.Type.LONG,
				"=", true, true, CPAttachmentFileEntry::getClassPK),
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "cdnURL", FinderColumn.Type.STRING,
				"=", true, true, CPAttachmentFileEntry::getCDNURL));

		_collectionPersistenceFinderByC_C_LtD_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_LtD_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"classNameId", "classPK", "displayDate", "status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_C_LtD_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Date.class.getName(), Integer.class.getName()
					},
					new String[] {
						"classNameId", "classPK", "displayDate", "status"
					},
					false),
				_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE,
				_SQL_COUNT_CPATTACHMENTFILEENTRY_WHERE,
				CPAttachmentFileEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					CPAttachmentFileEntry::getClassNameId),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, CPAttachmentFileEntry::getClassPK),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "displayDate",
					FinderColumn.Type.DATE, "<", true, true,
					CPAttachmentFileEntry::getDisplayDate),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					CPAttachmentFileEntry::getStatus));

		_collectionPersistenceFinderByC_C_T_ST =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_T_ST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"classNameId", "classPK", "type_", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_T_ST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {"classNameId", "classPK", "type_", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_C_T_ST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {"classNameId", "classPK", "type_", "status"},
					false),
				_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE,
				_SQL_COUNT_CPATTACHMENTFILEENTRY_WHERE,
				CPAttachmentFileEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					CPAttachmentFileEntry::getClassNameId),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, CPAttachmentFileEntry::getClassPK),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					CPAttachmentFileEntry::getType),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					CPAttachmentFileEntry::getStatus));

		_collectionPersistenceFinderByC_C_T_NotST =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_T_NotST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"classNameId", "classPK", "type_", "status"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByC_C_T_NotST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {"classNameId", "classPK", "type_", "status"},
					false),
				_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE,
				_SQL_COUNT_CPATTACHMENTFILEENTRY_WHERE,
				CPAttachmentFileEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					CPAttachmentFileEntry::getClassNameId),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, CPAttachmentFileEntry::getClassPK),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					CPAttachmentFileEntry::getType),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "status",
					FinderColumn.Type.INTEGER, "!=", true, true,
					CPAttachmentFileEntry::getStatus));

		_collectionPersistenceFinderByC_C_G_T_ST =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_G_T_ST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"classNameId", "classPK", "galleryEnabled", "type_",
						"status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_C_G_T_ST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"classNameId", "classPK", "galleryEnabled", "type_",
						"status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_C_G_T_ST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"classNameId", "classPK", "galleryEnabled", "type_",
						"status"
					},
					false),
				_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE,
				_SQL_COUNT_CPATTACHMENTFILEENTRY_WHERE,
				CPAttachmentFileEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					CPAttachmentFileEntry::getClassNameId),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, CPAttachmentFileEntry::getClassPK),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "galleryEnabled",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					CPAttachmentFileEntry::isGalleryEnabled),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					CPAttachmentFileEntry::getType),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					CPAttachmentFileEntry::getStatus));

		_collectionPersistenceFinderByC_C_G_T_NotST =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByC_C_G_T_NotST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"classNameId", "classPK", "galleryEnabled", "type_",
						"status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByC_C_G_T_NotST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"classNameId", "classPK", "galleryEnabled", "type_",
						"status"
					},
					false),
				_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE,
				_SQL_COUNT_CPATTACHMENTFILEENTRY_WHERE,
				CPAttachmentFileEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					CPAttachmentFileEntry::getClassNameId),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, CPAttachmentFileEntry::getClassPK),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "galleryEnabled",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					CPAttachmentFileEntry::isGalleryEnabled),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					CPAttachmentFileEntry::getType),
				new FinderColumn<>(
					"cpAttachmentFileEntry.", "status",
					FinderColumn.Type.INTEGER, "!=", true, true,
					CPAttachmentFileEntry::getStatus));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CPAttachmentFileEntry::getExternalReferenceCode),
				CPAttachmentFileEntry::getCompanyId),
			_SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE, "",
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CPAttachmentFileEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"cpAttachmentFileEntry.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, CPAttachmentFileEntry::getCompanyId));

		CPAttachmentFileEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPAttachmentFileEntryUtil.setPersistence(null);

		entityCache.removeCache(CPAttachmentFileEntryImpl.class.getName());
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
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CPAttachmentFileEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPATTACHMENTFILEENTRY =
		"SELECT cpAttachmentFileEntry FROM CPAttachmentFileEntry cpAttachmentFileEntry";

	private static final String _SQL_SELECT_CPATTACHMENTFILEENTRY_WHERE =
		"SELECT cpAttachmentFileEntry FROM CPAttachmentFileEntry cpAttachmentFileEntry WHERE ";

	private static final String _SQL_COUNT_CPATTACHMENTFILEENTRY_WHERE =
		"SELECT COUNT(cpAttachmentFileEntry) FROM CPAttachmentFileEntry cpAttachmentFileEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPAttachmentFileEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPAttachmentFileEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1694319760