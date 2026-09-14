/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for ObjectField. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Marco Leo
 * @see ObjectFieldServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ObjectFieldService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectFieldServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the object field remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ObjectFieldServiceUtil} if injection and service tracking are not available.
	 */
	public ObjectField addCustomObjectField(
			String externalReferenceCode, long listTypeDefinitionId,
			long objectDefinitionId, String businessType, String dbType,
			Map<Locale, String> descriptionMap, boolean indexed,
			boolean indexedAsKeyword, String indexedLanguageId,
			Map<Locale, String> labelMap, boolean localized, String name,
			String readOnly, String readOnlyConditionExpression,
			boolean required, boolean state,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException;

	public ObjectField deleteObjectField(long objectFieldId) throws Exception;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectField getObjectField(long objectFieldId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public ObjectField updateObjectField(
			String externalReferenceCode, long objectFieldId,
			long listTypeDefinitionId, String businessType, String dbType,
			Map<Locale, String> descriptionMap, boolean indexed,
			boolean indexedAsKeyword, String indexedLanguageId,
			Map<Locale, String> labelMap, boolean localized, String name,
			String readOnly, String readOnlyConditionExpression,
			boolean required, boolean state,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:849419468