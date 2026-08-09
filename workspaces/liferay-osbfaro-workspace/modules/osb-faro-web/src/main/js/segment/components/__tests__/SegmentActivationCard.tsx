import mockStore from 'test/mock-store';
import React from 'react';
import SegmentActivationCard from '../SegmentActivationCard';
import {BrowserRouter} from 'react-router-dom';
import {fireEvent, render} from '@testing-library/react';
import {fromJS} from 'immutable';
import {Provider} from 'react-redux';
import {
	SegmentActivationFrequencyTypes,
	SegmentActivationScheduleTypes,
} from 'shared/util/constants';

jest.unmock('react-dom');

const WrapperComponent: React.FC<{children: React.ReactNode}> = ({
	children,
}) => (
	<Provider store={mockStore()}>
		<BrowserRouter>{children}</BrowserRouter>
	</Provider>
);

jest.mock(
	'shared/components/DateRangeInput',
	() =>
		function MockDateInput({value}: {value: {start: string; end: string}}) {
			return (
				<div data-testid="mock-date-input">
					{value.start} {'-'} {value.end}
				</div>
			);
		}
);

describe('SegmentActivationCard', () => {
	it('should render when frequency type is batch', () => {
		const {container} = render(
			<WrapperComponent>
				<SegmentActivationCard
					segmentActivation={fromJS({
						frequencyType:
							SegmentActivationFrequencyTypes.Indefinitely,
						scheduleType: SegmentActivationScheduleTypes.Batch,
					})}
				/>
			</WrapperComponent>
		);
		expect(container).toMatchSnapshot();
	});

	it('should render with dates when frequency type is between', () => {
		const {container} = render(
			<WrapperComponent>
				<SegmentActivationCard
					segmentActivation={fromJS({
						frequencyType: SegmentActivationFrequencyTypes.Between,
						scheduleEndDate: '1757818800000',
						scheduleStartDate: '1756004400000',
						scheduleType: SegmentActivationScheduleTypes.Batch,
					})}
				/>
			</WrapperComponent>
		);
		expect(container).toMatchSnapshot();
	});

	it('should render the modal if the edit button is clicked', async () => {
		const {findByTestId, findByText, getByTestId} = render(
			<WrapperComponent>
				<SegmentActivationCard
					segmentActivation={fromJS({
						frequencyType:
							SegmentActivationFrequencyTypes.Indefinitely,
						scheduleType: SegmentActivationScheduleTypes.Batch,
					})}
				/>
			</WrapperComponent>
		);

		const editButton = getByTestId('edit-activation-button');
		fireEvent.click(editButton);

		const modal = await findByTestId('segment-activation-modal');
		expect(modal).toBeInTheDocument();

		const modalHeader = await findByText('Configure Activation');
		expect(modalHeader).toBeInTheDocument();
	});

	it('should render the modal with the expected configurations - indefinitely', async () => {
		const {findByTestId, findByText, getByTestId} = render(
			<WrapperComponent>
				<SegmentActivationCard
					segmentActivation={fromJS({
						frequencyType:
							SegmentActivationFrequencyTypes.Indefinitely,
						scheduleType: SegmentActivationScheduleTypes.Batch,
					})}
				/>
			</WrapperComponent>
		);

		const editButton = getByTestId('edit-activation-button');
		fireEvent.click(editButton);

		const modal = await findByTestId('segment-activation-modal');
		expect(modal).toBeInTheDocument();

		expect(await findByText('Configure Activation')).toBeInTheDocument();

		expect(await findByText('indefinitely')).toBeInTheDocument();
	});

	it('should render the modal with the expected configurations - between', async () => {
		const {findByTestId, findByText, getByTestId} = render(
			<WrapperComponent>
				<SegmentActivationCard
					segmentActivation={fromJS({
						frequencyType: SegmentActivationFrequencyTypes.Between,
						scheduleEndDate: '1757818800000',
						scheduleStartDate: '1756004400000',
						scheduleType: SegmentActivationScheduleTypes.Batch,
					})}
				/>
			</WrapperComponent>
		);

		const editButton = getByTestId('edit-activation-button');
		fireEvent.click(editButton);

		const modal = await findByTestId('segment-activation-modal');
		expect(modal).toBeInTheDocument();

		expect(await findByText('Configure Activation')).toBeInTheDocument();

		expect(await findByText('between')).toBeInTheDocument();

		expect(getByTestId('mock-date-input')).toBeInTheDocument();
	});
});
