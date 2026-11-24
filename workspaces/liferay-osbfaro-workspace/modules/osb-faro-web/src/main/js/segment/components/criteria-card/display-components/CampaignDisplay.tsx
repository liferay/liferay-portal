import React from 'react';
import {CustomValue} from 'shared/util/records';
import {getCampaignIds} from 'segment/segment-editor/dynamic/utils/custom-inputs';
import {getOperatorLabel} from '../utils';
import {getSegmentCategoryLabel} from 'segment/utils/labels';
import {IDisplayComponentProps} from '../types';
import {sub} from 'shared/util/lang';
import {useCampaignNames} from 'shared/hooks/useCampaignNames';
import {useParams} from 'react-router-dom';

/**
 * Reads as the segment builder's row does, for example "Individual campaign
 * is touched by 'Spring Sale'".
 */
const CampaignDisplay: React.FC<IDisplayComponentProps> = ({
	criterion,
	property,
	segmentCategory,
}) => {
	const {channelId = '', groupId = ''} = useParams<{
		channelId: string;
		groupId: string;
	}>();

	const ids = getCampaignIds(criterion.value as CustomValue);

	const {loading, names} = useCampaignNames({channelId, groupId, ids});

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

			{!loading &&
				ids.map((id, index) => (
					<React.Fragment key={id}>
						{index > 0 && ', '}

						{names[id] ? (
							<b>{`'${names[id]}'`}</b>
						) : (
							<b className="undefined-entity">
								{sub(Liferay.Language.get('undefined-x'), [
									property.label,
								])}
							</b>
						)}
					</React.Fragment>
				))}
		</>
	);
};

export default CampaignDisplay;
