/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.my.account.permissions.internal.model.listener;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsProps;

import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.Arrays;
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
 * @author Drew Brokke
 */
@Component(service = ModelListener.class)
public class CompanyModelListener extends BaseModelListener<Company> {

	@Override
	public void onAfterCreate(Company company) {
		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				PanelCategoryHelper panelCategoryHelper =
					new PanelCategoryHelper(_panelAppRegistry);

				List<PanelApp> panelApps = panelCategoryHelper.getAllPanelApps(
					PanelCategoryKeys.USER_MY_ACCOUNT);

				List<Portlet> portlets = new ArrayList<>(panelApps.size());

				for (PanelApp panelApp : panelApps) {
					Portlet portlet = _portletLocalService.getPortletById(
						panelApp.getPortletId());

					portlets.add(portlet);
				}

				_initPermissions(company.getCompanyId(), portlets);

				return null;
			});
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext,
			StringBundler.concat(
				"(&(objectClass=", PanelApp.class.getName(), ")",
				"(panel.category.key=", PanelCategoryKeys.USER_MY_ACCOUNT,
				"*))"),
			new PanelAppServiceTrackerCustomizer(bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private Role _getUserRole(long companyId) {
		try {
			return _roleLocalService.getRole(companyId, RoleConstants.USER);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get user role in company " + companyId,
				portalException);
		}

		return null;
	}

	private void _initPermissions(long companyId, List<Portlet> portlets) {
		Role userRole = _getUserRole(companyId);

		if (userRole == null) {
			return;
		}

		for (Portlet portlet : portlets) {
			try {
				List<String> actionIds =
					ResourceActionsUtil.getPortletResourceActions(
						portlet.getRootPortletId());

				_initPermissions(
					companyId, portlet.getPortletId(),
					portlet.getRootPortletId(), userRole, actionIds);
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Unable to initialize My Account panel permissions ",
						"for portlet ", portlet.getPortletId(), " in company ",
						companyId),
					exception);
			}
		}
	}

	private void _initPermissions(
			long companyId, String portletId, String rootPortletId,
			Role userRole, List<String> actionIds)
		throws Exception {

		PortletPreferences portletPreferences =
			_portletPreferencesFactory.getLayoutPortletSetup(
				companyId, companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
				LayoutConstants.DEFAULT_PLID, portletId,
				PortletConstants.DEFAULT_PREFERENCES);

		if (_prefsProps.getBoolean(
				portletPreferences,
				"myAccountAccessInControlPanelPermissionsInitialized")) {

			return;
		}

		if (actionIds.contains(ActionKeys.ACCESS_IN_CONTROL_PANEL)) {
			_resourcePermissionLocalService.addResourcePermission(
				companyId, rootPortletId, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(companyId), userRole.getRoleId(),
				ActionKeys.ACCESS_IN_CONTROL_PANEL);
		}

		portletPreferences.setValue(
			"myAccountAccessInControlPanelPermissionsInitialized",
			StringPool.TRUE);

		portletPreferences.store();
	}

	private void _initPermissions(Portlet portlet) {
		_companyLocalService.forEachCompany(
			company -> _initPermissions(
				company.getCompanyId(), Arrays.asList(portlet)));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyModelListener.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Reference
	private PrefsProps _prefsProps;

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