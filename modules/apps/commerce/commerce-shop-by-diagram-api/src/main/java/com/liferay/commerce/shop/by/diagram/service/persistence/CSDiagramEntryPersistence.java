/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.persistence;

import com.liferay.commerce.shop.by.diagram.exception.NoSuchCSDiagramEntryException;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the cs diagram entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CSDiagramEntryUtil
 * @generated
 */
@ProviderType
public interface CSDiagramEntryPersistence
	extends BasePersistence<CSDiagramEntry>, CTPersistence<CSDiagramEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CSDiagramEntryUtil} to access the cs diagram entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the matching cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findByCPDefinitionId(
		long CPDefinitionId);

	/**
	 * Returns a range of all the cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @return the range of matching cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findByCPDefinitionId(
		long CPDefinitionId, int start, int end);

	/**
	 * Returns an ordered range of all the cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry findByCPDefinitionId_First(
			long CPDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
				orderByComparator)
		throws NoSuchCSDiagramEntryException;

	/**
	 * Returns the first cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry fetchByCPDefinitionId_First(
		long CPDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
			orderByComparator);

	/**
	 * Returns the last cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry findByCPDefinitionId_Last(
			long CPDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
				orderByComparator)
		throws NoSuchCSDiagramEntryException;

	/**
	 * Returns the last cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry fetchByCPDefinitionId_Last(
		long CPDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
			orderByComparator);

	/**
	 * Returns the cs diagram entries before and after the current cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CSDiagramEntryId the primary key of the current cs diagram entry
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	public CSDiagramEntry[] findByCPDefinitionId_PrevAndNext(
			long CSDiagramEntryId, long CPDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
				orderByComparator)
		throws NoSuchCSDiagramEntryException;

	/**
	 * Removes all the cs diagram entries where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	public void removeByCPDefinitionId(long CPDefinitionId);

	/**
	 * Returns the number of cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cs diagram entries
	 */
	public int countByCPDefinitionId(long CPDefinitionId);

	/**
	 * Returns all the cs diagram entries where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @return the matching cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findByCPInstanceId(long CPInstanceId);

	/**
	 * Returns a range of all the cs diagram entries where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @return the range of matching cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findByCPInstanceId(
		long CPInstanceId, int start, int end);

	/**
	 * Returns an ordered range of all the cs diagram entries where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findByCPInstanceId(
		long CPInstanceId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cs diagram entries where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findByCPInstanceId(
		long CPInstanceId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry findByCPInstanceId_First(
			long CPInstanceId,
			com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
				orderByComparator)
		throws NoSuchCSDiagramEntryException;

	/**
	 * Returns the first cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry fetchByCPInstanceId_First(
		long CPInstanceId,
		com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
			orderByComparator);

	/**
	 * Returns the last cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry findByCPInstanceId_Last(
			long CPInstanceId,
			com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
				orderByComparator)
		throws NoSuchCSDiagramEntryException;

	/**
	 * Returns the last cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry fetchByCPInstanceId_Last(
		long CPInstanceId,
		com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
			orderByComparator);

	/**
	 * Returns the cs diagram entries before and after the current cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CSDiagramEntryId the primary key of the current cs diagram entry
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	public CSDiagramEntry[] findByCPInstanceId_PrevAndNext(
			long CSDiagramEntryId, long CPInstanceId,
			com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
				orderByComparator)
		throws NoSuchCSDiagramEntryException;

	/**
	 * Removes all the cs diagram entries where CPInstanceId = &#63; from the database.
	 *
	 * @param CPInstanceId the cp instance ID
	 */
	public void removeByCPInstanceId(long CPInstanceId);

	/**
	 * Returns the number of cs diagram entries where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching cs diagram entries
	 */
	public int countByCPInstanceId(long CPInstanceId);

	/**
	 * Returns all the cs diagram entries where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the matching cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findByCProductId(long CProductId);

	/**
	 * Returns a range of all the cs diagram entries where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @return the range of matching cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findByCProductId(
		long CProductId, int start, int end);

	/**
	 * Returns an ordered range of all the cs diagram entries where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findByCProductId(
		long CProductId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cs diagram entries where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findByCProductId(
		long CProductId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry findByCProductId_First(
			long CProductId,
			com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
				orderByComparator)
		throws NoSuchCSDiagramEntryException;

	/**
	 * Returns the first cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry fetchByCProductId_First(
		long CProductId,
		com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
			orderByComparator);

	/**
	 * Returns the last cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry findByCProductId_Last(
			long CProductId,
			com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
				orderByComparator)
		throws NoSuchCSDiagramEntryException;

	/**
	 * Returns the last cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry fetchByCProductId_Last(
		long CProductId,
		com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
			orderByComparator);

	/**
	 * Returns the cs diagram entries before and after the current cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CSDiagramEntryId the primary key of the current cs diagram entry
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	public CSDiagramEntry[] findByCProductId_PrevAndNext(
			long CSDiagramEntryId, long CProductId,
			com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
				orderByComparator)
		throws NoSuchCSDiagramEntryException;

	/**
	 * Removes all the cs diagram entries where CProductId = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 */
	public void removeByCProductId(long CProductId);

	/**
	 * Returns the number of cs diagram entries where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the number of matching cs diagram entries
	 */
	public int countByCProductId(long CProductId);

	/**
	 * Returns the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; or throws a <code>NoSuchCSDiagramEntryException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry findByCPDI_S(long CPDefinitionId, String sequence)
		throws NoSuchCSDiagramEntryException;

	/**
	 * Returns the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry fetchByCPDI_S(long CPDefinitionId, String sequence);

	/**
	 * Returns the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry fetchByCPDI_S(
		long CPDefinitionId, String sequence, boolean useFinderCache);

	/**
	 * Removes the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the cs diagram entry that was removed
	 */
	public CSDiagramEntry removeByCPDI_S(long CPDefinitionId, String sequence)
		throws NoSuchCSDiagramEntryException;

	/**
	 * Returns the number of cs diagram entries where CPDefinitionId = &#63; and sequence = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the number of matching cs diagram entries
	 */
	public int countByCPDI_S(long CPDefinitionId, String sequence);

	/**
	 * Returns the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCSDiagramEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCSDiagramEntryException;

	/**
	 * Returns the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry fetchByERC_C(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public CSDiagramEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cs diagram entry that was removed
	 */
	public CSDiagramEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCSDiagramEntryException;

	/**
	 * Returns the number of cs diagram entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cs diagram entries
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Caches the cs diagram entry in the entity cache if it is enabled.
	 *
	 * @param csDiagramEntry the cs diagram entry
	 */
	public void cacheResult(CSDiagramEntry csDiagramEntry);

	/**
	 * Caches the cs diagram entries in the entity cache if it is enabled.
	 *
	 * @param csDiagramEntries the cs diagram entries
	 */
	public void cacheResult(java.util.List<CSDiagramEntry> csDiagramEntries);

	/**
	 * Creates a new cs diagram entry with the primary key. Does not add the cs diagram entry to the database.
	 *
	 * @param CSDiagramEntryId the primary key for the new cs diagram entry
	 * @return the new cs diagram entry
	 */
	public CSDiagramEntry create(long CSDiagramEntryId);

	/**
	 * Removes the cs diagram entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry that was removed
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	public CSDiagramEntry remove(long CSDiagramEntryId)
		throws NoSuchCSDiagramEntryException;

	public CSDiagramEntry updateImpl(CSDiagramEntry csDiagramEntry);

	/**
	 * Returns the cs diagram entry with the primary key or throws a <code>NoSuchCSDiagramEntryException</code> if it could not be found.
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	public CSDiagramEntry findByPrimaryKey(long CSDiagramEntryId)
		throws NoSuchCSDiagramEntryException;

	/**
	 * Returns the cs diagram entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry, or <code>null</code> if a cs diagram entry with the primary key could not be found
	 */
	public CSDiagramEntry fetchByPrimaryKey(long CSDiagramEntryId);

	/**
	 * Returns all the cs diagram entries.
	 *
	 * @return the cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findAll();

	/**
	 * Returns a range of all the cs diagram entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @return the range of cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the cs diagram entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cs diagram entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cs diagram entries
	 */
	public java.util.List<CSDiagramEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the cs diagram entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of cs diagram entries.
	 *
	 * @return the number of cs diagram entries
	 */
	public int countAll();

}