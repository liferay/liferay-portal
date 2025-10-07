/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Session} from '../legacy/session';
import DefaultEventHandler from './DefaultEventHandler.es';
import DynamicInlineScroll from './DynamicInlineScroll.es';
import DynamicSelect from './DynamicSelect';
import PortletBase from './PortletBase.es';
import AutoSize from './autosize/autosize.es';
import BREAKPOINTS from './breakpoints';
import {
	component,
	componentReady,
	destroyComponent,
	destroyComponents,
	destroyUnfulfilledPromises,
	getComponentCache,
	initComponentCache,
} from './component.es';
import debounce, {cancelDebounce} from './debounce/debounce.es';
import delegate from './delegate/delegate.es';

import './dom_task_runner';

import './events';
import Disposable from './events/Disposable';
import EventEmitter from './events/EventEmitter';
import EventHandler from './events/EventHandler';
import {
	getLayoutIcons,
	hideLayoutPane,
	proposeLayout,
	publishToLive,
	showLayoutPane,
	toggleLayoutDetails,
} from './layout_exporter.es';

import './lazy_load';

import './liferay';
import {showTab} from './portal/tabs.es';
import {showTooltip} from './portal/tooltip.es';

import './portlet';
import portlet, {minimizePortlet} from './portlet/portlet.es';
import SideNavigation from './side_navigation.es';
import STATUS_CODE from './status_code';
import throttle from './throttle.es';
import addParams from './util/add_params';
import getCountries from './util/address/get_countries.es';
import getRegions from './util/address/get_regions.es';
import Cookie, {
	CONSENT_TYPES,
	checkConsent,
	getCookie,
	removeCookie,
	setCookie,
} from './util/cookie/cookie';
import fetch from './util/fetch.es';
import focusFormField from './util/focus_form_field';
import getFormElement from './util/form/get_form_element.es';
import objectToFormData from './util/form/object_to_form_data.es';
import postForm from './util/form/post_form.es';
import setFormValues from './util/form/set_form_values.es';
import formatStorage from './util/format_storage.es';
import formatXML from './util/format_xml.es';
import {
	getCheckedCheckboxes,
	getUncheckedCheckboxes,
} from './util/get_checkboxes';
import getCropRegion from './util/get_crop_region.es';
import getDOM from './util/get_dom';
import getElement from './util/get_element';
import getGeolocation from './util/get_geolocation';
import getLexiconIcon from './util/get_lexicon_icon';
import getLexiconIconTpl from './util/get_lexicon_icon_template';
import getOpener from './util/get_opener';
import getPortletId from './util/get_portlet_id';
import getPortletNamespace from './util/get_portlet_namespace.es';
import getSelectedOptionValues from './util/get_selected_option_values';
import getTop from './util/get_top';
import getURLWithSessionId from './util/get_url_with_session_id';
import getWindow from './util/get_window';
import {
	MAP_HTML_CHARS_ESCAPED,
	escapeHTML,
	unescapeHTML,
} from './util/html_util';
import inBrowserView from './util/in_browser_view';
import isObject from './util/is_object';
import isPhone from './util/is_phone';
import isTablet from './util/is_tablet';
import localStorage from './util/local_storage';
import memoize from './util/memoize';
import navigate from './util/navigate.es';
import normalizeFriendlyURL from './util/normalize_friendly_url';
import ns from './util/ns.es';
import objectToURLSearchParams from './util/object_to_url_search_params.es';
import openWindow, {openInDialog} from './util/open_window';
import {
	getPortletConfigurationIconAction,
	setPortletConfigurationIconAction,
} from './util/portlet_configuration_icon_action';
import createActionURL from './util/portlet_url/create_action_url.es';
import createPortletURL from './util/portlet_url/create_portlet_url.es';
import createRenderURL from './util/portlet_url/create_render_url.es';
import createResourceURL from './util/portlet_url/create_resource_url.es';
import removeEntitySelection from './util/remove_entity_selection';
import runScriptsInElement from './util/run_scripts_in_element.es';
import selectFolder from './util/select_folder';
import {getSessionValue, setSessionValue} from './util/session.es';
import sessionStorage from './util/session_storage';
import showCapsLock from './util/show_caps_lock';
import sub from './util/sub';
import toCharCode from './util/to_char_code.es';
import toggleBoxes from './util/toggle_boxes';
import toggleControls from './util/toggle_controls';
import toggleDisabled from './util/toggle_disabled';
import toggleRadio from './util/toggle_radio';
import toggleSelectBox from './util/toggle_select_box';
import {loadClientExtensions} from './utils/client_extensions/loadClientExtensions';
import loadEditorClientExtensions from './utils/client_extensions/loadEditorClientExtensions';
import {loadModule} from './utils/client_extensions/loadModule';

