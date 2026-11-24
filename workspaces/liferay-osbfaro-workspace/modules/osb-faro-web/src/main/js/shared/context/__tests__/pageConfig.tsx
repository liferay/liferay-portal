import React from 'react';
import {buildConfig} from 'shared/util/config';
import {PageConfigProvider, usePageConfig} from '../pageConfig';
import {render, renderHook} from '@testing-library/react';

const ChildComponent = () => {
	const {groupId} = usePageConfig();

	return <p>{groupId}</p>;
};

describe('PageConfigProvider', () => {
	it('gives its children the config that it receives', () => {
		const config = buildConfig({groupId: '23', params: {id: '123'}});

		const {result} = renderHook(() => usePageConfig(), {
			wrapper: ({children}) => (
				<PageConfigProvider config={config}>
					{children}
				</PageConfigProvider>
			),
		});

		expect(result.current).toBe(config);
	});

	it('throws when a component reads the config outside of it', () => {
		jest.spyOn(console, 'error').mockImplementation(() => {});

		expect(() => render(<ChildComponent />)).toThrow(
			'usePageConfig must be used within a PageConfigProvider'
		);

		jest.restoreAllMocks();
	});
});
