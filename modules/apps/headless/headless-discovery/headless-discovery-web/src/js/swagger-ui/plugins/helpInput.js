/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayPopover from '@clayui/popover';
import React, {useState} from 'react';

import Icon from '../../Icon';

function FilterableFieldsHelp({filterableFields, messageDetails}) {
	return (
		<>
			<p className="h5">Filterable Fields</p>

			{messageDetails && (
				<ClayLink
					href={messageDetails.url}
					rel="noopener noreferrer"
					target="_blank"
				>
					{messageDetails.message}
				</ClayLink>
			)}

			{filterableFields && <hr />}

			{filterableFields && (
				<ul className="c-mb-0 c-pl-0">
					{Object.keys(filterableFields).map((filterableField) => (
						<ClayLayout.ContentRow
							className="c-mr-2 c-my-2"
							containerElement="li"
							key={filterableField}
						>
							<ClayLayout.ContentCol expand>
								{filterableField}{' '}

								<small>
									({filterableFields[filterableField].type})
								</small>
							</ClayLayout.ContentCol>

							<ClayLayout.ContentCol>
								<ClayButton
									aria-label="Copy to Clipboard"
									displayType="secondary"
									monospaced
									onClick={() => {
										navigator.clipboard.writeText(
											filterableField
										);
									}}
									size="xs"
									title="Copy to Clipboard"
								>
									<Icon symbol="copy" />
								</ClayButton>
							</ClayLayout.ContentCol>
						</ClayLayout.ContentRow>
					))}
				</ul>
			)}
		</>
	);
}

function HelpInputWrapper({children, field, filterableFields}) {
	const messageDetails = window.learnResources[field]?.['en_US'];
	const [openPopover, setOpenPopover] = useState(false);

	return (
		<>
			{children}
			{(messageDetails || filterableFields) && (
				<ClayPopover
					closeOnClickOutside
					onShowChange={setOpenPopover}
					show={openPopover}
					trigger={
						<button className="btn-unstyled ml-2" type="button">
							<Icon symbol="question-circle" />
						</button>
					}
				>
					{field === 'filter' &&
					(filterableFields || messageDetails) ? (
						<FilterableFieldsHelp
							filterableFields={filterableFields}
							messageDetails={messageDetails}
						/>
					) : (
						messageDetails && (
							<ClayLink
								href={messageDetails.url}
								rel="noopener noreferrer"
								target="_blank"
							>
								{messageDetails.message}
							</ClayLink>
						)
					)}
				</ClayPopover>
			)}
		</>
	);
}

export default function helpSwaggerUIPlugin() {
	return {
		wrapComponents: {
			JsonSchema_string: (Original) => {
				return (props) => {
					let filterableFields;

					if (
						props.description === 'filter' &&
						props.schema.get('x-filterable')?.size
					) {
						filterableFields = props.schema
							.get('x-filterable')
							.toJS();
					}

					return (
						<HelpInputWrapper
							field={props.description}
							filterableFields={filterableFields}
						>
							<Original {...props} />
						</HelpInputWrapper>
					);
				};
			},
		},
	};
}
