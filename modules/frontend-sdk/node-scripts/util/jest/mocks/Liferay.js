/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-env jest */

import DefaultEventHandler from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/DefaultEventHandler.es';
import DynamicInlineScroll from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/DynamicInlineScroll.es';
import PortletBase from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/PortletBase.es';
import AutoSize from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/autosize/autosize.es';
import BREAKPOINTS from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/breakpoints';
import debounce, {
	cancelDebounce,
} from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/debounce/debounce.es';
import delegate from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/delegate/delegate.es';
import Disposable from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/events/Disposable';
import EventEmitter from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/events/EventEmitter';
import EventHandler from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/events/EventHandler';
import STATUS_CODE from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/status_code';
import throttle from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/throttle.es';
import addParams from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/add_params';
import getCountries from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/address/get_countries.es';
import getRegions from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/address/get_regions.es';
import {
	CONSENT_TYPES,
	checkConsent,
	getCookie,
	removeCookie,
	setCookie,
} from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/cookie/cookie';
import fetch from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/fetch.es';
import focusFormField from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/focus_form_field';
import getFormElement from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/form/get_form_element.es';
import objectToFormData from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/form/object_to_form_data.es';
import postForm from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/form/post_form.es';
import setFormValues from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/form/set_form_values.es';
import formatStorage from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/format_storage';
import {
	getCheckedCheckboxes,
	getUncheckedCheckboxes,
} from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/get_checkboxes';
import getCropRegion from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/get_crop_region.es';
import getGeolocation from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/get_geolocation';
import getLexiconIcon from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/get_lexicon_icon';
import getLexiconIconTpl from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/get_lexicon_icon_template';
import getOpener from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/get_opener';
import getPortletId from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/get_portlet_id';
import getPortletNamespace from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/get_portlet_namespace.es';
import getSelectedOptionValues from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/get_selected_option_values';
import getTop from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/get_top';
import getWindow from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/get_window';
import {
	escapeHTML,
	unescapeHTML,
} from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/html_util';
import inBrowserView from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/in_browser_view';
import isObject from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/is_object';
import isPhone from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/is_phone';
import isTablet from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/is_tablet';
import localStorage from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/local_storage';
import memoize from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/memoize';
import navigate from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/navigate.es';
import normalizeFriendlyURL from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/normalize_friendly_url';
import openWindow from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/open_window';
import createActionURL from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/portlet_url/create_action_url.es';
import createPortletURL from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/portlet_url/create_portlet_url.es';
import createRenderURL from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/portlet_url/create_render_url.es';
import createResourceURL from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/portlet_url/create_resource_url.es';
import removeEntitySelection from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/remove_entity_selection';
import runScriptsInElement from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/run_scripts_in_element.es';
import selectFolder from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/select_folder';
import {
	getSessionValue,
	setSessionValue,
} from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/session.es';
import sessionStorage from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/session_storage';
import showCapsLock from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/show_caps_lock';
import sub from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/sub';
import toggleBoxes from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/toggle_boxes';
import toggleControls from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/toggle_controls';
import toggleDisabled from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/toggle_disabled';
import toggleRadio from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/toggle_radio';
import toggleSelectBox from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/toggle_select_box';
import loadClientExtensions from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/utils/client_extensions/loadClientExtensions';
import loadEditorClientExtensions from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/utils/client_extensions/loadEditorClientExtensions';
import {loadModule} from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/utils/client_extensions/loadModule';
import zIndex from '../../../../../apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/zIndex';

const languageMap = {
	'days-abbreviation': 'd',
	'decimal-delimiter': '.',
	'hours-abbreviation': 'h',
	'minutes-abbreviation': 'min',
	'mmm-dd': 'MMM DD',
	'mmm-dd-hh-mm': 'MMM DD, HH:mm',
	'mmm-dd-hh-mm-a': 'MMM DD, hh:mm A',
	'mmm-dd-lt': 'MMM DD, LT',
	'mmm-dd-yyyy': 'MMM DD, YYYY',
	'mmm-dd-yyyy-lt': 'MMM DD, YYYY, LT',
	'option': 'Option',
	'thousand-abbreviation': 'K',
	'x-of-x-characters': '{0}/{1} characters',
};

