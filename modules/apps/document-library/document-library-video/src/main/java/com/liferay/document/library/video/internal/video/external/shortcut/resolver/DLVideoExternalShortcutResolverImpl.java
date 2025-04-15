/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.video.external.shortcut.resolver;

import com.liferay.document.library.video.external.shortcut.DLVideoExternalShortcut;
import com.liferay.document.library.video.external.shortcut.provider.DLVideoExternalShortcutProvider;
import com.liferay.document.library.video.external.shortcut.resolver.DLVideoExternalShortcutResolver;
import com.liferay.document.library.video.internal.constants.DLVideoConstants;
import com.liferay.document.library.video.internal.helper.DLVideoExternalShortcutMetadataHelper;
import com.liferay.document.library.video.internal.helper.DLVideoExternalShortcutMetadataHelperFactory;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.repository.model.FileVersion;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DLVideoExternalShortcutResolver.class)
public class DLVideoExternalShortcutResolverImpl
	implements DLVideoExternalShortcutResolver {

	@Override
	public DLVideoExternalShortcut resolve(FileVersion fileVersion) {
		DLVideoExternalShortcutMetadataHelper
			dlVideoExternalShortcutMetadataHelper =
				_dlVideoExternalShortcutMetadataHelperFactory.
					getDLVideoExternalShortcutMetadataHelper(fileVersion);

		if (dlVideoExternalShortcutMetadataHelper == null) {
			return null;
		}

		return _getDLVideoExternalShortcut(
			dlVideoExternalShortcutMetadataHelper);
	}

	@Override
	public DLVideoExternalShortcut resolve(String url) {
		for (DLVideoExternalShortcutProvider dlVideoExternalShortcutProvider :
				_dlVideoExternalShortcutProviders) {

			DLVideoExternalShortcut dlVideoExternalShortcut =
				dlVideoExternalShortcutProvider.getDLVideoExternalShortcut(url);

			if (dlVideoExternalShortcut != null) {
				return dlVideoExternalShortcut;
			}
		}

		return null;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_dlVideoExternalShortcutProviders = ServiceTrackerListFactory.open(
			bundleContext, DLVideoExternalShortcutProvider.class);
	}

	@Deactivate
	protected void deactivate() {
		_dlVideoExternalShortcutProviders.close();
	}

	private DLVideoExternalShortcut _getDLVideoExternalShortcut(
		DLVideoExternalShortcutMetadataHelper
			dlVideoExternalShortcutMetadataHelper) {

		return new DLVideoExternalShortcut() {

			@Override
			public String getDescription() {
				return dlVideoExternalShortcutMetadataHelper.getFieldValue(
					DLVideoConstants.DDM_FIELD_NAME_DESCRIPTION);
			}

			@Override
			public String getThumbnailURL() {
				return dlVideoExternalShortcutMetadataHelper.getFieldValue(
					DLVideoConstants.DDM_FIELD_NAME_THUMBNAIL_URL);
			}

			@Override
			public String getTitle() {
				return dlVideoExternalShortcutMetadataHelper.getFieldValue(
					DLVideoConstants.DDM_FIELD_NAME_TITLE);
			}

			@Override
			public String getURL() {
				return dlVideoExternalShortcutMetadataHelper.getFieldValue(
					DLVideoConstants.DDM_FIELD_NAME_URL);
			}

			@Override
			public String renderHTML(HttpServletRequest httpServletRequest) {
				return dlVideoExternalShortcutMetadataHelper.getFieldValue(
					DLVideoConstants.DDM_FIELD_NAME_HTML);
			}

		};
	}

	@Reference
	private DLVideoExternalShortcutMetadataHelperFactory
		_dlVideoExternalShortcutMetadataHelperFactory;

	private ServiceTrackerList<DLVideoExternalShortcutProvider>
		_dlVideoExternalShortcutProviders;

}