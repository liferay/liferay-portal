import LifecycleSettingsForm from '../LifecycleSettingsForm';
import React from 'react';
import {
	createStageCondition,
	DEFAULT_MAX_DAYS,
	IStageConfig,
	LIFECYCLE_STAGE_ORDER,
	MatchLogic,
} from 'lifecycle/utils/stageConfiguration';
import {MemoryRouter} from 'react-router-dom';
import {render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const stageConfig: IStageConfig = {
	conditions: [createStageCondition()],
	description: '',
	id: null,
	matchLogic: MatchLogic.All,
	maxTimeDays: DEFAULT_MAX_DAYS,
	maxTimeEnabled: true,
};

describe('LifecycleSettingsForm', () => {
	it('places the form in the BaseEditPage main content region', () => {
		render(
			<MemoryRouter>
				<LifecycleSettingsForm
					backURL="/lifecycle"
					lifecycleName="Customer Lifecycle"
					onLifecycleNameChange={jest.fn()}
					onStageChange={jest.fn()}
					onSubmit={jest.fn()}
					stageConfigs={LIFECYCLE_STAGE_ORDER.map(() => stageConfig)}
					submitLabel="Save"
				/>
			</MemoryRouter>
		);

		const main = screen
			.getByText('Stage Configuration')
			.closest('.base-edit-page-main');

		expect(main).toContainElement(
			screen.getByDisplayValue('Customer Lifecycle')
		);
		expect(main?.parentElement).toHaveClass('base-edit-page-body');
	});
});
