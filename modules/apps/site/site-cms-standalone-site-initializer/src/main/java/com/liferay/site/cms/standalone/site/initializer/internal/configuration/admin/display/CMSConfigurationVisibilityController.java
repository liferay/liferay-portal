/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.standalone.site.initializer.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationVisibilityController;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"configuration.pid=com.liferay.document.library.configuration.DLConfiguration",
		"configuration.pid=com.liferay.document.library.configuration.DLFileEntryConfiguration",
		"configuration.pid=com.liferay.document.library.configuration.DLFileEntryFriendlyURLConfiguration",
		"configuration.pid=com.liferay.document.library.configuration.DLFileEntryMimeTypeConfiguration",
		"configuration.pid=com.liferay.document.library.internal.configuration.DLFileOrderConfiguration",
		"configuration.pid=com.liferay.document.library.item.selector.web.internal.configuration.DLImageItemSelectorViewConfiguration",
		"configuration.pid=com.liferay.document.library.preview.audio.internal.configuration.DLAudioFFMPEGAudioConverterConfiguration",
		"configuration.pid=com.liferay.document.library.video.internal.configuration.DLVideoFFMPEGVideoConverterConfiguration"
	},
	service = ConfigurationVisibilityController.class
)
public class CMSConfigurationVisibilityController
	implements ConfigurationVisibilityController {

	@Override
	public boolean isVisible(
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK) {

		return false;
	}

}