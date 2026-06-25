import ClayLink from '@clayui/link';
import {ClayIconSpriteContext as PortalClayIconSpriteContext} from '@clayui/icon-runtime';
import FaroConstants, {RangeKeyTimeRanges} from 'shared/util/constants';
import Label from '@clayui/label';
import Loading from 'shared/components/Loading';
import React, {useEffect, useState} from 'react';
import {
	FrontendDataSet as BaseFrontendDataSet,
	EConfigInURLBehavior,
} from '@liferay/frontend-data-set-web';
import {CUSTOM_DATE_FORMAT, formatUTCDate} from 'shared/util/date';
import {Text} from '@clayui/core';
import {toRoute} from 'shared/util/router';

export * from '@liferay/frontend-data-set-web';

const {cur, delta, deltaValues} = FaroConstants.pagination;

export const rangeSelectors = [
	{
		label: Liferay.Language.get('last-24-hours'),
		value: RangeKeyTimeRanges.Last24Hours,
	},
	{
		label: Liferay.Language.get('yesterday'),
		value: RangeKeyTimeRanges.Yesterday,
	},
	{
		label: Liferay.Language.get('last-seven-days'),
		value: RangeKeyTimeRanges.Last7Days,
	},
	{
		label: Liferay.Language.get('last-28-days'),
		value: RangeKeyTimeRanges.Last28Days,
	},
	{
		label: Liferay.Language.get('last-30-days'),
		value: RangeKeyTimeRanges.Last30Days,
	},
	{
		label: Liferay.Language.get('last-90-days'),
		value: RangeKeyTimeRanges.Last90Days,
	},
	{
		label: Liferay.Language.get('last-180-days'),
		value: RangeKeyTimeRanges.Last180Days,
	},
	{
		label: Liferay.Language.get('last-year'),
		value: RangeKeyTimeRanges.LastYear,
	},
];

export const pagination = {
	deltas: deltaValues.map((delta) => ({label: delta})),
	initialDelta: delta,
	initialPageNumber: cur,
};

type FDSCellProps<TValue = unknown, TItemData = Record<string, unknown>> = {
	itemData: TItemData;
	value: TValue;
};

export const columns = {
	attributeNameAndValue: ({
		attributeName,
		value,
	}: {
		attributeName?: string;
		value: string | number;
	}) => (
		<div>
			<p className="mb-0 text-secondary">{attributeName}</p>
			<Text size={3} weight="semi-bold">
				{value}
			</Text>
		</div>
	),
	cmsLabelRenderer: ({
		displayType,
		label,
	}: {
		displayType: 'danger' | 'info' | 'secondary' | 'success' | 'warning';
		label: React.ReactNode;
	}) => (
		<Label
			className="fds-label font-weight-semi-bold rounded"
			displayType={displayType}
		>
			{label}
		</Label>
	),
	dateRenderer: ({value}: FDSCellProps<string | number>) => (
		<div>{value && formatUTCDate(value, CUSTOM_DATE_FORMAT)}</div>
	),
	nameAndLinkRenderer: ({
		channelId,
		groupId,
		itemData,
		route,
		value,
	}: {
		channelId: string;
		groupId: string;
		itemData: {id: string | number};
		route: string;
		value: string;
	}) => {
		const itemTitle = value || itemData.id;

		return (
			<ClayLink
				className="font-weight-semi-bold text-dark"
				href={toRoute(route, {
					channelId,
					groupId,
					id: itemData.id,
				})}
			>
				{itemTitle}
			</ClayLink>
		);
	},
};

export function useSnapshots(fdsName: string, enabled = true) {
	const fetchSnapshots = enabled && Liferay.FeatureFlags['LPS-164563'];

	const [snapshots, setSnapshots] = useState<Array<{
		configuration: string;
		erc: string;
		label: string;
	}> | null>(fetchSnapshots ? null : []);

	useEffect(() => {
		if (!fetchSnapshots) {
			return;
		}

		Liferay.Util.fetch(
			`/o/data-set-admin/snapshots?filter=fdsName eq '${fdsName}'`,
			{headers: {'Content-Type': 'application/json'}, method: 'GET'}
		)
			.then((res) => res.json())
			.then((data) => {
				const formattedSnapshots = data.items.map(
					(item: {
						externalReferenceCode: any;
						label: any;
						viewConfig: any;
					}) => ({
						configuration: item.viewConfig,
						erc: item.externalReferenceCode,
						label: item.label,
					})
				);

				setSnapshots(formattedSnapshots);
			})
			.catch((error) => {

				// eslint-disable-next-line no-console
				console.error('Failed to fetch snapshots:', error);

				setSnapshots([]);
			});
	}, [fetchSnapshots, fdsName]);

	return snapshots;
}

type IBaseFrontendDataSetProps = React.ComponentProps<
	typeof BaseFrontendDataSet
>;

/**
 * Drop-in wrapper around the @liferay/frontend-data-set-web FrontendDataSet.
 * Everything from the original module is re-exported, so this is the single
 * entry point for the data set across the app. When `snapshotsEnabled` is set,
 * the data set's saved view snapshots are fetched and the data set is only
 * mounted once they are ready (the base FrontendDataSet reads `snapshots` only
 * when its reducer is initialized on mount). `configInURLBehavior` defaults to
 * OFF so the data set does not dirty the URL unless a consumer overrides it.
 */
const FrontendDataSet = ({
	configInURLBehavior = EConfigInURLBehavior.OFF,
	snapshotsEnabled = false,
	...props
}: IBaseFrontendDataSetProps) => {
	const snapshots = useSnapshots(props.id, snapshotsEnabled);

	if (snapshots === null) {
		return <Loading />;
	}

	// The bundled @clayui/icon context (provided in App.tsx) does not reach the
	// FrontendDataSet, which renders with the DXP's runtime @clayui/icon. Feed
	// that runtime context the same sprite so the data set's icons resolve.

	return (
		<PortalClayIconSpriteContext.Provider value="/o/osb-faro-web/dist/sprite.svg">
			<BaseFrontendDataSet
				{...props}
				configInURLBehavior={configInURLBehavior}
				snapshots={
					snapshots as unknown as IBaseFrontendDataSetProps['snapshots']
				}
				snapshotsEnabled={snapshotsEnabled}
			/>
		</PortalClayIconSpriteContext.Provider>
	);
};

export {FrontendDataSet};
