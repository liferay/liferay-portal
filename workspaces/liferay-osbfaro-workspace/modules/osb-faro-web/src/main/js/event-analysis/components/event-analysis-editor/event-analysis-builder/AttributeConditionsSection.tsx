import ConditionsSection from 'shared/components/conditions-panel/ConditionsSection';
import React, {useRef} from 'react';
import SortableConditionChip from 'shared/components/condition-chip/SortableConditionChip';
import useKeyboardReorder from 'shared/components/condition-chip/useKeyboardReorder';
import {Attribute, Breakdown, Filter} from 'event-analysis/utils/types';
import {getConditionName} from 'event-analysis/utils/utils';
import {SortableChipTypes} from './SortableChipTypes';
import {useAttributes} from '../context/attributes';

type Condition = Breakdown | Filter;

interface IAttributeConditionsSectionProps<T extends Condition> {
	action: React.ReactNode;
	className: string;
	conditions: {[id: string]: T};
	dragType: SortableChipTypes;
	getDisplay: (attribute: Attribute, condition: T) => [string, string];
	onMove: (params: {from: number; to: number}) => void;
	onRemove: (params: {id: string}) => void;
	order: string[];
	title: string;
}

const AttributeConditionsSection = <T extends Condition>({
	action,
	className,
	conditions,
	dragType,
	getDisplay,
	onMove,
	onRemove,
	order,
	title,
}: IAttributeConditionsSectionProps<T>) => {
	const {attributes} = useAttributes();

	const sectionRef = useRef<HTMLElement>(null);

	const getName = (id: string) =>
		getConditionName(
			attributes[conditions[id].attributeId],
			conditions[id]
		);

	const getKeyboardProps = useKeyboardReorder({
		count: order.length,
		getName: (index) => getName(order[index]),
		onMove,
	});

	return (
		<ConditionsSection
			action={action}
			className={className}
			ref={sectionRef}
			title={title}
		>
			{!!order.length && (
				<div className="attribute-list c-gap-2 d-flex flex-column mt-3">
					{order.map((id, index) => {
						const [overline, label] = getDisplay(
							attributes[conditions[id].attributeId],
							conditions[id]
						);

						return (
							<SortableConditionChip
								dragType={dragType}
								index={index}
								key={id}
								keyboard={getKeyboardProps(index)}
								label={label}
								name={getName(id)}
								onMove={onMove}
								onRemove={() => {
									onRemove({id});

									setTimeout(() =>
										sectionRef.current?.focus()
									);
								}}
								overline={overline}
								sticker={{dataType: conditions[id].dataType}}
							/>
						);
					})}
				</div>
			)}
		</ConditionsSection>
	);
};

export default AttributeConditionsSection;
