/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import getRandomString from '../../../../utils/getRandomString';

const modifiedDate = new Date().toISOString();

export type Individual = {
	birthDate?: string;
	dataSourceId?: number | string;
	familyName?: string;
	firstName?: string;
	id: string;
	jobTitle?: string;
	name: string;
};

export async function createIndividuals({
	apiHelpers,
	channelId,
	individuals,
}: {
	apiHelpers: ApiHelpers;
	channelId?: string;
	individuals: Individual[];
}) {
	const formattedIndividuals = individuals.map(
		({
			birthDate = '1970-01-01T00:00:00.000Z',
			dataSourceId = 0,
			familyName = 'Smith',
			firstName,
			id,
			jobTitle,
			name,
		}) => {
			const givenName = firstName ?? name;

			return {
				birthday: birthDate,
				dataSourceId,
				dataSourceIds: [dataSourceId],
				emailAddress: `${name}@liferay.com`,
				fields: [
					{
						dataSourceId,
						name: 'birthday',
						value: String(new Date(birthDate).getTime()),
					},
					{
						dataSourceId,
						name: 'emailAddress',
						value: `${name}@liferay.com`,
					},
					{dataSourceId, name: 'firstName', value: givenName},
					{dataSourceId, name: 'lastName', value: familyName},
					...(jobTitle
						? [{dataSourceId, name: 'jobTitle', value: jobTitle}]
						: []),
				],
				firstName: givenName,
				id,
				lastName: familyName,
				modifiedDate,
			};
		}
	);

	await apiHelpers.jsonWebServicesOSBAsah.createIndividuals(
		formattedIndividuals
	);

	const individualIdentities = individuals.map(({id}) => ({
		createDate: modifiedDate,
		id,
		individualId: id,
	}));

	await apiHelpers.jsonWebServicesOSBAsah.createIdentities(
		individualIdentities
	);

	if (!channelId) {
		return;
	}

	await apiHelpers.jsonWebServicesOSBAsah.createIdentityActivitiesSummary(
		individuals.map(({dataSourceId = 0, id}) => ({
			activitiesCount: 1,
			channelId,
			dataSourceId,
			eventId: 'pageViewed',
			firstActivityDate: modifiedDate,
			identityId: id,
			individualId: id,
			lastActivityDate: modifiedDate,
		}))
	);
}

export function generateIndividual({name}: {name: string}): Individual {
	const id = getRandomString().replace(/-/g, '');

	return {
		id,
		name,
	};
}
