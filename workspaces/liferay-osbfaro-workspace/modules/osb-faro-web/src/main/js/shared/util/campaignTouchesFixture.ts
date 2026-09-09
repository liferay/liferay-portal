import {CampaignTouch, TimelineDay} from 'shared/util/activities';

/**
 * Placeholder day-level campaign touches, so the Day-Level card has something
 * to render while the backend query is still being built.
 *
 * Delete this module and its caller in LPD-104758, which replaces it with
 * `campaignTouchesByDay`. Nothing here reaches a real workspace's data — every
 * third day is left without campaigns, so the Day-Level card's empty state is
 * visible beside its filled one.
 */
const CAMPAIGN_TOUCHES: CampaignTouch[] = [
	{
		campaignId: 'fixture-q4',
		campaignName: 'Q4 Manufacturing ABM',
		dataSourceType: 'salesforce',
		touches: [
			{
				individualId: null,
				individualName: 'Elena Ruiz',
				jobTitle: 'Head of Maintenance',
				status: 'Attended',
			},
			{
				individualId: null,
				individualName: 'Tom Blake',
				jobTitle: 'Operations Manager',
				status: 'Sent',
			},
		],
	},
	{
		campaignId: 'fixture-q3',
		campaignName: 'Q3 Manufacturing ABM',
		dataSourceType: 'salesforce',
		touches: [
			{
				individualId: null,
				individualName: 'Michelle de Rue',
				jobTitle: 'VP of Operations',
				status: 'Registered',
			},
			{
				individualId: null,
				individualName: 'Marcus Vance',
				jobTitle: 'Director of Procurement',
				status: 'Attended',
			},
			{
				individualId: null,
				individualName: 'Sarah Jenkins',
				jobTitle: 'Plant Manager',
				status: 'Sent',
			},
			{
				individualId: null,
				individualName: 'David Mercer',
				jobTitle: 'Technical Lead',
				status: 'Registered',
			},
		],
	},
	{
		campaignId: 'fixture-q2',
		campaignName: 'Q2 Manufacturing ABM',
		dataSourceType: 'salesforce',
		touches: [
			{
				individualId: null,
				individualName: 'Priya Nair',
				jobTitle: 'Procurement Lead',
				status: 'Attended',
			},
			{
				individualId: null,
				individualName: 'Owen Clark',
				jobTitle: 'Site Engineer',
				status: 'Registered',
			},
		],
	},
	{
		campaignId: 'fixture-q1',
		campaignName: 'Q1 Manufacturing ABM',
		dataSourceType: 'salesforce',
		touches: [
			{
				individualId: null,
				individualName: 'Nadia Osei',
				jobTitle: 'Plant Director',
				status: 'Attended',
			},
		],
	},
	{
		campaignId: 'fixture-webinar',
		campaignName: 'Compaction Webinar Series',
		dataSourceType: 'salesforce',
		touches: [
			{
				individualId: null,
				individualName: 'Liam Fischer',
				jobTitle: 'Field Engineer',
				status: 'Registered',
			},
			{
				individualId: null,
				individualName: 'Ana Duarte',
				jobTitle: 'Fleet Manager',
				status: 'Attended',
			},
		],
	},
	{
		campaignId: 'fixture-roadtech',
		campaignName: 'RoadTech Product Launch',
		dataSourceType: 'salesforce',
		touches: [
			{
				individualId: null,
				individualName: 'Hugo Meyer',
				jobTitle: 'Procurement Analyst',
				status: 'Sent',
			},
		],
	},
	{
		campaignId: 'fixture-tradeshow',
		campaignName: 'Bauma Trade Show Follow-Up',
		dataSourceType: 'salesforce',
		touches: [
			{
				individualId: null,
				individualName: 'Ines Alvarez',
				jobTitle: 'Operations Director',
				status: 'Attended',
			},
			{
				individualId: null,
				individualName: 'Karl Jensen',
				jobTitle: 'Maintenance Lead',
				status: 'Registered',
			},
		],
	},
	{
		campaignId: 'fixture-nurture',
		campaignName: 'Asphalt Nurture Track',
		dataSourceType: 'salesforce',
		touches: [
			{
				individualId: null,
				individualName: 'Sofia Bianchi',
				jobTitle: 'Site Supervisor',
				status: 'Sent',
			},
		],
	},
	{
		campaignId: 'fixture-retarget',
		campaignName: 'Tandem Roller Retargeting',
		dataSourceType: 'salesforce',
		touches: [
			{
				individualId: null,
				individualName: 'Diego Torres',
				jobTitle: 'Equipment Buyer',
				status: 'Registered',
			},
		],
	},
];

export const withCampaignTouchesFixture = (
	days: TimelineDay[]
): TimelineDay[] =>
	days.map((day, index) => ({
		...day,
		campaigns: index % 3 === 1 ? [] : CAMPAIGN_TOUCHES,
	}));

export default withCampaignTouchesFixture;
