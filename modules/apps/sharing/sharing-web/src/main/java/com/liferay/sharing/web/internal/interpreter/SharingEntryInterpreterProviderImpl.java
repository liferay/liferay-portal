/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.interpreter;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.sharing.interpreter.SharingEntryInterpreter;
import com.liferay.sharing.interpreter.SharingEntryInterpreterProvider;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.web.internal.renderer.AssetRendererSharingEntryEditRenderer;
import com.liferay.sharing.web.internal.renderer.AssetRendererSharingEntryViewRenderer;

import jakarta.servlet.ServletContext;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = SharingEntryInterpreterProvider.class)
public class SharingEntryInterpreterProviderImpl
	implements SharingEntryInterpreterProvider {

	@Override
	public SharingEntryInterpreter getSharingEntryInterpreter(
		SharingEntry sharingEntry) {

		SharingEntryInterpreter sharingEntryInterpreter =
			_serviceTrackerMap.getService(sharingEntry.getClassName());

		if ((sharingEntryInterpreter == null) &&
			_isAssetObject(sharingEntry.getClassName())) {

			return _assetRendererSharingEntryInterpreter;
		}

		return sharingEntryInterpreter;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_assetRendererSharingEntryInterpreter =
			new AssetRendererSharingEntryInterpreter(
				_assetEntryLocalService,
				new AssetRendererSharingEntryEditRenderer(),
				new AssetRendererSharingEntryViewRenderer(_servletContext));
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, SharingEntryInterpreter.class,
			"(model.class.name=*)",
			(serviceReference, emitter) -> emitter.emit(
				(String)serviceReference.getProperty("model.class.name")));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private boolean _isAssetObject(String className) {
		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if (assetRendererFactory != null) {
			return true;
		}

		return false;
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	private AssetRendererSharingEntryInterpreter
		_assetRendererSharingEntryInterpreter;
	private ServiceTrackerMap<String, SharingEntryInterpreter>
		_serviceTrackerMap;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.sharing.web)")
	private ServletContext _servletContext;

}