const after = jest.fn(() => ({
	detach: () => {},
}));

const authToken = 'default-mocked-auth-token';

const component = jest.fn((componentId) => componentId);

const destroyComponent = jest.fn();

const detach = jest.fn((name, fn) => {
	global.removeEventListener(name, fn);
});

/**
 * Event support APIs on the `Liferay` object inherited from `A.Attributes`
 *
 * https://github.com/liferay/liferay-portal/blob/31073fb75fb0d3b309f9e0f921cb7a469aa2703d/modules/apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/events.js#L66
 * https://yuilibrary.com/yui/docs/api/classes/Attribute.html
 */
const events = {

	/**
	 * https://clarle.github.io/yui3/yui/docs/api/files/event-custom_js_event-target.js.html#l850
	 */
	after: jest.fn(),

	/**
	 * https://yuilibrary.com/yui/docs/api/files/event-custom_js_event-target.js.html#l372
	 */
	detach: jest.fn(),

	/**
	 * https://yuilibrary.com/yui/docs/api/files/event-custom_js_event-target.js.html#l700
	 */
	fire: jest.fn(),

	/**
	 * https://yuilibrary.com/yui/docs/api/files/event-custom_js_event-target.js.html#l227
	 */
	on: jest.fn(),

	/**
	 * https://yuilibrary.com/yui/docs/api/files/event-custom_js_event-target.js.html#l136
	 */
	once: jest.fn(),
};

const namespace = jest.fn((name) => {
	global.Liferay[name] = {};

	return global.Liferay[name];
});

const FeatureFlags = {};

const Icons = {
	spritemap: '/o/icons/pack/clay.svg',
};

/**
 * Contains a fallback/dummy implementation of
 * `Liferay.Language.get`. In practice, this call is rewritten in a
 * ServerFilter, so runtime calls to `Liferay.Language.get` should not
 * be found in production code. A better match for the real behaviour
 * would be a babel plugin to rewrite calls to the API with their
 * "translated" value.
 *
 * https://github.com/liferay/liferay-portal/blob/31073fb75fb0d3b309f9e0f921cb7a469aa2703d/modules/apps/frontend-js/frontend-js-aui-web/src/main/resources/META-INF/resources/liferay/language.js
 */
const Language = {

	/**
	 * https://github.com/liferay/liferay-portal/blob/f5bc2504a4f666241363a30975b6de5d57a6f627/portal-web/docroot/html/common/themes/top_js.jspf#L153
	 */
	available: {
		ar_SA: 'Arabic (Saudi Arabia)',
		ca_ES: 'Catalan (Spain)',
		de_DE: 'German (Germany)',
		en_US: 'English (United States)',
		es_ES: 'Spanish (Spain)',
		fi_FI: 'Finnish (Finland)',
		fr_FR: 'French (France)',
		hu_HU: 'Hungarian (Hungary)',
		ja_JP: 'Japanese (Japan)',
		nl_NL: 'Dutch (Netherlands)',
		pt_BR: 'Portuguese (Brazil)',
		sv_SE: 'Swedish (Sweden)',
		zh_CN: 'Chinese (China)',
	},

	/**
	 * https://github.com/liferay/liferay-portal/blob/31073fb75fb0d3b309f9e0f921cb7a469aa2703d/modules/apps/frontend-js/frontend-js-aui-web/src/main/resources/META-INF/resources/liferay/language.js#L18
	 */
	get: jest.fn((key) => {
		if (languageMap[key]) {
			return languageMap[key];
		}

		return key;
	}),
};

/**
 * https://github.com/liferay/liferay-portal/blob/f5bc2504a4f666241363a30975b6de5d57a6f627/portal-web/docroot/html/common/themes/top_js.jspf#L161
 */
const PortletKeys = {
	DOCUMENT_LIBRARY: 'DOCUMENT_LIBRARY',
	ITEM_SELECTOR: 'ITEM_SELECTOR',
};

/**
 * https://github.com/liferay/liferay-portal/blob/f8ea9617f99238f7f5b6e4824bf71ab2e64fdfdd/portal-web/docroot/html/common/themes/top_js.jspf#L168-L170
 */
