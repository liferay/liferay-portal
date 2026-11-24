import React from 'react';
import {createOrderIOMap, getSortFromOrderIOMap} from 'shared/util/pagination';
import {MemoryRouter} from 'react-router-dom';
import {render} from '@testing-library/react';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';

const DefaultComponent = ({delta, orderIOMap, page, query}) => {
	const {column, type} = getSortFromOrderIOMap(orderIOMap);

	return (
		<>
			{`delta: ${delta}`}

			{`field: ${column}`}

			{`sortOrder: ${type}`}

			{`page: ${page}`}

			{`query: ${query}`}
		</>
	);
};
const WrapperComponent = ({children, queryString = ''}) => (
	<MemoryRouter
		initialEntries={[
			`/workspace/23/321321/contacts/accounts/123123/interests/test${queryString}`,
		]}
	>
		{children}
	</MemoryRouter>
);

describe('useQueryPagination', () => {
	it('should return initial values', () => {
		const TestComponent = () => {
			const queryPaginationParams = useQueryPagination({
				initialDelta: 6,
				initialOrderIOMap: createOrderIOMap('initial orderIOMap'),
				initialPage: 13,
				initialQuery: 'initial query',
			});

			return <DefaultComponent {...queryPaginationParams} />;
		};

		const {container} = render(
			<WrapperComponent>
				<TestComponent />
			</WrapperComponent>
		);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});

	it('should return pagination params from query params values', () => {
		const TestComponent = () => {
			const queryPaginationParams = useQueryPagination({
				initialDelta: 6,
				initialOrderIOMap: createOrderIOMap('initial orderIOMap'),
				initialPage: 13,
				initialQuery: 'initial query',
			});

			return <DefaultComponent {...queryPaginationParams} />;
		};

		const {container} = render(
			<WrapperComponent queryString="?delta=5&field=name&sortOrder=DESC&query=meow&page=1">
				<TestComponent />
			</WrapperComponent>
		);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});
});
