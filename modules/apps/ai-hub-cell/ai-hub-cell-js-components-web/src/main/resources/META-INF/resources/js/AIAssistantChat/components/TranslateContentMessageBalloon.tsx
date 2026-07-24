/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayCheckbox} from '@clayui/form';
import ClayMultiSelect from '@clayui/multi-select';
import React from 'react';

import LanguageIdIcon from '../../TranslateContent/components/LanguageIdIcon';
import MessageBalloon from '../../TranslateContent/components/MessageBalloon';
import MessageHeader from '../../TranslateContent/components/MessageHeader';
import {TranslateContentMessageBalloonProps} from '../../TranslateContent/types';
import useTranslateContentAgent from '../../TranslateContent/useTranslateContentAgent';

import '../chat.scss';

const TranslateContentMessageBalloon: React.FC<
	TranslateContentMessageBalloonProps
> = (props) => {
	const {availableLanguageIds, results} = props;

	const {
		confirmDisabled,
		onTranslate,
		overwrite,
		overwriteAll,
		overwriteDisabled,
		overwriteLanguageIds,
		review,
		selectDisabled,
		selectedLanguageIds,
		setSelectedLanguageIds,
		setValue,
		showConfirm,
		showReview,
		submitted,
		toggleOverwriteLanguageId,
		toggleSelectedLanguageId,
		translatedLanguageIds,
		value,
	} = useTranslateContentAgent(props);

	if (results?.length) {
		return (
			<MessageBalloon>
				<MessageHeader
					message={Liferay.Language.get(
						'the-content-has-been-translated'
					)}
				/>
			</MessageBalloon>
		);
	}

	return (
		<>
			<MessageBalloon>
				<MessageHeader
					message={Liferay.Language.get(
						'which-languages-would-you-like-to-translate-into'
					)}
				/>

				<div className="ai-assistant-chat__language-select">
					<ClayMultiSelect
						disabled={selectDisabled}
						items={selectedLanguageIds.map((languageId) => ({
							label: languageId,
							value: languageId,
						}))}
						onChange={setValue}
						onItemsChange={(newItems) =>
							setSelectedLanguageIds(
								newItems.map((item) => item.value)
							)
						}
						placeholder={Liferay.Language.get('select-languages')}
						sourceItems={(availableLanguageIds ?? []).map(
							(languageId) => ({
								label: languageId,
								value: languageId,
							})
						)}
						spritemap={Liferay.Icons.spritemap}
						value={value}
					>
						{(item) => (
							<ClayMultiSelect.Item
								key={item.value}
								onClick={(event) => {
									event.preventDefault();

									toggleSelectedLanguageId(item.value);

									setValue('');
								}}
								style={{cursor: 'pointer'}}
								textValue={item.label}
							>
								<LanguageIdIcon languageId={item.value} />

								{item.label}
							</ClayMultiSelect.Item>
						)}
					</ClayMultiSelect>
				</div>
			</MessageBalloon>

			{!!selectedLanguageIds.length && (
				<div className="ai-assistant-chat__user-action">
					<ClayButton
						disabled={selectDisabled}
						displayType="primary"
						onClick={onTranslate}
						size="sm"
					>
						{Liferay.Language.get('translate')}
					</ClayButton>
				</div>
			)}

			{showConfirm && (
				<>
					<MessageBalloon>
						<MessageHeader
							message={Liferay.Language.get(
								'some-of-the-selected-languages-already-have-a-translation.-what-do-you-want-to-do'
							)}
						/>
					</MessageBalloon>

					<div className="ai-assistant-chat__user-action">
						<ClayButton
							disabled={confirmDisabled}
							displayType="secondary"
							onClick={review}
							size="sm"
						>
							{Liferay.Language.get('review')}
						</ClayButton>

						<ClayButton
							disabled={confirmDisabled}
							displayType="primary"
							onClick={overwriteAll}
							size="sm"
						>
							{Liferay.Language.get('overwrite-all')}
						</ClayButton>
					</div>
				</>
			)}

			{showReview && (
				<>
					<MessageBalloon>
						<MessageHeader
							message={Liferay.Language.get(
								'select-the-translations-you-want-to-overwrite'
							)}
						/>

						<div className="ai-assistant-chat__translation-review">
							{translatedLanguageIds.map((languageId) => (
								<ClayCheckbox
									checked={overwriteLanguageIds.includes(
										languageId
									)}
									disabled={submitted}
									key={languageId}
									label={languageId}
									onChange={() =>
										toggleOverwriteLanguageId(languageId)
									}
								/>
							))}
						</div>
					</MessageBalloon>

					<div className="ai-assistant-chat__user-action">
						<ClayButton
							disabled={overwriteDisabled}
							displayType="primary"
							onClick={overwrite}
							size="sm"
						>
							{Liferay.Language.get('overwrite')}
						</ClayButton>
					</div>
				</>
			)}
		</>
	);
};

export default TranslateContentMessageBalloon;
