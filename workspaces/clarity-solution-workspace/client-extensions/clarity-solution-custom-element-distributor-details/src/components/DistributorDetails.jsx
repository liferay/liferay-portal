import {useEffect, useState} from 'react';
import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';

const DistributorDetails = () => {
	const [selectedDistributor, setSelectedDistributor] = useState(null);

	useEffect(() => {
		const handle = Liferay.on('selectDistributor', (distributor) => {
			setSelectedDistributor(distributor);
		});

		return () => handle.detach();
	}, []);

	return !selectedDistributor ? (
		<ClayAlert displayType="info" title="Info:">
			Please select a distributor from the table.
		</ClayAlert>
	) : (
		<div className="row">
			<div className="col">
				<h2>{selectedDistributor.name}</h2>
				<p>
					Location: {selectedDistributor.city},{' '}
					{selectedDistributor.state}
				</p>
			</div>
			<div className="col">
				<ClayButton displayType="primary">
					Contact Distributor
				</ClayButton>
			</div>
		</div>
	);
};

export default DistributorDetails;
