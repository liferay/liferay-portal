/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.internal.instance.lifecycle;

import com.liferay.osb.patcher.util.PortletKeys;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.util.BasePortalLifecycle;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.model.ResourceAction;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.ResourcePermission;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.security.permission.ResourceActionsUtil;
import com.liferay.portal.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Ethan Bustad
 */
public class OSBPatcherServletContextListener
	extends BasePortalLifecycle implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		portalDestroy();
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		_servletContext = servletContextEvent.getServletContext();

		registerPortalLifecycle();
	}

	protected void addRole(Element roleElement) throws Exception {
		long companyId = PortalUtil.getDefaultCompanyId();

		String roleName = roleElement.elementText("name");

		boolean organizationRole = GetterUtil.getBoolean(
			roleElement.elementText("organization-role"));

		Role role = RoleLocalServiceUtil.fetchRole(companyId, roleName);

		if (role == null) {
			int type = RoleConstants.TYPE_REGULAR;

			if (organizationRole) {
				type = RoleConstants.TYPE_ORGANIZATION;
			}

			role = RoleLocalServiceUtil.addRole(
				UserLocalServiceUtil.getDefaultUserId(companyId), null, 0,
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

			ResourcePermissionLocalServiceUtil.setResourcePermissions(
				companyId, name, scope, primKey, role.getRoleId(),
				actionIds.toArray(new String[0]));
		}
	}

	protected void deleteOSBPatcherResourceActions() throws Exception {
		DynamicQuery resourceActionDynamicQuery =
			DynamicQueryFactoryUtil.forClass(ResourceAction.class);

		resourceActionDynamicQuery.add(getOSBPatcherResourceCriterion());

		List<ResourceAction> resourceActions =
			ResourceActionLocalServiceUtil.dynamicQuery(
				resourceActionDynamicQuery);

		for (ResourceAction resourceAction : resourceActions) {
			ResourceActionLocalServiceUtil.deleteResourceAction(resourceAction);
		}
	}

	protected void deleteOSBPatcherResourcePermissions() throws Exception {
		DynamicQuery resourcePermissionDynamicQuery =
			DynamicQueryFactoryUtil.forClass(ResourcePermission.class);

		resourcePermissionDynamicQuery.add(getOSBPatcherResourceCriterion());

		List<ResourcePermission> resourcePermissions =
			ResourcePermissionLocalServiceUtil.dynamicQuery(
				resourcePermissionDynamicQuery);

		for (ResourcePermission resourcePermission : resourcePermissions) {
			ResourcePermissionLocalServiceUtil.deleteResourcePermission(
				resourcePermission);
		}
	}

	@Override
	protected void doPortalDestroy() throws Exception {
	}

	@Override
	protected void doPortalInit() throws Exception {
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

	protected Criterion getOSBPatcherResourceCriterion() throws Exception {
		Criterion criterion1 = RestrictionsFactoryUtil.like(
			"name", "com.liferay.osb.patcher%");
		Criterion criterion2 = RestrictionsFactoryUtil.eq(
			"name", PortletKeys.OSB_PATCHER);

		return RestrictionsFactoryUtil.or(criterion1, criterion2);
	}

	protected void importResourceActions(ClassLoader classLoader)
		throws Exception {

		String servletContextName = _servletContext.getServletContextName();

		ResourceActionsUtil.read(
			servletContextName, classLoader, "resource-actions/default.xml");

		for (String portletId : _PORTLET_IDS) {
			List<String> portletActions =
				ResourceActionsUtil.getPortletResourceActions(portletId);

			ResourceActionLocalServiceUtil.checkResourceActions(
				portletId, portletActions);

			List<String> modelNames =
				ResourceActionsUtil.getPortletModelResources(portletId);

			for (String modelName : modelNames) {
				List<String> modelActions =
					ResourceActionsUtil.getModelResourceActions(modelName);

				ResourceActionLocalServiceUtil.checkResourceActions(
					modelName, modelActions);
			}
		}
	}

	private static final String[] _PORTLET_IDS = {PortletKeys.OSB_PATCHER};

	private ServletContext _servletContext;

}