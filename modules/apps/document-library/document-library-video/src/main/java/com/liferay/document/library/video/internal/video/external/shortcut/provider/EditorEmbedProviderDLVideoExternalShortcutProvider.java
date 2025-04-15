/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.video.external.shortcut.provider;

import com.liferay.document.library.video.external.shortcut.DLVideoExternalShortcut;
import com.liferay.document.library.video.external.shortcut.provider.DLVideoExternalShortcutProvider;
import com.liferay.frontend.editor.embed.EditorEmbedProvider;
import com.liferay.frontend.editor.embed.constants.EditorEmbedProviderTypeConstants;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Alejandro Tardín
 */
@Component(service = DLVideoExternalShortcutProvider.class)
public class EditorEmbedProviderDLVideoExternalShortcutProvider
	implements DLVideoExternalShortcutProvider {

	@Override
	public DLVideoExternalShortcut getDLVideoExternalShortcut(String url) {
		for (EditorEmbedProvider videoEditorEmbedProvider :
				_videoEditorEmbedProviders) {

			for (String urlScheme : videoEditorEmbedProvider.getURLSchemes()) {
				Pattern pattern = Pattern.compile(urlScheme);

				Matcher matcher = pattern.matcher(url);

				if (matcher.matches()) {
					return new DLVideoExternalShortcut() {

						@Override
						public String getURL() {
							return url;
						}

						@Override
						public String renderHTML(
							HttpServletRequest httpServletRequest) {

							return StringUtil.replace(
								videoEditorEmbedProvider.getTpl(), "{embedId}",
								matcher.group(1));
						}

					};
				}
			}
		}

		return null;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_videoEditorEmbedProviders = ServiceTrackerListFactory.open(
			bundleContext, EditorEmbedProvider.class,
			"(type=" + EditorEmbedProviderTypeConstants.VIDEO + ")");
	}

	@Deactivate
	protected void deactivate() {
		_videoEditorEmbedProviders.close();
	}

	private ServiceTrackerList<EditorEmbedProvider> _videoEditorEmbedProviders;

}