/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {openToast} from 'frontend-js-components-web';
import {createActionURL, createResourceURL, fetch} from 'frontend-js-web';
import React, {useContext, useEffect, useState} from 'react';

import {AppContext} from '../../AppContext';
import {BackButtonPortal} from '../../components/control-menu/ControlMenu';
import DocumentPreviewerWrapper from '../../components/document-previewer/DocumentPreviewerWrapper';
import {DOCUSIGN_STATUS} from '../../utils/contants';
import {toLocalDateTimeFormatted} from '../../utils/moment';
import {concatValues} from '../../utils/utils';

const QuestionLine = ({children, className, colon = true, question}) => (
	<div className={className}>
		<b>{`${question}${colon ? ':' : ''}`}</b>

		<span className="ml-1">{children}</span>
	</div>
);

const EnvelopeDetail = ({
	envelope: {
		createdLocalDateTime,
		emailBlurb,
		emailSubject,
		recipients,
		senderEmailAddress,
	},
	envelopeId,
}) => (
	<div className="envelope-view__details">
		<div>
			<b>{Liferay.Language.get('envelope-id')}</b>: {envelopeId}
		</div>

		<hr />

		<div className="d-flex">
			<QuestionLine
				className="flex-grow-1"
				colon={false}
				question={emailSubject}
			/>

			<QuestionLine
				colon={false}
				question={toLocalDateTimeFormatted(createdLocalDateTime)}
			/>
		</div>

		<QuestionLine question={Liferay.Language.get('to')}>
			{concatValues(recipients?.signers.map(({email}) => email))}
		</QuestionLine>

		<QuestionLine question={Liferay.Language.get('from')}>
			{senderEmailAddress}
		</QuestionLine>

		<p className="mt-2">{emailBlurb}</p>
	</div>
);

const EnvelopeHeader = ({docusignStatus, emailSubject, envelopeId}) => {
	const {baseResourceURL} = useContext(AppContext);

	return (
		<div className="envelope-view__header">
			<div>
				<span className="envelope-view__header__title">
					{emailSubject}
				</span>

				<ClayLabel className="ml-2" displayType={docusignStatus.color}>
					{docusignStatus.label}
				</ClayLabel>
			</div>

			<ClayButton
				onClick={() =>
					window.open(
						createResourceURL(baseResourceURL, {
							dsEnvelopeId: envelopeId,
							p_p_resource_id:
								'/digital_signature/get_ds_documents_as_bytes',
						}),
						'_blank'
					)
				}
			>
				<ClayIcon symbol="download" />

				<span className="ml-1">{Liferay.Language.get('download')}</span>
			</ClayButton>
		</div>
	);
};

function EnvelopeView({
	match: {
		params: {envelopeId},
	},
}) {
	const {baseResourceURL} = useContext(AppContext);

	const [{envelope, fileEntries = [], isLoading}, setEnvelope] = useState({
		envelope: {},
		isLoading: true,
	});

	const docusignStatus = DOCUSIGN_STATUS[envelope?.status] || {
		...DOCUSIGN_STATUS.other,
		label: envelope?.status,
	};

	const getEnvelope = async () => {
		try {
			const response = await fetch(
				createResourceURL(baseResourceURL, {
					dsEnvelopeId: envelopeId,
					p_p_resource_id: '/digital_signature/get_ds_envelope',
				})
			);

			const data = await response.json();

			setEnvelope({...data, isLoading: false});
		}
		catch (error) {
			openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				title: Liferay.Language.get('error'),
				type: 'danger',
			});
		}
	};

	useEffect(() => {
		if (envelopeId) {
			getEnvelope();
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [envelopeId]);

	if (isLoading) {
		return <ClayLoadingIndicator />;
	}

	return (
		<div className="envelope-view">
			<BackButtonPortal />

			<EnvelopeHeader
				docusignStatus={docusignStatus}
				emailSubject={envelope.emailSubject}
				envelopeId={envelopeId}
			/>

			<DocumentPreviewerWrapper fileEntries={fileEntries} />

			<EnvelopeDetail envelope={envelope} envelopeId={envelopeId} />

			<input
				type="hidden"
				value={createActionURL(baseResourceURL, {
					'dsEnvelopeId': envelopeId,
					'jakarta.portlet.action':
						'/digital_signature/delete_ds_envelope',
					'p_auth': Liferay.authToken,
				})}
			/>
		</div>
	);
}

export default EnvelopeView;
