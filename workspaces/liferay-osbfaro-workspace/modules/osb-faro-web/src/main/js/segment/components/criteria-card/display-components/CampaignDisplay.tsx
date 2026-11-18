import React from 'react';
import {CustomValue} from 'shared/util/records';
import {getCampaignIds} from 'segment/segment-editor/dynamic/utils/custom-inputs';
import {getOperatorLabel} from '../utils';
import {IDisplayComponentProps} from '../types';
import {sub} from 'shared/util/lang';
import {useCampaignNames} from 'shared/hooks/useCampaignNames';
import {useParams} from 'react-router-dom';

const CampaignDisplay: React.FC<IDisplayComponentProps> = ({
	criterion,
	property,
}) => {
	const {channelId = '', groupId = ''} = useParams<{
		channelId: string;
		groupId: string;
	}>();

	const {entityName, label, type} = property;

	const ids = getCampaignIds(criterion.value as CustomValue);

	const {loading, names} = useCampaignNames({channelId, groupId, ids});

	return (
		<>
			{entityName}

			<b>{label}</b>

			<span>{getOperatorLabel(criterion.operatorName ?? '', type)}</span>

			{!loading &&
				ids.map((id, index) => (
					<React.Fragment key={id}>
						{index > 0 && ', '}

						{names[id] ? (
							<b>{`'${names[id]}'`}</b>
						) : (
							<b className="undefined-entity">
								{sub(Liferay.Language.get('undefined-x'), [
									label,
								])}
							</b>
						)}
					</React.Fragment>
				))}
		</>
	);
};

export default CampaignDisplay;
