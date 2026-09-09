import moment from 'moment';
import {CAMPAIGNS_PER_PAGE} from 'shared/queries/CampaignTouchesByDayQuery';
import {CampaignDays} from 'shared/hooks/useCampaignTouchesByDay';
import {CampaignTouch, toDayKey} from 'shared/util/activities';

const MOCK_DAYS = 5;

const MEMBERS = [
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
	{
		individualId: null,
		individualName: 'Michelle de Rue',
		jobTitle: 'VP of Operations',
		status: 'Registered',
	},
	{
		individualId: null,
		individualName: 'Priya Raman',
		jobTitle: 'Plant Manager',
		status: 'Responded',
	},
];

const CAMPAIGN_NAMES = [
	'Q4 Manufacturing ABM',
	'Q3 Manufacturing ABM',
	'Compaction Webinar Series',
	'RoadTech Product Launch',
	'Bauma Trade Show Follow-Up',
	'Asphalt Nurture Track',
	'Q2 Manufacturing ABM',
	'Q1 Manufacturing ABM',
	'Paving Equipment Newsletter',
];

const buildCampaigns = (dayKey: string, total: number): CampaignTouch[] =>
	Array.from({length: total}, (unused, index) => ({
		campaignId: `${dayKey}-${index}`,
		campaignName: CAMPAIGN_NAMES[index % CAMPAIGN_NAMES.length],
		dataSourceType: 'salesforce',
		touches: MEMBERS.slice(0, (index % MEMBERS.length) + 1),
	}));

const buildDay = (
	dayKey: string,
	campaignsCount: number,
	delta: number,
	page: number
) => {
	const campaigns = buildCampaigns(dayKey, campaignsCount);

	return {
		campaigns: campaigns.slice((page - 1) * delta, page * delta),
		campaignsCount,
		delta,
		page,
		touchesCount: campaigns.reduce(
			(total, {touches}) => total + touches.length,
			0
		),
	};
};

export const mockCampaignTouchesByDay = ({
	date,
	delta = CAMPAIGNS_PER_PAGE,
	page = 1,
	rangeEnd,
}: {
	date?: string | null;
	delta?: number;
	page?: number;
	rangeEnd?: string | null;
}): CampaignDays => {
	const anchor = moment.utc(date || rangeEnd || undefined);

	const dayKeys = date
		? [toDayKey(anchor.toDate())]
		: Array.from({length: MOCK_DAYS}, (unused, index) =>
				toDayKey(moment.utc(anchor).subtract(index, 'days').toDate())
			);

	return dayKeys.reduce<CampaignDays>((days, dayKey, index) => {
		if (index % 3 === 1) {
			return days;
		}

		return {
			...days,
			[dayKey]: buildDay(dayKey, index === 0 ? 9 : 3, delta, page),
		};
	}, {});
};
