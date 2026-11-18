/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createPortletURL, dateUtils, sub} from 'frontend-js-web';
import React from 'react';

import {WorkflowTask} from '../../../common/types/WorkflowTask';

const WorkflowTaskRenderer = ({itemData}: {itemData: WorkflowTask}) => {
	return (
		<span className="align-items-start d-flex pb-2">
			<div className="flex-shrink-0 mr-3 sticker sticker-circle sticker-secondary">
				<span className="inline-item">
					<img
						className="avatar img-fluid logo-img mw-100 rounded-circle"
						src={
							itemData.auditUserImageURL
								? itemData.auditUserImageURL
								: '/image/user_portrait?img_id=0'
						}
					/>
				</span>
			</div>

			<div className="flex-grow-1">
				<p className="list-group-text mb-0 text-3 text-dark">
					{sub(
						Liferay.Language.get(
							'x-sent-you-x-for-x-in-the-workflow'
						),
						[
							itemData.auditUser,
							<a
								className="font-weight-bold text-decoration-underline"
								href={createPortletURL(
									itemData.workflowTaskURL,
									{
										mvcPath: '/edit_workflow_task.jsp',
										workflowTaskId: itemData.id,
									}
								).toString()}
								key="assetTitle"
							>
								{itemData.objectReviewed.assetTitle}
							</a>,
							itemData.label,
						]
					)}
				</p>

				<p className="mb-0 text-3 text-secondary">
					{dateUtils.fromNow(new Date(itemData?.assignedDate))}
				</p>
			</div>
		</span>
	);
};

export default WorkflowTaskRenderer;
