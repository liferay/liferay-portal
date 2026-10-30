import * as API from 'shared/api';
import * as data from 'test/data';
import AccountDisplay from '../AccountDisplay';
import React from 'react';
import {
	Conjunctions,
	CustomFunctionOperators,
	NotOperators,
	PropertyTypes,
	RelationalOperators
} from 'segment/segment-editor/dynamic/utils/constants';
import {fromJS, List, Map} from 'immutable';
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

	describe('with a Lifecycle criterion', () => {
		const lifecycleProperty = data.getImmutableMock(
			Property,
			data.mockProperty,
			1,
			{
				entityName: 'Account',
				label: 'Lifecycle',
				name: 'lifecycleStatus',
				propertykey: 'account',
				type: PropertyTypes.AccountSelectText
			}
		);

		const getLifecycleCriterion = (
			propertyName,
			operatorName,
			value,
			criterionOperatorName = CustomFunctionOperators.AccountsFilter
		) => ({
			operatorName: criterionOperatorName,
			propertyName,
			value: Map({
				criterionGroup: fromJS({
					items: Array.isArray(value)
						? [
								{
									conjunctionName: Conjunctions.Or,
									items: value.map(itemValue => ({
										operatorName,
										propertyName,
										value: itemValue
									}))
								}
						  ]
						: [{operatorName, propertyName, value}]
				})
			})
		});

		const renderLifecycleCriterion = criterion =>
			render(
				<StaticRouter>
					<AccountDisplay
						criterion={criterion}
						property={lifecycleProperty}
					/>
				</StaticRouter>
			);

		beforeEach(() => {
			API.lifecycle.fetchChannelLifecycles.mockReturnValueOnce(
				Promise.resolve([
					{
						externalReferenceCode: 'erc-lifecycle-100',
						id: '100',
						name: 'Customer',
						accountLifecycleStages: [
							{
								externalReferenceCode: 'erc-stage-1001',
								id: '1001',
								name: 'Aware',
								position: 1
							},
							{
								externalReferenceCode: 'erc-stage-1002',
								id: '1002',
								name: 'Engaged',
								position: 2
							}
						]
					}
				])
			);
		});

		it('renders the name of the lifecycle', async () => {
			const {container} = renderLifecycleCriterion(
				getLifecycleCriterion(
					'lifecycleStatus@erc-lifecycle-100',
					RelationalOperators.NE,
					null
				)
			);

			await waitFor(() =>
				expect(container).toHaveTextContent("'Customer'")
			);

			expect(container).not.toHaveTextContent('is known');
		});

		it('renders the stages after the name of their lifecycle', async () => {
			const {container} = renderLifecycleCriterion(
				getLifecycleCriterion(
					'lifecycleStatus@erc-lifecycle-100',
					RelationalOperators.EQ,
					['erc-stage-1001', 'erc-stage-1002']
				)
			);

			await waitFor(() =>
				expect(container).toHaveTextContent(
					"'Customer: Aware', 'Customer: Engaged'"
				)
			);
		});

		it('renders the lifecycle of a stage saved without its lifecycle', async () => {
			const {container} = renderLifecycleCriterion(
				getLifecycleCriterion(
					'lifecycleStatus',
					RelationalOperators.EQ,
					'erc-stage-1002'
				)
			);

			await waitFor(() =>
				expect(container).toHaveTextContent("'Customer: Engaged'")
			);

			expect(container).not.toHaveTextContent('1002');
		});

		it('renders the is not operator', async () => {
			const {container} = renderLifecycleCriterion(
				getLifecycleCriterion(
					'lifecycleStatus@erc-lifecycle-100',
					RelationalOperators.NE,
					null,
					NotOperators.NotAccountsFilter
				)
			);

			await waitFor(() =>
				expect(container).toHaveTextContent("'Customer'")
			);

			expect(container).toHaveTextContent('is not');
		});

		it('renders the undefined label when the lifecycle is gone', async () => {
			const {container, findByText} = renderLifecycleCriterion(
				getLifecycleCriterion(
					'lifecycleStatus@deleted-lifecycle',
					RelationalOperators.NE,
					null
				)
			);

			await findByText('Undefined Lifecycle');

			expect(container).not.toHaveTextContent('deleted-lifecycle');
		});

		it('renders the undefined label of a stage that is gone', async () => {
			const {container, findByText} = renderLifecycleCriterion(
				getLifecycleCriterion(
					'lifecycleStatus@erc-lifecycle-100',
					RelationalOperators.EQ,
					['erc-stage-1001', 'deleted-stage']
				)
			);

			await findByText('Undefined Stage');

			expect(container).toHaveTextContent(
				"'Customer: Aware', Undefined Stage"
			);

			expect(container).not.toHaveTextContent('deleted-stage');
		});
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
