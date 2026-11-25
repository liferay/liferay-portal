import Form from 'shared/components/form';
import React, {useContext} from 'react';
import {
	EntityType,
	ReferencedObjectsContext,
} from '../context/referencedObjects';
import {getCampaignIds} from '../utils/custom-inputs';
import {getSegmentCategoryLabel} from 'segment/utils/labels';
import {ISegmentEditorCustomInputBase} from '../utils/types';
import {SegmentCategories} from 'shared/util/constants';

interface ICampaignInputProps extends ISegmentEditorCustomInputBase {
	segmentCategory?: SegmentCategories;
}

/**
 * Shows the campaign a criterion row holds. Each row holds one campaign, the
 * one dragged in from the Conditions Library, so the campaign is not editable
 * here; several campaigns are combined as separate rows with AND/OR. Names
 * come from the segment's referenced objects, so a renamed campaign shows its
 * current name; a campaign missing from them is shown by the row as no longer
 * existing, never by this input.
 */
const CampaignInput: React.FC<ICampaignInputProps> = ({
	operatorRenderer: OperatorDropdown,
	segmentCategory,
	value,
}) => {
	const {referencedEntities} = useContext(ReferencedObjectsContext);

	const ids = getCampaignIds(value);

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
					{Liferay.Language.get('touched-by').toLowerCase()}
				</Form.GroupItem>

				<Form.GroupItem label>
					{ids.map((id, index) => (
						<React.Fragment key={id}>
							{index > 0 && ', '}

							<b>
								{referencedEntities.getIn([
									EntityType.Campaigns,
									id,
									'campaignName',
								])}
							</b>
						</React.Fragment>
					))}
				</Form.GroupItem>
			</Form.Group>
		</div>
	);
};

export default CampaignInput;
