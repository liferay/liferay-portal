/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Sergio González
 */
@ExtendedObjectClassDefinition(category = "documents-and-media")
@Meta.OCD(
	id = "com.liferay.document.library.configuration.DLConfiguration",
	localization = "content/Language", name = "dl-configuration-name"
)
@ProviderType
public interface DLConfiguration {

	@Meta.AD(deflt = "true", name = "add-default-structures", required = false)
	public boolean addDefaultStructures();

	@Meta.AD(
		deflt = "15", description = "check-interval-in-minutes-description",
		min = "1", name = "check-interval", required = false
	)
	public int checkInterval();

	@Meta.AD(
		deflt = "application/javascript|text/asp|text/css|text/ecmascript|text/html|text/javascript|text/x-c|text/x-fortran|text/x-java-source|text/x-jsp|text/x-pascal|text/x-script.perl|text/x-script.perl-module|text/xml",
		name = "code-file-mime-types", required = false
	)
	public String[] codeFileMimeTypes();

	@Meta.AD(
		deflt = "application/x-7z-compressed|application/x-ace-compressed|application/x-compressed|application/x-rar-compressed|application/x-zip-compressed|application/zip",
		name = "compressed-file-mime-types", required = false
	)
	public String[] compressedFileMimeTypes();

	/**
	 * Set the location of the XML file containing the configuration of the
	 * default display templates for the Document Library portlet.
	 */
	@Meta.AD(
		deflt = "com/liferay/document/library/web/portlet/display/template/dependencies/portlet-display-templates.xml",
		name = "display-templates-config", required = false
	)
	public String displayTemplatesConfig();

	@Meta.AD(
		deflt = "false", description = "file-entry-expire-all-versions",
		name = "expire-all-file-entry-versions-enabled", required = false
	)
	public boolean expireAllVersionsEnabled();

	@Meta.AD(
		deflt = "*", description = "file-extensions-help",
		name = "file-extensions", required = false
	)
	public String[] fileExtensions();

	@Meta.AD(
		deflt = "0", description = "maximum-number-of-versions-help",
		name = "maximum-number-of-versions", required = false
	)
	public int maximumNumberOfVersions();

	@Meta.AD(
		deflt = "audio|image|video", name = "multimedia-file-mime-types",
		required = false
	)
	public String[] multimediaFileMimeTypes();

	@Meta.AD(
		deflt = "application/mspowerpoint|application/powerpoint|application/vnd.apple.keynote|application/vnd.ms-powerpoint|application/vnd.oasis.opendocument.presentation|application/vnd.openxmlformats-officedocument.presentationml.presentation|application/x-mspowerpoint",
		name = "presentation-file-mime-types", required = false
	)
	public String[] presentationFileMimeTypes();

	@Meta.AD(
		deflt = "application/excel|application/vnd.ms-excel|application/vnd.oasis.opendocument.spreadsheet|application/vnd.openxmlformats-officedocument.spreadsheetml.sheet|application/vnd.sun.xml.calc|application/x-excel|application/x-msexcel",
		name = "spread-sheet-file-mime-types", required = false
	)
	public String[] spreadSheetFileMimeTypes();

	/**
	 * Set the interval, greater than 0, in hours on how often
	 * TempFileEntriesSchedulerJobConfiguration will run to check for expired
	 * temporary file entries.
	 */
	@Meta.AD(
		deflt = "1",
		description = "temporary-file-entries-check-interval-description",
		min = "1", name = "temporary-file-entries-check-interval",
		required = false
	)
	public int temporaryFileEntriesCheckInterval();

	@Meta.AD(
		deflt = "application/msword|application/vnd.oasis.opendocument.text|application/vnd.openxmlformats-officedocument.wordprocessingml.document|text/plain",
		name = "text-file-mime-types", required = false
	)
	public String[] textFileMimeTypes();

	@Meta.AD(
		deflt = "application/pdf", name = "vectorial-file-mime-types",
		required = false
	)
	public String[] vectorialFileMimeTypes();

	@Meta.AD(
		deflt = "true", description = "versioning-strategy-overridable-help",
		name = "versioning-strategy-overridable", required = false
	)
	public boolean versioningStrategyOverridable();

}