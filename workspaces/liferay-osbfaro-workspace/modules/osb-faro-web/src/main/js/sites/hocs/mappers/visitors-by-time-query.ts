import {getSafeRangeSelectors} from 'shared/util/util';
import {RangeSelectors} from 'shared/types';
import {safeResultToProps} from 'shared/util/mappers';
import {sum} from 'lodash';
import {WEEKDAYS} from 'shared/util/date';

interface IHeatMapItem {
	column: number;
	value: number;
}

interface IVisitorsByTimeResult {
	siteVisitorHeatMap: IHeatMapItem[];
}

const mapResultToProps = safeResultToProps((result: IVisitorsByTimeResult) => {
	const data = result.siteVisitorHeatMap;
	const sumTotal = sum(data.map(({value}) => value));

	return !sumTotal
		? {data, total: 0}
		: {
				data: data.map((item) => ({
					...item,
					column: WEEKDAYS[item.column],
				})),
			};
});

const mapPropsToOptions = ({
	accountId,
	rangeSelectors,
	router: {
		params: {channelId},
	},
	segmentId,
}: {
	accountId?: string | null;
	rangeSelectors: RangeSelectors;
	router: {params: {channelId: string}};
	segmentId?: string | null;
}) => ({
	variables: {
		accountId,
		channelId,
		segmentId,
		...getSafeRangeSelectors(rangeSelectors),
	},
});

export {mapPropsToOptions, mapResultToProps};
