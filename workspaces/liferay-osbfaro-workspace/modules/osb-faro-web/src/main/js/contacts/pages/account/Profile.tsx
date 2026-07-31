import AccountIndividuals from './components/AccountIndividuals';
import AccountInfo, {IAccount} from './components/AccountInfo';
import LifecycleStatus from './components/LifecycleStatus';
import React from 'react';
import TopCategoriesAndTags from './components/TopCategoriesAndTags';
import {SectionHeader} from 'shared/components/SectionHeader';

interface IProfileProps {
	account?: IAccount;
	loading?: boolean;
}

const Profile: React.FC<IProfileProps> = ({account, loading}) => (
	<section>
		<SectionHeader
			icon="plus-squares"
			title={Liferay.Language.get('account-details')}
		/>

		<div className="account-profile-cards mb-3">
			<LifecycleStatus />
			<AccountInfo account={account} loading={loading} />
			<TopCategoriesAndTags account={account} />
		</div>

		<AccountIndividuals />
	</section>
);

export default Profile;
