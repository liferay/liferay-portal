/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI().use(() => {
	Liferay.namespace('Patcher');

	Liferay.Patcher = {
		compareTicket(a, b) {
			const aParts = a.split('-');
			const bParts = b.split('-');

			if (aParts[0] !== bParts[0]) {
				return aParts[0] > bParts[0] ? 1 : -1;
			}

			if (aParts.length === 1 || bParts.length === 1) {
				return bParts.length - aParts.length;
			}

			return parseInt(aParts[1], 10) - parseInt(bParts[1], 10);
		},
		getTicketLink(ticket) {
			if (ticket.toUpperCase() !== ticket) {
				return ticket;
			}

			return (
				'<a class="nowrap" href="https://liferay.atlassian.net/browse/' +
				ticket +
				'" title="' +
				ticket +
				'" target="_blank">' +
				ticket +
				'</a>'
			);
		},
		getTicketLinks(text) {
			return text
				.split(',')
				.map((x) => {
					return x.trim();
				})
				.sort(Liferay.Patcher.compareTicket)
				.map(Liferay.Patcher.getTicketLink)
				.join(', ');
		},
		getTicketLinksPopover(Y, align_points, tickets, trigger) {
			const popover = new Y.Popover({
				align: {
					node: trigger,
					points: align_points,
				},
				bodyContent: Liferay.Patcher.getTicketLinks(tickets.value),
				headerContent: 'JIRA Links',
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
		populateProjectVersionField(productVersionId, select, map) {
			while (select.firstChild) {
				select.removeChild(select.firstChild);
			}

			if (productVersionId && productVersionId !== '0') {
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
	};
});
