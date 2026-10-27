import AccountsTouchedDataSet from './AccountsTouchedDataSet';
import Card from 'shared/components/Card';
import ClayTabs from '@clayui/tabs';
import IndividualsTouchedDataSet from './IndividualsTouchedDataSet';
import React, {useState} from 'react';

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
					<ClayTabs.Item key={tabId}>{title}</ClayTabs.Item>
				))}
			</ClayTabs>

			{TABS[activeIndex].tabId === 'accounts-touched' && (
				<AccountsTouchedDataSet
					campaignId={campaignId}
					channelId={channelId}
					groupId={groupId}
				/>
			)}

			{TABS[activeIndex].tabId === 'individuals-touched' && (
				<IndividualsTouchedDataSet
					campaignId={campaignId}
					channelId={channelId}
					groupId={groupId}
				/>
			)}
		</Card>
	);
};

export default MembershipsCard;
