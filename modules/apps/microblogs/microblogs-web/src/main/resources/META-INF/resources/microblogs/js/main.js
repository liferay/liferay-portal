/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI().use(
	'aui-base',
	'aui-io-plugin-deprecated',
	'liferay-util-window',
	function (A) {
		Liferay.namespace('Microblogs');

		Liferay.Microblogs = {
			init: function (param) {
				const instance = this;

				instance._baseActionURL = param.baseActionURL;
				instance._microblogsEntriesURL = param.microblogsEntriesURL;
			},

			closePopup: function () {
				const instance = this;

				const popup = instance.getPopup();

				if (popup) {
					popup.hide();
				}
			},

			displayPopup: function (url, title) {
				const instance = this;

				const popup = instance.getPopup();

				popup.show();

				popup.titleNode.html(title);

				popup.io.set('uri', url);

				popup.io.start();
			},

			getPopup: function () {
				const instance = this;

				if (!instance._popup) {
					instance._popup = Liferay.Util.Window.getWindow({
						dialog: {
							centered: true,
							constrain2view: true,
							cssClass: 'microblogs-portlet',
							modal: true,
							resizable: false,
							width: 475,
						},
					})
						.plug(A.Plugin.IO, {
							autoLoad: false,
						})
						.render();
				}

				return instance._popup;
			},

			updateMicroblogs: function (form, url, updateContainer) {
				const instance = this;

				Liferay.Util.fetch(form.getAttribute('action'), {
					body: new FormData(form.getDOM()),
					method: 'POST',
				}).then(function () {
					instance.updateMicroblogsList(url, updateContainer);

					Liferay.fire('microblogPosted');
				});
			},

			updateMicroblogsList: function (url, updateContainer) {
				const instance = this;

				instance._micrblogsEntries = updateContainer;

				if (!instance._micrblogsEntries.io) {
					instance._micrblogsEntries.plug(A.Plugin.IO, {
						autoLoad: false,
					});
				}

				if (!url) {
					url = instance._microblogsEntriesURL;
				}

				instance._micrblogsEntries.io.set('uri', url);

				instance._micrblogsEntries.io.start();
			},

			updateViewCount: function (microblogsEntryId) {
				const instance = this;

				const portletURL = new Liferay.Util.PortletURL.createPortletURL(
					instance._baseActionURL,
					{
						'jakarta.portlet.action':
							'updateMicroblogsEntryViewCount',
						microblogsEntryId,
						'p_p_state': 'normal',
					}
				);

				Liferay.Util.fetch(portletURL.toString(), {
					method: 'POST',
				});
			},
		};

		Liferay.on('sessionExpired', function (event) {
			const reload = () => {
				window.location.reload();
			};

			Liferay.Microblogs.displayPopup = reload;
			Liferay.Microblogs.updateMicroblogs = reload;
			Liferay.Microblogs.updateMicroblogsList = reload;
		});
	}
);
