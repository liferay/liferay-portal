/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.model.listener;

import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.util.PortalInstances;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tejesh Boggavarapu
 */
@Component(service = ModelListener.class)
public class ObjectDefinitionModelListener
	extends BaseModelListener<ObjectDefinition> {

	@Override
	public void onAfterRemove(ObjectDefinition objectDefinition)
		throws ModelListenerException {

		if (PortalInstances.isCompanyInDeletionProcess(
				objectDefinition.getCompanyId())) {

			return;
		}

		try {
			_deleteOAuth2ScopeGrants(objectDefinition);
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	private void _deleteOAuth2ScopeGrants(ObjectDefinition objectDefinition)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			_oAuth2ScopeGrantLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"companyId", objectDefinition.getCompanyId())
			).add(
				RestrictionsFactoryUtil.eq(
					"applicationName", objectDefinition.getOSGiJaxRsName())
			).add(
				RestrictionsFactoryUtil.eq(
					"bundleSymbolicName", "com.liferay.object.rest.impl")
			));
		actionableDynamicQuery.setPerformActionMethod(
			oAuth2ScopeGrant ->
				_oAuth2ScopeGrantLocalService.deleteOAuth2ScopeGrant(
					(OAuth2ScopeGrant)oAuth2ScopeGrant));

		actionableDynamicQuery.performActions();
	}

	@Reference
	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

}