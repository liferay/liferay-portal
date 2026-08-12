import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import React from 'react';
import {Option, Picker, Text} from '@clayui/core';

export enum GroupByMetric {
	DOWNLOADS = 'downloads',
	IMPRESSIONS = 'impressions',
	VIEWS = 'views',
}

export type GroupByMetricField =
	| 'downloadsMetric'
	| 'impressionsMetric'
	| 'viewsMetric';

export const GROUP_BY_METRICS: GroupByMetric[] = [
	GroupByMetric.DOWNLOADS,
	GroupByMetric.IMPRESSIONS,
	GroupByMetric.VIEWS,
];

export const GROUP_BY_TO_METRIC: Record<GroupByMetric, GroupByMetricField> = {
	[GroupByMetric.DOWNLOADS]: 'downloadsMetric',
	[GroupByMetric.IMPRESSIONS]: 'impressionsMetric',
	[GroupByMetric.VIEWS]: 'viewsMetric',
};

/**
 * Built on demand, and not held in a module constant, so that the labels are
 * translated when the card renders rather than when the bundle loads.
 */

export const getGroupByLabels = (): Record<GroupByMetric, string> => ({
	[GroupByMetric.DOWNLOADS]: Liferay.Language.get('downloads'),
	[GroupByMetric.IMPRESSIONS]: Liferay.Language.get('impressions'),
	[GroupByMetric.VIEWS]: Liferay.Language.get('views'),
});

const GroupByTrigger = React.forwardRef<
	HTMLButtonElement,
	React.ButtonHTMLAttributes<HTMLButtonElement> & {label?: string}
>(({label, ...rest}, ref) => (
	<ClayButton
		{...rest}
		className="rounded-lg"
		displayType="secondary"
		ref={ref}
		size="sm"
	>
		{label}
		<ClayIcon
			className="inline-item inline-item-after"
			symbol="caret-bottom"
		/>
	</ClayButton>
));

interface IGroupByPickerProps {

	/**
	 * The metrics to offer. Cards that support a different set of metrics per
	 * tab pass the set of the active tab.
	 */
	metrics: GroupByMetric[];
	onGroupByChange: (metric: GroupByMetric) => void;
	value: GroupByMetric;
}

const GroupByPicker: React.FC<IGroupByPickerProps> = ({
	metrics,
	onGroupByChange,
	value,
}) => {
	const groupByLabels = getGroupByLabels();

	return (
		<div className="align-items-center d-flex">
			<div className="font-weight-semi-bold mr-2">
				<Text size={3}>{Liferay.Language.get('group-by')}</Text>
			</div>

			<Picker
				aria-label={Liferay.Language.get('group-by')}
				as={GroupByTrigger}
				items={metrics}
				label={groupByLabels[value]}
				onSelectionChange={(key) =>
					onGroupByChange(key as GroupByMetric)
				}
				selectedKey={value}
			>
				{(key: GroupByMetric) => (
					<Option key={key}>{groupByLabels[key]}</Option>
				)}
			</Picker>
		</div>
	);
};

export default GroupByPicker;
