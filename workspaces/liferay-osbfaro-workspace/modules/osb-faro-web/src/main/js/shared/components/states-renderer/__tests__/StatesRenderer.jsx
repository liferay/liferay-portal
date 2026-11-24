import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import {cleanup, render} from '@testing-library/react';
import {
	mockEmptyState,
	mockErrorState,
	mockLoadingState,
	mockSuccessState
} from 'test/__mocks__/mock-objects';

const emptyState = {
	description: 'empty state description',
	title: 'empty state title'
};

const MockComponent = ({...states}) => (
	<StatesRenderer {...states}>
		<StatesRenderer.Empty
			description={emptyState.description}
			title={emptyState.title}
		/>

		<StatesRenderer.Empty>
			<NoResultsDisplay
				description={emptyState.description}
				title={emptyState.title}
			/>
		</StatesRenderer.Empty>

		<StatesRenderer.Error>
			<p>{'Error State'}</p>
		</StatesRenderer.Error>

		<StatesRenderer.Loading />

		<StatesRenderer.Loading>
			<p>{'Loading State'}</p>
		</StatesRenderer.Loading>

		<StatesRenderer.Success>
			<p>{'Success State'}</p>
		</StatesRenderer.Success>
	</StatesRenderer>
);

describe('StatesRenderer', () => {
	afterEach(cleanup);

	it('should render empty states', () => {
		const {container} = render(<MockComponent {...mockEmptyState} />);

		const emptyTitle = container.querySelectorAll('.no-results-title');
		const emptyDescription = container.querySelectorAll(
			'.no-results-description'
		);

		expect(emptyTitle).toHaveLength(2);

		expect(emptyTitle[0]).toHaveTextContent(emptyState.title);
		expect(emptyTitle[1]).toHaveTextContent(emptyState.title);

		expect(emptyDescription[0]).toHaveTextContent(emptyState.description);
		expect(emptyDescription[1]).toHaveTextContent(emptyState.description);
	});

	it('should render error state', () => {
		const {getByText} = render(<MockComponent {...mockErrorState} />);

		expect(getByText('Error State')).toBeInTheDocument();
	});

	it('should render loading states', async () => {
		const {container, getByText} = render(
			<MockComponent {...mockLoadingState} />
		);

		expect(container.querySelector('.loading-root')).toBeInTheDocument();
		expect(getByText('Loading State')).toBeInTheDocument();
	});

	it('should render success states', async () => {
		const {getByText} = render(<MockComponent {...mockSuccessState} />);

		expect(getByText('Success State')).toBeInTheDocument();
	});
});
