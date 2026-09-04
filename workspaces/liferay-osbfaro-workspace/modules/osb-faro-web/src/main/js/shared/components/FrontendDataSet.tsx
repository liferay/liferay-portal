import ClayLink from '@clayui/link';
import {ClayIconSpriteContext as PortalClayIconSpriteContext} from '@clayui/icon-runtime';
import FaroConstants, {RangeKeyTimeRanges} from 'shared/util/constants';
import Label from '@clayui/label';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense, useEffect, useState} from 'react';
import {formatUTCDate, getCustomDateFormat} from 'shared/util/date';
import {Text} from '@clayui/core';
import {setUriQueryValues, toRoute} from 'shared/util/router';
import {toThousands} from 'shared/util/numbers';

import type {
	EConfigInURLBehavior,
	IFrontendDataSetProps,
} from '@liferay/frontend-data-set-web';

// The data set is a webpack external the portal serves, and importing it here
// held every screen that renders one: this module is an ES module, so it could
// not evaluate until the portal had delivered the data set and its Clay
// dependency graph, and the page chunk importing it waited too. React Router
// runs navigations inside `startTransition`, so React held the previous screen
// rather than showing a fallback, and the section looked unresponsive for as
// long as that took. Loading it lazily lets each screen commit first and put a
// spinner where the data set goes.
//
// Nothing else in this module may import the external eagerly, or the wait
// comes straight back. That is why the type import above is `import type`,
// erased at compile time, and why the enum below is written as its value.

const BaseFrontendDataSet = lazy(() =>
	import('@liferay/frontend-data-set-web').then((module) => ({
		default: module.FrontendDataSet,
	}))
);

// `EConfigInURLBehavior.OFF`. It is a string enum, so the value is the whole of
// it, and reading the member would cost an eager import of the external.

const CONFIG_IN_URL_OFF = 'off' as EConfigInURLBehavior;

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
	countRenderer: ({value}: FDSCellProps<number | undefined>) => (
		<div>{toThousands(value ?? 0)}</div>
	),
	dateRenderer: ({value}: FDSCellProps<string | number>) => (
		<div>{value && formatUTCDate(value, getCustomDateFormat())}</div>
	),
	nameAndLinkRenderer: ({
		channelId,
		groupId,
		itemData,
		queryValues,
		route,
		value,
	}: {
		channelId: string;
		groupId: string;
		itemData: {id: string | number};
		queryValues?: {[key: string]: any};
		route: string;
		value: string;
	}) => {
		const itemTitle = value || itemData.id;

		const href = toRoute(route, {
			channelId,
			groupId,
			id: itemData.id,
		});

		return (
			<ClayLink
				className="font-weight-semi-bold text-dark"
				href={queryValues ? setUriQueryValues(queryValues, href) : href}
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

/**
 * Drop-in wrapper around the @liferay/frontend-data-set-web FrontendDataSet,
 * and the single entry point for the data set across the app. The underlying
 * data set is loaded lazily, so a screen rendering one is not held waiting for
 * the portal to deliver it. When `snapshotsEnabled` is set,
 * the data set's saved view snapshots are fetched and the data set is only
 * mounted once they are ready (the base FrontendDataSet reads `snapshots` only
 * when its reducer is initialized on mount). `configInURLBehavior` defaults to
 * OFF so the data set does not dirty the URL unless a consumer overrides it.
 */
const FrontendDataSet = ({
	configInURLBehavior = CONFIG_IN_URL_OFF,
	snapshotsEnabled = false,
	...props
}: IFrontendDataSetProps) => {
	const snapshots = useSnapshots(props.id, snapshotsEnabled);

	if (snapshots === null) {
		return <Loading />;
	}

	// The bundled @clayui/icon context (provided in App.tsx) does not reach the
	// FrontendDataSet, which renders with the DXP's runtime @clayui/icon. Feed
	// that runtime context the same sprite so the data set's icons resolve.

	return (
		<PortalClayIconSpriteContext.Provider value="/o/osb-faro-web/dist/sprite.svg">
			<Suspense fallback={<Loading />}>
				<BaseFrontendDataSet
					{...props}
					configInURLBehavior={configInURLBehavior}
					snapshots={
						snapshots as unknown as IFrontendDataSetProps['snapshots']
					}
					snapshotsEnabled={snapshotsEnabled}
				/>
			</Suspense>
		</PortalClayIconSpriteContext.Provider>
	);
};

export {FrontendDataSet};
