import * as data from 'test/data';

export const fetch = jest.fn(() => Promise.resolve(data.mockAccount()));

export const fetchDetails = jest.fn(() =>
	Promise.resolve(data.mockAccountDetails())
);

export const fetchFieldValues = jest.fn(() => Promise.resolve({items: []}));

export const search = jest.fn(() =>
	Promise.resolve(data.mockSearch(data.mockAccount))
);

export const searchAccounts = jest.fn(() =>
	Promise.resolve(data.mockSearch(data.mockAccount))
);

export const searchByFilter = jest.fn(() =>
	Promise.resolve({items: [data.mockAccount()], totalCount: 1})
);
