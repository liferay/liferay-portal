/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Pavel Savinov
 */
@ExtendedObjectClassDefinition(category = "assets")
@Meta.OCD(
	id = "com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration",
	localization = "content/Language",
	name = "asset-publisher-web-configuration-name"
)
public interface AssetPublisherWebConfiguration {

	/**
	 * Set the cron expression to schedule when to check for new assets.
	 * Users will be notified via email of new assets. If it is empty or
	 * invalid, {@link #checkInterval()} is used instead.
	 *
	 * @return cron expression to schedule when to check for new assets.
	 */
	@Meta.AD(
		deflt = "", description = "check-cron-expression-key-description",
		name = "check-cron-expression", required = false
	)
	public String checkCronExpression();

	/**
	 * Set the interval in hours on how often to check for new assets.
	 * Users will be notified via email of new assets. This field is ignored if
	 * {@link #checkCronExpression()} is set to a valid value.
	 *
	 * @return interval in hours on how often to check for new assets.
	 */
	@Meta.AD(
		deflt = "24", description = "check-interval-key-description",
		name = "check-interval", required = false
	)
	public int checkInterval();

	/**
	 * Set this to <code>true</code> to enable exporting contents related to
	 * asset entries for dynamic selection.
	 *
	 * @return <code>true</code> if dynamic export is enabled.
	 */
	@Meta.AD(
		deflt = "false", description = "dynamic-export-enabled-key-description",
		name = "dynamic-export-enabled", required = false
	)
	public boolean dynamicExportEnabled();

	/**
	 * Set the maximum number of entries to export for dynamic selection. Set
	 * this property to 0 if there is no limit.
	 *
	 * @return maximum number of entries to export for dynamic selection.
	 */
	@Meta.AD(
		deflt = "20", description = "dynamic-export-limit-key-description",
		name = "dynamic-export-limit", required = false
	)
	public int dynamicExportLimit();

	/**
	 * Set the maximum number of new entries to notify subscribers of Asset
	 * Publisher portlets. Set this property to 0 if there is no limit.
	 *
	 * @return maximum number of new entries to notify subscribers.
	 */
	@Meta.AD(
		deflt = "20",
		description = "dynamic-subscription-limit-key-description",
		name = "dynamic-subscription-limit", required = false
	)
	public int dynamicSubscriptionLimit();

	/**
	 * Set this to <code>true</code> to disable autoscroll when opening
	 * an asset.
	 *
	 * @return <code>true</code> autoScroll is enabled.
	 */
	@Meta.AD(
		deflt = "true", description = "enable-asset-auto-scroll-description",
		name = "enable-asset-auto-scroll", required = false
	)
	public boolean enableAutoscroll();

	/**
	 * Set this to <code>true</code> to enable exporting contents related to
	 * asset entries for manual selection.
	 *
	 * @return <code>true</code> if manual export is enabled.
	 */
	@Meta.AD(
		deflt = "false", description = "manual-export-enabled-key-description",
		name = "manual-export-enabled", required = false
	)
	public boolean manualExportEnabled();

	/**
	 * Set this to <code>true</code> to allow users to configure Asset
	 * Publisher, Most Viewed Assets, and Highest Rated Assets to skip the
	 * permissions checking on the displayed assets. Enabling this property will
	 * allow regular users to view assets that they do not have permission to
	 * view.
	 *
	 * @return <code>true</code> if permission checking is configurable.
	 */
	@Meta.AD(
		deflt = "false",
		description = "permission-checking-configurable-key-description",
		name = "permission-checking-configurable", required = false
	)
	public boolean permissionCheckingConfigurable();

	/**
	 * Set this to <code>true</code> to search assets in Asset Publisher from
	 * the index. Set this to <code>false</code> to search assets in Asset
	 * Publisher from the database.
	 *
	 * @return <code>true</code> search with index is enabled.
	 */
	@Meta.AD(
		deflt = "true", description = "search-with-index-key-description",
		name = "search-with-index", required = false
	)
	public boolean searchWithIndex();

}