/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

/**
 * @author Shinn Lok
 */
public interface Rels {

	public static final String ACCOUNT = "account";

	public static final String ACCOUNT_DETAILS = "account-details";

	public static final String ACCOUNT_INDIVIDUALS = "account-individuals";

	public static final String ACCOUNT_LIFECYCLE = "account-lifecycle";

	public static final String ACCOUNT_LIFECYCLE_ACCOUNTS =
		"account-lifecycle-accounts";

	public static final String ACCOUNT_LIFECYCLE_OVERVIEW =
		"account-lifecycle-overview";

	public static final String ACCOUNT_LIFECYCLE_STAGE_RULES =
		"account-lifecycle-stage-rules";

	public static final String ACCOUNT_LIFECYCLE_STAGES =
		"account-lifecycle-stages";

	public static final String ACCOUNT_LIFECYCLE_STATUS =
		"account-lifecycle-status";

	public static final String ACCOUNT_LIFECYCLES = "account-lifecycles";

	public static final String ACCOUNTS = "accounts";

	public static final String ACCOUNTS_DISTRIBUTION = "accounts-distribution";

	public static final String ACCOUNTS_INDIVIDUAL_SEGMENTS =
		"accounts-individual-segments";

	public static final String ACCOUNTS_METRICS = "accounts-metrics";

	public static final String ACTIVITIES = "activities";

	public static final String ACTIVITY = "activity";

	public static final String ACTIVITY_ASSETS = "activity-assets";

	public static final String ACTIVITY_GROUP = "activity-group";

	public static final String ACTIVITY_GROUPS = "activity-groups";

	public static final String ACTIVITY_TRANSFORMATIONS =
		"activity-transformations";

	public static final String ADMIN_DATA = "admin-data";

	public static final String ADMIN_NANITE = "admin-nanite";

	public static final String ADMIN_NANITES = "admin-nanites";

	public static final String ASSET = "asset";

	public static final String ASSET_SUMMARIES = "asset-summaries";

	public static final String ASSET_SUMMARY_CATEGORIES =
		"asset-summary-categories";

	public static final String ASSET_SUMMARY_MIME_TYPES =
		"asset-summary-mime-types";

	public static final String ASSET_SUMMARY_TAGS = "asset-summary-tags";

	public static final String ASSET_SUMMARY_TYPES = "asset-summary-types";

	public static final String ASSET_SUMMARY_VOCABULARIES =
		"asset-summary-vocabularies";

	public static final String ASSETS = "assets";

	public static final String BLOCKED_KEYWORD = "blocked-keyword";

	public static final String BLOCKED_KEYWORDS = "blocked-keywords";

	public static final String BULK = "bulk";

	public static final String CHANNEL = "channel";

	public static final String CHANNEL_CLEAR = "channel-clear";

	public static final String CHANNEL_DATA_SOURCES = "channel-data-sources";

	public static final String CHANNELS = "channels";

	public static final String CSV_INDIVIDUALS = "csv-individuals";

	public static final String DATA_SOURCE = "data-source";

	public static final String DATA_SOURCE_CHANNEL_CONNECTED =
		"data-sources-connected";

	public static final String DATA_SOURCE_DISCONNECT =
		"data-source-disconnect";

	public static final String DATA_SOURCE_DISCONNECT_ALL =
		"data-source-disconnect-all";

	public static final String DATA_SOURCE_DXP_GROUPS =
		"data-source-dxp-groups";

	public static final String DATA_SOURCE_DXP_ORGANIZATIONS =
		"data-source-dxp-organizations";

	public static final String DATA_SOURCE_DXP_USER_GROUPS =
		"data-source-dxp-user-groups";

	public static final String DATA_SOURCE_DXP_USERS_FIELDS =
		"data-source-dxp-users-fields";

	public static final String DATA_SOURCE_DXP_USERS_TOTAL =
		"data-source-dxp-users-total";

	public static final String DATA_SOURCE_METRICS_ACCOUNTS_COUNT =
		"data-source-metrics-accounts-count";

	public static final String DATA_SOURCE_METRICS_EVENTS_COUNT =
		"data-source-metrics-events-count";

	public static final String DATA_SOURCE_METRICS_USERS_COUNT =
		"data-source-metrics-users-count";

	public static final String DATA_SOURCE_PROGRESS = "data-source-progress";

	public static final String DATA_SOURCE_REFRESH_LIFERAY =
		"data-source-refresh-liferay";

	public static final String DATA_SOURCE_SALESFORCE_ACCOUNTS_FIELDS =
		"data-source-salesforce-accounts-fields";

