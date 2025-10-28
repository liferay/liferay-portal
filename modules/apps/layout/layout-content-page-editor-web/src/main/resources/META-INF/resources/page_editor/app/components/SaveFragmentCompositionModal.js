/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayCard from '@clayui/card';
import ClayForm, {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayModal, {useModal} from '@clayui/modal';
import ClaySticker from '@clayui/sticker';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

import Button from '../../common/components/Button';
import InvisibleFieldset from '../../common/components/InvisibleFieldset';
import {openImageSelector} from '../../common/openImageSelector';
import {config} from '../config/index';
import {useActiveItemIds} from '../contexts/ControlsContext';
import {useDispatch, useSelector} from '../contexts/StoreContext';
import addFragmentComposition from '../thunks/addFragmentComposition';

const SaveFragmentCompositionModal = ({itemId, onCloseModal}) => {
	const dispatch = useDispatch();

	const activeItemIds = useActiveItemIds();

	const [activeItemId] = activeItemIds;

	const isMounted = useIsMounted();

	const collections = useSelector((state) => state.collections || []);

	const [name, setName] = useState(undefined);
	const [description, setDescription] = useState('');
	const [fragmentCollectionId, setFragmentCollectionId] = useState(
		collections.length ? collections[0].fragmentCollectionId : -1
	);

	const [saveInlineContent, setSaveInlineContent] = useState(false);
	const [saveMappingConfiguration, setSaveMappingConfiguration] =
		useState(false);

	const {observer, onClose} = useModal({
		onClose: () => {
			if (isMounted()) {
				onCloseModal();
			}
		},
	});

	const [loading, setLoading] = useState(false);
	const [thumbnail, setThumbnail] = useState({});

	const handleSubmit = (event) => {
		event.preventDefault();

		if (!name) {
			setName('');
		}
		else {
			setLoading(true);

			dispatch(
				addFragmentComposition({
					description,
					fileEntryId: thumbnail.fileEntryId,
					fragmentCollectionId,
					itemId: itemId || activeItemId,
					name,
					saveInlineContent,
					saveMappingConfiguration,
				})
			)
				.then(() => {
					onClose();
				})
				.catch(() => {
					setLoading(false);
				});
		}
	};

	const handleThumbnailSelected = (image) => {
		setThumbnail(image);
	};

	const nameInputId = `${config.portletNamespace}fragmentCompositionName`;
	const descriptionInputId = `${config.portletNamespace}fragmentCompositionDescription`;

	return (
		<ClayModal
			className="page-editor__save-fragment-composition-modal page-editor__theme-adapter-buttons page-editor__theme-adapter-forms"
			containerProps={{className: 'cadmin'}}
			observer={observer}
			size="lg"
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('save-as-fragment')}
			</ClayModal.Header>

			<ClayModal.Body scrollable>
				<ClayForm
					autoComplete="off"
					className="mb-3"
					noValidate
					onSubmit={handleSubmit}
				>
					<InvisibleFieldset disabled={loading}>
						<ClayForm.Group
							className={name === '' ? 'has-error mb-3' : 'mb-3'}
						>
							<label htmlFor={nameInputId}>
								{Liferay.Language.get('name')}

								<ClayIcon
									className="ml-1 reference-mark"
									focusable="false"
									role="presentation"
									symbol="asterisk"
								/>
							</label>

							<ClayInput
								autoFocus
								id={nameInputId}
								maxlength={
									config.fragmentCompositionNameMaxLength
								}
								onChange={(event) =>
									setName(event.target.value)
								}
								onClick={(event) => {
									if (Liferay.Browser.isFirefox()) {
										event.target.focus();
									}
								}}
								placeholder={Liferay.Language.get('name')}
								required
								type="text"
								value={name || ''}
							/>

							{name === '' && (
								<ClayForm.FeedbackGroup>
									<ClayForm.FeedbackItem>
										<ClayForm.FeedbackIndicator symbol="exclamation-full" />

										{Liferay.Language.get(
											'this-field-is-required'
										)}
									</ClayForm.FeedbackItem>
								</ClayForm.FeedbackGroup>
							)}
						</ClayForm.Group>

						<ClayForm.Group>
							<ClayInput.Group>
								<ClayInput.GroupItem shrink>
									<ClayButton
										displayType="secondary"
										onClick={() =>
											openImageSelector(
												handleThumbnailSelected
											)
										}
										size="sm"
										value={Liferay.Language.get(
											'upload-thumbnail'
										)}
									>
										<ClayIcon
											className="mr-2"
											focusable="false"
											monospaced="true"
											role="presentation"
											symbol="upload"
										/>

										{Liferay.Language.get(
											'upload-thumbnail'
										)}
									</ClayButton>
								</ClayInput.GroupItem>

								<ClayInput.GroupItem className="align-items-center">
									<span className="ml-2 text-truncate">
										{thumbnail.title}
									</span>
								</ClayInput.GroupItem>
							</ClayInput.Group>
						</ClayForm.Group>

						<ClayForm.Group>
							<label htmlFor={descriptionInputId}>
								{Liferay.Language.get('description')}
							</label>

							<ClayInput
								component="textarea"
								maxlength={
									config.fragmentCompositionDescriptionMaxLength
								}
								onChange={(event) =>
									setDescription(event.target.value)
								}
								placeholder={Liferay.Language.get(
									'description'
								)}
								type="text"
								value={description}
							/>
						</ClayForm.Group>

						<ClayForm.Group>
							<ClayInput.Group className="input-group-stacked-sm-down">
								<ClayInput.GroupItem className="mr-4" shrink>
									<ClayCheckbox
										checked={saveInlineContent}
										id={`${config.portletNamespace}saveInlineContent`}
										label={Liferay.Language.get(
											'save-inline-content'
										)}
										onChange={(event) =>
											setSaveInlineContent(
												event.target.checked
											)
										}
									/>
								</ClayInput.GroupItem>

								<ClayInput.GroupItem>
									<ClayCheckbox
										checked={saveMappingConfiguration}
										id={`${config.portletNamespace}saveMappingConfiguration`}
										label={Liferay.Language.get(
											'save-mapping-configuration-and-link'
										)}
										onChange={(event) =>
											setSaveMappingConfiguration(
												event.target.checked
											)
										}
									/>
								</ClayInput.GroupItem>
							</ClayInput.Group>
						</ClayForm.Group>

						<ClayForm.Group>
							{collections.length ? (
								<>
									<p className="sheet-tertiary-title">
										{Liferay.Language.get(
											'select-fragment-set'
										)}
									</p>

									<ClayLayout.Row>
										{collections.map((collection) => (
											<ClayLayout.Col
												key={
													collection.fragmentCollectionId
												}
												md="4"
											>
												<ClayCard
													className={
														fragmentCollectionId ===
														collection.fragmentCollectionId
															? 'active'
															: ''
													}
													horizontal
													interactive
													onClick={() =>
														setFragmentCollectionId(
															collection.fragmentCollectionId
														)
													}
												>
													<ClayCard.Body>
														<ClayCard.Row>
															<ClayLayout.ContentCol containerElement="span">
																<ClaySticker
																	inline
																>
																	<ClayIcon symbol="folder" />
																</ClaySticker>
															</ClayLayout.ContentCol>

															<ClayLayout.ContentCol
																containerElement="span"
																expand
															>
																<ClayLayout.ContentSection containerElement="span">
																	<ClayCard.Description
																		displayType="title"
																		truncate
																	>
																		{
																			collection.name
																		}
																	</ClayCard.Description>
																</ClayLayout.ContentSection>
															</ClayLayout.ContentCol>
														</ClayCard.Row>
													</ClayCard.Body>
												</ClayCard>
											</ClayLayout.Col>
										))}
									</ClayLayout.Row>
								</>
							) : (
								<div className="alert alert-info">
									<ClayIcon
										className="inline-item inline-item-after mr-2 reference-mark"
										focusable="false"
										role="presentation"
										symbol="exclamation-full"
									/>

									{Liferay.Language.get(
										'this-fragment-will-be-saved-in-a-new-fragment-set-called-saved-fragments'
									)}
								</div>
							)}
						</ClayForm.Group>
					</InvisibleFieldset>
				</ClayForm>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							disabled={loading}
							displayType="secondary"
							onClick={onClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<Button
							disabled={loading}
							displayType="primary"
							loading={loading}
							onClick={handleSubmit}
						>
							{Liferay.Language.get('save')}
						</Button>
					</ClayButton.Group>
				}
			></ClayModal.Footer>
		</ClayModal>
	);
};

SaveFragmentCompositionModal.propTypes = {
	itemId: PropTypes.string,
	onCloseModal: PropTypes.func.isRequired,
};

export default SaveFragmentCompositionModal;
