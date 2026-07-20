/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.model.MBThreadFlag;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for MBThreadFlag. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see MBThreadFlagLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface MBThreadFlagLocalService
	extends BaseLocalService, CTService<MBThreadFlag>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.message.boards.service.impl.MBThreadFlagLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the message boards thread flag local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link MBThreadFlagLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the message boards thread flag to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBThreadFlagLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbThreadFlag the message boards thread flag
	 * @return the message boards thread flag that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public MBThreadFlag addMBThreadFlag(MBThreadFlag mbThreadFlag);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBThreadFlag addThreadFlag(
			long userId, MBThread thread, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new message boards thread flag with the primary key. Does not add the message boards thread flag to the database.
	 *
	 * @param threadFlagId the primary key for the new message boards thread flag
	 * @return the new message boards thread flag
	 */
	@Transactional(enabled = false)
	public MBThreadFlag createMBThreadFlag(long threadFlagId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the message boards thread flag with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBThreadFlagLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param threadFlagId the primary key of the message boards thread flag
	 * @return the message boards thread flag that was removed
	 * @throws PortalException if a message boards thread flag with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public MBThreadFlag deleteMBThreadFlag(long threadFlagId)
		throws PortalException;

	/**
	 * Deletes the message boards thread flag from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBThreadFlagLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbThreadFlag the message boards thread flag
	 * @return the message boards thread flag that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public MBThreadFlag deleteMBThreadFlag(MBThreadFlag mbThreadFlag);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteThreadFlag(long threadFlagId) throws PortalException;

	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteThreadFlag(MBThreadFlag threadFlag);

	public void deleteThreadFlagsByThreadId(long threadId);

	public void deleteThreadFlagsByUserId(long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadFlagModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadFlagModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBThreadFlag fetchMBThreadFlag(long threadFlagId);

	/**
	 * Returns the message boards thread flag matching the UUID and group.
	 *
	 * @param uuid the message boards thread flag's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards thread flag, or <code>null</code> if a matching message boards thread flag could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBThreadFlag fetchMBThreadFlagByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBThreadFlag fetchThreadFlag(long userId, MBThread thread);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the message boards thread flag with the primary key.
	 *
	 * @param threadFlagId the primary key of the message boards thread flag
	 * @return the message boards thread flag
	 * @throws PortalException if a message boards thread flag with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBThreadFlag getMBThreadFlag(long threadFlagId)
		throws PortalException;

	/**
	 * Returns the message boards thread flag matching the UUID and group.
	 *
	 * @param uuid the message boards thread flag's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards thread flag
	 * @throws PortalException if a matching message boards thread flag could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBThreadFlag getMBThreadFlagByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the message boards thread flags.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadFlagModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message boards thread flags
	 * @param end the upper bound of the range of message boards thread flags (not inclusive)
	 * @return the range of message boards thread flags
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBThreadFlag> getMBThreadFlags(int start, int end);

	/**
	 * Returns all the message boards thread flags matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards thread flags
	 * @param companyId the primary key of the company
	 * @return the matching message boards thread flags, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBThreadFlag> getMBThreadFlagsByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of message boards thread flags matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards thread flags
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of message boards thread flags
	 * @param end the upper bound of the range of message boards thread flags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching message boards thread flags, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBThreadFlag> getMBThreadFlagsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBThreadFlag> orderByComparator);

	/**
	 * Returns the number of message boards thread flags.
	 *
	 * @return the number of message boards thread flags
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getMBThreadFlagsCount();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBThreadFlag getThreadFlag(long userId, MBThread thread)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasThreadFlag(long userId, MBThread thread)
		throws PortalException;

	/**
	 * Updates the message boards thread flag in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBThreadFlagLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbThreadFlag the message boards thread flag
	 * @return the message boards thread flag that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public MBThreadFlag updateMBThreadFlag(MBThreadFlag mbThreadFlag);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<MBThreadFlag> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<MBThreadFlag> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<MBThreadFlag>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:1722500708