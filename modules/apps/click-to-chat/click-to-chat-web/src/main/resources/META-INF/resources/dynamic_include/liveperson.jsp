<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<aui:script position="inline" type="text/javascript">
	(window.lpTag = window.lpTag || {}),
		'undefined' == typeof window.lpTag._tagCount
			? ((window.lpTag = {
					autoStart: lpTag.autoStart !== !1,
					ctn: lpTag.ctn || [],
					dbs: lpTag.dbs || [],
					defer: function (t, e) {
						0 === e
							? ((this._defB = this._defB || []), this._defB.push(t))
							: 1 === e
								? ((this._defT = this._defT || []),
									this._defT.push(t))
								: ((this._defL = this._defL || []),
									this._defL.push(t));
					},
					ev: lpTag.ev || [],
					events: {
						bind: function (t, e, i) {
							lpTag.defer(() => {
								lpTag.events.bind(t, e, i);
							}, 0);
						},
						trigger: function (t, e, i) {
							lpTag.defer(() => {
								lpTag.events.trigger(t, e, i);
							}, 1);
						},
					},
					hooks: lpTag.hooks || [],
					identities: lpTag.identities || [],
					init: function () {
						(this._timing = this._timing || {}),
							(this._timing.start = new Date().getTime());
						var t = this;
						window.attachEvent
							? window.attachEvent('onload', () => {
									t._domReady('domReady');
								})
							: (window.addEventListener(
									'DOMContentLoaded',
									() => {
										t._domReady('contReady');
									},
									!1
								),
								window.addEventListener(
									'load',
									() => {
										t._domReady('domReady');
									},
									!1
								)),
							'undefined' === typeof window._lptStop && this.load();
					},
					load: function (t, e, i) {
						var n = this;
						setTimeout(() => {
							n._load(t, e, i);
						}, 0);
					},
					ovr: lpTag.ovr || {},
					protocol: 'https:',
					wl: lpTag.wl || null,
					scp: lpTag.scp || null,
					sdes: lpTag.sdes || [],
					section: lpTag.section || '',
					site:
						'<%= HtmlUtil.escapeJS(clickToChatChatProviderAccountId) %>' ||
						'',
					start: function () {
						this.autoStart = !0;
					},
					tagletSection: lpTag.tagletSection || null,
					vars: lpTag.vars || [],
					_domReady: function (t) {
						this.isDom ||
							((this.isDom = !0),
							this.events.trigger('LPT', 'DOM_READY', {
								t: t,
							})),
							(this._timing[t] = new Date().getTime());
					},
					_load: function (t, e, i) {
						var n = t;
						t ||
							(n =
								this.protocol +
								'//' +
								(this.ovr && this.ovr.domain
									? this.ovr.domain
									: 'lptag.liveperson.net') +
								'/tag/tag.js?site=' +
								this.site);
						var o = document.createElement('script');
						o.setAttribute('charset', e ? e : 'UTF-8'),
							i && o.setAttribute('id', i),
							o.setAttribute('src', n),
							document
								.getElementsByTagName('head')
								.item(0)
								.appendChild(o);
					},
					_tagCount: 1,
					_v: '1.10.0',
				}),
				lpTag.init())
			: (window.lpTag._tagCount += 1);

	lpTag.sdes = lpTag.sdes || [];

	lpTag.sdes.push({
		personal: {
			contacts: [
				{
					email: '<%= HtmlUtil.escapeJS(user.getEmailAddress()) %>',
				},
			],
			firstname: '<%= HtmlUtil.escapeJS(user.getFirstName()) %>',
			lastname: '<%= HtmlUtil.escapeJS(user.getLastName()) %>',
		},
		type: 'personal',
	});

	lpTag.sdes.push({
		info: {
			customerId: '<%= user.getScreenName() %>',
			userName: '<%= user.getUserId() %>',
		},
		type: 'ctmrinfo',
	});
</aui:script>