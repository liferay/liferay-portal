import ClayLink from '@clayui/link';
import FaroConstants from 'shared/util/constants';
import Label from '@clayui/label';
import Link from '@clayui/link';
import React, {useEffect, useState} from 'react';
import {AssetIcon, MimeTypes} from 'assets/components/AssetsIcon';
import {CUSTOM_DATE_FORMAT, formatUTCDate} from 'shared/util/date';
import {Routes, toRoute} from './router';
import {Text} from '@clayui/core';
import {toThousands} from './numbers';

const {cur, delta, deltaValues} = FaroConstants.pagination;

export const pagination = {
	deltas: deltaValues.map(delta => ({label: delta})),
	initialDelta: delta,
	initialPageNumber: cur
};

export const columns = {
	assetMetricRenderer: ({value}) => <span>{toThousands(value.value)}</span>,
	assetTitleRenderer: ({channelId, groupId, rangeSelectorParams}) => ({
		itemData,
		value
	}) => {
		const mapRoutes = {
			blog: Routes.ASSETS_BLOGS_OVERVIEW,
			document: Routes.ASSETS_DOCUMENTS_AND_MEDIA_OVERVIEW,
			form: Routes.ASSETS_FORMS_OVERVIEW,
			webContent: Routes.ASSETS_WEB_CONTENT_OVERVIEW
		};

		const assetTitle = value || itemData.id;
		const route =
			mapRoutes?.[itemData.assetType] ??
			Routes.ASSETS_OBJECT_ENTRY_OVERVIEW;

		const URL = `${toRoute(route, {
			assetId: itemData.id,
			channelId,
			groupId,
			touchpoint: 'Any',
			...(assetTitle && {
				title: encodeURIComponent(assetTitle)
			})
		})}?${rangeSelectorParams}`;

		return (
			<div className='align-items-center d-flex'>
				{itemData.mimeType && (
					<div className='mr-2'>
						<AssetIcon mimeType={itemData.mimeType as MimeTypes} />
					</div>
				)}

				<div>
					<ClayLink displayType='tertiary' href={URL}>
						{value || itemData.id}
					</ClayLink>

					<div>
						<Text color='secondary' size={3}>
							{itemData.id}
						</Text>
					</div>
				</div>
			</div>
		);
	},
	cmsLabelRenderer: ({
		displayType,
		label
	}: {
		displayType: 'danger' | 'info' | 'secondary' | 'success' | 'warning';
		label: React.ReactNode;
	}) => (
		<Label
			className='fds-label font-weight-semi-bold rounded'
			displayType={displayType}
		>
			{label}
		</Label>
	),
	dateRenderer: ({value}) => (
		<div>{value && formatUTCDate(value, CUSTOM_DATE_FORMAT)}</div>
	),
	nameAndLinkRenderer: ({groupId, itemData, value}) => {
		const itemTitle = value || itemData.id;

		return (
			<Link
				className='font-weight-semi-bold text-dark'
				href={toRoute(Routes.CONTACTS_ACCOUNT, {
					groupId,
					id: itemData.id
				})}
			>
				{itemTitle}
			</Link>
		);
	}
};

export function useSnapshots(fdsName: string) {
	if (
		!Liferay.FeatureFlags['LPD-34594'] ||
		!Liferay.FeatureFlags['LPS-164563']
	) {
		return [];
	}

	const [snapshots, setSnapshots] = useState([]);

	useEffect(() => {
		Liferay.Util.fetch(
			`/o/data-set-admin/snapshots?filter=fdsName eq '${fdsName}'`,
			{headers: {'Content-Type': 'application/json'}, method: 'GET'}
		)
			.then(res => res.json())
			.then(data => {
				const formattedSnapshots = data.items.map(
					(item: {
						externalReferenceCode: any;
						label: any;
						viewConfig: any;
					}) => ({
						configuration: item.viewConfig,
						erc: item.externalReferenceCode,
						label: item.label
					})
				);

				setSnapshots(formattedSnapshots);
			})
			.catch(error => {
				// eslint-disable-next-line no-console
				console.error('Failed to fetch snapshots:', error);
			});
	}, [fdsName]);

	return snapshots;
}
