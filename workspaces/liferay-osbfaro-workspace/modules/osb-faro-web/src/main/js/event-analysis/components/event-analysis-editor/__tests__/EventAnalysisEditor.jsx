import EventAnalysisEditor from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {CalculationTypes} from 'event-analysis/utils/types';
import {fireEvent, render} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

const renderEditor = (props) =>
	render(
		<Provider store={mockStore()}>
			<MemoryRouter>
				<RouterRoutes>
					<Route
						element={
							<MockedProvider
								cache={
									new InMemoryCache({
										addTypename: false,
										freezeResults: false
									})
								}
							>
								<EventAnalysisEditor
									type={CalculationTypes.Total}
									{...props}
								/>
							</MockedProvider>
						}
						path='/*'
					/>
				</RouterRoutes>
			</MemoryRouter>
		</Provider>
	);

describe('Event Analysis Editor', () => {
	it('render', () => {
		const {container, getByText} = renderEditor();

		expect(container).toMatchSnapshot();

		expect(
			container.querySelector('.canvas-root.event-analysis-editor-root')
		).toBeInTheDocument();
		expect(getByText('Analysis Insights')).toBeInTheDocument();
		expect(getByText('No Conditions Yet')).toBeInTheDocument();
		expect(
			getByText(
				'To create a new analysis, select an event, then add filters and breakdowns for more detail.'
			)
		).toBeInTheDocument();
	});

	it('marks the selected calculation type as the active button', () => {
		const {getByRole} = renderEditor({type: CalculationTypes.Unique});

		const uniqueButton = getByRole('button', {name: 'Unique'});

		expect(uniqueButton).toHaveClass('active');
		expect(uniqueButton).toHaveAttribute('aria-pressed', 'true');

		for (const name of ['Average', 'Total']) {
			const button = getByRole('button', {name});

			expect(button).not.toHaveClass('active');
			expect(button).toHaveAttribute('aria-pressed', 'false');
		}
	});

	it('changes the calculation type when a button is clicked', () => {
		const onTypeChange = jest.fn();

		const {getByRole} = renderEditor({onTypeChange});

		fireEvent.click(getByRole('button', {name: 'Average'}));

		expect(onTypeChange).toHaveBeenCalledWith(CalculationTypes.Average);
	});

	it('toggles the comparison with the previous period', () => {
		const onCompareToPreviousChange = jest.fn();

		const {getByLabelText} = renderEditor({
			compareToPrevious: false,
			onCompareToPreviousChange,
		});

		const checkbox = getByLabelText('Compare to Previous');

		expect(checkbox).not.toBeChecked();

		fireEvent.click(checkbox);

		expect(onCompareToPreviousChange).toHaveBeenCalledWith(true);
	});

	it('keeps the comparison and the date range together so they wrap as one', () => {
		const {getByLabelText} = renderEditor();

		const checkbox = getByLabelText('Compare to Previous').closest(
			'.compare-to-previous-checkbox'
		);

		const group = checkbox.parentElement;

		expect(group).toHaveClass('event-analysis-editor-period');
		expect(group.children).toHaveLength(2);
		expect(group.firstElementChild).toBe(checkbox);
	});
});