import './workflow';
import zIndex from './zIndex';

/**
 * @deprecated As of Athanasius (7.3.x), replaced by `import {BREAKPOINTS} from 'frontend-js-web'`
 */
Liferay.BREAKPOINTS = BREAKPOINTS;

/**
 * @deprecated As of Cavanaugh (7.4.x), replaced by `import {STATUS_CODE} from 'frontend-js-web'`
 */
Liferay.STATUS_CODE = STATUS_CODE;

/**
 * @deprecated As of Cavanaugh (7.4.x), replaced by `import {zIndex} from 'frontend-js-web'`
 */
Liferay.zIndex = zIndex;

Liferay.component = component;
Liferay.componentReady = componentReady;
Liferay.destroyComponent = destroyComponent;
Liferay.destroyComponents = destroyComponents;
Liferay.destroyUnfulfilledPromises = destroyUnfulfilledPromises;
Liferay.getComponentCache = getComponentCache;
Liferay.initComponentCache = initComponentCache;

Liferay.Address = {
	getCountries,
	getRegions,
};

/**
 * @deprecated As of Athanasius (7.3.x), with no direct replacement
 */
Liferay.DynamicSelect = DynamicSelect;

Liferay.LayoutExporter = {
	all: hideLayoutPane,
	details: toggleLayoutDetails,
	icons: getLayoutIcons(),
	proposeLayout,
	publishToLive,
	selected: showLayoutPane,
};

Liferay.Portal = {
	Tabs: {
		show: showTab,
	},
	ToolTip: {
		show: showTooltip,
	},
};

Liferay.Portlet = Liferay.Portlet || {};

Liferay.Portlet.minimize = minimizePortlet;

Liferay.SideNavigation = SideNavigation;

Liferay.Util = Liferay.Util || {};

Liferay.Util.MAP_HTML_CHARS_ESCAPED = MAP_HTML_CHARS_ESCAPED;

/**
 * @deprecated As of Athanasius (7.3.x), replaced by `import {addParams} from 'frontend-js-web'`
 */
Liferay.Util.addParams = addParams;

/**
 * Utils added to global namespace to be consumed by portal-web
 */
Liferay.Util.AutoSize = AutoSize;
Liferay.Util.debounce = debounce;
Liferay.Util.delegate = delegate;
Liferay.Util.DynamicInlineScroll = DynamicInlineScroll;
Liferay.Util.checkAll = (...args) => {
	import('frontend-js-web/legacy')
		.then(({checkAll}) => {
			checkAll(...args);
		})
		.catch(() => {});
};
Liferay.Util.checkAllBox = (...args) => {
	import('frontend-js-web/legacy')
		.then(({checkAllBox}) => {
			checkAllBox(...args);
		})
		.catch(() => {});
};
Liferay.Util.runScriptsInElement = runScriptsInElement;

/**
 * @deprecated As of Athanasius (7.3.x), with no direct replacement
 */
Liferay.Util.disableEsc = () => {
	if (document.all && window.event.keyCode === 27) {
		window.event.returnValue = false;
	}
};

const htmlEscapes = {
	'"': '&quot;',
	'&': '&amp;',
	"'": '&#39;',
	'<': '&lt;',
	'>': '&gt;',
};

