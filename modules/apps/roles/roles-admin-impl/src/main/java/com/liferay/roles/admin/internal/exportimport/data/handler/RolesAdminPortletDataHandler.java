/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.internal.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Conjunction;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.roles.admin.constants.RolesAdminPortletKeys;

import jakarta.portlet.PortletPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author David Mendez Gonzalez
 */
@Component(
	property = "jakarta.portlet.name=" + RolesAdminPortletKeys.ROLES_ADMIN,
	service = PortletDataHandler.class
)
public class RolesAdminPortletDataHandler extends BasePortletDataHandler {

	public static final String NAMESPACE = "roles_admin";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public boolean isSupportsDataStrategyCopyAsNew() {
		return false;
	}

	@Activate
	protected void activate() {
		setDataLevel(DataLevel.PORTAL);
		setDeletionSystemEventStagedModelTypes(new StagedModelType(Role.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "roles", true, true,
				new PortletDataHandlerControl[] {
					new PortletDataHandlerBoolean(
						NAMESPACE, "system-roles", true, false)
				},
				Role.class.getName(), StagedModelType.REFERRER_CLASS_NAME_ALL));

		Collections.addAll(
			_allSystemRoleNames, _portal.getSystemOrganizationRoles());
		Collections.addAll(_allSystemRoleNames, _portal.getSystemRoles());
		Collections.addAll(_allSystemRoleNames, _portal.getSystemSiteRoles());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				RolesAdminPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		List<Role> roles = _roleLocalService.getRoles(
			portletDataContext.getCompanyId());

		for (Role role : roles) {
			if (!role.isSystem() && !role.isTeam()) {
				_roleLocalService.deleteRole(role);
			}
		}

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		portletDataContext.addPortalPermissions();

		Element rootElement = addExportDataRootElement(portletDataContext);

		rootElement.addAttribute(
			"group-id", String.valueOf(portletDataContext.getScopeGroupId()));

		ActionableDynamicQuery actionableDynamicQuery =
			_getRoleActionableDynamicQuery(portletDataContext, true);

		actionableDynamicQuery.performActions();

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		portletDataContext.importPortalPermissions();

		Element rolesElement = portletDataContext.getImportDataGroupElement(
			Role.class);

		List<Element> roleElements = rolesElement.elements();

		for (Element roleElement : roleElements) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, roleElement);
		}

		return null;
	}

	@Override
	protected void doPrepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		ActionableDynamicQuery actionableDynamicQuery =
			_getRoleActionableDynamicQuery(portletDataContext, false);

		actionableDynamicQuery.performCount();
	}

	private ActionableDynamicQuery _getRoleActionableDynamicQuery(
		PortletDataContext portletDataContext, boolean export) {

		ActionableDynamicQuery actionableDynamicQuery =
			_roleLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				portletDataContext.addDateRangeCriteria(
					dynamicQuery, "modifiedDate");

				long classNameId = _portal.getClassNameId(Team.class);

				Property classNameIdProperty = PropertyFactoryUtil.forName(
					"classNameId");

				dynamicQuery.add(classNameIdProperty.ne(classNameId));

				if (!portletDataContext.getBooleanParameter(
						NAMESPACE, "system-roles")) {

					Conjunction conjunction =
						RestrictionsFactoryUtil.conjunction();

					Property nameProperty = PropertyFactoryUtil.forName("name");

					for (String roleName : _allSystemRoleNames) {
						conjunction.add(nameProperty.ne(roleName));
					}

					dynamicQuery.add(conjunction);
				}
			});

		@SuppressWarnings("unchecked")
		final ActionableDynamicQuery.PerformActionMethod<Role>
			performActionMethod =
				(ActionableDynamicQuery.PerformActionMethod<Role>)
					actionableDynamicQuery.getPerformActionMethod();

		ActionableDynamicQuery.PerformActionMethod<Role>
			performActionMethodWrapper =
				new RoleExportActionableDynamicQueryPerformActionMethod(
					performActionMethod, portletDataContext, export);

		actionableDynamicQuery.setPerformActionMethod(
			performActionMethodWrapper);

		return actionableDynamicQuery;
	}

	private final Set<String> _allSystemRoleNames = new HashSet<>();

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	private class RoleExportActionableDynamicQueryPerformActionMethod
		implements ActionableDynamicQuery.PerformActionMethod<Role> {

		public RoleExportActionableDynamicQueryPerformActionMethod(
			ActionableDynamicQuery.PerformActionMethod<Role>
				performActionMethod,
			PortletDataContext portletDataContext, boolean export) {

			_performActionMethod = performActionMethod;
			_portletDataContext = portletDataContext;
			_export = export;
		}

		@Override
		public void performAction(Role role) throws PortalException {
			if (!_export ||
				(!_portletDataContext.getBooleanParameter(
					NAMESPACE, "system-roles") &&
				 _allSystemRoleNames.contains(role.getName()))) {

				return;
			}

			_performActionMethod.performAction(role);
		}

		private final boolean _export;
		private final ActionableDynamicQuery.PerformActionMethod<Role>
			_performActionMethod;
		private final PortletDataContext _portletDataContext;

	}

}