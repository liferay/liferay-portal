/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// AC Version

export const ANALYTICS_CLIENT_VERSION = '1.3.0';

// Application ID

export const BLOG = 'Blog';
export const CUSTOM = 'Custom';
export const CUSTOM_EVENT = 'CustomEvent';
export const DOCUMENT = 'Document';
export const FORM = 'Form';
export const PAGE = 'Page';
export const WEB_CONTENT = 'WebContent';

export const DXP_APPLICATION_IDS = [
	BLOG,
	CUSTOM,
	DOCUMENT,
	FORM,
	PAGE,
	WEB_CONTENT,
];

// Default Config

export const DEBOUNCE = 1500;

export const FLUSH_INTERVAL = 2000;

// Custom Headers

export const HEADER_PROJECT_ID = 'OSB-Asah-Project-ID';

// Limit of a queue localStorage size in kilobytes.

export const QUEUE_STORAGE_LIMIT = 512;

// Queue priority

export const QUEUE_PRIORITY_DEFAULT = 1;

export const QUEUE_PRIORITY_IDENTITY = 10;

// Local Storage keys

export const STORAGE_KEY_CHANNEL_ID = 'ac_client_channel_id';

export const STORAGE_KEY_CONTEXTS = 'ac_client_context';

export const STORAGE_KEY_EVENTS = 'ac_client_batch';

export const STORAGE_KEY_IDENTITY = 'ac_client_identity';

export const STORAGE_KEY_MESSAGES = 'ac_message_queue';

export const STORAGE_KEY_MESSAGE_IDENTITY = 'ac_message_queue_identity';

export const STORAGE_KEY_PREV_EMAIL_ADDRESS_HASHED =
	'ac_client_previous_email_address_hash';

export const STORAGE_KEY_USER_ID = 'ac_client_user_id';

export const STORAGE_KEY_STORAGE_VERSION = 'ac_client_storage_version';

// Request Constants

export const LIMIT_FAILED_ATTEMPTS = 7;

export const REQUEST_TIMEOUT = 5000;

// DXP Timing

export const MARK_LOAD_EVENT_START = 'loadEventStartSPA';

export const MARK_NAVIGATION_START = 'navigationStartSPA';

export const MARK_PAGE_LOAD_TIME = 'pageLoadTimeSPA';

export const MARK_VIEW_DURATION = 'viewDurationSPA';

// Params Constants

export const PARAM_PORTLET_ID_KEY = 'p_p_id';

export const PARAM_CONFIGURATION_PORTLET_NAME =
	'com_liferay_portlet_configuration_web_portlet_PortletConfigurationPortlet';

export const PARAM_MODE_KEY = 'p_l_mode';

export const PARAM_PAGE_EDITOR_PORTLET_NAME =
	'com_liferay_layout_content_page_editor_web_internal_portlet_ContentPageEditorPortlet';

export const PARAM_VIEW_MODE = 'view';

// Read metrics Constants

export const READ_CHARS_PER_MIN = 500;

export const READ_LOGOGRAPHIC_LANGUAGES = new Set(['ja', 'ko', 'zh']);

export const READ_MINIMUM_SCROLL_DEPTH = 75;

export const READ_TIME_FACTOR = 0.75;

export const READ_WORDS_PER_MIN = 265;

// Track method

export const TRACK_DEFAULT_OPTIONS = {
	applicationId: CUSTOM_EVENT,
};

// Validation

export const VALIDATION_CONTEXT_VALUE_MAXIMUM_LENGTH = 2048;

export const VALIDATION_PROPERTIES_MAXIMUM_LENGTH = 25;

export const VALIDATION_PROPERTY_NAME_MAXIMUM_LENGTH = 255;

export const VALIDATION_PROPERTY_VALUE_MAXIMUM_LENGTH = 1024;
