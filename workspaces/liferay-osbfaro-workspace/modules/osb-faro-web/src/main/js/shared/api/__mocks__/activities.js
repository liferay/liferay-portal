import * as data from 'test/data';

export const fetchHistory = jest.fn(() =>
	Promise.resolve(data.mockActivityHistory())
);

export const searchCount = jest.fn(() => Promise.resolve(0));
