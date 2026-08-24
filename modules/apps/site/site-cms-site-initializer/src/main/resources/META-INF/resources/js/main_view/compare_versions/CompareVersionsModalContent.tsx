/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {Option, Picker} from '@clayui/core';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import classNames from 'classnames';
import {dateUtils, sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import '../../../css/components/CompareVersionsModal.scss';
import StatusLabel from '../../common/components/StatusLabel';
import {IAssetObjectEntry} from '../../common/types/AssetType';
import VersionService from '../info_panel/services/VersionService';
import {VIEW_CONTENT_VERSION_URL} from '../info_panel/util/constants';

interface CompareVersionsModalContentProps {
	apiURL: string;
	initialVersion: number;
	objectEntryId: number;
}

type VersionItem = IAssetObjectEntry;

type VersionsState =
	| {status: 'error' | 'loading'}
	| {items: VersionItem[]; status: 'loaded'};

function getVersionLabel(version: number) {
	return sub(Liferay.Language.get('version-x'), [version]);
}

function getVersionItem(items: VersionItem[], version: number | null) {
	return items.find((item) => getVersionNumber(item) === version);
}

function getVersionNumber(item: VersionItem) {
	return item.systemProperties.version.number;
}

export default function CompareVersionsModalContent({
	apiURL,
	initialVersion,
	objectEntryId,
}: CompareVersionsModalContentProps) {
	const [sourceVersion, setSourceVersion] = useState<number | null>(null);
	const [targetVersion, setTargetVersion] = useState<number | null>(null);
	const [versionsState, setVersionsState] = useState<VersionsState>({
		status: 'loading',
	});

	useEffect(() => {
		let stale = false;

		const getVersions = async () => {
			const {data, error} = await VersionService.getObjectEntryVersions(
				apiURL,
				{page: 1, pageSize: -1, sort: 'version:desc'}
			);

			if (stale) {
				return;
			}

			if (error !== null) {
				setVersionsState({status: 'error'});

				return;
			}

			const items: VersionItem[] = data.items;

			setSourceVersion(initialVersion);
			setVersionsState({items, status: 'loaded'});
		};

		getVersions();

		return () => {
			stale = true;
		};
	}, [apiURL, initialVersion]);

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('compare-versions')}
			</ClayModal.Header>

			<ClayModal.Body className="d-flex flex-column p-0">
				{versionsState.status === 'loading' ? (
					<ClayLoadingIndicator className="my-5" />
				) : null}

				{versionsState.status === 'error' ? (
					<ClayAlert className="m-3" displayType="danger">
						{Liferay.Language.get('an-unexpected-error-occurred')}
					</ClayAlert>
				) : null}

				{versionsState.status === 'loaded' ? (
					<div className="cms-compare-versions-panes d-flex flex-grow-1">
						<CompareVersionPane
							objectEntryId={objectEntryId}
							onVersionChange={setSourceVersion}
							selectedVersion={sourceVersion}
							versions={versionsState.items}
						/>

						<CompareVersionPane
							className="border-left"
							objectEntryId={objectEntryId}
							onVersionChange={setTargetVersion}
							selectedVersion={targetVersion}
							versions={versionsState.items}
						/>
					</div>
				) : null}
			</ClayModal.Body>
		</>
	);
}

function CompareVersionPane({
	className,
	objectEntryId,
	onVersionChange,
	selectedVersion,
	versions,
}: {
	className?: string;
	objectEntryId: number;
	onVersionChange: (version: number) => void;
	selectedVersion: number | null;
	versions: VersionItem[];
}) {
	const [iframeStatus, setIframeStatus] = useState<'loaded' | 'loading'>(
		'loading'
	);

	const selectedItem = getVersionItem(versions, selectedVersion);

	return (
		<div
			className={classNames(
				'cms-compare-versions-pane d-flex flex-column',
				className
			)}
		>
			<div className="align-items-center c-gap-3 d-flex p-3">
				<Picker
					aria-label={
						selectedVersion === null
							? Liferay.Language.get(
									'select-a-version-for-comparison'
								)
							: sub(
									Liferay.Language.get(
										'select-a-version.-current-version-x'
									),
									selectedVersion
								)
					}
					as={VersionPickerTrigger}
					items={versions.map((item) => ({
						label: getVersionLabel(getVersionNumber(item)),
						value: String(getVersionNumber(item)),
					}))}
					onSelectionChange={(key) => {
						setIframeStatus('loading');
						onVersionChange(Number(key));
					}}
					placeholder={`--${Liferay.Language.get('not-selected')}--`}
					selectedKey={
						selectedVersion === null
							? undefined
							: String(selectedVersion)
					}
				>
					{(item: {label: string; value: string}) => (
						<Option key={item.value}>{item.label}</Option>
					)}
				</Picker>

				{selectedItem ? (
					<>
						<span className="ml-auto text-3 text-secondary text-truncate">
							{sub(Liferay.Language.get('modified-by-x'), [
								selectedItem.creator.name,
							])}

							{` ${dateUtils.format(
								new Date(selectedItem.dateModified),
								'P p'
							)}`}
						</span>

						<StatusLabel label={selectedItem.status.label} />
					</>
				) : null}
			</div>

			<div className="cms-compare-versions-pane-content d-flex flex-column flex-grow-1 mx-2">
				{selectedVersion !== null && iframeStatus === 'loading' ? (
					<ClayLoadingIndicator className="my-5" />
				) : null}

				{selectedVersion !== null ? (
					<iframe
						className="border-0 flex-grow-1 w-100"
						onLoad={() => setIframeStatus('loaded')}
						src={`${VIEW_CONTENT_VERSION_URL}/compare_content_item?objectEntryId=${objectEntryId}&p_p_state=pop_up&version=${selectedVersion}`}
						title={getVersionLabel(selectedVersion)}
					/>
				) : null}
			</div>
		</div>
	);
}

const VersionPickerTrigger = React.forwardRef<HTMLButtonElement, any>(
	({children, ...otherProps}, ref) => (
		<button
			{...otherProps}
			className="form-control form-control-select form-control-select-secondary form-control-sm w-auto"
			ref={ref}
			type="button"
		>
			{children}
		</button>
	)
);
