/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayList from '@clayui/list';
import {ClayTooltipProvider} from '@clayui/tooltip';
import classNames from 'classnames';
import {LearnMessage} from 'frontend-js-components-web';
import {fetch, navigate, sub} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

import {IDataSet} from '../..//utils/types';
import RequiredMark from '../../components/RequiredMark';
import {DEFAULT_FETCH_HEADERS} from '../../utils/constants';
import getAPIExplorerURL from '../../utils/getAPIExplorerURL';
import getDataSetResourceURL from '../../utils/getDataSetResourceURL';
import openDefaultFailureToast from '../../utils/openDefaultFailureToast';
import openDefaultSuccessToast from '../../utils/openDefaultSuccessToast';
import {IDataSetSectionProps} from '../DataSet';

const getURLPreview = ({
	additionalAPIURLParameters = '',
	restApplication,
	restEndpoint,
}: {
	additionalAPIURLParameters: IDataSet['additionalAPIURLParameters'];
	restApplication: IDataSet['restApplication'];
	restEndpoint: IDataSet['restEndpoint'];
}) => {
	const encodedAdditionalAPIURLParameters = encodeURI(
		additionalAPIURLParameters.trim()
	);

	// This also removes the version (for example: `/v1.0`) in the rest endpoint
	// to avoid repeating the version when combining the restApplication and
	// restEndpoint.

	return (
		restApplication +
		restEndpoint
			.split('/')
			.filter((_, index) => index !== 1)
			.join('/') +
		'?' +
		encodedAdditionalAPIURLParameters
	);
};