const PropsValues = {
	JAVASCRIPT_SINGLE_PAGE_APPLICATION_TIMEOUT: 0,

	NTLM_AUTH_ENABLED: false,

	UPLOAD_SERVLET_REQUEST_IMPL_MAX_SIZE: 104857600,
};

/**
 * https://github.com/liferay/liferay-portal/blob/a4866af62eb89c69ee00d0e69dbe7ff092b50048/modules/apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/global.es.js#L101-L104
 */
const Session = {
	extend: jest.fn(() => {}),

	/**
	 * https://github.com/liferay/liferay-portal/blob/a4866af62eb89c69ee00d0e69dbe7ff092b50048/modules/apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/global.es.js#L102
	 */
	get: jest.fn(() => Promise.resolve({})),

	/**
	 * https://github.com/liferay/liferay-portal/blob/a4866af62eb89c69ee00d0e69dbe7ff092b50048/modules/apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/global.es.js#L103
	 */
	set: jest.fn(() => Promise.resolve({})),
};

/**
 * Contains APIs that provide information about the running context of
 * the portal. The JS ThemeDisplay object is a representation of its
 * Java counterpart simplified for JS access.
 *
 * https://github.com/liferay/liferay-portal/blob/31073fb75fb0d3b309f9e0f921cb7a469aa2703d/portal-web/docroot/html/common/themes/top_js.jspf#L147
 */
const ThemeDisplay = {

	/**
	 * https://github.com/liferay/liferay-portal/blob/a4866af62eb89c69ee00d0e69dbe7ff092b50048/portal-web/docroot/html/common/themes/top_js.jspf#L188
	 */
	getBCP47LanguageId: jest.fn(() => 'en-US'),

	getDefaultLanguageId: jest.fn(() => 'en_US'),

	/**
	 * https://github.com/liferay/liferay-portal/blob/31073fb75fb0d3b309f9e0f921cb7a469aa2703d/portal-web/docroot/html/common/themes/top_js.jspf#L217
	 */
	getDoAsUserIdEncoded: jest.fn(() => ''),

	/**
	 * https://github.com/liferay/liferay-portal/blob/a4866af62eb89c69ee00d0e69dbe7ff092b50048/portal-web/docroot/html/common/themes/top_js.jspf#L220
	 */
	getLanguageId: jest.fn(() => 'en_US'),

	getLayoutRelativeControlPanelURL: jest.fn(
		() => 'layoutRelativeControlPanelURL'
	),

	getLayoutRelativeURL: jest.fn(() => 'getLayoutRelativeURL'),

	/**
	 * https://github.com/liferay/liferay-portal/blob/a4866af62eb89c69ee00d0e69dbe7ff092b50048/portal-web/docroot/html/common/themes/top_js.jspf#L226
	 */
	getPathContext: jest.fn(() => '/'),

	/**
	 * https://github.com/liferay/liferay-portal/blob/31073fb75fb0d3b309f9e0f921cb7a469aa2703d/portal-web/docroot/html/common/themes/top_js.jspf#L235
	 */
	getPathMain: jest.fn(() => '/c'),

	/**
	 * https://github.com/liferay/liferay-portal/blob/31073fb75fb0d3b309f9e0f921cb7a469aa2703d/portal-web/docroot/html/common/themes/top_js.jspf#L238
	 */
	getPathThemeImages: jest.fn(
		() => 'http://localhost:8080/o/admin-theme/images'
	),

	getPlid: jest.fn(() => 'plid'),

	/**
	 * https://github.com/liferay/liferay-portal/blob/31073fb75fb0d3b309f9e0f921cb7a469aa2703d/portal-web/docroot/html/common/themes/top_js.jspf#L247
	 */
	getPortalURL: jest.fn(() => 'http://localhost:8080'),

	getScopeGroupId: jest.fn(() => '20126'),

	getUserId: jest.fn(() => '20164'),

	isImpersonated: jest.fn(() => false),

	isSignedIn: jest.fn(() => true),
};

/**
 * General utilities on the `Liferay` object. Possible API sources are:
 *
 * - https://github.com/liferay/liferay-portal/blob/31073fb75fb0d3b309f9e0f921cb7a469aa2703d/modules/apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/global.es.js
 * - https://github.com/liferay/liferay-portal/blob/31073fb75fb0d3b309f9e0f921cb7a469aa2703d/modules/apps/frontend-js/frontend-js-aui-web/src/main/resources/META-INF/resources/liferay/util.js
 */
