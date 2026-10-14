import BreakdownTable from './event-analysis-breakdown';
import Canvas from 'shared/components/canvas/Canvas';
import ClayButton from '@clayui/button';
import getCN from 'classnames';
import React from 'react';
import {CalculationTypes, Event} from 'event-analysis/utils/types';
import {ClayCheckbox} from '@clayui/form';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {RangeSelectors} from 'shared/types';

const CALCULATION_TYPES = [
	{label: Liferay.Language.get('total'), value: CalculationTypes.Total},
	{label: Liferay.Language.get('unique'), value: CalculationTypes.Unique},
	{label: Liferay.Language.get('average'), value: CalculationTypes.Average},
];

interface IEventAnalysisEditorProps extends React.HTMLAttributes<HTMLElement> {
	channelId: string;
	compareToPrevious: boolean;
	event: Event | null;
	onCompareToPreviousChange: (compareToPrevious: boolean) => void;
	onRangeSelectorsChange: (rangeSelectors: RangeSelectors) => void;
	onTypeChange: (type: CalculationTypes) => void;
	type: CalculationTypes;
	rangeSelectors: RangeSelectors;
}

const EventAnalysisEditor: React.FC<IEventAnalysisEditorProps> = ({
	channelId,
	compareToPrevious,
	event,
	onCompareToPreviousChange,
	onRangeSelectorsChange,
	onTypeChange,
	rangeSelectors,
	type,
}) => (
	<Canvas className="event-analysis-editor-root">
		<Canvas.Header title={Liferay.Language.get('analysis-insights')}>
			<Canvas.Actions>
				<ClayButton.Group className="type-selector">
					{CALCULATION_TYPES.map(({label, value}) => (
						<ClayButton
							aria-pressed={type === value}
							className={getCN({active: type === value})}
							displayType="secondary"
							key={value}
							onClick={() => onTypeChange(value)}
							size="sm"
						>
							{label}
						</ClayButton>
					))}
				</ClayButton.Group>

				<span className="align-self-stretch border-left" />

				<ClayCheckbox
					checked={compareToPrevious}
					containerProps={{
						className: 'compare-to-previous-checkbox mb-0 mt-2',
					}}
					label={Liferay.Language.get('compare-to-previous')}
					onChange={(event) =>
						onCompareToPreviousChange(event.currentTarget.checked)
					}
				/>

				<DropdownRangeKey
					bordered
					legacy={false}
					onRangeSelectorChange={onRangeSelectorsChange}
					rangeSelectors={rangeSelectors}
				/>
			</Canvas.Actions>
		</Canvas.Header>

		<Canvas.Body>
			{event ? (
				<BreakdownTable
					channelId={channelId}
					compareToPrevious={compareToPrevious}
					event={event}
					rangeSelectors={rangeSelectors}
					type={type}
				/>
			) : (
				<Canvas.EmptyState
					description={Liferay.Language.get(
						'to-create-a-new-analysis-select-an-event-then-add-filters-and-breakdowns-for-more-detail'
					)}
					title={Liferay.Language.get('no-conditions-yet')}
				/>
			)}
		</Canvas.Body>
	</Canvas>
);

export default EventAnalysisEditor;
