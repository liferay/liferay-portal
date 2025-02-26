/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Liferay} from '.';
import {fetcher} from './fetcher';

const HEADLESS_DELIVERY_BASE_URL_ = `${window.location.origin}/o/headless-delivery/v1.0`;
const HEADLESS_BASE_URL = `${window.location.origin}/o/`;

const fetchHeadless = async ({
	resolveAsJson = true,
	url,
}: {
	resolveAsJson?: boolean;
	url: string;
}) => {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(`${HEADLESS_DELIVERY_BASE_URL_}${url}`, {
		headers: {
			'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
			'Cache-Control': 'max-age=30, stale-while-revalidate=30',
			'x-csrf-token': Liferay.authToken,
		},
	});

	if (resolveAsJson) {
		return response.json();
	}

	return response;
};

const getBusinessEventById = async (id: string | number) => {
	return fetcher(`${HEADLESS_BASE_URL}${`c/businessevents/${id}`}`, {
		headers: {
			'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
			'Content-Type': 'application/json',
			'x-csrf-token': Liferay.authToken,
		},
		method: 'GET',
	});
};

const getBusinessEvents = async (filters: string) => {
	return fetcher(`${HEADLESS_BASE_URL}${`c/businessevents?${filters}`}`, {
		headers: {
			'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
			'Content-Type': 'application/json',
			'x-csrf-token': Liferay.authToken,
		},
		method: 'GET',
	});
};

const getHighPriorityContacts = async (filter: string) => {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${HEADLESS_BASE_URL}${`c/highprioritycontactses/?nestedFields=user&filter=${filter}`}`,
		{
			headers: {
				'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
				'Cache-Control': 'max-age=30, stale-while-revalidate=30',
				'x-csrf-token': Liferay.authToken,
			},
		}
	);

	return response.json();
};

const getTicketAttachments = async (filter: string) => {
	return fetcher(
		`${HEADLESS_BASE_URL}${`c/ticketattachments?filter=${filter}`}`,
		{
			headers: {
				'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
				'Cache-Control': 'max-age=30, stale-while-revalidate=30',
				'x-csrf-token': Liferay.authToken,
			},
			method: 'GET',
		}
	);
};

export {
	getBusinessEvents,
	getBusinessEventById,
	getHighPriorityContacts,
	getTicketAttachments,
	fetchHeadless,
};
