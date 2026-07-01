/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.service.persistence.impl;

import com.liferay.client.extension.exception.DuplicateClientExtensionEntryRelExternalReferenceCodeException;
import com.liferay.client.extension.exception.NoSuchClientExtensionEntryRelException;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.model.ClientExtensionEntryRelTable;
import com.liferay.client.extension.model.impl.ClientExtensionEntryRelImpl;
import com.liferay.client.extension.model.impl.ClientExtensionEntryRelModelImpl;
import com.liferay.client.extension.service.persistence.ClientExtensionEntryRelPersistence;
import com.liferay.client.extension.service.persistence.ClientExtensionEntryRelUtil;
import com.liferay.client.extension.service.persistence.impl.constants.ClientExtensionPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
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
 * The persistence implementation for the client extension entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = ClientExtensionEntryRelPersistence.class)
public class ClientExtensionEntryRelPersistenceImpl
	extends BasePersistenceImpl
		<ClientExtensionEntryRel, NoSuchClientExtensionEntryRelException>
	implements ClientExtensionEntryRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ClientExtensionEntryRelUtil</code> to access the client extension entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ClientExtensionEntryRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ClientExtensionEntryRel, NoSuchClientExtensionEntryRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the client extension entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ClientExtensionEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of client extension entry rels
	 * @param end the upper bound of the range of client extension entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching client extension entry rels
	 */
	@Override
	public List<ClientExtensionEntryRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ClientExtensionEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first client extension entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching client extension entry rel
	 * @throws NoSuchClientExtensionEntryRelException if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel findByUuid_First(
			String uuid,
			OrderByComparator<ClientExtensionEntryRel> orderByComparator)
		throws NoSuchClientExtensionEntryRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first client extension entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching client extension entry rel, or <code>null</code> if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel fetchByUuid_First(
		String uuid,
		OrderByComparator<ClientExtensionEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the client extension entry rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of client extension entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching client extension entry rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<ClientExtensionEntryRel, NoSuchClientExtensionEntryRelException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the client extension entry rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchClientExtensionEntryRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching client extension entry rel
	 * @throws NoSuchClientExtensionEntryRelException if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel findByUUID_G(String uuid, long groupId)
		throws NoSuchClientExtensionEntryRelException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the client extension entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching client extension entry rel, or <code>null</code> if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the client extension entry rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the client extension entry rel that was removed
	 */
	@Override
	public ClientExtensionEntryRel removeByUUID_G(String uuid, long groupId)
		throws NoSuchClientExtensionEntryRelException {

		ClientExtensionEntryRel clientExtensionEntryRel = findByUUID_G(
			uuid, groupId);

		return remove(clientExtensionEntryRel);
	}

	/**
	 * Returns the number of client extension entry rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching client extension entry rels
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<ClientExtensionEntryRel, NoSuchClientExtensionEntryRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the client extension entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ClientExtensionEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of client extension entry rels
	 * @param end the upper bound of the range of client extension entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching client extension entry rels
	 */
	@Override
	public List<ClientExtensionEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ClientExtensionEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first client extension entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching client extension entry rel
	 * @throws NoSuchClientExtensionEntryRelException if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ClientExtensionEntryRel> orderByComparator)
		throws NoSuchClientExtensionEntryRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first client extension entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching client extension entry rel, or <code>null</code> if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ClientExtensionEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the client extension entry rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of client extension entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching client extension entry rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ClientExtensionEntryRel, NoSuchClientExtensionEntryRelException>
			_collectionPersistenceFinderByType;

	/**
	 * Returns an ordered range of all the client extension entry rels where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ClientExtensionEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of client extension entry rels
	 * @param end the upper bound of the range of client extension entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching client extension entry rels
	 */
	@Override
	public List<ClientExtensionEntryRel> findByType(
		String type, int start, int end,
		OrderByComparator<ClientExtensionEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByType.find(
			finderCache, new Object[] {type}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first client extension entry rel in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching client extension entry rel
	 * @throws NoSuchClientExtensionEntryRelException if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel findByType_First(
			String type,
			OrderByComparator<ClientExtensionEntryRel> orderByComparator)
		throws NoSuchClientExtensionEntryRelException {

		return _collectionPersistenceFinderByType.findFirst(
			finderCache, new Object[] {type}, orderByComparator);
	}

	/**
	 * Returns the first client extension entry rel in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching client extension entry rel, or <code>null</code> if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel fetchByType_First(
		String type,
		OrderByComparator<ClientExtensionEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByType.fetchFirst(
			finderCache, new Object[] {type}, orderByComparator);
	}

	/**
	 * Removes all the client extension entry rels where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	@Override
	public void removeByType(String type) {
		_collectionPersistenceFinderByType.remove(
			finderCache, new Object[] {type});
	}

	/**
	 * Returns the number of client extension entry rels where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching client extension entry rels
	 */
	@Override
	public int countByType(String type) {
		return _collectionPersistenceFinderByType.count(
			finderCache, new Object[] {type});
	}

	private CollectionPersistenceFinder
		<ClientExtensionEntryRel, NoSuchClientExtensionEntryRelException>
			_collectionPersistenceFinderByC_CETERC;

	/**
	 * Returns an ordered range of all the client extension entry rels where companyId = &#63; and cetExternalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ClientExtensionEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param cetExternalReferenceCode the cet external reference code
	 * @param start the lower bound of the range of client extension entry rels
	 * @param end the upper bound of the range of client extension entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching client extension entry rels
	 */
	@Override
	public List<ClientExtensionEntryRel> findByC_CETERC(
		long companyId, String cetExternalReferenceCode, int start, int end,
		OrderByComparator<ClientExtensionEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CETERC.find(
			finderCache, new Object[] {companyId, cetExternalReferenceCode},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first client extension entry rel in the ordered set where companyId = &#63; and cetExternalReferenceCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param cetExternalReferenceCode the cet external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching client extension entry rel
	 * @throws NoSuchClientExtensionEntryRelException if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel findByC_CETERC_First(
			long companyId, String cetExternalReferenceCode,
			OrderByComparator<ClientExtensionEntryRel> orderByComparator)
		throws NoSuchClientExtensionEntryRelException {

		return _collectionPersistenceFinderByC_CETERC.findFirst(
			finderCache, new Object[] {companyId, cetExternalReferenceCode},
			orderByComparator);
	}

	/**
	 * Returns the first client extension entry rel in the ordered set where companyId = &#63; and cetExternalReferenceCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param cetExternalReferenceCode the cet external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching client extension entry rel, or <code>null</code> if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel fetchByC_CETERC_First(
		long companyId, String cetExternalReferenceCode,
		OrderByComparator<ClientExtensionEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByC_CETERC.fetchFirst(
			finderCache, new Object[] {companyId, cetExternalReferenceCode},
			orderByComparator);
	}

	/**
	 * Removes all the client extension entry rels where companyId = &#63; and cetExternalReferenceCode = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param cetExternalReferenceCode the cet external reference code
	 */
	@Override
	public void removeByC_CETERC(
		long companyId, String cetExternalReferenceCode) {

		_collectionPersistenceFinderByC_CETERC.remove(
			finderCache, new Object[] {companyId, cetExternalReferenceCode});
	}

	/**
	 * Returns the number of client extension entry rels where companyId = &#63; and cetExternalReferenceCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param cetExternalReferenceCode the cet external reference code
	 * @return the number of matching client extension entry rels
	 */
	@Override
	public int countByC_CETERC(
		long companyId, String cetExternalReferenceCode) {

		return _collectionPersistenceFinderByC_CETERC.count(
			finderCache, new Object[] {companyId, cetExternalReferenceCode});
	}

	private CollectionPersistenceFinder
		<ClientExtensionEntryRel, NoSuchClientExtensionEntryRelException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the client extension entry rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ClientExtensionEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of client extension entry rels
	 * @param end the upper bound of the range of client extension entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching client extension entry rels
	 */
	@Override
	public List<ClientExtensionEntryRel> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<ClientExtensionEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first client extension entry rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching client extension entry rel
	 * @throws NoSuchClientExtensionEntryRelException if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<ClientExtensionEntryRel> orderByComparator)
		throws NoSuchClientExtensionEntryRelException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first client extension entry rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching client extension entry rel, or <code>null</code> if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<ClientExtensionEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the client extension entry rels where classNameId = &#63; and classPK = &#63; from the database.
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
	 * Returns the number of client extension entry rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching client extension entry rels
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<ClientExtensionEntryRel, NoSuchClientExtensionEntryRelException>
			_collectionPersistenceFinderByC_C_T;

	/**
	 * Returns an ordered range of all the client extension entry rels where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ClientExtensionEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of client extension entry rels
	 * @param end the upper bound of the range of client extension entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching client extension entry rels
	 */
	@Override
	public List<ClientExtensionEntryRel> findByC_C_T(
		long classNameId, long classPK, String type, int start, int end,
		OrderByComparator<ClientExtensionEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_T.find(
			finderCache, new Object[] {classNameId, classPK, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first client extension entry rel in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching client extension entry rel
	 * @throws NoSuchClientExtensionEntryRelException if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel findByC_C_T_First(
			long classNameId, long classPK, String type,
			OrderByComparator<ClientExtensionEntryRel> orderByComparator)
		throws NoSuchClientExtensionEntryRelException {

		return _collectionPersistenceFinderByC_C_T.findFirst(
			finderCache, new Object[] {classNameId, classPK, type},
			orderByComparator);
	}

	/**
	 * Returns the first client extension entry rel in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching client extension entry rel, or <code>null</code> if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel fetchByC_C_T_First(
		long classNameId, long classPK, String type,
		OrderByComparator<ClientExtensionEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByC_C_T.fetchFirst(
			finderCache, new Object[] {classNameId, classPK, type},
			orderByComparator);
	}

	/**
	 * Removes all the client extension entry rels where classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	@Override
	public void removeByC_C_T(long classNameId, long classPK, String type) {
		_collectionPersistenceFinderByC_C_T.remove(
			finderCache, new Object[] {classNameId, classPK, type});
	}

	/**
	 * Returns the number of client extension entry rels where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching client extension entry rels
	 */
	@Override
	public int countByC_C_T(long classNameId, long classPK, String type) {
		return _collectionPersistenceFinderByC_C_T.count(
			finderCache, new Object[] {classNameId, classPK, type});
	}

	private UniquePersistenceFinder
		<ClientExtensionEntryRel, NoSuchClientExtensionEntryRelException>
			_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the client extension entry rel where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchClientExtensionEntryRelException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching client extension entry rel
	 * @throws NoSuchClientExtensionEntryRelException if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchClientExtensionEntryRelException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the client extension entry rel where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching client extension entry rel, or <code>null</code> if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the client extension entry rel where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the client extension entry rel that was removed
	 */
	@Override
	public ClientExtensionEntryRel removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchClientExtensionEntryRelException {

		ClientExtensionEntryRel clientExtensionEntryRel = findByERC_G(
			externalReferenceCode, groupId);

		return remove(clientExtensionEntryRel);
	}

	/**
	 * Returns the number of client extension entry rels where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching client extension entry rels
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public ClientExtensionEntryRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ClientExtensionEntryRel.class);

		setModelImplClass(ClientExtensionEntryRelImpl.class);
		setModelPKClass(long.class);

		setTable(ClientExtensionEntryRelTable.INSTANCE);
	}

	/**
	 * Creates a new client extension entry rel with the primary key. Does not add the client extension entry rel to the database.
	 *
	 * @param clientExtensionEntryRelId the primary key for the new client extension entry rel
	 * @return the new client extension entry rel
	 */
	@Override
	public ClientExtensionEntryRel create(long clientExtensionEntryRelId) {
		ClientExtensionEntryRel clientExtensionEntryRel =
			new ClientExtensionEntryRelImpl();

		clientExtensionEntryRel.setNew(true);
		clientExtensionEntryRel.setPrimaryKey(clientExtensionEntryRelId);

		String uuid = PortalUUIDUtil.generate();

		clientExtensionEntryRel.setUuid(uuid);

		clientExtensionEntryRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return clientExtensionEntryRel;
	}

	/**
	 * Removes the client extension entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param clientExtensionEntryRelId the primary key of the client extension entry rel
	 * @return the client extension entry rel that was removed
	 * @throws NoSuchClientExtensionEntryRelException if a client extension entry rel with the primary key could not be found
	 */
	@Override
	public ClientExtensionEntryRel remove(long clientExtensionEntryRelId)
		throws NoSuchClientExtensionEntryRelException {

		return remove((Serializable)clientExtensionEntryRelId);
	}

	@Override
	protected ClientExtensionEntryRel removeImpl(
		ClientExtensionEntryRel clientExtensionEntryRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(clientExtensionEntryRel)) {
				clientExtensionEntryRel = (ClientExtensionEntryRel)session.get(
					ClientExtensionEntryRelImpl.class,
					clientExtensionEntryRel.getPrimaryKeyObj());
			}

			if ((clientExtensionEntryRel != null) &&
				ctPersistenceHelper.isRemove(clientExtensionEntryRel)) {

				session.delete(clientExtensionEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (clientExtensionEntryRel != null) {
			clearCache(clientExtensionEntryRel);
		}

		return clientExtensionEntryRel;
	}

	@Override
	public ClientExtensionEntryRel updateImpl(
		ClientExtensionEntryRel clientExtensionEntryRel) {

		boolean isNew = clientExtensionEntryRel.isNew();

		if (!(clientExtensionEntryRel instanceof
				ClientExtensionEntryRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(clientExtensionEntryRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					clientExtensionEntryRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in clientExtensionEntryRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ClientExtensionEntryRel implementation " +
					clientExtensionEntryRel.getClass());
		}

		ClientExtensionEntryRelModelImpl clientExtensionEntryRelModelImpl =
			(ClientExtensionEntryRelModelImpl)clientExtensionEntryRel;

		if (Validator.isNull(clientExtensionEntryRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			clientExtensionEntryRel.setUuid(uuid);
		}

		if (Validator.isNull(
				clientExtensionEntryRel.getExternalReferenceCode())) {

			clientExtensionEntryRel.setExternalReferenceCode(
				clientExtensionEntryRel.getUuid());
		}
		else {
			if (!Objects.equals(
					clientExtensionEntryRelModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					clientExtensionEntryRel.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = clientExtensionEntryRel.getCompanyId();

					long groupId = clientExtensionEntryRel.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = clientExtensionEntryRel.getPrimaryKey();
					}

					try {
						clientExtensionEntryRel.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ClientExtensionEntryRel.class.getName(),
								classPK, ContentTypes.TEXT_HTML,
								Sanitizer.MODE_ALL,
								clientExtensionEntryRel.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			ClientExtensionEntryRel ercClientExtensionEntryRel = fetchByERC_G(
				clientExtensionEntryRel.getExternalReferenceCode(),
				clientExtensionEntryRel.getGroupId());

			if (isNew) {
				if (ercClientExtensionEntryRel != null) {
					throw new DuplicateClientExtensionEntryRelExternalReferenceCodeException(
						"Duplicate client extension entry rel with external reference code " +
							clientExtensionEntryRel.getExternalReferenceCode() +
								" and group " +
									clientExtensionEntryRel.getGroupId());
				}
			}
			else {
				if ((ercClientExtensionEntryRel != null) &&
					(clientExtensionEntryRel.getClientExtensionEntryRelId() !=
						ercClientExtensionEntryRel.
							getClientExtensionEntryRelId())) {

					throw new DuplicateClientExtensionEntryRelExternalReferenceCodeException(
						"Duplicate client extension entry rel with external reference code " +
							clientExtensionEntryRel.getExternalReferenceCode() +
								" and group " +
									clientExtensionEntryRel.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (clientExtensionEntryRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				clientExtensionEntryRel.setCreateDate(date);
			}
			else {
				clientExtensionEntryRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!clientExtensionEntryRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				clientExtensionEntryRel.setModifiedDate(date);
			}
			else {
				clientExtensionEntryRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(clientExtensionEntryRel)) {
				if (!isNew) {
					session.evict(
						ClientExtensionEntryRelImpl.class,
						clientExtensionEntryRel.getPrimaryKeyObj());
				}

				session.save(clientExtensionEntryRel);
			}
			else {
				clientExtensionEntryRel =
					(ClientExtensionEntryRel)session.merge(
						clientExtensionEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(clientExtensionEntryRel, false);

		if (isNew) {
			clientExtensionEntryRel.setNew(false);
		}

		clientExtensionEntryRel.resetOriginalValues();

		return clientExtensionEntryRel;
	}

	/**
	 * Returns the client extension entry rel with the primary key or throws a <code>NoSuchClientExtensionEntryRelException</code> if it could not be found.
	 *
	 * @param clientExtensionEntryRelId the primary key of the client extension entry rel
	 * @return the client extension entry rel
	 * @throws NoSuchClientExtensionEntryRelException if a client extension entry rel with the primary key could not be found
	 */
	@Override
	public ClientExtensionEntryRel findByPrimaryKey(
			long clientExtensionEntryRelId)
		throws NoSuchClientExtensionEntryRelException {

		return findByPrimaryKey((Serializable)clientExtensionEntryRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the client extension entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param clientExtensionEntryRelId the primary key of the client extension entry rel
	 * @return the client extension entry rel, or <code>null</code> if a client extension entry rel with the primary key could not be found
	 */
	@Override
	public ClientExtensionEntryRel fetchByPrimaryKey(
		long clientExtensionEntryRelId) {

		return fetchByPrimaryKey((Serializable)clientExtensionEntryRelId);
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
		return "clientExtensionEntryRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CLIENTEXTENSIONENTRYREL;
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
		return ClientExtensionEntryRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "ClientExtensionEntryRel";
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
		ctMergeColumnNames.add("cetExternalReferenceCode");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("clientExtensionEntryRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the client extension entry rel persistence.
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
			_SQL_SELECT_CLIENTEXTENSIONENTRYREL_WHERE,
			_SQL_COUNT_CLIENTEXTENSIONENTRYREL_WHERE,
			ClientExtensionEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"clientExtensionEntryRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				ClientExtensionEntryRel::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(ClientExtensionEntryRel::getUuid),
				ClientExtensionEntryRel::getGroupId),
			_SQL_SELECT_CLIENTEXTENSIONENTRYREL_WHERE, "",
			new FinderColumn<>(
				"clientExtensionEntryRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				ClientExtensionEntryRel::getUuid),
			new FinderColumn<>(
				"clientExtensionEntryRel.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, ClientExtensionEntryRel::getGroupId));

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
				_SQL_SELECT_CLIENTEXTENSIONENTRYREL_WHERE,
				_SQL_COUNT_CLIENTEXTENSIONENTRYREL_WHERE,
				ClientExtensionEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"clientExtensionEntryRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ClientExtensionEntryRel::getUuid),
				new FinderColumn<>(
					"clientExtensionEntryRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					ClientExtensionEntryRel::getCompanyId));

		_collectionPersistenceFinderByType = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByType",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByType",
				new String[] {String.class.getName()}, new String[] {"type_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByType",
				new String[] {String.class.getName()}, new String[] {"type_"},
				0, 1, false, null),
			_SQL_SELECT_CLIENTEXTENSIONENTRYREL_WHERE,
			_SQL_COUNT_CLIENTEXTENSIONENTRYREL_WHERE,
			ClientExtensionEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"clientExtensionEntryRel.", "type", "type_",
				FinderColumn.Type.STRING, "=", true, true,
				ClientExtensionEntryRel::getType));

		_collectionPersistenceFinderByC_CETERC =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CETERC",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "cetExternalReferenceCode"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CETERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "cetExternalReferenceCode"}, 0,
					2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_CETERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "cetExternalReferenceCode"}, 0,
					2, false, null),
				_SQL_SELECT_CLIENTEXTENSIONENTRYREL_WHERE,
				_SQL_COUNT_CLIENTEXTENSIONENTRYREL_WHERE,
				ClientExtensionEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"clientExtensionEntryRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					ClientExtensionEntryRel::getCompanyId),
				new FinderColumn<>(
					"clientExtensionEntryRel.", "cetExternalReferenceCode",
					FinderColumn.Type.STRING, "=", true, true,
					ClientExtensionEntryRel::getCETExternalReferenceCode));

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
			_SQL_SELECT_CLIENTEXTENSIONENTRYREL_WHERE,
			_SQL_COUNT_CLIENTEXTENSIONENTRYREL_WHERE,
			ClientExtensionEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"clientExtensionEntryRel.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				ClientExtensionEntryRel::getClassNameId),
			new FinderColumn<>(
				"clientExtensionEntryRel.", "classPK", FinderColumn.Type.LONG,
				"=", true, true, ClientExtensionEntryRel::getClassPK));

		_collectionPersistenceFinderByC_C_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"classNameId", "classPK", "type_"}, 0, 4, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"classNameId", "classPK", "type_"}, 0, 4, false,
				null),
			_SQL_SELECT_CLIENTEXTENSIONENTRYREL_WHERE,
			_SQL_COUNT_CLIENTEXTENSIONENTRYREL_WHERE,
			ClientExtensionEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"clientExtensionEntryRel.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				ClientExtensionEntryRel::getClassNameId),
			new FinderColumn<>(
				"clientExtensionEntryRel.", "classPK", FinderColumn.Type.LONG,
				"=", true, true, ClientExtensionEntryRel::getClassPK),
			new FinderColumn<>(
				"clientExtensionEntryRel.", "type", "type_",
				FinderColumn.Type.STRING, "=", true, true,
				ClientExtensionEntryRel::getType));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(
					ClientExtensionEntryRel::getExternalReferenceCode),
				ClientExtensionEntryRel::getGroupId),
			_SQL_SELECT_CLIENTEXTENSIONENTRYREL_WHERE, "",
			new FinderColumn<>(
				"clientExtensionEntryRel.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ClientExtensionEntryRel::getExternalReferenceCode),
			new FinderColumn<>(
				"clientExtensionEntryRel.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, ClientExtensionEntryRel::getGroupId));

		ClientExtensionEntryRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ClientExtensionEntryRelUtil.setPersistence(null);

		entityCache.removeCache(ClientExtensionEntryRelImpl.class.getName());
	}

	@Override
	@Reference(
		target = ClientExtensionPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = ClientExtensionPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = ClientExtensionPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		ClientExtensionEntryRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CLIENTEXTENSIONENTRYREL =
		"SELECT clientExtensionEntryRel FROM ClientExtensionEntryRel clientExtensionEntryRel";

	private static final String _SQL_SELECT_CLIENTEXTENSIONENTRYREL_WHERE =
		"SELECT clientExtensionEntryRel FROM ClientExtensionEntryRel clientExtensionEntryRel WHERE ";

	private static final String _SQL_COUNT_CLIENTEXTENSIONENTRYREL_WHERE =
		"SELECT COUNT(clientExtensionEntryRel) FROM ClientExtensionEntryRel clientExtensionEntryRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ClientExtensionEntryRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ClientExtensionEntryRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:193457806