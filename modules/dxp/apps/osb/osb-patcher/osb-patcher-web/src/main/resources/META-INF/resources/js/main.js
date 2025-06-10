/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI().use((A) => {
	Liferay.namespace('Patcher');

	Liferay.Patcher = {
		closeWindow () {
			const dialog = Liferay.Util.getWindow();

			if (dialog) {
				dialog.hide();
			}
		},
		compareTicket (a, b) {
			const aParts = a.split('-');
			const bParts = b.split('-');

			if (aParts[0] != bParts[0]) {
				return aParts[0] > bParts[0] ? 1 : -1;
			}

			if (aParts.length == 1 || bParts.length == 1) {
				return bParts.length - aParts.length;
			}

			return parseInt(aParts[1]) - parseInt(bParts[1]);
		},
		getTicketLink (className, ticket, title) {
			if (ticket.toUpperCase() != ticket) {
				return ticket;
			}

			let ticketURL = 'https://liferay.atlassian.net/browse/' + ticket;

			if (className) {
				const productVersionElement = querySelector(
					'patcherProductVersionId'
				);
				const productVersionId = productVersionElement.value;
				const projectVersionElement = querySelector(
					'patcherProjectVersionId'
				);
				const projectVersionId = projectVersionElement.value;

				const params = {
					advancedSearch: true,
					andOperator: true,
					hideOldFixVersions: true,
					patcherFixName: ticket,
					patcherProductVersionId: productVersionId,
					patcherProjectVersionIdFilter: projectVersionId,
				};

				ticketURL =
					'https://patcher.liferay.com/group/guest/patching/-/osb_patcher?' +
					getQueryString(params);
			}

			if (!title) {
				title = ticket;
			}

			return (
				'<a class="nowrap ' +
				className +
				'" href="' +
				ticketURL +
				'" title="' +
				title +
				'" target="_blank">' +
				ticket +
				'</a>'
			);
		},
		getTicketLinks (text) {
			return text
				.split(',')
				.map((x) => {
					return x.trim();
				})
				.sort(Liferay.Patcher.compareTicket)
				.map(Liferay.Patcher.getTicketLink.bind(null, ''))
				.join(', ');
		},
		getTicketLinksPopover (Y, align_points, tickets, trigger) {
			const popover = new Y.Popover({
				align: {
					node: trigger,
					points: align_points,
				},
				headerContent: 'JIRA Links',
				bodyContent: Liferay.Patcher.getTicketLinks(tickets.value),
				position: 'right',
				visible: false,
				zIndex: 1,
			}).render();

			trigger.on('click', () => {
				popover.set('visible', !popover.get('visible'));
				popover.set(
					'bodyContent',
					Liferay.Patcher.getTicketLinks(tickets.value)
				);
			});

			trigger.on('change', () => {
				popover.set(
					'bodyContent',
					Liferay.Patcher.getTicketLinks(tickets.value)
				);
			});
		},
		openWindow (url, title, modal, width) {
			Liferay.Util.openWindow({
				dialog: {
					align: Liferay.Util.Window.ALIGN_CENTER,
					modal,
					width,
				},
				title,
				uri: url,
			});
		},
		populateProjectVersionField (productVersionId, select, map) {
			while (select.firstChild) {
				select.removeChild(select.firstChild);
			}

			if (productVersionId && productVersionId != 0) {
				const projectVersions = map[productVersionId];

				for (let i = 0; i < projectVersions.length; i++) {
					if (!projectVersions[i].hide) {
						const option = document.createElement('option');

						option.innerHTML = projectVersions[i].name;

						option.value =
							projectVersions[i].patcherProjectVersionId;

						select.appendChild(option);
					}
				}
			}
		},
		updateProductVersionId (url, productVersionId, namespace) {
			if (url.indexOf('patcherProductVersionId') === -1) {
				if (url.indexOf('?') === -1) {
					url += '?';
				}
				else {
					url += '&';
				}

				var newurl =
					url +
					namespace +
					'patcherProductVersionId=' +
					productVersionId;
			}
			else {
				const re = /patcherProductVersionId=[0-9]*/;

				var newurl = url.replace(
					re,
					'patcherProductVersionId=' + productVersionId
				);
			}

			return newurl;
		},
	};
});
