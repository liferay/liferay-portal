import React from 'react';
import {getKnownIndividualsListCard} from 'assets/hocs/KnownIndividualsListCard';
import {Router} from 'shared/types';

const KnownIndividualsListCard = getKnownIndividualsListCard('journal');

const KnownIndividuals: React.FC<{
	router: Router;
}> = ({router}) => (
	<div className="row">
		<div className="col-sm-12">
			<KnownIndividualsListCard router={router} />
		</div>
	</div>
);

export default KnownIndividuals;
