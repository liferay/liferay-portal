/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Drew Brokke
 */
@ExtendedObjectClassDefinition(
	category = "documents-and-media", generateUI = false,
	scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.document.library.configuration.DLFileEntryConfiguration",
	localization = "content/Language", name = "dl-file-entry-configuration-name"
)
public interface DLFileEntryConfiguration {

	@Meta.AD(deflt = "3", name = "maximum-number-of-pages", required = false)
	public int maxNumberOfPages();

	@Meta.AD(deflt = "20971520", name = "maximum-file-size", required = false)
	public long previewableProcessorMaxSize();

}