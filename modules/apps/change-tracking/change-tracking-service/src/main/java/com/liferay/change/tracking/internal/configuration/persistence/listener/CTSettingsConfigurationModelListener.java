/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.configuration.persistence.listener;

import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Dictionary;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	property = "model.class.name=com.liferay.change.tracking.configuration.CTSettingsConfiguration",
	service = ConfigurationModelListener.class
)
public class CTSettingsConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onAfterSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		try {
			_onAfterSave(properties);
		}
		catch (PortalException portalException) {
			throw new ConfigurationModelListenerException(
				portalException, CTSettingsConfiguration.class, getClass(),
				properties);
		}
	}

	private void _onAfterSave(Dictionary<String, Object> properties)
		throws PortalException {

		long companyId = GetterUtil.getLong(properties.get("companyId"));

		if (companyId == 0) {
			return;
		}

		String[] defaultOwnerActionIds = GetterUtil.getStringValues(
			properties.get("defaultOwnerActionIds"));

		if (ArrayUtil.isEmpty(defaultOwnerActionIds)) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			Role role = _roleLocalService.getRole(
				companyId, RoleConstants.OWNER);

			List<CTCollection> ctCollections =
				_ctCollectionLocalService.getCTCollections(
					companyId, WorkflowConstants.STATUS_DRAFT,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			for (CTCollection ctCollection : ctCollections) {
				_resourcePermissionLocalService.setResourcePermissions(
					companyId, CTCollection.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(ctCollection.getCtCollectionId()),
					role.getRoleId(), defaultOwnerActionIds);
			}
		}
	}

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}