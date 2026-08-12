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

		expect(screen.getByText('Group By')).toBeInTheDocument();
		expect(
			screen.getByRole('combobox', {name: 'Group By'})
		).toHaveTextContent('Views');
	});

	it('should offer every metric it is given', () => {
		renderGroupByPicker();

		fireEvent.click(screen.getByRole('combobox', {name: 'Group By'}));

		expect(
			screen.getByRole('option', {name: 'Downloads'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: 'Impressions'})
		).toBeInTheDocument();
		expect(screen.getByRole('option', {name: 'Views'})).toBeInTheDocument();
	});

	it('should not offer a metric it is not given', () => {
		renderGroupByPicker({
			metrics: [GroupByMetric.IMPRESSIONS, GroupByMetric.VIEWS],
		});

		fireEvent.click(screen.getByRole('combobox', {name: 'Group By'}));

		expect(screen.queryByRole('option', {name: 'Downloads'})).toBeNull();
	});

	it('should report the metric the user picks', () => {
		const onGroupByChange = jest.fn();

		renderGroupByPicker({onGroupByChange});

		fireEvent.click(screen.getByRole('combobox', {name: 'Group By'}));

		fireEvent.click(screen.getByRole('option', {name: 'Views'}));

		expect(onGroupByChange).toHaveBeenCalledWith(GroupByMetric.VIEWS);
	});
});
