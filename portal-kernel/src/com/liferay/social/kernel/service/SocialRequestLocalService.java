/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.social.kernel.model.SocialRequest;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for SocialRequest. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see SocialRequestLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.social.kernel.model.SocialRequest"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface SocialRequestLocalService
	extends BaseLocalService, CTService<SocialRequest>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.social.service.impl.SocialRequestLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the social request local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link SocialRequestLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds a social request to the database.
	 *
	 * <p>
	 * In order to add a social request, both the requesting user and the
	 * receiving user must be from the same company and neither of them can be
	 * the default user.
	 * </p>
	 *
	 * @param userId the primary key of the requesting user
	 * @param groupId the primary key of the group
	 * @param className the class name of the asset that is the subject of the
	 request
	 * @param classPK the primary key of the asset that is the subject of the
	 request
	 * @param type the request's type
	 * @param extraData the extra data regarding the request
	 * @param receiverUserId the primary key of the user receiving the request
	 * @return the social request
	 */
	public SocialRequest addRequest(
			long userId, long groupId, String className, long classPK, int type,
			String extraData, long receiverUserId)
		throws PortalException;

	/**
	 * Adds the social request to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialRequest the social request
	 * @return the social request that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SocialRequest addSocialRequest(SocialRequest socialRequest);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new social request with the primary key. Does not add the social request to the database.
	 *
	 * @param requestId the primary key for the new social request
	 * @return the new social request
	 */
	@Transactional(enabled = false)
	public SocialRequest createSocialRequest(long requestId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Removes all the social requests for the receiving user.
	 *
	 * @param receiverUserId the primary key of the receiving user
	 */
	public void deleteReceiverUserRequests(long receiverUserId);

	/**
	 * Removes the social request identified by its primary key from the
	 * database.
	 *
	 * @param requestId the primary key of the social request
	 */
	public void deleteRequest(long requestId) throws PortalException;

	/**
	 * Removes the social request from the database.
	 *
	 * @param request the social request to be removed
	 */
	public void deleteRequest(SocialRequest request);

	public void deleteRequests(long className, long classPK);

	/**
	 * Deletes the social request with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param requestId the primary key of the social request
	 * @return the social request that was removed
	 * @throws PortalException if a social request with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public SocialRequest deleteSocialRequest(long requestId)
		throws PortalException;

	/**
	 * Deletes the social request from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialRequest the social request
	 * @return the social request that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public SocialRequest deleteSocialRequest(SocialRequest socialRequest);

	/**
	 * Removes all the social requests for the requesting user.
	 *
	 * @param userId the primary key of the requesting user
	 */
	public void deleteUserRequests(long userId);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
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
	public SocialRequest fetchSocialRequest(long requestId);

	/**
	 * Returns the social request matching the UUID and group.
	 *
	 * @param uuid the social request's UUID
	 * @param groupId the primary key of the group
	 * @return the matching social request, or <code>null</code> if a matching social request could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SocialRequest fetchSocialRequestByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

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

	/**
	 * Returns a range of all the social requests for the receiving user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param receiverUserId the primary key of the receiving user
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching social requests
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialRequest> getReceiverUserRequests(
		long receiverUserId, int start, int end);

	/**
	 * Returns a range of all the social requests with the given status for the
	 * receiving user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param receiverUserId the primary key of the receiving user
	 * @param status the social request's status
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching social requests
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialRequest> getReceiverUserRequests(
		long receiverUserId, int status, int start, int end);

	/**
	 * Returns the number of social requests for the receiving user.
	 *
	 * @param receiverUserId the primary key of the receiving user
	 * @return the number of matching social requests
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getReceiverUserRequestsCount(long receiverUserId);

	/**
	 * Returns the number of social requests with the given status for the
	 * receiving user.
	 *
	 * @param receiverUserId the primary key of the receiving user
	 * @param status the social request's status
	 * @return the number of matching social requests
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getReceiverUserRequestsCount(long receiverUserId, int status);

	/**
	 * Returns the social request with the primary key.
	 *
	 * @param requestId the primary key of the social request
	 * @return the social request
	 * @throws PortalException if a social request with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SocialRequest getSocialRequest(long requestId)
		throws PortalException;

	/**
	 * Returns the social request matching the UUID and group.
	 *
	 * @param uuid the social request's UUID
	 * @param groupId the primary key of the group
	 * @return the matching social request
	 * @throws PortalException if a matching social request could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SocialRequest getSocialRequestByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the social requests.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @return the range of social requests
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialRequest> getSocialRequests(int start, int end);

	/**
	 * Returns all the social requests matching the UUID and company.
	 *
	 * @param uuid the UUID of the social requests
	 * @param companyId the primary key of the company
	 * @return the matching social requests, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialRequest> getSocialRequestsByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of social requests matching the UUID and company.
	 *
	 * @param uuid the UUID of the social requests
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching social requests, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialRequest> getSocialRequestsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator);

	/**
	 * Returns the number of social requests.
	 *
	 * @return the number of social requests
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSocialRequestsCount();

	/**
	 * Returns a range of all the social requests for the requesting user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param userId the primary key of the requesting user
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching social requests
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialRequest> getUserRequests(long userId, int start, int end);

	/**
	 * Returns the number of social requests for the requesting user.
	 *
	 * @param userId the primary key of the requesting user
	 * @return the number of matching social requests
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserRequestsCount(long userId);

	/**
	 * Returns <code>true</code> if a matching social requests exists in the
	 * database.
	 *
	 * @param userId the primary key of the requesting user
	 * @param className the class name of the asset that is the subject of the
	 request
	 * @param classPK the primary key of the asset that is the subject of the
	 request
	 * @param type the request's type
	 * @param status the social request's status
	 * @return <code>true</code> if the request exists; <code>false</code>
	 otherwise
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasRequest(
		long userId, String className, long classPK, int type, int status);

	/**
	 * Returns <code>true</code> if a matching social request exists in the
	 * database.
	 *
	 * @param userId the primary key of the requesting user
	 * @param className the class name of the asset that is the subject of the
	 request
	 * @param classPK the primary key of the asset that is the subject of the
	 request
	 * @param type the request's type
	 * @param receiverUserId the primary key of the receiving user
	 * @param status the social request's status
	 * @return <code>true</code> if the social request exists;
	 <code>false</code> otherwise
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasRequest(
		long userId, String className, long classPK, int type,
		long receiverUserId, int status);

	/**
	 * Updates the social request replacing its status.
	 *
	 * <p>
	 * If the status is updated to {@link SocialRequestConstants#STATUS_CONFIRM}
	 * then {@link
	 * SocialRequestInterpreterLocalService#processConfirmation(
	 * SocialRequest, ThemeDisplay)} is called. If the status is updated to
	 * {@link SocialRequestConstants#STATUS_IGNORE} then {@link
	 * SocialRequestInterpreterLocalService#processRejection(
	 * SocialRequest, ThemeDisplay)} is called.
	 * </p>
	 *
	 * @param requestId the primary key of the social request
	 * @param status the new status
	 * @param themeDisplay the theme display
	 * @return the updated social request
	 */
	public SocialRequest updateRequest(
			long requestId, int status, ThemeDisplay themeDisplay)
		throws PortalException;

	/**
	 * Updates the social request in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialRequest the social request
	 * @return the social request that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SocialRequest updateSocialRequest(SocialRequest socialRequest);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<SocialRequest> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<SocialRequest> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SocialRequest>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:772876241