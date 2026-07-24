import VisitorsListCard from '../hocs/VisitorsListCard';
import React from 'react';

interface ITouchpointAccountsPageProps {
	router: object;
}

const TouchpointAccountsPage: React.FC<ITouchpointAccountsPageProps> = ({
	router,
}) => (
	<div className="row">
		<div className="col-sm-12">
			<VisitorsListCard router={router} />
		</div>
	</div>
);

export default TouchpointAccountsPage;
