import BasePage from 'settings/components/BasePage';

import ClayLayout from '@clayui/layout';

import React from 'react';

import {compose, withProject} from 'shared/hoc';
import {GenericBarsCard} from 'settings/components/usage-overview/GenericBarsCard';
import {GenericDonutChart} from 'settings/components/usage-overview/GenericDonutChart';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';

export type Resource = {
	capacity: string;
	measurement: string;
};

interface IUsageOverviewSaaS {
	groupId: string;
}

export const UsageOverviewSaaS: React.FC<IUsageOverviewSaaS> = ({groupId}) => {
	const currentUser = useCurrentUser();

	let pageActions = [];

	if (currentUser.isAdmin()) {
		pageActions = [
			{
				displayType: 'primary',
				href: 'https://support.liferay.com/',
				icon: {
					symbol: 'shortcut'
				},
				label: Liferay.Language.get('go-to-customer-portal'),
				target: '_blank'
			}
		];
	}

	const cardTitles = [
		Liferay.Language.get('number-of-sites'),
		Liferay.Language.get('authenticated-logins-malus'),
		Liferay.Language.get('anonymous-page-views-avp')
	];

	const resources: Array<Resource> = [
		{
			capacity: Liferay.Language.get('extension-capacity'),
			measurement: 'RAM'
		},
		{
			capacity: Liferay.Language.get('extension-capacity'),
			measurement: 'vCPU'
		},
		{
			capacity: Liferay.Language.get('storage-capacity'),
			measurement: ''
		}
	];

	return (
		<BasePage
			groupId={groupId}
			key='UsageOverview'
			pageActions={pageActions}
			pageDescription={Liferay.Language.get(
				'saas-plan-usage-is-determined-by-malus-and-avps'
			)}
			pageTitle={Liferay.Language.get('subscription-&-usage')}
		>
			<div className='saas-banner p-5 sm:p-8 md:p-10 xl:p-4'>
				<div className='text-white'>
					<h2 className='title'>
						{Liferay.Language.get('view-your-saas-project-metrics')}
					</h2>
					<p className='w-50 d-flex mb-0'>
						{Liferay.Language.get('as-a-saas-customer-description')}
					</p>
				</div>
			</div>
			<div className='gradient-opaque mt-5'>
				<div className='mt-2'>
					<div className='w-100'>
						<h2 className='title'>
							{Liferay.Language.get('sites-and-users')}
						</h2>
					</div>
					<ClayLayout.Row>
						{cardTitles.map(title => (
							<GenericBarsCard cardTitle={title} key={title} />
						))}
					</ClayLayout.Row>
				</div>
				<div className='mt-2'>
					<div className='w-100'>
						<h2 className='title'>
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

export default compose(withProject)(UsageOverviewSaaS);
