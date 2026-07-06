/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.user.personal.site.permissions.internal.model.listener;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Tomas Polesovsky
 */
@Component(service = ModelListener.class)
public class CompanyModelListener extends BaseModelListener<Company> {

	@Override
	public void onAfterCreate(Company company) {
		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				PanelCategoryHelper panelCategoryHelper =
					new PanelCategoryHelper(_panelAppRegistry);

				List<Portlet> portlets = TransformUtil.transform(
					panelCategoryHelper.getAllPanelApps(
						PanelCategoryKeys.SITE_ADMINISTRATION),
					panelApp -> _portletLocalService.getPortletById(
						panelApp.getPortletId()));

				_initPermissions(company.getCompanyId(), portlets);

				return null;
			});
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		String filter = StringBundler.concat(
			"(&(objectClass=", PanelApp.class.getName(),
			")(panel.category.key=", PanelCategoryKeys.SITE_ADMINISTRATION,
			"*))");

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, filter,
			new PanelAppServiceTrackerCustomizer(bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private Role _getPowerUserRole(long companyId) {
		try {
			return _roleLocalService.getRole(
				companyId, RoleConstants.POWER_USER);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get power user role in company " + companyId,
				portalException);
		}

		return null;
	}

	private Group _getUserPersonalSiteGroup(long companyId) {
		try {
			return _groupLocalService.getUserPersonalSiteGroup(companyId);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get user personal site group in company " +
					companyId,
				portalException);
		}

		return null;
	}

	private void _initPermissions(Company company, Portlet portlet) {
		long companyId = company.getCompanyId();

		Role powerUserRole = _getPowerUserRole(companyId);

		if (powerUserRole == null) {
			return;
		}

		Group userPersonalSiteGroup = _getUserPersonalSiteGroup(companyId);

		if (userPersonalSiteGroup == null) {
			return;
		}

		try {
			if (companyId == portlet.getCompanyId()) {
				_initPermissions(
					companyId, powerUserRole.getRoleId(),
					portlet.getRootPortletId(),
					userPersonalSiteGroup.getGroupId());
			}
		}
		catch (PortalException portalException) {
			_log.error(
				StringBundler.concat(
					"Unable to initialize user personal site permissions for ",
					"portlet ", portlet.getPortletId(), " in company ",
					company.getCompanyId()),
				portalException);
		}
	}

	private void _initPermissions(long companyId, List<Portlet> portlets) {
		Role powerUserRole = _getPowerUserRole(companyId);

		if (powerUserRole == null) {
			return;
		}

		Group userPersonalSiteGroup = _getUserPersonalSiteGroup(companyId);

		if (userPersonalSiteGroup == null) {
			return;
		}

		for (Portlet portlet : portlets) {
			try {
				if (companyId == portlet.getCompanyId()) {
					_initPermissions(
						companyId, powerUserRole.getRoleId(),
						portlet.getRootPortletId(),
						userPersonalSiteGroup.getGroupId());
				}
			}
			catch (PortalException portalException) {
				_log.error(
					StringBundler.concat(
						"Unable to initialize user personal site permissions ",
						"for portlet ", portlet.getPortletId(), " in company ",
						companyId),
					portalException);
			}
		}
	}

	private void _initPermissions(
			long companyId, long powerUserRoleId, String rootPortletId,
			long userPersonalSiteGroupId)
		throws PortalException {

		String primaryKey = String.valueOf(userPersonalSiteGroupId);

		int count = _resourcePermissionLocalService.getResourcePermissionsCount(
			companyId, rootPortletId, ResourceConstants.SCOPE_GROUP,
			primaryKey);

		if (count == 0) {
			List<String> portletActionIds =
				ResourceActionsUtil.getPortletResourceActions(rootPortletId);

			_resourcePermissionLocalService.setResourcePermissions(
				companyId, rootPortletId, ResourceConstants.SCOPE_GROUP,
				String.valueOf(userPersonalSiteGroupId), powerUserRoleId,
				portletActionIds.toArray(new String[0]));
		}

		String modelName = ResourceActionsUtil.getPortletRootModelResource(
			rootPortletId);

		if (Validator.isBlank(modelName)) {
			return;
		}

		count = _resourcePermissionLocalService.getResourcePermissionsCount(
			companyId, modelName, ResourceConstants.SCOPE_GROUP, primaryKey);

		if (count == 0) {
			List<String> modelActionIds =
				ResourceActionsUtil.getModelResourceActions(modelName);

			_resourcePermissionLocalService.setResourcePermissions(
				companyId, modelName, ResourceConstants.SCOPE_GROUP,
				String.valueOf(userPersonalSiteGroupId), powerUserRoleId,
				modelActionIds.toArray(new String[0]));
		}
	}

	private void _initPermissions(Portlet portlet) {
		_companyLocalService.forEachCompany(
			company -> _initPermissions(company, portlet));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyModelListener.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	private ServiceTracker<PanelApp, PanelApp> _serviceTracker;

	private class PanelAppServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<PanelApp, PanelApp> {

		public PanelAppServiceTrackerCustomizer(BundleContext bundleContext) {
			_bundleContext = bundleContext;
		}

		@Override
		public PanelApp addingService(
			ServiceReference<PanelApp> serviceReference) {

			PanelApp panelApp = _bundleContext.getService(serviceReference);

			try {
				Portlet portlet = panelApp.getPortlet();

				if (portlet == null) {
					portlet = _portletLocalService.getPortletById(
						panelApp.getPortletId());
				}

				if (portlet == null) {
					Class<?> panelAppClass = panelApp.getClass();

					_log.error(
						StringBundler.concat(
							"Unable to get portlet ", panelApp.getPortletId(),
							" for panel app ", panelAppClass.getName()));

					return panelApp;
				}

				_initPermissions(portlet);

				return panelApp;
			}
			catch (Throwable throwable) {
				_bundleContext.ungetService(serviceReference);

				throw throwable;
			}
		}

		@Override
		public void modifiedService(
			ServiceReference<PanelApp> serviceReference, PanelApp panelApp) {
		}

		@Override
		public void removedService(
			ServiceReference<PanelApp> serviceReference, PanelApp panelApp) {

			_bundleContext.ungetService(serviceReference);
		}

		private final BundleContext _bundleContext;

	}

}