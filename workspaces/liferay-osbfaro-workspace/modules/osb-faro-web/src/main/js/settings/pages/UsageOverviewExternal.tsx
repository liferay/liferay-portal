import BasePage from 'settings/components/base-page/BasePage';

import ClayLayout from '@clayui/layout';

import React from 'react';

import {compose, withProject} from 'shared/hoc';
import {GenericBarsCard} from 'settings/components/usage-overview/GenericBarsCard';
import {GenericDonutChart} from 'settings/components/usage-overview/GenericDonutChart';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {useParams} from 'react-router-dom';

export type Resource = {
	capacity: string;
	measurement: string;
};

export const UsageOverviewExternal = () => {
	const currentUser = useCurrentUser();
	const {groupId} = useParams<{groupId: string}>();
	const isLDP = useLDPEnabled({groupId});

	let pageActions: {
		displayType: string;
		href: string;
		icon: {symbol: string};
		label: string;
		target: string;
	}[] = [];

	if (currentUser.isAdmin()) {
		pageActions = [
			{
				displayType: 'primary',
				href: isLDP
					? 'https://one.liferay.com/'
					: 'https://support.liferay.com/',
				icon: {
					symbol: 'shortcut',
				},
				label: isLDP
					? Liferay.Language.get('go-to-liferay-one')
					: Liferay.Language.get('go-to-customer-portal'),
				target: '_blank',
			},
		];
	}

	const cardTitles = [
		Liferay.Language.get('number-of-sites'),
		Liferay.Language.get('authenticated-logins-malus'),
		Liferay.Language.get('anonymous-page-views-apv'),
	];

	const resources: Array<Resource> = [
		{
			capacity: Liferay.Language.get('extension-capacity'),
			measurement: 'RAM',
		},
		{
			capacity: Liferay.Language.get('extension-capacity'),
			measurement: 'vCPU',
		},
		{
			capacity: Liferay.Language.get('storage-capacity'),
			measurement: '',
		},
	];

	return (
		<BasePage
			key="UsageOverview"
			pageActions={pageActions}
			pageDescription={
				!isLDP &&
				Liferay.Language.get(
					'saas-plan-usage-is-determined-by-malus-and-apvs'
				)
			}
			pageTitle={Liferay.Language.get('subscription-&-usage')}
		>
			<div className="saas-banner p-5 sm:p-8 md:p-10 xl:p-4">
				<div className="text-white">
					<h2 className="title">
						{isLDP
							? Liferay.Language.get(
									'view-your-workspace-metrics'
								)
							: Liferay.Language.get(
									'view-your-saas-project-metrics'
								)}
					</h2>
					<p className="w-50 d-flex mb-0">
						{isLDP
							? Liferay.Language.get(
									'as-a-saas-customer-description-liferay-one'
								)
							: Liferay.Language.get(
									'as-a-saas-customer-description-customer-portal'
								)}
					</p>
				</div>
			</div>
			<div className="gradient-opaque mt-5">
				<div className="mt-2">
					<div className="w-100">
						<h2 className="title">
							{Liferay.Language.get('sites-and-users')}
						</h2>
					</div>
					<ClayLayout.Row>
						{cardTitles.map((title) => (
							<GenericBarsCard
								cardTitle={title}
								key={title}
								redactTitle={isLDP}
							/>
						))}
					</ClayLayout.Row>
				</div>
				<div className="mt-2">
					<div className="w-100">
						<h2 className="title">
							{Liferay.Language.get('resource-usage')}
						</h2>
					</div>
					<ClayLayout.Row>
						{resources.map(({capacity, measurement}) => (
							<GenericDonutChart
								capacity={capacity}
								key={`${measurement}-${measurement}`}
								measurement={measurement}
							/>
						))}
					</ClayLayout.Row>
				</div>
			</div>
		</BasePage>
	);
};

export default compose(withProject)(UsageOverviewExternal);
