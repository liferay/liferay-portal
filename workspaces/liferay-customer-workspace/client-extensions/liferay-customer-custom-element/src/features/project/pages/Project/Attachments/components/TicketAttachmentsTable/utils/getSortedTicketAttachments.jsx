/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function getSortedTicketAttachments(
	ticketAttachments,
	sortConfig
) {
	let sortedTicketAttachments = [];

	const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];

	if (ticketAttachments) {
		if (sortConfig.columnName === 'fileSize') {
			sortedTicketAttachments = sortSizes(ticketAttachments);
		} else if (sortConfig.columnName === 'ticketId') {
			sortedTicketAttachments = sortTicketId(ticketAttachments);
		} else {
			sortedTicketAttachments = ticketAttachments.sort((a, b) => {
				if (a[sortConfig.columnName] < b[sortConfig.columnName]) {
					return sortConfig.direction === 'ascending' ? -1 : 1;
				}
				if (a[sortConfig.columnName] > b[sortConfig.columnName]) {
					return sortConfig.direction === 'ascending' ? 1 : -1;
				}

				return 0;
			});
		}
	}

	function sortTicketId(ticketAttachments) {
		ticketAttachments.sort((x, y) => {
			if (x.ticketId < y.ticketId) {
				return sortConfig.direction === 'ascending' ? -1 : 1;
			}
			else if (x.ticketId > y.ticketId) {
				return sortConfig.direction === 'ascending' ? 1 : -1;
			}

			return 0;
		});

		return ticketAttachments;
	}

	function sortSizes(ticketAttachments) {
		ticketAttachments.sort((x, y) => {
			const x_fileSize = x.fileSize;
			const y_fileSize = y.fileSize;

			const x_index_first_char = x_fileSize.indexOf(
				x_fileSize.match(/[a-zA-Z]/).pop()
			);
			const y_index_first_char = y_fileSize.indexOf(
				y_fileSize.match(/[a-zA-Z]/).pop()
			);

			const splitAt = (index, xs) => [
				xs.slice(0, index),
				xs.slice(index),
			];

			const x_res = splitAt(x_index_first_char, x_fileSize);
			const y_res = splitAt(y_index_first_char, y_fileSize);
			const x_value = x_res[0];
			const x_unit = x_res[1];
			const y_value = y_res[0];
			const y_unit = y_res[1];

			const amount = casting(x_unit, y_unit, x_value);

			if (amount < y_value) {
				return -1;
			} else if (x_value > y_value) {
				return 1;
			} else {
				return 0;
			}
		});

		return sortConfig.direction === 'descending'
			? ticketAttachments.reverse()
			: ticketAttachments;
	}

	function casting(unit_from, unit_to, amount) {
		let i = sizes.indexOf(unit_from);
		const j = sizes.indexOf(unit_to);
		let r;
		if (i < j) {
			r = j - i;
		} else {
			r = j - i;
		}

		i = 0;
		if (r < 0) {
			r *= -1;
			while (i < r) {
				amount *= 1024;
				i++;
			}
		} else {
			while (i < r) {
				amount /= 1024;
				i++;
			}
		}

		return amount;
	}

	return sortedTicketAttachments;
}
