/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Jorge Díaz
 * @author Michael Bowerman
 */
@ExtendedObjectClassDefinition(
	category = "infrastructure",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	description = "export-import-service-configuration-description",
	id = "com.liferay.exportimport.configuration.ExportImportServiceConfiguration",
	localization = "content/Language",
	name = "export-import-service-configuration-name"
)
@ProviderType
public interface ExportImportServiceConfiguration {

	@Meta.AD(
		deflt = "false", description = "include-all-asset-links-help",
		name = "include-all-asset-links", required = false
	)
	public boolean includeAllAssetLinks();

	@Meta.AD(
		deflt = "true",
		description = "if-checked,-then-the-generated-previews-and-thumbnails-will-be-included-during-the-staging-process",
		name = "include-thumbnails-and-previews-during-staging",
		required = false
	)
	public boolean includeThumbnailsAndPreviewsDuringStaging();

	@Meta.AD(
		deflt = "false", description = "publish-permissions-by-default-help",
		name = "publish-permissions-by-default", required = false
	)
	public boolean publishPermissionsByDefault();

	@Meta.AD(
		deflt = "false",
		description = "replicate-individual-deletions-by-default-help",
		name = "replicate-individual-deletions-by-default", required = false
	)
	public boolean replicateIndividualDeletionsByDefault();

	@Meta.AD(
		deflt = "false",
		description = "if-checked-then-the-advanced-publication-configuration-screen-will-be-displayed-by-default-when-publishing-pages",
		name = "show-advanced-staging-configuration-by-default",
		required = false
	)
	public boolean showAdvancedStagingConfigurationByDefault();

	@Meta.AD(
		deflt = "true", description = "validate-file-entry-references-help",
		name = "validate-file-entry-references", required = false
	)
	public boolean validateFileEntryReferences();

	@Meta.AD(
		deflt = "true", description = "validate-journal-feed-references-help",
		name = "validate-journal-feed-references", required = false
	)
	public boolean validateJournalFeedReferences();

	@Meta.AD(
		deflt = "true", description = "validate-layout-references-help",
		name = "validate-layout-references", required = false
	)
	public boolean validateLayoutReferences();

	@Meta.AD(
		description = "validate-layout-references-whitelisted-url-pattern-help",
		name = "validate-layout-references-whitelisted-url-pattern",
		required = false
	)
	public String[] validateLayoutReferencesWhitelistedURLPatterns();

	@Meta.AD(
		deflt = "true", description = "validate-missing-references-help",
		name = "validate-missing-references", required = false
	)
	public boolean validateMissingReferences();

}