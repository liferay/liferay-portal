import Form from 'shared/components/form';
import React from 'react';
import {Conjunctions} from '../utils/constants';
import {Option, Picker} from '@clayui/core';
import {sub} from 'shared/util/lang';

const GROUP_OPERATOR_OPTIONS = [
	{label: Liferay.Language.get('all'), value: Conjunctions.And},
	{label: Liferay.Language.get('any'), value: Conjunctions.Or},
];

const getSelectedOption = (conjunctionName: string) =>
	GROUP_OPERATOR_OPTIONS.find(({value}) => value === conjunctionName) ??
	GROUP_OPERATOR_OPTIONS[0];

/**
 * The operator of a group as a plain sentence, such as "All of these criteria
 * must be met.", used as the accessible name of the group.
 */
export const getGroupOperatorLabel = (conjunctionName: string): string =>
	sub(Liferay.Language.get('x-of-these-criteria-must-be-met'), [
		getSelectedOption(conjunctionName).label,
	]) as string;

interface IGroupOperatorProps {
	conjunctionName: string;
	id: string;
	onChange: (conjunctionName: Conjunctions) => void;
}

/**
 * The "All / Any of these criteria must be met" control the lifecycle stage
 * configuration uses to match its conditions, with the same Picker as the other
 * segment inputs.
 */
const GroupOperator: React.FC<IGroupOperatorProps> = ({
	conjunctionName,
	id,
	onChange,
}) => {
	const selectedOption = getSelectedOption(conjunctionName);

	const parts = sub(
		Liferay.Language.get('x-of-these-criteria-must-be-met'),
		[
			<Picker
				aria-label={Liferay.Language.get('match-logic')}
				className="criterion-input"
				id={id}
				items={GROUP_OPERATOR_OPTIONS}
				key="matchLogic"
				onSelectionChange={(value) => onChange(value as Conjunctions)}
				selectedKey={selectedOption.value}
				shrink
			>
				{({label, value}) => <Option key={value}>{label}</Option>}
			</Picker>,
		],
		false
	) as React.ReactNode[];

	return (
		<div className="group-operator">
			<Form.Group autoFit className="mb-0">
				{parts.map((part, index) =>
					typeof part === 'string' ? (
						part.trim() && (
							<Form.GroupItem
								className="entity-name"
								key={index}
								label
								shrink
							>
								{part.trim()}
							</Form.GroupItem>
						)
					) : (
						<Form.GroupItem key={index} shrink>
							{part}
						</Form.GroupItem>
					)
				)}
			</Form.Group>
		</div>
	);
};

export default GroupOperator;
