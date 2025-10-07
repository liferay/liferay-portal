/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.exception.NoSuchObjectDefinitionSettingException;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the object definition setting service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectDefinitionSettingUtil
 * @generated
 */
@ProviderType
public interface ObjectDefinitionSettingPersistence
	extends BasePersistence<ObjectDefinitionSetting> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ObjectDefinitionSettingUtil} to access the object definition setting persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the object definition settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByUuid(String uuid);

	/**
	 * Returns a range of all the object definition settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the object definition settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator);

	/**
	 * Returns an ordered range of all the object definition settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Returns the first object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator);

	/**
	 * Returns the last object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Returns the last object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator);

	/**
	 * Returns the object definition settings before and after the current object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param objectDefinitionSettingId the primary key of the current object definition setting
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	public ObjectDefinitionSetting[] findByUuid_PrevAndNext(
			long objectDefinitionSettingId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Removes all the object definition settings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of object definition settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object definition settings
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns all the object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator);

	/**
	 * Returns an ordered range of all the object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Returns the first object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator);

	/**
	 * Returns the last object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Returns the last object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator);

	/**
	 * Returns the object definition settings before and after the current object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectDefinitionSettingId the primary key of the current object definition setting
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	public ObjectDefinitionSetting[] findByUuid_C_PrevAndNext(
			long objectDefinitionSettingId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Removes all the object definition settings where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object definition settings
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the object definition settings where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByObjectDefinitionId(
		long objectDefinitionId);

	/**
	 * Returns a range of all the object definition settings where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end);

	/**
	 * Returns an ordered range of all the object definition settings where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator);

	/**
	 * Returns an ordered range of all the object definition settings where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting findByObjectDefinitionId_First(
			long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Returns the first object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator);

	/**
	 * Returns the last object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting findByObjectDefinitionId_Last(
			long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Returns the last object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator);

	/**
	 * Returns the object definition settings before and after the current object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionSettingId the primary key of the current object definition setting
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	public ObjectDefinitionSetting[] findByObjectDefinitionId_PrevAndNext(
			long objectDefinitionSettingId, long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Removes all the object definition settings where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	public void removeByObjectDefinitionId(long objectDefinitionId);

	/**
	 * Returns the number of object definition settings where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object definition settings
	 */
	public int countByObjectDefinitionId(long objectDefinitionId);

	/**
	 * Returns all the object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByC_N(
		long companyId, String name);

	/**
	 * Returns a range of all the object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByC_N(
		long companyId, String name, int start, int end);

	/**
	 * Returns an ordered range of all the object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByC_N(
		long companyId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator);

	/**
	 * Returns an ordered range of all the object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findByC_N(
		long companyId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting findByC_N_First(
			long companyId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Returns the first object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting fetchByC_N_First(
		long companyId, String name,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator);

	/**
	 * Returns the last object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting findByC_N_Last(
			long companyId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Returns the last object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting fetchByC_N_Last(
		long companyId, String name,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator);

	/**
	 * Returns the object definition settings before and after the current object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionSettingId the primary key of the current object definition setting
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	public ObjectDefinitionSetting[] findByC_N_PrevAndNext(
			long objectDefinitionSettingId, long companyId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Removes all the object definition settings where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 */
	public void removeByC_N(long companyId, String name);

	/**
	 * Returns the number of object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching object definition settings
	 */
	public int countByC_N(long companyId, String name);

	/**
	 * Returns the object definition setting where objectDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchObjectDefinitionSettingException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting findByODI_N(
			long objectDefinitionId, String name)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Returns the object definition setting where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting fetchByODI_N(
		long objectDefinitionId, String name);

	/**
	 * Returns the object definition setting where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public ObjectDefinitionSetting fetchByODI_N(
		long objectDefinitionId, String name, boolean useFinderCache);

	/**
	 * Removes the object definition setting where objectDefinitionId = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the object definition setting that was removed
	 */
	public ObjectDefinitionSetting removeByODI_N(
			long objectDefinitionId, String name)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Returns the number of object definition settings where objectDefinitionId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the number of matching object definition settings
	 */
	public int countByODI_N(long objectDefinitionId, String name);

	/**
	 * Caches the object definition setting in the entity cache if it is enabled.
	 *
	 * @param objectDefinitionSetting the object definition setting
	 */
	public void cacheResult(ObjectDefinitionSetting objectDefinitionSetting);

	/**
	 * Caches the object definition settings in the entity cache if it is enabled.
	 *
	 * @param objectDefinitionSettings the object definition settings
	 */
	public void cacheResult(
		java.util.List<ObjectDefinitionSetting> objectDefinitionSettings);

	/**
	 * Creates a new object definition setting with the primary key. Does not add the object definition setting to the database.
	 *
	 * @param objectDefinitionSettingId the primary key for the new object definition setting
	 * @return the new object definition setting
	 */
	public ObjectDefinitionSetting create(long objectDefinitionSettingId);

	/**
	 * Removes the object definition setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting that was removed
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	public ObjectDefinitionSetting remove(long objectDefinitionSettingId)
		throws NoSuchObjectDefinitionSettingException;

	public ObjectDefinitionSetting updateImpl(
		ObjectDefinitionSetting objectDefinitionSetting);

	/**
	 * Returns the object definition setting with the primary key or throws a <code>NoSuchObjectDefinitionSettingException</code> if it could not be found.
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	public ObjectDefinitionSetting findByPrimaryKey(
			long objectDefinitionSettingId)
		throws NoSuchObjectDefinitionSettingException;

	/**
	 * Returns the object definition setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting, or <code>null</code> if a object definition setting with the primary key could not be found
	 */
	public ObjectDefinitionSetting fetchByPrimaryKey(
		long objectDefinitionSettingId);

	/**
	 * Returns all the object definition settings.
	 *
	 * @return the object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findAll();

	/**
	 * Returns a range of all the object definition settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the object definition settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator);

	/**
	 * Returns an ordered range of all the object definition settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object definition settings
	 */
	public java.util.List<ObjectDefinitionSetting> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the object definition settings from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of object definition settings.
	 *
	 * @return the number of object definition settings
	 */
	public int countAll();

}