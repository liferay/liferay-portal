import AccountMetricsCard from '../AccountMetricsCard';
import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const renderAccountMetricsCard = (props = {}) =>
	render(
		<AccountMetricsCard
			metrics={[{label: '{0} Individuals', value: 1234}]}
			title="Total Individuals"
			{...props}
		/>
	);

describe('AccountMetricsCard', () => {
	afterEach(cleanup);

	describe('rendering', () => {
		it('should render the title in uppercase', () => {
			renderAccountMetricsCard();

			expect(screen.getByText('TOTAL INDIVIDUALS')).toBeInTheDocument();
		});

		it('should render the metric label with the value subbed in', () => {
			const {container} = renderAccountMetricsCard();

			expect(container).toHaveTextContent('1.2K Individuals');
		});

		it('should abbreviate the value to thousands', () => {
			renderAccountMetricsCard({
				metrics: [{label: '{0} Individuals', value: 2500000}],
			});

			expect(screen.getByText('2.5M')).toBeInTheDocument();
		});

		it('should render the value as is when it is under a thousand', () => {
			renderAccountMetricsCard({
				metrics: [{label: '{0} Individuals', value: 842}],
			});

			expect(screen.getByText('842')).toBeInTheDocument();
		});

		it('should render every metric of the card', () => {
			const {container} = renderAccountMetricsCard({
				metrics: [
					{label: '{0} Known', value: 800},
					{label: '{0} Anonymous', value: 434},
				],
			});

			expect(container).toHaveTextContent('800 Known');
			expect(container).toHaveTextContent('434 Anonymous');
		});

		it('should render zero when the metric has no value', () => {
			renderAccountMetricsCard({
				metrics: [{label: '{0} Individuals'}],
			});

			expect(screen.getByText('0')).toBeInTheDocument();
		});

		it('should render zero rather than an empty card when the value is zero', () => {
			renderAccountMetricsCard({
				metrics: [{label: '{0} No Activity', value: 0}],
			});

			expect(screen.getByText('0')).toBeInTheDocument();
		});

		it('should render the singular label when the value is one', () => {
			const {container} = renderAccountMetricsCard({
				metrics: [
					{
						label: '{0} Individuals',
						singularLabel: '{0} Individual',
						value: 1,
					},
				],
			});

			expect(container).toHaveTextContent('1 Individual');
			expect(container).not.toHaveTextContent('1 Individuals');
		});

		it('should render the plural label for any other value', () => {
			const {container} = renderAccountMetricsCard({
				metrics: [
					{
						label: '{0} Individuals',
						singularLabel: '{0} Individual',
						value: 2,
					},
				],
			});

			expect(container).toHaveTextContent('2 Individuals');
		});

		it('should render the plural label for zero', () => {
			const {container} = renderAccountMetricsCard({
				metrics: [
					{
						label: '{0} Individuals',
						singularLabel: '{0} Individual',
						value: 0,
					},
				],
			});

			expect(container).toHaveTextContent('0 Individuals');
		});

		it('should render the plural label when the value is missing', () => {
			const {container} = renderAccountMetricsCard({
				metrics: [
					{
						label: '{0} Individuals',
						singularLabel: '{0} Individual',
					},
				],
			});

			expect(container).toHaveTextContent('0 Individuals');
		});

		it('should fall back to the label when there is no singular form', () => {
			const {container} = renderAccountMetricsCard({
				metrics: [{label: '{0} Known', value: 1}],
			});

			expect(container).toHaveTextContent('1 Known');
		});

		it('should render no metric when the list is empty', () => {
			const {container} = renderAccountMetricsCard({metrics: []});

			expect(screen.getByText('TOTAL INDIVIDUALS')).toBeInTheDocument();
			expect(container.querySelectorAll('.text-nowrap')).toHaveLength(0);
		});
	});

	describe('error', () => {
		it('should render the error message instead of the metrics', () => {
			renderAccountMetricsCard({error: true});

			expect(
				screen.getByText('An unexpected error occurred.')
			).toBeInTheDocument();
			expect(screen.queryByText('1.2K')).not.toBeInTheDocument();
		});

		it('should not render zeroed metrics when the request failed', () => {
			renderAccountMetricsCard({
				error: true,
				metrics: [{label: '{0} Individuals'}],
			});

			expect(screen.queryByText('0')).not.toBeInTheDocument();
		});

		it('should keep the title visible on error', () => {
			renderAccountMetricsCard({error: true});

			expect(screen.getByText('TOTAL INDIVIDUALS')).toBeInTheDocument();
		});

		it('should render the metrics when there is no error', () => {
			renderAccountMetricsCard({error: false});

			expect(
				screen.queryByText('An unexpected error occurred.')
			).not.toBeInTheDocument();
			expect(screen.getByText('1.2K')).toBeInTheDocument();
		});

		// `useRequest` keeps `error` set while a refetch is in flight, so both
		// flags can be true at once. Loading is checked first and wins.

		it('should favor the loading indicator while a failed request is refetching', () => {
			const {container} = renderAccountMetricsCard({
				error: true,
				loading: true,
			});

			expect(
				container.querySelector('.loading-root')
			).toBeInTheDocument();
			expect(
				screen.queryByText('An unexpected error occurred.')
			).not.toBeInTheDocument();
		});

		it('should offer a reload action that refetches the metrics', () => {
			const refetch = jest.fn();

			renderAccountMetricsCard({error: true, refetch});

			fireEvent.click(screen.getByText('Reload'));

			expect(refetch).toHaveBeenCalledTimes(1);
		});

		it('should render no reload action when there is nothing to refetch', () => {
			renderAccountMetricsCard({error: true});

			expect(
				screen.getByText('An unexpected error occurred.')
			).toBeInTheDocument();
			expect(screen.queryByText('Reload')).not.toBeInTheDocument();
		});
	});

	describe('loading', () => {
		it('should render the loading indicator instead of the metrics', () => {
			const {container} = renderAccountMetricsCard({loading: true});

			expect(
				container.querySelector('.loading-root')
			).toBeInTheDocument();
			expect(screen.queryByText('1.2K')).not.toBeInTheDocument();
		});

		it('should keep the title visible while loading', () => {
			renderAccountMetricsCard({loading: true});

			expect(screen.getByText('TOTAL INDIVIDUALS')).toBeInTheDocument();
		});

		it('should render the metrics when it is not loading', () => {
			const {container} = renderAccountMetricsCard({loading: false});

			expect(container.querySelector('.loading-root')).toBeNull();
			expect(screen.getByText('1.2K')).toBeInTheDocument();
		});
	});

	describe('styling', () => {
		it('should stretch the card to fill its column', () => {
			const {container} = renderAccountMetricsCard();

			expect(container.querySelector('.card-root')).toHaveClass(
				'flex-fill',
				'p-3',
				'w-100'
			);
		});

		it('should keep the class name passed by the consumer', () => {
			const {container} = renderAccountMetricsCard({
				className: 'custom-card',
			});

			expect(container.querySelector('.card-root')).toHaveClass(
				'custom-card',
				'flex-fill'
			);
		});

		it('should render the body without the default card padding', () => {
			const {container} = renderAccountMetricsCard();

			expect(container.querySelector('.card-body')).toHaveClass(
				'no-padding'
			);
		});

		it('should spread the metrics across the card body', () => {
			const {container} = renderAccountMetricsCard({
				metrics: [
					{label: '{0} Known', value: 800},
					{label: '{0} Anonymous', value: 434},
				],
			});

			expect(container.querySelector('.card-body > .d-flex')).toHaveClass(
				'justify-content-between'
			);
			expect(container.querySelectorAll('.text-nowrap')).toHaveLength(2);
		});
	});
});
