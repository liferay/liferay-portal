import React from 'react';
import UniqueVisitorsCell from '../UniqueVisitorsCell';
import {render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const renderUniqueVisitorsCell = (props = {}) =>
	render(
		<table>
			<tbody>
				<tr>
					<UniqueVisitorsCell
						trafficSplit={50}
						uniqueVisitors={1200}
						{...props}
					/>
				</tr>
			</tbody>
		</table>
	);

describe('UniqueVisitorsCell', () => {
	it('should render the traffic split', () => {
		const {container} = renderUniqueVisitorsCell();

		expect(container.querySelector('.traffic-quota')).toHaveTextContent(
			'50% Traffic Split'
		);
	});

	it('should render a zero traffic split', () => {
		const {container} = renderUniqueVisitorsCell({trafficSplit: 0});

		expect(container.querySelector('.traffic-quota')).toHaveTextContent(
			'0% Traffic Split'
		);
	});

	it('should render no data when the traffic split is missing', () => {
		const {container} = renderUniqueVisitorsCell({
			trafficSplit: undefined,
		});

		expect(container.querySelector('.traffic-quota')).toHaveTextContent(
			'– Traffic Split'
		);
		expect(screen.getByText('–')).toHaveClass('text-secondary');
	});
});
