/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayTable from '@clayui/table';
import React, {useEffect, useState} from 'react';

import {
	Launch,
	LaunchEntry,
	LaunchEntryContent,
	getLaunch,
	getLaunchEntryContent,
	listLaunchEntries,
} from '../api/launches';

const WORKFLOW_STATUS_APPROVED = 0;
const WORKFLOW_STATUS_DENIED = 4;
const WORKFLOW_STATUS_EXPIRED = 3;

function getEntryStatusLabel(
	status: number,
	launchPublished: boolean
): {displayType: 'danger' | 'info' | 'success'; label: string} {
	if (launchPublished) {
		if (status === WORKFLOW_STATUS_EXPIRED) {
			return {
				displayType: 'danger',
				label: Liferay.Language.get('expired'),
			};
		}

		return {
			displayType: 'success',
			label: Liferay.Language.get('published'),
		};
	}

	if (status === WORKFLOW_STATUS_DENIED) {
		return {displayType: 'danger', label: Liferay.Language.get('rejected')};
	}

	if (status === WORKFLOW_STATUS_APPROVED) {
		return {
			displayType: 'success',
			label: Liferay.Language.get('ready-to-publish'),
		};
	}

	return {displayType: 'info', label: Liferay.Language.get('pending')};
}

interface EntryRow extends LaunchEntry {
	content: LaunchEntryContent;
}

interface Props {
	getLaunchEntryContentResourceURL: string;
	launchId: number;
	onBack: () => void;
	onInvalid: () => void;
	portletNamespace: string;
}

export default function LaunchDetails({
	getLaunchEntryContentResourceURL,
	launchId,
	onBack,
	onInvalid,
	portletNamespace,
}: Props) {
	const [launch, setLaunch] = useState<Launch | null>(null);
	const [entryRows, setEntryRows] = useState<EntryRow[]>([]);
	const [error, setError] = useState<string | null>(null);
	const [loadingEntries, setLoadingEntries] = useState(true);

	useEffect(() => {
		getLaunch(launchId)
			.then(setLaunch)
			.catch((exception: Error) => {
				setError(exception.message);
				onInvalid();
			});
	}, [launchId, onInvalid]);

	useEffect(() => {
		listLaunchEntries(launchId)
			.then((entries) =>
				Promise.all(
					entries.map((entry) =>
						getLaunchEntryContent({
							className: entry.className,
							classPK: entry.classPK,
							classVersion: entry.classVersion,
							portletNamespace,
							resourceURL: getLaunchEntryContentResourceURL,
						})
							.then((content) => ({...entry, content}))
							.catch(() => null)
					)
				)
			)
			.then((rows) =>
				setEntryRows(
					rows.filter((row): row is EntryRow => row !== null)
				)
			)
			.finally(() => setLoadingEntries(false));
	}, [getLaunchEntryContentResourceURL, launchId, portletNamespace]);

	if (error) {
		return (
			<div className="p-4">
				<p className="text-danger">{error}</p>

				<ClayButton displayType="link" onClick={onBack}>
					<ClayIcon className="mr-2" symbol="angle-left" />

					{Liferay.Language.get('back-to-launches')}
				</ClayButton>
			</div>
		);
	}

	if (!launch) {
		return <div className="p-4">{Liferay.Language.get('loading')}</div>;
	}

	const launchPublished = launch.status?.code === WORKFLOW_STATUS_APPROVED;

	return (
		<div className="launch-details">
			<div className="container-fluid p-4">
				<nav aria-label="breadcrumb">
					<ol className="breadcrumb">
						<li className="breadcrumb-item">
							<a
								href="#"
								onClick={(event) => {
									event.preventDefault();
									onBack();
								}}
							>
								{Liferay.Language.get('launches')}
							</a>
						</li>

						<li
							aria-current="page"
							className="active breadcrumb-item"
						>
							{launch.name}
						</li>
					</ol>
				</nav>

				<h1 className="mb-4">{launch.name}</h1>

				{launch.description ? (
					<p className="text-secondary">{launch.description}</p>
				) : null}

				{loadingEntries ? (
					<div>{Liferay.Language.get('loading')}</div>
				) : entryRows.length ? (
					<EntriesTable
						entryRows={entryRows}
						launchPublished={launchPublished}
					/>
				) : (
					<ClayEmptyState
						description={Liferay.Language.get(
							'this-launch-has-no-entries-yet'
						)}
						imgProps={{
							alt: Liferay.Language.get('no-entries'),
						}}
						imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/empty_state.svg`}
						title={Liferay.Language.get('no-entries')}
					/>
				)}
			</div>
		</div>
	);
}

interface EntriesTableProps {
	entryRows: EntryRow[];
	launchPublished: boolean;
}

function EntriesTable({entryRows, launchPublished}: EntriesTableProps) {
	return (
		<ClayTable>
			<ClayTable.Head>
				<ClayTable.Row>
					<ClayTable.Cell headingCell>
						{Liferay.Language.get('title')}
					</ClayTable.Cell>

					<ClayTable.Cell headingCell>
						{Liferay.Language.get('version')}
					</ClayTable.Cell>

					<ClayTable.Cell headingCell>
						{Liferay.Language.get('author')}
					</ClayTable.Cell>

					<ClayTable.Cell headingCell>
						{Liferay.Language.get('type')}
					</ClayTable.Cell>

					<ClayTable.Cell headingCell>
						{Liferay.Language.get('space')}
					</ClayTable.Cell>

					<ClayTable.Cell headingCell>
						{Liferay.Language.get('modified')}
					</ClayTable.Cell>

					<ClayTable.Cell headingCell>
						{Liferay.Language.get('status')}
					</ClayTable.Cell>
				</ClayTable.Row>
			</ClayTable.Head>

			<ClayTable.Body>
				{entryRows.map((entryRow) => {
					const {displayType, label} = getEntryStatusLabel(
						entryRow.content.status,
						launchPublished
					);

					return (
						<ClayTable.Row key={entryRow.id}>
							<ClayTable.Cell>
								<span className="text-truncate-inline">
									{entryRow.content.title}
								</span>
							</ClayTable.Cell>

							<ClayTable.Cell>
								{entryRow.content.version}
							</ClayTable.Cell>

							<ClayTable.Cell>
								{entryRow.content.author}
							</ClayTable.Cell>

							<ClayTable.Cell>
								{entryRow.content.type}
							</ClayTable.Cell>

							<ClayTable.Cell>
								{entryRow.content.space}
							</ClayTable.Cell>

							<ClayTable.Cell>
								{new Date(
									entryRow.content.modified
								).toLocaleString()}
							</ClayTable.Cell>

							<ClayTable.Cell>
								<ClayLabel displayType={displayType}>
									{label}
								</ClayLabel>
							</ClayTable.Cell>
						</ClayTable.Row>
					);
				})}
			</ClayTable.Body>
		</ClayTable>
	);
}
