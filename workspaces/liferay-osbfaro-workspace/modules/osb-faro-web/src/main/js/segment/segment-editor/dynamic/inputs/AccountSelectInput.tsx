import Form from 'shared/components/form';
import React from 'react';
import {getPropertyValue, setPropertyValue} from '../utils/custom-inputs';
import {ISegmentEditorCustomInputBase} from '../utils/types';
import {Option, Picker} from '@clayui/core';
import {useLifecycleStageOptions} from 'shared/hooks/useLifecycleStageOptions';

interface IAccountSelectProps extends ISegmentEditorCustomInputBase {
	groupId: string;
	operatorRenderer: React.ElementType;
}

const AccountSelectInput: React.FC<IAccountSelectProps> = ({
	displayValue,
	groupId,
	onChange,
	operatorRenderer: OperatorDropdown,
	property,
	value,
}) => {
	const {options} = useLifecycleStageOptions({groupId});

	return (
		<div className="account-select-input-root criteria-statement">
			<Form.Group autoFit>
				<Form.GroupItem className="entity-name" label shrink>
					{property.entityName}
				</Form.GroupItem>

				<Form.GroupItem className="display-value" label shrink>
					{displayValue}
				</Form.GroupItem>

				<OperatorDropdown />

				<Form.GroupItem>
					<Picker
						disabled={options.length === 0}
						items={options}
						onSelectionChange={(stageId) =>
							onChange({
								valid: true,
								value: setPropertyValue(
									value,
									'value',
									0,
									String(stageId)
								),
							})
						}
						selectedKey={getPropertyValue(value, 'value', 0)}
					>
						{({label, value}) => (
							<Option key={value}>{label}</Option>
						)}
					</Picker>
				</Form.GroupItem>
			</Form.Group>
		</div>
	);
};

export default AccountSelectInput;
