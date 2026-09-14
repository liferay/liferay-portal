import React from 'react';
import {getVisitorsListCard} from 'assets/hocs/VisitorsListCard';
import {Router} from 'shared/types';

const Accounts: React.FC<{
	graphQLType: string;
	router: Router;
}> = ({graphQLType, router}) => {
	const VisitorsListCard = getVisitorsListCard(graphQLType);

	return (
		<div className="row">
			<div className="col-sm-12">
				<VisitorsListCard router={router} />
			</div>
		</div>
	);
};

export default Accounts;
