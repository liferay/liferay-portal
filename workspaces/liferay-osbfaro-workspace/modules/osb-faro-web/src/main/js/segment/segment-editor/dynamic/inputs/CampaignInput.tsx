import Form from 'shared/components/form';
import React from 'react';
import {getCampaignIds} from '../utils/custom-inputs';
import {getSegmentCategoryLabel} from 'segment/utils/labels';
import {ISegmentEditorCustomInputBase} from '../utils/types';
import {SegmentCategories} from 'shared/util/constants';
import {sub} from 'shared/util/lang';
import {useCampaignNames} from 'shared/hooks/useCampaignNames';

interface ICampaignInputProps extends ISegmentEditorCustomInputBase {
	segmentCategory?: SegmentCategories;
}

/**
 * Shows the campaign a criterion row holds. Each row holds one campaign, the
 * one dragged in from the Conditions Library, so the campaign is not editable
 * here; several campaigns are combined as separate rows with AND/OR.
 */
const CampaignInput: React.FC<ICampaignInputProps> = ({
	channelId,
	groupId,
	operatorRenderer: OperatorDropdown,
	property,
	segmentCategory,
	value,
}) => {
	const ids = getCampaignIds(value);

	const {loading, names} = useCampaignNames({
		channelId: channelId!,
		groupId: groupId!,
		ids,
	});

	return (
		<div className="criteria-statement">
			<Form.Group autoFit>
				<Form.GroupItem className="entity-name" label shrink>
					{getSegmentCategoryLabel(segmentCategory)}
				</Form.GroupItem>

				<Form.GroupItem className="entity-name" label shrink>
					{Liferay.Language.get('campaign').toLowerCase()}
				</Form.GroupItem>

				<OperatorDropdown />

				<Form.GroupItem className="entity-name" label shrink>
					{Liferay.Language.get('touched-by')}
				</Form.GroupItem>

				<Form.GroupItem label>
					{loading
						? Liferay.Language.get('loading')
						: ids.map((id, index) => (
								<React.Fragment key={id}>
									{index > 0 && ', '}

									{names[id] ? (
										<b>{names[id]}</b>
									) : (
										<b className="undefined-entity">
											{sub(
												Liferay.Language.get(
													'undefined-x'
												),
												[property.label]
											)}
										</b>
									)}
								</React.Fragment>
							))}
				</Form.GroupItem>
			</Form.Group>
		</div>
	);
};

export default CampaignInput;
