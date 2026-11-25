import EmbeddedAlertList from 'shared/components/EmbeddedAlertList';
import React, {useContext} from 'react';
import {AlertTypes} from 'shared/components/Alert';
import {CriterionGroup} from './utils/types';
import {hasUnavailableCampaign} from './utils/campaigns';
import {ReferencedObjectsContext} from './context/referencedObjects';

const UnavailableCampaignsAlert: React.FC<{criteria: CriterionGroup}> = ({
	criteria,
}) => {
	const {referencedEntities} = useContext(ReferencedObjectsContext);

	if (!hasUnavailableCampaign(criteria, referencedEntities)) {
		return null;
	}

	return (
		<EmbeddedAlertList
			alerts={[
				{
					iconSymbol: 'exclamation-full',
					message: Liferay.Language.get(
						'this-segment-has-a-criterion-that-is-no-longer-available.-update-or-remove-it-to-keep-the-segment-accurate'
					),
					title: Liferay.Language.get('error'),
					type: AlertTypes.Danger,
				},
			]}
		/>
	);
};

export default UnavailableCampaignsAlert;
