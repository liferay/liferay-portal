import * as API from 'shared/api';
import AccountSelectInput from '../AccountSelectInput';
import React from 'react';
import {cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import {createCustomValueMap} from '../../utils/custom-inputs';
import {Property} from 'shared/util/records';
import {PropertyTypes} from '../../utils/constants';

jest.unmock('react-dom');

const mockValue = createCustomValueMap([
	{key: 'criterionGroup', value: [{operatorName: 'eq', value: 'erc-stage-1002'}]}
]);

const defaultProps = {
	displayValue: 'Lifecycle Stage',
	groupId: '123',
	onChange: jest.fn(),
	operatorRenderer: () => <div>{'is'}</div>,
	property: new Property({
		entityName: 'Account',
		label: 'Lifecycle Stage',
		name: 'lifecycleStatus',
		type: PropertyTypes.AccountSelectText
	}),
	value: mockValue
};

const DefaultComponent = props => (
	<AccountSelectInput {...defaultProps} {...props} />
);

describe('AccountSelectInput', () => {
	afterEach(cleanup);

	beforeEach(() => {
		API.lifecycle.fetchAccountLifecycles.mockReturnValue(
			Promise.resolve([{id: '1'}])
		);

		API.lifecycle.fetchLifecycle.mockReturnValue(
			Promise.resolve({
				stages: [
					{
						priority: 2,
						externalReferenceCode: 'erc-stage-1002',
						id: '1002',
						stageName: 'Engaged'
					},
					{
						priority: 1,
						externalReferenceCode: 'erc-stage-1001',
						id: '1001',
						stageName: 'Aware'
					}
				]
			})
		);
	});

	it('should render the entity, the property and the operator of the criterion', () => {
		const {getAllByText, getByText} = render(<DefaultComponent />);

		expect(getByText('Account')).toBeTruthy();
		expect(getAllByText('Lifecycle Stage')[0]).toBeTruthy();
		expect(getByText('is')).toBeTruthy();
	});

	it('should render the stage of the selected external reference code', async () => {
		const {getByText} = render(<DefaultComponent />);

		await waitFor(() => expect(getByText('Engaged')).toBeTruthy());
	});

	it('should render the stages of the lifecycle sorted by display order', async () => {
		const {getAllByRole, getByText} = render(<DefaultComponent />);

		await waitFor(() => expect(getByText('Engaged')).toBeTruthy());

		fireEvent.click(getByText('Engaged'));

		const options = getAllByRole('option');

		expect(options[0]).toHaveTextContent('Aware');
		expect(options[1]).toHaveTextContent('Engaged');
	});

	it('should store the external reference code of the selected stage', async () => {
		const onChange = jest.fn();

		const {getAllByRole, getByText} = render(
			<DefaultComponent onChange={onChange} />
		);

		await waitFor(() => expect(getByText('Engaged')).toBeTruthy());

		fireEvent.click(getByText('Engaged'));
		fireEvent.click(getAllByRole('option')[0]);

		expect(
			onChange.mock.calls[0][0].value.getIn([
				'criterionGroup',
				'items',
				0,
				'value'
			])
		).toBe('erc-stage-1001');
	});
});
