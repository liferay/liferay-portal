/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayColorPicker from '@clayui/color-picker';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import Sticker from '@clayui/sticker';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {
	ChangeEvent,
	useCallback,
	useContext,
	useEffect,
	useRef,
} from 'react';
import {useDropzone} from 'react-dropzone';

import {DSRContext} from './DSRInitializer';
import {TDSRContext, TDSRRoomDetailsStepProps} from './DSRTypes';
import FieldErrorMessage from './FieldErrorMessage';

export function getBase64(file: File): Promise<string> {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();

		reader.onload = () => resolve(reader.result as string);
		reader.onerror = (error) => reject(error);

		reader.readAsDataURL(file);
	});
}

export function getImage(name: string): string {
	return `${Liferay.ThemeDisplay.getPortalURL()}${Liferay.ThemeDisplay.getPathContext()}/o/site-dsr-site-initializer/images/${name}`;
}

function DSRRoomDetailsStep({
	numberOfSteps,
	setHandleStepSubmit,
	showHeader = true,
	step = 1,
}: TDSRRoomDetailsStepProps) {
	const {dataContext, loading, setDataContext} =
		useContext<TDSRContext>(DSRContext);
	const clientLogoInputFileRef = useRef(null);
	const {getInputProps, getRootProps, isDragActive} = useDropzone({
		accept: ['image/*'],
		disabled: loading,
		multiple: false,
		onDropAccepted: async (acceptedFiles) => {
			if (acceptedFiles && acceptedFiles.length) {
				const file = acceptedFiles[0];
				const bannerBase64 = await getBase64(file);

				setDataContext((prevState) => ({
					...prevState,
					banner: {
						base64: bannerBase64,
						name: file.name,
						size: file.size,
					},
					errors: {
						...prevState.errors,
						banner: null,
					},
				}));
			}
		},
		onDropRejected: async (rejectedFiles) => {
			if (rejectedFiles && rejectedFiles.length) {
				setDataContext((prevState) => ({
					...prevState,
					errors: {
						...prevState.errors,
						banner: Liferay.Language.get('this-file-is-not-valid'),
					},
				}));
			}
		},
	});

	const fieldValid = useCallback(
		(name: string, value: null | string | undefined): boolean => {
			if (['clientName', 'roomName'].includes(name)) {
				return !!value;
			}

			return true;
		},
		[]
	);

	const handleBannerDelete = useCallback(
		(event: any) => {
			event.preventDefault();
			event.stopPropagation();

			setDataContext((prevState) => ({
				...prevState,
				banner: {},
				errors: {
					...prevState.errors,
					banner: null,
				},
			}));
		},
		[setDataContext]
	);

	const handleClientLogoChange = useCallback(
		async (event: ChangeEvent<HTMLInputElement>) => {
			event.preventDefault();
			event.stopPropagation();

			const file = (event.target?.files || [])[0] as File;

			if (file) {
				const clientLogoBase64 = await getBase64(file);

				setDataContext((prevState) => ({
					...prevState,
					clientLogo: {
						base64: clientLogoBase64,
					},
					errors: {
						...prevState.errors,
						clientLogo: null,
					},
				}));
			}
		},
		[setDataContext]
	);

	const handleClientLogoDelete = useCallback(
		(event: any) => {
			event.preventDefault();
			event.stopPropagation();

			if (clientLogoInputFileRef.current) {
				(clientLogoInputFileRef.current as HTMLInputElement).value = '';
			}

			setDataContext((prevState) => ({
				...prevState,
				clientLogo: {},
				errors: {
					...prevState.errors,
					clientLogo: null,
				},
			}));
		},
		[clientLogoInputFileRef, setDataContext]
	);

	const handleClientLogoFileChooser = useCallback(
		async (event: any) => {
			event.preventDefault();

			if (clientLogoInputFileRef.current) {
				(clientLogoInputFileRef.current as HTMLInputElement).click();
			}
		},
		[clientLogoInputFileRef]
	);

	const handleFieldChange = useCallback(
		({
			target: {name, value},
		}: {
			target: {
				name: string;
				value: string;
			};
		}) => {
			setDataContext((prevState) => ({
				...prevState,
				errors: {
					...prevState.errors,
					[name]: fieldValid(name, value)
						? ''
						: Liferay.Language.get('this-field-is-mandatory'),
				},
				[name]: value,
			}));
		},
		[setDataContext, fieldValid]
	);

	const handlePrimaryColorChange = useCallback(
		(data: string) => {
			handleFieldChange({target: {name: 'primaryColor', value: data}});
		},
		[handleFieldChange]
	);

	const handleSecondaryColorChange = useCallback(
		(data: string) => {
			handleFieldChange({target: {name: 'secondaryColor', value: data}});
		},
		[handleFieldChange]
	);

	useEffect(() => {
		setHandleStepSubmit(() => async (event: Event): Promise<boolean> => {
			event.preventDefault();

			handleFieldChange({
				target: {name: 'clientName', value: dataContext.clientName},
			});
			handleFieldChange({
				target: {name: 'roomName', value: dataContext.roomName},
			});
			setDataContext((prevState) => ({
				...prevState,
				errors: {
					...prevState.errors,
					banner: null,
					clientLogo: null,
				},
			}));

			if (
				!fieldValid('banner', dataContext.banner.base64) ||
				!fieldValid('clientLogo', dataContext.clientLogo.base64) ||
				!fieldValid('clientName', dataContext.clientName) ||
				!fieldValid('primaryColor', dataContext.primaryColor) ||
				!fieldValid('roomName', dataContext.roomName) ||
				!fieldValid('secondaryColor', dataContext.secondaryColor)
			) {
				return Promise.resolve(false);
			}

			return Promise.resolve(true);
		});
	}, [
		dataContext,
		fieldValid,
		handleFieldChange,
		setDataContext,
		setHandleStepSubmit,
	]);

	return (
		<>
			{showHeader ? (
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
						{Liferay.Language.get('customize-your-room')}
					</div>

					<div className="text-secondary">
						{Liferay.Language.get(
							"personalize-your-room-to-match-your-customers'-brand"
						)}
					</div>
				</div>
			) : (
				<></>
			)}
			<div className="mt-4 row">
				<ClayForm.Group
					className={classNames('col-12', {
						'has-error': !!dataContext.errors.clientLogo,
					})}
				>
					<label className="d-block" htmlFor="dsr-client-logo">
						{Liferay.Language.get('upload-client-logo')}
					</label>

					<input
						accept="image/*"
						aria-hidden="true"
						className="d-none"
						data-qa-id="clientLogoInput"
						disabled={loading}
						id="dsr-client-logo"
						onChange={handleClientLogoChange}
						ref={clientLogoInputFileRef}
						type="file"
					/>

					<div className="align-items-center d-flex">
						<Sticker className="mr-2" shape="user-icon" size="xxl">
							<Sticker.Image
								alt="placeholder"
								data-qa-id="clientLogoSticker"
								src={
									dataContext.clientLogo.base64 ||
									getImage('logo.svg')
								}
							/>
						</Sticker>

						<ClayButton
							className="ml-2"
							data-qa-id="clientLogoButton"
							disabled={loading}
							displayType="primary"
							onClick={handleClientLogoFileChooser}
							size="sm"
						>
							{Liferay.Language.get('upload-image')}
						</ClayButton>

						{dataContext.clientLogo.base64 && (
							<ClayButton
								className="ml-3"
								data-qa-id="clientLogoDeleteButton"
								disabled={loading}
								displayType="secondary"
								onClick={handleClientLogoDelete}
								size="sm"
							>
								{Liferay.Language.get('delete')}
							</ClayButton>
						)}
					</div>

					<FieldErrorMessage
						error={dataContext.errors.clientLogo}
						name="clientLogo"
					/>
				</ClayForm.Group>
			</div>
			<div className="row">
				<ClayForm.Group
					className={classNames('col-6', {
						'has-error': !!dataContext.errors.clientName,
					})}
				>
					<label className="d-block" htmlFor="dsr-client-name">
						{Liferay.Language.get('client-name')}

						<span className="c-ml-2 reference-mark">
							<ClayIcon symbol="asterisk" />
						</span>
					</label>

					<ClayInput
						aria-label={Liferay.Language.get('client-name')}
						data-qa-id="clientNameInput"
						disabled={loading}
						id="dsr-client-name"
						name="clientName"
						onChange={handleFieldChange}
						required={true}
						type="text"
						value={dataContext.clientName || ''}
					/>

					<FieldErrorMessage
						error={dataContext.errors.clientName}
						name="clientName"
					/>
				</ClayForm.Group>

				<ClayForm.Group
					className={classNames('col-6', {
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
						onChange={handleFieldChange}
						required={true}
						type="text"
						value={dataContext.roomName || ''}
					/>

					<FieldErrorMessage
						error={dataContext.errors.roomName}
						name="roomName"
					/>
				</ClayForm.Group>
			</div>
			<div className="row">
				<ClayForm.Group
					className={classNames('col-12', {
						'has-error': !!dataContext.errors.banner,
					})}
				>
					<label className="d-block" htmlFor="dsr-banner">
						{Liferay.Language.get(
							'upload-digital-sales-room-header-image'
						)}
					</label>

					<div
						{...getRootProps({
							className: classNames('dropzone', {
								'dropzone-disabled': loading,
								'dropzone-drag-active': isDragActive,
							}),
						})}
					>
						<input aria-hidden="true" {...getInputProps()} />

						<div
							className="dsr-drag-zone"
							data-qa-id="bannerImage"
							style={{
								backgroundImage: dataContext.banner.base64
									? `url(${dataContext.banner.base64})`
									: 'none',
							}}
						>
							{dataContext.banner.base64 ? (
								<>
									<ClayButton
										className="ml-2"
										data-qa-id="dsr-banner-button"
										disabled={loading}
										displayType="primary"
										size="sm"
									>
										{Liferay.Language.get('upload-file')}
									</ClayButton>

									<ClayButton
										className="ml-3"
										data-qa-id="dsr-banner-delete-button"
										disabled={loading}
										displayType="secondary"
										onClick={handleBannerDelete}
										size="sm"
									>
										{Liferay.Language.get('delete')}
									</ClayButton>
								</>
							) : (
								<>
									<div className="text-center">
										<img
											alt={Liferay.Language.get(
												'drag-and-drop-your-file-or'
											)}
											src={getImage(
												'drag_drop_image.svg'
											)}
										/>

										<p className="my-2 text-weight-semi-bold">
											{Liferay.Language.get(
												'drag-and-drop-your-file-or'
											)}
										</p>

										<ClayButton displayType="secondary">
											{Liferay.Language.get(
												'select-file'
											)}
										</ClayButton>
									</div>
								</>
							)}
						</div>
					</div>

					<FieldErrorMessage
						error={dataContext.errors.banner}
						name="banner"
					/>
				</ClayForm.Group>
			</div>
			<div className="row">
				<ClayForm.Group
					className={classNames('col-6', {
						'has-error': !!dataContext.errors.primaryColor,
					})}
				>
					<label className="d-block" htmlFor="dsr-primary-color">
						{Liferay.Language.get('primary-color')}
					</label>

					<ClayColorPicker
						data-qa-id="primaryColorInput"
						disabled={loading}
						name="dsr-primary-color"
						onChange={handlePrimaryColorChange}
						showHex={true}
						value={dataContext.primaryColor}
					/>

					<FieldErrorMessage
						error={dataContext.errors.primaryColor}
						name="primaryColor"
					/>
				</ClayForm.Group>

				<ClayForm.Group
					className={classNames('col-6', {
						'has-error': !!dataContext.errors.secondaryColor,
					})}
				>
					<label className="d-block" htmlFor="dsr-secondary-color">
						{Liferay.Language.get('secondary-color')}
					</label>

					<ClayColorPicker
						data-qa-id="secondaryColorInput"
						disabled={loading}
						name="dsr-secondary-color"
						onChange={handleSecondaryColorChange}
						showHex={true}
						value={dataContext.secondaryColor}
					/>

					<FieldErrorMessage
						error={dataContext.errors.secondaryColor}
						name="secondaryColor"
					/>
				</ClayForm.Group>
			</div>
			<div className="row">
				<ClayForm.Group
					className={classNames('col-12', {
						'has-error': !!dataContext.errors.friendlyURL,
					})}
				>
					<label className="d-block" htmlFor="dsr-friendly-url">
						{Liferay.Language.get('friendly-url')}
					</label>

					<ClayInput
						aria-label={Liferay.Language.get('friendly-url')}
						data-qa-id="friendlyURLInput"
						disabled={loading}
						id="dsr-friendly-url"
						name="friendlyURL"
						onChange={handleFieldChange}
						type="text"
						value={dataContext.friendlyURL || ''}
					/>

					<FieldErrorMessage
						error={dataContext.errors.friendlyURL}
						name="friendlyURL"
					/>
				</ClayForm.Group>
			</div>
		</>
	);
}

export default DSRRoomDetailsStep;
