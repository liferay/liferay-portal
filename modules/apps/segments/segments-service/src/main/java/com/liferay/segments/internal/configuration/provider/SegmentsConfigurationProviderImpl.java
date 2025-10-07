/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.configuration.provider;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.configuration.SegmentsCompanyConfiguration;
import com.liferay.segments.configuration.SegmentsConfiguration;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	configurationPid = "com.liferay.segments.configuration.SegmentsConfiguration",
	service = SegmentsConfigurationProvider.class
)
public class SegmentsConfigurationProviderImpl
	implements SegmentsConfigurationProvider {

	@Override
	public void clearSegmentsCompanyConfigurations() {
		_companyIds.clear();
		_pids.clear();
		_segmentsCompanyConfigurations.clear();
	}

	@Override
	public String getCompanyConfigurationURL(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_portal.getUser(httpServletRequest));

		if (permissionChecker.isCompanyAdmin()) {
			String factoryPid = SegmentsCompanyConfiguration.class.getName();

			String pid = factoryPid;

			SegmentsCompanyConfiguration segmentsCompanyConfiguration =
				_segmentsCompanyConfigurations.getOrDefault(
					_portal.getCompanyId(httpServletRequest),
					_configurationProvider.getCompanyConfiguration(
						SegmentsCompanyConfiguration.class,
						_portal.getCompanyId(httpServletRequest)));

			if (segmentsCompanyConfiguration != null) {
				pid = _pids.get(_portal.getCompanyId(httpServletRequest));
			}

			return PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					httpServletRequest,
					ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/configuration_admin/edit_configuration"
			).setRedirect(
				ParamUtil.getString(
					httpServletRequest, "backURL",
					_portal.getCurrentCompleteURL(httpServletRequest))
			).setParameter(
				"factoryPid", factoryPid
			).setParameter(
				"pid", pid
			).buildString();
		}

		return null;
	}

	@Override
	public String getConfigurationURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_portal.getUser(httpServletRequest));

		if (!permissionChecker.isOmniadmin()) {
			return null;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/configuration_admin/edit_configuration"
		).setRedirect(
			_portal.getCurrentCompleteURL(httpServletRequest)
		).setParameter(
			"factoryPid", SegmentsConfiguration.class.getName()
		).buildString();
	}

	@Override
	public boolean isRoleSegmentationEnabled() throws ConfigurationException {
		return _segmentsConfiguration.roleSegmentationEnabled();
	}

	@Override
	public boolean isRoleSegmentationEnabled(long companyId)
		throws ConfigurationException {

		if (!_segmentsConfiguration.roleSegmentationEnabled()) {
			return false;
		}

		SegmentsCompanyConfiguration segmentsCompanyConfiguration =
			_segmentsCompanyConfigurations.getOrDefault(
				companyId,
				_configurationProvider.getCompanyConfiguration(
					SegmentsCompanyConfiguration.class, companyId));

		if (segmentsCompanyConfiguration == null) {
			return _segmentsConfiguration.roleSegmentationEnabled();
		}

		return segmentsCompanyConfiguration.roleSegmentationEnabled();
	}

	@Override
	public boolean isSegmentationEnabled() throws ConfigurationException {
		return _segmentsConfiguration.segmentationEnabled();
	}

	@Override
	public boolean isSegmentationEnabled(long companyId)
		throws ConfigurationException {

		if (!_segmentsConfiguration.segmentationEnabled()) {
			return false;
		}

		SegmentsCompanyConfiguration segmentsCompanyConfiguration =
			_segmentsCompanyConfigurations.getOrDefault(
				companyId,
				_configurationProvider.getCompanyConfiguration(
					SegmentsCompanyConfiguration.class, companyId));

		if (segmentsCompanyConfiguration == null) {
			return _segmentsConfiguration.segmentationEnabled();
		}

		return segmentsCompanyConfiguration.segmentationEnabled();
	}

	@Override
	public boolean isSegmentsCompanyConfigurationDefined(long companyId)
		throws ConfigurationException {

		SegmentsCompanyConfiguration segmentsCompanyConfiguration =
			_segmentsCompanyConfigurations.getOrDefault(
				companyId,
				_configurationProvider.getCompanyConfiguration(
					SegmentsCompanyConfiguration.class, companyId));

		if (segmentsCompanyConfiguration != null) {
			return true;
		}

		return false;
	}

	@Override
	public void resetSegmentsCompanyConfiguration(long companyId)
		throws ConfigurationException {

		_configurationProvider.deleteCompanyConfiguration(
			SegmentsCompanyConfiguration.class, companyId);
	}

	@Override
	public void updateSegmentsCompanyConfiguration(
			long companyId,
			SegmentsCompanyConfiguration segmentsCompanyConfiguration)
		throws ConfigurationException {

		_configurationProvider.saveCompanyConfiguration(
			SegmentsCompanyConfiguration.class, companyId,
			HashMapDictionaryBuilder.<String, Object>put(
				"roleSegmentationEnabled",
				segmentsCompanyConfiguration.roleSegmentationEnabled()
			).put(
				"segmentationEnabled",
				segmentsCompanyConfiguration.segmentationEnabled()
			).build());
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		_serviceRegistration = bundleContext.registerService(
			ManagedServiceFactory.class,
			new SegmentsCompanyConfigurationManagedServiceFactory(),
			HashMapDictionaryBuilder.put(
				Constants.SERVICE_PID,
				"com.liferay.segments.configuration." +
					"SegmentsCompanyConfiguration.scoped"
			).build());
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_segmentsConfiguration = ConfigurableUtil.createConfigurable(
			SegmentsConfiguration.class, properties);
	}

	private void _unmapPid(String pid) {
		Long companyId = _companyIds.remove(pid);

		if (companyId != null) {
			_pids.remove(companyId);
			_segmentsCompanyConfigurations.remove(companyId);
		}
	}

	private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	private final Map<Long, String> _pids = new ConcurrentHashMap<>();

	@Reference
	private Portal _portal;

	private final Map<Long, SegmentsCompanyConfiguration>
		_segmentsCompanyConfigurations = new ConcurrentHashMap<>();
	private volatile SegmentsConfiguration _segmentsConfiguration;
	private ServiceRegistration<ManagedServiceFactory> _serviceRegistration;

	private class SegmentsCompanyConfigurationManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			_unmapPid(pid);
		}

		@Override
		public String getName() {
			return "com.liferay.segments.configuration." +
				"SegmentsCompanyConfiguration.scoped";
		}

		@Override
		public void updated(String pid, Dictionary<String, ?> dictionary) {
			_unmapPid(pid);

			long companyId = GetterUtil.getLong(
				dictionary.get("companyId"), CompanyConstants.SYSTEM);

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						companyId)) {

				if (companyId != CompanyConstants.SYSTEM) {
					_segmentsCompanyConfigurations.put(
						companyId,
						ConfigurableUtil.createConfigurable(
							SegmentsCompanyConfiguration.class, dictionary));
					_companyIds.put(pid, companyId);
					_pids.put(companyId, pid);
				}
			}
		}

	}

}