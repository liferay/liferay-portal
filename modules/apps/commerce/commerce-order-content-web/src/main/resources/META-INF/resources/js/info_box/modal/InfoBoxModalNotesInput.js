/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {ClayInput, ClayToggle} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {InfiniteScrollerComponent} from 'commerce-frontend-js';
import {dateUtils} from 'frontend-js-web';
import React from 'react';

import UserIcon from '../../UserIcon';

const InfoBoxModalNotes = ({
	handleDelete,
	handleToggle,
	hasManageOrderNotesPermission,
	hasManageOrderRestrictedNotesPermission,
	isRestricted,
	notes,
	setInputValue,
	spritemap,
}) => {
	const formatUTCDate = (value) => {
		const date = new Date(value);

		date.setMilliseconds(0);

		return dateUtils.fromNow(date);
	};

	return (
		<>
			<InfiniteScrollerComponent maxHeight="300px" scrollCompleted>
				<div className="lfr-discussion-body">
					{notes.map((note, index) => {
						const {
							author,
							authorId,
							authorPortraitURL,
							content,
							id,
							modifiedDate,
							restricted,
						} = note;

						return (
							<div className="panel-body" key={note.id}>
								<div className="card-row d-flex mb-2">
									<UserIcon
										fullName={author}
										portraitURL={authorPortraitURL}
										userId={authorId}
									/>

									<div
										className="flex-grow-1 pl-2"
										key={index}
									>
										<div className="d-flex mb-1">
											<span className="author">
												{author}

												{themeDisplay.getUserId() ===
													authorId.toString() &&
													` (${Liferay.Language.get('you')})`}
											</span>

											<span className="d-flex m-1 small">
												{formatUTCDate(modifiedDate)}
											</span>
										</div>

										<div>
											{restricted && (
												<ClayIcon
													spritemap={spritemap}
													symbol="lock"
												/>
											)}
										</div>

										<div className="d-flex mb-2">
											{content}
										</div>
									</div>

									<div className="card-col">
										<ClayDropDown
											trigger={
												<ClayButtonWithIcon
													aria-label={Liferay.Language.get(
														'more-actions'
													)}
													className="btn-outline-borderless btn-outline-secondary"
													displayType="unstyled"
													symbol="ellipsis-v"
												/>
											}
										>
											<ClayDropDown.ItemList>
												<ClayDropDown.Item
													onClick={() => {
														handleDelete(id);
													}}
												>
													{Liferay.Language.get(
														'delete'
													)}
												</ClayDropDown.Item>
											</ClayDropDown.ItemList>
										</ClayDropDown>
									</div>
								</div>

								<hr className="mt-1 separator" />
							</div>
						);
					})}
				</div>
			</InfiniteScrollerComponent>

			{hasManageOrderNotesPermission && (
				<ClayInput.Group className="commerce-panel">
					<ClayInput.GroupItem>
						<ClayInput
							aria-required={true}
							className="field form-control lfr-textarea"
							component="textarea"
							id="infoBoxModalInput"
							maxLength={4000}
							onChange={(event) => {
								event.preventDefault();
								setInputValue(event.target.value);
							}}
							placeholder={Liferay.Language.get(
								'type-your-note-here'
							)}
							required={true}
							type="text"
						/>

						<span className="hide-accessible sr-only">
							{Liferay.Language.get('required')}
						</span>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			)}

			{hasManageOrderRestrictedNotesPermission && (
				<div className="form-group inline-item">
					<ClayToggle
						label={Liferay.Language.get('private')}
						onToggle={handleToggle}
						spritemap={spritemap}
						toggled={isRestricted}
					/>

					<ClayButtonWithIcon
						className="lfr-portal-tooltip ml-1 taglib-icon-help"
						displayType="unstyled"
						symbol="question-circle-full"
						title={Liferay.Language.get('restricted-help')}
					/>
				</div>
			)}
		</>
	);
};

export default InfoBoxModalNotes;
