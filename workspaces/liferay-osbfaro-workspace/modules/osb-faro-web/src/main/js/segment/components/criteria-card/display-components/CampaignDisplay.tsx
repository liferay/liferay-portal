import React, {useContext} from 'react';
import {CustomValue} from 'shared/util/records';
import {
	EntityType,
	ReferencedObjectsContext,
} from 'segment/segment-editor/dynamic/context/referencedObjects';
import {getCampaignIds} from 'segment/segment-editor/dynamic/utils/custom-inputs';
import {getOperatorLabel} from '../utils';
import {getSegmentCategoryLabel} from 'segment/utils/labels';
import {IDisplayComponentProps} from '../types';

/**
 * Reads as the segment builder's row does, for example "Individual campaign
 * is touched by 'Spring Sale'". Names come from the segment's referenced
 * objects, the same source the segment builder reads, so a renamed campaign
 * shows its current name.
 */
const CampaignDisplay: React.FC<IDisplayComponentProps> = ({
	criterion,
	property,
	segmentCategory,
}) => {
	const {referencedEntities} = useContext(ReferencedObjectsContext);

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

			<span>{Liferay.Language.get('touched-by')}</span>

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
