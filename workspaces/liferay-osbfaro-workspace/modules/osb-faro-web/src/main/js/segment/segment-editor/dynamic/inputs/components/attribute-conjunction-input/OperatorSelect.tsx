import Form from 'shared/components/form';
import getCN from 'classnames';
import React from 'react';
import {Criterion} from '../../../utils/types';
import {DataTypes} from 'event-analysis/utils/types';
import {
	FunctionalOperators,
	RelationalOperators,
} from '../../../utils/constants';
import {getDefaultAttributeValue, getOperatorOptions} from './utils';
import {Option, Picker} from '@clayui/core';

interface IOperatorSelectProps {
	dataType: DataTypes;
	onChange: (params: {criterion: Criterion}) => void;
	operatorName: Criterion['operatorName'];
	small?: boolean;
}

const OperatorSelect: React.FC<IOperatorSelectProps> = ({
	dataType,
	onChange,
	operatorName,
	small,
}) => {
	if (dataType === DataTypes.Boolean) {
		return (
			<Form.GroupItem className="conjunction ml-1 mr-1" label shrink>
				{Liferay.Language.get('is')}
			</Form.GroupItem>
		);
	}

	return (
		<Form.GroupItem shrink>
			<Picker
				className={getCN('operator-input', {
					'form-control-sm': small,
				})}
				items={
					getOperatorOptions(dataType) as {
						label: string;
						value: string;
					}[]
				}
				onSelectionChange={(newOperatorName) => {
					let criterion: Criterion = {
						operatorName:
							newOperatorName as unknown as Criterion['operatorName'],
					};

					if (
						(newOperatorName === FunctionalOperators.Between &&
							operatorName !== FunctionalOperators.Between) ||
						(newOperatorName !== FunctionalOperators.Between &&
							operatorName === FunctionalOperators.Between)
					) {
						criterion = {
							...criterion,
							value: getDefaultAttributeValue(
								dataType,
								newOperatorName as
									| FunctionalOperators
									| RelationalOperators
							),
						};
					}

					onChange({criterion});
				}}
				selectedKey={operatorName}
			>
				{({label, value}) => <Option key={value}>{label}</Option>}
			</Picker>
		</Form.GroupItem>
	);
};

export default OperatorSelect;
