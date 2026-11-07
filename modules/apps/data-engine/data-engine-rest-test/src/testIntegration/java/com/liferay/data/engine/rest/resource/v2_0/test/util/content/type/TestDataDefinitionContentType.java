/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.resource.v2_0.test.util.content.type;

import com.liferay.data.engine.content.type.DataDefinitionContentType;
import com.liferay.data.engine.rest.resource.v2_0.test.util.content.type.test.util.ModelResourceActionTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rodrigo Paulino
 */
@Component(
	property = "content.type=test", service = DataDefinitionContentType.class
)
public class TestDataDefinitionContentType
	implements DataDefinitionContentType {

	@Override
	public boolean allowEmptyDataDefinition() {
		return _allowEmptyDataDefinition;
	}

	@Override
	public boolean allowInvalidAvailableLocalesForProperty() {
		return true;
	}

	@Override
	public boolean allowReferencedDataDefinitionDeletion() {
		return true;
	}

	@Override
	public long getClassNameId() {
		return _portal.getClassNameId(TestDataDefinitionContentType.class);
	}

	@Override
	public String getContentType() {
		return "test";
	}

	@Override
	public String getPortletResourceName() {
		return ModelResourceActionTestUtil.PORTLET_RESOURCE_NAME;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long companyId, long groupId,
			String resourceName, long primKey, long userId, String actionId)
		throws PortalException {

		return true;
	}

	@Override
	public boolean hasPortletPermission(
			PermissionChecker permissionChecker, long groupId, String actionId)
		throws PortalException {

		return true;
	}

	@Override
	public boolean isDataRecordCollectionPermissionCheckingEnabled() {
		return true;
	}

	public void setAllowEmptyDataDefinition(boolean allowEmptyDataDefinition) {
		_allowEmptyDataDefinition = allowEmptyDataDefinition;
	}

	private boolean _allowEmptyDataDefinition = true;

	@Reference
	private Portal _portal;

}