import React from 'react';
import {getVisitorsListCard} from 'assets/hocs/VisitorsListCard';
import {Router} from 'shared/types';

const VisitorsListCard = getVisitorsListCard('journal');

const Accounts: React.FC<{
	router: Router;
}> = ({router}) => (
	<div className="row">
		<div className="col-sm-12">
			<VisitorsListCard router={router} />
		</div>
	</div>
);

export default Accounts;
