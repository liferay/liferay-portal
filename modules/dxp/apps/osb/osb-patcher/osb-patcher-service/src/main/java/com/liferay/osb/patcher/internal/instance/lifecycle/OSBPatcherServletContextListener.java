/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.internal.instance.lifecycle;

import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ethan Bustad
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class OSBPatcherServletContextListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		deleteOSBPatcherResourceActions();

		deleteOSBPatcherResourcePermissions();

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		importResourceActions(classLoader);

		InputStream inputStream = classLoader.getResourceAsStream(
			"/com/liferay/osb/patcher/dependencies/roles.xml");

		String xml = new String(FileUtil.getBytes(inputStream));

		Document document = SAXReaderUtil.read(xml);

		Element rootElement = document.getRootElement();

		List<Element> roleElements = rootElement.elements("role");

		for (Element roleElement : roleElements) {
			addRole(roleElement);
		}
	}

	protected void addRole(Element roleElement) throws Exception {
		long companyId = _portal.getDefaultCompanyId();

		String roleName = roleElement.elementText("name");

		boolean organizationRole = GetterUtil.getBoolean(
			roleElement.elementText("organization-role"));

		Role role = _roleLocalService.fetchRole(companyId, roleName);

		if (role == null) {
			int type = RoleConstants.TYPE_REGULAR;

			if (organizationRole) {
				type = RoleConstants.TYPE_ORGANIZATION;
			}

			role = _roleLocalService.addRole(
				null, _userLocalService.getDefaultUserId(companyId), null, 0,
				roleName, null, null, type, null, null);
		}

		int scope = ResourceConstants.SCOPE_COMPANY;

		String primKey = String.valueOf(companyId);

		if (organizationRole) {
			scope = ResourceConstants.SCOPE_GROUP_TEMPLATE;

			primKey = String.valueOf(0);
		}

		List<Element> defaultActionKeysElements = roleElement.elements(
			"default-action-keys");

		for (Element defaultActionKeysElement : defaultActionKeysElements) {
			String name = defaultActionKeysElement.attributeValue(
				"resourceName");

			List<String> actionIds = new ArrayList<>();

			List<Element> actionKeyElements = defaultActionKeysElement.elements(
				"action-key");

			for (Element actionKeyElement : actionKeyElements) {
				actionIds.add(actionKeyElement.getText());
			}

			_resourcePermissionLocalService.setResourcePermissions(
				companyId, name, scope, primKey, role.getRoleId(),
				actionIds.toArray(new String[0]));
		}
	}

	protected void deleteOSBPatcherResourceActions() throws Exception {
		DynamicQuery resourceActionDynamicQuery =
			_resourceActionLocalService.dynamicQuery();

		resourceActionDynamicQuery.add(getOSBPatcherResourceCriterion());

		List<ResourceAction> resourceActions =
			_resourceActionLocalService.dynamicQuery(
				resourceActionDynamicQuery);

		for (ResourceAction resourceAction : resourceActions) {
			_resourceActionLocalService.deleteResourceAction(resourceAction);
		}
	}

	protected void deleteOSBPatcherResourcePermissions() throws Exception {
		DynamicQuery resourcePermissionDynamicQuery =
			_resourcePermissionLocalService.dynamicQuery();

		resourcePermissionDynamicQuery.add(getOSBPatcherResourceCriterion());

		List<ResourcePermission> resourcePermissions =
			_resourcePermissionLocalService.dynamicQuery(
				resourcePermissionDynamicQuery);

		for (ResourcePermission resourcePermission : resourcePermissions) {
			_resourcePermissionLocalService.deleteResourcePermission(
				resourcePermission);
		}
	}

	protected Criterion getOSBPatcherResourceCriterion() throws Exception {
		Criterion criterion1 = RestrictionsFactoryUtil.like(
			"name", "com.liferay.osb.patcher%");
		Criterion criterion2 = RestrictionsFactoryUtil.eq(
			"name", PatcherPortletKeys.PATCHER);

		return RestrictionsFactoryUtil.or(criterion1, criterion2);
	}

	protected void importResourceActions(ClassLoader classLoader)
		throws Exception {

		ResourceActionsUtil.populateModelResources(
			classLoader, "resource-actions/default.xml");
		ResourceActionsUtil.populatePortletResources(
			classLoader, "resource-actions/default.xml");

		for (String portletId : _PORTLET_IDS) {
			List<String> portletActions =
				ResourceActionsUtil.getPortletResourceActions(portletId);

			_resourceActionLocalService.checkResourceActions(
				portletId, portletActions);

			List<String> modelNames =
				ResourceActionsUtil.getPortletModelResources(portletId);

			for (String modelName : modelNames) {
				List<String> modelActions =
					ResourceActionsUtil.getModelResourceActions(modelName);

				_resourceActionLocalService.checkResourceActions(
					modelName, modelActions);
			}
		}
	}

	private static final String[] _PORTLET_IDS = {PatcherPortletKeys.PATCHER};

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}