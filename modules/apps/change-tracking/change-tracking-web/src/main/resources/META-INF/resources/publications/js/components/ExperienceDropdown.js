/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker, Text} from '@clayui/core';
import ClayLabel from '@clayui/label';
import Layout from '@clayui/layout';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';

const ExperienceDropdown = ({
	activeSegmentsExperience,
	updatePreviewRender,
	...props
}) => {
	const [selectedKey, setSelectedKey] = useState(activeSegmentsExperience.id);

	useEffect(() => {
		updatePreviewRender(selectedKey);
	}, [activeSegmentsExperience.id, selectedKey, updatePreviewRender]);

	const handleSelectionChange = (key) => {
		setSelectedKey(key);
	};

	return (
		props.segmentsExperiences.length > 1 && (
			<div className="mb-2 mr-2">
				<Picker
					aria-label={Liferay.Language.get('experience-selector')}
					items={props.segmentsExperiences}
					messages={{
						itemDescribedby: Liferay.Language.get(
							'you-are-currently-on-a-text-element,-inside-of-a-list-box'
						),
						itemSelected: Liferay.Language.get('x-selected'),
						scrollToBottomAriaLabel:
							Liferay.Language.get('scroll-to-bottom'),
						scrollToTopAriaLabel:
							Liferay.Language.get('scroll-to-top'),
					}}
					onSelectionChange={handleSelectionChange}
					selectedKey={selectedKey}
					selecteditem={activeSegmentsExperience}
				>
					{(experience) => (
						<Option key={experience.id} textValue={experience.name}>
							<Layout.ContentRow>
								<Layout.ContentCol className="c-pl-0" expand>
									<Text size={3} weight="semi-bold">
										{experience.name}
									</Text>

									<Text
										aria-hidden
										className="mr-1"
										color="secondary"
										size={2}
									>
										{Liferay.Language.get('segment') + ': '}

										<Text size={2}>
											{experience.segmentName}
										</Text>
									</Text>
								</Layout.ContentCol>

								<Layout.ContentCol>
									<ExperienceStatusLabel
										active={experience.active}
									/>
								</Layout.ContentCol>
							</Layout.ContentRow>
						</Option>
					)}
				</Picker>
			</div>
		)
	);
};

ExperienceDropdown.propTypes = {
	activeSegmentsExperience: PropTypes.shape({
		active: PropTypes.bool,
		id: PropTypes.string,
		isDefault: PropTypes.bool,
		name: PropTypes.string,
		segmentName: PropTypes.string,
	}),
	segmentsExperiences: PropTypes.arrayOf(
		PropTypes.shape({
			active: PropTypes.bool,
			id: PropTypes.string,
			isDefault: PropTypes.bool,
			name: PropTypes.string,
			segmentName: PropTypes.string,
		})
	),
	updatePreviewRender: PropTypes.func,
};

const ExperienceStatusLabel = (props) => {
	let displayType = null;
	let label = null;

	if (props.active) {
		displayType = 'success';
		label = Liferay.Language.get('active');
	}
	else if (!props.active) {
		displayType = 'secondary';
		label = Liferay.Language.get('inactive');
	}

	return (
		<ClayLabel
			className="flex-shrink-0 inline-item-after"
			displayType={displayType}
		>
			{label}
		</ClayLabel>
	);
};

ExperienceStatusLabel.propTypes = {
	active: PropTypes.bool,
};

export default ExperienceDropdown;
