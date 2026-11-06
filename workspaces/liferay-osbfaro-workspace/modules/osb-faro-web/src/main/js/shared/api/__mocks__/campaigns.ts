export const fetchCampaignMetrics = jest.fn(() => Promise.resolve([]));

export const fetchCampaignAcquisitionParameters = jest.fn(() =>
	Promise.resolve([])
);

export const fetchCampaign = jest.fn(() =>
	Promise.resolve({campaignName: '', id: ''})
);

export const searchCampaignNames = jest.fn(() =>
	Promise.resolve({items: [], totalCount: 0})
);
