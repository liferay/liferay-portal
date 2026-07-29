import {CompositionTypes} from 'shared/util/constants';
import {getSafeRangeSelectors} from 'shared/util/util';
import {RangeSelectors} from 'shared/types';
import {safeResultToProps} from 'shared/util/mappers';

const getMapResultToProps = (compositionBagName: CompositionTypes) =>
	safeResultToProps(
		({
			[compositionBagName]: {compositions, maxCount, total, totalCount},
		}: {
			[key: string]: {
				compositions: Array<any>;
				maxCount: number;
				total: number;
				totalCount: number;
			};
		}) => ({
			empty: !total,
			items: compositions,
			maxCount,
			total,
			totalCount,
		})
	);

interface IMapPropsArgs {
	accountId?: string | null;
	channelId: string;
	delta: number;
	id: string;
	page: number;
	rangeSelectors: RangeSelectors;
	segmentId?: string | null;
}

const mapPropsToOptions = ({
	accountId,
	channelId,
	delta,
	id,
	page,
	rangeSelectors,
	segmentId,
}: IMapPropsArgs) => ({
	variables: {
		accountId,
		channelId,
		id,
		segmentId,
		size: delta,
		start: (page - 1) * delta,
		...getSafeRangeSelectors(rangeSelectors),
	},
});

const mapCardPropsToOptions = ({
	accountId,
	activeTabId,
	channelId,
	rangeSelectors,
	segmentId,
}: {
	accountId?: string | null;
	activeTabId: string;
	channelId: string;
	rangeSelectors: RangeSelectors;
	segmentId?: string | null;
}) => ({
	variables: {
		accountId,
		activeTabId,
		channelId,
		segmentId,
		size: 5,
		start: 0,
		...getSafeRangeSelectors(rangeSelectors),
	},
});

export {getMapResultToProps, mapCardPropsToOptions, mapPropsToOptions};
