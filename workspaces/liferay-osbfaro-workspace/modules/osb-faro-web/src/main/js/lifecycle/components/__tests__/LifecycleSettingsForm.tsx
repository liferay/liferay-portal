import LifecycleSettingsForm from '../LifecycleSettingsForm';
import React from 'react';
import {MemoryRouter} from 'react-router-dom';
import {render, screen} from '@testing-library/react';
import {resetBodyWidth, setBodyWidth} from 'test/body-width';
import {useLifecycleSettingsForm} from 'lifecycle/hooks/useLifecycleSettingsForm';
import {useRequest} from 'shared/hooks/useRequest';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

jest.mock('react-redux', () => ({
	useDispatch: () => jest.fn(),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '123', groupId: '23'}),
}));

jest.mock('shared/hooks/useUnsavedChangesPrompt', () => ({
	useUnsavedChangesPrompt: jest.fn(),
}));

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({timeZoneId: 'UTC'}),
}));

jest.mock('shared/hooks/useHistoryAdapter', () => ({
	useHistoryAdapter: () => ({push: jest.fn()}),
}));

const LifecycleSettingsFormHarness = () => {
	const form = useLifecycleSettingsForm({
		description: '',
		name: 'Customer Lifecycle',
		stageConfigs: () => [],
	});

	return (
		<LifecycleSettingsForm
			authorName="Anneta Smith"
			form={form}
			onSubmit={jest.fn()}
			submitLabel="Save"
			title="Edit Lifecycle"
		/>
	);
};

describe('LifecycleSettingsForm', () => {
	beforeEach(() => {
		setBodyWidth(1440);

		(useRequest as jest.Mock).mockReturnValue({data: [], loading: false});
	});

	afterEach(() => resetBodyWidth());

	it('places the form in the BaseEditPage main content region', () => {
		render(
			<MemoryRouter>
				<LifecycleSettingsFormHarness />
			</MemoryRouter>
		);

		const main = screen
			.getByDisplayValue('Customer Lifecycle')
			.closest('.base-edit-page-main');

		expect(main).not.toBeNull();
		expect(main?.parentElement).toHaveClass('base-edit-page-body');
	});

	it('places the lifecycle conditions panel in the sidebar', () => {
		render(
			<MemoryRouter>
				<LifecycleSettingsFormHarness />
			</MemoryRouter>
		);

		expect(
			screen
				.getByRole('heading', {name: /lifecycle conditions/i})
				.closest('.base-edit-page-sidebar')
		).not.toBeNull();
	});
});
