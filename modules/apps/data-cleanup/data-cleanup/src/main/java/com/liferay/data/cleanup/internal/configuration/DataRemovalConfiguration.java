/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Kevin Lee
 */
@ExtendedObjectClassDefinition(category = "upgrades")
@Meta.OCD(
	id = "com.liferay.data.cleanup.internal.configuration.DataRemovalConfiguration",
	name = "data-removal-configuration-name"
)
public interface DataRemovalConfiguration {

	@Meta.AD(deflt = "false", name = "fix-counter-values", required = false)
	public boolean fixCounterValues();

	@Meta.AD(
		deflt = "false", name = "remove-analytics-message-data",
		required = false
	)
	public boolean removeAnalyticsMessageData();

	@Meta.AD(
		deflt = "false", name = "remove-company-orphan-data", required = false
	)
	public boolean removeCompanyOrphanData();

	@Meta.AD(
		deflt = "false", name = "remove-configuration-orphan-data",
		required = false
	)
	public boolean removeConfigurationOrphanData();

	@Meta.AD(deflt = "false", name = "remove-ddm-orphan-data", required = false)
	public boolean removeDDMOrphanData();

	@Meta.AD(
		deflt = "false", name = "remove-dl-file-entry-orphan-data",
		required = false
	)
	public boolean removeDLFileEntryOrphanData();

	@Meta.AD(
		deflt = "false", name = "remove-dl-preview-cts-content-data",
		required = false
	)
	public boolean removeDLPreviewCTSContentData();

	@Meta.AD(
		deflt = "false", name = "remove-expired-journal-articles",
		required = false
	)
	public boolean removeExpiredJournalArticles();

	@Meta.AD(
		deflt = "false", name = "remove-group-orphan-data", required = false
	)
	public boolean removeGroupOrphanData();

	@Meta.AD(
		deflt = "false", name = "remove-journal-orphan-data", required = false
	)
	public boolean removeJournalOrphanData();

	@Meta.AD(
		deflt = "false", name = "remove-null-unicode-content-data",
		required = false
	)
	public boolean removeNullUnicodeContentData();

	@Meta.AD(
		deflt = "false", name = "remove-publications-older-than-6-months",
		required = false
	)
	public boolean removeOutdatedPublishedCTCollections();

	@Meta.AD(
		deflt = "false", name = "remove-published-cts-content-data",
		required = false
	)
	public boolean removePublishedCTSContentData();

	@Meta.AD(
		deflt = "false", name = "remove-quartz-job-details-data",
		required = false
	)
	public boolean removeQuartzJobDetailsData();

	@Meta.AD(
		deflt = "false", name = "remove-user-orphan-data", required = false
	)
	public boolean removeUserOrphanData();

	@Meta.AD(
		deflt = "false", name = "remove-widget-layout-type-settings",
		required = false
	)
	public boolean removeWidgetLayoutTypeSettings();

	@Meta.AD(
		deflt = "false", name = "remove-layout-classed-model-usage-orphan-data",
		required = false
	)
	public boolean removeLayoutClassedModelUsageOrphanData();

}