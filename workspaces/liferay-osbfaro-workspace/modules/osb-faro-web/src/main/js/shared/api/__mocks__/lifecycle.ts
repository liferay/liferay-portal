export const createLifecycle = jest.fn(() => Promise.resolve({stages: []}));

export const fetchAccountLifecycles = jest.fn(() => Promise.resolve([]));

export const fetchLifecycle = jest.fn(() => Promise.resolve({stages: []}));

export const fetchLifecycles = jest.fn(() => Promise.resolve([]));

export const fetchLifecyclesPage = jest.fn(() =>
	Promise.resolve({
		items: [],
		lastPage: 1,
		page: 1,
		pageSize: 20,
		totalCount: 0,
	})
);

export const setDefaultLifecycle = jest.fn(() => Promise.resolve());

export const updateLifecycle = jest.fn(() => Promise.resolve({stages: []}));
