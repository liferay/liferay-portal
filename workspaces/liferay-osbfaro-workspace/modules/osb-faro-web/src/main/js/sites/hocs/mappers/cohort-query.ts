import {Interval} from 'shared/types';
import {safeResultToProps} from 'shared/util/mappers';

interface ICohortResult {
	cohort: {
		anonymousCohortHeatMapMetrics: unknown[];
		knownCohortHeatMapMetrics: unknown[];
		visitorsCohortHeatMapMetrics: unknown[];
	};
}

const mapResultToProps = safeResultToProps(
	({
		cohort: {
			anonymousCohortHeatMapMetrics,
			knownCohortHeatMapMetrics,
			visitorsCohortHeatMapMetrics,
		},
	}: ICohortResult) => ({
		data: {
			anonymousVisitors: {
				items: anonymousCohortHeatMapMetrics,
			},
			knownVisitors: {
				items: knownCohortHeatMapMetrics,
			},
			visitors: {
				items: visitorsCohortHeatMapMetrics,
			},
		},
		empty: [
			anonymousCohortHeatMapMetrics,
			knownCohortHeatMapMetrics,
			visitorsCohortHeatMapMetrics,
		].some((metric) => !metric.length),
	})
);

const mapPropsToOptions = ({
	accountId,
	channelId,
	interval,
}: {
	accountId?: string | null;
	channelId: string;
	interval: Interval;
}) => ({
	variables: {
		accountId,
		channelId,
		interval,
	},
});

export {mapPropsToOptions, mapResultToProps};
