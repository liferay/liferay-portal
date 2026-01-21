/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Adolfo Pérez
 */
@ExtendedObjectClassDefinition(
	category = "documents-and-media", generateUI = false,
	scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.document.library.internal.configuration.DLSizeLimitConfiguration",
	localization = "content/Language", name = "dl-size-limit-configuration-name"
)
public interface DLSizeLimitConfiguration {

	@Meta.AD(
		deflt = "0", description = "file-max-size-help",
		name = "maximum-file-size", required = false
	)
	public long fileMaxSize();

	@Meta.AD(deflt = "52428800", name = "max-size-to-copy", required = false)
	public long maxSizeToCopy();

	@Meta.AD(deflt = "", name = "mime-type-size-limit-name", required = false)
	public String[] mimeTypeSizeLimit();

}