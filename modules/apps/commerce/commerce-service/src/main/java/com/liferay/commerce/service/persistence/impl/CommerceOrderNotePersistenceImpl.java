/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.exception.DuplicateCommerceOrderNoteExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchOrderNoteException;
import com.liferay.commerce.model.CommerceOrderNote;
import com.liferay.commerce.model.CommerceOrderNoteTable;
import com.liferay.commerce.model.impl.CommerceOrderNoteImpl;
import com.liferay.commerce.model.impl.CommerceOrderNoteModelImpl;
import com.liferay.commerce.service.persistence.CommerceOrderNotePersistence;
import com.liferay.commerce.service.persistence.CommerceOrderNoteUtil;
import com.liferay.commerce.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
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

import java.util.Date;
import java.util.HashMap;
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
 * The persistence implementation for the commerce order note service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceOrderNotePersistence.class)
public class CommerceOrderNotePersistenceImpl
	extends BasePersistenceImpl<CommerceOrderNote, NoSuchOrderNoteException>
	implements CommerceOrderNotePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceOrderNoteUtil</code> to access the commerce order note persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceOrderNoteImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceOrderNote, NoSuchOrderNoteException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce order notes where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderNoteModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce order notes
	 * @param end the upper bound of the range of commerce order notes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order notes
	 */
	@Override
	public List<CommerceOrderNote> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceOrderNote> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce order note in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order note
	 * @throws NoSuchOrderNoteException if a matching commerce order note could not be found
	 */
	@Override
	public CommerceOrderNote findByUuid_First(
			String uuid, OrderByComparator<CommerceOrderNote> orderByComparator)
		throws NoSuchOrderNoteException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce order note in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order note, or <code>null</code> if a matching commerce order note could not be found
	 */
	@Override
	public CommerceOrderNote fetchByUuid_First(
		String uuid, OrderByComparator<CommerceOrderNote> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce order notes where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce order notes where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce order notes
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<CommerceOrderNote, NoSuchOrderNoteException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the commerce order note where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchOrderNoteException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce order note
	 * @throws NoSuchOrderNoteException if a matching commerce order note could not be found
	 */
	@Override
	public CommerceOrderNote findByUUID_G(String uuid, long groupId)
		throws NoSuchOrderNoteException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the commerce order note where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order note, or <code>null</code> if a matching commerce order note could not be found
	 */
	@Override
	public CommerceOrderNote fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the commerce order note where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce order note that was removed
	 */
	@Override
	public CommerceOrderNote removeByUUID_G(String uuid, long groupId)
		throws NoSuchOrderNoteException {

		CommerceOrderNote commerceOrderNote = findByUUID_G(uuid, groupId);

		return remove(commerceOrderNote);
	}

	/**
	 * Returns the number of commerce order notes where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce order notes
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderNote, NoSuchOrderNoteException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce order notes where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderNoteModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce order notes
	 * @param end the upper bound of the range of commerce order notes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order notes
	 */
	@Override
	public List<CommerceOrderNote> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrderNote> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order note in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order note
	 * @throws NoSuchOrderNoteException if a matching commerce order note could not be found
	 */
	@Override
	public CommerceOrderNote findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceOrderNote> orderByComparator)
		throws NoSuchOrderNoteException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce order note in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order note, or <code>null</code> if a matching commerce order note could not be found
	 */
	@Override
	public CommerceOrderNote fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceOrderNote> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce order notes where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce order notes where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce order notes
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderNote, NoSuchOrderNoteException>
			_collectionPersistenceFinderByCommerceOrderId;

	/**
	 * Returns an ordered range of all the commerce order notes where commerceOrderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderNoteModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param start the lower bound of the range of commerce order notes
	 * @param end the upper bound of the range of commerce order notes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order notes
	 */
	@Override
	public List<CommerceOrderNote> findByCommerceOrderId(
		long commerceOrderId, int start, int end,
		OrderByComparator<CommerceOrderNote> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceOrderId.find(
			finderCache, new Object[] {commerceOrderId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order note in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order note
	 * @throws NoSuchOrderNoteException if a matching commerce order note could not be found
	 */
	@Override
	public CommerceOrderNote findByCommerceOrderId_First(
			long commerceOrderId,
			OrderByComparator<CommerceOrderNote> orderByComparator)
		throws NoSuchOrderNoteException {

		return _collectionPersistenceFinderByCommerceOrderId.findFirst(
			finderCache, new Object[] {commerceOrderId}, orderByComparator);
	}

	/**
	 * Returns the first commerce order note in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order note, or <code>null</code> if a matching commerce order note could not be found
	 */
	@Override
	public CommerceOrderNote fetchByCommerceOrderId_First(
		long commerceOrderId,
		OrderByComparator<CommerceOrderNote> orderByComparator) {

		return _collectionPersistenceFinderByCommerceOrderId.fetchFirst(
			finderCache, new Object[] {commerceOrderId}, orderByComparator);
	}

	/**
	 * Removes all the commerce order notes where commerceOrderId = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 */
	@Override
	public void removeByCommerceOrderId(long commerceOrderId) {
		_collectionPersistenceFinderByCommerceOrderId.remove(
			finderCache, new Object[] {commerceOrderId});
	}

	/**
	 * Returns the number of commerce order notes where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @return the number of matching commerce order notes
	 */
	@Override
	public int countByCommerceOrderId(long commerceOrderId) {
		return _collectionPersistenceFinderByCommerceOrderId.count(
			finderCache, new Object[] {commerceOrderId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderNote, NoSuchOrderNoteException>
			_collectionPersistenceFinderByC_R;

	/**
	 * Returns an ordered range of all the commerce order notes where commerceOrderId = &#63; and restricted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderNoteModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param restricted the restricted
	 * @param start the lower bound of the range of commerce order notes
	 * @param end the upper bound of the range of commerce order notes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order notes
	 */
	@Override
	public List<CommerceOrderNote> findByC_R(
		long commerceOrderId, boolean restricted, int start, int end,
		OrderByComparator<CommerceOrderNote> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_R.find(
			finderCache, new Object[] {commerceOrderId, restricted}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order note in the ordered set where commerceOrderId = &#63; and restricted = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param restricted the restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order note
	 * @throws NoSuchOrderNoteException if a matching commerce order note could not be found
	 */
	@Override
	public CommerceOrderNote findByC_R_First(
			long commerceOrderId, boolean restricted,
			OrderByComparator<CommerceOrderNote> orderByComparator)
		throws NoSuchOrderNoteException {

		return _collectionPersistenceFinderByC_R.findFirst(
			finderCache, new Object[] {commerceOrderId, restricted},
			orderByComparator);
	}

	/**
	 * Returns the first commerce order note in the ordered set where commerceOrderId = &#63; and restricted = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param restricted the restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order note, or <code>null</code> if a matching commerce order note could not be found
	 */
	@Override
	public CommerceOrderNote fetchByC_R_First(
		long commerceOrderId, boolean restricted,
		OrderByComparator<CommerceOrderNote> orderByComparator) {

		return _collectionPersistenceFinderByC_R.fetchFirst(
			finderCache, new Object[] {commerceOrderId, restricted},
			orderByComparator);
	}

	/**
	 * Removes all the commerce order notes where commerceOrderId = &#63; and restricted = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param restricted the restricted
	 */
	@Override
	public void removeByC_R(long commerceOrderId, boolean restricted) {
		_collectionPersistenceFinderByC_R.remove(
			finderCache, new Object[] {commerceOrderId, restricted});
	}

	/**
	 * Returns the number of commerce order notes where commerceOrderId = &#63; and restricted = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param restricted the restricted
	 * @return the number of matching commerce order notes
	 */
	@Override
	public int countByC_R(long commerceOrderId, boolean restricted) {
		return _collectionPersistenceFinderByC_R.count(
			finderCache, new Object[] {commerceOrderId, restricted});
	}

	private UniquePersistenceFinder<CommerceOrderNote, NoSuchOrderNoteException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce order note where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOrderNoteException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order note
	 * @throws NoSuchOrderNoteException if a matching commerce order note could not be found
	 */
	@Override
	public CommerceOrderNote findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderNoteException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce order note where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order note, or <code>null</code> if a matching commerce order note could not be found
	 */
	@Override
	public CommerceOrderNote fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce order note where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce order note that was removed
	 */
	@Override
	public CommerceOrderNote removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderNoteException {

		CommerceOrderNote commerceOrderNote = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceOrderNote);
	}

	/**
	 * Returns the number of commerce order notes where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce order notes
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceOrderNotePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceOrderNote.class);

		setModelImplClass(CommerceOrderNoteImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceOrderNoteTable.INSTANCE);
	}

	/**
	 * Creates a new commerce order note with the primary key. Does not add the commerce order note to the database.
	 *
	 * @param commerceOrderNoteId the primary key for the new commerce order note
	 * @return the new commerce order note
	 */
	@Override
	public CommerceOrderNote create(long commerceOrderNoteId) {
		CommerceOrderNote commerceOrderNote = new CommerceOrderNoteImpl();

		commerceOrderNote.setNew(true);
		commerceOrderNote.setPrimaryKey(commerceOrderNoteId);

		String uuid = PortalUUIDUtil.generate();

		commerceOrderNote.setUuid(uuid);

		commerceOrderNote.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceOrderNote;
	}

	/**
	 * Removes the commerce order note with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceOrderNoteId the primary key of the commerce order note
	 * @return the commerce order note that was removed
	 * @throws NoSuchOrderNoteException if a commerce order note with the primary key could not be found
	 */
	@Override
	public CommerceOrderNote remove(long commerceOrderNoteId)
		throws NoSuchOrderNoteException {

		return remove((Serializable)commerceOrderNoteId);
	}

	@Override
	protected CommerceOrderNote removeImpl(
		CommerceOrderNote commerceOrderNote) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceOrderNote)) {
				commerceOrderNote = (CommerceOrderNote)session.get(
					CommerceOrderNoteImpl.class,
					commerceOrderNote.getPrimaryKeyObj());
			}

			if (commerceOrderNote != null) {
				session.delete(commerceOrderNote);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceOrderNote != null) {
			clearCache(commerceOrderNote);
		}

		return commerceOrderNote;
	}

	@Override
	public CommerceOrderNote updateImpl(CommerceOrderNote commerceOrderNote) {
		boolean isNew = commerceOrderNote.isNew();

		if (!(commerceOrderNote instanceof CommerceOrderNoteModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceOrderNote.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceOrderNote);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceOrderNote proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceOrderNote implementation " +
					commerceOrderNote.getClass());
		}

		CommerceOrderNoteModelImpl commerceOrderNoteModelImpl =
			(CommerceOrderNoteModelImpl)commerceOrderNote;

		if (Validator.isNull(commerceOrderNote.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceOrderNote.setUuid(uuid);
		}

		if (Validator.isNull(commerceOrderNote.getExternalReferenceCode())) {
			commerceOrderNote.setExternalReferenceCode(
				commerceOrderNote.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceOrderNoteModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceOrderNote.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceOrderNote.getCompanyId();

					long groupId = commerceOrderNote.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = commerceOrderNote.getPrimaryKey();
					}

					try {
						commerceOrderNote.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceOrderNote.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commerceOrderNote.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceOrderNote ercCommerceOrderNote = fetchByERC_C(
				commerceOrderNote.getExternalReferenceCode(),
				commerceOrderNote.getCompanyId());

			if (isNew) {
				if (ercCommerceOrderNote != null) {
					throw new DuplicateCommerceOrderNoteExternalReferenceCodeException(
						"Duplicate commerce order note with external reference code " +
							commerceOrderNote.getExternalReferenceCode() +
								" and company " +
									commerceOrderNote.getCompanyId());
				}
			}
			else {
				if ((ercCommerceOrderNote != null) &&
					(commerceOrderNote.getCommerceOrderNoteId() !=
						ercCommerceOrderNote.getCommerceOrderNoteId())) {

					throw new DuplicateCommerceOrderNoteExternalReferenceCodeException(
						"Duplicate commerce order note with external reference code " +
							commerceOrderNote.getExternalReferenceCode() +
								" and company " +
									commerceOrderNote.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceOrderNote.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceOrderNote.setCreateDate(date);
			}
			else {
				commerceOrderNote.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceOrderNoteModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceOrderNote.setModifiedDate(date);
			}
			else {
				commerceOrderNote.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceOrderNote);
			}
			else {
				commerceOrderNote = (CommerceOrderNote)session.merge(
					commerceOrderNote);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceOrderNote, false);

		if (isNew) {
			commerceOrderNote.setNew(false);
		}

		commerceOrderNote.resetOriginalValues();

		return commerceOrderNote;
	}

	/**
	 * Returns the commerce order note with the primary key or throws a <code>NoSuchOrderNoteException</code> if it could not be found.
	 *
	 * @param commerceOrderNoteId the primary key of the commerce order note
	 * @return the commerce order note
	 * @throws NoSuchOrderNoteException if a commerce order note with the primary key could not be found
	 */
	@Override
	public CommerceOrderNote findByPrimaryKey(long commerceOrderNoteId)
		throws NoSuchOrderNoteException {

		return findByPrimaryKey((Serializable)commerceOrderNoteId);
	}

	/**
	 * Returns the commerce order note with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceOrderNoteId the primary key of the commerce order note
	 * @return the commerce order note, or <code>null</code> if a commerce order note with the primary key could not be found
	 */
	@Override
	public CommerceOrderNote fetchByPrimaryKey(long commerceOrderNoteId) {
		return fetchByPrimaryKey((Serializable)commerceOrderNoteId);
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
		return "commerceOrderNoteId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEORDERNOTE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceOrderNoteModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce order note persistence.
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
			_SQL_SELECT_COMMERCEORDERNOTE_WHERE,
			_SQL_COUNT_COMMERCEORDERNOTE_WHERE,
			CommerceOrderNoteModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"commerceOrderNote.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CommerceOrderNote::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CommerceOrderNote::getUuid),
				CommerceOrderNote::getGroupId),
			_SQL_SELECT_COMMERCEORDERNOTE_WHERE, "",
			new FinderColumn<>(
				"commerceOrderNote.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CommerceOrderNote::getUuid),
			new FinderColumn<>(
				"commerceOrderNote.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CommerceOrderNote::getGroupId));

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
				_SQL_SELECT_COMMERCEORDERNOTE_WHERE,
				_SQL_COUNT_COMMERCEORDERNOTE_WHERE,
				CommerceOrderNoteModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"commerceOrderNote.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceOrderNote::getUuid),
				new FinderColumn<>(
					"commerceOrderNote.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceOrderNote::getCompanyId));

		_collectionPersistenceFinderByCommerceOrderId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceOrderId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceOrderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceOrderId",
					new String[] {Long.class.getName()},
					new String[] {"commerceOrderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceOrderId",
					new String[] {Long.class.getName()},
					new String[] {"commerceOrderId"}, false),
				_SQL_SELECT_COMMERCEORDERNOTE_WHERE,
				_SQL_COUNT_COMMERCEORDERNOTE_WHERE,
				CommerceOrderNoteModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"commerceOrderNote.", "commerceOrderId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceOrderNote::getCommerceOrderId));

		_collectionPersistenceFinderByC_R = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_R",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"commerceOrderId", "restricted"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_R",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"commerceOrderId", "restricted"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_R",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"commerceOrderId", "restricted"}, false),
			_SQL_SELECT_COMMERCEORDERNOTE_WHERE,
			_SQL_COUNT_COMMERCEORDERNOTE_WHERE,
			CommerceOrderNoteModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"commerceOrderNote.", "commerceOrderId", FinderColumn.Type.LONG,
				"=", true, true, CommerceOrderNote::getCommerceOrderId),
			new FinderColumn<>(
				"commerceOrderNote.", "restricted", FinderColumn.Type.BOOLEAN,
				"=", true, true, CommerceOrderNote::isRestricted));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CommerceOrderNote::getExternalReferenceCode),
				CommerceOrderNote::getCompanyId),
			_SQL_SELECT_COMMERCEORDERNOTE_WHERE, "",
			new FinderColumn<>(
				"commerceOrderNote.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceOrderNote::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceOrderNote.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CommerceOrderNote::getCompanyId));

		CommerceOrderNoteUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceOrderNoteUtil.setPersistence(null);

		entityCache.removeCache(CommerceOrderNoteImpl.class.getName());
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
		CommerceOrderNoteModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEORDERNOTE =
		"SELECT commerceOrderNote FROM CommerceOrderNote commerceOrderNote";

	private static final String _SQL_SELECT_COMMERCEORDERNOTE_WHERE =
		"SELECT commerceOrderNote FROM CommerceOrderNote commerceOrderNote WHERE ";

	private static final String _SQL_COUNT_COMMERCEORDERNOTE_WHERE =
		"SELECT COUNT(commerceOrderNote) FROM CommerceOrderNote commerceOrderNote WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1659075659