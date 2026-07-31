<script data-senna-track="temporary" type="text/javascript">
(function() {
	function buildESMStub(contextPath, symbol) {
		return (
			(...args) => {
				import(
					Liferay.ThemeDisplay.getCDNHost() +
					Liferay.ThemeDisplay.getPathContext() +
						'/o/' +
						contextPath +
						'/__liferay__/index.js'
				).then(
					(exports) => exports[symbol](...args)
				);
			}
		);
	}

	function defineReadOnlyGlobal(name, getValue) {
		Object.defineProperty(
			window,
			name,
			{
				get: getValue,
				set: (x) => {
					if (x !== getValue()) {
						console.error(`Global variable '${name}' is read-only`);
					}
				}
			}
		);
	}

	function isObject(item) {
		return (item && typeof item === 'object' && !Array.isArray(item));
	}

	function merge(target, source) {
		for (const key in source) {
			if (isObject(source[key])) {
				if (!target[key]) {
					Object.assign(target, { [key]: {} });
				}

				merge(target[key], source[key]);
			}
			else {
				Object.assign(target, { [key]: source[key] });
			}
		}
	}

	let __liferay = {
AUI: {
getCombine: () => false,
getComboPath: () => '/combo/?minifierType=&t=0&',
getDateFormat: () => '%m/%d/%Y',
getEditorCKEditorPath: () => '/o/ckeditor',
getFilter: () => 'raw',
getFilterConfig: () => ({
replaceStr: () => '.js?minifierType=&t=0',
searchExp: () => '\\.js$',
}),
getJavaScriptRootPath: () => '/o/frontend-js-web',
getPortletRootPath: () => '/html/portlet',
getStaticResourceURLParams: () => '?minifierType=&t=0',
},
Browser: {
acceptsGzip: () => false,
getMajorVersion: () => '42.0',
getRevision: () => '42.0',
getVersion: () => '42.0',
isAir: () => false,
isChrome: () => false,
isEdge: () => false,
isFirefox: () => false,
isGecko: () => false,
isIe: () => false,
isIphone: () => false,
isLinux: () => false,
isMac: () => false,
isMobile: () => false,
isMozilla: () => false,
isOpera: () => false,
isRtf: () => false,
isSafari: () => false,
isSun: () => false,
isWebKit: () => false,
isWindows: () => false,
},
Data: {
ICONS_INLINE_SVG: true,
NAV_SELECTOR: '#navigation',
NAV_SELECTOR_MOBILE: '#navigationCollapse',
isCustomizationView: () => false,
notices: [
{
message: 'the-portal-will-shutdown-for-maintenance-in-x-minutes',
title: 'maintenance-alert<span class="mx-2">2009/04/23, 00:00:00 UTC</span>',
type: 'warning'
},
],
},
FeatureFlags: {
},
Language: {
	_cache:
		window?.Liferay?.Language?._cache
			? Liferay.Language._cache
			: {},
	available: {
'fr_FR': 'français\x20\x28France\x29',
'en_CA': 'English\x20\x28Canada\x29',

	},
	direction: {
'fr_FR': 'ltr',
'en_CA': 'ltr',

	},
	get:
		(key) => {
			let value = Liferay.Language._cache[key];

			if (value === undefined) {
				value = key;
			}

			return value;
		},
},
Portlet: {
openModal: buildESMStub('frontend-js-components-web', 'openPortletModal'),
openWindow: buildESMStub('frontend-js-components-web', 'openPortletWindow'),
},
PortletKeys: {
DOCUMENT_LIBRARY: 'com_liferay_document_library_web_portlet_DLPortlet',
DYNAMIC_DATA_MAPPING: 'com_liferay_dynamic_data_mapping_web_portlet_DDMPortlet',
INSTANCE_SETTINGS: 'com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet',
ITEM_SELECTOR: 'com_liferay_item_selector_web_portlet_ItemSelectorPortlet',
},
PropsValues: {
ENTERPRISE_PRODUCT_AI_HUB_ENABLED: false,
JAVASCRIPT_SINGLE_PAGE_APPLICATION_TIMEOUT: 0,
UPLOAD_SERVLET_REQUEST_IMPL_MAX_SIZE: 0,
},
ThemeDisplay: {
getBCP47LanguageId: () => 'en-US',
getCanonicalURL: () => 'http\x3a\x2f\x2flocalhost\x3a8080',
getCDNBaseURL: () => 'http://localhost:8080',
getCDNDynamicResourcesHost: () => '',
getCDNHost: () => '',
getCompanyGroupId: () => '0',
getCompanyId: () => '0',
getDefaultLanguageId: () => 'en',
getDoAsUserIdEncoded: () => '',
getLanguageId: () => 'en_US',
getParentGroupId: () => '0',
getParentSiteGroupId: () => '0',
getPathContext: () => '',
getPathImage: () => '/image',
getPathJavaScript: () => '/o/frontend-js-web',
getPathMain: () => 'c',
getPathThemeImages: () => 'http://localhost:8080/o/classic-theme/images',
getPathThemeRoot: () => '/o/classic-theme',
getPlid: () => '0',
getPortalURL: () => 'http://localhost:8080',
getRealUserId: () => '0',
getRemoteAddr: () => '127.0.0.1',
getRemoteHost: () => '127.0.0.1',
getScopeGroupId: () => '0',
getScopeGroupIdOrLiveGroupId: () => '0',
getSessionId: () => '',
getSiteAdminURL: () => 'http://localhost:8080/group/guest/~/control_panel/manage?p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view',
getSiteGroupId: () => '0',
getTimeZone: () => 'UTC',
getURLControlPanel: () => '/group/control_panel?refererPlid=8',
getURLHome: () => 'http\x3a\x2f\x2flocalhost\x3a8080\x2fweb\x2fguest',
getUserEmailAddress: () => '',
getUserId: () => '0',
getUserName: () => '',
isAddSessionIdToURL: () => false,
isImpersonated: () => false,
isSignedIn: () => false,
isStagedPortlet: () => false,
isStateExclusive: () => false,
isStateMaximized: () => false,
isStatePopUp: () => false,
},
Util: {
Window: {
	_map: {},
	getById: (id) => Liferay.Util.Window._map[id],
},
openAlertModal: buildESMStub('frontend-js-components-web', 'openAlertModal'),
openConfirmModal: buildESMStub('frontend-js-components-web', 'openConfirmModal'),
openModal: buildESMStub('frontend-js-components-web', 'openModal'),
openSelectionModal: buildESMStub('frontend-js-components-web', 'openSelectionModal'),
openSimpleInputModal: buildESMStub('frontend-js-components-web', 'openSimpleInputModal'),
openToast: buildESMStub('frontend-js-components-web', 'openToast'),
},
authToken: 'LrPaVz44',
currentURL: '\x2f',
currentURLEncoded: '\x252F',

	};

	if (window.Liferay) {
		merge(window.Liferay, __liferay);
	}
	else {
		defineReadOnlyGlobal('Liferay', () => __liferay);
		defineReadOnlyGlobal('themeDisplay', () => window.Liferay.ThemeDisplay);
	}
})();
</script>