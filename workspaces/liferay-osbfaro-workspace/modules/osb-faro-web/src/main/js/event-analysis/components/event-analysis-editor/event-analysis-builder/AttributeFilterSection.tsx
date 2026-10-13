import AddConditionButton from 'shared/components/conditions-panel/AddConditionButton';
import AttributeConditionsSection from './AttributeConditionsSection';
import AttributeFilterDropdown from './attribute-filter-dropdown';
import React from 'react';
import {Align} from '@clayui/drop-down';
import {getFilterDisplay} from 'event-analysis/utils/utils';
import {getSafeDecodedURIComponent} from 'shared/util/util';
import {SortableChipTypes} from './SortableChipTypes';
import {useAttributes} from '../context/attributes';

interface IAttributeFilterSectionProps {
	eventId?: string;
}

const AttributeFilterSection: React.FC<IAttributeFilterSectionProps> = ({
	eventId,
}) => {
	const {attributes, deleteFilter, filterOrder, filters, moveFilter} =
		useAttributes();

	return (
		<AttributeConditionsSection
			action={
				eventId && (
					<AttributeFilterDropdown
						alignmentPosition={Align.RightTop}
						eventId={eventId}
						trigger={
							<AddConditionButton
								label={Liferay.Language.get('add-filter')}
							/>
						}
						uneditableIds={Object.keys(attributes)}
					/>
				)
			}
			className="attribute-filter-section-root"
			conditions={filters}
			dragType={SortableChipTypes.Filter}
			getDisplay={(attribute, filter) => {
				const [overline, label] = getFilterDisplay(attribute, filter);

				return [overline, getSafeDecodedURIComponent(label)];
			}}
			onMove={moveFilter}
			onRemove={deleteFilter}
			order={filterOrder}
			title={Liferay.Language.get('filter-by')}
		/>
	);
};

export default AttributeFilterSection;
