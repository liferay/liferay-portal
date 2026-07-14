/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import React, {useCallback, useEffect, useId, useRef, useState} from 'react';

import {Space, getSpaces} from '../services/getSpaces';

interface SpaceSelectionModalContentProps {
	onSelectSpace: (groupId: number | null) => void;
}

const SpaceSelectionModalContent: React.FC<SpaceSelectionModalContentProps> = ({
	onSelectSpace,
}) => {
	const [spaces, setSpaces] = useState<Space[]>([]);
	const [groupId, setGroupId] = useState<number | undefined>();

	const onSelectSpaceRef = useRef(onSelectSpace);
	const resolvedRef = useRef<boolean>(false);
	const selectId = useId();

	useEffect(() => {
		onSelectSpaceRef.current = onSelectSpace;
	});

	const resolve = useCallback((selectedGroupId: number | null) => {
		if (!resolvedRef.current) {
			resolvedRef.current = true;

			onSelectSpaceRef.current(selectedGroupId);
		}
	}, []);

	const {observer} = useModal({
		onClose: () => resolve(null),
	});

	useEffect(() => {
		getSpaces()
			.then((loadedSpaces) => {
				if (!loadedSpaces.length) {
					Liferay.Util.openToast({
						message: Liferay.Language.get(
							'there-are-no-spaces-available-to-save-the-image'
						),
						type: 'info',
					});

					resolve(null);

					return;
				}

				if (loadedSpaces.length === 1) {
					resolve(loadedSpaces[0].siteId);

					return;
				}

				setSpaces(loadedSpaces);
				setGroupId(loadedSpaces[0].siteId);
			})
			.catch(() => {
				Liferay.Util.openToast({
					message: Liferay.Language.get(
						'the-spaces-could-not-be-loaded'
					),
					type: 'danger',
				});

				resolve(null);
			});
	}, [resolve]);

	if (!spaces.length) {
		return null;
	}

	return (
		<ClayModal observer={observer} size="sm">
			<ClayForm
				onSubmit={(event) => {
					event.preventDefault();

					if (groupId) {
						resolve(groupId);
					}
				}}
			>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{Liferay.Language.get('select-a-space')}
				</ClayModal.Header>

				<ClayModal.Body>
					<ClayForm.Group>
						<label htmlFor={selectId}>
							{Liferay.Language.get('space')}
						</label>

						<ClaySelectWithOption
							id={selectId}
							onChange={(event) =>
								setGroupId(Number(event.target.value))
							}
							options={spaces.map((space) => ({
								label: space.name,
								value: String(space.siteId),
							}))}
							value={String(groupId)}
						/>
					</ClayForm.Group>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={() => resolve(null)}
								type="button"
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								disabled={!groupId}
								displayType="primary"
								type="submit"
							>
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</ClayModal>
	);
};

export default SpaceSelectionModalContent;
