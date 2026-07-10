/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import ClayList from '@clayui/list';
import React from 'react';

import {ElementVariation} from './elementVariationsReducer';
import {EditableElementOption} from './getEditableElementOptions';

function hasValueInAnyLanguage(
	localizedValue: Record<string, string>
): boolean {
	return Object.values(localizedValue).some(Boolean);
}

interface Props {
	audiences: Array<{label: string; value: string}>;
	editableElementOptions: EditableElementOption[];
	elementVariations: ElementVariation[];
	onDeleteElementVariation: (elementVariation: ElementVariation) => void;
	onEditElementVariation: (key: string) => void;
}

export default function ElementVariationsList({
	audiences,
	editableElementOptions,
	elementVariations,
	onDeleteElementVariation,
	onEditElementVariation,
}: Props) {
	const groupedElementVariations = elementVariations.reduce(
		(groupedElementVariations, elementVariation) => {
			const targetElementVariations =
				groupedElementVariations[elementVariation.targetElement] ?? [];

			targetElementVariations.push(elementVariation);

			groupedElementVariations[elementVariation.targetElement] =
				targetElementVariations;

			return groupedElementVariations;
		},
		{} as Record<string, ElementVariation[]>
	);

	return (
		<>
			{Object.entries(groupedElementVariations).map(
				([targetElement, targetElementVariations]) => (
					<ClayList className="mx-3" key={targetElement}>
						{[
							<ClayList.Header className="text-none" key="header">
								{editableElementOptions.find(
									(editableElementOption) =>
										editableElementOption.value ===
										targetElement
								)?.label ?? targetElement}
							</ClayList.Header>,
							...targetElementVariations.map(
								(elementVariation) => (
									<ClayList.Item
										flex
										key={elementVariation.key}
									>
										<ClayList.ItemField expand>
											<ClayList.ItemTitle>
												{elementVariation.name}
											</ClayList.ItemTitle>

											<ClayList.ItemText>
												{elementVariation.audienceEntryERCs
													.map(
														(audienceEntryERC) =>
															audiences.find(
																(audience) =>
																	audience.value ===
																	audienceEntryERC
															)?.label
													)
													.filter(Boolean)
													.join(', ')}
											</ClayList.ItemText>

											<ClayList.ItemText>
												<div>
													{hasValueInAnyLanguage(
														elementVariation.html
													) ? (
														<ClayLabel
															className="label-inverse-content-1"
															displayType="unstyled"
															inverse
														>
															{Liferay.Language.get(
																'html'
															)}
														</ClayLabel>
													) : null}

													{hasValueInAnyLanguage(
														elementVariation.js
													) ? (
														<ClayLabel
															className="label-inverse-content-8"
															displayType="unstyled"
															inverse
														>
															{Liferay.Language.get(
																'javascript'
															)}
														</ClayLabel>
													) : null}

													{elementVariation.hide ? (
														<ClayLabel
															displayType="success"
															inverse
														>
															{Liferay.Language.get(
																'hide-element'
															)}
														</ClayLabel>
													) : null}
												</div>
											</ClayList.ItemText>
										</ClayList.ItemField>

										<ClayList.ItemField className="p-0">
											<ClayList.QuickActionMenu>
												<ClayList.QuickActionMenu.Item
													onClick={() =>
														onEditElementVariation(
															elementVariation.key
														)
													}
													symbol="pencil"
													title={Liferay.Language.get(
														'edit'
													)}
												/>

												<ClayList.QuickActionMenu.Item
													onClick={() =>
														onDeleteElementVariation(
															elementVariation
														)
													}
													symbol="trash"
													title={Liferay.Language.get(
														'delete'
													)}
												/>
											</ClayList.QuickActionMenu>
										</ClayList.ItemField>
									</ClayList.Item>
								)
							),
						]}
					</ClayList>
				)
			)}
		</>
	);
}
