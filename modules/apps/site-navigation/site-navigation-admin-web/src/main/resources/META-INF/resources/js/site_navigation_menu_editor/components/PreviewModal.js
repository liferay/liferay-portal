/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import classNames from 'classnames';
import {addParams} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useRef, useState} from 'react';

import {useConstants} from '../contexts/ConstantsContext';

export function PreviewModal({observer}) {
	const {
		displayTemplateOptions,
		portletNamespace,
		previewSiteNavigationMenuURL,
	} = useConstants();

	const displayTemplateSelectId = `${portletNamespace}-displayTemplateSelect`;

	const [displayTemplateId, setDisplayTemplateId] = useState(
		displayTemplateOptions.find((option) => option.selected).value
	);

	const [loading, setLoading] = useState(true);

	const iframeRef = useRef();

	const previewURL = addParams(
		{
			[`${portletNamespace}ddmTemplateKey`]: displayTemplateId,
		},
		previewSiteNavigationMenuURL
	);

	return (
		<ClayModal observer={observer} size="xl">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('preview-menu')}
			</ClayModal.Header>

			<ClayModal.Body className="site-navigation__preview-modal">
				<ClayForm.Group className="h-100">
					<label htmlFor={displayTemplateSelectId}>
						{Liferay.Language.get('display-template')}
					</label>

					<ClaySelectWithOption
						id={displayTemplateSelectId}
						onChange={(event) => {
							setDisplayTemplateId(event.target.value);
							setLoading(true);
						}}
						options={displayTemplateOptions}
						value={displayTemplateId}
					/>

					{loading && (
						<div className="pt-4">
							<ClayLoadingIndicator />
						</div>
					)}

					<iframe
						className={classNames('border-0 h-75 mt-4 w-100', {
							'd-none': loading,
						})}
						onLoad={() => {
							iframeRef.current.contentDocument.body.classList.add(
								'p-3'
							);

							iframeRef.current.contentDocument.body.addEventListener(
								'click',
								(event) => event.preventDefault(),
								{capture: true}
							);

							setLoading(false);
						}}
						ref={iframeRef}
						src={previewURL}
					/>
				</ClayForm.Group>
			</ClayModal.Body>
		</ClayModal>
	);
}

PreviewModal.propTypes = {
	observer: PropTypes.object.isRequired,
};
