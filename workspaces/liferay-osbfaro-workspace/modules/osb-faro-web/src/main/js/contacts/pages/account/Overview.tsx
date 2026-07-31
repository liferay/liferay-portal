import AccountInfoBar from './components/AccountInfoBar';
import BasePage from 'shared/components/base-page';
import ClayLayout from '@clayui/layout';
import React from 'react';
import TopAssets from './components/TopAssets';
import TopCategoriesAndTags from './components/TopCategoriesAndTags';
import TopPagesCard from './components/TopPagesCard';
import {IAccount} from './components/AccountInfo';
import {SectionHeader} from 'shared/components/SectionHeader';
import {useParams} from 'react-router-dom';
import {useQueryParams} from 'shared/hooks/useQueryParams';

interface IOverviewProps {
	account?: IAccount;
}

const Overview: React.FC<IOverviewProps> = ({account}) => {
	const {channelId, groupId, id} = useParams<{
		channelId: string;
		groupId: string;
		id: string;
	}>();
	const query = useQueryParams();

	return (
		<BasePage.Context.Provider
			value={{
				accountId: id,
				accountName: account?.accountName,
				filters: {},
				router: {
					params: {channelId, groupId, id},
					query,
				},
			}}
		>
			<SectionHeader
				icon="box-container"
				title={Liferay.Language.get('account-info')}
			/>

			<AccountInfoBar
				accountName={account?.accountName}
				accountType={account?.accountType}
				annualRevenue={account?.annualRevenue}
				country={account?.country}
				industry={account?.industry}
				lifecycleStage={account?.lifecycleStage}
			/>

			<SectionHeader
				className="mb-3 mt-4"
				icon="display-content"
				title={Liferay.Language.get('engagement-summary')}
			/>

			<ClayLayout.Row>
				<ClayLayout.Col size={12}>
					<TopPagesCard className="top-pages-card-root" />
				</ClayLayout.Col>
			</ClayLayout.Row>

			<ClayLayout.Row>
				<ClayLayout.Col className="d-flex flex-column" size={12} xl={6}>
					<TopAssets account={account} className="flex-grow-1" />
				</ClayLayout.Col>

				<ClayLayout.Col className="d-flex flex-column" size={12} xl={6}>
					<TopCategoriesAndTags
						account={account}
						className="flex-grow-1"
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>
		</BasePage.Context.Provider>
	);
};

export default Overview;
