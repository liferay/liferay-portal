import * as API from 'shared/api';
import {campaignPagination} from '../campaignPagination';
import {PropertyTypes} from '../../utils/constants';

jest.mock('shared/api', () => ({
	campaigns: {searchCampaignNames: jest.fn()},
}));

describe('campaignPagination', () => {
	afterEach(() => {
		API.campaigns.searchCampaignNames.mockReset();
	});

	it('should search the property campaigns by name, one page at a time', async () => {
		API.campaigns.searchCampaignNames.mockResolvedValueOnce({
			items: [{id: 'c1', name: 'Spring Sale'}],
			totalCount: 30,
		});

		const result = await campaignPagination.api({
			channelId: '123',
			groupId: '456',
			keywords: 'spring',
			page: 2,
			pageSize: 12,
		});

		expect(API.campaigns.searchCampaignNames).toHaveBeenCalledWith({
			channelId: '123',
			groupId: '456',
			page: 2,
			pageSize: 12,
			query: 'spring',
		});

		expect(result).toEqual({
			items: [{id: 'c1', name: 'Spring Sale'}],
			totalCount: 30,
		});
	});

	it('should create one campaign property per campaign, keeping its ID', () => {
		const property = campaignPagination.createProperty({
			id: 'c1',
			name: 'Spring Sale',
		});

		expect(property.id).toBe('c1');
		expect(property.label).toBe('Spring Sale');
		expect(property.propertyKey).toBe('campaign');
		expect(property.type).toBe(PropertyTypes.Campaign);
	});
});
