/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMemo} from 'react';
import {Liferay} from '~/services/liferay';
import {getBusinessEvents} from '~/services/liferay/api';
import {IBusinessEvent} from '~/utils/types';

export default function useAccountsSyncBusinessEvents(
	accountExternalReferenceCode: string,
	businessEvent: IBusinessEvent,
	isEdition: boolean,
	isRemoval: boolean
): any {
	const filterQuery = useMemo<string>(() => {
		let filterQuery = `filter=eventStatus ne 'canceled' and eventStatus ne 'completed' and r_accountEntryToBusinessEvents_accountEntryId eq '${businessEvent.r_accountEntryToBusinessEvents_accountEntryId || ''}'`;

		if (isEdition || isRemoval) {
			filterQuery += ` and id ne '${businessEvent.id || ''}'`;
		}

		filterQuery += `&sort=targetGoLiveDateTime:asc`;

		return filterQuery;
	}, [
		businessEvent.id,
		businessEvent.r_accountEntryToBusinessEvents_accountEntryId,
		isEdition,
		isRemoval,
	]);

	const updateAccountBusinessEvents = async () => {
		const businessEventsResponse = await getBusinessEvents(
			encodeURI(filterQuery)
		);

		const formattedBusinessEvents = businessEventsResponse.items.map(
			(businessEvent: IBusinessEvent) => {
				return {
					associatedTicketIds: JSON.parse(
						businessEvent.associatedTickets!
					).map((id: string) => Number(id)),
					currentVersion: businessEvent.currentLiferayVersion?.key
						? businessEvent.currentLiferayVersion?.name
						: null,
					description: businessEvent.description || null,
					eventType: businessEvent.eventType || null,
					name: businessEvent.name,
					newVersion: businessEvent.newLiferayVersion?.key
						? businessEvent.newLiferayVersion?.name
						: null,
					targetGoLiveDateTime:
						businessEvent.targetGoLiveDateTime?.split('T')[0],
				};
			}
		);

		if (isEdition || !isRemoval) {
			formattedBusinessEvents.push({
				associatedTicketIds: JSON.parse(
					businessEvent.associatedTickets!
				).map((id: string) => Number(id)),
				currentVersion: businessEvent.currentLiferayVersion?.key
					? businessEvent.currentLiferayVersion?.name
					: null,
				description: businessEvent.description || null,
				eventType: businessEvent.eventType || null,
				name: businessEvent.name,
				newVersion: businessEvent.newLiferayVersion?.key
					? businessEvent.newLiferayVersion?.name
					: null,
				targetGoLiveDateTime:
					businessEvent.targetGoLiveDateTime?.split('T')[0],
			});
		}

		const response: Response =
			await Liferay.OAuth2Client.FromUserAgentApplication(
				'liferay-customer-etc-spring-boot-oaua'
			).fetch(
				`/accounts/${accountExternalReferenceCode}/sync-business-events`,
				{
					body: JSON.stringify({
						businessEvents: formattedBusinessEvents,
					}),
					method: 'POST',
				}
			);

		if (!response.ok) {
			throw new Error(`Failed to update Org: ${response.statusText}`);
		}

		return response;
	};

	return {updateAccountBusinessEvents};
}
