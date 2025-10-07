/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service;

import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CSDiagramEntry. This utility wraps
 * <code>com.liferay.commerce.shop.by.diagram.service.impl.CSDiagramEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CSDiagramEntryLocalService
 * @generated
 */
public class CSDiagramEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.shop.by.diagram.service.impl.CSDiagramEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the cs diagram entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CSDiagramEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param csDiagramEntry the cs diagram entry
	 * @return the cs diagram entry that was added
	 */
	public static CSDiagramEntry addCSDiagramEntry(
		CSDiagramEntry csDiagramEntry) {

		return getService().addCSDiagramEntry(csDiagramEntry);
	}

	public static CSDiagramEntry addCSDiagramEntry(
			long userId, long cpDefinitionId, long cpInstanceId,
			long cProductId, boolean diagram, int quantity, String sequence,
			String sku,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCSDiagramEntry(
			userId, cpDefinitionId, cpInstanceId, cProductId, diagram, quantity,
			sequence, sku, serviceContext);
	}

	/**
	 * Creates a new cs diagram entry with the primary key. Does not add the cs diagram entry to the database.
	 *
	 * @param CSDiagramEntryId the primary key for the new cs diagram entry
	 * @return the new cs diagram entry
	 */
	public static CSDiagramEntry createCSDiagramEntry(long CSDiagramEntryId) {
		return getService().createCSDiagramEntry(CSDiagramEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCSDiagramEntries(long cpDefinitionId)
		throws PortalException {

		getService().deleteCSDiagramEntries(cpDefinitionId);
	}

	/**
	 * Deletes the cs diagram entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CSDiagramEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param csDiagramEntry the cs diagram entry
	 * @return the cs diagram entry that was removed
	 * @throws PortalException
	 */
	public static CSDiagramEntry deleteCSDiagramEntry(
			CSDiagramEntry csDiagramEntry)
		throws PortalException {

		return getService().deleteCSDiagramEntry(csDiagramEntry);
	}

	/**
	 * Deletes the cs diagram entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CSDiagramEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry that was removed
	 * @throws PortalException if a cs diagram entry with the primary key could not be found
	 */
	public static CSDiagramEntry deleteCSDiagramEntry(long CSDiagramEntryId)
		throws PortalException {

		return getService().deleteCSDiagramEntry(CSDiagramEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static CSDiagramEntry fetchCSDiagramEntry(long CSDiagramEntryId) {
		return getService().fetchCSDiagramEntry(CSDiagramEntryId);
	}

	public static CSDiagramEntry fetchCSDiagramEntry(
		long cpDefinitionId, String sequence) {

		return getService().fetchCSDiagramEntry(cpDefinitionId, sequence);
	}

	public static CSDiagramEntry fetchCSDiagramEntryByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchCSDiagramEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<CSDiagramEntry> getCPDefinitionRelatedCSDiagramEntries(
		long cpDefinitionId) {

		return getService().getCPDefinitionRelatedCSDiagramEntries(
			cpDefinitionId);
	}

	public static List<CSDiagramEntry> getCProductCSDiagramEntries(
			long cProductId, int start, int end,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws PortalException {

		return getService().getCProductCSDiagramEntries(
			cProductId, start, end, orderByComparator);
	}

	/**
	 * Returns a range of all the cs diagram entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @return the range of cs diagram entries
	 */
	public static List<CSDiagramEntry> getCSDiagramEntries(int start, int end) {
		return getService().getCSDiagramEntries(start, end);
	}

	public static List<CSDiagramEntry> getCSDiagramEntries(
		long cpDefinitionId, int start, int end) {

		return getService().getCSDiagramEntries(cpDefinitionId, start, end);
	}

	/**
	 * Returns the number of cs diagram entries.
	 *
	 * @return the number of cs diagram entries
	 */
	public static int getCSDiagramEntriesCount() {
		return getService().getCSDiagramEntriesCount();
	}

	public static int getCSDiagramEntriesCount(long cpDefinitionId) {
		return getService().getCSDiagramEntriesCount(cpDefinitionId);
	}

	/**
	 * Returns the cs diagram entry with the primary key.
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry
	 * @throws PortalException if a cs diagram entry with the primary key could not be found
	 */
	public static CSDiagramEntry getCSDiagramEntry(long CSDiagramEntryId)
		throws PortalException {

		return getService().getCSDiagramEntry(CSDiagramEntryId);
	}

	public static CSDiagramEntry getCSDiagramEntry(
			long cpDefinitionId, String sequence)
		throws PortalException {

		return getService().getCSDiagramEntry(cpDefinitionId, sequence);
	}

	public static CSDiagramEntry getCSDiagramEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCSDiagramEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the cs diagram entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CSDiagramEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param csDiagramEntry the cs diagram entry
	 * @return the cs diagram entry that was updated
	 */
	public static CSDiagramEntry updateCSDiagramEntry(
		CSDiagramEntry csDiagramEntry) {

		return getService().updateCSDiagramEntry(csDiagramEntry);
	}

	public static CSDiagramEntry updateCSDiagramEntry(
			long csDiagramEntryId, long cpInstanceId, long cProductId,
			boolean diagram, int quantity, String sequence, String sku,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCSDiagramEntry(
			csDiagramEntryId, cpInstanceId, cProductId, diagram, quantity,
			sequence, sku, serviceContext);
	}

	public static CSDiagramEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CSDiagramEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			CSDiagramEntryLocalServiceUtil.class,
			CSDiagramEntryLocalService.class);

}