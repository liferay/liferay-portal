import VisitorsListCard from '../hocs/VisitorsListCard';
import React from 'react';
import {Router} from 'shared/types';

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
