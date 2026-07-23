/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayPopover from '@clayui/popover';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {Toggle} from '@liferay/object-js-components-web';
import {LearnMessage} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {
	getSettingValue,
	setSettingValue,
} from '../../utils/objectDefinitionSettings';

import './ConfigurationContainer.scss';

const ALLOW_STANDALONE_OBJECT_ENTRY = 'allowStandaloneObjectEntry';

interface ConfigurationContainerProps {
	hasStandaloneEntries?: boolean;
	hasUpdateObjectDefinitionPermission: boolean;
	isApproved: boolean;
	isEnableObjectEntrySchedule: boolean;
	isLinkedObjectDefinition?: boolean;
	isRootDescendantNode?: boolean;
	onSubmit?: (editedObjectDefinition?: Partial<ObjectDefinition>) => void;
	setValues: (values: Partial<ObjectDefinition>) => void;
	values: Partial<ObjectDefinition>;
}

export function ConfigurationContainer({
	hasStandaloneEntries,
	hasUpdateObjectDefinitionPermission,
	isApproved,
	isEnableObjectEntrySchedule,
	isLinkedObjectDefinition,
	isRootDescendantNode,
	onSubmit,
	setValues,
	values,
}: ConfigurationContainerProps) {
	const [showAllowStandalonePopover, setShowAllowStandalonePopover] =
		useState(false);
	const isCommentsEnabled =
		values.scope === 'site' || Liferay.FeatureFlags['LPD-43996'];

	const isReadOnly = !values.modifiable && values.system;

	const disabled =
		!hasUpdateObjectDefinitionPermission ||
		isLinkedObjectDefinition ||
		isReadOnly;

	return (
		<div className="lfr-objects__object-definition-details-configuration">
			<ClayForm.Group>
				<Toggle
					disabled={disabled}
					label={sub(
						Liferay.Language.get('show-widget-in-x'),
						Liferay.Language.get('page-builder')
					)}
					name="showWidget"
					onBlur={(event) => {
						event.stopPropagation();

						if (onSubmit) {
							onSubmit();
						}
					}}
					onToggle={() => setValues({portlet: !values.portlet})}
					toggled={values.portlet}
				/>
			</ClayForm.Group>

			<ClayForm.Group>
				<Toggle
					disabled={disabled}
					label={sub(
						Liferay.Language.get('enable-x'),
						Liferay.Language.get('categorization-of-object-entries')
					)}
					name="enableCategorization"
					onBlur={(event) => {
						event.stopPropagation();

						if (onSubmit) {
							onSubmit();
						}
					}}
					onToggle={() =>
						setValues({
							enableCategorization: !values.enableCategorization,
						})
					}
					toggled={values.enableCategorization}
				/>
			</ClayForm.Group>

			<ClayForm.Group className="lfr-objects__comments-enable-comments">
				<Toggle
					disabled={disabled}
					label={sub(
						Liferay.Language.get('enable-x'),
						isCommentsEnabled
							? Liferay.Language.get('comments')
							: Liferay.Language.get('comments-in-page-builder')
					)}
					name="enableComments"
					onBlur={(event) => {
						event.stopPropagation();

						if (onSubmit) {
							onSubmit();
						}
					}}
					onToggle={() =>
						setValues({
							enableComments: !values.enableComments,
						})
					}
					toggled={values.enableComments}
				/>

				{isCommentsEnabled && (
					<>
						&nbsp;
						<ClayTooltipProvider>
							<span
								title={Liferay.Language.get(
									'you-can-manage-comments-in-the-headless-api-and-the-page-builder'
								)}
							>
								<ClayIcon
									className="lfr-objects__comments-tooltip-icon"
									symbol="question-circle-full"
								/>
							</span>
						</ClayTooltipProvider>
					</>
				)}
			</ClayForm.Group>

			<ClayForm.Group>
				<Toggle
					disabled={disabled || isApproved}
					label={sub(
						Liferay.Language.get('enable-x'),
						Liferay.Language.get('indexed-search')
					)}
					name="enableIndexSearch"
					onBlur={(event) => {
						event.stopPropagation();

						if (onSubmit) {
							onSubmit();
						}
					}}
					onToggle={() =>
						setValues({
							enableIndexSearch: !values.enableIndexSearch,
						})
					}
					toggled={values.enableIndexSearch}
				/>
			</ClayForm.Group>

			<ClayForm.Group>
				<Toggle
					disabled={isLinkedObjectDefinition || isReadOnly}
					label={sub(
						Liferay.Language.get('enable-x'),
						Liferay.Language.get('entry-history-in-audit-framework')
					)}
					name="enableEntryHistory"
					onBlur={(event) => {
						event.stopPropagation();

						if (onSubmit) {
							onSubmit();
						}
					}}
					onToggle={() =>
						setValues({
							enableObjectEntryHistory:
								!values.enableObjectEntryHistory,
						})
					}
					toggled={values.enableObjectEntryHistory}
				/>
			</ClayForm.Group>

			<ClayForm.Group>
				<Toggle
					disabled={disabled}
					label={Liferay.Language.get(
						'allow-users-to-save-entries-as-draft'
					)}
					name="enableObjectEntryDraft"
					onBlur={(event) => {
						event.stopPropagation();

						if (onSubmit) {
							onSubmit();
						}
					}}
					onToggle={() =>
						setValues({
							enableObjectEntryDraft:
								!values.enableObjectEntryDraft,
						})
					}
					toggled={values.enableObjectEntryDraft}
				/>
			</ClayForm.Group>

			{isRootDescendantNode &&
				(() => {
					const allowStandaloneObjectEntry =
						(getSettingValue(
							values.objectDefinitionSettings,
							ALLOW_STANDALONE_OBJECT_ENTRY
						) ?? 'true') === 'true';

					const standaloneDisabledByEntries =
						allowStandaloneObjectEntry && hasStandaloneEntries;

					return (
						<ClayForm.Group
							className="lfr-objects__allow-standalone-entries"
							onMouseLeave={() =>
								setShowAllowStandalonePopover(false)
							}
						>
							<Toggle
								disabled={
									disabled || standaloneDisabledByEntries
								}
								label={Liferay.Language.get(
									'allow-standalone-entries'
								)}
								name={ALLOW_STANDALONE_OBJECT_ENTRY}
								onBlur={(event) => {
									event.stopPropagation();

									if (onSubmit) {
										onSubmit();
									}
								}}
								onToggle={() => {
									setValues({
										objectDefinitionSettings:
											setSettingValue(
												values.objectDefinitionSettings,
												ALLOW_STANDALONE_OBJECT_ENTRY,
												allowStandaloneObjectEntry
													? 'false'
													: 'true'
											),
									});
								}}
								toggled={allowStandaloneObjectEntry}
							/>

							<ClayPopover
								alignPosition="top"
								closeOnClickOutside={true}
								disableScroll
								header={
									standaloneDisabledByEntries
										? Liferay.Language.get(
												'disabling-standalone-entries-not-allowed'
											)
										: Liferay.Language.get(
												'standalone-entries'
											)
								}
								onMouseLeave={() =>
									setShowAllowStandalonePopover(false)
								}
								onMouseOver={() =>
									setShowAllowStandalonePopover(true)
								}
								onShowChange={setShowAllowStandalonePopover}
								show={showAllowStandalonePopover}
								trigger={
									<ClayIcon
										aria-label={Liferay.Language.get(
											'help-text'
										)}
										className="lfr-objects__allow-standalone-entries-tooltip-icon"
										onFocus={() =>
											setShowAllowStandalonePopover(true)
										}
										onMouseOver={() =>
											setShowAllowStandalonePopover(true)
										}
										symbol="question-circle-full"
									/>
								}
							>
								{standaloneDisabledByEntries
									? Liferay.Language.get(
											'this-object-has-existing-standalone-entries'
										)
									: Liferay.Language.get(
											'when-enabled-you-can-create-entries-without-a-parent-object'
										)}
								&nbsp;
								<LearnMessage
									resource="object-web"
									resourceKey="inheritance-relationships"
								/>
							</ClayPopover>
						</ClayForm.Group>
					);
				})()}

			<>
				<ClayForm.Group>
					<Toggle
						disabled={
							disabled ||
							(isEnableObjectEntrySchedule && isApproved)
						}
						label={Liferay.Language.get(
							'allow-users-to-schedule-a-display-expiration-and-review-date-for-entries'
						)}
						name="enableObjectEntrySchedule"
						onBlur={(event) => {
							event.stopPropagation();

							if (onSubmit) {
								onSubmit();
							}
						}}
						onToggle={() => {
							setValues({
								enableObjectEntrySchedule:
									!values.enableObjectEntrySchedule,
							});
						}}
						toggled={values.enableObjectEntrySchedule}
					/>
				</ClayForm.Group>

				<ClayForm.Group>
					<Toggle
						disabled={disabled}
						label={sub(
							Liferay.Language.get('enable-x'),
							Liferay.Language.get('mapping-in-form-container')
						)}
						name="enableFormContainer"
						onBlur={(event) => {
							event.stopPropagation();

							if (onSubmit) {
								onSubmit();
							}
						}}
						onToggle={() => {
							setValues({
								enableFormContainer:
									!values.enableFormContainer,
							});
						}}
						toggled={values.enableFormContainer}
					/>
				</ClayForm.Group>
			</>
		</div>
	);
}
