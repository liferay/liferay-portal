/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {
	Launch,
	createLaunch,
	createLaunchEntry,
	getLaunch,
	listLaunchEntriesForAsset,
	listLaunches,
} from '../api/launches';

import type {Observer} from '@clayui/modal/src/types';

const ADD_TO_LAUNCH_EVENT = 'addToLaunch';

interface AddToLaunchEventDetail {
	className: string;
	classPK: number;
	classVersion: string;
}

const CREATE_NEW_VALUE = 'create-new-launch';

type Status = 'already-added' | 'loading' | 'picking';

export default function AddToLaunchModal() {
	const {observer, onOpenChange, open} = useModal({
		onClose: () => onOpenChange(false),
	});

	const [detail, setDetail] = useState<AddToLaunchEventDetail | null>(null);

	useEffect(() => {
		const handleAddToLaunch = (eventDetail: AddToLaunchEventDetail) => {
			setDetail(eventDetail);
			onOpenChange(true);
		};

		Liferay.on(ADD_TO_LAUNCH_EVENT, handleAddToLaunch);

		return () => Liferay.detach(ADD_TO_LAUNCH_EVENT, handleAddToLaunch);
	}, [onOpenChange]);

	if (!open || !detail) {
		return null;
	}

	return (
		<AddToLaunchModalContent
			detail={detail}
			observer={observer}
			onClose={() => onOpenChange(false)}
		/>
	);
}

interface AddToLaunchModalContentProps {
	detail: AddToLaunchEventDetail;
	observer: Observer;
	onClose: () => void;
}

function AddToLaunchModalContent({
	detail,
	observer,
	onClose,
}: AddToLaunchModalContentProps) {
	const [status, setStatus] = useState<Status>('loading');
	const [error, setError] = useState<string | null>(null);
	const [submitting, setSubmitting] = useState(false);

	const [existingLaunchName, setExistingLaunchName] = useState<string | null>(
		null
	);

	const [launches, setLaunches] = useState<Launch[]>([]);
	const [selectedValue, setSelectedValue] = useState<string>('');
	const [newLaunchName, setNewLaunchName] = useState('');
	const [newLaunchDescription, setNewLaunchDescription] = useState('');

	useEffect(() => {
		let canceled = false;

		async function load() {
			try {
				const launchEntries = await listLaunchEntriesForAsset(detail);

				if (canceled) {
					return;
				}

				if (launchEntries.length) {
					const launch = await getLaunch(
						launchEntries[0]
							.r_launchSetToLaunchEntries_c_launchSetId
					);

					if (canceled) {
						return;
					}

					setExistingLaunchName(launch.name);
					setStatus('already-added');

					return;
				}

				const launches = await listLaunches();

				if (canceled) {
					return;
				}

				setLaunches(launches);
				setStatus('picking');
			}
			catch (exception) {
				if (!canceled) {
					setError((exception as Error).message);
				}
			}
		}

		load();

		return () => {
			canceled = true;
		};
	}, [detail]);

	const handleAdd = async () => {
		if (!selectedValue) {
			setError(
				sub(
					Liferay.Language.get('the-x-field-is-required'),
					Liferay.Language.get('launch')
				)
			);

			return;
		}

		const creatingNew = selectedValue === CREATE_NEW_VALUE;

		const trimmedName = newLaunchName.trim();

		if (creatingNew && !trimmedName) {
			setError(
				sub(
					Liferay.Language.get('the-x-field-is-required'),
					Liferay.Language.get('name')
				)
			);

			return;
		}

		setError(null);
		setSubmitting(true);

		try {
			const launchId = creatingNew
				? (
						await createLaunch({
							description: newLaunchDescription.trim(),
							name: trimmedName,
						})
					).id
				: Number(selectedValue);

			await createLaunchEntry({...detail, launchSetId: launchId});

			const launch = creatingNew
				? {id: launchId, name: trimmedName}
				: launches.find((candidate) => candidate.id === launchId);

			openToast({
				message: sub(
					Liferay.Language.get('the-item-was-added-to-x'),
					launch?.name || Liferay.Language.get('launch')
				),
				type: 'success',
			});

			onClose();
		}
		catch (exception) {
			setError((exception as Error).message);
		}
		finally {
			setSubmitting(false);
		}
	};

	return (
		<ClayModal observer={observer}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('add-to-launch')}
			</ClayModal.Header>

			<ClayModal.Body>
				{status === 'loading' && (
					<div className="text-center">
						<span className="loading-animation" />
					</div>
				)}

				{status === 'already-added' && (
					<ClayAlert displayType="info">
						{sub(
							Liferay.Language.get(
								'this-item-already-belongs-to-x'
							),
							existingLaunchName
						)}
					</ClayAlert>
				)}

				{status === 'picking' && (
					<ClayForm.Group>
						<label htmlFor="addToLaunchSelect">
							{Liferay.Language.get('select-a-launch')}
						</label>

						<ClaySelectWithOption
							id="addToLaunchSelect"
							onChange={(event) =>
								setSelectedValue(event.target.value)
							}
							options={[
								{
									label: Liferay.Language.get('select'),
									value: '',
								},
								...launches.map((launch) => ({
									label: launch.name,
									value: String(launch.id),
								})),
								{
									label: Liferay.Language.get(
										'create-a-new-launch'
									),
									value: CREATE_NEW_VALUE,
								},
							]}
							value={selectedValue}
						/>

						{selectedValue === CREATE_NEW_VALUE && (
							<div className="mt-3">
								<ClayForm.Group>
									<label htmlFor="addToLaunchNewName">
										{Liferay.Language.get('name')}
									</label>

									<ClayInput
										id="addToLaunchNewName"
										onChange={(event) =>
											setNewLaunchName(event.target.value)
										}
										placeholder={Liferay.Language.get(
											'untitled-launch'
										)}
										value={newLaunchName}
									/>
								</ClayForm.Group>

								<ClayForm.Group>
									<label htmlFor="addToLaunchNewDescription">
										{Liferay.Language.get('description')}
									</label>

									<ClayInput
										component="textarea"
										id="addToLaunchNewDescription"
										onChange={(event) =>
											setNewLaunchDescription(
												event.target.value
											)
										}
										value={newLaunchDescription}
									/>
								</ClayForm.Group>
							</div>
						)}
					</ClayForm.Group>
				)}

				{error && (
					<ClayAlert
						displayType="warning"
						onClose={() => setError(null)}
					>
						{error}
					</ClayAlert>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						{status === 'picking' && (
							<ClayButton
								disabled={submitting}
								onClick={handleAdd}
							>
								{Liferay.Language.get('add')}
							</ClayButton>
						)}
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
