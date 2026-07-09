import AttributeFilterBox from './AttributeFilterBox';
import EventPropertiesQuery, {
	EventPropertiesData,
	EventPropertiesVariables,
} from '../../queries/EventPropertiesQuery';
import React from 'react';
import {
	AttributeConjunctionChangeParams,
	AttributeFilterState,
	Criterion,
} from '../../utils/types';
import {cloneAttributes} from 'event-analysis/utils/utils';
import {NAME} from 'shared/util/pagination';
import {OrderByDirections} from 'shared/util/constants';
import {SafeResults} from 'shared/hoc/util';
import {useQuery} from '@apollo/client';

interface IAttributeFilterSectionProps {
	conjunctionCriterion: Criterion;
	eventId: string;
	onChange: (params: AttributeConjunctionChangeParams) => void;
	onClear: () => void;
	touched: AttributeFilterState;
	valid: AttributeFilterState;
}

const AttributeFilterSection: React.FC<IAttributeFilterSectionProps> = ({
	conjunctionCriterion,
	eventId,
	onChange,
	onClear,
	touched,
	valid,
}) => {
	const result = useQuery<EventPropertiesData, EventPropertiesVariables>(
		EventPropertiesQuery,
		{
			skip: !eventId,
			variables: {
				eventId,
				keyword: '',
				page: 0,
				size: 25,
				sort: {
					column: NAME,
					type: OrderByDirections.Ascending,
				},
			},
		}
	);

	if (!eventId) {
		return null;
	}

	return (
		<SafeResults {...result} page={false} pageDisplay={false}>
			{(data: any) => {
				const rawAttributes =
					data?.eventProperties?.eventProperties || [];
				const attributes = cloneAttributes(rawAttributes);

				if (!attributes.length) {
					return null;
				}

				return (
					<AttributeFilterBox
						attributes={attributes}
						conjunctionCriterion={conjunctionCriterion}
						onChange={onChange}
						onClear={onClear}
						touched={touched}
						valid={valid}
					/>
				);
			}}
		</SafeResults>
	);
};

export default AttributeFilterSection;
