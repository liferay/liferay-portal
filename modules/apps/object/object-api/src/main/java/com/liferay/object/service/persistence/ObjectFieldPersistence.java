/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.exception.NoSuchObjectFieldException;
import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the object field service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectFieldUtil
 * @generated
 */
@ProviderType
public interface ObjectFieldPersistence extends BasePersistence<ObjectField> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ObjectFieldUtil} to access the object field persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the object fields where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public java.util.List<ObjectField> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object field in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
				orderByComparator)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the first object field in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator);

	/**
	 * Removes all the object fields where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of object fields where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object fields
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns an ordered range of all the object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public java.util.List<ObjectField> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object field in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
				orderByComparator)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the first object field in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator);

	/**
	 * Removes all the object fields where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object fields
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public java.util.List<ObjectField> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object field in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
				orderByComparator)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the first object field in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator);

	/**
	 * Removes all the object fields where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of object fields where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object fields
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns an ordered range of all the object fields where listTypeDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public java.util.List<ObjectField> findByListTypeDefinitionId(
		long listTypeDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByListTypeDefinitionId_First(
			long listTypeDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
				orderByComparator)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByListTypeDefinitionId_First(
		long listTypeDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator);

	/**
	 * Removes all the object fields where listTypeDefinitionId = &#63; from the database.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 */
	public void removeByListTypeDefinitionId(long listTypeDefinitionId);

	/**
	 * Returns the number of object fields where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the number of matching object fields
	 */
	public int countByListTypeDefinitionId(long listTypeDefinitionId);

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public java.util.List<ObjectField> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByObjectDefinitionId_First(
			long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
				orderByComparator)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator);

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	public void removeByObjectDefinitionId(long objectDefinitionId);

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object fields
	 */
	public int countByObjectDefinitionId(long objectDefinitionId);

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public java.util.List<ObjectField> findByC_U(
		long companyId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object field in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByC_U_First(
			long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
				orderByComparator)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the first object field in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByC_U_First(
		long companyId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator);

	/**
	 * Removes all the object fields where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	public void removeByC_U(long companyId, long userId);

	/**
	 * Returns the number of object fields where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching object fields
	 */
	public int countByC_U(long companyId, long userId);

	/**
	 * Returns an ordered range of all the object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public java.util.List<ObjectField> findByLTDI_S(
		long listTypeDefinitionId, boolean state, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByLTDI_S_First(
			long listTypeDefinitionId, boolean state,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
				orderByComparator)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByLTDI_S_First(
		long listTypeDefinitionId, boolean state,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator);

	/**
	 * Removes all the object fields where listTypeDefinitionId = &#63; and state = &#63; from the database.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 */
	public void removeByLTDI_S(long listTypeDefinitionId, boolean state);

	/**
	 * Returns the number of object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @return the number of matching object fields
	 */
	public int countByLTDI_S(long listTypeDefinitionId, boolean state);

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public java.util.List<ObjectField> findByODI_BT(
		long objectDefinitionId, String businessType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByODI_BT_First(
			long objectDefinitionId, String businessType,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
				orderByComparator)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByODI_BT_First(
		long objectDefinitionId, String businessType,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator);

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and businessType = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 */
	public void removeByODI_BT(long objectDefinitionId, String businessType);

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @return the number of matching object fields
	 */
	public int countByODI_BT(long objectDefinitionId, String businessType);

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public java.util.List<ObjectField> findByODI_DTN(
		long objectDefinitionId, String dbTableName, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByODI_DTN_First(
			long objectDefinitionId, String dbTableName,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
				orderByComparator)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByODI_DTN_First(
		long objectDefinitionId, String dbTableName,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator);

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and dbTableName = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 */
	public void removeByODI_DTN(long objectDefinitionId, String dbTableName);

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @return the number of matching object fields
	 */
	public int countByODI_DTN(long objectDefinitionId, String dbTableName);

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public java.util.List<ObjectField> findByODI_I(
		long objectDefinitionId, boolean indexed, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByODI_I_First(
			long objectDefinitionId, boolean indexed,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
				orderByComparator)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByODI_I_First(
		long objectDefinitionId, boolean indexed,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator);

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and indexed = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 */
	public void removeByODI_I(long objectDefinitionId, boolean indexed);

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @return the number of matching object fields
	 */
	public int countByODI_I(long objectDefinitionId, boolean indexed);

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public java.util.List<ObjectField> findByODI_L(
		long objectDefinitionId, boolean localized, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByODI_L_First(
			long objectDefinitionId, boolean localized,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
				orderByComparator)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByODI_L_First(
		long objectDefinitionId, boolean localized,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator);

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and localized = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 */
	public void removeByODI_L(long objectDefinitionId, boolean localized);

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @return the number of matching object fields
	 */
	public int countByODI_L(long objectDefinitionId, boolean localized);

	/**
	 * Returns the object field where objectDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchObjectFieldException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByODI_N(long objectDefinitionId, String name)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the object field where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByODI_N(
		long objectDefinitionId, String name, boolean useFinderCache);

	/**
	 * Removes the object field where objectDefinitionId = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the object field that was removed
	 */
	public ObjectField removeByODI_N(long objectDefinitionId, String name)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the number of matching object fields
	 */
	public int countByODI_N(long objectDefinitionId, String name);

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public java.util.List<ObjectField> findByODI_S(
		long objectDefinitionId, boolean system, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByODI_S_First(
			long objectDefinitionId, boolean system,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
				orderByComparator)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByODI_S_First(
		long objectDefinitionId, boolean system,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator);

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and system = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 */
	public void removeByODI_S(long objectDefinitionId, boolean system);

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @return the number of matching object fields
	 */
	public int countByODI_S(long objectDefinitionId, boolean system);

	/**
	 * Returns the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or throws a <code>NoSuchObjectFieldException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId,
		boolean useFinderCache);

	/**
	 * Removes the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the object field that was removed
	 */
	public ObjectField removeByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the number of object fields where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object fields
	 */
	public int countByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId);

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public java.util.List<ObjectField> findByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByODI_DBT_I_First(
			long objectDefinitionId, String dbType, boolean indexed,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
				orderByComparator)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByODI_DBT_I_First(
		long objectDefinitionId, String dbType, boolean indexed,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator);

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 */
	public void removeByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed);

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @return the number of matching object fields
	 */
	public int countByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed);

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public java.util.List<ObjectField> findByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public ObjectField findByODI_L_S_First(
			long objectDefinitionId, boolean localized, boolean system,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
				orderByComparator)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public ObjectField fetchByODI_L_S_First(
		long objectDefinitionId, boolean localized, boolean system,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator);

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 */
	public void removeByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system);

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @return the number of matching object fields
	 */
	public int countByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system);

	/**
	 * Creates a new object field with the primary key. Does not add the object field to the database.
	 *
	 * @param objectFieldId the primary key for the new object field
	 * @return the new object field
	 */
	public ObjectField create(long objectFieldId);

	/**
	 * Removes the object field with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectFieldId the primary key of the object field
	 * @return the object field that was removed
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public ObjectField remove(long objectFieldId)
		throws NoSuchObjectFieldException;

	public ObjectField updateImpl(ObjectField objectField);

	/**
	 * Returns the object field with the primary key or throws a <code>NoSuchObjectFieldException</code> if it could not be found.
	 *
	 * @param objectFieldId the primary key of the object field
	 * @return the object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public ObjectField findByPrimaryKey(long objectFieldId)
		throws NoSuchObjectFieldException;

	/**
	 * Returns the object field with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectFieldId the primary key of the object field
	 * @return the object field, or <code>null</code> if a object field with the primary key could not be found
	 */
	public ObjectField fetchByPrimaryKey(long objectFieldId);

	/**
	 * Returns the object field where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public default ObjectField fetchByODI_N(
		long objectDefinitionId, String name) {

		return fetchByODI_N(objectDefinitionId, name, true);
	}

	/**
	 * Returns the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public default ObjectField fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId) {

		return fetchByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId, true);
	}

	/**
	 * Returns all the object fields where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object fields
	 */
	public default java.util.List<ObjectField> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object fields where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public default java.util.List<ObjectField> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object fields where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public default java.util.List<ObjectField> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object fields
	 */
	public default java.util.List<ObjectField> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public default java.util.List<ObjectField> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public default java.util.List<ObjectField> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object fields where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching object fields
	 */
	public default java.util.List<ObjectField> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object fields where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public default java.util.List<ObjectField> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public default java.util.List<ObjectField> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object fields where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the matching object fields
	 */
	public default java.util.List<ObjectField> findByListTypeDefinitionId(
		long listTypeDefinitionId) {

		return findByListTypeDefinitionId(
			listTypeDefinitionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object fields where listTypeDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public default java.util.List<ObjectField> findByListTypeDefinitionId(
		long listTypeDefinitionId, int start, int end) {

		return findByListTypeDefinitionId(
			listTypeDefinitionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object fields where listTypeDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public default java.util.List<ObjectField> findByListTypeDefinitionId(
		long listTypeDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator) {

		return findByListTypeDefinitionId(
			listTypeDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object fields
	 */
	public default java.util.List<ObjectField> findByObjectDefinitionId(
		long objectDefinitionId) {

		return findByObjectDefinitionId(
			objectDefinitionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public default java.util.List<ObjectField> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end) {

		return findByObjectDefinitionId(
			objectDefinitionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public default java.util.List<ObjectField> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator) {

		return findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object fields where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching object fields
	 */
	public default java.util.List<ObjectField> findByC_U(
		long companyId, long userId) {

		return findByC_U(
			companyId, userId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object fields where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public default java.util.List<ObjectField> findByC_U(
		long companyId, long userId, int start, int end) {

		return findByC_U(companyId, userId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public default java.util.List<ObjectField> findByC_U(
		long companyId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator) {

		return findByC_U(
			companyId, userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @return the matching object fields
	 */
	public default java.util.List<ObjectField> findByLTDI_S(
		long listTypeDefinitionId, boolean state) {

		return findByLTDI_S(
			listTypeDefinitionId, state,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public default java.util.List<ObjectField> findByLTDI_S(
		long listTypeDefinitionId, boolean state, int start, int end) {

		return findByLTDI_S(
			listTypeDefinitionId, state, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public default java.util.List<ObjectField> findByLTDI_S(
		long listTypeDefinitionId, boolean state, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator) {

		return findByLTDI_S(
			listTypeDefinitionId, state, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @return the matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_BT(
		long objectDefinitionId, String businessType) {

		return findByODI_BT(
			objectDefinitionId, businessType,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_BT(
		long objectDefinitionId, String businessType, int start, int end) {

		return findByODI_BT(
			objectDefinitionId, businessType, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_BT(
		long objectDefinitionId, String businessType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator) {

		return findByODI_BT(
			objectDefinitionId, businessType, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @return the matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_DTN(
		long objectDefinitionId, String dbTableName) {

		return findByODI_DTN(
			objectDefinitionId, dbTableName,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_DTN(
		long objectDefinitionId, String dbTableName, int start, int end) {

		return findByODI_DTN(
			objectDefinitionId, dbTableName, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_DTN(
		long objectDefinitionId, String dbTableName, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator) {

		return findByODI_DTN(
			objectDefinitionId, dbTableName, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @return the matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_I(
		long objectDefinitionId, boolean indexed) {

		return findByODI_I(
			objectDefinitionId, indexed,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_I(
		long objectDefinitionId, boolean indexed, int start, int end) {

		return findByODI_I(objectDefinitionId, indexed, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_I(
		long objectDefinitionId, boolean indexed, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator) {

		return findByODI_I(
			objectDefinitionId, indexed, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @return the matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_L(
		long objectDefinitionId, boolean localized) {

		return findByODI_L(
			objectDefinitionId, localized,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_L(
		long objectDefinitionId, boolean localized, int start, int end) {

		return findByODI_L(
			objectDefinitionId, localized, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_L(
		long objectDefinitionId, boolean localized, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator) {

		return findByODI_L(
			objectDefinitionId, localized, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @return the matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_S(
		long objectDefinitionId, boolean system) {

		return findByODI_S(
			objectDefinitionId, system,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_S(
		long objectDefinitionId, boolean system, int start, int end) {

		return findByODI_S(objectDefinitionId, system, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_S(
		long objectDefinitionId, boolean system, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator) {

		return findByODI_S(
			objectDefinitionId, system, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @return the matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed) {

		return findByODI_DBT_I(
			objectDefinitionId, dbType, indexed,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed, int start,
		int end) {

		return findByODI_DBT_I(
			objectDefinitionId, dbType, indexed, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator) {

		return findByODI_DBT_I(
			objectDefinitionId, dbType, indexed, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @return the matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system) {

		return findByODI_L_S(
			objectDefinitionId, localized, system,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system, int start,
		int end) {

		return findByODI_L_S(
			objectDefinitionId, localized, system, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public default java.util.List<ObjectField> findByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectField>
			orderByComparator) {

		return findByODI_L_S(
			objectDefinitionId, localized, system, start, end,
			orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:574407705