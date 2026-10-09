import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import DateBreakdown from './DateBreakdown';
import DurationBreakdown from './DurationBreakdown';
import FilterInfo from '../../FilterInfo';
import NumberBreakdown from './NumberBreakdown';
import React from 'react';
import {DataTypes} from 'shared/types/DataTypes';
import {Attribute, AttributeOwnerTypes} from 'event-analysis/utils/types';
import {useAttributes} from '../../../context/attributes';

import {IBreakdownProps} from 'event-analysis/utils/types';

const BREAKDOWNS_MAP: Partial<Record<DataTypes, React.FC<IBreakdownProps>>> = {
	[DataTypes.Date]: DateBreakdown,
	[DataTypes.Duration]: DurationBreakdown,
	[DataTypes.Number]: NumberBreakdown,
};

interface IBreakdownOptionsProps extends React.HTMLAttributes<HTMLDivElement> {
	attribute: Attribute;
	attributeOwnerType: AttributeOwnerTypes;
	onActiveChange: (active: boolean) => void;
	onAttributeChange: (attribute?: Attribute) => void;
	onEditClick?: (id: string) => void;
}

const BreakdownOptions: React.FC<IBreakdownOptionsProps> = ({
	attribute,
	attributeOwnerType,
	onActiveChange,
	onAttributeChange,
	onEditClick,
}) => {
	const {addBreakdown} = useAttributes();

	const {
		dataType,
		description,
		displayName,
		id: attributeId,
		name,
	} = attribute;

	const BreakdownBody = BREAKDOWNS_MAP[dataType];

	if (!BreakdownBody) {
		return null;
	}

	return (
		<div className="attribute-options">
			<div className="options-header">
				<ClayButton
					className="button-root back-to-attributes-button"
					displayType="unstyled"
					onClick={() => onAttributeChange(undefined)}
					size="sm"
				>
					<ClayIcon
						className="icon-root mr-2"
						symbol="angle-left-small"
					/>

					{Liferay.Language.get('back-to-attributes')}
				</ClayButton>

				<FilterInfo
					dataType={dataType}
					name={displayName || name}
					onEditClick={onEditClick}
				/>
			</div>

			<BreakdownBody
				attributeId={attributeId}
				attributeOwnerType={attributeOwnerType}
				description={description}
				displayName={displayName ?? ''}
				onSubmit={(newBreakdown: IBreakdownProps['breakdown']) => {
					addBreakdown({
						attribute,
						breakdown: newBreakdown!,
					});

					onAttributeChange(undefined);

					onActiveChange(false);
				}}
			/>
		</div>
	);
};

export default BreakdownOptions;
