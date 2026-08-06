import * as API from 'shared/api';
import * as data from 'test/data';
import AccountDisplay from '../AccountDisplay';
import React from 'react';
import {
	CustomFunctionOperators,
	NotOperators,
	PropertyTypes,
	RelationalOperators
} from 'segment/segment-editor/dynamic/utils/constants';
import {List, Map} from 'immutable';
import {Property} from 'shared/util/records';
import {render, waitFor} from '@testing-library/react';
import {StaticRouter} from 'react-router';

jest.unmock('react-dom');

describe('AccountDisplay', () => {
	const propertyName = 'organization/description/value';

	const mockCriterion = {
		operatorName: CustomFunctionOperators.AccountsFilter,
		propertyName,
		value: Map({
			criterionGroup: Map({
				items: List([
					Map({
						operatorName: RelationalOperators.EQ,
						propertyName: 'organization/description/value',
						value: 'this is a description'
					})
				])
			})
		})
	};

	const mockProperty = data.getImmutableMock(Property, data.mockProperty, 1, {
		entityName: 'Account',
		label: 'description',
		name: propertyName,
		propertykey: 'account',
		type: PropertyTypes.AccountText
	});

	it('renders', () => {
		const {container} = render(
			<AccountDisplay criterion={mockCriterion} property={mockProperty} />
		);

		expect(container).toMatchSnapshot();
	});

	it('renders the label of the lifecycle stage', async () => {
		API.lifecycle.fetchAccountLifecycles.mockReturnValueOnce(
			Promise.resolve([{id: '1'}])
		);

		API.lifecycle.fetchLifecycle.mockReturnValueOnce(
			Promise.resolve({
				stages: [{displayOrder: 1, id: '1002', stageType: 'ENGAGED'}]
			})
		);

		const criterion = {
			operatorName: CustomFunctionOperators.AccountsFilter,
			propertyName: 'lifecycleStatus',
			value: Map({
				criterionGroup: Map({
					items: List([
						Map({
							operatorName: RelationalOperators.EQ,
							propertyName: 'lifecycleStatus',
							value: '1002'
						})
					])
				})
			})
		};

		const property = data.getImmutableMock(Property, data.mockProperty, 1, {
			entityName: 'Account',
			label: 'Lifecycle Stage',
			name: 'lifecycleStatus',
			propertykey: 'account',
			type: PropertyTypes.AccountSelectText
		});

		const {container} = render(
			<StaticRouter>
				<AccountDisplay criterion={criterion} property={property} />
			</StaticRouter>
		);

		await waitFor(() => expect(container).toHaveTextContent("'Engaged'"));

		expect(container).not.toHaveTextContent('1002');
	});

	it('renders the is not operator of a lifecycle stage criterion', async () => {
		API.lifecycle.fetchAccountLifecycles.mockReturnValueOnce(
			Promise.resolve([{id: '1'}])
		);

		API.lifecycle.fetchLifecycle.mockReturnValueOnce(
			Promise.resolve({
				stages: [{displayOrder: 1, id: '1002', stageType: 'ENGAGED'}]
			})
		);

		const criterion = {
			operatorName: NotOperators.NotAccountsFilter,
			propertyName: 'lifecycleStatus',
			value: Map({
				criterionGroup: Map({
					items: List([
						Map({
							operatorName: RelationalOperators.EQ,
							propertyName: 'lifecycleStatus',
							value: '1002'
						})
					])
				})
			})
		};

		const property = data.getImmutableMock(Property, data.mockProperty, 1, {
			entityName: 'Account',
			label: 'Lifecycle Stage',
			name: 'lifecycleStatus',
			propertykey: 'account',
			type: PropertyTypes.AccountSelectText
		});

		const {container} = render(
			<StaticRouter>
				<AccountDisplay criterion={criterion} property={property} />
			</StaticRouter>
		);

		await waitFor(() => expect(container).toHaveTextContent("'Engaged'"));

		expect(container).toHaveTextContent('is not');
	});

	it('renders the undefined label when the lifecycle stage is gone', async () => {
		API.lifecycle.fetchAccountLifecycles.mockReturnValueOnce(
			Promise.resolve([{id: '1'}])
		);

		API.lifecycle.fetchLifecycle.mockReturnValueOnce(
			Promise.resolve({
				stages: [{displayOrder: 1, id: '1002', stageType: 'ENGAGED'}]
			})
		);

		const criterion = {
			operatorName: CustomFunctionOperators.AccountsFilter,
			propertyName: 'lifecycleStatus',
			value: Map({
				criterionGroup: Map({
					items: List([
						Map({
							operatorName: RelationalOperators.EQ,
							propertyName: 'lifecycleStatus',
							value: 'deleted-stage'
						})
					])
				})
			})
		};

		const property = data.getImmutableMock(Property, data.mockProperty, 1, {
			entityName: 'Account',
			label: 'Lifecycle Stage',
			name: 'lifecycleStatus',
			propertykey: 'account',
			type: PropertyTypes.AccountSelectText
		});

		const {container} = render(
			<StaticRouter>
				<AccountDisplay criterion={criterion} property={property} />
			</StaticRouter>
		);

		await waitFor(() =>
			expect(container.querySelector('.undefined-entity')).toBeTruthy()
		);

		expect(container).not.toHaveTextContent('deleted-stage');
	});

	it('renders w/ a unknownType', () => {
		const criterion = {...mockCriterion};

		criterion.value = criterion.value.setIn(
			['criterionGroup', 'items', 0, 'value'],
			null
		);

		const {container} = render(
			<AccountDisplay criterion={criterion} property={mockProperty} />
		);

		expect(container).toHaveTextContent(/is unknown/);
	});
});
