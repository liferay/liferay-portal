/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect, useState} from 'react';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import {Liferay} from '~/services/liferay';
import {IBusinessEvent, ITicket} from '~/utils/types';

const useAccountsTickets = (
	businessEvent?: IBusinessEvent,
	externalReferenceCode?: string,
	skip?: boolean
) => {
	const {featureFlags} = useAppPropertiesContext();
	const [loading, setLoading] = useState(true);
	const [tickets, setTickets] = useState<ITicket[] | undefined>(undefined);

	const fetchTickets = useCallback(async () => {
		if (skip || !externalReferenceCode) {
			setLoading(false);

			return;
		}

		try {
			let ticketsParam = '';

			if (businessEvent && featureFlags.includes('LRSD-8280')) {
				const associatedTickets = JSON.parse(
					businessEvent.associatedTickets!
				);

				ticketsParam = `?ticketIds=${associatedTickets.join(',')}`;
			}

			const response: ITicket[] =
				await Liferay.OAuth2Client.FromUserAgentApplication(
					'liferay-customer-etc-spring-boot-oaua'
				)
					.fetch(
						`/accounts/${externalReferenceCode}/tickets${ticketsParam}`
					)
					.then((response: {json: () => any}) => response.json());

			setTickets(response);

			setLoading(false);
		}
		catch (error) {
			console.error('Error fetching tickets data:', error);

			setTickets(undefined);

			setLoading(false);
		}
	}, [businessEvent, externalReferenceCode, featureFlags, skip]);

	useEffect(() => {
		setLoading(true);

		fetchTickets();
	}, [fetchTickets]);

	return {loading, tickets};
};

export default useAccountsTickets;
