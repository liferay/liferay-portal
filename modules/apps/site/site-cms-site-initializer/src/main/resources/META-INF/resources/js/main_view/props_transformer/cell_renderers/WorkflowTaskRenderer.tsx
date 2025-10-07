/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createPortletURL, dateUtils} from 'frontend-js-web';
import React from 'react';

import {WorkflowTask} from '../../../common/types/WorkflowTask';

const WorkflowTaskRenderer = ({itemData}: {itemData: WorkflowTask}) => {
	return (
		<span className="align-items-center d-flex">
			<div className="autofit-col">
				<div className="c-mr-2 sticker sticker-circle sticker-lg sticker-secondary">
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
			</div>

			<div className="autofit-col autofit-col-expand mt-3">
				<p className="list-group-text text-3 text-dark">
					{`${itemData.auditUser} sent you `}

					<a
						className="home-link"
						href={createPortletURL(itemData.myWorkflowTasksURL, {
							mvcPath: '/edit_workflow_task.jsp',
							workflowTaskId: itemData.id,
						}).toString()}
					>
						{itemData.objectReviewed.assetTitle}
					</a>

					{` for ${itemData.name} in the workflow.`}
				</p>

				<p className="text-3 text-secondary">
					{dateUtils.fromNow(new Date(itemData?.assignedDate))}
				</p>
			</div>
		</span>
	);
};

export default WorkflowTaskRenderer;
