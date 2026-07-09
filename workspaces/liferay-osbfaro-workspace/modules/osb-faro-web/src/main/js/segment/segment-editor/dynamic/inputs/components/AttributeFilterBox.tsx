import AttributeConjunctionInput from './attribute-conjunction-input';
import Form from 'shared/components/form';
import getCN from 'classnames';
import React from 'react';
import {Attribute} from 'event-analysis/utils/types';
import {
	AttributeConjunctionChangeParams,
	AttributeFilterState,
	Criterion,
} from '../../utils/types';

interface IAttributeFilterBoxProps {
	attributes: Attribute[];
	conjunctionCriterion: Criterion;
	onChange: (params: AttributeConjunctionChangeParams) => void;
	onClear: () => void;
	touched: AttributeFilterState;
	valid: AttributeFilterState;
}

const AttributeFilterBox: React.FC<IAttributeFilterBoxProps> = ({
	attributes,
	conjunctionCriterion,
	onChange,
	onClear,
	touched,
	valid,
}) => {
	const hasRequiredMessage = touched.attributeValue && !valid.attributeValue;

	return (
		<div
			className={getCN(
				'attribute-filter-box c-gap-3 d-flex flex-wrap p-2 py-3',
				{'align-items-center': !hasRequiredMessage}
			)}
		>
			<Form.GroupItem
				className={getCN('conjunction d-flex', {
					'align-items-center': !hasRequiredMessage,
					'pt-1': hasRequiredMessage,
				})}
				label
				shrink
			>
				{Liferay.Language.get('where-event-attribute').toLowerCase()}
			</Form.GroupItem>

			<AttributeConjunctionInput
				attributes={attributes}
				conjunctionCriterion={conjunctionCriterion}
				onChange={onChange}
				onClear={onClear}
				small
				touched={touched}
				valid={valid}
			/>
		</div>
	);
};

export default AttributeFilterBox;
