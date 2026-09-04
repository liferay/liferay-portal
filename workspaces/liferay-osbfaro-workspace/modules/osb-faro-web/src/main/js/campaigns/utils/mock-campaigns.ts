/**
 * Mocked stand-in for `GET /campaigns?channelId&page&size`, which does not
 * exist yet. The shapes below mirror the agreed contract so the backend
 * integration task can delete this module and feed `FrontendDataSet` an
 * `apiURL` without touching the table. The `PageDTO` envelope the endpoint
 * wraps these in is not modelled here: the data set fetches and unwraps it
 * itself once it runs on `apiURL`.
 *
 * The endpoint is all-time: it carries no time range.
 */

export interface ICampaign {
	accountsTouched: number;
	campaignName: string;
	campaignType: string;
	endDate: string;
	id: string;
	individualsTouched: number;
	startDate: string;
	status: string;
}

export const mockCampaigns: ICampaign[] = [
	{
		accountsTouched: 342,
		campaignName: 'Q3 Manufacturing Webinar',
		campaignType: 'Webinar',
		endDate: '2026-08-31',
		id: '1',
		individualsTouched: 15200,
		startDate: '2026-07-01',
		status: 'Completed',
	},
	{
		accountsTouched: 215,
		campaignName: 'Brand Ads Retargeting',
		campaignType: 'Advertisement',
		endDate: '2026-09-30',
		id: '2',
		individualsTouched: 9800,
		startDate: '2026-06-15',
		status: 'In Progress',
	},
	{
		accountsTouched: 198,
		campaignName: 'Content Management System',
		campaignType: 'Email',
		endDate: '2026-07-31',
		id: '3',
		individualsTouched: 7200,
		startDate: '2026-05-01',
		status: 'Completed',
	},
	{
		accountsTouched: 178,
		campaignName: 'Platform Architecture Overview',
		campaignType: 'Webinar',
		endDate: '2026-08-15',
		id: '4',
		individualsTouched: 7400,
		startDate: '2026-06-01',
		status: 'Completed',
	},
	{
		accountsTouched: 156,
		campaignName: 'API Developer Documentation',
		campaignType: 'Email',
		endDate: '2026-09-15',
		id: '5',
		individualsTouched: 6900,
		startDate: '2026-07-15',
		status: 'In Progress',
	},
	{
		accountsTouched: 134,
		campaignName: 'Analytics Cloud Dashboard',
		campaignType: 'Email',
		endDate: '2026-08-31',
		id: '6',
		individualsTouched: 5600,
		startDate: '2026-07-01',
		status: 'Completed',
	},
	{
		accountsTouched: 112,
		campaignName: 'Multi-Cloud Solutions Guide',
		campaignType: 'Content Syndication',
		endDate: '2026-09-30',
		id: '7',
		individualsTouched: 3000,
		startDate: '2026-08-01',
		status: 'In Progress',
	},
	{
		accountsTouched: 96,
		campaignName: 'Digital Experience Platform',
		campaignType: 'Event',
		endDate: '2026-06-30',
		id: '8',
		individualsTouched: 3100,
		startDate: '2026-04-01',
		status: 'Completed',
	},
	{
		accountsTouched: 87,
		campaignName: 'Partner Enablement Series',
		campaignType: 'Webinar',
		endDate: '2026-09-30',
		id: '9',
		individualsTouched: 2400,
		startDate: '2026-08-01',
		status: 'In Progress',
	},
	{
		accountsTouched: 73,
		campaignName: 'Commerce Platform Features',
		campaignType: 'Email',
		endDate: '2026-07-15',
		id: '10',
		individualsTouched: 2100,
		startDate: '2026-05-15',
		status: 'Completed',
	},
	{
		accountsTouched: 64,
		campaignName: 'Identity Provider Selection',
		campaignType: 'Content Syndication',
		endDate: '2026-08-31',
		id: '11',
		individualsTouched: 1800,
		startDate: '2026-06-15',
		status: 'Completed',
	},
	{
		accountsTouched: 51,
		campaignName: 'Sustainability Report Launch',
		campaignType: 'Event',
		endDate: '2026-09-30',
		id: '12',
		individualsTouched: 1500,
		startDate: '2026-09-01',
		status: 'In Progress',
	},
];
