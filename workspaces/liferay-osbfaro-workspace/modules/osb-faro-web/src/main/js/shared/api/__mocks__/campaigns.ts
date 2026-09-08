export const fetchCampaignMetrics = jest.fn(() => Promise.resolve([]));

export const fetchCampaign = jest.fn(() =>
	Promise.resolve({campaignName: '', id: ''})
);
