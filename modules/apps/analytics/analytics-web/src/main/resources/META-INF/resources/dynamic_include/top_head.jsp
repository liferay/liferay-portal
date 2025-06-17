<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<meta content="<%= (String)request.getAttribute(AnalyticsWebKeys.ANALYTICS_CLIENT_READABLE_CONTENT) %>" name="data-analytics-readable-content" />

<aui:script senna="temporary" type="text/javascript">
	var runMiddlewares = function () {
		<liferay-util:dynamic-include key="/dynamic_include/top_head.jsp#analytics" />
	};

	var analyticsClientChannelId =
		'<%= (String)request.getAttribute(AnalyticsWebKeys.ANALYTICS_CLIENT_CHANNEL_ID) %>';
	var analyticsClientGroupIds =
		<%= (String)request.getAttribute(AnalyticsWebKeys.ANALYTICS_CLIENT_GROUP_IDS) %>;
	var analyticsCookiesConsentMode =
		<%= (boolean)request.getAttribute(AnalyticsWebKeys.ANALYTICS_COOKIES_EXPLICIT_CONSENT_MODE) %>;

	var cookieManagers = {
		'cookie.onetrust': {
			checkConsent: () => {
				var OptanonActiveGroups = window.OptanonActiveGroups;

				return OptanonActiveGroups && OptanonActiveGroups.includes('C0002');
			},
			enabled: () => {
				if (!window.OneTrustStub && !window.OneTrust) {
					return Promise.resolve(false);
				}

				return new Promise((resolve, reject) => {
					var startTime = Date.now();

					var checkObject = () => {
						if (window['OneTrust']) {
							resolve(window['OneTrust']);
						}
						else if (Date.now() - startTime >= 5000) {
							reject();
						}
						else {
							setTimeout(checkObject, 100);
						}
					};

					checkObject();
				})
					.then(() => {
						return Promise.resolve(true);
					})
					.catch(() => {
						return Promise.resolve(false);
					});
			},
			onConsentChange: (callbackFn) => {
				var OneTrust = window.OneTrust;

				OneTrust.OnConsentChanged(callbackFn);
			},
		},
		'cookie.liferay': {
			checkConsent: ({navigation}) => {
				var performanceCookieEnabled = Liferay.Util.Cookie.get(
					Liferay.Util.Cookie.TYPES.PERFORMANCE
				);

				if (performanceCookieEnabled === 'false') {
					if (window.Analytics) {
						Analytics.dispose();
					}

					return false;
				}

				if (
					!analyticsCookiesConsentMode &&
					typeof performanceCookieEnabled === 'undefined'
				) {
					return true;
				}

				if (navigation === 'normal' && window.Analytics) {
					return false;
				}

				return performanceCookieEnabled === 'true';
			},
			enabled: () => {
				return Promise.resolve(true);
			},
			onConsentChange: (callbackFn) => {
				Liferay.on('cookieBannerSetCookie', callbackFn);
			},
		},
	};

	function <portlet:namespace />getAnalyticsSDKVersion() {
		switch (
			'<%= GetterUtil.getString(PropsUtil.get(PropsKeys.ANALYTICS_CLOUD_CLIENT_JS_VERSION)) %>'
		) {
			case 'DEV': {
				return 'https://analytics-js-dev-cdn.liferay.com';
			}
			case 'INTERNAL': {
				return 'https://analytics-js-internal-cdn.liferay.com';
			}
			default: {
				return 'https://analytics-js-cdn.liferay.com';
			}
		}
	}
</aui:script>

<aui:script id="liferayAnalyticsScript" senna="permanent" type="text/javascript">
	var allPromises = Object.keys(cookieManagers).map((key) =>
		cookieManagers[key].enabled()
	);

	Promise.all(allPromises).then((result) => {
		var selectedIndex = result.findIndex((enabled) => enabled);
		var selectedCookieManager = Object.values(cookieManagers)[selectedIndex];

		function <portlet:namespace />initializeAnalyticsSDK() {
			(function (u, c, a, m, o, l) {
				o = 'script';
				l = document;
				a = l.createElement(o);
				m = l.getElementsByTagName(o)[0];
				a.async = 1;
				a.src = u;
				a.onload = c;
				m.parentNode.insertBefore(a, m);
			})(<portlet:namespace />getAnalyticsSDKVersion(), () => {
				var config =
					<%= (String)request.getAttribute(AnalyticsWebKeys.ANALYTICS_CLIENT_CONFIG) %>;

				var dxpMiddleware = function (request) {
					request.context.canonicalUrl = themeDisplay.getCanonicalURL();
					request.context.channelId = analyticsClientChannelId;
					request.context.groupId =
						themeDisplay.getScopeGroupIdOrLiveGroupId();

					return request;
				};

				Analytics.create(config, [dxpMiddleware]);

				if (themeDisplay.isSignedIn()) {
					Analytics.setIdentity({
						email: themeDisplay.getUserEmailAddress(),
						name: themeDisplay.getUserName(),
					});
				}

				runMiddlewares();

				Analytics.send('pageViewed', 'Page');

				<c:if test="<%= GetterUtil.getBoolean(PropsUtil.get(PropsKeys.JAVASCRIPT_SINGLE_PAGE_APPLICATION_ENABLED)) %>">
					Liferay.on('endNavigate', (event) => {
						var allPromises = Object.keys(cookieManagers).map((key) =>
							cookieManagers[key].enabled()
						);

						Promise.all(allPromises).then((result) => {
							function <portlet:namespace />initializeAnalyticsSDKFromSPA(
								event
							) {
								Analytics.dispose();

								if (
									!themeDisplay.isControlPanel() &&
									analyticsClientGroupIds.indexOf(
										String(
											themeDisplay.getScopeGroupIdOrLiveGroupId()
										)
									) >= 0
								) {
									Analytics.create(config, [dxpMiddleware]);

									if (themeDisplay.isSignedIn()) {
										Analytics.setIdentity({
											email: themeDisplay.getUserEmailAddress(),
											name: themeDisplay.getUserName(),
										});
									}

									runMiddlewares();

									Analytics.send('pageViewed', 'Page', {
										page: event.path,
									});
								}
							}

							var selectedIndex = result.findIndex((enabled) => enabled);
							var selectedCookieManager =
								Object.values(cookieManagers)[selectedIndex];

							if (selectedCookieManager) {
								selectedCookieManager.onConsentChange(() => {
									if (
										selectedCookieManager.checkConsent({
											navigation: 'spa',
										})
									) {
										<portlet:namespace />initializeAnalyticsSDKFromSPA(
											event
										);
									}
								});

								if (
									selectedCookieManager.checkConsent({
										navigation: 'spa',
									})
								) {
									<portlet:namespace />initializeAnalyticsSDKFromSPA(
										event
									);
								}
							}
							else {
								<portlet:namespace />initializeAnalyticsSDKFromSPA(
									event
								);
							}
						});
					});
				</c:if>
			});
		}

		if (selectedCookieManager) {
			selectedCookieManager.onConsentChange(() => {
				if (selectedCookieManager.checkConsent({navigation: 'normal'})) {
					<portlet:namespace />initializeAnalyticsSDK();
				}
			});

			if (selectedCookieManager.checkConsent({navigation: 'normal'})) {
				<portlet:namespace />initializeAnalyticsSDK();
			}
		}
		else {
			<portlet:namespace />initializeAnalyticsSDK();
		}
	});
</aui:script>