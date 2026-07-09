import ClayLink from '@clayui/link';
import React from 'react';
import {Routes, toRoute} from 'shared/util/router';
import {Text} from '@clayui/core';

export const buildHeaderSubtitle = (
	individual: {
		accountName: string;
		accounts?: Array<{accountName?: string; id?: string}>;
		lastSessionCountry: string;
		properties: {email: string};
	},
	{channelId, groupId}: {channelId: string; groupId: string}
) => {
	const {email} = individual.properties;
	const {accountName, accounts, lastSessionCountry} = individual;

	const accountId = accounts?.[0]?.id;

	const account =
		accountName && accountId ? (
			<ClayLink
				href={toRoute(Routes.CONTACTS_ACCOUNT, {
					channelId,
					groupId,
					id: accountId,
				})}
			>
				{accountName}
			</ClayLink>
		) : (
			accountName
		);

	const items = [email, account, lastSessionCountry].filter(Boolean);

	return (
		<Text color="secondary" size={3}>
			{items.map((item, index) => (
				<React.Fragment key={index}>
					{index > 0 && ' | '}

					{item}
				</React.Fragment>
			))}
		</Text>
	);
};
