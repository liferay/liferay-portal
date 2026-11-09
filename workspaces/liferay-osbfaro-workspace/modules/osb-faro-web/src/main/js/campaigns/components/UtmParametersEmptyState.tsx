import ClayEmptyState from '@clayui/empty-state';
import React from 'react';

const UtmParametersEmptyState = () => (
	<div className="border py-5 rounded">
		<ClayEmptyState
			className="text-center"
			description={Liferay.Language.get(
				'this-campaign-has-no-utm-parameters-associated-with-it-yet'
			)}
			small
			title={Liferay.Language.get('no-utm-parameters-found')}
		/>
	</div>
);

export default UtmParametersEmptyState;
