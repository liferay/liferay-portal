import React, {useContext} from 'react';
import {CustomValue} from 'shared/util/records';
import {
	EntityType,
	ReferencedObjectsContext,
} from 'segment/segment-editor/dynamic/context/referencedObjects';
import {getCampaignIds} from 'segment/segment-editor/dynamic/utils/custom-inputs';
import {getOperatorLabel} from '../utils';
import {getSegmentCategoryLabel} from 'segment/utils/labels';
import {getUnavailableCampaignIds} from 'segment/segment-editor/dynamic/utils/campaigns';
import {IDisplayComponentProps} from '../types';

const CampaignDisplay: React.FC<IDisplayComponentProps> = ({
	criterion,
	property,
	segmentCategory,
}) => {
	const {referencedEntities} = useContext(ReferencedObjectsContext);

	if (getUnavailableCampaignIds(criterion, referencedEntities).length) {
		return (
			<b className="undefined-property">
				{Liferay.Language.get('campaign-no-longer-exists')}
			</b>
		);
	}

	const ids = getCampaignIds(criterion.value as CustomValue);

	return (
		<>
			<span className="sentence-start">
				{getSegmentCategoryLabel(segmentCategory)}
			</span>

			<span>{Liferay.Language.get('campaign').toLowerCase()}</span>

			<span>
				{getOperatorLabel(criterion.operatorName ?? '', property.type)}
			</span>

			<span>{Liferay.Language.get('touched-by').toLowerCase()}</span>

			{ids.map((id, index) => (
				<React.Fragment key={id}>
					{index > 0 && ', '}

					<b>{`'${referencedEntities.getIn([
						EntityType.Campaigns,
						id,
						'campaignName',
					])}'`}</b>
				</React.Fragment>
			))}
		</>
	);
};

export default CampaignDisplay;