const reUnescapedHtml = /[&<>"']/g;
const reHasUnescapedHtml = RegExp(reUnescapedHtml.source);

Liferay.Util.escape = (string) => {
	return string && reHasUnescapedHtml.test(string)
		? string.replace(reUnescapedHtml, (chr) => htmlEscapes[chr])
		: string || '';
};
Liferay.Util.escapeHTML = escapeHTML;
Liferay.Util.fetch = fetch;

/**
 * @deprecated As of Athanasius (7.3.x), replaced by `import {focusFormField} from 'frontend-js-web'`
 */
Liferay.Util.focusFormField = focusFormField;

Liferay.Util.formatStorage = formatStorage;
Liferay.Util.formatXML = formatXML;
Liferay.Util.getCheckedCheckboxes = getCheckedCheckboxes;
Liferay.Util.getUncheckedCheckboxes = getUncheckedCheckboxes;
Liferay.Util.getCropRegion = getCropRegion;

/**
 * @deprecated As of Athanasius (7.3.x), with no direct replacement
 */
Liferay.Util.getDOM = getDOM;

/**
 * @deprecated As of Athanasius (7.3.x), with no direct replacement
 */
Liferay.Util.getElement = getElement;

Liferay.Util.getGeolocation = getGeolocation;
Liferay.Util.getFormElement = getFormElement;
Liferay.Util.getLexiconIcon = getLexiconIcon;
Liferay.Util.getLexiconIconTpl = getLexiconIconTpl;
Liferay.Util.getOpener = getOpener;
Liferay.Util.getPortletConfigurationIconAction =
	getPortletConfigurationIconAction;

/**
 * @deprecated As of Athanasius (7.3.x), replaced by `import {getPortletId} from 'frontend-js-web'`
 */
Liferay.Util.getPortletId = getPortletId;

Liferay.Util.getPortletNamespace = getPortletNamespace;
Liferay.Util.getSelectedOptionValues = getSelectedOptionValues;
Liferay.Util.getTop = getTop;
Liferay.Util.getURLWithSessionId = getURLWithSessionId;
Liferay.Util.getWindow = getWindow;

/**
 * @deprecated As of Athanasius (7.3.x), replaced by `import {inBrowserView} from 'frontend-js-web'`
 */
Liferay.Util.inBrowserView = inBrowserView;

/**
 * @deprecated As of Athanasius (7.3.x), replaced by `import {isPhone} from 'frontend-js-web'`
 */
Liferay.Util.isPhone = isPhone;

/**
 * @deprecated As of Athanasius (7.3.x), replaced by `import {isTablet} from 'frontend-js-web'`
 */
Liferay.Util.isTablet = isTablet;

Liferay.Util.loadClientExtensions = loadClientExtensions;
Liferay.Util.loadEditorClientExtensions = loadEditorClientExtensions;
Liferay.Util.navigate = navigate;
Liferay.Util.ns = ns;
Liferay.Util.objectToFormData = objectToFormData;
Liferay.Util.objectToURLSearchParams = objectToURLSearchParams;

/**
 * @deprecated As of Athanasius (7.3.x), replaced by `import {normalizeFriendlyURL} from 'frontend-js-web'`
 */
Liferay.Util.normalizeFriendlyURL = normalizeFriendlyURL;

Liferay.Util.PortletURL = {
	createActionURL,
	createPortletURL,
	createRenderURL,
	createResourceURL,
};

Liferay.Util.postForm = postForm;
Liferay.Util.setFormValues = setFormValues;
Liferay.Util.toCharCode = toCharCode;

/**
 * @deprecated As of Athanasius (7.3.x), replaced by `import {toggleDisabled} from 'frontend-js-web'`
 */
Liferay.Util.toggleDisabled = toggleDisabled;

Liferay.Util.openWindow = openWindow;
Liferay.Util.openInDialog = openInDialog;
Liferay.Util.removeEntitySelection = removeEntitySelection;
Liferay.Util.selectFolder = selectFolder;
Liferay.Util.setPortletConfigurationIconAction =
	setPortletConfigurationIconAction;
Liferay.Util.showCapsLock = showCapsLock;
Liferay.Util.sub = sub;

Liferay.Util.Session = {
	get: getSessionValue,
	set: setSessionValue,
};

Liferay.Util.toggleBoxes = toggleBoxes;
Liferay.Util.toggleControls = toggleControls;
Liferay.Util.toggleRadio = toggleRadio;
Liferay.Util.toggleSelectBox = toggleSelectBox;

const htmlUnescapes = {
	'&#39;': "'",
	'&amp;': '&',
	'&gt;': '>',
	'&lt;': '<',
	'&quot;': '"',
};

const reEscapedHtml = /&(?:amp|lt|gt|quot|#(0+)?39);/g;
const reHasEscapedHtml = RegExp(reEscapedHtml.source);

Liferay.Util.unescape = (string) => {
	return string && reHasEscapedHtml.test(string)
		? string.replace(
				reEscapedHtml,
				(entity) => htmlUnescapes[entity] || "'"
			)
		: string || '';
};

Liferay.Util.unescapeHTML = unescapeHTML;

Liferay.Util.checkAll = (...args) => {
	import(
		themeDisplay.getPathContext() +
			'/o/frontend-js-web/__liferay__/legacy.js'
	).then(({checkAll}) => {
		checkAll(...args);
	});
};
Liferay.Util.checkAllBox = (...args) => {
	import(
		themeDisplay.getPathContext() +
			'/o/frontend-js-web/__liferay__/legacy.js'
	).then(({checkAllBox}) => {
		checkAllBox(...args);
	});
};
Liferay.Util.Cookie = Cookie;

Liferay.Util.LocalStorage = localStorage;
Liferay.Util.SessionStorage = sessionStorage;

/* Set Portlet 3.0 hub global variable */

window.portlet = portlet;

Liferay.__INTERNALS = {
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
	Session,
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
