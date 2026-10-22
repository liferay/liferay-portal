import React from 'react';
import variantColumns from '../variant-columns';
import {render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const renderCell = (accessor: string, data: object) => {
	const column = variantColumns({
		bestVariant: null,
		metric: 'BOUNCE_RATE',
		metricUnit: '%',
		publishedDXPVariantId: null,
		status: 'RUNNING',
		type: 'AB',
		winnerDXPVariantId: null,
	}).find((column) => column.accessor === accessor);

	const CellRenderer = column!.cellRenderer as React.FC<{data: object}>;

	return render(
		<table>
			<tbody>
				<tr>
					<CellRenderer data={data} />
				</tr>
			</tbody>
		</table>
	);
};

describe('variantColumns', () => {
	describe('median', () => {
		it('should render the median', () => {
			const {container} = renderCell('median', {median: 12.5});

			expect(container.textContent).toBe('12.50%');
		});

		it('should render a zero median', () => {
			const {container} = renderCell('median', {median: 0});

			expect(container.textContent).toBe('0%');
		});

		it('should render no data when the median is missing', () => {
			const {container} = renderCell('median', {});

			expect(container.textContent).toBe('–');
			expect(screen.getByText('–')).toHaveClass('text-secondary');
		});
	});

	describe('confidenceInterval', () => {
		it('should render the confidence interval', () => {
			const {container} = renderCell('confidenceInterval', {
				confidenceInterval: [10, 20],
			});

			expect(container.textContent).toBe('10% - 20%');
		});

		it('should render no data when the confidence interval is missing', () => {
			const {container} = renderCell('confidenceInterval', {
				confidenceInterval: [],
			});

			expect(container.textContent).toBe('–');
			expect(screen.getByText('–')).toHaveClass('text-secondary');
		});
	});

	describe('improvement', () => {
		it('should render a lift', () => {
			const {container} = renderCell('improvement', {improvement: 12.5});

			expect(container).toHaveTextContent('12.5% lift');
		});

		it('should render a zero improvement as zero', () => {
			const {container} = renderCell('improvement', {improvement: 0});

			expect(container.textContent).toBe('0%');
		});

		it('should render no data when the improvement is missing', () => {
			const {container} = renderCell('improvement', {});

			expect(container.textContent).toBe('–');
			expect(screen.getByText('–')).toHaveClass('text-secondary');
		});
	});

	describe('probabilityToWin', () => {
		it('should render the probability to win', () => {
			const {container} = renderCell('probabilityToWin', {
				probabilityToWin: 50.4321,
			});

			expect(container).toHaveTextContent('50.4%');
		});

		it('should render a zero probability to win as zero', () => {
			const {container} = renderCell('probabilityToWin', {
				probabilityToWin: 0,
			});

			expect(container.textContent).toBe('0%');
		});

		it('should render no data when the probability to win is missing', () => {
			const {container} = renderCell('probabilityToWin', {
				probabilityToWin: null,
			});

			expect(container.textContent).toBe('–');
			expect(screen.getByText('–')).toHaveClass('text-secondary');
		});
	});
});
