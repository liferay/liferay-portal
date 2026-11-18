/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.instance.lifecycle;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.serializer.FDSSerializer;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.instance.lifecycle.InitialRequestPortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.pim.site.initializer.internal.fragment.renderer.ProductRelationshipsSectionFragmentRenderer;
import com.liferay.site.pim.site.initializer.internal.fragment.renderer.ProductsSectionFragmentRenderer;
import com.liferay.site.pim.site.initializer.internal.util.PIMObjectEntryFolderUtil;
import com.liferay.site.pim.site.initializer.internal.util.SiteInitializerUtil;

import jakarta.servlet.ServletContext;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class PIMInitialRequestPortalInstanceLifecycleListener
	extends InitialRequestPortalInstanceLifecycleListener {

	@Override
	public long getLastModifiedTime() {
		return _lastModifiedTime;
	}

	@Activate
	@Override
	protected void activate(BundleContext bundleContext) {
		BundleReference bundleReference =
			(BundleReference)_pimServletContext.getClassLoader();

		Bundle pimBundle = bundleReference.getBundle();

		Bundle bundle = bundleContext.getBundle();

		_lastModifiedTime = Math.max(
			bundle.getLastModified(), pimBundle.getLastModified());

		_serviceRegistrations.add(
			bundleContext.registerService(
				FragmentRenderer.class,
				new ProductRelationshipsSectionFragmentRenderer(
					_fdsSerializer, _language, _objectEntryLocalService,
					_servletContext),
				null));
		_serviceRegistrations.add(
			bundleContext.registerService(
				FragmentRenderer.class,
				new ProductsSectionFragmentRenderer(_language, _servletContext),
				null));

		super.activate(bundleContext);
	}

	@Deactivate
	protected void deactivate() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	@Override
	protected void doPortalInstanceRegistered(long companyId) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-96666") ||
			(_groupLocalService.fetchGroup(companyId, GroupConstants.CMS) ==
				null)) {

			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			SiteInitializerUtil.initialize(
				companyId, _cmsSiteInitializer, _pimSiteInitializer);

			PIMObjectEntryFolderUtil.addProductsObjectEntryFolders(
				companyId, _depotEntryLocalService,
				_objectEntryFolderLocalService);
		}
	}

	@Reference(
		target = "(site.initializer.key=com.liferay.site.initializer.cms)"
	)
	private SiteInitializer _cmsSiteInitializer;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private FDSSerializer _fdsSerializer;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	private long _lastModifiedTime;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.site.initializer.pim)"
	)
	private ServletContext _pimServletContext;

	@Reference(
		target = "(site.initializer.key=com.liferay.site.initializer.pim)"
	)
	private SiteInitializer _pimSiteInitializer;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.site.pim.site.initializer)"
	)
	private ServletContext _servletContext;

}