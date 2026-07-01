/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.exception.DuplicateCommerceOrderAttachmentExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchOrderAttachmentException;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.model.CommerceOrderAttachmentTable;
import com.liferay.commerce.model.impl.CommerceOrderAttachmentImpl;
import com.liferay.commerce.model.impl.CommerceOrderAttachmentModelImpl;
import com.liferay.commerce.service.persistence.CommerceOrderAttachmentPersistence;
import com.liferay.commerce.service.persistence.CommerceOrderAttachmentUtil;
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
 * The persistence implementation for the commerce order attachment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceOrderAttachmentPersistence.class)
public class CommerceOrderAttachmentPersistenceImpl
	extends BasePersistenceImpl
		<CommerceOrderAttachment, NoSuchOrderAttachmentException>
	implements CommerceOrderAttachmentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceOrderAttachmentUtil</code> to access the commerce order attachment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceOrderAttachmentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceOrderAttachment, NoSuchOrderAttachmentException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce order attachments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderAttachmentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce order attachments
	 * @param end the upper bound of the range of commerce order attachments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order attachments
	 */
	@Override
	public List<CommerceOrderAttachment> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceOrderAttachment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce order attachment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order attachment
	 * @throws NoSuchOrderAttachmentException if a matching commerce order attachment could not be found
	 */
	@Override
	public CommerceOrderAttachment findByUuid_First(
			String uuid,
			OrderByComparator<CommerceOrderAttachment> orderByComparator)
		throws NoSuchOrderAttachmentException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce order attachment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order attachment, or <code>null</code> if a matching commerce order attachment could not be found
	 */
	@Override
	public CommerceOrderAttachment fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceOrderAttachment> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce order attachments where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce order attachments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce order attachments
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CommerceOrderAttachment, NoSuchOrderAttachmentException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the commerce order attachment where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchOrderAttachmentException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce order attachment
	 * @throws NoSuchOrderAttachmentException if a matching commerce order attachment could not be found
	 */
	@Override
	public CommerceOrderAttachment findByUUID_G(String uuid, long groupId)
		throws NoSuchOrderAttachmentException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the commerce order attachment where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order attachment, or <code>null</code> if a matching commerce order attachment could not be found
	 */
	@Override
	public CommerceOrderAttachment fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the commerce order attachment where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce order attachment that was removed
	 */
	@Override
	public CommerceOrderAttachment removeByUUID_G(String uuid, long groupId)
		throws NoSuchOrderAttachmentException {

		CommerceOrderAttachment commerceOrderAttachment = findByUUID_G(
			uuid, groupId);

		return remove(commerceOrderAttachment);
	}

	/**
	 * Returns the number of commerce order attachments where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce order attachments
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderAttachment, NoSuchOrderAttachmentException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce order attachments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderAttachmentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce order attachments
	 * @param end the upper bound of the range of commerce order attachments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order attachments
	 */
	@Override
	public List<CommerceOrderAttachment> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrderAttachment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order attachment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order attachment
	 * @throws NoSuchOrderAttachmentException if a matching commerce order attachment could not be found
	 */
	@Override
	public CommerceOrderAttachment findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceOrderAttachment> orderByComparator)
		throws NoSuchOrderAttachmentException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce order attachment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order attachment, or <code>null</code> if a matching commerce order attachment could not be found
	 */
	@Override
	public CommerceOrderAttachment fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceOrderAttachment> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce order attachments where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce order attachments where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce order attachments
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderAttachment, NoSuchOrderAttachmentException>
			_collectionPersistenceFinderByCommerceOrderId;

	/**
	 * Returns an ordered range of all the commerce order attachments where commerceOrderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderAttachmentModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param start the lower bound of the range of commerce order attachments
	 * @param end the upper bound of the range of commerce order attachments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order attachments
	 */
	@Override
	public List<CommerceOrderAttachment> findByCommerceOrderId(
		long commerceOrderId, int start, int end,
		OrderByComparator<CommerceOrderAttachment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceOrderId.find(
			finderCache, new Object[] {commerceOrderId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order attachment in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order attachment
	 * @throws NoSuchOrderAttachmentException if a matching commerce order attachment could not be found
	 */
	@Override
	public CommerceOrderAttachment findByCommerceOrderId_First(
			long commerceOrderId,
			OrderByComparator<CommerceOrderAttachment> orderByComparator)
		throws NoSuchOrderAttachmentException {

		return _collectionPersistenceFinderByCommerceOrderId.findFirst(
			finderCache, new Object[] {commerceOrderId}, orderByComparator);
	}

	/**
	 * Returns the first commerce order attachment in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order attachment, or <code>null</code> if a matching commerce order attachment could not be found
	 */
	@Override
	public CommerceOrderAttachment fetchByCommerceOrderId_First(
		long commerceOrderId,
		OrderByComparator<CommerceOrderAttachment> orderByComparator) {

		return _collectionPersistenceFinderByCommerceOrderId.fetchFirst(
			finderCache, new Object[] {commerceOrderId}, orderByComparator);
	}

	/**
	 * Removes all the commerce order attachments where commerceOrderId = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 */
	@Override
	public void removeByCommerceOrderId(long commerceOrderId) {
		_collectionPersistenceFinderByCommerceOrderId.remove(
			finderCache, new Object[] {commerceOrderId});
	}

	/**
	 * Returns the number of commerce order attachments where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @return the number of matching commerce order attachments
	 */
	@Override
	public int countByCommerceOrderId(long commerceOrderId) {
		return _collectionPersistenceFinderByCommerceOrderId.count(
			finderCache, new Object[] {commerceOrderId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderAttachment, NoSuchOrderAttachmentException>
			_collectionPersistenceFinderByC_R;

	/**
	 * Returns an ordered range of all the commerce order attachments where commerceOrderId = &#63; and restricted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderAttachmentModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param restricted the restricted
	 * @param start the lower bound of the range of commerce order attachments
	 * @param end the upper bound of the range of commerce order attachments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order attachments
	 */
	@Override
	public List<CommerceOrderAttachment> findByC_R(
		long commerceOrderId, boolean restricted, int start, int end,
		OrderByComparator<CommerceOrderAttachment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_R.find(
			finderCache, new Object[] {commerceOrderId, restricted}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order attachment in the ordered set where commerceOrderId = &#63; and restricted = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param restricted the restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order attachment
	 * @throws NoSuchOrderAttachmentException if a matching commerce order attachment could not be found
	 */
	@Override
	public CommerceOrderAttachment findByC_R_First(
			long commerceOrderId, boolean restricted,
			OrderByComparator<CommerceOrderAttachment> orderByComparator)
		throws NoSuchOrderAttachmentException {

		return _collectionPersistenceFinderByC_R.findFirst(
			finderCache, new Object[] {commerceOrderId, restricted},
			orderByComparator);
	}

	/**
	 * Returns the first commerce order attachment in the ordered set where commerceOrderId = &#63; and restricted = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param restricted the restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order attachment, or <code>null</code> if a matching commerce order attachment could not be found
	 */
	@Override
	public CommerceOrderAttachment fetchByC_R_First(
		long commerceOrderId, boolean restricted,
		OrderByComparator<CommerceOrderAttachment> orderByComparator) {

		return _collectionPersistenceFinderByC_R.fetchFirst(
			finderCache, new Object[] {commerceOrderId, restricted},
			orderByComparator);
	}

	/**
	 * Removes all the commerce order attachments where commerceOrderId = &#63; and restricted = &#63; from the database.
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
	 * Returns the number of commerce order attachments where commerceOrderId = &#63; and restricted = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param restricted the restricted
	 * @return the number of matching commerce order attachments
	 */
	@Override
	public int countByC_R(long commerceOrderId, boolean restricted) {
		return _collectionPersistenceFinderByC_R.count(
			finderCache, new Object[] {commerceOrderId, restricted});
	}

	private UniquePersistenceFinder
		<CommerceOrderAttachment, NoSuchOrderAttachmentException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce order attachment where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOrderAttachmentException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order attachment
	 * @throws NoSuchOrderAttachmentException if a matching commerce order attachment could not be found
	 */
	@Override
	public CommerceOrderAttachment findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderAttachmentException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce order attachment where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order attachment, or <code>null</code> if a matching commerce order attachment could not be found
	 */
	@Override
	public CommerceOrderAttachment fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce order attachment where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce order attachment that was removed
	 */
	@Override
	public CommerceOrderAttachment removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderAttachmentException {

		CommerceOrderAttachment commerceOrderAttachment = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceOrderAttachment);
	}

	/**
	 * Returns the number of commerce order attachments where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce order attachments
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceOrderAttachmentPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceOrderAttachment.class);

		setModelImplClass(CommerceOrderAttachmentImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceOrderAttachmentTable.INSTANCE);
	}

	/**
	 * Creates a new commerce order attachment with the primary key. Does not add the commerce order attachment to the database.
	 *
	 * @param commerceOrderAttachmentId the primary key for the new commerce order attachment
	 * @return the new commerce order attachment
	 */
	@Override
	public CommerceOrderAttachment create(long commerceOrderAttachmentId) {
		CommerceOrderAttachment commerceOrderAttachment =
			new CommerceOrderAttachmentImpl();

		commerceOrderAttachment.setNew(true);
		commerceOrderAttachment.setPrimaryKey(commerceOrderAttachmentId);

		String uuid = PortalUUIDUtil.generate();

		commerceOrderAttachment.setUuid(uuid);

		commerceOrderAttachment.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceOrderAttachment;
	}

	/**
	 * Removes the commerce order attachment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceOrderAttachmentId the primary key of the commerce order attachment
	 * @return the commerce order attachment that was removed
	 * @throws NoSuchOrderAttachmentException if a commerce order attachment with the primary key could not be found
	 */
	@Override
	public CommerceOrderAttachment remove(long commerceOrderAttachmentId)
		throws NoSuchOrderAttachmentException {

		return remove((Serializable)commerceOrderAttachmentId);
	}

	@Override
	protected CommerceOrderAttachment removeImpl(
		CommerceOrderAttachment commerceOrderAttachment) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceOrderAttachment)) {
				commerceOrderAttachment = (CommerceOrderAttachment)session.get(
					CommerceOrderAttachmentImpl.class,
					commerceOrderAttachment.getPrimaryKeyObj());
			}

			if (commerceOrderAttachment != null) {
				session.delete(commerceOrderAttachment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceOrderAttachment != null) {
			clearCache(commerceOrderAttachment);
		}

		return commerceOrderAttachment;
	}

	@Override
	public CommerceOrderAttachment updateImpl(
		CommerceOrderAttachment commerceOrderAttachment) {

		boolean isNew = commerceOrderAttachment.isNew();

		if (!(commerceOrderAttachment instanceof
				CommerceOrderAttachmentModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceOrderAttachment.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceOrderAttachment);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceOrderAttachment proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceOrderAttachment implementation " +
					commerceOrderAttachment.getClass());
		}

		CommerceOrderAttachmentModelImpl commerceOrderAttachmentModelImpl =
			(CommerceOrderAttachmentModelImpl)commerceOrderAttachment;

		if (Validator.isNull(commerceOrderAttachment.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceOrderAttachment.setUuid(uuid);
		}

		if (Validator.isNull(
				commerceOrderAttachment.getExternalReferenceCode())) {

			commerceOrderAttachment.setExternalReferenceCode(
				commerceOrderAttachment.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceOrderAttachmentModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceOrderAttachment.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceOrderAttachment.getCompanyId();

					long groupId = commerceOrderAttachment.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = commerceOrderAttachment.getPrimaryKey();
					}

					try {
						commerceOrderAttachment.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceOrderAttachment.class.getName(),
								classPK, ContentTypes.TEXT_HTML,
								Sanitizer.MODE_ALL,
								commerceOrderAttachment.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceOrderAttachment ercCommerceOrderAttachment = fetchByERC_C(
				commerceOrderAttachment.getExternalReferenceCode(),
				commerceOrderAttachment.getCompanyId());

			if (isNew) {
				if (ercCommerceOrderAttachment != null) {
					throw new DuplicateCommerceOrderAttachmentExternalReferenceCodeException(
						"Duplicate commerce order attachment with external reference code " +
							commerceOrderAttachment.getExternalReferenceCode() +
								" and company " +
									commerceOrderAttachment.getCompanyId());
				}
			}
			else {
				if ((ercCommerceOrderAttachment != null) &&
					(commerceOrderAttachment.getCommerceOrderAttachmentId() !=
						ercCommerceOrderAttachment.
							getCommerceOrderAttachmentId())) {

					throw new DuplicateCommerceOrderAttachmentExternalReferenceCodeException(
						"Duplicate commerce order attachment with external reference code " +
							commerceOrderAttachment.getExternalReferenceCode() +
								" and company " +
									commerceOrderAttachment.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceOrderAttachment.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceOrderAttachment.setCreateDate(date);
			}
			else {
				commerceOrderAttachment.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceOrderAttachmentModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceOrderAttachment.setModifiedDate(date);
			}
			else {
				commerceOrderAttachment.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceOrderAttachment);
			}
			else {
				commerceOrderAttachment =
					(CommerceOrderAttachment)session.merge(
						commerceOrderAttachment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceOrderAttachment, false);

		if (isNew) {
			commerceOrderAttachment.setNew(false);
		}

		commerceOrderAttachment.resetOriginalValues();

		return commerceOrderAttachment;
	}

	/**
	 * Returns the commerce order attachment with the primary key or throws a <code>NoSuchOrderAttachmentException</code> if it could not be found.
	 *
	 * @param commerceOrderAttachmentId the primary key of the commerce order attachment
	 * @return the commerce order attachment
	 * @throws NoSuchOrderAttachmentException if a commerce order attachment with the primary key could not be found
	 */
	@Override
	public CommerceOrderAttachment findByPrimaryKey(
			long commerceOrderAttachmentId)
		throws NoSuchOrderAttachmentException {

		return findByPrimaryKey((Serializable)commerceOrderAttachmentId);
	}

	/**
	 * Returns the commerce order attachment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceOrderAttachmentId the primary key of the commerce order attachment
	 * @return the commerce order attachment, or <code>null</code> if a commerce order attachment with the primary key could not be found
	 */
	@Override
	public CommerceOrderAttachment fetchByPrimaryKey(
		long commerceOrderAttachmentId) {

		return fetchByPrimaryKey((Serializable)commerceOrderAttachmentId);
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
		return "commerceOrderAttachmentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEORDERATTACHMENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceOrderAttachmentModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce order attachment persistence.
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
			_SQL_SELECT_COMMERCEORDERATTACHMENT_WHERE,
			_SQL_COUNT_COMMERCEORDERATTACHMENT_WHERE,
			CommerceOrderAttachmentModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"commerceOrderAttachment.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceOrderAttachment::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CommerceOrderAttachment::getUuid),
				CommerceOrderAttachment::getGroupId),
			_SQL_SELECT_COMMERCEORDERATTACHMENT_WHERE, "",
			new FinderColumn<>(
				"commerceOrderAttachment.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceOrderAttachment::getUuid),
			new FinderColumn<>(
				"commerceOrderAttachment.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, CommerceOrderAttachment::getGroupId));

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
				_SQL_SELECT_COMMERCEORDERATTACHMENT_WHERE,
				_SQL_COUNT_COMMERCEORDERATTACHMENT_WHERE,
				CommerceOrderAttachmentModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceOrderAttachment.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceOrderAttachment::getUuid),
				new FinderColumn<>(
					"commerceOrderAttachment.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceOrderAttachment::getCompanyId));

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
				_SQL_SELECT_COMMERCEORDERATTACHMENT_WHERE,
				_SQL_COUNT_COMMERCEORDERATTACHMENT_WHERE,
				CommerceOrderAttachmentModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceOrderAttachment.", "commerceOrderId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceOrderAttachment::getCommerceOrderId));

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
			_SQL_SELECT_COMMERCEORDERATTACHMENT_WHERE,
			_SQL_COUNT_COMMERCEORDERATTACHMENT_WHERE,
			CommerceOrderAttachmentModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"commerceOrderAttachment.", "commerceOrderId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceOrderAttachment::getCommerceOrderId),
			new FinderColumn<>(
				"commerceOrderAttachment.", "restricted",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				CommerceOrderAttachment::isRestricted));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CommerceOrderAttachment::getExternalReferenceCode),
				CommerceOrderAttachment::getCompanyId),
			_SQL_SELECT_COMMERCEORDERATTACHMENT_WHERE, "",
			new FinderColumn<>(
				"commerceOrderAttachment.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceOrderAttachment::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceOrderAttachment.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, CommerceOrderAttachment::getCompanyId));

		CommerceOrderAttachmentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceOrderAttachmentUtil.setPersistence(null);

		entityCache.removeCache(CommerceOrderAttachmentImpl.class.getName());
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
		CommerceOrderAttachmentModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEORDERATTACHMENT =
		"SELECT commerceOrderAttachment FROM CommerceOrderAttachment commerceOrderAttachment";

	private static final String _SQL_SELECT_COMMERCEORDERATTACHMENT_WHERE =
		"SELECT commerceOrderAttachment FROM CommerceOrderAttachment commerceOrderAttachment WHERE ";

	private static final String _SQL_COUNT_COMMERCEORDERATTACHMENT_WHERE =
		"SELECT COUNT(commerceOrderAttachment) FROM CommerceOrderAttachment commerceOrderAttachment WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1386505831