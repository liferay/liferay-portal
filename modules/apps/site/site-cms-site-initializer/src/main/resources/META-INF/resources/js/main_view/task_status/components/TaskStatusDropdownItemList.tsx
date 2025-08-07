/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
import DropDown from "@clayui/drop-down";
import {COMPLETED, ITask, STATUS_PROPERTIES} from "../TaskStatusType";
import ClayPanel from "@clayui/panel";
import {sub} from "frontend-js-web";
import Label from "@clayui/label";
import Button from "@clayui/button";
import React from "react";
import '../../../../css/components/AssetTaskStatus.scss';
import moment from "moment";

function TaskStatusDropdownItemList ({items}: any) {
    return (
        <>
            <DropDown.ItemList
                className="task-status"
                items={items}>
                {(rawTask: unknown) => {

                    const {taskResult = COMPLETED, ...task} = rawTask as ITask;

                    const properties = STATUS_PROPERTIES[taskResult];

                    return (
                        <ClayPanel
                            collapsable
                            displayTitle={
                            <ClayPanel.Title
                                className="d-flex task-status-item">
                                <div className="task-status-item-icon">
                                    <properties.component
                                        className={properties.iconClassName}
                                        displayType={properties?.displayType}
                                        size="sm"
                                        symbol={properties?.icon}
                                    />
                                </div>
                                <div className="task-status-item-text">
                                    <p className="h5 m-0 mr-1">
                                        {Liferay.Language.get(
                                            task.actionName
                                        )}
                                    </p>
                                    <span className="d-flex">
                                    <p className="m-0 small text-secondary text-uppercase">
                                        {sub(
                                            Liferay.Language.get(
                                                'x-items'
                                            ),
                                            [task.taskItems]
                                        )}
                                    </p>
                                    <span className="ml-1 mr-1 small">-</span>
                                    <p className="m-0 small text-secondary text-uppercase">
                                        {
                                            moment(
                                                task.dateModified
                                            ).fromNow(false)
                                        }
                                    </p>
                                    </span>
                                    <p className="mb-2">
                                        <Label
                                            displayType={properties.labelDisplayType}>
                                            {Liferay.Language.get(taskResult)}
                                        </Label>
                                    </p>
                                </div>
                            </ClayPanel.Title>}
                            key={task.id}
                            showCollapseIcon={true}
                        >

                            <ClayPanel.Body className="d-flex">
                                <Button
                                    className="btn-xs"
                                    displayType="success"
                                >
                                    {Liferay.Language.get('dismiss')}
                                </Button>
                                <Button
                                    className="border-success btn-xs text-success"
                                    displayType="secondary"
                                >
                                    {Liferay.Language.get('view')}
                                </Button>
                            </ClayPanel.Body>
                        </ClayPanel>
                    );
                }}
            </DropDown.ItemList>
            <a
                className="border-top btn btn-link text-secondary w-100"
                href="#"
            >
                {Liferay.Language.get("view-all-tasks")}
            </a>
        </>
    )
}

export default TaskStatusDropdownItemList;