const Util = {
	PortletURL: {
		createResourceURL: jest.fn(() => 'http://0.0.0.0/liferay/o'),
	},

	escape: jest.fn((data) => data),

	/**
	 * https://github.com/liferay/liferay-portal/blob/31073fb75fb0d3b309f9e0f921cb7a469aa2703d/modules/apps/frontend-js/frontend-js-aui-web/src/main/resources/META-INF/resources/liferay/util.js#L442
	 */
	getGeolocation: jest.fn(),

	/**
	 * https://github.com/liferay/liferay-portal/blob/a4866af62eb89c69ee00d0e69dbe7ff092b50048/modules/apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/global.es.js#L75
	 */
	isEqual: jest.fn((a, b) => a === b),

	/**
	 * https://github.com/liferay/liferay-portal/blob/31073fb75fb0d3b309f9e0f921cb7a469aa2703d/modules/apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/navigate.es.js
	 */
	navigate: jest.fn(),

	/**
	 * https://github.com/liferay/liferay-portal/blob/31073fb75fb0d3b309f9e0f921cb7a469aa2703d/modules/apps/frontend-js/frontend-js-web/test/liferay/util/ns.es.js
	 */
	ns: jest.fn(() => ({})),

	openToast: jest.fn(() => true),

	selectEntity: jest.fn(() => {}),

	/**
	 * https://github.com/liferay/liferay-portal/blob/31073fb75fb0d3b309f9e0f921cb7a469aa2703d/modules/apps/frontend-js/frontend-js-aui-web/src/main/resources/META-INF/resources/liferay/util.js#L999
	 */
	sub: jest.fn((string, ...args) => {
		const matchX = new RegExp('(^x-)|(-x-)|(-x$)', 'gm');

		if (string.match(matchX) && args) {
			return string.replace('x', args);
		}
		else if (args.length > 1) {
			args.forEach((arg, index) => {
				string = string.replace(`{${index}}`, arg);
			});
		}

		return string;
	}),
};

const CSP = {
	nonce: '',
};

const __INTERNALS = {
	AutoSize,
	BREAKPOINTS,
	CONSENT_TYPES,
	DefaultEventHandler,
	Disposable,
	DynamicInlineScroll,
	EventEmitter,
	EventHandler,
	PortletBase,
	STATUS_CODE,
	addParams,
	cancelDebounce,
	checkConsent,
	createActionURL,
	createPortletURL,
	createRenderURL,
	createResourceURL,
	debounce,
	delegate,
	escapeHTML,
	fetch,
	focusFormField,
	formatStorage,
	getCheckedCheckboxes,
	getCookie,
	getCountries,
	getCropRegion,
	getFormElement,
	getGeolocation,
	getLexiconIcon,
	getLexiconIconTpl,
	getOpener,
	getPortletId,
	getPortletNamespace,
	getRegions,
	getSelectedOptionValues,
	getSessionValue,
	getTop,
	getUncheckedCheckboxes,
	getWindow,
	inBrowserView,
	isObject,
	isPhone,
	isTablet,
	loadClientExtensions,
	loadEditorClientExtensions,
	loadModule,
	localStorage,
	memoize,
	navigate,
	normalizeFriendlyURL,
	objectToFormData,
	openWindow,
	postForm,
	removeCookie,
	removeEntitySelection,
	runScriptsInElement,
	selectFolder,
	sessionStorage,
	setCookie,
	setFormValues,
	setSessionValue,
	showCapsLock,
	sub,
	throttle,
	toggleBoxes,
	toggleControls,
	toggleDisabled,
	toggleRadio,
	toggleSelectBox,
	unescapeHTML,
	zIndex,
};

module.exports = {
	...events,
	__INTERNALS,
	CSP,
	FeatureFlags,
	Icons,
	Language,
	PortletKeys,
	PropsValues,
	Session,
	ThemeDisplay,
	Util,
	after,
	authToken,
	component,
	destroyComponent,
	detach,
	namespace,
};
