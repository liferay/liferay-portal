/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Lino Alves
 */
@ExtendedObjectClassDefinition(
	category = "forms", scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.dynamic.data.mapping.form.web.internal.configuration.DDMFormWebConfiguration",
	localization = "content/Language", name = "ddm-form-web-configuration-name"
)
public interface DDMFormWebConfiguration {

	@Meta.AD(
		deflt = "1", description = "autosave-interval-description",
		name = "autosave-interval-name", required = false
	)
	public int autosaveInterval();

	@Meta.AD(
		deflt = "enabled-with-warning", name = "csv-export",
		optionLabels = {"enabled", "enabled-with-warning", "disabled"},
		optionValues = {"enabled", "enabled-with-warning", "disabled"},
		required = false
	)
	public String csvExport();

	@Meta.AD(
		deflt = "doc, docx, jpeg, jpg, pdf, png, ppt, pptx, tiff, txt, xls, xlsx",
		description = "guest-upload-file-extensions-help",
		name = "guest-upload-file-extensions", required = false
	)
	public String guestUploadFileExtensions();

	@Meta.AD(
		deflt = "25", description = "guest-upload-maximum-file-size-help",
		name = "guest-upload-maximum-file-size", required = false
	)
	public long guestUploadMaximumFileSize();

	@Meta.AD(
		deflt = "5", description = "guest-upload-maximum-submissions-help",
		name = "guest-upload-maximum-submissions", required = false
	)
	public int guestUploadMaximumSubmissions();

	@Meta.AD(
		deflt = "5", description = "maximum-repetitions-for-upload-fields-help",
		name = "maximum-repetitions-for-upload-fields", required = false
	)
	public int maximumRepetitionsForUploadFields();

	@Meta.AD(
		deflt = "false", description = "propagate-language-selection-help",
		name = "propagate-language-selection", required = false
	)
	public boolean propagateLanguageSelection();

}