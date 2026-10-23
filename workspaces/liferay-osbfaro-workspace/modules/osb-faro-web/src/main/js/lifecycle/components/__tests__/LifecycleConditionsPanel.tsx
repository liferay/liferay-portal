import LifecycleConditionsPanel from '../LifecycleConditionsPanel';
import React from 'react';
import {
	createStageCondition,
	IStageCondition,
	IStageConfig,
	MatchLogic,
} from 'lifecycle/utils/stageConfiguration';
import {LIFECYCLE_STAGE_DEFAULT_MAX_DAYS} from 'lifecycle/utils/constants';
import {fireEvent, render, screen, within} from '@testing-library/react';

jest.unmock('react-dom');

const completeCondition: Partial<IStageCondition> = {
	conditionValue: '1000',
	field: 'account.annualRevenue',
	fieldDataCategory: 'Number',
	fieldDataType: 'NUMERIC',
	operator: 'gt',
};

const buildStage = (
	key: string,
	name: string,
	conditions: Partial<IStageCondition>[] = [{}]
): IStageConfig => ({
	conditions: conditions.map((condition) => ({
		...createStageCondition(),
		...condition,
	})),
	description: 'A stage',
	id: null,
	key,
	matchLogic: MatchLogic.All,
	maxTimeDays: LIFECYCLE_STAGE_DEFAULT_MAX_DAYS,
	maxTimeEnabled: true,
	name,
});

const STAGES = [
	buildStage('aware', 'Aware', [completeCondition, completeCondition]),
	buildStage('qualified', 'Qualified', [completeCondition]),
	buildStage('pipeline', 'Pipeline'),
];

const renderPanel = (
	props: Partial<React.ComponentProps<typeof LifecycleConditionsPanel>> = {}
) =>
	render(
		<LifecycleConditionsPanel
			lifecycleName="Growth and Retention Hub"
			onAddStage={jest.fn()}
			onMoveStage={jest.fn()}
			onRemoveStage={jest.fn()}
			onSelect={jest.fn()}
			selectedKey={null}
			stageConfigs={STAGES}
			stageErrorKeys={[]}
			summaryError={false}
			{...props}
		/>
	);

