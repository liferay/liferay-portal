/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {PortletDataHandlerSelection} from '../../../utils/contentSelection';
import {FieldCheckbox} from '../FieldCheckbox';

export interface SectionFooterField {
	key: string;
	label: string;
}

interface SectionFooterProps {
	fields: readonly SectionFooterField[];
	name: string;
	onChange: (value: PortletDataHandlerSelection | undefined) => void;
	portletDataHandlerSelection: PortletDataHandlerSelection | undefined;
	subtitle?: string;
	title: string;
}

export default function SectionFooter({
	fields,
	name,
	onChange,
	portletDataHandlerSelection,
	subtitle,
	title,
}: SectionFooterProps) {
	const portletDataHandlerSelections =
		portletDataHandlerSelection &&
		typeof portletDataHandlerSelection === 'object'
			? portletDataHandlerSelection
			: undefined;

	return (
		<>
			<hr className="my-3" />

			<div className="p-3">
				<div className="font-weight-bold text-3">{title}</div>

				{subtitle ? (
					<small className="d-block mb-3 text-secondary">
						{subtitle}
					</small>
				) : null}

				<div className="c-gap-1 d-flex flex-column pl-4">
					{fields.map((field) => (
						<FieldCheckbox
							bordered={false}
							checked={Boolean(
								portletDataHandlerSelections?.[field.key]
							)}
							key={field.key}
							label={field.label}
							name={`${name}.${field.key}`}
							onChange={(checked) => {
								const nextPortletDataHandlerSelections: Record<
									string,
									true
								> = {};

								fields.forEach((otherField) => {
									if (
										otherField.key === field.key
											? checked
											: Boolean(
													portletDataHandlerSelections?.[
														otherField.key
													]
												)
									) {
										nextPortletDataHandlerSelections[
											otherField.key
										] = true;
									}
								});

								onChange(
									Object.keys(
										nextPortletDataHandlerSelections
									).length
										? (nextPortletDataHandlerSelections as PortletDataHandlerSelection)
										: undefined
								);
							}}
						/>
					))}
				</div>
			</div>
		</>
	);
}
