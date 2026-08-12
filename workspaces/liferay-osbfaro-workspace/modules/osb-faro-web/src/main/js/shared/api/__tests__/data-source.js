jest.mock('shared/util/request');

import sendRequest from 'shared/util/request';
import {
	createLiferay,
	createSalesforce,
	updateLiferay,
	updateSalesforce,
} from '../data-source';

const commonLiferayArgs = {
	credentials: {
		login: 'test',
		password: 'testPassword',
	},
	fieldMappingMaps: [],
	name: 'test',
	url: 'test.com',
};

describe('Data Source API', () => {
	describe('Liferay Data Sources', () => {
		it('should be called with data to CREATE to a liferay data source', () => {
			createLiferay({...commonLiferayArgs, groupId: '23'});

			expect(sendRequest).toHaveBeenCalledWith({
				data: commonLiferayArgs,
				method: 'POST',
				path: 'contacts/23/data_source/liferay',
			});
		});

		it('should be called with data to UPDATE to a liferay data source', () => {
			const dataArgs = {
				...commonLiferayArgs,
				analyticsConfiguration: {},
				contactsConfiguration: {},
			};

			updateLiferay({...dataArgs, groupId: '23', id: '1'});

			expect(sendRequest).toHaveBeenCalledWith({
				data: dataArgs,
				method: 'PATCH',
				path: 'contacts/23/data_source/1/liferay',
			});
		});
	});

	describe('Salesforce Data Sources', () => {
		it('should enable all campaigns when CREATING a salesforce data source', () => {
			createSalesforce({
				accountsConfiguration: {enableAllAccounts: true},
				groupId: '23',
				name: 'test',
			});

			expect(sendRequest).toHaveBeenCalledWith({
				data: {
					accountsConfiguration: {enableAllAccounts: true},
					campaignsConfiguration: {enableAllCampaigns: true},
					name: 'test',
				},
				method: 'POST',
				path: 'contacts/23/data_source/salesforce',
			});
		});

		it('should enable all campaigns when UPDATING a salesforce data source', () => {
			updateSalesforce({
				accountsConfiguration: {enableAllAccounts: true},
				groupId: '23',
				id: '1',
				name: 'test',
			});

			expect(sendRequest).toHaveBeenCalledWith({
				data: {
					accountsConfiguration: {enableAllAccounts: true},
					campaignsConfiguration: {enableAllCampaigns: true},
					name: 'test',
				},
				method: 'PATCH',
				path: 'contacts/23/data_source/1/salesforce',
			});
		});

		it('should enable all campaigns when UPDATING only the name', () => {
			updateSalesforce({groupId: '23', id: '1', name: 'test'});

			expect(sendRequest).toHaveBeenCalledWith({
				data: {
					campaignsConfiguration: {enableAllCampaigns: true},
					name: 'test',
				},
				method: 'PATCH',
				path: 'contacts/23/data_source/1/salesforce',
			});
		});
	});
});
