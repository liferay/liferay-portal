/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useCallback, useContext, useEffect} from 'react';

import {ensureLeadingSlash} from '../common/utils/ensureLeadingSlash';
import {IRoomContext, IRoomStepProps} from '../common/utils/types';
import FieldErrorMessage from './FieldErrorMessage';
import {RoomContext} from './RoomInitializer';

function RoomNameStep({
	numberOfSteps,
	setHandleStepSubmit,
	showHeader = true,
	step = 3,
}: IRoomStepProps) {
	const {dataContext, loading, setDataContext} =
		useContext<IRoomContext>(RoomContext);

	const handleRoomNameChange = useCallback(
		({target: {value}}: React.ChangeEvent<HTMLInputElement>) => {
			setDataContext((prevState) => ({
				...prevState,
				errors: {
					...prevState.errors,
					roomName: value
						? null
						: Liferay.Language.get('this-field-is-mandatory'),
				},
				roomName: value,
			}));
		},
		[setDataContext]
	);

	const handleFriendlyURLChange = useCallback(
		({target: {value}}: React.ChangeEvent<HTMLInputElement>) => {
			setDataContext((prevState) => ({
				...prevState,
				friendlyURL: value,
			}));
		},
		[setDataContext]
	);

	const handleFriendlyURLBlur = useCallback(
		({target: {value}}: React.FocusEvent<HTMLInputElement>) => {
			setDataContext((prevState) => ({
				...prevState,
				friendlyURL: ensureLeadingSlash(value),
			}));
		},
		[setDataContext]
	);

	useEffect(() => {
		setHandleStepSubmit(() => async (event: Event): Promise<boolean> => {
			event.preventDefault();

			if (!dataContext.roomName) {
				setDataContext((prevState) => ({
					...prevState,
					errors: {
						...prevState.errors,
						roomName: Liferay.Language.get(
							'this-field-is-mandatory'
						),
					},
				}));

				return Promise.resolve(false);
			}

			return Promise.resolve(true);
		});
	}, [dataContext.roomName, setDataContext, setHandleStepSubmit]);

	return (
		<>
			{showHeader && (
				<div>
					<div
						className="mb-1 text-secondary"
						data-qa-id="stepLocator"
					>
						{sub(
							Liferay.Language.get('step-x-of-x'),
							step,
							numberOfSteps
						)}
					</div>

					<div
						className="mb-1 text-6 text-weight-bold"
						data-qa-id="stepTitle"
					>
						{Liferay.Language.get('name-your-room')}
					</div>

					<div className="text-secondary">
						{Liferay.Language.get(
							'give-your-room-a-clear-name-and-a-custom-url-to-make-it-easily-recognizable-and-accessible'
						)}
					</div>
				</div>
			)}

			<div className="mt-4">
				<ClayForm.Group
					className={classNames({
						'has-error': !!dataContext.errors.roomName,
					})}
				>
					<label className="d-block" htmlFor="dsr-room-name">
						{Liferay.Language.get('room-name')}

						<span className="c-ml-2 reference-mark">
							<ClayIcon symbol="asterisk" />
						</span>
					</label>

					<ClayInput
						aria-label={Liferay.Language.get('room-name')}
						data-qa-id="roomNameInput"
						disabled={loading}
						id="dsr-room-name"
						name="roomName"
						onChange={handleRoomNameChange}
						required={true}
						type="text"
						value={dataContext.roomName || ''}
					/>

					<FieldErrorMessage
						error={dataContext.errors.roomName}
						name="roomName"
					/>
				</ClayForm.Group>

				<ClayForm.Group className="mt-3">
					<label className="d-block" htmlFor="dsr-friendly-url">
						{Liferay.Language.get('friendly-url')}
					</label>

					<div className="mb-1 text-2 text-secondary">
						{Liferay.ThemeDisplay.getPortalURL() +
							Liferay.ThemeDisplay.getPathFriendlyURLPublic()}
					</div>

					<ClayInput
						aria-label={Liferay.Language.get('friendly-url')}
						data-qa-id="friendlyURLInput"
						disabled={loading}
						id="dsr-friendly-url"
						name="friendlyURL"
						onBlur={handleFriendlyURLBlur}
						onChange={handleFriendlyURLChange}
						type="text"
						value={dataContext.friendlyURL || ''}
					/>
				</ClayForm.Group>
			</div>
		</>
	);
}

export default RoomNameStep;
