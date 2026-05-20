import * as data from 'test/data';
import {EntityTypes} from 'shared/util/constants';

export const fetch = jest.fn(() => Promise.resolve(data.mockCSVDataSource()));

export const search = jest.fn(({states = []}) => {
	if (states.length) {
		return Promise.resolve({items: [], total: 0});
	}

	return Promise.resolve(data.mockSearch(data.mockCSVDataSource));
});

export const fetchChannels = jest.fn(() =>
	Promise.resolve(data.mockSearch(data.mockLiferayDataSource))
);

export const fetchDeletePreview = jest.fn(() =>
	Promise.resolve({
		[EntityTypes.Account]: 123,
		[EntityTypes.Asset]: 23,
		[EntityTypes.Individual]: 1,
		[EntityTypes.IndividualsSegment]: 12,
		[EntityTypes.Page]: 1234
	})
);

export const fetchFieldValues = jest.fn(() =>
	Promise.resolve([
		{
			name: 'email',
			values: ['test@liferay.com']
		},
		{
			name: 'familyName',
			values: ['Bloggs']
		},
		{
			name: 'givenName',
			values: ['Joe']
		}
	])
);

export const fetchLiferaySyncCounts = jest.fn(() =>
	Promise.resolve(data.mockLiferaySyncCounts())
);

export const fetchMappings = jest.fn(() =>
	Promise.resolve([data.mockMapping()])
);

export const fetchMappingsLite = jest.fn(() =>
	Promise.resolve([
		data.mockMapping(0, {
			context: 'demographics',
			id: '123',
			mapping: {
				id: 'mappingId',
				name: 'MappingName',
				values: ['mappingValue']
			},
			ownerType: 'individual',
			type: 'Text',
			values: ['value']
		})
	])
);

export const fetchProgress = jest.fn(() =>
	Promise.resolve(data.mockProgress())
);

export const fetchUserGroups = jest.fn(() =>
	Promise.resolve([
		{
			id: 0,
			name: 'Test User Group'
		}
	])
);

export const fetchUserGroupsById = jest.fn(() => Promise.resolve([]));

export const fetchOrganizations = jest.fn(() =>
	Promise.resolve([data.mockOrganization()])
);

export const fetchOrganizationsById = jest.fn(() => Promise.resolve([]));

export const fetchOAuthUrl = jest.fn(() =>
	Promise.resolve({
		oAuthAuthorizationURL: 'https://foobar.biz',
		oAuthTokenSecret: 'bizzybuzz'
	})
);

export const fetchToken = jest.fn(() => Promise.resolve('Test Token'));

export const createCSV = jest.fn(() =>
	Promise.resolve(data.mockCSVDataSource())
);
export const createLiferay = jest.fn(() =>
	Promise.resolve(data.mockLiferayDataSource())
);

export const updateCSV = jest.fn(() =>
	Promise.resolve(data.mockCSVDataSource())
);
export const updateLiferay = jest.fn(() =>
	Promise.resolve(data.mockLiferayDataSource())
);

export const fetchSites = jest.fn(() =>
	Promise.resolve(data.mockSearch(data.mockSite))
);

export const fetchSitesById = jest.fn(() => Promise.resolve([]));

export const fetchCompanies = jest.fn(() =>
	Promise.resolve([data.mockCompany()])
);
