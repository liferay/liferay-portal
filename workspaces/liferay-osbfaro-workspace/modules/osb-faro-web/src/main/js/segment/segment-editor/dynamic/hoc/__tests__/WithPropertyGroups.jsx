import * as API from 'shared/api';
import React, {useState} from 'react';
import withPropertyGroups from '../WithPropertyGroups';
import {act, fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const TestComponent = ({propertyGroupsIList}) => (
	<div>
		{propertyGroupsIList &&
			propertyGroupsIList.map((attribute, i) => {
				if (attribute) {
					return (
						<div key={i}>
							{attribute.label}

							{attribute.propertySubgroups.map(
								({label, properties}, i) => (
									<div key={i}>{`${
										label || attribute.label
									}-${i}: ${properties.size}`}</div>
								)
							)}
						</div>
					);
				}
			})}
	</div>
);

describe('WithPropertyGroups', () => {
	it('should pass propertyGroups to the WrappedComponent', async () => {
		API.fieldMappings.search.mockReturnValueOnce(
			Promise.resolve({
				items: [
					{
						context: 'demographics',
						displayName: 'Individual Value',
						id: '123',
						name: 'Individual val',
						ownerType: 'individual',
						rawType: 'Text',
						type: 'Text'
					}
				],
				total: 1
			})
		);

		API.fieldMappings.search.mockReturnValueOnce(
			Promise.resolve({
				items: [
					{
						context: 'custom',
						displayName: 'Individual Custom',
						id: '123',
						name: 'Individual Custom',
						ownerType: 'individual',
						rawType: 'Text',
						type: 'Text'
					}
				],
				total: 1
			})
		);

		API.fieldMappings.search.mockReturnValueOnce(
			Promise.resolve({
				items: [
					{
						context: 'organization',
						displayName: 'Account Value',
						id: '123',
						name: 'Account Value',
						ownerType: 'account',
						rawType: 'Text',
						type: 'Text'
					}
				],
				total: 1
			})
		);

		API.fieldMappings.search.mockReturnValueOnce(
			Promise.resolve({
				items: [
					{
						context: 'account',
						displayName: 'Account Custom Field',
						id: '123',
						name: 'Account Custom',
						ownerType: 'account',
						rawType: 'Text',
						type: 'Text'
					}
				],
				total: 1
			})
		);

		API.fieldMappings.search.mockReturnValueOnce(
			Promise.resolve({
				items: [
					{
						context: 'custom',
						displayName: 'Organization Custom',
						id: '123',
						name: 'Organization Custom',
						ownerType: 'organization',
						rawType: 'Text',
						type: 'Text'
					}
				],
				total: 1
			})
		);

		const WrappedComponent = withPropertyGroups(TestComponent);

		const {container} = render(
			<MemoryRouter>
				<WrappedComponent channelId='123' groupId='123' type='BATCH' />
			</MemoryRouter>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	// The editor is composed onto this HOC, and a remount here takes its
	// `useBlocker` registration with it, so React Router drops a blocked
	// navigation and the unsaved changes prompt never opens. See LPD-104396.

	it('should keep the wrapped component mounted when an ancestor re-renders', async () => {
		API.fieldMappings.search.mockClear();

		const StatefulComponent = () => {
			const [draft, setDraft] = useState('');

			return (
				<input
					aria-label='draft'
					onChange={event => setDraft(event.target.value)}
					value={draft}
				/>
			);
		};

		const WrappedComponent = withPropertyGroups(StatefulComponent);

		const Ancestor = () => {
			const [renderCount, setRenderCount] = useState(0);

			return (
				<div>
					<button onClick={() => setRenderCount(renderCount + 1)}>
						{'re-render'}
					</button>

					<WrappedComponent
						channelId='123'
						groupId='123'
						type='BATCH'
					/>
				</div>
			);
		};

		render(
			<MemoryRouter>
				<Ancestor />
			</MemoryRouter>
		);

		await act(async () => {});

		const requestCount = API.fieldMappings.search.mock.calls.length;

		fireEvent.change(screen.getByLabelText('draft'), {
			target: {value: 'unsaved work'}
		});

		fireEvent.click(screen.getByText('re-render'));

		expect(screen.getByLabelText('draft')).toHaveValue('unsaved work');
		expect(API.fieldMappings.search).toHaveBeenCalledTimes(requestCount);
	});

	describe('Error handling', () => {
		const originalSearch = API.fieldMappings.search;

		beforeEach(() => {
			API.fieldMappings.search = jest.fn(() =>
				Promise.reject(new Error('failed'))
			);
		});

		afterEach(() => {
			API.fieldMappings.search = originalSearch;
		});

		it('should render the error page when the request fails', async () => {
			const WrappedComponent = withPropertyGroups(TestComponent);

			const {container} = render(
				<MemoryRouter>
					<WrappedComponent
						channelId='123'
						groupId='123'
						type='BATCH'
					/>
				</MemoryRouter>
			);

			await act(async () => {});

			expect(container.querySelector('.error-page-root')).toBeTruthy();
		});
	});

	describe('Testing Conditional Requests', () => {
		beforeEach(() => {
			API.interests.searchKeywords = jest.fn();
		});

		test('Should return mocked data when segmentType is Batch', async () => {
			API.interests.searchKeywords.mockReturnValueOnce(
				Promise.resolve({
					items: [
						{id: 'kw1', name: 'Keyword 1'},
						{id: 'kw2', name: 'Keyword 2'},
						{id: 'kw2', name: 'Keyword 3'},
						{id: 'kw2', name: 'Keyword 4'},
						{id: 'kw2', name: 'Keyword 5'}
					],
					total: 5
				})
			);

			const SESSION_PROPERTIES = [
				{key: 'prop1', value: 'value1'},
				{key: 'prop2', value: 'value2'}
			];

			const segmentType = 'BATCH';

			const [keywordsResponse, sessionPropertiesResponse] =
				await Promise.all([
					segmentType === 'BATCH'
						? API.interests.searchKeywords({
								channelId: 123,
								delta: 50,
								groupId: 123
						  })
						: Promise.resolve({items: []}),

					segmentType === 'BATCH'
						? Promise.resolve(SESSION_PROPERTIES)
						: Promise.resolve([])
				]);

			expect(API.interests.searchKeywords).toHaveBeenCalledWith({
				channelId: 123,
				delta: 50,
				groupId: 123
			});

			expect(keywordsResponse.items.length).toBe(5);
			expect(keywordsResponse.items[0].name).toBe('Keyword 1');

			expect(sessionPropertiesResponse).toEqual(SESSION_PROPERTIES);
		});
	});
});
