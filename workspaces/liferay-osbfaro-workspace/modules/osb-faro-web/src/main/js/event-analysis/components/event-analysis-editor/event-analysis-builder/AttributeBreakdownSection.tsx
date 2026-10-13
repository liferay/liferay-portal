import AddConditionButton from 'shared/components/conditions-panel/AddConditionButton';
import AttributeBreakdownDropdown from './attribute-breakdown-dropdown';
import AttributeConditionsSection from './AttributeConditionsSection';
import React from 'react';
import {Align} from '@clayui/drop-down';
import {getBreakdownDisplay} from 'event-analysis/utils/utils';
import {SortableChipTypes} from './SortableChipTypes';
import {useAttributes} from '../context/attributes';

const MAX_ATTRIBUTES = 5;

interface IAttributeBreakdownSectionProps {
	eventId?: string;
}

const AttributeBreakdownSection: React.FC<IAttributeBreakdownSectionProps> = ({
	eventId,
}) => {
	const {
		addBreakdown,
		attributes,
		breakdownOrder,
		breakdowns,
		deleteBreakdown,
		moveBreakdown,
	} = useAttributes();

	return (
		<AttributeConditionsSection
			action={
				eventId &&
				breakdownOrder.length < MAX_ATTRIBUTES && (
					<AttributeBreakdownDropdown
						alignmentPosition={Align.RightTop}
						disabledIds={breakdownOrder.map(
							(breakdownId) => breakdowns[breakdownId].attributeId
						)}
						eventId={eventId}
						onAttributeSelect={addBreakdown}
						trigger={
							<AddConditionButton
								label={Liferay.Language.get('add-breakdown')}
							/>
						}
						uneditableIds={Object.keys(attributes)}
					/>
				)
			}
			className="attribute-breakdown-section-root"
			conditions={breakdowns}
			dragType={SortableChipTypes.Breakdown}
			getDisplay={(attribute, breakdown) =>
				getBreakdownDisplay(attribute, breakdown.attributeType)
			}
			onMove={moveBreakdown}
			onRemove={deleteBreakdown}
			order={breakdownOrder}
			title={Liferay.Language.get('breakdown-by')}
		/>
	);
};

export default AttributeBreakdownSection;
