import * as API from 'shared/api';
import CampaignInput from '../CampaignInput';
import React from 'react';
import {CAMPAIGN_PROPERTY} from '../../utils/properties';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import {CustomValue} from 'shared/util/records';
import {getDefaultValue} from '../../criteria-sidebar/CriteriaSidebarCollapse';
import {List} from 'immutable';

jest.unmock('react-dom');

const fetchCampaign = API.campaigns.fetchCampaign as jest.Mock;
const searchCampaignNames = API.campaigns.searchCampaignNames as jest.Mock;

const getValue = (ids: string[] = []): CustomValue =>
	getDefaultValue(CAMPAIGN_PROPERTY).setIn(
		['criterionGroup', 'items', 0, 'value'],
		List(ids)
	);

const renderInput = ({
	ids,
	onChange = jest.fn(),
}: {
	ids?: string[];
	onChange?: jest.Mock;
} = {}) =>
	render(
		<CampaignInput
			channelId="456"
			displayValue={CAMPAIGN_PROPERTY.label}
			groupId="123"
			onChange={onChange}
			operatorRenderer={() => <div>{'is'}</div>}
			property={CAMPAIGN_PROPERTY}
			touched={false}
			valid={false}
			value={getValue(ids)}
		/>
	);

const getSelectedIds = (onChange: jest.Mock) =>
	onChange.mock.calls[0][0].value
		.getIn(['criterionGroup', 'items', 0, 'value'])
		.toJS();

describe('CampaignInput', () => {
	afterEach(cleanup);

	beforeEach(() => {
		fetchCampaign.mockReset();
		searchCampaignNames.mockReset();
		searchCampaignNames.mockReturnValue(
			Promise.resolve({
				items: [
					{id: 'c1', name: 'Spring Sale'},
					{id: 'c2', name: 'Black Friday'},
				],
				totalCount: 2,
			})
		);
	});

	it('should search campaigns by the text typed', async () => {
		renderInput();

		const input = screen.getByRole('combobox', {name: 'Campaign Name'});

		fireEvent.focus(input);
		fireEvent.change(input, {target: {value: 'black'}});

		await waitFor(() =>
			expect(searchCampaignNames).toHaveBeenLastCalledWith({
				channelId: '456',
				groupId: '123',
				page: 1,
				pageSize: 20,
				query: 'black',
			})
		);
	});

	it('should add the chosen campaign to the criterion and make it valid', async () => {
		const onChange = jest.fn();

		renderInput({onChange});

		fireEvent.focus(screen.getByRole('combobox', {name: 'Campaign Name'}));

		fireEvent.click(
			await screen.findByRole('option', {name: 'Black Friday'})
		);

		expect(getSelectedIds(onChange)).toEqual(['c2']);
		expect(onChange.mock.calls[0][0]).toMatchObject({
			touched: true,
			valid: true,
		});
	});

	it('should not offer a campaign the criterion already holds', async () => {
		fetchCampaign.mockReturnValue(
			Promise.resolve({campaignName: 'Spring Sale', id: 'c1'})
		);

		renderInput({ids: ['c1']});

		fireEvent.focus(screen.getByRole('combobox', {name: 'Campaign Name'}));

		expect(
			await screen.findByRole('option', {name: 'Black Friday'})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('option', {name: 'Spring Sale'})
		).not.toBeInTheDocument();
	});

	it('should show the names of the campaigns a saved criterion holds', async () => {
		fetchCampaign.mockImplementation(({id}) =>
			Promise.resolve({
				campaignName: id === 'c1' ? 'Spring Sale' : 'Black Friday',
				id,
			})
		);

		renderInput({ids: ['c1', 'c2']});

		expect(await screen.findByText('Spring Sale')).toBeInTheDocument();
		expect(screen.getByText('Black Friday')).toBeInTheDocument();
		expect(fetchCampaign).toHaveBeenCalledWith({
			channelId: '456',
			groupId: '123',
			id: 'c1',
		});
	});

	it('should show a campaign that no longer exists as undefined', async () => {
		fetchCampaign.mockReturnValue(Promise.reject(new Error('Not Found')));

		renderInput({ids: ['c1']});

		expect(
			await screen.findByText('Undefined Campaign Name')
		).toBeInTheDocument();
	});

	it('should make the criterion invalid when its last campaign is removed', async () => {
		fetchCampaign.mockReturnValue(
			Promise.resolve({campaignName: 'Spring Sale', id: 'c1'})
		);

		const onChange = jest.fn();

		renderInput({ids: ['c1'], onChange});

		fireEvent.click(
			await screen.findByRole('button', {name: 'Remove Spring Sale'})
		);

		expect(getSelectedIds(onChange)).toEqual([]);
		expect(onChange.mock.calls[0][0].valid).toBe(false);
	});
});