	public static final String DATA_SOURCE_SALESFORCE_USERS_FIELDS =
		"data-source-salesforce-users-fields";

	public static final String DATA_SOURCES = "data-sources";

	public static final String DEFINITIONS_INDIVIDUAL_ATTRIBUTES =
		"definitions-individual-attributes";

	public static final String DXP_ENTITIES_USERS_COUNT =
		"dxp-entities-users-count";

	public static final String FIELD = "field";

	public static final String FIELD_MAPPING = "field-mapping";

	public static final String FIELD_MAPPINGS = "field-mappings";

	public static final String FIELD_NAMES = "field-names";

	public static final String FIELDS = "fields";

	public static final String IDENTITIES_COUNT = "identities-count";

	public static final String INDIVIDUAL = "individual";

	public static final String INDIVIDUAL_INDIVIDUAL_SEGMENTS =
		"individual-individual-segments";

	public static final String INDIVIDUAL_SEGMENT = "individual-segment";

	public static final String INDIVIDUAL_SEGMENT_ACTIVATION =
		"individual-segment-activation";

	public static final String INDIVIDUAL_SEGMENT_ASSIGN_CHANNEL =
		"individual-segment-assign-channel";

	public static final String INDIVIDUAL_SEGMENT_INDIVIDUALS =
		"individual-segment-individuals";

	public static final String INDIVIDUAL_SEGMENT_MEMBERSHIP =
		"individual-segment-membership";

	public static final String INDIVIDUAL_SEGMENT_MEMBERSHIP_CHANGES =
		"individual-segment-membership-changes";

	public static final String INDIVIDUAL_SEGMENT_MEMBERSHIPS =
		"individual-segment-memberships";

	public static final String INDIVIDUAL_SEGMENT_REAL_TIME_MEMBERSHIP_METRIC =
		"individual-segment-real-time-membership-metric";

	public static final String INDIVIDUAL_SEGMENT_REAL_TIME_MEMBERSHIPS =
		"individual-segment-real-time-memberships";

	public static final String INDIVIDUAL_SEGMENTS = "individual-segments";

	public static final String INDIVIDUAL_TRANSFORMATION =
		"individual-transformation";

	public static final String INDIVIDUAL_TRANSFORMATIONS =
		"individual-transformations";

	public static final String INDIVIDUALS = "individuals";

	public static final String INDIVIDUALS_COUNT = "individuals-count";

	public static final String INDIVIDUALS_CREATED_BETWEEN_COUNT =
		"individuals-created-between-count";

	public static final String INDIVIDUALS_CREATED_SINCE_COUNT =
		"individuals-created-since-count";

	public static final String INDIVIDUALS_DISTRIBUTION =
		"individuals-distribution";

	public static final String INDIVIDUALS_ENRICHED_PROFILES_COUNT =
		"individuals-enriched-profiles-count";

	public static final String INTEREST = "interest";

	public static final String INTEREST_KEYWORDS = "interest-keywords";

	public static final String INTEREST_TRANSFORMATION =
		"interest-transformation";

	public static final String INTEREST_TRANSFORMATIONS =
		"interest-transformations";

	public static final String INTERESTS = "interests";

	public static final String MEMBERSHIP = "membership";

	public static final String MEMBERSHIP_CHANGE = "membership-change";

	public static final String MEMBERSHIP_CHANGES = "membership-changes";

	public static final String MEMBERSHIP_CHANGES_TRANSFORMATION =
		"membership-changes-transformation";

	public static final String MEMBERSHIP_CHANGES_TRANSFORMATIONS =
		"membership-changes-transformations";

	public static final String MEMBERSHIPS = "memberships";

	public static final String ORGANIZATIONS = "organizations";

	public static final String PAGE_EXPERIENCES = "page-experiences";

	public static final String PAGE_VISIT = "page-visited";

	public static final String PAGE_VISITED = "page-visited";

	public static final String PAGES_VISITED = "pages-visited";

	public static final String PREVIEW_DISABLED_SEGMENTS =
		"preview-disabled-segments";

	public static final String PROJECT_USAGE_METRICS = "project-usage-metrics";

	public static final String PROJECTS_LAST_SEEN_DATE =
		"projects-last-seen-date";

	public static final String REPORTS_EXPORT_CSV_COUNT =
		"reports-export-csv-count";

	public static final String SESSION_VALUES = "session-values";

	public static final String WORKSPACE = "workspace";

	public interface Interests {

		public static final String PAGES_VISITED = "pages-visited";

	}

}