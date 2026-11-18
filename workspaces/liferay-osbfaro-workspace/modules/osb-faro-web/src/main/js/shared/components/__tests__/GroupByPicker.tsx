import GroupByPicker, {GROUP_BY_METRICS, GroupByMetric} from '../GroupByPicker';
import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const renderGroupByPicker = ({
	metrics = GROUP_BY_METRICS,
	onGroupByChange = jest.fn(),
	value = GroupByMetric.IMPRESSIONS,
}: {
	metrics?: GroupByMetric[];
	onGroupByChange?: (metric: GroupByMetric) => void;
	value?: GroupByMetric;
} = {}) =>
	render(
		<GroupByPicker
			metrics={metrics}
			onGroupByChange={onGroupByChange}
			value={value}
		/>
	);

describe('GroupByPicker', () => {
	afterEach(cleanup);

	it('should render the label of the selected metric', () => {
		renderGroupByPicker({value: GroupByMetric.VIEWS});

		expect(screen.getByText(/^group by$/i)).toBeInTheDocument();
		expect(
			screen.getByRole('combobox', {name: /^group by$/i})
		).toHaveTextContent(/views/i);
	});

	it('should offer every metric it is given', () => {
		renderGroupByPicker();

		fireEvent.click(screen.getByRole('combobox', {name: /^group by$/i}));

		expect(
			screen.getByRole('option', {name: /^downloads$/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: /^impressions$/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: /^views$/i})
		).toBeInTheDocument();
	});

	it('should not offer a metric it is not given', () => {
		renderGroupByPicker({
			metrics: [GroupByMetric.IMPRESSIONS, GroupByMetric.VIEWS],
		});

		fireEvent.click(screen.getByRole('combobox', {name: /^group by$/i}));

		expect(screen.queryByRole('option', {name: /^downloads$/i})).toBeNull();
	});

	it('should report the metric the user picks', () => {
		const onGroupByChange = jest.fn();

		renderGroupByPicker({onGroupByChange});

		fireEvent.click(screen.getByRole('combobox', {name: /^group by$/i}));

		fireEvent.click(screen.getByRole('option', {name: /^views$/i}));

		expect(onGroupByChange).toHaveBeenCalledWith(GroupByMetric.VIEWS);
	});
});