describe('LifecycleConditionsPanel', () => {
	it('renders the configuration summary and the structure of stages', () => {
		renderPanel();

		expect(
			screen.getByRole('heading', {name: /lifecycle conditions/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole('group', {name: /configuration summary/i})
		).toHaveTextContent(/growth and retention hub/i);
		expect(
			screen.getByRole('region', {name: /structure/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: /add.stage/i})
		).toBeInTheDocument();
	});

	it('numbers each stage and shows its trigger status', () => {
		const {container} = renderPanel();

		expect(screen.getByRole('group', {name: /^aware$/i})).toHaveTextContent(
			/stage 1.+2 conditions/i
		);
		expect(
			screen.getByRole('group', {name: /^qualified$/i})
		).toHaveTextContent(/stage 2.+1 condition/i);
		expect(
			screen.getByRole('group', {name: /^pipeline$/i})
		).toHaveTextContent(/stage 3.+no trigger/i);
		expect(container.querySelectorAll('.sticker-success')).toHaveLength(2);
		expect(container.querySelectorAll('.sticker-warning')).toHaveLength(1);
	});

	it('shows the green check only once the stage is fully configured', () => {
		const {container} = renderPanel({
			stageConfigs: [
				{
					...buildStage('aware', 'Aware', [completeCondition]),
					description: '',
				},
				buildStage('qualified', '', [completeCondition]),
				buildStage('pipeline', 'Pipeline', [
					completeCondition,
					{field: 'account.annualRevenue'},
				]),
			],
		});

		expect(container.querySelector('.sticker-success')).toBeNull();
		expect(container.querySelectorAll('.sticker-warning')).toHaveLength(3);
		expect(screen.getByRole('group', {name: /^aware$/i})).toHaveTextContent(
			/stage 1.+1 condition/i
		);
	});

	it('gives every stage chip a drag handle and a remove button', () => {
		renderPanel();

		const chip = screen.getByRole('group', {name: /^aware$/i});

		expect(
			within(chip).getByRole('button', {name: /drag.aware/i})
		).toBeInTheDocument();
		expect(
			within(chip).getByRole('button', {name: /remove.aware/i})
		).toBeInTheDocument();
	});

	it('keeps the configuration summary sticker when selected, but not the stage stickers', () => {
		renderPanel({selectedKey: null});

		expect(
			screen.getByRole('group', {name: /configuration summary/i})
		).toHaveClass('condition-chip-static-sticker');
		expect(screen.getByRole('group', {name: /^aware$/i})).not.toHaveClass(
			'condition-chip-static-sticker'
		);
	});

	it('cannot remove the configuration summary', () => {
		renderPanel();

		expect(
			within(
				screen.getByRole('group', {name: /configuration summary/i})
			).queryByRole('button')
		).toBeNull();
	});

	it('names an unnamed stage by its number', () => {
		renderPanel({stageConfigs: [buildStage('new', '')]});

		expect(
			screen.getByRole('group', {name: /^stage 1$/i})
		).toBeInTheDocument();
	});

	it('marks the selected item as current', () => {
		const {rerender} = renderPanel();

		expect(
			screen.getByRole('group', {name: /configuration summary/i})
		).toHaveAttribute('aria-current', 'true');

		rerender(
			<LifecycleConditionsPanel
				lifecycleName="Growth and Retention Hub"
				onAddStage={jest.fn()}
				onMoveStage={jest.fn()}
				onRemoveStage={jest.fn()}
				onSelect={jest.fn()}
				selectedKey="qualified"
				stageConfigs={STAGES}
				stageErrorKeys={[]}
				summaryError={false}
			/>
		);

		expect(
			screen.getByRole('group', {name: /^qualified$/i})
		).toHaveAttribute('aria-current', 'true');
		expect(
			screen.getByRole('group', {name: /configuration summary/i})
		).not.toHaveAttribute('aria-current');
	});

	it('selects a stage or the summary', () => {
		const onSelect = jest.fn();

		renderPanel({onSelect});

		fireEvent.click(screen.getByRole('group', {name: /^pipeline$/i}));

		expect(onSelect).toHaveBeenLastCalledWith('pipeline');

		fireEvent.click(
			screen.getByRole('group', {name: /configuration summary/i})
		);

		expect(onSelect).toHaveBeenLastCalledWith(null);
	});

	it('shows an error icon in place of a name when asked', () => {
		renderPanel({stageErrorKeys: ['aware'], summaryError: true});

		const summary = screen.getByRole('group', {
			name: /configuration summary/i,
		});
		const aware = screen.getByRole('group', {name: /^aware$/i});

		expect(summary).not.toHaveTextContent(/growth and retention hub/i);
		expect(summary).toHaveTextContent(/error/i);
		expect(
			aware.querySelector('.lexicon-icon-exclamation-full')
		).toBeInTheDocument();
	});

	it('shows the empty state without stages and adds one from it', () => {
		const onAddStage = jest.fn();

		renderPanel({onAddStage, stageConfigs: []});

		expect(screen.getByText(/no stages yet/i)).toBeInTheDocument();
		expect(
			screen.getByText(/add stages to define its structure/i)
		).toBeInTheDocument();

		fireEvent.click(screen.getByRole('button', {name: /^new$/i}));

		expect(onAddStage).toHaveBeenCalledTimes(1);
		expect(screen.getByRole('status')).toHaveTextContent(/added.+stage 1/i);
	});

	it('adds a stage from the structure button', () => {
		const onAddStage = jest.fn();

		renderPanel({onAddStage});

		expect(screen.queryByText(/no stages yet/i)).toBeNull();

		fireEvent.click(screen.getByRole('button', {name: /add.stage/i}));

		expect(onAddStage).toHaveBeenCalledTimes(1);
		expect(screen.getByRole('status')).toHaveTextContent(/added.+stage 4/i);
	});

	it('removes a stage and announces it', () => {
		const onRemoveStage = jest.fn();

		renderPanel({onRemoveStage});

		fireEvent.click(
			screen.getByRole('button', {name: /remove.qualified/i})
		);

		expect(onRemoveStage).toHaveBeenCalledWith('qualified');
		expect(screen.getByRole('status')).toHaveTextContent(
			/removed.+qualified/i
		);
	});

	it('moves a stage with the keyboard and announces where it lands', () => {
		const onMoveStage = jest.fn();

		renderPanel({onMoveStage});

		const handle = screen.getByRole('button', {name: /drag.aware/i});

		handle.focus();

		fireEvent.keyDown(handle, {key: 'Enter'});
		fireEvent.keyDown(handle, {key: 'ArrowDown'});
		fireEvent.keyDown(handle, {key: 'Enter'});

		expect(onMoveStage).toHaveBeenCalledWith({from: 0, to: 1});
		expect(screen.getByRole('status')).toHaveTextContent(/aware.+2.+3/i);
	});
});
