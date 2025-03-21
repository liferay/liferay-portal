/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * The Portlet URL Utility
 *
 * @deprecated As of Athanasius (7.3.x), replaced by Liferay.Util.PortletURL
 * @module liferay-portlet-url
 */

AUI.add(
	'liferay-portlet-url',
	(A) => {
		const Lang = A.Lang;

		const PortletURL = function (lifecycle, params, basePortletURL) {
			const instance = this;

			instance.params = {};

			instance.reservedParams = {
				doAsGroupId: null,
				doAsUserId: null,
				doAsUserLanguageId: null,
				p_auth: null,
				p_auth_secret: null,
				p_f_id: null,
				p_j_a_id: null,
				p_l_id: null,
				p_l_reset: null,
				p_p_auth: null,
				p_p_cacheability: null,
				p_p_i_id: null,
				p_p_id: null,
				p_p_isolated: null,
				p_p_lifecycle: null,
				p_p_mode: null,
				p_p_resource_id: null,
				p_p_state: null,
				p_p_state_rcv: null,
				p_p_static: null,
				p_p_url_type: null,
				p_p_width: null,
				p_t_lifecycle: null,
				p_v_l_s_g_id: null,
				refererGroupId: null,
				refererPlid: null,
				saveLastPath: null,
				scroll: null,
			};

			if (!basePortletURL) {
				basePortletURL =
					themeDisplay.getPortalURL() +
					themeDisplay.getPathMain() +
					'/portal/layout?p_l_id=' +
					themeDisplay.getPlid();
			}

			instance.options = {
				basePortletURL,
				escapeXML: null,
				secure: null,
			};

			A.each(params, (item, index) => {
				if (Lang.isValue(item)) {
					if (instance._isReservedParam(index)) {
						instance.reservedParams[index] = item;
					}
					else {
						instance.params[index] = item;
					}
				}
			});

			if (lifecycle) {
				instance.setLifecycle(lifecycle);
			}
		};

		PortletURL.prototype = {
			_isReservedParam(paramName) {
				const instance = this;

				let result = false;

				A.each(instance.reservedParams, (item, index) => {
					if (index === paramName) {
						result = true;
					}
				});

				return result;
			},

			/*
			 * @deprecated As of Wilberforce (7.0.x)
			 */

			setCopyCurrentRenderParameters() {
				const instance = this;

				return instance;
			},

			setDoAsGroupId(doAsGroupId) {
				const instance = this;

				instance.reservedParams.doAsGroupId = doAsGroupId;

				return instance;
			},

			setDoAsUserId(doAsUserId) {
				const instance = this;

				instance.reservedParams.doAsUserId = doAsUserId;

				return instance;
			},

			/*
			 * @deprecated As of Wilberforce (7.0.x)
			 */

			setEncrypt() {
				const instance = this;

				return instance;
			},

			setEscapeXML(escapeXML) {
				const instance = this;

				instance.options.escapeXML = escapeXML;

				return instance;
			},

			setLifecycle(lifecycle) {
				const instance = this;

				const reservedParams = instance.reservedParams;

				if (lifecycle === PortletURL.ACTION_PHASE) {
					reservedParams.p_auth = Liferay.authToken;
					reservedParams.p_p_lifecycle = PortletURL.ACTION_PHASE;
				}
				else if (lifecycle === PortletURL.RENDER_PHASE) {
					reservedParams.p_p_lifecycle = PortletURL.RENDER_PHASE;
				}
				else if (lifecycle === PortletURL.RESOURCE_PHASE) {
					reservedParams.p_p_lifecycle = PortletURL.RESOURCE_PHASE;
					reservedParams.p_p_cacheability = 'cacheLevelPage';
				}

				return instance;
			},

			setName(name) {
				const instance = this;

				instance.setParameter('jakarta.portlet.action', name);

				return instance;
			},

			setParameter(key, value) {
				const instance = this;

				if (instance._isReservedParam(key)) {
					instance.reservedParams[key] = value;
				}
				else {
					instance.params[key] = value;
				}

				return instance;
			},

			setParameters(parameters) {
				const instance = this;

				A.each(parameters, (item, index) => {
					instance.setParameter(index, item);
				});

				return instance;
			},

			setPlid(plid) {
				const instance = this;

				instance.reservedParams.p_l_id = plid;

				return instance;
			},

			/*
			 * @deprecated As of Wilberforce (7.0.x)
			 */

			setPortletConfiguration() {
				const instance = this;

				return instance;
			},

			setPortletId(portletId) {
				const instance = this;

				instance.reservedParams.p_p_id = portletId;

				return instance;
			},

			setPortletMode(portletMode) {
				const instance = this;

				instance.reservedParams.p_p_mode = portletMode;

				return instance;
			},

			setResourceId(resourceId) {
				const instance = this;

				instance.reservedParams.p_p_resource_id = resourceId;

				return instance;
			},

			/*
			 * @deprecated As of Mueller (7.2.x), with no direct replacement
			 */
			setSecure(secure) {
				const instance = this;

				instance.options.secure = secure;

				return instance;
			},

			setWindowState(windowState) {
				const instance = this;

				instance.reservedParams.p_p_state = windowState;

				return instance;
			},

			toString() {
				const instance = this;

				const options = instance.options;

				const reservedParameters = {};

				Object.entries(instance.reservedParams).forEach(
					([key, value]) => {
						if (value !== null && value !== undefined) {
							reservedParameters[key] = value;
						}
					}
				);

				const parameters = {
					...instance.params,
					...reservedParameters,
				};

				const portletURL = Liferay.Util.PortletURL.createPortletURL(
					options.basePortletURL,
					parameters
				);

				if (options.secure) {
					portletURL.protocol = 'https:';
				}

				if (options.escapeXML) {
					portletURL.href = Lang.String.escapeHTML(portletURL.href);
				}

				return portletURL.toString();
			},
		};

		A.mix(PortletURL, {
			ACTION_PHASE: '1',

			RENDER_PHASE: '0',

			RESOURCE_PHASE: '2',

			// These are constructor functions and so must not use the concise
			// method syntax. See COMMERCE-5035.

			createActionURL: function createActionURL() {
				return new PortletURL(PortletURL.ACTION_PHASE);
			},

			createRenderURL: function createRenderURL() {
				return new PortletURL(PortletURL.RENDER_PHASE);
			},

			createResourceURL: function createResourceURL() {
				return new PortletURL(PortletURL.RESOURCE_PHASE);
			},

			createURL: function createURL(basePortletURL, params) {
				return new PortletURL(null, params, basePortletURL);
			},
		});

		Liferay.PortletURL = PortletURL;
	},
	'',
	{
		requires: ['aui-base'],
	}
);
