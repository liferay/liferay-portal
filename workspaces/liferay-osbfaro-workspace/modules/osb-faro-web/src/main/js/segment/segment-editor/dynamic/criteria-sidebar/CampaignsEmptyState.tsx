import CriteriaSidebarDataSourceEmptyState from './CriteriaSidebarDataSourceEmptyState';
import EmptyState from '@clayui/empty-state';
import Loading from 'shared/components/Loading';
import React from 'react';
import {useDataSources} from 'shared/context/dataSources';

const CampaignsEmptyState: React.FC = () => {
	const {empty: noDataSources, loading} = useDataSources();

	if (loading) {
		return <Loading overlay />;
	}

	if (noDataSources) {
		return (
			<CriteriaSidebarDataSourceEmptyState
				description={Liferay.Language.get(
					'connect-a-data-source-containing-campaign-data'
				)}
				title={Liferay.Language.get('no-campaign-data-has-been-synced')}
			/>
		);
	}

	return (
		<div className="align-items-center d-flex empty-message h-100 justify-content-center">
			<EmptyState
				className="text-center"
				description={Liferay.Language.get(
					'no-campaigns-were-synced-from-the-connected-data-sources'
				)}
				title={Liferay.Language.get('no-campaigns-were-found')}
			/>
		</div>
	);
};

export default CampaignsEmptyState;
