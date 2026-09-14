import React from 'react';
import {getKnownIndividualsListCard} from 'assets/hocs/KnownIndividualsListCard';
import {Router} from 'shared/types';

const KnownIndividuals: React.FC<{
	graphQLType: string;
	router: Router;
}> = ({graphQLType, router}) => {
	const KnownIndividualsListCard = getKnownIndividualsListCard(graphQLType);

	return (
		<div className="row">
			<div className="col-sm-12">
				<KnownIndividualsListCard router={router} />
			</div>
		</div>
	);
};

export default KnownIndividuals;
