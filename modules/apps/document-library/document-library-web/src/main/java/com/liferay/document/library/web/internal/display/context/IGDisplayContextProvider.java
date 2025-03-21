/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.display.context.IGDisplayContextFactory;
import com.liferay.document.library.display.context.IGViewFileVersionDisplayContext;
import com.liferay.document.library.kernel.versioning.VersioningStrategy;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.document.library.web.internal.helper.DLTrashHelper;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Iván Zaera
 */
@Component(service = IGDisplayContextProvider.class)
public class IGDisplayContextProvider {

	public IGViewFileVersionDisplayContext
		getIGViewFileVersionActionsDisplayContext(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			FileShortcut fileShortcut) {

		try {
			IGViewFileVersionDisplayContext igViewFileVersionDisplayContext =
				new DefaultIGViewFileVersionDisplayContext(
					_dlTrashHelper, _dlURLHelper, fileShortcut,
					httpServletRequest, _versioningStrategy);

			if (fileShortcut == null) {
				return igViewFileVersionDisplayContext;
			}

			for (IGDisplayContextFactory igDisplayContextFactory :
					_igDisplayContextFactories) {

				igViewFileVersionDisplayContext =
					igDisplayContextFactory.getIGViewFileVersionDisplayContext(
						igViewFileVersionDisplayContext, httpServletRequest,
						httpServletResponse, fileShortcut);
			}

			return igViewFileVersionDisplayContext;
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	public IGViewFileVersionDisplayContext
		getIGViewFileVersionActionsDisplayContext(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FileVersion fileVersion) {

		try {
			IGViewFileVersionDisplayContext igViewFileVersionDisplayContext =
				new DefaultIGViewFileVersionDisplayContext(
					httpServletRequest, fileVersion, _dlTrashHelper,
					_versioningStrategy, _dlURLHelper);

			if (fileVersion == null) {
				return igViewFileVersionDisplayContext;
			}

			for (IGDisplayContextFactory igDisplayContextFactory :
					_igDisplayContextFactories) {

				igViewFileVersionDisplayContext =
					igDisplayContextFactory.getIGViewFileVersionDisplayContext(
						igViewFileVersionDisplayContext, httpServletRequest,
						httpServletResponse, fileVersion);
			}

			return igViewFileVersionDisplayContext;
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_igDisplayContextFactories = ServiceTrackerListFactory.open(
			bundleContext, IGDisplayContextFactory.class);
	}

	@Deactivate
	protected void deactivate(BundleContext bundleContext) {
		_igDisplayContextFactories.close();
	}

	@Reference
	private DLTrashHelper _dlTrashHelper;

	@Reference
	private DLURLHelper _dlURLHelper;

	private ServiceTrackerList<IGDisplayContextFactory>
		_igDisplayContextFactories;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile VersioningStrategy _versioningStrategy;

}