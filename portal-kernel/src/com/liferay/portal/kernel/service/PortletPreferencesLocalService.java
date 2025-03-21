/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.settings.PortletInstanceSettingsLocator;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.spring.aop.Property;
import com.liferay.portal.kernel.spring.aop.Retry;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for PortletPreferences. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see PortletPreferencesLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.portal.kernel.model.PortletPreferences"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface PortletPreferencesLocalService
	extends BaseLocalService, CTService<PortletPreferences>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.PortletPreferencesLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the portlet preferences local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link PortletPreferencesLocalServiceUtil} if injection and service tracking are not available.
	 */
	public PortletPreferences addPortletPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, Portlet portlet, String defaultPreferences);

	/**
	 * Adds the portlet preferences to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferences the portlet preferences
	 * @return the portlet preferences that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public PortletPreferences addPortletPreferences(
		PortletPreferences portletPreferences);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new portlet preferences with the primary key. Does not add the portlet preferences to the database.
	 *
	 * @param portletPreferencesId the primary key for the new portlet preferences
	 * @return the new portlet preferences
	 */
	@Transactional(enabled = false)
	public PortletPreferences createPortletPreferences(
		long portletPreferencesId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Deletes the portlet preferences with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferencesId the primary key of the portlet preferences
	 * @return the portlet preferences that was removed
	 * @throws PortalException if a portlet preferences with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public PortletPreferences deletePortletPreferences(
			long portletPreferencesId)
		throws PortalException;

	public void deletePortletPreferences(
		long ownerId, int ownerType, long plid);

	public void deletePortletPreferences(
			long ownerId, int ownerType, long plid, String portletId)
		throws PortalException;

	/**
	 * Deletes the portlet preferences from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferences the portlet preferences
	 * @return the portlet preferences that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public PortletPreferences deletePortletPreferences(
		PortletPreferences portletPreferences);

	public void deletePortletPreferencesByOwnerId(long ownerId);

	public void deletePortletPreferencesByPlid(long plid);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferencesModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferencesModelImpl</code>.
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
	public PortletPreferences fetchPortletPreferences(
		long portletPreferencesId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PortletPreferences fetchPortletPreferences(
		long ownerId, int ownerType, long plid, String portletId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public jakarta.portlet.PortletPreferences fetchPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public jakarta.portlet.PortletPreferences fetchPreferences(
		PortletPreferencesIds portletPreferencesIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(enabled = false)
	public jakarta.portlet.PortletPreferences getDefaultPreferences(
		long companyId, String portletId);

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

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Settings getPortletInstanceSettings(
		long companyId, long groupId, String portletId,
		PortletInstanceSettingsLocator portletInstanceSettingsLocator,
		Settings portalPreferencesSettings);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PortletPreferences> getPortletPreferences();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PortletPreferences> getPortletPreferences(
		int ownerType, long plid, String portletId);

	/**
	 * Returns the portlet preferences with the primary key.
	 *
	 * @param portletPreferencesId the primary key of the portlet preferences
	 * @return the portlet preferences
	 * @throws PortalException if a portlet preferences with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PortletPreferences getPortletPreferences(long portletPreferencesId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PortletPreferences> getPortletPreferences(
		long ownerId, int ownerType, long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PortletPreferences getPortletPreferences(
			long ownerId, int ownerType, long plid, String portletId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PortletPreferences> getPortletPreferences(
			long companyId, long ownerId, int ownerType, String portletId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PortletPreferences> getPortletPreferences(
		long companyId, long groupId, long ownerId, int ownerType,
		String portletId, boolean privateLayout);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PortletPreferences> getPortletPreferences(
		long plid, String portletId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PortletPreferences> getPortletPreferencesByOwnerId(
		long ownerId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PortletPreferences> getPortletPreferencesByPlid(long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PortletPreferences> getPortletPreferencesByPortletId(
		String portletId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getPortletPreferencesCount(
		int ownerType, long plid, String portletId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getPortletPreferencesCount(int ownerType, String portletId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getPortletPreferencesCount(
		long ownerId, int ownerType, long plid, Portlet portlet,
		boolean excludeDefaultPreferences);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getPortletPreferencesCount(
		long ownerId, int ownerType, String portletId,
		boolean excludeDefaultPreferences);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPortletPreferencesCount(
		long companyId, long ownerId, int ownerType, String portletId);

	/**
	 * Returns a range of all the portlet preferenceses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portlet preferenceses
	 * @param end the upper bound of the range of portlet preferenceses (not inclusive)
	 * @return the range of portlet preferenceses
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PortletPreferences> getPortletPreferenceses(int start, int end);

	/**
	 * Returns the number of portlet preferenceses.
	 *
	 * @return the number of portlet preferenceses
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPortletPreferencesesCount();

	@Retry(
		acceptor = SQLStateAcceptor.class,
		properties = {
			@Property(
				name = SQLStateAcceptor.SQLSTATE,
				value = SQLStateAcceptor.SQLSTATE_INTEGRITY_CONSTRAINT_VIOLATION
			)
		}
	)
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public jakarta.portlet.PortletPreferences getPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId);

	@Retry(
		acceptor = SQLStateAcceptor.class,
		properties = {
			@Property(
				name = SQLStateAcceptor.SQLSTATE,
				value = SQLStateAcceptor.SQLSTATE_INTEGRITY_CONSTRAINT_VIOLATION
			)
		}
	)
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public jakarta.portlet.PortletPreferences getPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, String defaultPreferences);

	@Retry(
		acceptor = SQLStateAcceptor.class,
		properties = {
			@Property(
				name = SQLStateAcceptor.SQLSTATE,
				value = SQLStateAcceptor.SQLSTATE_INTEGRITY_CONSTRAINT_VIOLATION
			)
		}
	)
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public jakarta.portlet.PortletPreferences getPreferences(
		PortletPreferencesIds portletPreferencesIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<String, jakarta.portlet.PortletPreferences> getStrictPreferences(
		Layout layout, List<Portlet> portlets);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public jakarta.portlet.PortletPreferences getStrictPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public jakarta.portlet.PortletPreferences getStrictPreferences(
		PortletPreferencesIds portletPreferencesIds);

	/**
	 * Updates the portlet preferences in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferences the portlet preferences
	 * @return the portlet preferences that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public PortletPreferences updatePortletPreferences(
		PortletPreferences portletPreferences);

	public PortletPreferences updatePreferences(
		long ownerId, int ownerType, long plid, String portletId,
		jakarta.portlet.PortletPreferences portletPreferences);

	public PortletPreferences updatePreferences(
		long ownerId, int ownerType, long plid, String portletId, String xml);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<PortletPreferences> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<PortletPreferences> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<PortletPreferences>, R, E>
				updateUnsafeFunction)
		throws E;

}