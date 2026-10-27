import AccountsTouchedDataSet from './AccountsTouchedDataSet';
import Card from 'shared/components/Card';
import ClayTabs from '@clayui/tabs';
import IndividualsTouchedDataSet from './IndividualsTouchedDataSet';
import React, {useState} from 'react';
import {useLifecycleDetails} from 'lifecycle/hooks/useLifecycleDetails';

interface IMembershipsCardProps {
	campaignId: string;
	channelId: string;
	groupId: string;
}

const TABS = [
	{
		tabId: 'accounts-touched',
		title: Liferay.Language.get('accounts-touched'),
	},
	{
		tabId: 'individuals-touched',
		title: Liferay.Language.get('individuals-touched'),
	},
];

const MembershipsCard: React.FC<IMembershipsCardProps> = ({
	campaignId,
	channelId,
	groupId,
}) => {
	const [activeIndex, setActiveIndex] = useState(0);

	const {lifecycles, loading: lifecyclesLoading} = useLifecycleDetails({
		channelId,
		groupId,
	});

	return (
		<Card minHeight={300}>
			<Card.Header>
				<Card.Title>{Liferay.Language.get('membership')}</Card.Title>
			</Card.Header>

			<ClayTabs
				active={activeIndex}
				className="mb-3"
				onActiveChange={(index) => setActiveIndex(Number(index))}
			>
				{TABS.map(({tabId, title}) => (
					<ClayTabs.Item
						innerProps={{'aria-controls': `${tabId}-tabpanel`}}
						key={tabId}
					>
						{title}
					</ClayTabs.Item>
				))}
			</ClayTabs>

			<ClayTabs.Content activeIndex={activeIndex}>
				<ClayTabs.TabPane id="accounts-touched-tabpanel">
					<AccountsTouchedDataSet
						campaignId={campaignId}
						channelId={channelId}
						groupId={groupId}
						lifecycles={lifecycles}
						loading={lifecyclesLoading}
					/>
				</ClayTabs.TabPane>

				<ClayTabs.TabPane id="individuals-touched-tabpanel">
					<IndividualsTouchedDataSet
						campaignId={campaignId}
						channelId={channelId}
						groupId={groupId}
					/>
				</ClayTabs.TabPane>
			</ClayTabs.Content>
		</Card>
	);
};

export default MembershipsCard;
