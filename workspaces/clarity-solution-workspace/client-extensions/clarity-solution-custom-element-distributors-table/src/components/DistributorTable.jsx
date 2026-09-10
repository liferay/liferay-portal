import { useEffect, useState } from 'react';
import ClayTable from '@clayui/table';
import ClayButton from '@clayui/button';
import { getDistributors } from 'clarity-solution-js-import-maps-entry-distributor-details-service';
import gold from '../resources/gold.png';
import bronze from '../resources/bronze.png';
import silver from '../resources/silver.png';

const DistributorsTable = () => {
	const [distributors, setDistributors] = useState([]);
	const getTierImage = (tierKey) => {
		switch (tierKey) {
			case 'bronze':
				return bronze;
			case 'gold':
				return gold;
			case 'silver':
				return silver;
			default:
				return null;
		}
	}
	const selectDistributor = (distributor) => {
		Liferay.fire('selectDistributor', distributor);
	};

	useEffect(() => {
		getDistributors()
			.then((distributors) => setDistributors(distributors));
	}, []);

	return (
		<ClayTable>
			<ClayTable.Head>
				<ClayTable.Row>
					<ClayTable.Cell headingCell>Name</ClayTable.Cell>
					<ClayTable.Cell headingCell>City</ClayTable.Cell>
					<ClayTable.Cell headingCell>State</ClayTable.Cell>
					<ClayTable.Cell headingCell>Tier</ClayTable.Cell>
					<ClayTable.Cell headingCell>Action</ClayTable.Cell>
				</ClayTable.Row>
			</ClayTable.Head>
			<ClayTable.Body>
				{distributors.map((distributor) => (
					<ClayTable.Row key={distributor.id}>
						<ClayTable.Cell>{distributor.name}</ClayTable.Cell>
						<ClayTable.Cell>{distributor.city}</ClayTable.Cell>
						<ClayTable.Cell>{distributor.state}</ClayTable.Cell>
						<ClayTable.Cell>
							<img className='img img-fluid' src={getTierImage(distributor.tier.key)} alt={distributor.tier.key} />
						</ClayTable.Cell>
						<ClayTable.Cell>
							<ClayButton
								displayType="secondary"
								small
								onClick={() => selectDistributor(distributor)}
							>
								View Details
							</ClayButton>
						</ClayTable.Cell>
					</ClayTable.Row>
				))}
			</ClayTable.Body>
		</ClayTable>
	);
};

export default DistributorsTable;