const Details = ({
	backURL,
	dataSet,
	namespace,
	onDataSetUpdate,
}: IDataSetSectionProps) => {
	const [labelValidationError, setLabelValidationError] = useState(false);

	const [urlPreview, setURLPreview] = useState(
		getURLPreview({
			additionalAPIURLParameters: dataSet.additionalAPIURLParameters,
			restApplication: dataSet.restApplication,
			restEndpoint: dataSet.restEndpoint,
		})
	);

	const descriptionRef = useRef<HTMLInputElement>(null);
	const labelRef = useRef<HTMLInputElement>(null);
	const parametersRef = useRef<HTMLInputElement>(null);

	const handleKeyUpParameters = (
		event: React.ChangeEvent<HTMLInputElement>
	) => {
		setURLPreview(
			getURLPreview({
				additionalAPIURLParameters: event.currentTarget.value,
				restApplication: dataSet.restApplication,
				restEndpoint: dataSet.restEndpoint,
			})
		);
	};

	const updateFDSView = async () => {
		const body = {
			additionalAPIURLParameters: parametersRef.current?.value,
			description: descriptionRef.current?.value,
			label: labelRef.current?.value,
		};

		const url = getDataSetResourceURL({
			dataSetERC: dataSet.externalReferenceCode,
		});

		const response = await fetch(url, {
			body: JSON.stringify(body),
			headers: DEFAULT_FETCH_HEADERS,
			method: 'PATCH',
		});

		if (!response.ok) {
			openDefaultFailureToast();

			return;
		}

		const responseJSON = await response.json();

		if (responseJSON?.id) {
			openDefaultSuccessToast();

			const controlMenuHeaderTitles = document.getElementsByClassName(
				'control-menu-level-1-heading'
			);

			if (controlMenuHeaderTitles.length === 1) {
				controlMenuHeaderTitles[0].innerHTML = Liferay.Util.escapeHTML(
					labelRef.current?.value ?? ''
				);
			}
			onDataSetUpdate(responseJSON);
		}
		else {
			openDefaultFailureToast();
		}
	};

	const {restApplication, restEndpoint, restSchema} = dataSet;

	const apiExplorerURL = getAPIExplorerURL(restApplication);

	return (
		<ClayLayout.ContainerFluid
			className="mt-3 visualization-modes"
			size="lg"
		>
			<ClayLayout.Sheet>
				<ClayLayout.SheetHeader>
					<h2 className="sheet-title">
						{Liferay.Language.get('details')}
					</h2>
				</ClayLayout.SheetHeader>

				<ClayLayout.SheetSection>
					<ClayForm.Group
						className={classNames({
							'has-error': labelValidationError,
						})}
					>
						<label htmlFor={`${namespace}dataSetLabelInput`}>
							{Liferay.Language.get('name')}

							<RequiredMark />
						</label>

						<ClayInput
							defaultValue={dataSet.label}
							id={`${namespace}dataSetLabelInput`}
							onBlur={() =>
								setLabelValidationError(
									!labelRef.current?.value
								)
							}
							ref={labelRef}
							type="text"
						/>

						{labelValidationError && (
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
						<label htmlFor={`${namespace}dataSetDesctiptionInput`}>
							{Liferay.Language.get('description')}
						</label>

						<ClayInput
							defaultValue={dataSet.description}
							id={`${namespace}dataSetDesctiptionInput`}
							ref={descriptionRef}
							type="text"
						/>
					</ClayForm.Group>
				</ClayLayout.SheetSection>

				<ClayLayout.SheetSection className="mb-4">
					<h3 className="sheet-subtitle">
						{Liferay.Language.get('rest-information')}
					</h3>

					<ClayList className="flex-row flex-wrap">
						<ClayList.Item
							className="border-0 col-12 col-sm-6"
							flex
						>
							<ClayList.ItemField className="justify-content-center">
								<ClayIcon symbol="api-web" />
							</ClayList.ItemField>

							<ClayList.ItemField expand>
								<ClayList.ItemTitle>
									{Liferay.Language.get('application')}
								</ClayList.ItemTitle>

								<ClayList.ItemText>
									<ClayTooltipProvider>
										<ClayLink
											data-tooltip-align="top"
											decoration="underline"
											displayType="tertiary"
											href={apiExplorerURL}
											rel="noopener noreferrer"
											target="_blank"
											title={apiExplorerURL}
										>
											{restApplication}

											<span className="inline-item inline-item-after">
												<ClayIcon
													className="text-2 text-secondary"
													symbol="shortcut"
												/>
											</span>
										</ClayLink>
									</ClayTooltipProvider>
								</ClayList.ItemText>
							</ClayList.ItemField>
						</ClayList.Item>

						<ClayList.Item
							className="border-0 col-12 col-sm-6"
							flex
						>
							<ClayList.ItemField className="justify-content-center">
								<ClayIcon symbol="diagram" />
							</ClayList.ItemField>

							<ClayList.ItemField>
								<ClayList.ItemTitle>
									{Liferay.Language.get('schema')}
								</ClayList.ItemTitle>

								<ClayList.ItemText>
									{restSchema}
								</ClayList.ItemText>
							</ClayList.ItemField>
						</ClayList.Item>

						<ClayList.Item className="border-0 col-12" flex>
							<ClayList.ItemField className="justify-content-center">
								<ClayIcon symbol="nodes" />
							</ClayList.ItemField>

							<ClayList.ItemField>
								<ClayList.ItemTitle>
									{Liferay.Language.get('endpoint')}
								</ClayList.ItemTitle>

								<ClayList.ItemText>
									{restEndpoint}
								</ClayList.ItemText>
							</ClayList.ItemField>
						</ClayList.Item>
					</ClayList>
				</ClayLayout.SheetSection>

				<ClayLayout.SheetSection className="mb-4">
					<h3 className="sheet-subtitle">
						{Liferay.Language.get('advanced-optional-parameters')}
					</h3>

					<ClayForm.Group>
						<label htmlFor={`${namespace}dataSetParametersInput`}>
							{Liferay.Language.get('parameters')}

							<span
								className="label-icon lfr-portal-tooltip ml-2"
								title={Liferay.Language.get(
									'data-set-parameters-help'
								)}
							>
								<ClayIcon symbol="question-circle-full" />
							</span>
						</label>

						<ClayInput
							component="textarea"
							defaultValue={dataSet.additionalAPIURLParameters}
							id={`${namespace}dataSetParametersInput`}
							onChange={handleKeyUpParameters}
							placeholder={sub(
								Liferay.Language.get(
									'data-set-parameters-placeholder'
								),
								'filter=dateCreated le 2012-05-29T00:00:00.000Z&flatten=true&sort=name'
							)}
							ref={parametersRef}
							type="text"
						/>
					</ClayForm.Group>

					<ClayForm.Group>
						<label htmlFor={`${namespace}dataSetURLPreviewInput`}>
							{Liferay.Language.get('url-preview')}

							<span
								className="label-icon lfr-portal-tooltip ml-2"
								title={Liferay.Language.get('url-preview-help')}
							>
								<ClayIcon symbol="question-circle-full" />
							</span>
						</label>

						<ClayInput
							id={`${namespace}dataSetURLPreviewInput`}
							readOnly
							value={urlPreview}
						/>
					</ClayForm.Group>

					<LearnMessage
						className="single-link text-3"
						resource="frontend-data-set-admin-web"
						resourceKey="rest-parameters"
					/>
				</ClayLayout.SheetSection>

				<ClayLayout.SheetFooter>
					<ClayButton.Group spaced>
						<ClayButton onClick={updateFDSView}>
							{Liferay.Language.get('save')}
						</ClayButton>

						<ClayButton
							displayType="secondary"
							onClick={() => navigate(backURL)}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>
					</ClayButton.Group>
				</ClayLayout.SheetFooter>
			</ClayLayout.Sheet>
		</ClayLayout.ContainerFluid>
	);
};

export default Details;
