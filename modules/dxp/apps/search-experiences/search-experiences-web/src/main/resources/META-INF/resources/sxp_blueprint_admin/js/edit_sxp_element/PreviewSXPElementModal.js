/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal, {useModal} from '@clayui/modal';
import {fetch} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

import ErrorBoundary from '../shared/ErrorBoundary';
import JSONSXPElement from '../shared/JSONSXPElement';
import SXPElement from '../shared/sxp_element/index';
import {DEFAULT_HEADERS} from '../utils/fetch/fetch_data';
import isEmpty from '../utils/functions/is_empty';
import getUIConfigurationValues from '../utils/sxp_element/get_ui_configuration_values';
import isCustomJSONSXPElement from '../utils/sxp_element/is_custom_json_sxp_element';

export default function PreviewSXPElementModal({
	isSXPElementJSONInvalid,
	onTitleAndDescriptionChange,
	sxpElementJSONObject,
}) {
	const titleAndDescriptionRef = useRef({});

	const [loading, setLoading] = useState(false);
	const [preview, setPreview] = useState({
		sxpElementJSONObject: {},
		uiConfigurationValues: {},
	});
	const [visible, setVisible] = useState(false);

	const _handleClose = () => {
		setVisible(false);

		setPreview({
			sxpElementJSONObject: {},
			uiConfigurationValues: {},
		});

		if (!isEmpty(titleAndDescriptionRef.current)) {
			onTitleAndDescriptionChange(titleAndDescriptionRef.current);

			titleAndDescriptionRef.current = {};
		}
	};

	const {observer} = useModal({
		onClose: _handleClose,
	});

	const _fetchPreview = () => {
		setLoading(true);

		fetch('/o/search-experiences-rest/v1.0/sxp-elements/preview', {
			body: JSON.stringify({
				description_i18n: sxpElementJSONObject.description_i18n,
				elementDefinition: sxpElementJSONObject.elementDefinition,
				title_i18n: sxpElementJSONObject.title_i18n,
			}),
			headers: DEFAULT_HEADERS,
			method: 'POST',
		})
			.then((response) => response.json())
			.then((jsonObject) => {
				const {description, title} = jsonObject;

				setPreview({
					sxpElementJSONObject: jsonObject,
					uiConfigurationValues: getUIConfigurationValues(jsonObject),
				});

				titleAndDescriptionRef.current = {description, title};
			})
			.catch(() => {
				setPreview({
					sxpElementJSONObject,
					uiConfigurationValues:
						getUIConfigurationValues(sxpElementJSONObject),
				});
			})
			.finally(() => {
				setLoading(false);
			});
	};

	const _handleOpenModal = () => {
		setVisible(true);

		if (!isSXPElementJSONInvalid) {
			_fetchPreview();
		}
	};

	return (
		<>
			{visible && (
				<ClayModal
					className="sxp-preview-modal-root"
					observer={observer}
					size="lg"
				>
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('preview-configuration')}
					</ClayModal.Header>

					<ClayModal.Body>
						{!isSXPElementJSONInvalid ? (
							<div className="portlet-sxp-blueprint-admin">
								{loading ? (
									<div
										className="d-flex inline-item"
										style={{
											height: '400px',
										}}
									>
										<ClayLoadingIndicator
											displayType="secondary"
											size="sm"
										/>
									</div>
								) : (
									<ErrorBoundary>
										{isCustomJSONSXPElement(
											preview.uiConfigurationValues
										) ? (
											<JSONSXPElement
												collapseAll={false}
												readOnly={true}
												sxpElement={
													preview.sxpElementJSONObject
												}
												uiConfigurationValues={
													preview.uiConfigurationValues
												}
											/>
										) : (
											<SXPElement
												collapseAll={false}
												sxpElement={
													preview.sxpElementJSONObject
												}
												uiConfigurationValues={
													preview.uiConfigurationValues
												}
											/>
										)}
									</ErrorBoundary>
								)}
							</div>
						) : (
							<ClayEmptyState
								description={Liferay.Language.get(
									'json-may-be-incorrect-and-we-were-unable-to-load-a-preview-of-the-configuration'
								)}
								imgSrc="/o/admin-theme/images/states/empty_state.svg"
								title={Liferay.Language.get(
									'unable-to-load-preview'
								)}
							/>
						)}
					</ClayModal.Body>
				</ClayModal>
			)}

			<ClayButton
				borderless
				displayType="secondary"
				onClick={_handleOpenModal}
				small
			>
				{Liferay.Language.get('preview')}
			</ClayButton>
		</>
	);
}
